package model;

import model.exceptions.*;
import org.json.JSONObject;
import persistance.JsonReader;
import persistance.Savable;
import ui.ImagePath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static java.lang.Math.abs;

// Represents a warrior unit in a player's army with movement and combat stats, the faction
// they belong to, their position on the stage, and whether they are alive or not
public class Warrior implements Savable {

    public static final int SPEED_BONUS = 5;
    private static final int LUCK_BUFFER = 30;

    private static Set<String> WARRIOR_NAMES = new HashSet<>();
    private static Set<String> OLD_WARRIOR_NAMES = new HashSet<>();

    private final String name;
    private int hp;
    private final int strength;
    private final int speed;
    private final int defense;
    private final int luck;
    private final int movement;
    private final int range;
    private boolean isAlive;
    private boolean canMove;
    private boolean canAttack;
    private Position position;
    private String faction;
    private final ImagePath imageSource;

    // REQUIRES: hp > 0, strength > 0, speed >= 0, defense >= 0,
    //           movement > 0, and range > 0
    // EFFECTS: initializes the warrior with the name and their stats
    //          if name belongs to another warrior, throws DuplicateNameException
    public Warrior(String name, int hp, int strength, int speed, int defense, int luck,
                   int movement, int range, ImagePath imageSource) throws DuplicateNameException {
        if (WARRIOR_NAMES.contains(name)) {
            throw new DuplicateNameException();
        }
        this.name = name;
        WARRIOR_NAMES.add(name);
        this.hp = hp;
        this.strength = strength;
        this.speed = speed;
        this.defense = defense;
        this.luck = luck;
        this.movement = movement;
        this.range = range;
        this.imageSource = imageSource;
        isAlive = true;
        canMove = false;
        canAttack = false;
        position = null;
        faction = "None";
    }

    // MODIFIES: this
    // EFFECTS: stores the old set of names into old_names and sets the collection of used names
    //          to an empty set
    public static void resetNames() {
        OLD_WARRIOR_NAMES = WARRIOR_NAMES;
        WARRIOR_NAMES = new HashSet<>();
    }

    // MODIFIES: this
    // EFFECTS: sets the collection of unavailable names back to its previous version
    public static void revertResetNames() {
        WARRIOR_NAMES = OLD_WARRIOR_NAMES;
    }

    // EFFECTS: returns a list of all the opponents warriors within range in any order
    //          if this warrior does not have a position, throws a NoPositionException
    public ArrayList<Warrior> checkValidOpponents(Stage stage) {
        checkHasPosition();
        ArrayList<Warrior> opponents = new ArrayList<>();
        for (int x = getRealRange(); x >= -getRealRange(); x--) {
            for (int y = getRealRange(); y >= -getRealRange(); y--) {
                int newPosX = x + position.getPosX();
                int newPosY = y + position.getPosY();
                if ((abs(x) + abs(y)) <= getRealRange()) {
                    try {
                        Warrior unit = stage.getPosition(newPosX, newPosY).getUnit();
                        if ((unit != null) && (!unit.getFaction().equals(faction))) {
                            opponents.add(unit);
                        }
                    } catch (InvalidPositionException e) {
                        // keep looking for valid opponents
                    }
                }
            }
        }
        return opponents;
    }

    // MODIFIES: this, opponent, EventLog
    // EFFECTS: starts a battle with the opponent. If warrior's speed + SPEED_BONUS < opponent's speed,
    //          opponent attacks first. Otherwise, warrior attacks first.
    //          Warrior that didn't attack first then attack's second if they are still alive
    //          Opponent can only fight back if their position is within their range
    //          Sets canMove to false and canAttack to false after the battle.
    //          Creates GameEvents that document the events of the battle and adds them to
    //          the EventLog.
    //          throws NoPositionException if this warrior or opponent don't have a position
    public void battle(Warrior opponent) {
        checkHasPosition();
        opponent.checkHasPosition();
        EventLog.getInstance().logEvent(new GameEvent(this + " is battling " + opponent));
        if (getRealSpeed(true) >= opponent.getRealSpeed(false)) {
            EventLog.getInstance().logEvent(new GameEvent(this + " is attacking " + opponent + " first!"));
            attack(opponent);
            opponentCounterAttack(opponent);
        } else {
            if (position.getDistance(opponent.getPosition()) <= opponent.getRealRange()) {
                EventLog.getInstance().logEvent(new GameEvent(opponent + " is faster than "
                        + this + " and is attacking first!"));
                opponent.attack(this);
            }
            if (isAlive) {
                EventLog.getInstance().logEvent(new GameEvent(this + " is counterattacking " + opponent));
                attack(opponent);
            }
        }
        canMove = false;
        canAttack = false;
    }

