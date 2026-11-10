package com.nhakhoa.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Tiện ích giao diện: Bảng màu, Font chữ và Renderers hiện đại
 * Sử dụng ký tự chữ chuẩn, tương thích 100% font chữ Windows, không bị lỗi ô vuông []
 */
public class UITheme {
    // Bảng màu hiện đại (Modern Medical Clinic Theme)
    public static final Color PRIMARY = new Color(13, 148, 136);       // Teal đậm sang trọng
    public static final Color PRIMARY_HOVER = new Color(15, 118, 110);
    public static final Color PRIMARY_LIGHT = new Color(204, 251, 241);
    public static final Color SECONDARY = new Color(30, 41, 59);       // Slate tối sang trọng (Sidebar)
    public static final Color BG_MAIN = new Color(248, 250, 252);      // Nền chính mềm mại
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(226, 232, 240); // Viền xám mảnh tinh tế
    public static final Color TEXT_DARK = new Color(15, 23, 42);       // Chữ đen xám dịu mắt
    public static final Color TEXT_MUTED = new Color(100, 116, 139);   // Chữ phụ
    public static final Color SUCCESS = new Color(16, 185, 129);       // Xanh lá thành công
    public static final Color WARNING = new Color(245, 158, 11);       // Cam cảnh báo
    public static final Color DANGER = new Color(239, 68, 68);         // Đỏ lỗi / xóa
    public static final Color INFO = new Color(14, 165, 233);          // Xanh dương thông tin

    // Fonts chuẩn Segoe UI của Windows, hiển thị tiếng Việt hoàn hảo
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    /**
     * Tạo nút bấm có phong cách phẳng, bo góc hiện đại
     */
    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    /**
     * Tạo card bo góc viền nhẹ
     */
    public static JPanel createCard() {
        JPanel pnl = new JPanel();
        pnl.setBackground(BG_CARD);
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return pnl;
    }

    /**
     * Renderer vẽ trạng thái dạng Pill badge có màu sắc sinh động
     */
    public static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        public StatusBadgeRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value == null) return lbl;

            String val = value.toString();
            lbl.setFont(FONT_BOLD);

            if (isSelected) {
                lbl.setBackground(table.getSelectionBackground());
                lbl.setForeground(table.getSelectionForeground());
                return lbl;
            }

            switch (val) {
                case "HOAT_DONG":
                case "DA_TRA":
                case "HOAN_THANH":
                case "DA_HOAN_TAT":
                case "DANG_AP_DUNG":
                case "KHA_DUNG":
                case "DA_KHAM":
                    lbl.setBackground(new Color(209, 250, 229));
                    lbl.setForeground(new Color(6, 95, 70));
                    lbl.setText(" " + val + " ");
                    break;

                case "CHO_KHAM":
                case "CHUA_TRA":
                case "CON_NO":
                case "DANG_THUC_HIEN":
                case "DANG_DIEU_TRI":
                case "DANG_LAM":
                case "DANG_TRUC":
                    lbl.setBackground(new Color(254, 243, 199));
                    lbl.setForeground(new Color(146, 64, 14));
                    lbl.setText(" " + val + " ");
                    break;

                case "DA_DEN":
                    lbl.setBackground(new Color(224, 242, 254));
                    lbl.setForeground(new Color(3, 105, 161));
                    lbl.setText(" ĐÃ ĐẾN ");
                    break;

                case "TAM_KHOA":
                case "HET_HAN":
                case "HET_HANG":
                case "DA_HUY":
                case "HUY":
                case "KHOA":
                case "NGHI_VIEC":
                    lbl.setBackground(new Color(254, 226, 226));
                    lbl.setForeground(new Color(153, 27, 27));
                    lbl.setText(" " + val + " ");
                    break;

                default:
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(TEXT_DARK);
                    break;
            }
            lbl.setOpaque(true);
            return lbl;
        }
    }

    /**
     * Cấu hình JTable hiện đại
     */
    public static void formatTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(FONT_REGULAR);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(TEXT_DARK);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(204, 251, 241));
        table.setSelectionForeground(new Color(15, 23, 42));
    }
}
