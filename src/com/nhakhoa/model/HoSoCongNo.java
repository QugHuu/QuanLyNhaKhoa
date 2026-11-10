package com.nhakhoa.model;

/**
 * Model ánh xạ bảng HO_SO_CONG_NO
 */
public class HoSoCongNo {
    private String maCongNo;
    private String maHoaDon;
    private String tenBN;
    private double tongNoPhatSinh;
    private double soDuNoConLai;
    private String tinhTrang; // CON_NO, DA_HOAN_TAT, DIEU_CHINH

    public HoSoCongNo() {}

    public HoSoCongNo(String maCongNo, String maHoaDon, String tenBN, double tongNoPhatSinh, double soDuNoConLai, String tinhTrang) {
        this.maCongNo = maCongNo;
        this.maHoaDon = maHoaDon;
        this.tenBN = tenBN;
        this.tongNoPhatSinh = tongNoPhatSinh;
        this.soDuNoConLai = soDuNoConLai;
        this.tinhTrang = tinhTrang;
    }

    public String getMaCongNo() { return maCongNo; }
    public void setMaCongNo(String maCongNo) { this.maCongNo = maCongNo; }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public String getTenBN() { return tenBN; }
    public void setTenBN(String tenBN) { this.tenBN = tenBN; }

    public double getTongNoPhatSinh() { return tongNoPhatSinh; }
    public void setTongNoPhatSinh(double tongNoPhatSinh) { this.tongNoPhatSinh = tongNoPhatSinh; }

    public double getSoDuNoConLai() { return soDuNoConLai; }
    public void setSoDuNoConLai(double soDuNoConLai) { this.soDuNoConLai = soDuNoConLai; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }
}
