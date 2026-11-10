package com.nhakhoa.gui;

import com.nhakhoa.bus.BenhNhanBUS;
import com.nhakhoa.model.BenhNhan;
import com.nhakhoa.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        setLayout(new BorderLayout(18, 0));
        setBorder(new EmptyBorder(18, 24, 18, 24));
        setBackground(UITheme.BG_MAIN);

        // LEFT: Form Card
        JPanel pnlLeftCard = UITheme.createCard();
        pnlLeftCard.setLayout(new BorderLayout(0, 14));
        pnlLeftCard.setPreferredSize(new Dimension(380, 0));

        JLabel lblFormTitle = new JLabel("THÔNG TIN HỒ SƠ BỆNH NHÂN");
        lblFormTitle.setFont(UITheme.FONT_SUBTITLE);
        lblFormTitle.setForeground(UITheme.TEXT_DARK);

        JPanel pnlForm = new JPanel(new GridLayout(5, 1, 0, 8));
        pnlForm.setOpaque(false);

        txtMaBN = new JTextField();
        txtMaBN.setEditable(false);
        txtMaBN.setBackground(new Color(241, 245, 249));

        txtHoTen = new JTextField();
        txtNgaySinh = new JTextField();
        txtCCCD = new JTextField();
        txtSDT = new JTextField();

        pnlForm.add(taoFieldInput("Mã bệnh nhân:", txtMaBN));
        pnlForm.add(taoFieldInput("Họ và tên (*):", txtHoTen));
        pnlForm.add(taoFieldInput("Ngày sinh (dd/MM/yyyy):", txtNgaySinh));
        pnlForm.add(taoFieldInput("Số CCCD / Định danh:", txtCCCD));
        pnlForm.add(taoFieldInput("Số điện thoại (*):", txtSDT));

        JPanel pnlTienSu = new JPanel(new BorderLayout(0, 4));
        pnlTienSu.setOpaque(false);
        JLabel lblTS = new JLabel("Tiền sử bệnh lý / Dị ứng thuốc:");
        lblTS.setFont(UITheme.FONT_BOLD);
        lblTS.setForeground(UITheme.TEXT_DARK);
        txtTienSu = new JTextArea(4, 20);
        txtTienSu.setLineWrap(true);
        pnlTienSu.add(lblTS, BorderLayout.NORTH);
        pnlTienSu.add(new JScrollPane(txtTienSu), BorderLayout.CENTER);

        // Các nút thao tác
        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 8, 8));
        pnlButtons.setOpaque(false);

        JButton btnThem = UITheme.createButton("➕  Thêm mới", UITheme.PRIMARY, Color.WHITE);
        JButton btnSua = UITheme.createButton("✏️  Cập nhật", UITheme.INFO, Color.WHITE);
        JButton btnXoa = UITheme.createButton("🔒  Khóa hồ sơ", UITheme.DANGER, Color.WHITE);
        JButton btnLamMoi = UITheme.createButton("🔄  Làm mới", Color.WHITE, UITheme.TEXT_DARK);
        btnLamMoi.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        btnThem.addActionListener(e -> xuLyThem());
        btnSua.addActionListener(e -> xuLySua());
        btnXoa.addActionListener(e -> xuLyXoa());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnLamMoi);

        JPanel pnlFormWrapper = new JPanel(new BorderLayout(0, 10));
        pnlFormWrapper.setOpaque(false);
        pnlFormWrapper.add(pnlForm, BorderLayout.NORTH);
        pnlFormWrapper.add(pnlTienSu, BorderLayout.CENTER);

        pnlLeftCard.add(lblFormTitle, BorderLayout.NORTH);
        pnlLeftCard.add(pnlFormWrapper, BorderLayout.CENTER);
        pnlLeftCard.add(pnlButtons, BorderLayout.SOUTH);

        // RIGHT: Danh sách + Tìm kiếm Card
        JPanel pnlRightCard = UITheme.createCard();
        pnlRightCard.setLayout(new BorderLayout(0, 12));

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlSearch.setOpaque(false);
        txtTimKiem = new JTextField(20);
        txtTimKiem.putClientProperty("JTextField.placeholderText", "Tìm theo Họ tên hoặc SĐT...");
        JButton btnTim = UITheme.createButton("🔍 Tìm", UITheme.PRIMARY, Color.WHITE);
        btnTim.addActionListener(e -> xuLyTimKiem());

        pnlSearch.add(new JLabel("Tìm kiếm:"));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTim);

        String[] headers = {"Mã BN", "Họ Tên Bệnh Nhân", "Ngày Sinh", "Số CCCD", "SĐT", "Tiền Sử Bệnh", "Trạng Thái"};
        modelBenhNhan = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblBenhNhan = new JTable(modelBenhNhan);
        UITheme.formatTable(tblBenhNhan);
        tblBenhNhan.getColumnModel().getColumn(6).setCellRenderer(new UITheme.StatusBadgeRenderer());

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

        pnlRightCard.add(pnlSearch, BorderLayout.NORTH);
        pnlRightCard.add(new JScrollPane(tblBenhNhan), BorderLayout.CENTER);

        add(pnlLeftCard, BorderLayout.WEST);
        add(pnlRightCard, BorderLayout.CENTER);

        taiDuLieu();
        lamMoiForm();
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
