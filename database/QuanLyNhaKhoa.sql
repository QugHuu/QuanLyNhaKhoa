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
    MaTaiKhoan VARCHAR(10) UNIQUE NULL FOREIGN KEY REFERENCES TAI_KHOAN(MaTaiKhoan)
);

-- 1.4 BAC_SI
CREATE TABLE BAC_SI (
    MaBS VARCHAR(10) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(10) NOT NULL,
    Email VARCHAR(100) NULL,
    ChuyenKhoa NVARCHAR(50) NULL,
    SoChungChiHanhNghe VARCHAR(30) UNIQUE NOT NULL,
    HocVi NVARCHAR(50) NULL,
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'DANG_LAM' CHECK (TinhTrang IN ('DANG_LAM', 'NGHI_PHEP', 'NGHI_VIEC')),
    MaTaiKhoan VARCHAR(10) UNIQUE NULL FOREIGN KEY REFERENCES TAI_KHOAN(MaTaiKhoan)
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
    SoGhe NVARCHAR(50) NOT NULL,
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
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'CHO_KHAM' CHECK (TinhTrang IN ('CHO_KHAM', 'DA_DAT', 'DA_DEN', 'DANG_KHAM', 'DA_KHAM', 'HOAN_TAT', 'DA_HUY')),
    LyDoKham NVARCHAR(255) NULL,
    LyDoHuy NVARCHAR(255) NULL
);

-- --------------------------------------------------------------------
-- 3. NHOM C: KHAM VA DIEU TRI NHA KHOA
-- --------------------------------------------------------------------

-- 3.1 PHIEU_KHAM
CREATE TABLE PHIEU_KHAM (
    MaPhieuKham VARCHAR(12) PRIMARY KEY,
    MaLichHen VARCHAR(12) NULL FOREIGN KEY REFERENCES LICH_HEN(MaLichHen),
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
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'CHUA_TRA' CHECK (TinhTrang IN ('CHUA_TRA', 'CON_NO', 'DANG_NO', 'DA_TRA', 'HUY', 'DA_HUY'))
);

-- 3.7 PHIEU_DIEU_TRI
CREATE TABLE PHIEU_DIEU_TRI (
    MaPhieuDT VARCHAR(12) PRIMARY KEY,
    MaPhieuKham VARCHAR(12) NOT NULL FOREIGN KEY REFERENCES PHIEU_KHAM(MaPhieuKham),
    MaHoaDon VARCHAR(12) NULL FOREIGN KEY REFERENCES HOA_DON(MaHoaDon),
    NgayLap DATETIME NOT NULL DEFAULT GETDATE(),
    KetQuaLamSang NVARCHAR(500) NULL,
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'HOAN_THANH' CHECK (TinhTrang IN ('CHO', 'DANG_LAM', 'DANG_DIEU_TRI', 'HOAN_THANH', 'DA_HUY', 'HUY'))
);

-- Cap nhat FK cho KE_HOACH_NIENG_RANG
ALTER TABLE KE_HOACH_NIENG_RANG ADD CONSTRAINT FK_KH_PDT FOREIGN KEY (MaPhieuDT) REFERENCES PHIEU_DIEU_TRI(MaPhieuDT);

-- 3.8 CHI_TIET_DIEU_TRI
CREATE TABLE CHI_TIET_DIEU_TRI (
    MaCTDT INT IDENTITY(1,1) PRIMARY KEY,
    MaPhieuDT VARCHAR(12) NOT NULL FOREIGN KEY REFERENCES PHIEU_DIEU_TRI(MaPhieuDT),
    MaDV VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES DICH_VU_NHA_KHOA(MaDV),
    ViTriRang NVARCHAR(50) NULL,
    SoLuong INT NOT NULL DEFAULT 1 CHECK (SoLuong > 0),
    DonGiaApDung FLOAT NOT NULL CHECK (DonGiaApDung >= 0),
    ThanhTien FLOAT NOT NULL CHECK (ThanhTien >= 0)
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
    TinhTrang VARCHAR(20) NOT NULL DEFAULT 'CON_NO' CHECK (TinhTrang IN ('CON_NO', 'DANG_NO', 'DA_HOAN_TAT', 'DIEU_CHINH', 'DA_HUY'))
);

-- 5.2 PHIEU_THANH_TOAN
CREATE TABLE PHIEU_THANH_TOAN (
    MaPhieuTT VARCHAR(12) PRIMARY KEY,
    MaHoaDon VARCHAR(12) NOT NULL FOREIGN KEY REFERENCES HOA_DON(MaHoaDon),
    MaCongNo VARCHAR(12) NULL FOREIGN KEY REFERENCES HO_SO_CONG_NO(MaCongNo),
    NgayThanhToan DATETIME NOT NULL DEFAULT GETDATE(),
    LoaiGiaoDich VARCHAR(20) NOT NULL DEFAULT 'THU_TIEN' CHECK (LoaiGiaoDich IN ('THU_TIEN', 'DAT_COC', 'TRA_GOP', 'HOAN_TIEN')),
    SoTien FLOAT NOT NULL CHECK (SoTien > 0),
    PhuongThuc VARCHAR(20) NOT NULL DEFAULT 'TIEN_MAT' CHECK (PhuongThuc IN ('TIEN_MAT', 'CHUYEN_KHOAN', 'THE'))
);

-- --------------------------------------------------------------------
-- DU LIEU MAU KHOI TAO HE THONG (SEED DATA)
-- --------------------------------------------------------------------

-- 1. VAI TRO
INSERT INTO VAI_TRO (MaVaiTro, TenVaiTro, MoTaQuyen) VALUES
('VT_QUANLY', N'Quản lý phòng khám', N'Toàn quyền quản trị, xem báo cáo doanh thu, phân quyền nhân sự, duyệt hủy hóa đơn và hoàn tiền'),
('VT_LETAN',  N'Lễ tân / Thu ngân',   N'Tiếp đón bệnh nhân, xếp lịch hẹn, điều phối ghế, lập hóa đơn, thu tiền đợt và quản lý công nợ'),
('VT_BACSI',  N'Bác sĩ điều trị',     N'Khám bệnh lâm sàng, chẩn đoán hình ảnh X-Quang, chỉ định thủ thuật, lập kế hoạch niềng răng, kê đơn thuốc'),
('VT_KHO',    N'Thủ kho / Vật tư',    N'Quản lý danh mục vật tư y tế, nhập kho lô hàng, theo dõi hạn sử dụng theo chuẩn FEFO, kiểm kê kho');

-- 2. TAI KHOAN (Mat khau mac dinh de dang nhap: 123456)
INSERT INTO TAI_KHOAN (MaTaiKhoan, TenDangNhap, MatKhau, SoLanNhapSai, TinhTrang, MaVaiTro) VALUES
('TK001', 'admin',    '123456', 0, 'HOAT_DONG', 'VT_QUANLY'),
('TK002', 'letan',    '123456', 0, 'HOAT_DONG', 'VT_LETAN'),
('TK003', 'letan2',   '123456', 0, 'HOAT_DONG', 'VT_LETAN'),
('TK004', 'thukho',   '123456', 0, 'HOAT_DONG', 'VT_KHO'),
('TK005', 'bacsi',    '123456', 0, 'HOAT_DONG', 'VT_BACSI'),
('TK006', 'bs_mai',   '123456', 0, 'HOAT_DONG', 'VT_BACSI'),
('TK007', 'bs_duy',   '123456', 0, 'HOAT_DONG', 'VT_BACSI'),
('TK008', 'bs_linh',  '123456', 0, 'HOAT_DONG', 'VT_BACSI');

