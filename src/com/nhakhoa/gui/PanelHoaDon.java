package com.nhakhoa.gui;

import com.nhakhoa.bus.BenhNhanBUS;
import com.nhakhoa.dao.HoaDonDAO;
import com.nhakhoa.dao.KeHoachNiengRangDAO;
import com.nhakhoa.model.BenhNhan;
import com.nhakhoa.model.HoSoCongNo;
import com.nhakhoa.model.HoaDon;
import com.nhakhoa.model.KeHoachNiengRang;
import com.nhakhoa.model.TaiKhoan;
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
    private JTable tblHoaDon, tblCongNo, tblNiengRang;
    private DefaultTableModel modelHoaDon, modelCongNo, modelNiengRang;

    private JLabel lblPayPatient, lblPayInvoice, lblPayTotal, lblPayDiscount, lblPayPaid, lblPayRemaining;
    private JTextField txtPayAmount;
    private JComboBox<String> cboPayMethod;
    private JButton btnConfirmPay;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private BenhNhanBUS benhNhanBUS = new BenhNhanBUS();
    private KeHoachNiengRangDAO keHoachDAO = new KeHoachNiengRangDAO();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private String currentSelectedMaHD = null;
    private String currentSelectedMaCN = null;
    private double currentRemaining = 0;
    private TaiKhoan currentUser;

    public PanelHoaDon() {
        this(null);
    }

    public PanelHoaDon(TaiKhoan user) {
        this.currentUser = user;
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

        // TAB 3: KẾ HOẠCH NIỀNG RĂNG (LUẬT 4)
        JPanel pnlTab3 = new JPanel(new BorderLayout(0, 10));
        pnlTab3.setOpaque(false);

        String[] headersNR = {"Mã KH", "Bệnh Nhân", "Mã HĐ", "Dư Nợ Còn Lại", "Lộ Trình Giai Đoạn", "Lý Do Tạm Hoãn/Hủy", "Trạng Thái"};
        modelNiengRang = new DefaultTableModel(headersNR, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNiengRang = new JTable(modelNiengRang);
        UITheme.formatTable(tblNiengRang);
        tblNiengRang.getColumnModel().getColumn(6).setCellRenderer(new UITheme.StatusBadgeRenderer());

        JPanel pnlNRAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pnlNRAction.setOpaque(false);

        JButton btnTamHoanNR = UITheme.createButton("Tạm hoãn kế hoạch (Bảo lưu công nợ)", Color.WHITE, UITheme.TEXT_DARK);
        btnTamHoanNR.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnTamHoanNR.addActionListener(e -> xuLyTamHoanNiengRang());

        JButton btnHuyNR = UITheme.createButton("Hủy kế hoạch (Quản lý phê duyệt)", UITheme.DANGER_BG, UITheme.DANGER);
        btnHuyNR.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.DANGER_BG, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnHuyNR.addActionListener(e -> xuLyHuyNiengRang());

        pnlNRAction.add(btnTamHoanNR);
        pnlNRAction.add(btnHuyNR);

        pnlTab3.add(new JScrollPane(tblNiengRang), BorderLayout.CENTER);
        pnlTab3.add(pnlNRAction, BorderLayout.SOUTH);

        tabBills.addTab("Danh sách hóa đơn", pnlTab1);
        tabBills.addTab("Sổ theo dõi công nợ trả góp", pnlTab2);
        tabBills.addTab("Kế hoạch niềng răng & trả góp", pnlTab3);

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

        JPanel pnlActionButtons = new JPanel(new GridLayout(2, 1, 0, 6));
        pnlActionButtons.setOpaque(false);

        btnConfirmPay = UITheme.createButton("Xác nhận thanh toán", UITheme.PRIMARY, Color.WHITE);
        btnConfirmPay.setPreferredSize(new Dimension(0, 38));
        btnConfirmPay.setEnabled(false);
        btnConfirmPay.addActionListener(e -> xuLyXacNhanThanhToan());

        JButton btnInHD = UITheme.createButton("In hóa đơn / Phiếu thu", Color.WHITE, UITheme.TEXT_DARK);
        btnInHD.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        btnInHD.addActionListener(e -> hienDialogInHoaDon());

        // FR-24: Nút hủy hóa đơn
        JButton btnHuyHD = UITheme.createButton("Huy HĐ", UITheme.DANGER_BG, UITheme.DANGER);
        btnHuyHD.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.DANGER_BG, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        btnHuyHD.addActionListener(e -> xuLyHuyHoaDon());

        pnlActionButtons.add(btnConfirmPay);
        pnlActionButtons.add(btnInHD);
        pnlActionButtons.add(btnHuyHD);

        JPanel pnlCenterPay = new JPanel(new BorderLayout(0, 10));
        pnlCenterPay.setOpaque(false);
        pnlCenterPay.add(pnlCalc, BorderLayout.NORTH);
        pnlCenterPay.add(pnlInputBox, BorderLayout.CENTER);

        pnlPayCard.add(pnlPayTop, BorderLayout.NORTH);
        pnlPayCard.add(pnlCenterPay, BorderLayout.CENTER);
        pnlPayCard.add(pnlActionButtons, BorderLayout.SOUTH);

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

        modelNiengRang.setRowCount(0);
        List<KeHoachNiengRang> listKH = keHoachDAO.getAll();
        for (KeHoachNiengRang kh : listKH) {
            modelNiengRang.addRow(new Object[]{
                    kh.getMaKeHoach(),
                    kh.getTenBN() + " (" + kh.getMaBN() + ")",
                    kh.getMaHoaDon(),
                    FormatUtil.formatMoney(kh.getSoDuNo()),
                    kh.getLoTrinhGiaiDoan(),
                    kh.getLyDoTamHoanHuy() != null ? kh.getLyDoTamHoanHuy() : "---",
                    kh.getTinhTrang()
            });
        }
    }

    private void xuLyTamHoanNiengRang() {
        int row = tblNiengRang.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một kế hoạch niềng răng trong bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maKH = modelNiengRang.getValueAt(row, 0).toString();
        String tenBN = modelNiengRang.getValueAt(row, 1).toString();

        String lyDo = JOptionPane.showInputDialog(this, "Nhập lý do tạm hoãn kế hoạch niềng răng của " + tenBN + ":", "Tạm hoãn kế hoạch", JOptionPane.QUESTION_MESSAGE);
        if (lyDo == null || lyDo.trim().isEmpty()) return;

        boolean ok = keHoachDAO.tamHoanKeHoach(maKH, lyDo.trim());
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "ĐÃ TẠM HOÃN KẾ HOẠCH THÀNH CÔNG!\n\n"
                    + "• Kế hoạch: " + maKH + " (" + tenBN + ")\n"
                    + "• Trạng thái mới: Tạm hoãn\n"
                    + "• Lịch hẹn tái khám đã được dừng.\n"
                    + "• Kế hoạch và công nợ được bảo lưu nguyên vẹn.",
                    "Tạm hoãn thành công", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể cập nhật tạm hoãn! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHuyNiengRang() {
        int row = tblNiengRang.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một kế hoạch niềng răng trong bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maKH = modelNiengRang.getValueAt(row, 0).toString();
        String tenBN = modelNiengRang.getValueAt(row, 1).toString();
        String duNoStr = modelNiengRang.getValueAt(row, 3).toString();

        // 1. Nhập lý do hủy
        String lyDo = JOptionPane.showInputDialog(this, "Nhập lý do chấm dứt / hủy kế hoạch niềng răng của " + tenBN + ":", "Hủy kế hoạch niềng răng", JOptionPane.QUESTION_MESSAGE);
        if (lyDo == null || lyDo.trim().isEmpty()) return;

        // 2. Nhập số tiền đề xuất giảm trừ công nợ
        String soTienStr = JOptionPane.showInputDialog(this, "Nhập số tiền đề xuất giảm trừ/miễn giảm công nợ cho bệnh nhân (VNĐ):\n(Dư nợ hiện tại: " + duNoStr + ")", "0");
        if (soTienStr == null) return;
        double soTienGiamNo = 0;
        try {
            soTienGiamNo = Double.parseDouble(soTienStr.replaceAll("[^0-9]", ""));
        } catch (Exception ex) {
            soTienGiamNo = 0;
        }

        // 3. Quản lý phê duyệt
        boolean daPheDuyet = false;
        String nguoiDuyet = "admin";

        if (currentUser != null && currentUser.isQuanLy()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xác nhận phê duyệt hủy kế hoạch " + maKH + " và cập nhật điều chỉnh công nợ?\n"
                    + "• Bệnh nhân: " + tenBN + "\n"
                    + "• Lý do hủy: " + lyDo + "\n"
                    + "• Số tiền giảm trừ công nợ: " + FormatUtil.formatMoney(soTienGiamNo),
                    "Quản lý phê duyệt", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                daPheDuyet = true;
                nguoiDuyet = currentUser.getTenDangNhap();
            } else {
                return;
            }
        } else {
            JPanel pnlAuth = new JPanel(new GridLayout(2, 2, 8, 8));
            JTextField txtAdminUser = new JTextField("admin");
            JPasswordField txtAdminPass = new JPasswordField();
            pnlAuth.add(new JLabel("Tài khoản Quản lý:"));
            pnlAuth.add(txtAdminUser);
            pnlAuth.add(new JLabel("Mật khẩu Quản lý:"));
            pnlAuth.add(txtAdminPass);

            int authRes = JOptionPane.showConfirmDialog(this, pnlAuth,
                    "YÊU CẦU QUẢN LÝ PHÊ DUYỆT - Vui lòng xác thực",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (authRes == JOptionPane.OK_OPTION) {
                String u = txtAdminUser.getText().trim();
                String p = new String(txtAdminPass.getPassword()).trim();
                com.nhakhoa.dao.TaiKhoanDAO tkDAO = new com.nhakhoa.dao.TaiKhoanDAO();
                TaiKhoan ql = tkDAO.dangNhap(u, p);
                if (ql != null && ql.isQuanLy() && !"TAM_KHOA".equalsIgnoreCase(ql.getTinhTrang())) {
                    daPheDuyet = true;
                    nguoiDuyet = ql.getTenDangNhap();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "XÁC THỰC THẤT BẠI!\nChỉ tài khoản Quản lý mới có quyền phê duyệt hủy kế hoạch và điều chỉnh công nợ.",
                            "Từ chối phê duyệt", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Yêu cầu hủy kế hoạch chưa được Quản lý phê duyệt. Số liệu công nợ được giữ nguyên.",
                        "Chưa phê duyệt", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (daPheDuyet) {
            boolean ok = keHoachDAO.huyKeHoachVaDieuChinhCongNo(maKH, lyDo.trim(), soTienGiamNo, true, nguoiDuyet);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "ĐÃ PHÊ DUYỆT VÀ HỦY KẾ HOẠCH THÀNH CÔNG!\n\n"
                        + "• Kế hoạch: " + maKH + " (" + tenBN + ")\n"
                        + "• Trạng thái mới: Đã hủy\n"
                        + "• Người phê duyệt: " + nguoiDuyet + "\n"
                        + "• Công nợ đã điều chỉnh: Giảm trừ " + FormatUtil.formatMoney(soTienGiamNo) + ".",
                        "Phê duyệt thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyXacNhanThanhToan() {
        if (currentUser != null && !currentUser.isLetan() && !currentUser.isQuanLy()) {
            JOptionPane.showMessageDialog(this,
                    "Chức năng thu ngân và thanh toán viện phí chỉ dành cho Lễ tân hoặc Quản lý!",
                    "Thông báo phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double soTien = Double.parseDouble(txtPayAmount.getText().trim());
            String pthuc = cboPayMethod.getSelectedItem().toString();

            if (currentSelectedMaCN != null) {
                // Thu nợ trả góp
                boolean ok = hoaDonDAO.traNo(currentSelectedMaCN, currentSelectedMaHD, soTien, pthuc);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Đã ghi nhận thanh toán trả góp thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
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

    private void hienDialogInHoaDon() {
        if (currentSelectedMaHD == null && currentSelectedMaCN == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hóa đơn hoặc hồ sơ công nợ cần in phiếu thu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Phiếu thu / Hóa đơn tài chính", true);
        dlg.setSize(480, 560);
        dlg.setLocationRelativeTo(this);

        JPanel pnl = new JPanel(new BorderLayout(0, 16));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header Clinic
        JPanel pnlClinic = new JPanel(new GridLayout(3, 1, 0, 2));
        pnlClinic.setOpaque(false);
        JLabel lblCName = new JLabel("PHÒNG KHÁM NHA KHOA SÀI GÒN DENTAL", JLabel.CENTER);
        lblCName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCName.setForeground(UITheme.PRIMARY);

        JLabel lblCAdd = new JLabel("123 Nguyễn Thị Minh Khai, Quận 3, TP. Hồ Chí Minh", JLabel.CENTER);
        lblCAdd.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCAdd.setForeground(UITheme.TEXT_MUTED);

        JLabel lblCHotline = new JLabel("Hotline: 1900 6868 · Website: saigondental.vn", JLabel.CENTER);
        lblCHotline.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCHotline.setForeground(UITheme.TEXT_MUTED);

        pnlClinic.add(lblCName);
        pnlClinic.add(lblCAdd);
        pnlClinic.add(lblCHotline);

        // Divider
        JSeparator sep1 = new JSeparator();

        // Bill Body
        JPanel pnlBillBody = new JPanel();
        pnlBillBody.setLayout(new BoxLayout(pnlBillBody, BoxLayout.Y_AXIS));
        pnlBillBody.setOpaque(false);

        JLabel lblTitleDoc = new JLabel("PHIẾU THU TIỀN ĐIỀU TRỊ", JLabel.CENTER);
        lblTitleDoc.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitleDoc.setForeground(UITheme.TEXT_DARK);
        lblTitleDoc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDatePrint = new JLabel("Ngày in: " + sdf.format(new Date()), JLabel.CENTER);
        lblDatePrint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblDatePrint.setForeground(UITheme.TEXT_MUTED);
        lblDatePrint.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlBillBody.add(lblTitleDoc);
        pnlBillBody.add(Box.createRigidArea(new Dimension(0, 4)));
        pnlBillBody.add(lblDatePrint);
        pnlBillBody.add(Box.createRigidArea(new Dimension(0, 14)));

        pnlBillBody.add(createBillRow("Mã chứng từ:", currentSelectedMaHD != null ? currentSelectedMaHD : currentSelectedMaCN));
        pnlBillBody.add(createBillRow("Khách hàng:", lblPayPatient.getText()));
        pnlBillBody.add(createBillRow("Dịch vụ điều trị:", "Khám & điều trị chuyên khoa nha"));
        pnlBillBody.add(createBillRow("Tổng chi phí:", lblPayTotal.getText()));
        pnlBillBody.add(createBillRow("Ưu đãi / Giảm giá:", lblPayDiscount.getText()));
        pnlBillBody.add(createBillRow("Số tiền thanh toán:", txtPayAmount.getText() + " đ"));
        pnlBillBody.add(createBillRow("Phương thức:", cboPayMethod.getSelectedItem().toString()));
        pnlBillBody.add(createBillRow("Còn lại (nợ lũy kế):", lblPayRemaining.getText()));

        pnlBillBody.add(Box.createRigidArea(new Dimension(0, 20)));

        // Signatures
        JPanel pnlSign = new JPanel(new GridLayout(1, 2));
        pnlSign.setOpaque(false);

        JPanel pnlSign1 = new JPanel(new GridLayout(2, 1));
        pnlSign1.setOpaque(false);
        JLabel lblS1 = new JLabel("Người nộp tiền", JLabel.CENTER);
        lblS1.setFont(UITheme.FONT_BOLD);
        JLabel lblS1Sub = new JLabel("(Ký, ghi rõ họ tên)", JLabel.CENTER);
        lblS1Sub.setFont(UITheme.FONT_TINY);
        lblS1Sub.setForeground(UITheme.TEXT_MUTED);
        pnlSign1.add(lblS1);
        pnlSign1.add(lblS1Sub);

        JPanel pnlSign2 = new JPanel(new GridLayout(2, 1));
        pnlSign2.setOpaque(false);
        JLabel lblS2 = new JLabel("Thu ngân / Thu quỹ", JLabel.CENTER);
        lblS2.setFont(UITheme.FONT_BOLD);
        JLabel lblS2Sub = new JLabel("(Đã ký điện tử)", JLabel.CENTER);
        lblS2Sub.setFont(UITheme.FONT_TINY);
        lblS2Sub.setForeground(UITheme.PRIMARY);
        pnlSign2.add(lblS2);
        pnlSign2.add(lblS2Sub);

        pnlSign.add(pnlSign1);
        pnlSign.add(pnlSign2);

        pnlBillBody.add(pnlSign);

        // Buttons
        JPanel pnlBottomBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBottomBtns.setOpaque(false);

        JButton btnPrint = UITheme.createButton("In hóa đơn (Máy in / PDF)", UITheme.PRIMARY, Color.WHITE);
        btnPrint.addActionListener(e -> {
            JOptionPane.showMessageDialog(dlg, "Đã gửi lệnh in đến máy in hóa đơn thành công!\nPhiếu thu chuẩn A5 / K80.", "In thành công", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
        });

        JButton btnClose = UITheme.createButton("Đóng", Color.WHITE, UITheme.TEXT_DARK);
        btnClose.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        btnClose.addActionListener(e -> dlg.dispose());

        pnlBottomBtns.add(btnClose);
        pnlBottomBtns.add(btnPrint);

        JPanel pnlTopWrap = new JPanel(new BorderLayout(0, 10));
        pnlTopWrap.setOpaque(false);
        pnlTopWrap.add(pnlClinic, BorderLayout.NORTH);
        pnlTopWrap.add(sep1, BorderLayout.SOUTH);

        pnl.add(pnlTopWrap, BorderLayout.NORTH);
        pnl.add(pnlBillBody, BorderLayout.CENTER);
        pnl.add(pnlBottomBtns, BorderLayout.SOUTH);

        dlg.setContentPane(pnl);
        dlg.setVisible(true);
    }

    private JPanel createBillRow(String label, String value) {
        JPanel r = new JPanel(new BorderLayout());
        r.setOpaque(false);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(UITheme.TEXT_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 12));
        v.setForeground(UITheme.TEXT_DARK);

        r.add(l, BorderLayout.WEST);
        r.add(v, BorderLayout.EAST);
        return r;
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

    // FR-24: Hủy hóa đơn
    private void xuLyHuyHoaDon() {
        if (currentUser != null && !currentUser.isQuanLy()) {
            JOptionPane.showMessageDialog(this,
                    "Chỉ Quản lý phòng khám mới có thẩm quyền phê duyệt hủy hóa đơn tài chính!",
                    "Thông báo phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (currentSelectedMaHD == null || currentSelectedMaHD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long chon hoa don can huy!", "Thong bao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String lyDo = JOptionPane.showInputDialog(this,
            "Nhap ly do huy hoa don " + currentSelectedMaHD + ":",
            "Huy hoa don", JOptionPane.WARNING_MESSAGE);
        if (lyDo == null) return;
        lyDo = lyDo.trim();
        if (lyDo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ly do huy khong duoc de trong!", "Loi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "XÁC NHẬN hủy hóa đơn " + currentSelectedMaHD + "?\nLý do: " + lyDo + "\n\nHành động này KHÔNG thể hoàn tác!",
            "Xác nhận hủy hóa đơn", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = hoaDonDAO.huyHoaDon(currentSelectedMaHD);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Đã hủy hóa đơn " + currentSelectedMaHD + " thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            currentSelectedMaHD = null;
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Hóa đơn đã bị hủy trước đó hoặc có lỗi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