    // MODIFIES: this, opponent, EventLog
    // EFFECTS: begins the counterattack stage for the opponent and updates the EventLog
    //          with the outcomes of the counterattack
    private void opponentCounterAttack(Warrior opponent) {
        if (opponent.getIsAlive()) {
            if (position.getDistance(opponent.getPosition()) <= opponent.getRealRange()) {
                EventLog.getInstance().logEvent(new GameEvent(opponent + " is counterattacking " + this));
                opponent.attack(this);
            } else {
                EventLog.getInstance().logEvent(new GameEvent(opponent
                        + " is too far away for them to counterattack"));
            }
        }
    }

    // REQUIRES: opponent must be alive
    // MODIFIES: opponent, EventLog
    // EFFECTS: if strength - opponent's defense > 0, then returns true and opponent loses
    //                  the corresponding hp and this amount is logged in the EventLog
    //          else, returns false and updates the EventLog with a GameEvent stating the attack
    //                  was ineffective
    public boolean attack(Warrior opponent) {
        int dmg = getRealStrength()  + getCriticalHit() - opponent.getRealDefense();
        if (dmg > 0) {
            EventLog.getInstance().logEvent(new GameEvent(this + " dealt "
                    + dmg + " damage to " + opponent));
            opponent.reduceHP(dmg);
            return true;
        } else {
            String plural = name + (name.endsWith("s") ? "'" : "'s");
            EventLog.getInstance().logEvent(new GameEvent(plural + " attack was ineffective!"));
            return false;
        }
    }

    // EFFECTS: if a randomly generated integer between 0 and luck + luck buffer is less than the warrior's
    //          luck, then returns the inverse of a randomly generated double between 0 and 1 rounded
    //          down to the nearest integer.
    //          else returns 0
    private int getCriticalHit() {
        Random rand = new Random();
        if (rand.nextInt(getRealLuck() + LUCK_BUFFER) < getRealLuck()) {
            return (int) (1.0 / (1.0 - rand.nextDouble()));
        }
        return 0;
    }

    // REQUIRES: dmg > 0
    // MODIFIES: this, GameEvent
    // EFFECTS: hp decreases by dmg. If hp <= 0, then the warrior dies and the EventLog is updated
    //          with the warrior's death
    public void reduceHP(int dmg) {
        hp -= dmg;
        if (hp <= 0) {
            hp = 0;
            isAlive = false;
            canMove = false;
            canAttack = false;
            removePosition();
            EventLog.getInstance().logEvent(new GameEvent(this + " has perished!"));
        }
    }

    // MODIFIES: this, stage, EventLog
    // EFFECTS: tries to move this warrior to a new position on the map x units right and
    //          y units down, negative x and y go the reverse direction.
    //          if this warrior has no position, throws NoPositionException
    //          if canMove is false, throws an ImmobileException
    //          if x and y combined is larger than movement, throws a TooFarException
    //          if the new position is not on the map, throws an InvalidPositionException
    //          if the new position already has a warrior on it, throws an OccupiedException
    //          else, updates the position of this warrior and  updates the original position
    //                  of this warrior on the map to have no unit and the new position of the
    //                  warrior to have this warrior as its unit. Updates the EventLog accordingly
    public void move(Stage stage, int x, int y) throws CheckedGameException {
        checkHasPosition();
        int newPosX = x + position.getPosX();
        int newPosY = y + position.getPosY();
        if (!canMove) {
            throw new ImmobileException();
        } else if ((abs(x) + abs(y)) > getRealMovement()) {
            throw new TooFarException();
        } else if (!stage.checkValidPosition(newPosX, newPosY)) {
            throw new InvalidPositionException();
        } else if (stage.getPosition(newPosX, newPosY).getUnit() != null) {
            throw new OccupiedException();
        } else {
            setPosition(stage.getPosition(newPosX, newPosY));
            canMove = false;
            EventLog.getInstance().logEvent(new GameEvent(this + " moved to " + position));
        }
    }

