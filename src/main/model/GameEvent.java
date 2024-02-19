package model;

import java.util.Calendar;
import java.util.Date;

// An event relating to a Warrior in an Army
// Inspired by https://github.students.cs.ubc.ca/CPSC210/AlarmSystem.git
public class GameEvent {
    private static final int HASH_CONSTANT = 17;
    private final Date dateLogged;
    private final String description;

    // EFFECTS: creates an event with a date and a description
    public GameEvent(String description) {
        dateLogged = Calendar.getInstance().getTime();
        this.description = description;
    }


    public Date getDate() {
        return dateLogged;
    }

    public String getDescription() {
        return description;
    }

    // EFFECTS: returns true if the other object is of the same class and has the same dateLogged
    //          and same description
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (other.getClass() != this.getClass()) {
            return false;
        }

        GameEvent otherEvent = (GameEvent) other;

        return (this.dateLogged.equals(otherEvent.dateLogged)
                && this.description.equals(otherEvent.description));
    }

    // EFFECTS: creates a hashcode of the object based on the date and the description
    @Override
    public int hashCode() {
        return (HASH_CONSTANT * dateLogged.hashCode() + description.hashCode());
    }

    // EFFECTS: creates a string representation of this object
    @Override
    public String toString() {
        return dateLogged.toString() + "\n" + description + "\n";
    }
}
