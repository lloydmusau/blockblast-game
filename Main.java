import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BlockBlastWindow window = new BlockBlastWindow();
            window.setVisible(true);
        });
    }
}