package model;

import model.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.ImagePath;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

// Tests for the Warrior Class
public class WarriorTest {

    private Warrior w1;
    private Warrior w2;
    private Warrior w3;
    private Warrior w4;
    private Warrior w5;
    private Warrior lucky1;
    private Warrior lucky2;
    private Stage stage;

    @BeforeEach
    void setup() {
        Warrior.resetNames();
        try {
            w1 = new Warrior("A", 10, 5, 1 + Warrior.SPEED_BONUS,
                    2, 0, 3, 1, ImagePath.NONE);
            w2 = new Warrior("B", 12, 4, 12,
                    10, 0, 8, 1, ImagePath.AXE);
            w3 = new Warrior("C", 5, 10, 1,
                    1, 0,4, 2, ImagePath.BOW);
            w4 = new Warrior("D", 7, 7, 1,
                    3, 0, 20, 3, ImagePath.NONE);
            w5 = new Warrior("Boss", 7, 5, 100,
                    0, 0, 1, 2, ImagePath.NONE);
            lucky1 = new Warrior("Lucky", 10, 3,1,
                    10,50,1,1, ImagePath.NONE);
            lucky2 = new Warrior("Lucky2", 10, 2,1,
                    10,60,1,1, ImagePath.NONE);
        } catch (DuplicateNameException e) {
            fail("Should not have thrown exception");
        }
        stage = new Stage(10,10);
    }

    @Test
    void checkPlaceWarrior() {
        assertEquals(10,w1.getHP());
        assertEquals(5, w1.getStrength());
        assertEquals("A", w1.getName());
        assertEquals(6, w1.getSpeed());
        assertEquals(2, w1.getDefense());
        assertEquals(3, w1.getMovement());
        assertEquals(1, w1.getRange());
        assertTrue(w1.getIsAlive());
        assertFalse(w1.getCanMove());
        assertFalse(w1.getCanAttack());
        assertEquals("None", w1.getFaction());
        assertNull(w1.getPosition());
        assertEquals(ImagePath.NONE, w1.getImageSource());
        try {
            w1.placeWarrior(stage, 1, 20);
            fail("Should have thrown exception.");
        } catch (InvalidPositionException e) {
            assertNull(w1.getPosition());
        } catch (Exception e) {
            fail("Wrong exception.");
        }
        try {
            w1.placeWarrior(stage, 3, 5);
            assertEquals(stage.getPosition(3,5), w1.getPosition());
            assertEquals(w1, stage.getPosition(3,5).getUnit());
        } catch (Exception e) {
            fail("Wrong exception.");
        }
        try {
            w2.placeWarrior(stage, 3, 5);
            fail("Should have thrown exception.");
        } catch (OccupiedException e) {
            try {
                assertEquals(stage.getPosition(3, 5), w1.getPosition());
                assertEquals(w1, stage.getPosition(3, 5).getUnit());
            } catch (InvalidPositionException e1) {
                fail("Should not have thrown exception");
            }
        } catch (Exception e) {
            fail("Wrong exception.");
        }
        try {
            w2.placeWarrior(stage,9,9);
            assertEquals(10,w1.getHP());
            assertEquals(5, w1.getStrength());
            assertEquals("A", w1.getName());
            assertEquals(6, w1.getSpeed());
            assertEquals(2, w1.getDefense());
            assertEquals(3, w1.getMovement());
            assertEquals(1, w1.getRange());
        } catch (Exception e) {
            fail("Wrong exception.");
        }
    }

