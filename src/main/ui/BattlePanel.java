package ui;

import model.*;
import model.exceptions.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.*;

// Represents a panel used for the battle phase of the game
public class BattlePanel extends DefaultPanel {

    private final Battle battle;

    private final JPanel battleOptionsArea;
    private final JPanel battleArea;

    private final JLabel battleError;

    private final ConfirmMoveFrame confirmMove;
    private final ConfirmAttackFrame confirmAttack;

    private final String playerArmy;

    private final JButton loadGameButton;
    private JButton move;
    private JButton end;

    private Map<Position, JButton> battleButtonMap;
    private Set<JButton> battleOptionButtons;

    private Timer timer;

    // EFFECTS: creates all elements required for the battle phase
    public BattlePanel(Battle battle, JButton load) {
        super();
        this.battle = battle;
        this.loadGameButton = load;
        playerArmy = battle.getCompetitors().get(0).getFactionName();
        setLayout(new GridBagLayout());
        JPanel stageArea = makeStageArea();
        battleOptionsArea = makeBattleOptionsArea();
        battleArea = new DefaultPanel();
        battleError = UiFormatter.makeErrorLabel();
        add(stageArea, UiFormatter.makeGBC(0,0,1,1,1,1));
        add(battleError, UiFormatter.makeGBC(0,1,2,1,2,0));
        add(battleArea, UiFormatter.makeGBC(0,2,2,1,2,0));
        add(battleOptionsArea, UiFormatter.makeGBC(1,0,1,1,0,1));
        confirmMove = new ConfirmMoveFrame();
        confirmAttack = new ConfirmAttackFrame();
    }

    // MODIFIES: this
    // EFFECTS: constructs a JPanel with JButton's representing each position of the stage
    //          and adds each pair of position and JButton to the battleButtonMap
    private JPanel makeStageArea() {
        JPanel stageArea = new DefaultPanel();
        battleButtonMap = new HashMap<>();
        stageArea.setLayout(new GridLayout(battle.getStageHeight(), battle.getStageWidth()));
        for (Position pos : battle) {
            JButton posButton = makeBattleButton(pos);
            stageArea.add(posButton);
        }
        return stageArea;
    }

    // MODIFIES: this
    // EFFECTS: creates a button associated with an x,y position of the stage, and adds a
    //          relationship between this position and this new button to [button map name]
    private JButton makeBattleButton(Position pos) {
        JButton battleButton = UiFormatter.makeDefaultButton();
        battleButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        if (pos.getUnit() != null) {
            battleButton.setText(pos.getUnit().getName());
            battleButton.setIcon(GUI.getInstance().getWarriorIcon(pos.getUnit()));
            battleButton.setDisabledIcon(battleButton.getIcon());
            if (pos.getUnit().getFaction().equals(playerArmy)) {
                battleButton.setBorder(BorderFactory.createLineBorder(UiFormatter.PLAYER1_UNIT));
            } else {
                battleButton.setBorder(BorderFactory.createLineBorder(UiFormatter.PLAYER2_UNIT));
            }
        }
        battleButton.addActionListener(e -> playerAction(pos));
        battleButtonMap.put(pos, battleButton);
        return battleButton;
    }

    // MODIFIES: this
    // EFFECTS: creates the battle options area
    private JPanel makeBattleOptionsArea() {
        battleOptionButtons = new HashSet<>();
        battleOptionButtons.add(loadGameButton);
        JPanel battleOptions = new DefaultPanel();
        battleOptions.setLayout(new GridBagLayout());
        battleOptions.add(makeMovableButton(), UiFormatter.makeGBC(0,0,1,1,1,1));
        battleOptions.add(makeSaveButton(), UiFormatter.makeGBC(0,1,1,1,1,1));
        battleOptions.add(makeEndTurnButton(), UiFormatter.makeGBC(0,2,1,1,1,1));
        battleOptions.add(loadGameButton, UiFormatter.makeGBC(0,3,1,1,1,1));
        battleOptions.add(makeForfeitButton(), UiFormatter.makeGBC(0,4,1,1,1,1));
        battleOptions.add(makeQuitButton(), UiFormatter.makeGBC(0,5,1,1,1,1));
        makeEndWarriorTurnButton();
        return battleOptions;
    }

    // EFFECTS: displays the movable warriors in player1's army as buttons
    private JButton makeMovableButton() {
        move = UiFormatter.makeDefaultButton("View Movable Warriors");
        move.addActionListener(e -> {
            battle.deselectPosition();
            setBattleArea(false);
            for (Warrior warrior : battle.getActiveFaction().canMove()) {
                battleArea.add(makeActivateButton(warrior));
            }
            highlightStage();
            GUI.getInstance().refreshPanel();
        });
        battleOptionButtons.add(move);
        return move;
    }

