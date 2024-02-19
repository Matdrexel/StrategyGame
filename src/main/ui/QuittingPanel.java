package ui;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;

// Represents a panel used for quitting the game
public class QuittingPanel extends DefaultPanel {

    // EFFECTS: Creates a Panel allowing the user to save and quit, just quit, or cancel the quit
    public QuittingPanel() {
        super();
        setLayout(new GridBagLayout());
        add(UiFormatter.makeSplashLabel("Would you like to save?", UiFormatter.BABY_BLUE, Color.BLACK),
                UiFormatter.makeGBC(0,0,3,1,1,6));
        add(makeSaveAndQuitButton(), UiFormatter.makeGBC(0,1,1,1,1,1));
        add(makeNoSaveAndQuitButton(), UiFormatter.makeGBC(1,1,1,1,1,1));
        add(makeCancelQuitButton(), UiFormatter.makeGBC(2,1,1,1,1,1));
    }

    // EFFECTS: returns a JButton that tries to save the armies and stage to JSON and then
    //          closes the application
    private JButton makeSaveAndQuitButton() {
        JButton yes = UiFormatter.makeDefaultButton("Save");
        yes.addActionListener(e -> {
            try {
                GUI.getInstance().saveGame();
                GUI.getInstance().quit();
            } catch (FileNotFoundException e1) {
                yes.setText("Error: cannot save");
            }
        });
        return yes;
    }

    // EFFECTS: returns a JButton that quits the application
    private JButton makeNoSaveAndQuitButton() {
        JButton no = UiFormatter.makeDefaultButton("Don't Save");
        no.addActionListener(e -> GUI.getInstance().quit());
        return no;
    }

    // EFFECTS: returns a JButton that returns to the battle phase if pressed
    private JButton makeCancelQuitButton() {
        JButton cancel = UiFormatter.makeDefaultButton("Cancel");
        cancel.addActionListener(e -> GUI.getInstance().cancelQuit());
        return cancel;
    }
}