    @Test
    void testMove() {
        w1.setCanMove(true);
        w2.setCanMove(true);
        try {
            w1.placeWarrior(stage,1,1);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception.");
        }
        try {
            w1.move(stage, 3,3);
            fail("Should have thrown exception");
        } catch (TooFarException e) {
            // pass
        } catch (Exception e) {
            fail("Wrong Exception");
        }
        try {
            w1.move(stage, 3,1);
            fail("Should have thrown exception");
        } catch (TooFarException e) {
            // pass
        } catch (Exception e) {
            fail("Wrong Exception");
        }
        try {
            w1.move(stage, -2,0);
            fail("Should have thrown exception");
        } catch (InvalidPositionException e) {
            // pass
        } catch (Exception e) {
            fail("Wrong Exception");
        }
        try {
            w1.move(stage, 2,1);
            assertFalse(w1.getCanMove());
            assertEquals(stage.getPosition(3,2), w1.getPosition());
            assertEquals(w1, stage.getPosition(3,2).getUnit());
            assertNull(stage.getPosition(1,1).getUnit());
        } catch (Exception e) {
            fail("Wrong Exception");
        }
        try {
            w2.placeWarrior(stage, 4,4);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception.");
        }
        try {
            w2.move(stage, 0, 8);
            fail("Should have thrown exception");
        } catch (InvalidPositionException e) {
            // pass
        } catch (Exception e) {
            fail("Wrong Exception");
        }
        try {
            w2.move(stage,-1,-2);
            fail("Should have thrown exception");
        } catch (OccupiedException e) {
            // pass
        } catch (Exception e) {
            fail("Wrong Exception");
        }
        try {
            w2.move(stage, -2,-2);
            assertFalse(w2.getCanMove());
        } catch (Exception e) {
            fail("Wrong Exception");
        }
        try {
            w1.move(stage,1,1);
            fail("Should have thrown exception");
        } catch (ImmobileException e) {
            // pass
        } catch (Exception e) {
            fail("Wrong Exception");
        }
        w2.setCanMove(true);
        try {
            w2.move(stage, 0,0);
            fail("Should have thrown exception");
        } catch (OccupiedException e) {
            // pass
        } catch (Exception e) {
            fail("Wrong Exception");
        }
    }

    @Test
    void testReduceHP() {
        w1.setCanMove(true);
        w1.setCanAttack(true);
        w1.reduceHP(1);
        assertEquals(9, w1.getHP());
        w1.reduceHP(8);
        assertEquals(1, w1.getHP());
        assertTrue(w1.getCanAttack());
        assertTrue(w1.getCanMove());
        assertTrue(w1.getIsAlive());
        w1.reduceHP(1);
        assertEquals(0, w1.getHP());
        assertFalse(w1.getIsAlive());
        assertFalse(w1.getCanMove());
        assertFalse(w1.getCanAttack());
        assertEquals(ImagePath.AXE, w2.getImageSource());
        w2.setCanAttack(true);
        w2.setCanMove(true);
        w2.reduceHP(13);
        assertEquals(0, w2.getHP());
        assertFalse(w2.getIsAlive());
        assertFalse(w2.getCanMove());
        assertFalse(w2.getCanAttack());
        w3.setCanMove(false);
        w3.setCanAttack(false);
        w3.reduceHP(500);
        assertEquals(0, w3.getHP());
        assertFalse(w3.getIsAlive());
        assertFalse(w3.getCanMove());
        assertFalse(w3.getCanAttack());
    }

    @Test
    void testAttack() {
        assertFalse(w1.attack(w2));
        assertEquals(12, w2.getHP());
        assertTrue(w2.getIsAlive());
        assertTrue(w2.attack(w1));
        assertEquals(8, w1.getHP());
        assertTrue(w1.getIsAlive());
        assertFalse(w3.attack(w2));
        assertEquals(12,w2.getHP());
        assertTrue(w3.getIsAlive());
        assertTrue(w3.attack(w1));
        assertEquals(0, w1.getHP());
        assertFalse(w1.getIsAlive());
        assertFalse(w5.attack(w2));
    }