-- 3. NHAN VIEN
INSERT INTO NHAN_VIEN (MaNV, HoTen, SoDienThoai, Email, ChucVu, TinhTrang, MaTaiKhoan) VALUES
('NV001', N'Trần Quang Hữu',   '0901234567', 'huu.quanly@nhakhoa.vn',  N'Quản lý',        'DANG_LAM', 'TK001'),
('NV002', N'Nguyễn Thị Mai',   '0912345678', 'mai.letan@nhakhoa.vn',   N'Lễ tân',         'DANG_LAM', 'TK002'),
('NV003', N'Lê Thị Thu Thảo',  '0919888777', 'thao.thungan@nhakhoa.vn',N'Thu ngân',       'DANG_LAM', 'TK003'),
('NV004', N'Lê Văn Khoa',      '0923456789', 'khoa.kho@nhakhoa.vn',    N'Nhân viên kho',  'DANG_LAM', 'TK004');

-- 4. BAC SI
INSERT INTO BAC_SI (MaBS, HoTen, SoDienThoai, Email, ChuyenKhoa, SoChungChiHanhNghe, HocVi, TinhTrang, MaTaiKhoan) VALUES
('BS001', N'BS.CKI Nguyễn Văn Hùng', '0934567890', 'hung.nhakhoa@gmail.com',  N'Tiểu phẫu & Cấy ghép Implant', 'CCHN-00123/HCM', N'Bác sĩ Chuyên khoa I', 'DANG_LAM', 'TK005'),
('BS002', N'ThS.BS Lê Thu Trang',     '0945678901', 'trang.nieng@gmail.com',    N'Chỉnh nha Niềng răng',         'CCHN-00456/HCM', N'Thạc sĩ Bác sĩ',       'DANG_LAM', 'TK006'),
('BS003', N'BS. Phạm Nhật Duy',       '0938112233', 'duy.phuchinh@gmail.com',  N'Phục hình & Răng sứ thẩm mỹ',  'CCHN-00789/HCM', N'Bác sĩ Răng Hàm Mặt',  'DANG_LAM', 'TK007'),
('BS004', N'BS. Trần Cẩm Linh',       '0909554433', 'linh.noinha@gmail.com',   N'Nội nha điều trị tủy & Nha chu','CCHN-00999/HCM', N'Bác sĩ Răng Hàm Mặt',  'DANG_LAM', 'TK008');

-- 5. CA LAM VIEC
INSERT INTO CA_LAM_VIEC (MaCa, MaBS, NgayLamViec, GioBatDau, GioKetThuc, TinhTrang) VALUES
('CA001', 'BS001', CAST(GETDATE() AS DATE), '08:00:00', '12:00:00', 'DANG_TRUC'),
('CA002', 'BS001', CAST(GETDATE() AS DATE), '13:30:00', '17:30:00', 'DANG_TRUC'),
('CA003', 'BS002', CAST(GETDATE() AS DATE), '08:00:00', '16:30:00', 'DANG_TRUC'),
('CA004', 'BS003', CAST(GETDATE() AS DATE), '08:30:00', '17:00:00', 'DANG_TRUC'),
('CA005', 'BS004', CAST(GETDATE() AS DATE), '13:00:00', '20:30:00', 'DANG_TRUC'),
('CA006', 'BS001', DATEADD(day, 1, CAST(GETDATE() AS DATE)), '08:00:00', '17:00:00', 'DANG_TRUC'),
('CA007', 'BS002', DATEADD(day, 1, CAST(GETDATE() AS DATE)), '08:00:00', '17:00:00', 'DANG_TRUC');

-- 6. PHONG KHAM VA GHE DIEU TRI
INSERT INTO PHONG_KHAM (MaPhong, TenPhong, TinhTrang) VALUES
('P101', N'Phòng Khám Tổng Quát 1',        'HOAT_DONG'),
('P102', N'Phòng Phẫu Thuật Vô Trùng & Implant', 'HOAT_DONG'),
('P103', N'Phòng Chỉnh Nha & Thẩm Mỹ',     'HOAT_DONG'),
('P104', N'Phòng Nội Nha & Nha Chu',       'HOAT_DONG'),
('P105', N'Phòng Chụp Phim X-Quang Kỹ Thuật Số', 'HOAT_DONG');

INSERT INTO GHE_DIEU_TRI (MaGhe, SoGhe, TinhTrang, MaPhong) VALUES
('GHE01', N'Ghế Nha Số 1',        'HOAT_DONG', 'P101'),
('GHE02', N'Ghế Nha Số 2',        'HOAT_DONG', 'P101'),
('GHE03', N'Ghế Phẫu Thuật Vô Trùng', 'HOAT_DONG', 'P102'),
('GHE04', N'Ghế Chỉnh Nha Số 1',   'HOAT_DONG', 'P103'),
('GHE05', N'Ghế Chỉnh Nha Số 2',   'HOAT_DONG', 'P103'),
('GHE06', N'Ghế Điều Trị Nội Nha', 'HOAT_DONG', 'P104'),
('GHE07', N'Ghế Nha Dự Phòng',     'BAO_TRI',   'P101');

