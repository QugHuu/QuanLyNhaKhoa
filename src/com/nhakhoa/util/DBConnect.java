package com.nhakhoa.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class kết nối Cơ sở dữ liệu SQL Server
 * Viết theo chuẩn sinh viên: Đơn giản, rõ ràng, dễ bảo vệ đồ án
 */
public class DBConnect {
    // Cấu hình chuỗi kết nối SQL Server
    // Hỗ trợ kết nối SQLEXPRESS02 với Windows Authentication hoặc tài khoản nhakhoa
    private static final String DB_NAME = "QuanLyNhaKhoa";
    
    public static Connection getConnection() {
        // Đảm bảo JDBC driver dùng Unicode cho string parameters
        System.setProperty("file.encoding", "UTF-8");
        Connection conn = null;
        try {
            // Cách 1: Thử kết nối qua Windows Authentication (Integrated Security)
            String urlWindowsAuth = "jdbc:sqlserver://localhost\\SQLEXPRESS02;databaseName=" + DB_NAME + ";integratedSecurity=true;trustServerCertificate=true;sendStringParametersAsUnicode=true;";
            conn = DriverManager.getConnection(urlWindowsAuth);
            return conn;
        } catch (Exception e1) {
            try {
                // Cách 2: Thử kết nối qua port động 49672 hoặc tài khoản nhakhoa/123
                String urlSQLAuth = "jdbc:sqlserver://localhost\\SQLEXPRESS02;databaseName=" + DB_NAME + ";user=nhakhoa;password=123;trustServerCertificate=true;sendStringParametersAsUnicode=true;";
                conn = DriverManager.getConnection(urlSQLAuth);
                return conn;
            } catch (Exception e2) {
                try {
                    // Cách 3: Thử kết nối SQLEXPRESS mặc định
                    String urlDefault = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=" + DB_NAME + ";integratedSecurity=true;trustServerCertificate=true;sendStringParametersAsUnicode=true;";
                    conn = DriverManager.getConnection(urlDefault);
                    return conn;
                } catch (Exception e3) {
                    System.err.println("Lỗi kết nối CSDL: " + e3.getMessage());
                }
            }
        }
        return conn;
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
