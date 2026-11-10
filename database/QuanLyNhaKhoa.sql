-- ====================================================================
-- HE THONG QUAN LY PHONG KHAM NHA KHOA (NHOM 5 - DH SAI GON)
-- He quan tri CSDL: Microsoft SQL Server 2022
-- ====================================================================

USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = N'QuanLyNhaKhoa')
BEGIN
    ALTER DATABASE QuanLyNhaKhoa SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QuanLyNhaKhoa;
END
GO

CREATE DATABASE QuanLyNhaKhoa COLLATE Vietnamese_CI_AS;
GO

USE QuanLyNhaKhoa;
GO

-- --------------------------------------------------------------------
-- 1. NHOM A: HE THONG VA TAI KHOAN
-- --------------------------------------------------------------------

-- 1.1 VAI_TRO
CREATE TABLE VAI_TRO (
    MaVaiTro VARCHAR(10) PRIMARY KEY,
    TenVaiTro NVARCHAR(50) UNIQUE NOT NULL,
    MoTaQuyen NVARCHAR(500) NOT NULL
);

-- 1.2 TAI_KHOAN
CREATE TABLE TAI_KHOAN (
    MaTaiKhoan VARCHAR(10) PRIMARY KEY,
    TenDangNhap VARCHAR(50) UNIQUE NOT NULL,
    MatKhau VARCHAR(60) NOT NULL,
    SoLanNhapSai INT NOT NULL DEFAULT 0,
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'HOAT_DONG' CHECK (TinhTrang IN ('HOAT_DONG', 'TAM_KHOA')),
    MaVaiTro VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES VAI_TRO(MaVaiTro)
);

-- 1.3 NHAN_VIEN
CREATE TABLE NHAN_VIEN (
    MaNV VARCHAR(10) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(10) NOT NULL,
    Email VARCHAR(100) NULL,
    ChucVu NVARCHAR(50) NOT NULL CHECK (ChucVu IN (N'Quản lý', N'Lễ tân', N'Thu ngân', N'Nhân viên kho')),
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'DANG_LAM' CHECK (TinhTrang IN ('DANG_LAM', 'NGHI_VIEC')),
    MaTaiKhoan VARCHAR(10) UNIQUE NOT NULL FOREIGN KEY REFERENCES TAI_KHOAN(MaTaiKhoan)
);

-- 1.4 BAC_SI
CREATE TABLE BAC_SI (
    MaBS VARCHAR(10) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(10) NOT NULL,
    ChuyenKhoa NVARCHAR(50) NULL,
    SoChungChiHanhNghe VARCHAR(30) UNIQUE NOT NULL,
    HocVi NVARCHAR(50) NULL,
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'DANG_LAM' CHECK (TinhTrang IN ('DANG_LAM', 'NGHI_VIEC')),
    MaTaiKhoan VARCHAR(10) UNIQUE NOT NULL FOREIGN KEY REFERENCES TAI_KHOAN(MaTaiKhoan)
);

-- 1.5 CA_LAM_VIEC
CREATE TABLE CA_LAM_VIEC (
    MaCa VARCHAR(10) PRIMARY KEY,
    MaBS VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES BAC_SI(MaBS),
    NgayLamViec DATE NOT NULL,
    GioBatDau TIME NOT NULL,
    GioKetThuc TIME NOT NULL,
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'DANG_TRUC' CHECK (TinhTrang IN ('DANG_TRUC', 'NGHI_PHEP', 'HOAN_THANH'))
);

-- 1.6 AUDIT_LOG
CREATE TABLE AUDIT_LOG (
    MaLog VARCHAR(15) PRIMARY KEY,
    MaTaiKhoan VARCHAR(10) NULL FOREIGN KEY REFERENCES TAI_KHOAN(MaTaiKhoan),
    ThoiGian DATETIME NOT NULL DEFAULT GETDATE(),
    HanhDong VARCHAR(50) NOT NULL,
    DoiTuongTacDong VARCHAR(50) NOT NULL,
    LyDo NVARCHAR(255) NULL
);