    @Test
    void testCheckOpponents() {
        w1.setFaction("Red");
        w2.setFaction("Blue");
        w3.setFaction("Green");
        w4.setFaction("Green");
        w4.setCanMove(true);
        try {
            w2.placeWarrior(stage, 1,1);
            w1.placeWarrior(stage, 1,2);
            w3.placeWarrior(stage, 2,1);
            w4.placeWarrior(stage, 9,9);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception");
        }
        ArrayList<Warrior> list1 = new ArrayList<>();
        list1.add(w2);
        assertEquals(list1, w1.checkValidOpponents(stage));
        ArrayList<Warrior> list2 = new ArrayList<>();
        list2.add(w3);
        list2.add(w1);
        assertEquals(list2, w2.checkValidOpponents(stage));
        ArrayList<Warrior> list3 = new ArrayList<>();
        list3.add(w1);
        list3.add(w2);
        assertEquals(list3, w3.checkValidOpponents(stage));
        ArrayList<Warrior> list4 = new ArrayList<>();
        assertEquals(list4, w4.checkValidOpponents(stage));
        try {
            w4.move(stage,-6,-7);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception.");
        }
        list4.add(w1);
        list4.add(w2);
        assertEquals(list4, w4.checkValidOpponents(stage));
    }

    @Test
    void testBattleSpeedy() {
        w1.setCanAttack(true);
        w1.setCanMove(true);
        w3.setCanAttack(true);
        w3.setCanMove(true);
        try {
            w1.placeWarrior(stage, 1,1);
            w3.placeWarrior(stage, 2,1);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception.");
        }
        w3.battle(w1);
        assertEquals(2,w1.getHP());
        assertTrue(w1.getIsAlive());
        assertTrue(w1.getCanAttack());
        assertTrue(w1.getCanMove());
        assertEquals(1,w3.getHP());
        assertTrue(w3.getIsAlive());
        assertFalse(w3.getCanAttack());
        assertFalse(w3.getCanMove());
        w1.battle(w3);
        assertEquals(2,w1.getHP());
        assertTrue(w1.getIsAlive());
        assertFalse(w1.getCanAttack());
        assertFalse(w1.getCanMove());
        assertEquals(0,w3.getHP());
        assertFalse(w3.getIsAlive());
        assertFalse(w3.getCanAttack());
        assertFalse(w3.getCanMove());
    }

    @Test
    void testBattleSlow() {
        w2.setCanAttack(true);
        w2.setCanMove(true);
        w4.setCanAttack(true);
        w4.setCanMove(true);
        try {
            w2.placeWarrior(stage, 1,1);
            w4.placeWarrior(stage, 2,3);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception.");
        }
        w4.battle(w2);
        assertEquals(7,w4.getHP());
        assertTrue(w4.getIsAlive());
        assertFalse(w4.getCanAttack());
        assertFalse(w4.getCanMove());
        assertEquals(12,w2.getHP());
        assertTrue(w2.getIsAlive());
        assertTrue(w2.getCanAttack());
        assertTrue(w2.getCanMove());
        w4.setCanMove(true);
        try {
            w4.move(stage,-1,-1);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception.");
        }
        w4.battle(w2);
        assertEquals(6,w4.getHP());
        assertTrue(w4.getIsAlive());
        assertFalse(w4.getCanAttack());
        assertFalse(w4.getCanMove());
        assertEquals(12,w2.getHP());
        assertTrue(w2.getIsAlive());
        assertTrue(w2.getCanAttack());
        assertTrue(w2.getCanMove());
        for (int i = 0; i < 6; i++) {
            w4.battle(w2);
        }
        assertEquals(0,w4.getHP());
        assertFalse(w4.getIsAlive());
        assertFalse(w4.getCanAttack());
        assertFalse(w4.getCanMove());
    }

    @Test
    void testBattleRange() {
        w1.setCanAttack(true);
        w1.setCanMove(true);
        w5.setCanAttack(true);
        w5.setCanMove(true);
        try {
            w1.placeWarrior(stage, 1,1);
            w5.placeWarrior(stage, 2,2);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception.");
        }
        w5.battle(w1);
        assertEquals(7,w1.getHP());
        assertTrue(w1.getIsAlive());
        assertTrue(w1.getCanAttack());
        assertTrue(w1.getCanMove());
        assertEquals(7,w5.getHP());
        assertTrue(w5.getIsAlive());
        assertFalse(w5.getCanAttack());
        assertFalse(w5.getCanMove());
    }

