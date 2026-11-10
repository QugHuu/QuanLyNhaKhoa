package com.nhakhoa.gui;

import com.nhakhoa.bus.LichHenBUS;
import com.nhakhoa.dao.DichVuDAO;
import com.nhakhoa.dao.PhieuKhamDAO;
import com.nhakhoa.model.*;
import com.nhakhoa.util.FormatUtil;

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
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        JLabel lblTitle = new JLabel("KHÁM & ĐIỀU TRỊ NHA KHOA (PHÂN HỆ 4 - DÀNH CHO BÁC SĨ)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(25, 42, 86));

        // TOP: Chọn bệnh nhân và chẩn đoán
        JPanel pnlTop = new JPanel(new GridLayout(3, 2, 10, 8));
        pnlTop.setBackground(Color.WHITE);
        pnlTop.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(12, 15, 12, 15)
        ));

        cboBenhNhanCho = new JComboBox<>();
        JButton btnReloadQueue = new JButton("🔄 Tải hàng đợi đã đến");
        btnReloadQueue.addActionListener(e -> taiDanhSachHangDoi());

        JPanel pnlQueue = new JPanel(new BorderLayout(5, 0));
        pnlQueue.setOpaque(false);
        pnlQueue.add(cboBenhNhanCho, BorderLayout.CENTER);
        pnlQueue.add(btnReloadQueue, BorderLayout.EAST);

        txtTrieuChung = new JTextField();
        txtChanDoan = new JTextField();

        pnlTop.add(new JLabel("Bệnh nhân trong hàng đợi (Đã đến - RB08):"));
        pnlTop.add(pnlQueue);
        pnlTop.add(new JLabel("Triệu chứng lâm sàng:"));
        pnlTop.add(txtTrieuChung);
        pnlTop.add(new JLabel("Chẩn đoán bác sĩ:"));
        pnlTop.add(txtChanDoan);

        // CENTER: 2 tab / 2 cột: Cột 1 Chỉ định Dịch vụ Điều trị, Cột 2 Kê đơn thuốc
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlCenter.setOpaque(false);

        // CỘT 1: Dịch vụ điều trị
        JPanel pnlDV = new JPanel(new BorderLayout(8, 8));
        pnlDV.setBackground(Color.WHITE);
        pnlDV.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel pnlDVInput = new JPanel(new GridLayout(3, 2, 5, 5));
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

        JButton btnThemDV = new JButton("➕ Thêm dịch vụ");
        btnThemDV.setBackground(new Color(52, 152, 219));
        btnThemDV.setForeground(Color.BLACK);
        btnThemDV.addActionListener(e -> xuLyThemDV());

        JPanel pnlDVTop = new JPanel(new BorderLayout(5, 5));
        pnlDVTop.setOpaque(false);
        pnlDVTop.add(new JLabel("CHỈ ĐỊNH DỊCH VỤ NHA KHOA"), BorderLayout.NORTH);
        pnlDVTop.add(pnlDVInput, BorderLayout.CENTER);
        pnlDVTop.add(btnThemDV, BorderLayout.SOUTH);

        String[] headersDV = {"Tên Dịch Vụ", "Vị Trí Răng", "SL", "Đơn Giá", "Thành Tiền"};
        modelDV = new DefaultTableModel(headersDV, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDichVuChiDinh = new JTable(modelDV);
        tblDichVuChiDinh.setRowHeight(24);

        pnlDV.add(pnlDVTop, BorderLayout.NORTH);
        pnlDV.add(new JScrollPane(tblDichVuChiDinh), BorderLayout.CENTER);

        // CỘT 2: Kê đơn thuốc
        JPanel pnlThuoc = new JPanel(new BorderLayout(8, 8));
        pnlThuoc.setBackground(Color.WHITE);
        pnlThuoc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel pnlThuocInput = new JPanel(new GridLayout(4, 2, 5, 5));
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

        JButton btnThemThuoc = new JButton("➕ Thêm thuốc vào đơn");
        btnThemThuoc.setBackground(new Color(155, 89, 182));
        btnThemThuoc.setForeground(Color.BLACK);
        btnThemThuoc.addActionListener(e -> xuLyThemThuoc());

        JPanel pnlThuocTop = new JPanel(new BorderLayout(5, 5));
        pnlThuocTop.setOpaque(false);
        pnlThuocTop.add(new JLabel("KÊ ĐƠN THUỐC ĐIỀU TRỊ"), BorderLayout.NORTH);
        pnlThuocTop.add(pnlThuocInput, BorderLayout.CENTER);
        pnlThuocTop.add(btnThemThuoc, BorderLayout.SOUTH);

        String[] headersThuoc = {"Tên Thuốc", "ĐVT", "Số Lượng", "Liều Dùng"};
        modelThuoc = new DefaultTableModel(headersThuoc, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDonThuoc = new JTable(modelThuoc);
        tblDonThuoc.setRowHeight(24);

        pnlThuoc.add(pnlThuocTop, BorderLayout.NORTH);
        pnlThuoc.add(new JScrollPane(tblDonThuoc), BorderLayout.CENTER);

        pnlCenter.add(pnlDV);
        pnlCenter.add(pnlThuoc);

        // BOTTOM: Lời dặn & Lưu toàn bộ ca khám
        JPanel pnlBottom = new JPanel(new BorderLayout(10, 10));
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JPanel pnlLoiDan = new JPanel(new BorderLayout(5, 5));
        pnlLoiDan.setOpaque(false);
        pnlLoiDan.add(new JLabel("Lời dặn của bác sĩ:"), BorderLayout.WEST);
        txtLoiDan = new JTextField("Tái khám sau 1 tuần. Kiêng ăn nhai đồ quá cứng.");
        pnlLoiDan.add(txtLoiDan, BorderLayout.CENTER);

        JButton btnLuuCaKham = new JButton("💾 HOÀN THÀNH CA KHÁM & XUẤT KHO VẬT TƯ (FEFO)");
        btnLuuCaKham.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLuuCaKham.setBackground(new Color(46, 204, 113));
        btnLuuCaKham.setForeground(Color.BLACK);
        btnLuuCaKham.setPreferredSize(new Dimension(0, 45));
        btnLuuCaKham.addActionListener(e -> xuLyLuuCaKham());

        pnlBottom.add(pnlLoiDan, BorderLayout.CENTER);
        pnlBottom.add(btnLuuCaKham, BorderLayout.SOUTH);

        add(lblTitle, BorderLayout.NORTH);
        add(pnlTop, BorderLayout.BEFORE_FIRST_LINE);
        add(pnlCenter, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);

        taiDanhSachHangDoi();
        taiDanhSachDichVu();
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

                // Reset form
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