-- --------------------------------------------------------------------
-- 2. NHOM B: QUAN LY BENH NHAN VA LICH HEN
-- --------------------------------------------------------------------

-- 2.1 HO_SO_BENH_NHAN
CREATE TABLE HO_SO_BENH_NHAN (
    MaBN VARCHAR(10) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    NgaySinh DATE NULL,
    SoCCCD VARCHAR(12) NULL,
    SoDienThoai VARCHAR(10) NOT NULL,
    TienSuBenh NVARCHAR(500) NULL,
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'HOAT_DONG' CHECK (TinhTrang IN ('HOAT_DONG', 'KHOA')),
    NgayTao DATETIME NOT NULL DEFAULT GETDATE()
);

-- 2.2 PHONG_KHAM
CREATE TABLE PHONG_KHAM (
    MaPhong VARCHAR(10) PRIMARY KEY,
    TenPhong NVARCHAR(50) NOT NULL,
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'HOAT_DONG' CHECK (TinhTrang IN ('HOAT_DONG', 'BAO_TRI'))
);

-- 2.3 GHE_DIEU_TRI
CREATE TABLE GHE_DIEU_TRI (
    MaGhe VARCHAR(10) PRIMARY KEY,
    SoGhe NVARCHAR(20) NOT NULL,
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'HOAT_DONG' CHECK (TinhTrang IN ('HOAT_DONG', 'BAO_TRI')),
    MaPhong VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES PHONG_KHAM(MaPhong)
);

-- 2.4 KE_HOACH_NIENG_RANG (Khai bao truoc de LICH_HEN tham chieu)
CREATE TABLE KE_HOACH_NIENG_RANG (
    MaKeHoach VARCHAR(10) PRIMARY KEY,
    MaPhieuDT VARCHAR(12) NULL,
    LoTrinhGiaiDoan NVARCHAR(MAX) NULL,
    LyDoTamHoanHuy NVARCHAR(255) NULL,
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'DANG_THUC_HIEN' CHECK (TinhTrang IN ('DANG_THUC_HIEN', 'HOAN_THANH', 'TAM_HOAN', 'HUY'))
);

-- 2.5 LICH_HEN
CREATE TABLE LICH_HEN (
    MaLichHen VARCHAR(12) PRIMARY KEY,
    MaBN VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES HO_SO_BENH_NHAN(MaBN),
    MaBS VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES BAC_SI(MaBS),
    MaGhe VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES GHE_DIEU_TRI(MaGhe),
    MaKeHoach VARCHAR(10) NULL FOREIGN KEY REFERENCES KE_HOACH_NIENG_RANG(MaKeHoach),
    NgayHen DATE NOT NULL,
    GioBatDau TIME NOT NULL,
    GioKetThuc TIME NOT NULL,
    SoThuTuCho INT NOT NULL DEFAULT 0,
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'CHO_KHAM' CHECK (TinhTrang IN ('CHO_KHAM', 'DA_DEN', 'DA_KHAM', 'DA_HUY'))
);

-- --------------------------------------------------------------------
-- 3. NHOM C: KHAM VA DIEU TRI NHA KHOA
-- --------------------------------------------------------------------

-- 3.1 PHIEU_KHAM
CREATE TABLE PHIEU_KHAM (
    MaPhieuKham VARCHAR(12) PRIMARY KEY,
    MaLichHen VARCHAR(12) UNIQUE NULL FOREIGN KEY REFERENCES LICH_HEN(MaLichHen),
    MaBN VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES HO_SO_BENH_NHAN(MaBN),
    MaBS VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES BAC_SI(MaBS),
    NgayKham DATETIME NOT NULL DEFAULT GETDATE(),
    TrieuChung NVARCHAR(500) NULL,
    ChanDoan NVARCHAR(500) NULL,
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'HOAN_THANH' CHECK (TinhTrang IN ('CHO_XU_LY', 'HOAN_THANH'))
);

