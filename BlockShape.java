import java.awt.Color;

public class BlockShape {
    private int[][] shape;
    private Color color;
    private int colorCode;

    public BlockShape(int[][] shape, Color color, int colorCode) {
        this.shape = shape;
        this.color = color;
        this.colorCode = colorCode;
    }

    public int[][] getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public int getColorCode() {
        return colorCode;
    }

    public int getBlockCount() {
        int count = 0;
        for (int[] row : shape) {
            for (int val : row) {
                if (val == 1) count++;
            }
        }
        return count;
    }

    public static BlockShape getRandomShape() {
        int[][][] possibleShapes = {
                { {1} },                      // Single block
                { {1, 1}, {1, 1} },          // 2x2 square
                { {1, 1, 1} },               // 3-line horizontal
                { {1}, {1}, {1} },           // 3-line vertical
                { {1, 1}, {1, 0} }           // L-shape
        };

        Color[] colors = { Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.GREEN, Color.YELLOW };
        int index = (int) (Math.random() * possibleShapes.length);

        return new BlockShape(possibleShapes[index], colors[index], index + 1);
    }
}