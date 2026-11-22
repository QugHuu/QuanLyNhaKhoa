package com.nhakhoa.gui;

import com.nhakhoa.dao.NhanSuDAO;
import com.nhakhoa.dao.TaiKhoanDAO;
import com.nhakhoa.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * FR-42: Quản lý nhân sự (nhân viên & bác sĩ)
 * FR-43: Phân quyền tài khoản
 * FR-44: Quản lý thông tin bác sĩ
 */
public class PanelNhanSu extends JPanel {

    private NhanSuDAO nhanSuDAO = new NhanSuDAO();
    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    // Tab Nhân viên
    private JTable tblNhanVien;
    private DefaultTableModel modelNV;

    // Tab Bác sĩ
    private JTable tblBacSi;
    private DefaultTableModel modelBS;

    // Tab Tài khoản
    private JTable tblTaiKhoan;
    private DefaultTableModel modelTK;

    public PanelNhanSu() {
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        setBackground(UITheme.BG_MAIN);

        // ==================== HEADER ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Quản lý nhân sự & tài khoản");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Quản lý nhân viên, bác sĩ và phân quyền hệ thống");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTitle.setOpaque(false);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // ==================== TABS ====================
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.setFont(UITheme.FONT_BOLD);
        tabPane.addTab("  Nhan vien  ", taoTabNhanVien());
        tabPane.addTab("  Bac si  ", taoTabBacSi());
        tabPane.addTab("  Tai khoan & phan quyen  ", taoTabTaiKhoan());

        add(pnlHeader, BorderLayout.NORTH);
        add(tabPane, BorderLayout.CENTER);

        taiDuLieu();
    }

    // ===================== TAB NHÂN VIÊN =====================
    private JPanel taoTabNhanVien() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBorder(new EmptyBorder(12, 0, 0, 0));
        pnl.setBackground(UITheme.BG_MAIN);

        // Toolbar
        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlToolbar.setOpaque(false);
        JButton btnThem = UITheme.createButton("+ Thêm nhân viên", UITheme.PRIMARY, Color.WHITE);
        JButton btnSua = UITheme.createButton("Sửa", UITheme.BG_CARD, UITheme.TEXT_DARK);
        JButton btnKhoa = UITheme.createButton("Khóa / Nghỉ việc", UITheme.BG_CARD, UITheme.DANGER);
        pnlToolbar.add(btnThem);
        pnlToolbar.add(btnSua);
        pnlToolbar.add(btnKhoa);

        // Table
        String[] cols = {"Mã NV", "Họ tên", "Số điện thoại", "Email", "Chức vụ", "Tình trạng", "Tài khoản"};
        modelNV = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNhanVien = new JTable(modelNV);
        UITheme.formatTable(tblNhanVien);
        tblNhanVien.getColumnModel().getColumn(5).setCellRenderer(new UITheme.StatusBadgeRenderer());

        // Actions
        btnThem.addActionListener(e -> hienThiDialogNhanVien(null));
        btnSua.addActionListener(e -> {
            int row = tblNhanVien.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
            String maNV = (String) modelNV.getValueAt(row, 0);
            hienThiDialogNhanVien(maNV);
        });
        btnKhoa.addActionListener(e -> {
            int row = tblNhanVien.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
            String maNV = (String) modelNV.getValueAt(row, 0);
            String hoTen = (String) modelNV.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận chuyển trạng thái nghỉ việc cho: " + hoTen + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = nhanSuDAO.khoaNhanVien(maNV);
                if (ok) { taiDuLieu(); JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái!", "Thành công", JOptionPane.INFORMATION_MESSAGE); }
            }
        });

