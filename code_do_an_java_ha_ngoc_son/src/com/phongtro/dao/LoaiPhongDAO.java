package com.phongtro.dao;

import com.phongtro.model.LoaiPhong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoaiPhongDAO {

    public List<LoaiPhong> getAll() throws SQLException {
        String sql = "SELECT MaLoaiPhong, TenLoaiPhong, DienTich, GiaThue, MoTa FROM LoaiPhong ORDER BY MaLoaiPhong";
        List<LoaiPhong> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new LoaiPhong(rs.getInt("MaLoaiPhong"), rs.getString("TenLoaiPhong"),
                        rs.getBigDecimal("DienTich"), rs.getBigDecimal("GiaThue"), rs.getString("MoTa")));
            }
        }
        return list;
    }

    /** MaLoaiPhong là IDENTITY nên không insert cột này. */
    public int insert(LoaiPhong lp) throws SQLException {
        String sql = "INSERT INTO LoaiPhong (TenLoaiPhong, DienTich, GiaThue, MoTa) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, lp.getTenLoaiPhong());
            ps.setBigDecimal(2, lp.getDienTich());
            ps.setBigDecimal(3, lp.getGiaThue());
            ps.setString(4, lp.getMoTa());
            return ps.executeUpdate();
        }
    }

    public int update(LoaiPhong lp) throws SQLException {
        String sql = "UPDATE LoaiPhong SET TenLoaiPhong = ?, DienTich = ?, GiaThue = ?, MoTa = ? WHERE MaLoaiPhong = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, lp.getTenLoaiPhong());
            ps.setBigDecimal(2, lp.getDienTich());
            ps.setBigDecimal(3, lp.getGiaThue());
            ps.setString(4, lp.getMoTa());
            ps.setInt(5, lp.getMaLoaiPhong());
            return ps.executeUpdate();
        }
    }

    /** Lỗi 547 nếu vẫn còn phòng thuộc loại này. */
    public int delete(int maLoaiPhong) throws SQLException {
        String sql = "DELETE FROM LoaiPhong WHERE MaLoaiPhong = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maLoaiPhong);
            return ps.executeUpdate();
        }
    }
}