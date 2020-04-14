package cattail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

public class CattailGame {
    private Minefield minefield;

    public CattailGame(Minefield minefield) {
        this.minefield = minefield;
        startNewGame();
    }

    void startNewGame() {
        minefield.startGame();
    }

    public void pressLeftButton(int x, int y) {
        if (minefield.isGameOver()) {
            startNewGame();
            return;
        }
        if (minefield.isFirstStep()) {
            minefield.setFirstStep(x, y);

        }
        minefield.setOpened(x, y);
        minefield.isWin();
    }

    public void pressRightButton(int x, int y) {
        minefield.toggleFlagged(x, y);
    }

    public void selectMode(String mode) {
        switch (mode) {
            case "easy":
                minefield = new Minefield(9, 9, 10);
                startNewGame();
                break;
            case "middle":
                minefield = new Minefield(16, 16, 40);
                startNewGame();
                break;
            case "expert":
                minefield = new Minefield(30, 16, 100);
                startNewGame();
                break;
        }
    }

    public String readText(String actionCommand, StringBuilder windowName) {
        StringBuilder rules = new StringBuilder();

        String filePath = "";
        if (actionCommand.equals("rules")) {
            filePath = "res/rulesOfGame.txt";
            windowName.append("Правила");
        }
        if (actionCommand.equals("advices")) {
            filePath = "res/advices.txt";
            windowName.append("Советы");
        }

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            Stream<String> stream = br.lines();
            stream.forEach(e ->
                rules.append(e).append("\n")
            );
        } catch (IOException ex) {
            rules.append("Не удалось отобразить текст.");
        }

        return rules.toString().trim();
    }

    public Minefield getMinefield() {
        return minefield;
    }
}
