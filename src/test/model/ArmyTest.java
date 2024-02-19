package model;

import model.exceptions.DuplicateNameException;
import model.exceptions.NoPositionAvailableException;
import model.exceptions.CheckedGameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.ImagePath;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

// Tests for the Army class
class ArmyTest {

    private Army army1;
    private Army army2;
    private Warrior w1;
    private Warrior w2;
    private Warrior w3;
    private Warrior w4;
    private Warrior w5;
    private Warrior w6;
    private Warrior w7;
    private Stage stage1;
    private Stage stage2;

    @BeforeEach
    void setup() {
        Army.resetNames();
        Warrior.resetNames();
        try {
            army1 = new Army("Goods");
            army2 = new Army("Empty");
            w1 = new Warrior("A", 1,1,1,1,1,1,1, ImagePath.NONE);
            w2 = new Warrior("B", 1,1,1,1,1,1,1, ImagePath.NONE);
            w3 = new Warrior("C",1,1,1,1,1,1,1, ImagePath.NONE);
            w4 = new Warrior("D", 1,1,1,1,1,1,1, ImagePath.NONE);
            w5 = new Warrior("E", 1,1,1,1,1,1,1, ImagePath.NONE);
            w6 = new Warrior("F", 1,1,1,1,1,1,1, ImagePath.NONE);
            w7 = new Warrior("G", 1,1,1,1,1,1,1, ImagePath.NONE);
        } catch (DuplicateNameException e) {
            fail("Should not have duplicate names");
        }
        stage1 = new Stage(3,2);
        stage2 = new Stage(5,5);
    }

    @Test
    void testAddWarriorAndIsAlive() {
        assertFalse(army1.isAlive());
        assertTrue(army1.canMove().isEmpty());
        assertEquals("Goods", army1.getFactionName());
        assertEquals("Empty", army2.getFactionName());
        army1.addWarrior(w1);
        army1.addWarrior(w2);
        assertEquals("Goods",w1.getFaction());
        assertEquals("Goods",w2.getFaction());
        assertTrue(army1.canMove().isEmpty());
        w1.setCanMove(true);
        assertEquals(1, army1.canMove().size());
        assertTrue(army1.canMove().contains(w1));
        assertFalse(army1.canMove().contains(w2));
        assertTrue(army1.isAlive());
        w2.reduceHP(1);
        assertTrue(army1.isAlive());
        assertTrue(army1.canMove().contains(w1));
        assertFalse(army1.canMove().contains(w2));
        w1.reduceHP(1);
        assertFalse(army1.isAlive());
    }

    @Test
    void testForfeit() {
        army1.forfeit();
        assertFalse(army1.isAlive());
        army1.addWarrior(w1);
        army1.addWarrior(w2);
        army1.addWarrior(w3);
        assertTrue(army1.isAlive());
        assertTrue(army1.getWarriors().contains(w1));
        assertTrue(army1.getWarriors().contains(w2));
        assertTrue(army1.getWarriors().contains(w3));
        w2.setIsAlive(false);
        assertTrue(army1.isAlive());
        army1.forfeit();
        assertFalse(army1.isAlive());
    }

    @Test
    void testSelectWarriorBeginEndTurn() {
        army1.addWarrior(w1);
        army1.addWarrior(w2);
        army1.addWarrior(w3);
        assertEquals(w3, army1.selectWarrior("C"));
        assertEquals(w1, army1.selectWarrior("A"));
        assertNull(army1.selectWarrior("D"));
        assertNull(army2.selectWarrior("A"));
        w2.reduceHP(1);
        assertNull(army1.selectWarrior("B"));
        army1.beginTurn();
        assertTrue(army1.selectWarrior("C").getCanAttack());
        assertTrue(army1.selectWarrior("A").getCanMove());
        assertNull(army1.selectWarrior("B"));
        army2.beginTurn();
        assertFalse(army2.isAlive());
        army1.endTurn();
        assertFalse(army1.selectWarrior("C").getCanAttack());
        assertFalse(army1.selectWarrior("A").getCanMove());
        assertNull(army1.selectWarrior("B"));
        army2.endTurn();
        assertNull(army2.selectWarrior("Anything"));
    }

    @Test
    void testCanAttack() {
        army1.addWarrior(w1);
        army1.addWarrior(w2);
        army1.addWarrior(w3);
        assertEquals(0, army1.canAttack().size());
        w1.setCanAttack(true);
        assertEquals(1, army1.canAttack().size());
        assertTrue(army1.canAttack().contains(w1));
        assertFalse(army1.canAttack().contains(w2));
        army1.beginTurn();
        assertEquals(3, army1.canAttack().size());
        assertTrue(army1.canAttack().contains(w1));
        assertTrue(army1.canAttack().contains(w2));
        assertTrue(army1.canAttack().contains(w3));
        w1.setCanAttack(false);
        assertEquals(2, army1.canAttack().size());
        assertFalse(army1.canAttack().contains(w1));
        assertTrue(army1.canAttack().contains(w2));
        assertTrue(army1.canAttack().contains(w3));
        army1.endTurn();
        assertEquals(0, army1.canAttack().size());
    }

