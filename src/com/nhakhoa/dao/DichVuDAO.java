package com.nhakhoa.dao;

import com.nhakhoa.model.DichVu;
import com.nhakhoa.util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DichVuDAO {

    public List<DichVu> getAll() {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM DICH_VU_NHA_KHOA ORDER BY MaDV ASC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                DichVu dv = new DichVu(
                    rs.getString("MaDV"),
                    rs.getString("TenDV"),
                    rs.getDouble("DonGia"),
                    rs.getString("TinhTrang")
                );
                list.add(dv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String taoMaDVMoi() {
        String sql = "SELECT TOP 1 MaDV FROM DICH_VU_NHA_KHOA ORDER BY MaDV DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1); // DV08
                int num = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("DV%02d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "DV01";
    }

    public boolean them(DichVu dv) {
        String sql = "INSERT INTO DICH_VU_NHA_KHOA (MaDV, TenDV, DonGia, TinhTrang) VALUES (?, ?, ?, 'DANG_AP_DUNG')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dv.getMaDV());
            ps.setString(2, dv.getTenDV());
            ps.setDouble(3, dv.getDonGia());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sua(DichVu dv) {
        String sql = "UPDATE DICH_VU_NHA_KHOA SET TenDV = ?, DonGia = ?, TinhTrang = ? WHERE MaDV = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dv.getTenDV());
            ps.setDouble(2, dv.getDonGia());
            ps.setString(3, dv.getTinhTrang());
            ps.setString(4, dv.getMaDV());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
