package com.nhakhoa.model;

import java.util.Date;

/**
 * Model ánh xạ bảng PHIEU_KHAM
 */
public class PhieuKham {
    private String maPhieuKham;
    private String maLichHen;
    private String maBN;
    private String tenBN;
    private String maBS;
    private String tenBS;
    private Date ngayKham;
    private String trieuChung;
    private String chanDoan;
    private String tinhTrang;

    public PhieuKham() {}

    public PhieuKham(String maPhieuKham, String maLichHen, String maBN, String tenBN, String maBS, String tenBS, Date ngayKham, String trieuChung, String chanDoan, String tinhTrang) {
        this.maPhieuKham = maPhieuKham;
        this.maLichHen = maLichHen;
        this.maBN = maBN;
        this.tenBN = tenBN;
        this.maBS = maBS;
        this.tenBS = tenBS;
        this.ngayKham = ngayKham;
        this.trieuChung = trieuChung;
        this.chanDoan = chanDoan;
        this.tinhTrang = tinhTrang;
    }

    public String getMaPhieuKham() { return maPhieuKham; }
    public void setMaPhieuKham(String maPhieuKham) { this.maPhieuKham = maPhieuKham; }

    public String getMaLichHen() { return maLichHen; }
    public void setMaLichHen(String maLichHen) { this.maLichHen = maLichHen; }

    public String getMaBN() { return maBN; }
    public void setMaBN(String maBN) { this.maBN = maBN; }

    public String getTenBN() { return tenBN; }
    public void setTenBN(String tenBN) { this.tenBN = tenBN; }

    public String getMaBS() { return maBS; }
    public void setMaBS(String maBS) { this.maBS = maBS; }

    public String getTenBS() { return tenBS; }
    public void setTenBS(String tenBS) { this.tenBS = tenBS; }

    public Date getNgayKham() { return ngayKham; }
    public void setNgayKham(Date ngayKham) { this.ngayKham = ngayKham; }

    public String getTrieuChung() { return trieuChung; }
    public void setTrieuChung(String trieuChung) { this.trieuChung = trieuChung; }

    public String getChanDoan() { return chanDoan; }
    public void setChanDoan(String chanDoan) { this.chanDoan = chanDoan; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }
}
