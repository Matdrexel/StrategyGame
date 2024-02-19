package ui;

import model.Army;
import model.Warrior;
import model.exceptions.DuplicateNameException;

import javax.swing.*;
import java.awt.*;
import java.util.*;

// Represents a panel used for choosing the warriors in the user's army
public class ChooseWarriorsPanel extends DefaultPanel {

    private final Army player1;

    private Warrior activeWarrior;

    private final JPanel customArea;
    private JPanel recruitArea;
    private JPanel armyArea;
    private JPanel optionsArea;
    private JPanel statArea;

    private JButton addRecruit;

    private final GridBagConstraints chooseMainLocation;
    private final GridBagConstraints chooseWarriorsLocation;

    private final Set<Warrior> recruits;

    private final Map<Warrior, ImageIcon> warriorImageMap;
    private final Map<Warrior, JButton> recruitButtonMap;

    // EFFECTS: creates the choose recruits window
    public ChooseWarriorsPanel(Army player1) {
        super();
        this.player1 = player1;
        recruits = new HashSet<>();
        warriorImageMap = new HashMap<>();
        recruitButtonMap = new HashMap<>();
        makeRecruits();
        setLayout(new GridBagLayout());
        chooseMainLocation = UiFormatter.makeGBC(0,0,3,1,3,4);
        chooseWarriorsLocation = UiFormatter.makeGBC(0,1,4,1,4,0);
        makeRecruitButtons();
        createOptionButtons();
        makeDefaultStatScreen();
        customArea = new CustomWarriorPanel(this);
    }

    // MODIFIES: this
    // EFFECTS: makes a list of all warriors the player can choose to recruit and records
    //          the number of total recruits
    @SuppressWarnings("methodlength")
    private void makeRecruits() {
        try {
            recruits.add(makeWarrior("Lucia", 8, 12, 10,
                    1, 3,6, 1, ImagePath.AXE));
            recruits.add(makeWarrior("Moria", 12, 9, 5,
                    3, 2,5, 2, ImagePath.SWORD));
            recruits.add(makeWarrior("Kayla", 20, 7, 4,
                    2, 8,7, 2, ImagePath.DAGGER));
            recruits.add(makeWarrior("Johny", 1, 100, 5,
                    0, 20,30, 1, ImagePath.AXE));
            recruits.add(makeWarrior("Isiah", 16, 2, 1,
                    7, 25,10, 3, ImagePath.SHIELD));
            recruits.add(makeWarrior("Mitsi", 20, 7, 2,
                    5, 9,5, 1, ImagePath.SWORD));
            recruits.add(makeWarrior("Judie", 4, 6, 7,
                    8, 13,3, 7, ImagePath.SHIELD));
            recruits.add(makeWarrior("Agara", 6, 10, 2,
                    4, 11,7, 3, ImagePath.BOW));
            recruits.add(makeWarrior("Bulky", 20, 18, 0,
                    4, 1,3, 1, ImagePath.AXE));
            recruits.add(makeWarrior("Benji", 30, 5, 0,
                    0, 18,15, 5, ImagePath.BOW));
            recruits.add(makeWarrior("Frazz", 20, 8, 7,
                    3, 16,6, 1, ImagePath.DAGGER));
        } catch (DuplicateNameException e) {
            throw new RuntimeException("ERROR: " + e.getMessage());
        }
    }

    // MODIFIES: this
    // EFFECTS: constructs a new warrior with the parameters' stats and
    //          associates the warrior to the image icon and returns the warrior.
    //          If this warrior has a duplicate name, throws DuplicateNameException
    private Warrior makeWarrior(String name, int hp, int strength, int speed, int defense,
                                int luck, int movement, int range, ImagePath image) throws DuplicateNameException {
        Warrior warrior = new Warrior(name, hp, strength, speed, defense, luck, movement, range, image);
        warriorImageMap.put(warrior, UiFormatter.makeResizableImageForBattle(image));
        return warrior;
    }

