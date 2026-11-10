package com.nhakhoa.model;

import java.util.Date;

public class DonThuoc {
    private String maDonThuoc;
    private String maPhieuKham;
    private Date ngayKe;
    private String loiDanBacSi;

    public DonThuoc() {}

    public DonThuoc(String maDonThuoc, String maPhieuKham, Date ngayKe, String loiDanBacSi) {
        this.maDonThuoc = maDonThuoc;
        this.maPhieuKham = maPhieuKham;
        this.ngayKe = ngayKe;
        this.loiDanBacSi = loiDanBacSi;
    }

    public String getMaDonThuoc() { return maDonThuoc; }
    public void setMaDonThuoc(String maDonThuoc) { this.maDonThuoc = maDonThuoc; }

    public String getMaPhieuKham() { return maPhieuKham; }
    public void setMaPhieuKham(String maPhieuKham) { this.maPhieuKham = maPhieuKham; }

    public Date getNgayKe() { return ngayKe; }
    public void setNgayKe(Date ngayKe) { this.ngayKe = ngayKe; }

    public String getLoiDanBacSi() { return loiDanBacSi; }
    public void setLoiDanBacSi(String loiDanBacSi) { this.loiDanBacSi = loiDanBacSi; }
}
