package com.nhakhoa.gui;

import com.nhakhoa.bus.BenhNhanBUS;
import com.nhakhoa.bus.LichHenBUS;
import com.nhakhoa.model.BenhNhan;
import com.nhakhoa.model.LichHen;
import com.nhakhoa.util.FormatUtil;
import com.nhakhoa.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PanelLichHen extends JPanel {
    private JComboBox<BenhNhan> cboBenhNhan;
    private JComboBox<String> cboBacSi;
    private JComboBox<String> cboGhe;
    private JTextField txtNgayHen, txtGioBatDau, txtGioKetThuc, txtMaLichHen;
    private JTable tblLichHen;
    private DefaultTableModel modelLichHen;

    private LichHenBUS lichHenBUS = new LichHenBUS();
    private BenhNhanBUS benhNhanBUS = new BenhNhanBUS();
    private List<String[]> listBS;
    private List<String[]> listGhe;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public PanelLichHen() {
        setLayout(new BorderLayout(18, 0));
        setBorder(new EmptyBorder(18, 24, 18, 24));
        setBackground(UITheme.BG_MAIN);

        // LEFT: Card Form đặt lịch
        JPanel pnlLeftCard = UITheme.createCard();
        pnlLeftCard.setLayout(new BorderLayout(0, 14));
        pnlLeftCard.setPreferredSize(new Dimension(380, 0));

        JLabel lblFormTitle = new JLabel("ĐẶT LỊCH HẸN & TIẾP ĐÓN");
        lblFormTitle.setFont(UITheme.FONT_SUBTITLE);
        lblFormTitle.setForeground(UITheme.TEXT_DARK);

        JPanel pnlForm = new JPanel(new GridLayout(7, 1, 0, 8));
        pnlForm.setOpaque(false);

        txtMaLichHen = new JTextField(lichHenBUS.taoMaLHMoi());
        txtMaLichHen.setEditable(false);
        txtMaLichHen.setBackground(new Color(241, 245, 249));

        cboBenhNhan = new JComboBox<>();
        cboBacSi = new JComboBox<>();
        cboGhe = new JComboBox<>();

        txtNgayHen = new JTextField(sdf.format(new Date()));
        txtGioBatDau = new JTextField("09:00");
        txtGioKetThuc = new JTextField("09:45");

        pnlForm.add(taoFieldInput("Mã lịch hẹn:", txtMaLichHen));
        pnlForm.add(taoFieldInput("Bệnh nhân đặt khám:", cboBenhNhan));
        pnlForm.add(taoFieldInput("Bác sĩ phụ trách:", cboBacSi));
        pnlForm.add(taoFieldInput("Ghế & Phòng điều trị:", cboGhe));
        pnlForm.add(taoFieldInput("Ngày hẹn (dd/MM/yyyy):", txtNgayHen));
        pnlForm.add(taoFieldInput("Giờ bắt đầu (HH:mm):", txtGioBatDau));
        pnlForm.add(taoFieldInput("Giờ kết thúc (HH:mm):", txtGioKetThuc));

        // Buttons
        JPanel pnlButtons = new JPanel(new GridLayout(3, 1, 0, 8));
        pnlButtons.setOpaque(false);

        JButton btnDatLich = UITheme.createButton("Đặt lịch hẹn mới", UITheme.PRIMARY, Color.WHITE);
        JButton btnDaDen = UITheme.createButton("Bệnh nhân đã đến phòng khám", UITheme.INFO, Color.WHITE);
        JButton btnHuy = UITheme.createButton("Hủy lịch hẹn đã chọn", Color.WHITE, UITheme.DANGER);
        btnHuy.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 202, 202), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        btnDatLich.addActionListener(e -> xuLyDatLich());
        btnDaDen.addActionListener(e -> xuLyBenhNhanDaDen());
        btnHuy.addActionListener(e -> xuLyHuyLich());

        pnlButtons.add(btnDatLich);
        pnlButtons.add(btnDaDen);
        pnlButtons.add(btnHuy);

        pnlLeftCard.add(lblFormTitle, BorderLayout.NORTH);
        pnlLeftCard.add(pnlForm, BorderLayout.CENTER);
        pnlLeftCard.add(pnlButtons, BorderLayout.SOUTH);

        // RIGHT: Card Danh sách lịch hẹn
        JPanel pnlRightCard = UITheme.createCard();
        pnlRightCard.setLayout(new BorderLayout(0, 12));

        JLabel lblTableTitle = new JLabel("DANH SÁCH LỊCH HẸN TRÊN TOÀN HỆ THỐNG");
        lblTableTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTableTitle.setForeground(UITheme.TEXT_DARK);

        String[] headers = {"Mã Lịch", "Bệnh Nhân", "Bác Sĩ", "Ghế Điều Trị", "Ngày Hẹn", "Bắt Đầu", "Kết Thúc", "Trạng Thái"};
        modelLichHen = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblLichHen = new JTable(modelLichHen);
        UITheme.formatTable(tblLichHen);
        tblLichHen.getColumnModel().getColumn(7).setCellRenderer(new UITheme.StatusBadgeRenderer());

        pnlRightCard.add(lblTableTitle, BorderLayout.NORTH);
        pnlRightCard.add(new JScrollPane(tblLichHen), BorderLayout.CENTER);

        add(pnlLeftCard, BorderLayout.WEST);
        add(pnlRightCard, BorderLayout.CENTER);

        loadComboboxData();
        taiDuLieu();
    }

    private JPanel taoFieldInput(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 3));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BOLD);
        l.setForeground(UITheme.TEXT_DARK);
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void loadComboboxData() {
        cboBenhNhan.removeAllItems();
        List<BenhNhan> bnList = benhNhanBUS.getAll();
        for (BenhNhan bn : bnList) {
            cboBenhNhan.addItem(bn);
        }

        cboBacSi.removeAllItems();
        listBS = lichHenBUS.getDanhSachBacSi();
        for (String[] bs : listBS) {
            cboBacSi.addItem(bs[1]);
        }

        cboGhe.removeAllItems();
        listGhe = lichHenBUS.getDanhSachGhe();
        for (String[] g : listGhe) {
            cboGhe.addItem(g[1]);
        }
    }

    public void taiDuLieu() {
        modelLichHen.setRowCount(0);
        List<LichHen> list = lichHenBUS.getAll();
        for (LichHen lh : list) {
            modelLichHen.addRow(new Object[]{
                    lh.getMaLichHen(),
                    lh.getTenBN(),
                    lh.getTenBS(),
                    lh.getMaGhe(),
                    sdf.format(lh.getNgayHen()),
                    FormatUtil.formatShortTime(lh.getGioBatDau()),
                    FormatUtil.formatShortTime(lh.getGioKetThuc()),
                    lh.getTinhTrang()
            });
        }
    }

    private void xuLyDatLich() {
        try {
            BenhNhan bn = (BenhNhan) cboBenhNhan.getSelectedItem();
            if (bn == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bệnh nhân!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idxBS = cboBacSi.getSelectedIndex();
            int idxGhe = cboGhe.getSelectedIndex();
            if (idxBS < 0 || idxGhe < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bác sĩ và ghế!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maBS = listBS.get(idxBS)[0];
            String maGhe = listGhe.get(idxGhe)[0];

            Date ngay = sdf.parse(txtNgayHen.getText().trim());

            LichHen lh = new LichHen();
            lh.setMaLichHen(txtMaLichHen.getText().trim());
            lh.setMaBN(bn.getMaBN());
            lh.setMaBS(maBS);
            lh.setMaGhe(maGhe);
            lh.setNgayHen(ngay);
            lh.setGioBatDau(txtGioBatDau.getText().trim());
            lh.setGioKetThuc(txtGioKetThuc.getText().trim());
            lh.setSoThuTuCho(0);
            lh.setTinhTrang("CHO_KHAM");

            lichHenBUS.them(lh);
            JOptionPane.showMessageDialog(this, "Đặt lịch hẹn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
            txtMaLichHen.setText(lichHenBUS.taoMaLHMoi());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi đặt lịch (RB07)", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyBenhNhanDaDen() {
        int row = tblLichHen.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch hẹn trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maLich = modelLichHen.getValueAt(row, 0).toString();
        try {
            lichHenBUS.doiTrangThai(maLich, "DA_DEN");
            JOptionPane.showMessageDialog(this, "Bệnh nhân " + maLich + " đã đến! Hồ sơ chuyển sang hàng chờ khám của Bác sĩ (RB08).", "Tiếp nhận thành công", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHuyLich() {
        int row = tblLichHen.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch hẹn cần hủy trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maLich = modelLichHen.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn hủy lịch hẹn " + maLich + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                lichHenBUS.doiTrangThai(maLich, "DA_HUY");
                JOptionPane.showMessageDialog(this, "Đã hủy lịch hẹn thành công!");
                taiDuLieu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
