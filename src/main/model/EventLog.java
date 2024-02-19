package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

// Represents a log of events related to Warriors in Armies
// Inspired by https://github.students.cs.ubc.ca/CPSC210/AlarmSystem.git
public class EventLog implements Iterable<GameEvent> {

    private static EventLog theLog;
    private final Collection<GameEvent> events;

    // EFFECTS: creates a new empty collection of Events
    private EventLog() {
        events = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: creates a new instance of the Event Log and returns it if there is not
    //          already an instance of the Event Log. Otherwise, returns the already created log
    public static EventLog getInstance() {
        if (theLog == null) {
            theLog = new EventLog();
        }
        return theLog;
    }

    // MODIFIES: this
    // EFFECTS: adds an Event to the log
    public void logEvent(GameEvent e) {
        events.add(e);
    }

    // MODIFIES: this
    // EFFECTS: clears the event log then logs the clearing of the event log as an event
    public void clear() {
        events.clear();
        logEvent(new GameEvent("Event log cleared."));
    }

    @Override
    public Iterator<GameEvent> iterator() {
        return events.iterator();
    }
}
