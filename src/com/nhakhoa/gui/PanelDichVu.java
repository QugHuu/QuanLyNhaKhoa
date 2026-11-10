package com.nhakhoa.gui;

import com.nhakhoa.dao.DichVuDAO;
import com.nhakhoa.model.DichVu;
import com.nhakhoa.util.FormatUtil;

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
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        JLabel lblTitle = new JLabel("DANH MỤC DỊCH VỤ & BẢNG GIÁ NIÊM YẾT (PHÂN HỆ 5)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(25, 42, 86));

        // LEFT: Form
        JPanel pnlLeft = new JPanel(new BorderLayout(10, 10));
        pnlLeft.setPreferredSize(new Dimension(360, 0));
        pnlLeft.setBackground(Color.WHITE);
        pnlLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 8, 12));
        pnlForm.setOpaque(false);

        txtMaDV = new JTextField(dichVuDAO.taoMaDVMoi());
        txtMaDV.setEditable(false);
        txtMaDV.setBackground(new Color(240, 240, 240));

        txtTenDV = new JTextField();
        txtDonGia = new JTextField();
        cboTinhTrang = new JComboBox<>(new String[]{"DANG_AP_DUNG", "NGUNG_AP_DUNG"});

        pnlForm.add(new JLabel("Mã Dịch Vụ:"));
        pnlForm.add(txtMaDV);
        pnlForm.add(new JLabel("Tên Dịch Vụ:"));
        pnlForm.add(txtTenDV);
        pnlForm.add(new JLabel("Đơn Giá (VNĐ):"));
        pnlForm.add(txtDonGia);
        pnlForm.add(new JLabel("Tình Trạng:"));
        pnlForm.add(cboTinhTrang);

        JPanel pnlButtons = new JPanel(new GridLayout(1, 3, 8, 8));
        pnlButtons.setOpaque(false);
        JButton btnThem = new JButton("➕ Thêm");
        JButton btnSua = new JButton("✏️ Sửa");
        JButton btnLamMoi = new JButton("🔄 Làm mới");

        btnThem.setBackground(new Color(46, 204, 113));
        btnThem.setForeground(Color.BLACK);
        btnSua.setBackground(new Color(52, 152, 219));
        btnSua.setForeground(Color.BLACK);

        btnThem.addActionListener(e -> xuLyThem());
        btnSua.addActionListener(e -> xuLySua());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnLamMoi);

        pnlLeft.add(new JLabel("THÔNG TIN DỊCH VỤ NHA KHOA", JLabel.CENTER), BorderLayout.NORTH);
        pnlLeft.add(pnlForm, BorderLayout.CENTER);
        pnlLeft.add(pnlButtons, BorderLayout.SOUTH);

        // RIGHT: Table
        JPanel pnlRight = new JPanel(new BorderLayout(10, 10));
        pnlRight.setBackground(Color.WHITE);
        pnlRight.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        String[] headers = {"Mã DV", "Tên Dịch Vụ Nha Khoa", "Đơn Giá Niêm Yết", "Tình Trạng"};
        modelDichVu = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDichVu = new JTable(modelDichVu);
        tblDichVu.setRowHeight(26);

        tblDichVu.getSelectionModel().addListSelectionListener(e -> {
            int row = tblDichVu.getSelectedRow();
            if (row >= 0) {
                txtMaDV.setText(modelDichVu.getValueAt(row, 0).toString());
                txtTenDV.setText(modelDichVu.getValueAt(row, 1).toString());
                cboTinhTrang.setSelectedItem(modelDichVu.getValueAt(row, 3).toString());
            }
        });

        pnlRight.add(new JLabel("BẢNG GIÁ DỊCH VỤ HIỆN HÀNH"), BorderLayout.NORTH);
        pnlRight.add(new JScrollPane(tblDichVu), BorderLayout.CENTER);

        add(lblTitle, BorderLayout.NORTH);
        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);

        taiDuLieu();
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
