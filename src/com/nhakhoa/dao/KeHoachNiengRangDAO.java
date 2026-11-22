package com.nhakhoa.dao;

import com.nhakhoa.model.KeHoachNiengRang;
import com.nhakhoa.util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class KeHoachNiengRangDAO {

    public List<KeHoachNiengRang> getAll() {
        List<KeHoachNiengRang> list = new ArrayList<>();
        String sql = "SELECT kh.MaKeHoach, kh.MaPhieuDT, kh.LoTrinhGiaiDoan, kh.LyDoTamHoanHuy, kh.TinhTrang, "
                   + "       COALESCE(bn.MaBN, '---') AS MaBN, "
                   + "       COALESCE(bn.HoTen, N'Bệnh nhân điều trị') AS TenBN, "
                   + "       COALESCE(cn.SoDuNoConLai, 0) AS SoDuNo, "
                   + "       COALESCE(hd.MaHoaDon, '---') AS MaHoaDon "
                   + "FROM KE_HOACH_NIENG_RANG kh "
                   + "LEFT JOIN PHIEU_DIEU_TRI pdt ON kh.MaPhieuDT = pdt.MaPhieuDT "
                   + "LEFT JOIN PHIEU_KHAM pk ON pdt.MaPhieuKham = pk.MaPhieuKham "
                   + "LEFT JOIN HO_SO_BENH_NHAN bn ON pk.MaBN = bn.MaBN "
                   + "LEFT JOIN HOA_DON hd ON pdt.MaHoaDon = hd.MaHoaDon OR (bn.MaBN = hd.MaBN) "
                   + "LEFT JOIN HO_SO_CONG_NO cn ON hd.MaHoaDon = cn.MaHoaDon "
                   + "ORDER BY kh.MaKeHoach ASC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                KeHoachNiengRang kh = new KeHoachNiengRang(
                    rs.getString("MaKeHoach"),
                    rs.getString("MaPhieuDT"),
                    rs.getString("MaBN"),
                    rs.getString("TenBN"),
                    rs.getString("LoTrinhGiaiDoan"),
                    rs.getString("LyDoTamHoanHuy"),
                    rs.getString("TinhTrang"),
                    rs.getDouble("SoDuNo"),
                    rs.getString("MaHoaDon")
                );
                list.add(kh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * LUẬT 4 - XỬ LÝ NIỀNG RĂNG KHI "TẠM HOÃN":
     * - Cập nhật trạng thái 'TAM_HOAN'.
     * - Dừng lịch hẹn tái khám trong tương lai.
     * - TUYỆT ĐỐI KHÔNG TỰ ĐỘNG THAY ĐỔI CÔNG NỢ HOẶC HÓA ĐƠN.
     */
    public boolean tamHoanKeHoach(String maKeHoach, String lyDo) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            // 1. Cập nhật trạng thái kế hoạch thành TAM_HOAN
            String sqlKH = "UPDATE KE_HOACH_NIENG_RANG SET TinhTrang = 'TAM_HOAN', LyDoTamHoanHuy = ? WHERE MaKeHoach = ?";
            PreparedStatement psKH = conn.prepareStatement(sqlKH);
            psKH.setString(1, lyDo);
            psKH.setString(2, maKeHoach);
            psKH.executeUpdate();

            // 2. Dừng các lịch hẹn tái khám trong tương lai
            String sqlLH = "UPDATE LICH_HEN SET TinhTrang = 'DA_HUY', LyDoHuy = N'Dừng lịch do bệnh nhân tạm hoãn niềng răng' "
                         + "WHERE MaKeHoach = ? AND NgayHen >= CAST(GETDATE() AS DATE) AND TinhTrang IN ('CHO_KHAM', 'DA_DAT')";
            PreparedStatement psLH = conn.prepareStatement(sqlLH);
            psLH.setString(1, maKeHoach);
            psLH.executeUpdate();

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
     * LUẬT 4 - XỬ LÝ NIỀNG RĂNG KHI "ĐÃ HỦY":
     * - BẮT BUỘC CÓ BƯỚC QUẢN LÝ PHÊ DUYỆT mới cập nhật số liệu công nợ.
     */
    public boolean huyKeHoachVaDieuChinhCongNo(String maKeHoach, String lyDo, double soTienGiamNo, boolean daPheDuyetBoiQuanLy, String maQuanLyDuyet) {
        if (!daPheDuyetBoiQuanLy) {
            return false; // Từ chối cập nhật nếu chưa có sự phê duyệt của Quản lý
        }

        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            // 1. Cập nhật trạng thái kế hoạch niềng răng thành HUY
            String sqlKH = "UPDATE KE_HOACH_NIENG_RANG SET TinhTrang = 'HUY', LyDoTamHoanHuy = ? WHERE MaKeHoach = ?";
            PreparedStatement psKH = conn.prepareStatement(sqlKH);
            psKH.setString(1, lyDo + " (Đã duyệt bởi Quản lý: " + maQuanLyDuyet + ")");
            psKH.setString(2, maKeHoach);
            psKH.executeUpdate();

            // 2. Dừng toàn bộ các lịch tái khám
            String sqlLH = "UPDATE LICH_HEN SET TinhTrang = 'DA_HUY', LyDoHuy = N'Hủy lịch do chấm dứt kế hoạch niềng răng' "
                         + "WHERE MaKeHoach = ? AND NgayHen >= CAST(GETDATE() AS DATE) AND TinhTrang IN ('CHO_KHAM', 'DA_DAT')";
            PreparedStatement psLH = conn.prepareStatement(sqlLH);
            psLH.setString(1, maKeHoach);
            psLH.executeUpdate();

            // 3. Nếu có điều chỉnh/giảm trừ công nợ sau khi Quản lý duyệt
            if (soTienGiamNo > 0) {
                String sqlCN = "UPDATE HO_SO_CONG_NO "
                             + "SET SoDuNoConLai = CASE WHEN SoDuNoConLai >= ? THEN SoDuNoConLai - ? ELSE 0 END, "
                             + "    TinhTrang = CASE WHEN (SoDuNoConLai - ?) <= 0 THEN 'HOAN_TAT' ELSE TinhTrang END "
                             + "WHERE MaHoaDon IN ("
                             + "    SELECT pdt.MaHoaDon FROM PHIEU_DIEU_TRI pdt "
                             + "    JOIN KE_HOACH_NIENG_RANG kh ON pdt.MaPhieuDT = kh.MaPhieuDT "
                             + "    WHERE kh.MaKeHoach = ?"
                             + ")";
                PreparedStatement psCN = conn.prepareStatement(sqlCN);
                psCN.setDouble(1, soTienGiamNo);
                psCN.setDouble(2, soTienGiamNo);
                psCN.setDouble(3, soTienGiamNo);
                psCN.setString(4, maKeHoach);
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
}