-- 3.2 TEP_HINH_ANH
CREATE TABLE TEP_HINH_ANH (
    MaHinhAnh VARCHAR(12) PRIMARY KEY,
    MaPhieuKham VARCHAR(12) NOT NULL FOREIGN KEY REFERENCES PHIEU_KHAM(MaPhieuKham),
    DuongDanTep VARCHAR(255) NOT NULL,
    GhiChuChuyenMon NVARCHAR(500) NULL
);

-- 3.3 DON_THUOC
CREATE TABLE DON_THUOC (
    MaDonThuoc VARCHAR(12) PRIMARY KEY,
    MaPhieuKham VARCHAR(12) UNIQUE NOT NULL FOREIGN KEY REFERENCES PHIEU_KHAM(MaPhieuKham),
    NgayKe DATETIME NOT NULL DEFAULT GETDATE(),
    LoiDanBacSi NVARCHAR(500) NULL
);

-- 3.4 CHI_TIET_DON_THUOC
CREATE TABLE CHI_TIET_DON_THUOC (
    MaDonThuoc VARCHAR(12) NOT NULL FOREIGN KEY REFERENCES DON_THUOC(MaDonThuoc),
    TenThuoc NVARCHAR(100) NOT NULL,
    DonViTinh NVARCHAR(20) NOT NULL,
    SoLuong INT NOT NULL CHECK (SoLuong > 0),
    LieuDung NVARCHAR(255) NOT NULL,
    PRIMARY KEY (MaDonThuoc, TenThuoc)
);

-- 3.5 DICH_VU_NHA_KHOA
CREATE TABLE DICH_VU_NHA_KHOA (
    MaDV VARCHAR(10) PRIMARY KEY,
    TenDV NVARCHAR(150) NOT NULL,
    DonGia FLOAT NOT NULL CHECK (DonGia >= 0),
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'DANG_AP_DUNG' CHECK (TinhTrang IN ('DANG_AP_DUNG', 'NGUNG_AP_DUNG'))
);

-- 3.6 HOA_DON (Khai bao truoc de PHIEU_DIEU_TRI tham chieu)
CREATE TABLE HOA_DON (
    MaHoaDon VARCHAR(12) PRIMARY KEY,
    MaBN VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES HO_SO_BENH_NHAN(MaBN),
    NgayLap DATETIME NOT NULL DEFAULT GETDATE(),
    TongTienDichVu FLOAT NOT NULL DEFAULT 0,
    TienGiamGia FLOAT NOT NULL DEFAULT 0,
    TienCocDaTru FLOAT NOT NULL DEFAULT 0,
    TongCanThanhToan FLOAT NOT NULL DEFAULT 0,
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'CHUA_TRA' CHECK (TinhTrang IN ('CHUA_TRA', 'CON_NO', 'DA_TRA', 'HUY'))
);

-- 3.7 PHIEU_DIEU_TRI
CREATE TABLE PHIEU_DIEU_TRI (
    MaPhieuDT VARCHAR(12) PRIMARY KEY,
    MaPhieuKham VARCHAR(12) NOT NULL FOREIGN KEY REFERENCES PHIEU_KHAM(MaPhieuKham),
    MaHoaDon VARCHAR(12) NULL FOREIGN KEY REFERENCES HOA_DON(MaHoaDon),
    NgayLap DATETIME NOT NULL DEFAULT GETDATE(),
    KetQuaLamSang NVARCHAR(500) NULL,
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'HOAN_THANH' CHECK (TinhTrang IN ('DANG_DIEU_TRI', 'HOAN_THANH', 'HUY'))
);

-- Cap nhat FK cho KE_HOACH_NIENG_RANG
ALTER TABLE KE_HOACH_NIENG_RANG ADD CONSTRAINT FK_KH_PDT FOREIGN KEY (MaPhieuDT) REFERENCES PHIEU_DIEU_TRI(MaPhieuDT);

