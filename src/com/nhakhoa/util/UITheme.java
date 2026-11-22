package com.nhakhoa.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Hệ thống Style & Theme chuẩn theo Mockup Figma Nha Khoa:
 * - Tone chủ đạo: Deep Teal (#006C5B) & Soft Mint (#E6F4F1)
 * - Sidebar sáng màu (White / Light gray) hiện đại
 * - Card trắng nổi bật trên nền xám nhạt (#F4F6F8)
 * - Huy hiệu Pill badge đa sắc, bo góc mềm mại
 */
public class UITheme {
    // Bảng màu chuẩn từ mockup
    public static final Color PRIMARY = new Color(0, 108, 91);           // Deep Teal đậm đà (#006C5B)
    public static final Color PRIMARY_HOVER = new Color(0, 85, 71);
    public static final Color PRIMARY_LIGHT = new Color(230, 244, 241);  // Mint nhạt (#E6F4F1)
    
    public static final Color SIDEBAR_BG = Color.WHITE;                  // Sidebar sáng màu theo mockup
    public static final Color BG_MAIN = new Color(244, 246, 248);        // Nền canvas xám sáng (#F4F6F8)
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(226, 232, 240);   // Viền xám mảnh (#E2E8F0)
    
    public static final Color TEXT_DARK = new Color(15, 23, 42);         // Chữ chính Slate 900
    public static final Color TEXT_MUTED = new Color(100, 116, 139);     // Chữ phụ Slate 500
    
    public static final Color SUCCESS = new Color(16, 185, 129);         // Xanh lá (#10B981)
    public static final Color WARNING_BG = new Color(254, 243, 199);     // Nền cam nhạt
    public static final Color WARNING_TEXT = new Color(180, 83, 9);      // Chữ cam đậm
    public static final Color WARNING = WARNING_TEXT;
    public static final Color DANGER_BG = new Color(254, 226, 226);      // Nền đỏ nhạt
    public static final Color DANGER_TEXT = new Color(185, 28, 28);      // Chữ đỏ đậm
    public static final Color DANGER = DANGER_TEXT;
    public static final Color INFO_BG = new Color(224, 242, 254);        // Nền xanh dương nhạt
    public static final Color INFO_TEXT = new Color(3, 105, 161);
    public static final Color INFO = INFO_TEXT;

    // Font chuẩn giao diện
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TINY = new Font("Segoe UI", Font.PLAIN, 11);

    /**
     * Tạo nút bấm bo tròn hiện đại theo mockup
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
     * Tạo Card bo góc mềm mại viền nhẹ
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
     * Tạo Card không padding mặc định
     */
    public static JPanel createCardRaw() {
        JPanel pnl = new JPanel();
        pnl.setBackground(BG_CARD);
        pnl.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        return pnl;
    }

    /**
     * Renderer vẽ trạng thái dạng Pill badge theo chuẩn Figma mockup
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
            lbl.setFont(FONT_SMALL);

            if (isSelected) {
                lbl.setBackground(table.getSelectionBackground());
                lbl.setForeground(table.getSelectionForeground());
                return lbl;
            }

            switch (val) {
                case "HOAT_DONG": lbl.setBackground(PRIMARY_LIGHT); lbl.setForeground(PRIMARY); lbl.setText("  Hoạt động  "); break;
                case "DA_TRA": lbl.setBackground(PRIMARY_LIGHT); lbl.setForeground(PRIMARY); lbl.setText("  Đã trả  "); break;
                case "HOAN_THANH": case "DA_HOAN_TAT": lbl.setBackground(PRIMARY_LIGHT); lbl.setForeground(PRIMARY); lbl.setText("  Hoàn thành  "); break;
                case "DANG_AP_DUNG": case "KHA_DUNG": lbl.setBackground(PRIMARY_LIGHT); lbl.setForeground(PRIMARY); lbl.setText("  Khả dụng  "); break;
                case "DA_KHAM": lbl.setBackground(PRIMARY_LIGHT); lbl.setForeground(PRIMARY); lbl.setText("  Đã khám  "); break;
                case "CHO_KHAM": lbl.setBackground(WARNING_BG); lbl.setForeground(WARNING_TEXT); lbl.setText("  Chờ khám  "); break;
                case "CHUA_TRA": lbl.setBackground(WARNING_BG); lbl.setForeground(WARNING_TEXT); lbl.setText("  Chưa trả  "); break;
                case "CON_NO": case "DANG_NO": lbl.setBackground(WARNING_BG); lbl.setForeground(WARNING_TEXT); lbl.setText("  Còn nợ  "); break;
                case "DANG_THUC_HIEN": case "DANG_DIEU_TRI": lbl.setBackground(WARNING_BG); lbl.setForeground(WARNING_TEXT); lbl.setText("  Đang điều trị  "); break;
                case "DANG_LAM": case "DANG_TRUC": lbl.setBackground(PRIMARY_LIGHT); lbl.setForeground(PRIMARY); lbl.setText("  Đang làm việc  "); break;
                case "DA_DEN": lbl.setBackground(INFO_BG); lbl.setForeground(INFO_TEXT); lbl.setText("  Đã đến  "); break;
                case "TAM_KHOA": case "KHOA": lbl.setBackground(DANGER_BG); lbl.setForeground(DANGER_TEXT); lbl.setText("  Bị khóa  "); break;
                case "HET_HAN": lbl.setBackground(DANGER_BG); lbl.setForeground(DANGER_TEXT); lbl.setText("  Hết hạn  "); break;
                case "HET_HANG": lbl.setBackground(DANGER_BG); lbl.setForeground(DANGER_TEXT); lbl.setText("  Hết hàng  "); break;
                case "DA_HUY": case "HUY": lbl.setBackground(DANGER_BG); lbl.setForeground(DANGER_TEXT); lbl.setText("  Đã hủy  "); break;
                case "NGHI_VIEC": lbl.setBackground(DANGER_BG); lbl.setForeground(DANGER_TEXT); lbl.setText("  Nghỉ việc  "); break;
                case "NGHI_PHEP": lbl.setBackground(WARNING_BG); lbl.setForeground(WARNING_TEXT); lbl.setText("  Nghỉ phép  "); break;
                default: lbl.setBackground(Color.WHITE); lbl.setForeground(TEXT_DARK); lbl.setText("  " + val + "  "); break;
            }
            lbl.setOpaque(true);
            return lbl;
        }
    }

    /**
     * Cấu hình JTable hiện đại
     */
    public static void formatTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(FONT_REGULAR);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_DARK);
    }
}
