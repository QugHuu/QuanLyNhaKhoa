package com.nhakhoa.dao;

import com.nhakhoa.util.DBConnect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {

    public double getTongDoanhThu() {
        String sql = "SELECT COALESCE(SUM(SoTien), 0) FROM PHIEU_THANH_TOAN WHERE LoaiGiaoDich IN ('THU_TIEN', 'TRA_GOP', 'DAT_COC')";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getSoLuongBenhNhan() {
        String sql = "SELECT COUNT(*) FROM HO_SO_BENH_NHAN WHERE TinhTrang = 'HOAT_DONG'";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getSoLuongLichHenHomNay() {
        String sql = "SELECT COUNT(*) FROM LICH_HEN WHERE NgayHen = CAST(GETDATE() AS DATE)";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getSoVatTuCanhBao() {
        // Tổng số lô sắp hết hạn + số vật tư dưới tồn kho tối thiểu
        int count = 0;
        String sql1 = "SELECT COUNT(*) FROM LO_VAT_TU "
                    + "WHERE TinhTrangLo = 'KHA_DUNG' "
                    + "  AND HanSuDung BETWEEN CAST(GETDATE() AS DATE) AND DATEADD(day, 30, CAST(GETDATE() AS DATE))";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql1)) {
            if (rs.next()) {
                count += rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<Object[]> getThongKeDoanhThuTheoDichVu() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT dv.TenDV, COUNT(ct.MaDV) AS SoLanSuDung, SUM(ct.ThanhTien) AS DoanhThu "
                   + "FROM CHI_TIET_DIEU_TRI ct "
                   + "JOIN DICH_VU_NHA_KHOA dv ON ct.MaDV = dv.MaDV "
                   + "GROUP BY dv.TenDV "
                   + "ORDER BY DoanhThu DESC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("TenDV"),
                    rs.getInt("SoLanSuDung"),
                    rs.getDouble("DoanhThu")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
