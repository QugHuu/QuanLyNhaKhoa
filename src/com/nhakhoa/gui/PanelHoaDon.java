package com.nhakhoa.gui;

import com.nhakhoa.bus.BenhNhanBUS;
import com.nhakhoa.dao.HoaDonDAO;
import com.nhakhoa.model.BenhNhan;
import com.nhakhoa.model.HoSoCongNo;
import com.nhakhoa.model.HoaDon;
import com.nhakhoa.util.FormatUtil;
import com.nhakhoa.util.UITheme;

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
        setLayout(new BorderLayout(18, 0));
        setBorder(new EmptyBorder(18, 24, 18, 24));
        setBackground(UITheme.BG_MAIN);

        // LEFT: Card Lập hóa đơn
        JPanel pnlLeftCard = UITheme.createCard();
        pnlLeftCard.setLayout(new BorderLayout(0, 14));
        pnlLeftCard.setPreferredSize(new Dimension(380, 0));

        JLabel lblFormTitle = new JLabel("LẬP HÓA ĐƠN & THU TIỀN");
        lblFormTitle.setFont(UITheme.FONT_SUBTITLE);
        lblFormTitle.setForeground(UITheme.TEXT_DARK);

        JPanel pnlForm = new JPanel(new GridLayout(5, 1, 0, 8));
        pnlForm.setOpaque(false);

        cboBenhNhan = new JComboBox<>();
        txtTongTien = new JTextField("600000");
        txtGiamGia = new JTextField("0");
        txtThanhToanNgay = new JTextField("600000");
        cboPhuongThuc = new JComboBox<>(new String[]{"TIEN_MAT", "CHUYEN_KHOAN"});

        pnlForm.add(taoFieldInput("Bệnh nhân thanh toán:", cboBenhNhan));
        pnlForm.add(taoFieldInput("Tổng tiền dịch vụ (VNĐ):", txtTongTien));
        pnlForm.add(taoFieldInput("Tiền giảm giá / Khuyến mãi:", txtGiamGia));
        pnlForm.add(taoFieldInput("Khách thanh toán đợt này:", txtThanhToanNgay));
        pnlForm.add(taoFieldInput("Hình thức thanh toán:", cboPhuongThuc));

        JButton btnLapHD = UITheme.createButton("💳  LẬP HÓA ĐƠN & THU TIỀN", UITheme.PRIMARY, Color.WHITE);
        btnLapHD.setPreferredSize(new Dimension(0, 42));
        btnLapHD.addActionListener(e -> xuLyLapHoaDon());

        JLabel lblNote = new JLabel("<html><i style='color:#64748b;'>* Nếu bệnh nhân trả thiếu, phần còn lại sẽ tự động ghi vào Sổ Công Nợ Trả Góp (RB15).</i></html>");

        JPanel pnlLeftBottom = new JPanel(new BorderLayout(0, 8));
        pnlLeftBottom.setOpaque(false);
        pnlLeftBottom.add(btnLapHD, BorderLayout.NORTH);
        pnlLeftBottom.add(lblNote, BorderLayout.CENTER);

        pnlLeftCard.add(lblFormTitle, BorderLayout.NORTH);
        pnlLeftCard.add(pnlForm, BorderLayout.CENTER);
        pnlLeftCard.add(pnlLeftBottom, BorderLayout.SOUTH);

        // RIGHT: Card Tabs (Hóa đơn & Sổ nợ)
        JPanel pnlRightCard = UITheme.createCard();
        pnlRightCard.setLayout(new BorderLayout());

        JTabbedPane tabRight = new JTabbedPane();

        // TAB 1: HÓA ĐƠN
        JPanel pnlTabHD = new JPanel(new BorderLayout(0, 10));
        pnlTabHD.setOpaque(false);
        String[] headersHD = {"Mã HĐ", "Bệnh Nhân", "Ngày Lập", "Tổng Tiền", "Giảm Giá", "Cần Trả", "Trạng Thái"};
        modelHoaDon = new DefaultTableModel(headersHD, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHoaDon = new JTable(modelHoaDon);
        UITheme.formatTable(tblHoaDon);
        tblHoaDon.getColumnModel().getColumn(6).setCellRenderer(new UITheme.StatusBadgeRenderer());
        pnlTabHD.add(new JScrollPane(tblHoaDon), BorderLayout.CENTER);

        // TAB 2: CÔNG NỢ
        JPanel pnlTabCN = new JPanel(new BorderLayout(0, 10));
        pnlTabCN.setOpaque(false);

        String[] headersCN = {"Mã Công Nợ", "Mã Hóa Đơn", "Bệnh Nhân", "Tổng Nợ Ban Đầu", "Dư Nợ Còn Lại", "Trạng Thái"};
        modelCongNo = new DefaultTableModel(headersCN, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCongNo = new JTable(modelCongNo);
        UITheme.formatTable(tblCongNo);
        tblCongNo.getColumnModel().getColumn(5).setCellRenderer(new UITheme.StatusBadgeRenderer());

        JButton btnThuNo = UITheme.createButton("💵  Thu tiền đợt tiếp theo cho hồ sơ nợ đã chọn", UITheme.INFO, Color.WHITE);
        btnThuNo.setPreferredSize(new Dimension(0, 38));
        btnThuNo.addActionListener(e -> xuLyThuNo());

        pnlTabCN.add(new JScrollPane(tblCongNo), BorderLayout.CENTER);
        pnlTabCN.add(btnThuNo, BorderLayout.SOUTH);

        tabRight.addTab("📋  Danh Sách Hóa Đơn", pnlTabHD);
        tabRight.addTab("📖  Sổ Theo Dõi Công Nợ Trả Góp (RB15)", pnlTabCN);

        pnlRightCard.add(tabRight, BorderLayout.CENTER);

        add(pnlLeftCard, BorderLayout.WEST);
        add(pnlRightCard, BorderLayout.CENTER);

        taiBenhNhan();
        taiDuLieu();
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

    private void taiBenhNhan() {
        cboBenhNhan.removeAllItems();
        List<BenhNhan> list = benhNhanBUS.getAll();
        for (BenhNhan bn : list) {
            cboBenhNhan.addItem(bn);
        }
    }

    public void taiDuLieu() {
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
