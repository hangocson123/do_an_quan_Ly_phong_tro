package com.phongtro.view;

import com.phongtro.dao.LoaiPhongDAO;
import com.phongtro.model.LoaiPhong;

import com.phongtro.ui.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/** Quản lý loại phòng và giá thuê niêm yết (chỉ Admin được mở). */
public class LoaiPhongFrame extends JFrame {
    private final LoaiPhongDAO dao = new LoaiPhongDAO();

    private final JTextField txtMa = new JTextField();
    private final JTextField txtTen = new JTextField();
    private final JTextField txtDienTich = new JTextField();
    private final JTextField txtGia = new JTextField();
    private final JTextField txtMoTa = new JTextField();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã loại", "Tên loại phòng", "Diện tích (m²)", "Giá thuê (VNĐ)", "Mô tả"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final DecimalFormat fmtTien = new DecimalFormat("#,##0");
    private final DecimalFormat fmtDienTich = new DecimalFormat("0.0");
    private List<LoaiPhong> data = new ArrayList<>();
    private LoaiPhong dangChon = null;

    public LoaiPhongFrame() {
        setTitle("Quản lý loại phòng & giá thuê");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setMinimumSize(new Dimension(860, 560));
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
        txtMa.setToolTipText("Mã loại tự động tăng");
        txtGia.setToolTipText("Chỉ nhập số, ví dụ 2800000 hoặc 2.800.000");

        JLabel hint = new JLabel("* Giá ở đây là giá niêm yết của các phòng thuộc loại này. "
                + "Hợp đồng và hóa đơn đã có không bị đổi theo.");
        hint.setForeground(Color.GRAY);

        JPanel form = new JPanel(new GridLayout(3, 4, 12, 8));
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Thông tin loại phòng", TitledBorder.LEFT, TitledBorder.TOP));
        form.add(new JLabel("Mã loại:"));         form.add(txtMa);
        form.add(new JLabel("Tên loại phòng:"));  form.add(txtTen);
        form.add(new JLabel("Diện tích (m²):"));  form.add(txtDienTich);
        form.add(new JLabel("Giá thuê (VNĐ):"));  form.add(txtGia);
        form.add(new JLabel("Mô tả:"));           form.add(txtMoTa);
        form.add(new JLabel());                   form.add(new JLabel());

        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa / đổi giá");
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

        JPanel mid = new JPanel(new BorderLayout(0, 8));
        mid.add(hint, BorderLayout.NORTH);
        mid.add(actions, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.add(form, BorderLayout.NORTH);
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
    private void taiTatCa() {
        try {
            data = dao.getAll();
            model.setRowCount(0);
            for (LoaiPhong lp : data) {
                model.addRow(new Object[]{lp.getMaLoaiPhong(), lp.getTenLoaiPhong(),
                        fmtDienTich.format(lp.getDienTich()), fmtTien.format(lp.getGiaThue()),
                        lp.getMoTa() == null ? "" : lp.getMoTa()});
            }
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void chonDong() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= data.size()) return;
        LoaiPhong lp = data.get(row);
        dangChon = lp;
        txtMa.setText(String.valueOf(lp.getMaLoaiPhong()));
        txtTen.setText(lp.getTenLoaiPhong());
        txtDienTich.setText(lp.getDienTich().toPlainString());
        txtGia.setText(lp.getGiaThue().toBigInteger().toString());
        txtMoTa.setText(lp.getMoTa() == null ? "" : lp.getMoTa());
    }

    private void them() {
        LoaiPhong lp = docForm();
        if (lp == null) return;
        try {
            dao.insert(lp);
            JOptionPane.showMessageDialog(this, "Thêm loại phòng thành công!");
            lamMoi();
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void sua() {
        if (dangChon == null) {
            canhBao("Vui lòng chọn một loại phòng trong bảng để sửa!");
            return;
        }
        LoaiPhong lp = docForm();
        if (lp == null) return;
        lp.setMaLoaiPhong(dangChon.getMaLoaiPhong());

        if (lp.getGiaThue().compareTo(dangChon.getGiaThue()) != 0) { // đổi giá -> hỏi lại cho chắc
            int choice = JOptionPane.showConfirmDialog(this,
                    "Đổi giá loại \"" + dangChon.getTenLoaiPhong() + "\" từ "
                    + fmtTien.format(dangChon.getGiaThue()) + " thành " + fmtTien.format(lp.getGiaThue()) + " VNĐ?\n"
                    + "Giá mới áp dụng cho các phòng thuộc loại này.\n"
                    + "Hợp đồng và hóa đơn đã có không bị thay đổi.",
                    "Xác nhận đổi giá", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) return;
        }
        try {
            if (dao.update(lp) > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                lamMoi();
            } else {
                canhBao("Không tìm thấy loại phòng cần sửa!");
            }
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void xoa() {
        if (dangChon == null) {
            canhBao("Vui lòng chọn một loại phòng trong bảng để xóa!");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa loại phòng \"" + dangChon.getTenLoaiPhong() + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if (dao.delete(dangChon.getMaLoaiPhong()) > 0) {
                JOptionPane.showMessageDialog(this, "Đã xóa loại phòng!");
                lamMoi();
            } else {
                canhBao("Không tìm thấy loại phòng cần xóa!");
            }
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void lamMoi() {
        dangChon = null;
        txtMa.setText("");
        txtTen.setText("");
        txtDienTich.setText("");
        txtGia.setText("");
        txtMoTa.setText("");
        taiTatCa();
        table.clearSelection();
        txtTen.requestFocus();
    }

    /** Đọc và kiểm tra form theo các ràng buộc của bảng LoaiPhong; null nếu không hợp lệ. */
    private LoaiPhong docForm() {
        String ten = txtTen.getText().trim();
        String dtStr = txtDienTich.getText().trim().replace(',', '.');
        String giaStr = txtGia.getText().trim().replaceAll("[.,\\s]", ""); // cho phép gõ 2.800.000
        String moTa = txtMoTa.getText().trim();

        if (ten.isEmpty() || ten.length() > 50) {
            canhBao("Tên loại phòng bắt buộc, tối đa 50 ký tự!");
            txtTen.requestFocus();
            return null;
        }
        BigDecimal dienTich;
        try {
            dienTich = new BigDecimal(dtStr);
        } catch (NumberFormatException ex) {
            canhBao("Diện tích phải là số (ví dụ 25 hoặc 25.5)!");
            txtDienTich.requestFocus();
            return null;
        }
        if (dienTich.signum() <= 0 || dienTich.compareTo(new BigDecimal("9999.9")) > 0) {
            canhBao("Diện tích phải lớn hơn 0 và tối đa 9999.9 m²!");
            txtDienTich.requestFocus();
            return null;
        }
        if (!giaStr.matches("\\d{1,12}")) {
            canhBao("Giá thuê phải là số không âm (tối đa 12 chữ số)!");
            txtGia.requestFocus();
            return null;
        }
        if (moTa.length() > 255) {
            canhBao("Mô tả tối đa 255 ký tự!");
            return null;
        }
        return new LoaiPhong(0, ten, dienTich.setScale(1, RoundingMode.HALF_UP), new BigDecimal(giaStr),
                moTa.isEmpty() ? null : moTa);
    }

    private void canhBao(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }

    private void baoLoi(SQLException ex) {
        String msg;
        switch (ex.getErrorCode()) {
            case 547:
                msg = "Không thể xóa: đang có phòng thuộc loại này (hoặc dữ liệu vi phạm ràng buộc).";
                break;
            case 2627:
            case 2601:
                msg = "Tên loại phòng đã tồn tại!";
                break;
            default:
                msg = "Lỗi CSDL: " + ex.getMessage();
        }
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}