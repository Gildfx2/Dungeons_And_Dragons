package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BoardPanel extends JPanel {
    private TilePanel[][] grid;
    private Map<Character, Image> spriteMap;

    private Image playerRightImg;
    private Image playerLeftImg;

    private final int TILE_SIZE = 64;

    private Image wallImg;
    private Image wallShadowImg;
    private Image floorImg;

    private Map<Character, EnemySprite> enemySpritesMap;

    public BoardPanel(int rows, int cols) {
        setBackground(new Color(20, 15, 20));
        setBorder(BorderFactory.createLineBorder(new Color(205, 145, 60), 4));

        grid = null;
        spriteMap = new HashMap<>();
        enemySpritesMap = new HashMap<>();

        Random rand = new Random();
        // picking random wall and floor
        int randomFloor = rand.nextInt(3) + 1;
        int randomWall = rand.nextInt(3) + 1;

        // Load map sprites dynamically based on random numbers
        wallImg = loadImage("src/assets/wall" + randomWall + ".png");
        wallShadowImg = loadImage("src/assets/wall_shadow" + randomWall + ".png");
        floorImg = loadImage("src/assets/floor" + randomFloor + ".png");

        spriteMap.put('@', loadImage("src/assets/player.png"));

        // Load enemy sprites
        enemySpritesMap.put('s', new EnemySprite("lannister_soldier"));
        enemySpritesMap.put('k', new EnemySprite("lannister_knight"));
        enemySpritesMap.put('q', new EnemySprite("queen's_guard"));
        enemySpritesMap.put('z', new EnemySprite("wright"));
        enemySpritesMap.put('b', new EnemySprite("bear_wright"));
        enemySpritesMap.put('g', new EnemySprite("giant_wright"));
        enemySpritesMap.put('w', new EnemySprite("white_walker"));
        enemySpritesMap.put('M', new EnemySprite("the_mountain"));
        enemySpritesMap.put('C', new EnemySprite("queen_cersei"));
        enemySpritesMap.put('K', new EnemySprite("night's_king"));
        enemySpritesMap.put('Q', new EnemySprite("queen's_trap"));
        enemySpritesMap.put('D', new EnemySprite("death_trap"));
        enemySpritesMap.put('B', new EnemySprite("bonus_trap"));
    }

    private Image loadImage(String path) {
        File imgFile = new File(path);
        if (!imgFile.exists()) {
            return null;
        }

        try {
            ImageIcon icon = new ImageIcon(path);
            return icon.getImage().getScaledInstance(TILE_SIZE, TILE_SIZE, Image.SCALE_REPLICATE);
        } catch (Exception e) {
            return null;
        }
    }

    public void updateBoard(String boardString) {
        String[] rawLines = boardString.split("\n");
        List<String> mazeLines = new ArrayList<>();
        int maxCols = 0;

        // Parse level structure
        for (String line : rawLines) {
            line = line.replace("\r", "");
            if (line.trim().isEmpty() || line.contains("Health:") || line.contains("Attack:") || line.contains("Select")) {
                continue;
            }
            mazeLines.add(line);
            if (line.length() > maxCols) {
                maxCols = line.length();
            }
        }

        int maxRows = mazeLines.size();
        if (maxRows == 0 || maxCols == 0) return;

        // Rebuild grid if dimensions change
        if (grid == null || grid.length != maxRows || grid[0].length != maxCols) {
            removeAll();
            setLayout(new GridLayout(maxRows, maxCols));
            grid = new TilePanel[maxRows][maxCols];

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int availableWidth = screenSize.width - 400;
            int availableHeight = screenSize.height - 450;
            int dynamicTileSize = Math.min(availableWidth / maxCols, availableHeight / maxRows);

            for (int i = 0; i < maxRows; i++) {
                for (int j = 0; j < maxCols; j++) {
                    TilePanel tile = new TilePanel();
                    tile.setPreferredSize(new Dimension(dynamicTileSize, dynamicTileSize));
                    grid[i][j] = tile;
                    add(tile);
                }
            }
            revalidate();
            repaint();
        }

        // Update tiles
        for (int y = 0; y < maxRows; y++) {
            String line = mazeLines.get(y);
            for (int x = 0; x < maxCols; x++) {
                grid[y][x].setHpPercentage(-1.0); // -1.0 indicates no health bar

                if (x < line.length()) {
                    char c = line.charAt(x);

                    if (c == '#') {
                        boolean isFloorBelow = (y + 1 < maxRows && x < mazeLines.get(y + 1).length() && mazeLines.get(y + 1).charAt(x) != '#');
                        grid[y][x].setImages(isFloorBelow ? wallShadowImg : wallImg, null);
                    } else if (c == '.') {
                        grid[y][x].setImages(floorImg, null);
                    } else if (spriteMap.containsKey(c)) {
                        grid[y][x].setImages(floorImg, spriteMap.get(c));
                    } else if (enemySpritesMap != null && enemySpritesMap.containsKey(c)) {
                        grid[y][x].setImages(floorImg, null);
                    } else {
                        grid[y][x].setImages(floorImg, null);
                    }
                } else {
                    grid[y][x].setImages(null, null);
                }
            }
        }
    }

    public void setEnemyData(int x, int y, char enemyChar, double percentage, boolean facingRight) {
        if (y >= 0 && y < grid.length && x >= 0 && x < grid[0].length) {
            grid[y][x].setHpPercentage(percentage);
            if (enemySpritesMap.containsKey(enemyChar)) {
                Image correctImage = enemySpritesMap.get(enemyChar).getImage(facingRight);
                grid[y][x].setImages(floorImg, correctImage);
            }
        }
    }

    public void setPlayerSprites(String baseName) {
        playerRightImg = loadImage("src/assets/" + baseName + "_right.png");
        playerLeftImg = loadImage("src/assets/" + baseName + "_left.png");

        if (playerRightImg != null) {
            spriteMap.put('@', playerRightImg);
        }
    }

    public void setPlayerFacingRight(boolean isFacingRight) {
        if (isFacingRight && playerRightImg != null) {
            spriteMap.put('@', playerRightImg);
        } else if (!isFacingRight && playerLeftImg != null) {
            spriteMap.put('@', playerLeftImg);
        }
    }

    private class TilePanel extends JPanel {
        private Image baseImage;
        private Image overlayImage;
        private double hpPercentage = -1.0;

        public TilePanel() {
            setOpaque(true);
            setBackground(new Color(20, 15, 20));
        }

        public void setImages(Image base, Image overlay) {
            this.baseImage = base;
            this.overlayImage = overlay;
            repaint();
        }

        public void setHpPercentage(double percentage) {
            this.hpPercentage = percentage;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (baseImage != null) {
                g.drawImage(baseImage, 0, 0, getWidth(), getHeight(), this);
            }
            if (overlayImage != null) {
                g.drawImage(overlayImage, 0, 0, getWidth(), getHeight(), this);
            }

            // Draw health bar
            if (hpPercentage >= 0.0 && overlayImage != null) {
                int barWidth = 50;
                int barHeight = 6;
                int x = (getWidth() - barWidth) / 2;
                int y = 0;

                g.setColor(new Color(20, 20, 20));
                g.fillRect(x, y, barWidth, barHeight);

                if (hpPercentage > 0.6) {
                    g.setColor(new Color(60, 200, 60));
                } else if (hpPercentage > 0.3) {
                    g.setColor(new Color(220, 180, 40));
                } else {
                    g.setColor(new Color(220, 40, 40));
                }

                int healthWidth = (int)((barWidth - 2) * hpPercentage);
                if (healthWidth > 0) {
                    g.fillRect(x + 1, y + 1, healthWidth, barHeight - 2);
                }
            }
        }
    }

    private class EnemySprite {
        private Image leftImg;
        private Image rightImg;

        public EnemySprite(String baseName) {
            leftImg = loadImage("src/assets/" + baseName + "_left.png");
            rightImg = loadImage("src/assets/" + baseName + "_right.png");

            if (leftImg == null && rightImg == null) {
                Image singleImg = loadImage("src/assets/" + baseName + ".png");
                leftImg = singleImg;
                rightImg = singleImg;
            }
        }

        public Image getImage(boolean facingRight) {
            if (facingRight && rightImg != null) return rightImg;
            if (!facingRight && leftImg != null) return leftImg;
            return rightImg != null ? rightImg : leftImg;
        }
    }
}