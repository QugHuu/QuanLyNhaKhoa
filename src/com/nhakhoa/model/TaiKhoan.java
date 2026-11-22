package com.nhakhoa.model;

/**
 * Model ánh xạ bảng TAI_KHOAN & VAI_TRO
 */
public class TaiKhoan {
    private String maTaiKhoan;
    private String tenDangNhap;
    private String matKhau;
    private int soLanNhapSai;
    private String tinhTrang;
    private String maVaiTro;
    private String tenVaiTro;
    private String hoTen;
    private String maBacSi;

    public TaiKhoan() {}

    public TaiKhoan(String maTaiKhoan, String tenDangNhap, String matKhau, int soLanNhapSai, String tinhTrang, String maVaiTro, String tenVaiTro, String hoTen) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.soLanNhapSai = soLanNhapSai;
        this.tinhTrang = tinhTrang;
        this.maVaiTro = maVaiTro;
        this.tenVaiTro = tenVaiTro;
        this.hoTen = hoTen;
    }

    public String getMaTaiKhoan() { return maTaiKhoan; }
    public void setMaTaiKhoan(String maTaiKhoan) { this.maTaiKhoan = maTaiKhoan; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public int getSoLanNhapSai() { return soLanNhapSai; }
    public void setSoLanNhapSai(int soLanNhapSai) { this.soLanNhapSai = soLanNhapSai; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    public String getMaVaiTro() { return maVaiTro; }
    public void setMaVaiTro(String maVaiTro) { this.maVaiTro = maVaiTro; }

    public String getTenVaiTro() { return tenVaiTro; }
    public void setTenVaiTro(String tenVaiTro) { this.tenVaiTro = tenVaiTro; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getMaBacSi() { return maBacSi; }
    public void setMaBacSi(String maBacSi) { this.maBacSi = maBacSi; }

    public boolean isQuanLy() {
        return "VT_QUANLY".equalsIgnoreCase(maVaiTro);
    }

    public boolean isLetan() {
        return "VT_LETAN".equalsIgnoreCase(maVaiTro);
    }

    public boolean isBacSi() {
        return "VT_BACSI".equalsIgnoreCase(maVaiTro);
    }

    public boolean isKho() {
        return "VT_KHO".equalsIgnoreCase(maVaiTro);
    }
}