    // EFFECTS: sets the battle area as a stats panel if forStats is true, and as a button area
    //          if forStats is false
    private void setBattleArea(boolean forStats) {
        if (forStats) {
            battleArea.removeAll();
            battleArea.setLayout(new GridBagLayout());
        } else {
            battleArea.removeAll();
            battleArea.setLayout(new GridLayout(0,4,2,2));
            battleArea.setBackground(Color.BLACK);
        }
    }

    // EFFECTS: creates a JButton which sets the warrior to the active warrior and adds their
    //          stats to the battle area
    private JButton makeActivateButton(Warrior warrior) {
        JButton activate = UiFormatter.makeDefaultButton(warrior.getName(), warrior.getImageSource());
        activate.addActionListener(e -> {
            battle.selectPosition(warrior);
            refreshBattle();
        });
        return activate;
    }

    // EFFECTS: creates a JButton that saves the current game state using JSON
    private JButton makeSaveButton() {
        JButton save = UiFormatter.makeDefaultButton("Save Game");
        save.addActionListener(e -> {
            try {
                GUI.getInstance().saveGame();
                battleError.setText("Game saved successfully");
            } catch (FileNotFoundException e1) {
                battleError.setText("Game could not be saved.");
            }
        });
        battleOptionButtons.add(save);
        return save;
    }

    // EFFECTS: creates a JButton that ends player1's turn and begins player2's turn
    private JButton makeEndTurnButton() {
        JButton endTurn = UiFormatter.makeDefaultButton("End Turn");
        endTurn.addActionListener(e -> {
            battle.endArmyTurn();
            refreshBattle();
        });
        battleOptionButtons.add(endTurn);
        return endTurn;
    }

    // EFFECTS: creates a JButton that forfeits the game and takes you to the loss screen
    private JButton makeForfeitButton() {
        JButton forfeit = UiFormatter.makeDefaultButton("Forfeit");
        forfeit.addActionListener(e -> {
            battle.getActiveFaction().forfeit();
            refreshBattle();
        });
        battleOptionButtons.add(forfeit);
        return forfeit;
    }

    // EFFECTS: creates a button that prompts the user to save their game then closes the program
    private JButton makeQuitButton() {
        JButton quit = UiFormatter.makeDefaultButton("Quit Game");
        quit.addActionListener(e -> GUI.getInstance().saveAndQuit());
        battleOptionButtons.add(quit);
        return quit;
    }

    // MODIFIES: this
    // EFFECTS: creates a JButton which will set the activeWarrior to not be able to move
    //          or attack
    private void makeEndWarriorTurnButton() {
        end = UiFormatter.makeDefaultButton("Finish Warrior's Turn");
        end.addActionListener(e -> {
            battle.endWarriorTurn();
            refreshBattle();
        });
        battleOptionButtons.add(end);
    }

    // MODIFIES: this
    // EFFECTS: if the active warrior isn't in player1's army, sets the active warrior
    //          to this unit and updates the map based on that warrior's range and movement
    //          if they aren't null.
    //          otherwise, attempts to move the active warrior to pos if pos has no unit on it,
    //
    private void playerAction(Position pos) {
        battleError.setText("");
        int action = battle.selectPosition(pos);
        if (action == Battle.MOVE) {
            confirmMove.openFrame();
        } else if (action == Battle.BATTLE) {
            confirmAttack.openFrame(battleButtonMap.get(battle.getActivePosition()));
        }
        refreshBattle();
    }

    // MODIFIES: this
    // EFFECTS: refreshes the screen with new stage highlight, the stats panel, and options buttons
    private void refreshBattle() {
        highlightStage();
        refreshStatsPanel();
        refreshOptionButtons();
        checkTurnOver();
        GUI.getInstance().refreshPanel();
    }

    // MODIFIES: this
    // EFFECTS: checks if player1's turn is over, and if it is, begins the computer's turn. Ends the game
    //          if the game is over
    private void checkTurnOver() {
        checkIfOver();
        if (battle.isTurnOver()) {
            battle.incrementTurn();
            computerTurn();
        }
    }

    // MODIFIES: this
    // EFFECTS: if there is no active warrior, removes backgrounds of all buttons
    //          if the active warrior is in player1's army, highlights all cells within
    //          the active warrior's movement and within range + movement with shades of green
    //          else, highlights all cells within the active warrior's movement and within
    //          range + movement with shades of red
    private void highlightStage() {
        Map<Position, Color> positionColorMap = battle.positionColors();
        for (Position pos : battle) {
            battleButtonMap.get(pos).setBackground(positionColorMap.get(pos));
        }
    }

