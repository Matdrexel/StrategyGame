package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

// Tests for the Event Class
// Inspired by https://github.students.cs.ubc.ca/CPSC210/AlarmSystem.git
public class GameEventTest {

    private GameEvent e;
    private GameEvent e2;
    private Date d;

    @BeforeEach
    public void setup() {
        e = new GameEvent("Frazz was added to player1's army");
        d = Calendar.getInstance().getTime();
        e2 = new GameEvent("Other Event");
    }

    @Test
    public void testEvent() {
        assertEquals("Frazz was added to player1's army", e.getDescription());
        assertTrue(Math.abs(e.getDate().getTime() - d.getTime()) <= 15);
        assertEquals(d + "\nFrazz was added to player1's army\n", e.toString());
    }

    @Test
    public void testEqualsAndHash() {
        assertFalse(e.equals(null));
        assertFalse(e.equals(e2));
        assertFalse(e.equals(d));
        assertEquals(e.hashCode(), e.hashCode());
        assertNotEquals(e.hashCode(), e2.hashCode());
    }
}
