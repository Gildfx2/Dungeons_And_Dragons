package Game;

import GUI.GUIInputProvider;
import GUI.GameWindow;
import Tiles.Enemies.Enemy;
import Tiles.Enemies.Trap;
import Tiles.Players.*;
import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GameRunner {
    private TileFactory tileFactory;
    private List<Level> levels;
    private GameWindow window;
    private GUIInputProvider inputProvider;

    public GameRunner(GameWindow window, GUIInputProvider inputProvider) {
        this.tileFactory = new TileFactory();
        this.window = window;
        this.inputProvider = inputProvider;
    }

    public void initialize(String levelDirectory, int indexReceived) {
        FileParser parser = new FileParser(tileFactory, indexReceived,
                msg -> SwingUtilities.invokeLater(() -> window.getInfoPanel().addMessage(msg)),
                inputProvider);

        File root = new File(levelDirectory);
        levels = Arrays.stream(Objects.requireNonNull(root.listFiles()))
                .map(file -> {
                    try {
                        return parser.parseLevel(file);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to parse level file: " + file.getName(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    public void start() {
        new Thread(() -> {
            for (Level currentLevel : levels) {
                currentLevel.initializePlayer();
                updateGUI(currentLevel);

                while (!currentLevel.won()) {
                    if (!currentLevel.processTick()) {
                        updateGUI(currentLevel);
                        SwingUtilities.invokeLater(() -> window.showGameOverPopup());
                        return;
                    }
                    updateGUI(currentLevel);
                }
            }
            SwingUtilities.invokeLater(() -> window.showVictoryPopup());
        }).start();
    }

    private void updateGUI(Level currentLevel) {
        SwingUtilities.invokeLater(() -> {
            Player p = currentLevel.getPlayer();
            window.getBoardPanel().setPlayerFacingRight(inputProvider.isFacingRight());

            window.refreshScreen(
                    currentLevel.toString(),
                    p.getHealth().getAmount(),
                    p.getHealth().getCapacity(),
                    p.getAttack(),
                    p.getDefense(),
                    p.getLevel(),
                    p.getExperience(),
                    p.getLevel() * 50
            );

            updatePlayerResource(p);
            updateEnemies(currentLevel);
        });
    }

    private void updatePlayerResource(Player p) {
        if (p instanceof Warrior) {
            Warrior w = (Warrior) p;
            window.getInfoPanel().updateResource("CD", w.getRemainingCoolDown(), w.getAbilityCoolDown());
        } else if (p instanceof Mage) {
            Mage m = (Mage) p;
            window.getInfoPanel().updateResource("MANA", m.getMana().getAmount(), m.getMana().getCapacity());
        } else if (p instanceof Rogue) {
            Rogue r = (Rogue) p;
            window.getInfoPanel().updateResource("ENERGY", r.getCurrentEnergy(), 100);
        } else if (p instanceof Hunter) {
            Hunter h = (Hunter) p;
            window.getInfoPanel().updateResource("ARROWS", h.getArrowsCount(), 0);
        }
    }

    private void updateEnemies(Level currentLevel) {
        for (Enemy enemy : currentLevel.getEnemies()) {
            if (!enemy.alive()) continue;

            // Handle invisible traps properly using OOP
            if (enemy instanceof Trap) {
                Trap trap = (Trap) enemy;
                if (!trap.isVisible()) {
                    continue; // Skip rendering HP bar and image if trap is hidden
                }
            }

            int ex = enemy.getPosition().getX();
            int ey = enemy.getPosition().getY();
            double hpPercent = (double) enemy.getHealth().getAmount() / enemy.getHealth().getCapacity();

            window.getBoardPanel().setEnemyData(ex, ey, enemy.getTile(), hpPercent, enemy.isFacingRight());
        }
    }
}