    // MODIFIES: this
    // EFFECTS: creates buttons for the user to choose the available recruits
    private void makeRecruitButtons() {
        recruitArea = new DefaultPanel();
        recruitArea.setBackground(Color.BLACK);
        recruitArea.setLayout(new GridLayout(0, 4, 2, 2));
        for (Warrior recruit : recruits) {
            JButton button = makeRecruitButton(recruit);
            recruitArea.add(button);
        }
        add(recruitArea, chooseWarriorsLocation);
        armyArea = new DefaultPanel();
        armyArea.setBackground(Color.BLACK);
        armyArea.setLayout(new GridLayout(0, 4, 2, 2));
    }

    // MODIFIES: this
    // EFFECTS: creates a JButton which displays the stats of the recruit and adds
    //          an association between this recruit and this button to the recruit button map
    private JButton makeRecruitButton(Warrior recruit) {
        JButton button = UiFormatter.makeDefaultButton(recruit.getName());
        button.setBackground(Color.WHITE);
        button.setToolTipText("View " + recruit.getName() + "'s stats");
        recruitButtonMap.put(recruit, button);
        button.addActionListener(e -> {
            if (!player1.getWarriors().contains(recruit)) {
                optionsArea.remove(addRecruit);
                optionsArea.add(addRecruit, 0);
            }
            displayStats(recruit);
            colourRecruitButtons(button);
        });
        button.setIcon(warriorImageMap.get(recruit));
        return button;
    }

