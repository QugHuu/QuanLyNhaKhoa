package com.nhakhoa.gui;

import com.nhakhoa.dao.ThongKeDAO;
import com.nhakhoa.util.FormatUtil;
import com.nhakhoa.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelThongKe extends JPanel {
    private JLabel lblTongDoanhThu, lblTongBenhNhan;
    private JTable tblDoanhThuDichVu;
    private DefaultTableModel modelDoanhThu;

    private ThongKeDAO thongKeDAO = new ThongKeDAO();

    public PanelThongKe() {
        setLayout(new BorderLayout(0, 18));
        setBorder(new EmptyBorder(18, 24, 18, 24));
        setBackground(UITheme.BG_MAIN);

        // TOP: 2 Thẻ lớn
        JPanel pnlTop = new JPanel(new GridLayout(1, 2, 16, 0));
        pnlTop.setOpaque(false);

        lblTongDoanhThu = new JLabel("0 đ", JLabel.CENTER);
        lblTongDoanhThu.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTongDoanhThu.setForeground(UITheme.SUCCESS);

        lblTongBenhNhan = new JLabel("0", JLabel.CENTER);
        lblTongBenhNhan.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTongBenhNhan.setForeground(UITheme.INFO);

        JPanel pnlBox1 = UITheme.createCard();
        pnlBox1.setLayout(new BorderLayout(0, 8));
        JLabel lblB1 = new JLabel("TỔNG DOANH THU THỰC THU (TIỀN MẶT + CHUYỂN KHOẢN):", JLabel.CENTER);
        lblB1.setFont(UITheme.FONT_BOLD);
        lblB1.setForeground(UITheme.TEXT_MUTED);
        pnlBox1.add(lblB1, BorderLayout.NORTH);
        pnlBox1.add(lblTongDoanhThu, BorderLayout.CENTER);

        JPanel pnlBox2 = UITheme.createCard();
        pnlBox2.setLayout(new BorderLayout(0, 8));
        JLabel lblB2 = new JLabel("TỔNG HỒ SƠ BỆNH NHÂN ĐANG HOẠT ĐỘNG:", JLabel.CENTER);
        lblB2.setFont(UITheme.FONT_BOLD);
        lblB2.setForeground(UITheme.TEXT_MUTED);
        pnlBox2.add(lblB2, BorderLayout.NORTH);
        pnlBox2.add(lblTongBenhNhan, BorderLayout.CENTER);

        pnlTop.add(pnlBox1);
        pnlTop.add(pnlBox2);

        // CENTER: Table Card
        JPanel pnlTableCard = UITheme.createCard();
        pnlTableCard.setLayout(new BorderLayout(0, 12));

        JLabel lblSub = new JLabel("CƠ CẤU DOANH THU THEO TỪNG THỦ THUẬT / DỊCH VỤ NHA KHOA (FR-61)");
        lblSub.setFont(UITheme.FONT_SUBTITLE);
        lblSub.setForeground(UITheme.TEXT_DARK);

        JButton btnReload = UITheme.createButton("Làm mới báo cáo", Color.WHITE, UITheme.TEXT_DARK);
        btnReload.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btnReload.addActionListener(e -> taiDuLieu());

        JPanel pnlTableHeader = new JPanel(new BorderLayout());
        pnlTableHeader.setOpaque(false);
        pnlTableHeader.add(lblSub, BorderLayout.WEST);
        pnlTableHeader.add(btnReload, BorderLayout.EAST);

        String[] headers = {"Tên Dịch Vụ Nha Khoa", "Số Lần Chỉ Định Thực Hiện", "Tổng Doanh Thu"};
        modelDoanhThu = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDoanhThuDichVu = new JTable(modelDoanhThu);
        UITheme.formatTable(tblDoanhThuDichVu);

        pnlTableCard.add(pnlTableHeader, BorderLayout.NORTH);
        pnlTableCard.add(new JScrollPane(tblDoanhThuDichVu), BorderLayout.CENTER);

        add(pnlTop, BorderLayout.NORTH);
        add(pnlTableCard, BorderLayout.CENTER);

        taiDuLieu();
    }

    public void taiDuLieu() {
        lblTongDoanhThu.setText(FormatUtil.formatMoney(thongKeDAO.getTongDoanhThu()));
        lblTongBenhNhan.setText(String.valueOf(thongKeDAO.getSoLuongBenhNhan()) + " người");

        modelDoanhThu.setRowCount(0);
        List<Object[]> list = thongKeDAO.getThongKeDoanhThuTheoDichVu();
        for (Object[] row : list) {
            modelDoanhThu.addRow(new Object[]{
                    row[0],
                    row[1] + " lần",
                    FormatUtil.formatMoney((Double) row[2])
            });
        }
    }
}
