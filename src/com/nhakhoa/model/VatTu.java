package com.nhakhoa.model;

/**
 * Model ánh xạ bảng VAT_TU
 */
public class VatTu {
    private String maVatTu;
    private String tenVatTu;
    private int mucTonToiThieu;
    private String donViTinh;
    private int tongTon; // Tính tổng tồn từ các lô còn khả dụng

    public VatTu() {}

    public VatTu(String maVatTu, String tenVatTu, int mucTonToiThieu, String donViTinh, int tongTon) {
        this.maVatTu = maVatTu;
        this.tenVatTu = tenVatTu;
        this.mucTonToiThieu = mucTonToiThieu;
        this.donViTinh = donViTinh;
        this.tongTon = tongTon;
    }

    public String getMaVatTu() { return maVatTu; }
    public void setMaVatTu(String maVatTu) { this.maVatTu = maVatTu; }

    public String getTenVatTu() { return tenVatTu; }
    public void setTenVatTu(String tenVatTu) { this.tenVatTu = tenVatTu; }

    public int getMucTonToiThieu() { return mucTonToiThieu; }
    public void setMucTonToiThieu(int mucTonToiThieu) { this.mucTonToiThieu = mucTonToiThieu; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public int getTongTon() { return tongTon; }
    public void setTongTon(int tongTon) { this.tongTon = tongTon; }

    public boolean isCanhBaoTonThap() {
        return tongTon <= mucTonToiThieu;
    }

    @Override
    public String toString() {
        return tenVatTu + " (" + donViTinh + ")";
    }
}
