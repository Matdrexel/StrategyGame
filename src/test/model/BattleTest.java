package model;

import model.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.ImagePath;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BattleTest {

    Stage stage;
    Army army1;
    Army army2;
    Army army3;
    Warrior w1;
    Warrior w2;
    Warrior w3;
    Warrior w4;
    Warrior w5;
    Warrior w6;
    Warrior w7;
    Battle battle;

    @BeforeEach
    void setup() {
        stage = new Stage(6,7);
        Army.resetNames();
        Warrior.resetNames();
        try {
            army1 = new Army("1");
            army2 = new Army("2");
            army3 = new Army("3");
            w1 = new Warrior("1",99,1,1,1,1,90,99, ImagePath.NONE);
            w2 = new Warrior("2",99,1,1,1,1,1,1, ImagePath.NONE);
            w3 = new Warrior("3",99,1,1,1,1,8,1, ImagePath.NONE);
            w4 = new Warrior("4",99,1,1,1,1,1,1, ImagePath.NONE);
            w5 = new Warrior("5",99,1,1,1,1,1,1, ImagePath.NONE);
            w6 = new Warrior("6",99,1,1,1,1,1,1, ImagePath.NONE);
            w7 = new Warrior("7",99,1,1,1,1,1,1, ImagePath.NONE);
            army1.addWarrior(w1);
            army1.addWarrior(w2);
            army1.addWarrior(w3);
            army2.addWarrior(w4);
            army2.addWarrior(w5);
            army2.addWarrior(w6);
            army3.addWarrior(w7);
            army1.placeArmy(stage);
            army2.placeArmy(stage);
            army3.placeArmy(stage);
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception");
        }
        army1.beginTurn();
        battle = new Battle(stage, Arrays.asList(army1, army2, army3));
    }

    @Test
    void testInitializeBattle() {
        try {
            Stage stage2 = new Stage(3,4);
            battle = new Battle(stage2, Arrays.asList(army1, army2));
            fail("Should have thrown exception");
        } catch (InvalidBattleException e) {
            // pass
        }
        try {
            Army army4 = new Army("4");
            Warrior w8 = new Warrior("8", 1,1,1,
                    1,1,1,1,ImagePath.NONE);
            army4.addWarrior(w8);
            battle = new Battle(stage, Arrays.asList(army4, army2));
        } catch (InvalidBattleException e) {
            // pass
        } catch (CheckedGameException e) {
            fail("Wrong exception");
        }
    }

    @Test
    void testSelectPosition() {
        assertNull(battle.getActiveWarrior());
        assertNull(battle.getActivePosition());
        assertEquals(Battle.SELECT, battle.selectPosition(w1.getPosition()));
        assertEquals(w1, battle.getActiveWarrior());
        assertEquals(w1.getPosition(), battle.getActivePosition());
        assertEquals(Battle.DESELECT, battle.selectPosition(battle.getActivePosition()));
        assertNull(battle.getActiveWarrior());
        assertEquals(Battle.SELECT, battle.selectPosition(w7.getPosition()));
        assertEquals(w7, battle.getActiveWarrior());
        assertEquals(w7.getPosition(), battle.getActivePosition());
        assertEquals(Battle.DESELECT, battle.selectPosition(battle.getActivePosition()));
        assertNull(battle.getActiveWarrior());
        assertEquals(Battle.SELECT, battle.selectPosition(w7.getPosition()));
        assertEquals(w7, battle.getActiveWarrior());
        assertEquals(w7.getPosition(), battle.getActivePosition());
        assertEquals(Battle.SELECT, battle.selectPosition(w6.getPosition()));
        assertEquals(w6, battle.getActiveWarrior());
        assertEquals(w6.getPosition(), battle.getActivePosition());
        assertEquals(Battle.SELECT, battle.selectPosition(w2.getPosition()));
        assertEquals(w2, battle.getActiveWarrior());
        assertEquals(w2.getPosition(), battle.getActivePosition());
        assertEquals(Battle.SELECT, battle.selectPosition(w1.getPosition()));
        assertEquals(w1, battle.getActiveWarrior());
        assertEquals(w1.getPosition(), battle.getActivePosition());
        Position oldPos = w1.getPosition();
        w1.findMove(stage);
        assertEquals(Battle.MOVE, battle.selectPosition(oldPos));
        assertEquals(w1, battle.getActiveWarrior());
        assertEquals(oldPos, battle.getActivePosition());
        assertEquals(Battle.BATTLE, battle.selectPosition(w7.getPosition()));
        assertEquals(w1, battle.getActiveWarrior());
        assertEquals(w7.getPosition(), battle.getActivePosition());
        battle.deselectPosition();
        assertEquals(Battle.SELECT, battle.selectPosition(w5.getPosition()));
        assertEquals(w5, battle.getActiveWarrior());
        assertEquals(w5.getPosition(), battle.getActivePosition());
        assertEquals(Battle.SELECT, battle.selectPosition(oldPos));
        assertNull(battle.getActiveWarrior());
        assertEquals(oldPos, battle.getActivePosition());
        assertEquals(Battle.SELECT, battle.selectPosition(w5.getPosition()));
        assertEquals(w5, battle.getActiveWarrior());
        assertEquals(w5.getPosition(), battle.getActivePosition());
        assertEquals(Battle.SELECT, battle.selectPosition(w7.getPosition()));
        assertEquals(w7, battle.getActiveWarrior());
        assertEquals(w7.getPosition(), battle.getActivePosition());
    }

    @Test
    void testSelectPositionWithWarrior() {
        battle.selectPosition(w1);
        assertEquals(w1, battle.getActiveWarrior());
        assertEquals(w1.getPosition(), battle.getActivePosition());
        battle.selectPosition(w4);
        assertEquals(w4, battle.getActiveWarrior());
        assertEquals(w4.getPosition(), battle.getActivePosition());
        battle.selectPosition(w4);
        assertEquals(w4, battle.getActiveWarrior());
        assertEquals(w4.getPosition(), battle.getActivePosition());
        battle.selectPosition(w7);
        assertEquals(w7, battle.getActiveWarrior());
        assertEquals(w7.getPosition(), battle.getActivePosition());
    }

    @Test
    void testMoveWarrior() {
        Position target = w2.getPosition();
        assertTrue(w2.findMove(stage));
        battle.selectPosition(w1);
        assertEquals(Battle.MOVE, battle.selectPosition(target));
        try {
            battle.moveWarrior();
            assertEquals(target, w1.getPosition());
            assertEquals(target, battle.getActivePosition());
            assertEquals(w1, battle.getActiveWarrior());
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception");
        }
        target = w3.getPosition();
        assertTrue(w3.findMove(stage));
        assertEquals(Battle.MOVE, battle.selectPosition(target));
        try {
            battle.moveWarrior();
            fail("Should have thrown exception");
        } catch (ImmobileException e) {
            assertEquals(w1, battle.getActiveWarrior());
            assertEquals(target, battle.getActivePosition());
        } catch (CheckedGameException e) {
            fail("Should have thrown Immobile Exception");
        }
        w2.setCanMove(true);
        for (Position pos : battle) {
            if ((pos.getDistance(w2.getPosition()) > w2.getRealMovement()) && (pos.getUnit() == null)) {
                target = pos;
                break;
            }
        }
        battle.selectPosition(w2);
        assertEquals(Battle.MOVE, battle.selectPosition(target));
        try {
            battle.moveWarrior();
            fail("Should have thrown exception");
        } catch (TooFarException e) {
            assertEquals(w2, battle.getActiveWarrior());
            assertEquals(target, battle.getActivePosition());
        } catch (CheckedGameException e) {
            fail("Should have thrown too far exception");
        }
    }

    @Test
    void testIsWarriorActionable() {
        assertFalse(battle.isWarriorActionable());
        battle.selectPosition(w1);
        assertTrue(battle.isWarriorActionable());
        battle.deselectPosition();
        assertFalse(battle.isWarriorActionable());
        w3.findMove(stage);
        battle.selectPosition(w3);
        assertTrue(battle.isWarriorActionable());
        w3.setCanAttack(false);
        assertFalse(battle.isWarriorActionable());
        battle.selectPosition(w4);
        assertFalse(battle.isWarriorActionable());
        w3.setCanAttack(true);
        assertFalse(battle.isWarriorActionable());
        battle.selectPosition(w2);
        assertTrue(battle.isWarriorActionable());
    }

    @Test
    void testBattleWarrior() {
        battle.selectPosition(w1);
        assertEquals(Battle.BATTLE, battle.selectPosition(w4.getPosition()));
        try {
            battle.battleWarrior();
            assertNull(battle.getActiveWarrior());
            assertFalse(w1.getCanAttack());
            assertNotNull(battle.getActivePosition2());
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception");
        }
        battle.selectPosition(w1);
        assertEquals(Battle.BATTLE, battle.selectPosition(w5.getPosition()));
        try {
            battle.battleWarrior();
            fail("Should have thrown an exception");
        } catch (ImmobileException e) {
            assertEquals(w1, battle.getActiveWarrior());
        } catch (CheckedGameException e) {
            fail("Should have thrown an exception");
        }
        battle.selectPosition(w2);
        assertEquals(Battle.BATTLE, battle.selectPosition(w6.getPosition()));
        try {
            battle.battleWarrior();
            fail("Should have thrown an exception");
        } catch (TooFarException e) {
            assertEquals(w2, battle.getActiveWarrior());
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception");
        }
    }

    @Test
    void testDeselectPosition() {
        battle.selectPosition(w1);
        battle.deselectPosition();
        assertNull(battle.getActiveWarrior());
        assertNull(battle.getActivePosition());
        assertNull(battle.getActivePosition2());
        battle.selectPosition(w1);
        battle.selectPosition(w4.getPosition());
        try {
            battle.battleWarrior();
            battle.deselectPosition();
            assertNull(battle.getActiveWarrior());
            assertNull(battle.getActivePosition());
            assertNull(battle.getActivePosition2());
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception");
        }
        battle.selectPosition(w2);
        battle.selectPosition(w1.getPosition());
        battle.deselectPosition();
        assertNull(battle.getActiveWarrior());
        assertNull(battle.getActivePosition());
        assertNull(battle.getActivePosition2());
    }

    @Test
    void testCheckIfOver() {
        assertFalse(battle.checkIfOver());
        battle.endArmyTurn();
        assertFalse(battle.checkIfOver());
        battle.incrementTurn();
        assertFalse(battle.checkIfOver());
        army3.forfeit();
        assertFalse(battle.checkIfOver());
        battle.incrementTurn();
        w1.setIsAlive(false);
        assertFalse(battle.checkIfOver());
        army2.forfeit();
        assertTrue(battle.checkIfOver());
    }

    @Test
    void testEndWarriorTurn() {
        battle.selectPosition(w1);
        assertTrue(w1.getCanAttack());
        assertTrue(w1.getCanMove());
        assertTrue(w1.getIsAlive());
        battle.endWarriorTurn();
        assertNull(battle.getActiveWarrior());
        assertFalse(w1.getCanAttack());
        assertFalse(w1.getCanMove());
        assertTrue(w1.getIsAlive());
        battle.endWarriorTurn();
        assertTrue(w2.getCanAttack());
        assertTrue(w2.getCanMove());
        assertTrue(w2.getIsAlive());
    }

    @Test
    void testEndArmyTurn() {
        assertEquals(army1.getWarriors().size(), army1.canAttack().size());
        assertFalse(battle.isTurnOver());
        assertTrue(battle.endArmyTurn());
        assertEquals(0, army1.canAttack().size());
        assertTrue(battle.isTurnOver());
        assertTrue(battle.endArmyTurn());
        assertEquals(0, army1.canAttack().size());
        army2.forfeit();
        battle.incrementTurn();
        assertFalse(battle.isTurnOver());
        assertTrue(battle.endArmyTurn());
        assertTrue(battle.isTurnOver());
        battle.incrementTurn();
        assertFalse(battle.isTurnOver());
        assertTrue(battle.endArmyTurn());
        assertTrue(battle.isTurnOver());
        assertEquals(0, army3.canAttack().size());
        army1.forfeit();
        assertFalse(battle.endArmyTurn());
    }

    @Test
    void testIncrementTurn() {
        assertEquals(1, battle.getTurn());
        assertEquals(army1, battle.getActiveFaction());
        battle.endArmyTurn();
        battle.incrementTurn();
        assertEquals(1, battle.getTurn());
        assertEquals(army2, battle.getActiveFaction());
        battle.incrementTurn();
        assertEquals(1, battle.getTurn());
        assertEquals(army3, battle.getActiveFaction());
        battle.incrementTurn();
        assertEquals(2, battle.getTurn());
        assertEquals(army1, battle.getActiveFaction());
        army2.forfeit();
        battle.setTurn(5);
        battle.incrementTurn();
        assertEquals(5, battle.getTurn());
        assertEquals(army3, battle.getActiveFaction());
        battle.incrementTurn();
        assertEquals(6, battle.getTurn());
        assertEquals(army1, battle.getActiveFaction());
        battle.incrementTurn();
        battle.setTurn(2);
        assertEquals(2, battle.getTurn());
        assertEquals(army3, battle.getActiveFaction());
        army1.forfeit();
        try {
            battle.incrementTurn();
            fail("Should have thrown exception");
        } catch (UncheckedGameException e) {
            // pass
        }
    }

    @Test
    void testComputerizedActions() {
        int attackers = army1.canAttack().size();
        boolean firstRun = true;
        int action = -1;
        while (action != Battle.FINISH) {
            action = battle.computerizedAction();
            if (action == Battle.MOVE) {
                assertNull(battle.getActivePosition().getUnit());
                assertTrue(battle.getActiveWarrior().getIsAlive());
                assertFalse(battle.getActiveWarrior().getCanMove());
                assertTrue(battle.getActiveWarrior().getCanAttack());
            } else if (action == Battle.BATTLE) {
                assertNull(battle.getActiveWarrior());
                assertEquals(attackers - 1, army1.canAttack().size());
                attackers--;
            }
            if (firstRun) {
                firstRun = false;
                battle.selectPosition(w2);
                battle.endWarriorTurn();
                attackers--;
            }
        }
    }

    @Test
    // note: this does not test which colours are being produced
    void testPositionColoursValid() {
        Map<Position, Color> colorMap = battle.positionColors();
        for (Position pos : stage) {
            assertTrue(colorMap.containsKey(pos));
        }
        for (Position pos : battle) {
            assertTrue(colorMap.containsKey(pos));
        }
        battle.selectPosition(w2);
        colorMap = battle.positionColors();
        for (Position pos : battle) {
            assertTrue(colorMap.containsKey(pos));
        }
        battle.selectPosition(w7);
        for (Position pos : battle) {
            assertTrue(colorMap.containsKey(pos));
        }
    }
}