-- 7. BENH NHAN (20 Hồ sơ bệnh nhân đa dạng lứa tuổi, bệnh lý, khu vực)
INSERT INTO HO_SO_BENH_NHAN (MaBN, HoTen, NgaySinh, SoCCCD, SoDienThoai, TienSuBenh, TinhTrang) VALUES
('BN001', N'Phạm Minh Tuấn',    '1995-04-12', '079095001234', '0911223344', N'Sức khỏe bình thường, không dị ứng thuốc', 'HOAT_DONG'),
('BN002', N'Võ Hoàng Yến',      '2001-08-25', '079201005678', '0988776655', N'Dị ứng nhẹ với thuốc kháng sinh Penicillin', 'HOAT_DONG'),
('BN003', N'Đỗ Quốc Bảo',       '1988-11-03', '079088009876', '0977123456', N'Tiền sử cao huyết áp nhẹ (đang uống Amlodipine)', 'HOAT_DONG'),
('BN004', N'Trần Thị Cẩm Vân',  '1999-02-14', '079199003322', '0933445566', N'Khỏe mạnh, nướu nhạy cảm khi ăn đồ lạnh', 'HOAT_DONG'),
('BN005', N'Lê Hoàng Long',     '2003-06-19', '079203004411', '0908123789', N'Đang mọc 2 răng khôn hàm dưới gây sưng đau góc hàm', 'HOAT_DONG'),
('BN006', N'Nguyễn Bích Ngọc',  '1992-09-30', '079192008899', '0918765432', N'Răng ố vàng do nhiễm Tetracycline nhẹ thời nhỏ', 'HOAT_DONG'),
('BN007', N'Huỳnh Văn Phát',    '1975-01-15', '079075003355', '0944667788', N'Tiền sử đái tháo đường Type 2, đường huyết ổn định', 'HOAT_DONG'),
('BN008', N'Bùi Thị Mai Anh',   '2005-12-08', '079205001122', '0966332211', N'Hàm trên hô nhẹ, răng chen chúc độ 2', 'HOAT_DONG'),
('BN009', N'Trương Gia Huy',    '2017-03-22', NULL,           '0903112233', N'Răng sữa 51, 52 lung lay độ 3, sâu kẽ răng cối sữa', 'HOAT_DONG'),
('BN010', N'Vũ Thị Lan Hương',  '1982-07-11', '079182006677', '0982334455', N'Tụt nướu viền chân răng, chảy máu chân răng khi đánh răng', 'HOAT_DONG'),
('BN011', N'Đinh Tiến Đạt',     '1997-10-05', '079197009988', '0971223344', N'Mẻ góc cắn răng cửa 11 do chấn thương thể thao', 'HOAT_DONG'),
('BN012', N'Phạm Thanh Hằng',   '1990-05-18', '079190004433', '0909887766', N'Cười hở lợi nhẹ, muốn tạo hình viền nướu thẩm mỹ', 'HOAT_DONG'),
('BN013', N'Vương Quốc Cường',  '1985-08-27', '079185002211', '0937665544', N'Mất răng hàm 36 và 46 đã 3 năm, tiêu xương nhẹ', 'HOAT_DONG'),
('BN014', N'Ngô Thảo Vy',       '2002-11-14', '079202007788', '0913556677', N'Bệnh nhân niềng răng tháng thứ 6, theo dõi kéo kẽ', 'HOAT_DONG'),
('BN015', N'Phan Anh Dũng',     '1994-04-03', '079194005566', '0988112299', N'Đau buốt răng khôn hàm trên bên trái khi nhai', 'HOAT_DONG'),
('BN016', N'Đặng Kim Ngân',     '2000-02-28', '079200003344', '0943221100', N'Men răng khỏe, có nhu cầu đính đá răng khểnh', 'HOAT_DONG'),
('BN017', N'Lý Tuấn Kiệt',      '1968-12-20', '079068001199', '0902889900', N'Mất nhiều răng hai hàm, khó ăn nhai thức ăn cứng', 'HOAT_DONG'),
('BN018', N'Hoàng Thùy Linh',   '1998-08-09', '079198007766', '0978990011', N'Mòn cổ chân răng các răng 14, 15 buốt ê buốt kéo dài', 'HOAT_DONG'),
('BN019', N'Châu Tấn Lộc',      '2010-09-17', '079210008822', '0912445566', N'Sâu răng rãnh mặt nhai răng hàm 16, 26', 'HOAT_DONG'),
('BN020', N'Mai Phương Thảo',   '1993-01-29', '079193006655', '0938776611', N'Tái khám sau phẫu thuật nhổ răng khôn 7 ngày để cắt chỉ', 'HOAT_DONG');

-- 8. DICH VU NHA KHOA (18 Dịch vụ đầy đủ bảng giá phòng khám chuẩn)
INSERT INTO DICH_VU_NHA_KHOA (MaDV, TenDV, DonGia, TinhTrang) VALUES
('DV01', N'Khám tổng quát & Lập phác đồ điều trị',       50000,    'DANG_AP_DUNG'),
('DV02', N'Cạo vôi răng & Đánh bóng siêu âm (2 hàm)',    250000,   'DANG_AP_DUNG'),
('DV03', N'Trám răng thẩm mỹ Composite quang trùng hợp',  350000,   'DANG_AP_DUNG'),
('DV04', N'Điều trị tủy răng trước (răng cửa/nanh)',     800000,   'DANG_AP_DUNG'),
('DV05', N'Điều trị tủy răng cối lớn nhiều chân',        1500000,  'DANG_AP_DUNG'),
('DV06', N'Nhổ răng lung lay / răng một chân thông thường',300000,  'DANG_AP_DUNG'),
('DV07', N'Nhổ răng khôn mọc thẳng hàm trên',           1000000,  'DANG_AP_DUNG'),
('DV08', N'Tiểu phẫu nhổ răng khôn ngầm/lệch Piezotome', 2500000,  'DANG_AP_DUNG'),
('DV09', N'Tẩy trắng răng tại phòng khám Pola Office',   1800000,  'DANG_AP_DUNG'),
('DV10', N'Chụp phim X-Quang Panorama toàn cảnh',        150000,   'DANG_AP_DUNG'),
('DV11', N'Chụp phim CT Cone Beam 3D khảo sát xương',    450000,   'DANG_AP_DUNG'),
('DV12', N'Răng sứ kim loại Titan bảo hành 5 năm',       2000000,  'DANG_AP_DUNG'),
('DV13', N'Răng toàn sứ Zirconia Dentsply Đức',          4500000,  'DANG_AP_DUNG'),
('DV14', N'Răng toàn sứ Cercon HT cao cấp',              6000000,  'DANG_AP_DUNG'),
('DV15', N'Niềng răng mắc cài kim loại 3M (trọn gói)',   32000000, 'DANG_AP_DUNG'),
('DV16', N'Niềng răng mắc cài sứ thẩm mỹ tự buộc',      45000000, 'DANG_AP_DUNG'),
('DV17', N'Cấy ghép trụ Implant Dentium Hàn Quốc',       16000000, 'DANG_AP_DUNG'),
('DV18', N'Cấy ghép trụ Implant Straumann Thụy Sĩ',      32000000, 'DANG_AP_DUNG');

-- 9. VAT TU NHA KHOA (16 Vật tư tiêu hao và khí cụ y tế)
INSERT INTO VAT_TU (MaVatTu, TenVatTu, MucTonToiThieu, DonViTinh) VALUES
('VT01', N'Găng tay y tế Latex có bột (Hộp 50 đôi)', 15, N'Hộp'),
('VT02', N'Khẩu trang y tế kháng khuẩn 4 lớp (Hộp 50 cái)', 15, N'Hộp'),
('VT03', N'Thuốc tê tiêm Lidocaine 2% ống thủy tinh', 30, N'Ống'),
('VT04', N'Bông gòn cuộn nha khoa vô trùng', 20, N'Gói'),
('VT05', N'Composite đặc trám răng 3M Filtek Z250', 8, N'Tuýp'),
('VT06', N'Bộ mắc cài kim loại 3M Unitek (Bộ 2 hàm)', 5, N'Bộ'),
('VT07', N'Thuốc tẩy trắng Pola Office 35% Hydrogen Peroxide', 6, N'Tuýp'),
('VT08', N'Chỉ phẫu thuật tự tiêu Vicryl 4.0 có kim', 15, N'Tép'),
('VT09', N'Kim tiêm nha khoa dùng 1 lần 27G ngắn', 25, N'Hộp'),
('VT10', N'Gel xoi mòn men răng Acid Etchant 37%', 5, N'Tuýp'),
('VT11', N'Keo dán dán dính ngà Single Bond Universal 3M', 4, N'Lọ'),
('VT12', N'Trụ Implant Dentium SuperLine Ø4.0 x 10mm', 3, N'Trụ'),
('VT13', N'Bột lấy dấu Alginate Cavex Hà Lan (Túi 500g)', 8, N'Túi'),
('VT14', N'Dây cung chỉnh nha NiTi tròn kích thước 0.014', 10, N'Sợi'),
('VT15', N'Dung dịch sát khuẩn Povidine 10% (Chai 500ml)', 10, N'Chai'),
('VT16', N'Gạc phẫu thuật vô trùng 5x5cm (Gói 10 miếng)', 20, N'Gói');

