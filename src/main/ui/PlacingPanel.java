package ui;

import model.*;
import model.exceptions.CheckedGameException;
import model.exceptions.OccupiedException;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// Represents a panel used to place the user's army on the stage
public class PlacingPanel extends DefaultPanel {

    private final Stage stage;
    private final Army player1;
    private final ConfirmationFrame confirm;
    private final JPanel placeArmyArea;
    private final JLabel placeWarriorError;
    private final Map<Warrior, JButton> placeButtonMap;
    private final Map<Position, JButton> stageButtonMap;
    private Warrior activeWarrior;
    private Position activePosition;

    // EFFECTS: Initializes a panel with all the positions on the stage and all the warriors in player1
    //          to be placed
    public PlacingPanel(Stage stage, Army player1) {
        super();
        this.stage = stage;
        this.player1 = player1;
        placeButtonMap = new HashMap<>();
        stageButtonMap = new HashMap<>();
        setLayout(new GridBagLayout());
        JPanel stageArea = new DefaultPanel();
        stageArea.setLayout(new GridLayout(stage.getFarthestBottom() + 1, stage.getFarthestRight() + 1));
        for (Position pos : this.stage) {
            stageArea.add(makePlacingButton(pos));
        }
        add(stageArea, UiFormatter.makeGBC(0,0,1,1,1,1));
        placeArmyArea = makePlaceArmyButtons();
        placeWarriorError = UiFormatter.makeErrorLabel();
        add(placeWarriorError, UiFormatter.makeGBC(0,1,1,1,1,0));
        add(placeArmyArea, UiFormatter.makeGBC(0,2,1,1,1,0));
        confirm = new ConfirmPlacementFrame();
    }

    // EFFECTS: creates a button representing a position on the stage for a warrior to
    //          be placed
    private JButton makePlacingButton(Position pos) {
        JButton positionButton = UiFormatter.makeDefaultButton();
        stageButtonMap.put(pos, positionButton);
        positionButton.addActionListener(e -> {
            activePosition = pos;
            confirm.openFrame(positionButton);
        });
        return positionButton;
    }

    // MODIFIES: this
    // EFFECTS: places the active warrior in position x and y of the map and modifies
    //          the JFrame accordingly. Once all warriors have been placed, begins
    //          placing the enemy warriors
    private void placeWarrior(Position pos, JButton positionButton) {
        if (activeWarrior != null) {
            try {
                activeWarrior.placeWarrior(stage, pos.getPosX(), pos.getPosY());
                positionButton.setText(activeWarrior.getName());
                positionButton.setIcon(GUI.getInstance().getWarriorIcon(activeWarrior));
                positionButton.setBorder(BorderFactory.createLineBorder(UiFormatter.PLAYER1_UNIT));
                placeArmyArea.remove(placeButtonMap.get(activeWarrior));
                placeButtonMap.remove(activeWarrior);
                activeWarrior = null;
                placeWarriorError.setText("");
                GUI.getInstance().refreshPanel();
                if (placeButtonMap.isEmpty()) {
                    GUI.getInstance().placeEnemyArmy();
                }
            } catch (OccupiedException e1) {
                placeWarriorError.setText("This Position is Occupied");
            } catch (CheckedGameException e1) {
                throw new RuntimeException("ERROR: " + e1.getMessage());
            }
        } else {
            placeWarriorError.setText("Please select a warrior");
        }
    }

    // MODIFIES: this
    // EFFECTS: creates a JPanel representing each of the warriors that have not yet been
    //          placed and records how many warriors are required to be placed
    private JPanel makePlaceArmyButtons() {
        JPanel placeArmy = new DefaultPanel();
        placeArmy.setLayout(new GridLayout(0,4,2,2));
        placeArmy.setBackground(Color.BLACK);
        for (Warrior warrior : player1) {
            placeArmy.add(placeWarriorButton(warrior));
        }
        return placeArmy;
    }

    // EFFECTS: creates a button that activates the warrior on the button
    private JButton placeWarriorButton(Warrior warrior) {
        JButton warriorButton = UiFormatter.makeDefaultButton(warrior.getName(),
                GUI.getInstance().getWarriorIcon(warrior));
        warriorButton.setBackground(Color.WHITE);
        warriorButton.addActionListener(e -> {
            activeWarrior = warrior;
            for (Warrior w : placeButtonMap.keySet()) {
                JButton b = placeButtonMap.get(w);
                if (b == warriorButton) {
                    b.setBackground(UiFormatter.PLAYER1_UNIT);
                } else {
                    b.setBackground(Color.WHITE);
                }
            }
        });
        placeButtonMap.put(warrior, warriorButton);
        return warriorButton;
    }

    // Represents a class used for confirming the placement of a Warrior
    private class ConfirmPlacementFrame extends ConfirmationFrame {

        public ConfirmPlacementFrame() {
            super("Would you like to place this warrior here?");
        }

        // MODIFIES: this
        // EFFECTS: tries to place the active warrior in the active position
        @Override
        protected void yesAction() {
            placeWarrior(activePosition, stageButtonMap.get(activePosition));
        }

        // MODIFIES: this
        // EFFECTS: removes the error text
        @Override
        protected void noAction() {
            placeWarriorError.setText("");
        }
    }
}
