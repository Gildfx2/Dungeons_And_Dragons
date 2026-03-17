package GUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;

public class InfoPanel extends JPanel {
    private JTextPane messageLog;
    private JPanel statsPanel;
    private JLabel attackLabel;
    private JLabel defenseLabel;
    private JProgressBar hpBar;
    private JProgressBar resourceBar;
    private JLabel resourceLabel;
    private JLabel levelLabel;
    private JLabel expLabel;

    private Font customFont;

    private final Color BACKGROUND_COLOR = new Color(30, 28, 38);
    private final Color GOLD_BORDER = new Color(205, 145, 60);
    private final Color TEXT_COLOR = new Color(230, 230, 230);

    public InfoPanel() {

        setLayout(new BorderLayout(30, 0));

        setPreferredSize(new Dimension(800, 320));
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loadCustomFont();

        Border rpgBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_BORDER, 2),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(20, 15, 20), 4),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
        );

        // Stats area
        statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(4, 1, 0, 15));
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setBorder(rpgBorder);

        statsPanel.setPreferredSize(new Dimension(480, 300));

        JPanel atkDefRow = new JPanel(new BorderLayout());
        atkDefRow.setOpaque(false);

        attackLabel = new JLabel("ATK: 0");
        attackLabel.setFont(customFont.deriveFont(18f));
        attackLabel.setForeground(new Color(255, 150, 150));

        defenseLabel = new JLabel("DEF: 0");
        defenseLabel.setFont(customFont.deriveFont(18f));
        defenseLabel.setForeground(new Color(150, 150, 255));
        defenseLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        atkDefRow.add(attackLabel, BorderLayout.WEST);
        atkDefRow.add(defenseLabel, BorderLayout.EAST);

        JPanel hpRow = createBarRow("HP:");
        hpBar = createCleanBar(new Color(200, 40, 40));
        hpRow.add(hpBar, BorderLayout.CENTER);

        JPanel resRow = createBarRow("STM:");
        resourceLabel = (JLabel) resRow.getComponent(0);
        resourceBar = createCleanBar(new Color(240, 190, 40));
        resRow.add(resourceBar, BorderLayout.CENTER);

        JPanel levelExpRow = new JPanel(new BorderLayout());
        levelExpRow.setOpaque(false);

        levelLabel = new JLabel("LV: 1");
        levelLabel.setFont(customFont.deriveFont(20f));
        levelLabel.setForeground(TEXT_COLOR);

        expLabel = new JLabel("EXP: 0/50");
        expLabel.setFont(customFont.deriveFont(20f));
        expLabel.setForeground(TEXT_COLOR);
        expLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        levelExpRow.add(levelLabel, BorderLayout.WEST);
        levelExpRow.add(expLabel, BorderLayout.EAST);

        // Add rows to panel
        statsPanel.add(atkDefRow);
        statsPanel.add(hpRow);
        statsPanel.add(resRow);
        statsPanel.add(levelExpRow);

        // Combat log
        messageLog = new JTextPane();
        messageLog.setEditable(false);
        messageLog.setBackground(BACKGROUND_COLOR);
        messageLog.setMargin(new Insets(15, 20, 15, 20));

        SimpleAttributeSet paragraphStyle = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(paragraphStyle, 0.4f);
        messageLog.setParagraphAttributes(paragraphStyle, false);

        JScrollPane scrollPane = new JScrollPane(messageLog);
        scrollPane.setBorder(rpgBorder);

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

        add(statsPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createBarRow(String title) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(customFont.deriveFont(18f));
        label.setForeground(TEXT_COLOR);
        label.setPreferredSize(new Dimension(140, 40));
        row.add(label, BorderLayout.WEST);
        return row;
    }

    private JProgressBar createCleanBar(Color barColor) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(100);
        bar.setStringPainted(true);
        bar.setForeground(barColor);
        bar.setBackground(new Color(40, 40, 40));
        bar.setFont(customFont.deriveFont(16f));
        bar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        return bar;
    }

    private void loadCustomFont() {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/assets/pixel_font.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
        } catch (Exception e) {
            customFont = new Font("Monospaced", Font.BOLD, 18);
        }
    }

    public void addMessage(String msg) {
        StyledDocument doc = messageLog.getStyledDocument();
        Style defaultStyle = messageLog.addStyle("Default", null);
        StyleConstants.setForeground(defaultStyle, TEXT_COLOR);
        StyleConstants.setFontFamily(defaultStyle, customFont.getFamily());
        StyleConstants.setFontSize(defaultStyle, 18);

        Style highlightStyle = messageLog.addStyle("Highlight", null);
        StyleConstants.setFontFamily(highlightStyle, customFont.getFamily());
        StyleConstants.setFontSize(highlightStyle, 18);

        boolean isPositive = msg.contains("experience") || msg.contains("healing") || msg.contains("level");

        try {
            String[] words = msg.split("(?<=\\s)|(?=\\s)");
            for (String word : words) {
                if (word.matches(".*\\d+.*")) {
                    StyleConstants.setForeground(highlightStyle, isPositive ? new Color(100, 255, 100) : new Color(255, 100, 100));
                    doc.insertString(doc.getLength(), word, highlightStyle);
                }
                else if (word.matches("(?i).*(damage|died|killed).*")) {
                    StyleConstants.setForeground(highlightStyle, new Color(255, 100, 100));
                    doc.insertString(doc.getLength(), word, highlightStyle);
                }
                else {
                    doc.insertString(doc.getLength(), word, defaultStyle);
                }
            }
            doc.insertString(doc.getLength(), "\n", defaultStyle);
            messageLog.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void updateHP(int currentHp, int maxHp) {
        hpBar.setMaximum(maxHp);
        hpBar.setValue(currentHp);
        hpBar.setString(currentHp + "/" + maxHp);
    }

    public void updateLevelAndExp(int level, int exp, int reqExp) {
        levelLabel.setText("LV: " + level);
        expLabel.setText("EXP: " + exp + "/" + reqExp);
    }

    public void updateResource(String name, int current, int max) {
        if (name.equalsIgnoreCase("CD") || name.equalsIgnoreCase("COOL DOWN")) {
            resourceLabel.setText("<html><div style='margin-bottom: 5px;'>COOL</div>DOWN:</html>");
        } else {
            resourceLabel.setText(name + ":");
        }
        resourceBar.setMaximum(Math.max(1, max));
        resourceBar.setValue(current);
        if (max == 0) {
            resourceBar.setString(String.valueOf(current));
        } else {
            resourceBar.setString(current + "/" + max);
        }
    }

    public void updateCombatStats(int attack, int defense) {
        attackLabel.setText("ATK: " + attack);
        defenseLabel.setText("DEF: " + defense);
    }
}