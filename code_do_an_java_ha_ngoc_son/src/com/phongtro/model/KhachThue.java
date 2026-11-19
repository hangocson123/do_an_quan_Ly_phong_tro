package com.phongtro.model;

import java.time.LocalDate;

public class KhachThue {
    private int maKhach;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String cccd;
    private String sdt;
    private String email;
    private String queQuan;
    private String tenDangNhap;        // tài khoản đăng nhập của người thuê (null nếu chưa có)
    private boolean taiKhoanHoatDong;  // trạng thái tài khoản

    public KhachThue() {}

    public KhachThue(int maKhach, String hoTen, LocalDate ngaySinh, String gioiTinh,
                     String cccd, String sdt, String email, String queQuan) {
        this.maKhach = maKhach;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.cccd = cccd;
        this.sdt = sdt;
        this.email = email;
        this.queQuan = queQuan;
    }

    public int getMaKhach() { return maKhach; }
    public void setMaKhach(int maKhach) { this.maKhach = maKhach; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getQueQuan() { return queQuan; }
    public void setQueQuan(String queQuan) { this.queQuan = queQuan; }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public boolean isTaiKhoanHoatDong() { return taiKhoanHoatDong; }
    public void setTaiKhoanHoatDong(boolean taiKhoanHoatDong) { this.taiKhoanHoatDong = taiKhoanHoatDong; }
}