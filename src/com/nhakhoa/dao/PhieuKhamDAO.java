package com.nhakhoa.dao;

import com.nhakhoa.model.ChiTietDieuTri;
import com.nhakhoa.model.ChiTietDonThuoc;
import com.nhakhoa.model.PhieuKham;
import com.nhakhoa.util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PhieuKhamDAO {

    public List<PhieuKham> getAll() {
        List<PhieuKham> list = new ArrayList<>();
        String sql = "SELECT pk.MaPhieuKham, pk.MaLichHen, pk.MaBN, bn.HoTen AS TenBN, "
                   + "       pk.MaBS, bs.HoTen AS TenBS, pk.NgayKham, pk.TrieuChung, "
                   + "       pk.ChanDoan, pk.TinhTrang "
                   + "FROM PHIEU_KHAM pk "
                   + "JOIN HO_SO_BENH_NHAN bn ON pk.MaBN = bn.MaBN "
                   + "JOIN BAC_SI bs ON pk.MaBS = bs.MaBS "
                   + "ORDER BY pk.NgayKham DESC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                PhieuKham pk = new PhieuKham(
                    rs.getString("MaPhieuKham"),
                    rs.getString("MaLichHen"),
                    rs.getString("MaBN"),
                    rs.getString("TenBN"),
                    rs.getString("MaBS"),
                    rs.getString("TenBS"),
                    rs.getTimestamp("NgayKham"),
                    rs.getString("TrieuChung"),
                    rs.getString("ChanDoan"),
                    rs.getString("TinhTrang")
                );
                list.add(pk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String taoMaPKMoi() {
        String sql = "SELECT TOP 1 MaPhieuKham FROM PHIEU_KHAM ORDER BY MaPhieuKham DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1); // PK001
                int num = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("PK%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "PK001";
    }

    public String taoMaPDTMoi() {
        String sql = "SELECT TOP 1 MaPhieuDT FROM PHIEU_DIEU_TRI ORDER BY MaPhieuDT DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1); // PDT001
                int num = Integer.parseInt(lastId.substring(3)) + 1;
                return String.format("PDT%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "PDT001";
    }

    public String taoMaDonThuocMoi() {
        String sql = "SELECT TOP 1 MaDonThuoc FROM DON_THUOC ORDER BY MaDonThuoc DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1); // DT001
                int num = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("DT%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "DT001";
    }

    /**
     * Lưu ca khám bệnh và điều trị của Bác sĩ (Gồm Phiếu khám, Điều trị, Đơn thuốc)
     */
    public boolean luuCaKham(PhieuKham pk, List<ChiTietDieuTri> listCTDT, String loiDan, List<ChiTietDonThuoc> listThuoc) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            // 1. Thêm PHIEU_KHAM
            String sqlPK = "INSERT INTO PHIEU_KHAM (MaPhieuKham, MaLichHen, MaBN, MaBS, NgayKham, TrieuChung, ChanDoan, TinhTrang) "
                         + "VALUES (?, ?, ?, ?, GETDATE(), ?, ?, 'HOAN_THANH')";
            PreparedStatement psPK = conn.prepareStatement(sqlPK);
            psPK.setString(1, pk.getMaPhieuKham());
            psPK.setString(2, pk.getMaLichHen());
            psPK.setString(3, pk.getMaBN());
            psPK.setString(4, pk.getMaBS());
            psPK.setString(5, pk.getTrieuChung());
            psPK.setString(6, pk.getChanDoan());
            psPK.executeUpdate();

            // 2. Cập nhật trạng thái LICH_HEN thành DA_KHAM (nếu có lịch hẹn)
            if (pk.getMaLichHen() != null && !pk.getMaLichHen().isEmpty()) {
                String sqlLH = "UPDATE LICH_HEN SET TinhTrang = 'DA_KHAM' WHERE MaLichHen = ?";
                PreparedStatement psLH = conn.prepareStatement(sqlLH);
                psLH.setString(1, pk.getMaLichHen());
                psLH.executeUpdate();
            }

            // 3. Thêm PHIEU_DIEU_TRI & CHI_TIET_DIEU_TRI (nếu có dịch vụ)
            if (listCTDT != null && !listCTDT.isEmpty()) {
                String maPDT = taoMaPDTMoi();
                String sqlPDT = "INSERT INTO PHIEU_DIEU_TRI (MaPhieuDT, MaPhieuKham, NgayLap, KetQuaLamSang, TinhTrang) "
                              + "VALUES (?, ?, GETDATE(), ?, 'HOAN_THANH')";
                PreparedStatement psPDT = conn.prepareStatement(sqlPDT);
                psPDT.setString(1, maPDT);
                psPDT.setString(2, pk.getMaPhieuKham());
                psPDT.setString(3, pk.getChanDoan());
                psPDT.executeUpdate();

                String sqlCTDT = "INSERT INTO CHI_TIET_DIEU_TRI (MaPhieuDT, MaDV, ViTriRang, SoLuong, DonGiaApDung, ThanhTien) "
                               + "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement psCTDT = conn.prepareStatement(sqlCTDT);

                for (ChiTietDieuTri ct : listCTDT) {
                    psCTDT.setString(1, maPDT);
                    psCTDT.setString(2, ct.getMaDV());
                    psCTDT.setString(3, ct.getViTriRang());
                    psCTDT.setInt(4, ct.getSoLuong());
                    psCTDT.setDouble(5, ct.getDonGiaApDung());
                    psCTDT.setDouble(6, ct.getThanhTien());
                    psCTDT.executeUpdate();
                }

                // LUẬT 3: TRỪ TỒN KHO FEFO CHÍNH THỨC SAU KHI DỊCH VỤ ĐƯỢC BÁC SĨ XÁC NHẬN HOÀN THÀNH
                String maPXK = "PXK_" + (maPDT.length() > 4 ? maPDT.substring(maPDT.length() - 4) : maPDT);
                String sqlCheckPXK = "SELECT COUNT(*) FROM PHIEU_XUAT_KHO WHERE MaPhieuXuat = ?";
                PreparedStatement psCheckPXK = conn.prepareStatement(sqlCheckPXK);
                psCheckPXK.setString(1, maPXK);
                ResultSet rsCheckPXK = psCheckPXK.executeQuery();
                boolean existsPXK = (rsCheckPXK.next() && rsCheckPXK.getInt(1) > 0);
                rsCheckPXK.close();

                if (!existsPXK) {
                    String sqlPXK = "INSERT INTO PHIEU_XUAT_KHO (MaPhieuXuat, MaTaiKhoan, MaPhieuDT, NgayChungTu, LoaiXuat) "
                                  + "VALUES (?, 'TK001', ?, GETDATE(), 'TU_DONG_FEFO')";
                    PreparedStatement psPXK = conn.prepareStatement(sqlPXK);
                    psPXK.setString(1, maPXK);
                    psPXK.setString(2, maPDT);
                    psPXK.executeUpdate();

                    String sqlDM = "SELECT dm.MaVatTu, SUM(ct.SoLuong * dm.SoLuongTieuHao) AS TongCanXuat "
                                 + "FROM CHI_TIET_DIEU_TRI ct "
                                 + "JOIN DINH_MUC_VAT_TU dm ON ct.MaDV = dm.MaDV "
                                 + "WHERE ct.MaPhieuDT = ? "
                                 + "GROUP BY dm.MaVatTu";
                    PreparedStatement psDM = conn.prepareStatement(sqlDM);
                    psDM.setString(1, maPDT);
                    ResultSet rsDM = psDM.executeQuery();

                    String sqlLoFEFO = "SELECT MaLo, SoLuongTon FROM LO_VAT_TU "
                                     + "WHERE MaVatTu = ? AND TinhTrangLo = 'KHA_DUNG' AND HanSuDung > CAST(GETDATE() AS DATE) "
                                     + "ORDER BY HanSuDung ASC";
                    PreparedStatement psLo = conn.prepareStatement(sqlLoFEFO);

                    String sqlInsertCTXK = "INSERT INTO CHI_TIET_XUAT_KHO (MaPhieuXuat, MaLo, SoLuongXuat) VALUES (?, ?, ?)";
                    PreparedStatement psCTXK = conn.prepareStatement(sqlInsertCTXK);

                    String sqlUpdateLo = "UPDATE LO_VAT_TU SET SoLuongTon = SoLuongTon - ?, "
                                       + "TinhTrangLo = CASE WHEN (SoLuongTon - ?) <= 0 THEN 'HET_HANG' ELSE TinhTrangLo END "
                                       + "WHERE MaLo = ?";
                    PreparedStatement psUpLo = conn.prepareStatement(sqlUpdateLo);

                    while (rsDM.next()) {
                        String maVT = rsDM.getString("MaVatTu");
                        double canXuat = rsDM.getDouble("TongCanXuat");

                        psLo.setString(1, maVT);
                        ResultSet rsLo = psLo.executeQuery();
                        while (rsLo.next() && canXuat > 0) {
                            String maLo = rsLo.getString("MaLo");
                            int tonLo = rsLo.getInt("SoLuongTon");

                            int xuatTuLo;
                            if (tonLo >= canXuat) {
                                xuatTuLo = (int) Math.ceil(canXuat);
                                canXuat = 0;
                            } else {
                                xuatTuLo = tonLo;
                                canXuat -= tonLo;
                            }

                            psCTXK.setString(1, maPXK);
                            psCTXK.setString(2, maLo);
                            psCTXK.setInt(3, xuatTuLo);
                            psCTXK.executeUpdate();

                            psUpLo.setInt(1, xuatTuLo);
                            psUpLo.setInt(2, xuatTuLo);
                            psUpLo.setString(3, maLo);
                            psUpLo.executeUpdate();
                        }
                        rsLo.close();
                    }
                    rsDM.close();
                }
            }

            // 4. Thêm DON_THUOC & CHI_TIET_DON_THUOC (nếu có kê thuốc)
            if (listThuoc != null && !listThuoc.isEmpty()) {
                String maDT = taoMaDonThuocMoi();
                String sqlDT = "INSERT INTO DON_THUOC (MaDonThuoc, MaPhieuKham, NgayKe, LoiDanBacSi) VALUES (?, ?, GETDATE(), ?)";
                PreparedStatement psDT = conn.prepareStatement(sqlDT);
                psDT.setString(1, maDT);
                psDT.setString(2, pk.getMaPhieuKham());
                psDT.setString(3, loiDan);
                psDT.executeUpdate();

                String sqlCTThuoc = "INSERT INTO CHI_TIET_DON_THUOC (MaDonThuoc, TenThuoc, DonViTinh, SoLuong, LieuDung) "
                                  + "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psCTThuoc = conn.prepareStatement(sqlCTThuoc);
                for (ChiTietDonThuoc ct : listThuoc) {
                    psCTThuoc.setString(1, maDT);
                    psCTThuoc.setString(2, ct.getTenThuoc());
                    psCTThuoc.setString(3, ct.getDonViTinh());
                    psCTThuoc.setInt(4, ct.getSoLuong());
                    psCTThuoc.setString(5, ct.getLieuDung());
                    psCTThuoc.executeUpdate();
                }
            }

            // 5. Tự động lập Hóa đơn chờ thanh toán cho Lễ tân thu tiền (nếu có dịch vụ điều trị)
            if (listCTDT != null && !listCTDT.isEmpty()) {
                double tongTien = 0;
                for (ChiTietDieuTri ct : listCTDT) tongTien += ct.getThanhTien();
                HoaDonDAO hdDAO = new HoaDonDAO();
                String maHD = hdDAO.taoMaHDMoi();
                String sqlHD = "INSERT INTO HOA_DON (MaHoaDon, MaBN, NgayLap, TongTienDichVu, TienGiamGia, TienCocDaTru, TongCanThanhToan, TinhTrang) "
                             + "VALUES (?, ?, GETDATE(), ?, 0, 0, ?, 'CHUA_TRA')";
                PreparedStatement psHD = conn.prepareStatement(sqlHD);
                psHD.setString(1, maHD);
                psHD.setString(2, pk.getMaBN());
                psHD.setDouble(3, tongTien);
                psHD.setDouble(4, tongTien);
                psHD.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) {}
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (Exception ex) {}
            }
            DBConnect.close(conn);
        }
    }

    /**
     * Lấy toàn bộ lịch sử khám và điều trị thực tế của một bệnh nhân
     */
    public List<Object[]> getLichSuDieuTriCuaBenhNhan(String maBN) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT pk.MaPhieuKham, pk.NgayKham, "
                   + "       COALESCE(dv.TenDV, N'Khám & tư vấn tổng quát') AS TenDV, "
                   + "       bs.HoTen AS TenBS, "
                   + "       pk.ChanDoan, "
                   + "       pk.TinhTrang "
                   + "FROM PHIEU_KHAM pk "
                   + "JOIN BAC_SI bs ON pk.MaBS = bs.MaBS "
                   + "LEFT JOIN PHIEU_DIEU_TRI pdt ON pk.MaPhieuKham = pdt.MaPhieuKham "
                   + "LEFT JOIN CHI_TIET_DIEU_TRI ctdt ON pdt.MaPhieuDT = ctdt.MaPhieuDT "
                   + "LEFT JOIN DICH_VU_NHA_KHOA dv ON ctdt.MaDV = dv.MaDV "
                   + "WHERE pk.MaBN = ? "
                   + "ORDER BY pk.NgayKham DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maBN);
            try (ResultSet rs = ps.executeQuery()) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                while (rs.next()) {
                    String maPK = rs.getString("MaPhieuKham");
                    String ngay = rs.getTimestamp("NgayKham") != null ? sdf.format(rs.getTimestamp("NgayKham")) : "---";
                    String dv = rs.getString("TenDV");
                    String bs = rs.getString("TenBS");
                    String cd = rs.getString("ChanDoan");
                    String tt = rs.getString("TinhTrang");
                    list.add(new Object[]{maPK, ngay, dv, bs, cd, tt});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Xuất chi tiết toàn bộ hồ sơ Phiếu khám & Đơn thuốc dưới dạng HTML chuyên nghiệp
     */
    public String getChiTietPhieuKhamHTML(String maPK) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:Segoe UI, sans-serif; font-size:12px; color:#1e293b; padding:15px;'>");

        String sqlPK = "SELECT pk.MaPhieuKham, pk.NgayKham, pk.TrieuChung, pk.ChanDoan, pk.TinhTrang, "
                     + "       bn.MaBN, bn.HoTen AS TenBN, bn.NgaySinh, bn.SoDienThoai, bn.SoCCCD, bn.TienSuBenh, "
                     + "       bs.HoTen AS TenBS, bs.ChuyenKhoa "
                     + "FROM PHIEU_KHAM pk "
                     + "JOIN HO_SO_BENH_NHAN bn ON pk.MaBN = bn.MaBN "
                     + "JOIN BAC_SI bs ON pk.MaBS = bs.MaBS "
                     + "WHERE pk.MaPhieuKham = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlPK)) {
            ps.setString(1, maPK);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.text.SimpleDateFormat sdfNgay = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                    html.append("<div style='text-align:center; border-bottom:2px solid #006C5B; padding-bottom:8px; margin-bottom:12px;'>");
                    html.append("<h2 style='color:#006C5B; margin:0;'>NHA KHOA SÀI GÒN DENTAL</h2>");
                    html.append("<p style='margin:2px 0; color:#64748b;'>Địa chỉ: 123 Nguyễn Văn Cừ, Quận 5, TP.HCM · Hotline: 1900 6868</p>");
                    html.append("<h3 style='margin:8px 0 0 0; color:#0f172a;'>PHIẾU KHÁM & CHỈ ĐỊNH ĐIỀU TRỊ</h3>");
                    html.append("<p style='margin:2px 0; font-size:11px; color:#64748b;'>Mã phiếu: <b>").append(rs.getString("MaPhieuKham")).append("</b> | Ngày lập: ").append(rs.getTimestamp("NgayKham") != null ? sdfNgay.format(rs.getTimestamp("NgayKham")) : "---").append("</p>");
                    html.append("</div>");

                    html.append("<table style='width:100%; margin-bottom:12px; font-size:12px;'>");
                    html.append("<tr><td><b>Họ tên bệnh nhân:</b> ").append(rs.getString("TenBN")).append(" (").append(rs.getString("MaBN")).append(")</td>");
                    html.append("<td><b>Bác sĩ phụ trách:</b> ").append(rs.getString("TenBS")).append("</td></tr>");
                    html.append("<tr><td><b>Số điện thoại:</b> ").append(rs.getString("SoDienThoai")).append("</td>");
                    html.append("<td><b>Chuyên khoa:</b> ").append(rs.getString("ChuyenKhoa") != null ? rs.getString("ChuyenKhoa") : "Răng Hàm Mặt").append("</td></tr>");
                    html.append("<tr><td colspan='2'><b>CCCD / Định danh:</b> ").append(rs.getString("SoCCCD") != null ? rs.getString("SoCCCD") : "---").append("</td></tr>");

                    String tienSu = rs.getString("TienSuBenh");
                    if (tienSu != null && !tienSu.trim().isEmpty()) {
                        html.append("<tr><td colspan='2' style='color:#b45309;'><b>Tiền sử / Dị ứng:</b> ").append(tienSu).append("</td></tr>");
                    }
                    html.append("</table>");

                    html.append("<div style='background:#f8fafc; border-left:3px solid #006C5B; padding:8px 12px; margin-bottom:12px;'>");
                    html.append("<p style='margin:3px 0;'><b>Triệu chứng lâm sàng:</b> ").append(rs.getString("TrieuChung")).append("</p>");
                    html.append("<p style='margin:3px 0;'><b>Chẩn đoán xác định:</b> <span style='color:#006C5B; font-weight:bold;'>").append(rs.getString("ChanDoan")).append("</span></p>");
                    html.append("</div>");

                    html.append("<h4 style='margin:10px 0 6px 0; color:#0f172a;'>DỊCH VỤ & THỦ THUẬT ĐIỀU TRỊ</h4>");
                    html.append("<table border='1' cellspacing='0' cellpadding='5' style='width:100%; border-collapse:collapse; border-color:#e2e8f0; font-size:11px;'>");
                    html.append("<tr style='background:#f1f5f9; font-weight:bold;'><th>Dịch vụ điều trị</th><th>Vị trí răng</th><th>SL</th><th>Đơn giá</th><th>Thành tiền</th></tr>");

                    String sqlDV = "SELECT dv.TenDV, ct.ViTriRang, ct.SoLuong, ct.DonGiaApDung, ct.ThanhTien "
                                 + "FROM PHIEU_DIEU_TRI pdt "
                                 + "JOIN CHI_TIET_DIEU_TRI ct ON pdt.MaPhieuDT = ct.MaPhieuDT "
                                 + "JOIN DICH_VU_NHA_KHOA dv ON ct.MaDV = dv.MaDV "
                                 + "WHERE pdt.MaPhieuKham = ?";
                    double tongTienDV = 0;
                    boolean coDV = false;
                    try (PreparedStatement psDV = conn.prepareStatement(sqlDV)) {
                        psDV.setString(1, maPK);
                        try (ResultSet rsDV = psDV.executeQuery()) {
                            while (rsDV.next()) {
                                coDV = true;
                                tongTienDV += rsDV.getDouble("ThanhTien");
                                html.append("<tr>");
                                html.append("<td>").append(rsDV.getString("TenDV")).append("</td>");
                                html.append("<td align='center'>").append(rsDV.getString("ViTriRang")).append("</td>");
                                html.append("<td align='center'>").append(rsDV.getInt("SoLuong")).append("</td>");
                                html.append("<td align='right'>").append(com.nhakhoa.util.FormatUtil.formatMoney(rsDV.getDouble("DonGiaApDung"))).append("</td>");
                                html.append("<td align='right'><b>").append(com.nhakhoa.util.FormatUtil.formatMoney(rsDV.getDouble("ThanhTien"))).append("</b></td>");
                                html.append("</tr>");
                            }
                        }
                    }
                    if (!coDV) {
                        html.append("<tr><td colspan='5' align='center' style='color:#64748b;'>Khám và tư vấn chuyên khoa (Không chỉ định thủ thuật)</td></tr>");
                    }
                    html.append("</table>");
                    if (tongTienDV > 0) {
                        html.append("<p align='right' style='margin:4px 0 10px 0;'><b>Tổng chi phí dịch vụ: </b><span style='color:#006C5B; font-weight:bold; font-size:13px;'>")
                            .append(com.nhakhoa.util.FormatUtil.formatMoney(tongTienDV)).append("</span></p>");
                    }

                    String sqlThuoc = "SELECT dt.LoiDanBacSi, ct.TenThuoc, ct.DonViTinh, ct.SoLuong, ct.LieuDung "
                                    + "FROM DON_THUOC dt "
                                    + "JOIN CHI_TIET_DON_THUOC ct ON dt.MaDonThuoc = ct.MaDonThuoc "
                                    + "WHERE dt.MaPhieuKham = ?";
                    try (PreparedStatement psT = conn.prepareStatement(sqlThuoc)) {
                        psT.setString(1, maPK);
                        try (ResultSet rsT = psT.executeQuery()) {
                            boolean coThuoc = false;
                            String loiDan = "";
                            StringBuilder sbT = new StringBuilder();
                            sbT.append("<h4 style='margin:12px 0 6px 0; color:#0f172a;'>ĐƠN THUỐC ĐIỀU TRỊ</h4>");
                            sbT.append("<table border='1' cellspacing='0' cellpadding='5' style='width:100%; border-collapse:collapse; border-color:#e2e8f0; font-size:11px;'>");
                            sbT.append("<tr style='background:#f1f5f9; font-weight:bold;'><th>Tên thuốc</th><th>Đơn vị</th><th>SL</th><th>Hướng dẫn sử dụng & Liều dùng</th></tr>");

                            while (rsT.next()) {
                                coThuoc = true;
                                loiDan = rsT.getString("LoiDanBacSi");
                                sbT.append("<tr>");
                                sbT.append("<td><b>").append(rsT.getString("TenThuoc")).append("</b></td>");
                                sbT.append("<td align='center'>").append(rsT.getString("DonViTinh")).append("</td>");
                                sbT.append("<td align='center'>").append(rsT.getInt("SoLuong")).append("</td>");
                                sbT.append("<td>").append(rsT.getString("LieuDung")).append("</td>");
                                sbT.append("</tr>");
                            }
                            sbT.append("</table>");

                            if (coThuoc) {
                                html.append(sbT.toString());
                                if (loiDan != null && !loiDan.trim().isEmpty()) {
                                    html.append("<p style='margin:6px 0; font-size:11px;'><b>Lời dặn bác sĩ:</b> <i>").append(loiDan).append("</i></p>");
                                }
                            }
                        }
                    }

                    html.append("<table style='width:100%; margin-top:20px; text-align:center;'>");
                    html.append("<tr><td style='width:50%;'></td><td style='width:50%;'><b>BÁC SĨ ĐIỀU TRỊ</b><br><br><br><b>").append(rs.getString("TenBS")).append("</b></td></tr>");
                    html.append("</table>");
                } else {
                    html.append("<p style='color:red;'>Không tìm thấy phiếu khám có mã ").append(maPK).append("</p>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            html.append("<p style='color:red;'>Lỗi khi tải phiếu khám: ").append(e.getMessage()).append("</p>");
        }

        html.append("</body></html>");
        return html.toString();
    }
}
