package com.nhakhoa;

import com.nhakhoa.gui.LoginForm;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Thiết lập giao diện chuẩn hệ điều hành Windows hiện đại
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            LoginForm login = new LoginForm();
            login.setVisible(true);
        });
    }
}