-- 10. DINH MUC VAT TU (Chi tiết tiêu hao vật tư theo chuẩn y khoa)
INSERT INTO DINH_MUC_VAT_TU (MaDV, MaVatTu, SoLuongTieuHao) VALUES
('DV02', 'VT01', 1),   -- Cạo vôi: 1 đôi găng
('DV02', 'VT02', 1),   -- 1 khẩu trang
('DV02', 'VT04', 1),   -- 1 cuộn bông gòn
('DV03', 'VT01', 1),   -- Trám composite: 1 găng
('DV03', 'VT04', 1),   -- 1 bông gòn
('DV03', 'VT05', 0.2), -- 0.2 tuýp composite
('DV03', 'VT10', 0.1), -- 0.1 tuýp etchant
('DV03', 'VT11', 0.05),-- keo bonding
('DV07', 'VT01', 1),   -- Nhổ răng khôn thẳng: 1 găng
('DV07', 'VT03', 2),   -- 2 ống thuốc tê
('DV07', 'VT16', 1),   -- 1 gói gạc
('DV08', 'VT01', 2),   -- Tiểu phẫu nhổ răng ngầm: 2 đôi găng vô trùng
('DV08', 'VT03', 3),   -- 3 ống thuốc tê
('DV08', 'VT08', 1),   -- 1 tép chỉ tự tiêu
('DV08', 'VT15', 0.05),-- Povidine sát trùng
('DV08', 'VT16', 2),   -- 2 gói gạc
('DV09', 'VT01', 1),   -- Tẩy trắng: 1 găng
('DV09', 'VT07', 0.5), -- 0.5 tuýp thuốc tẩy trắng
('DV15', 'VT06', 1),   -- Niềng răng: 1 bộ mắc cài 3M
('DV15', 'VT14', 2),   -- 2 sợi dây cung NiTi
('DV17', 'VT01', 2),   -- Cấy Implant: 2 găng phẫu thuật
('DV17', 'VT03', 3),   -- 3 ống thuốc tê
('DV17', 'VT08', 1),   -- 1 tép chỉ tự tiêu
('DV17', 'VT12', 1);   -- 1 trụ Implant Dentium

-- 11. LO VAT TU (Du lieu FEFO phong phu: co lo date xa, lo can date de canh bao, lo het han, lo het hang)
INSERT INTO LO_VAT_TU (MaLo, MaVatTu, SoLo, HanSuDung, SoLuongTon, TinhTrangLo) VALUES
('LO001', 'VT01', 'LOT-GT-202501', '2027-12-31', 85, 'KHA_DUNG'),
('LO002', 'VT01', 'LOT-GT-202406', '2026-12-15', 20, 'KHA_DUNG'),  -- Hạn gần (ưu tiên FEFO xuất trước)
('LO003', 'VT02', 'LOT-KT-202502', '2028-02-28', 120,'KHA_DUNG'),
('LO004', 'VT03', 'LOT-TE-202411', '2026-11-28', 18, 'KHA_DUNG'),  -- Cận hạn rất gần (< 10 ngày - CẢNH BÁO ĐỎ!)
('LO005', 'VT03', 'LOT-TE-202505', '2027-05-20', 140,'KHA_DUNG'),
('LO006', 'VT04', 'LOT-BG-202501', '2028-01-30', 45, 'KHA_DUNG'),
('LO007', 'VT05', 'LOT-CP-202412', '2026-12-20', 6,  'KHA_DUNG'),  -- Cận hạn (~1 tháng)
('LO008', 'VT05', 'LOT-CP-202508', '2027-08-15', 18, 'KHA_DUNG'),
('LO009', 'VT06', 'LOT-MC-202503', '2028-06-30', 9,  'KHA_DUNG'),
('LO010', 'VT07', 'LOT-TT-202501', '2027-04-30', 12, 'KHA_DUNG'),
('LO011', 'VT08', 'LOT-CH-202410', '2026-12-05', 14, 'KHA_DUNG'),  -- Cận hạn (~15 ngày)
('LO012', 'VT08', 'LOT-CH-202506', '2027-10-31', 50, 'KHA_DUNG'),
('LO013', 'VT09', 'LOT-KT-202504', '2028-04-15', 60, 'KHA_DUNG'),
('LO014', 'VT10', 'LOT-EC-202502', '2027-02-28', 15, 'KHA_DUNG'),
('LO015', 'VT11', 'LOT-BD-202507', '2027-07-31', 10, 'KHA_DUNG'),
('LO016', 'VT12', 'LOT-IP-202509', '2029-09-30', 8,  'KHA_DUNG'),
('LO017', 'VT13', 'LOT-AL-202501', '2027-01-15', 22, 'KHA_DUNG'),
('LO018', 'VT14', 'LOT-DC-202506', '2028-12-31', 35, 'KHA_DUNG'),
('LO019', 'VT15', 'LOT-PV-202503', '2027-03-31', 28, 'KHA_DUNG'),
('LO020', 'VT16', 'LOT-GC-202504', '2027-11-30', 40, 'KHA_DUNG'),
('LO021', 'VT03', 'LOT-TE-202312', '2025-10-01', 0,  'HET_HANG'),  -- Đã xuất hết sạch
('LO022', 'VT07', 'LOT-TT-202305', '2025-05-15', 5,  'HET_HAN');   -- Đã hết hạn (bị khóa xuất)

-- 12. PHIEU NHAP KHO
INSERT INTO PHIEU_NHAP_KHO (MaPhieuNhap, MaTaiKhoan, NgayChungTu) VALUES
('PNK001', 'TK004', DATEADD(day, -30, GETDATE())),
('PNK002', 'TK004', DATEADD(day, -15, GETDATE())),
('PNK003', 'TK004', DATEADD(day, -5,  GETDATE()));

INSERT INTO CHI_TIET_NHAP_KHO (MaPhieuNhap, MaLo, SoLuongNhap) VALUES
('PNK001', 'LO001', 100),
('PNK001', 'LO003', 150),
('PNK001', 'LO005', 150),
('PNK002', 'LO008', 20),
('PNK002', 'LO009', 10),
('PNK002', 'LO012', 60),
('PNK003', 'LO016', 10),
('PNK003', 'LO018', 40);

-- 13. KE HOACH NIENG RANG (Dai han & tra gop)
INSERT INTO KE_HOACH_NIENG_RANG (MaKeHoach, MaPhieuDT, LoTrinhGiaiDoan, LyDoTamHoanHuy, TinhTrang) VALUES
('KHNR001', NULL, N'Giai đoạn 1 (Tháng 1-3): Nhổ răng 14, 24 giải phóng khoảng, gắn mắc cài kim loại 3M hai hàm. Giai đoạn 2 (Tháng 4-12): Đóng khoảng nhổ răng bằng dây cung SS và minivis. Giai đoạn 3 (Tháng 13-18): Tinh chỉnh khớp cắn và tháo mắc cài, đeo hàm duy trì Hawley.', NULL, 'DANG_THUC_HIEN'),
('KHNR002', NULL, N'Giai đoạn 1 (Tháng 1): Quét dấu 3D iTero, lên phác đồ ClinCheck máng trong suốt Invisalign. Giai đoạn 2 (Tháng 2-10): Đeo 24 cặp khay máng trong suốt, tái khám mỗi 6 tuần.', NULL, 'DANG_THUC_HIEN');

