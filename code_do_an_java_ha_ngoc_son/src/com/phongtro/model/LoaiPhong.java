package com.phongtro.model;

import java.math.BigDecimal;

public class LoaiPhong {
    private int maLoaiPhong;
    private String tenLoaiPhong;
    private BigDecimal dienTich;
    private BigDecimal giaThue;
    private String moTa;

    public LoaiPhong() {}

    public LoaiPhong(int maLoaiPhong, String tenLoaiPhong, BigDecimal dienTich, BigDecimal giaThue, String moTa) {
        this.maLoaiPhong = maLoaiPhong;
        this.tenLoaiPhong = tenLoaiPhong;
        this.dienTich = dienTich;
        this.giaThue = giaThue;
        this.moTa = moTa;
    }

    public int getMaLoaiPhong() { return maLoaiPhong; }
    public void setMaLoaiPhong(int maLoaiPhong) { this.maLoaiPhong = maLoaiPhong; }
    public String getTenLoaiPhong() { return tenLoaiPhong; }
    public void setTenLoaiPhong(String tenLoaiPhong) { this.tenLoaiPhong = tenLoaiPhong; }
    public BigDecimal getDienTich() { return dienTich; }
    public void setDienTich(BigDecimal dienTich) { this.dienTich = dienTich; }
    public BigDecimal getGiaThue() { return giaThue; }
    public void setGiaThue(BigDecimal giaThue) { this.giaThue = giaThue; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    /** JComboBox hiển thị tên loại phòng nhờ toString(). */
    @Override
    public String toString() { return tenLoaiPhong; }
}