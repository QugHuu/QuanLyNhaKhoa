package com.nhakhoa.gui;

import com.nhakhoa.model.TaiKhoan;
import com.nhakhoa.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainFrame extends JFrame {
    private TaiKhoan currentUser;
    private JPanel pnlContent;
    private CardLayout cardLayout;
    private JLabel lblPageTitle;
    private List<JButton> menuButtons = new ArrayList<>();

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
        setTitle("Hệ Thống Quản Lý Phòng Khám Nha Khoa - Sài Gòn Dental");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1320, 800);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel pnlRoot = new JPanel(new BorderLayout());
        pnlRoot.setBackground(UITheme.BG_MAIN);

        // ==================== SIDEBAR TRÁI ====================
        JPanel pnlSidebar = new JPanel(new BorderLayout());
        pnlSidebar.setBackground(new Color(15, 23, 42)); // Slate 900
        pnlSidebar.setPreferredSize(new Dimension(250, 0));
        pnlSidebar.setBorder(new EmptyBorder(16, 12, 16, 12));

        // 1. Logo & Thương hiệu
        JPanel pnlBrand = new JPanel(new BorderLayout(0, 4));
        pnlBrand.setOpaque(false);
        pnlBrand.setBorder(new EmptyBorder(6, 8, 18, 8));

        JLabel lblBrandName = new JLabel("🦷 SÀI GÒN DENTAL");
        lblBrandName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBrandName.setForeground(Color.WHITE);

        JLabel lblBrandSub = new JLabel("Hệ thống quản lý phòng khám");
        lblBrandSub.setFont(UITheme.FONT_SMALL);
        lblBrandSub.setForeground(new Color(148, 163, 184));

        pnlBrand.add(lblBrandName, BorderLayout.NORTH);
        pnlBrand.add(lblBrandSub, BorderLayout.CENTER);

        // 2. User Info Card
        JPanel pnlUser = new JPanel(new BorderLayout(8, 4));
        pnlUser.setBackground(new Color(30, 41, 59));
        pnlUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel lblAvatar = new JLabel("👤");
        lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        JPanel pnlUserText = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlUserText.setOpaque(false);

        JLabel lblUserName = new JLabel(currentUser.getHoTen());
        lblUserName.setFont(UITheme.FONT_BOLD);
        lblUserName.setForeground(Color.WHITE);

        JLabel lblUserRole = new JLabel(currentUser.getTenVaiTro());
        lblUserRole.setFont(UITheme.FONT_SMALL);
        lblUserRole.setForeground(UITheme.PRIMARY_LIGHT);

        pnlUserText.add(lblUserName);
        pnlUserText.add(lblUserRole);

        pnlUser.add(lblAvatar, BorderLayout.WEST);
        pnlUser.add(pnlUserText, BorderLayout.CENTER);

        JPanel pnlSidebarTop = new JPanel(new BorderLayout(0, 12));
        pnlSidebarTop.setOpaque(false);
        pnlSidebarTop.add(pnlBrand, BorderLayout.NORTH);
        pnlSidebarTop.add(pnlUser, BorderLayout.CENTER);

        // 3. Navigation Menu Items
        JPanel pnlNav = new JPanel(new GridLayout(9, 1, 0, 6));
        pnlNav.setOpaque(false);
        pnlNav.setBorder(new EmptyBorder(14, 0, 0, 0));

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
        pnlContent.add(panelBenhNhan, "BENH_NHAN");
        pnlContent.add(panelLichHen, "LICH_HEN");
        pnlContent.add(panelKhamBenh, "KHAM_BENH");
        pnlContent.add(panelHoaDon, "HOA_DON");
        pnlContent.add(panelKhoVatTu, "KHO_VAT_TU");
        pnlContent.add(panelDichVu, "DICH_VU");
        pnlContent.add(panelThongKe, "THONG_KE");

        // Các nút phân quyền RBAC
        boolean isQL = currentUser.isQuanLy();
        boolean isLT = currentUser.isLetan();
        boolean isBS = currentUser.isBacSi();
        boolean isKho = currentUser.isKho();

        pnlNav.add(taoNutNav("📊  Tổng quan hệ thống", "TONG_QUAN", "BẢNG TỔNG QUAN ĐIỀU HÀNH"));
        if (isQL || isLT || isBS) pnlNav.add(taoNutNav("👥  Hồ sơ bệnh nhân", "BENH_NHAN", "QUẢN LÝ HỒ SƠ BỆNH NHÂN"));
        if (isQL || isLT || isBS) pnlNav.add(taoNutNav("📅  Lịch hẹn khám", "LICH_HEN", "QUẢN LÝ LỊCH HẸN & TIẾP NHẬN BỆNH NHÂN"));
        if (isQL || isBS) pnlNav.add(taoNutNav("🩺  Khám & Điều trị", "KHAM_BENH", "KHÁM BỆNH, CHỈ ĐỊNH DỊCH VỤ & KÊ ĐƠN THUỐC"));
        if (isQL || isLT) pnlNav.add(taoNutNav("💰  Hóa đơn & Công nợ", "HOA_DON", "THANH TOÁN HÓA ĐƠN & SỔ NỢ TRẢ GÓP"));
        if (isQL || isKho) pnlNav.add(taoNutNav("📦  Kho vật tư (FEFO)", "KHO_VAT_TU", "QUẢN LÝ KHO & THEO DÕI HẠN DÙNG FEFO"));
        if (isQL) pnlNav.add(taoNutNav("🛠️  Bảng giá dịch vụ", "DICH_VU", "DANH MỤC THỦ THUẬT & BẢNG GIÁ NIÊM YẾT"));
        if (isQL) pnlNav.add(taoNutNav("📈  Báo cáo thống kê", "THONG_KE", "BÁO CÁO DOANH THU & CHỈ SỐ HOẠT ĐỘNG"));

        // 4. Logout Button ở đáy Sidebar
        JButton btnLogout = new JButton("🚪  Đăng xuất");
        btnLogout.setFont(UITheme.FONT_BOLD);
        btnLogout.setForeground(new Color(248, 113, 113));
        btnLogout.setBackground(new Color(30, 41, 59));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.putClientProperty("JButton.buttonType", "roundRect");
        btnLogout.setBorder(new EmptyBorder(10, 14, 10, 14));
        btnLogout.addActionListener(e -> xuLyDangXuat());

        pnlSidebar.add(pnlSidebarTop, BorderLayout.NORTH);
        pnlSidebar.add(pnlNav, BorderLayout.CENTER);
        pnlSidebar.add(btnLogout, BorderLayout.SOUTH);

        // ==================== HEADER TRÊN ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setPreferredSize(new Dimension(0, 56));
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(10, 24, 10, 24)
        ));

        lblPageTitle = new JLabel("BẢNG TỔNG QUAN ĐIỀU HÀNH");
        lblPageTitle.setFont(UITheme.FONT_TITLE);
        lblPageTitle.setForeground(UITheme.TEXT_DARK);

        JPanel pnlHeaderRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 4));
        pnlHeaderRight.setOpaque(false);

        JLabel lblDate = new JLabel("📅 " + new SimpleDateFormat("EEEE, dd/MM/yyyy").format(new Date()));
        lblDate.setFont(UITheme.FONT_REGULAR);
        lblDate.setForeground(UITheme.TEXT_MUTED);

        JLabel lblStatus = new JLabel("🟢 CSDL Sẵn sàng");
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(new Color(16, 185, 129));
        lblStatus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(167, 243, 208), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));

        pnlHeaderRight.add(lblDate);
        pnlHeaderRight.add(lblStatus);

        pnlHeader.add(lblPageTitle, BorderLayout.WEST);
        pnlHeader.add(pnlHeaderRight, BorderLayout.EAST);

        // ==================== GHÉP LAYOUT ====================
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

    private JButton taoNutNav(String text, String cardName, String pageTitle) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BOLD);
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(new Color(15, 23, 42));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));

        btn.addActionListener(e -> {
            cardLayout.show(pnlContent, cardName);
            lblPageTitle.setText(pageTitle);

            // Cập nhật trạng thái active cho các nút menu
            for (JButton b : menuButtons) {
                b.setBackground(new Color(15, 23, 42));
                b.setForeground(new Color(203, 213, 225));
            }
            btn.setBackground(UITheme.PRIMARY);
            btn.setForeground(Color.WHITE);

            // Tự động tải lại dữ liệu khi chuyển tab
            if ("TONG_QUAN".equals(cardName)) panelTongQuan.taiDuLieu();
            else if ("LICH_HEN".equals(cardName)) panelLichHen.taiDuLieu();
            else if ("KHAM_BENH".equals(cardName)) panelKhamBenh.taiDanhSachHangDoi();
            else if ("HOA_DON".equals(cardName)) panelHoaDon.taiDuLieu();
            else if ("KHO_VAT_TU".equals(cardName)) panelKhoVatTu.taiDuLieu();
            else if ("DICH_VU".equals(cardName)) panelDichVu.taiDuLieu();
            else if ("THONG_KE".equals(cardName)) panelThongKe.taiDuLieu();
        });

        menuButtons.add(btn);
        return btn;
    }

    private void xuLyDangXuat() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
        }
    }
}
