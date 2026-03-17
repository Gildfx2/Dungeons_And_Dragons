package GUI;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private BoardPanel boardPanel;
    private InfoPanel infoPanel;
    private GUIInputProvider inputProvider;

    public GameWindow(int rows, int cols) {
        setTitle("Dungeons & Dragons - GUI Version");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // Main background
        getContentPane().setBackground(new Color(30, 28, 38));

        boardPanel = new BoardPanel(rows, cols);
        infoPanel = new InfoPanel();

        // Wrap board to keep it centered
        JPanel boardWrapper = new JPanel(new GridBagLayout());
        boardWrapper.setBackground(new Color(30, 28, 38));
        boardWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        boardWrapper.add(boardPanel);

        add(boardWrapper, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);

        // Set fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    public void showVictoryPopup() {
        showCustomPopup(
                "VICTORY!",
                "You have conquered the dungeons!",
                new Color(255, 215, 0), // Gold
                new Color(205, 145, 60), // Dark Gold
                new Color(30, 28, 38), // Standard Dark
                Color.BLACK,
                "Quit Game"
        );
    }

    public void showGameOverPopup() {
        showCustomPopup(
                "YOU DIED",
                "The dungeons have claimed another soul...",
                new Color(220, 40, 40), // Bright Red
                new Color(150, 20, 20), // Dark Red
                new Color(20, 10, 10), // Blood Dark
                Color.WHITE,
                "Accept Fate (Quit)"
        );
    }

    // Generic popup builder to prevent code duplication (DRY)
    private void showCustomPopup(String title, String subtitle, Color titleColor, Color borderColor, Color bgColor, Color btnTextColor, String btnText) {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.getContentPane().setBackground(new Color(30, 28, 38));

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 4),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(titleColor);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 60));

        JLabel subLabel = new JLabel(subtitle, SwingConstants.CENTER);
        subLabel.setForeground(Color.LIGHT_GRAY);
        subLabel.setFont(new Font("Monospaced", Font.BOLD, 20));

        JButton exitBtn = new JButton(btnText);
        exitBtn.setBackground(borderColor);
        exitBtn.setForeground(btnTextColor);
        exitBtn.setFont(new Font("Monospaced", Font.BOLD, 18));
        exitBtn.setFocusPainted(false);
        exitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subLabel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(exitBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void attachInputProvider(GUIInputProvider inputProvider) {
        this.inputProvider = inputProvider;
        this.addKeyListener(inputProvider);
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    public void refreshScreen(String boardString, int currentHp, int maxHp, int attack, int defense, int level, int exp, int reqExp) {
        boardPanel.updateBoard(boardString);
        infoPanel.updateHP(currentHp, maxHp);
        infoPanel.updateCombatStats(attack, defense);
        infoPanel.updateLevelAndExp(level, exp, reqExp);
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }
}