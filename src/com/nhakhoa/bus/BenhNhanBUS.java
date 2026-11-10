package com.nhakhoa.bus;

import com.nhakhoa.dao.BenhNhanDAO;
import com.nhakhoa.model.BenhNhan;

import java.util.List;

public class BenhNhanBUS {
    private BenhNhanDAO benhNhanDAO = new BenhNhanDAO();

    public List<BenhNhan> getAll() {
        return benhNhanDAO.getAll();
    }

    public List<BenhNhan> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return benhNhanDAO.timKiem(keyword.trim());
    }

    public String taoMaBNMoi() {
        return benhNhanDAO.taoMaBNMoi();
    }

    public void them(BenhNhan bn) throws Exception {
        validate(bn);
        if (bn.getMaBN() == null || bn.getMaBN().trim().isEmpty()) {
            bn.setMaBN(benhNhanDAO.taoMaBNMoi());
        }
        if (!benhNhanDAO.them(bn)) {
            throw new Exception("Thêm hồ sơ bệnh nhân thất bại!");
        }
    }

    public void sua(BenhNhan bn) throws Exception {
        validate(bn);
        if (!benhNhanDAO.sua(bn)) {
            throw new Exception("Cập nhật thông tin bệnh nhân thất bại!");
        }
    }

    public void xoa(String maBN) throws Exception {
        if (!benhNhanDAO.xoa(maBN)) {
            throw new Exception("Khóa hồ sơ bệnh nhân thất bại!");
        }
    }

    private void validate(BenhNhan bn) throws Exception {
        if (bn.getHoTen() == null || bn.getHoTen().trim().isEmpty()) {
            throw new Exception("Họ tên bệnh nhân không được để trống!");
        }
        if (bn.getSoDienThoai() == null || bn.getSoDienThoai().trim().length() < 9) {
            throw new Exception("Số điện thoại không hợp lệ (ít nhất 9-10 chữ số)!");
        }
    }
}
