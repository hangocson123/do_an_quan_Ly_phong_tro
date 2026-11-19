package com.phongtro.view;

import com.phongtro.dao.LoaiPhongDAO;
import com.phongtro.dao.PhongTroDAO;
import com.phongtro.model.LoaiPhong;
import com.phongtro.model.PhongTro;

import com.phongtro.ui.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PhongTroFrame extends JFrame {
    private final PhongTroDAO dao = new PhongTroDAO();
    private final LoaiPhongDAO loaiDao = new LoaiPhongDAO();

    private final JTextField txtMa = new JTextField();            // tự tăng, chỉ để xem
    private final JTextField txtTen = new JTextField();
    private final JTextField txtTang = new JTextField();
    private final JComboBox<LoaiPhong> cboLoai = new JComboBox<>();
    private final JTextField txtGia = new JTextField();           // lấy từ loại phòng, chỉ để xem
    private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"Trống", "Đang thuê", "Bảo trì"});
    private final JTextField txtGhiChu = new JTextField();
    private final JTextField txtTimKiem = new JTextField(18);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã phòng", "Tên phòng", "Tầng", "Loại phòng", "Giá thuê (VNĐ)", "Trạng thái", "Ghi chú"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final DecimalFormat money = new DecimalFormat("#,##0");
    private List<PhongTro> data = new ArrayList<>(); // dữ liệu đang hiển thị, cùng thứ tự với JTable
    private int maDangChon = -1;                      // MaPhong của dòng đang chọn (-1 = chưa chọn)

    public PhongTroFrame() {
        setTitle("Quản lý phòng trọ");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1120, 720);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.add(buildTop(), BorderLayout.NORTH);
        root.add(buildTable(), BorderLayout.CENTER);
        setContentPane(root);
        UITheme.apply(this);

        taiLoaiPhong();
        taiTatCa();
    }

    // ================= GIAO DIỆN =================
    private JPanel buildTop() {
        txtMa.setEditable(false);
        txtMa.setToolTipText("Mã phòng tự động tăng");
        txtGia.setEditable(false);
        txtGia.setToolTipText("Giá thuê theo loại phòng đã chọn");
        cboLoai.addActionListener(e -> capNhatGia());

        JPanel form = new JPanel(new GridLayout(4, 4, 12, 8));
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Thông tin phòng", TitledBorder.LEFT, TitledBorder.TOP));
        form.add(new JLabel("Mã phòng:"));       form.add(txtMa);
        form.add(new JLabel("Tên phòng:"));      form.add(txtTen);
        form.add(new JLabel("Tầng:"));           form.add(txtTang);
        form.add(new JLabel("Loại phòng:"));     form.add(cboLoai);
        form.add(new JLabel("Giá thuê (VNĐ):")); form.add(txtGia);
        form.add(new JLabel("Trạng thái:"));     form.add(cboTrangThai);
        form.add(new JLabel("Ghi chú:"));        form.add(txtGhiChu);
        form.add(new JLabel());                  form.add(new JLabel());

        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnLamMoi = new JButton("Làm mới");
        JButton btnTim = new JButton("Tìm kiếm");

        btnThem.addActionListener(e -> them());
        btnSua.addActionListener(e -> sua());
        btnXoa.addActionListener(e -> xoa());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnTim.addActionListener(e -> timKiem());
        txtTimKiem.addActionListener(e -> timKiem()); // Enter trong ô tìm kiếm

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.add(btnThem);
        actions.add(btnSua);
        actions.add(btnXoa);
        actions.add(btnLamMoi);

        JPanel search = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        search.add(new JLabel("Từ khóa:"));
        search.add(txtTimKiem);
        search.add(btnTim);

        JPanel bar = new JPanel(new BorderLayout());
        bar.add(actions, BorderLayout.WEST);
        bar.add(search, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.add(form, BorderLayout.NORTH);
        top.add(bar, BorderLayout.CENTER);
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
    private void taiLoaiPhong() {
        try {
            for (LoaiPhong lp : loaiDao.getAll()) {
                cboLoai.addItem(lp);
            }
        } catch (SQLException ex) {
            baoLoi(ex);
        }
        capNhatGia();
    }

    private void capNhatGia() {
        LoaiPhong lp = (LoaiPhong) cboLoai.getSelectedItem();
        txtGia.setText(lp == null ? "" : money.format(lp.getGiaThue()));
    }

    private void hienThi(List<PhongTro> list) {
        data = list;
        model.setRowCount(0);
        for (PhongTro p : list) {
            model.addRow(new Object[]{p.getMaPhong(), p.getTenPhong(), p.getTang(), p.getTenLoaiPhong(),
                    money.format(p.getGiaThue()), p.getTrangThai(), p.getGhiChu()});
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
        PhongTro p = data.get(row);
        maDangChon = p.getMaPhong();
        txtMa.setText(String.valueOf(p.getMaPhong()));
        txtTen.setText(p.getTenPhong());
        txtTang.setText(String.valueOf(p.getTang()));
        for (int i = 0; i < cboLoai.getItemCount(); i++) {
            if (cboLoai.getItemAt(i).getMaLoaiPhong() == p.getMaLoaiPhong()) {
                cboLoai.setSelectedIndex(i);
                break;
            }
        }
        cboTrangThai.setSelectedItem(p.getTrangThai());
        txtGhiChu.setText(p.getGhiChu() == null ? "" : p.getGhiChu());
    }

    private void them() {
        PhongTro p = docForm();
        if (p == null) return;
        try {
            dao.insert(p);
            JOptionPane.showMessageDialog(this, "Thêm phòng thành công!");
            lamMoi();
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void sua() {
        if (maDangChon <= 0) {
            canhBao("Vui lòng chọn một phòng trong bảng để sửa!");
            return;
        }
        PhongTro p = docForm();
        if (p == null) return;
        p.setMaPhong(maDangChon);
        try {
            if (dao.update(p) > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                lamMoi();
            } else {
                canhBao("Không tìm thấy phòng cần sửa!");
            }
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void xoa() {
        if (maDangChon <= 0) {
            canhBao("Vui lòng chọn một phòng trong bảng để xóa!");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa phòng " + txtTen.getText().trim() + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if (dao.delete(maDangChon) > 0) {
                JOptionPane.showMessageDialog(this, "Đã xóa phòng!");
                lamMoi();
            } else {
                canhBao("Không tìm thấy phòng cần xóa!");
            }
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
            List<PhongTro> kq = dao.search(kw);
            hienThi(kq);
            if (kq.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phòng nào phù hợp.");
            }
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void lamMoi() {
        maDangChon = -1;
        txtMa.setText("");
        txtTen.setText("");
        txtTang.setText("");
        txtGhiChu.setText("");
        txtTimKiem.setText("");
        if (cboLoai.getItemCount() > 0) cboLoai.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        taiTatCa();
        table.clearSelection();
        txtTen.requestFocus();
    }

    /** Đọc và kiểm tra dữ liệu trên form; trả về null nếu không hợp lệ. */
    private PhongTro docForm() {
        String ten = txtTen.getText().trim();
        String tangStr = txtTang.getText().trim();
        String ghiChu = txtGhiChu.getText().trim();
        LoaiPhong lp = (LoaiPhong) cboLoai.getSelectedItem();

        if (ten.isEmpty()) {
            canhBao("Vui lòng nhập Tên phòng!");
            txtTen.requestFocus();
            return null;
        }
        if (ten.length() > 20) {
            canhBao("Tên phòng tối đa 20 ký tự!");
            return null;
        }
        int tang;
        try {
            tang = Integer.parseInt(tangStr);
            if (tang < 1 || tang > 255) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            canhBao("Tầng phải là số nguyên từ 1 đến 255!");
            txtTang.requestFocus();
            return null;
        }
        if (lp == null) {
            canhBao("Chưa có loại phòng nào trong CSDL!");
            return null;
        }
        if (ghiChu.length() > 255) {
            canhBao("Ghi chú tối đa 255 ký tự!");
            return null;
        }
        return new PhongTro(0, ten, tang, lp.getMaLoaiPhong(), lp.getTenLoaiPhong(), lp.getGiaThue(),
                (String) cboTrangThai.getSelectedItem(), ghiChu.isEmpty() ? null : ghiChu);
    }

    private void canhBao(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }

    private void baoLoi(SQLException ex) {
        String msg;
        switch (ex.getErrorCode()) {
            case 547:
                msg = "Thao tác vi phạm ràng buộc dữ liệu (ví dụ: phòng đã có hợp đồng nên không thể xóa).";
                break;
            case 2627:
            case 2601:
                msg = "Tên phòng đã tồn tại!";
                break;
            default:
                msg = "Lỗi CSDL: " + ex.getMessage();
        }
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}