package com.nhakhoa;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.nhakhoa.gui.LoginForm;

import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        // Cấu hình giao diện hiện đại FlatMacLightLaf (macOS Style tinh tế)
        try {
            FlatMacLightLaf.setup();

            // Cấu hình bo góc và khoảng cách chuẩn hiện đại
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            UIManager.put("Table.rowHeight", 34);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("TableHeader.height", 36);
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        SwingUtilities.invokeLater(() -> {
            LoginForm login = new LoginForm();
            login.setVisible(true);
        });
    }
}
