package com.phongtro.view;

import com.phongtro.model.TaiKhoan;
import com.phongtro.ui.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/** Trang tổng quan AN CƯ - dashboard quản lý nhà trọ. */
public class MainFrame extends JFrame {
    private final TaiKhoan taiKhoan;
    private PhongTroFrame phongTroFrame;
    private KhachThueFrame khachThueFrame;
    private LoaiPhongFrame loaiPhongFrame;
    private NhanVienFrame nhanVienFrame;

    public MainFrame(TaiKhoan tk) {
        this.taiKhoan = tk;
        boolean laAdmin = "Admin".equalsIgnoreCase(tk.getVaiTro());
        setTitle("AN CƯ | Không gian quản trị");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(940, 620));
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.PAPER);
        root.add(buildSidebar(laAdmin), BorderLayout.WEST);
        root.add(buildWorkspace(laAdmin), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildSidebar(boolean laAdmin) {
        JPanel side = new JPanel();
        side.setBackground(UITheme.FOREST);
        side.setPreferredSize(new Dimension(250, 0));
        side.setBorder(new EmptyBorder(30, 22, 22, 22));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandRow.setOpaque(false);
        brandRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandRow.setMaximumSize(new Dimension(205, 44));
        JLabel mark = new JLabel("A");
        mark.setHorizontalAlignment(SwingConstants.CENTER);
        mark.setPreferredSize(new Dimension(40, 40));
        mark.setOpaque(true);
        mark.setBackground(UITheme.COPPER);
        mark.setForeground(Color.WHITE);
        mark.setFont(new Font(Font.SERIF, Font.BOLD, 24));
        JLabel brand = new JLabel("AN CƯ");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font(Font.SERIF, Font.BOLD, 23));
        brandRow.add(mark);
        brandRow.add(brand);

        JLabel caption = new JLabel("PROPERTY DESK");
        caption.setFont(new Font("Segoe UI", Font.BOLD, 10));
        caption.setForeground(new Color(184, 200, 185));
        caption.setBorder(new EmptyBorder(10, 3, 28, 0));

        JLabel menuTitle = new JLabel("KHÔNG GIAN LÀM VIỆC");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 9));
        menuTitle.setForeground(new Color(157, 181, 162));
        menuTitle.setBorder(new EmptyBorder(0, 3, 11, 0));

        side.add(brandRow);
        side.add(caption);
        side.add(menuTitle);
        side.add(sideItem("⌂", "Tổng quan", true, () -> {}));
        side.add(Box.createVerticalStrut(7));
        side.add(sideItem("01", "Phòng trọ", false, this::openPhong));
        side.add(Box.createVerticalStrut(7));
        side.add(sideItem("02", "Khách thuê", false, this::openKhach));
        if (laAdmin) {
            side.add(Box.createVerticalStrut(7));
            side.add(sideItem("03", "Loại phòng & giá", false, this::openLoai));
            side.add(Box.createVerticalStrut(7));
            side.add(sideItem("04", "Nhân viên", false, this::openNhanVien));
        }

        side.add(Box.createVerticalGlue());
        JPanel userCard = new JPanel();
        userCard.setOpaque(true);
        userCard.setBackground(new Color(35, 78, 67));
        userCard.setBorder(new EmptyBorder(13, 12, 13, 12));
        userCard.setLayout(new BoxLayout(userCard, BoxLayout.Y_AXIS));
        JLabel userName = new JLabel(taiKhoan.getHoTen());
        userName.setForeground(Color.WHITE);
        userName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel role = new JLabel(laAdmin ? "QUẢN TRỊ VIÊN" : "NHÂN VIÊN");
        role.setForeground(new Color(202, 188, 153));
        role.setFont(new Font("Segoe UI", Font.BOLD, 9));
        userCard.add(userName);
        userCard.add(Box.createVerticalStrut(5));
        userCard.add(role);
        userCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(userCard);
        side.add(Box.createVerticalStrut(12));
        side.add(sideItem("↪", "Đăng xuất", false, this::dangXuat));
        side.add(Box.createVerticalStrut(7));
        side.add(sideItem("×", "Thoát", false, () -> System.exit(0)));
        return side;
    }

    private JButton sideItem(String icon, String title, boolean selected, Runnable action) {
        JButton button = new JButton("  " + icon + "     " + title);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(selected ? UITheme.FOREST : new Color(225, 232, 222));
        button.setBackground(selected ? new Color(238, 239, 222) : UITheme.FOREST);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        button.setPreferredSize(new Dimension(206, 43));
        button.addActionListener(e -> action.run());
        return button;
    }

    private JPanel buildWorkspace(boolean laAdmin) {
        JPanel workspace = new JPanel(new BorderLayout(0, 22));
        workspace.setBackground(UITheme.PAPER);
        workspace.setBorder(new EmptyBorder(30, 34, 26, 34));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel eyebrow = new JLabel("AN CƯ  /  BẢNG ĐIỀU KHIỂN");
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 10));
        eyebrow.setForeground(UITheme.COPPER_DARK);
        JLabel heading = new JLabel("Tổng quan quản lý");
        heading.setFont(new Font(Font.SERIF, Font.BOLD, 29));
        heading.setForeground(UITheme.FOREST);
        JLabel subtitle = new JLabel("Chọn một phân hệ để bắt đầu công việc của bạn.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(UITheme.MUTED);
        titles.add(eyebrow);
        titles.add(Box.createVerticalStrut(7));
        titles.add(heading);
        titles.add(Box.createVerticalStrut(5));
        titles.add(subtitle);
        top.add(titles, BorderLayout.WEST);

        JLabel session = new JLabel("●  PHIÊN LÀM VIỆC");
        session.setOpaque(true);
        session.setBackground(new Color(231, 239, 229));
        session.setForeground(UITheme.FOREST);
        session.setFont(new Font("Segoe UI", Font.BOLD, 10));
        session.setBorder(new EmptyBorder(9, 12, 9, 12));
        top.add(session, BorderLayout.EAST);

        JPanel hero = new JPanel(new BorderLayout(20, 0));
        hero.setBackground(UITheme.FOREST);
        hero.setBorder(new EmptyBorder(25, 28, 25, 28));
        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));
        JLabel heroEyebrow = new JLabel("KHÔNG GIAN ĐIỀU HÀNH");
        heroEyebrow.setFont(new Font("Segoe UI", Font.BOLD, 10));
        heroEyebrow.setForeground(new Color(211, 172, 119));
        JLabel heroTitle = new JLabel("<html>Vận hành ngăn nắp.<br>An tâm mỗi ngày.</html>");
        heroTitle.setFont(new Font(Font.SERIF, Font.BOLD, 25));
        heroTitle.setForeground(Color.WHITE);
        JLabel heroDesc = new JLabel("Xin chào, " + taiKhoan.getHoTen() + " — chúc bạn một ngày làm việc hiệu quả.");
        heroDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        heroDesc.setForeground(new Color(218, 228, 216));
        heroText.add(heroEyebrow);
        heroText.add(Box.createVerticalStrut(9));
        heroText.add(heroTitle);
        heroText.add(Box.createVerticalStrut(10));
        heroText.add(heroDesc);
        hero.add(heroText, BorderLayout.CENTER);

        JPanel heroMark = new JPanel(new GridBagLayout());
        heroMark.setOpaque(false);
        JLabel monogram = new JLabel("A");
        monogram.setFont(new Font(Font.SERIF, Font.BOLD, 76));
        monogram.setForeground(new Color(199, 151, 94));
        heroMark.add(monogram);
        hero.add(heroMark, BorderLayout.EAST);

        JPanel modulesTitle = new JPanel(new BorderLayout());
        modulesTitle.setOpaque(false);
        JLabel modulesHeading = new JLabel("CÁC PHÂN HỆ");
        modulesHeading.setFont(new Font("Segoe UI", Font.BOLD, 11));
        modulesHeading.setForeground(UITheme.MUTED);
        modulesTitle.add(modulesHeading, BorderLayout.WEST);

        JPanel cards = new JPanel(new GridLayout(laAdmin ? 2 : 1, 2, 16, 16));
        cards.setOpaque(false);
        cards.add(moduleCard("01", "Phòng trọ", "Thông tin phòng, tầng, giá và tình trạng thuê.", this::openPhong));
        cards.add(moduleCard("02", "Khách thuê", "Hồ sơ khách thuê và tài khoản người thuê.", this::openKhach));
        if (laAdmin) {
            cards.add(moduleCard("03", "Loại phòng & giá", "Thiết lập danh mục loại phòng và đơn giá.", this::openLoai));
            cards.add(moduleCard("04", "Nhân viên", "Quản lý tài khoản và phân quyền sử dụng.", this::openNhanVien));
        }

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(modulesTitle, BorderLayout.NORTH);
        center.add(cards, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        JLabel note = new JLabel("AN CƯ  •  PROPERTY MANAGEMENT SYSTEM");
        note.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        note.setForeground(new Color(145, 145, 132));
        footer.add(note, BorderLayout.WEST);
        JLabel role = new JLabel(laAdmin ? "QUYỀN: ADMIN" : "QUYỀN: NHÂN VIÊN");
        role.setFont(new Font("Segoe UI", Font.BOLD, 10));
        role.setForeground(UITheme.COPPER_DARK);
        footer.add(role, BorderLayout.EAST);

        JPanel body = new JPanel(new BorderLayout(0, 22));
        body.setOpaque(false);
        body.add(hero, BorderLayout.NORTH);
        body.add(center, BorderLayout.CENTER);
        workspace.add(top, BorderLayout.NORTH);
        workspace.add(body, BorderLayout.CENTER);
        workspace.add(footer, BorderLayout.SOUTH);
        return workspace;
    }

    private JPanel moduleCard(String number, String title, String description, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(UITheme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.LINE, 1, true), new EmptyBorder(10, 14, 10, 14)));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel badge = new JLabel(number);
        badge.setOpaque(true);
        badge.setBackground(new Color(239, 229, 210));
        badge.setForeground(UITheme.COPPER_DARK);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setPreferredSize(new Dimension(34, 28));
        top.add(badge, BorderLayout.WEST);
        JLabel arrow = new JLabel("↗");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 20));
        arrow.setForeground(UITheme.COPPER);
        top.add(arrow, BorderLayout.EAST);

        JLabel name = new JLabel(title);
        name.setFont(new Font(Font.SERIF, Font.BOLD, 18));
        name.setForeground(UITheme.FOREST);
        JLabel desc = new JLabel(description);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        desc.setForeground(UITheme.MUTED);

        JButton open = new JButton("MỞ PHÂN HỆ  →");
        open.setFont(new Font("Segoe UI", Font.BOLD, 10));
        open.setForeground(UITheme.FOREST);
        open.setBackground(new Color(231, 239, 232));
        open.setOpaque(true);
        open.setBorderPainted(false);
        open.setFocusPainted(false);
        open.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        open.setHorizontalAlignment(SwingConstants.LEFT);
        open.setMargin(new Insets(3, 8, 3, 8));
        open.setPreferredSize(new Dimension(0, 23));
        open.setMaximumSize(new Dimension(Integer.MAX_VALUE, 23));
        open.addActionListener(e -> action.run());

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(name);
        text.add(Box.createVerticalStrut(4));
        text.add(desc);
        card.add(top, BorderLayout.NORTH);
        card.add(text, BorderLayout.CENTER);
        card.add(open, BorderLayout.SOUTH);
        return card;
    }

    private void openPhong() {
        if (phongTroFrame == null || !phongTroFrame.isDisplayable()) phongTroFrame = new PhongTroFrame();
        phongTroFrame.setVisible(true);
        phongTroFrame.toFront();
    }

    private void openKhach() {
        if (khachThueFrame == null || !khachThueFrame.isDisplayable()) khachThueFrame = new KhachThueFrame();
        khachThueFrame.setVisible(true);
        khachThueFrame.toFront();
    }

    private void openLoai() {
        if (loaiPhongFrame == null || !loaiPhongFrame.isDisplayable()) loaiPhongFrame = new LoaiPhongFrame();
        loaiPhongFrame.setVisible(true);
        loaiPhongFrame.toFront();
    }

    private void openNhanVien() {
        if (nhanVienFrame == null || !nhanVienFrame.isDisplayable()) nhanVienFrame = new NhanVienFrame(taiKhoan);
        nhanVienFrame.setVisible(true);
        nhanVienFrame.toFront();
    }

    private void dangXuat() {
        if (phongTroFrame != null) phongTroFrame.dispose();
        if (khachThueFrame != null) khachThueFrame.dispose();
        if (loaiPhongFrame != null) loaiPhongFrame.dispose();
        if (nhanVienFrame != null) nhanVienFrame.dispose();
        new LoginFrame().setVisible(true);
        dispose();
    }
}
