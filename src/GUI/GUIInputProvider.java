package GUI;

import CLI.InputQuery;
import Utils.Action;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GUIInputProvider extends KeyAdapter implements InputQuery {
    private Action currentAction = null;
    private final Object lock = new Object();
    private boolean facingRight = true;

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        Action action = null;

        switch (key) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                action = Action.LEFT;
                facingRight = false;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                action = Action.RIGHT;
                facingRight = true;
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                action = Action.UP;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                action = Action.DOWN;
                break;
            case KeyEvent.VK_E:
                action = Action.SPECIAL_ABILITY;
                break;
            case KeyEvent.VK_Q:
                action = Action.NONE;
                break;
        }

        if (action != null) {
            synchronized (lock) {
                currentAction = action;
                lock.notify();
            }
        }
    }

    @Override
    public Action getInput() {
        synchronized (lock) {
            while (currentAction == null) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            Action temp = currentAction;
            currentAction = null;
            return temp;
        }
    }

    public boolean isFacingRight() {
        return facingRight;
    }
}