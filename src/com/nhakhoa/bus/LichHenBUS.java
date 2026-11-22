package com.nhakhoa.bus;

import com.nhakhoa.dao.LichHenDAO;
import com.nhakhoa.model.LichHen;

import java.util.List;

public class LichHenBUS {
    private LichHenDAO lichHenDAO = new LichHenDAO();

    public List<LichHen> getAll() {
        return lichHenDAO.getAll();
    }

    public List<LichHen> getLichHenDaDen() {
        return lichHenDAO.getLichHenDaDen();
    }

    public String taoMaLHMoi() {
        return lichHenDAO.taoMaLHMoi();
    }

    public List<String[]> getDanhSachBacSi() {
        return lichHenDAO.getDanhSachBacSi();
    }

    public List<String[]> getDanhSachGhe() {
        return lichHenDAO.getDanhSachGhe();
    }

    public void them(LichHen lh) throws Exception {
        if (lh.getMaBN() == null || lh.getMaBN().isEmpty()) {
            throw new Exception("Vui lòng chọn bệnh nhân đặt hẹn!");
        }
        if (lh.getMaBS() == null || lh.getMaBS().isEmpty()) {
            throw new Exception("Vui lòng chọn bác sĩ phụ trách!");
        }
        if (lh.getMaGhe() == null || lh.getMaGhe().isEmpty()) {
            throw new Exception("Vui lòng chọn ghế điều trị!");
        }
        if (lh.getNgayHen() == null) {
            throw new Exception("Vui lòng chọn ngày hẹn khám!");
        }
        if (lh.getGioBatDau().compareTo(lh.getGioKetThuc()) >= 0) {
            throw new Exception("Giờ bắt đầu phải trước giờ kết thúc!");
        }

        // Kiểm tra trùng lịch bác sĩ hoặc trùng ghế điều trị
        java.sql.Date sqlDate = new java.sql.Date(lh.getNgayHen().getTime());
        boolean trungLich = lichHenDAO.kiemTraTrungLich(lh.getMaBS(), lh.getMaGhe(), sqlDate, lh.getGioBatDau(), lh.getGioKetThuc());
        if (trungLich) {
            throw new Exception("Trùng lịch hẹn! Bác sĩ hoặc Ghế điều trị đã có ca hẹn khác trong khung giờ này.\n"
                    + "Vui lòng chọn khung giờ khác hoặc điều phối sang Bác sĩ/Ghế khác.");
        }

        if (lh.getMaLichHen() == null || lh.getMaLichHen().isEmpty()) {
            lh.setMaLichHen(lichHenDAO.taoMaLHMoi());
        }

        if (!lichHenDAO.them(lh)) {
            throw new Exception("Đặt lịch hẹn thất bại!");
        }
    }

    public void doiTrangThai(String maLichHen, String trangThai) throws Exception {
        if (!lichHenDAO.doiTrangThai(maLichHen, trangThai)) {
            throw new Exception("Cập nhật trạng thái lịch hẹn thất bại!");
        }
    }

    public List<LichHen> getLichBacSiTheoNgay(String maBS, java.sql.Date ngayHen) {
        return lichHenDAO.getLichBacSiTheoNgay(maBS, ngayHen);
    }
}
