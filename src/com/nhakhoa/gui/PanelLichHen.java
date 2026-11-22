package com.nhakhoa.gui;

import com.nhakhoa.bus.BenhNhanBUS;
import com.nhakhoa.bus.LichHenBUS;
import com.nhakhoa.model.BenhNhan;
import com.nhakhoa.model.LichHen;
import com.nhakhoa.model.TaiKhoan;
import com.nhakhoa.util.FormatUtil;
import com.nhakhoa.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private JComboBox<String> cboLocBacSi;
    private JTextField txtNgayHen, txtGioBatDau, txtGioKetThuc;

    private LichHenBUS lichHenBUS = new LichHenBUS();
    private BenhNhanBUS benhNhanBUS = new BenhNhanBUS();
    private List<String[]> listBS;
    private List<String[]> listGhe;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private String selectedMaLich = null;
    private List<LichHen> currentList = new ArrayList<>();

    private static final String[][] CA_KHAM_CHUAN = {
        {"08:00", "08:45"},
        {"09:00", "09:45"},
        {"10:00", "10:45"},
        {"11:00", "11:45"},
        {"13:30", "14:15"},
        {"14:30", "15:15"},
        {"15:30", "16:15"},
        {"16:30", "17:15"},
        {"17:30", "18:15"},
        {"18:30", "19:15"}
    };

    private TaiKhoan currentUser;

    public PanelLichHen() {
        this(null);
    }

    public PanelLichHen(TaiKhoan user) {
        this.currentUser = user;
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
        JButton btnToday = new JButton("Hôm nay");
        btnToday.setFont(UITheme.FONT_SMALL);
        final java.util.Calendar calDate = java.util.Calendar.getInstance();
        SimpleDateFormat sdfThu = new SimpleDateFormat("EEEE, dd/MM/yyyy", new java.util.Locale("vi", "VN"));
        JLabel lblCurDate = new JLabel(sdfThu.format(calDate.getTime()));
        lblCurDate.setFont(UITheme.FONT_BOLD);

        btnPrev.addActionListener(e -> {
            calDate.add(java.util.Calendar.DAY_OF_MONTH, -1);
            lblCurDate.setText(sdfThu.format(calDate.getTime()));
            taiDuLieu();
        });
        btnNext.addActionListener(e -> {
            calDate.add(java.util.Calendar.DAY_OF_MONTH, 1);
            lblCurDate.setText(sdfThu.format(calDate.getTime()));
            taiDuLieu();
        });
        btnToday.addActionListener(e -> {
            calDate.setTime(new Date());
            lblCurDate.setText(sdfThu.format(calDate.getTime()));
            taiDuLieu();
        });

        pnlDateSwitch.add(btnPrev);
        pnlDateSwitch.add(btnToday);
        pnlDateSwitch.add(btnNext);
        pnlDateSwitch.add(lblCurDate);

        JLabel lblLocBS = new JLabel("   |   Xem lịch Bác sĩ:");
        lblLocBS.setFont(UITheme.FONT_SMALL);
        lblLocBS.setForeground(UITheme.TEXT_MUTED);
        cboLocBacSi = new JComboBox<>();
        cboLocBacSi.setFont(UITheme.FONT_SMALL);
        listBS = lichHenBUS.getDanhSachBacSi();
        if (currentUser != null && currentUser.isBacSi() && currentUser.getMaBacSi() != null) {
            cboLocBacSi.addItem(currentUser.getHoTen());
            cboLocBacSi.setEnabled(false);
        } else {
            cboLocBacSi.addItem("Tất cả bác sĩ");
            for (String[] bs : listBS) cboLocBacSi.addItem(bs[1]);
        }
        cboLocBacSi.addActionListener(e -> taiDuLieu());
        pnlDateSwitch.add(lblLocBS);
        pnlDateSwitch.add(cboLocBacSi);

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
            if (row >= 0 && row < currentList.size()) {
                LichHen lh = currentList.get(row);
                selectedMaLich = lh.getMaLichHen();
                String gio = FormatUtil.formatShortTime(lh.getGioBatDau()) + " - " + FormatUtil.formatShortTime(lh.getGioKetThuc());
                lblDetailTime.setText("Khung giờ: " + gio);
                lblDetailName.setText(lh.getTenBN() != null ? lh.getTenBN() : "---");
                lblDetailPhone.setText("Liên hệ: " + (lh.getSoDienThoai() != null && !lh.getSoDienThoai().trim().isEmpty() ? lh.getSoDienThoai() : "Chưa có SĐT"));
                lblDetailDoctor.setText("Bác sĩ: " + (lh.getTenBS() != null ? lh.getTenBS() : "---"));
                lblDetailChair.setText("Vị trí: " + (lh.getMaGhe() != null ? lh.getMaGhe() : "---"));
                String tt = lh.getTinhTrang();
                lblDetailStatus.setText("Trạng thái: " + (tt != null ? tt : "---"));

                boolean isLetanOrAdmin = (currentUser == null || currentUser.isLetan() || currentUser.isQuanLy());
                btnTiepNhan.setEnabled("CHO_KHAM".equals(tt) && isLetanOrAdmin);
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

        btnTiepNhan = UITheme.createButton("Tiếp nhận vào khám", UITheme.PRIMARY, Color.WHITE);
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
        List<LichHen> allList = lichHenBUS.getAll();
        String currentMaBS = (currentUser != null && currentUser.isBacSi()) ? currentUser.getMaBacSi() : null;

        int filterBSIdx = (cboLocBacSi != null) ? cboLocBacSi.getSelectedIndex() : 0;
        String filterMaBS = (currentMaBS != null) ? currentMaBS :
                ((filterBSIdx > 0 && listBS != null && filterBSIdx - 1 < listBS.size()) ? listBS.get(filterBSIdx - 1)[0] : null);

        currentList = new ArrayList<>();
        for (LichHen lh : allList) {
            if (filterMaBS != null && !filterMaBS.equals(lh.getMaBS())) {
                continue;
            }
            currentList.add(lh);
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
        if (modelLichHen.getRowCount() > 0) {
            tblLichHen.setRowSelectionInterval(0, 0);
        } else {
            selectedMaLich = null;
            lblDetailName.setText("Chưa chọn lịch hẹn");
            lblDetailPhone.setText("Liên hệ: ---");
            lblDetailTime.setText("Khung giờ: ---");
            lblDetailDoctor.setText("Bác sĩ: ---");
            lblDetailChair.setText("Vị trí: ---");
            lblDetailStatus.setText("Trạng thái: ---");
            btnTiepNhan.setEnabled(false);
            btnHuy.setEnabled(false);
        }
    }

    private void xuLyTiepNhan() {
        if (selectedMaLich == null) return;
        if (currentUser != null && !currentUser.isLetan() && !currentUser.isQuanLy()) {
            JOptionPane.showMessageDialog(this,
                    "Chức năng xác nhận 'Đã đến khám' thuộc nghiệp vụ của Lễ tân tiếp đón!",
                    "Thông báo phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            lichHenBUS.doiTrangThai(selectedMaLich, "DA_DEN");
            JOptionPane.showMessageDialog(this, "Đã tiếp nhận bệnh nhân! Hồ sơ chuyển sang hàng chờ của Bác sĩ.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // FR-13: Hủy lịch hẹn kèm lý do bắt buộc
    private void xuLyHuy() {
        if (selectedMaLich == null) return;

        if (currentUser != null && currentUser.isBacSi() && currentUser.getMaBacSi() != null) {
            LichHen curLich = currentList.stream().filter(l -> l.getMaLichHen().equals(selectedMaLich)).findFirst().orElse(null);
            if (curLich != null && !currentUser.getMaBacSi().equals(curLich.getMaBS())) {
                JOptionPane.showMessageDialog(this,
                        "Bạn chỉ có quyền hủy lịch hẹn thuộc phân công của chính mình!",
                        "Thông báo phân quyền", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Yêu cầu nhập lý do hủy (FR-13)
        String lyDo = JOptionPane.showInputDialog(this,
            "Nhap ly do huy lich hen " + selectedMaLich + ":\n(Bat buoc, toi thieu 5 ky tu)",
            "Huy lich hen", JOptionPane.QUESTION_MESSAGE);

        if (lyDo == null) return; // Người dùng bấm Cancel
        lyDo = lyDo.trim();
        if (lyDo.length() < 3) {
            JOptionPane.showMessageDialog(this, "Vui long nhap ly do huy (toi thieu 3 ky tu)!", "Loi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            com.nhakhoa.dao.LichHenDAO lichHenDAO = new com.nhakhoa.dao.LichHenDAO();
            boolean ok = lichHenDAO.huyLichHen(selectedMaLich, lyDo);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã hủy lịch hẹn thành công!\nLý do: " + lyDo, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi hủy lịch hẹn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienDialogTaoLich() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Đặt lịch hẹn khám - Tra cứu thời khóa biểu Bác sĩ", true);
        dlg.setSize(880, 560);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel pnlRoot = new JPanel(new BorderLayout(0, 14));
        pnlRoot.setBackground(Color.WHITE);
        pnlRoot.setBorder(new EmptyBorder(18, 20, 18, 20));

        // Center: 2 panels (Left: Form, Right: Bác sĩ schedule)
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlCenter.setOpaque(false);

        // LEFT PANEL: Form
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 10));
        pnlLeft.setOpaque(false);

        JLabel lblDTitle = new JLabel("THÔNG TIN ĐẶT LỊCH");
        lblDTitle.setFont(UITheme.FONT_SUBTITLE);
        lblDTitle.setForeground(UITheme.PRIMARY);

        JPanel pnlForm = new JPanel(new GridLayout(6, 1, 0, 8));
        pnlForm.setOpaque(false);

        cboBenhNhan = new JComboBox<>();
        for (BenhNhan bn : benhNhanBUS.getAll()) cboBenhNhan.addItem(bn);

        cboBacSi = new JComboBox<>();
        listBS = lichHenBUS.getDanhSachBacSi();
        for (String[] bs : listBS) cboBacSi.addItem(bs[1]);
        if (currentUser != null && currentUser.isBacSi() && currentUser.getHoTen() != null) {
            cboBacSi.setSelectedItem(currentUser.getHoTen());
            cboBacSi.setEnabled(false); // Bác sĩ chỉ tạo lịch hẹn cho chính mình
        }

        cboGhe = new JComboBox<>();
        listGhe = lichHenBUS.getDanhSachGhe();
        for (String[] g : listGhe) cboGhe.addItem(g[1]);

        txtNgayHen = new JTextField(sdf.format(new Date()));
        txtGioBatDau = new JTextField("08:00");
        txtGioKetThuc = new JTextField("08:45");

        pnlForm.add(taoField("Bệnh nhân:", cboBenhNhan));
        pnlForm.add(taoField("Bác sĩ phụ trách:", cboBacSi));
        pnlForm.add(taoField("Ghế điều trị:", cboGhe));
        pnlForm.add(taoField("Ngày hẹn (dd/MM/yyyy):", txtNgayHen));
        pnlForm.add(taoField("Giờ bắt đầu:", txtGioBatDau));
        pnlForm.add(taoField("Giờ kết thúc:", txtGioKetThuc));

        pnlLeft.add(lblDTitle, BorderLayout.NORTH);
        pnlLeft.add(pnlForm, BorderLayout.CENTER);

        // RIGHT PANEL: Doctor Schedule & Free Slots
        JPanel pnlRight = new JPanel(new BorderLayout(0, 8));
        pnlRight.setBackground(new Color(248, 250, 252));
        pnlRight.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JPanel pnlRightHeader = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlRightHeader.setOpaque(false);
        JLabel lblRightTitle = new JLabel("THỜI KHÓA BIỂU BÁC SĨ");
        lblRightTitle.setFont(UITheme.FONT_SUBTITLE);
        lblRightTitle.setForeground(UITheme.TEXT_DARK);
        JLabel lblRightSub = new JLabel("💡 Nhấp vào khung giờ TRỐNG để tự động điền giờ khám");
        lblRightSub.setFont(UITheme.FONT_SMALL);
        lblRightSub.setForeground(UITheme.PRIMARY);
        pnlRightHeader.add(lblRightTitle);
        pnlRightHeader.add(lblRightSub);

        String[] schedHeaders = {"Khung Giờ", "Trạng Thái", "Chi Tiết / Bệnh Nhân"};
        DefaultTableModel modelSched = new DefaultTableModel(schedHeaders, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblSched = new JTable(modelSched);
        tblSched.setRowHeight(30);
        tblSched.setFont(UITheme.FONT_SMALL);
        tblSched.getTableHeader().setFont(UITheme.FONT_BOLD);
        tblSched.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblSched.getColumnModel().getColumn(1).setPreferredWidth(105);
        tblSched.getColumnModel().getColumn(2).setPreferredWidth(160);

        tblSched.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(UITheme.FONT_BOLD);
                if (value != null && value.toString().contains("TRỐNG")) {
                    l.setBackground(new Color(240, 253, 250));
                    l.setForeground(UITheme.PRIMARY);
                } else {
                    l.setBackground(new Color(254, 242, 242));
                    l.setForeground(UITheme.DANGER);
                }
                return l;
            }
        });

        JLabel lblSlotTip = new JLabel("Vui lòng chọn một khung giờ trống bên trên.");
        lblSlotTip.setFont(UITheme.FONT_SMALL);
        lblSlotTip.setForeground(UITheme.TEXT_MUTED);

        tblSched.getSelectionModel().addListSelectionListener(e -> {
            int row = tblSched.getSelectedRow();
            if (row >= 0 && row < CA_KHAM_CHUAN.length) {
                String status = modelSched.getValueAt(row, 1).toString();
                String start = CA_KHAM_CHUAN[row][0];
                String end = CA_KHAM_CHUAN[row][1];
                if (status.contains("TRỐNG")) {
                    txtGioBatDau.setText(start);
                    txtGioKetThuc.setText(end);
                    lblSlotTip.setText("✓ Đã chọn ca trống: " + start + " - " + end);
                    lblSlotTip.setForeground(UITheme.PRIMARY);
                } else {
                    lblSlotTip.setText("⚠ Bác sĩ đã có hẹn lúc " + start + "! Hãy chọn ca trống.");
                    lblSlotTip.setForeground(UITheme.DANGER);
                }
            }
        });

        pnlRight.add(pnlRightHeader, BorderLayout.NORTH);
        pnlRight.add(new JScrollPane(tblSched), BorderLayout.CENTER);
        pnlRight.add(lblSlotTip, BorderLayout.SOUTH);

        pnlCenter.add(pnlLeft);
        pnlCenter.add(pnlRight);

        // Helper update schedule
        Runnable capNhatLichBS = () -> {
            int idxBS = cboBacSi.getSelectedIndex();
            if (idxBS < 0 || idxBS >= listBS.size()) return;
            String maBS = listBS.get(idxBS)[0];
            String tenBS = listBS.get(idxBS)[1];
            String ngayStr = txtNgayHen.getText().trim();
            java.sql.Date sqlDate;
            try {
                sqlDate = new java.sql.Date(sdf.parse(ngayStr).getTime());
            } catch (Exception ex) {
                sqlDate = new java.sql.Date(System.currentTimeMillis());
            }
            lblRightTitle.setText("LỊCH CỦA " + tenBS.toUpperCase());

            List<LichHen> listBusy = lichHenBUS.getLichBacSiTheoNgay(maBS, sqlDate);
            modelSched.setRowCount(0);
            boolean daChonSlotDau = false;

            for (String[] slot : CA_KHAM_CHUAN) {
                String start = slot[0];
                String end = slot[1];
                LichHen busy = null;
                for (LichHen lh : listBusy) {
                    if (!(lh.getGioKetThuc().compareTo(start) <= 0 || lh.getGioBatDau().compareTo(end) >= 0)) {
                        busy = lh;
                        break;
                    }
                }
                if (busy != null) {
                    modelSched.addRow(new Object[]{start + " - " + end, "ĐÃ KÍN LỊCH", busy.getTenBN() + " (" + busy.getMaGhe() + ")"});
                } else {
                    modelSched.addRow(new Object[]{start + " - " + end, "TRỐNG LỊCH", "Nhấp để chọn"});
                    if (!daChonSlotDau) {
                        txtGioBatDau.setText(start);
                        txtGioKetThuc.setText(end);
                        lblSlotTip.setText("✓ Đã tự chọn ca trống gần nhất: " + start + " - " + end);
                        lblSlotTip.setForeground(UITheme.PRIMARY);
                        daChonSlotDau = true;
                    }
                }
            }
        };

        cboBacSi.addActionListener(e -> capNhatLichBS.run());
        txtNgayHen.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                capNhatLichBS.run();
            }
        });
        txtNgayHen.addActionListener(e -> capNhatLichBS.run());

        // Initialize schedule for initial doctor & date
        capNhatLichBS.run();

        // Bottom Actions
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);
        JButton btnCancel = UITheme.createButton("Đóng", Color.WHITE, UITheme.TEXT_DARK);
        btnCancel.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        btnCancel.addActionListener(e -> dlg.dispose());

        JButton btnSave = UITheme.createButton("Xác nhận đặt lịch", UITheme.PRIMARY, Color.WHITE);
        btnSave.setPreferredSize(new Dimension(180, 38));
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
                JOptionPane.showMessageDialog(dlg, "Lỗi: " + ex.getMessage(), "Lỗi đặt lịch", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlActions.add(btnCancel);
        pnlActions.add(btnSave);

        pnlRoot.add(pnlCenter, BorderLayout.CENTER);
        pnlRoot.add(pnlActions, BorderLayout.SOUTH);

        dlg.setContentPane(pnlRoot);
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
