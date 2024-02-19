package ui;

import model.Warrior;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// Represents a panel to view the Instructions
public class InstructionsPanel extends DefaultPanel {

    private static final String INSTRUCTIONS_1 = "<html><center>Setting up the game:<br/><br/>In this game, "
            + "you will construct an army unique pieces, called warriors, and battle a "
            + "computer player in a tactical RPG format.<br/> The game begins by "
            + "choosing your warriors. You will be given a list of warriors along "
            + "with their stats to pick from.<br/> Next, you will choose the size "
            + "of the stage you'd like to play on.<br/> The stage is a rectangular "
            + "grid where each grid square can house a warrior.<br/> You must "
            + "then place all of the warriors in you army on a square on the "
            + "map. No two warriors can occupy the same position.<br/> Once you "
            + "have placed all your warriors, the computer player will place "
            + "all of their warriors and the battle will begin.</center></html>";
    private static final String INSTRUCTIONS_2 = "<html><center>Game and Movement:<br/><br/>The goal "
            + "of the battle is to reduce all of your opponent's warriors' 'hp' to 0.<br/>"
            + "When your turn begins, you can choose any alive warrior in your army to move or "
            + "attack an enemy warrior.<br/> Each warrior can move a maximum of "
            + "'movement' squares away.<br/>After moving, this warrior cannot be "
            + "moved again this turn.<br/> To be able to attack an enemy unit, you must be a "
            + "maximum of 'range' grid squares away.<br/> Each warrior can only attack once per turn, "
            + "and cannot move after attacking.<br/> If your warrior has their 'hp' reach 0,"
            + "they no longer appear on the map and cannot act.<br/> Once none of your "
            + "warriors can attack and/or you have chosen to end your turn, your opponent"
            + " begins their turn.<br/> The game continues until one army has successfully "
            + "reduced all of their opponent's warriors' 'hp' to 0.</center></html>";
    private static final String INSTRUCTIONS_3 = "<html><center>Battling:<br/><br/>The warrior that "
            + "lands the first blow is determined by which has the higher 'speed' stat.<br/> A "
            + "speed bonus of " + Warrior.SPEED_BONUS + " is added to the warrior that "
            + "started the battle.<br/> The amount of damage dealt from an "
            + "attack is the attacker's 'strength' stat minus the opponents 'defense' stat.<br/> "
            + "If this number is 0 or less, no damage is dealt.<br/> This damage then reduces "
            + "the opponent's hp by the corresponding amount.<br/> If their hp reaches below 0, "
            + "the warrior will die and be removed from the map.<br/> If the opponent is "
            + "still alive and the attacker is within their 'range' stat, they will then "
            + "attack their attacker.</center></html>";
    private static final String INSTRUCTIONS_4 = "<html><center>Stats Descriptions:<br/><br/>"
            + "hp -- The warrior's health. The warrior dies if this reaches 0<br/>"
            + "strength -- The warrior's power. Used to calculate the warrior's damage in battle<br/>"
            + "defense -- The warrior's toughness. Reduces the damage taken from enemy warriors<br/>"
            + "speed -- The warrior's preparedness. Determines which warrior attacks first in battle<br/>"
            + "movement -- The warrior's traversal. The number of grid spaces the warrior can move<br/>"
            + "range -- The warriors reach. The number of squares away a warrior can attack an enemy</center></html>";

    private final JLabel askMessage;
    private final JLabel instructionsMessage;
    private final GridBagConstraints labelLocation;
    private final ArrayList<String> instructionsOrder = new ArrayList<>();
    private JButton previous;
    private JButton next;
    private int instructionNum;

