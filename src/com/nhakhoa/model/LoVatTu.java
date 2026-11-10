package com.nhakhoa.model;

import java.util.Date;

/**
 * Model ánh xạ bảng LO_VAT_TU (Quản lý kho theo FEFO)
 */
public class LoVatTu {
    private String maLo;
    private String maVatTu;
    private String tenVatTu;
    private String donViTinh;
    private String soLo;
    private Date hanSuDung;
    private int soLuongTon;
    private String tinhTrangLo;

    public LoVatTu() {}

    public LoVatTu(String maLo, String maVatTu, String tenVatTu, String donViTinh, String soLo, Date hanSuDung, int soLuongTon, String tinhTrangLo) {
        this.maLo = maLo;
        this.maVatTu = maVatTu;
        this.tenVatTu = tenVatTu;
        this.donViTinh = donViTinh;
        this.soLo = soLo;
        this.hanSuDung = hanSuDung;
        this.soLuongTon = soLuongTon;
        this.tinhTrangLo = tinhTrangLo;
    }

    public String getMaLo() { return maLo; }
    public void setMaLo(String maLo) { this.maLo = maLo; }

    public String getMaVatTu() { return maVatTu; }
    public void setMaVatTu(String maVatTu) { this.maVatTu = maVatTu; }

    public String getTenVatTu() { return tenVatTu; }
    public void setTenVatTu(String tenVatTu) { this.tenVatTu = tenVatTu; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public String getSoLo() { return soLo; }
    public void setSoLo(String soLo) { this.soLo = soLo; }

    public Date getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(Date hanSuDung) { this.hanSuDung = hanSuDung; }

    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }

    public String getTinhTrangLo() { return tinhTrangLo; }
    public void setTinhTrangLo(String tinhTrangLo) { this.tinhTrangLo = tinhTrangLo; }

    /**
     * Kiểm tra lô vật tư có sắp hết hạn không (trong vòng 30 ngày)
     */
    public boolean isSapHetHan() {
        if (hanSuDung == null) return false;
        long diff = hanSuDung.getTime() - System.currentTimeMillis();
        long days = diff / (1000 * 60 * 60 * 24);
        return days <= 30 && days >= 0;
    }

    /**
     * Kiểm tra lô vật tư đã quá hạn sử dụng chưa
     */
    public boolean isDaHetHan() {
        if (hanSuDung == null) return false;
        return hanSuDung.getTime() < System.currentTimeMillis();
    }
}
