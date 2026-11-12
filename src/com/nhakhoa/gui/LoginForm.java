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
        setTitle("Đăng nhập - Hệ thống quản lý phòng khám Nha Khoa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel pnlRoot = new JPanel(new GridBagLayout());
        pnlRoot.setBackground(UITheme.BG_MAIN);

        // Card đăng nhập ở giữa
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(380, 470));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(28, 28, 24, 28)
        ));

        // Header: Logo & Title
        JPanel pnlHeader = new JPanel(new GridLayout(3, 1, 0, 4));
        pnlHeader.setOpaque(false);

        JLabel lblLogo = new JLabel("NHA KHOA", JLabel.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblLogo.setForeground(UITheme.PRIMARY);

        JLabel lblTitle = new JLabel("Đăng nhập", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Sử dụng tài khoản được cấp để tiếp tục", JLabel.CENTER);
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        pnlHeader.add(lblLogo);
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);

        // Center: Form
        JPanel pnlForm = new JPanel(new GridLayout(4, 1, 0, 10));
        pnlForm.setOpaque(false);

        txtUsername = new JTextField("admin");
        txtUsername.setFont(UITheme.FONT_REGULAR);
        txtUsername.putClientProperty("JTextField.placeholderText", "Nhập tên đăng nhập hoặc email");
        txtUsername.setPreferredSize(new Dimension(0, 38));

        txtPassword = new JPasswordField("123456");
        txtPassword.setFont(UITheme.FONT_REGULAR);
        txtPassword.putClientProperty("JTextField.placeholderText", "Nhập mật khẩu");
        txtPassword.setPreferredSize(new Dimension(0, 38));

        JPanel pnlUser = new JPanel(new BorderLayout(0, 4));
        pnlUser.setOpaque(false);
        JLabel lblU = new JLabel("Tài khoản");
        lblU.setFont(UITheme.FONT_BOLD);
        pnlUser.add(lblU, BorderLayout.NORTH);
        pnlUser.add(txtUsername, BorderLayout.CENTER);

        JPanel pnlPass = new JPanel(new BorderLayout(0, 4));
        pnlPass.setOpaque(false);
        JLabel lblP = new JLabel("Mật khẩu");
        lblP.setFont(UITheme.FONT_BOLD);
        pnlPass.add(lblP, BorderLayout.NORTH);
        pnlPass.add(txtPassword, BorderLayout.CENTER);

        // Checkbox & Quên mật khẩu
        JPanel pnlExtra = new JPanel(new BorderLayout());
        pnlExtra.setOpaque(false);
        JCheckBox chkRemember = new JCheckBox("Ghi nhớ đăng nhập", true);
        chkRemember.setFont(UITheme.FONT_SMALL);
        chkRemember.setForeground(UITheme.TEXT_MUTED);
        chkRemember.setOpaque(false);

        JLabel lblForgot = new JLabel("Quên mật khẩu?");
        lblForgot.setFont(UITheme.FONT_SMALL);
        lblForgot.setForeground(UITheme.PRIMARY);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblForgot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(LoginForm.this,
                        "Vui lòng liên hệ Quản trị viên (Admin) hoặc Bộ phận IT phòng khám để cấp lại mật khẩu!\n\nTài khoản demo nhanh:\n- Quản lý: admin / admin123\n- Lễ tân: letan01 / 123456\n- Bác sĩ: bacsi01 / 123456\n- Thủ kho: kho01 / 123456",
                        "Hỗ trợ quên mật khẩu", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        pnlExtra.add(chkRemember, BorderLayout.WEST);
        pnlExtra.add(lblForgot, BorderLayout.EAST);

        btnLogin = UITheme.createButton("Đăng nhập  →", UITheme.PRIMARY, Color.WHITE);
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

        pnlForm.add(pnlUser);
        pnlForm.add(pnlPass);
        pnlForm.add(pnlExtra);
        pnlForm.add(btnLogin);

        // Footer: Chọn nhanh quyền & Trợ giúp
        JPanel pnlFooter = new JPanel(new BorderLayout(0, 8));
        pnlFooter.setOpaque(false);

        JLabel lblHelp = new JLabel("Chưa có tài khoản? Liên hệ quản trị phòng khám.", JLabel.CENTER);
        lblHelp.setFont(UITheme.FONT_TINY);
        lblHelp.setForeground(UITheme.TEXT_MUTED);

        JPanel pnlQuick = new JPanel(new GridLayout(1, 4, 4, 0));
        pnlQuick.setOpaque(false);
        pnlQuick.add(createMiniBtn("Quản lý", "admin"));
        pnlQuick.add(createMiniBtn("Lễ tân", "letan"));
        pnlQuick.add(createMiniBtn("Bác sĩ", "bacsi"));
        pnlQuick.add(createMiniBtn("Thủ kho", "thukho"));

        pnlFooter.add(lblHelp, BorderLayout.NORTH);
        pnlFooter.add(pnlQuick, BorderLayout.SOUTH);

        card.add(pnlHeader, BorderLayout.NORTH);
        card.add(pnlForm, BorderLayout.CENTER);
        card.add(pnlFooter, BorderLayout.SOUTH);

        pnlRoot.add(card);
        setContentPane(pnlRoot);
    }

    private JButton createMiniBtn(String text, String user) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBackground(new Color(241, 245, 249));
        btn.setForeground(UITheme.TEXT_MUTED);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setBorder(new EmptyBorder(4, 4, 4, 4));
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
