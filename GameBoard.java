public class GameBoard {
    private int[][] grid; // 0 for empty, values 1-5 for colored blocks
    private int size = 8;
    private int score;

    public GameBoard() {
        grid = new int[size][size];
        score = 0;
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getScore() {
        return score;
    }

    public boolean canPlace(BlockShape shape, int row, int col) {
        int[][] shapeData = shape.getShape();
        for (int r = 0; r < shapeData.length; r++) {
            for (int c = 0; c < shapeData[r].length; c++) {
                if (shapeData[r][c] == 1) {
                    int targetRow = row + r;
                    int targetCol = col + c;
                    if (targetRow < 0 || targetRow >= size || targetCol < 0 || targetCol >= size) {
                        return false; // Out of bounds
                    }
                    if (grid[targetRow][targetCol] != 0) {
                        return false; // Cell already occupied
                    }
                }
            }
        }
        return true;
    }

    public int placeShape(BlockShape shape, int row, int col) {
        int[][] shapeData = shape.getShape();
        for (int r = 0; r < shapeData.length; r++) {
            for (int c = 0; c < shapeData[r].length; c++) {
                if (shapeData[r][c] == 1) {
                    grid[row + r][col + c] = shape.getColorCode();
                }
            }
        }
        score += shape.getBlockCount() * 10;
        return checkAndClear();
    }

    public int checkAndClear() {
        boolean[] rowsToClear = new boolean[size];
        boolean[] colsToClear = new boolean[size];

        // Check rows
        for (int r = 0; r < size; r++) {
            boolean full = true;
            for (int c = 0; c < size; c++) {
                if (grid[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) rowsToClear[r] = true;
        }

        // Check cols
        for (int c = 0; c < size; c++) {
            boolean full = true;
            for (int r = 0; r < size; r++) {
                if (grid[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) colsToClear[c] = true;
        }

        int clearedCount = 0;
        // Clear rows
        for (int r = 0; r < size; r++) {
            if (rowsToClear[r]) {
                for (int c = 0; c < size; c++) {
                    grid[r][c] = 0;
                }
                clearedCount++;
            }
        }

        // Clear cols
        for (int c = 0; c < size; c++) {
            if (colsToClear[c]) {
                for (int r = 0; r < size; r++) {
                    grid[r][c] = 0;
                }
                clearedCount++;
            }
        }

        if (clearedCount > 0) {
            score += clearedCount * 100;
        }
        return clearedCount;
    }

    public void reset() {
        grid = new int[size][size];
        score = 0;
    }
}