    // MODIFIES: this
    // EFFECTS: switches out the stats panel based on the active warrior selected
    private void refreshStatsPanel() {
        if (battle.getActiveWarrior() == null) {
            battleArea.removeAll();
        } else {
            setBattleArea(true);
            battleArea.add(makeBattleStats(), UiFormatter.makeGBC(0,0,1,1,1,1));
        }
    }

    // MODIFIES: this
    // EFFECTS: replaces the buttons in the options area depending on whether the active warrior
    //          is part of player 1's army and can attack
    private void refreshOptionButtons() {
        battleOptionsArea.remove(end);
        battleOptionsArea.remove(move);
        if (battle.isWarriorActionable()) {
            battleOptionsArea.add(end, UiFormatter.makeGBC(0,0,1,1,1,1));
        } else {
            battleOptionsArea.add(move, UiFormatter.makeGBC(0,0,1,1,1,1));
        }
    }

    // MODIFIES: this
    // EFFECTS: Moves the activeWarrior to pos if it is a valid move and updates the JFrame
    //          accordingly
    private void tryMoveUnit() {
        Position oldPosition = battle.getActiveWarrior().getPosition();
        try {
            battle.moveWarrior();
            refreshMove(oldPosition, battle.getActivePosition());
        } catch (ImmobileException e1) {
            battleError.setText(battle.getActiveWarrior() + " is immobile");
            battle.deselectPosition();
        } catch (TooFarException e1) {
            battleError.setText(battle.getActiveWarrior() + " cannot move this far");
            battle.deselectPosition();
        } catch (CheckedGameException e1) {
            throw new RuntimeException("ERROR: " + e1.getMessage());
        }
        refreshBattle();
    }

    // REQUIRES: newPosition.getUnit() != null
    // MODIFIES: this
    // EFFECTS: sets the old position button to have no text and a black border, and sets the
    //          new position button to have the warriors name and c as its border colour
    private void refreshMove(Position oldPosition, Position newPosition) {
        battleButtonMap.get(oldPosition).setText("");
        battleButtonMap.get(oldPosition).setIcon(null);
        battleButtonMap.get(oldPosition).setBorder(BorderFactory.createLineBorder(Color.BLACK));
        battleButtonMap.get(newPosition).setText(newPosition.getUnit().getName());
        battleButtonMap.get(newPosition).setIcon(GUI.getInstance().getWarriorIcon(newPosition.getUnit()));
        battleButtonMap.get(newPosition).setDisabledIcon(GUI.getInstance().getWarriorIcon(newPosition.getUnit()));
        if (newPosition.getUnit().getFaction().equals(playerArmy)) {
            battleButtonMap.get(newPosition).setBorder(BorderFactory.createLineBorder(UiFormatter.PLAYER1_UNIT));
        } else {
            battleButtonMap.get(newPosition).setBorder(BorderFactory.createLineBorder(UiFormatter.PLAYER2_UNIT));
        }
    }

    private void tryBattleUnit() {
        try {
            battle.battleWarrior();
            removeDeadWarriors(battle.getActivePosition(), battle.getActivePosition2());
        } catch (ImmobileException e) {
            battleError.setText(battle.getActiveWarrior() + " is unable to attack this turn.");
        } catch (TooFarException e) {
            battleError.setText(battle.getActiveWarrior() + " cannot attack "
                    + battle.getActivePosition().getUnit() + " because they are too far");
        } finally {
            battle.deselectPosition();
            refreshBattle();
        }
    }

    // MODIFIES: this
    // EFFECTS: removes all dead warrior's names from the position buttons
    private void removeDeadWarriors(Position pos1, Position pos2) {
        if (pos1.getUnit() == null) {
            removeDeadWarrior(pos1);
        }
        if (pos2.getUnit() == null) {
            removeDeadWarrior(pos2);
        }
    }

    // REQUIRES: pos.getUnit == null
    // MODIFIES: this
    // EFFECTS: removes the dead warrior from the button and adds a blood icon
    private void removeDeadWarrior(Position pos) {
        JButton button = battleButtonMap.get(pos);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setText("");
        button.setIcon(UiFormatter.bloodImage);
        button.setDisabledIcon(button.getIcon());
    }

