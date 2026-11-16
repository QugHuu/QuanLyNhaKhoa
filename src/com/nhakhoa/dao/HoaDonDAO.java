package com.nhakhoa.dao;

import com.nhakhoa.model.HoSoCongNo;
import com.nhakhoa.model.HoaDon;
import com.nhakhoa.util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    public List<HoaDon> getAll() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT hd.MaHoaDon, hd.MaBN, bn.HoTen AS TenBN, hd.NgayLap, "
                   + "       hd.TongTienDichVu, hd.TienGiamGia, hd.TienCocDaTru, hd.TongCanThanhToan, hd.TinhTrang "
                   + "FROM HOA_DON hd "
                   + "JOIN HO_SO_BENH_NHAN bn ON hd.MaBN = bn.MaBN "
                   + "ORDER BY hd.NgayLap DESC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                HoaDon hd = new HoaDon(
                    rs.getString("MaHoaDon"),
                    rs.getString("MaBN"),
                    rs.getString("TenBN"),
                    rs.getTimestamp("NgayLap"),
                    rs.getDouble("TongTienDichVu"),
                    rs.getDouble("TienGiamGia"),
                    rs.getDouble("TienCocDaTru"),
                    rs.getDouble("TongCanThanhToan"),
                    rs.getString("TinhTrang")
                );
                list.add(hd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<HoSoCongNo> getAllCongNo() {
        List<HoSoCongNo> list = new ArrayList<>();
        String sql = "SELECT cn.MaCongNo, cn.MaHoaDon, bn.HoTen AS TenBN, "
                   + "       cn.TongNoPhatSinh, cn.SoDuNoConLai, cn.TinhTrang "
                   + "FROM HO_SO_CONG_NO cn "
                   + "JOIN HOA_DON hd ON cn.MaHoaDon = hd.MaHoaDon "
                   + "JOIN HO_SO_BENH_NHAN bn ON hd.MaBN = bn.MaBN "
                   + "ORDER BY cn.SoDuNoConLai DESC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                HoSoCongNo cn = new HoSoCongNo(
                    rs.getString("MaCongNo"),
                    rs.getString("MaHoaDon"),
                    rs.getString("TenBN"),
                    rs.getDouble("TongNoPhatSinh"),
                    rs.getDouble("SoDuNoConLai"),
                    rs.getString("TinhTrang")
                );
                list.add(cn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String taoMaHDMoi() {
        String sql = "SELECT TOP 1 MaHoaDon FROM HOA_DON ORDER BY MaHoaDon DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1); // HD001
                int num = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("HD%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "HD001";
    }

    public String taoMaPTTMoi() {
        String sql = "SELECT TOP 1 MaPhieuTT FROM PHIEU_THANH_TOAN ORDER BY MaPhieuTT DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1); // PTT001
                int num = Integer.parseInt(lastId.substring(3)) + 1;
                return String.format("PTT%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "PTT001";
    }

    public String taoMaCongNoMoi() {
        String sql = "SELECT TOP 1 MaCongNo FROM HO_SO_CONG_NO ORDER BY MaCongNo DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1); // CN001
                int num = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("CN%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "CN001";
    }

    /**
     * Lập hóa đơn và xử lý thanh toán / công nợ (RB14, RB15)
     */
    public boolean lapHoaDonVaThanhToan(String maBN, double tongDichVu, double giamGia, double tienTraNgay, String phuongThuc) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            String maHD = taoMaHDMoi();
            double tongCanTra = tongDichVu - giamGia;
            if (tongCanTra < 0) tongCanTra = 0;

            String tinhTrangHD = (tienTraNgay >= tongCanTra) ? "DA_TRA" : (tienTraNgay > 0 ? "CON_NO" : "CHUA_TRA");

            // 1. Thêm HOA_DON
            String sqlHD = "INSERT INTO HOA_DON (MaHoaDon, MaBN, NgayLap, TongTienDichVu, TienGiamGia, TienCocDaTru, TongCanThanhToan, TinhTrang) "
                         + "VALUES (?, ?, GETDATE(), ?, ?, 0, ?, ?)";
            PreparedStatement psHD = conn.prepareStatement(sqlHD);
            psHD.setString(1, maHD);
            psHD.setString(2, maBN);
            psHD.setDouble(3, tongDichVu);
            psHD.setDouble(4, giamGia);
            psHD.setDouble(5, tongCanTra);
            psHD.setString(6, tinhTrangHD);
            psHD.executeUpdate();

            // 2. Thêm PHIEU_THANH_TOAN (nếu có thanh toán ngay)
            if (tienTraNgay > 0) {
                String maPTT = taoMaPTTMoi();
                String sqlPTT = "INSERT INTO PHIEU_THANH_TOAN (MaPhieuTT, MaHoaDon, NgayThanhToan, LoaiGiaoDich, SoTien, PhuongThuc) "
                              + "VALUES (?, ?, GETDATE(), 'THU_TIEN', ?, ?)";
                PreparedStatement psPTT = conn.prepareStatement(sqlPTT);
                psPTT.setString(1, maPTT);
                psPTT.setString(2, maHD);
                psPTT.setDouble(3, tienTraNgay);
                psPTT.setString(4, phuongThuc);
                psPTT.executeUpdate();
            }

            // Gán MaHoaDon cho các Phiếu điều trị hoàn thành của bệnh nhân chưa có hóa đơn (RB14)
            String sqlLinkPDT = "UPDATE PHIEU_DIEU_TRI SET MaHoaDon = ? WHERE MaHoaDon IS NULL AND MaPhieuKham IN (SELECT MaPhieuKham FROM PHIEU_KHAM WHERE MaBN = ?)";
            try (PreparedStatement psLink = conn.prepareStatement(sqlLinkPDT)) {
                psLink.setString(1, maHD);
                psLink.setString(2, maBN);
                psLink.executeUpdate();
            } catch (Exception ex) {}

            // 3. Nếu còn thiếu -> Tạo HO_SO_CONG_NO (RB15: trả góp / ghi nợ)
            if (tienTraNgay < tongCanTra) {
                double noPhatSinh = tongCanTra - tienTraNgay;
                String maCN = taoMaCongNoMoi();
                String sqlCN = "INSERT INTO HO_SO_CONG_NO (MaCongNo, MaHoaDon, TongNoPhatSinh, SoDuNoConLai, TinhTrang) "
                             + "VALUES (?, ?, ?, ?, 'CON_NO')";
                PreparedStatement psCN = conn.prepareStatement(sqlCN);
                psCN.setString(1, maCN);
                psCN.setString(2, maHD);
                psCN.setDouble(3, noPhatSinh);
                psCN.setDouble(4, noPhatSinh);
                psCN.executeUpdate();
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
     * Bệnh nhân trả nợ công nợ
     */
    public boolean traNo(String maCongNo, String maHoaDon, double soTienTra, String phuongThuc) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            // 1. Lấy dư nợ hiện tại
            String sqlSelect = "SELECT SoDuNoConLai FROM HO_SO_CONG_NO WHERE MaCongNo = ?";
            PreparedStatement psSel = conn.prepareStatement(sqlSelect);
            psSel.setString(1, maCongNo);
            ResultSet rs = psSel.executeQuery();

            if (rs.next()) {
                double duNoHienTai = rs.getDouble("SoDuNoConLai");
                double duNoMoi = duNoHienTai - soTienTra;
                if (duNoMoi < 0) duNoMoi = 0;

                String tinhTrangCN = (duNoMoi == 0) ? "DA_HOAN_TAT" : "CON_NO";

                // 2. Cập nhật HO_SO_CONG_NO
                String sqlUpCN = "UPDATE HO_SO_CONG_NO SET SoDuNoConLai = ?, TinhTrang = ? WHERE MaCongNo = ?";
                PreparedStatement psUpCN = conn.prepareStatement(sqlUpCN);
                psUpCN.setDouble(1, duNoMoi);
                psUpCN.setString(2, tinhTrangCN);
                psUpCN.setString(3, maCongNo);
                psUpCN.executeUpdate();

                // 3. Nếu hết nợ -> Đổi HOA_DON sang DA_TRA
                if (duNoMoi == 0) {
                    String sqlUpHD = "UPDATE HOA_DON SET TinhTrang = 'DA_TRA' WHERE MaHoaDon = ?";
                    PreparedStatement psUpHD = conn.prepareStatement(sqlUpHD);
                    psUpHD.setString(1, maHoaDon);
                    psUpHD.executeUpdate();
                }

                // 4. Ghi nhận phiếu thanh toán
                String maPTT = taoMaPTTMoi();
                String sqlPTT = "INSERT INTO PHIEU_THANH_TOAN (MaPhieuTT, MaHoaDon, MaCongNo, NgayThanhToan, LoaiGiaoDich, SoTien, PhuongThuc) "
                              + "VALUES (?, ?, ?, GETDATE(), 'TRA_GOP', ?, ?)";
                PreparedStatement psPTT = conn.prepareStatement(sqlPTT);
                psPTT.setString(1, maPTT);
                psPTT.setString(2, maHoaDon);
                psPTT.setString(3, maCongNo);
                psPTT.setDouble(4, soTienTra);
                psPTT.setString(5, phuongThuc);
                psPTT.executeUpdate();

                conn.commit();
                return true;
            }
            return false;
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

    // FR-24: Hủy hóa đơn với lý do (soft delete)
    public boolean huyHoaDon(String maHD) {
        String sql = "UPDATE HOA_DON SET TinhTrang = 'DA_HUY' WHERE MaHoaDon = ? AND TinhTrang <> 'DA_HUY'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                // Hủy công nợ liên quan nếu có
                String sqlCN = "UPDATE HO_SO_CONG_NO SET TinhTrang = 'DA_HUY' WHERE MaHoaDon = ?";
                try (PreparedStatement psCN = conn.prepareStatement(sqlCN)) {
                    psCN.setString(1, maHD);
                    psCN.executeUpdate();
                } catch (Exception ex) {}
            }
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy chi tiết dịch vụ trong hóa đơn
    public java.util.List<String[]> getChiTietHoaDon(String maHD) {
        java.util.List<String[]> list = new java.util.ArrayList<>();
        String sql = "SELECT dv.TenDV AS TenDichVu, ctdt.SoLuong, ctdt.DonGiaApDung AS DonGia, ctdt.ThanhTien "
                   + "FROM CHI_TIET_DIEU_TRI ctdt "
                   + "JOIN PHIEU_DIEU_TRI pdt ON ctdt.MaPhieuDT = pdt.MaPhieuDT "
                   + "JOIN DICH_VU_NHA_KHOA dv ON ctdt.MaDV = dv.MaDV "
                   + "WHERE pdt.MaHoaDon = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("TenDichVu"),
                    rs.getString("SoLuong"),
                    rs.getString("DonGia"),
                    rs.getString("ThanhTien")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
