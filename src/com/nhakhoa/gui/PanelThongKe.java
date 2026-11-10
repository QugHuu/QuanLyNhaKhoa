package com.nhakhoa.gui;

import com.nhakhoa.dao.ThongKeDAO;
import com.nhakhoa.util.FormatUtil;

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
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        JLabel lblTitle = new JLabel("BÁO CÁO & THỐNG KÊ ĐIỀU HÀNH PHÒNG KHÁM (PHÂN HỆ 7)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(25, 42, 86));

        // TOP: Thống kê nhanh
        JPanel pnlTop = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlTop.setOpaque(false);

        lblTongDoanhThu = new JLabel("0 đ", JLabel.CENTER);
        lblTongDoanhThu.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTongDoanhThu.setForeground(new Color(46, 204, 113));

        lblTongBenhNhan = new JLabel("0", JLabel.CENTER);
        lblTongBenhNhan.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTongBenhNhan.setForeground(new Color(52, 152, 219));

        JPanel pnlBox1 = new JPanel(new BorderLayout(5, 5));
        pnlBox1.setBackground(Color.WHITE);
        pnlBox1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));
        pnlBox1.add(new JLabel("TỔNG DOANH THU THỰC THU (TIỀN MẶT + CHUYỂN KHOẢN):", JLabel.CENTER), BorderLayout.NORTH);
        pnlBox1.add(lblTongDoanhThu, BorderLayout.CENTER);

        JPanel pnlBox2 = new JPanel(new BorderLayout(5, 5));
        pnlBox2.setBackground(Color.WHITE);
        pnlBox2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));
        pnlBox2.add(new JLabel("TỔNG HỒ SƠ BỆNH NHÂN ĐANG HOẠT ĐỘNG:", JLabel.CENTER), BorderLayout.NORTH);
        pnlBox2.add(lblTongBenhNhan, BorderLayout.CENTER);

        pnlTop.add(pnlBox1);
        pnlTop.add(pnlBox2);

        // CENTER: Bảng thống kê theo từng dịch vụ
        JPanel pnlTable = new JPanel(new BorderLayout(10, 10));
        pnlTable.setBackground(Color.WHITE);
        pnlTable.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblSub = new JLabel("CƠ CẤU DOANH THU THEO TỪNG THỦ THUẬT / DỊCH VỤ NHA KHOA (FR-61)");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSub.setForeground(new Color(44, 62, 80));

        JButton btnReload = new JButton("🔄 Làm mới báo cáo");
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
        tblDoanhThuDichVu.setRowHeight(26);

        pnlTable.add(pnlTableHeader, BorderLayout.NORTH);
        pnlTable.add(new JScrollPane(tblDoanhThuDichVu), BorderLayout.CENTER);

        add(lblTitle, BorderLayout.NORTH);
        add(pnlTop, BorderLayout.BEFORE_FIRST_LINE);
        add(pnlTable, BorderLayout.CENTER);

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
                    row[1],
                    FormatUtil.formatMoney((Double) row[2])
            });
        }
    }
}
