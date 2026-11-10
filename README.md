# HỆ THỐNG QUẢN LÝ PHÒNG KHÁM NHA KHOA (SÀI GÒN DENTAL)
> **Đồ án môn học:** Phân tích Thiết kế Hệ thống Hướng đối tượng (PTTKHT)  
> **Đơn vị:** Đại học Sài Gòn (SGU) - Khoa Công nghệ Thông tin  
> **Nhóm thực hiện:** Nhóm 5  
> **Ngôn ngữ & Nền tảng:** Java SE 24 Desktop (Java Swing), Microsoft SQL Server 2022  

---

## 1. Cấu trúc dự án (Mô hình 3 lớp chuẩn sinh viên)
```text
QuanLyNhaKhoa/
├── database/
│   └── QuanLyNhaKhoa.sql         # Script tạo CSDL 28 bảng + dữ liệu mẫu ban đầu
├── lib/
│   ├── mssql-jdbc.jar            # Thư viện Microsoft JDBC Driver 13.4
│   └── mssql-jdbc_auth-13.4.0.x64.dll # Thư viện xác thực Windows Authentication
├── bin/                          # Thư mục chứa các file .class sau khi biên dịch
├── src/
│   └── com/nhakhoa/
│       ├── App.java              # Lớp Main khởi chạy ứng dụng
│       ├── util/
│       │   ├── DBConnect.java    # Quản lý kết nối SQL Server (hỗ trợ Windows Auth)
│       │   └── FormatUtil.java   # Định dạng tiền tệ VNĐ và ngày tháng dd/MM/yyyy
│       ├── model/                # 13 lớp Entity/DTO ánh xạ bảng CSDL
│       │   ├── TaiKhoan.java     # TAI_KHOAN & VAI_TRO
│       │   ├── BenhNhan.java     # HO_SO_BENH_NHAN
│       │   ├── LichHen.java      # LICH_HEN
│       │   ├── PhieuKham.java    # PHIEU_KHAM
│       │   ├── DichVu.java       # DICH_VU_NHA_KHOA
│       │   ├── DonThuoc.java     # DON_THUOC
│       │   ├── ChiTietDonThuoc.java # CHI_TIET_DON_THUOC
│       │   ├── PhieuDieuTri.java # PHIEU_DIEU_TRI
│       │   ├── ChiTietDieuTri.java # CHI_TIET_DIEU_TRI
│       │   ├── VatTu.java        # VAT_TU & DINH_MUC_VAT_TU
│       │   ├── LoVatTu.java      # LO_VAT_TU (FEFO)
│       │   ├── HoaDon.java       # HOA_DON
│       │   └── HoSoCongNo.java   # HO_SO_CONG_NO
│       ├── dao/                  # Tầng truy xuất CSDL qua JDBC
│       │   ├── TaiKhoanDAO.java
│       │   ├── BenhNhanDAO.java
│       │   ├── LichHenDAO.java
│       │   ├── PhieuKhamDAO.java
│       │   ├── DichVuDAO.java
│       │   ├── KhoDAO.java       # Thuật toán xuất kho FEFO (HanSuDung ASC)
│       │   ├── HoaDonDAO.java    # Lập hóa đơn, trả góp, thu nợ
│       │   └── ThongKeDAO.java   # Thống kê doanh thu, số liệu dashboard
│       ├── bus/                  # Tầng xử lý nghiệp vụ (Business Logic)
│       │   ├── TaiKhoanBUS.java  # Khóa sau 3 lần nhập sai (RB05)
│       │   ├── BenhNhanBUS.java  # Kiểm tra tính hợp lệ hồ sơ
│       │   └── LichHenBUS.java   # Chống trùng lịch Bác sĩ & Ghế (RB07)
│       └── gui/                  # Tầng giao diện người dùng (Java Swing)
│           ├── LoginForm.java    # Màn hình đăng nhập (có nút chọn nhanh tài khoản)
│           ├── MainFrame.java    # Giao diện chính (Sidebar navigation, phân quyền RBAC)
│           ├── PanelTongQuan.java# Dashboard số liệu KPI
│           ├── PanelBenhNhan.java# Quản lý hồ sơ bệnh nhân (Thêm, Sửa, Khóa, Tìm kiếm)
│           ├── PanelLichHen.java # Đặt lịch hẹn, tiếp nhận bệnh nhân đã đến (RB08)
│           ├── PanelKhamBenh.java# Khám bệnh, chỉ định dịch vụ, kê đơn thuốc (Bác sĩ)
│           ├── PanelHoaDon.java  # Hóa đơn thanh toán, theo dõi sổ nợ trả góp (RB15)
│           ├── PanelKhoVatTu.java# Quản lý kho theo FEFO, cảnh báo cận hạn & tồn thấp
│           ├── PanelDichVu.java  # Bảng giá dịch vụ nha khoa
│           └── PanelThongKe.java # Báo cáo doanh thu theo từng dịch vụ (FR-61)
├── run.bat                       # File nhấp đúp để tự động biên dịch và chạy
└── sources.txt                   # Danh sách file nguồn Java phục vụ biên dịch
```

