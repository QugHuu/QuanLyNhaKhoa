package com.nhakhoa.gui;

import com.nhakhoa.model.TaiKhoan;
import com.nhakhoa.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private TaiKhoan currentUser;
    private JPanel pnlContent;
    private CardLayout cardLayout;
    private List<JButton> menuButtons = new ArrayList<>();
    private java.util.Map<String, JButton> navButtonMap = new java.util.HashMap<>();

    private PanelTongQuan panelTongQuan;
    private PanelBenhNhan panelBenhNhan;
    private PanelLichHen panelLichHen;
    private PanelKhamBenh panelKhamBenh;
    private PanelHoaDon panelHoaDon;
    private PanelKhoVatTu panelKhoVatTu;
    private PanelDichVu panelDichVu;
    private PanelThongKe panelThongKe;

    public MainFrame(TaiKhoan user) {
        this.currentUser = user;
        setTitle("NHA KHOA - Hệ Thống Quản Lý Phòng Khám");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1360, 820);
        setMinimumSize(new Dimension(1180, 720));
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel pnlRoot = new JPanel(new BorderLayout());
        pnlRoot.setBackground(UITheme.BG_MAIN);

        // ==================== SIDEBAR TRẮNG HIỆN ĐẠI (Theo Mockup) ====================
        JPanel pnlSidebar = new JPanel(new BorderLayout());
        pnlSidebar.setBackground(UITheme.SIDEBAR_BG);
        pnlSidebar.setPreferredSize(new Dimension(220, 0));
        pnlSidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER_COLOR));

        // 1. Logo & Cơ sở
        JPanel pnlSidebarTop = new JPanel(new BorderLayout(0, 10));
        pnlSidebarTop.setOpaque(false);
        pnlSidebarTop.setBorder(new EmptyBorder(18, 16, 12, 16));

        JPanel pnlLogoBox = new JPanel(new BorderLayout(0, 2));
        pnlLogoBox.setOpaque(false);
        JLabel lblLogoText = new JLabel("NHA KHOA");
        lblLogoText.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblLogoText.setForeground(UITheme.PRIMARY);

        JLabel lblLogoSub = new JLabel("HỆ THỐNG QUẢN TRỊ");
        lblLogoSub.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblLogoSub.setForeground(UITheme.TEXT_MUTED);

        pnlLogoBox.add(lblLogoText, BorderLayout.NORTH);
        pnlLogoBox.add(lblLogoSub, BorderLayout.CENTER);

        // Clinic Branch Chip
        JPanel pnlBranch = new JPanel(new BorderLayout(4, 0));
        pnlBranch.setBackground(new Color(248, 250, 252));
        pnlBranch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        JLabel lblBranch = new JLabel("Cơ sở chính - TP. HCM");
        lblBranch.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblBranch.setForeground(UITheme.TEXT_DARK);
        pnlBranch.add(lblBranch, BorderLayout.CENTER);

        pnlSidebarTop.add(pnlLogoBox, BorderLayout.NORTH);
        pnlSidebarTop.add(pnlBranch, BorderLayout.CENTER);

        // 2. Navigation Items
        JPanel pnlNav = new JPanel(new GridLayout(8, 1, 0, 4));
        pnlNav.setOpaque(false);
        pnlNav.setBorder(new EmptyBorder(8, 10, 0, 10));

        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);
        pnlContent.setBackground(UITheme.BG_MAIN);

        // Khởi tạo các panels
        panelTongQuan = new PanelTongQuan();
        panelBenhNhan = new PanelBenhNhan();
        panelLichHen = new PanelLichHen();
        panelKhamBenh = new PanelKhamBenh();
        panelHoaDon = new PanelHoaDon();
        panelKhoVatTu = new PanelKhoVatTu();
        panelDichVu = new PanelDichVu();
        panelThongKe = new PanelThongKe();

        pnlContent.add(panelTongQuan, "TONG_QUAN");
        pnlContent.add(panelLichHen, "LICH_HEN");
        pnlContent.add(panelBenhNhan, "BENH_NHAN");
        pnlContent.add(panelKhamBenh, "KHAM_BENH");
        pnlContent.add(panelHoaDon, "HOA_DON");
        pnlContent.add(panelKhoVatTu, "KHO_VAT_TU");
        pnlContent.add(panelDichVu, "DICH_VU");
        pnlContent.add(panelThongKe, "THONG_KE");

        // RBAC Permissions
        boolean isQL = currentUser.isQuanLy();
        boolean isLT = currentUser.isLetan();
        boolean isBS = currentUser.isBacSi();
        boolean isKho = currentUser.isKho();

        pnlNav.add(taoNutNav("Tổng quan", "TONG_QUAN"));
        if (isQL || isLT || isBS) pnlNav.add(taoNutNav("Lịch hẹn", "LICH_HEN"));
        if (isQL || isLT || isBS) pnlNav.add(taoNutNav("Bệnh nhân", "BENH_NHAN"));
        if (isQL || isBS) pnlNav.add(taoNutNav("Khám & điều trị", "KHAM_BENH"));
        if (isQL || isLT) pnlNav.add(taoNutNav("Thanh toán & công nợ", "HOA_DON"));
        if (isQL || isKho) pnlNav.add(taoNutNav("Kho vật tư", "KHO_VAT_TU"));
        if (isQL) pnlNav.add(taoNutNav("Bảng giá dịch vụ", "DICH_VU"));
        if (isQL) pnlNav.add(taoNutNav("Báo cáo & thống kê", "THONG_KE"));

        // 3. Sidebar Bottom: Help Card & Logout
        JPanel pnlSidebarBottom = new JPanel(new BorderLayout(0, 10));
        pnlSidebarBottom.setOpaque(false);
        pnlSidebarBottom.setBorder(new EmptyBorder(0, 12, 16, 12));

        JPanel pnlHelpCard = new JPanel(new BorderLayout(0, 4));
        pnlHelpCard.setBackground(new Color(240, 253, 250)); // Teal 50
        pnlHelpCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 251, 241), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblH1 = new JLabel("Cần hỗ trợ?");
        lblH1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblH1.setForeground(UITheme.PRIMARY);

        JLabel lblH2 = new JLabel("Xem hướng dẫn sử dụng");
        lblH2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblH2.setForeground(UITheme.TEXT_MUTED);

        pnlHelpCard.add(lblH1, BorderLayout.NORTH);
        pnlHelpCard.add(lblH2, BorderLayout.CENTER);
        pnlHelpCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlHelpCard.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this,
                        "HƯỚNG DẪN SỬ DỤNG NHANH HỆ THỐNG NHA KHOA:\n\n"
                        + "1. Lễ tân: Tiếp nhận bệnh nhân, Đặt lịch hẹn (chống trùng RB07), Lập hóa đơn & Thu trả góp (RB15).\n"
                        + "2. Bác sĩ: Khám & điều trị trên Sơ đồ răng 32 chiếc, Kê đơn thuốc, Cảnh báo dị ứng (RB01).\n"
                        + "3. Thủ kho: Quản lý lô vật tư theo nguyên tắc FEFO - Hạn gần xuất trước (RB11, RB12).\n"
                        + "4. Quản lý: Xem biểu đồ doanh thu, hiệu suất bác sĩ, xuất báo cáo Excel UTF-8.\n\n"
                        + "Xem chi tiết kịch bản bảo vệ trong file: HUONG_DAN_BAO_VE_DO_AN.md",
                        "Hướng dẫn sử dụng", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLogout.setForeground(new Color(220, 38, 38));
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(BorderFactory.createLineBorder(new Color(254, 202, 202), 1, true));
        btnLogout.setPreferredSize(new Dimension(0, 32));
        btnLogout.addActionListener(e -> xuLyDangXuat());

        pnlSidebarBottom.add(pnlHelpCard, BorderLayout.NORTH);
        pnlSidebarBottom.add(btnLogout, BorderLayout.SOUTH);

        pnlSidebar.add(pnlSidebarTop, BorderLayout.NORTH);
        pnlSidebar.add(pnlNav, BorderLayout.CENTER);
        pnlSidebar.add(pnlSidebarBottom, BorderLayout.SOUTH);

        // ==================== TOP HEADER (Thanh tìm kiếm & Profile) ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setPreferredSize(new Dimension(0, 56));
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(8, 20, 8, 24)
        ));

        // Ô tìm kiếm toàn hệ thống
        JTextField txtGlobalSearch = new JTextField();
        txtGlobalSearch.setPreferredSize(new Dimension(380, 36));
        txtGlobalSearch.setFont(UITheme.FONT_REGULAR);
        txtGlobalSearch.putClientProperty("JTextField.placeholderText", "Tìm bệnh nhân, số điện thoại, mã hồ sơ... (Nhấn Enter)");
        txtGlobalSearch.putClientProperty("JComponent.roundRect", true);
        txtGlobalSearch.addActionListener(e -> {
            String kw = txtGlobalSearch.getText().trim();
            if (!kw.isEmpty()) {
                navigateTo("BENH_NHAN");
                panelBenhNhan.timKiemTuBenNgoai(kw);
            }
        });

        // Profile user bên phải
        JPanel pnlHeaderRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 2));
        pnlHeaderRight.setOpaque(false);

        JPanel pnlUserBadge = new JPanel(new BorderLayout(8, 0));
        pnlUserBadge.setOpaque(false);

        JPanel pnlUserText = new JPanel(new GridLayout(2, 1, 0, 1));
        pnlUserText.setOpaque(false);
        JLabel lblUName = new JLabel(currentUser.getHoTen(), SwingConstants.RIGHT);
        lblUName.setFont(UITheme.FONT_BOLD);
        lblUName.setForeground(UITheme.TEXT_DARK);

        JLabel lblURole = new JLabel(currentUser.getTenVaiTro(), SwingConstants.RIGHT);
        lblURole.setFont(UITheme.FONT_TINY);
        lblURole.setForeground(UITheme.TEXT_MUTED);

        pnlUserText.add(lblUName);
        pnlUserText.add(lblURole);

        JLabel lblAvatarCircle = new JLabel(" " + currentUser.getHoTen().substring(0, 1) + " ", JLabel.CENTER);
        lblAvatarCircle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAvatarCircle.setOpaque(true);
        lblAvatarCircle.setBackground(UITheme.PRIMARY_LIGHT);
        lblAvatarCircle.setForeground(UITheme.PRIMARY);
        lblAvatarCircle.setBorder(BorderFactory.createLineBorder(new Color(153, 246, 228), 1, true));

        pnlUserBadge.add(pnlUserText, BorderLayout.CENTER);
        pnlUserBadge.add(lblAvatarCircle, BorderLayout.EAST);

        pnlHeaderRight.add(pnlUserBadge);

        pnlHeader.add(txtGlobalSearch, BorderLayout.WEST);
        pnlHeader.add(pnlHeaderRight, BorderLayout.EAST);

        // ==================== MAIN CONTENT ====================
        JPanel pnlMainArea = new JPanel(new BorderLayout());
        pnlMainArea.add(pnlHeader, BorderLayout.NORTH);
        pnlMainArea.add(pnlContent, BorderLayout.CENTER);

        pnlRoot.add(pnlSidebar, BorderLayout.WEST);
        pnlRoot.add(pnlMainArea, BorderLayout.CENTER);

        setContentPane(pnlRoot);

        // Chọn tab đầu tiên mặc định
        if (!menuButtons.isEmpty()) {
            menuButtons.get(0).doClick();
        }
    }

    private JButton taoNutNav(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_REGULAR);
        btn.setForeground(new Color(71, 85, 105));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));

        btn.addActionListener(e -> {
            cardLayout.show(pnlContent, cardName);

            // Active tab style
            for (JButton b : menuButtons) {
                b.setBackground(Color.WHITE);
                b.setForeground(new Color(71, 85, 105));
                b.setFont(UITheme.FONT_REGULAR);
            }
            btn.setBackground(UITheme.PRIMARY_LIGHT);
            btn.setForeground(UITheme.PRIMARY);
            btn.setFont(UITheme.FONT_BOLD);

            // Reload data
            if ("TONG_QUAN".equals(cardName)) panelTongQuan.taiDuLieu();
            else if ("LICH_HEN".equals(cardName)) panelLichHen.taiDuLieu();
            else if ("BENH_NHAN".equals(cardName)) panelBenhNhan.taiDuLieu();
            else if ("KHAM_BENH".equals(cardName)) panelKhamBenh.taiDanhSachHangDoi();
            else if ("HOA_DON".equals(cardName)) panelHoaDon.taiDuLieu();
            else if ("KHO_VAT_TU".equals(cardName)) panelKhoVatTu.taiDuLieu();
            else if ("DICH_VU".equals(cardName)) panelDichVu.taiDuLieu();
            else if ("THONG_KE".equals(cardName)) panelThongKe.taiDuLieu();
        });

        menuButtons.add(btn);
        navButtonMap.put(cardName, btn);
        return btn;
    }

    public void navigateTo(String cardName) {
        JButton btn = navButtonMap.get(cardName);
        if (btn != null) {
            btn.doClick();
        } else {
            JOptionPane.showMessageDialog(this, "Tài khoản [" + currentUser.getTenVaiTro() + "] không có quyền truy cập phân hệ này!", "Phân quyền (RBAC)", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void xuLyDangXuat() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
        }
    }
}
