# TÀI LIỆU HƯỚNG DẪN BẢO VỆ ĐỒ ÁN MÔN PHÂN TÍCH THIẾT KẾ HỆ THỐNG
## HỆ THỐNG QUẢN LÝ PHÒNG KHÁM NHA KHOA (SÀI GÒN DENTAL)
**Học phần:** Phân tích & Thiết kế Hệ thống Thông tin (PTTKHTTT)  
**Nhóm thực hiện:** Nhóm 5  
**Công nghệ:** Java Desktop (Swing + FlatLaf UI), Microsoft SQL Server 2022, Kiến trúc 3 lớp (3-Tier)

---

## 1. HƯỚNG DẪN KHỞI CHẠY CHƯƠNG TRÌNH

### Cách 1: Khởi chạy nhanh bằng 1 click
- Nhấp đúp chuột vào file **`run.bat`** tại thư mục gốc `C:\Users\admin\QuanLyNhaKhoa`.
- Script sẽ tự động quét toàn bộ mã nguồn Java, biên dịch mã hóa chuẩn UTF-8 và mở giao diện ứng dụng.

### Cách 2: Khởi chạy thủ công qua Terminal / Command Prompt
```bash
cd c:\Users\admin\QuanLyNhaKhoa
cmd /c run.bat
```

---

## 2. DANH SÁCH TÀI KHOẢN DEMO PHÂN QUYỀN (RBAC)
Hệ thống tích hợp sẵn 4 nút **chọn nhanh vai trò** trên màn hình Đăng nhập để thuận tiện khi thuyết trình trước hội đồng:

| Vai trò | Tên đăng nhập | Mật khẩu | Quyền hạn trong hệ thống |
| :--- | :--- | :--- | :--- |
| **Quản lý (Admin)** | `admin` | `admin123` | Toàn quyền truy cập tất cả 8 phân hệ, bảng giá dịch vụ & báo cáo |
| **Lễ tân** | `letan01` | `123456` | Tiếp nhận bệnh nhân, đặt lịch hẹn, lập hóa đơn & thu tiền trả góp |
| **Bác sĩ điều trị** | `bacsi01` | `123456` | Xem lịch khám, khám lâm sàng, tương tác sơ đồ răng, kê đơn thuốc |
| **Thủ kho** | `kho01` | `123456` | Theo dõi tồn kho theo lô, nhập hàng mới, cảnh báo FEFO & xuất kho |

---

## 3. KỊCH BẢN 8 BƯỚC THUYẾT TRÌNH DEMO TRƯỚC GIẢNG VIÊN

### Bước 1: Màn hình Đăng nhập (`01 · Đăng nhập`)
- **Điểm nhấn:** Thiết kế Card tối giản, hiện đại, màu chủ đạo Deep Teal (`#006C5B`).
- **Nghiệp vụ chứng minh:**
  - Kiểm tra tài khoản & mật khẩu với CSDL SQL Server.
  - Khóa tài khoản tạm thời nếu đăng nhập sai quá 3 lần liên tiếp (Ràng buộc **RB04**).
  - Bấm nút chọn nhanh vai trò (Quản lý/Lễ tân/Bác sĩ/Thủ kho) để đăng nhập tức thì.

### Bước 2: Bảng Tổng quan Dashboard (`02 · Tổng quan`)
- **Điểm nhấn:** 4 thẻ KPI động (Doanh thu, Lượt khám, Doanh thu dịch vụ, Công nợ phát sinh).
- **Nghiệp vụ chứng minh:**
  - Hộp cảnh báo chú ý: Cảnh báo 03 lô thuốc cận hạn trong 30 ngày và công nợ tới hạn.
  - Danh sách lịch hẹn trong ngày: Bấm trực tiếp vào dòng lịch hẹn để **"Tiếp nhận bệnh nhân"** ngay tại sảnh.

### Bước 3: Phân hệ Quản lý Lịch hẹn (`03 · Lịch hẹn`)
- **Điểm nhấn:** Hiển thị lịch hẹn theo ngày, giờ, ghế khám và bác sĩ.
- **Nghiệp vụ chứng minh:**
  - Bấm **"+ Tạo lịch hẹn mới"**: Hệ thống tự động kiểm tra chống trùng giờ ghế và trùng lịch bác sĩ (**RB07**).
  - Chọn lịch hẹn và bấm **"Tiếp nhận vào khám"**: Trạng thái chuyển thành `DA_DEN`, bệnh nhân lập tức xuất hiện trong hàng đợi của bác sĩ.

