package ui;

import model.exceptions.DuplicateNameException;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

// Represents a Panel used for Creating a custom warrior
public class CustomWarriorPanel extends DefaultPanel {

    private final ChooseWarriorsPanel parent;
    private final JLabel customError;
    private JTextField customName;
    private JTextField customHP;
    private JTextField customStrength;
    private JTextField customSpeed;
    private JTextField customDefense;
    private JTextField customLuck;
    private JTextField customMovement;
    private JTextField customRange;
    private Set<JButton> warriorImageButtons;
    private ImagePath activeImage;

    // MODIFIES: this
    // EFFECTS: creates the panel to design custom warriors
    public CustomWarriorPanel(ChooseWarriorsPanel choosePanel) {
        super();
        parent = choosePanel;
        setLayout(new GridBagLayout());
        setBackground(UiFormatter.BABY_BLUE);
        JLabel title = new JLabel("Create a Custom Warrior", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        add(title, UiFormatter.makeGBC(0,0,5,2,5,2));
        makeCustomWarriorStats();
        customError = UiFormatter.makeErrorLabel();
        add(customError, UiFormatter.makeGBC(0,10,5,1,5,0));
        makeImageButtons();
    }

    // MODIFIES: this
    // EFFECTS: creates the JTextFields and Labels for the Custom Warrior Panel
    private void makeCustomWarriorStats() {
        customName = makeCustomWarriorStat("Name", 2, -1);
        customHP = makeCustomWarriorStat("HP", 3, 1);
        customStrength = makeCustomWarriorStat("Strength", 4, 1);
        customSpeed = makeCustomWarriorStat("Speed", 5, 0);
        customDefense = makeCustomWarriorStat("Defense", 6, 0);
        customLuck = makeCustomWarriorStat("Luck",7,0);
        customMovement = makeCustomWarriorStat("Movement", 8, 1);
        customRange = makeCustomWarriorStat("Range", 9, 1);
    }

    // MODIFIES: this
    // EFFECTS: sets up a JTextField with an associated JLabel in the given y position. The JLabel has
    //          a tool tip stating the minimum value you can set that TextField to, or nothing
    //          if min == -1
    private JTextField makeCustomWarriorStat(String field, int y, int min) {
        JTextField customEntry = new JTextField();
        if (min != -1) {
            customEntry.setToolTipText("Must be greater than or equal to " + min);
        }
        customEntry.addActionListener(e -> createCustomWarrior());
        JLabel customField = new JLabel(field + ":     ");
        customField.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        add(customField, UiFormatter.makeGBC(0,y,2,1,2,1));
        add(customEntry, UiFormatter.makeGBC(2,y,3,1,3,1));
        return customEntry;
    }

    // MODIFIES: this
    // EFFECTS: if the custom warrior has correctly entered stats, creates a custom warrior
    //          else, notifies the user what the error was in the custom error JLabel
    private void createCustomWarrior() {
        String name = customName.getText();
        if (allValidCustomStats() && (activeImage != null)) {
            customError.setText("");
            addCustomWarrior(name);
        } else if (!allValidCustomStats()) {
            customError.setText("One or more of the stats fields is invalid");
        } else {
            customError.setText("Please select an icon for your custom warrior");
        }
    }

    // EFFECTS: returns true if the text in all the Custom Warrior JTextField's have valid entries
    private boolean allValidCustomStats() {
        boolean b1 = customHP.getText().matches("[1-9]\\d*");
        boolean b2 = customStrength.getText().matches("[1-9]\\d*");
        boolean b3 = customSpeed.getText().matches("([1-9]\\d*)|0");
        boolean b4 = customDefense.getText().matches("([1-9]\\d*)|0");
        boolean b5 = customLuck.getText().matches("([1-9]\\d*)|0");
        boolean b6 = customMovement.getText().matches("[1-9]\\d*");
        boolean b7 = customRange.getText().matches("[1-9]\\d*");
        return (b1 && b2 && b3 && b4 && b5 && b6 && b7);
    }

    // MODIFIES: this
    // EFFECTS: creates a custom warrior with the entered stats and add it to player1's army
    private void addCustomWarrior(String name) {
        int hp = Integer.parseInt(customHP.getText());
        int strength = Integer.parseInt(customStrength.getText());
        int speed = Integer.parseInt(customSpeed.getText());
        int defense = Integer.parseInt(customDefense.getText());
        int luck = Integer.parseInt(customLuck.getText());
        int movement = Integer.parseInt(customMovement.getText());
        int range = Integer.parseInt(customRange.getText());
        try {
            parent.makeCustomWarrior(name, hp, strength, speed, defense, luck, movement, range, activeImage);
            activeImage = null;
            customName.setText("");
            customHP.setText("");
            customStrength.setText("");
            customSpeed.setText("");
            customDefense.setText("");
            customLuck.setText("");
            customMovement.setText("");
            customRange.setText("");
            colourImageButtons(null);
        } catch (DuplicateNameException e) {
            customError.setText("This warrior has the same name as another warrior. Please select a different name.");
        }
    }

    // MODIFIES: this
    // EFFECTS: creates the image buttons that can be selected for a custom warrior
    private void makeImageButtons() {
        JPanel imageButtons = new DefaultPanel();
        imageButtons.setLayout(new GridLayout(1,0));
        warriorImageButtons = new HashSet<>();
        imageButtons.add(makeImageButton(ImagePath.SWORD));
        imageButtons.add(makeImageButton(ImagePath.AXE));
        imageButtons.add(makeImageButton(ImagePath.BOW));
        imageButtons.add(makeImageButton(ImagePath.SHIELD));
        imageButtons.add(makeImageButton(ImagePath.DAGGER));
        add(imageButtons, UiFormatter.makeGBC(0,11,5,2,5,2));
    }

    // EFFECTS: creates a JButton that actives the image path when pressed
    private JButton makeImageButton(ImagePath imagePath) {
        JButton imageButton = UiFormatter.makeDefaultButton(null, imagePath);
        imageButton.addActionListener(e -> {
            activeImage = imagePath;
            colourImageButtons(imageButton);
            createCustomWarrior();
        });
        warriorImageButtons.add(imageButton);
        return imageButton;
    }

    // MODIFIES: this
    // EFFECTS: makes image button green and removes colours from all other buttons
    //          in the warrior image buttons set
    private void colourImageButtons(JButton imageButton) {
        for (JButton button : warriorImageButtons) {
            if (button == imageButton) {
                button.setBackground(UiFormatter.PLAYER1_UNIT);
            } else {
                button.setBackground(null);
            }
        }
    }
}
