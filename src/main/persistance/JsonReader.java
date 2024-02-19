package persistance;

import model.*;
import model.exceptions.CheckedGameException;
import model.exceptions.SaveException;
import org.json.JSONArray;
import org.json.JSONObject;
import ui.ImagePath;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

// Represents a reader that can read the JSON game state stored in the source file
// Code influenced by the Json Serialization Demo here:
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
public class JsonReader {

    public static final String BATTLE = "battle";
    public static final String STAGE = "stage";
    public static final String COMPETITORS = "competitors";
    public static final String TURN = "turn";
    public static final String SUB_TURN = "subturn";

    public static final String LENGTH = "length";
    public static final String WIDTH = "width";
    public static final String POSITIONS = "positions";

    public static final String FACTION_NAME = "name";
    public static final String WARRIORS = "warriors";

    public static final String NAME = "name";
    public static final String HP = "hp";
    public static final String STRENGTH = "strength";
    public static final String SPEED = "speed";
    public static final String DEFENSE = "defense";
    public static final String LUCK = "luck";
    public static final String MOVEMENT = "movement";
    public static final String RANGE = "range";
    public static final String IMAGE = "image";
    public static final String ALIVE = "alive?";
    public static final String MOVE = "can move?";
    public static final String ATTACK = "can attack?";
    public static final String X = "x";
    public static final String Y = "y";

    private final String source;

    // EFFECTS: constructs a reader to load data from the source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads the stage and army data from the json source file and returns a hashMap
    //          containing all 3 objects with keys from the JsonWriter constants; throws an IOException
    //          if it cannot read the file, and throws GameException if the army and the stage do not match
    //          if read successfully, clears the eventLog and adds a GameEvent saying that the data has been loaded
    public Battle read() throws IOException, SaveException {
        String jsonData = readFile(source);
        JSONObject json = new JSONObject(jsonData);
        Battle data = parseData((JSONObject) json.get(BATTLE));
        EventLog el = EventLog.getInstance();
        el.clear();
        el.logEvent(new GameEvent("Loaded previous game from " + source));
        return data;
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }
        return contentBuilder.toString();
    }

    // EFFECTS: parses the json object to create the stage and two armies, and returns them
    //          in a Map. If the stage and armies are impossible to create, throws a GameException
    private Battle parseData(JSONObject json) throws SaveException {
        JSONObject jsonStage = json.getJSONObject(STAGE);
        Stage stage = parseStage(jsonStage.getInt(LENGTH), jsonStage.getInt(WIDTH), jsonStage.getJSONArray(POSITIONS));
        List<Army> competitors = parseArmies(json.getJSONArray(COMPETITORS), stage);
        int turn = json.getInt(TURN);
        int subTurn = json.getInt(SUB_TURN);
        Battle battle = new Battle(stage, competitors);
        battle.setTurn(turn);
        battle.setActiveArmyIndex(subTurn);
        return battle;
    }

    private Stage parseStage(int length, int width, JSONArray positions) throws SaveException {
        Stage stage = new Stage(length, width);
        Iterator<Position> stageIterator = stage.iterator();
        Iterator<Object> positionsIterator = positions.iterator();
        while (stageIterator.hasNext() && positionsIterator.hasNext()) {
            Position pos = stageIterator.next();
            JSONObject modifiers = (JSONObject) positionsIterator.next();
            pos.setModifiers(modifiers.getInt(STRENGTH), modifiers.getInt(SPEED), modifiers.getInt(DEFENSE),
                    modifiers.getInt(LUCK), modifiers.getInt(MOVEMENT), modifiers.getInt(RANGE));
        }
        if (stageIterator.hasNext() || positionsIterator.hasNext()) {
            throw new SaveException();
        }
        return stage;
    }

    private List<Army> parseArmies(JSONArray armies, Stage stage) throws SaveException {
        List<Army> competitors = new ArrayList<>();
        Warrior.resetNames();
        Army.resetNames();
        for (Object army : armies) {
            try {
                competitors.add(parseArmy((JSONObject) army, stage));
            } catch (CheckedGameException e) {
                Army.revertResetNames();
                Warrior.revertResetNames();
                throw new SaveException();
            }
        }
        return competitors;
    }

    // MODIFIES: stage
    // EFFECTS: creates an Army based on data, and updates the stage with the army warriors' positions
    //          if the armies are impossible to create, throws a GameException
    private Army parseArmy(JSONObject data, Stage stage) throws CheckedGameException {
        String armyName = data.getString(FACTION_NAME);
        Army savedArmy = new Army(armyName);
        for (Object jsonWarrior : data.getJSONArray(WARRIORS)) {
            JSONObject nextWarrior = (JSONObject) jsonWarrior;
            addWarrior(savedArmy, nextWarrior, stage);
        }
        return savedArmy;
    }

    // MODIFIES: army, stage
    // EFFECTS: converts the saved json object into a warrior and adds it to the army
    //          and updates the map with the warriors position if it exists
    private void addWarrior(Army army, JSONObject json, Stage stage) throws CheckedGameException {
        String name = json.getString(NAME);
        int hp = json.getInt(HP);
        int strength = json.getInt(STRENGTH);
        int speed = json.getInt(SPEED);
        int defense = json.getInt(DEFENSE);
        int luck = json.getInt(LUCK);
        int movement = json.getInt(MOVEMENT);
        int range = json.getInt(RANGE);
        String imageSource = json.getString(IMAGE);
        Warrior warrior;
        warrior = new Warrior(name, hp, strength, speed, defense, luck,
                    movement, range, ImagePath.getImageValue(imageSource));
        warrior.setIsAlive(json.getBoolean(ALIVE));
        warrior.setCanMove(json.getBoolean(MOVE));
        warrior.setCanAttack(json.getBoolean(ATTACK));
        if (json.getInt(X) >= 0) {
            warrior.placeWarrior(stage, json.getInt(X), json.getInt(Y));
        }
        army.addWarrior(warrior);
    }
}
