package com.phongtro.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Quản lý bảng TaiKhoanNguoiThue (mỗi người thuê tối đa 1 tài khoản). */
public class TaiKhoanNguoiThueDAO {

    public int insert(int maKhach, String tenDangNhap, String matKhau, boolean hoatDong) throws SQLException {
        String sql = "INSERT INTO TaiKhoanNguoiThue (TenDangNhap, MatKhau, MaKhach, TrangThai) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            ps.setInt(3, maKhach);
            ps.setBoolean(4, hoatDong);
            return ps.executeUpdate();
        }
    }

    /** matKhau null hoặc rỗng = giữ nguyên mật khẩu cũ. */
    public int update(int maKhach, String matKhau, boolean hoatDong) throws SQLException {
        boolean doiMatKhau = matKhau != null && !matKhau.isEmpty();
        String sql = doiMatKhau
                ? "UPDATE TaiKhoanNguoiThue SET TrangThai = ?, MatKhau = ? WHERE MaKhach = ?"
                : "UPDATE TaiKhoanNguoiThue SET TrangThai = ? WHERE MaKhach = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, hoatDong);
            if (doiMatKhau) {
                ps.setString(2, matKhau);
                ps.setInt(3, maKhach);
            } else {
                ps.setInt(2, maKhach);
            }
            return ps.executeUpdate();
        }
    }
}