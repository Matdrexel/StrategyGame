package persistance;

import org.json.JSONObject;

// Interface for objects that can be saved to a file with a JSON representation
// Code influenced by the Json Serialization Demo here:
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
public interface Savable {
    // EFFECTS: returns this as a JSON object
    JSONObject toJson();
}
