package com.phongtro.view;

import com.phongtro.dao.KhachThueDAO;
import com.phongtro.dao.TaiKhoanNguoiThueDAO;
import com.phongtro.model.KhachThue;

import com.phongtro.ui.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

/** Quản lý người thuê và cấp tài khoản đăng nhập cho họ. */
public class KhachThueFrame extends JFrame {
    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final String HOAT_DONG = "Hoạt động";
    private static final String DA_KHOA = "Đã khóa";

    private final KhachThueDAO dao = new KhachThueDAO();
    private final TaiKhoanNguoiThueDAO tkDao = new TaiKhoanNguoiThueDAO();

    // thông tin người thuê
    private final JTextField txtMa = new JTextField();
    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtNgaySinh = new JTextField();
    private final JComboBox<String> cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
    private final JTextField txtCCCD = new JTextField();
    private final JTextField txtSDT = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtQueQuan = new JTextField();
    // tài khoản đăng nhập
    private final JTextField txtTenDN = new JTextField();
    private final JPasswordField txtMatKhau = new JPasswordField();
    private final JComboBox<String> cboTrangThaiTK = new JComboBox<>(new String[]{HOAT_DONG, DA_KHOA});
    private final JTextField txtTimKiem = new JTextField(16);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã khách", "Họ tên", "Ngày sinh", "Giới tính", "CCCD", "Số điện thoại",
                    "Email", "Quê quán", "Tài khoản"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private List<KhachThue> data = new ArrayList<>();
    private KhachThue dangChon = null;

    public KhachThueFrame() {
        setTitle("Quản lý khách thuê");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1220, 800);
        setMinimumSize(new Dimension(1040, 680));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.add(buildTop(), BorderLayout.NORTH);
        root.add(buildTable(), BorderLayout.CENTER);
        setContentPane(root);
        UITheme.apply(this);