### Bước 4: Hồ sơ bệnh nhân tập trung (`04 · Bệnh nhân`)
- **Điểm nhấn:** Bố cục chia đôi: danh sách tìm kiếm tức thì bên trái + hồ sơ bệnh án chuyên sâu bên phải.
- **Nghiệp vụ chứng minh:**
  - **Banner cảnh báo tiền sử dị ứng** màu cam nổi bật (Ví dụ: Dị ứng Penicillin, tim mạch) giúp bác sĩ không chỉ định sai thuốc (**RB01**).
  - Bấm **"+ Thêm bệnh nhân"** hoặc **"Chỉnh sửa"** để cập nhật thông tin CCCD, ngày sinh, số điện thoại (**RB02** - xóa mềm, giữ lịch sử).

### Bước 5: Khám lâm sàng & Sơ đồ răng 32 chiếc (`05 · Khám & điều trị`)
- **Điểm nhấn:** **Sơ đồ răng tương tác 32 chiếc (Răng 18 – 48)**: Nhấp chuột vào bất kỳ răng nào (ví dụ Răng 36, Răng 48) để chọn vị trí thủ thuật.
- **Nghiệp vụ chứng minh:**
  - Form nhập triệu chứng, chẩn đoán, lời dặn bác sĩ.
  - Bảng chỉ định dịch vụ thủ thuật nha khoa (Trám răng, Cạo vôi, Chỉnh nha...).
  - Nút **"+ Thêm thuốc"**: Chọn thuốc, nhập số lượng, liều dùng để kê đơn.
  - Bấm **"Hoàn tất điều trị & Trừ kho (FEFO)"**: Tự động lưu phiếu khám và gọi trigger trừ kho thuốc theo hạn dùng.

### Bước 6: Kho vật tư & Thuật toán FEFO (`07 · Kho vật tư`)
- **Điểm nhấn:** Quản lý kho theo từng lô hàng (`LO_VAT_TU`) sắp xếp theo thứ tự `HanSuDung ASC` (Hạn gần nhất lên trên cùng).
- **Nghiệp vụ chứng minh:**
  - **Banner cảnh báo màu vàng:** Các lô cận hạn trong 30 ngày (**RB11**).
  - Nút **"Xuất kho"**: Nhập số lượng xuất, hệ thống chứng minh thuật toán FEFO tự động trừ lô có hạn sử dụng gần nhất trước (**RB12**).
  - Nút **"+ Nhập lô mới"**: Nhập số lô, hạn sử dụng, số lượng nhập.

### Bước 7: Thanh toán & Sổ nợ trả góp (`06 · Thanh toán & công nợ`)
- **Điểm nhấn:** Tab chuyển đổi giữa **Hóa đơn điều trị** và **Sổ công nợ trả góp**.
- **Nghiệp vụ chứng minh:**
  - Tự động tính toán tổng tiền, chiết khấu ưu đãi, số tiền phải thanh toán đợt này (**RB14**).
  - Ghi nhận thanh toán trả góp từng đợt và tự động giảm trừ dư nợ (**RB15**).
  - Nút **"In hóa đơn / Phiếu thu"**: Mở hộp thoại phiếu thu chuẩn phòng khám (đầy đủ logo, mã chứng từ, chi tiết tiền, chữ ký thu ngân) có thể in ra máy in hoặc xuất PDF.

### Bước 8: Báo cáo phân tích & Thống kê (`08 · Báo cáo & thống kê`)
- **Điểm nhấn:**
  - Biểu đồ cột (Bar Chart) vẽ trực tiếp bằng Java 2D biểu diễn doanh thu dịch vụ theo từng ngày trong tuần.
  - Thanh tiến trình (Progress Bar) cơ cấu doanh thu theo nhóm dịch vụ (Chỉnh nha 41%, Phục hình 29%, Nha chu 19%...).
  - Bảng hiệu suất làm việc chi tiết của từng bác sĩ điều trị.
  - Nút **"+ Xuất báo cáo"**: Xuất file `BaoCaoDoanhThu_NhaKhoa.csv` (chuẩn UTF-8 BOM) mở được ngay trên Microsoft Excel mà không bị lỗi font chữ tiếng Việt.

