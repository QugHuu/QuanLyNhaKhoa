package com.nhakhoa.dao;

import com.nhakhoa.model.TaiKhoan;
import com.nhakhoa.util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Xử lý truy xuất tài khoản người dùng
 */
public class TaiKhoanDAO {

    public TaiKhoan dangNhap(String username, String password) {
        String sql = "SELECT tk.MaTaiKhoan, tk.TenDangNhap, tk.MatKhau, tk.SoLanNhapSai, tk.TinhTrang, "
                   + "       tk.MaVaiTro, vt.TenVaiTro, "
                   + "       COALESCE(nv.HoTen, bs.HoTen, N'Người dùng') AS HoTen "
                   + "FROM TAI_KHOAN tk "
                   + "JOIN VAI_TRO vt ON tk.MaVaiTro = vt.MaVaiTro "
                   + "LEFT JOIN NHAN_VIEN nv ON tk.MaTaiKhoan = nv.MaTaiKhoan "
                   + "LEFT JOIN BAC_SI bs ON tk.MaTaiKhoan = bs.MaTaiKhoan "
                   + "WHERE tk.TenDangNhap = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection();
            if (conn == null) return null;

            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                String maTK = rs.getString("MaTaiKhoan");
                String passInDB = rs.getString("MatKhau");
                int soLanSai = rs.getInt("SoLanNhapSai");
                String tinhTrang = rs.getString("TinhTrang");
                String maVaiTro = rs.getString("MaVaiTro");
                String tenVaiTro = rs.getString("TenVaiTro");
                String hoTen = rs.getString("HoTen");

                TaiKhoan tk = new TaiKhoan(maTK, username, passInDB, soLanSai, tinhTrang, maVaiTro, tenVaiTro, hoTen);

                // Kiểm tra tài khoản đã bị khóa chưa
                if ("TAM_KHOA".equalsIgnoreCase(tinhTrang)) {
                    return tk; // Sẽ xử lý thông báo tài khoản bị khóa ở BUS
                }

                // Kiểm tra mật khẩu
                if (password.equals(passInDB)) {
                    // Đăng nhập thành công -> Reset số lần nhập sai về 0
                    resetSoLanNhapSai(maTK);
                    tk.setSoLanNhapSai(0);
                    return tk;
                } else {
                    // Nhập sai -> Tăng số lần nhập sai
                    soLanSai++;
                    tangSoLanNhapSai(maTK, soLanSai);
                    tk.setSoLanNhapSai(soLanSai);
                    if (soLanSai >= 3) {
                        khoaTaiKhoan(maTK);
                        tk.setTinhTrang("TAM_KHOA");
                    }
                    return tk;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnect.close(conn);
        }
        return null; // Không tìm thấy tên đăng nhập
    }

    public void resetSoLanNhapSai(String maTK) {
        String sql = "UPDATE TAI_KHOAN SET SoLanNhapSai = 0 WHERE MaTaiKhoan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTK);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tangSoLanNhapSai(String maTK, int soLan) {
        String sql = "UPDATE TAI_KHOAN SET SoLanNhapSai = ? WHERE MaTaiKhoan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLan);
            ps.setString(2, maTK);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void khoaTaiKhoan(String maTK) {
        String sql = "UPDATE TAI_KHOAN SET TinhTrang = 'TAM_KHOA' WHERE MaTaiKhoan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTK);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean moKhoaTaiKhoan(String maTK) {
        String sql = "UPDATE TAI_KHOAN SET TinhTrang = 'HOAT_DONG', SoLanNhapSai = 0 WHERE MaTaiKhoan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTK);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
