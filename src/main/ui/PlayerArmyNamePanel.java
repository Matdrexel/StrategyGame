package ui;

import model.Army;

// Represents an ArmyNamePanel to set the name of the user's army
public class PlayerArmyNamePanel extends ArmyNamePanel {

    // EFFECTS: initializes the Panel and sets the frame to frame
    public PlayerArmyNamePanel() {
        super();
    }

    // EFFECTS: returns the instructions text to tell the user to input their army name
    @Override
    protected String getInstructions() {
        return "What would you like your army to be called?";
    }

    // EFFECTS: returns a tool tip to tell the user to make a name for their army
    @Override
    protected String getTipText() {
        return "Make a name for your army";
    }

    // MODIFIES: frame
    // EFFECTS: sets the user's army name to the value the user input
    @Override
    protected void textFieldAction(Army player) {
        GUI.getInstance().setPlayer1(player);
    }
}