    // MODIFIES: this, stage, EventLog
    // EFFECTS: places warrior in (x,y) position on the map
    //          if the position isn't on the stage, throws an InvalidPositionException
    //          if the position is occupied by another warrior, throws an OccupiedException
    //          otherwise, updates the warriors position and updates the unit on the position
    //              and updates the EventLog accordingly
    public void placeWarrior(Stage stage, int x, int y) throws CheckedGameException {
        if (!stage.checkValidPosition(x,y)) {
            throw new InvalidPositionException();
        } else if (!stage.getPosition(x,y).placeUnit(this)) {
            throw new OccupiedException();
        } else {
            EventLog.getInstance().logEvent(new GameEvent(name + " was placed at " + position.toString()));
        }
    }

    // MODIFIES: this, p
    // EFFECTS: sets this position to p and the positions unit to this if position != p
    public void setPosition(Position p) {
        if (position != p) {
            if (position != null) {
                position.removeUnit();
            }
            position = p;
            p.setWarrior(this);
        }
    }

    // MODIFIES: this, position
    // EFFECTS: sets position to null and the old position to have no unit on it
    public void removePosition() {
        if (position != null) {
            Position oldPos = position;
            position = null;
            oldPos.removeUnit();
        }
    }

    // MODIFIES: this, stage
    // EFFECTS: moves warrior within its attack range of the first unit it is close enough to.
    //          if it finds someone, returns true and updates the map accordingly and this units
    //          position accordingly
    //          otherwise, returns false and unit doesn't move
    //          if this warrior doesn't have a position, throws a NoPositionException
    public boolean findMove(Stage stage) {
        checkHasPosition();
        if (!canMove) {
            return false;
        }
        for (int x = (getRealMovement() + getRealRange()); x >= -(getRealMovement() + getRealRange()); x--) {
            for (int y = (getRealMovement() + getRealRange()); y >= -(getRealMovement() + getRealRange()); y--) {
                int newPosX = x + position.getPosX();
                int newPosY = y + position.getPosY();
                if ((abs(x) + abs(y)) <= (getRealRange() + getRealMovement())) {
                    try {
                        Warrior enemy = stage.getPosition(newPosX, newPosY).getUnit();
                        if ((enemy != null) && (!enemy.getFaction().equals(faction))) {
                            if (checkIfMovable(stage, enemy)) {
                                return true;
                            }
                        }
                    } catch (InvalidPositionException e) {
                        // keep searching for a valid move
                    }
                }
            }
        }
        return false;
    }

    // MODIFIES: this, stage
    // EFFECTS: returns true if the unit can successfully move in a space within its range
    //          around the enemy and updates the map and warrior's position accordingly
    //          returns false if it cannot
    private boolean checkIfMovable(Stage stage, Warrior enemy) {
        checkHasPosition();
        for (int x2 = getRealRange(); x2 >= -getRealRange(); x2--) {
            for (int y2 = getRealRange(); y2 >= -getRealRange(); y2--) {
                int newPosX2 = x2 + enemy.getPosition().getPosX();
                int newPosY2 = y2 + enemy.getPosition().getPosY();
                if ((abs(x2) + abs(y2) <= getRealRange())) {
                    try {
                        move(stage, (newPosX2 - position.getPosX()), (newPosY2 - position.getPosY()));
                        return true;
                    } catch (CheckedGameException e) {
                        // keep searching
                    }
                }
            }
        }
        return false;
    }