-- 3.8 CHI_TIET_DIEU_TRI
CREATE TABLE CHI_TIET_DIEU_TRI (
    MaPhieuDT VARCHAR(12) NOT NULL FOREIGN KEY REFERENCES PHIEU_DIEU_TRI(MaPhieuDT),
    MaDV VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES DICH_VU_NHA_KHOA(MaDV),
    ViTriRang NVARCHAR(50) NULL,
    SoLuong INT NOT NULL DEFAULT 1 CHECK (SoLuong > 0),
    DonGiaApDung FLOAT NOT NULL CHECK (DonGiaApDung >= 0),
    ThanhTien FLOAT NOT NULL CHECK (ThanhTien >= 0),
    PRIMARY KEY (MaPhieuDT, MaDV)
);

-- --------------------------------------------------------------------
-- 4. NHOM D: QUAN LY KHO VA VAT TU FEFO
-- --------------------------------------------------------------------

-- 4.1 VAT_TU
CREATE TABLE VAT_TU (
    MaVatTu VARCHAR(10) PRIMARY KEY,
    TenVatTu NVARCHAR(150) NOT NULL,
    MucTonToiThieu INT NOT NULL DEFAULT 10 CHECK (MucTonToiThieu >= 0),
    DonViTinh NVARCHAR(20) NOT NULL
);

-- 4.2 DINH_MUC_VAT_TU
CREATE TABLE DINH_MUC_VAT_TU (
    MaDV VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES DICH_VU_NHA_KHOA(MaDV),
    MaVatTu VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES VAT_TU(MaVatTu),
    SoLuongTieuHao FLOAT NOT NULL DEFAULT 1 CHECK (SoLuongTieuHao > 0),
    PRIMARY KEY (MaDV, MaVatTu)
);

-- 4.3 LO_VAT_TU
CREATE TABLE LO_VAT_TU (
    MaLo VARCHAR(12) PRIMARY KEY,
    MaVatTu VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES VAT_TU(MaVatTu),
    SoLo VARCHAR(50) NOT NULL,
    HanSuDung DATE NOT NULL,
    SoLuongTon INT NOT NULL DEFAULT 0 CHECK (SoLuongTon >= 0),
    TinhTrangLo VARCHAR(20) NOT NULL DEFAULT 'KHA_DUNG' CHECK (TinhTrangLo IN ('KHA_DUNG', 'HET_HAN', 'HET_HANG'))
);

-- 4.4 PHIEU_NHAP_KHO
CREATE TABLE PHIEU_NHAP_KHO (
    MaPhieuNhap VARCHAR(12) PRIMARY KEY,
    MaTaiKhoan VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES TAI_KHOAN(MaTaiKhoan),
    NgayChungTu DATETIME NOT NULL DEFAULT GETDATE()
);

-- 4.5 CHI_TIET_NHAP_KHO
CREATE TABLE CHI_TIET_NHAP_KHO (
    MaPhieuNhap VARCHAR(12) NOT NULL FOREIGN KEY REFERENCES PHIEU_NHAP_KHO(MaPhieuNhap),
    MaLo VARCHAR(12) NOT NULL FOREIGN KEY REFERENCES LO_VAT_TU(MaLo),
    SoLuongNhap INT NOT NULL CHECK (SoLuongNhap > 0),
    PRIMARY KEY (MaPhieuNhap, MaLo)
);

-- 4.6 PHIEU_XUAT_KHO
CREATE TABLE PHIEU_XUAT_KHO (
    MaPhieuXuat VARCHAR(12) PRIMARY KEY,
    MaTaiKhoan VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES TAI_KHOAN(MaTaiKhoan),
    MaPhieuDT VARCHAR(12) NULL FOREIGN KEY REFERENCES PHIEU_DIEU_TRI(MaPhieuDT),
    NgayChungTu DATETIME NOT NULL DEFAULT GETDATE(),
    LoaiXuat VARCHAR(20) NOT NULL DEFAULT 'TU_DONG_FEFO' CHECK (LoaiXuat IN ('TU_DONG_FEFO', 'THU_CONG'))
);

