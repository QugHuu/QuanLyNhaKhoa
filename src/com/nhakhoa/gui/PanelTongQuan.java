package com.nhakhoa.gui;

import com.nhakhoa.dao.LichHenDAO;
import com.nhakhoa.dao.ThongKeDAO;
import com.nhakhoa.model.LichHen;
import com.nhakhoa.util.FormatUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelTongQuan extends JPanel {
    private JLabel lblDoanhThu;
    private JLabel lblBenhNhan;
    private JLabel lblLichHen;
    private JLabel lblKhoCanhBao;
    private JTable tblLichHenHomNay;
    private DefaultTableModel modelLichHen;

    private ThongKeDAO thongKeDAO = new ThongKeDAO();
    private LichHenDAO lichHenDAO = new LichHenDAO();

    public PanelTongQuan() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        // Tiêu đề
        JLabel lblTitle = new JLabel("BẢNG TỔNG QUAN HỆ THỐNG PHÒNG KHÁM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(25, 42, 86));

        // Panel chứa các thẻ KPI
        JPanel pnlKPI = new JPanel(new GridLayout(1, 4, 15, 0));
        pnlKPI.setOpaque(false);

        lblDoanhThu = new JLabel("0 đ");
        lblBenhNhan = new JLabel("0");
        lblLichHen = new JLabel("0");
        lblKhoCanhBao = new JLabel("0");

        pnlKPI.add(taoTheKPI("DOANH THU", lblDoanhThu, new Color(46, 204, 113), "💰"));
        pnlKPI.add(taoTheKPI("TỔNG BỆNH NHÂN", lblBenhNhan, new Color(52, 152, 219), "👥"));
        pnlKPI.add(taoTheKPI("LỊCH HẸN HÔM NAY", lblLichHen, new Color(155, 89, 182), "📅"));
        pnlKPI.add(taoTheKPI("CẢNH BÁO KHO (FEFO)", lblKhoCanhBao, new Color(231, 76, 60), "⚠️"));

        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setOpaque(false);
        pnlTop.add(lblTitle, BorderLayout.NORTH);
        pnlTop.add(pnlKPI, BorderLayout.CENTER);

        // Bảng lịch hẹn hôm nay
        JPanel pnlTable = new JPanel(new BorderLayout(0, 10));
        pnlTable.setBackground(Color.WHITE);
        pnlTable.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblSub = new JLabel("DANH SÁCH LỊCH HẸN KHÁM HÔM NAY");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSub.setForeground(new Color(44, 62, 80));

        JButton btnReload = new JButton("🔄 Làm mới số liệu");
        btnReload.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnReload.addActionListener(e -> taiDuLieu());

        JPanel pnlTableHeader = new JPanel(new BorderLayout());
        pnlTableHeader.setOpaque(false);
        pnlTableHeader.add(lblSub, BorderLayout.WEST);
        pnlTableHeader.add(btnReload, BorderLayout.EAST);

        String[] headers = {"Mã Lịch", "Bệnh Nhân", "Số Điện Thoại", "Bác Sĩ", "Ghế Khám", "Khung Giờ", "STT", "Trạng Thái"};
        modelLichHen = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblLichHenHomNay = new JTable(modelLichHen);
        tblLichHenHomNay.setRowHeight(28);
        tblLichHenHomNay.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblLichHenHomNay.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        pnlTable.add(pnlTableHeader, BorderLayout.NORTH);
        pnlTable.add(new JScrollPane(tblLichHenHomNay), BorderLayout.CENTER);

        add(pnlTop, BorderLayout.NORTH);
        add(pnlTable, BorderLayout.CENTER);

        taiDuLieu();
    }

    private JPanel taoTheKPI(String title, JLabel lblValue, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, color),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(225, 230, 235)),
                        new EmptyBorder(15, 15, 15, 15)
                )
        ));

        JLabel lblTitle = new JLabel(icon + " " + title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(120, 130, 140));

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValue.setForeground(new Color(40, 50, 60));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    public void taiDuLieu() {
        lblDoanhThu.setText(FormatUtil.formatMoney(thongKeDAO.getTongDoanhThu()));
        lblBenhNhan.setText(String.valueOf(thongKeDAO.getSoLuongBenhNhan()));
        lblLichHen.setText(String.valueOf(thongKeDAO.getSoLuongLichHenHomNay()));
        lblKhoCanhBao.setText(String.valueOf(thongKeDAO.getSoVatTuCanhBao()) + " mục");

        modelLichHen.setRowCount(0);
        List<LichHen> list = lichHenDAO.getAll();
        for (LichHen lh : list) {
            modelLichHen.addRow(new Object[]{
                    lh.getMaLichHen(),
                    lh.getTenBN(),
                    lh.getSoDienThoai(),
                    lh.getTenBS(),
                    lh.getMaGhe(),
                    lh.getGioBatDau() + " - " + lh.getGioKetThuc(),
                    lh.getSoThuTuCho(),
                    lh.getTinhTrang()
            });
        }
    }
}
