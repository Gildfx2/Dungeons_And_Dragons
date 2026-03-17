package GUI;

import Game.GameRunner;
import Game.TileFactory;
import Tiles.Players.Player;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.List;

public class MainMenu extends JFrame {

    private final Color BACKGROUND_COLOR = new Color(30, 28, 38);
    private final Color GOLD_BORDER = new Color(205, 145, 60);
    private final Color TEXT_COLOR = new Color(230, 230, 230);

    public MainMenu() {
        setTitle("Dungeons & Dragons - Select Your Hero");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 750);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Header
        JLabel titleLabel = new JLabel("SELECT YOUR HERO", SwingConstants.CENTER);
        titleLabel.setForeground(GOLD_BORDER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Player selection grid
        JPanel playersPanel = new JPanel();
        playersPanel.setLayout(new GridLayout(0, 2, 20, 20));
        playersPanel.setBackground(BACKGROUND_COLOR);
        playersPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        TileFactory factory = new TileFactory();
        List<Player> players = factory.listPlayers();

        for (int i = 0; i < players.size(); i++) {
            playersPanel.add(createPlayerCard(players.get(i), i));
        }

        add(createCustomScrollPane(playersPanel), BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }

    private JScrollPane createCustomScrollPane(JPanel content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = GOLD_BORDER;
                this.trackColor = BACKGROUND_COLOR;
            }
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });

        return scrollPane;
    }

    private JPanel createPlayerCard(Player player, int index) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(new Color(40, 38, 48));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_BORDER, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        String baseName = player.getName().toLowerCase().replace(" ", "_");
        String menuImagePath = "src/assets/" + baseName + "_right.png";

        JLabel imageLabel;
        try {
            ImageIcon originalIcon = new ImageIcon(menuImagePath);
            Image scaledImage = originalIcon.getImage().getScaledInstance(64, 64, Image.SCALE_REPLICATE);
            imageLabel = new JLabel(new ImageIcon(scaledImage), SwingConstants.CENTER);
        } catch (Exception e) {
            imageLabel = new JLabel("?", SwingConstants.CENTER);
            imageLabel.setForeground(Color.GRAY);
            imageLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        }

        imageLabel.setPreferredSize(new Dimension(80, 80));
        card.add(imageLabel, BorderLayout.WEST);

        // Card details
        JPanel details = new JPanel(new GridLayout(4, 1));
        details.setOpaque(false);

        JLabel nameLabel = new JLabel(player.getName());
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setFont(new Font("Monospaced", Font.BOLD, 20));

        JLabel classLabel = new JLabel("Class: " + player.getClass().getSimpleName());
        classLabel.setForeground(new Color(150, 150, 255));
        classLabel.setFont(new Font("Monospaced", Font.ITALIC, 14));

        JLabel statsLabel = new JLabel(String.format("HP: %d | ATK: %d | DEF: %d",
                player.getHealth().getCapacity(), player.getAttack(), player.getDefense()));
        statsLabel.setForeground(new Color(200, 200, 200));
        statsLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));

        String perkText = player.getSpecialAbilityName();
        JLabel perkLabel = new JLabel("Ability: " + perkText);
        perkLabel.setForeground(new Color(255, 215, 0));
        perkLabel.setFont(new Font("Monospaced", Font.BOLD, 12));

        details.add(nameLabel);
        details.add(classLabel);
        details.add(statsLabel);
        details.add(perkLabel);
        card.add(details, BorderLayout.CENTER);

        // Select button
        JButton selectBtn = new JButton("Select");
        selectBtn.setBackground(GOLD_BORDER);
        selectBtn.setForeground(Color.BLACK);
        selectBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        selectBtn.setFocusPainted(false);
        selectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        selectBtn.addActionListener(e -> startGameWithPlayer(index, baseName));

        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setOpaque(false);
        btnPanel.add(selectBtn, BorderLayout.EAST);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }


    private void startGameWithPlayer(int selectedIndex, String baseName) {
        this.dispose();

        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow(15, 30);
            window.getBoardPanel().setPlayerSprites(baseName);

            GUIInputProvider inputProvider = new GUIInputProvider();
            window.attachInputProvider(inputProvider);

            GameRunner runner = new GameRunner(window, inputProvider);
            runner.initialize("levels", selectedIndex);

            window.setVisible(true);
            window.getInfoPanel().addMessage("Welcome to Dungeons & Dragons!");
            runner.start();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}