package com.nhakhoa.gui;

import com.nhakhoa.model.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private TaiKhoan currentUser;
    private JPanel pnlContent;
    private CardLayout cardLayout;

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
        setTitle("HỆ THỐNG QUẢN LÝ PHÒNG KHÁM NHA KHOA - SÀI GÒN DENTAL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 760);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel pnlRoot = new JPanel(new BorderLayout());

        // TOP HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(25, 42, 86));
        pnlHeader.setPreferredSize(new Dimension(0, 60));
        pnlHeader.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblLogo = new JLabel("🦷 NHA KHOA SÀI GÒN - HỆ THỐNG QUẢN LÝ NỘI BỘ");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(Color.WHITE);

        JPanel pnlUser = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        pnlUser.setOpaque(false);

        String info = "👤 " + currentUser.getHoTen() + " [" + currentUser.getTenVaiTro() + "]";
        JLabel lblUser = new JLabel(info);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(new Color(220, 230, 242));

        JButton btnDangXuat = new JButton("🚪 Đăng xuất");
        btnDangXuat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDangXuat.setBackground(new Color(231, 76, 60));
        btnDangXuat.setForeground(Color.BLACK);
        btnDangXuat.addActionListener(e -> xuLyDangXuat());

        pnlUser.add(lblUser);
        pnlUser.add(btnDangXuat);

        pnlHeader.add(lblLogo, BorderLayout.WEST);
        pnlHeader.add(pnlUser, BorderLayout.EAST);

        // LEFT SIDEBAR
        JPanel pnlSidebar = new JPanel(new GridLayout(9, 1, 0, 5));
        pnlSidebar.setBackground(new Color(39, 60, 117));
        pnlSidebar.setPreferredSize(new Dimension(230, 0));
        pnlSidebar.setBorder(new EmptyBorder(10, 8, 10, 8));

        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);

        // Khởi tạo các panel chức năng
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

        // Các nút Sidebar Menu (Phân quyền RBAC)
        JButton btnTongQuan = taoNutMenu("📊 Tổng quan hệ thống", "TONG_QUAN");
        JButton btnBenhNhan = taoNutMenu("👥 Hồ sơ bệnh nhân", "BENH_NHAN");
        JButton btnLichHen = taoNutMenu("📅 Lịch hẹn khám", "LICH_HEN");
        JButton btnKhamBenh = taoNutMenu("🩺 Khám & Điều trị", "KHAM_BENH");
        JButton btnHoaDon = taoNutMenu("💰 Hóa đơn & Công nợ", "HOA_DON");
        JButton btnKho = taoNutMenu("📦 Kho vật tư (FEFO)", "KHO_VAT_TU");
        JButton btnDichVu = taoNutMenu("🛠️ Bảng giá dịch vụ", "DICH_VU");
        JButton btnThongKe = taoNutMenu("📈 Báo cáo thống kê", "THONG_KE");

        pnlSidebar.add(btnTongQuan);

        // RBAC: Kiểm tra vai trò để phân quyền bật/tắt nút menu
        boolean isQL = currentUser.isQuanLy();
        boolean isLT = currentUser.isLetan();
        boolean isBS = currentUser.isBacSi();
        boolean isKho = currentUser.isKho();

        if (isQL || isLT || isBS) pnlSidebar.add(btnBenhNhan);
        if (isQL || isLT || isBS) pnlSidebar.add(btnLichHen);
        if (isQL || isBS) pnlSidebar.add(btnKhamBenh);
        if (isQL || isLT) pnlSidebar.add(btnHoaDon);
        if (isQL || isKho) pnlSidebar.add(btnKho);
        if (isQL) pnlSidebar.add(btnDichVu);
        if (isQL) pnlSidebar.add(btnThongKe);

        pnlRoot.add(pnlHeader, BorderLayout.NORTH);
        pnlRoot.add(pnlSidebar, BorderLayout.WEST);
        pnlRoot.add(pnlContent, BorderLayout.CENTER);

        setContentPane(pnlRoot);
    }

    private JButton taoNutMenu(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.BLACK);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 75, 140)),
                new EmptyBorder(8, 12, 8, 12)
        ));

        btn.addActionListener(e -> {
            cardLayout.show(pnlContent, cardName);
            // Tải lại dữ liệu khi bấm vào tab
            if ("TONG_QUAN".equals(cardName)) panelTongQuan.taiDuLieu();
            else if ("LICH_HEN".equals(cardName)) panelLichHen.taiDuLieu();
            else if ("KHAM_BENH".equals(cardName)) panelKhamBenh.taiDanhSachHangDoi();
            else if ("HOA_DON".equals(cardName)) panelHoaDon.taiDuLieu();
            else if ("KHO_VAT_TU".equals(cardName)) panelKhoVatTu.taiDuLieu();
            else if ("DICH_VU".equals(cardName)) panelDichVu.taiDuLieu();
            else if ("THONG_KE".equals(cardName)) panelThongKe.taiDuLieu();
        });

        return btn;
    }

    private void xuLyDangXuat() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Đăng xuất", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
        }
    }
}