---

## 4. TỔ CHỨC KIẾN TRÚC MÃ NGUỒN (3-TIER ARCHITECTURE)

```
c:\Users\admin\QuanLyNhaKhoa\
├── database/
│   └── QuanLyNhaKhoa.sql        # Kịch bản CSDL 28 bảng + dữ liệu mẫu tiếng Việt chuẩn
├── lib/
│   ├── mssql-jdbc.jar           # Driver kết nối Microsoft SQL Server
│   ├── flatlaf.jar              # Thư viện giao diện phẳng hiện đại (FlatLaf 3.5)
│   └── mssql-jdbc_auth...dll    # Thư viện Windows Integrated Authentication
├── src/com/nhakhoa/
│   ├── App.java                 # Điểm khởi chạy chương trình (Main Entry Point)
│   ├── model/                   # Data Transfer Objects (DTO): TaiKhoan, BenhNhan, LichHen, ...
│   ├── dao/                     # Data Access Objects (SQL Queries, Transactions): TaiKhoanDAO, ...
│   ├── bus/                     # Business Logic Layer (Validation, Rules RB01-RB16): LichHenBUS, ...
│   ├── gui/                     # Presentation Layer (JFrame, JPanel, Sơ đồ răng, Biểu đồ): MainFrame, ...
│   └── util/                    # Hỗ trợ: DBConnect, FormatUtil, UITheme (màu sắc, font chữ)
├── run.bat                      # Script biên dịch và chạy ứng dụng 1-click
└── BaoCaoDoanhThu_NhaKhoa.csv   # File kết xuất báo cáo Excel tự động
```

---

## 5. BỘ CÂU HỎI THƯỜNG GẶP KHI BẢO VỆ ĐỒ ÁN (FAQ)

### Q1: Tại sao phòng khám nha khoa phải áp dụng nguyên tắc FEFO thay vì FIFO?
> **Trả lời:** Thuốc tê (Lidocain, Articaine), chất gắn răng và chỉ phẫu thuật có hạn sử dụng nghiêm ngặt và ngày nhập kho không phản ánh đúng hạn sử dụng (lô nhập sau có thể có hạn dùng ngắn hơn lô nhập trước từ nhà phân phối). Nguyên tắc **FEFO (First Expired, First Out - Hạn gần xuất trước)** đảm bảo các vật tư sắp hết hạn luôn được ưu tiên xuất dùng trước, ngăn ngừa rủi ro y tế và giảm thiểu hư hao thuốc quá đát.

### Q2: Ràng buộc RB07 (chống trùng lịch) được xử lý như thế nào?
> **Trả lời:** Được kiểm tra tại lớp `LichHenBUS` và `LichHenDAO` bằng mệnh đề giao thoa thời gian:
> `WHERE (GioBatDau < @GioKetThucMoi AND GioKetThuc > @GioBatDauMoi) AND (MaBS = @MaBS OR MaGhe = @MaGhe)`
> Nếu tồn tại ít nhất 1 dòng trùng lịch, hệ thống throw Exception và chặn không cho lưu lịch hẹn.

### Q3: Vì sao dự án áp dụng mô hình 3 lớp (3-Tier)?
> **Trả lời:**
> 1. **GUI (Presentation):** Chỉ làm nhiệm vụ hiển thị giao diện, bắt sự kiện chuột và phím. Không chứa câu lệnh SQL.
> 2. **BUS (Business Logic):** Chứa các quy tắc nghiệp vụ, kiểm tra tính hợp lệ dữ liệu và các ràng buộc toàn vẹn RB01–RB16.
> 3. **DAO (Data Access):** Độc lập quản lý việc mở kết nối JDBC, thực thi câu lệnh SQL/Prepared Statement và trả về danh sách đối tượng Model (DTO).
> Cách phân tầng này giúp mã nguồn dễ bảo trì, dễ mở rộng và kiểm thử độc lập.

