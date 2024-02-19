package model;

import model.exceptions.DuplicateNameException;
import model.exceptions.InvalidPositionException;
import model.exceptions.CheckedGameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.ImagePath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

// Tests for the Stage class
public class StageTest {

    private Stage stage1;
    private Stage stage2;
    private Warrior w1;
    private Warrior w2;

    @BeforeEach
    void setup() {
        stage1 = new Stage(2,2);
        stage2 = new Stage(15, 13);
        Warrior.resetNames();
        try {
            w1 = new Warrior("jill", 1, 1, 1, 1,
                    1,1, 1, ImagePath.NONE);
            w2 = new Warrior("jane", 1, 1, 1, 1,
                    1,1, 1, ImagePath.NONE);
        } catch (DuplicateNameException e) {
            fail("Should not have thrown exception.");
        }
    }

    @Test
    void testGetValidPosition() {
        assertEquals(1, stage1.getFarthestBottom());
        assertEquals(1, stage1.getFarthestRight());
        assertEquals(14, stage2.getFarthestBottom());
        assertEquals(12, stage2.getFarthestRight());
        assertEquals(2, stage1.getGrid().size());
        assertEquals(13, stage2.getGrid().get(0).size());
        assertTrue(stage1.checkValidPosition(1,1));
        assertTrue(stage2.checkValidPosition(12,1));
        assertTrue(stage1.checkValidPosition(0,0));
        assertTrue(stage2.checkValidPosition(0,14));
        assertTrue(stage1.checkValidPosition(1,0));
        assertTrue(stage2.checkValidPosition(9,5));
        assertFalse(stage1.checkValidPosition(-1,1));
        assertFalse(stage1.checkValidPosition(0,2));
        assertFalse(stage1.checkValidPosition(0,5));
        assertFalse(stage1.checkValidPosition(1,-4));
        assertFalse(stage1.checkValidPosition(1,-1));
        assertFalse(stage1.checkValidPosition(20,0));
        assertFalse(stage1.checkValidPosition(20,20));
        assertFalse(stage2.checkValidPosition(20,0));
        assertFalse(stage2.checkValidPosition(12,49));
        assertFalse(stage2.checkValidPosition(13,0));
        assertFalse(stage2.checkValidPosition(0,15));
        assertFalse(stage2.checkValidPosition(-1,12));
        assertFalse(stage2.checkValidPosition(-10,9));
        assertFalse(stage2.checkValidPosition(1,-1));
        assertFalse(stage2.checkValidPosition(1,-49));
        assertFalse(stage2.checkValidPosition(-10,50));
        assertEquals(1, stage1.getFarthestBottom());
        assertEquals(1, stage1.getFarthestRight());
        assertEquals(14, stage2.getFarthestBottom());
        assertEquals(12, stage2.getFarthestRight());
    }

    @Test
    void testGetPosition() {
        try {
            assertEquals(1, stage1.getPosition(1, 0).getPosX());
            assertEquals(9, stage2.getPosition(5, 9).getPosY());
            assertNull(stage2.getPosition(4, 4).getUnit());
            stage2.getPosition(5, 6).placeUnit(w1);
            assertEquals(w1, stage2.getPosition(5, 6).getUnit());
            stage2.getPosition(4, 2).placeUnit(w2);
            assertEquals(w2, stage2.getPosition(4, 2).getUnit());
            w2.reduceHP(1);
            assertEquals(w1, stage2.getPosition(5, 6).getUnit());
            assertNull(stage2.getPosition(4, 2).getUnit());
        } catch (CheckedGameException e) {
            fail("Should not have thrown exception.");
        }
    }

    @Test
    void testGetInvalidPosition() {
        try {
            stage1.getPosition(2,0);
            fail("Should have thrown exception");
        } catch (InvalidPositionException e) {
            try {
                stage2.getPosition(4, -1);
                fail("Should have thrown exception");
            } catch (InvalidPositionException e1) {
                // success
            }
        }
    }

    @Test
    void testStageIterator() {
        int count = 0;
        ArrayList<Position> positions = new ArrayList<>();
        for (Position p : stage2) {
            count++;
            positions.add(p);
        }
        assertEquals(195, count);
        for (Position p : positions) {
            stage2.checkValidPosition(p.getPosX(), p.getPosY());
        }
        Iterator<Position> iterator = stage1.iterator();
        assertTrue(iterator.hasNext());
        count = 0;
        try {
            while (iterator.hasNext()) {
                Position next = iterator.next();
                count++;
                assertTrue(stage1.checkValidPosition(next.getPosX(), next.getPosY()));
            }
        } catch (Exception e) {
            fail("Should not have thrown exception");
        }
        assertEquals(4, count);
        try {
            iterator.next();
            fail("Should have thrown exception");
        } catch (NoSuchElementException e) {
            // success
        }
    }
}
