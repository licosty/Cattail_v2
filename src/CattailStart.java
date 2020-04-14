import cattail.*;

public class CattailStart {
    public static void main(String[] args) {
        Minefield minefield = new Minefield();
        CattailGame game = new CattailGame(minefield);
        new CattailView(game, minefield);
    }
}
