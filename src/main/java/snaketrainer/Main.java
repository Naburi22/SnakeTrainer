package snaketrainer;

import javax.swing.SwingUtilities;
import snaketrainer.ui.SnakeWindow;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SnakeWindow window = new SnakeWindow();
            window.setVisible(true);
        });
    }
}
