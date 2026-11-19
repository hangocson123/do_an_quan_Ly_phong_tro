package com.phongtro.view;

import com.phongtro.dao.KhachThueDAO;
import com.phongtro.dao.TaiKhoanDAO;
import com.phongtro.model.KhachThue;
import com.phongtro.model.TaiKhoan;
import com.phongtro.ui.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

/** Màn hình đăng nhập theo phong cách AN CƯ - Property Desk. */
public class LoginFrame extends JFrame {
    private final JComboBox<String> cboDoiTuong = new JComboBox<>(
            new String[]{"Nhân viên / Quản trị", "Người thuê"});
    private final JTextField txtUser = new JTextField(20);
    private final JPasswordField txtPass = new JPasswordField(20);
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final KhachThueDAO khachThueDAO = new KhachThueDAO();
    private JButton btnLogin;
    private JLabel lblStatus;

    public LoginFrame() {
        setTitle("AN CƯ | Đăng nhập hệ thống quản lý phòng trọ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 560));
        setSize(980, 620);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(UITheme.PAPER);
        root.add(buildBrandPanel());
        root.add(buildFormPanel());
        setContentPane(root);
    }

    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.FOREST);
        panel.setBorder(new EmptyBorder(48, 42, 38, 38));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel mark = new JPanel(new GridBagLayout());
        mark.setBackground(UITheme.COPPER);
        mark.setPreferredSize(new Dimension(58, 58));
        mark.setMaximumSize(new Dimension(58, 58));
        mark.setMinimumSize(new Dimension(58, 58));
        JLabel markText = new JLabel("A");
        markText.setFont(new Font(Font.SERIF, Font.BOLD, 31));
        markText.setForeground(Color.WHITE);
        mark.add(markText);
        mark.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brand = new JLabel("AN CƯ");
        brand.setFont(new Font(Font.SERIF, Font.BOLD, 38));
        brand.setForeground(new Color(250, 247, 238));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel english = new JLabel("PROPERTY MANAGEMENT");
        english.setFont(new Font("Segoe UI", Font.BOLD, 11));
        english.setForeground(new Color(202, 211, 196));
        english.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel divider = new JLabel("━━━━━━━━━━━━━━━━━━━━━━━━━━");
        divider.setForeground(UITheme.COPPER);
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statement = new JLabel("<html>Quản lý nơi ở,<br>chăm chút từng trải nghiệm.</html>");
        statement.setFont(new Font(Font.SERIF, Font.BOLD, 27));
        statement.setForeground(Color.WHITE);
        statement.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel description = new JLabel("<html>Một không gian quản trị gọn gàng cho phòng trọ,<br>khách thuê và vận hành hằng ngày.</html>");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        description.setForeground(new Color(206, 217, 207));
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(mark);
        panel.add(Box.createVerticalStrut(24));
        panel.add(brand);
        panel.add(Box.createVerticalStrut(3));
        panel.add(english);
        panel.add(Box.createVerticalStrut(22));
        panel.add(divider);
        panel.add(Box.createVerticalStrut(64));
        panel.add(statement);
        panel.add(Box.createVerticalStrut(18));
        panel.add(description);
        panel.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("AN CƯ  •  PROPERTY DESK  /  2026");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer.setForeground(new Color(169, 188, 173));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(footer);
        return panel;
    }

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.PAPER);
        outer.setBorder(new EmptyBorder(38, 44, 38, 44));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("CỔNG ĐĂNG NHẬP");
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 11));
        eyebrow.setForeground(UITheme.COPPER_DARK);
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Chào mừng trở lại");
        title.setFont(new Font(Font.SERIF, Font.BOLD, 29));
        title.setForeground(UITheme.FOREST);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Đăng nhập để tiếp tục quản lý hệ thống.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(UITheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(eyebrow);
        form.add(Box.createVerticalStrut(10));
        form.add(title);
        form.add(Box.createVerticalStrut(8));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(36));

        addField(form, "ĐĂNG NHẬP VỚI TƯ CÁCH", cboDoiTuong);
        addField(form, "TÊN ĐĂNG NHẬP", txtUser);
        addField(form, "MẬT KHẨU", txtPass);

        cboDoiTuong.addActionListener(e -> {
            txtUser.setText("");
            txtPass.setText("");
            lblStatus.setText(" ");
            txtUser.requestFocusInWindow();
        });

        JCheckBox showPass = new JCheckBox("Hiển thị mật khẩu");
        showPass.setOpaque(false);
        showPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPass.setForeground(UITheme.MUTED);
        showPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPass.addActionListener(e -> txtPass.setEchoChar(showPass.isSelected() ? (char) 0 : '•'));
        form.add(showPass);
        form.add(Box.createVerticalStrut(16));

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(UITheme.DANGER);
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblStatus);
        form.add(Box.createVerticalStrut(8));

        btnLogin = new JButton("ĐĂNG NHẬP  →");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(UITheme.FOREST);
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLogin.setPreferredSize(new Dimension(330, 46));
        btnLogin.addActionListener(e -> dangNhap());
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btnLogin.isEnabled()) btnLogin.setBackground(UITheme.FOREST_LIGHT);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (btnLogin.isEnabled()) btnLogin.setBackground(UITheme.FOREST);
            }
        });
        form.add(btnLogin);

        JButton btnExit = new JButton("Thoát chương trình");
        btnExit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnExit.setForeground(UITheme.MUTED);
        btnExit.setBorderPainted(false);
        btnExit.setContentAreaFilled(false);
        btnExit.setFocusPainted(false);
        btnExit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExit.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExit.addActionListener(e -> System.exit(0));
        form.add(Box.createVerticalStrut(15));
        form.add(btnExit);

        outer.add(form);
        getRootPane().setDefaultButton(btnLogin);
        return outer;
    }

    private void addField(JPanel form, String caption, JComponent input) {
        JLabel label = new JLabel(caption);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(UITheme.MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        input.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        input.setPreferredSize(new Dimension(340, 42));
        input.setBackground(new Color(255, 254, 250));
        input.setForeground(UITheme.INK);
        input.setBorder(new LineBorder(new Color(218, 214, 202), 1, true));
        if (input instanceof JTextField) {
            ((JTextField) input).setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(218, 214, 202), 1, true), new EmptyBorder(8, 11, 8, 11)));
        }
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(label);
        form.add(Box.createVerticalStrut(7));
        form.add(input);
        form.add(Box.createVerticalStrut(18));
    }

    private void dangNhap() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            hienLoi("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            if (user.isEmpty()) txtUser.requestFocusInWindow(); else txtPass.requestFocusInWindow();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("ĐANG XÁC THỰC...");
        lblStatus.setForeground(UITheme.COPPER_DARK);
        lblStatus.setText("Đang kiểm tra thông tin đăng nhập...");

        final boolean dangNhapNguoiThue = cboDoiTuong.getSelectedIndex() == 1;
        SwingWorker<Object, Void> worker = new SwingWorker<Object, Void>() {
            @Override
            protected Object doInBackground() throws Exception {
                if (dangNhapNguoiThue) return khachThueDAO.dangNhap(user, pass);
                return taiKhoanDAO.dangNhap(user, pass);
            }

            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                btnLogin.setText("ĐĂNG NHẬP  →");
                btnLogin.setBackground(UITheme.FOREST);
                try {
                    Object result = get();
                    if (result == null) {
                        dangNhapSai();
                        return;
                    }
                    if (result instanceof KhachThue) {
                        KhachThue kt = (KhachThue) result;
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Đăng nhập thành công. Xin chào " + kt.getHoTen() + "!",
                                "Chào mừng", JOptionPane.INFORMATION_MESSAGE);
                        new NguoiThueFrame(kt).setVisible(true);
                    } else {
                        TaiKhoan tk = (TaiKhoan) result;
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Đăng nhập thành công. Xin chào " + tk.getHoTen() + "!",
                                "Chào mừng", JOptionPane.INFORMATION_MESSAGE);
                        new MainFrame(tk).setVisible(true);
                    }
                    dispose();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    hienLoi("Quá trình đăng nhập bị gián đoạn.");
                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause() == null ? ex : ex.getCause();
                    cause.printStackTrace();
                    if (cause instanceof SQLException) {
                        SQLException sql = (SQLException) cause;
                        String msg = sql.getErrorCode() == 208
                                ? "Chưa có bảng TaiKhoanNguoiThue trong CSDL. Hãy chạy script SQL tạo bảng trước!"
                                : "Không thể truy cập cơ sở dữ liệu:\n" + sql.getMessage();
                        JOptionPane.showMessageDialog(LoginFrame.this, msg,
                                "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
                        hienLoi("Không thể kết nối cơ sở dữ liệu.");
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Lỗi hệ thống: " + cause.getMessage(), "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                        hienLoi("Đăng nhập thất bại do lỗi hệ thống.");
                    }
                }
            }
        };
        worker.execute();
    }

    private void dangNhapSai() {
        hienLoi("Sai tên đăng nhập, mật khẩu hoặc tài khoản đã bị khóa.");
        txtPass.setText("");
        txtPass.requestFocusInWindow();
        JOptionPane.showMessageDialog(this,
                "Sai tên đăng nhập hoặc mật khẩu (hoặc tài khoản đã bị khóa)!",
                "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
    }

    private void hienLoi(String message) {
        lblStatus.setForeground(UITheme.DANGER);
        lblStatus.setText(message);
    }
}