        pnl.add(pnlToolbar, BorderLayout.NORTH);
        pnl.add(new JScrollPane(tblNhanVien), BorderLayout.CENTER);
        return pnl;
    }

    // ===================== TAB BÁC SĨ =====================
    private JPanel taoTabBacSi() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBorder(new EmptyBorder(12, 0, 0, 0));
        pnl.setBackground(UITheme.BG_MAIN);

        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlToolbar.setOpaque(false);
        JButton btnThem = UITheme.createButton("+ Thêm bác sĩ", UITheme.PRIMARY, Color.WHITE);
        JButton btnSua = UITheme.createButton("Sửa thông tin", UITheme.BG_CARD, UITheme.TEXT_DARK);
        JButton btnDoiTT = UITheme.createButton("Đổi trạng thái", UITheme.BG_CARD, UITheme.WARNING);
        pnlToolbar.add(btnThem);
        pnlToolbar.add(btnSua);
        pnlToolbar.add(btnDoiTT);

        String[] cols = {"Mã BS", "Họ tên", "Số điện thoại", "Email", "Chuyên khoa", "Học vị", "Tình trạng", "Tài khoản"};
        modelBS = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBacSi = new JTable(modelBS);
        UITheme.formatTable(tblBacSi);
        tblBacSi.getColumnModel().getColumn(6).setCellRenderer(new UITheme.StatusBadgeRenderer());

        btnThem.addActionListener(e -> hienThiDialogBacSi(null));
        btnSua.addActionListener(e -> {
            int row = tblBacSi.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn bác sĩ!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
            hienThiDialogBacSi((String) modelBS.getValueAt(row, 0));
        });
        btnDoiTT.addActionListener(e -> {
            int row = tblBacSi.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn bác sĩ!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
            String maBS = (String) modelBS.getValueAt(row, 0);
            String tinhTrangHienTai = (String) modelBS.getValueAt(row, 6);
            String[] options = {"DANG_LAM", "NGHI_PHEP", "NGHI_VIEC"};
            String chon = (String) JOptionPane.showInputDialog(this, "Chọn trạng thái mới:", "Đổi trạng thái",
                JOptionPane.QUESTION_MESSAGE, null, options, tinhTrangHienTai);
            if (chon != null) {
                nhanSuDAO.doiTinhTrangBacSi(maBS, chon);
                taiDuLieu();
            }
        });

        pnl.add(pnlToolbar, BorderLayout.NORTH);
        pnl.add(new JScrollPane(tblBacSi), BorderLayout.CENTER);
        return pnl;
    }

    // ===================== TAB TÀI KHOẢN =====================
    private JPanel taoTabTaiKhoan() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBorder(new EmptyBorder(12, 0, 0, 0));
        pnl.setBackground(UITheme.BG_MAIN);

        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlToolbar.setOpaque(false);
        JButton btnTao = UITheme.createButton("+ Tạo tài khoản", UITheme.PRIMARY, Color.WHITE);
        JButton btnDoiVT = UITheme.createButton("Đổi vai trò", UITheme.BG_CARD, UITheme.INFO);
        JButton btnMoKhoa = UITheme.createButton("Mở khóa", UITheme.BG_CARD, UITheme.SUCCESS);
        JButton btnReset = UITheme.createButton("Reset mật khẩu", UITheme.BG_CARD, UITheme.WARNING);
        pnlToolbar.add(btnTao);
        pnlToolbar.add(btnDoiVT);
        pnlToolbar.add(btnMoKhoa);
        pnlToolbar.add(btnReset);

        String[] cols = {"Mã TK", "Tên đăng nhập", "Họ tên", "Vai trò", "Tình trạng"};
        modelTK = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTaiKhoan = new JTable(modelTK);
        UITheme.formatTable(tblTaiKhoan);
        tblTaiKhoan.getColumnModel().getColumn(4).setCellRenderer(new UITheme.StatusBadgeRenderer());

        btnTao.addActionListener(e -> hienThiDialogTaoTaiKhoan());
        btnDoiVT.addActionListener(e -> {
            int row = tblTaiKhoan.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
            String maTK = (String) modelTK.getValueAt(row, 0);
            List<String[]> dsVaiTro = taiKhoanDAO.getAllVaiTro();
            String[] tenVT = dsVaiTro.stream().map(v -> v[0] + " - " + v[1]).toArray(String[]::new);
            String chon = (String) JOptionPane.showInputDialog(this, "Chọn vai trò mới:", "Phân quyền",
                JOptionPane.QUESTION_MESSAGE, null, tenVT, tenVT[0]);
            if (chon != null) {
                String maVT = chon.split(" - ")[0];
                taiKhoanDAO.doiVaiTro(maTK, maVT);
                taiDuLieu();
                JOptionPane.showMessageDialog(this, "Đã đổi vai trò thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        btnMoKhoa.addActionListener(e -> {
            int row = tblTaiKhoan.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
            String maTK = (String) modelTK.getValueAt(row, 0);
            taiKhoanDAO.moKhoaTaiKhoan(maTK);
            taiDuLieu();
            JOptionPane.showMessageDialog(this, "Đã mở khóa tài khoản!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        });
        btnReset.addActionListener(e -> {
            int row = tblTaiKhoan.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
            String maTK = (String) modelTK.getValueAt(row, 0);
            String tenDN = (String) modelTK.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Reset mật khẩu tài khoản [" + tenDN + "] về '123456'?",
                "Xác nhận reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                taiKhoanDAO.resetMatKhau(maTK);
                JOptionPane.showMessageDialog(this, "Đã reset mật khẩu về '123456'!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        pnl.add(pnlToolbar, BorderLayout.NORTH);
        pnl.add(new JScrollPane(tblTaiKhoan), BorderLayout.CENTER);
        return pnl;
    }

    // ===================== DIALOGS =====================

    private void hienThiDialogNhanVien(String maNVEdit) {
        JTextField txtHoTen = new JTextField(20);
        JTextField txtSDT = new JTextField(15);
        JTextField txtEmail = new JTextField(20);
        JTextField txtChucVu = new JTextField(15);

        // Điền dữ liệu nếu đang sửa
        if (maNVEdit != null) {
            List<String[]> list = nhanSuDAO.getAllNhanVien();
            for (String[] row : list) {
                if (row[0].equals(maNVEdit)) {
                    txtHoTen.setText(row[1]);
                    txtSDT.setText(row[2]);
                    txtEmail.setText(row[3]);
                    txtChucVu.setText(row[4]);
                    break;
                }
            }
        }

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("Họ và tên:")); form.add(txtHoTen);
        form.add(new JLabel("Số điện thoại:")); form.add(txtSDT);
        form.add(new JLabel("Email:")); form.add(txtEmail);
        form.add(new JLabel("Chức vụ:")); form.add(txtChucVu);

        String title = maNVEdit == null ? "Thêm nhân viên mới" : "Sửa thông tin nhân viên";
        int result = JOptionPane.showConfirmDialog(this, form, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String hoTen = txtHoTen.getText().trim();
            if (hoTen.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
            boolean ok;
            if (maNVEdit == null) {
                String maMoi = nhanSuDAO.taoMaNVMoi();
                ok = nhanSuDAO.themNhanVien(maMoi, hoTen, txtSDT.getText().trim(), txtEmail.getText().trim(), txtChucVu.getText().trim());
            } else {
                ok = nhanSuDAO.capNhatNhanVien(maNVEdit, hoTen, txtSDT.getText().trim(), txtEmail.getText().trim(), txtChucVu.getText().trim());
            }
            if (ok) { taiDuLieu(); JOptionPane.showMessageDialog(this, "Lưu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE); }
            else JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi lưu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienThiDialogBacSi(String maBSEdit) {
        JTextField txtHoTen = new JTextField(20);
        JTextField txtSDT = new JTextField(15);
        JTextField txtEmail = new JTextField(20);
        JTextField txtCK = new JTextField(20);
        JTextField txtHocVi = new JTextField(15);

        if (maBSEdit != null) {
            List<String[]> list = nhanSuDAO.getAllBacSi();
            for (String[] row : list) {
                if (row[0].equals(maBSEdit)) {
                    txtHoTen.setText(row[1]); txtSDT.setText(row[2]);
                    txtEmail.setText(row[3]); txtCK.setText(row[4]);
                    txtHocVi.setText(row[5]);
                    break;
                }
            }
        }

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("Họ và tên:")); form.add(txtHoTen);
        form.add(new JLabel("Số điện thoại:")); form.add(txtSDT);
        form.add(new JLabel("Email:")); form.add(txtEmail);
        form.add(new JLabel("Chuyên khoa:")); form.add(txtCK);
        form.add(new JLabel("Học vị:")); form.add(txtHocVi);

        String title = maBSEdit == null ? "Thêm bác sĩ mới" : "Sửa thông tin bác sĩ";
        int result = JOptionPane.showConfirmDialog(this, form, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String hoTen = txtHoTen.getText().trim();
            if (hoTen.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
            boolean ok;
            if (maBSEdit == null) {
                String maMoi = nhanSuDAO.taoMaBSMoi();
                ok = nhanSuDAO.themBacSi(maMoi, hoTen, txtSDT.getText().trim(), txtEmail.getText().trim(), txtCK.getText().trim(), txtHocVi.getText().trim());
            } else {
                ok = nhanSuDAO.capNhatBacSi(maBSEdit, hoTen, txtSDT.getText().trim(), txtEmail.getText().trim(), txtCK.getText().trim(), txtHocVi.getText().trim());
            }
            if (ok) { taiDuLieu(); JOptionPane.showMessageDialog(this, "Lưu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE); }
            else JOptionPane.showMessageDialog(this, "Có lỗi xảy ra!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienThiDialogTaoTaiKhoan() {
        JTextField txtTenDN = new JTextField(15);
        JPasswordField txtMK = new JPasswordField(15);
        List<String[]> dsVaiTro = taiKhoanDAO.getAllVaiTro();
        JComboBox<String> cboVT = new JComboBox<>();
        for (String[] vt : dsVaiTro) cboVT.addItem(vt[0] + " - " + vt[1]);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Tên đăng nhập:")); form.add(txtTenDN);
        form.add(new JLabel("Mật khẩu:")); form.add(txtMK);
        form.add(new JLabel("Vai trò:")); form.add(cboVT);

        int result = JOptionPane.showConfirmDialog(this, form, "Tạo tài khoản mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String tenDN = txtTenDN.getText().trim();
            String matKhau = new String(txtMK.getPassword()).trim();
            if (tenDN.isEmpty() || matKhau.length() < 4) {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập và mật khẩu (tối thiểu 4 ký tự) không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String maVT = ((String) cboVT.getSelectedItem()).split(" - ")[0];
            // Tạo mã TK mới
            String maTK = "TK" + System.currentTimeMillis() % 10000;
            boolean ok = taiKhoanDAO.taoTaiKhoan(maTK, tenDN, matKhau, maVT);
            if (ok) { taiDuLieu(); JOptionPane.showMessageDialog(this, "Tạo tài khoản thành công!\nMã: " + maTK, "Thành công", JOptionPane.INFORMATION_MESSAGE); }
            else JOptionPane.showMessageDialog(this, "Có lỗi! Tên đăng nhập có thể đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===================== TẢI DỮ LIỆU =====================
    public void taiDuLieu() {
        // Nhân viên
        modelNV.setRowCount(0);
        for (String[] row : nhanSuDAO.getAllNhanVien()) {
            modelNV.addRow(row);
        }
        // Bác sĩ
        modelBS.setRowCount(0);
        for (String[] row : nhanSuDAO.getAllBacSi()) {
            modelBS.addRow(row);
        }
        // Tài khoản
        modelTK.setRowCount(0);
        for (String[] row : taiKhoanDAO.getAllTaiKhoan()) {
            modelTK.addRow(row);
        }
    }
}