    // EFFECTS: creates a JPanel that displays the warrior's stats
    private JPanel makeBattleStats() {
        Warrior activeWarrior = battle.getActiveWarrior();
        JPanel battleStats = new DefaultPanel();
        battleStats.setLayout(new GridBagLayout());
        battleStats.add(new JLabel(activeWarrior.getName()),
                UiFormatter.makeGBC(0,0,7,1,7,1));
        battleStats.add(new JLabel("HP: " + activeWarrior.getHP()),
                UiFormatter.makeGBC(0,1,1,1,1,1));
        battleStats.add(new JLabel("Strength: " + activeWarrior.getRealStrength()),
                UiFormatter.makeGBC(1,1,1,1,1,1));
        battleStats.add(new JLabel("Speed: " + activeWarrior.getRealSpeed(true)),
                UiFormatter.makeGBC(2,1,1,1,1,1));
        battleStats.add(new JLabel("Defense: " + activeWarrior.getRealDefense()),
                UiFormatter.makeGBC(3,1,1,1,1,1));
        battleStats.add(new JLabel("Luck: " + activeWarrior.getRealLuck()),
                UiFormatter.makeGBC(4,1,1,1,1,1));
        battleStats.add(new JLabel("Movement: " + activeWarrior.getRealMovement()),
                UiFormatter.makeGBC(5,1,1,1,1,1));
        battleStats.add(new JLabel("Range: " + activeWarrior.getRealRange()),
                UiFormatter.makeGBC(6,1,1,1,1,1));
        battleStats.setBackground(Color.WHITE);
        return battleStats;
    }

    // MODIFIES: this
    // EFFECTS: goes through player2's turn and refreshes the screen according to timer
    //          after each warrior acts
    private void computerTurn() {
        setBattleButtons(false);
        timer = new Timer(1000, computerInstructions());
        timer.start();
    }

    // MODIFIES: this
    // EFFECTS: if both player1 and player2 have at least one alive warrior in their army's
    //          then with each alive warrior in player2's army, this warrior attacks any of
    //          player1's warriors within its range. If there is no warriors within range,
    //          then the warrior moves to within its range of one of player1's warriors and
    //          attacks any of player1's warriors within range. If there are no warriors in its
    //          movement range, then the warrior does nothing.
    private ActionListener computerInstructions() {
        return e -> {
            if (battle.checkIfOver()) {
                finishComputer();
            } else {
                int result = battle.computerizedAction();
                if (result == Battle.FINISH) {
                    finishComputer();
                } else if (result == Battle.MOVE) {
                    refreshMove(battle.getActivePosition(), battle.getActiveWarrior().getPosition());
                } else {
                    removeDeadWarriors(battle.getActivePosition(), battle.getActivePosition2());
                }
                highlightStage();
            }
        };
    }

    // MODIFIES: this
    // EFFECTS: finishes the computer phase. If there is only one army remaining, ends the battle
    //          otherwise, increments the turn and allows the user to move again
    private void finishComputer() {
        timer.stop();
        if (!battle.endArmyTurn()) {
            GUI.getInstance().endBattle();
        } else {
            setBattleButtons(true);
            battle.incrementTurn();
        }
    }

    // MODIFIES: this
    // EFFECTS: produces a win screen if player1 has defeated all their opponents and a losing
    //          screen if all of player1's army has been defeated. Otherwise, reactivates buttons
    private void checkIfOver() {
        if (battle.checkIfOver()) {
            GUI.getInstance().endBattle();
        }
    }

    // MODIFIES: this
    // EFFECTS: if b is true, enables all buttons in the battle stage, else disables them
    private void setBattleButtons(boolean b) {
        for (Position pos : battleButtonMap.keySet()) {
            battleButtonMap.get(pos).setEnabled(b);
        }
        for (JButton button : battleOptionButtons) {
            button.setEnabled(b);
        }
    }

    private class ConfirmMoveFrame extends ConfirmationFrame {

        public ConfirmMoveFrame() {
            super("Would you like to move here?");
        }

        @Override
        protected void yesAction() {
            tryMoveUnit();
        }

        @Override
        protected void noAction() {
            battleError.setText("");
        }

        // MODIFIES: this
        // EFFECTS: opens this frame and changes the text to include the active warrior's name
        public void openFrame() {
            super.setTitleMessage("Would you like to move " + battle.getActiveWarrior() + " here?");
            super.openFrame(battleButtonMap.get(battle.getActivePosition()));
        }
    }

    private class ConfirmAttackFrame extends ConfirmationFrame {

        public ConfirmAttackFrame() {
            super("Would you like to attack this warrior?");
        }

        @Override
        protected void yesAction() {
            tryBattleUnit();
        }

        @Override
        protected void noAction() {
            battleError.setText("");
        }
    }
}