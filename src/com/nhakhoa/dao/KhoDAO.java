package com.nhakhoa.dao;

import com.nhakhoa.model.ChiTietDieuTri;
import com.nhakhoa.model.LoVatTu;
import com.nhakhoa.model.VatTu;
import com.nhakhoa.util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Xử lý dữ liệu Kho và Vật tư theo nguyên tắc FEFO (First Expired, First Out)
 */
public class KhoDAO {

    public List<VatTu> getAllVatTu() {
        List<VatTu> list = new ArrayList<>();
        String sql = "SELECT vt.MaVatTu, vt.TenVatTu, vt.MucTonToiThieu, vt.DonViTinh, "
                   + "       COALESCE(SUM(lo.SoLuongTon), 0) AS TongTon "
                   + "FROM VAT_TU vt "
                   + "LEFT JOIN LO_VAT_TU lo ON vt.MaVatTu = lo.MaVatTu AND lo.TinhTrangLo = 'KHA_DUNG' "
                   + "GROUP BY vt.MaVatTu, vt.TenVatTu, vt.MucTonToiThieu, vt.DonViTinh "
                   + "ORDER BY vt.MaVatTu ASC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                VatTu vt = new VatTu(
                    rs.getString("MaVatTu"),
                    rs.getString("TenVatTu"),
                    rs.getInt("MucTonToiThieu"),
                    rs.getString("DonViTinh"),
                    rs.getInt("TongTon")
                );
                list.add(vt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy danh sách Lô vật tư theo nguyên tắc FEFO:
     * Hạn sử dụng gần nhất xếp lên đầu tiên (HanSuDung ASC)
     */
    public List<LoVatTu> getAllLoVatTu() {
        List<LoVatTu> list = new ArrayList<>();
        String sql = "SELECT lo.MaLo, lo.MaVatTu, vt.TenVatTu, vt.DonViTinh, lo.SoLo, "
                   + "       lo.HanSuDung, lo.SoLuongTon, lo.TinhTrangLo "
                   + "FROM LO_VAT_TU lo "
                   + "JOIN VAT_TU vt ON lo.MaVatTu = vt.MaVatTu "
                   + "ORDER BY lo.HanSuDung ASC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                LoVatTu lo = new LoVatTu(
                    rs.getString("MaLo"),
                    rs.getString("MaVatTu"),
                    rs.getString("TenVatTu"),
                    rs.getString("DonViTinh"),
                    rs.getString("SoLo"),
                    rs.getDate("HanSuDung"),
                    rs.getInt("SoLuongTon"),
                    rs.getString("TinhTrangLo")
                );
                list.add(lo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * FR-60 / RB11: Thống kê các lô vật tư sắp hết hạn (trong vòng 30 ngày)
     */
    public List<LoVatTu> getLoSapHetHan() {
        List<LoVatTu> list = new ArrayList<>();
        String sql = "SELECT lo.MaLo, lo.MaVatTu, vt.TenVatTu, vt.DonViTinh, lo.SoLo, "
                   + "       lo.HanSuDung, lo.SoLuongTon, lo.TinhTrangLo "
                   + "FROM LO_VAT_TU lo "
                   + "JOIN VAT_TU vt ON lo.MaVatTu = vt.MaVatTu "
                   + "WHERE lo.TinhTrangLo = 'KHA_DUNG' "
                   + "  AND lo.HanSuDung BETWEEN CAST(GETDATE() AS DATE) AND DATEADD(day, 30, CAST(GETDATE() AS DATE)) "
                   + "ORDER BY lo.HanSuDung ASC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                LoVatTu lo = new LoVatTu(
                    rs.getString("MaLo"),
                    rs.getString("MaVatTu"),
                    rs.getString("TenVatTu"),
                    rs.getString("DonViTinh"),
                    rs.getString("SoLo"),
                    rs.getDate("HanSuDung"),
                    rs.getInt("SoLuongTon"),
                    rs.getString("TinhTrangLo")
                );
                list.add(lo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * FR-58: Cảnh báo vật tư có tổng tồn kho thấp hơn định mức tối thiểu
     */
    public List<VatTu> getVatTuTonThap() {
        List<VatTu> all = getAllVatTu();
        List<VatTu> result = new ArrayList<>();
        for (VatTu vt : all) {
            if (vt.isCanhBaoTonThap()) {
                result.add(vt);
            }
        }
        return result;
    }

    public String taoMaVTMoi() {
        String sql = "SELECT TOP 1 MaVatTu FROM VAT_TU ORDER BY MaVatTu DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1); // VT08
                int num = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("VT%02d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "VT01";
    }

    public String taoMaLoMoi() {
        String sql = "SELECT TOP 1 MaLo FROM LO_VAT_TU ORDER BY MaLo DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1); // LO008
                int num = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("LO%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "LO001";
    }

    public boolean themVatTu(VatTu vt) {
        String sql = "INSERT INTO VAT_TU (MaVatTu, TenVatTu, MucTonToiThieu, DonViTinh) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vt.getMaVatTu());
            ps.setString(2, vt.getTenVatTu());
            ps.setInt(3, vt.getMucTonToiThieu());
            ps.setString(4, vt.getDonViTinh());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean themLoVatTu(LoVatTu lo) {
        String sql = "INSERT INTO LO_VAT_TU (MaLo, MaVatTu, SoLo, HanSuDung, SoLuongTon, TinhTrangLo) VALUES (?, ?, ?, ?, ?, 'KHA_DUNG')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lo.getMaLo());
            ps.setString(2, lo.getMaVatTu());
            ps.setString(3, lo.getSoLo());
            ps.setDate(4, new java.sql.Date(lo.getHanSuDung().getTime()));
            ps.setInt(5, lo.getSoLuongTon());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * RB12 / FR-59: Thuật toán xuất kho tự động theo FEFO
     * Tìm các lô còn khả dụng của vật tư, ưu tiên lô có HanSuDung nhỏ nhất trừ trước
     */
    public boolean xuatKhoFEFO(String maVatTu, int soLuongXuat) {
        String sqlSelectLo = "SELECT MaLo, SoLuongTon FROM LO_VAT_TU "
                           + "WHERE MaVatTu = ? AND TinhTrangLo = 'KHA_DUNG' AND HanSuDung > GETDATE() "
                           + "ORDER BY HanSuDung ASC";

        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false); // Bật giao dịch để đảm bảo toàn vẹn

            PreparedStatement ps = conn.prepareStatement(sqlSelectLo);
            ps.setString(1, maVatTu);
            ResultSet rs = ps.executeQuery();

            int canTru = soLuongXuat;
            while (rs.next() && canTru > 0) {
                String maLo = rs.getString("MaLo");
                int tonLo = rs.getInt("SoLuongTon");

                if (tonLo <= canTru) {
                    // Lô này không đủ hoặc vừa đủ -> trừ hết về 0 và chuyển sang HET_HANG
                    String sqlUpdate = "UPDATE LO_VAT_TU SET SoLuongTon = 0, TinhTrangLo = 'HET_HANG' WHERE MaLo = ?";
                    PreparedStatement psUp = conn.prepareStatement(sqlUpdate);
                    psUp.setString(1, maLo);
                    psUp.executeUpdate();
                    canTru -= tonLo;
                } else {
                    // Lô này đủ trừ
                    int tonMoi = tonLo - canTru;
                    String sqlUpdate = "UPDATE LO_VAT_TU SET SoLuongTon = ? WHERE MaLo = ?";
                    PreparedStatement psUp = conn.prepareStatement(sqlUpdate);
                    psUp.setInt(1, tonMoi);
                    psUp.setString(2, maLo);
                    psUp.executeUpdate();
                    canTru = 0;
                }
            }

            if (canTru > 0) {
                // Không đủ tồn kho để xuất
                conn.rollback();
                return false;
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

    // FR-56: Kiểm kê tồn kho - điều chỉnh số lượng thực tế
    public boolean dieuChinhTonKho(String maVatTu, int soLuongThucTe, String lyDo) {
        // Tìm lô KHA_DUNG đầu tiên để điều chỉnh
        String sqlFind = "SELECT TOP 1 MaLo, SoLuongTon FROM LO_VAT_TU WHERE MaVatTu = ? AND TinhTrangLo = 'KHA_DUNG' ORDER BY HanSuDung ASC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlFind)) {
            ps.setString(1, maVatTu);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String maLo = rs.getString("MaLo");
                String sqlUp = "UPDATE LO_VAT_TU SET SoLuongTon = ? WHERE MaLo = ?";
                try (PreparedStatement psUp = conn.prepareStatement(sqlUp)) {
                    psUp.setInt(1, soLuongThucTe);
                    psUp.setString(2, maLo);
                    return psUp.executeUpdate() > 0;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Kiểm tra tồn kho đủ dùng (FR-33)
    public boolean kiemTraTonKhaDung(String maVatTu, int soLuongCan) {
        String sql = "SELECT COALESCE(SUM(SoLuongTon), 0) AS TongTon FROM LO_VAT_TU "
                   + "WHERE MaVatTu = ? AND TinhTrangLo = 'KHA_DUNG' AND HanSuDung > GETDATE()";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maVatTu);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("TongTon") >= soLuongCan;
        } catch (Exception e) { e.printStackTrace(); }
        return true;
    }

    /**
     * LUẬT 2: KIỂM TRA ĐỊNH MỨC VẬT TƯ KHẢ DỤNG (Còn hạn sử dụng + Đủ định mức)
     * Trả về thông báo lỗi chi tiết nếu thiếu, hoặc null nếu đủ.
     * Chưa trừ kho ở bước này!
     */
    public String kiemTraDinhMucVatTuKhaDung(List<ChiTietDieuTri> listCTDT) {
        if (listCTDT == null || listCTDT.isEmpty()) return null;

        Map<String, Double> mapCanDung = new HashMap<>();
        Map<String, String> mapTenVT = new HashMap<>();
        Map<String, String> mapDVT = new HashMap<>();

        String sqlDM = "SELECT dm.MaVatTu, vt.TenVatTu, vt.DonViTinh, dm.SoLuongTieuHao "
                     + "FROM DINH_MUC_VAT_TU dm "
                     + "JOIN VAT_TU vt ON dm.MaVatTu = vt.MaVatTu "
                     + "WHERE dm.MaDV = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlDM)) {

            for (ChiTietDieuTri ct : listCTDT) {
                ps.setString(1, ct.getMaDV());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String maVT = rs.getString("MaVatTu");
                        String tenVT = rs.getString("TenVatTu");
                        String dvt = rs.getString("DonViTinh");
                        double tieuHao = rs.getDouble("SoLuongTieuHao") * ct.getSoLuong();

                        mapCanDung.put(maVT, mapCanDung.getOrDefault(maVT, 0.0) + tieuHao);
                        mapTenVT.put(maVT, tenVT);
                        mapDVT.put(maVT, dvt);
                    }
                }
            }

            if (mapCanDung.isEmpty()) return null;

            String sqlTon = "SELECT COALESCE(SUM(SoLuongTon), 0) AS TonKhaDung "
                          + "FROM LO_VAT_TU "
                          + "WHERE MaVatTu = ? AND TinhTrangLo = 'KHA_DUNG' AND HanSuDung > CAST(GETDATE() AS DATE)";

            try (PreparedStatement psTon = conn.prepareStatement(sqlTon)) {
                StringBuilder sbThieu = new StringBuilder();
                for (Map.Entry<String, Double> entry : mapCanDung.entrySet()) {
                    String maVT = entry.getKey();
                    double canDung = entry.getValue();

                    psTon.setString(1, maVT);
                    try (ResultSet rsTon = psTon.executeQuery()) {
                        double tonKhaDung = 0;
                        if (rsTon.next()) {
                            tonKhaDung = rsTon.getDouble("TonKhaDung");
                        }

                        if (tonKhaDung < canDung) {
                            if (sbThieu.length() > 0) sbThieu.append("\n");
                            sbThieu.append("• ").append(mapTenVT.get(maVT))
                                   .append(" (Mã: ").append(maVT).append("): Định mức cần ")
                                   .append(canDung).append(" ").append(mapDVT.get(maVT))
                                   .append(" | Tồn kho khả dụng (còn hạn) chỉ có: ")
                                   .append((int)tonKhaDung).append(" ").append(mapDVT.get(maVT));
                        }
                    }
                }

                if (sbThieu.length() > 0) {
                    return sbThieu.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
