package com.nhakhoa.gui;

import com.nhakhoa.bus.BenhNhanBUS;
import com.nhakhoa.bus.LichHenBUS;
import com.nhakhoa.dao.DichVuDAO;
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
    private JComboBox<LichHen> cboBenhNhanCho;
    private JLabel lblPatientBadge, lblAllergyNotice;
    private JTextField txtTrieuChung, txtChanDoan, txtLoiDan, txtViTriRangChon;
    private JComboBox<DichVu> cboDichVu;
    private JSpinner spSoLuongDV;
    private JLabel lblTongTamTinh;

    private JTable tblDichVuChiDinh, tblDonThuoc;
    private DefaultTableModel modelDV, modelThuoc;

    private List<ChiTietDieuTri> listCTDT = new ArrayList<>();
    private List<ChiTietDonThuoc> listThuoc = new ArrayList<>();
    private List<JButton> toothButtons = new ArrayList<>();

    private LichHenBUS lichHenBUS = new LichHenBUS();
    private BenhNhanBUS benhNhanBUS = new BenhNhanBUS();
    private DichVuDAO dichVuDAO = new DichVuDAO();
    private PhieuKhamDAO phieuKhamDAO = new PhieuKhamDAO();

    public PanelKhamBenh() {
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

        JLabel lblSub = new JLabel("Khám lâm sàng, chẩn đoán sơ đồ răng và chỉ định thủ thuật");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        pnlTitle.add(lblHeading);
        pnlTitle.add(lblSub);

        JPanel pnlHeaderActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlHeaderActions.setOpaque(false);

        JButton btnLuuKham = UITheme.createButton("Lưu phiếu khám", Color.WHITE, UITheme.TEXT_DARK);
        btnLuuKham.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        btnLuuKham.addActionListener(e -> xuLyLuuCaKham());

        JButton btnHoanTat = UITheme.createButton("Hoàn tất điều trị & Trừ kho (FEFO)", UITheme.PRIMARY, Color.WHITE);
        btnHoanTat.addActionListener(e -> xuLyLuuCaKham());

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

        // ==================== 3. BODY: 2 CỘT (SƠ ĐỒ RĂNG + DỊCH VỤ) ====================
        JPanel pnlBody = new JPanel(new BorderLayout(14, 0));
        pnlBody.setOpaque(false);

        // CỘT TRÁI (Sơ đồ răng + Khám lâm sàng)
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 12));
        pnlLeft.setOpaque(false);

        // Sơ đồ răng Card (Theo Mockup 05)
        JPanel pnlDentalChartCard = UITheme.createCard();
        pnlDentalChartCard.setLayout(new BorderLayout(0, 10));

        JPanel pnlChartTitle = new JPanel(new BorderLayout());
        pnlChartTitle.setOpaque(false);
        JLabel lblChart = new JLabel("Sơ đồ răng (Người lớn 18 - 48)");
        lblChart.setFont(UITheme.FONT_SUBTITLE);
        lblChart.setForeground(UITheme.TEXT_DARK);

        txtViTriRangChon = new JTextField("Răng 36", 10);
        txtViTriRangChon.setEditable(false);
        txtViTriRangChon.setBackground(new Color(241, 245, 249));

        JPanel pnlSelTooth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        pnlSelTooth.setOpaque(false);
        pnlSelTooth.add(new JLabel("Đang chọn:"));
        pnlSelTooth.add(txtViTriRangChon);

        pnlChartTitle.add(lblChart, BorderLayout.WEST);
        pnlChartTitle.add(pnlSelTooth, BorderLayout.EAST);

        // Grid Sơ đồ răng 2 hàm
        JPanel pnlTeethGrid = new JPanel(new GridLayout(2, 1, 0, 6));
        pnlTeethGrid.setOpaque(false);

        // Hàm trên (18..11 | 21..28)
        JPanel pnlUpperTeeth = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
        pnlUpperTeeth.setOpaque(false);
        int[] upper = {18, 17, 16, 15, 14, 13, 12, 11, 21, 22, 23, 24, 25, 26, 27, 28};
        for (int t : upper) pnlUpperTeeth.add(taoNutRang(t));

        // Hàm dưới (48..41 | 31..38)
        JPanel pnlLowerTeeth = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
        pnlLowerTeeth.setOpaque(false);
        int[] lower = {48, 47, 46, 45, 44, 43, 42, 41, 31, 32, 33, 34, 35, 36, 37, 38};
        for (int t : lower) pnlLowerTeeth.add(taoNutRang(t));

        pnlTeethGrid.add(pnlUpperTeeth);
        pnlTeethGrid.add(pnlLowerTeeth);

        pnlDentalChartCard.add(pnlChartTitle, BorderLayout.NORTH);
        pnlDentalChartCard.add(pnlTeethGrid, BorderLayout.CENTER);

        // Card Khám lâm sàng
        JPanel pnlClinicalCard = UITheme.createCard();
        pnlClinicalCard.setLayout(new GridLayout(3, 1, 0, 8));

        txtTrieuChung = new JTextField("Đau nhức răng hàm dưới khi ăn đồ nóng lạnh");
        txtChanDoan = new JTextField("Sâu răng số 36 độ 2, viêm nướu nhẹ");
        txtLoiDan = new JTextField("Tránh nhai đồ quá cứng trong 24h, tái khám sau 1 tuần");

        pnlClinicalCard.add(taoField("Triệu chứng lâm sàng:", txtTrieuChung));
        pnlClinicalCard.add(taoField("Chẩn đoán bác sĩ:", txtChanDoan));
        pnlClinicalCard.add(taoField("Lời dặn sau thủ thuật:", txtLoiDan));

        pnlLeft.add(pnlDentalChartCard, BorderLayout.NORTH);
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

        String[] headersDV = {"Dịch vụ nha khoa", "Răng", "SL", "Thành tiền"};
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

    private JButton taoNutRang(int soRang) {
        JButton btn = new JButton(String.valueOf(soRang));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setPreferredSize(new Dimension(38, 30));
        btn.setBackground(soRang == 36 ? UITheme.PRIMARY_LIGHT : Color.WHITE);
        btn.setForeground(soRang == 36 ? UITheme.PRIMARY : UITheme.TEXT_DARK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(soRang == 36 ? UITheme.PRIMARY : UITheme.BORDER_COLOR, 1, true));

        btn.addActionListener(e -> {
            for (JButton b : toothButtons) {
                b.setBackground(Color.WHITE);
                b.setForeground(UITheme.TEXT_DARK);
                b.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
            }
            btn.setBackground(UITheme.PRIMARY_LIGHT);
            btn.setForeground(UITheme.PRIMARY);
            btn.setBorder(BorderFactory.createLineBorder(UITheme.PRIMARY, 1, true));
            txtViTriRangChon.setText("Răng " + soRang);
        });

        toothButtons.add(btn);
        return btn;
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
        for (LichHen lh : list) {
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
        } else {
            lblPatientBadge.setText("  Chưa có bệnh nhân ở phòng chờ  ");
            lblAllergyNotice.setText("  Vui lòng tiếp nhận tại sảnh  ");
            lblAllergyNotice.setBackground(new Color(241, 245, 249));
            lblAllergyNotice.setForeground(UITheme.TEXT_MUTED);
            lblAllergyNotice.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
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

        ChiTietDieuTri ct = new ChiTietDieuTri("", dv.getMaDV(), dv.getTenDV(), txtViTriRangChon.getText().trim(), sl, dv.getDonGia(), tt);
        listCTDT.add(ct);

        modelDV.addRow(new Object[]{dv.getTenDV(), txtViTriRangChon.getText().trim(), sl, FormatUtil.formatMoney(tt)});

        double tong = 0;
        for (ChiTietDieuTri c : listCTDT) tong += c.getThanhTien();
        lblTongTamTinh.setText("Tổng tạm tính: " + FormatUtil.formatMoney(tong));
    }

    private void xuLyLuuCaKham() {
        LichHen lh = (LichHen) cboBenhNhanCho.getSelectedItem();
        if (lh == null) {
            JOptionPane.showMessageDialog(this, "Không có bệnh nhân nào trong hàng đợi khám!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
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
                JOptionPane.showMessageDialog(this, "Đã lưu kết quả điều trị cho " + lh.getTenBN() + "!\nHệ thống tự động trừ tồn kho theo FEFO (RB12).", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                listCTDT.clear();
                listThuoc.clear();
                modelDV.setRowCount(0);
                modelThuoc.setRowCount(0);
                lblTongTamTinh.setText("Tổng tạm tính: 0 đ");
                taiDanhSachHangDoi();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