-- 4.7 CHI_TIET_XUAT_KHO
CREATE TABLE CHI_TIET_XUAT_KHO (
    MaPhieuXuat VARCHAR(12) NOT NULL FOREIGN KEY REFERENCES PHIEU_XUAT_KHO(MaPhieuXuat),
    MaLo VARCHAR(12) NOT NULL FOREIGN KEY REFERENCES LO_VAT_TU(MaLo),
    SoLuongXuat INT NOT NULL CHECK (SoLuongXuat > 0),
    PRIMARY KEY (MaPhieuXuat, MaLo)
);

-- --------------------------------------------------------------------
-- 5. NHOM E: THANH TOAN VA CONG NO
-- --------------------------------------------------------------------

-- 5.1 HO_SO_CONG_NO
CREATE TABLE HO_SO_CONG_NO (
    MaCongNo VARCHAR(12) PRIMARY KEY,
    MaHoaDon VARCHAR(12) UNIQUE NOT NULL FOREIGN KEY REFERENCES HOA_DON(MaHoaDon),
    TongNoPhatSinh FLOAT NOT NULL CHECK (TongNoPhatSinh >= 0),
    SoDuNoConLai FLOAT NOT NULL CHECK (SoDuNoConLai >= 0),
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'CON_NO' CHECK (TinhTrang IN ('CON_NO', 'DA_HOAN_TAT', 'DIEU_CHINH'))
);

-- 5.2 PHIEU_THANH_TOAN
CREATE TABLE PHIEU_THANH_TOAN (
    MaPhieuTT VARCHAR(12) PRIMARY KEY,
    MaHoaDon VARCHAR(12) NOT NULL FOREIGN KEY REFERENCES HOA_DON(MaHoaDon),
    MaCongNo VARCHAR(12) NULL FOREIGN KEY REFERENCES HO_SO_CONG_NO(MaCongNo),
    NgayThanhToan DATETIME NOT NULL DEFAULT GETDATE(),
    LoaiGiaoDich VARCHAR(20) NOT NULL DEFAULT 'THU_TIEN' CHECK (LoaiGiaoDich IN ('THU_TIEN', 'DAT_COC', 'TRA_GOP', 'HOAN_TIEN')),
    SoTien FLOAT NOT NULL CHECK (SoTien > 0),
    PhuongThuc VARCHAR(20) NOT NULL DEFAULT 'TIEN_MAT' CHECK (PhuongThuc IN ('TIEN_MAT', 'CHUYEN_KHOAN'))
);

-- --------------------------------------------------------------------
-- DU LIEU MAU KHOI TAO HE THONG (SEED DATA)
-- --------------------------------------------------------------------

-- 1. VAI TRO
INSERT INTO VAI_TRO (MaVaiTro, TenVaiTro, MoTaQuyen) VALUES
('VT_QUANLY', N'Quản lý phòng khám', N'Toàn quyền hệ thống, xem báo cáo, phân quyền, duyệt hủy hóa đơn'),
('VT_LETAN',  N'Lễ tân / Thu ngân',   N'Tiếp đón bệnh nhân, đặt lịch hẹn, lập hóa đơn, thu tiền'),
('VT_BACSI',  N'Bác sĩ điều trị',     N'Khám bệnh, chỉ định dịch vụ, điều trị, kê đơn thuốc'),
('VT_KHO',    N'Thủ kho / Vật tư',    N'Quản lý danh mục vật tư, nhập/xuất kho, theo dõi hạn dùng FEFO');

-- 2. TAI KHOAN (Mat khau mac dinh de sinh vien de dang nhap: 123456)
INSERT INTO TAI_KHOAN (MaTaiKhoan, TenDangNhap, MatKhau, SoLanNhapSai, TinhTrang, MaVaiTro) VALUES
('TK001', 'admin',  '123456', 0, 'HOAT_DONG', 'VT_QUANLY'),
('TK002', 'letan',   '123456', 0, 'HOAT_DONG', 'VT_LETAN'),
('TK003', 'bacsi',   '123456', 0, 'HOAT_DONG', 'VT_BACSI'),
('TK004', 'thukho',  '123456', 0, 'HOAT_DONG', 'VT_KHO'),
('TK005', 'bs_mai',  '123456', 0, 'HOAT_DONG', 'VT_BACSI');

