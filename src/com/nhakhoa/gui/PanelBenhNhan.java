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
    private JTextField txtSearch;
    private JTable tblListPatients;
    private DefaultTableModel modelListPatients;

    private JLabel lblProfileName, lblProfileSub;
    private JLabel lblInfoDOB, lblInfoPhone, lblInfoCCCD, lblInfoAddress, lblInfoDoctor, lblInfoStatus;
    private JLabel lblAllergyAlert;
    private JTable tblTreatmentHistory;
    private DefaultTableModel modelHistory;

    private BenhNhanBUS benhNhanBUS = new BenhNhanBUS();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private BenhNhan selectedBN = null;

    public PanelBenhNhan() {
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        setBackground(UITheme.BG_MAIN);

        // ==================== 1. PAGE HEADER ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTitle.setOpaque(false);
        JLabel lblHeading = new JLabel("Hồ sơ bệnh nhân");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeading.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Thông tin tập trung, liên kết xuyên suốt quá trình điều trị");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        pnlTitle.add(lblHeading);
        pnlTitle.add(lblSub);

        JPanel pnlHeaderActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlHeaderActions.setOpaque(false);

        JButton btnThemBN = UITheme.createButton("+ Thêm bệnh nhân", UITheme.PRIMARY, Color.WHITE);
        btnThemBN.addActionListener(e -> hienDialogThemBN(null));
        pnlHeaderActions.add(btnThemBN);

        pnlHeader.add(pnlTitle, BorderLayout.WEST);
        pnlHeader.add(pnlHeaderActions, BorderLayout.EAST);

        // ==================== 2. BODY SPLIT (LEFT LIST + RIGHT PROFILE) ====================
        JPanel pnlBody = new JPanel(new BorderLayout(14, 0));
        pnlBody.setOpaque(false);

        // CỘT TRÁI: Tìm kiếm + Danh sách bệnh nhân
        JPanel pnlLeftCard = UITheme.createCard();
        pnlLeftCard.setLayout(new BorderLayout(0, 10));
        pnlLeftCard.setPreferredSize(new Dimension(320, 0));

        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm tên, SĐT, mã bệnh nhân...");
        txtSearch.putClientProperty("JComponent.roundRect", true);
        txtSearch.setPreferredSize(new Dimension(0, 36));
        txtSearch.addActionListener(e -> xuLyTimKiem());

        String[] headersList = {"Mã BN", "Họ Tên", "Số Điện Thoại"};
        modelListPatients = new DefaultTableModel(headersList, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblListPatients = new JTable(modelListPatients);
        UITheme.formatTable(tblListPatients);

        tblListPatients.getSelectionModel().addListSelectionListener(e -> {
            int row = tblListPatients.getSelectedRow();
            if (row >= 0) {
                String ma = modelListPatients.getValueAt(row, 0).toString();
                hienChiTietBenhNhan(ma);
            }
        });

        pnlLeftCard.add(txtSearch, BorderLayout.NORTH);
        pnlLeftCard.add(new JScrollPane(tblListPatients), BorderLayout.CENTER);

        // CỘT PHẢI: Chi tiết hồ sơ bệnh nhân (Theo Mockup 04)
        JPanel pnlRightCard = UITheme.createCard();
        pnlRightCard.setLayout(new BorderLayout(0, 14));

        // Profile Top
        JPanel pnlProfileTop = new JPanel(new BorderLayout());
        pnlProfileTop.setOpaque(false);

        JPanel pnlNameBox = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlNameBox.setOpaque(false);
        lblProfileName = new JLabel("Chưa chọn bệnh nhân");
        lblProfileName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblProfileName.setForeground(UITheme.TEXT_DARK);

        lblProfileSub = new JLabel("Chọn bệnh nhân từ danh sách để xem hồ sơ");
        lblProfileSub.setFont(UITheme.FONT_SMALL);
        lblProfileSub.setForeground(UITheme.TEXT_MUTED);

        pnlNameBox.add(lblProfileName);
        pnlNameBox.add(lblProfileSub);

        JButton btnEditProfile = UITheme.createButton("Chỉnh sửa hồ sơ", Color.WHITE, UITheme.TEXT_DARK);
        btnEditProfile.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        btnEditProfile.addActionListener(e -> {
            if (selectedBN != null) hienDialogThemBN(selectedBN);
        });

        pnlProfileTop.add(pnlNameBox, BorderLayout.WEST);
        pnlProfileTop.add(btnEditProfile, BorderLayout.EAST);

        // Info Grid 6 ô
        JPanel pnlInfoGrid = new JPanel(new GridLayout(2, 3, 10, 8));
        pnlInfoGrid.setBackground(new Color(248, 250, 252));
        pnlInfoGrid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        lblInfoDOB = new JLabel("Ngày sinh: ---");
        lblInfoPhone = new JLabel("SĐT: ---");
        lblInfoCCCD = new JLabel("CCCD: ---");
        lblInfoAddress = new JLabel("Địa chỉ: TP. Hồ Chí Minh");
        lblInfoDoctor = new JLabel("Bác sĩ: BS. Nguyễn Văn Hùng");
        lblInfoStatus = new JLabel("Hồ sơ: Hoạt động");

        pnlInfoGrid.add(lblInfoDOB);
        pnlInfoGrid.add(lblInfoPhone);
        pnlInfoGrid.add(lblInfoCCCD);
        pnlInfoGrid.add(lblInfoAddress);
        pnlInfoGrid.add(lblInfoDoctor);
        pnlInfoGrid.add(lblInfoStatus);

        // Allergy Warning Banner
        lblAllergyAlert = new JLabel("Tiền sử bệnh lý / Dị ứng: Không có ghi nhận dị ứng");
        lblAllergyAlert.setFont(UITheme.FONT_BOLD);
        lblAllergyAlert.setForeground(UITheme.WARNING_TEXT);
        lblAllergyAlert.setBackground(UITheme.WARNING_BG);
        lblAllergyAlert.setOpaque(true);
        lblAllergyAlert.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(253, 230, 138), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JPanel pnlProfileHeaderGroup = new JPanel(new BorderLayout(0, 10));
        pnlProfileHeaderGroup.setOpaque(false);
        pnlProfileHeaderGroup.add(pnlProfileTop, BorderLayout.NORTH);
        pnlProfileHeaderGroup.add(pnlInfoGrid, BorderLayout.CENTER);
        pnlProfileHeaderGroup.add(lblAllergyAlert, BorderLayout.SOUTH);

        // Profile Tabs
        JTabbedPane tabProfile = new JTabbedPane();

        JPanel pnlTabHistory = new JPanel(new BorderLayout());
        pnlTabHistory.setOpaque(false);

        String[] headersH = {"Ngày điều trị", "Thủ thuật / Dịch vụ nha khoa", "Bác sĩ phụ trách", "Kết quả lâm sàng", "Trạng thái"};
        modelHistory = new DefaultTableModel(headersH, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTreatmentHistory = new JTable(modelHistory);
        UITheme.formatTable(tblTreatmentHistory);
        tblTreatmentHistory.getColumnModel().getColumn(4).setCellRenderer(new UITheme.StatusBadgeRenderer());
        pnlTabHistory.add(new JScrollPane(tblTreatmentHistory), BorderLayout.CENTER);

        tabProfile.addTab("Lịch sử khám & điều trị", pnlTabHistory);
        tabProfile.addTab("Kế hoạch niềng răng", new JLabel("Lộ trình chỉnh nha dài hạn liên kết hồ sơ.", JLabel.CENTER));
        tabProfile.addTab("Phim X-Quang & Hình ảnh", new JLabel("Kho lưu trữ hình ảnh chẩn đoán cận lâm sàng.", JLabel.CENTER));

        pnlRightCard.add(pnlProfileHeaderGroup, BorderLayout.NORTH);
        pnlRightCard.add(tabProfile, BorderLayout.CENTER);

        pnlBody.add(pnlLeftCard, BorderLayout.WEST);
        pnlBody.add(pnlRightCard, BorderLayout.CENTER);

        add(pnlHeader, BorderLayout.NORTH);
        add(pnlBody, BorderLayout.CENTER);

        taiDuLieu();
    }

    public void timKiemTuBenNgoai(String keyword) {
        txtSearch.setText(keyword);
        xuLyTimKiem();
    }

    public void taiDuLieu() {
        modelListPatients.setRowCount(0);
        List<BenhNhan> list = benhNhanBUS.getAll();
        for (BenhNhan bn : list) {
            modelListPatients.addRow(new Object[]{bn.getMaBN(), bn.getHoTen(), bn.getSoDienThoai()});
        }
        if (!list.isEmpty()) {
            tblListPatients.setRowSelectionInterval(0, 0);
        }
    }

    private void xuLyTimKiem() {
        modelListPatients.setRowCount(0);
        List<BenhNhan> list = benhNhanBUS.timKiem(txtSearch.getText().trim());
        for (BenhNhan bn : list) {
            modelListPatients.addRow(new Object[]{bn.getMaBN(), bn.getHoTen(), bn.getSoDienThoai()});
        }
    }

    private void hienChiTietBenhNhan(String maBN) {
        List<BenhNhan> list = benhNhanBUS.getAll();
        for (BenhNhan bn : list) {
            if (bn.getMaBN().equals(maBN)) {
                selectedBN = bn;
                lblProfileName.setText(bn.getHoTen());
                lblProfileSub.setText(bn.getMaBN() + " · Hồ sơ mở ngày: " + (bn.getNgayTao() != null ? sdf.format(bn.getNgayTao()) : "10/11/2026"));
                lblInfoDOB.setText("Ngày sinh: " + (bn.getNgaySinh() != null ? sdf.format(bn.getNgaySinh()) : "---"));
                lblInfoPhone.setText("SĐT: " + bn.getSoDienThoai());
                lblInfoCCCD.setText("CCCD: " + (bn.getSoCCCD() != null ? bn.getSoCCCD() : "---"));
                lblInfoStatus.setText("Trạng thái: " + bn.getTinhTrang());

                String ts = bn.getTienSuBenh();
                if (ts != null && !ts.trim().isEmpty()) {
                    lblAllergyAlert.setText("Tiền sử bệnh lý / Dị ứng: " + ts);
                    lblAllergyAlert.setVisible(true);
                } else {
                    lblAllergyAlert.setText("Tiền sử bệnh lý: Không có ghi nhận dị ứng đặc biệt");
                }

                // Nạp lịch sử mẫu
                modelHistory.setRowCount(0);
                modelHistory.addRow(new Object[]{"10/11/2026", "Cạo vôi răng & Đánh bóng (2 hàm)", "BS. Nguyễn Văn Hùng", "Cao răng độ 1, làm sạch êm", "HOAN_THANH"});
                modelHistory.addRow(new Object[]{"15/09/2026", "Trám răng thẩm mỹ Composite - Răng 36", "BS. Nguyễn Văn Hùng", "Xoang sâu loại II, khớp cắn chuẩn", "HOAN_THANH"});
                break;
            }
        }
    }

    private void hienDialogThemBN(BenhNhan bnEdit) {
        boolean isEdit = (bnEdit != null);
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), isEdit ? "Cập nhật hồ sơ bệnh nhân" : "Thêm bệnh nhân mới", true);
        dlg.setSize(440, 500);
        dlg.setLocationRelativeTo(this);

        JPanel pnl = new JPanel(new BorderLayout(0, 14));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblTitle = new JLabel(isEdit ? "CẬP NHẬT HỒ SƠ" : "THÊM HỒ SƠ BỆNH NHÂN");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.PRIMARY);

        JPanel pnlForm = new JPanel(new GridLayout(6, 1, 0, 8));
        pnlForm.setOpaque(false);

        JTextField txtMa = new JTextField(isEdit ? bnEdit.getMaBN() : benhNhanBUS.taoMaBNMoi());
        txtMa.setEditable(false);
        JTextField txtTen = new JTextField(isEdit ? bnEdit.getHoTen() : "");
        JTextField txtNS = new JTextField(isEdit && bnEdit.getNgaySinh() != null ? sdf.format(bnEdit.getNgaySinh()) : "15/08/1998");
        JTextField txtC = new JTextField(isEdit && bnEdit.getSoCCCD() != null ? bnEdit.getSoCCCD() : "");
        JTextField txtSDT = new JTextField(isEdit ? bnEdit.getSoDienThoai() : "");
        JTextField txtTS = new JTextField(isEdit && bnEdit.getTienSuBenh() != null ? bnEdit.getTienSuBenh() : "");

        pnlForm.add(taoField("Mã bệnh nhân:", txtMa));
        pnlForm.add(taoField("Họ và tên (*):", txtTen));
        pnlForm.add(taoField("Ngày sinh (dd/MM/yyyy):", txtNS));
        pnlForm.add(taoField("Số CCCD:", txtC));
        pnlForm.add(taoField("Số điện thoại (*):", txtSDT));
        pnlForm.add(taoField("Tiền sử bệnh lý / Dị ứng:", txtTS));

        JButton btnLuu = UITheme.createButton(isEdit ? "Lưu thay đổi" : "Lưu bệnh nhân", UITheme.PRIMARY, Color.WHITE);
        btnLuu.setPreferredSize(new Dimension(0, 40));
        btnLuu.addActionListener(e -> {
            try {
                Date ns = sdf.parse(txtNS.getText().trim());
                BenhNhan bn = new BenhNhan(txtMa.getText().trim(), txtTen.getText().trim(), ns, txtC.getText().trim(), txtSDT.getText().trim(), txtTS.getText().trim(), "HOAT_DONG", new Date());
                if (isEdit) {
                    benhNhanBUS.sua(bn);
                } else {
                    benhNhanBUS.them(bn);
                }
                JOptionPane.showMessageDialog(dlg, "Thao tác thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                taiDuLieu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnl.add(lblTitle, BorderLayout.NORTH);
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