---

## 2. Hướng dẫn chạy chương trình
### Cách 1: Chạy trực tiếp bằng file `run.bat` (Khuyên dùng)
- Chỉ cần nhấp đúp chuột vào file `run.bat` trong thư mục gốc `QuanLyNhaKhoa`.
- Script sẽ tự động gom các file `.java`, biên dịch vào thư mục `bin` và mở giao diện ứng dụng.

### Cách 2: Chạy từ cửa sổ dòng lệnh (Terminal / PowerShell / CMD)
```cmd
cd C:\Users\admin\QuanLyNhaKhoa
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -cp "lib\mssql-jdbc.jar;lib\*" -d bin @sources.txt
java -cp "bin;lib\mssql-jdbc.jar;lib\*" "-Djava.library.path=lib" com.nhakhoa.App
```

---

## 3. Danh sách tài khoản thử nghiệm (Đã có sẵn trên giao diện)
Mật khẩu mặc định của tất cả tài khoản mẫu là: `123456`

| Tên đăng nhập | Mật khẩu | Họ và Tên | Vai trò (Role) | Chức năng truy cập |
|---|---|---|---|---|
| **`admin`** | `123456` | Trần Quang Hữu | **Quản lý phòng khám** | Toàn quyền tất cả các phân hệ |
| **`letan`** | `123456` | Nguyễn Thị Mai | **Lễ tân / Thu ngân** | Tiếp nhận BN, Đặt lịch hẹn, Lập hóa đơn, Thu nợ |
| **`bacsi`** | `123456` | BS.CKI Nguyễn Văn Hùng | **Bác sĩ điều trị** | Xem lịch hẹn, Tiếp nhận ca khám, Kê đơn, Chỉ định DV |
| **`thukho`** | `123456` | Lê Văn Khoa | **Thủ kho / Vật tư** | Quản lý lô theo FEFO, Nhập kho, Cảnh báo cận hạn |

*(Trên form đăng nhập đã tích hợp sẵn 4 nút bấm chọn nhanh tài khoản để tiện báo cáo vấn đáp với thầy cô).*

---

## 4. Bảng đối chiếu các ràng buộc toàn vẹn (RBTV) trong đồ án

| Mã RB | Nội dung ràng buộc trong báo cáo | Vị trí cài đặt trong mã nguồn |
|---|---|---|
| **RB01, RB02** | Toàn vẹn miền giá trị `CHECK` & Khóa ngoại `FOREIGN KEY` (Không xóa vật lý dòng cha) | `QuanLyNhaKhoa.sql`, `BenhNhanDAO.xoa()` (soft delete) |
| **RB05** | Khóa tạm thời tài khoản khi nhập sai liên tiếp ≥ 3 lần (`TAM_KHOA`) | `TaiKhoanBUS.java`, `TaiKhoanDAO.dangNhap()` |
| **RB06** | Ràng buộc thời gian ca trực và lịch hẹn (`GioKetThuc > GioBatDau`) | `LichHenBUS.java`, `QuanLyNhaKhoa.sql` |
| **RB07** | Chống trùng lịch Bác sĩ hoặc trùng Ghế điều trị trong cùng khung giờ | `LichHenDAO.kiemTraTrungLich()`, `LichHenBUS.java` |
| **RB08** | Tiếp nhận khám: Chỉ cho phép tạo phiếu khám khi lịch hẹn chuyển sang trạng thái `DA_DEN` | `LichHenBUS.doiTrangThai()`, `PanelKhamBenh.java` (lọc hàng đợi) |
| **RB11, RB12** | Quản lý kho theo nguyên tắc **FEFO (First Expired, First Out)**: ưu tiên xuất các lô có `HanSuDung` nhỏ nhất còn khả dụng | `KhoDAO.getAllLoVatTu()` (`ORDER BY HanSuDung ASC`), `KhoDAO.xuatKhoFEFO()` |
| **RB14, RB15** | Điều kiện lập hóa đơn, thanh toán nhiều đợt và tự động tạo `HO_SO_CONG_NO` nếu trả thiếu | `HoaDonDAO.lapHoaDonVaThanhToan()`, `PanelHoaDon.java` |

