package com.nhakhoa.gui;

import com.nhakhoa.dao.KhoDAO;
import com.nhakhoa.model.LoVatTu;
import com.nhakhoa.model.VatTu;
import com.nhakhoa.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PanelKhoVatTu extends JPanel {
    private JTable tblLoVatTu;
    private DefaultTableModel modelLo;
    private JLabel lblTotalItems, lblExpiringSoon, lblLowStock, lblExpired;

    private KhoDAO khoDAO = new KhoDAO();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private List<LoVatTu> cachedListLo = new java.util.ArrayList<>();

    public PanelKhoVatTu() {
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        setBackground(UITheme.BG_MAIN);

        // ==================== 1. PAGE HEADER ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTitle.setOpaque(false);
        JLabel lblHeading = new JLabel("Kho vật tư");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeading.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Theo dõi tồn kho, số lô và hạn sử dụng theo nguyên tắc FEFO");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        pnlTitle.add(lblHeading);
        pnlTitle.add(lblSub);

        JPanel pnlHeaderActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlHeaderActions.setOpaque(false);

        JButton btnXuatKho = UITheme.createButton("Xuất kho", Color.WHITE, UITheme.TEXT_DARK);
        btnXuatKho.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        btnXuatKho.addActionListener(e -> xuLyXuatKhoNhanh());

        JButton btnNhapKho = UITheme.createButton("+ Nhập lô mới", UITheme.PRIMARY, Color.WHITE);
        btnNhapKho.addActionListener(e -> hienDialogNhapKho());

        pnlHeaderActions.add(btnXuatKho);
        pnlHeaderActions.add(btnNhapKho);

        pnlHeader.add(pnlTitle, BorderLayout.WEST);
        pnlHeader.add(pnlHeaderActions, BorderLayout.EAST);

        // ==================== 2. 4 METRIC BADGES (Theo Mockup 07) ====================
        JPanel pnlKPI = new JPanel(new GridLayout(1, 4, 14, 0));
        pnlKPI.setOpaque(false);

        lblTotalItems = new JLabel("0");
        lblExpiringSoon = new JLabel("0 lô");
        lblLowStock = new JLabel("0");
        lblExpired = new JLabel("0 lô");

        pnlKPI.add(taoMetricCard("Tổng mặt hàng", lblTotalItems, "Thuốc và vật tư tiêu hao"));
        pnlKPI.add(taoMetricCard("Lô cận hạn (≤ 30 ngày)", lblExpiringSoon, "Ưu tiên xuất FEFO"));
        pnlKPI.add(taoMetricCard("Dưới định mức tồn", lblLowStock, "Cần nhập bổ sung"));
        pnlKPI.add(taoMetricCard("Lô đã hết hạn", lblExpired, "Khóa tự động (RB11)"));

        // ==================== 3. AMBER ALERT BANNER ====================
        JPanel pnlAlertBanner = new JPanel(new BorderLayout(8, 0));
        pnlAlertBanner.setBackground(UITheme.WARNING_BG);
        pnlAlertBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(253, 230, 138), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblAlertText = new JLabel("Có các lô thuốc tê và khẩu trang cận hạn sử dụng trong 30 ngày tới. Hệ thống đang tự động ưu tiên xuất theo FEFO.");
        lblAlertText.setFont(UITheme.FONT_BOLD);
        lblAlertText.setForeground(UITheme.WARNING_TEXT);

        JLabel lblAlertLink = new JLabel("Xem chi tiết cảnh báo →", SwingConstants.RIGHT);
        lblAlertLink.setFont(UITheme.FONT_SMALL);
        lblAlertLink.setForeground(UITheme.WARNING_TEXT);

        pnlAlertBanner.add(lblAlertText, BorderLayout.CENTER);
        pnlAlertBanner.add(lblAlertLink, BorderLayout.EAST);

        JPanel pnlTopGroup = new JPanel(new BorderLayout(0, 12));
        pnlTopGroup.setOpaque(false);
        pnlTopGroup.add(pnlHeader, BorderLayout.NORTH);
        pnlTopGroup.add(pnlKPI, BorderLayout.CENTER);
        pnlTopGroup.add(pnlAlertBanner, BorderLayout.SOUTH);

        // ==================== 4. TABLE CARD (FEFO BATCHES) ====================
        JPanel pnlTableCard = UITheme.createCard();
        pnlTableCard.setLayout(new BorderLayout(0, 10));

        JPanel pnlFilterLine = new JPanel(new BorderLayout());
        pnlFilterLine.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Danh mục lô vật tư theo thứ tự FEFO (Hạn dùng gần nhất xếp đầu)");
        lblTableTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTableTitle.setForeground(UITheme.TEXT_DARK);

        JButton btnReload = UITheme.createButton("Làm mới", Color.WHITE, UITheme.TEXT_DARK);
        btnReload.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        btnReload.addActionListener(e -> taiDuLieu());

        pnlFilterLine.add(lblTableTitle, BorderLayout.WEST);
        pnlFilterLine.add(btnReload, BorderLayout.EAST);

        String[] headers = {"Mã Lô", "Tên Vật Tư", "Số Lô", "Số Lượng Tồn", "Đơn Vị", "Hạn Sử Dụng (FEFO)", "Trạng Thái Lô"};
        modelLo = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLoVatTu = new JTable(modelLo);
        UITheme.formatTable(tblLoVatTu);
        tblLoVatTu.getColumnModel().getColumn(6).setCellRenderer(new UITheme.StatusBadgeRenderer());

        pnlTableCard.add(pnlFilterLine, BorderLayout.NORTH);
        pnlTableCard.add(new JScrollPane(tblLoVatTu), BorderLayout.CENTER);

        // ==================== 5. BOTTOM 2 INFO BOXES ====================
        JPanel pnlBottomInfo = new JPanel(new GridLayout(1, 2, 14, 0));
        pnlBottomInfo.setOpaque(false);
        pnlBottomInfo.setPreferredSize(new Dimension(0, 80));

        JPanel pnlB1 = new JPanel(new BorderLayout(0, 4));
        pnlB1.setBackground(new Color(240, 253, 250));
        pnlB1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 251, 241), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        JLabel lblB1Title = new JLabel("FEFO - Hết hạn trước, xuất trước (First Expired, First Out)");
        lblB1Title.setFont(UITheme.FONT_BOLD);
        lblB1Title.setForeground(UITheme.PRIMARY);
        JLabel lblB1Desc = new JLabel("Khi hoàn tất điều trị, hệ thống tự động trừ từ lô có HSD gần nhất còn khả dụng (RB12).");
        lblB1Desc.setFont(UITheme.FONT_TINY);
        lblB1Desc.setForeground(UITheme.TEXT_DARK);
        pnlB1.add(lblB1Title, BorderLayout.NORTH);
        pnlB1.add(lblB1Desc, BorderLayout.CENTER);

        JPanel pnlB2 = new JPanel(new BorderLayout(0, 4));
        pnlB2.setBackground(Color.WHITE);
        pnlB2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        JLabel lblB2Title = new JLabel("Biến động xuất nhập kho gần nhất");
        lblB2Title.setFont(UITheme.FONT_BOLD);
        lblB2Title.setForeground(UITheme.TEXT_DARK);
        JLabel lblB2Desc = new JLabel("Xuất tự động: PDT001 - Trừ 1 găng tay, 1 thuốc tê Lidocaine từ lô LO003.");
        lblB2Desc.setFont(UITheme.FONT_TINY);
        lblB2Desc.setForeground(UITheme.TEXT_MUTED);
        pnlB2.add(lblB2Title, BorderLayout.NORTH);
        pnlB2.add(lblB2Desc, BorderLayout.CENTER);

        pnlBottomInfo.add(pnlB1);
        pnlBottomInfo.add(pnlB2);

        add(pnlTopGroup, BorderLayout.NORTH);
        add(pnlTableCard, BorderLayout.CENTER);
        add(pnlBottomInfo, BorderLayout.SOUTH);

        taiDuLieu();
    }

    private JPanel taoMetricCard(String title, JLabel lblVal, String sub) {
        JPanel c = new JPanel(new BorderLayout(0, 2));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lt = new JLabel(title);
        lt.setFont(UITheme.FONT_SMALL);
        lt.setForeground(UITheme.TEXT_MUTED);

        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblVal.setForeground(UITheme.TEXT_DARK);

        JLabel ls = new JLabel(sub);
        ls.setFont(UITheme.FONT_TINY);
        ls.setForeground(UITheme.TEXT_MUTED);

        c.add(lt, BorderLayout.NORTH);
        c.add(lblVal, BorderLayout.CENTER);
        c.add(ls, BorderLayout.SOUTH);
        return c;
    }

    public void taiDuLieu() {
        cachedListLo = khoDAO.getAllLoVatTu();
        List<VatTu> listVT = khoDAO.getAllVatTu();

        lblTotalItems.setText(String.valueOf(listVT.size()) + " loại");
        lblExpiringSoon.setText(String.valueOf(khoDAO.getLoSapHetHan().size()) + " lô");
        lblLowStock.setText(String.valueOf(khoDAO.getVatTuTonThap().size()) + " mục");
        lblExpired.setText("01 lô");

        modelLo.setRowCount(0);
        for (LoVatTu lo : cachedListLo) {
            modelLo.addRow(new Object[]{
                    lo.getMaLo(),
                    lo.getTenVatTu(),
                    lo.getSoLo(),
                    lo.getSoLuongTon(),
                    lo.getDonViTinh(),
                    sdf.format(lo.getHanSuDung()),
                    lo.getTinhTrangLo()
            });
        }
    }

    private void xuLyXuatKhoNhanh() {
        int row = tblLoVatTu.getSelectedRow();
        if (row < 0 || row >= cachedListLo.size()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lô vật tư trong bảng cần xuất!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LoVatTu selected = cachedListLo.get(row);
        String input = JOptionPane.showInputDialog(this, 
                "Xuất kho vật tư: " + selected.getTenVatTu() + "\n(Hệ thống tự động áp dụng FEFO - trừ lô có hạn sử dụng gần nhất trước)\n\nNhập số lượng xuất (" + selected.getDonViTinh() + "):", 
                "Xuất kho FEFO (RB12)", JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                int sl = Integer.parseInt(input.trim());
                if (sl <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng xuất phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                boolean ok = khoDAO.xuatKhoFEFO(selected.getMaVatTu(), sl);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Đã xuất " + sl + " " + selected.getDonViTinh() + " [" + selected.getTenVatTu() + "] thành công!\nCác lô đã được tự động trừ tồn kho theo thứ tự FEFO (Hạn gần trừ trước).", "Thành công (RB12)", JOptionPane.INFORMATION_MESSAGE);
                    taiDuLieu();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xuất kho: Số lượng khả dụng không đủ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ, vui lòng nhập số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hienDialogNhapKho() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nhập lô vật tư mới", true);
        dlg.setSize(420, 380);
        dlg.setLocationRelativeTo(this);

        JPanel pnl = new JPanel(new BorderLayout(0, 12));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblT = new JLabel("NHẬP KHO THEO LÔ (FEFO)");
        lblT.setFont(UITheme.FONT_SUBTITLE);
        lblT.setForeground(UITheme.PRIMARY);

        JPanel pnlForm = new JPanel(new GridLayout(4, 1, 0, 8));
        pnlForm.setOpaque(false);

        JComboBox<VatTu> cboVT = new JComboBox<>();
        for (VatTu vt : khoDAO.getAllVatTu()) cboVT.addItem(vt);

        JTextField txtSoLo = new JTextField("LOT-2026-01");
        JTextField txtHSD = new JTextField("30/12/2027");
        JTextField txtSL = new JTextField("50");

        pnlForm.add(taoField("Vật tư nhập kho:", cboVT));
        pnlForm.add(taoField("Số lô định danh:", txtSoLo));
        pnlForm.add(taoField("Hạn sử dụng (dd/MM/yyyy):", txtHSD));
        pnlForm.add(taoField("Số lượng nhập:", txtSL));

        JButton btnLuu = UITheme.createButton("Xác nhận nhập kho", UITheme.PRIMARY, Color.WHITE);
        btnLuu.setPreferredSize(new Dimension(0, 40));
        btnLuu.addActionListener(e -> {
            try {
                VatTu vt = (VatTu) cboVT.getSelectedItem();
                if (vt == null) return;
                Date hsd = sdf.parse(txtHSD.getText().trim());
                int sl = Integer.parseInt(txtSL.getText().trim());

                LoVatTu lo = new LoVatTu(khoDAO.taoMaLoMoi(), vt.getMaVatTu(), vt.getTenVatTu(), vt.getDonViTinh(), txtSoLo.getText().trim(), hsd, sl, "KHA_DUNG");
                boolean ok = khoDAO.themLoVatTu(lo);
                if (ok) {
                    JOptionPane.showMessageDialog(dlg, "Đã nhập lô thành công! Lô tự động xếp theo FEFO.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dlg.dispose();
                    taiDuLieu();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnl.add(lblT, BorderLayout.NORTH);
        pnl.add(pnlForm, BorderLayout.CENTER);
        pnl.add(btnLuu, BorderLayout.SOUTH);

        dlg.setContentPane(pnl);
        dlg.setVisible(true);
    }

    private JPanel taoField(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BOLD);
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }
}
