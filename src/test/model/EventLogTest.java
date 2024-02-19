package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Tests for the EventLog Class
// Inspired by https://github.students.cs.ubc.ca/CPSC210/AlarmSystem.git
public class EventLogTest {
    private GameEvent e1;
    private GameEvent e2;
    private GameEvent e3;

    @BeforeEach
    public void setup() {
        e1 = new GameEvent("A1");
        e2 = new GameEvent("A2");
        e3 = new GameEvent("A3");
        EventLog el = EventLog.getInstance();
        el.logEvent(e1);
        el.logEvent(e2);
        el.logEvent(e3);
    }

    @Test
    public void testLogEvent() {
        List<GameEvent> l = new ArrayList<>();

        EventLog el = EventLog.getInstance();
        for (GameEvent next : el) {
            l.add(next);
        }

        assertTrue(l.contains(e1));
        assertTrue(l.contains(e2));
        assertTrue(l.contains(e3));
    }

    @Test
    public void testClear() {
        EventLog el = EventLog.getInstance();
        el.clear();
        Iterator<GameEvent> itr = el.iterator();
        assertTrue(itr.hasNext());   // After log is cleared, the clear log event is added
        assertEquals("Event log cleared.", itr.next().getDescription());
        assertFalse(itr.hasNext());
    }
}
