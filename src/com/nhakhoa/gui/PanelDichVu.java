package com.nhakhoa.gui;

import com.nhakhoa.dao.DichVuDAO;
import com.nhakhoa.model.DichVu;
import com.nhakhoa.util.FormatUtil;
import com.nhakhoa.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelDichVu extends JPanel {
    private JTextField txtMaDV, txtTenDV, txtDonGia, txtTimKiem;
    private JComboBox<String> cboTinhTrang;
    private JTable tblDichVu;
    private DefaultTableModel modelDichVu;

    private DichVuDAO dichVuDAO = new DichVuDAO();

    public PanelDichVu() {
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(16, 24, 20, 24));
        setBackground(UITheme.BG_MAIN);

        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTitle.setOpaque(false);
        JLabel lblHeading = new JLabel("Bảng giá dịch vụ");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeading.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Danh mục thủ thuật, đơn giá niêm yết và trạng thái áp dụng");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        pnlTitle.add(lblHeading);
        pnlTitle.add(lblSub);

        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // Content Split
        JPanel pnlContent = new JPanel(new BorderLayout(18, 0));
        pnlContent.setOpaque(false);

        // LEFT: Form Card
        JPanel pnlLeftCard = UITheme.createCard();
        pnlLeftCard.setLayout(new BorderLayout(0, 14));
        pnlLeftCard.setPreferredSize(new Dimension(360, 0));

        JLabel lblFormTitle = new JLabel("Thông tin dịch vụ");
        lblFormTitle.setFont(UITheme.FONT_SUBTITLE);
        lblFormTitle.setForeground(UITheme.TEXT_DARK);

        JPanel pnlForm = new JPanel(new GridLayout(4, 1, 0, 10));
        pnlForm.setOpaque(false);

        txtMaDV = new JTextField(dichVuDAO.taoMaDVMoi());
        txtMaDV.setEditable(false);
        txtMaDV.setBackground(new Color(241, 245, 249));

        txtTenDV = new JTextField();
        txtDonGia = new JTextField();
        cboTinhTrang = new JComboBox<>(new String[]{"DANG_AP_DUNG", "NGUNG_AP_DUNG"});

        pnlForm.add(taoFieldInput("Mã dịch vụ:", txtMaDV));
        pnlForm.add(taoFieldInput("Tên dịch vụ nha khoa:", txtTenDV));
        pnlForm.add(taoFieldInput("Đơn giá niêm yết (VNĐ):", txtDonGia));
        pnlForm.add(taoFieldInput("Trạng thái áp dụng:", cboTinhTrang));

        JPanel pnlButtons = new JPanel(new GridLayout(1, 3, 8, 0));
        pnlButtons.setOpaque(false);
        JButton btnThem = UITheme.createButton("Thêm mới", UITheme.PRIMARY, Color.WHITE);
        JButton btnSua = UITheme.createButton("Cập nhật", UITheme.INFO, Color.WHITE);
        JButton btnLamMoi = UITheme.createButton("Làm mới", Color.WHITE, UITheme.TEXT_DARK);
        btnLamMoi.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        btnThem.addActionListener(e -> xuLyThem());
        btnSua.addActionListener(e -> xuLySua());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnLamMoi);

        pnlLeftCard.add(lblFormTitle, BorderLayout.NORTH);
        pnlLeftCard.add(pnlForm, BorderLayout.CENTER);
        pnlLeftCard.add(pnlButtons, BorderLayout.SOUTH);

        // RIGHT: Table Card
        JPanel pnlRightCard = UITheme.createCard();
        pnlRightCard.setLayout(new BorderLayout(0, 12));

        JPanel pnlTableTop = new JPanel(new BorderLayout());
        pnlTableTop.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Danh mục dịch vụ niêm yết");
        lblTableTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTableTitle.setForeground(UITheme.TEXT_DARK);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlSearch.setOpaque(false);
        txtTimKiem = new JTextField(16);
        txtTimKiem.putClientProperty("JTextField.placeholderText", "Tìm tên dịch vụ...");
        JButton btnTim = UITheme.createButton("Tìm", Color.WHITE, UITheme.TEXT_DARK);
        btnTim.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        btnTim.addActionListener(e -> timKiemDichVu());
        txtTimKiem.addActionListener(e -> timKiemDichVu());

        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTim);

        pnlTableTop.add(lblTableTitle, BorderLayout.WEST);
        pnlTableTop.add(pnlSearch, BorderLayout.EAST);

        String[] headers = {"Mã DV", "Tên Dịch Vụ Nha Khoa", "Đơn Giá Niêm Yết", "Tình Trạng"};
        modelDichVu = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDichVu = new JTable(modelDichVu);
        UITheme.formatTable(tblDichVu);
        tblDichVu.getColumnModel().getColumn(3).setCellRenderer(new UITheme.StatusBadgeRenderer());

        tblDichVu.getSelectionModel().addListSelectionListener(e -> {
            int row = tblDichVu.getSelectedRow();
            if (row >= 0) {
                txtMaDV.setText(modelDichVu.getValueAt(row, 0).toString());
                txtTenDV.setText(modelDichVu.getValueAt(row, 1).toString());
                String giaRaw = modelDichVu.getValueAt(row, 2).toString().replaceAll("[^0-9]", "");
                txtDonGia.setText(giaRaw);
                cboTinhTrang.setSelectedItem(modelDichVu.getValueAt(row, 3).toString());
            }
        });

        pnlRightCard.add(pnlTableTop, BorderLayout.NORTH);
        pnlRightCard.add(new JScrollPane(tblDichVu), BorderLayout.CENTER);

        pnlContent.add(pnlLeftCard, BorderLayout.WEST);
        pnlContent.add(pnlRightCard, BorderLayout.CENTER);

        add(pnlHeader, BorderLayout.NORTH);
        add(pnlContent, BorderLayout.CENTER);

        taiDuLieu();
    }

    private JPanel taoFieldInput(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BOLD);
        l.setForeground(UITheme.TEXT_DARK);
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    public void taiDuLieu() {
        modelDichVu.setRowCount(0);
        List<DichVu> list = dichVuDAO.getAll();
        for (DichVu dv : list) {
            modelDichVu.addRow(new Object[]{
                    dv.getMaDV(),
                    dv.getTenDV(),
                    FormatUtil.formatMoney(dv.getDonGia()),
                    dv.getTinhTrang()
            });
        }
    }

    private void timKiemDichVu() {
        String kw = txtTimKiem.getText().trim().toLowerCase();
        if (kw.isEmpty()) {
            taiDuLieu();
            return;
        }
        modelDichVu.setRowCount(0);
        List<DichVu> list = dichVuDAO.getAll();
        for (DichVu dv : list) {
            if (dv.getTenDV().toLowerCase().contains(kw) || dv.getMaDV().toLowerCase().contains(kw)) {
                modelDichVu.addRow(new Object[]{
                        dv.getMaDV(),
                        dv.getTenDV(),
                        FormatUtil.formatMoney(dv.getDonGia()),
                        dv.getTinhTrang()
                });
            }
        }
    }

    private void lamMoiForm() {
        txtMaDV.setText(dichVuDAO.taoMaDVMoi());
        txtTenDV.setText("");
        txtDonGia.setText("");
        cboTinhTrang.setSelectedIndex(0);
        tblDichVu.clearSelection();
    }

    private void xuLyThem() {
        try {
            String ten = txtTenDV.getText().trim();
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên dịch vụ không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double gia = Double.parseDouble(txtDonGia.getText().trim());

            DichVu dv = new DichVu(txtMaDV.getText().trim(), ten, gia, "DANG_AP_DUNG");
            if (dichVuDAO.them(dv)) {
                JOptionPane.showMessageDialog(this, "Thêm dịch vụ nha khoa thành công!");
                taiDuLieu();
                lamMoiForm();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLySua() {
        try {
            String ten = txtTenDV.getText().trim();
            double gia = Double.parseDouble(txtDonGia.getText().trim());
            String tt = cboTinhTrang.getSelectedItem().toString();

            DichVu dv = new DichVu(txtMaDV.getText().trim(), ten, gia, tt);
            if (dichVuDAO.sua(dv)) {
                JOptionPane.showMessageDialog(this, "Cập nhật dịch vụ thành công!");
                taiDuLieu();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
