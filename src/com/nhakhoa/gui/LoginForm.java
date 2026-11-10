package com.nhakhoa.gui;

import com.nhakhoa.bus.TaiKhoanBUS;
import com.nhakhoa.model.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginForm extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    private TaiKhoanBUS taiKhoanBUS = new TaiKhoanBUS();

    public LoginForm() {
        setTitle("ĐĂNG NHẬP HỆ THỐNG - NHA KHOA SÀI GÒN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 480);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel pnlRoot = new JPanel(new BorderLayout());
        pnlRoot.setBackground(Color.WHITE);

        // TOP HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(25, 42, 86));
        pnlHeader.setPreferredSize(new Dimension(0, 90));
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("🦷 NHA KHOA SÀI GÒN", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("HỆ THỐNG QUẢN LÝ PHÒNG KHÁM NHA KHOA NỘI BỘ", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(220, 230, 242));

        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        pnlHeader.add(lblSub, BorderLayout.CENTER);

        // CENTER FORM
        JPanel pnlCenter = new JPanel(new GridLayout(4, 1, 0, 8));
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(new EmptyBorder(20, 45, 10, 45));

        txtUsername = new JTextField("admin");
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtPassword = new JPasswordField("123456");
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel pnlUser = new JPanel(new BorderLayout(5, 5));
        pnlUser.setOpaque(false);
        pnlUser.add(new JLabel("Tên đăng nhập:"), BorderLayout.NORTH);
        pnlUser.add(txtUsername, BorderLayout.CENTER);

        JPanel pnlPass = new JPanel(new BorderLayout(5, 5));
        pnlPass.setOpaque(false);
        pnlPass.add(new JLabel("Mật khẩu:"), BorderLayout.NORTH);
        pnlPass.add(txtPassword, BorderLayout.CENTER);

        btnLogin = new JButton("ĐĂNG NHẬP VÀO HỆ THỐNG");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setPreferredSize(new Dimension(0, 42));
        btnLogin.addActionListener(e -> xuLyDangNhap());

        // Hỗ trợ ấn Enter để đăng nhập
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    xuLyDangNhap();
                }
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);

        pnlCenter.add(pnlUser);
        pnlCenter.add(pnlPass);
        pnlCenter.add(new JLabel("")); // khoảng trống
        pnlCenter.add(btnLogin);

        // BOTTOM: Các nút chọn nhanh tài khoản mẫu (Rất tiện cho chấm đồ án!)
        JPanel pnlQuick = new JPanel(new BorderLayout(5, 5));
        pnlQuick.setOpaque(false);
        pnlQuick.setBorder(new EmptyBorder(10, 30, 20, 30));

        JLabel lblQuick = new JLabel("Chọn nhanh tài khoản thử nghiệm:", JLabel.CENTER);
        lblQuick.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblQuick.setForeground(new Color(100, 110, 120));

        JPanel pnlQuickBtns = new JPanel(new GridLayout(2, 2, 8, 8));
        pnlQuickBtns.setOpaque(false);

        JButton btnAdmin = new JButton("👨‍💼 Quản lý (admin)");
        JButton btnLeTan = new JButton("👩‍💼 Lễ tân (letan)");
        JButton btnBacSi = new JButton("👨‍⚕️ Bác sĩ (bacsi)");
        JButton btnThuKho = new JButton("📦 Thủ kho (thukho)");

        btnAdmin.addActionListener(e -> { txtUsername.setText("admin"); txtPassword.setText("123456"); });
        btnLeTan.addActionListener(e -> { txtUsername.setText("letan"); txtPassword.setText("123456"); });
        btnBacSi.addActionListener(e -> { txtUsername.setText("bacsi"); txtPassword.setText("123456"); });
        btnThuKho.addActionListener(e -> { txtUsername.setText("thukho"); txtPassword.setText("123456"); });

        pnlQuickBtns.add(btnAdmin);
        pnlQuickBtns.add(btnLeTan);
        pnlQuickBtns.add(btnBacSi);
        pnlQuickBtns.add(btnThuKho);

        pnlQuick.add(lblQuick, BorderLayout.NORTH);
        pnlQuick.add(pnlQuickBtns, BorderLayout.CENTER);

        pnlRoot.add(pnlHeader, BorderLayout.NORTH);
        pnlRoot.add(pnlCenter, BorderLayout.CENTER);
        pnlRoot.add(pnlQuick, BorderLayout.SOUTH);

        setContentPane(pnlRoot);
    }

    private void xuLyDangNhap() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        try {
            TaiKhoan tk = taiKhoanBUS.dangNhap(username, password);
            JOptionPane.showMessageDialog(this,
                    "Đăng nhập thành công!\nXin chào: " + tk.getHoTen() + " (" + tk.getTenVaiTro() + ")",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            this.dispose();
            SwingUtilities.invokeLater(() -> {
                MainFrame main = new MainFrame(tk);
                main.setVisible(true);
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đăng nhập (RB05)", JOptionPane.ERROR_MESSAGE);
        }
    }
}
