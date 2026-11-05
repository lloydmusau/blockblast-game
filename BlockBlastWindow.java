import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class BlockBlastWindow extends JFrame {
    private ArrayList<FloatingText> activePopups = new ArrayList<>();
    private GameBoard board;
    private ArrayList<BlockShape> availableShapes;
    private BlockShape selectedShape = null;
    private JLabel scoreLabel;
    private JPanel canvasPanel;

    // Drag coordinates
    private int mouseX = -1;
    private int mouseY = -1;

    public BlockBlastWindow() {
        Timer animationTimer = new Timer(30, e -> {
            if (!activePopups.isEmpty()) {
                for (int i = activePopups.size() - 1; i >= 0; i--) {
                    FloatingText ft = activePopups.get(i);
                    ft.y -= 2; // Drift upward
                    ft.life--; // Decrease life
                    if (ft.life <= 0) {
                        activePopups.remove(i); // Delete when expired
                    }
                }
                canvasPanel.repaint(); // Redraw screen to show floating movement
            }
        });
        animationTimer.start();
        board = new GameBoard();
        availableShapes = new ArrayList<>();
        generateNewShapes();

        setTitle("Block Blast ");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel for Score
        JPanel topPanel = new JPanel();
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(scoreLabel);
        add(topPanel, BorderLayout.NORTH);

        // Unified Single Canvas Panel for everything
        canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame(g);
            }
        };
        canvasPanel.setBackground(new Color(15, 20, 35));

        // Unified Mouse Listeners on the single canvas
        canvasPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                checkForShapeSelection(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleDrop(e.getX(), e.getY());
            }
        });

        canvasPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                canvasPanel.repaint();
            }
        });

        add(canvasPanel, BorderLayout.CENTER);
    }

    private void generateNewShapes() {
        availableShapes.clear();
        for (int i = 0; i < 3; i++) {
            availableShapes.add(BlockShape.getRandomShape());
        }
    }

    private void drawGame(Graphics g) {
        // 1. Draw 8x8 Grid in the upper portion
        int gridSize = 400;
        int gridX = (canvasPanel.getWidth() - gridSize) / 2;
        int gridY = 30;
        int cellSize = gridSize / 8;

        int[][] grid = board.getGrid();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int x = gridX + (c * cellSize);
                int y = gridY + (r * cellSize);

                if (grid[r][c] == 0) {
                    g.setColor(new Color(60, 60, 60));
                } else {
                    g.setColor(getColorFromCode(grid[r][c]));
                }
                g.fillRect(x + 2, y + 2, cellSize - 4, cellSize - 4);
            }
        }

        // 2. Draw Available Shapes Dock at the bottom
        int dockY = 480;
        int startX = 60;
        int spacing = 130;

        for (int i = 0; i < availableShapes.size(); i++) {
            BlockShape shape = availableShapes.get(i);
            // If this shape is currently being dragged, don't draw it in the dock
            if (shape == selectedShape) continue;

            int boxX = startX + (i * spacing);
            g.setColor(new Color(50, 50, 50));
            g.fillRect(boxX, dockY, 100, 100);
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(boxX, dockY, 100, 100);

            // Draw shape blocks inside dock box
            g.setColor(shape.getColor());
            int[][] shapeData = shape.getShape();
            int blockSize = 20;
            int offsetX = boxX + (100 - (shapeData[0].length * blockSize)) / 2;
            int offsetY = dockY + (100 - (shapeData.length * blockSize)) / 2;

            for (int r = 0; r < shapeData.length; r++) {
                for (int c = 0; c < shapeData[r].length; c++) {
                    if (shapeData[r][c] == 1) {
                        g.fillRect(offsetX + (c * blockSize), offsetY + (r * blockSize), blockSize - 2, blockSize - 2);
                    }
                }
            }
        }

        // 3. Draw the Dragged Shape following the cursor smoothly
        if (selectedShape != null && mouseX != -1) {
            g.setColor(selectedShape.getColor());
            int[][] shapeData = selectedShape.getShape();
            int blockSize = 35; // Larger blocks while dragging for visibility

            for (int r = 0; r < shapeData.length; r++) {
                for (int c = 0; c < shapeData[r].length; c++) {
                    if (shapeData[r][c] == 1) {
                        // Position shape centered right above the cursor pointer
                        g.fillRect(mouseX + (c * blockSize) - 30, mouseY + (r * blockSize) - 50, blockSize - 3, blockSize - 3);
                    }
                }
            }
        }
        // Draw floating combo popups
        g.setFont(new Font("Arial", Font.BOLD, 22));
        for (FloatingText ft : activePopups) {
            g.setColor(Color.YELLOW);
            g.drawString(ft.text, ft.x, ft.y);
        }
    }

    private void checkForShapeSelection(int px, int py) {
        int dockY = 480;
        int startX = 60;
        int spacing = 130;

        for (int i = 0; i < availableShapes.size(); i++) {
            int boxX = startX + (i * spacing);
            if (px >= boxX && px <= boxX + 100 && py >= dockY && py <= dockY + 100) {
                selectedShape = availableShapes.get(i);
                mouseX = px;
                mouseY = py;
                canvasPanel.repaint();
                break;
            }
        }
    }

    private void handleDrop(int px, int py) {
        if (selectedShape == null) return;

        int gridSize = 400;
        int gridX = (canvasPanel.getWidth() - gridSize) / 2;
        int gridY = 30;
        int cellSize = gridSize / 8;

        // Account for the offset where the shape is held above the cursor
        int targetX = px - 30 - gridX;
        int targetY = py - 50 - gridY;

        int col = targetX / cellSize;
        int row = targetY / cellSize;

        // Check if dropped inside the 8x8 grid bounds
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            if (board.canPlace(selectedShape, row, col)) {
                // 1. Place the shape and capture the number of cleared lines
                int linesCleared = board.placeShape(selectedShape, row, col);

                // 2. If lines were cleared, spawn floating balloon text right at the mouse position!
                if (linesCleared > 0) {
                    String message = "👍 Nice! +" + (linesCleared * 100);
                    if (linesCleared == 3) message = "🚀 Amazing! +" + (linesCleared * 100);
                    if (linesCleared >= 4) message = "🌟 EPIC COMBO! +" + (linesCleared * 100);

                    activePopups.add(new FloatingText(message, px, py));
                }

                availableShapes.remove(selectedShape);

                if (availableShapes.isEmpty()) {
                    generateNewShapes();
                }

                scoreLabel.setText("Score: " + board.getScore());

                if (isGameOver()) {
                    JOptionPane.showMessageDialog(this, "Game Over! Final Score: " + board.getScore());
                    board.reset();
                    generateNewShapes();
                    scoreLabel.setText("Score: 0");
                }
            }
        }

        // Reset drag state
        selectedShape = null;
        mouseX = -1;
        mouseY = -1;
        canvasPanel.repaint();
    }

    private boolean isGameOver() {
        if (availableShapes.isEmpty()) return false;
        for (BlockShape shape : availableShapes) {
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if (board.canPlace(shape, r, c)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private Color getColorFromCode(int code) {
        switch (code) {
            case 1: return Color.CYAN;
            case 2: return Color.MAGENTA;
            case 3: return Color.ORANGE;
            case 4: return Color.GREEN;
            case 5: return Color.YELLOW;
            default: return Color.BLUE;
        }
    }
    class FloatingText {
        String text;
        int x, y;
        int life; // Counts down how long the popup stays alive

        public FloatingText(String text, int x, int y) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.life = 30; // Lasts for 30 animation frames
        }
    }
}