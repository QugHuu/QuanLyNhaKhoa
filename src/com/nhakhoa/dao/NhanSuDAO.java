package com.nhakhoa.dao;

import com.nhakhoa.util.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FR-42: Quản lý nhân sự
 * FR-44: Quản lý thông tin bác sĩ
 * DAO xử lý nhân viên và bác sĩ
 */
public class NhanSuDAO {

    // ===================== NHÂN VIÊN =====================

    public List<String[]> getAllNhanVien() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT nv.MaNhanVien, nv.HoTen, nv.SoDienThoai, nv.Email, "
                   + "       nv.ChucVu, nv.TinhTrang, tk.TenDangNhap "
                   + "FROM NHAN_VIEN nv "
                   + "LEFT JOIN TAI_KHOAN tk ON nv.MaTaiKhoan = tk.MaTaiKhoan "
                   + "WHERE nv.TinhTrang <> 'DA_XOA' "
                   + "ORDER BY nv.HoTen";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("MaNhanVien"),
                    rs.getString("HoTen"),
                    rs.getString("SoDienThoai"),
                    rs.getString("Email"),
                    rs.getString("ChucVu"),
                    rs.getString("TinhTrang"),
                    rs.getString("TenDangNhap")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String taoMaNVMoi() {
        String sql = "SELECT TOP 1 MaNhanVien FROM NHAN_VIEN ORDER BY MaNhanVien DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String last = rs.getString(1); // NV001
                int num = Integer.parseInt(last.substring(2)) + 1;
                return String.format("NV%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NV001";
    }

    public boolean themNhanVien(String maVN, String hoTen, String sdt, String email, String chucVu) {
        String sql = "INSERT INTO NHAN_VIEN (MaNhanVien, HoTen, SoDienThoai, Email, ChucVu, TinhTrang) "
                   + "VALUES (?, N?, ?, ?, N?, 'DANG_LAM')";
        // Dùng setNString để đảm bảo Unicode
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO NHAN_VIEN (MaNhanVien, HoTen, SoDienThoai, Email, ChucVu, TinhTrang) "
               + "VALUES (?, ?, ?, ?, ?, 'DANG_LAM')")) {
            ps.setString(1, maVN);
            ps.setNString(2, hoTen);
            ps.setString(3, sdt);
            ps.setString(4, email);
            ps.setNString(5, chucVu);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhatNhanVien(String maNV, String hoTen, String sdt, String email, String chucVu) {
        String sql = "UPDATE NHAN_VIEN SET HoTen = ?, SoDienThoai = ?, Email = ?, ChucVu = ? WHERE MaNhanVien = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, hoTen);
            ps.setString(2, sdt);
            ps.setString(3, email);
            ps.setNString(4, chucVu);
            ps.setString(5, maNV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Vô hiệu hóa nhân viên (soft delete)
    public boolean khoaNhanVien(String maNV) {
        String sql = "UPDATE NHAN_VIEN SET TinhTrang = 'NGHI_VIEC' WHERE MaNhanVien = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===================== BÁC SĨ =====================

    public List<String[]> getAllBacSi() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT bs.MaBS, bs.HoTen, bs.SoDienThoai, bs.Email, "
                   + "       bs.ChuyenKhoa, bs.HocVi, bs.TinhTrang, tk.TenDangNhap "
                   + "FROM BAC_SI bs "
                   + "LEFT JOIN TAI_KHOAN tk ON bs.MaTaiKhoan = tk.MaTaiKhoan "
                   + "ORDER BY bs.HoTen";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("MaBS"),
                    rs.getString("HoTen"),
                    rs.getString("SoDienThoai"),
                    rs.getString("Email"),
                    rs.getString("ChuyenKhoa"),
                    rs.getString("HocVi"),
                    rs.getString("TinhTrang"),
                    rs.getString("TenDangNhap")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String taoMaBSMoi() {
        String sql = "SELECT TOP 1 MaBS FROM BAC_SI ORDER BY MaBS DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String last = rs.getString(1); // BS001
                int num = Integer.parseInt(last.substring(2)) + 1;
                return String.format("BS%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "BS001";
    }

    public boolean themBacSi(String maBS, String hoTen, String sdt, String email, String chuyenKhoa, String hocVi) {
        String sql = "INSERT INTO BAC_SI (MaBS, HoTen, SoDienThoai, Email, ChuyenKhoa, HocVi, TinhTrang) "
                   + "VALUES (?, ?, ?, ?, ?, ?, 'DANG_LAM')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maBS);
            ps.setNString(2, hoTen);
            ps.setString(3, sdt);
            ps.setString(4, email);
            ps.setNString(5, chuyenKhoa);
            ps.setNString(6, hocVi);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhatBacSi(String maBS, String hoTen, String sdt, String email, String chuyenKhoa, String hocVi) {
        String sql = "UPDATE BAC_SI SET HoTen = ?, SoDienThoai = ?, Email = ?, ChuyenKhoa = ?, HocVi = ? WHERE MaBS = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, hoTen);
            ps.setString(2, sdt);
            ps.setString(3, email);
            ps.setNString(4, chuyenKhoa);
            ps.setNString(5, hocVi);
            ps.setString(6, maBS);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean doiTinhTrangBacSi(String maBS, String tinhTrang) {
        String sql = "UPDATE BAC_SI SET TinhTrang = ? WHERE MaBS = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tinhTrang);
            ps.setString(2, maBS);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