    @Test
    void testGenerateArmy() {
        army1.addWarrior(w1);
        army1.addWarrior(w2);
        try {
            army1.addWarrior(new Warrior("Enemy 1", 1,1,1,
                    1,1,1,1, ImagePath.NONE));
        } catch (DuplicateNameException e) {
            fail("Should not have thrown exception.");
        }
        ArrayList<Warrior> available = new ArrayList<>();
        available.add(w3);
        army2.generateArmy(army1, available);
        assertEquals(3,army2.getWarriors().size());
        assertTrue(army2.getWarriors().contains(w3));
        assertNull(army2.selectWarrior("Enemy 1"));
        assertEquals("Enemy 2", army2.selectWarrior("Enemy 2").getName());
        assertEquals("Enemy 3", army2.selectWarrior("Enemy 3").getName());
    }

    @Test
    void testGenerateArmyOneWarrior() {
        army1.addWarrior(w1);
        ArrayList<Warrior> available = new ArrayList<>();
        available.add(w2);
        available.add(w3);
        army2.generateArmy(army1, available);
        assertEquals(1, army2.getWarriors().size());
        assertTrue(army2.getWarriors().contains(w2));
    }

    @Test
    void testPlaceArmy() {
        army1.addWarrior(w1);
        army1.addWarrior(w2);
        army2.addWarrior(w3);
        army2.addWarrior(w4);
        army2.addWarrior(w5);
        try {
            army1.placeArmy(stage1);
            for (Warrior w : army1) {
                assertNotNull(w.getPosition());
            }
            positionsFilled(2);
            army2.placeArmy(stage1);
            for (Warrior w : army2) {
                assertNotNull(w.getPosition());
            }
            positionsFilled(5);
            army1.placeArmy(stage1);
            army2.placeArmy(stage1);
            for (Warrior w : army1) {
                assertNotNull(w.getPosition());
            }
            for (Warrior w : army2) {
                assertNotNull(w.getPosition());
            }
            positionsFilled(5);
        } catch (NoPositionAvailableException e) {
            fail("Should not have thrown exception");
        }
    }

    @Test
    void testUnconventionalPlaceArmy() {
        army1.addWarrior(w1);
        army1.addWarrior(w2);
        army1.addWarrior(w3);
        army2.addWarrior(w4);
        army2.addWarrior(w5);
        army2.addWarrior(w6);
        army2.addWarrior(w7);
        try {
            w2.placeWarrior(stage1, 1, 2);
            army1.placeArmy(stage1);
            for (Warrior w : army1) {
                assertNotNull(w.getPosition());
            }
            assertEquals(stage1.getPosition(1, 2), w2.getPosition());
            positionsFilled(3);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception");
        }
        try {
            army2.placeArmy(stage1);
            fail("Should have thrown exception");
        } catch (NoPositionAvailableException e) {
            positionsFilled(6);
            for (Warrior w : army1) {
                assertNotNull(w.getPosition());
            }
            int count = 0;
            for (Warrior w : army2) {
                if (w.getPosition() != null) {
                    count++;
                }
            }
            assertEquals(3, count);
        }
    }

    void positionsFilled(int expected) {
        int count = 0;
        for (Position position : stage1) {
            if (position.getUnit() != null) {
                count++;
            }
        }
        assertEquals(expected, count);
    }

    @Test
    void testDuplicateName() {
        try {
            army1 = new Army("Goods");
            fail("Should have thrown exception");
        } catch (DuplicateNameException e) {
            Army.resetNames();
            Army.revertResetNames();
        }
        try {
            army2 = new Army("Empty");
            fail("Should have thrown exception");
        } catch (DuplicateNameException e) {
            Army.resetNames();
        }
        try {
            army1 = new Army("Goods");
            Army.revertResetNames();
        } catch (DuplicateNameException e) {
            fail("Should not have thrown exception");
        }
        try {
            army1 = new Army("New Name");
        } catch (DuplicateNameException e) {
            fail("Should not have thrown exception");
        }
        try {
            army2 = new Army("New Name");
            fail("Should have thrown exception");
        } catch (DuplicateNameException e) {
            // pass
        }
    }

    @Test
    void testValidPositions() {
        assertFalse(army1.validPositions(stage1));
        assertFalse(army1.validPositions(stage2));
        army1.addWarrior(w1);
        army1.addWarrior(w2);
        assertFalse(army1.validPositions(stage1));
        assertFalse(army1.validPositions(stage2));
        try {
            w1.placeWarrior(stage2, 0, 0);
            w2.placeWarrior(stage2, 2, 1);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception");
        }
        assertFalse(army1.validPositions(stage1));
        assertTrue(army1.validPositions(stage2));
        try {
            w2.setCanMove(true);
            w2.move(stage2, 1, 0);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception");
        }
        assertFalse(army1.validPositions(stage1));
        assertTrue(army1.validPositions(stage2));
    }
}