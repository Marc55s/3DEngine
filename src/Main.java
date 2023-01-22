import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Screen gamePanel = new Screen();
        frame.addKeyListener(new KeyHandler());
        frame.add(gamePanel);
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        gamePanel.startGameThread();

    }


}
