package com.phongtro.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

/** Bộ giao diện AN CƯ: xanh rừng, màu giấy ấm và điểm nhấn đồng. */
public final class UITheme {
    public static final Color FOREST = new Color(25, 63, 54);
    public static final Color FOREST_LIGHT = new Color(43, 88, 76);
    public static final Color COPPER = new Color(190, 132, 76);
    public static final Color COPPER_DARK = new Color(155, 99, 48);
    public static final Color PAPER = new Color(245, 241, 232);
    public static final Color SURFACE = new Color(255, 254, 250);
    public static final Color INK = new Color(41, 48, 44);
    public static final Color MUTED = new Color(119, 125, 117);
    public static final Color LINE = new Color(224, 220, 209);
    public static final Color DANGER = new Color(174, 76, 63);
    public static final Color PALE_GREEN = new Color(231, 239, 232);

    private UITheme() {}

    public static void apply(JFrame frame) {
        frame.getContentPane().setBackground(PAPER);
        styleTree(frame.getContentPane());
        frame.getRootPane().setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    public static void styleTree(Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            if (isDefaultPanelColor(panel.getBackground())) panel.setBackground(SURFACE);
            panel.setOpaque(true);
            if (panel.getBorder() instanceof TitledBorder) {
                TitledBorder titled = (TitledBorder) panel.getBorder();
                titled.setTitleColor(FOREST);
                titled.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
                panel.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(LINE, 1, true),
                        new CompoundBorder(new EmptyBorder(4, 6, 6, 6), titled)));
            }
        }

        if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            if (label.getForeground() == null || label.getForeground().equals(Color.BLACK)
                    || label.getForeground().equals(UIManager.getColor("Label.foreground"))) {
                label.setForeground(INK);
            }
            if (label.getFont() == null || label.getFont().getSize() <= 12) {
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            }
        }

        if (component instanceof JTextField) {
            JTextField field = (JTextField) component;
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            field.setForeground(INK);
            field.setBackground(SURFACE);
            field.setCaretColor(FOREST);
            field.setBorder(new CompoundBorder(new LineBorder(LINE, 1, true),
                    new EmptyBorder(7, 9, 7, 9)));
            field.setPreferredSize(new Dimension(Math.max(100, field.getPreferredSize().width), 34));
        }

        if (component instanceof JComboBox<?>) {
            JComboBox<?> combo = (JComboBox<?>) component;
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            combo.setForeground(INK);
            combo.setBackground(SURFACE);
            combo.setBorder(new LineBorder(LINE, 1, true));
            combo.setPreferredSize(new Dimension(Math.max(100, combo.getPreferredSize().width), 34));
        }

        if (component instanceof AbstractButton) {
            styleButton((AbstractButton) component);
        }

        if (component instanceof JTable) {
            JTable table = (JTable) component;
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            table.setForeground(INK);
            table.setBackground(SURFACE);
            table.setSelectionBackground(PALE_GREEN);
            table.setSelectionForeground(FOREST);
            table.setGridColor(LINE);
            table.setRowHeight(30);
            table.setShowVerticalLines(false);
            table.setIntercellSpacing(new Dimension(0, 1));
            table.setFillsViewportHeight(true);
            JTableHeader header = table.getTableHeader();
            header.setFont(new Font("Segoe UI", Font.BOLD, 12));
            header.setBackground(FOREST);
            header.setForeground(Color.WHITE);
            header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
            header.setReorderingAllowed(false);
        }

        if (component instanceof JScrollPane) {
            JScrollPane scroll = (JScrollPane) component;
            scroll.setBorder(new LineBorder(LINE, 1, true));
            scroll.getViewport().setBackground(SURFACE);
        }

        if (component instanceof JTabbedPane) {
            JTabbedPane tabs = (JTabbedPane) component;
            tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
            tabs.setForeground(FOREST);
            tabs.setBackground(PAPER);
        }

        if (component instanceof JSplitPane) {
            ((JSplitPane) component).setBorder(new EmptyBorder(4, 4, 4, 4));
            ((JSplitPane) component).setBackground(PAPER);
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                styleTree(child);
            }
        }
    }

    public static void styleButton(AbstractButton button) {
        String text = button.getText() == null ? "" : button.getText().trim().toLowerCase();
        Color bg = FOREST;
        Color fg = Color.WHITE;
        if (text.contains("xóa") || text.contains("thoát")) {
            bg = new Color(249, 238, 233);
            fg = DANGER;
        } else if (text.contains("sửa") || text.contains("cập nhật") || text.contains("cấp /")) {
            bg = new Color(247, 237, 219);
            fg = COPPER_DARK;
        } else if (text.contains("làm mới") || text.contains("đăng xuất") || text.contains("hủy")) {
            bg = new Color(235, 235, 228);
            fg = INK;
        } else if (text.contains("tìm kiếm") || text.contains("đăng nhập") || text.contains("mở chức năng")) {
            bg = FOREST;
            fg = Color.WHITE;
        }
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(fg);
        button.setBackground(bg);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 14, 8, 14));
    }

    private static boolean isDefaultPanelColor(Color color) {
        Color ui = UIManager.getColor("Panel.background");
        return color == null || color.equals(ui) || color.equals(new Color(238, 238, 238));
    }
}
