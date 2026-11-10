package com.nhakhoa.model;

import java.util.Date;

public class PhieuDieuTri {
    private String maPhieuDT;
    private String maPhieuKham;
    private String maHoaDon;
    private Date ngayLap;
    private String ketQuaLamSang;
    private String tinhTrang;

    public PhieuDieuTri() {}

    public PhieuDieuTri(String maPhieuDT, String maPhieuKham, String maHoaDon, Date ngayLap, String ketQuaLamSang, String tinhTrang) {
        this.maPhieuDT = maPhieuDT;
        this.maPhieuKham = maPhieuKham;
        this.maHoaDon = maHoaDon;
        this.ngayLap = ngayLap;
        this.ketQuaLamSang = ketQuaLamSang;
        this.tinhTrang = tinhTrang;
    }

    public String getMaPhieuDT() { return maPhieuDT; }
    public void setMaPhieuDT(String maPhieuDT) { this.maPhieuDT = maPhieuDT; }

    public String getMaPhieuKham() { return maPhieuKham; }
    public void setMaPhieuKham(String maPhieuKham) { this.maPhieuKham = maPhieuKham; }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public Date getNgayLap() { return ngayLap; }
    public void setNgayLap(Date ngayLap) { this.ngayLap = ngayLap; }

    public String getKetQuaLamSang() { return ketQuaLamSang; }
    public void setKetQuaLamSang(String ketQuaLamSang) { this.ketQuaLamSang = ketQuaLamSang; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }
}
