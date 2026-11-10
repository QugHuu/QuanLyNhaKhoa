package com.nhakhoa.model;

import java.util.Date;

/**
 * Model ánh xạ bảng LICH_HEN
 */
public class LichHen {
    private String maLichHen;
    private String maBN;
    private String tenBN;
    private String soDienThoai;
    private String maBS;
    private String tenBS;
    private String maGhe;
    private String maKeHoach;
    private Date ngayHen;
    private String gioBatDau;
    private String gioKetThuc;
    private int soThuTuCho;
    private String tinhTrang; // CHO_KHAM, DA_DEN, DA_KHAM, DA_HUY

    public LichHen() {}

    public LichHen(String maLichHen, String maBN, String tenBN, String soDienThoai, String maBS, String tenBS, String maGhe, String maKeHoach, Date ngayHen, String gioBatDau, String gioKetThuc, int soThuTuCho, String tinhTrang) {
        this.maLichHen = maLichHen;
        this.maBN = maBN;
        this.tenBN = tenBN;
        this.soDienThoai = soDienThoai;
        this.maBS = maBS;
        this.tenBS = tenBS;
        this.maGhe = maGhe;
        this.maKeHoach = maKeHoach;
        this.ngayHen = ngayHen;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.soThuTuCho = soThuTuCho;
        this.tinhTrang = tinhTrang;
    }

    public String getMaLichHen() { return maLichHen; }
    public void setMaLichHen(String maLichHen) { this.maLichHen = maLichHen; }

    public String getMaBN() { return maBN; }
    public void setMaBN(String maBN) { this.maBN = maBN; }

    public String getTenBN() { return tenBN; }
    public void setTenBN(String tenBN) { this.tenBN = tenBN; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getMaBS() { return maBS; }
    public void setMaBS(String maBS) { this.maBS = maBS; }

    public String getTenBS() { return tenBS; }
    public void setTenBS(String tenBS) { this.tenBS = tenBS; }

    public String getMaGhe() { return maGhe; }
    public void setMaGhe(String maGhe) { this.maGhe = maGhe; }

    public String getMaKeHoach() { return maKeHoach; }
    public void setMaKeHoach(String maKeHoach) { this.maKeHoach = maKeHoach; }

    public Date getNgayHen() { return ngayHen; }
    public void setNgayHen(Date ngayHen) { this.ngayHen = ngayHen; }

    public String getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(String gioBatDau) { this.gioBatDau = gioBatDau; }

    public String getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(String gioKetThuc) { this.gioKetThuc = gioKetThuc; }

    public int getSoThuTuCho() { return soThuTuCho; }
    public void setSoThuTuCho(int soThuTuCho) { this.soThuTuCho = soThuTuCho; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    @Override
    public String toString() {
        return maLichHen + " - " + tenBN + " (" + gioBatDau + ")";
    }
}
