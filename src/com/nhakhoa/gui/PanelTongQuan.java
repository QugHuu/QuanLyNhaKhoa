package com.nhakhoa.gui;

import com.nhakhoa.dao.LichHenDAO;
import com.nhakhoa.dao.ThongKeDAO;
import com.nhakhoa.model.LichHen;
import com.nhakhoa.util.FormatUtil;
import com.nhakhoa.util.UITheme;

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
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(20, 24, 20, 24));
        setBackground(UITheme.BG_MAIN);

        // TOP: 4 Thẻ chỉ số KPI
        JPanel pnlKPI = new JPanel(new GridLayout(1, 4, 16, 0));
        pnlKPI.setOpaque(false);

        lblDoanhThu = new JLabel("0 đ");
        lblBenhNhan = new JLabel("0");
        lblLichHen = new JLabel("0");
        lblKhoCanhBao = new JLabel("0");

        pnlKPI.add(taoTheKPI("TỔNG DOANH THU", lblDoanhThu, UITheme.SUCCESS, "Thực thu toàn phòng khám"));
        pnlKPI.add(taoTheKPI("HỒ SƠ BỆNH NHÂN", lblBenhNhan, UITheme.INFO, "Đang quản lý điều trị"));
        pnlKPI.add(taoTheKPI("LỊCH HẸN HÔM NAY", lblLichHen, new Color(139, 92, 246), "Số lượt đặt khám"));
        pnlKPI.add(taoTheKPI("CẢNH BÁO KHO (FEFO)", lblKhoCanhBao, UITheme.DANGER, "Cận hạn / Dưới định mức"));

        // CENTER: Bảng danh sách lịch hẹn hôm nay
        JPanel pnlTableCard = UITheme.createCard();
        pnlTableCard.setLayout(new BorderLayout(0, 14));

        JPanel pnlTableHeader = new JPanel(new BorderLayout());
        pnlTableHeader.setOpaque(false);

        JLabel lblSub = new JLabel("DANH SÁCH LỊCH HẸN KHÁM BỆNH TRONG NGÀY");
        lblSub.setFont(UITheme.FONT_SUBTITLE);
        lblSub.setForeground(UITheme.TEXT_DARK);

        JButton btnReload = UITheme.createButton("Làm mới số liệu", Color.WHITE, UITheme.TEXT_DARK);
        btnReload.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btnReload.addActionListener(e -> taiDuLieu());

        pnlTableHeader.add(lblSub, BorderLayout.WEST);
        pnlTableHeader.add(btnReload, BorderLayout.EAST);

        String[] headers = {"Mã Lịch", "Bệnh Nhân", "Số Điện Thoại", "Bác Sĩ", "Ghế Điều Trị", "Khung Giờ Hẹn", "STT Chờ", "Trạng Thái"};
        modelLichHen = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblLichHenHomNay = new JTable(modelLichHen);
        UITheme.formatTable(tblLichHenHomNay);
        tblLichHenHomNay.getColumnModel().getColumn(7).setCellRenderer(new UITheme.StatusBadgeRenderer());

        pnlTableCard.add(pnlTableHeader, BorderLayout.NORTH);
        pnlTableCard.add(new JScrollPane(tblLichHenHomNay), BorderLayout.CENTER);

        add(pnlKPI, BorderLayout.NORTH);
        add(pnlTableCard, BorderLayout.CENTER);

        taiDuLieu();
    }

    private JPanel taoTheKPI(String title, JLabel lblValue, Color accentColor, String note) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                        new EmptyBorder(14, 16, 14, 16)
                )
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(UITheme.TEXT_MUTED);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(UITheme.TEXT_DARK);

        JLabel lblNote = new JLabel(note);
        lblNote.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNote.setForeground(UITheme.TEXT_MUTED);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        card.add(lblNote, BorderLayout.SOUTH);

        return card;
    }

    public void taiDuLieu() {
        lblDoanhThu.setText(FormatUtil.formatMoney(thongKeDAO.getTongDoanhThu()));
        lblBenhNhan.setText(String.valueOf(thongKeDAO.getSoLuongBenhNhan()) + " người");
        lblLichHen.setText(String.valueOf(thongKeDAO.getSoLuongLichHenHomNay()) + " lượt");
        lblKhoCanhBao.setText(String.valueOf(thongKeDAO.getSoVatTuCanhBao()) + " mục");

        modelLichHen.setRowCount(0);
        List<LichHen> list = lichHenDAO.getAll();
        for (LichHen lh : list) {
            String gioFormat = FormatUtil.formatShortTime(lh.getGioBatDau()) + " - " + FormatUtil.formatShortTime(lh.getGioKetThuc());
            modelLichHen.addRow(new Object[]{
                    lh.getMaLichHen(),
                    lh.getTenBN(),
                    lh.getSoDienThoai(),
                    lh.getTenBS(),
                    lh.getMaGhe(),
                    gioFormat,
                    lh.getSoThuTuCho() > 0 ? "STT " + lh.getSoThuTuCho() : "-",
                    lh.getTinhTrang()
            });
        }
    }
}
