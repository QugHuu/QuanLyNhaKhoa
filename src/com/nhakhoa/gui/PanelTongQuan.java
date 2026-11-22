package com.nhakhoa.gui;

import com.nhakhoa.dao.LichHenDAO;
import com.nhakhoa.dao.ThongKeDAO;
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

public class PanelTongQuan extends JPanel {
    private JLabel lblLuotKham;
    private JLabel lblBenhNhanCho;
    private JLabel lblDoanhThu;
    private JLabel lblCongNo;
    private JTable tblLichHenSapToi;
    private DefaultTableModel modelLichHen;

    private ThongKeDAO thongKeDAO = new ThongKeDAO();
    private LichHenDAO lichHenDAO = new LichHenDAO();
    private List<LichHen> cachedListLich = new java.util.ArrayList<>();
    private com.nhakhoa.model.TaiKhoan currentUser;

    public PanelTongQuan() {
        this(null);
    }

    public PanelTongQuan(com.nhakhoa.model.TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        setBackground(UITheme.BG_MAIN);

        // ==================== 1. PAGE HEADER ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTitle.setOpaque(false);
        JLabel lblHeading = new JLabel("Tổng quan phòng khám");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeading.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Xem nhanh tình trạng hoạt động hôm nay và các việc ưu tiên");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        pnlTitle.add(lblHeading);
        pnlTitle.add(lblSub);

        JPanel pnlHeaderActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlHeaderActions.setOpaque(false);

        JLabel lblToday = new JLabel(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        lblToday.setFont(UITheme.FONT_SMALL);
        lblToday.setForeground(UITheme.TEXT_MUTED);
        lblToday.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        pnlHeaderActions.add(lblToday);

        pnlHeader.add(pnlTitle, BorderLayout.WEST);
        pnlHeader.add(pnlHeaderActions, BorderLayout.EAST);

        // ==================== 2. ROW 4 KPI CARDS ====================
        JPanel pnlKPI = new JPanel(new GridLayout(1, 4, 14, 0));
        pnlKPI.setOpaque(false);

        lblLuotKham = new JLabel("0");
        lblBenhNhanCho = new JLabel("0");
        lblDoanhThu = new JLabel("0 đ");
        lblCongNo = new JLabel("0 đ");

        if (currentUser != null && currentUser.isBacSi()) {
            pnlKPI.add(taoCardKPI("Lịch hẹn của tôi", lblLuotKham, "Tổng ca hẹn phụ trách"));
            pnlKPI.add(taoCardKPI("Bệnh nhân chờ khám", lblBenhNhanCho, "Đang chờ tại phòng khám"));
            pnlKPI.add(taoCardKPI("Bác sĩ phụ trách", lblDoanhThu, "Bác sĩ điều trị"));
            pnlKPI.add(taoCardKPI("Chuyên khoa", lblCongNo, "Khoa Răng Hàm Mặt"));
        } else if (currentUser != null && currentUser.isKho()) {
            pnlKPI.add(taoCardKPI("Danh mục vật tư", lblLuotKham, "Mặt hàng trong kho"));
            pnlKPI.add(taoCardKPI("Lô khả dụng", lblBenhNhanCho, "Sẵn sàng xuất dùng"));
            pnlKPI.add(taoCardKPI("Lô cận hạn", lblDoanhThu, "Dưới 30 ngày"));
            pnlKPI.add(taoCardKPI("Cảnh báo tồn kho", lblCongNo, "Chạm ngưỡng an toàn"));
        } else {
            pnlKPI.add(taoCardKPI("Lượt khám hôm nay", lblLuotKham, "Tổng lịch khám"));
            pnlKPI.add(taoCardKPI("Bệnh nhân đang chờ", lblBenhNhanCho, "Tại sảnh phòng khám"));
            pnlKPI.add(taoCardKPI("Doanh thu hôm nay", lblDoanhThu, "Tiền mặt & chuyển khoản"));
            pnlKPI.add(taoCardKPI("Công nợ cần thu", lblCongNo, "Trả góp đến hạn"));
        }

        JPanel pnlTop = new JPanel(new BorderLayout(0, 14));
        pnlTop.setOpaque(false);
        pnlTop.add(pnlHeader, BorderLayout.NORTH);
        pnlTop.add(pnlKPI, BorderLayout.CENTER);

        // ==================== 3. BODY (LEFT TABLE + RIGHT ATTENTION) ====================
        JPanel pnlBody = new JPanel(new BorderLayout(14, 0));
        pnlBody.setOpaque(false);

        // CỘT TRÁI (Lịch hẹn sắp tới)
        JPanel pnlLeftCard = UITheme.createCard();
        pnlLeftCard.setLayout(new BorderLayout(0, 12));

        JPanel pnlLeftHeader = new JPanel(new BorderLayout());
        pnlLeftHeader.setOpaque(false);
        JLabel lblTableTitle = new JLabel("Lịch hẹn sắp tới");
        lblTableTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTableTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblFilter = new JLabel("Xem theo: Hôm nay ▼");
        lblFilter.setFont(UITheme.FONT_TINY);
        lblFilter.setForeground(UITheme.PRIMARY);

        pnlLeftHeader.add(lblTableTitle, BorderLayout.WEST);
        pnlLeftHeader.add(lblFilter, BorderLayout.EAST);

        String[] headers = {"Giờ hẹn", "Bệnh nhân", "Bác sĩ", "Ghế", "Trạng thái", "Hành động"};
        modelLichHen = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichHenSapToi = new JTable(modelLichHen);
        UITheme.formatTable(tblLichHenSapToi);
        tblLichHenSapToi.getColumnModel().getColumn(4).setCellRenderer(new UITheme.StatusBadgeRenderer());

        tblLichHenSapToi.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tblLichHenSapToi.getSelectedRow();
                if (row >= 0 && row < cachedListLich.size()) {
                    LichHen lh = cachedListLich.get(row);
                    if (!"DA_KHAM".equals(lh.getTinhTrang()) && !"DA_HUY".equals(lh.getTinhTrang())) {
                        if (currentUser != null && !currentUser.isLetan() && !currentUser.isQuanLy()) {
                            JOptionPane.showMessageDialog(PanelTongQuan.this,
                                    "Chức năng tiếp nhận bệnh nhân do Lễ tân thực hiện khi bệnh nhân đến sảnh phòng khám!",
                                    "Thông báo phân quyền", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        int opt = JOptionPane.showConfirmDialog(PanelTongQuan.this,
                                "Tiếp nhận bệnh nhân [" + lh.getTenBN() + "] vào phòng khám ngay?",
                                "Tiếp nhận bệnh nhân", JOptionPane.YES_NO_OPTION);
                        if (opt == JOptionPane.YES_OPTION) {
                            try {
                                lichHenDAO.doiTrangThai(lh.getMaLichHen(), "DA_DEN");
                                JOptionPane.showMessageDialog(PanelTongQuan.this, "Đã tiếp nhận bệnh nhân thành công! Hồ sơ đã được chuyển đến bác sĩ.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                                taiDuLieu();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(PanelTongQuan.this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        });

        pnlLeftCard.add(pnlLeftHeader, BorderLayout.NORTH);
        pnlLeftCard.add(new JScrollPane(tblLichHenSapToi), BorderLayout.CENTER);

        // CỘT PHẢI (Cần chú ý)
        JPanel pnlRightCard = UITheme.createCard();
        pnlRightCard.setLayout(new BorderLayout(0, 12));
        pnlRightCard.setPreferredSize(new Dimension(320, 0));

        JLabel lblAttTitle = new JLabel("Cần chú ý");
        lblAttTitle.setFont(UITheme.FONT_SUBTITLE);
        lblAttTitle.setForeground(UITheme.TEXT_DARK);

        JPanel pnlAttentionList = new JPanel(new GridLayout(3, 1, 0, 10));
        pnlAttentionList.setOpaque(false);

        if (currentUser != null && currentUser.isBacSi()) {
            pnlAttentionList.add(taoAlertItem("Bệnh nhân đang chờ khám", "Ưu tiên khám đúng giờ hẹn", UITheme.INFO_BG, UITheme.INFO_TEXT, "Vào khám", "KHAM_BENH"));
            pnlAttentionList.add(taoAlertItem("Lịch làm việc hôm nay", "Kiểm tra danh sách ca điều trị", UITheme.WARNING_BG, UITheme.WARNING_TEXT, "Xem lịch", "LICH_HEN"));
            pnlAttentionList.add(taoAlertItem("Hồ sơ bệnh nhân phụ trách", "Xem tiền sử dị ứng và kế hoạch", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, "Xem hồ sơ", "BENH_NHAN"));
        } else if (currentUser != null && currentUser.isKho()) {
            pnlAttentionList.add(taoAlertItem("03 lô vật tư cận hạn", "Khẩu trang, thuốc tê cận 30 ngày", UITheme.WARNING_BG, UITheme.WARNING_TEXT, "Xem kho", "KHO_VAT_TU"));
            pnlAttentionList.add(taoAlertItem("Vật tư chạm ngưỡng", "Có 02 vật tư dưới mức tối thiểu", UITheme.DANGER_BG, UITheme.DANGER_TEXT, "Kiểm kê", "KHO_VAT_TU"));
            pnlAttentionList.add(taoAlertItem("Nhập kho lô vật tư", "Cập nhật lô hàng mới nhập", UITheme.INFO_BG, UITheme.INFO_TEXT, "Nhập kho", "KHO_VAT_TU"));
        } else {
            pnlAttentionList.add(taoAlertItem("03 lô vật tư cận hạn", "Khẩu trang, thuốc tê cận 30 ngày", UITheme.WARNING_BG, UITheme.WARNING_TEXT, "Xem kho", "KHO_VAT_TU"));
            pnlAttentionList.add(taoAlertItem("Công nợ cần thu trong ngày", "Có 02 hồ sơ quá hạn trả góp", UITheme.INFO_BG, UITheme.INFO_TEXT, "Xem nợ", "HOA_DON"));
            pnlAttentionList.add(taoAlertItem("Kho kiểm kê định mức", "Có 02 vật tư dưới mức tối thiểu", UITheme.DANGER_BG, UITheme.DANGER_TEXT, "Chi tiết", "KHO_VAT_TU"));
        }

        pnlRightCard.add(lblAttTitle, BorderLayout.NORTH);
        pnlRightCard.add(pnlAttentionList, BorderLayout.CENTER);

        pnlBody.add(pnlLeftCard, BorderLayout.CENTER);
        pnlBody.add(pnlRightCard, BorderLayout.EAST);

        add(pnlTop, BorderLayout.NORTH);
        add(pnlBody, BorderLayout.CENTER);

        taiDuLieu();
    }

    private JPanel taoCardKPI(String title, JLabel lblValue, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblT.setForeground(UITheme.TEXT_MUTED);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(UITheme.FONT_TINY);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        card.add(lblT, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);

        return card;
    }

    private JPanel taoAlertItem(String title, String desc, Color bg, Color textCol, String actionText, String targetCard) {
        JPanel p = new JPanel(new BorderLayout(6, 2));
        p.setBackground(bg);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_BOLD);
        lblTitle.setForeground(textCol);

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(UITheme.FONT_TINY);
        lblDesc.setForeground(UITheme.TEXT_DARK);

        JLabel lblAction = new JLabel(actionText + " →", SwingConstants.RIGHT);
        lblAction.setFont(UITheme.FONT_TINY);
        lblAction.setForeground(textCol);
        lblAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblAction.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Window w = SwingUtilities.getWindowAncestor(PanelTongQuan.this);
                if (w instanceof MainFrame) {
                    ((MainFrame) w).navigateTo(targetCard);
                }
            }
        });

        p.add(lblTitle, BorderLayout.NORTH);
        p.add(lblDesc, BorderLayout.CENTER);
        p.add(lblAction, BorderLayout.SOUTH);

        return p;
    }

    public void taiDuLieu() {
        if (currentUser != null && currentUser.isBacSi()) {
            String myMaBS = currentUser.getMaBacSi();
            long countChoToi = lichHenDAO.getLichHenDaDen().stream()
                    .filter(l -> myMaBS == null || myMaBS.equals(l.getMaBS())).count();
            long countLichCuaToi = lichHenDAO.getAll().stream()
                    .filter(l -> myMaBS == null || myMaBS.equals(l.getMaBS())).count();
            lblLuotKham.setText(String.valueOf(countLichCuaToi));
            lblBenhNhanCho.setText(String.valueOf(countChoToi));
            lblDoanhThu.setText(currentUser.getHoTen());
            lblCongNo.setText("Răng Hàm Mặt");
        } else if (currentUser != null && currentUser.isKho()) {
            com.nhakhoa.dao.KhoDAO khoDAO = new com.nhakhoa.dao.KhoDAO();
            lblLuotKham.setText(String.valueOf(khoDAO.getAllVatTu().size()));
            lblBenhNhanCho.setText(String.valueOf(khoDAO.getAllLoVatTu().size()));
            lblDoanhThu.setText("03 lô");
            lblCongNo.setText("02 vật tư");
        } else {
            lblLuotKham.setText(String.valueOf(thongKeDAO.getSoLuongLichHenHomNay()));
            lblBenhNhanCho.setText(String.valueOf(lichHenDAO.getLichHenDaDen().size()));
            lblDoanhThu.setText(FormatUtil.formatMoney(thongKeDAO.getTongDoanhThu()));
            lblCongNo.setText("32.800.000 đ");
        }

        modelLichHen.setRowCount(0);
        List<LichHen> allList = lichHenDAO.getAll();
        String currentMaBS = (currentUser != null && currentUser.isBacSi()) ? currentUser.getMaBacSi() : null;
        cachedListLich = new java.util.ArrayList<>();

        boolean isTiepNhanRole = (currentUser == null || currentUser.isLetan() || currentUser.isQuanLy());
        for (LichHen lh : allList) {
            if (currentMaBS != null && !currentMaBS.equals(lh.getMaBS())) {
                continue;
            }
            cachedListLich.add(lh);
            String gio = FormatUtil.formatShortTime(lh.getGioBatDau()) + " - " + FormatUtil.formatShortTime(lh.getGioKetThuc());
            modelLichHen.addRow(new Object[]{
                    gio,
                    lh.getTenBN(),
                    lh.getTenBS(),
                    lh.getMaGhe(),
                    lh.getTinhTrang(),
                    isTiepNhanRole ? "Tiếp nhận  →" : "Xem  →"
            });
        }
    }
}
