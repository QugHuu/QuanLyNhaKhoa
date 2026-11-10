package com.nhakhoa.model;

import java.util.Date;

/**
 * Model ánh xạ bảng HOA_DON
 */
public class HoaDon {
    private String maHoaDon;
    private String maBN;
    private String tenBN;
    private Date ngayLap;
    private double tongTienDichVu;
    private double tienGiamGia;
    private double tienCocDaTru;
    private double tongCanThanhToan;
    private String tinhTrang; // CHUA_TRA, CON_NO, DA_TRA, HUY

    public HoaDon() {}

    public HoaDon(String maHoaDon, String maBN, String tenBN, Date ngayLap, double tongTienDichVu, double tienGiamGia, double tienCocDaTru, double tongCanThanhToan, String tinhTrang) {
        this.maHoaDon = maHoaDon;
        this.maBN = maBN;
        this.tenBN = tenBN;
        this.ngayLap = ngayLap;
        this.tongTienDichVu = tongTienDichVu;
        this.tienGiamGia = tienGiamGia;
        this.tienCocDaTru = tienCocDaTru;
        this.tongCanThanhToan = tongCanThanhToan;
        this.tinhTrang = tinhTrang;
    }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public String getMaBN() { return maBN; }
    public void setMaBN(String maBN) { this.maBN = maBN; }

    public String getTenBN() { return tenBN; }
    public void setTenBN(String tenBN) { this.tenBN = tenBN; }

    public Date getNgayLap() { return ngayLap; }
    public void setNgayLap(Date ngayLap) { this.ngayLap = ngayLap; }

    public double getTongTienDichVu() { return tongTienDichVu; }
    public void setTongTienDichVu(double tongTienDichVu) { this.tongTienDichVu = tongTienDichVu; }

    public double getTienGiamGia() { return tienGiamGia; }
    public void setTienGiamGia(double tienGiamGia) { this.tienGiamGia = tienGiamGia; }

    public double getTienCocDaTru() { return tienCocDaTru; }
    public void setTienCocDaTru(double tienCocDaTru) { this.tienCocDaTru = tienCocDaTru; }

    public double getTongCanThanhToan() { return tongCanThanhToan; }
    public void setTongCanThanhToan(double tongCanThanhToan) { this.tongCanThanhToan = tongCanThanhToan; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }
}
