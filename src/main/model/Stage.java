package model;

import model.exceptions.InvalidPositionException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistance.JsonReader;
import persistance.Savable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

// Represents the game map where all units are placed, having a list of list of positions
// called grid, the farthest row towards the bottom called farthestBottom, and the farthest
// positions towards the right side of the map called farthestRight
public class Stage implements Savable, Iterable<Position> {

    private final ArrayList<ArrayList<Position>> grid;
    private final int farthestBottom;
    private final int farthestRight;

    // REQUIRES: length > 0, width > 0
    // EFFECTS: constructs a list of list of positions grid, and calculates the bottommost row
    //          and rightmost column
    public Stage(int length, int width) {
        grid = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            ArrayList<Position> tempList = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                tempList.add(new Position(j, i));
            }
            grid.add(tempList);
        }
        this.farthestBottom = length - 1;
        this.farthestRight = width - 1;
        EventLog.getInstance().logEvent(new GameEvent("Created a "
                + length + " by " + width + " sized stage"));
    }

    // EFFECTS: returns the Position at the x and y coordinates, throws InvalidPositionException
    //          if this position does not exist
    public Position getPosition(int posX, int posY) throws InvalidPositionException {
        if (checkValidPosition(posX, posY)) {
            return grid.get(posY).get(posX);
        }
        throw new InvalidPositionException();
    }

    // EFFECTS: checks if the coordinates x and y point to a position in the grid
    public boolean checkValidPosition(int x, int y) {
        return ((x >= 0) && (x <= farthestRight) && (y >= 0) && (y <= farthestBottom));
    }

    // EFFECTS: returns the JSON representation of this stage
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(JsonReader.WIDTH, farthestRight + 1);
        json.put(JsonReader.LENGTH, farthestBottom + 1);
        json.put(JsonReader.POSITIONS, positionsToJson());
        return json;
    }

    private JSONArray positionsToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Position pos : this) {
            jsonArray.put(pos.toJson());
        }
        return jsonArray;
    }

    public ArrayList<ArrayList<Position>> getGrid() {
        return grid;
    }

    public int getFarthestBottom() {
        return farthestBottom;
    }

    public int getFarthestRight() {
        return farthestRight;
    }

    // EFFECTS: returns the positions iterator of this stage
    public Iterator<Position> iterator() {
        return new StageIterator();
    }

    // Represents the iterator used for accessing the positions in the stage
    public class StageIterator implements Iterator<Position> {
        private int pointerX;
        private int pointerY;

        // EFFECTS: initializes pointerX and pointerY to 0
        public StageIterator() {
            pointerX = 0;
            pointerY = 0;
        }

        // EFFECTS: returns true if pointerY is still within the range of possible Y values
        //          for positions in the stage
        @Override
        public boolean hasNext() {
            return pointerY <= farthestBottom;
        }

        // MODIFIES: this
        // EFFECTS: returns the position at the x,y value of the stage and increments the x pointer by 1
        //          if the x pointer is no longer within the range of possible x values, resets the
        //          x pointer to 0 and increments the y pointer by 1
        //          if there is no next position, throws NoSuchElementException
        @Override
        public Position next() {
            try {
                Position nextPos = getPosition(pointerX, pointerY);
                pointerX++;
                if (pointerX > farthestRight) {
                    pointerX = 0;
                    pointerY++;
                }
                return nextPos;
            } catch (InvalidPositionException e) {
                throw new NoSuchElementException();
            }
        }
    }
}
