package com.nhakhoa.gui;

import com.nhakhoa.dao.KhoDAO;
import com.nhakhoa.model.LoVatTu;
import com.nhakhoa.model.VatTu;
import com.nhakhoa.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PanelKhoVatTu extends JPanel {
    private JTable tblLoVatTu;
    private DefaultTableModel modelLo;
    private JLabel lblTotalItems, lblExpiringSoon, lblLowStock, lblExpired;
    private JTextField txtSearch;

    private KhoDAO khoDAO = new KhoDAO();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private List<LoVatTu> cachedListLo = new java.util.ArrayList<>();
    private com.nhakhoa.model.TaiKhoan currentUser;

    public PanelKhoVatTu() {
        this(null);
    }

    public PanelKhoVatTu(com.nhakhoa.model.TaiKhoan user) {
        this.currentUser = user;
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

        JLabel lblSub = new JLabel("Theo dõi tồn kho, hạn sử dụng và quản lý các lô vật tư y tế");
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

        JButton btnKiemKe = UITheme.createButton("Kiểm kê kho", UITheme.BG_CARD, UITheme.INFO);
        btnKiemKe.addActionListener(e -> xuLyKiemKe());

        pnlHeaderActions.add(btnXuatKho);
        pnlHeaderActions.add(btnKiemKe);
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
        pnlKPI.add(taoMetricCard("Lô cận hạn (≤ 30 ngày)", lblExpiringSoon, "Cần ưu tiên xuất trước"));
        pnlKPI.add(taoMetricCard("Dưới định mức tồn", lblLowStock, "Cần nhập bổ sung"));
        pnlKPI.add(taoMetricCard("Lô đã hết hạn", lblExpired, "Tạm khóa sử dụng"));

        // ==================== 3. AMBER ALERT BANNER ====================
        JPanel pnlAlertBanner = new JPanel(new BorderLayout(8, 0));
        pnlAlertBanner.setBackground(UITheme.WARNING_BG);
        pnlAlertBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(253, 230, 138), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblAlertText = new JLabel("Có các lô vật tư cận hạn sử dụng trong 30 ngày tới. Vui lòng lưu ý xuất dùng trước.");
        lblAlertText.setFont(UITheme.FONT_BOLD);
        lblAlertText.setForeground(UITheme.WARNING_TEXT);

        JLabel lblAlertLink = new JLabel("Xem chi tiết cảnh báo →", SwingConstants.RIGHT);
        lblAlertLink.setFont(UITheme.FONT_SMALL);
        lblAlertLink.setForeground(UITheme.WARNING_TEXT);
        lblAlertLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblAlertLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                List<LoVatTu> sapHet = khoDAO.getLoSapHetHan();
                List<VatTu> tonThap = khoDAO.getVatTuTonThap();
                StringBuilder sb = new StringBuilder();
                sb.append("1. CÁC LÔ CẬN HẠN SỬ DỤNG (≤ 30 NGÀY):\n");
                if (sapHet.isEmpty()) {
                    sb.append("   - Không có lô nào cận hạn trong 30 ngày.\n");
                } else {
                    for (LoVatTu lo : sapHet) {
                        sb.append(String.format("   - [%s] %s (Số lô: %s) - HSD: %s - Tồn: %d %s\n",
                                lo.getMaLo(), lo.getTenVatTu(), lo.getSoLo(), sdf.format(lo.getHanSuDung()), lo.getSoLuongTon(), lo.getDonViTinh()));
                    }
                }
                sb.append("\n2. VẬT TƯ DƯỚI ĐỊNH MỨC TỒN KHO TỐI THIỂU:\n");
                if (tonThap.isEmpty()) {
                    sb.append("   - Tất cả vật tư đều đạt định mức an toàn.\n");
                } else {
                    for (VatTu vt : tonThap) {
                        sb.append(String.format("   - [%s] %s - Tồn: %d (Tối thiểu: %d %s)\n",
                                vt.getMaVatTu(), vt.getTenVatTu(), vt.getTongTon(), vt.getMucTonToiThieu(), vt.getDonViTinh()));
                    }
                }
                JOptionPane.showMessageDialog(PanelKhoVatTu.this, sb.toString(), "Chi tiết cảnh báo Kho", JOptionPane.WARNING_MESSAGE);
            }
        });

        pnlAlertBanner.add(lblAlertText, BorderLayout.CENTER);
        pnlAlertBanner.add(lblAlertLink, BorderLayout.EAST);

        JPanel pnlTopGroup = new JPanel(new BorderLayout(0, 12));
        pnlTopGroup.setOpaque(false);
        pnlTopGroup.add(pnlHeader, BorderLayout.NORTH);
        pnlTopGroup.add(pnlKPI, BorderLayout.CENTER);
        pnlTopGroup.add(pnlAlertBanner, BorderLayout.SOUTH);

        // ==================== 4. TABLE CARD ====================
        JPanel pnlTableCard = UITheme.createCard();
        pnlTableCard.setLayout(new BorderLayout(0, 10));

        JPanel pnlFilterLine = new JPanel(new BorderLayout(8, 0));
        pnlFilterLine.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Danh mục các lô vật tư trong kho");
        lblTableTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTableTitle.setForeground(UITheme.TEXT_DARK);

        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm tên vật tư, số lô...");
        txtSearch.putClientProperty("JComponent.roundRect", true);
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { locDanhSach(); }
            @Override public void removeUpdate(DocumentEvent e) { locDanhSach(); }
            @Override public void changedUpdate(DocumentEvent e) { locDanhSach(); }
        });

        JButton btnReload = UITheme.createButton("Làm mới", Color.WHITE, UITheme.TEXT_DARK);
        btnReload.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        btnReload.addActionListener(e -> taiDuLieu());

        JPanel pnlRightFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlRightFilter.setOpaque(false);
        pnlRightFilter.add(txtSearch);
        pnlRightFilter.add(btnReload);

        pnlFilterLine.add(lblTableTitle, BorderLayout.WEST);
        pnlFilterLine.add(pnlRightFilter, BorderLayout.EAST);

        String[] headers = {"Mã Lô", "Tên Vật Tư", "Số Lô", "Số Lượng Tồn", "Đơn Vị", "Hạn Sử Dụng", "Trạng Thái Lô"};
        modelLo = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLoVatTu = new JTable(modelLo);
        UITheme.formatTable(tblLoVatTu);
        tblLoVatTu.getColumnModel().getColumn(6).setCellRenderer(new UITheme.StatusBadgeRenderer());

        pnlTableCard.add(pnlFilterLine, BorderLayout.NORTH);
        pnlTableCard.add(new JScrollPane(tblLoVatTu), BorderLayout.CENTER);

        add(pnlTopGroup, BorderLayout.NORTH);
        add(pnlTableCard, BorderLayout.CENTER);

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

    private void locDanhSach() {
        String keyword = txtSearch != null ? txtSearch.getText().trim().toLowerCase() : "";
        modelLo.setRowCount(0);
        for (LoVatTu lo : cachedListLo) {
            String ten = lo.getTenVatTu() != null ? lo.getTenVatTu().toLowerCase() : "";
            String soLo = lo.getSoLo() != null ? lo.getSoLo().toLowerCase() : "";
            if (keyword.isEmpty() || ten.contains(keyword) || soLo.contains(keyword)) {
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
        if (currentUser != null && !currentUser.isKho() && !currentUser.isQuanLy()) {
            JOptionPane.showMessageDialog(this,
                    "Chức năng quản lý xuất kho chỉ dành cho Thủ kho hoặc Quản lý!",
                    "Thông báo phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = tblLoVatTu.getSelectedRow();
        if (row < 0 || row >= cachedListLo.size()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lô vật tư trong bảng cần xuất!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LoVatTu selected = cachedListLo.get(row);
        String input = JOptionPane.showInputDialog(this, 
                "Xuất kho vật tư: " + selected.getTenVatTu() + "\n\nNhập số lượng xuất (" + selected.getDonViTinh() + "):", 
                "Xuất kho vật tư", JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                int sl = Integer.parseInt(input.trim());
                if (sl <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng xuất phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                boolean ok = khoDAO.xuatKhoFEFO(selected.getMaVatTu(), sl);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Đã xuất " + sl + " " + selected.getDonViTinh() + " [" + selected.getTenVatTu() + "] thành công!", "Xuất kho thành công", JOptionPane.INFORMATION_MESSAGE);
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
        if (currentUser != null && !currentUser.isKho() && !currentUser.isQuanLy()) {
            JOptionPane.showMessageDialog(this,
                    "Chức năng nhập lô vật tư mới chỉ dành cho Thủ kho hoặc Quản lý!",
                    "Thông báo phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nhập lô vật tư mới", true);
        dlg.setSize(420, 380);
        dlg.setLocationRelativeTo(this);

        JPanel pnl = new JPanel(new BorderLayout(0, 12));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblT = new JLabel("NHẬP KHO VẬT TƯ THEO LÔ");
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
                    dlg.dispose();
                    taiDuLieu();
                    for (int i = 0; i < modelLo.getRowCount(); i++) {
                        if (lo.getMaLo().equals(modelLo.getValueAt(i, 0))) {
                            tblLoVatTu.setRowSelectionInterval(i, i);
                            tblLoVatTu.scrollRectToVisible(tblLoVatTu.getCellRect(i, 0, true));
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(this, "Đã nhập lô " + lo.getMaLo() + " thành công!", "Nhập kho thành công", JOptionPane.INFORMATION_MESSAGE);
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

    private void xuLyKiemKe() {
        // Chọn vật tư
        List<VatTu> dsVT = khoDAO.getAllVatTu();
        if (dsVT.isEmpty()) { JOptionPane.showMessageDialog(this, "Không có vật tư nào trong kho!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        String[] tenVT = dsVT.stream().map(v -> v.getMaVatTu() + " - " + v.getTenVatTu() + " (Tồn: " + v.getTongTon() + " " + v.getDonViTinh() + ")").toArray(String[]::new);
        String chon = (String) JOptionPane.showInputDialog(this, "Chọn vật tư cần kiểm kê:", "Kiểm kê tồn kho",
            JOptionPane.QUESTION_MESSAGE, null, tenVT, tenVT[0]);
        if (chon == null) return;
        String maVT = chon.split(" - ")[0];

        String soLuongStr = JOptionPane.showInputDialog(this, "Nhập số lượng thực tế đếm được trong kho:\n(Vật tư: " + chon + ")", "Kiểm kê tồn kho", JOptionPane.QUESTION_MESSAGE);
        if (soLuongStr == null || soLuongStr.trim().isEmpty()) return;
        try {
            int soLuong = Integer.parseInt(soLuongStr.trim());
            String lyDo = JOptionPane.showInputDialog(this, "Nhập lý do điều chỉnh tồn kho (nếu có):", "Lý do kiểm kê", JOptionPane.QUESTION_MESSAGE);
            if (lyDo == null || lyDo.trim().isEmpty()) lyDo = "Kiểm kê định kỳ";
            khoDAO.dieuChinhTonKho(maVT, soLuong, lyDo);
            JOptionPane.showMessageDialog(this, "Đã cập nhật số lượng tồn kho thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng nhập vào không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }
}
