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
    private JTextField txtMaDV, txtTenDV, txtDonGia;
    private JComboBox<String> cboTinhTrang;
    private JTable tblDichVu;
    private DefaultTableModel modelDichVu;

    private DichVuDAO dichVuDAO = new DichVuDAO();

    public PanelDichVu() {
        setLayout(new BorderLayout(18, 0));
        setBorder(new EmptyBorder(18, 24, 18, 24));
        setBackground(UITheme.BG_MAIN);

        // LEFT: Form Card
        JPanel pnlLeftCard = UITheme.createCard();
        pnlLeftCard.setLayout(new BorderLayout(0, 14));
        pnlLeftCard.setPreferredSize(new Dimension(360, 0));

        JLabel lblFormTitle = new JLabel("THỦ THUẬT & DỊCH VỤ");
        lblFormTitle.setFont(UITheme.FONT_SUBTITLE);
        lblFormTitle.setForeground(UITheme.TEXT_DARK);

        JPanel pnlForm = new JPanel(new GridLayout(4, 1, 0, 8));
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
        JButton btnThem = UITheme.createButton("➕ Thêm", UITheme.PRIMARY, Color.WHITE);
        JButton btnSua = UITheme.createButton("✏️ Sửa", UITheme.INFO, Color.WHITE);
        JButton btnLamMoi = UITheme.createButton("🔄 Làm mới", Color.WHITE, UITheme.TEXT_DARK);
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

        JLabel lblTableTitle = new JLabel("BẢNG GIÁ DỊCH VỤ NIÊM YẾT HIỆN HÀNH");
        lblTableTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTableTitle.setForeground(UITheme.TEXT_DARK);

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
                cboTinhTrang.setSelectedItem(modelDichVu.getValueAt(row, 3).toString());
            }
        });

        pnlRightCard.add(lblTableTitle, BorderLayout.NORTH);
        pnlRightCard.add(new JScrollPane(tblDichVu), BorderLayout.CENTER);

        add(pnlLeftCard, BorderLayout.WEST);
        add(pnlRightCard, BorderLayout.CENTER);

        taiDuLieu();
    }

    private JPanel taoFieldInput(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 3));
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
