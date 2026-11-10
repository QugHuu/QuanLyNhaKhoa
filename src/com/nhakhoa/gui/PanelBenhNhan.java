package com.nhakhoa.gui;

import com.nhakhoa.bus.BenhNhanBUS;
import com.nhakhoa.model.BenhNhan;
import com.nhakhoa.util.FormatUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PanelBenhNhan extends JPanel {
    private JTextField txtMaBN, txtHoTen, txtNgaySinh, txtCCCD, txtSDT, txtTimKiem;
    private JTextArea txtTienSu;
    private JTable tblBenhNhan;
    private DefaultTableModel modelBenhNhan;

    private BenhNhanBUS benhNhanBUS = new BenhNhanBUS();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public PanelBenhNhan() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        // Header
        JLabel lblHeader = new JLabel("QUẢN LÝ HỒ SƠ BỆNH NHÂN (PHÂN HỆ 2)");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(new Color(25, 42, 86));

        // LEFT: Form nhập liệu
        JPanel pnlLeft = new JPanel(new BorderLayout(10, 10));
        pnlLeft.setPreferredSize(new Dimension(360, 0));
        pnlLeft.setBackground(Color.WHITE);
        pnlLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 8, 12));
        pnlForm.setOpaque(false);

        txtMaBN = new JTextField();
        txtMaBN.setEditable(false);
        txtMaBN.setBackground(new Color(240, 240, 240));

        txtHoTen = new JTextField();
        txtNgaySinh = new JTextField(); // định dạng dd/MM/yyyy
        txtCCCD = new JTextField();
        txtSDT = new JTextField();

        pnlForm.add(new JLabel("Mã BN:"));
        pnlForm.add(txtMaBN);
        pnlForm.add(new JLabel("Họ và Tên (*):"));
        pnlForm.add(txtHoTen);
        pnlForm.add(new JLabel("Ngày sinh (dd/MM/yyyy):"));
        pnlForm.add(txtNgaySinh);
        pnlForm.add(new JLabel("Số CCCD:"));
        pnlForm.add(txtCCCD);
        pnlForm.add(new JLabel("Số Điện Thoại (*):"));
        pnlForm.add(txtSDT);

        JPanel pnlTienSu = new JPanel(new BorderLayout(5, 5));
        pnlTienSu.setOpaque(false);
        pnlTienSu.add(new JLabel("Tiền sử bệnh / Dị ứng:"), BorderLayout.NORTH);
        txtTienSu = new JTextArea(4, 20);
        txtTienSu.setLineWrap(true);
        pnlTienSu.add(new JScrollPane(txtTienSu), BorderLayout.CENTER);

        // Buttons
        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 8, 8));
        pnlButtons.setOpaque(false);
        JButton btnThem = new JButton("➕ Thêm mới");
        JButton btnSua = new JButton("✏️ Cập nhật");
        JButton btnXoa = new JButton("🔒 Khóa hồ sơ");
        JButton btnLamMoi = new JButton("🔄 Làm mới");

        btnThem.setBackground(new Color(46, 204, 113));
        btnThem.setForeground(Color.BLACK);
        btnSua.setBackground(new Color(52, 152, 219));
        btnSua.setForeground(Color.BLACK);
        btnXoa.setBackground(new Color(231, 76, 60));
        btnXoa.setForeground(Color.BLACK);

        btnThem.addActionListener(e -> xuLyThem());
        btnSua.addActionListener(e -> xuLySua());
        btnXoa.addActionListener(e -> xuLyXoa());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnLamMoi);

        JPanel pnlFormAll = new JPanel(new BorderLayout(10, 10));
        pnlFormAll.setOpaque(false);
        pnlFormAll.add(pnlForm, BorderLayout.NORTH);
        pnlFormAll.add(pnlTienSu, BorderLayout.CENTER);
        pnlFormAll.add(pnlButtons, BorderLayout.SOUTH);

        pnlLeft.add(new JLabel("THÔNG TIN BỆNH NHÂN", JLabel.CENTER), BorderLayout.NORTH);
        pnlLeft.add(pnlFormAll, BorderLayout.CENTER);

        // RIGHT: Danh sách + Tìm kiếm
        JPanel pnlRight = new JPanel(new BorderLayout(10, 10));
        pnlRight.setBackground(Color.WHITE);
        pnlRight.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlSearch.setOpaque(false);
        txtTimKiem = new JTextField(20);
        JButton btnTim = new JButton("🔍 Tìm kiếm");
        btnTim.addActionListener(e -> xuLyTimKiem());
        pnlSearch.add(new JLabel("Tìm theo Tên / SĐT:"));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTim);

        String[] headers = {"Mã BN", "Họ Tên", "Ngày Sinh", "Số CCCD", "SĐT", "Tiền Sử Bệnh", "Trạng Thái"};
        modelBenhNhan = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblBenhNhan = new JTable(modelBenhNhan);
        tblBenhNhan.setRowHeight(26);
        tblBenhNhan.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tblBenhNhan.getSelectionModel().addListSelectionListener(e -> {
            int row = tblBenhNhan.getSelectedRow();
            if (row >= 0) {
                txtMaBN.setText(modelBenhNhan.getValueAt(row, 0).toString());
                txtHoTen.setText(modelBenhNhan.getValueAt(row, 1).toString());
                txtNgaySinh.setText(modelBenhNhan.getValueAt(row, 2) != null ? modelBenhNhan.getValueAt(row, 2).toString() : "");
                txtCCCD.setText(modelBenhNhan.getValueAt(row, 3) != null ? modelBenhNhan.getValueAt(row, 3).toString() : "");
                txtSDT.setText(modelBenhNhan.getValueAt(row, 4).toString());
                txtTienSu.setText(modelBenhNhan.getValueAt(row, 5) != null ? modelBenhNhan.getValueAt(row, 5).toString() : "");
            }
        });

        pnlRight.add(pnlSearch, BorderLayout.NORTH);
        pnlRight.add(new JScrollPane(tblBenhNhan), BorderLayout.CENTER);

        add(lblHeader, BorderLayout.NORTH);
        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);

        taiDuLieu();
        lamMoiForm();
    }

    private void taiDuLieu() {
        modelBenhNhan.setRowCount(0);
        List<BenhNhan> list = benhNhanBUS.getAll();
        for (BenhNhan bn : list) {
            modelBenhNhan.addRow(new Object[]{
                    bn.getMaBN(),
                    bn.getHoTen(),
                    bn.getNgaySinh() != null ? sdf.format(bn.getNgaySinh()) : "",
                    bn.getSoCCCD(),
                    bn.getSoDienThoai(),
                    bn.getTienSuBenh(),
                    bn.getTinhTrang()
            });
        }
    }

    private void xuLyTimKiem() {
        modelBenhNhan.setRowCount(0);
        List<BenhNhan> list = benhNhanBUS.timKiem(txtTimKiem.getText());
        for (BenhNhan bn : list) {
            modelBenhNhan.addRow(new Object[]{
                    bn.getMaBN(),
                    bn.getHoTen(),
                    bn.getNgaySinh() != null ? sdf.format(bn.getNgaySinh()) : "",
                    bn.getSoCCCD(),
                    bn.getSoDienThoai(),
                    bn.getTienSuBenh(),
                    bn.getTinhTrang()
            });
        }
    }

    private void lamMoiForm() {
        txtMaBN.setText(benhNhanBUS.taoMaBNMoi());
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        txtCCCD.setText("");
        txtSDT.setText("");
        txtTienSu.setText("");
        txtTimKiem.setText("");
        tblBenhNhan.clearSelection();
    }

    private void xuLyThem() {
        try {
            Date ns = null;
            if (!txtNgaySinh.getText().trim().isEmpty()) {
                ns = sdf.parse(txtNgaySinh.getText().trim());
            }
            BenhNhan bn = new BenhNhan(
                    txtMaBN.getText().trim(),
                    txtHoTen.getText().trim(),
                    ns,
                    txtCCCD.getText().trim(),
                    txtSDT.getText().trim(),
                    txtTienSu.getText().trim(),
                    "HOAT_DONG",
                    new Date()
            );
            benhNhanBUS.them(bn);
            JOptionPane.showMessageDialog(this, "Thêm hồ sơ bệnh nhân thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
            lamMoiForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLySua() {
        try {
            Date ns = null;
            if (!txtNgaySinh.getText().trim().isEmpty()) {
                ns = sdf.parse(txtNgaySinh.getText().trim());
            }
            BenhNhan bn = new BenhNhan(
                    txtMaBN.getText().trim(),
                    txtHoTen.getText().trim(),
                    ns,
                    txtCCCD.getText().trim(),
                    txtSDT.getText().trim(),
                    txtTienSu.getText().trim(),
                    "HOAT_DONG",
                    null
            );
            benhNhanBUS.sua(bn);
            JOptionPane.showMessageDialog(this, "Cập nhật hồ sơ bệnh nhân thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyXoa() {
        String maBN = txtMaBN.getText().trim();
        if (maBN.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bệnh nhân cần khóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn khóa hồ sơ bệnh nhân " + maBN + " (RB02 bảo lưu dữ liệu)?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                benhNhanBUS.xoa(maBN);
                JOptionPane.showMessageDialog(this, "Đã khóa hồ sơ thành công!");
                taiDuLieu();
                lamMoiForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