    @Test
    void testFindMove() {
        w1.setFaction("Good");
        w4.setFaction("Bad");
        w1.setCanMove(true);
        w4.setCanMove(true);
        try {
            w1.placeWarrior(stage, 7,7);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception.");
        }
        assertFalse(w1.findMove(stage));
        assertEquals(7,w1.getPosition().getPosX());
        assertEquals(7,w1.getPosition().getPosY());
        try {
            assertEquals(w1, stage.getPosition(7, 7).getUnit());
        } catch (InvalidPositionException e) {
            fail("Should not have thrown exception");
        }
        try {
            w4.placeWarrior(stage, 4, 4);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception.");
        }
        assertTrue(w4.findMove(stage));
        assertEquals(9,w4.getPosition().getPosX());
        assertEquals(8,w4.getPosition().getPosY());
        try {
            assertEquals(w4, stage.getPosition(9, 8).getUnit());
            assertNull(stage.getPosition(4, 4).getUnit());
            assertTrue(w1.findMove(stage));
            assertEquals(w1, stage.getPosition(9, 7).getUnit());
            assertNull(stage.getPosition(7, 7).getUnit());
            assertEquals(9, w1.getPosition().getPosX());
            assertEquals(7, w1.getPosition().getPosY());
        } catch (InvalidPositionException e) {
            fail("should not have thrown exception");
        }
    }

    @Test
    void testFindMoveSurrounded() {
        w2.setFaction("Good");
        w1.setFaction("Good");
        w3.setFaction("Good");
        w4.setFaction("Evil");
        try {
            w4.placeWarrior(stage, 0, 0);
            w1.placeWarrior(stage, 0, 1);
            w3.placeWarrior(stage, 1, 0);
            w2.placeWarrior(stage, 2, 2);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception.");
        }
        assertFalse(w2.findMove(stage));
    }

    @Test
    void testDuplicateNames() {
        assertEquals("A", w1.toString());
        assertEquals("Boss", w5.toString());
        try {
            w1 = new Warrior("A", 1,1,1,1,1,1,1, ImagePath.NONE);
            fail("Should have thrown exception");
        } catch (DuplicateNameException e) {
            Warrior.resetNames();
            Warrior.revertResetNames();
        }
        try {
            w2 = new Warrior("B", 1,1,1,1,1,1,1, ImagePath.NONE);
            fail("Should have thrown exception");
        } catch (DuplicateNameException e) {
            Warrior.resetNames();
        }
        try {
            w3 = new Warrior("C", 1,1,1,1,1,1,1, ImagePath.NONE);
            Army.revertResetNames();
        } catch (DuplicateNameException e) {
            fail("Should not have thrown exception");
        }
        try {
            w4 = new Warrior("New Name", 1,1,1,1,
                    1,1,1, ImagePath.NONE);
        } catch (DuplicateNameException e) {
            fail("Should not have thrown exception");
        }
        try {
            w5 = new Warrior("New Name", 1,1,1,1,
                    1,1,1, ImagePath.NONE);
            fail("Should have thrown exception");
        } catch (DuplicateNameException e) {
            // pass
        }
    }

