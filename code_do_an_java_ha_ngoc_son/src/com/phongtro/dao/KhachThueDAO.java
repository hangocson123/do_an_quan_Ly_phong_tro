package com.phongtro.dao;

import com.phongtro.model.KhachThue;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class KhachThueDAO {
    // LEFT JOIN: người thuê chưa có tài khoản vẫn hiện ra (TenDangNhap = null)
    private static final String SELECT =
            "SELECT k.MaKhach, k.HoTen, k.NgaySinh, k.GioiTinh, k.CCCD, k.SDT, k.Email, k.QueQuan, "
          + "t.TenDangNhap, t.TrangThai AS TKTrangThai "
          + "FROM KhachThue k LEFT JOIN TaiKhoanNguoiThue t ON t.MaKhach = k.MaKhach ";

    public List<KhachThue> getAll() throws SQLException {
        return query(SELECT + "ORDER BY k.MaKhach");
    }

    public List<KhachThue> search(String keyword) throws SQLException {
        String like = "%" + keyword + "%";
        return query(SELECT
                + "WHERE CAST(k.MaKhach AS NVARCHAR(20)) LIKE ? OR k.HoTen LIKE ? OR k.CCCD LIKE ? "
                + "OR k.SDT LIKE ? OR k.QueQuan LIKE ? OR t.TenDangNhap LIKE ? ORDER BY k.MaKhach",
                like, like, like, like, like, like);
    }

    /** Người thuê đăng nhập bằng tài khoản trong bảng TaiKhoanNguoiThue. Sai thông tin trả về null. */
    public KhachThue dangNhap(String tenDangNhap, String matKhau) throws SQLException {
        List<KhachThue> list = query(SELECT + "WHERE t.TenDangNhap = ? AND t.MatKhau = ? AND t.TrangThai = 1",
                tenDangNhap, matKhau);
        return list.isEmpty() ? null : list.get(0);
    }

    /** MaKhach là IDENTITY nên không insert cột này. */
    public int insert(KhachThue kt) throws SQLException {
        String sql = "INSERT INTO KhachThue (HoTen, NgaySinh, GioiTinh, CCCD, SDT, Email, QueQuan) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ghiThamSo(ps, kt);
            return ps.executeUpdate();
        }
    }

    public int update(KhachThue kt) throws SQLException {
        String sql = "UPDATE KhachThue SET HoTen = ?, NgaySinh = ?, GioiTinh = ?, CCCD = ?, SDT = ?, "
                   + "Email = ?, QueQuan = ? WHERE MaKhach = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ghiThamSo(ps, kt);
            ps.setInt(8, kt.getMaKhach());
            return ps.executeUpdate();
        }
    }

    /** Xóa người thuê (tài khoản đăng nhập của họ tự bị xóa theo). Lỗi 547 nếu đã có hợp đồng. */
    public int delete(int maKhach) throws SQLException {
        String sql = "DELETE FROM KhachThue WHERE MaKhach = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maKhach);
            return ps.executeUpdate();
        }
    }

    /** Các hợp đồng của người thuê. Cột: MaHopDong, TenPhong, Tang, TenLoaiPhong, NgayBatDau, NgayKetThuc,
     *  GiaThue, TienCoc, SoNguoi, TrangThai, GhiChu. */
    public List<Object[]> getHopDong(int maKhach) throws SQLException {
        return queryRows(
                "SELECT h.MaHopDong, p.TenPhong, p.Tang, lp.TenLoaiPhong, h.NgayBatDau, h.NgayKetThuc, "
              + "h.GiaThue, h.TienCoc, h.SoNguoi, h.TrangThai, h.GhiChu "
              + "FROM HopDong h JOIN Phong p ON h.MaPhong = p.MaPhong "
              + "JOIN LoaiPhong lp ON p.MaLoaiPhong = lp.MaLoaiPhong "
              + "WHERE h.MaKhach = ? ORDER BY h.NgayBatDau DESC", maKhach);
    }

    /** Các hóa đơn của người thuê. Cột: MaHoaDon, TenPhong, Thang, Nam, NgayLap, TienPhong, TongTien,
     *  TrangThai, NgayThanhToan, GhiChu. */
    public List<Object[]> getHoaDon(int maKhach) throws SQLException {
        return queryRows(
                "SELECT hd.MaHoaDon, p.TenPhong, hd.Thang, hd.Nam, hd.NgayLap, hd.TienPhong, hd.TongTien, "
              + "hd.TrangThai, hd.NgayThanhToan, hd.GhiChu "
              + "FROM HoaDon hd JOIN HopDong h ON hd.MaHopDong = h.MaHopDong "
              + "JOIN Phong p ON h.MaPhong = p.MaPhong "
              + "WHERE h.MaKhach = ? ORDER BY hd.Nam DESC, hd.Thang DESC", maKhach);
    }

    /** Chi tiết một hóa đơn; điều kiện MaKhach đảm bảo chỉ xem được hóa đơn của chính mình.
     *  Cột: TenDichVu, DonViTinh, ChiSoCu, ChiSoMoi, SoLuong, DonGia, ThanhTien. */
    public List<Object[]> getChiTietHoaDon(int maHoaDon, int maKhach) throws SQLException {
        return queryRows(
                "SELECT dv.TenDichVu, dv.DonViTinh, ct.ChiSoCu, ct.ChiSoMoi, ct.SoLuong, ct.DonGia, ct.ThanhTien "
              + "FROM ChiTietHoaDon ct JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu "
              + "JOIN HoaDon hd ON ct.MaHoaDon = hd.MaHoaDon "
              + "JOIN HopDong h ON hd.MaHopDong = h.MaHopDong "
              + "WHERE ct.MaHoaDon = ? AND h.MaKhach = ? ORDER BY ct.MaCT", maHoaDon, maKhach);
    }

    // ---------- hỗ trợ ----------
    private void ghiThamSo(PreparedStatement ps, KhachThue kt) throws SQLException {
        ps.setString(1, kt.getHoTen());
        if (kt.getNgaySinh() == null) ps.setNull(2, Types.DATE);
        else ps.setDate(2, Date.valueOf(kt.getNgaySinh()));
        ps.setString(3, kt.getGioiTinh());
        ps.setString(4, kt.getCccd());
        ps.setString(5, kt.getSdt());
        ps.setString(6, kt.getEmail());
        ps.setString(7, kt.getQueQuan());
    }

    private List<KhachThue> query(String sql, String... params) throws SQLException {
        List<KhachThue> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private KhachThue map(ResultSet rs) throws SQLException {
        Date d = rs.getDate("NgaySinh");
        KhachThue kt = new KhachThue(rs.getInt("MaKhach"), rs.getString("HoTen"),
                d == null ? null : d.toLocalDate(), rs.getString("GioiTinh"),
                rs.getString("CCCD"), rs.getString("SDT"), rs.getString("Email"), rs.getString("QueQuan"));
        kt.setTenDangNhap(rs.getString("TenDangNhap"));
        kt.setTaiKhoanHoatDong(rs.getBoolean("TKTrangThai"));
        return kt;
    }

    /** Chạy câu SELECT và trả về từng dòng dưới dạng Object[] (ngày được đổi sang LocalDate). */
    private List<Object[]> queryRows(String sql, Object... params) throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                int cols = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] row = new Object[cols];
                    for (int i = 0; i < cols; i++) {
                        Object v = rs.getObject(i + 1);
                        row[i] = (v instanceof Date) ? ((Date) v).toLocalDate() : v;
                    }
                    rows.add(row);
                }
            }
        }
        return rows;
    }
}