-- 14. LICH HEN (Lịch hẹn đa dạng hôm nay, các ca đã khám, đang chờ và đã hủy)
INSERT INTO LICH_HEN (MaLichHen, MaBN, MaBS, MaGhe, MaKeHoach, NgayHen, GioBatDau, GioKetThuc, SoThuTuCho, TinhTrang, LyDoKham, LyDoHuy) VALUES
-- Ca đã khám xong hôm nay
('LH001', 'BN001', 'BS001', 'GHE01', NULL,      CAST(GETDATE() AS DATE), '08:00:00', '08:45:00', 1, 'DA_KHAM',  N'Khám răng sâu và ê buốt khi ăn đồ ngọt', NULL),
('LH002', 'BN004', 'BS003', 'GHE02', NULL,      CAST(GETDATE() AS DATE), '08:30:00', '09:30:00', 2, 'DA_KHAM',  N'Cạo vôi răng và tư vấn tẩy trắng răng', NULL),
('LH003', 'BN005', 'BS001', 'GHE03', NULL,      CAST(GETDATE() AS DATE), '09:00:00', '10:15:00', 3, 'DA_KHAM',  N'Tiểu phẫu nhổ răng khôn mọc lệch hàm dưới 38', NULL),
-- Ca đã đến ngồi chờ sảnh tiếp nhận
('LH004', 'BN002', 'BS002', 'GHE04', 'KHNR001', CAST(GETDATE() AS DATE), '10:00:00', '11:00:00', 4, 'DA_DEN',   N'Tái khám niềng răng định kỳ tháng thứ 3, thay dây cung', NULL),
('LH005', 'BN007', 'BS004', 'GHE06', NULL,      CAST(GETDATE() AS DATE), '10:30:00', '11:45:00', 5, 'DA_DEN',   N'Đặt thuốc diệt tủy đợt 2 răng hàm 46', NULL),
-- Ca sắp tới trong chiều nay
('LH006', 'BN003', 'BS001', 'GHE03', NULL,      CAST(GETDATE() AS DATE), '13:30:00', '15:00:00', 6, 'CHO_KHAM', N'Cấy ghép trụ Implant Dentium răng 36', NULL),
('LH007', 'BN006', 'BS003', 'GHE02', NULL,      CAST(GETDATE() AS DATE), '14:00:00', '15:30:00', 7, 'CHO_KHAM', N'Thử sườn và gắn tạm răng sứ Cercon 4 răng cửa', NULL),
('LH008', 'BN008', 'BS002', 'GHE04', 'KHNR002', CAST(GETDATE() AS DATE), '15:00:00', '16:00:00', 8, 'CHO_KHAM', N'Nhận cặp khay Invisalign số 05 và kiểm tra attachment', NULL),
('LH009', 'BN009', 'BS001', 'GHE01', NULL,      CAST(GETDATE() AS DATE), '16:00:00', '16:45:00', 9, 'CHO_KHAM', N'Nhổ răng sữa lung lay và bôi gel ngừa sâu răng', NULL),
-- Ca hủy lịch hẹn kèm lý do thực tế (FR-13)
('LH010', 'BN011', 'BS001', 'GHE01', NULL,      CAST(GETDATE() AS DATE), '11:00:00', '11:45:00', 0, 'DA_HUY',   N'Trám kẽ răng mẻ', N'Bệnh nhân bận họp đột xuất tại công ty, xin dời sang cuối tuần'),
('LH011', 'BN013', 'BS001', 'GHE03', NULL,      DATEADD(day, -1, CAST(GETDATE() AS DATE)), '09:00:00', '10:30:00', 0, 'DA_HUY', N'Tư vấn Implant', N'Bệnh nhân bị cảm sốt, không đủ điều kiện sức khỏe khám phẫu thuật'),
-- Ca ngày mai
('LH012', 'BN014', 'BS002', 'GHE04', 'KHNR001', DATEADD(day, 1, CAST(GETDATE() AS DATE)), '09:00:00', '10:00:00', 0, 'CHO_KHAM', N'Tái khám chỉnh nha thay thun chuỗi', NULL),
('LH013', 'BN015', 'BS001', 'GHE03', NULL,      DATEADD(day, 1, CAST(GETDATE() AS DATE)), '10:00:00', '11:00:00', 0, 'CHO_KHAM', N'Nhổ răng khôn mọc thẳng 28', NULL),
('LH014', 'BN020', 'BS001', 'GHE01', NULL,      DATEADD(day, 1, CAST(GETDATE() AS DATE)), '14:30:00', '15:00:00', 0, 'CHO_KHAM', N'Khám kiểm tra vết thương và cắt chỉ', NULL);

-- 15. PHIEU KHAM
INSERT INTO PHIEU_KHAM (MaPhieuKham, MaLichHen, MaBN, MaBS, NgayKham, TrieuChung, ChanDoan, TinhTrang) VALUES
('PK001', 'LH001', 'BN001', 'BS001', GETDATE(), N'Đau buốt nhẹ răng hàm dưới khi uống nước đá lạnh, thức ăn dắt kẽ', N'Sâu ngà nông răng số 36 mặt nhai; vôi răng dưới nướu độ 1', 'HOAN_THANH'),
('PK002', 'LH002', 'BN004', 'BS003', GETDATE(), N'Màu men răng sậm màu, có mảng bám vôi răng bề mặt trong răng cửa', N'Nhiễm màu ngoại sinh do trà/cà phê; viêm nướu viền nhẹ', 'HOAN_THANH'),
('PK003', 'LH003', 'BN005', 'BS001', GETDATE(), N'Sưng đau vùng góc hàm trái dưới, há miệng hạn chế, nướu trùm viêm đỏ', N'Răng khôn 38 mọc lệch 75 độ đâm vào chân răng 37 gây viêm quanh thân răng cấp', 'HOAN_THANH'),
('PK004', 'LH004', 'BN002', 'BS002', DATEADD(day, -60, GETDATE()), N'Khách hàng muốn nắn chỉnh răng chen chúc hai hàm và cải thiện thẩm mỹ nụ cười', N'Sai khớp cắn hạng I do răng chen chúc trung bình hai hàm, khớp cắn sâu 3mm', 'HOAN_THANH'),
('PK005', 'LH005', 'BN007', 'BS004', DATEADD(day, -3,  GETDATE()), N'Đau nhức dữ dội răng 46 về đêm, lan lên thái dương, gõ dọc đau buốt', N'Viêm tủy cấp không hồi phục răng hàm lớn 46 có 3 ống tủy', 'HOAN_THANH'),
('PK006', 'LH007', 'BN006', 'BS003', DATEADD(day, -7,  GETDATE()), N'Răng cửa xỉn màu nặng, hình thể răng ngắn không đều', N'Thiểu sản men răng cửa hàm trên, chỉ định bọc 4 răng toàn sứ thẩm mỹ Cercon', 'HOAN_THANH');

-- 16. TEP HINH ANH X-QUANG
INSERT INTO TEP_HINH_ANH (MaHinhAnh, MaPhieuKham, DuongDanTep, GhiChuChuyenMon) VALUES
('IMG001', 'PK003', 'C:/NhaKhoaData/XQuang/BN005_R38_Pano.jpg', N'Phim Panorama thể hiện rõ răng 38 lệch 75 độ, chân răng cong gần dây thần kinh ổ răng dưới'),
('IMG002', 'PK004', 'C:/NhaKhoaData/XQuang/BN002_Cephalo.jpg', N'Phim Cephalo đo sọ nghiêng đánh giá trục răng cửa hàm trên nhô 112 độ');

