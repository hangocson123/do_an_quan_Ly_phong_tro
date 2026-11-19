package com.phongtro.dao;

import com.phongtro.model.TaiKhoan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDAO {

    /** Trả về TaiKhoan nếu đúng thông tin và tài khoản đang hoạt động, ngược lại trả về null. */
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) throws SQLException {
        String sql = "SELECT TenDangNhap, MatKhau, HoTen, VaiTro, TrangThai FROM TaiKhoan "
                   + "WHERE TenDangNhap = ? AND MatKhau = ? AND TrangThai = 1";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TaiKhoan(rs.getString("TenDangNhap"), rs.getString("MatKhau"),
                            rs.getString("HoTen"), rs.getString("VaiTro"), rs.getBoolean("TrangThai"));
                }
            }
        }
        return null;
    }

    /** Không đọc cột MatKhau để không đưa mật khẩu lên giao diện. */
    public List<TaiKhoan> getAll() throws SQLException {
        String sql = "SELECT TenDangNhap, HoTen, VaiTro, TrangThai FROM TaiKhoan ORDER BY VaiTro, TenDangNhap";
        List<TaiKhoan> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TaiKhoan(rs.getString("TenDangNhap"), null, rs.getString("HoTen"),
                        rs.getString("VaiTro"), rs.getBoolean("TrangThai")));
            }
        }
        return list;
    }

    public int insert(TaiKhoan tk) throws SQLException {
        String sql = "INSERT INTO TaiKhoan (TenDangNhap, MatKhau, HoTen, VaiTro, TrangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getHoTen());
            ps.setString(4, tk.getVaiTro());
            ps.setBoolean(5, tk.isTrangThai());
            return ps.executeUpdate();
        }
    }

    /** Nếu matKhau null hoặc rỗng thì giữ nguyên mật khẩu cũ. */
    public int update(TaiKhoan tk) throws SQLException {
        boolean doiMatKhau = tk.getMatKhau() != null && !tk.getMatKhau().isEmpty();
        String sql = doiMatKhau
                ? "UPDATE TaiKhoan SET HoTen = ?, VaiTro = ?, TrangThai = ?, MatKhau = ? WHERE TenDangNhap = ?"
                : "UPDATE TaiKhoan SET HoTen = ?, VaiTro = ?, TrangThai = ? WHERE TenDangNhap = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tk.getHoTen());
            ps.setString(2, tk.getVaiTro());
            ps.setBoolean(3, tk.isTrangThai());
            if (doiMatKhau) {
                ps.setString(4, tk.getMatKhau());
                ps.setString(5, tk.getTenDangNhap());
            } else {
                ps.setString(4, tk.getTenDangNhap());
            }
            return ps.executeUpdate();
        }
    }

    public int delete(String tenDangNhap) throws SQLException {
        String sql = "DELETE FROM TaiKhoan WHERE TenDangNhap = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            return ps.executeUpdate();
        }
    }
}