    // MODIFIES: this
    // EFFECTS: colours all recruit buttons white except for the parameter button which
    //          is coloured to be active
    private void colourRecruitButtons(JButton button) {
        for (Warrior r : recruitButtonMap.keySet()) {
            JButton b = recruitButtonMap.get(r);
            if (b == button) {
                b.setBackground(UiFormatter.PLAYER1_UNIT);
            } else {
                b.setBackground(Color.WHITE);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: displays the stats of the warrior and sets the activeWarrior to the
    //          recruit
    private void displayStats(Warrior recruit) {
        if (!recruit.getFaction().equals(player1.getFactionName())) {
            activeWarrior = recruit;
        }
        remove(customArea);
        remove(statArea);
        statArea = new DefaultPanel();
        statArea.setLayout(new GridBagLayout());
        statArea.setBackground(UiFormatter.BABY_BLUE);
        JLabel name = new JLabel("<html><u>" + recruit.getName()
                + (((recruit.getName()).endsWith("s")) ? "'" : "'s") + " Stats</u></html>", SwingConstants.CENTER);
        name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        statArea.add(name, UiFormatter.makeGBC(0,0,2,1,2,1));
        makeStat("HP", recruit.getHP(), 40, 1);
        makeStat("Strength", recruit.getStrength(), 20, 2);
        makeStat("Speed", recruit.getSpeed(), 20, 3);
        makeStat("Defense", recruit.getDefense(), 15, 4);
        makeStat("Luck", recruit.getLuck(), 30,5);
        makeStat("Movement", recruit.getMovement(), 12, 6);
        makeStat("Range", recruit.getRange(), 6, 7);
        add(statArea, chooseMainLocation);
        GUI.getInstance().refreshPanel();
    }

    // MODIFIES: this
    // EFFECTS: creates a stat bar and label based on the warrior's stats and places it
    //          in the statArea at the given height
    private void makeStat(String label, int stat, int max, int height) {
        JProgressBar statBar = UiFormatter.makeStatBar(stat, max);
        JLabel statLabel = new JLabel(label + ":   ");
        statLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        statArea.add(statLabel, UiFormatter.makeGBC(0,height, 1,1,0,1));
        statArea.add(statBar, UiFormatter.makeGBC(1,height, 1,1,1,1));
    }

    // MODIFIES: this
    // EFFECTS: creates buttons for the user to create a custom warrior or add
    //          the selected recruit to their army
    private void createOptionButtons() {
        optionsArea = new DefaultPanel();
        optionsArea.setLayout(new GridLayout(0,1));
        addRecruit = makeAddWarriorButton();
        JButton customWarrior = makeCustomWarriorButton();
        optionsArea.add(addRecruit);
        optionsArea.add(customWarrior);
        add(optionsArea, UiFormatter.makeGBC(3,0,1,1,0,4));
    }

    // EFFECTS: creates and returns a new JButton that adds the active warrior to the user's army
    private JButton makeAddWarriorButton() {
        JButton addRecruit = UiFormatter.makeDefaultButton("Add Recruit to Army");
        addRecruit.addActionListener(e -> addWarrior());
        return addRecruit;
    }

    // MODIFIES: this
    // EFFECTS: adds the active warrior to player1's army and removes the button associated
    //          with them
    private void addWarrior() {
        if (activeWarrior != null) {
            player1.addWarrior(activeWarrior);
            recruits.remove(activeWarrior);
            JButton warriorButton = recruitButtonMap.get(activeWarrior);
            recruitArea.remove(warriorButton);
            armyArea.add(warriorButton);
            activeWarrior = null;
            remove(statArea);
            remove(customArea);
            makeDefaultStatScreen();
            colourRecruitButtons(null);
            if (player1.getWarriors().size() == 1) {
                makeNewButtons();
            }
        } else {
            remove(statArea);
            makeDefaultStatScreen();
            statArea.add(new JLabel("Please select a warrior"));
        }
        GUI.getInstance().refreshPanel();
    }

    // MODIFIES: this
    // EFFECTS: adds a button to toggle between your army and the recruits and another
    //          button to finish selecting recruits
    private void makeNewButtons() {
        JButton viewArmy = makeViewArmyButton();
        optionsArea.add(viewArmy);
        JButton complete = makeCompleteButton();
        optionsArea.add(complete);
    }

    // EFFECTS: creates a JButton that moves to the make opponent phase when pressed
    private JButton makeCompleteButton() {
        JButton complete = UiFormatter.makeDefaultButton("Army is complete");
        complete.addActionListener(e -> GUI.getInstance().finishChooseRecruits(recruits, warriorImageMap));
        return complete;
    }

    // EFFECTS: creates and returns a new JButton which toggles between recruits and army
    private JButton makeViewArmyButton() {
        JButton viewArmy = UiFormatter.makeDefaultButton("View Army");
        viewArmy.addActionListener(e -> {
            if (viewArmy.getText().equals("View Recruits")) {
                viewArmy.setText("View Army");
                remove(armyArea);
                optionsArea.add(addRecruit, 0);
                add(recruitArea, chooseWarriorsLocation);
            } else {
                viewArmy.setText("View Recruits");
                remove(recruitArea);
                optionsArea.remove(addRecruit);
                add(armyArea, chooseWarriorsLocation);
            }
            GUI.getInstance().refreshPanel();
        });
        return viewArmy;
    }

    // EFFECTS: creates and returns a new JButton which creates a custom warrior
    private JButton makeCustomWarriorButton() {
        JButton customWarrior = UiFormatter.makeDefaultButton("Create custom warrior");
        customWarrior.addActionListener(e -> {
            remove(statArea);
            add(customArea, chooseMainLocation);
            optionsArea.remove(addRecruit);
            activeWarrior = null;
            colourRecruitButtons(null);
            GUI.getInstance().refreshPanel();
        });
        return customWarrior;
    }

    // EFFECTS: creates a default stats JPanel and returns it
    private void makeDefaultStatScreen() {
        statArea = new DefaultPanel();
        statArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        statArea.setBackground(Color.ORANGE);
        add(statArea, chooseMainLocation);
    }

    // MODIFIES: this
    // EFFECTS: tries to make a custom warrior with the parameter specifications and adds them to player1's army
    //          if this custom warrior has the same name as another warrior, throws DuplicateNameException
    public void makeCustomWarrior(String name, int hp, int strength, int speed, int defense,
                                  int luck, int movement, int range, ImagePath image) throws DuplicateNameException {
        activeWarrior = makeWarrior(name, hp, strength, speed, defense, luck, movement, range, image);
        makeRecruitButton(activeWarrior);
        addWarrior();
    }
}
