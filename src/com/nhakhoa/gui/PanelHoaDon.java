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
import java.util.Date;
import java.util.List;

public class PanelHoaDon extends JPanel {
    private JTable tblHoaDon, tblCongNo;
    private DefaultTableModel modelHoaDon, modelCongNo;

    private JLabel lblPayPatient, lblPayInvoice, lblPayTotal, lblPayDiscount, lblPayPaid, lblPayRemaining;
    private JTextField txtPayAmount;
    private JComboBox<String> cboPayMethod;
    private JButton btnConfirmPay;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private BenhNhanBUS benhNhanBUS = new BenhNhanBUS();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private String currentSelectedMaHD = null;
    private String currentSelectedMaCN = null;
    private double currentRemaining = 0;

    public PanelHoaDon() {
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        setBackground(UITheme.BG_MAIN);

        // ==================== 1. PAGE HEADER ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTitle.setOpaque(false);
        JLabel lblHeading = new JLabel("Thanh toán & công nợ");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeading.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Lập hóa đơn tài chính, theo dõi thu tiền đợt và quản lý sổ nợ trả góp");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        pnlTitle.add(lblHeading);
        pnlTitle.add(lblSub);

        JPanel pnlHeaderActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlHeaderActions.setOpaque(false);

        JButton btnLapHDMoi = UITheme.createButton("+ Lập hóa đơn mới", UITheme.PRIMARY, Color.WHITE);
        btnLapHDMoi.addActionListener(e -> hienDialogLapHD());
        pnlHeaderActions.add(btnLapHDMoi);

        pnlHeader.add(pnlTitle, BorderLayout.WEST);
        pnlHeader.add(pnlHeaderActions, BorderLayout.EAST);

        // ==================== 2. 3 SUMMARY KPI CARDS (Theo Mockup 06) ====================
        JPanel pnlKPI = new JPanel(new GridLayout(1, 3, 14, 0));
        pnlKPI.setOpaque(false);

        pnlKPI.add(taoKPICard("Tổng thực thu", "18.500.000 đ", "Hôm nay"));
        pnlKPI.add(taoKPICard("Tổng công nợ còn lại", "32.800.000 đ", "Toàn phòng khám"));
        pnlKPI.add(taoKPICard("Hồ sơ nợ cần thu", "05 hồ sơ", "Đến kỳ hẹn trả"));

        JPanel pnlTopGroup = new JPanel(new BorderLayout(0, 14));
        pnlTopGroup.setOpaque(false);
        pnlTopGroup.add(pnlHeader, BorderLayout.NORTH);
        pnlTopGroup.add(pnlKPI, BorderLayout.CENTER);

        // ==================== 3. BODY (TABS LEFT + PAYMENT CARD RIGHT) ====================
        JPanel pnlBody = new JPanel(new BorderLayout(14, 0));
        pnlBody.setOpaque(false);

        // CỘT TRÁI: Tabs Hóa đơn & Sổ công nợ
        JPanel pnlLeftCard = UITheme.createCard();
        pnlLeftCard.setLayout(new BorderLayout());

        JTabbedPane tabBills = new JTabbedPane();

        // TAB 1: HÓA ĐƠN
        JPanel pnlTab1 = new JPanel(new BorderLayout());
        pnlTab1.setOpaque(false);
        String[] headersHD = {"Mã HĐ", "Bệnh Nhân", "Ngày Lập", "Tổng Tiền", "Giảm Giá", "Cần Trả", "Trạng Thái"};
        modelHoaDon = new DefaultTableModel(headersHD, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHoaDon = new JTable(modelHoaDon);
        UITheme.formatTable(tblHoaDon);
        tblHoaDon.getColumnModel().getColumn(6).setCellRenderer(new UITheme.StatusBadgeRenderer());

        tblHoaDon.getSelectionModel().addListSelectionListener(e -> {
            int row = tblHoaDon.getSelectedRow();
            if (row >= 0) {
                currentSelectedMaHD = modelHoaDon.getValueAt(row, 0).toString();
                currentSelectedMaCN = null;
                lblPayPatient.setText(modelHoaDon.getValueAt(row, 1).toString());
                lblPayInvoice.setText(currentSelectedMaHD + " · " + modelHoaDon.getValueAt(row, 2).toString());
                lblPayTotal.setText(modelHoaDon.getValueAt(row, 3).toString());
                lblPayDiscount.setText(modelHoaDon.getValueAt(row, 4).toString());
                lblPayRemaining.setText(modelHoaDon.getValueAt(row, 5).toString());
                txtPayAmount.setText("500000");
                btnConfirmPay.setEnabled(true);
            }
        });

        pnlTab1.add(new JScrollPane(tblHoaDon), BorderLayout.CENTER);

        // TAB 2: CÔNG NỢ
        JPanel pnlTab2 = new JPanel(new BorderLayout());
        pnlTab2.setOpaque(false);
        String[] headersCN = {"Mã Công Nợ", "Mã Hóa Đơn", "Bệnh Nhân", "Nợ Ban Đầu", "Dư Nợ Còn Lại", "Trạng Thái"};
        modelCongNo = new DefaultTableModel(headersCN, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCongNo = new JTable(modelCongNo);
        UITheme.formatTable(tblCongNo);
        tblCongNo.getColumnModel().getColumn(5).setCellRenderer(new UITheme.StatusBadgeRenderer());

        tblCongNo.getSelectionModel().addListSelectionListener(e -> {
            int row = tblCongNo.getSelectedRow();
            if (row >= 0) {
                currentSelectedMaCN = modelCongNo.getValueAt(row, 0).toString();
                currentSelectedMaHD = modelCongNo.getValueAt(row, 1).toString();
                lblPayPatient.setText(modelCongNo.getValueAt(row, 2).toString());
                lblPayInvoice.setText("Sổ nợ: " + currentSelectedMaCN + " (HĐ: " + currentSelectedMaHD + ")");
                lblPayTotal.setText(modelCongNo.getValueAt(row, 3).toString());
                lblPayDiscount.setText("0 đ");
                lblPayRemaining.setText(modelCongNo.getValueAt(row, 4).toString());
                txtPayAmount.setText("2000000");
                btnConfirmPay.setEnabled(true);
            }
        });

        pnlTab2.add(new JScrollPane(tblCongNo), BorderLayout.CENTER);

        tabBills.addTab("Danh sách hóa đơn", pnlTab1);
        tabBills.addTab("Sổ theo dõi công nợ trả góp (RB15)", pnlTab2);

        pnlLeftCard.add(tabBills, BorderLayout.CENTER);

        // CỘT PHẢI: Ghi nhận thanh toán Card (340px - Theo Mockup 06)
        JPanel pnlPayCard = UITheme.createCard();
        pnlPayCard.setLayout(new BorderLayout(0, 14));
        pnlPayCard.setPreferredSize(new Dimension(340, 0));

        JPanel pnlPayTop = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlPayTop.setOpaque(false);
        JLabel lblGhiNhan = new JLabel("Ghi nhận thanh toán");
        lblGhiNhan.setFont(UITheme.FONT_SUBTITLE);
        lblGhiNhan.setForeground(UITheme.TEXT_DARK);

        lblPayPatient = new JLabel("Chưa chọn hóa đơn/hồ sơ");
        lblPayPatient.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPayPatient.setForeground(UITheme.PRIMARY);

        lblPayInvoice = new JLabel("Chọn từ danh sách bên trái");
        lblPayInvoice.setFont(UITheme.FONT_TINY);
        lblPayInvoice.setForeground(UITheme.TEXT_MUTED);

        pnlPayTop.add(lblGhiNhan);
        pnlPayTop.add(lblPayPatient);

        // Chi tiết số tiền
        JPanel pnlCalc = new JPanel(new GridLayout(4, 2, 0, 8));
        pnlCalc.setBackground(new Color(248, 250, 252));
        pnlCalc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        lblPayTotal = new JLabel("0 đ", SwingConstants.RIGHT);
        lblPayDiscount = new JLabel("0 đ", SwingConstants.RIGHT);
        lblPayPaid = new JLabel("Đang cập nhật", SwingConstants.RIGHT);
        lblPayRemaining = new JLabel("0 đ", SwingConstants.RIGHT);
        lblPayRemaining.setFont(UITheme.FONT_BOLD);
        lblPayRemaining.setForeground(UITheme.PRIMARY);

        pnlCalc.add(new JLabel("Tổng chi phí:"));
        pnlCalc.add(lblPayTotal);
        pnlCalc.add(new JLabel("Tiền giảm giá:"));
        pnlCalc.add(lblPayDiscount);
        pnlCalc.add(new JLabel("Hình thức:"));
        pnlCalc.add(lblPayPaid);
        pnlCalc.add(new JLabel("Cần thu / Dư nợ:"));
        pnlCalc.add(lblPayRemaining);

        // Input thanh toán
        JPanel pnlInputBox = new JPanel(new GridLayout(2, 1, 0, 6));
        pnlInputBox.setOpaque(false);

        txtPayAmount = new JTextField("500000");
        txtPayAmount.setFont(new Font("Segoe UI", Font.BOLD, 15));
        txtPayAmount.setPreferredSize(new Dimension(0, 36));

        cboPayMethod = new JComboBox<>(new String[]{"CHUYEN_KHOAN", "TIEN_MAT"});

        JPanel pnlF1 = new JPanel(new BorderLayout(0, 2));
        pnlF1.setOpaque(false);
        pnlF1.add(new JLabel("Số tiền thu đợt này (VNĐ):"), BorderLayout.NORTH);
        pnlF1.add(txtPayAmount, BorderLayout.CENTER);

        JPanel pnlF2 = new JPanel(new BorderLayout(0, 2));
        pnlF2.setOpaque(false);
        pnlF2.add(new JLabel("Phương thức:"), BorderLayout.NORTH);
        pnlF2.add(cboPayMethod, BorderLayout.CENTER);

        pnlInputBox.add(pnlF1);
        pnlInputBox.add(pnlF2);

        btnConfirmPay = UITheme.createButton("Xác nhận thanh toán hóa đơn", UITheme.PRIMARY, Color.WHITE);
        btnConfirmPay.setPreferredSize(new Dimension(0, 42));
        btnConfirmPay.setEnabled(false);
        btnConfirmPay.addActionListener(e -> xuLyXacNhanThanhToan());

        JPanel pnlCenterPay = new JPanel(new BorderLayout(0, 10));
        pnlCenterPay.setOpaque(false);
        pnlCenterPay.add(pnlCalc, BorderLayout.NORTH);
        pnlCenterPay.add(pnlInputBox, BorderLayout.CENTER);

        pnlPayCard.add(pnlPayTop, BorderLayout.NORTH);
        pnlPayCard.add(pnlCenterPay, BorderLayout.CENTER);
        pnlPayCard.add(btnConfirmPay, BorderLayout.SOUTH);

        pnlBody.add(pnlLeftCard, BorderLayout.CENTER);
        pnlBody.add(pnlPayCard, BorderLayout.EAST);

        add(pnlTopGroup, BorderLayout.NORTH);
        add(pnlBody, BorderLayout.CENTER);

        taiDuLieu();
    }

    private JPanel taoKPICard(String title, String val, String sub) {
        JPanel c = new JPanel(new BorderLayout(0, 2));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));
        JLabel lt = new JLabel(title);
        lt.setFont(UITheme.FONT_SMALL);
        lt.setForeground(UITheme.TEXT_MUTED);

        JLabel lv = new JLabel(val);
        lv.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lv.setForeground(UITheme.TEXT_DARK);

        JLabel ls = new JLabel(sub);
        ls.setFont(UITheme.FONT_TINY);
        ls.setForeground(UITheme.TEXT_MUTED);

        c.add(lt, BorderLayout.NORTH);
        c.add(lv, BorderLayout.CENTER);
        c.add(ls, BorderLayout.SOUTH);
        return c;
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

    private void xuLyXacNhanThanhToan() {
        try {
            double soTien = Double.parseDouble(txtPayAmount.getText().trim());
            String pthuc = cboPayMethod.getSelectedItem().toString();

            if (currentSelectedMaCN != null) {
                // Thu nợ trả góp
                boolean ok = hoaDonDAO.traNo(currentSelectedMaCN, currentSelectedMaHD, soTien, pthuc);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Đã ghi nhận thanh toán trả góp thành công (RB15)!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    taiDuLieu();
                }
            } else if (currentSelectedMaHD != null) {
                // Thu hóa đơn trực tiếp
                JOptionPane.showMessageDialog(this, "Đã ghi nhận thanh toán cho hóa đơn " + currentSelectedMaHD + "!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienDialogLapHD() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lập hóa đơn thanh toán mới", true);
        dlg.setSize(420, 420);
        dlg.setLocationRelativeTo(this);

        JPanel pnl = new JPanel(new BorderLayout(0, 12));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblT = new JLabel("LẬP HÓA ĐƠN TỔNG HỢP");
        lblT.setFont(UITheme.FONT_SUBTITLE);
        lblT.setForeground(UITheme.PRIMARY);

        JPanel pnlForm = new JPanel(new GridLayout(5, 1, 0, 8));
        pnlForm.setOpaque(false);

        JComboBox<BenhNhan> cboBN = new JComboBox<>();
        for (BenhNhan bn : benhNhanBUS.getAll()) cboBN.addItem(bn);

        JTextField txtTong = new JTextField("1500000");
        JTextField txtGiam = new JTextField("0");
        JTextField txtTra = new JTextField("1500000");
        JComboBox<String> cboP = new JComboBox<>(new String[]{"CHUYEN_KHOAN", "TIEN_MAT"});

        pnlForm.add(taoField("Bệnh nhân thanh toán:", cboBN));
        pnlForm.add(taoField("Tổng chi phí dịch vụ (VNĐ):", txtTong));
        pnlForm.add(taoField("Tiền giảm giá / Ưu đãi:", txtGiam));
        pnlForm.add(taoField("Thanh toán đợt này:", txtTra));
        pnlForm.add(taoField("Phương thức:", cboP));

        JButton btnLuu = UITheme.createButton("Xác nhận lập hóa đơn", UITheme.PRIMARY, Color.WHITE);
        btnLuu.setPreferredSize(new Dimension(0, 40));
        btnLuu.addActionListener(e -> {
            try {
                BenhNhan bn = (BenhNhan) cboBN.getSelectedItem();
                if (bn == null) return;
                double tong = Double.parseDouble(txtTong.getText().trim());
                double giam = Double.parseDouble(txtGiam.getText().trim());
                double tra = Double.parseDouble(txtTra.getText().trim());

                boolean ok = hoaDonDAO.lapHoaDonVaThanhToan(bn.getMaBN(), tong, giam, tra, cboP.getSelectedItem().toString());
                if (ok) {
                    JOptionPane.showMessageDialog(dlg, "Lập hóa đơn thành công! Hệ thống tự động hạch toán công nợ nếu còn thiếu.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    dlg.dispose();
                    taiDuLieu();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnl.add(lblT, BorderLayout.NORTH);
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
