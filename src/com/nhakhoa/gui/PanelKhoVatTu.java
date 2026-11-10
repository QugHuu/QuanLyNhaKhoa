package com.nhakhoa.gui;

import com.nhakhoa.dao.KhoDAO;
import com.nhakhoa.model.LoVatTu;
import com.nhakhoa.model.VatTu;
import com.nhakhoa.util.FormatUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
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
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        JLabel lblTitle = new JLabel("QUẢN LÝ KHO & THEO DÕI VẬT TƯ THEO NGUYÊN TẮC FEFO (PHÂN HỆ 6)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(25, 42, 86));

        JTabbedPane tabMain = new JTabbedPane();

        // TAB 1: Quản lý Lô theo FEFO
        JPanel pnlTab1 = new JPanel(new BorderLayout(10, 10));
        pnlTab1.setBackground(Color.WHITE);
        pnlTab1.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Form thêm lô mới
        JPanel pnlNhapLo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlNhapLo.setOpaque(false);
        cboVatTu = new JComboBox<>();
        cboVatTu.setPreferredSize(new Dimension(200, 28));
        txtSoLo = new JTextField(10);
        txtHSD = new JTextField(8); // dd/MM/yyyy
        txtHSD.setText("31/12/2027");
        txtSLTon = new JTextField("50", 5);

        JButton btnNhapLo = new JButton("📥 Nhập Lô Mới");
        btnNhapLo.setBackground(new Color(46, 204, 113));
        btnNhapLo.setForeground(Color.BLACK);
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
        tblLoVatTu.setRowHeight(25);

        // Đánh dấu màu đỏ cho lô hết hạn / màu cam cho lô sắp hết hạn
        tblLoVatTu.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String tinhTrang = table.getValueAt(row, 6).toString();
                    if ("HET_HANG".equalsIgnoreCase(tinhTrang)) {
                        c.setForeground(Color.GRAY);
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        pnlTab1.add(pnlNhapLo, BorderLayout.NORTH);
        pnlTab1.add(new JScrollPane(tblLoVatTu), BorderLayout.CENTER);

        // TAB 2: Danh mục tổng hợp vật tư & định mức tồn
        JPanel pnlTab2 = new JPanel(new BorderLayout(10, 10));
        pnlTab2.setBackground(Color.WHITE);
        pnlTab2.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] headersTH = {"Mã Vật Tư", "Tên Vật Tư", "Đơn Vị Tính", "Định Mức Tồn Tối Thiểu", "Tổng Tồn Hiện Tại", "Đánh Giá"};
        modelTongHop = new DefaultTableModel(headersTH, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblVatTuTongHop = new JTable(modelTongHop);
        tblVatTuTongHop.setRowHeight(25);
        pnlTab2.add(new JScrollPane(tblVatTuTongHop), BorderLayout.CENTER);

        // TAB 3: Cảnh báo Hạn dùng & Tồn thấp (FR-58, FR-60)
        JPanel pnlTab3 = new JPanel(new BorderLayout(10, 10));
        pnlTab3.setBackground(Color.WHITE);
        pnlTab3.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] headersCB = {"Loại Cảnh Báo", "Tên Mục", "Chi Tiết Cảnh Báo", "Mức Độ"};
        modelCanhBao = new DefaultTableModel(headersCB, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCanhBao = new JTable(modelCanhBao);
        tblCanhBao.setRowHeight(26);
        pnlTab3.add(new JScrollPane(tblCanhBao), BorderLayout.CENTER);

        tabMain.addTab("📦 Quản Lý Lô Theo FEFO (HanSuDung ASC)", pnlTab1);
        tabMain.addTab("📊 Tổng Hợp Tồn Kho & Định Mức", pnlTab2);
        tabMain.addTab("⚠️ Bảng Cảnh Báo Cận Hạn & Dưới Định Mức", pnlTab3);

        add(lblTitle, BorderLayout.NORTH);
        add(tabMain, BorderLayout.CENTER);

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
        // 1. Tải Lô FEFO
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

        // 2. Tải tổng hợp
        modelTongHop.setRowCount(0);
        List<VatTu> listVT = khoDAO.getAllVatTu();
        for (VatTu vt : listVT) {
            String danhGia = vt.isCanhBaoTonThap() ? "⚠️ DƯỚI ĐỊNH MỨC TỐI THIỂU" : "✅ Đạt chuẩn";
            modelTongHop.addRow(new Object[]{
                    vt.getMaVatTu(),
                    vt.getTenVatTu(),
                    vt.getDonViTinh(),
                    vt.getMucTonToiThieu(),
                    vt.getTongTon(),
                    danhGia
            });
        }

        // 3. Tải cảnh báo
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
