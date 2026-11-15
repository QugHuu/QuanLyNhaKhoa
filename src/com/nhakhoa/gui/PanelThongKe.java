package com.nhakhoa.gui;

import com.nhakhoa.dao.ThongKeDAO;
import com.nhakhoa.util.FormatUtil;
import com.nhakhoa.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class PanelThongKe extends JPanel {

    private JLabel lblDoanhThuDV, lblThucThu, lblTongLuotKham, lblTyLeQuayLai;
    private JTable tblHieuSuatBS;
    private DefaultTableModel modelBS;
    private JLabel lblFooterSummary;
    private JPanel pnlServiceGroupList;
    private JTable tblVatTuHetHan;
    private DefaultTableModel modelVatTuHetHan;
    private JTable tblNhapXuatTon;
    private DefaultTableModel modelNXT;

    private ThongKeDAO thongKeDAO = new ThongKeDAO();

    public PanelThongKe() {
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(16, 24, 20, 24));
        setBackground(UITheme.BG_MAIN);

        // 1. HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTitle.setOpaque(false);
        JLabel lblHeading = new JLabel("Báo cáo & thống kê");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeading.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Toàn bộ kết quả hoạt động, tất cả các ca và doanh thu");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        pnlTitle.add(lblHeading);
        pnlTitle.add(lblSub);

        JPanel pnlHeaderActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlHeaderActions.setOpaque(false);

        JLabel lblDateFilter = new JLabel("  04 - 10/11/2026  ");
        lblDateFilter.setFont(UITheme.FONT_BOLD);
        lblDateFilter.setForeground(UITheme.TEXT_DARK);
        lblDateFilter.setOpaque(true);
        lblDateFilter.setBackground(Color.WHITE);
        lblDateFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        JButton btnExport = UITheme.createButton("+ Xuất báo cáo", UITheme.PRIMARY, Color.WHITE);
        btnExport.addActionListener(e -> xuLyXuatBaoCao());

        pnlHeaderActions.add(lblDateFilter);
        pnlHeaderActions.add(btnExport);

        pnlHeader.add(pnlTitle, BorderLayout.WEST);
        pnlHeader.add(pnlHeaderActions, BorderLayout.EAST);

        // Scrollable Body
        JPanel pnlBody = new JPanel();
        pnlBody.setLayout(new BoxLayout(pnlBody, BoxLayout.Y_AXIS));
        pnlBody.setOpaque(false);

        // 2. TOP 4 KPI CARDS
        JPanel pnlTopCards = new JPanel(new GridLayout(1, 4, 14, 0));
        pnlTopCards.setOpaque(false);
        pnlTopCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        lblDoanhThuDV = new JLabel("111.400.000 đ");
        lblThucThu = new JLabel("98.600.000 đ");
        lblTongLuotKham = new JLabel("142 lượt");
        lblTyLeQuayLai = new JLabel("68,3%");

        pnlTopCards.add(createKpiCard("Doanh thu dịch vụ", lblDoanhThuDV, "Tổng giá trị ca thực hiện", UITheme.PRIMARY));
        pnlTopCards.add(createKpiCard("Thực thu thực tế", lblThucThu, "Đã quyết toán tiền mặt & CK", UITheme.SUCCESS));
        pnlTopCards.add(createKpiCard("Tổng lượt khám", lblTongLuotKham, "+12% so với kỳ trước", UITheme.INFO));
        pnlTopCards.add(createKpiCard("Tỷ lệ quay lại", lblTyLeQuayLai, "Khám định kỳ & điều trị dài hạn", UITheme.TEXT_DARK));

        pnlBody.add(pnlTopCards);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 14)));

        // 3. MIDDLE ROW: Bar Chart (Left 60%) + Service Groups (Right 40%)
        JPanel pnlMiddle = new JPanel(new GridBagLayout());
        pnlMiddle.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 7);
        gbc.weightx = 0.60;
        gbc.weighty = 1.0;
        gbc.gridx = 0;

        // Chart Card
        JPanel pnlChartCard = UITheme.createCard();
        pnlChartCard.setLayout(new BorderLayout(0, 8));
        pnlChartCard.setPreferredSize(new Dimension(540, 220));

        JPanel pnlChartTitle = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlChartTitle.setOpaque(false);
        JLabel lblChartH = new JLabel("Doanh thu dịch vụ theo ngày");
        lblChartH.setFont(UITheme.FONT_SUBTITLE);
        lblChartH.setForeground(UITheme.TEXT_DARK);
        JLabel lblChartSub = new JLabel("Đơn vị: Triệu VNĐ · Kỳ 04 - 10 tháng 11, 2026");
        lblChartSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblChartSub.setForeground(UITheme.TEXT_MUTED);
        pnlChartTitle.add(lblChartH);
        pnlChartTitle.add(lblChartSub);

        pnlChartCard.add(pnlChartTitle, BorderLayout.NORTH);
        pnlChartCard.add(new RevenueBarChart(), BorderLayout.CENTER);

        pnlMiddle.add(pnlChartCard, gbc);

        // Service Group Card
        gbc.insets = new Insets(0, 7, 0, 0);
        gbc.weightx = 0.40;
        gbc.gridx = 1;

        JPanel pnlServiceCard = UITheme.createCard();
        pnlServiceCard.setLayout(new BorderLayout(0, 8));
        pnlServiceCard.setPreferredSize(new Dimension(380, 220));

        JLabel lblServH = new JLabel("Doanh thu theo nhóm dịch vụ");
        lblServH.setFont(UITheme.FONT_SUBTITLE);
        lblServH.setForeground(UITheme.TEXT_DARK);
        pnlServiceCard.add(lblServH, BorderLayout.NORTH);

        pnlServiceGroupList = new JPanel();
        pnlServiceGroupList.setLayout(new BoxLayout(pnlServiceGroupList, BoxLayout.Y_AXIS));
        pnlServiceGroupList.setOpaque(false);

        pnlServiceGroupList.add(createServiceProgressRow("Chỉnh nha & Niềng răng", "45.400.000 đ", 41));
        pnlServiceGroupList.add(Box.createRigidArea(new Dimension(0, 8)));
        pnlServiceGroupList.add(createServiceProgressRow("Phục hình & Cấy ghép Implant", "32.000.000 đ", 29));
        pnlServiceGroupList.add(Box.createRigidArea(new Dimension(0, 8)));
        pnlServiceGroupList.add(createServiceProgressRow("Nha khoa tổng quát & Trám răng", "21.000.000 đ", 19));
        pnlServiceGroupList.add(Box.createRigidArea(new Dimension(0, 8)));
        pnlServiceGroupList.add(createServiceProgressRow("Chăm sóc & Cạo vôi răng", "13.000.000 đ", 11));

        pnlServiceCard.add(pnlServiceGroupList, BorderLayout.CENTER);
        pnlMiddle.add(pnlServiceCard, gbc);

        pnlBody.add(pnlMiddle);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 14)));

        // 4. BOTTOM ROW: Doctor Performance (Left 65%) + Highlights (Right 35%)
        JPanel pnlBottom = new JPanel(new GridBagLayout());
        pnlBottom.setOpaque(false);

        gbc.insets = new Insets(0, 0, 0, 7);
        gbc.weightx = 0.65;
        gbc.gridx = 0;

        JPanel pnlDoctorCard = UITheme.createCard();
        pnlDoctorCard.setLayout(new BorderLayout(0, 10));
        pnlDoctorCard.setPreferredSize(new Dimension(580, 230));

        JLabel lblDocH = new JLabel("Hiệu suất theo bác sĩ");
        lblDocH.setFont(UITheme.FONT_SUBTITLE);
        lblDocH.setForeground(UITheme.TEXT_DARK);
        pnlDoctorCard.add(lblDocH, BorderLayout.NORTH);

        String[] headers = {"Bác sĩ", "Số ca", "Lượt khám", "Doanh thu mang lại"};
        modelBS = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHieuSuatBS = new JTable(modelBS);
        UITheme.formatTable(tblHieuSuatBS);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblHieuSuatBS.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        JScrollPane scrBS = new JScrollPane(tblHieuSuatBS);
        scrBS.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        pnlDoctorCard.add(scrBS, BorderLayout.CENTER);

        lblFooterSummary = new JLabel(" TỔNG CỘNG: 142 ca/lượt khám · 111.400.000 đ");
        lblFooterSummary.setFont(UITheme.FONT_BOLD);
        lblFooterSummary.setForeground(UITheme.PRIMARY);
        lblFooterSummary.setBorder(new EmptyBorder(4, 4, 0, 4));
        pnlDoctorCard.add(lblFooterSummary, BorderLayout.SOUTH);

        pnlBottom.add(pnlDoctorCard, gbc);

        // Highlight Card
        gbc.insets = new Insets(0, 7, 0, 0);
        gbc.weightx = 0.35;
        gbc.gridx = 1;

        JPanel pnlHighlightCard = UITheme.createCard();
        pnlHighlightCard.setLayout(new BorderLayout(0, 10));
        pnlHighlightCard.setPreferredSize(new Dimension(340, 230));

        JLabel lblHlTitle = new JLabel("Điểm nhấn trong kỳ");
        lblHlTitle.setFont(UITheme.FONT_SUBTITLE);
        lblHlTitle.setForeground(UITheme.TEXT_DARK);
        pnlHighlightCard.add(lblHlTitle, BorderLayout.NORTH);

        JPanel pnlHlItems = new JPanel();
        pnlHlItems.setLayout(new BoxLayout(pnlHlItems, BoxLayout.Y_AXIS));
        pnlHlItems.setOpaque(false);

        pnlHlItems.add(createHighlightItem("41,7%", "Doanh thu lớn nhất từ dịch vụ chỉnh nha & niềng răng", UITheme.PRIMARY));
        pnlHlItems.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlHlItems.add(createHighlightItem("32,8 triệu đ", "Doanh thu trung bình theo ghế / tuần", UITheme.INFO));
        pnlHlItems.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlHlItems.add(createHighlightItem("03 lô cận hạn", "Cần ưu tiên xuất kho vật tư theo nguyên tắc FEFO", UITheme.WARNING));

        pnlHighlightCard.add(pnlHlItems, BorderLayout.CENTER);
        pnlBottom.add(pnlHighlightCard, gbc);

        pnlBody.add(pnlBottom);

        // FR-67: Card vật tư sắp hết hạn
        pnlBody.add(Box.createRigidArea(new Dimension(0, 14)));
        JPanel pnlVatTuAlertCard = UITheme.createCard();
        pnlVatTuAlertCard.setLayout(new BorderLayout(0, 8));
        pnlVatTuAlertCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel pnlVTHeader = new JPanel(new BorderLayout());
        pnlVTHeader.setOpaque(false);
        JLabel lblVTH = new JLabel("Lo vat tu sap het han (trong 60 ngay)");
        lblVTH.setFont(UITheme.FONT_SUBTITLE);
        lblVTH.setForeground(UITheme.TEXT_DARK);
        pnlVTHeader.add(lblVTH, BorderLayout.WEST);
        pnlVatTuAlertCard.add(pnlVTHeader, BorderLayout.NORTH);

        String[] colsVT = {"Ma lo", "Ten vat tu", "So lo", "So luong", "Han su dung", "Con lai"};
        tblVatTuHetHan = new JTable(modelVatTuHetHan = new DefaultTableModel(colsVT, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.formatTable(tblVatTuHetHan);
        pnlVatTuAlertCard.add(new JScrollPane(tblVatTuHetHan), BorderLayout.CENTER);
        pnlBody.add(pnlVatTuAlertCard);

        // FR-66: Card Nhập-Xuất-Tồn
        pnlBody.add(Box.createRigidArea(new Dimension(0, 14)));
        JPanel pnlNXTCard = UITheme.createCard();
        pnlNXTCard.setLayout(new BorderLayout(0, 8));
        pnlNXTCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        JLabel lblNXTH = new JLabel("Bao cao Nhap-Xuat-Ton kho");
        lblNXTH.setFont(UITheme.FONT_SUBTITLE);
        lblNXTH.setForeground(UITheme.TEXT_DARK);
        pnlNXTCard.add(lblNXTH, BorderLayout.NORTH);

        String[] colsNXT = {"Ma VT", "Ten vat tu", "Don vi", "Tong nhap", "Tong xuat", "Ton cuoi"};
        tblNhapXuatTon = new JTable(modelNXT = new DefaultTableModel(colsNXT, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.formatTable(tblNhapXuatTon);
        pnlNXTCard.add(new JScrollPane(tblNhapXuatTon), BorderLayout.CENTER);
        pnlBody.add(pnlNXTCard);

        JScrollPane mainScroll = new JScrollPane(pnlBody);
        mainScroll.setBorder(null);
        mainScroll.setOpaque(false);
        mainScroll.getViewport().setOpaque(false);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        add(pnlHeader, BorderLayout.NORTH);
        add(mainScroll, BorderLayout.CENTER);

        taiDuLieu();
    }

    private JPanel createKpiCard(String title, JLabel lblValue, String note, Color accentColor) {
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 4));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(UITheme.TEXT_MUTED);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValue.setForeground(accentColor);

        JLabel lblNote = new JLabel(note);
        lblNote.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNote.setForeground(new Color(100, 116, 139));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        card.add(lblNote, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createServiceProgressRow(String groupName, String revenue, int percent) {
        JPanel pnl = new JPanel(new BorderLayout(0, 4));
        pnl.setOpaque(false);

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lblName = new JLabel(groupName);
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblName.setForeground(UITheme.TEXT_DARK);

        JLabel lblRev = new JLabel(revenue);
        lblRev.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRev.setForeground(UITheme.PRIMARY);

        pnlHeader.add(lblName, BorderLayout.WEST);
        pnlHeader.add(lblRev, BorderLayout.EAST);

        JProgressBar pb = new JProgressBar(0, 100);
        pb.setValue(percent);
        pb.setPreferredSize(new Dimension(0, 7));
        pb.setForeground(UITheme.PRIMARY);
        pb.setBackground(new Color(241, 245, 249));
        pb.setBorderPainted(false);

        pnl.add(pnlHeader, BorderLayout.NORTH);
        pnl.add(pb, BorderLayout.SOUTH);
        return pnl;
    }

    private JPanel createHighlightItem(String metric, String desc, Color badgeColor) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(false);

        JLabel lblM = new JLabel(metric, JLabel.CENTER);
        lblM.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblM.setForeground(badgeColor);
        lblM.setOpaque(true);
        lblM.setBackground(new Color(241, 245, 249));
        lblM.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        lblM.setPreferredSize(new Dimension(95, 30));

        JLabel lblD = new JLabel("<html>" + desc + "</html>");
        lblD.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblD.setForeground(UITheme.TEXT_DARK);

        item.add(lblM, BorderLayout.WEST);
        item.add(lblD, BorderLayout.CENTER);
        return item;
    }

    private void xuLyXuatBaoCao() {
        try {
            java.io.File file = new java.io.File("BaoCaoDoanhThu_NhaKhoa.csv");
            try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(new java.io.FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8)) {
                // Ghi UTF-8 BOM để Excel hiển thị tiếng Việt có dấu chuẩn 100%
                writer.write('\ufeff');

                writer.write("BÁO CÁO TỔNG HỢP HOẠT ĐỘNG PHÒNG KHÁM NHA KHOA SÀI GÒN\n");
                writer.write("Kỳ thống kê: 04/11/2026 - 10/11/2026\n");
                writer.write("Ngày kết xuất: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()) + "\n\n");

                writer.write("1. CHỈ SỐ HOẠT ĐỘNG CHÍNH (KPI)\n");
                writer.write("Chỉ số,Giá trị,Ghi chú\n");
                writer.write("Doanh thu dịch vụ," + lblDoanhThuDV.getText() + ",Tổng giá trị các ca thực hiện\n");
                writer.write("Thực thu thực tế," + lblThucThu.getText() + ",Đã quyết toán tiền mặt và chuyển khoản\n");
                writer.write("Tổng lượt khám," + lblTongLuotKham.getText() + ",+12% so với kỳ trước\n");
                writer.write("Tỷ lệ quay lại," + lblTyLeQuayLai.getText() + ",Khám định kỳ & điều trị dài hạn\n\n");

                writer.write("2. CƠ CẤU DOANH THU THEO NHÓM DỊCH VỤ\n");
                writer.write("Nhóm dịch vụ,Doanh thu (VNĐ),Tỷ trọng\n");
                writer.write("Chỉnh nha & Niềng răng,45.400.000 đ,41%\n");
                writer.write("Phục hình & Cấy ghép Implant,32.000.000 đ,29%\n");
                writer.write("Nha khoa tổng quát & Trám răng,21.000.000 đ,19%\n");
                writer.write("Chăm sóc & Cạo vôi răng,13.000.000 đ,11%\n\n");

                writer.write("3. BẢNG HIỆU SUẤT THEO BÁC SĨ\n");
                writer.write("Họ và tên Bác sĩ,Số ca,Lượt khám,Doanh thu mang lại\n");
                for (int i = 0; i < modelBS.getRowCount(); i++) {
                    writer.write(String.format("\"%s\",%s,%s,\"%s\"\n",
                            modelBS.getValueAt(i, 0),
                            modelBS.getValueAt(i, 1),
                            modelBS.getValueAt(i, 2),
                            modelBS.getValueAt(i, 3)));
                }
            }

            int choice = JOptionPane.showOptionDialog(this,
                    "Đã xuất file báo cáo thành công!\nTên file: BaoCaoDoanhThu_NhaKhoa.csv\n(Định dạng UTF-8 CSV tương thích 100% với Microsoft Excel)",
                    "Xuất báo cáo thành công",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Mở file ngay", "Đóng"},
                    "Mở file ngay");

            if (choice == JOptionPane.YES_OPTION) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void taiDuLieu() {
        double tt = thongKeDAO.getTongDoanhThuThucTe();
        double dv = thongKeDAO.getTongDoanhThuDichVu();
        int luot = thongKeDAO.getTongLuotKham();

        lblDoanhThuDV.setText(FormatUtil.formatMoney(dv));
        lblThucThu.setText(FormatUtil.formatMoney(tt));
        lblTongLuotKham.setText(luot + " lượt");
        lblTyLeQuayLai.setText("68,3%");

        modelBS.setRowCount(0);
        List<Object[]> listBS = thongKeDAO.getThongKeHieuSuatBacSi();
        double sumDt = 0;
        int sumCa = 0;
        for (Object[] row : listBS) {
            sumCa += (int) row[1];
            sumDt += (double) row[3];
            modelBS.addRow(new Object[]{
                    row[0],
                    row[1] + " ca",
                    row[2] + " lượt",
                    FormatUtil.formatMoney((Double) row[3])
            });
        }
        lblFooterSummary.setText(" TỔNG CỘNG: " + sumCa + " ca/lượt khám · " + FormatUtil.formatMoney(sumDt));

        // FR-67
        modelVatTuHetHan.setRowCount(0);
        for (String[] row : thongKeDAO.getLoSapHetHan(60)) {
            modelVatTuHetHan.addRow(row);
        }
        // FR-66
        modelNXT.setRowCount(0);
        for (String[] row : thongKeDAO.getBaoCaoNhapXuatTon()) {
            modelNXT.addRow(row);
        }
    }

    // Custom Bar Chart Component
    private static class RevenueBarChart extends JComponent {
        private final String[] days = {"04/11 (T2)", "05/11 (T3)", "06/11 (T4)", "07/11 (T5)", "08/11 (T6)", "09/11 (T7)", "10/11 (CN)"};
        private final double[] values = {12.4, 15.0, 14.5, 22.8, 11.2, 18.4, 17.1};
        private final double maxVal = 25.0;

        public RevenueBarChart() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            int padLeft = 16;
            int padRight = 16;
            int padBottom = 26;
            int padTop = 26;

            int chartH = height - padTop - padBottom;
            int chartW = width - padLeft - padRight;

            int n = days.length;
            int barWidth = Math.min(36, (chartW / n) - 18);
            int slotW = chartW / n;

            // Draw baseline
            g2.setColor(new Color(226, 232, 240));
            g2.drawLine(padLeft, height - padBottom, width - padRight, height - padBottom);

            Font fontLabel = new Font("Segoe UI", Font.PLAIN, 10);
            Font fontVal = new Font("Segoe UI", Font.BOLD, 10);

            for (int i = 0; i < n; i++) {
                int barH = (int) ((values[i] / maxVal) * chartH);
                int x = padLeft + i * slotW + (slotW - barWidth) / 2;
                int y = height - padBottom - barH;

                // Color: Highlight the highest bar (T5) with Primary Teal, others with softer Teal
                if (values[i] >= 20.0) {
                    g2.setColor(UITheme.PRIMARY);
                } else {
                    g2.setColor(new Color(153, 213, 202));
                }

                // Rounded bar
                RoundRectangle2D bar = new RoundRectangle2D.Float(x, y, barWidth, barH, 6, 6);
                g2.fill(bar);

                // Value on top
                g2.setFont(fontVal);
                g2.setColor(UITheme.TEXT_DARK);
                String valStr = String.format("%.1f", values[i]);
                FontMetrics fmVal = g2.getFontMetrics();
                int valX = x + (barWidth - fmVal.stringWidth(valStr)) / 2;
                g2.drawString(valStr, valX, y - 5);

                // Label at bottom
                g2.setFont(fontLabel);
                g2.setColor(UITheme.TEXT_MUTED);
                FontMetrics fm = g2.getFontMetrics();
                int labelX = x + (barWidth - fm.stringWidth(days[i])) / 2;
                g2.drawString(days[i], labelX, height - padBottom + 16);
            }

            g2.dispose();
        }
    }
}
