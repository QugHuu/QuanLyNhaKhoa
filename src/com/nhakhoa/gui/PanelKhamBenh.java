package com.nhakhoa.gui;

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
    private JTextField txtTrieuChung, txtChanDoan, txtViTriRang, txtLoiDan;
    private JComboBox<DichVu> cboDichVu;
    private JSpinner spSoLuongDV;
    private JTextField txtTenThuoc, txtDVTThuoc, txtLieuDung;
    private JSpinner spSoLuongThuoc;

    private JTable tblDichVuChiDinh, tblDonThuoc;
    private DefaultTableModel modelDV, modelThuoc;

    private List<ChiTietDieuTri> listCTDT = new ArrayList<>();
    private List<ChiTietDonThuoc> listThuoc = new ArrayList<>();

    private LichHenBUS lichHenBUS = new LichHenBUS();
    private DichVuDAO dichVuDAO = new DichVuDAO();
    private PhieuKhamDAO phieuKhamDAO = new PhieuKhamDAO();

    public PanelKhamBenh() {
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(18, 24, 18, 24));
        setBackground(UITheme.BG_MAIN);

        // TOP: Card Tiếp nhận & Chẩn đoán
        JPanel pnlTopCard = UITheme.createCard();
        pnlTopCard.setLayout(new GridLayout(3, 2, 16, 8));

        cboBenhNhanCho = new JComboBox<>();
        JButton btnReloadQueue = UITheme.createButton("Tải hàng đợi", Color.WHITE, UITheme.TEXT_DARK);
        btnReloadQueue.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnReloadQueue.addActionListener(e -> taiDanhSachHangDoi());

        JPanel pnlQueue = new JPanel(new BorderLayout(8, 0));
        pnlQueue.setOpaque(false);
        pnlQueue.add(cboBenhNhanCho, BorderLayout.CENTER);
        pnlQueue.add(btnReloadQueue, BorderLayout.EAST);

        txtTrieuChung = new JTextField();
        txtChanDoan = new JTextField();

        pnlTopCard.add(taoFieldInput("Bệnh nhân trong hàng đợi (Đã đến - RB08):", pnlQueue));
        pnlTopCard.add(taoFieldInput("Triệu chứng lâm sàng:", txtTrieuChung));
        pnlTopCard.add(taoFieldInput("Chẩn đoán chuyên môn của Bác sĩ:", txtChanDoan));

        // CENTER: 2 Cột Dịch vụ & Đơn thuốc
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 16, 0));
        pnlCenter.setOpaque(false);

        // CỘT 1: Dịch vụ điều trị
        JPanel pnlDVCard = UITheme.createCard();
        pnlDVCard.setLayout(new BorderLayout(0, 10));

        JLabel lblDVTitle = new JLabel("CHỈ ĐỊNH DỊCH VỤ NHA KHOA");
        lblDVTitle.setFont(UITheme.FONT_SUBTITLE);
        lblDVTitle.setForeground(UITheme.TEXT_DARK);

        JPanel pnlDVInput = new JPanel(new GridLayout(3, 2, 8, 6));
        pnlDVInput.setOpaque(false);
        cboDichVu = new JComboBox<>();
        spSoLuongDV = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        txtViTriRang = new JTextField("Răng 36");

        pnlDVInput.add(new JLabel("Dịch vụ:"));
        pnlDVInput.add(cboDichVu);
        pnlDVInput.add(new JLabel("Vị trí răng:"));
        pnlDVInput.add(txtViTriRang);
        pnlDVInput.add(new JLabel("Số lượng:"));
        pnlDVInput.add(spSoLuongDV);

        JButton btnThemDV = UITheme.createButton("Thêm dịch vụ", UITheme.PRIMARY, Color.WHITE);
        btnThemDV.addActionListener(e -> xuLyThemDV());

        JPanel pnlDVTop = new JPanel(new BorderLayout(0, 8));
        pnlDVTop.setOpaque(false);
        pnlDVTop.add(lblDVTitle, BorderLayout.NORTH);
        pnlDVTop.add(pnlDVInput, BorderLayout.CENTER);
        pnlDVTop.add(btnThemDV, BorderLayout.SOUTH);

        String[] headersDV = {"Tên Dịch Vụ", "Vị Trí Răng", "SL", "Đơn Giá", "Thành Tiền"};
        modelDV = new DefaultTableModel(headersDV, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDichVuChiDinh = new JTable(modelDV);
        UITheme.formatTable(tblDichVuChiDinh);

        pnlDVCard.add(pnlDVTop, BorderLayout.NORTH);
        pnlDVCard.add(new JScrollPane(tblDichVuChiDinh), BorderLayout.CENTER);

        // CỘT 2: Kê đơn thuốc
        JPanel pnlThuocCard = UITheme.createCard();
        pnlThuocCard.setLayout(new BorderLayout(0, 10));

        JLabel lblThuocTitle = new JLabel("KÊ ĐƠN THUỐC ĐIỀU TRỊ");
        lblThuocTitle.setFont(UITheme.FONT_SUBTITLE);
        lblThuocTitle.setForeground(UITheme.TEXT_DARK);

        JPanel pnlThuocInput = new JPanel(new GridLayout(4, 2, 8, 6));
        pnlThuocInput.setOpaque(false);
        txtTenThuoc = new JTextField("Amoxicillin 500mg");
        txtDVTThuoc = new JTextField("Viên");
        spSoLuongThuoc = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        txtLieuDung = new JTextField("Uống 1 viên sau ăn, ngày 2 lần");

        pnlThuocInput.add(new JLabel("Tên thuốc:"));
        pnlThuocInput.add(txtTenThuoc);
        pnlThuocInput.add(new JLabel("Đơn vị tính:"));
        pnlThuocInput.add(txtDVTThuoc);
        pnlThuocInput.add(new JLabel("Số lượng:"));
        pnlThuocInput.add(spSoLuongThuoc);
        pnlThuocInput.add(new JLabel("Liều dùng:"));
        pnlThuocInput.add(txtLieuDung);

        JButton btnThemThuoc = UITheme.createButton("Thêm vào đơn", new Color(139, 92, 246), Color.WHITE);
        btnThemThuoc.addActionListener(e -> xuLyThemThuoc());

        JPanel pnlThuocTop = new JPanel(new BorderLayout(0, 8));
        pnlThuocTop.setOpaque(false);
        pnlThuocTop.add(lblThuocTitle, BorderLayout.NORTH);
        pnlThuocTop.add(pnlThuocInput, BorderLayout.CENTER);
        pnlThuocTop.add(btnThemThuoc, BorderLayout.SOUTH);

        String[] headersThuoc = {"Tên Thuốc", "ĐVT", "SL", "Liều Dùng"};
        modelThuoc = new DefaultTableModel(headersThuoc, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDonThuoc = new JTable(modelThuoc);
        UITheme.formatTable(tblDonThuoc);

        pnlThuocCard.add(pnlThuocTop, BorderLayout.NORTH);
        pnlThuocCard.add(new JScrollPane(tblDonThuoc), BorderLayout.CENTER);

        pnlCenter.add(pnlDVCard);
        pnlCenter.add(pnlThuocCard);

        // BOTTOM: Card Lời dặn & Nút Lưu Hoàn Tất
        JPanel pnlBottomCard = UITheme.createCard();
        pnlBottomCard.setLayout(new BorderLayout(14, 10));

        JPanel pnlLoiDan = new JPanel(new BorderLayout(8, 0));
        pnlLoiDan.setOpaque(false);
        JLabel lblLD = new JLabel("Lời dặn của bác sĩ:");
        lblLD.setFont(UITheme.FONT_BOLD);
        txtLoiDan = new JTextField("Tái khám sau 1 tuần. Kiêng ăn nhai đồ quá cứng.");
        pnlLoiDan.add(lblLD, BorderLayout.WEST);
        pnlLoiDan.add(txtLoiDan, BorderLayout.CENTER);

        JButton btnLuuCaKham = UITheme.createButton("HOÀN TẤT CA KHÁM & TỰ ĐỘNG XUẤT KHO VẬT TƯ (FEFO - RB12)", UITheme.SUCCESS, Color.WHITE);
        btnLuuCaKham.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuuCaKham.setPreferredSize(new Dimension(0, 44));
        btnLuuCaKham.addActionListener(e -> xuLyLuuCaKham());

        pnlBottomCard.add(pnlLoiDan, BorderLayout.CENTER);
        pnlBottomCard.add(btnLuuCaKham, BorderLayout.SOUTH);

        add(pnlTopCard, BorderLayout.NORTH);
        add(pnlCenter, BorderLayout.CENTER);
        add(pnlBottomCard, BorderLayout.SOUTH);

        taiDanhSachHangDoi();
        taiDanhSachDichVu();
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

    public void taiDanhSachHangDoi() {
        cboBenhNhanCho.removeAllItems();
        List<LichHen> list = lichHenBUS.getLichHenDaDen();
        for (LichHen lh : list) {
            cboBenhNhanCho.addItem(lh);
        }
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
        double thanhTien = sl * dv.getDonGia();

        ChiTietDieuTri ct = new ChiTietDieuTri("", dv.getMaDV(), dv.getTenDV(), txtViTriRang.getText().trim(), sl, dv.getDonGia(), thanhTien);
        listCTDT.add(ct);

        modelDV.addRow(new Object[]{
                dv.getTenDV(),
                txtViTriRang.getText().trim(),
                sl,
                FormatUtil.formatMoney(dv.getDonGia()),
                FormatUtil.formatMoney(thanhTien)
        });
    }

    private void xuLyThemThuoc() {
        String ten = txtTenThuoc.getText().trim();
        if (ten.isEmpty()) return;
        int sl = (int) spSoLuongThuoc.getValue();
        String dvt = txtDVTThuoc.getText().trim();
        String lieu = txtLieuDung.getText().trim();

        ChiTietDonThuoc ct = new ChiTietDonThuoc("", ten, dvt, sl, lieu);
        listThuoc.add(ct);

        modelThuoc.addRow(new Object[]{ten, dvt, sl, lieu});
    }

    private void xuLyLuuCaKham() {
        LichHen lh = (LichHen) cboBenhNhanCho.getSelectedItem();
        if (lh == null) {
            JOptionPane.showMessageDialog(this, "Không có bệnh nhân nào trong hàng đợi khám!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtChanDoan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập chẩn đoán của bác sĩ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Hoàn tất ca khám cho bệnh nhân " + lh.getTenBN() + "!\nHệ thống đã lưu hồ sơ và tự động xuất kho theo FEFO (RB12).", "Thành công", JOptionPane.INFORMATION_MESSAGE);

                listCTDT.clear();
                listThuoc.clear();
                modelDV.setRowCount(0);
                modelThuoc.setRowCount(0);
                txtTrieuChung.setText("");
                txtChanDoan.setText("");
                taiDanhSachHangDoi();
            } else {
                JOptionPane.showMessageDialog(this, "Lưu ca khám thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
