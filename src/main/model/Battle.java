package model;

import model.exceptions.*;
import org.json.JSONArray;
import org.json.JSONObject;
import persistance.JsonReader;
import persistance.Savable;
import ui.UiFormatter;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Battle implements Iterable<Position>, Savable {

    public static final int DESELECT = 0;
    public static final int SELECT = 1;
    public static final int MOVE = 2;
    public static final int BATTLE = 3;
    public static final int FINISH = 4;

    private final Stage stage;
    private final List<Army> competitors;
    private Warrior activeWarrior;
    private Position activePosition;
    private Position activePosition2;
    private int turn;
    private int activeArmyIndex;
    private int computerIndex;
    private List<Warrior> actionableWarriors;

    // EFFECTS: begins a battle on stage between the competitors
    //          throws invalid battle exception if the armies have not yet been placed on the stage
    public Battle(Stage stage, List<Army> competitors) {
        int count = 0;
        for (Army army : competitors) {
            if (!army.validPositions(stage)) {
                throw new InvalidBattleException();
            }
            count += army.isAlive() ? 1 : 0;
        }
        if (count <= 1) {
            throw new InvalidBattleException();
        }
        this.stage = stage;
        this.competitors = competitors;
        turn = 1;
        activeArmyIndex = 0;
        computerIndex = -1;
    }

    // REQUIRES: pos must be on the stage
    // MODIFIES: this
    // EFFECTS: sets pos to be the active position.
    //          If the warrior on this position is the same as the active warrior, returns DESELECT
    //          If there is no active warrior or the active warrior is not part of the active faction,
    //              sets the active warrior to be the positions warrior and returns SELECT
    //          If the active warrior is part of the active faction and the position has no warrior on it, returns MOVE
    //          Else, returns BATTLE
    public int selectPosition(Position pos) {
        activePosition = pos;
        if (activeWarrior == null || !activeWarrior.getFaction().equals(getActiveFaction().getFactionName())) {
            if (activeWarrior == pos.getUnit()) {
                activeWarrior = null;
                return DESELECT;
            } else {
                activeWarrior = pos.getUnit();
                return SELECT;
            }
        } else {
            if (pos.getUnit() == null) {
                return MOVE;
            } else if (pos.getUnit().equals(activeWarrior)) {
                activeWarrior = null;
                return DESELECT;
            } else if (pos.getUnit().getFaction().equals(getActiveFaction().getFactionName())) {
                activeWarrior = pos.getUnit();
                return SELECT;
            } else {
                return BATTLE;
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: sets the active warrior to warrior and the active position to the warriors position
    public void selectPosition(Warrior w) {
        activeWarrior = w;
        activePosition = w.getPosition();
    }

    // MODIFIES: this
    // EFFECTS: deselects the active warrior, position, and additional position
    public void deselectPosition() {
        activePosition = null;
        activePosition2 = null;
        activeWarrior = null;
    }

    // REQUIRES: selectPosition should have returned MOVE when last called
    // MODIFIES: this
    // EFFECTS: moves the active warrior to the active position on the stage, and throws a checked game exception
    //          if this is not possible
    public void moveWarrior() throws CheckedGameException {
        activeWarrior.move(stage, activePosition.getPosX() - activeWarrior.getPosition().getPosX(),
                activePosition.getPosY() - activeWarrior.getPosition().getPosY());
    }

    // REQUIRES: selectPosition should have returned BATTLE when last called
    // MODIFIES: this
    // EFFECTS: the active warrior attempts to battle the warrior in active position. Throws ImmobileException
    //          if this warrior is unable to attack, and TooFarException if the opponent is too far away
    //          deselects the active warrior if the battle is successful
    public void battleWarrior() throws ImmobileException, TooFarException {
        if (activeWarrior.checkValidOpponents(stage).contains(activePosition.getUnit())
                && activeWarrior.getCanAttack()) {
            activePosition2 = activeWarrior.getPosition();
            activeWarrior.battle(activePosition.getUnit());
            activeWarrior = null;
        } else if (!activeWarrior.getCanAttack()) {
            throw new ImmobileException();
        } else {
            throw new TooFarException();
        }
    }

    // EFFECTS: returns a colour map associated each position to a colour
    // note: since this function is mainly used for UI, it does not have associated tests
    public Map<Position, Color> positionColors() {
        if (activeWarrior == null) {
            Map<Position, Color> positions = new HashMap<>();
            for (Position pos : stage) {
                positions.put(pos, UiFormatter.BLANK);
            }
            return positions;
        } else {
            int closeThreshHold;
            int farThreshHold;
            if (!activeWarrior.getFaction().equals(getActiveFaction().getFactionName()) || activeWarrior.getCanMove()) {
                closeThreshHold = activeWarrior.getRealMovement();
                farThreshHold = activeWarrior.getRealMovement() + activeWarrior.getRealRange();
            } else {
                closeThreshHold = activeWarrior.getCanAttack() ? activeWarrior.getRealRange() : 0;
                farThreshHold = closeThreshHold;
            }
            return getPositionColors(closeThreshHold, farThreshHold);
        }
    }

    // EFFECTS: returns a colour map of positions based on the active warrior's affiliation and proximity to
    //          nearby positions
    private Map<Position, Color> getPositionColors(int close, int far) {
        Map<Position, Color> positionColorMap = new HashMap<>();
        Color exactColor = activeWarrior.getFaction().equals(getActiveFaction().getFactionName())
                ? UiFormatter.PLAYER1_UNIT : UiFormatter.PLAYER2_UNIT;
        Color closeColor = activeWarrior.getFaction().equals(getActiveFaction().getFactionName())
                ? UiFormatter.CLOSE_GOOD : UiFormatter.CLOSE_EVIL;
        Color farColor = activeWarrior.getFaction().equals(getActiveFaction().getFactionName())
                ? UiFormatter.FAR_GOOD : UiFormatter.FAR_EVIL;
        for (Position pos : stage) {
            int dist = activeWarrior.getPosition().getDistance(pos);
            if (dist == 0) {
                positionColorMap.put(pos, exactColor);
            } else if (dist <= close) {
                positionColorMap.put(pos, closeColor);
            } else if (dist <= far) {
                positionColorMap.put(pos, farColor);
            } else {
                positionColorMap.put(pos, UiFormatter.BLANK);
            }
        }
        return positionColorMap;
    }

    // MODIFIES: this
    // EFFECTS: ends the active warriors turn then deselects the active warrior
    public void endWarriorTurn() {
        if (activeWarrior != null) {
            activeWarrior.setCanMove(false);
            activeWarrior.setCanAttack(false);
            activeWarrior = null;
        }
    }

    // MODIFIES: this
    // EFFECTS: if the game is not over, ends the active army's turn
    public boolean endArmyTurn() {
        if (!checkIfOver()) {
            getActiveFaction().endTurn();
            return true;
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: deselects the active warrior and positions and increments the active army index to the next alive army
    //          if the battle has cycled through all the competitors, increments the turn by 1 and continues searching
    //          for the next alive army
    //          if the game is over and this method is called, throws an unchecked game exception
    public void incrementTurn() {
        deselectPosition();
        computerIndex = -1;
        int oldIndex = activeArmyIndex;
        activeArmyIndex++;
        while (oldIndex != activeArmyIndex) {
            if (activeArmyIndex == competitors.size()) {
                activeArmyIndex = 0;
                turn++;
            } else if (!competitors.get(activeArmyIndex).isAlive()) {
                activeArmyIndex++;
            } else {
                competitors.get(activeArmyIndex).beginTurn();
                return;
            }
        }
        throw new UncheckedGameException("Turn incremented when the battle is over.");
    }

    // MODIFIES: this
    // EFFECTS: performs a possible action for the active faction
    //          if this action is moving a warrior, returns MOVE
    //          if this action is engaging in a battle, returns BATTLE
    //          if no actions can be performed, returns FINISH
    public int computerizedAction() {
        initComputer();
        while (true) {
            if (computerIndex == actionableWarriors.size()) {
                return FINISH;
            } else {
                activeWarrior = actionableWarriors.get(computerIndex);
                activePosition = activeWarrior.getPosition();
                List<Warrior> opponents = activeWarrior.checkValidOpponents(stage);
                if (!opponents.isEmpty() && activeWarrior.getCanAttack()) {
                    activePosition2 = opponents.get(0).getPosition();
                    activeWarrior.battle(activePosition2.getUnit());
                    activeWarrior = null;
                    computerIndex++;
                    return BATTLE;
                } else if (activeWarrior.findMove(stage)) {
                    return MOVE;
                } else {
                    computerIndex++;
                }
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes the computer index if it has not been initialized already, and sets the actionable
    //          warriors to be all warriors in the active faction that are able to attack
    private void initComputer() {
        if (computerIndex == -1) {
            computerIndex = 0;
            actionableWarriors = getActiveFaction().canAttack();
        }
    }

    // EFFECTS: returns the army that is currently active
    public Army getActiveFaction() {
        return competitors.get(activeArmyIndex);
    }

    // EFFECTS: returns true if the active warrior is a member of the active army and can attack
    public boolean isWarriorActionable() {
        return (activeWarrior != null) && (activeWarrior.getFaction().equals(getActiveWarrior().getFaction()))
                && activeWarrior.getCanAttack();
    }

    // EFFECTS: returns true if there is only one competitor whos army is alive, else false
    public boolean checkIfOver() {
        int count = 0;
        for (Army army : competitors) {
            if (army.isAlive()) {
                count++;
            }
        }
        return count == 1;
    }

    // EFFECTS: returns true if not of the warriors in the active faction can attack
    public boolean isTurnOver() {
        return competitors.get(activeArmyIndex).canAttack().isEmpty();
    }

    public int getStageHeight() {
        return stage.getFarthestBottom() + 1;
    }

    public int getStageWidth() {
        return stage.getFarthestRight() + 1;
    }

    public Warrior getActiveWarrior() {
        return activeWarrior;
    }

    public Position getActivePosition() {
        return activePosition;
    }

    public Position getActivePosition2() {
        return activePosition2;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setActiveArmyIndex(int subTurn) {
        activeArmyIndex = subTurn;
    }

    public int getTurn() {
        return turn;
    }

    public List<Army> getCompetitors() {
        return competitors;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(JsonReader.STAGE, stage.toJson());
        JSONArray jsonArray = new JSONArray();
        for (Army competitor : competitors) {
            jsonArray.put(competitor.toJson());
        }
        json.put(JsonReader.COMPETITORS, jsonArray);
        json.put(JsonReader.TURN, turn);
        json.put(JsonReader.SUB_TURN, activeArmyIndex);
        return json;
    }

    @Override
    public Iterator<Position> iterator() {
        return stage.iterator();
    }
}
