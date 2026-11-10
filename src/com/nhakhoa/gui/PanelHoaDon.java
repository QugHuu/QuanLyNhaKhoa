package com.nhakhoa.gui;

import com.nhakhoa.bus.BenhNhanBUS;
import com.nhakhoa.dao.HoaDonDAO;
import com.nhakhoa.model.BenhNhan;
import com.nhakhoa.model.HoSoCongNo;
import com.nhakhoa.model.HoaDon;
import com.nhakhoa.util.FormatUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class PanelHoaDon extends JPanel {
    private JComboBox<BenhNhan> cboBenhNhan;
    private JTextField txtTongTien, txtGiamGia, txtThanhToanNgay;
    private JComboBox<String> cboPhuongThuc;
    private JTable tblHoaDon, tblCongNo;
    private DefaultTableModel modelHoaDon, modelCongNo;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private BenhNhanBUS benhNhanBUS = new BenhNhanBUS();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public PanelHoaDon() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        JLabel lblTitle = new JLabel("THANH TOÁN & QUẢN LÝ CÔNG NỢ TRẢ GÓP (PHÂN HỆ 3)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(25, 42, 86));

        // LEFT: Lập hóa đơn mới
        JPanel pnlLeft = new JPanel(new BorderLayout(10, 10));
        pnlLeft.setPreferredSize(new Dimension(380, 0));
        pnlLeft.setBackground(Color.WHITE);
        pnlLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 8, 12));
        pnlForm.setOpaque(false);

        cboBenhNhan = new JComboBox<>();
        txtTongTien = new JTextField("600000");
        txtGiamGia = new JTextField("0");
        txtThanhToanNgay = new JTextField("600000");
        cboPhuongThuc = new JComboBox<>(new String[]{"TIEN_MAT", "CHUYEN_KHOAN"});

        pnlForm.add(new JLabel("Bệnh nhân:"));
        pnlForm.add(cboBenhNhan);
        pnlForm.add(new JLabel("Tổng tiền dịch vụ:"));
        pnlForm.add(txtTongTien);
        pnlForm.add(new JLabel("Tiền giảm giá:"));
        pnlForm.add(txtGiamGia);
        pnlForm.add(new JLabel("Khách thanh toán ngay:"));
        pnlForm.add(txtThanhToanNgay);
        pnlForm.add(new JLabel("Hình thức thanh toán:"));
        pnlForm.add(cboPhuongThuc);

        JButton btnLapHD = new JButton("💳 LẬP HÓA ĐƠN & THU TIỀN");
        btnLapHD.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLapHD.setBackground(new Color(46, 204, 113));
        btnLapHD.setForeground(Color.BLACK);
        btnLapHD.setPreferredSize(new Dimension(0, 42));
        btnLapHD.addActionListener(e -> xuLyLapHoaDon());

        JLabel lblNote = new JLabel("<html><i>* Lưu ý: Nếu khách thanh toán chưa đủ, hệ thống tự động ghi nhận vào Sổ Công Nợ (RB15).</i></html>");
        lblNote.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNote.setForeground(new Color(120, 130, 140));

        JPanel pnlLeftBottom = new JPanel(new BorderLayout(0, 8));
        pnlLeftBottom.setOpaque(false);
        pnlLeftBottom.add(btnLapHD, BorderLayout.CENTER);
        pnlLeftBottom.add(lblNote, BorderLayout.SOUTH);

        pnlLeft.add(new JLabel("TẠO HÓA ĐƠN TÀI CHÍNH", JLabel.CENTER), BorderLayout.NORTH);
        pnlLeft.add(pnlForm, BorderLayout.CENTER);
        pnlLeft.add(pnlLeftBottom, BorderLayout.SOUTH);

        // RIGHT: Gồm 2 tab: Tab 1 Danh sách Hóa đơn, Tab 2 Sổ công nợ
        JTabbedPane tabRight = new JTabbedPane();

        // TAB 1: HÓA ĐƠN
        JPanel pnlTabHD = new JPanel(new BorderLayout(5, 5));
        pnlTabHD.setBackground(Color.WHITE);
        String[] headersHD = {"Mã HĐ", "Bệnh Nhân", "Ngày Lập", "Tổng Tiền", "Giảm Giá", "Phải Trả", "Trạng Thái"};
        modelHoaDon = new DefaultTableModel(headersHD, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHoaDon = new JTable(modelHoaDon);
        tblHoaDon.setRowHeight(25);
        pnlTabHD.add(new JScrollPane(tblHoaDon), BorderLayout.CENTER);

        // TAB 2: CÔNG NỢ
        JPanel pnlTabCN = new JPanel(new BorderLayout(8, 8));
        pnlTabCN.setBackground(Color.WHITE);
        pnlTabCN.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] headersCN = {"Mã Công Nợ", "Mã Hóa Đơn", "Bệnh Nhân", "Nợ Ban Đầu", "Dư Nợ Còn Lại", "Trạng Thái"};
        modelCongNo = new DefaultTableModel(headersCN, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCongNo = new JTable(modelCongNo);
        tblCongNo.setRowHeight(25);

        JButton btnThuNo = new JButton("💵 Thu nợ tiếp theo cho hồ sơ được chọn");
        btnThuNo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnThuNo.setBackground(new Color(52, 152, 219));
        btnThuNo.setForeground(Color.BLACK);
        btnThuNo.addActionListener(e -> xuLyThuNo());

        pnlTabCN.add(new JScrollPane(tblCongNo), BorderLayout.CENTER);
        pnlTabCN.add(btnThuNo, BorderLayout.SOUTH);

        tabRight.addTab("📋 Danh Sách Hóa Đơn", pnlTabHD);
        tabRight.addTab("📖 Sổ Theo Dõi Công Nợ Trả Góp (RB15)", pnlTabCN);

        add(lblTitle, BorderLayout.NORTH);
        add(pnlLeft, BorderLayout.WEST);
        add(tabRight, BorderLayout.CENTER);

        taiBenhNhan();
        taiDuLieu();
    }

    private void taiBenhNhan() {
        cboBenhNhan.removeAllItems();
        List<BenhNhan> list = benhNhanBUS.getAll();
        for (BenhNhan bn : list) {
            cboBenhNhan.addItem(bn);
        }
    }

    public void taiDuLieu() {
        // Tải hóa đơn
        modelHoaDon.setRowCount(0);
        List<HoaDon> listHD = hoaDonDAO.getAll();
        for (HoaDon hd : listHD) {
            modelHoaDon.addRow(new Object[]{
                    hd.getMaHoaDon(),
                    hd.getTenBN(),
                    sdf.format(hd.getNgayLap()),
                    FormatUtil.formatMoney(hd.getTongTienDichVu()),
                    FormatUtil.formatMoney(hd.getTienGiamGia()),
                    FormatUtil.formatMoney(hd.getTongCanThanhToan()),
                    hd.getTinhTrang()
            });
        }

        // Tải công nợ
        modelCongNo.setRowCount(0);
        List<HoSoCongNo> listCN = hoaDonDAO.getAllCongNo();
        for (HoSoCongNo cn : listCN) {
            modelCongNo.addRow(new Object[]{
                    cn.getMaCongNo(),
                    cn.getMaHoaDon(),
                    cn.getTenBN(),
                    FormatUtil.formatMoney(cn.getTongNoPhatSinh()),
                    FormatUtil.formatMoney(cn.getSoDuNoConLai()),
                    cn.getTinhTrang()
            });
        }
    }

    private void xuLyLapHoaDon() {
        BenhNhan bn = (BenhNhan) cboBenhNhan.getSelectedItem();
        if (bn == null) return;

        try {
            double tongTien = Double.parseDouble(txtTongTien.getText().trim());
            double giamGia = Double.parseDouble(txtGiamGia.getText().trim());
            double tienTra = Double.parseDouble(txtThanhToanNgay.getText().trim());
            String phuongThuc = cboPhuongThuc.getSelectedItem().toString();

            boolean ok = hoaDonDAO.lapHoaDonVaThanhToan(bn.getMaBN(), tongTien, giamGia, tienTra, phuongThuc);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Lập hóa đơn và xử lý thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Lập hóa đơn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyThuNo() {
        int row = tblCongNo.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng công nợ cần thu tiền!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maCN = modelCongNo.getValueAt(row, 0).toString();
        String maHD = modelCongNo.getValueAt(row, 1).toString();
        String tinhTrang = modelCongNo.getValueAt(row, 5).toString();

        if ("DA_HOAN_TAT".equalsIgnoreCase(tinhTrang)) {
            JOptionPane.showMessageDialog(this, "Hồ sơ công nợ này đã hoàn tất thanh toán!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Nhập số tiền bệnh nhân thanh toán đợt này (VNĐ):", "Thu nợ trả góp", JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                double soTienTra = Double.parseDouble(input.trim());
                if (soTienTra <= 0) {
                    JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean ok = hoaDonDAO.traNo(maCN, maHD, soTienTra, "TIEN_MAT");
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Ghi nhận thanh toán trả góp thành công (RB15)!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    taiDuLieu();
                } else {
                    JOptionPane.showMessageDialog(this, "Thu tiền nợ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
