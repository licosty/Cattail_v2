package cattail;

public class Matrix {

    private Icon[][] matrix;
    private static int columns;
    private static int rows;

    public Matrix(Icon defaultIcon, int width, int height) {
        columns = width;
        rows = height;
        this.matrix = new Icon[columns][rows];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                matrix[x][y] = defaultIcon;
            }
        }
    }

    public Icon getIconByCoords(int x, int y) {
        if (isNotBound(x, y))
            return matrix[x][y];
        return null;
    }

    public void setIconByCoords(int x, int y, Icon icon) {
        if (isNotBound(x, y))
            matrix[x][y] = icon;
    }

    public static boolean isNotBound(int x, int y) {
        return x >= 0 && x < columns &&
               y >= 0 && y < rows;
    }
}
