package com.phongtro.view;

import com.phongtro.dao.TaiKhoanDAO;
import com.phongtro.model.TaiKhoan;

import com.phongtro.ui.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Quản lý nhân viên = quản lý các tài khoản trong bảng TaiKhoan (chỉ Admin được mở). */
public class NhanVienFrame extends JFrame {
    private static final String HOAT_DONG = "Hoạt động";
    private static final String DA_KHOA = "Đã khóa";

    private final TaiKhoanDAO dao = new TaiKhoanDAO();
    private final TaiKhoan nguoiDung; // tài khoản Admin đang đăng nhập

    private final JTextField txtTenDN = new JTextField();
    private final JTextField txtHoTen = new JTextField();
    private final JPasswordField txtMatKhau = new JPasswordField();
    private final JComboBox<String> cboVaiTro = new JComboBox<>(new String[]{"NhanVien", "Admin"});
    private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{HOAT_DONG, DA_KHOA});

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Tên đăng nhập", "Họ tên", "Vai trò", "Trạng thái"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private List<TaiKhoan> data = new ArrayList<>();
    private String dangChon = null; // tên đăng nhập của dòng đang chọn

    public NhanVienFrame(TaiKhoan nguoiDung) {
        this.nguoiDung = nguoiDung;
        setTitle("Quản lý nhân viên");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(940, 650);
        setMinimumSize(new Dimension(820, 560));
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
        JLabel hint = new JLabel("* Khi sửa: để trống mật khẩu nếu không muốn đổi");
        hint.setForeground(Color.GRAY);

        JPanel form = new JPanel(new GridLayout(3, 4, 12, 8));
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Thông tin nhân viên", TitledBorder.LEFT, TitledBorder.TOP));
        form.add(new JLabel("Tên đăng nhập:")); form.add(txtTenDN);
        form.add(new JLabel("Họ tên:"));        form.add(txtHoTen);
        form.add(new JLabel("Mật khẩu:"));      form.add(txtMatKhau);
        form.add(new JLabel("Vai trò:"));       form.add(cboVaiTro);
        form.add(new JLabel("Trạng thái:"));    form.add(cboTrangThai);
        form.add(new JLabel());                 form.add(hint);

        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnLamMoi = new JButton("Làm mới");
        btnThem.addActionListener(e -> them());
        btnSua.addActionListener(e -> sua());
        btnXoa.addActionListener(e -> xoa());
        btnLamMoi.addActionListener(e -> lamMoi());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.add(btnThem);
        actions.add(btnSua);
        actions.add(btnXoa);
        actions.add(btnLamMoi);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.add(form, BorderLayout.NORTH);
        top.add(actions, BorderLayout.CENTER);
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
    private void taiTatCa() {
        try {
            data = dao.getAll();
            model.setRowCount(0);
            for (TaiKhoan tk : data) {
                model.addRow(new Object[]{tk.getTenDangNhap(), tk.getHoTen(), tk.getVaiTro(),
                        tk.isTrangThai() ? HOAT_DONG : DA_KHOA});
            }
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void chonDong() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= data.size()) return;
        TaiKhoan tk = data.get(row);
        dangChon = tk.getTenDangNhap();
        txtTenDN.setText(tk.getTenDangNhap());
        txtTenDN.setEditable(false); // không đổi tên đăng nhập (khóa chính)
        txtHoTen.setText(tk.getHoTen());
        txtMatKhau.setText("");
        cboVaiTro.setSelectedItem(tk.getVaiTro());
        cboTrangThai.setSelectedItem(tk.isTrangThai() ? HOAT_DONG : DA_KHOA);
    }

    private void them() {
        TaiKhoan tk = docForm(true);
        if (tk == null) return;
        try {
            dao.insert(tk);
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
            lamMoi();
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void sua() {
        if (dangChon == null) {
            canhBao("Vui lòng chọn một nhân viên trong bảng để sửa!");
            return;
        }
        TaiKhoan tk = docForm(false);
        if (tk == null) return;
        tk.setTenDangNhap(dangChon);
        if (laChinhMinh(dangChon) && (!"Admin".equals(tk.getVaiTro()) || !tk.isTrangThai())) {
            canhBao("Không thể hạ quyền hoặc khóa chính tài khoản đang đăng nhập!");
            return;
        }
        try {
            if (dao.update(tk) > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                lamMoi();
            } else {
                canhBao("Không tìm thấy tài khoản cần sửa!");
            }
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void xoa() {
        if (dangChon == null) {
            canhBao("Vui lòng chọn một nhân viên trong bảng để xóa!");
            return;
        }
        if (laChinhMinh(dangChon)) {
            canhBao("Không thể xóa tài khoản đang đăng nhập!");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa tài khoản \"" + dangChon + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if (dao.delete(dangChon) > 0) {
                JOptionPane.showMessageDialog(this, "Đã xóa nhân viên!");
                lamMoi();
            } else {
                canhBao("Không tìm thấy tài khoản cần xóa!");
            }
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void lamMoi() {
        dangChon = null;
        txtTenDN.setText("");
        txtTenDN.setEditable(true);
        txtHoTen.setText("");
        txtMatKhau.setText("");
        cboVaiTro.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        taiTatCa();
        table.clearSelection();
        txtTenDN.requestFocus();
    }

    private boolean laChinhMinh(String tenDangNhap) {
        return tenDangNhap.equalsIgnoreCase(nguoiDung.getTenDangNhap());
    }

    /** Đọc và kiểm tra form; khiThem = true thì bắt buộc nhập tên đăng nhập và mật khẩu. */
    private TaiKhoan docForm(boolean khiThem) {
        String ten = txtTenDN.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String mk = new String(txtMatKhau.getPassword());

        if (khiThem) {
            if (ten.isEmpty() || !ten.matches("\\S+") || ten.length() > 50) {
                canhBao("Tên đăng nhập bắt buộc, không có dấu cách, tối đa 50 ký tự!");
                return null;
            }
            if (mk.isEmpty()) {
                canhBao("Vui lòng nhập mật khẩu!");
                return null;
            }
        }
        if (hoTen.isEmpty() || hoTen.length() > 100) {
            canhBao("Họ tên bắt buộc, tối đa 100 ký tự!");
            return null;
        }
        if (mk.length() > 255) {
            canhBao("Mật khẩu tối đa 255 ký tự!");
            return null;
        }
        return new TaiKhoan(ten, mk, hoTen, (String) cboVaiTro.getSelectedItem(),
                HOAT_DONG.equals(cboTrangThai.getSelectedItem()));
    }

    private void canhBao(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }

    private void baoLoi(SQLException ex) {
        String msg;
        switch (ex.getErrorCode()) {
            case 2627:
            case 2601:
                msg = "Tên đăng nhập đã tồn tại!";
                break;
            case 547:
                msg = "Dữ liệu vi phạm ràng buộc (vai trò chỉ được là Admin hoặc NhanVien).";
                break;
            default:
                msg = "Lỗi CSDL: " + ex.getMessage();
        }
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}