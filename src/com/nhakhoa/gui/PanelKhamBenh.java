package com.nhakhoa.gui;

import com.nhakhoa.bus.BenhNhanBUS;
import com.nhakhoa.bus.LichHenBUS;
import com.nhakhoa.dao.DichVuDAO;
import com.nhakhoa.dao.KhoDAO;
import com.nhakhoa.dao.PhieuKhamDAO;
import com.nhakhoa.model.*;
import com.nhakhoa.util.FormatUtil;
import com.nhakhoa.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelKhamBenh extends JPanel {
    private TaiKhoan currentUser;
    private JComboBox<LichHen> cboBenhNhanCho;
    private JLabel lblPatientBadge, lblAllergyNotice;
    private JTextField txtTrieuChung, txtChanDoan, txtPhuongPhap, txtLoiDan, txtGhiChu;
    private JComboBox<DichVu> cboDichVu;
    private JSpinner spSoLuongDV;
    private JLabel lblTongTamTinh;

    private JTable tblDichVuChiDinh, tblDonThuoc;
    private DefaultTableModel modelDV, modelThuoc;

    private List<ChiTietDieuTri> listCTDT = new ArrayList<>();
    private List<ChiTietDonThuoc> listThuoc = new ArrayList<>();

    private LichHenBUS lichHenBUS = new LichHenBUS();
    private BenhNhanBUS benhNhanBUS = new BenhNhanBUS();
    private DichVuDAO dichVuDAO = new DichVuDAO();
    private PhieuKhamDAO phieuKhamDAO = new PhieuKhamDAO();
    private KhoDAO khoDAO = new KhoDAO();

    public PanelKhamBenh() {
        this(null);
    }

    public PanelKhamBenh(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        setBackground(UITheme.BG_MAIN);

        // ==================== 1. PAGE HEADER ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTitle.setOpaque(false);
        JLabel lblHeading = new JLabel("Khám & điều trị");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeading.setForeground(UITheme.TEXT_DARK);

        String subText = (currentUser != null && currentUser.isBacSi()) ?
                "Hàng đợi bệnh nhân tiếp nhận của Bác sĩ: " + currentUser.getHoTen() :
                "Khám lâm sàng, chẩn đoán bệnh án và chỉ định điều trị";
        JLabel lblSub = new JLabel(subText);
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        pnlTitle.add(lblHeading);
        pnlTitle.add(lblSub);

        JPanel pnlHeaderActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlHeaderActions.setOpaque(false);

        JButton btnLichSuKham = UITheme.createButton("Lịch sử ca khám", Color.WHITE, UITheme.TEXT_DARK);
        btnLichSuKham.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        btnLichSuKham.addActionListener(e -> hienDialogLichSuKham());

        JButton btnLuuKham = UITheme.createButton("Lưu phiếu khám", Color.WHITE, UITheme.TEXT_DARK);
        btnLuuKham.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        btnLuuKham.addActionListener(e -> xuLyLuuCaKham());

        JButton btnHoanTat = UITheme.createButton("Hoàn tất điều trị", UITheme.PRIMARY, Color.WHITE);
        btnHoanTat.addActionListener(e -> xuLyLuuCaKham());

        pnlHeaderActions.add(btnLichSuKham);
        pnlHeaderActions.add(btnLuuKham);
        pnlHeaderActions.add(btnHoanTat);

        pnlHeader.add(pnlTitle, BorderLayout.WEST);
        pnlHeader.add(pnlHeaderActions, BorderLayout.EAST);

        // ==================== 2. PATIENT BANNER CARD ====================
        JPanel pnlBannerCard = UITheme.createCard();
        pnlBannerCard.setLayout(new BorderLayout(14, 0));

        JPanel pnlQueueBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlQueueBox.setOpaque(false);
        pnlQueueBox.add(new JLabel("Hàng đợi khám (Đã đến):"));
        cboBenhNhanCho = new JComboBox<>();
        cboBenhNhanCho.setPreferredSize(new Dimension(280, 32));
        cboBenhNhanCho.addActionListener(e -> capNhatThongTinBN());
        pnlQueueBox.add(cboBenhNhanCho);

        JButton btnReload = new JButton("Tải lại");
        btnReload.addActionListener(e -> taiDanhSachHangDoi());
        pnlQueueBox.add(btnReload);

        JPanel pnlBannerStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBannerStatus.setOpaque(false);

        lblPatientBadge = new JLabel("  Ghế 01 · Phòng 1  ");
        lblPatientBadge.setFont(UITheme.FONT_SMALL);
        lblPatientBadge.setOpaque(true);
        lblPatientBadge.setBackground(UITheme.INFO_BG);
        lblPatientBadge.setForeground(UITheme.INFO_TEXT);

        lblAllergyNotice = new JLabel("  Tiền sử: Bình thường  ");
        lblAllergyNotice.setFont(UITheme.FONT_SMALL);
        lblAllergyNotice.setOpaque(true);
        lblAllergyNotice.setBackground(UITheme.WARNING_BG);
        lblAllergyNotice.setForeground(UITheme.WARNING_TEXT);

        pnlBannerStatus.add(lblPatientBadge);
        pnlBannerStatus.add(lblAllergyNotice);

        pnlBannerCard.add(pnlQueueBox, BorderLayout.WEST);
        pnlBannerCard.add(pnlBannerStatus, BorderLayout.EAST);

        JPanel pnlTopGroup = new JPanel(new BorderLayout(0, 10));
        pnlTopGroup.setOpaque(false);
        pnlTopGroup.add(pnlHeader, BorderLayout.NORTH);
        pnlTopGroup.add(pnlBannerCard, BorderLayout.SOUTH);

        // ==================== 3. BODY: 2 CỘT (KHÁM BỆNH ÁN + DỊCH VỤ) ====================
        JPanel pnlBody = new JPanel(new BorderLayout(14, 0));
        pnlBody.setOpaque(false);

        // CỘT TRÁI (Hồ sơ khám lâm sàng & Bệnh án)
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 12));
        pnlLeft.setOpaque(false);

        JPanel pnlClinicalCard = UITheme.createCard();
        pnlClinicalCard.setLayout(new BorderLayout(0, 12));

        JLabel lblClinicalTitle = new JLabel("Hồ sơ khám lâm sàng & Điều trị");
        lblClinicalTitle.setFont(UITheme.FONT_SUBTITLE);
        lblClinicalTitle.setForeground(UITheme.PRIMARY);

        JPanel pnlClinicalForm = new JPanel(new GridLayout(5, 1, 0, 10));
        pnlClinicalForm.setOpaque(false);

        txtTrieuChung = new JTextField("Đau nhức ê buốt vùng răng hàm khi ăn nhai đồ nóng lạnh");
        txtChanDoan = new JTextField("Sâu răng ngà nông, viêm nướu nhẹ quanh chân răng");
        txtPhuongPhap = new JTextField("Trám răng phục hồi thẩm mỹ Composite, vệ sinh cạo vôi răng");
        txtLoiDan = new JTextField("Tránh nhai thức ăn quá cứng trong 24h, vệ sinh răng miệng ngày 2 lần");
        txtGhiChu = new JTextField("Bệnh nhân hợp tác tốt, không có dấu hiệu dị ứng thuốc tê");

        pnlClinicalForm.add(taoField("Triệu chứng lâm sàng ban đầu:", txtTrieuChung));
        pnlClinicalForm.add(taoField("Chẩn đoán chuyên môn của Bác sĩ:", txtChanDoan));
        pnlClinicalForm.add(taoField("Phương pháp / Phác đồ điều trị:", txtPhuongPhap));
        pnlClinicalForm.add(taoField("Lời dặn sau thủ thuật & Hướng dẫn vệ sinh:", txtLoiDan));
        pnlClinicalForm.add(taoField("Ghi chú tiến trình theo dõi:", txtGhiChu));

        pnlClinicalCard.add(lblClinicalTitle, BorderLayout.NORTH);
        pnlClinicalCard.add(pnlClinicalForm, BorderLayout.CENTER);

        pnlLeft.add(pnlClinicalCard, BorderLayout.CENTER);

        // CỘT PHẢI (Chỉ định dịch vụ & Kê đơn thuốc - 460px)
        JPanel pnlRight = new JPanel(new BorderLayout(0, 12));
        pnlRight.setPreferredSize(new Dimension(460, 0));
        pnlRight.setOpaque(false);

        // Card Chỉ định dịch vụ
        JPanel pnlDVCard = UITheme.createCard();
        pnlDVCard.setLayout(new BorderLayout(0, 8));

        JPanel pnlDVTop = new JPanel(new BorderLayout());
        pnlDVTop.setOpaque(false);
        JLabel lblDVTitle = new JLabel("Chỉ định dịch vụ");
        lblDVTitle.setFont(UITheme.FONT_SUBTITLE);
        lblDVTitle.setForeground(UITheme.TEXT_DARK);
        pnlDVTop.add(lblDVTitle, BorderLayout.WEST);

        JPanel pnlDVAdd = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        pnlDVAdd.setOpaque(false);
        cboDichVu = new JComboBox<>();
        cboDichVu.setPreferredSize(new Dimension(200, 30));
        spSoLuongDV = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JButton btnAddDV = UITheme.createButton("+ Thêm", UITheme.PRIMARY, Color.WHITE);
        btnAddDV.addActionListener(e -> xuLyThemDV());

        pnlDVAdd.add(cboDichVu);
        pnlDVAdd.add(spSoLuongDV);
        pnlDVAdd.add(btnAddDV);

        String[] headersDV = {"Dịch vụ điều trị", "Số lượng", "Đơn giá", "Thành tiền"};
        modelDV = new DefaultTableModel(headersDV, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDichVuChiDinh = new JTable(modelDV);
        UITheme.formatTable(tblDichVuChiDinh);

        JPanel pnlDVBottom = new JPanel(new BorderLayout());
        pnlDVBottom.setOpaque(false);
        lblTongTamTinh = new JLabel("Tổng tạm tính: 0 đ");
        lblTongTamTinh.setFont(UITheme.FONT_BOLD);
        lblTongTamTinh.setForeground(UITheme.PRIMARY);
        pnlDVBottom.add(lblTongTamTinh, BorderLayout.EAST);

        JPanel pnlDVCardNorth = new JPanel(new BorderLayout(0, 6));
        pnlDVCardNorth.setOpaque(false);
        pnlDVCardNorth.add(pnlDVTop, BorderLayout.NORTH);
        pnlDVCardNorth.add(pnlDVAdd, BorderLayout.SOUTH);

        pnlDVCard.add(pnlDVCardNorth, BorderLayout.NORTH);
        pnlDVCard.add(new JScrollPane(tblDichVuChiDinh), BorderLayout.CENTER);
        pnlDVCard.add(pnlDVBottom, BorderLayout.SOUTH);

        // Card Kê đơn thuốc
        JPanel pnlThuocCard = UITheme.createCard();
        pnlThuocCard.setLayout(new BorderLayout(0, 8));
        pnlThuocCard.setPreferredSize(new Dimension(0, 200));

        JPanel pnlThuocHeader = new JPanel(new BorderLayout());
        pnlThuocHeader.setOpaque(false);

        JLabel lblThuocTitle = new JLabel("Kê đơn thuốc điều trị");
        lblThuocTitle.setFont(UITheme.FONT_SUBTITLE);
        lblThuocTitle.setForeground(UITheme.TEXT_DARK);

        JButton btnThemThuoc = UITheme.createButton("+ Thêm thuốc", Color.WHITE, UITheme.TEXT_DARK);
        btnThemThuoc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        btnThemThuoc.addActionListener(e -> hienDialogThemThuoc());

        pnlThuocHeader.add(lblThuocTitle, BorderLayout.WEST);
        pnlThuocHeader.add(btnThemThuoc, BorderLayout.EAST);

        String[] headersT = {"Tên thuốc", "SL", "Liều dùng"};
        modelThuoc = new DefaultTableModel(headersT, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDonThuoc = new JTable(modelThuoc);
        UITheme.formatTable(tblDonThuoc);

        pnlThuocCard.add(pnlThuocHeader, BorderLayout.NORTH);
        pnlThuocCard.add(new JScrollPane(tblDonThuoc), BorderLayout.CENTER);

        pnlRight.add(pnlDVCard, BorderLayout.CENTER);
        pnlRight.add(pnlThuocCard, BorderLayout.SOUTH);

        pnlBody.add(pnlLeft, BorderLayout.CENTER);
        pnlBody.add(pnlRight, BorderLayout.EAST);

        add(pnlTopGroup, BorderLayout.NORTH);
        add(pnlBody, BorderLayout.CENTER);

        taiDanhSachHangDoi();
        taiDanhSachDichVu();
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

    public void taiDanhSachHangDoi() {
        cboBenhNhanCho.removeAllItems();
        List<LichHen> list = lichHenBUS.getLichHenDaDen();
        String currentMaBS = (currentUser != null && currentUser.isBacSi()) ? currentUser.getMaBacSi() : null;
        for (LichHen lh : list) {
            if (currentMaBS != null && !currentMaBS.equals(lh.getMaBS())) {
                continue; // Bác sĩ chỉ thấy danh sách bệnh nhân của mình!
            }
            cboBenhNhanCho.addItem(lh);
        }
        capNhatThongTinBN();
    }

    private void capNhatThongTinBN() {
        LichHen lh = (LichHen) cboBenhNhanCho.getSelectedItem();
        if (lh != null) {
            lblPatientBadge.setText("  " + lh.getMaGhe() + " · Phòng khám chính  ");
            BenhNhan bn = benhNhanBUS.getById(lh.getMaBN());
            if (bn != null && bn.getTienSuBenh() != null && !bn.getTienSuBenh().trim().isEmpty()) {
                lblAllergyNotice.setText("  Tiền sử/Dị ứng: " + bn.getTienSuBenh() + "  ");
                lblAllergyNotice.setBackground(UITheme.WARNING_BG);
                lblAllergyNotice.setForeground(UITheme.WARNING_TEXT);
                lblAllergyNotice.setBorder(BorderFactory.createLineBorder(new Color(253, 230, 138), 1, true));
            } else {
                lblAllergyNotice.setText("  Tiền sử: Khỏe mạnh bình thường  ");
                lblAllergyNotice.setBackground(UITheme.PRIMARY_LIGHT);
                lblAllergyNotice.setForeground(UITheme.PRIMARY);
                lblAllergyNotice.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
            }
            if (txtTrieuChung.getText().trim().isEmpty()) {
                txtTrieuChung.setText("Đau nhức ê buốt vùng răng hàm khi ăn nhai đồ nóng lạnh");
                txtChanDoan.setText("Sâu răng ngà nông, viêm nướu nhẹ quanh chân răng");
                txtPhuongPhap.setText("Trám răng phục hồi thẩm mỹ Composite, vệ sinh cạo vôi răng");
                txtLoiDan.setText("Tránh nhai thức ăn quá cứng trong 24h, vệ sinh răng miệng ngày 2 lần");
                txtGhiChu.setText("Bệnh nhân hợp tác tốt, không có dấu hiệu dị ứng thuốc tê");
            }
        } else {
            lblPatientBadge.setText("  Chưa có bệnh nhân ở phòng chờ  ");
            lblAllergyNotice.setText("  Vui lòng tiếp nhận tại sảnh  ");
            lblAllergyNotice.setBackground(new Color(241, 245, 249));
            lblAllergyNotice.setForeground(UITheme.TEXT_MUTED);
            lblAllergyNotice.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
            txtTrieuChung.setText("");
            txtChanDoan.setText("");
            txtPhuongPhap.setText("");
            txtLoiDan.setText("");
            txtGhiChu.setText("");
        }
    }

    private void hienDialogThemThuoc() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Kê đơn thuốc điều trị", true);
        dlg.setSize(400, 320);
        dlg.setLocationRelativeTo(this);

        JPanel pnl = new JPanel(new BorderLayout(0, 12));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblT = new JLabel("CHỌN THUỐC ĐIỀU TRỊ");
        lblT.setFont(UITheme.FONT_SUBTITLE);
        lblT.setForeground(UITheme.PRIMARY);

        JPanel pnlForm = new JPanel(new GridLayout(3, 1, 0, 8));
        pnlForm.setOpaque(false);

        JComboBox<String> cboThuoc = new JComboBox<>(new String[]{
                "Amoxicillin 500mg (Kháng sinh)",
                "Paracetamol 500mg (Giảm đau, hạ sốt)",
                "Rodogyl (Kháng sinh răng miệng Spiramycin+Metronidazole)",
                "Ibuprofen 400mg (Kháng viêm non-steroid)",
                "Nước súc miệng Chlorhexidine 0.12% (Chai 250ml)"
        });

        JSpinner spSL = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        JTextField txtLieu = new JTextField("Ngày uống 2 lần, mỗi lần 1 viên sau ăn no");

        pnlForm.add(taoField("Loại thuốc:", cboThuoc));
        pnlForm.add(taoField("Số lượng:", spSL));
        pnlForm.add(taoField("Liều dùng & Hướng dẫn:", txtLieu));

        JButton btnXacNhan = UITheme.createButton("Thêm vào đơn thuốc", UITheme.PRIMARY, Color.WHITE);
        btnXacNhan.setPreferredSize(new Dimension(0, 38));
        btnXacNhan.addActionListener(e -> {
            String ten = cboThuoc.getSelectedItem().toString();
            int sl = (int) spSL.getValue();
            String lieu = txtLieu.getText().trim();

            ChiTietDonThuoc ct = new ChiTietDonThuoc("", "", ten, sl, lieu);
            listThuoc.add(ct);
            modelThuoc.addRow(new Object[]{ten, sl, lieu});

            dlg.dispose();
        });

        pnl.add(lblT, BorderLayout.NORTH);
        pnl.add(pnlForm, BorderLayout.CENTER);
        pnl.add(btnXacNhan, BorderLayout.SOUTH);

        dlg.setContentPane(pnl);
        dlg.setVisible(true);
    }

    private void taiDanhSachDichVu() {
        cboDichVu.removeAllItems();
        List<DichVu> list = dichVuDAO.getAll();
        for (DichVu dv : list) {
            cboDichVu.addItem(dv);
        }
    }

    private void xuLyThemDV() {
        DichVu dv = (DichVu) cboDichVu.getSelectedItem();
        if (dv == null) return;
        int sl = (int) spSoLuongDV.getValue();
        double tt = sl * dv.getDonGia();

        ChiTietDieuTri ct = new ChiTietDieuTri("", dv.getMaDV(), dv.getTenDV(), "Răng hàm", sl, dv.getDonGia(), tt);

        // LUẬT 2: Kiểm tra tồn kho khả dụng (còn hạn + đủ định mức) trước khi cho phép chỉ định dịch vụ
        List<ChiTietDieuTri> dsKiemTra = new ArrayList<>(listCTDT);
        dsKiemTra.add(ct);
        String thieuVT = khoDAO.kiemTraDinhMucVatTuKhaDung(dsKiemTra);
        if (thieuVT != null) {
            JOptionPane.showMessageDialog(this,
                    "CẢNH BÁO TỒN KHO VẬT TƯ!\n\n"
                    + "Không đủ vật tư khả dụng trong kho để thực hiện dịch vụ này:\n"
                    + thieuVT + "\n\n"
                    + "Vui lòng thông báo Thủ kho nhập bổ sung vật tư trước khi chỉ định.",
                    "Không đủ vật tư khả dụng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        listCTDT.add(ct);
        modelDV.addRow(new Object[]{dv.getTenDV(), sl, FormatUtil.formatMoney(dv.getDonGia()), FormatUtil.formatMoney(tt)});

        double tong = 0;
        for (ChiTietDieuTri c : listCTDT) tong += c.getThanhTien();
        lblTongTamTinh.setText("Tổng tạm tính: " + FormatUtil.formatMoney(tong));
    }

    private void xuLyLuuCaKham() {
        if (currentUser != null && !currentUser.isBacSi() && !currentUser.isQuanLy()) {
            JOptionPane.showMessageDialog(this,
                    "Chức năng khám chữa bệnh và lưu ca điều trị chỉ dành cho Bác sĩ điều trị!",
                    "Thông báo phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LichHen lh = (LichHen) cboBenhNhanCho.getSelectedItem();
        if (lh == null) {
            JOptionPane.showMessageDialog(this,
                    "Hiện không có bệnh nhân nào trong hàng đợi khám!\n"
                    + "Vui lòng tiếp nhận bệnh nhân vào khám trước.",
                    "Chưa có bệnh nhân ở phòng chờ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentUser != null && currentUser.isBacSi() && currentUser.getMaBacSi() != null) {
            if (!currentUser.getMaBacSi().equals(lh.getMaBS())) {
                JOptionPane.showMessageDialog(this,
                        "Bệnh nhân này thuộc phân công khám của Bác sĩ khác (" + lh.getTenBS() + ")!\nBạn không thể lưu ca điều trị này.",
                        "Thông báo phân quyền", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Kiểm tra tồn kho khả dụng trước khi lưu ca điều trị
        if (!listCTDT.isEmpty()) {
            String thieuVT = khoDAO.kiemTraDinhMucVatTuKhaDung(listCTDT);
            if (thieuVT != null) {
                JOptionPane.showMessageDialog(this,
                        "TỪ CHỐI LƯU CA ĐIỀU TRỊ!\n\n"
                        + "Kho vật tư hiện tại không đủ số lượng khả dụng:\n"
                        + thieuVT + "\n\n"
                        + "Vui lòng kiểm tra lại tồn kho vật tư trước khi xác nhận hoàn tất.",
                        "Từ chối lưu - Thiếu vật tư", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            PhieuKham pk = new PhieuKham();
            pk.setMaPhieuKham(phieuKhamDAO.taoMaPKMoi());
            pk.setMaLichHen(lh.getMaLichHen());
            pk.setMaBN(lh.getMaBN());
            pk.setMaBS(lh.getMaBS());
            pk.setTrieuChung(txtTrieuChung.getText().trim());
            pk.setChanDoan(txtChanDoan.getText().trim());

            boolean ok = phieuKhamDAO.luuCaKham(pk, listCTDT, txtLoiDan.getText().trim(), listThuoc);
            if (ok) {
                String thongBao = "LƯU KẾT QUẢ ĐIỀU TRỊ THÀNH CÔNG!\n\n"
                        + "• Bệnh nhân: " + lh.getTenBN() + " (" + lh.getMaBN() + ")\n"
                        + "• Bác sĩ phụ trách: " + (currentUser != null ? currentUser.getHoTen() : lh.getTenBS()) + "\n"
                        + "• Mã phiếu khám: " + pk.getMaPhieuKham() + "\n"
                        + "• Trạng thái: Đã hoàn tất ca khám và điều trị.";

                JOptionPane.showMessageDialog(this, thongBao, "Khám bệnh thành công", JOptionPane.INFORMATION_MESSAGE);

                listCTDT.clear();
                listThuoc.clear();
                modelDV.setRowCount(0);
                modelThuoc.setRowCount(0);
                lblTongTamTinh.setText("Tổng tạm tính: 0 đ");
                taiDanhSachHangDoi();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể lưu kết quả điều trị! Vui lòng kiểm tra lại kết nối CSDL hoặc dữ liệu.",
                        "Lỗi lưu trữ", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienDialogLichSuKham() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Danh sách Phiếu khám & Hồ sơ điều trị", true);
        dlg.setSize(850, 560);
        dlg.setLocationRelativeTo(this);

        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblTitle = new JLabel("LỊCH SỬ CÁC PHIẾU KHÁM & ĐIỀU TRỊ ĐÃ LẬP");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.PRIMARY);

        String[] headers = {"Mã Phiếu", "Bệnh Nhân", "Bác Sĩ", "Ngày Khám", "Chẩn Đoán", "Trạng Thái"};
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(model);
        UITheme.formatTable(tbl);
        tbl.getColumnModel().getColumn(5).setCellRenderer(new UITheme.StatusBadgeRenderer());

        List<PhieuKham> listPK = phieuKhamDAO.getAll();
        String currentMaBS = (currentUser != null && currentUser.isBacSi()) ? currentUser.getMaBacSi() : null;
        java.text.SimpleDateFormat sdfNgay = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (PhieuKham pk : listPK) {
            if (currentMaBS != null && !currentMaBS.equals(pk.getMaBS())) {
                continue;
            }
            model.addRow(new Object[]{
                pk.getMaPhieuKham(),
                pk.getTenBN(),
                pk.getTenBS(),
                pk.getNgayKham() != null ? sdfNgay.format(pk.getNgayKham()) : "---",
                pk.getChanDoan(),
                pk.getTinhTrang()
            });
        }

        Runnable xemChiTiet = () -> {
            int row = tbl.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng chọn một phiếu khám trong bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String maPK = model.getValueAt(row, 0).toString();
            String html = phieuKhamDAO.getChiTietPhieuKhamHTML(maPK);

            JDialog dlgDetail = new JDialog(dlg, "Chi tiết phiếu khám - " + maPK, true);
            dlgDetail.setSize(680, 650);
            dlgDetail.setLocationRelativeTo(dlg);

            JEditorPane ep = new JEditorPane();
            ep.setContentType("text/html");
            ep.setEditable(false);
            ep.setText(html);
            ep.setCaretPosition(0);

            JPanel pnlD = new JPanel(new BorderLayout(0, 10));
            pnlD.setBackground(Color.WHITE);
            pnlD.setBorder(new EmptyBorder(10, 14, 10, 14));
            pnlD.add(new JScrollPane(ep), BorderLayout.CENTER);

            JPanel pnlB = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            pnlB.setOpaque(false);
            JButton btnIn = UITheme.createButton("In phiếu khám", UITheme.PRIMARY, Color.WHITE);
            btnIn.addActionListener(e -> {
                try { ep.print(); } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dlgDetail, "Lỗi in: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });
            JButton btnDong = UITheme.createButton("Đóng", Color.WHITE, UITheme.TEXT_DARK);
            btnDong.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
            btnDong.addActionListener(e -> dlgDetail.dispose());
            pnlB.add(btnIn);
            pnlB.add(btnDong);
            pnlD.add(pnlB, BorderLayout.SOUTH);

            dlgDetail.setContentPane(pnlD);
            dlgDetail.setVisible(true);
        };

        tbl.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) xemChiTiet.run();
            }
        });

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBottom.setOpaque(false);
        JButton btnXem = UITheme.createButton("Xem & In phiếu khám", UITheme.PRIMARY, Color.WHITE);
        btnXem.addActionListener(e -> xemChiTiet.run());
        JButton btnClose = UITheme.createButton("Đóng", Color.WHITE, UITheme.TEXT_DARK);
        btnClose.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        btnClose.addActionListener(e -> dlg.dispose());
        pnlBottom.add(btnXem);
        pnlBottom.add(btnClose);

        pnl.add(lblTitle, BorderLayout.NORTH);
        pnl.add(new JScrollPane(tbl), BorderLayout.CENTER);
        pnl.add(pnlBottom, BorderLayout.SOUTH);

        dlg.setContentPane(pnl);
        dlg.setVisible(true);
    }
}
