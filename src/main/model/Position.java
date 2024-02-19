package model;

import org.json.JSONObject;
import persistance.JsonReader;
import persistance.Savable;

import static java.lang.Math.abs;

// Represents an x and y coordinate found on the stage with the unit on it, or null
// if there is nobody on this position
public class Position implements Savable {

    private final int posX;
    private final int posY;
    private Warrior unit;
    private int strengthModifier;
    private int speedModifier;
    private int defenseModifier;
    private int luckModifier;
    private int movementModifier;
    private int rangeModifier;

    // REQUIRES: posX >= 0, posY >=0
    // EFFECTS: initializes position in the x,y coordinates given and with no unit and
    //          all stat modifiers set to 0
    public Position(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        unit = null;
        strengthModifier = 0;
        speedModifier = 0;
        defenseModifier = 0;
        luckModifier = 0;
        movementModifier = 0;
        rangeModifier = 0;
    }

    // MODIFIES: this
    // EFFECTS: if there is no unit in this position, sets this position to have that warrior
    //          as its unit and returns true
    //          if there is a unit in this position, returns false
    public boolean placeUnit(Warrior warrior) {
        if (unit == null) {
            setWarrior(warrior);
            return true;
        } else {
            return false;
        }
    }

    // MODIFIES: this, warrior
    // EFFECTS: if warrior is not this position's unit, sets unit to be this warrior
    public void setWarrior(Warrior warrior) {
        if (warrior != unit) {
            if (unit != null) {
                unit.removePosition();
            }
            unit = warrior;
            warrior.setPosition(this);
        }
    }

    // MODIFIES: this, unit
    // EFFECTS: removes the unit on this position
    public void removeUnit() {
        if (unit != null) {
            Warrior oldWarrior = unit;
            unit = null;
            oldWarrior.removePosition();
        }
    }

    // REQUIRES: other cannot be null
    // EFFECTS: returns the number of positions horizontally + number of positions vertically
    //          from another position
    public int getDistance(Position other) {
        return (abs(posX - other.getPosX()) + abs(posY - other.getPosY()));
    }

    // MODIFIES: this
    // EFFECTS: sets all modifiers to their corresponding inputted values
    public void setModifiers(int strength, int speed, int defense, int luck, int movement, int range) {
        setStrengthModifier(strength);
        setSpeedModifier(speed);
        setDefenseModifier(defense);
        setLuckModifier(luck);
        setMovementModifier(movement);
        setRangeModifier(range);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(JsonReader.STRENGTH, strengthModifier);
        json.put(JsonReader.SPEED, speedModifier);
        json.put(JsonReader.DEFENSE, defenseModifier);
        json.put(JsonReader.LUCK, luckModifier);
        json.put(JsonReader.MOVEMENT, movementModifier);
        json.put(JsonReader.RANGE, rangeModifier);
        return json;
    }

    public void setStrengthModifier(int strength) {
        strengthModifier = strength;
    }

    public void setSpeedModifier(int speed) {
        speedModifier = speed;
    }

    public void setDefenseModifier(int defense) {
        defenseModifier = defense;
    }

    public void setLuckModifier(int luck) {
        luckModifier = luck;
    }

    public void setMovementModifier(int movement) {
        movementModifier = movement;
    }

    public void setRangeModifier(int range) {
        rangeModifier = range;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public Warrior getUnit() {
        return unit;
    }

    public int getStrengthModifier() {
        return strengthModifier;
    }

    public int getSpeedModifier() {
        return speedModifier;
    }

    public int getDefenseModifier() {
        return defenseModifier;
    }

    public int getLuckModifier() {
        return luckModifier;
    }

    public int getMovementModifier() {
        return movementModifier;
    }

    public int getRangeModifier() {
        return rangeModifier;
    }

    // EFFECTS: returns a string representation of this position
    @Override
    public String toString() {
        return "(" + posX + "," + posY + ")";
    }
}
