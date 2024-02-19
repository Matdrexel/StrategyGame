package model;

import model.exceptions.DuplicateNameException;
import model.exceptions.InvalidPositionException;
import model.exceptions.NoPositionAvailableException;
import model.exceptions.CheckedGameException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistance.JsonReader;
import persistance.Savable;

import java.util.*;

import static java.lang.Math.max;

// Represents an army of warriors and a name for the army
public class Army implements Savable, Iterable<Warrior> {

    private static Set<String> ARMY_NAMES = new HashSet<>();
    private static Set<String> OLD_ARMY_NAMES = new HashSet<>();

    private final String factionName;
    private final ArrayList<Warrior> warriors;

    // EFFECTS: Creates an army with the given name and no warriors
    //          throws DuplicateNameException if the name already belongs to another army
    public Army(String name) throws DuplicateNameException {
        if (ARMY_NAMES.contains(name)) {
            throw new DuplicateNameException();
        }
        this.factionName = name;
        ARMY_NAMES.add(name);
        warriors = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: stores the old set of names into the old army names and sets the collection
    //          of used names to an empty set
    public static void resetNames() {
        OLD_ARMY_NAMES = ARMY_NAMES;
        ARMY_NAMES = new HashSet<>();
    }

    // MODIFIES: this
    // EFFECTS: sets the collection of unavailable names back to its previous version
    public static void revertResetNames() {
        ARMY_NAMES = OLD_ARMY_NAMES;
    }

    // REQUIRES: no 2 warriors can have the same name in the army,
    //           warrior is not in another army,
    //           warrior is not null
    // MODIFIES: this, warrior, EventLog
    // EFFECTS: adds the warrior to the army and changes the warriors faction to this army and
    //          adds a GameEvent to the EventLog
    public void addWarrior(Warrior warrior) {
        warrior.setFaction(factionName);
        warriors.add(warrior);
        EventLog.getInstance().logEvent(new GameEvent(warrior.getName() + " was added to " + factionName));
    }

    // EFFECTS: returns the warrior with the same name, or null if warrior doesn't exist
    public Warrior selectWarrior(String name) {
        for (Warrior warrior : warriors) {
            if (warrior.getName().equals(name) && warrior.getIsAlive()) {
                return warrior;
            }
        }
        return null;
    }

    // EFFECTS: returns true if all warriors in the army are on the stage
    public boolean validPositions(Stage stage) {
        if (warriors.isEmpty()) {
            return false;
        }
        for (Warrior warrior : this) {
            Position warriorPosition = warrior.getPosition();
            if (!warrior.getIsAlive()) {
                continue;
            } else if (warriorPosition == null) {
                return false;
            }
            try {
                Position stagePosition = stage.getPosition(warriorPosition.getPosX(), warriorPosition.getPosY());
                if (stagePosition.getUnit() != warrior) {
                    return false;
                }
            } catch (InvalidPositionException e) {
                return false;
            }
        }
        return true;
    }

    // EFFECTS: returns true if there are alive warriors in the army, else returns false
    public boolean isAlive() {
        for (Warrior warrior : warriors) {
            if (warrior.getIsAlive()) {
                return true;
            }
        }
        return false;
    }

    // EFFECTS: returns list of all warriors in the army that can move
    public List<Warrior> canMove() {
        ArrayList<Warrior> move = new ArrayList<>();
        for (Warrior warrior : warriors) {
            if (warrior.getCanMove()) {
                move.add(warrior);
            }
        }
        return move;
    }

    // EFFECTS: returns list of all warriors in the army that can attack
    public List<Warrior> canAttack() {
        ArrayList<Warrior> attack = new ArrayList<>();
        for (Warrior warrior : warriors) {
            if (warrior.getCanAttack()) {
                attack.add(warrior);
            }
        }
        return attack;
    }

    // MODIFIES: warriors in army, EventLog
    // EFFECTS: sets all warriors in army to have canMove = false and canAttack = false
    //          and adds this to the EventLog
    public void endTurn() {
        for (Warrior warrior : warriors) {
            warrior.setCanMove(false);
            warrior.setCanAttack(false);
        }
        EventLog.getInstance().logEvent(new GameEvent(factionName + " ended their turn."));
    }

    // MODIFIES: warriors in army, EventLog
    // EFFECTS: sets all alive warriors to have canMove = true and canAttack = true
    //          and adds this to the EventLog
    public void beginTurn() {
        for (Warrior warrior : warriors) {
            if (warrior.getIsAlive()) {
                warrior.setCanAttack(true);
                warrior.setCanMove(true);
            }
        }
        String plural = factionName.endsWith("s") ? "'" : "'s";
        EventLog.getInstance().logEvent(new GameEvent("It is " + factionName + plural + " turn"));
    }

    // MODIFIES: warriors in army, EventLog
    // EFFECTS: sets all warriors to have isAlive = false and add a GameEvent to the EventLog
    public void forfeit() {
        for (Warrior warrior : warriors) {
            warrior.setIsAlive(false);
        }
        EventLog.getInstance().logEvent(new GameEvent(factionName + " has forfeit the game"));
    }

    // REQUIRES: warriors.size() == 0
    // MODIFIES: this
    // EFFECTS: generates this army to have the same number of warriors as opponent, prioritizing
    //          taking warriors from available, then creating custom warriors if there are
    //          not enough available warriors
    public void generateArmy(Army opponent, Collection<Warrior> available) {
        int armySize = opponent.warriors.size();
        for (Warrior recruit : available) {
            if (warriors.size() == armySize) {
                break;
            } else {
                addWarrior(recruit);
            }
        }
        if (armySize != warriors.size()) {
            makeCustomWarriors(opponent, armySize);
        }
    }

    // MODIFIES: this
    // EFFECTS: creates custom enemy warriors with similar stats to opponent's warriors
    //          until both armies have the same number of warriors
    private void makeCustomWarriors(Army opponent, int armySize) {
        int i = 0;
        int enemyNum = 1;
        while (warriors.size() < armySize) {
            String name = "Enemy " + enemyNum;
            enemyNum++;
            try {
                Warrior comparison = opponent.warriors.get(i);
                Warrior newEnemy = new Warrior(name, comparison.getHP() + 2,
                        comparison.getStrength() + 1, max(comparison.getSpeed() - 1,0),
                        max(comparison.getDefense() - 1,0), max(comparison.getLuck() - 1, 0),
                        max(comparison.getMovement() - 1, 1), max(comparison.getRange() - 1, 1),
                        comparison.getImageSource());
                addWarrior(newEnemy);
                i++;
            } catch (DuplicateNameException e) {
                // continue making warriors
            }
        }
    }

    // MODIFIES: this, stage
    // EFFECTS: places all warriors onto the stage if they haven't already been placed.
    //          if there are not enough positions on the stage, then throws NoPositionAvailableException
    public void placeArmy(Stage stage) throws NoPositionAvailableException {
        int gap = 3;
        int enemyNum = 0;
        for (int offset = 0; offset < gap; offset++) {
            for (int y = 0; y <= stage.getFarthestBottom(); y++) {
                for (int x = ((offset + y) % gap); x <= stage.getFarthestRight(); x = x + gap) {
                    enemyNum = autoPlaceNextWarrior(enemyNum, stage, x, y);
                    if (enemyNum == warriors.size()) {
                        return;
                    }
                }
            }
        }
        throw new NoPositionAvailableException("Not all of the warriors in "
                    + factionName + " can be placed on the stage.");
    }

    // MODIFIES: this, stage
    // EFFECTS: increments enemyNum to the index of the first unplaced warrior, then tries
    //          to place that warrior at x,y of the stage. If this is successful, returns enemyNum + 1,
    //          else just returns enemyNum. If there are no more unplaced warriors, then returns the size
    //          of warriors
    private int autoPlaceNextWarrior(int enemyNum, Stage stage, int x, int y) {
        while (warriors.get(enemyNum).getPosition() != null) {
            enemyNum++;
            if (enemyNum == warriors.size()) {
                return enemyNum;
            }
        }
        try {
            warriors.get(enemyNum).placeWarrior(stage, x, y);
            enemyNum++;
            return enemyNum;
        } catch (CheckedGameException e) {
            return enemyNum;
        }
    }

    // EFFECTS: returns the JSON representation of this army object
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(JsonReader.FACTION_NAME, factionName);
        json.put(JsonReader.WARRIORS, armyToJson());
        return json;
    }

    // EFFECTS: returns warriors in army as a JSON Array
    private JSONArray armyToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Warrior warrior : warriors) {
            jsonArray.put(warrior.toJson());
        }

        return jsonArray;
    }

    public String getFactionName() {
        return factionName;
    }

    public ArrayList<Warrior> getWarriors() {
        return warriors;
    }

    @Override
    public Iterator<Warrior> iterator() {
        return warriors.iterator();
    }
}
