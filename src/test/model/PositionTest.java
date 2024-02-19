package model;

import model.exceptions.DuplicateNameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.ImagePath;

import static org.junit.jupiter.api.Assertions.*;

// Tests for the Position class
public class PositionTest {
    private Position pos1;
    private Position pos2;
    private Position pos3;
    private Warrior w1;
    private Warrior w2;

    @BeforeEach
    void setup() {
        pos1 = new Position(0, 0);
        pos2 = new Position(4, 7);
        pos3 = new Position(15, 3);
        Warrior.resetNames();
        try {
            w1 = new Warrior("bob", 1, 1, 1, 1,
                    1, 1, 1, ImagePath.NONE);
            w2 = new Warrior("bill", 1, 1, 1, 1,
                    1, 1, 1, ImagePath.NONE);
        } catch (DuplicateNameException e) {
            fail("Should not have thrown exception.");
        }
    }

    @Test
    void testPlaceAndRemove() {
        assertEquals("(0,0)", pos1.toString());
        assertEquals("(4,7)", pos2.toString());
        assertEquals("(15,3)", pos3.toString());
        assertNull(pos1.getUnit());
        assertNull(pos2.getUnit());
        assertEquals(0, pos1.getPosX());
        assertEquals(4, pos2.getPosX());
        assertEquals(0, pos1.getPosY());
        assertEquals(7, pos2.getPosY());
        assertTrue(pos1.placeUnit(w1));
        assertEquals(pos1, w1.getPosition());
        assertEquals(w1, pos1.getUnit());
        assertFalse(pos1.placeUnit(w2));
        assertEquals(w1, pos1.getUnit());
        assertEquals(pos1, w1.getPosition());
        assertNull(w2.getPosition());
        pos1.removeUnit();
        assertNull(pos1.getUnit());
        pos2.removeUnit();
        assertNull(pos2.getUnit());
        assertEquals(0, pos1.getPosX());
        assertEquals(4, pos2.getPosX());
        assertEquals(0, pos1.getPosY());
        assertEquals(7, pos2.getPosY());
        assertEquals("(0,0)", pos1.toString());
        assertEquals("(4,7)", pos2.toString());
        assertEquals("(15,3)", pos3.toString());
    }

    @Test
    void testGetDistance() {
        assertEquals(11, pos1.getDistance(pos2));
        assertEquals(11, pos2.getDistance(pos1));
        assertEquals(0, pos2.getDistance(pos2));
        assertEquals(15, pos2.getDistance(pos3));
        assertEquals(15, pos3.getDistance(pos2));
    }

    @Test
    void testSetWarrior() {
        pos1.setWarrior(w1);
        assertEquals(w1, pos1.getUnit());
        assertEquals(pos1, w1.getPosition());
        pos1.setWarrior(w2);
        assertNull(w1.getPosition());
        assertEquals(w2, pos1.getUnit());
        assertEquals(pos1, w2.getPosition());
    }

    @Test
    void testModifiers() {
        assertEquals(0, pos1.getStrengthModifier());
        assertEquals(0, pos1.getSpeedModifier());
        assertEquals(0, pos1.getDefenseModifier());
        assertEquals(0, pos1.getLuckModifier());
        assertEquals(0, pos1.getMovementModifier());
        assertEquals(0, pos1.getRangeModifier());
        pos1.setModifiers(2,3,-3,-5, 4, 8);
        assertEquals(2, pos1.getStrengthModifier());
        assertEquals(3, pos1.getSpeedModifier());
        assertEquals(-3, pos1.getDefenseModifier());
        assertEquals(-5, pos1.getLuckModifier());
        assertEquals(4, pos1.getMovementModifier());
        assertEquals(8, pos1.getRangeModifier());
    }
}