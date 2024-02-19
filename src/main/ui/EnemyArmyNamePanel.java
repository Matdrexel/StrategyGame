package ui;

import model.Army;

// Represents an ArmyNamePanel to set the name of the enemy army
public class EnemyArmyNamePanel extends ArmyNamePanel {

    // EFFECTS: initializes the panel with the frame and the name that is not allowed for the enemy army
    public EnemyArmyNamePanel() {
        super();
    }

    // EFFECTS: returns the instructions text that prompts the user to input the enemy's army name
    @Override
    protected String getInstructions() {
        return "What would you like your opponent's army to be called?";
    }

    // EFFECTS: returns the tip that notifies the user that this name cannot be the same as their army
    @Override
    protected String getTipText() {
        return "Note: this must be different than your army name";
    }

    // MODIFIES: this, frame
    // EFFECTS: if the name the user inputted is illegal, then sets the error label to inform the user
    //          they need to pick a different name
    //          else sets the enemy army's name to the user's inputted text
    @Override
    protected void textFieldAction(Army enemy) {
        GUI.getInstance().setPlayer2(enemy);
    }
}
