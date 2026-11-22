package com.nhakhoa.model;

/**
 * Model đại diện Kế hoạch niềng răng (chỉnh nha dài hạn)
 */
public class KeHoachNiengRang {
    private String maKeHoach;
    private String maPhieuDT;
    private String maBN;
    private String tenBN;
    private String loTrinhGiaiDoan;
    private String lyDoTamHoanHuy;
    private String tinhTrang; // DANG_THUC_HIEN, HOAN_THANH, TAM_HOAN, HUY
    private double soDuNo;
    private String maHoaDon;

    public KeHoachNiengRang() {}

    public KeHoachNiengRang(String maKeHoach, String maPhieuDT, String maBN, String tenBN, String loTrinhGiaiDoan, String lyDoTamHoanHuy, String tinhTrang, double soDuNo, String maHoaDon) {
        this.maKeHoach = maKeHoach;
        this.maPhieuDT = maPhieuDT;
        this.maBN = maBN;
        this.tenBN = tenBN;
        this.loTrinhGiaiDoan = loTrinhGiaiDoan;
        this.lyDoTamHoanHuy = lyDoTamHoanHuy;
        this.tinhTrang = tinhTrang;
        this.soDuNo = soDuNo;
        this.maHoaDon = maHoaDon;
    }

    public String getMaKeHoach() { return maKeHoach; }
    public void setMaKeHoach(String maKeHoach) { this.maKeHoach = maKeHoach; }

    public String getMaPhieuDT() { return maPhieuDT; }
    public void setMaPhieuDT(String maPhieuDT) { this.maPhieuDT = maPhieuDT; }

    public String getMaBN() { return maBN; }
    public void setMaBN(String maBN) { this.maBN = maBN; }

    public String getTenBN() { return tenBN; }
    public void setTenBN(String tenBN) { this.tenBN = tenBN; }

    public String getLoTrinhGiaiDoan() { return loTrinhGiaiDoan; }
    public void setLoTrinhGiaiDoan(String loTrinhGiaiDoan) { this.loTrinhGiaiDoan = loTrinhGiaiDoan; }

    public String getLyDoTamHoanHuy() { return lyDoTamHoanHuy; }
    public void setLyDoTamHoanHuy(String lyDoTamHoanHuy) { this.lyDoTamHoanHuy = lyDoTamHoanHuy; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    public double getSoDuNo() { return soDuNo; }
    public void setSoDuNo(double soDuNo) { this.soDuNo = soDuNo; }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
}
