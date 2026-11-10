package com.nhakhoa.model;

import java.util.Date;

/**
 * Model ánh xạ bảng HO_SO_BENH_NHAN
 */
public class BenhNhan {
    private String maBN;
    private String hoTen;
    private Date ngaySinh;
    private String soCCCD;
    private String soDienThoai;
    private String tienSuBenh;
    private String tinhTrang;
    private Date ngayTao;

    public BenhNhan() {}

    public BenhNhan(String maBN, String hoTen, Date ngaySinh, String soCCCD, String soDienThoai, String tienSuBenh, String tinhTrang, Date ngayTao) {
        this.maBN = maBN;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.soCCCD = soCCCD;
        this.soDienThoai = soDienThoai;
        this.tienSuBenh = tienSuBenh;
        this.tinhTrang = tinhTrang;
        this.ngayTao = ngayTao;
    }

    public String getMaBN() { return maBN; }
    public void setMaBN(String maBN) { this.maBN = maBN; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getSoCCCD() { return soCCCD; }
    public void setSoCCCD(String soCCCD) { this.soCCCD = soCCCD; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getTienSuBenh() { return tienSuBenh; }
    public void setTienSuBenh(String tienSuBenh) { this.tienSuBenh = tienSuBenh; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    @Override
    public String toString() {
        return maBN + " - " + hoTen + " (" + soDienThoai + ")";
    }
}
