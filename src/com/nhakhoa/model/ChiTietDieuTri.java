package com.nhakhoa.model;

public class ChiTietDieuTri {
    private String maPhieuDT;
    private String maDV;
    private String tenDV;
    private String viTriRang;
    private int soLuong;
    private double donGiaApDung;
    private double thanhTien;

    public ChiTietDieuTri() {}

    public ChiTietDieuTri(String maPhieuDT, String maDV, String tenDV, String viTriRang, int soLuong, double donGiaApDung, double thanhTien) {
        this.maPhieuDT = maPhieuDT;
        this.maDV = maDV;
        this.tenDV = tenDV;
        this.viTriRang = viTriRang;
        this.soLuong = soLuong;
        this.donGiaApDung = donGiaApDung;
        this.thanhTien = thanhTien;
    }

    public String getMaPhieuDT() { return maPhieuDT; }
    public void setMaPhieuDT(String maPhieuDT) { this.maPhieuDT = maPhieuDT; }

    public String getMaDV() { return maDV; }
    public void setMaDV(String maDV) { this.maDV = maDV; }

    public String getTenDV() { return tenDV; }
    public void setTenDV(String tenDV) { this.tenDV = tenDV; }

    public String getViTriRang() { return viTriRang; }
    public void setViTriRang(String viTriRang) { this.viTriRang = viTriRang; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGiaApDung() { return donGiaApDung; }
    public void setDonGiaApDung(double donGiaApDung) { this.donGiaApDung = donGiaApDung; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }
}
