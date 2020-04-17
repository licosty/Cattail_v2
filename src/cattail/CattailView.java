package cattail;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CattailView extends JPanel {
    private CattailGame game;
    private Minefield minefield;

    private final String gameName = "Cattail";
    private final int ICON_SIZE = 50;

    private JFrame frame;
    private JMenuBar menuBar;
    private JDialog jDialog;

    public CattailView(CattailGame game, Minefield minefield) {
        this.game = game;
        this.minefield = minefield;
        setIcons();
        initFrame();
    }

    private void initFrame() {
        frame = new JFrame(gameName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(getIcon("cattail-icon"));
        frame.setResizable(false);

        setPreferredSize(new Dimension(
                minefield.getColumns() * ICON_SIZE,
                minefield.getRows() * ICON_SIZE));
        frame.add(this);

        createMenuBar();
        frame.setJMenuBar(menuBar);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / ICON_SIZE;
                int y = e.getY() / ICON_SIZE;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    game.pressLeftButton(x, y);
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    game.pressRightButton(x, y);
                }
                repaint();
                if (minefield.isGameOver()) {
                    createDialog();
                    jDialog.setVisible(true);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int y = 0; y < minefield.getRows(); y++) {
            for (int x = 0; x < minefield.getColumns(); x++) {
                g.drawImage(minefield.getIcon(x, y).icon,
                            x * ICON_SIZE,
                            y * ICON_SIZE, this);
            }
        }
    }

    private void createMenuBar() {
        Font font = new Font("Verdana", Font.PLAIN, 11);
        menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Игра");

        JMenuItem newGame = new JMenuItem("Новая игра");
        newGame.setFont(font);
        gameMenu.add(newGame);

        newGame.addActionListener(e -> {
            game.startNewGame();
            repaint();
        });

        JMenu settings = new JMenu("Уровень сложности");
        settings.setFont(font);
        gameMenu.add(settings);

        JMenuItem easy = new JMenuItem("Простой 9х9");
        easy.setFont(font);
        easy.setActionCommand("easy");
        easy.addActionListener(this::selectMode);

        JMenuItem middle = new JMenuItem("Средний 16х16");
        middle.setFont(font);
        middle.setActionCommand("middle");
        middle.addActionListener(this::selectMode);

        JMenuItem expert = new JMenuItem("Эксперт 30х16");
        expert.setFont(font);
        expert.setActionCommand("expert");
        expert.addActionListener(this::selectMode);

        settings.add(easy);
        settings.add(middle);
        settings.add(expert);

        gameMenu.addSeparator();

        JMenuItem exit = new JMenuItem("Выход");
        exit.setFont(font);
        gameMenu.add(exit);

        exit.addActionListener(e -> System.exit(0));

        JMenu help = new JMenu("Помощь");

        JMenuItem gameRules = new JMenuItem("Правила игры");
        gameRules.setFont(font);
        gameRules.setActionCommand("rules");
        help.add(gameRules);

        gameRules.addActionListener(this::createInfoDialog);

        JMenuItem advices = new JMenuItem("Советы");
        advices.setFont(font);
        advices.setActionCommand("advices");
        help.add(advices);

        advices.addActionListener(this::createInfoDialog);

        menuBar.add(gameMenu);
        menuBar.add(help);

    }

    private void createDialog() {
        jDialog = new JDialog(frame, gameName, true);
        jDialog.setIconImage(getIcon("cattail-icon"));
        jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jDialog.setMinimumSize(new Dimension(180, 90));
        jDialog.setResizable(false);

        JPanel label = createLabel();
        JPanel button = createButton();
        jDialog.add(label, BorderLayout.NORTH);
        jDialog.add(button, BorderLayout.SOUTH);

        jDialog.pack();
        jDialog.setLocationRelativeTo(frame);
    }

    private JPanel createLabel() {
        JLabel label = new JLabel(minefield.getMessage());
        JPanel panel = new JPanel();
        panel.add(label);
        return panel;
    }

    private JPanel createButton() {
        JPanel panel = new JPanel();
        JButton ok = new JButton("ok");
        ok.addActionListener(e -> {
            game.pressLeftButton(1, 1);
            frame.repaint();
            jDialog.setVisible(false);
        });
        panel.add(ok);
        return panel;
    }

    private void selectMode(ActionEvent e) {
        game.selectMode(e.getActionCommand());
        minefield = game.getMinefield();
        repaint();

        frame.remove(this);
        setPreferredSize(new Dimension(
                minefield.getColumns() * ICON_SIZE,
                minefield.getRows() * ICON_SIZE));
        frame.add(this);

        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private void createInfoDialog(ActionEvent e) {
        Font font = new Font("Verdana", Font.PLAIN, 11);

        JTextArea textArea = new JTextArea(0, frame.getInsets().top);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEnabled(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textArea.setDisabledTextColor(Color.black);
        textArea.setFont(font);

        textArea.setMinimumSize(new Dimension(
                minefield.getColumns() * ICON_SIZE,
                minefield.getRows() * ICON_SIZE));

        StringBuilder windowName = new StringBuilder();
        textArea.setText(game.readText(e.getActionCommand(), windowName));

        JDialog dialogText = new JDialog(frame, windowName.toString(), true);
        dialogText.setIconImage(getIcon("cattail-icon"));
        dialogText.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        dialogText.add(textArea);

        dialogText.setResizable(false);
        dialogText.pack();
        dialogText.pack();
        dialogText.setLocationRelativeTo(frame);
        dialogText.setVisible(true);
    }

    public Image getIcon(String iconName) {
        String name = "/icons/" + iconName + ".png";
        ImageIcon image = new ImageIcon(getClass().getResource(name));
        return image.getImage();
    }

    private void setIcons() {
        for (Icon i : Icon.values()) {
            i.icon = getIcon(i.name().toLowerCase());
        }
    }
}
