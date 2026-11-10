package com.nhakhoa.model;

/**
 * Model ánh xạ bảng DICH_VU_NHA_KHOA
 */
public class DichVu {
    private String maDV;
    private String tenDV;
    private double donGia;
    private String tinhTrang;

    public DichVu() {}

    public DichVu(String maDV, String tenDV, double donGia, String tinhTrang) {
        this.maDV = maDV;
        this.tenDV = tenDV;
        this.donGia = donGia;
        this.tinhTrang = tinhTrang;
    }

    public String getMaDV() { return maDV; }
    public void setMaDV(String maDV) { this.maDV = maDV; }

    public String getTenDV() { return tenDV; }
    public void setTenDV(String tenDV) { this.tenDV = tenDV; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    @Override
    public String toString() {
        return tenDV;
    }
}