    // EFFECTS: initializes the instructions panel with a section to have the label explaining
    //          what the user should do, and the buttons for the user to press
    public InstructionsPanel() {
        super();
        setLayout(new GridBagLayout());
        makeInstructionsOrder();
        askMessage = UiFormatter.makeSplashLabel("Would you like to view the instructions?",
                UiFormatter.BABY_BLUE, Color.BLACK);
        instructionsMessage = new JLabel(INSTRUCTIONS_1, SwingConstants.CENTER);
        instructionsMessage.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        instructionsMessage.setBackground(UiFormatter.BABY_BLUE);
        instructionsMessage.setOpaque(true);
        labelLocation = UiFormatter.makeGBC(0,0,2,1, 1, 6);
        JPanel buttonPanel = new DefaultPanel();
        buttonPanel.setLayout(new GridLayout(1,2));
        buttonPanel.add(makePreviousButton());
        buttonPanel.add(makeNextButton());
        askInstructions();
        add(buttonPanel, UiFormatter.makeGBC(0,1,-100,-100,1,1));
    }

    // MODIFIES: this, frame
    // EFFECTS: sets the JLabel to the askMessage and sets askPhase to true
    private void askInstructions() {
        remove(instructionsMessage);
        add(askMessage, labelLocation);
        instructionNum = -1;
        previous.setText("Yes");
        next.setText("No");
        GUI.getInstance().refreshPanel();
    }

    // MODIFIES: this
    // EFFECTS: creates the order of the instructions messages
    private void makeInstructionsOrder() {
        instructionsOrder.add(INSTRUCTIONS_1);
        instructionsOrder.add(INSTRUCTIONS_2);
        instructionsOrder.add(INSTRUCTIONS_3);
        instructionsOrder.add(INSTRUCTIONS_4);
    }

    // MODIFIES: this
    // EFFECTS: creates a button used to begin showing the instructions if the user is not yet
    //          viewing instructions, or moves backwards through the instructions menu
    private JButton makePreviousButton() {
        previous = UiFormatter.makeDefaultButton("Yes");
        previous.addActionListener(e -> {
            if (instructionNum == -1) {
                instructionNum++;
                viewInstructions();
            } else {
                instructionNum--;
                backwardsAction();
            }
        });
        return previous;
    }

    // MODIFIES: this, frame
    // EFFECTS: sets the label and the text on buttons to the first instruction label
    private void viewInstructions() {
        remove(askMessage);
        add(instructionsMessage, labelLocation);
        instructionsMessage.setText(INSTRUCTIONS_1);
        previous.setText("Back to Start Menu");
        next.setText("Next");
        GUI.getInstance().refreshPanel();
    }

    // MODIFIES: this
    // EFFECTS: if the instruction index is less than 0, then returns to asking the user if
    //          they would like to view the instructions
    //          else, sets the text to the instruction index of the instructions order,
    //          and updates the button text accordingly
    private void backwardsAction() {
        if (instructionNum == -1) {
            askInstructions();
        } else {
            instructionsMessage.setText(instructionsOrder.get(instructionNum));
            if (instructionNum == 0) {
                previous.setText("Back to Start Menu");
            } else if (instructionNum == (instructionsOrder.size() - 2)) {
                next.setText("Next");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: creates a button used to skip the instructions if the user is not yet
    //          viewing instructions, or moves forwards through the instructions menu
    private JButton makeNextButton() {
        next = UiFormatter.makeDefaultButton("No");
        next.addActionListener(e -> {
            if (instructionNum == -1) {
                finishPhase();
            } else {
                instructionNum++;
                forwardsAction();
            }
        });
        return next;
    }

    // MODIFIES: this
    // EFFECTS: if the instruction index is equal to the instructionsOrder size, then
    //          moves the GUI to the next phase
    //          else, sets the text to the instruction index of the instructions order,
    //          and updates the button text accordingly
    private void forwardsAction() {
        if (instructionNum == instructionsOrder.size()) {
            finishPhase();
        } else {
            instructionsMessage.setText(instructionsOrder.get(instructionNum));
            if (instructionNum == 1) {
                previous.setText("Previous");
            } else if (instructionNum == (instructionsOrder.size() - 1)) {
                next.setText("Finish Instructions");
            }
        }
    }

    // MODIFIES: frame
    // EFFECTS: finishes the instructions phase
    private void finishPhase() {
        GUI.getInstance().finishedInstructions();
    }
}
