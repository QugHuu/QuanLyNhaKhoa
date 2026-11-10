package com.nhakhoa.gui;

import com.nhakhoa.bus.BenhNhanBUS;
import com.nhakhoa.bus.LichHenBUS;
import com.nhakhoa.model.BenhNhan;
import com.nhakhoa.model.LichHen;
import com.nhakhoa.util.FormatUtil;

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
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        JLabel lblTitle = new JLabel("QUẢN LÝ LỊCH HẸN & TIẾP NHẬN BỆNH NHÂN (PHÂN HỆ 2)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(25, 42, 86));

        // LEFT: Form đặt lịch
        JPanel pnlLeft = new JPanel(new BorderLayout(10, 10));
        pnlLeft.setPreferredSize(new Dimension(380, 0));
        pnlLeft.setBackground(Color.WHITE);
        pnlLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel pnlForm = new JPanel(new GridLayout(7, 2, 8, 12));
        pnlForm.setOpaque(false);

        txtMaLichHen = new JTextField(lichHenBUS.taoMaLHMoi());
        txtMaLichHen.setEditable(false);
        txtMaLichHen.setBackground(new Color(240, 240, 240));

        cboBenhNhan = new JComboBox<>();
        cboBacSi = new JComboBox<>();
        cboGhe = new JComboBox<>();

        txtNgayHen = new JTextField(sdf.format(new Date()));
        txtGioBatDau = new JTextField("09:00:00");
        txtGioKetThuc = new JTextField("09:45:00");

        pnlForm.add(new JLabel("Mã lịch:"));
        pnlForm.add(txtMaLichHen);
        pnlForm.add(new JLabel("Bệnh nhân:"));
        pnlForm.add(cboBenhNhan);
        pnlForm.add(new JLabel("Bác sĩ phụ trách:"));
        pnlForm.add(cboBacSi);
        pnlForm.add(new JLabel("Ghế điều trị:"));
        pnlForm.add(cboGhe);
        pnlForm.add(new JLabel("Ngày hẹn (dd/MM/yyyy):"));
        pnlForm.add(txtNgayHen);
        pnlForm.add(new JLabel("Giờ bắt đầu:"));
        pnlForm.add(txtGioBatDau);
        pnlForm.add(new JLabel("Giờ kết thúc:"));
        pnlForm.add(txtGioKetThuc);

        // Buttons
        JPanel pnlButtons = new JPanel(new GridLayout(3, 1, 8, 8));
        pnlButtons.setOpaque(false);

        JButton btnDatLich = new JButton("📅 Đặt lịch hẹn mới");
        JButton btnDaDen = new JButton("✅ Bệnh nhân đã đến phòng khám");
        JButton btnHuy = new JButton("❌ Hủy lịch hẹn này");

        btnDatLich.setBackground(new Color(46, 204, 113));
        btnDatLich.setForeground(Color.BLACK);
        btnDaDen.setBackground(new Color(52, 152, 219));
        btnDaDen.setForeground(Color.BLACK);
        btnHuy.setBackground(new Color(231, 76, 60));
        btnHuy.setForeground(Color.BLACK);

        btnDatLich.addActionListener(e -> xuLyDatLich());
        btnDaDen.addActionListener(e -> xuLyBenhNhanDaDen());
        btnHuy.addActionListener(e -> xuLyHuyLich());

        pnlButtons.add(btnDatLich);
        pnlButtons.add(btnDaDen);
        pnlButtons.add(btnHuy);

        pnlLeft.add(new JLabel("THÔNG TIN LỊCH HẸN", JLabel.CENTER), BorderLayout.NORTH);
        pnlLeft.add(pnlForm, BorderLayout.CENTER);
        pnlLeft.add(pnlButtons, BorderLayout.SOUTH);

        // RIGHT: Danh sách lịch hẹn
        JPanel pnlRight = new JPanel(new BorderLayout(10, 10));
        pnlRight.setBackground(Color.WHITE);
        pnlRight.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        String[] headers = {"Mã Lịch", "Bệnh Nhân", "Bác Sĩ", "Ghế", "Ngày Hẹn", "Giờ Bắt Đầu", "Giờ Kết Thúc", "Trạng Thái"};
        modelLichHen = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblLichHen = new JTable(modelLichHen);
        tblLichHen.setRowHeight(26);
        tblLichHen.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        pnlRight.add(new JLabel("DANH SÁCH TOÀN BỘ LỊCH HẸN"), BorderLayout.NORTH);
        pnlRight.add(new JScrollPane(tblLichHen), BorderLayout.CENTER);

        add(lblTitle, BorderLayout.NORTH);
        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);

        loadComboboxData();
        taiDuLieu();
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
            cboBacSi.addItem(bs[1]); // Hiển thị tên BS
        }

        cboGhe.removeAllItems();
        listGhe = lichHenBUS.getDanhSachGhe();
        for (String[] g : listGhe) {
            cboGhe.addItem(g[1]); // Hiển thị tên Ghế
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
                    lh.getGioBatDau(),
                    lh.getGioKetThuc(),
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
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi đặt lịch", JOptionPane.ERROR_MESSAGE);
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
            // RB08: Cập nhật trạng thái DA_DEN để Bác sĩ có thể tiếp nhận khám
            lichHenBUS.doiTrangThai(maLich, "DA_DEN");
            JOptionPane.showMessageDialog(this, "Bệnh nhân " + maLich + " đã đến! Hồ sơ chuyển sang hàng chờ khám của Bác sĩ.", "Tiếp nhận thành công", JOptionPane.INFORMATION_MESSAGE);
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