-- 3. NHAN VIEN
INSERT INTO NHAN_VIEN (MaNV, HoTen, SoDienThoai, Email, ChucVu, TinhTrang, MaTaiKhoan) VALUES
('NV001', N'Trần Quang Hữu', '0901234567', 'huu.quanly@nhakhoa.vn', N'Quản lý', 'DANG_LAM', 'TK001'),
('NV002', N'Nguyễn Thị Mai', '0912345678', 'mai.letan@nhakhoa.vn',  N'Lễ tân',  'DANG_LAM', 'TK002'),
('NV003', N'Lê Văn Khoa',    '0923456789', 'khoa.kho@nhakhoa.vn',   N'Nhân viên kho', 'DANG_LAM', 'TK004');

-- 4. BAC SI
INSERT INTO BAC_SI (MaBS, HoTen, SoDienThoai, ChuyenKhoa, SoChungChiHanhNghe, HocVi, TinhTrang, MaTaiKhoan) VALUES
('BS001', N'BS.CKI Nguyễn Văn Hùng', '0934567890', N'Răng Hàm Mặt', 'CCHN-00123/HCM', N'Bác sĩ Chuyên khoa I', 'DANG_LAM', 'TK003'),
('BS002', N'ThS.BS Lê Thu Trang',     '0945678901', N'Chỉnh nha Niềng răng', 'CCHN-00456/HCM', N'Thạc sĩ Bác sĩ', 'DANG_LAM', 'TK005');

-- 5. CA LAM VIEC
INSERT INTO CA_LAM_VIEC (MaCa, MaBS, NgayLamViec, GioBatDau, GioKetThuc, TinhTrang) VALUES
('CA001', 'BS001', CAST(GETDATE() AS DATE), '08:00:00', '12:00:00', 'DANG_TRUC'),
('CA002', 'BS001', CAST(GETDATE() AS DATE), '13:30:00', '17:30:00', 'DANG_TRUC'),
('CA003', 'BS002', CAST(GETDATE() AS DATE), '08:00:00', '16:30:00', 'DANG_TRUC');

-- 6. PHONG KHAM VA GHE DIEU TRI
INSERT INTO PHONG_KHAM (MaPhong, TenPhong, TinhTrang) VALUES
('P101', N'Phòng Khám Tổng Quát 1', 'HOAT_DONG'),
('P102', N'Phòng Tiểu Phẫu & Implant', 'HOAT_DONG'),
('P103', N'Phòng Chỉnh Nha - Niềng Răng', 'HOAT_DONG');

INSERT INTO GHE_DIEU_TRI (MaGhe, SoGhe, TinhTrang, MaPhong) VALUES
('GHE01', N'Ghế Nha Số 1', 'HOAT_DONG', 'P101'),
('GHE02', N'Ghế Nha Số 2', 'HOAT_DONG', 'P101'),
('GHE03', N'Ghế Phẫu Thuật', 'HOAT_DONG', 'P102'),
('GHE04', N'Ghế Chỉnh Nha', 'HOAT_DONG', 'P103');

-- 7. BENH NHAN
INSERT INTO HO_SO_BENH_NHAN (MaBN, HoTen, NgaySinh, SoCCCD, SoDienThoai, TienSuBenh, TinhTrang) VALUES
('BN001', N'Phạm Minh Tuấn', '1995-04-12', '079095001234', '0911223344', N'Không có tiền sử dị ứng, tim mạch bình thường', 'HOAT_DONG'),
('BN002', N'Võ Hoàng Yến',   '2001-08-25', '079201005678', '0988776655', N'Dị ứng Penicillin nhẹ', 'HOAT_DONG'),
('BN003', N'Đỗ Quốc Bảo',    '1988-11-03', '079088009876', '0977123456', N'Tiền sử cao huyết áp nhẹ', 'HOAT_DONG'),
('BN004', N'Trần Thị Cẩm Vân', '1999-02-14', '079199003322', '0933445566', N'Sức khỏe tốt', 'HOAT_DONG');