-- 17. PHIEU DIEU TRI VA CHI TIET DIEU TRI
INSERT INTO PHIEU_DIEU_TRI (MaPhieuDT, MaPhieuKham, MaHoaDon, NgayLap, KetQuaLamSang, TinhTrang) VALUES
('PDT001', 'PK001', NULL, GETDATE(), N'Đã cạo sạch vôi răng 2 hàm, trám bít xoang sâu R36 bằng Composite 3M Z250. Khớp cắn chuẩn không cộm.', 'HOAN_THANH'),
('PDT002', 'PK002', NULL, GETDATE(), N'Đã cạo vôi đánh bóng và thực hiện tẩy trắng răng Pola Office 3 chu kỳ x 8 phút. Răng lên 2.5 tone màu.', 'HOAN_THANH'),
('PDT003', 'PK003', NULL, GETDATE(), N'Tiểu phẫu tách vạt, chia thân răng 38 bằng máy siêu âm Piezotome, lấy trọn chân răng, khâu 2 mũi chỉ tự tiêu Vicryl 4.0.', 'HOAN_THANH'),
('PDT004', 'PK004', NULL, DATEADD(day, -60, GETDATE()), N'Lắp khí cụ nong hàm và gắn bộ mắc cài kim loại 3M hai hàm, đi dây NiTi 0.014.', 'HOAN_THANH'),
('PDT005', 'PK005', NULL, DATEADD(day, -3,  GETDATE()), N'Mở tủy răng 46, đo chiều dài làm việc 3 ống tủy, bơm rửa NaOCl 3%, đặt thuốc diệt tủy Ca(OH)2.', 'HOAN_THANH'),
('PDT006', 'PK006', NULL, DATEADD(day, -7,  GETDATE()), N'Mài cùi 4 răng cửa thẩm mỹ (11, 12, 21, 22), lấy dấu cao su gửi Labo đúc sứ Cercon HT, gắn răng tạm.', 'HOAN_THANH');

-- Cập nhật lại FK cho Kế hoạch niềng răng
UPDATE KE_HOACH_NIENG_RANG SET MaPhieuDT = 'PDT004' WHERE MaKeHoach = 'KHNR001';

-- Chi tiết dịch vụ điều trị
INSERT INTO CHI_TIET_DIEU_TRI (MaPhieuDT, MaDV, ViTriRang, SoLuong, DonGiaApDung, ThanhTien) VALUES
('PDT001', 'DV02', N'Hai hàm', 1, 250000, 250000),
('PDT001', 'DV03', N'Răng 36',  1, 350000, 350000),

('PDT002', 'DV02', N'Hai hàm', 1, 250000, 250000),
('PDT002', 'DV09', N'Toàn hàm',1, 1800000, 1800000),

('PDT003', 'DV10', N'Toàn cảnh',1, 150000, 150000),
('PDT003', 'DV08', N'Răng 38', 1, 2500000, 2500000),

('PDT004', 'DV15', N'Hai hàm', 1, 32000000, 32000000),

('PDT005', 'DV05', N'Răng 46', 1, 1500000, 1500000),

('PDT006', 'DV14', N'Răng 11', 1, 6000000, 6000000),
('PDT006', 'DV14', N'Răng 12', 1, 6000000, 6000000),
('PDT006', 'DV14', N'Răng 21', 1, 6000000, 6000000),
('PDT006', 'DV14', N'Răng 22', 1, 6000000, 6000000);

-- 18. PHIEU XUAT KHO TU DONG THEO FEFO
INSERT INTO PHIEU_XUAT_KHO (MaPhieuXuat, MaTaiKhoan, MaPhieuDT, NgayChungTu, LoaiXuat) VALUES
('PXK001', 'TK004', 'PDT001', GETDATE(), 'TU_DONG_FEFO'),
('PXK002', 'TK004', 'PDT002', GETDATE(), 'TU_DONG_FEFO'),
('PXK003', 'TK004', 'PDT003', GETDATE(), 'TU_DONG_FEFO');

INSERT INTO CHI_TIET_XUAT_KHO (MaPhieuXuat, MaLo, SoLuongXuat) VALUES
('PXK001', 'LO001', 1),
('PXK001', 'LO003', 1),
('PXK001', 'LO006', 1),
('PXK002', 'LO001', 1),
('PXK002', 'LO010', 1),
('PXK003', 'LO001', 2),
('PXK003', 'LO004', 3), -- Trừ đúng lô thuốc tê cận hạn nhất LO004
('PXK003', 'LO011', 1);

-- 19. DON THUOC VA CHI TIET DON THUOC
INSERT INTO DON_THUOC (MaDonThuoc, MaPhieuKham, NgayKe, LoiDanBacSi) VALUES
('DT001', 'PK001', GETDATE(), N'Kiêng ăn đồ quá nóng hoặc quá lạnh trong 24 giờ đầu. Vệ sinh chỉ nha khoa sau ăn.'),
('DT002', 'PK003', GETDATE(), N'Cắn chặt gạc cầm máu trong 45 phút, chườm lạnh ngoài má ngày đầu, không súc miệng nước muối mạnh, uống thuốc đúng giờ.'),
('DT003', 'PK005', DATEADD(day, -3, GETDATE()), N'Nếu đau nhức tăng uống thêm 1 viên Paracetamol. Tránh nhai thức ăn cứng bên hàm đang đặt thuốc diệt tủy.');

INSERT INTO CHI_TIET_DON_THUOC (MaDonThuoc, TenThuoc, DonViTinh, SoLuong, LieuDung) VALUES
('DT001', N'Paracetamol 500mg',           N'Viên', 10, N'Uống 1 viên khi đau nhức, cách nhau tối thiểu 6 tiếng'),
('DT001', N'Nước súc miệng Kin Gingival',   N'Chai', 1,  N'Súc miệng ngày 2 lần sáng và tối sau khi đánh răng'),

('DT002', N'Amoxicillin 500mg',            N'Viên', 14, N'Sáng 1 viên, Chiều 1 viên sau khi ăn no (uống liên tục 7 ngày)'),
('DT002', N'Ibuprofen 400mg',              N'Viên', 10, N'Uống 1 viên sau ăn khi còn sưng đau buốt'),
('DT002', N'Alpha Choay (Kháng phù nề)',   N'Vỉ',   2,  N'Ngậm dưới lưỡi 2 viên/lần, ngày 2 lần'),

('DT003', N'Rodogyl (Kháng sinh nha khoa)',N'Viên', 14, N'Uống ngày 2 lần, mỗi lần 1 viên sau ăn no'),
('DT003', N'Paracetamol 500mg',           N'Viên', 10, N'Uống khi đau nhức răng');

-- 20. HOA DON VA HO SO CONG NO
-- HD001: BN001 (Đã trả 100% bằng Chuyển khoản)
INSERT INTO HOA_DON (MaHoaDon, MaBN, NgayLap, TongTienDichVu, TienGiamGia, TienCocDaTru, TongCanThanhToan, TinhTrang) VALUES
('HD001', 'BN001', GETDATE(), 600000, 50000, 0, 550000, 'DA_TRA');
UPDATE PHIEU_DIEU_TRI SET MaHoaDon = 'HD001' WHERE MaPhieuDT = 'PDT001';

-- HD002: BN004 (Đã trả 100% bằng Quẹt thẻ)
INSERT INTO HOA_DON (MaHoaDon, MaBN, NgayLap, TongTienDichVu, TienGiamGia, TienCocDaTru, TongCanThanhToan, TinhTrang) VALUES
('HD002', 'BN004', GETDATE(), 2050000, 100000, 0, 1950000, 'DA_TRA');
UPDATE PHIEU_DIEU_TRI SET MaHoaDon = 'HD002' WHERE MaPhieuDT = 'PDT002';

