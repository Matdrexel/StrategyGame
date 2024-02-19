package persistance;

import model.*;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

// Represents a writer that stores a JSON representation of the game state to a file
// Code influenced by the Json Serialization Demo here:
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
public class JsonWriter {

    private static final int TAB = 4;
    private final String source;
    private PrintWriter writer;

    // EFFECTS: creates a JSON writer so save to the source file
    public JsonWriter(String source) {
        this.source = source;
    }

    // MODIFIES: this
    // EFFECTS: opens the JSON writer and throws FileNotFoundException if
    //          the file cannot be opened
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(source);
    }

    // MODIFIES: this
    // EFFECTS: closes the JSON writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this, EventLog
    // EFFECTS: creates a json representation of the game and saves it to the source file and
    //          updates the EventLog accordingly
    public void write(Battle battle) {
        JSONObject json = new JSONObject();
        json.put(JsonReader.BATTLE, battle.toJson());
        saveToFile(json.toString(TAB));
        EventLog.getInstance().logEvent(new GameEvent("Successfully saved game to " + source));
    }

    // MODIFIES: this
    // EFFECTS: writes converted json string to file
    public void saveToFile(String json) {
        writer.print(json);
    }
}
