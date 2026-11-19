package com.phongtro.view;

import com.phongtro.dao.KhachThueDAO;
import com.phongtro.model.KhachThue;

import com.phongtro.ui.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Màn hình chỉ-xem cho người thuê: thông tin cá nhân, hợp đồng, hóa đơn và chi tiết hóa đơn của chính họ. */
public class NguoiThueFrame extends JFrame {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DecimalFormat fmtTien = new DecimalFormat("#,##0");
    private final DecimalFormat fmtSo = new DecimalFormat("#,##0.#");

    private final KhachThueDAO dao = new KhachThueDAO();
    private final KhachThue kt;

    private final JTable tblHopDong = taoBang("Mã hợp đồng", "Phòng", "Tầng", "Loại phòng", "Ngày bắt đầu",
            "Ngày kết thúc", "Giá thuê", "Tiền cọc", "Số người", "Trạng thái", "Ghi chú");
    private final JTable tblHoaDon = taoBang("Mã hóa đơn", "Phòng", "Tháng", "Năm", "Ngày lập", "Tiền phòng",
            "Tổng tiền", "Trạng thái", "Ngày thanh toán", "Ghi chú");
    private final JTable tblChiTiet = taoBang("Dịch vụ", "Đơn vị", "Chỉ số cũ", "Chỉ số mới", "Số lượng",
            "Đơn giá", "Thành tiền");
    private List<Object[]> hoaDonRows = new ArrayList<>();

    public NguoiThueFrame(KhachThue kt) {
        this.kt = kt;
        setTitle("Thông tin người thuê - " + kt.getHoTen());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 740);
        setMinimumSize(new Dimension(1020, 640));
        setLocationRelativeTo(null);

        JLabel hello = new JLabel("Xin chào, " + kt.getHoTen());
        hello.setFont(new Font("Segoe UI", Font.BOLD, 18));
        hello.setForeground(new Color(0x1F4E79));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Thông tin cá nhân", buildThongTin());
        tabs.addTab("Hợp đồng thuê", new JScrollPane(tblHopDong));
        tabs.addTab("Hóa đơn", buildHoaDon());

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnLogout);

        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.add(hello, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
        UITheme.apply(this);

        taiHopDong();
        taiHoaDon();
    }

    // ================= GIAO DIỆN =================
    private JTable taoBang(String... cols) {
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable t = new JTable(m);
        t.setRowHeight(24);
        t.getTableHeader().setReorderingAllowed(false);
        return t;
    }

    private DefaultTableModel mo(JTable t) {
        return (DefaultTableModel) t.getModel();
    }

    private JPanel buildThongTin() {
        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
        addRow(form, "Mã khách:", String.valueOf(kt.getMaKhach()));
        addRow(form, "Họ tên:", kt.getHoTen());
        addRow(form, "Ngày sinh:", kt.getNgaySinh() == null ? "" : kt.getNgaySinh().format(DATE));
        addRow(form, "Giới tính:", kt.getGioiTinh());
        addRow(form, "CCCD:", kt.getCccd());
        addRow(form, "Số điện thoại:", kt.getSdt());
        addRow(form, "Email:", kt.getEmail() == null ? "" : kt.getEmail());
        addRow(form, "Quê quán:", kt.getQueQuan() == null ? "" : kt.getQueQuan());

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBorder(new EmptyBorder(20, 40, 20, 40));
        wrap.add(form, BorderLayout.NORTH);
        return wrap;
    }

    private void addRow(JPanel form, String label, String value) {
        JTextField tf = new JTextField(value);
        tf.setEditable(false);
        form.add(new JLabel(label));
        form.add(tf);
    }

    private JSplitPane buildHoaDon() {
        tblHoaDon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) taiChiTiet();
        });
        JScrollPane bottom = new JScrollPane(tblChiTiet);
        bottom.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn đang chọn"));
        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tblHoaDon), bottom);
        sp.setResizeWeight(0.5);
        return sp;
    }

    // ================= TẢI DỮ LIỆU =================
    private void taiHopDong() {
        try {
            DefaultTableModel m = mo(tblHopDong);
            m.setRowCount(0);
            for (Object[] r : dao.getHopDong(kt.getMaKhach())) {
                Object[] row = dinhDang(r);
                if (r[5] == null) row[5] = "Không thời hạn"; // NgayKetThuc để trống
                m.addRow(row);
            }
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void taiHoaDon() {
        try {
            hoaDonRows = dao.getHoaDon(kt.getMaKhach());
            DefaultTableModel m = mo(tblHoaDon);
            m.setRowCount(0);
            for (Object[] r : hoaDonRows) {
                m.addRow(dinhDang(r));
            }
            if (!hoaDonRows.isEmpty()) tblHoaDon.setRowSelectionInterval(0, 0);
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    private void taiChiTiet() {
        DefaultTableModel m = mo(tblChiTiet);
        m.setRowCount(0);
        int row = tblHoaDon.getSelectedRow();
        if (row < 0 || row >= hoaDonRows.size()) return;
        int maHoaDon = ((Number) hoaDonRows.get(row)[0]).intValue();
        try {
            for (Object[] r : dao.getChiTietHoaDon(maHoaDon, kt.getMaKhach())) {
                m.addRow(new Object[]{r[0], r[1], so(r[2]), so(r[3]), so(r[4]),
                        fmtTien.format(r[5]), fmtTien.format(r[6])});
            }
        } catch (SQLException ex) {
            baoLoi(ex);
        }
    }

    // ================= HỖ TRỢ ĐỊNH DẠNG =================
    private Object[] dinhDang(Object[] r) {
        Object[] out = new Object[r.length];
        for (int i = 0; i < r.length; i++) {
            Object v = r[i];
            if (v == null) out[i] = "";
            else if (v instanceof LocalDate) out[i] = ((LocalDate) v).format(DATE);
            else if (v instanceof BigDecimal) out[i] = fmtTien.format(v);
            else out[i] = v.toString();
        }
        return out;
    }

    private String so(Object v) {
        return v == null ? "" : fmtSo.format(v);
    }

    private void baoLoi(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}