    @Test
    void testPositionModifiers() {
        assertEquals(4, w2.getRealStrength());
        assertEquals(12, w2.getRealSpeed(false));
        assertEquals(12, w2.getRealSpeed(true));
        assertEquals(10, w2.getRealDefense());
        assertEquals(8, w2.getRealMovement());
        assertEquals(1, w2.getRealRange());
        try {
            w2.setCanMove(true);
            w2.setCanAttack(true);
            w2.placeWarrior(stage, 0, 0);
            w5.placeWarrior(stage, 9, 5);
            w2.setFaction("Good");
            w5.setFaction("Evil");
            stage.getPosition(9, 5).setModifiers(21, 0, 3,
                    0,0, 2);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception");
        }
        assertEquals(4, w2.getRealStrength());
        assertEquals(12, w2.getRealSpeed(false));
        assertEquals(12 + Warrior.SPEED_BONUS, w2.getRealSpeed(true));
        assertEquals(10, w2.getRealDefense());
        assertEquals(0, w2.getRealLuck());
        assertEquals(8, w2.getRealMovement());
        assertEquals(1, w2.getRealRange());
        try {
            stage.getPosition(0, 0).setModifiers(5, 100, 4,
                    5,2, 3);
            stage.getPosition(9, 1).setModifiers(5, 100, 4,
                    0,2, 3);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception");
        }
        assertEquals(9, w2.getRealStrength());
        assertEquals(112, w2.getRealSpeed(false));
        assertEquals(112 + Warrior.SPEED_BONUS, w2.getRealSpeed(true));
        assertEquals(14, w2.getRealDefense());
        assertEquals(5, w2.getRealLuck());
        assertEquals(10, w2.getRealMovement());
        assertEquals(4, w2.getRealRange());
        try {
            w2.move(stage, 9, 1);
        } catch (TooFarException e) {
            fail("Not too far with stage modifier");
        } catch (Exception e) {
            fail("Should not have thrown exception.");
        }
        assertEquals(1, w2.checkValidOpponents(stage).size());
        assertTrue(w2.checkValidOpponents(stage).contains(w5));
        w2.battle(w5);
        assertFalse(w2.getIsAlive());
        assertEquals(1, w5.getHP());
    }

    @Test
    void testNegativeStatModifiers() {
        try {
            w2.placeWarrior(stage, 0, 0);
            stage.getPosition(0,0).setModifiers(-20,-13,-10, -7, -8, -1);
        } catch (Exception e) {
            fail("Should not have thrown exception");
        }
        assertEquals(0, w2.getRealStrength());
        assertEquals(0, w2.getRealSpeed(false));
        assertEquals(Warrior.SPEED_BONUS - 1, w2.getRealSpeed(true));
        assertEquals(0, w2.getRealDefense());
        assertEquals(0, w2.getRealLuck());
        assertEquals(1, w2.getRealMovement());
        assertEquals(1, w2.getRealRange());
        try {
            stage.getPosition(0,0).setSpeedModifier(-2000);
        } catch (Exception e) {
            fail("Should not have thrown exception.");
        }
        assertEquals(0, w2.getRealSpeed(false));
        assertEquals(0, w2.getRealSpeed(true));
    }

    @Test
    void testInvalidMethodsWithoutPositions() {
        try {
            w1.checkValidOpponents(stage);
            fail("Should have thrown exception.");
        } catch (NoPositionException e) {
            // success
        }
        try {
            w1.findMove(stage);
            fail("Should have thrown exception.");
        } catch (NoPositionException e) {
            // success
        }
        try {
            w1.move(stage, 1, 0);
            fail("Should have thrown exception.");
        } catch (NoPositionException e) {
            // success
        } catch (Exception e) {
            fail("Wrong exception.");
        }
        try {
            w1.battle(w2);
            fail("Should have thrown exception.");
        } catch (NoPositionException e) {
            try {
                w1.placeWarrior(stage, 0, 0);
            } catch (Exception e1) {
                fail("Should not have thrown exception");
            }
        }
        try {
            w1.battle(w2);
            fail("Should have thrown exception.");
        } catch (NoPositionException e) {
            // success
        }
        try {
            w2.battle(w1);
            fail("Should have thrown exception.");
        } catch (NoPositionException e) {
            // success
        }
    }

    @Test
    void testLuck() {
        try {
            lucky1.placeWarrior(stage, 0, 0);
            lucky2.placeWarrior(stage, 1, 0);
        } catch (Exception e) {
            fail("Should not have thrown exception");
        }
        while (lucky1.getIsAlive() && lucky2.getIsAlive()) {
            lucky1.setCanAttack(true);
            lucky2.setCanAttack(true);
            lucky1.battle(lucky2);
            if (lucky2.getIsAlive() && lucky1.getIsAlive()) {
                lucky2.battle(lucky1);
            }
        }
        assertFalse(lucky1.getIsAlive() && lucky2.getIsAlive());
    }
}
