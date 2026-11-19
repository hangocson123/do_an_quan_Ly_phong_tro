package com.phongtro.dao;

import com.phongtro.model.PhongTro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** DAO cho bảng Phong (JOIN LoaiPhong để lấy tên loại và giá thuê). */
public class PhongTroDAO {
    private static final String SELECT =
            "SELECT p.MaPhong, p.TenPhong, p.Tang, p.MaLoaiPhong, lp.TenLoaiPhong, lp.GiaThue, p.TrangThai, p.GhiChu "
          + "FROM Phong p JOIN LoaiPhong lp ON p.MaLoaiPhong = lp.MaLoaiPhong ";

    public List<PhongTro> getAll() throws SQLException {
        return query(SELECT + "ORDER BY p.MaPhong");
    }

    public List<PhongTro> search(String keyword) throws SQLException {
        String like = "%" + keyword + "%";
        return query(SELECT
                + "WHERE CAST(p.MaPhong AS NVARCHAR(20)) LIKE ? OR p.TenPhong LIKE ? "
                + "OR lp.TenLoaiPhong LIKE ? OR p.TrangThai LIKE ? ORDER BY p.MaPhong",
                like, like, like, like);
    }

    /** MaPhong là IDENTITY nên không insert cột này. */
    public int insert(PhongTro p) throws SQLException {
        String sql = "INSERT INTO Phong (TenPhong, Tang, MaLoaiPhong, TrangThai, GhiChu) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getTenPhong());
            ps.setInt(2, p.getTang());
            ps.setInt(3, p.getMaLoaiPhong());
            ps.setString(4, p.getTrangThai());
            ps.setString(5, p.getGhiChu());
            return ps.executeUpdate();
        }
    }

    public int update(PhongTro p) throws SQLException {
        String sql = "UPDATE Phong SET TenPhong = ?, Tang = ?, MaLoaiPhong = ?, TrangThai = ?, GhiChu = ? WHERE MaPhong = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getTenPhong());
            ps.setInt(2, p.getTang());
            ps.setInt(3, p.getMaLoaiPhong());
            ps.setString(4, p.getTrangThai());
            ps.setString(5, p.getGhiChu());
            ps.setInt(6, p.getMaPhong());
            return ps.executeUpdate();
        }
    }

    public int delete(int maPhong) throws SQLException {
        String sql = "DELETE FROM Phong WHERE MaPhong = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maPhong);
            return ps.executeUpdate();
        }
    }

    private List<PhongTro> query(String sql, String... params) throws SQLException {
        List<PhongTro> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PhongTro(rs.getInt("MaPhong"), rs.getString("TenPhong"), rs.getInt("Tang"),
                            rs.getInt("MaLoaiPhong"), rs.getString("TenLoaiPhong"), rs.getBigDecimal("GiaThue"),
                            rs.getString("TrangThai"), rs.getString("GhiChu")));
                }
            }
        }
        return list;
    }
}