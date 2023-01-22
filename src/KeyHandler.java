import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {


    public static Direction direction = Direction.NONE;

    public enum Direction {
        W, A, S, D, UP, DOWN, LEFT, RIGHT, NONE
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> direction = Direction.UP;
            case KeyEvent.VK_DOWN -> direction = Direction.DOWN;
            case KeyEvent.VK_LEFT -> direction = Direction.LEFT;
            case KeyEvent.VK_RIGHT -> direction = Direction.RIGHT;
            case KeyEvent.VK_W -> direction = Direction.W;
            case KeyEvent.VK_S -> direction = Direction.S;
            case KeyEvent.VK_A -> direction = Direction.A;
            case KeyEvent.VK_D -> direction = Direction.D;
            default -> direction = Direction.NONE;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        direction = Direction.NONE;
    }
}
