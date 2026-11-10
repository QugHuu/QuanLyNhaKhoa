package com.nhakhoa.dao;

import com.nhakhoa.model.LichHen;
import com.nhakhoa.util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LichHenDAO {

    public List<LichHen> getAll() {
        List<LichHen> list = new ArrayList<>();
        String sql = "SELECT lh.MaLichHen, lh.MaBN, bn.HoTen AS TenBN, bn.SoDienThoai, "
                   + "       lh.MaBS, bs.HoTen AS TenBS, lh.MaGhe, lh.MaKeHoach, "
                   + "       lh.NgayHen, lh.GioBatDau, lh.GioKetThuc, lh.SoThuTuCho, lh.TinhTrang "
                   + "FROM LICH_HEN lh "
                   + "JOIN HO_SO_BENH_NHAN bn ON lh.MaBN = bn.MaBN "
                   + "JOIN BAC_SI bs ON lh.MaBS = bs.MaBS "
                   + "ORDER BY lh.NgayHen DESC, lh.GioBatDau ASC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                LichHen lh = new LichHen(
                    rs.getString("MaLichHen"),
                    rs.getString("MaBN"),
                    rs.getString("TenBN"),
                    rs.getString("SoDienThoai"),
                    rs.getString("MaBS"),
                    rs.getString("TenBS"),
                    rs.getString("MaGhe"),
                    rs.getString("MaKeHoach"),
                    rs.getDate("NgayHen"),
                    rs.getString("GioBatDau"),
                    rs.getString("GioKetThuc"),
                    rs.getInt("SoThuTuCho"),
                    rs.getString("TinhTrang")
                );
                list.add(lh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<LichHen> getLichHenDaDen() {
        List<LichHen> list = new ArrayList<>();
        String sql = "SELECT lh.MaLichHen, lh.MaBN, bn.HoTen AS TenBN, bn.SoDienThoai, "
                   + "       lh.MaBS, bs.HoTen AS TenBS, lh.MaGhe, lh.MaKeHoach, "
                   + "       lh.NgayHen, lh.GioBatDau, lh.GioKetThuc, lh.SoThuTuCho, lh.TinhTrang "
                   + "FROM LICH_HEN lh "
                   + "JOIN HO_SO_BENH_NHAN bn ON lh.MaBN = bn.MaBN "
                   + "JOIN BAC_SI bs ON lh.MaBS = bs.MaBS "
                   + "WHERE lh.TinhTrang IN ('DA_DEN', 'CHO_KHAM', 'DANG_KHAM') "
                   + "ORDER BY CASE WHEN lh.TinhTrang = 'DA_DEN' THEN 1 ELSE 2 END, lh.SoThuTuCho ASC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                LichHen lh = new LichHen(
                    rs.getString("MaLichHen"),
                    rs.getString("MaBN"),
                    rs.getString("TenBN"),
                    rs.getString("SoDienThoai"),
                    rs.getString("MaBS"),
                    rs.getString("TenBS"),
                    rs.getString("MaGhe"),
                    rs.getString("MaKeHoach"),
                    rs.getDate("NgayHen"),
                    rs.getString("GioBatDau"),
                    rs.getString("GioKetThuc"),
                    rs.getInt("SoThuTuCho"),
                    rs.getString("TinhTrang")
                );
                list.add(lh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String taoMaLHMoi() {
        String sql = "SELECT TOP 1 MaLichHen FROM LICH_HEN ORDER BY MaLichHen DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1); // LH003
                int num = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("LH%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "LH001";
    }

    /**
     * RB07: Chống trùng lịch Bác sĩ hoặc Ghế điều trị trong cùng khoảng thời gian
     */
    public boolean kiemTraTrungLich(String maBS, String maGhe, java.sql.Date ngayHen, String gioBD, String gioKT) {
        String sql = "SELECT COUNT(*) FROM LICH_HEN "
                   + "WHERE NgayHen = ? AND TinhTrang <> 'DA_HUY' "
                   + "  AND (MaBS = ? OR MaGhe = ?) "
                   + "  AND NOT (GioKetThuc <= ? OR GioBatDau >= ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, ngayHen);
            ps.setString(2, maBS);
            ps.setString(3, maGhe);
            ps.setString(4, gioBD);
            ps.setString(5, gioKT);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // > 0 là bị trùng lịch
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean them(LichHen lh) {
        String sql = "INSERT INTO LICH_HEN (MaLichHen, MaBN, MaBS, MaGhe, NgayHen, GioBatDau, GioKetThuc, SoThuTuCho, TinhTrang) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lh.getMaLichHen());
            ps.setString(2, lh.getMaBN());
            ps.setString(3, lh.getMaBS());
            ps.setString(4, lh.getMaGhe());
            ps.setDate(5, new java.sql.Date(lh.getNgayHen().getTime()));
            ps.setString(6, lh.getGioBatDau());
            ps.setString(7, lh.getGioKetThuc());
            ps.setInt(8, lh.getSoThuTuCho());
            ps.setString(9, lh.getTinhTrang());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean doiTrangThai(String maLichHen, String tinhTrang) {
        String sql = "UPDATE LICH_HEN SET TinhTrang = ? WHERE MaLichHen = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tinhTrang);
            ps.setString(2, maLichHen);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String[]> getDanhSachBacSi() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT MaBS, HoTen FROM BAC_SI WHERE TinhTrang = 'DANG_LAM'";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{ rs.getString("MaBS"), rs.getString("HoTen") });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String[]> getDanhSachGhe() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT g.MaGhe, g.SoGhe, p.TenPhong FROM GHE_DIEU_TRI g JOIN PHONG_KHAM p ON g.MaPhong = p.MaPhong WHERE g.TinhTrang = 'HOAT_DONG'";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{ rs.getString("MaGhe"), rs.getString("SoGhe") + " (" + rs.getString("TenPhong") + ")" });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