-- HD003: BN005 (Đã trả 100% bằng Tiền mặt)
INSERT INTO HOA_DON (MaHoaDon, MaBN, NgayLap, TongTienDichVu, TienGiamGia, TienCocDaTru, TongCanThanhToan, TinhTrang) VALUES
('HD003', 'BN005', GETDATE(), 2650000, 150000, 0, 2500000, 'DA_TRA');
UPDATE PHIEU_DIEU_TRI SET MaHoaDon = 'HD003' WHERE MaPhieuDT = 'PDT003';

-- HD004: BN002 (Gói niềng răng 32.000.000đ - Trả trước 12.000.000đ, nợ lại 20.000.000đ trả góp)
INSERT INTO HOA_DON (MaHoaDon, MaBN, NgayLap, TongTienDichVu, TienGiamGia, TienCocDaTru, TongCanThanhToan, TinhTrang) VALUES
('HD004', 'BN002', DATEADD(day, -60, GETDATE()), 32000000, 0, 0, 32000000, 'CON_NO');
UPDATE PHIEU_DIEU_TRI SET MaHoaDon = 'HD004' WHERE MaPhieuDT = 'PDT004';

INSERT INTO HO_SO_CONG_NO (MaCongNo, MaHoaDon, TongNoPhatSinh, SoDuNoConLai, TinhTrang) VALUES
('CN001', 'HD004', 20000000, 17000000, 'CON_NO'); -- Đã đóng thêm đợt 2: 3.000.000đ -> còn nợ 17.000.000đ

-- HD005: BN006 (Gói răng sứ 24.000.000đ - Giảm 2.000.000đ, cọc trước 10.000.000đ)
INSERT INTO HOA_DON (MaHoaDon, MaBN, NgayLap, TongTienDichVu, TienGiamGia, TienCocDaTru, TongCanThanhToan, TinhTrang) VALUES
('HD005', 'BN006', DATEADD(day, -7, GETDATE()), 24000000, 2000000, 0, 22000000, 'CON_NO');
UPDATE PHIEU_DIEU_TRI SET MaHoaDon = 'HD005' WHERE MaPhieuDT = 'PDT006';

INSERT INTO HO_SO_CONG_NO (MaCongNo, MaHoaDon, TongNoPhatSinh, SoDuNoConLai, TinhTrang) VALUES
('CN002', 'HD005', 12000000, 12000000, 'CON_NO'); -- Đóng đợt đầu 10.000.000đ, còn thiếu 12.000.000đ khi gắn răng sứ chính thức

-- HD006: Hóa đơn bị hủy do nhập nhầm dịch vụ (phục vụ test AUDIT_LOG & RB16)
INSERT INTO HOA_DON (MaHoaDon, MaBN, NgayLap, TongTienDichVu, TienGiamGia, TienCocDaTru, TongCanThanhToan, TinhTrang) VALUES
('HD006', 'BN003', DATEADD(day, -2, GETDATE()), 350000, 0, 0, 350000, 'HUY');

-- 21. PHIEU THANH TOAN (Bien lai thu dong tien thuc te)
INSERT INTO PHIEU_THANH_TOAN (MaPhieuTT, MaHoaDon, MaCongNo, NgayThanhToan, LoaiGiaoDich, SoTien, PhuongThuc) VALUES
('PTT001', 'HD001', NULL,    GETDATE(),                            'THU_TIEN', 550000,   'CHUYEN_KHOAN'),
('PTT002', 'HD002', NULL,    GETDATE(),                            'THU_TIEN', 1950000,  'THE'),
('PTT003', 'HD003', NULL,    GETDATE(),                            'THU_TIEN', 2500000,  'TIEN_MAT'),
-- Thanh toán đợt 1 niềng răng (12tr)
('PTT004', 'HD004', NULL,    DATEADD(day, -60, GETDATE()),         'THU_TIEN', 12000000, 'CHUYEN_KHOAN'),
-- Thanh toán trả góp đợt 2 niềng răng (3tr)
('PTT005', 'HD004', 'CN001', DATEADD(day, -30, GETDATE()),         'TRA_GOP',  3000000,  'CHUYEN_KHOAN'),
-- Thanh toán đặt cọc răng sứ (10tr)
('PTT006', 'HD005', NULL,    DATEADD(day, -7,  GETDATE()),         'DAT_COC',  10000000, 'TIEN_MAT'),
-- Đặt cọc giữ chỗ làm Implant BN003 (5tr)
('PTT007', 'HD003', NULL,    DATEADD(day, -1,  GETDATE()),         'DAT_COC',  5000000,  'CHUYEN_KHOAN');

-- 22. AUDIT LOG (Nhat ky kiem toan he thong - FR-51, RB16)
INSERT INTO AUDIT_LOG (MaLog, MaTaiKhoan, ThoiGian, HanhDong, DoiTuongTacDong, LyDo) VALUES
('LOG0001', 'TK001', DATEADD(day, -2, GETDATE()), 'HUY_HOA_DON',    'HD006',   N'Lễ tân lập nhầm dịch vụ cạo vôi răng cho bệnh nhân Đỗ Quốc Bảo'),
('LOG0002', 'TK001', DATEADD(day, -5, GETDATE()), 'DIEU_CHINH_NO',  'CN001',   N'Điều chỉnh lịch đóng tiền trả góp đợt 2 theo thỏa thuận bệnh nhân'),
('LOG0003', 'TK004', DATEADD(day, -10, GETDATE()),'KIEM_KE_KHO',   'LO004',   N'Kiểm kê phát hiện hư hỏng 2 ống thuốc tê vỡ trong hộp khi vận chuyển'),
('LOG0004', 'TK001', DATEADD(day, -1, GETDATE()), 'XAC_NHAN_COC',   'PTT007',  N'Xác nhận thu cọc trước 5.000.000đ gói cấy ghép Implant Dentium');

-- --------------------------------------------------------------------
-- 4.7. THIET KE CHI MUC TOI UU TRUY VAN (INDEXES)
-- --------------------------------------------------------------------
CREATE NONCLUSTERED INDEX IDX_BN_SDT_CCCD ON HO_SO_BENH_NHAN (SoDienThoai, SoCCCD);
CREATE NONCLUSTERED INDEX IDX_LICH_HEN_CONFLICT ON LICH_HEN (NgayHen, GioBatDau, GioKetThuc, MaBS, MaGhe);
CREATE NONCLUSTERED INDEX IDX_LO_VAT_TU_FEFO ON LO_VAT_TU (MaVatTu, HanSuDung, SoLuongTon, TinhTrangLo);
CREATE NONCLUSTERED INDEX IDX_HOA_DON_BENH_NHAN ON HOA_DON (MaBN, NgayLap, TinhTrang);
CREATE NONCLUSTERED INDEX IDX_AUDIT_LOG_TIME ON AUDIT_LOG (ThoiGian DESC, MaTaiKhoan);
GO

-- --------------------------------------------------------------------
-- 4.8. THU TUC LUU TRU VA TRIGGER RANG BUOC NGHIEP VU (RB01 - RB16)
-- --------------------------------------------------------------------

