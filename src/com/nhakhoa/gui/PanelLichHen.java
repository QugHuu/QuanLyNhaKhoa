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
    private JTable tblLichHen;
    private DefaultTableModel modelLichHen;

    private JLabel lblDetailName, lblDetailPhone, lblDetailDoctor, lblDetailTime, lblDetailChair, lblDetailStatus;
    private JButton btnTiepNhan, btnHuy;

    private JComboBox<BenhNhan> cboBenhNhan;
    private JComboBox<String> cboBacSi;
    private JComboBox<String> cboGhe;
    private JTextField txtNgayHen, txtGioBatDau, txtGioKetThuc;

    private LichHenBUS lichHenBUS = new LichHenBUS();
    private BenhNhanBUS benhNhanBUS = new BenhNhanBUS();
    private List<String[]> listBS;
    private List<String[]> listGhe;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private String selectedMaLich = null;

    public PanelLichHen() {
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        setBackground(UITheme.BG_MAIN);

        // ==================== 1. PAGE HEADER ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTitle.setOpaque(false);
        JLabel lblHeading = new JLabel("Lịch hẹn");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeading.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Sắp xếp lịch khám, theo dõi tiếp nhận tại sảnh và ghế điều trị");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        pnlTitle.add(lblHeading);
        pnlTitle.add(lblSub);

        JPanel pnlHeaderActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlHeaderActions.setOpaque(false);

        JButton btnTaoLich = UITheme.createButton("+ Tạo lịch hẹn mới", UITheme.PRIMARY, Color.WHITE);
        btnTaoLich.addActionListener(e -> hienDialogTaoLich());
        pnlHeaderActions.add(btnTaoLich);

        pnlHeader.add(pnlTitle, BorderLayout.WEST);
        pnlHeader.add(pnlHeaderActions, BorderLayout.EAST);

        // ==================== 2. FILTER TOOLBAR ====================
        JPanel pnlFilterBar = new JPanel(new BorderLayout());
        pnlFilterBar.setOpaque(false);

        JPanel pnlDateSwitch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlDateSwitch.setOpaque(false);
        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        JLabel lblCurDate = new JLabel("Thứ Ba, " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        lblCurDate.setFont(UITheme.FONT_BOLD);
        pnlDateSwitch.add(btnPrev);
        pnlDateSwitch.add(lblCurDate);
        pnlDateSwitch.add(btnNext);

        JPanel pnlViewType = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlViewType.setOpaque(false);
        JLabel lblView = new JLabel("Chế độ xem: Ngày | Tuần | Tháng");
        lblView.setFont(UITheme.FONT_SMALL);
        lblView.setForeground(UITheme.TEXT_MUTED);
        pnlViewType.add(lblView);

        pnlFilterBar.add(pnlDateSwitch, BorderLayout.WEST);
        pnlFilterBar.add(pnlViewType, BorderLayout.EAST);

        JPanel pnlTopWrapper = new JPanel(new BorderLayout(0, 12));
        pnlTopWrapper.setOpaque(false);
        pnlTopWrapper.add(pnlHeader, BorderLayout.NORTH);
        pnlTopWrapper.add(pnlFilterBar, BorderLayout.SOUTH);

        // ==================== 3. BODY: TABLE & DETAIL CARD ====================
        JPanel pnlBody = new JPanel(new BorderLayout(14, 0));
        pnlBody.setOpaque(false);

        // TABLE CARD (LEFT / CENTER)
        JPanel pnlTableCard = UITheme.createCard();
        pnlTableCard.setLayout(new BorderLayout());

        String[] headers = {"Mã Lịch", "Khung Giờ", "Bệnh Nhân", "Bác Sĩ Điều Trị", "Phòng & Ghế", "STT", "Trạng Thái"};
        modelLichHen = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichHen = new JTable(modelLichHen);
        UITheme.formatTable(tblLichHen);
        tblLichHen.getColumnModel().getColumn(6).setCellRenderer(new UITheme.StatusBadgeRenderer());

        tblLichHen.getSelectionModel().addListSelectionListener(e -> {
            int row = tblLichHen.getSelectedRow();
            if (row >= 0) {
                selectedMaLich = modelLichHen.getValueAt(row, 0).toString();
                lblDetailTime.setText(modelLichHen.getValueAt(row, 1).toString());
                lblDetailName.setText(modelLichHen.getValueAt(row, 2).toString());
                lblDetailDoctor.setText(modelLichHen.getValueAt(row, 3).toString());
                lblDetailChair.setText(modelLichHen.getValueAt(row, 4).toString());
                String tt = modelLichHen.getValueAt(row, 6).toString();
                lblDetailStatus.setText(tt);

                btnTiepNhan.setEnabled("CHO_KHAM".equals(tt));
                btnHuy.setEnabled(!"DA_HUY".equals(tt) && !"DA_KHAM".equals(tt));
            }
        });

        pnlTableCard.add(new JScrollPane(tblLichHen), BorderLayout.CENTER);

        // DETAIL CARD (RIGHT - 340px) Theo Mockup 03
        JPanel pnlDetailCard = UITheme.createCard();
        pnlDetailCard.setLayout(new BorderLayout(0, 14));
        pnlDetailCard.setPreferredSize(new Dimension(340, 0));

        JLabel lblDetailHeader = new JLabel("Chi tiết lịch hẹn");
        lblDetailHeader.setFont(UITheme.FONT_SUBTITLE);
        lblDetailHeader.setForeground(UITheme.TEXT_DARK);

        JPanel pnlDetailInfo = new JPanel(new GridLayout(6, 1, 0, 8));
        pnlDetailInfo.setOpaque(false);

        lblDetailName = new JLabel("Chưa chọn lịch hẹn");
        lblDetailName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDetailName.setForeground(UITheme.PRIMARY);

        lblDetailPhone = new JLabel("Liên hệ: ---");
        lblDetailPhone.setFont(UITheme.FONT_SMALL);

        lblDetailTime = new JLabel("Khung giờ: ---");
        lblDetailTime.setFont(UITheme.FONT_SMALL);

        lblDetailDoctor = new JLabel("Bác sĩ: ---");
        lblDetailDoctor.setFont(UITheme.FONT_SMALL);

        lblDetailChair = new JLabel("Vị trí: ---");
        lblDetailChair.setFont(UITheme.FONT_SMALL);

        lblDetailStatus = new JLabel("Trạng thái: ---");
        lblDetailStatus.setFont(UITheme.FONT_BOLD);

        pnlDetailInfo.add(lblDetailName);
        pnlDetailInfo.add(lblDetailPhone);
        pnlDetailInfo.add(lblDetailTime);
        pnlDetailInfo.add(lblDetailDoctor);
        pnlDetailInfo.add(lblDetailChair);
        pnlDetailInfo.add(lblDetailStatus);

        JPanel pnlDetailActions = new JPanel(new GridLayout(2, 1, 0, 8));
        pnlDetailActions.setOpaque(false);

        btnTiepNhan = UITheme.createButton("Tiếp nhận vào khám (RB08)", UITheme.PRIMARY, Color.WHITE);
        btnTiepNhan.setEnabled(false);
        btnTiepNhan.addActionListener(e -> xuLyTiepNhan());

        btnHuy = UITheme.createButton("Hủy lịch hẹn này", Color.WHITE, UITheme.DANGER);
        btnHuy.setBorder(BorderFactory.createLineBorder(new Color(254, 202, 202), 1, true));
        btnHuy.setEnabled(false);
        btnHuy.addActionListener(e -> xuLyHuy());

        pnlDetailActions.add(btnTiepNhan);
        pnlDetailActions.add(btnHuy);

        pnlDetailCard.add(lblDetailHeader, BorderLayout.NORTH);
        pnlDetailCard.add(pnlDetailInfo, BorderLayout.CENTER);
        pnlDetailCard.add(pnlDetailActions, BorderLayout.SOUTH);

        pnlBody.add(pnlTableCard, BorderLayout.CENTER);
        pnlBody.add(pnlDetailCard, BorderLayout.EAST);

        add(pnlTopWrapper, BorderLayout.NORTH);
        add(pnlBody, BorderLayout.CENTER);

        taiDuLieu();
    }

    public void taiDuLieu() {
        modelLichHen.setRowCount(0);
        List<LichHen> list = lichHenBUS.getAll();
        for (LichHen lh : list) {
            String gio = FormatUtil.formatShortTime(lh.getGioBatDau()) + " - " + FormatUtil.formatShortTime(lh.getGioKetThuc());
            modelLichHen.addRow(new Object[]{
                    lh.getMaLichHen(),
                    gio,
                    lh.getTenBN(),
                    lh.getTenBS(),
                    lh.getMaGhe(),
                    lh.getSoThuTuCho() > 0 ? "STT " + lh.getSoThuTuCho() : "-",
                    lh.getTinhTrang()
            });
        }
    }

    private void xuLyTiepNhan() {
        if (selectedMaLich == null) return;
        try {
            lichHenBUS.doiTrangThai(selectedMaLich, "DA_DEN");
            JOptionPane.showMessageDialog(this, "Đã tiếp nhận bệnh nhân! Hồ sơ chuyển sang hàng chờ của Bác sĩ.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHuy() {
        if (selectedMaLich == null) return;
        int cf = JOptionPane.showConfirmDialog(this, "Xác nhận hủy lịch hẹn " + selectedMaLich + "?", "Hủy lịch", JOptionPane.YES_NO_OPTION);
        if (cf == JOptionPane.YES_OPTION) {
            try {
                lichHenBUS.doiTrangThai(selectedMaLich, "DA_HUY");
                JOptionPane.showMessageDialog(this, "Đã hủy lịch hẹn thành công!");
                taiDuLieu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hienDialogTaoLich() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tạo lịch hẹn mới", true);
        dlg.setSize(420, 480);
        dlg.setLocationRelativeTo(this);

        JPanel pnl = new JPanel(new BorderLayout(0, 14));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblDTitle = new JLabel("ĐẶT LỊCH HẸN KHÁM");
        lblDTitle.setFont(UITheme.FONT_SUBTITLE);
        lblDTitle.setForeground(UITheme.PRIMARY);

        JPanel pnlForm = new JPanel(new GridLayout(6, 1, 0, 8));
        pnlForm.setOpaque(false);

        cboBenhNhan = new JComboBox<>();
        for (BenhNhan bn : benhNhanBUS.getAll()) cboBenhNhan.addItem(bn);

        cboBacSi = new JComboBox<>();
        listBS = lichHenBUS.getDanhSachBacSi();
        for (String[] bs : listBS) cboBacSi.addItem(bs[1]);

        cboGhe = new JComboBox<>();
        listGhe = lichHenBUS.getDanhSachGhe();
        for (String[] g : listGhe) cboGhe.addItem(g[1]);

        txtNgayHen = new JTextField(sdf.format(new Date()));
        txtGioBatDau = new JTextField("09:00");
        txtGioKetThuc = new JTextField("09:45");

        pnlForm.add(taoField("Bệnh nhân:", cboBenhNhan));
        pnlForm.add(taoField("Bác sĩ phụ trách:", cboBacSi));
        pnlForm.add(taoField("Ghế điều trị:", cboGhe));
        pnlForm.add(taoField("Ngày hẹn (dd/MM/yyyy):", txtNgayHen));
        pnlForm.add(taoField("Giờ bắt đầu:", txtGioBatDau));
        pnlForm.add(taoField("Giờ kết thúc:", txtGioKetThuc));

        JButton btnSave = UITheme.createButton("Xác nhận đặt lịch", UITheme.PRIMARY, Color.WHITE);
        btnSave.setPreferredSize(new Dimension(0, 40));
        btnSave.addActionListener(e -> {
            try {
                BenhNhan bn = (BenhNhan) cboBenhNhan.getSelectedItem();
                int idxBS = cboBacSi.getSelectedIndex();
                int idxGhe = cboGhe.getSelectedIndex();
                if (bn == null || idxBS < 0 || idxGhe < 0) return;

                LichHen lh = new LichHen();
                lh.setMaLichHen(lichHenBUS.taoMaLHMoi());
                lh.setMaBN(bn.getMaBN());
                lh.setMaBS(listBS.get(idxBS)[0]);
                lh.setMaGhe(listGhe.get(idxGhe)[0]);
                lh.setNgayHen(sdf.parse(txtNgayHen.getText().trim()));
                lh.setGioBatDau(txtGioBatDau.getText().trim());
                lh.setGioKetThuc(txtGioKetThuc.getText().trim());
                lh.setTinhTrang("CHO_KHAM");

                lichHenBUS.them(lh);
                JOptionPane.showMessageDialog(dlg, "Đặt lịch hẹn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                taiDuLieu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnl.add(lblDTitle, BorderLayout.NORTH);
        pnl.add(pnlForm, BorderLayout.CENTER);
        pnl.add(btnSave, BorderLayout.SOUTH);

        dlg.setContentPane(pnl);
        dlg.setVisible(true);
    }

    private JPanel taoField(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BOLD);
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }
}
