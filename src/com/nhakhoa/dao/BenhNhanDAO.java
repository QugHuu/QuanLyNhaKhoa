package com.nhakhoa.dao;

import com.nhakhoa.model.BenhNhan;
import com.nhakhoa.util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BenhNhanDAO {

    public BenhNhan getById(String maBN) {
        String sql = "SELECT * FROM HO_SO_BENH_NHAN WHERE MaBN = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maBN);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new BenhNhan(
                    rs.getString("MaBN"),
                    rs.getString("HoTen"),
                    rs.getDate("NgaySinh"),
                    rs.getString("SoCCCD"),
                    rs.getString("SoDienThoai"),
                    rs.getString("TienSuBenh"),
                    rs.getString("TinhTrang"),
                    rs.getTimestamp("NgayTao")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BenhNhan> getAll() {
        List<BenhNhan> list = new ArrayList<>();
        String sql = "SELECT * FROM HO_SO_BENH_NHAN ORDER BY MaBN DESC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                BenhNhan bn = new BenhNhan(
                    rs.getString("MaBN"),
                    rs.getString("HoTen"),
                    rs.getDate("NgaySinh"),
                    rs.getString("SoCCCD"),
                    rs.getString("SoDienThoai"),
                    rs.getString("TienSuBenh"),
                    rs.getString("TinhTrang"),
                    rs.getTimestamp("NgayTao")
                );
                list.add(bn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BenhNhan> timKiem(String keyword) {
        List<BenhNhan> list = new ArrayList<>();
        String sql = "SELECT * FROM HO_SO_BENH_NHAN WHERE HoTen LIKE ? OR SoDienThoai LIKE ? OR MaBN LIKE ? ORDER BY MaBN DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BenhNhan bn = new BenhNhan(
                        rs.getString("MaBN"),
                        rs.getString("HoTen"),
                        rs.getDate("NgaySinh"),
                        rs.getString("SoCCCD"),
                        rs.getString("SoDienThoai"),
                        rs.getString("TienSuBenh"),
                        rs.getString("TinhTrang"),
                        rs.getTimestamp("NgayTao")
                    );
                    list.add(bn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String taoMaBNMoi() {
        String sql = "SELECT TOP 1 MaBN FROM HO_SO_BENH_NHAN ORDER BY MaBN DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1); // BN004
                int num = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("BN%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "BN001";
    }

    public boolean them(BenhNhan bn) {
        String sql = "INSERT INTO HO_SO_BENH_NHAN (MaBN, HoTen, NgaySinh, SoCCCD, SoDienThoai, TienSuBenh, TinhTrang, NgayTao) "
                   + "VALUES (?, ?, ?, ?, ?, ?, 'HOAT_DONG', GETDATE())";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bn.getMaBN());
            ps.setString(2, bn.getHoTen());
            if (bn.getNgaySinh() != null) {
                ps.setDate(3, new java.sql.Date(bn.getNgaySinh().getTime()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            ps.setString(4, bn.getSoCCCD());
            ps.setString(5, bn.getSoDienThoai());
            ps.setString(6, bn.getTienSuBenh());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sua(BenhNhan bn) {
        String sql = "UPDATE HO_SO_BENH_NHAN SET HoTen = ?, NgaySinh = ?, SoCCCD = ?, SoDienThoai = ?, TienSuBenh = ? WHERE MaBN = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bn.getHoTen());
            if (bn.getNgaySinh() != null) {
                ps.setDate(2, new java.sql.Date(bn.getNgaySinh().getTime()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            ps.setString(3, bn.getSoCCCD());
            ps.setString(4, bn.getSoDienThoai());
            ps.setString(5, bn.getTienSuBenh());
            ps.setString(6, bn.getMaBN());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoa(String maBN) {
        // Cập nhật tình trạng thành KHOA (Soft delete) để không vi phạm toàn vẹn dữ liệu RB02
        String sql = "UPDATE HO_SO_BENH_NHAN SET TinhTrang = 'KHOA' WHERE MaBN = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maBN);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
