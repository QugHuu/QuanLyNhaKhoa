package com.nhakhoa.gui;

import com.nhakhoa.dao.KhoDAO;
import com.nhakhoa.model.LoVatTu;
import com.nhakhoa.model.VatTu;
import com.nhakhoa.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PanelKhoVatTu extends JPanel {
    private JTable tblLoVatTu, tblVatTuTongHop, tblCanhBao;
    private DefaultTableModel modelLo, modelTongHop, modelCanhBao;

    private JComboBox<VatTu> cboVatTu;
    private JTextField txtSoLo, txtHSD, txtSLTon;

    private KhoDAO khoDAO = new KhoDAO();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public PanelKhoVatTu() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(18, 24, 18, 24));
        setBackground(UITheme.BG_MAIN);

        JPanel pnlCard = UITheme.createCard();
        pnlCard.setLayout(new BorderLayout());

        JTabbedPane tabMain = new JTabbedPane();

        // TAB 1: Quản lý Lô theo FEFO
        JPanel pnlTab1 = new JPanel(new BorderLayout(0, 12));
        pnlTab1.setOpaque(false);

        // Form nhập lô mới
        JPanel pnlNhapLo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        pnlNhapLo.setBackground(new Color(241, 245, 249));
        pnlNhapLo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        cboVatTu = new JComboBox<>();
        cboVatTu.setPreferredSize(new Dimension(200, 30));
        txtSoLo = new JTextField(10);
        txtHSD = new JTextField(8);
        txtHSD.setText("31/12/2027");
        txtSLTon = new JTextField("50", 5);

        JButton btnNhapLo = UITheme.createButton("Nhập Lô Mới", UITheme.PRIMARY, Color.WHITE);
        btnNhapLo.addActionListener(e -> xuLyNhapLo());

        pnlNhapLo.add(new JLabel("Vật tư:"));
        pnlNhapLo.add(cboVatTu);
        pnlNhapLo.add(new JLabel("Số lô:"));
        pnlNhapLo.add(txtSoLo);
        pnlNhapLo.add(new JLabel("HSD (dd/MM/yyyy):"));
        pnlNhapLo.add(txtHSD);
        pnlNhapLo.add(new JLabel("Số lượng:"));
        pnlNhapLo.add(txtSLTon);
        pnlNhapLo.add(btnNhapLo);

        String[] headersLo = {"Mã Lô", "Tên Vật Tư", "ĐVT", "Số Lô", "Hạn Sử Dụng (FEFO)", "Số Lượng Tồn", "Tình Trạng Lô"};
        modelLo = new DefaultTableModel(headersLo, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLoVatTu = new JTable(modelLo);
        UITheme.formatTable(tblLoVatTu);
        tblLoVatTu.getColumnModel().getColumn(6).setCellRenderer(new UITheme.StatusBadgeRenderer());

        pnlTab1.add(pnlNhapLo, BorderLayout.NORTH);
        pnlTab1.add(new JScrollPane(tblLoVatTu), BorderLayout.CENTER);

        // TAB 2: Danh mục tổng hợp vật tư & định mức tồn
        JPanel pnlTab2 = new JPanel(new BorderLayout(0, 10));
        pnlTab2.setOpaque(false);

        String[] headersTH = {"Mã Vật Tư", "Tên Vật Tư Nha Khoa", "Đơn Vị Tính", "Định Mức Tồn Tối Thiểu", "Tổng Tồn Hiện Tại", "Đánh Giá Tiêu Chuẩn"};
        modelTongHop = new DefaultTableModel(headersTH, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblVatTuTongHop = new JTable(modelTongHop);
        UITheme.formatTable(tblVatTuTongHop);
        pnlTab2.add(new JScrollPane(tblVatTuTongHop), BorderLayout.CENTER);

        // TAB 3: Cảnh báo Hạn dùng & Tồn thấp (FR-58, FR-60)
        JPanel pnlTab3 = new JPanel(new BorderLayout(0, 10));
        pnlTab3.setOpaque(false);

        String[] headersCB = {"Loại Cảnh Báo", "Tên Mục / Mặt Hàng", "Chi Tiết Thông Số Cảnh Báo", "Mức Độ Cần Xử Lý"};
        modelCanhBao = new DefaultTableModel(headersCB, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCanhBao = new JTable(modelCanhBao);
        UITheme.formatTable(tblCanhBao);
        pnlTab3.add(new JScrollPane(tblCanhBao), BorderLayout.CENTER);

        tabMain.addTab("Quản Lý Lô Theo FEFO (HanSuDung ASC)", pnlTab1);
        tabMain.addTab("Tổng Hợp Tồn Kho & Định Mức", pnlTab2);
        tabMain.addTab("Bảng Cảnh Báo Cận Hạn & Dưới Định Mức", pnlTab3);

        pnlCard.add(tabMain, BorderLayout.CENTER);
        add(pnlCard, BorderLayout.CENTER);

        taiDanhSachVatTu();
        taiDuLieu();
    }

    private void taiDanhSachVatTu() {
        cboVatTu.removeAllItems();
        List<VatTu> list = khoDAO.getAllVatTu();
        for (VatTu vt : list) {
            cboVatTu.addItem(vt);
        }
    }

    public void taiDuLieu() {
        modelLo.setRowCount(0);
        List<LoVatTu> listLo = khoDAO.getAllLoVatTu();
        for (LoVatTu lo : listLo) {
            modelLo.addRow(new Object[]{
                    lo.getMaLo(),
                    lo.getTenVatTu(),
                    lo.getDonViTinh(),
                    lo.getSoLo(),
                    sdf.format(lo.getHanSuDung()),
                    lo.getSoLuongTon(),
                    lo.getTinhTrangLo()
            });
        }

        modelTongHop.setRowCount(0);
        List<VatTu> listVT = khoDAO.getAllVatTu();
        for (VatTu vt : listVT) {
            String danhGia = vt.isCanhBaoTonThap() ? "DƯỚI ĐỊNH MỨC TỐI THIỂU" : "Đạt chuẩn tồn";
            modelTongHop.addRow(new Object[]{
                    vt.getMaVatTu(),
                    vt.getTenVatTu(),
                    vt.getDonViTinh(),
                    vt.getMucTonToiThieu(),
                    vt.getTongTon(),
                    danhGia
            });
        }

        modelCanhBao.setRowCount(0);
        List<LoVatTu> listSapHetHan = khoDAO.getLoSapHetHan();
        for (LoVatTu lo : listSapHetHan) {
            modelCanhBao.addRow(new Object[]{
                    "CẬN HẠN DÙNG (≤ 30 ngày)",
                    lo.getTenVatTu() + " (Lô: " + lo.getSoLo() + ")",
                    "HSD: " + sdf.format(lo.getHanSuDung()) + " - Tồn: " + lo.getSoLuongTon() + " " + lo.getDonViTinh(),
                    "NGUY HIỂM (Ưu tiên xuất FEFO)"
            });
        }

        List<VatTu> listTonThap = khoDAO.getVatTuTonThap();
        for (VatTu vt : listTonThap) {
            modelCanhBao.addRow(new Object[]{
                    "TỒN KHO DƯỚI MỨC TỐI THIỂU",
                    vt.getTenVatTu(),
                    "Tồn: " + vt.getTongTon() + " / Định mức: " + vt.getMucTonToiThieu() + " " + vt.getDonViTinh(),
                    "CẦN NHẬP BỔ SUNG"
            });
        }
    }

    private void xuLyNhapLo() {
        VatTu vt = (VatTu) cboVatTu.getSelectedItem();
        if (vt == null) return;

        String soLo = txtSoLo.getText().trim();
        if (soLo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số lô hàng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date hsd = sdf.parse(txtHSD.getText().trim());
            int sl = Integer.parseInt(txtSLTon.getText().trim());

            LoVatTu lo = new LoVatTu(
                    khoDAO.taoMaLoMoi(),
                    vt.getMaVatTu(),
                    vt.getTenVatTu(),
                    vt.getDonViTinh(),
                    soLo,
                    hsd,
                    sl,
                    "KHA_DUNG"
            );

            boolean ok = khoDAO.themLoVatTu(lo);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Nhập lô vật tư thành công!\nHệ thống đã tự động sắp xếp theo FEFO (HanSuDung ASC).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
                txtSoLo.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Nhập lô vật tư thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
