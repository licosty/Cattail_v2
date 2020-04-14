package cattail;

import java.util.ArrayList;
import java.util.Random;

import static cattail.Icon.*;
import static cattail.Matrix.isNotBound;

public class Minefield {
    private Random random = new Random();

    private Matrix upperLayer;
    private Matrix tailLayer;

    private int columns;
    private int rows;
    private int tails;

    private int[] firstStep;
    private int countFlags;
    private boolean gameOver;
    private String message;

    public Minefield() {
        columns = 9;
        rows = 9;
        tails = 10;
        firstStep = new int[]{-1, -1};
    }

    public Minefield(int col, int row, int tail) {
        columns = col;
        rows = row;
        tails = tail;
        firstStep = new int[]{-1, -1};
    }

    public void startGame() {
        placeTails();
        placeUpperLayer();
        countFlags = columns * rows;
        gameOver = false;
        setFirstStep(-1, -1);
    }

    void placeTails() {
        tailLayer = new Matrix(ZERO, columns, rows);
        for (int i = 0; i < tails; i++) {
            placeTail();
        }
    }

    private void placeTail() {

        while (true) {
            int[] coord = getRandomCoord();
            if (coord[0] == firstStep[0] && coord[1] == firstStep[1])
                continue;
            if (TAIL == tailLayer.getIconByCoords(coord[0], coord[1]))
                continue;

            tailLayer.setIconByCoords(coord[0], coord[1], TAIL);

            for (int[] array : getCoordsAround(coord[0], coord[1])) {
                if (TAIL != tailLayer.getIconByCoords(array[0], array[1])) {
                    tailLayer.setIconByCoords(
                            array[0], array[1],
                            tailLayer.getIconByCoords(array[0], array[1]).getNextIcon());
                }
            }
            break;
        }
    }

    void placeUpperLayer() {
        upperLayer = new Matrix(Icon.CLOSED, columns, rows);
    }

    int[] getRandomCoord() {
        int[] coord = new int[2];
        coord[0] = random.nextInt(columns);
        coord[1] = random.nextInt(rows);

        return coord;
    }

    ArrayList<int[]> getCoordsAround(int coordX, int coordY) {
        ArrayList<int[]> list = new ArrayList<>();
        for (int x = coordX - 1; x <= coordX + 1; x++) {
            for (int y = coordY - 1; y <= coordY + 1 ; y++) {
                if (isNotBound(x, y)) {
                    if (x != coordX || y != coordY)
                        list.add(new int[]{x, y});
                }
            }
        }
        return list;
    }

    void toggleFlagged(int x, int y) {
        if (upperLayer.getIconByCoords(x, y) == Icon.FLAGGED)
            upperLayer.setIconByCoords(x, y, Icon.INFORM);
        else if (upperLayer.getIconByCoords(x, y) == Icon.INFORM)
            upperLayer.setIconByCoords(x, y, Icon.CLOSED);
        else if (upperLayer.getIconByCoords(x, y) == Icon.CLOSED)
            upperLayer.setIconByCoords(x, y, Icon.FLAGGED);
    }

    void setOpened(int x, int y) {
        switch (upperLayer.getIconByCoords(x, y)) {
            case FLAGGED:
            case INFORM: break;
            case OPENED: openAroundNumber(x, y); break;
            case CLOSED:
                switch (tailLayer.getIconByCoords(x, y)) {
                    case ZERO: openCellsAround(x, y); break;
                    case TAIL: openAllBombs(x, y); break;
                    default: upperLayer.setIconByCoords(x, y, Icon.OPENED);
                        countFlagsDecrement();
                }
        }
    }

    private void openAroundNumber(int x, int y) {
        if (getCountFlagsAround(x, y) == tailLayer.getIconByCoords(x, y).ordinal()) {
            for (int[] around : getCoordsAround(x, y)){
                if (upperLayer.getIconByCoords(around[0], around[1]) == Icon.CLOSED) {
                    setOpened(around[0], around[1]);
                }
            }
        }
    }

    private int getCountFlagsAround(int x, int y) {
        int count = 0;
        for (int[] around : getCoordsAround(x, y)) {
            if (upperLayer.getIconByCoords(around[0], around[1]) == Icon.FLAGGED) {
                count++;
            }
        }
        return count;
    }

    private void openCellsAround(int x, int y) {
        upperLayer.setIconByCoords(x, y, Icon.OPENED);
        countFlagsDecrement();
        for (int[] around : getCoordsAround(x, y)) {
            setOpened(around[0], around[1]);
        }
    }

    private void openAllBombs(int bombX, int bombY) {
        gameOver = true;
        message = "Один котик сегодня зол";
        upperLayer.setIconByCoords(bombX, bombY, Icon.STEPPED);
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                if (tailLayer.getIconByCoords(x, y) == Icon.TAIL) {
                    if (upperLayer.getIconByCoords(x, y) == Icon.CLOSED) {
                        upperLayer.setIconByCoords(x, y, Icon.OPENED);
                    }

                } else {
                    if (upperLayer.getIconByCoords(x, y) == Icon.FLAGGED) {
                        upperLayer.setIconByCoords(x, y, Icon.NOTAIL);
                    }
                }
            }
        }
    }

    public boolean isFirstStep() {
        return firstStep[0] == -1 && firstStep[1] == -1;
    }

    public void setFirstStep(int x, int y) {
        firstStep[0] = x;
        firstStep[1] = y;
        if (tailLayer.getIconByCoords(x, y) == TAIL) {
            placeTails();
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    void isWin() {
        if (countFlags == tails) {
            gameOver = true;
            message = "Поздравляю! Все хвостики остались целы!";
        }
    }

    public Icon getIcon(int x, int y) {
        if (upperLayer.getIconByCoords(x, y) == Icon.OPENED)
            return tailLayer.getIconByCoords(x, y);
        return upperLayer.getIconByCoords(x, y);
    }

    private void countFlagsDecrement() {
        countFlags--;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public String getMessage() {
        return message;
    }
}
