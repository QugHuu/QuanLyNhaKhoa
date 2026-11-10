package com.nhakhoa.bus;

import com.nhakhoa.dao.TaiKhoanDAO;
import com.nhakhoa.model.TaiKhoan;

public class TaiKhoanBUS {
    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    public TaiKhoan dangNhap(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập tên đăng nhập!");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập mật khẩu!");
        }

        TaiKhoan tk = taiKhoanDAO.dangNhap(username.trim(), password.trim());
        if (tk == null) {
            throw new Exception("Tên đăng nhập không tồn tại!");
        }

        if ("TAM_KHOA".equalsIgnoreCase(tk.getTinhTrang())) {
            throw new Exception("Tài khoản đang bị TẠM KHÓA do đăng nhập sai quá 3 lần hoặc nhân sự đã nghỉ việc!");
        }

        if (!password.trim().equals(tk.getMatKhau())) {
            int conLai = 3 - tk.getSoLanNhapSai();
            if (conLai > 0) {
                throw new Exception("Mật khẩu không đúng! Bạn còn " + conLai + " lần thử trước khi tài khoản bị khóa.");
            } else {
                throw new Exception("Bạn đã nhập sai 3 lần! Tài khoản vừa bị KHÓA BẢO MẬT.");
            }
        }

        return tk;
    }
}
