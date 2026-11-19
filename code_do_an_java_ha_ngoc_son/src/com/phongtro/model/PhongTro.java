package com.phongtro.model;

import java.math.BigDecimal;

/** Ánh xạ bảng Phong (kèm tên loại phòng và giá thuê lấy từ bảng LoaiPhong). */
public class PhongTro {
    private int maPhong;
    private String tenPhong;
    private int tang;
    private int maLoaiPhong;
    private String tenLoaiPhong;
    private BigDecimal giaThue;
    private String trangThai;
    private String ghiChu;

    public PhongTro() {}

    public PhongTro(int maPhong, String tenPhong, int tang, int maLoaiPhong, String tenLoaiPhong,
                    BigDecimal giaThue, String trangThai, String ghiChu) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.tang = tang;
        this.maLoaiPhong = maLoaiPhong;
        this.tenLoaiPhong = tenLoaiPhong;
        this.giaThue = giaThue;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public int getMaPhong() { return maPhong; }
    public void setMaPhong(int maPhong) { this.maPhong = maPhong; }
    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }
    public int getTang() { return tang; }
    public void setTang(int tang) { this.tang = tang; }
    public int getMaLoaiPhong() { return maLoaiPhong; }
    public void setMaLoaiPhong(int maLoaiPhong) { this.maLoaiPhong = maLoaiPhong; }
    public String getTenLoaiPhong() { return tenLoaiPhong; }
    public void setTenLoaiPhong(String tenLoaiPhong) { this.tenLoaiPhong = tenLoaiPhong; }
    public BigDecimal getGiaThue() { return giaThue; }
    public void setGiaThue(BigDecimal giaThue) { this.giaThue = giaThue; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}