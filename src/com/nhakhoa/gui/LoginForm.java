package com.nhakhoa.gui;

import com.nhakhoa.bus.TaiKhoanBUS;
import com.nhakhoa.model.TaiKhoan;
import com.nhakhoa.util.UITheme;

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
        setTitle("Đăng nhập hệ thống - Nha Khoa Sài Gòn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 540);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel pnlRoot = new JPanel(new BorderLayout());
        pnlRoot.setBackground(UITheme.BG_MAIN);

        // BANNER HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout(0, 6));
        pnlHeader.setBackground(UITheme.PRIMARY);
        pnlHeader.setPreferredSize(new Dimension(0, 100));
        pnlHeader.setBorder(new EmptyBorder(20, 24, 18, 24));

        JLabel lblLogo = new JLabel("NHA KHOA SÀI GÒN", JLabel.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("HỆ THỐNG QUẢN TRỊ NỘI BỘ PHÒNG KHÁM", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(204, 251, 241));

        pnlHeader.add(lblLogo, BorderLayout.NORTH);
        pnlHeader.add(lblSub, BorderLayout.CENTER);

        // CARD ĐĂNG NHẬP
        JPanel pnlCenterWrapper = new JPanel(new GridBagLayout());
        pnlCenterWrapper.setOpaque(false);
        pnlCenterWrapper.setBorder(new EmptyBorder(15, 25, 10, 25));

        JPanel card = UITheme.createCard();
        card.setLayout(new GridLayout(5, 1, 0, 10));
        card.setPreferredSize(new Dimension(420, 250));

        txtUsername = new JTextField("admin");
        txtUsername.setFont(UITheme.FONT_REGULAR);
        txtUsername.putClientProperty("JTextField.placeholderText", "Nhập tên tài khoản...");

        txtPassword = new JPasswordField("123456");
        txtPassword.setFont(UITheme.FONT_REGULAR);
        txtPassword.putClientProperty("JTextField.placeholderText", "Nhập mật khẩu...");

        JPanel pnlUser = new JPanel(new BorderLayout(4, 4));
        pnlUser.setOpaque(false);
        JLabel lblU = new JLabel("Tên đăng nhập:");
        lblU.setFont(UITheme.FONT_BOLD);
        lblU.setForeground(UITheme.TEXT_DARK);
        pnlUser.add(lblU, BorderLayout.NORTH);
        pnlUser.add(txtUsername, BorderLayout.CENTER);

        JPanel pnlPass = new JPanel(new BorderLayout(4, 4));
        pnlPass.setOpaque(false);
        JLabel lblP = new JLabel("Mật khẩu:");
        lblP.setFont(UITheme.FONT_BOLD);
        lblP.setForeground(UITheme.TEXT_DARK);
        pnlPass.add(lblP, BorderLayout.NORTH);
        pnlPass.add(txtPassword, BorderLayout.CENTER);

        btnLogin = UITheme.createButton("ĐĂNG NHẬP", UITheme.PRIMARY, Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(0, 42));
        btnLogin.addActionListener(e -> xuLyDangNhap());

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

        card.add(pnlUser);
        card.add(pnlPass);
        card.add(new JLabel(""));
        card.add(btnLogin);

        pnlCenterWrapper.add(card);

        // BOTTOM: Chọn nhanh tài khoản demo
        JPanel pnlQuick = new JPanel(new BorderLayout(0, 8));
        pnlQuick.setOpaque(false);
        pnlQuick.setBorder(new EmptyBorder(0, 25, 20, 25));

        JLabel lblQuickTitle = new JLabel("Chọn nhanh tài khoản kiểm thử đồ án:", JLabel.CENTER);
        lblQuickTitle.setFont(UITheme.FONT_SMALL);
        lblQuickTitle.setForeground(UITheme.TEXT_MUTED);

        JPanel pnlQuickBtns = new JPanel(new GridLayout(2, 2, 8, 8));
        pnlQuickBtns.setOpaque(false);

        JButton btnAdmin = createQuickBtn("Quản lý (admin)", "admin");
        JButton btnLeTan = createQuickBtn("Lễ tân (letan)", "letan");
        JButton btnBacSi = createQuickBtn("Bác sĩ (bacsi)", "bacsi");
        JButton btnThuKho = createQuickBtn("Thủ kho (thukho)", "thukho");

        pnlQuickBtns.add(btnAdmin);
        pnlQuickBtns.add(btnLeTan);
        pnlQuickBtns.add(btnBacSi);
        pnlQuickBtns.add(btnThuKho);

        pnlQuick.add(lblQuickTitle, BorderLayout.NORTH);
        pnlQuick.add(pnlQuickBtns, BorderLayout.CENTER);

        pnlRoot.add(pnlHeader, BorderLayout.NORTH);
        pnlRoot.add(pnlCenterWrapper, BorderLayout.CENTER);
        pnlRoot.add(pnlQuick, BorderLayout.SOUTH);

        setContentPane(pnlRoot);
    }

    private JButton createQuickBtn(String text, String user) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_SMALL);
        btn.setBackground(Color.WHITE);
        btn.setForeground(UITheme.TEXT_DARK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.addActionListener(e -> {
            txtUsername.setText(user);
            txtPassword.setText("123456");
        });
        return btn;
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
