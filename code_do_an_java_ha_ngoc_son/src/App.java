import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.phongtro.view.LoginFrame;

public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.messageFont", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
            UIManager.put("OptionPane.buttonFont", new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        } catch (Exception ignored) {
            // Dùng giao diện mặc định nếu hệ điều hành không hỗ trợ Look & Feel.
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