-- 8. DICH VU NHA KHOA
INSERT INTO DICH_VU_NHA_KHOA (MaDV, TenDV, DonGia, TinhTrang) VALUES
('DV01', N'Khám tổng quát & Tư vấn',       50000,   'DANG_AP_DUNG'),
('DV02', N'Cạo vôi răng & Đánh bóng (2 hàm)', 250000,  'DANG_AP_DUNG'),
('DV03', N'Trám răng thẩm mỹ Composite',    350000,  'DANG_AP_DUNG'),
('DV04', N'Điều trị tủy răng (1 ống tủy)',   800000,  'DANG_AP_DUNG'),
('DV05', N'Nhổ răng khôn (tiểu phẫu hàm dưới)', 1500000, 'DANG_AP_DUNG'),
('DV06', N'Tẩy trắng răng tại phòng khám',  1800000, 'DANG_AP_DUNG'),
('DV07', N'Niềng răng mắc cài kim loại',    30000000, 'DANG_AP_DUNG'),
('DV08', N'Chụp phim X-Quang Panorama',     150000,  'DANG_AP_DUNG');

-- 9. VAT TU NHA KHOA
INSERT INTO VAT_TU (MaVatTu, TenVatTu, MucTonToiThieu, DonViTinh) VALUES
('VT01', N'Găng tay y tế Latex (hộp 50 đôi)', 10, N'Hộp'),
('VT02', N'Khẩu trang y tế 4 lớp (hộp 50 cái)', 10, N'Hộp'),
('VT03', N'Thuốc tê tiêm Lidocaine 2%', 20, N'Ống'),
('VT04', N'Bông gòn cuộn nha khoa', 15, N'Gói'),
('VT05', N'Composite đặc trám răng 3M Z250', 5, N'Tuýp'),
('VT06', N'Mắc cài kim loại 3M (bộ)', 3, N'Bộ'),
('VT07', N'Thuốc tẩy trắng Pola Office 35%', 5, N'Tuýp'),
('VT08', N'Chỉ phẫu thuật tự tiêu 4.0', 10, N'Tép');

-- 10. DINH MUC VAT TU (Moi dich vu tieu hao bao nhieu vat tu)
INSERT INTO DINH_MUC_VAT_TU (MaDV, MaVatTu, SoLuongTieuHao) VALUES
('DV02', 'VT01', 1), -- Cao voi rang dung 1 doi gang tay
('DV02', 'VT02', 1), -- 1 khau trang
('DV02', 'VT04', 1), -- 1 cuon bong gon
('DV03', 'VT01', 1),
('DV03', 'VT03', 1), -- Tram rang co the gay te
('DV03', 'VT05', 0.2), -- 0.2 tuyp composite
('DV05', 'VT01', 2), -- Nho rang khon dung 2 doi gang tay
('DV05', 'VT03', 2), -- 2 ong thuoc te
('DV05', 'VT08', 1); -- 1 tep chi tieu

-- 11. LO VAT TU (Du lieu FEFO - Co lo het han truoc, lo het han sau)
INSERT INTO LO_VAT_TU (MaLo, MaVatTu, SoLo, HanSuDung, SoLuongTon, TinhTrangLo) VALUES
('LO001', 'VT01', 'LOT-GT-202501', '2026-12-31', 50, 'KHA_DUNG'),
('LO002', 'VT01', 'LOT-GT-202606', '2027-06-30', 80, 'KHA_DUNG'),
('LO003', 'VT03', 'LOT-TE-202410', '2026-11-30', 25, 'KHA_DUNG'), -- Han rat gan (can date!)
('LO004', 'VT03', 'LOT-TE-202505', '2027-05-15', 60, 'KHA_DUNG'),
('LO005', 'VT05', 'LOT-CP-202502', '2027-02-28', 12, 'KHA_DUNG'),
('LO006', 'VT06', 'LOT-MC-202508', '2028-08-30', 8,  'KHA_DUNG'),
('LO007', 'VT08', 'LOT-CH-202503', '2027-03-31', 30, 'KHA_DUNG'),
('LO008', 'VT02', 'LOT-KT-202401', '2025-01-01', 0,  'HET_HANG');

