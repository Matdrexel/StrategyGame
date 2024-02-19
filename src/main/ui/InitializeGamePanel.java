package ui;

import javax.swing.*;
import java.awt.*;

// Represents a Panel for the user to select whether they want to load a game or continue one
public class InitializeGamePanel extends DefaultPanel {

    // EFFECTS: allows the user to select whether they want to start a new game or load a previous one
    public InitializeGamePanel(JButton loadGame) {
        super();
        setLayout(new GridBagLayout());
        JLabel splashScreen = UiFormatter.makeSplashLabel("Welcome to my Game!", Color.BLUE, Color.WHITE);
        JPanel buttonPanel = new DefaultPanel();
        buttonPanel.setLayout(new GridLayout(1,2));
        buttonPanel.add(makeNewGameButton());
        buttonPanel.add(loadGame);
        add(splashScreen, UiFormatter.makeGBC(0,0,2,1,2,6));
        add(buttonPanel, UiFormatter.makeGBC(0,1,1,1,1,5));
    }

    // EFFECTS: creates a button that starts a new game.
    private JButton makeNewGameButton() {
        JButton newGame = UiFormatter.makeDefaultButton("New Game");
        newGame.addActionListener(e -> GUI.getInstance().startNewGame());
        return newGame;
    }
}
