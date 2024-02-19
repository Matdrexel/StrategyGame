package ui;

import model.Army;
import model.exceptions.DuplicateNameException;

import javax.swing.*;
import java.awt.*;

// Represents a JPanel for the player to input an army name
public abstract class ArmyNamePanel extends DefaultPanel {

    private final JLabel nameArmyError;
    private final JTextField name;

    // EFFECTS: creates a new ArmyPanel and places a message label, error label, and text fiel
    //          in the Panel
    public ArmyNamePanel() {
        super();
        setLayout(new GridBagLayout());
        add(makeMessageLabel(), UiFormatter.makeGBC(0,0,-100,-100,1,6));
        nameArmyError = UiFormatter.makeErrorLabel();
        add(nameArmyError, UiFormatter.makeGBC(0,1,-100,-100,1,0));
        name = makeTextField();
        add(name, UiFormatter.makeGBC(0,2,-100,-100,1,2));
    }

    // EFFECTS: creates a splashLabel with the instructions message
    private JLabel makeMessageLabel() {
        return UiFormatter.makeSplashLabel("<html>" + getInstructions() + "</html>", Color.BLUE, Color.WHITE);
    }

    // EFFECTS: creates a JTextField that allows the user to input a name
    private JTextField makeTextField() {
        JTextField makeName = new JTextField();
        makeName.setToolTipText(getTipText());
        makeName.addActionListener(e -> {
            try {
                Army army = new Army(getArmyName());
                textFieldAction(army);
            } catch (DuplicateNameException e1) {
                illegalName();
            }
        });
        return makeName;
    }

    // MODIFIES: this
    // EFFECTS: sets the text of the error label to notify the user that their inputted name is illegal
    protected void illegalName() {
        nameArmyError.setText("Enemy army cannot have the same name as your army");
    }

    // EFFECTS: returns the text found in name
    protected String getArmyName() {
        return name.getText();
    }

    // EFFECTS: returns the string that tells the user what to input into the text field
    protected abstract String getInstructions();

    // EFFECTS: returns the string that gives the user a hint as to what to put in the text field
    protected abstract String getTipText();

    // EFFECTS: the action that happens when something is entered into the text field
    protected abstract void textFieldAction(Army army);
}