-- 12. LICH HEN MAU
INSERT INTO LICH_HEN (MaLichHen, MaBN, MaBS, MaGhe, MaKeHoach, NgayHen, GioBatDau, GioKetThuc, SoThuTuCho, TinhTrang) VALUES
('LH001', 'BN001', 'BS001', 'GHE01', NULL, CAST(GETDATE() AS DATE), '08:30:00', '09:15:00', 1, 'DA_KHAM'),
('LH002', 'BN002', 'BS001', 'GHE02', NULL, CAST(GETDATE() AS DATE), '09:30:00', '10:15:00', 2, 'DA_DEN'),
('LH003', 'BN003', 'BS002', 'GHE04', NULL, CAST(GETDATE() AS DATE), '10:30:00', '11:30:00', 3, 'CHO_KHAM');

-- 13. PHIEU KHAM MAU
INSERT INTO PHIEU_KHAM (MaPhieuKham, MaLichHen, MaBN, MaBS, NgayKham, TrieuChung, ChanDoan, TinhTrang) VALUES
('PK001', 'LH001', 'BN001', 'BS001', GETDATE(), N'Đau nhức răng hàm dưới khi ăn nhai đồ lạnh', N'Sâu răng số 36 độ 2, viêm nướu nhẹ viền chân răng', 'HOAN_THANH');

-- 14. PHIEU DIEU TRI VA CHI TIET DIEU TRI
INSERT INTO HOA_DON (MaHoaDon, MaBN, NgayLap, TongTienDichVu, TienGiamGia, TienCocDaTru, TongCanThanhToan, TinhTrang) VALUES
('HD001', 'BN001', GETDATE(), 600000, 50000, 0, 550000, 'DA_TRA');

INSERT INTO PHIEU_DIEU_TRI (MaPhieuDT, MaPhieuKham, MaHoaDon, NgayLap, KetQuaLamSang, TinhTrang) VALUES
('PDT001', 'PK001', 'HD001', GETDATE(), N'Đã cạo vôi 2 hàm và trám bít xoang sâu R36 bằng Composite 3M Z250. Khớp cắn êm.', 'HOAN_THANH');

INSERT INTO CHI_TIET_DIEU_TRI (MaPhieuDT, MaDV, ViTriRang, SoLuong, DonGiaApDung, ThanhTien) VALUES
('PDT001', 'DV02', N'Hai hàm', 1, 250000, 250000),
('PDT001', 'DV03', N'Răng 36', 1, 350000, 350000);

-- 15. PHIEU THANH TOAN
INSERT INTO PHIEU_THANH_TOAN (MaPhieuTT, MaHoaDon, MaCongNo, NgayThanhToan, LoaiGiaoDich, SoTien, PhuongThuc) VALUES
('PTT001', 'HD001', NULL, GETDATE(), 'THU_TIEN', 550000, 'CHUYEN_KHOAN');

-- 16. DON THUOC VA CHI TIET DON THUOC
INSERT INTO DON_THUOC (MaDonThuoc, MaPhieuKham, NgayKe, LoiDanBacSi) VALUES
('DT001', 'PK001', GETDATE(), N'Uống thuốc sau khi ăn no, không nhai đồ cứng vào vùng răng vừa trám trong 24h.');

INSERT INTO CHI_TIET_DON_THUOC (MaDonThuoc, TenThuoc, DonViTinh, SoLuong, LieuDung) VALUES
('DT001', N'Paracetamol 500mg', N'Viên', 10, N'Uống 1 viên khi đau nhức, cách nhau 6 tiếng'),
('DT001', N'Nước súc miệng Kin Gingival', N'Chai', 1, N'Súc miệng ngày 2 lần sáng và tối');

PRINT N'=== KHOI TAO CSDL QUAN LY NHA KHOA THANH CONG! ===';