-- RB06 & RB07: SP Kiem tra ca truc va chong trung lich Bac si / Ghe dieu tri
CREATE OR ALTER PROCEDURE sp_DatLichHen
    @MaLichHen VARCHAR(12),
    @MaBN VARCHAR(10),
    @MaBS VARCHAR(10),
    @MaGhe VARCHAR(10),
    @MaKeHoach VARCHAR(10) = NULL,
    @NgayHen DATE,
    @GioBatDau TIME,
    @GioKetThuc TIME,
    @LyDoKham NVARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;

    -- 1. Kiem tra gio ket thuc phai sau gio bat dau (RB06)
    IF @GioKetThuc <= @GioBatDau
    BEGIN
        RAISERROR(N'Lỗi: Giờ kết thúc phải lớn hơn giờ bắt đầu!', 16, 1);
        RETURN;
    END

    -- 2. Kiem tra ca truc hop le cua Bac si (RB06)
    IF NOT EXISTS (
        SELECT 1 FROM CA_LAM_VIEC 
        WHERE MaBS = @MaBS AND NgayLamViec = @NgayHen 
          AND GioBatDau <= @GioBatDau AND GioKetThuc >= @GioKetThuc 
          AND TinhTrang = 'DANG_TRUC'
    )
    BEGIN
        RAISERROR(N'Lỗi: Bác sĩ không có ca trực phân công trong khung giờ này!', 16, 1);
        RETURN;
    END

    -- 3. Kiem tra trung lich thoi gian thuc cua Bac si hoac Ghe (RB07)
    IF EXISTS (
        SELECT 1 FROM LICH_HEN 
        WHERE NgayHen = @NgayHen AND TinhTrang NOT IN ('DA_HUY')
          AND (MaBS = @MaBS OR MaGhe = @MaGhe)
          AND NOT (GioKetThuc <= @GioBatDau OR GioBatDau >= @GioKetThuc)
    )
    BEGIN
        RAISERROR(N'Lỗi: Trùng lịch hẹn! Bác sĩ hoặc Ghế điều trị đã có người sử dụng trong khoảng thời gian này!', 16, 1);
        RETURN;
    END

    -- 4. Them moi lich hen
    INSERT INTO LICH_HEN (MaLichHen, MaBN, MaBS, MaGhe, MaKeHoach, NgayHen, GioBatDau, GioKetThuc, SoThuTuCho, TinhTrang, LyDoKham)
    VALUES (@MaLichHen, @MaBN, @MaBS, @MaGhe, @MaKeHoach, @NgayHen, @GioBatDau, @GioKetThuc, 0, 'CHO_KHAM', @LyDoKham);
END;
GO

-- RB12: Trigger tu dong xuat kho theo nguyen tac FEFO khi hoan thanh dieu tri
CREATE OR ALTER TRIGGER trg_XuatKhoTuDong_FEFO
ON PHIEU_DIEU_TRI
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (SELECT 1 FROM inserted i JOIN deleted d ON i.MaPhieuDT = d.MaPhieuDT 
               WHERE i.TinhTrang = 'HOAN_THANH' AND d.TinhTrang <> 'HOAN_THANH')
    BEGIN
        DECLARE @MaPhieuDT VARCHAR(12), @MaTaiKhoan VARCHAR(10), @MaPhieuXuat VARCHAR(12);
        SELECT @MaPhieuDT = MaPhieuDT FROM inserted;
        SET @MaTaiKhoan = 'TK001';
        SET @MaPhieuXuat = 'PXK_' + SUBSTRING(@MaPhieuDT, 4, 8);

        IF NOT EXISTS (SELECT 1 FROM PHIEU_XUAT_KHO WHERE MaPhieuDT = @MaPhieuDT)
        BEGIN
            INSERT INTO PHIEU_XUAT_KHO (MaPhieuXuat, MaTaiKhoan, MaPhieuDT, NgayChungTu, LoaiXuat)
            VALUES (@MaPhieuXuat, @MaTaiKhoan, @MaPhieuDT, GETDATE(), 'TU_DONG_FEFO');

            DECLARE @MaVatTu VARCHAR(10), @SoLuongCanXuat FLOAT;
            DECLARE cur_VT CURSOR FOR
                SELECT dm.MaVatTu, SUM(ct.SoLuong * dm.SoLuongTieuHao)
                FROM CHI_TIET_DIEU_TRI ct
                JOIN DINH_MUC_VAT_TU dm ON ct.MaDV = dm.MaDV
                WHERE ct.MaPhieuDT = @MaPhieuDT
                GROUP BY dm.MaVatTu;

            OPEN cur_VT;
            FETCH NEXT FROM cur_VT INTO @MaVatTu, @SoLuongCanXuat;

            WHILE @@FETCH_STATUS = 0
            BEGIN
                DECLARE @MaLo VARCHAR(12), @TonLo INT, @XuatTuLo INT;
                DECLARE cur_Lo CURSOR FOR
                    SELECT MaLo, SoLuongTon 
                    FROM LO_VAT_TU 
                    WHERE MaVatTu = @MaVatTu AND TinhTrangLo = 'KHA_DUNG' AND HanSuDung > CAST(GETDATE() AS DATE)
                    ORDER BY HanSuDung ASC;

                OPEN cur_Lo;
                FETCH NEXT FROM cur_Lo INTO @MaLo, @TonLo;

                WHILE @@FETCH_STATUS = 0 AND @SoLuongCanXuat > 0
                BEGIN
                    IF @TonLo >= @SoLuongCanXuat
                    BEGIN
                        SET @XuatTuLo = CAST(CEILING(@SoLuongCanXuat) AS INT);
                        SET @SoLuongCanXuat = 0;
                    END
                    ELSE
                    BEGIN
                        SET @XuatTuLo = @TonLo;
                        SET @SoLuongCanXuat = @SoLuongCanXuat - @TonLo;
                    END

                    INSERT INTO CHI_TIET_XUAT_KHO (MaPhieuXuat, MaLo, SoLuongXuat)
                    VALUES (@MaPhieuXuat, @MaLo, @XuatTuLo);

                    UPDATE LO_VAT_TU 
                    SET SoLuongTon = SoLuongTon - @XuatTuLo,
                        TinhTrangLo = CASE WHEN (SoLuongTon - @XuatTuLo) <= 0 THEN 'HET_HANG' ELSE TinhTrangLo END
                    WHERE MaLo = @MaLo;

                    FETCH NEXT FROM cur_Lo INTO @MaLo, @TonLo;
                END
                CLOSE cur_Lo;
                DEALLOCATE cur_Lo;

                FETCH NEXT FROM cur_VT INTO @MaVatTu, @SoLuongCanXuat;
            END
            CLOSE cur_VT;
            DEALLOCATE cur_VT;
        END
    END
END;
GO

-- RB16: Trigger tu dong ghi nhat ky kiem toan khi huy hoa don
CREATE OR ALTER TRIGGER trg_AuditLog_HoaDon
ON HOA_DON
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (SELECT 1 FROM inserted i JOIN deleted d ON i.MaHoaDon = d.MaHoaDon 
               WHERE i.TinhTrang IN ('HUY', 'DA_HUY') AND d.TinhTrang NOT IN ('HUY', 'DA_HUY'))
    BEGIN
        INSERT INTO AUDIT_LOG (MaLog, MaTaiKhoan, ThoiGian, HanhDong, DoiTuongTacDong, LyDo)
        SELECT 'LOG' + SUBSTRING(CONVERT(VARCHAR(36), NEWID()), 1, 8),
               'TK001',
               GETDATE(),
               'HUY_HOA_DON',
               i.MaHoaDon,
               N'Hủy hóa đơn thanh toán điều trị'
        FROM inserted i;
    END
END;
GO

PRINT N'=== KHOI TAO CSDL QUAN LY NHA KHOA THANH CONG! ===';