        taiTatCa();
    }

    // ================= GIAO DIỆN =================
    private JPanel buildTop() {
        txtMa.setEditable(false);
        txtMa.setToolTipText("Mã khách tự động tăng");
        txtNgaySinh.setToolTipText("Định dạng dd/MM/yyyy, ví dụ 15/01/2000 (có thể để trống)");

        JPanel formKhach = new JPanel(new GridLayout(4, 4, 12, 8));
        formKhach.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Thông tin người thuê", TitledBorder.LEFT, TitledBorder.TOP));
        formKhach.add(new JLabel("Mã khách:"));              formKhach.add(txtMa);
        formKhach.add(new JLabel("Họ tên:"));                formKhach.add(txtHoTen);
        formKhach.add(new JLabel("Ngày sinh (dd/MM/yyyy):")); formKhach.add(txtNgaySinh);
        formKhach.add(new JLabel("Giới tính:"));             formKhach.add(cboGioiTinh);
        formKhach.add(new JLabel("CCCD:"));                  formKhach.add(txtCCCD);
        formKhach.add(new JLabel("Số điện thoại:"));         formKhach.add(txtSDT);
        formKhach.add(new JLabel("Email:"));                 formKhach.add(txtEmail);
        formKhach.add(new JLabel("Quê quán:"));              formKhach.add(txtQueQuan);

        JPanel grid = new JPanel(new GridLayout(1, 6, 12, 8));
        grid.add(new JLabel("Tên đăng nhập:")); grid.add(txtTenDN);
        grid.add(new JLabel("Mật khẩu:"));      grid.add(txtMatKhau);
        grid.add(new JLabel("Trạng thái TK:")); grid.add(cboTrangThaiTK);
        JLabel hint = new JLabel("Chọn người thuê trong bảng, nhập tài khoản rồi bấm \"Cấp / cập nhật tài khoản\". "
                + "Khi đã có tài khoản: để trống mật khẩu nếu không đổi.");
        hint.setForeground(Color.GRAY);
        JPanel formTK = new JPanel(new BorderLayout(0, 6));
        formTK.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Tài khoản đăng nhập của người thuê", TitledBorder.LEFT, TitledBorder.TOP));
        formTK.add(grid, BorderLayout.CENTER);
        formTK.add(hint, BorderLayout.SOUTH);

        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnTaiKhoan = new JButton("Cấp / cập nhật tài khoản");
        JButton btnLamMoi = new JButton("Làm mới");
        JButton btnTim = new JButton("Tìm kiếm");
        btnThem.addActionListener(e -> them());
        btnSua.addActionListener(e -> sua());
        btnXoa.addActionListener(e -> xoa());
        btnTaiKhoan.addActionListener(e -> capTaiKhoan());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnTim.addActionListener(e -> timKiem());
        txtTimKiem.addActionListener(e -> timKiem());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.add(btnThem);
        actions.add(btnSua);
        actions.add(btnXoa);
        actions.add(btnTaiKhoan);
        actions.add(btnLamMoi);

        JPanel search = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        search.add(new JLabel("Từ khóa:"));
        search.add(txtTimKiem);
        search.add(btnTim);

        JPanel bar = new JPanel(new BorderLayout());
        bar.add(actions, BorderLayout.WEST);
        bar.add(search, BorderLayout.EAST);

        JPanel mid = new JPanel(new BorderLayout(0, 10));
        mid.add(formTK, BorderLayout.NORTH);
        mid.add(bar, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.add(formKhach, BorderLayout.NORTH);
        top.add(mid, BorderLayout.CENTER);
        return top;
    }

    private JScrollPane buildTable() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) chonDong();
        });
        return new JScrollPane(table);
    }

    // ================= XỬ LÝ DỮ LIỆU =================
    private void hienThi(List<KhachThue> list) {
        data = list;
        model.setRowCount(0);
        for (KhachThue k : list) {
            String tk = k.getTenDangNhap() == null ? "(chưa có)"
                    : k.getTenDangNhap() + (k.isTaiKhoanHoatDong() ? "" : " (đã khóa)");
            model.addRow(new Object[]{k.getMaKhach(), k.getHoTen(),
                    k.getNgaySinh() == null ? "" : k.getNgaySinh().format(DATE), k.getGioiTinh(),
                    k.getCccd(), k.getSdt(), k.getEmail() == null ? "" : k.getEmail(),
                    k.getQueQuan() == null ? "" : k.getQueQuan(), tk});
        }
    }

    private void taiTatCa() {
        try {
            hienThi(dao.getAll());
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void chonDong() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= data.size()) return;
        KhachThue k = data.get(row);
        dangChon = k;
        txtMa.setText(String.valueOf(k.getMaKhach()));
        txtHoTen.setText(k.getHoTen());
        txtNgaySinh.setText(k.getNgaySinh() == null ? "" : k.getNgaySinh().format(DATE));
        cboGioiTinh.setSelectedItem(k.getGioiTinh());
        txtCCCD.setText(k.getCccd());
        txtSDT.setText(k.getSdt());
        txtEmail.setText(k.getEmail() == null ? "" : k.getEmail());
        txtQueQuan.setText(k.getQueQuan() == null ? "" : k.getQueQuan());

        boolean coTaiKhoan = k.getTenDangNhap() != null;
        txtTenDN.setText(coTaiKhoan ? k.getTenDangNhap() : "");
        txtTenDN.setEditable(!coTaiKhoan); // đã có tài khoản thì không đổi tên đăng nhập
        txtMatKhau.setText("");
        cboTrangThaiTK.setSelectedItem(!coTaiKhoan || k.isTaiKhoanHoatDong() ? HOAT_DONG : DA_KHOA);
    }

    private void them() {
        KhachThue k = docForm();
        if (k == null) return;
        try {
            dao.insert(k);
            JOptionPane.showMessageDialog(this, "Thêm người thuê thành công!\n"
                    + "Chọn người đó trong bảng rồi bấm \"Cấp / cập nhật tài khoản\" để tạo tài khoản đăng nhập.");
            lamMoi();
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void sua() {
        if (dangChon == null) {
            canhBao("Vui lòng chọn một người thuê trong bảng để sửa!");
            return;
        }
        KhachThue k = docForm();
        if (k == null) return;
        k.setMaKhach(dangChon.getMaKhach());
        try {
            if (dao.update(k) > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                lamMoi();
            } else {
                canhBao("Không tìm thấy người thuê cần sửa!");
            }
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void xoa() {
        if (dangChon == null) {
            canhBao("Vui lòng chọn một người thuê trong bảng để xóa!");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa người thuê \"" + dangChon.getHoTen() + "\"?\n"
                + "Tài khoản đăng nhập của họ (nếu có) cũng sẽ bị xóa.",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if (dao.delete(dangChon.getMaKhach()) > 0) {
                JOptionPane.showMessageDialog(this, "Đã xóa người thuê!");
                lamMoi();
            } else {
                canhBao("Không tìm thấy người thuê cần xóa!");
            }
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    /** Chưa có tài khoản -> tạo mới; đã có -> đổi mật khẩu (nếu nhập) và trạng thái. */
    private void capTaiKhoan() {
        if (dangChon == null) {
            canhBao("Vui lòng chọn một người thuê trong bảng!");
            return;
        }
        boolean daCo = dangChon.getTenDangNhap() != null;
        String ten = daCo ? dangChon.getTenDangNhap() : txtTenDN.getText().trim();
        String mk = new String(txtMatKhau.getPassword());
        boolean hoatDong = HOAT_DONG.equals(cboTrangThaiTK.getSelectedItem());

        if (!daCo) {
            if (ten.isEmpty() || !ten.matches("\\S+") || ten.length() > 50) {
                canhBao("Tên đăng nhập bắt buộc, không có dấu cách, tối đa 50 ký tự!");
                txtTenDN.requestFocus();
                return;
            }
            if (mk.isEmpty()) {
                canhBao("Vui lòng nhập mật khẩu cho tài khoản mới!");
                txtMatKhau.requestFocus();
                return;
            }
        }
        if (mk.length() > 255) {
            canhBao("Mật khẩu tối đa 255 ký tự!");
            return;
        }
        try {
            if (daCo) {
                tkDao.update(dangChon.getMaKhach(), mk, hoatDong);
                JOptionPane.showMessageDialog(this, "Đã cập nhật tài khoản \"" + ten + "\"!");
            } else {
                tkDao.insert(dangChon.getMaKhach(), ten, mk, hoatDong);
                JOptionPane.showMessageDialog(this, "Đã tạo tài khoản \"" + ten + "\" cho " + dangChon.getHoTen() + "!");
            }
            lamMoi();
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void timKiem() {
        String kw = txtTimKiem.getText().trim();
        if (kw.isEmpty()) {
            taiTatCa();
            return;
        }
        try {
            List<KhachThue> kq = dao.search(kw);
            hienThi(kq);
            if (kq.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy người thuê nào phù hợp.");
            }
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void lamMoi() {
        dangChon = null;
        txtMa.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        txtCCCD.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtQueQuan.setText("");
        txtTenDN.setText("");
        txtTenDN.setEditable(true);
        txtMatKhau.setText("");
        txtTimKiem.setText("");
        cboGioiTinh.setSelectedIndex(0);
        cboTrangThaiTK.setSelectedIndex(0);
        taiTatCa();
        table.clearSelection();
        txtHoTen.requestFocus();
    }

    /** Đọc và kiểm tra form theo đúng các ràng buộc CHECK của bảng KhachThue; null nếu không hợp lệ. */
    private KhachThue docForm() {
        String hoTen = txtHoTen.getText().trim();
        String ns = txtNgaySinh.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String que = txtQueQuan.getText().trim();

        if (hoTen.isEmpty() || hoTen.length() > 100) {
            canhBao("Họ tên bắt buộc, tối đa 100 ký tự!");
            txtHoTen.requestFocus();
            return null;
        }
        LocalDate ngaySinh = null;
        if (!ns.isEmpty()) {
            try {
                ngaySinh = LocalDate.parse(ns, DATE);
            } catch (DateTimeParseException ex) {
                canhBao("Ngày sinh không hợp lệ! Nhập theo dd/MM/yyyy, ví dụ 15/01/2000.");
                txtNgaySinh.requestFocus();
                return null;
            }
            if (ngaySinh.isAfter(LocalDate.now())) {
                canhBao("Ngày sinh không được ở tương lai!");
                return null;
            }
        }
        if (!cccd.matches("\\d{12}|\\d{9}")) {
            canhBao("CCCD phải gồm 12 chữ số (hoặc 9 chữ số với CMND cũ)!");
            txtCCCD.requestFocus();
            return null;
        }
        if (!sdt.matches("\\d{10,11}")) {
            canhBao("Số điện thoại phải gồm 10 hoặc 11 chữ số!");
            txtSDT.requestFocus();
            return null;
        }
        if (email.length() > 100) {
            canhBao("Email tối đa 100 ký tự!");
            return null;
        }
        if (que.length() > 200) {
            canhBao("Quê quán tối đa 200 ký tự!");
            return null;
        }
        return new KhachThue(0, hoTen, ngaySinh, (String) cboGioiTinh.getSelectedItem(), cccd, sdt,
                email.isEmpty() ? null : email, que.isEmpty() ? null : que);
    }

    private void canhBao(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }

    private void baoLoi(SQLException ex) {
        int code = ex.getErrorCode();
        String detail = String.valueOf(ex.getMessage());
        String msg;
        if (code == 208) {
            msg = "Chưa có bảng TaiKhoanNguoiThue trong CSDL. Hãy chạy script SQL tạo bảng trước!";
        } else if (code == 547) {
            msg = "Người thuê đã có hợp đồng nên không thể xóa (hoặc dữ liệu vi phạm ràng buộc).";
        } else if (code == 2627 || code == 2601) {
            msg = detail.contains("CCCD") ? "CCCD này đã tồn tại!"
                : detail.contains("TaiKhoanNguoiThue") ? "Tên đăng nhập này đã tồn tại!"
                : "Dữ liệu bị trùng!";
        } else {
            msg = "Lỗi CSDL: " + detail;
        }
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}