    // EFFECTS: converts this warrior into a JSON Object and returns it
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(JsonReader.NAME, name);
        json.put(JsonReader.HP, hp);
        json.put(JsonReader.STRENGTH, strength);
        json.put(JsonReader.SPEED, speed);
        json.put(JsonReader.DEFENSE, defense);
        json.put(JsonReader.LUCK, luck);
        json.put(JsonReader.MOVEMENT, movement);
        json.put(JsonReader.RANGE, range);
        json.put(JsonReader.ALIVE, isAlive);
        json.put(JsonReader.MOVE, canMove);
        json.put(JsonReader.ATTACK, canAttack);
        json.put(JsonReader.IMAGE, imageSource.getImagePath());
        if (position == null) {
            json.put(JsonReader.X, -1);
            json.put(JsonReader.Y, -1);
        } else {
            json.put(JsonReader.X, position.getPosX());
            json.put(JsonReader.Y, position.getPosY());
        }
        return json;
    }

    // EFFECTS: throws a NoPositionException if this warrior does not have a position
    private void checkHasPosition() throws NoPositionException {
        if (position == null) {
            throw new NoPositionException();
        }
    }

    public String getName() {
        return name;
    }

    public int getHP() {
        return hp;
    }

    public int getStrength() {
        return strength;
    }

    // EFFECTS: if this unit has a position, returns the max of their strength plus their positions
    //          strength modifier and 0, else returns their strength
    public int getRealStrength() {
        if (position == null) {
            return strength;
        } else {
            return Math.max(strength + position.getStrengthModifier(), 0);
        }
    }

    public int getSpeed() {
        return speed;
    }

    // EFFECTS: if this warrior doesn't have a position, then returns speed
    //          else, returns the maximum of the speed plus the positions speed modifier plus the speed bonus if the
    //          warrior is attacking and 0
    public int getRealSpeed(boolean attacking) {
        if (position == null) {
            return speed;
        } else {
            return Math.max(speed + position.getSpeedModifier() + (attacking ? SPEED_BONUS : 0), 0);
        }
    }

    public int getDefense() {
        return defense;
    }

    // EFFECTS: if this unit has a position, returns the max of their defense plus their positions
    //          defense modifier and 0, else returns their defense
    public int getRealDefense() {
        if (position == null) {
            return defense;
        } else {
            return Math.max(defense + position.getDefenseModifier(), 0);
        }
    }

    public int getLuck() {
        return luck;
    }

    // EFFECTS: if this unit has a position, returns the max of their luck plus their positions
    //          luck modifier and 0, else returns their luck
    public int getRealLuck() {
        if (position == null) {
            return luck;
        } else {
            return Math.max(luck + position.getLuckModifier(), 0);
        }
    }

    public int getMovement() {
        return movement;
    }

    // EFFECTS: if this unit has a position, returns the max of their movement plus their positions
    //          movement modifier and 1, else returns their movement
    public int getRealMovement() {
        if (position == null) {
            return movement;
        } else {
            return Math.max(movement + position.getMovementModifier(), 1);
        }
    }

    public int getRange() {
        return range;
    }

    // EFFECTS: if this unit has a position, returns the max of their range plus their positions
    //          range modifier and 1, else returns their range
    public int getRealRange() {
        if (position == null) {
            return range;
        } else {
            return Math.max(range + position.getRangeModifier(), 1);
        }
    }

    public String getFaction() {
        return faction;
    }

    public boolean getIsAlive() {
        return isAlive;
    }

    public boolean getCanMove() {
        return canMove;
    }

    public boolean getCanAttack() {
        return canAttack;
    }

    public Position getPosition() {
        return position;
    }

    public ImagePath getImageSource() {
        return imageSource;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public void setCanMove(boolean b) {
        canMove = b;
    }

    public void setCanAttack(boolean b) {
        canAttack = b;
    }

    public void setIsAlive(boolean b) {
        isAlive = b;
    }

    @Override
    public String toString() {
        return getName();
    }

}
