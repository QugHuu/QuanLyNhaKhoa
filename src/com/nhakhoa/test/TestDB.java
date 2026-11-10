package com.nhakhoa.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDB {
    public static void main(String[] args) {
        String[] urls = {
            "jdbc:sqlserver://localhost\\SQLEXPRESS02;databaseName=QuanLyNhaKhoa;integratedSecurity=true;trustServerCertificate=true;",
            "jdbc:sqlserver://localhost:49672;databaseName=QuanLyNhaKhoa;integratedSecurity=true;trustServerCertificate=true;"
        };
        
        for (String url : urls) {
            try {
                System.out.println("Testing: " + url);
                Connection conn = DriverManager.getConnection(url);
                System.out.println(">>> SUCCESS! Connected with URL: " + url);
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT MaTaiKhoan, TenDangNhap, MaVaiTro FROM TAI_KHOAN");
                while (rs.next()) {
                    System.out.println("   TK: " + rs.getString("MaTaiKhoan") + " | User: " + rs.getString("TenDangNhap") + " | Role: " + rs.getString("MaVaiTro"));
                }
                conn.close();
                return;
            } catch (Exception e) {
                System.out.println("Failed: " + e.getMessage());
            }
        }
    }
}
