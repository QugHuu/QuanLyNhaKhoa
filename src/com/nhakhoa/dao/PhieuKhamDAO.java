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
