package persistance;

import model.*;
import model.exceptions.DuplicateNameException;
import model.exceptions.CheckedGameException;
import model.exceptions.SaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.ImagePath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Tests for the JsonReader and JsonWriter classes
public class JsonTest {
    Army army1;
    Army army2;
    Warrior good1;
    Warrior good2;
    Warrior good3;
    Warrior evil1;
    Warrior evil2;
    Warrior evil3;
    Stage stage;
    JsonWriter writer;
    JsonReader reader;
    Battle data;
    Battle original;

    @BeforeEach
    void setup() {
        Army.resetNames();
        try {
            army1 = new Army("Player 1");
            army2 = new Army("Player 2");
        } catch (DuplicateNameException e) {
            fail("Should not have thrown exception.");
        }
        Warrior.resetNames();
        try {
            good1 = new Warrior("Good 1", 1, 2, 3, 4,
                    3,5, 6, ImagePath.NONE);
            good2 = new Warrior("Good 2", 3, 4, 2, 1,
                    1, 1, 5, ImagePath.SWORD);
            good3 = new Warrior("Good 3", 1, 1, 0, 0,
                    2,1, 1, ImagePath.NONE);
            evil1 = new Warrior("Evil 1", 10, 11, 12, 9,
                    0,8, 7, ImagePath.NONE);
            evil2 = new Warrior("Evil 2", 1, 3, 2, 6,
                    6,3, 2, ImagePath.NONE);
            evil3 = new Warrior("Evil 3", 5, 4, 3,
                    2, 5, 1, 6, ImagePath.SHIELD);
        } catch (DuplicateNameException e) {
            fail("Should have thrown exception.");
        }
    }

    @Test
    void testInvalidWritePath() {
        try {
            stage = new Stage(2,4);
            writer = new JsonWriter("./date/my\nillegalFileName.json");
            writer.open();
            fail("this file should not be found");
        } catch (FileNotFoundException e) {
            // pass
        }
    }

    @Test
    void testNonExistentFile() {
        try {
            reader = new JsonReader("./data/DoesNotExist2.json");
            data = reader.read();
            fail("Path should not be readable");
        } catch (IOException e) {
            // pass
        } catch (CheckedGameException e) {
            fail("File should be valid");
        }
    }

    @Test
    void testBasicArmy() {
        try {
            String path = "./data/testBasicArmys.json";
            stage = new Stage(10,10);
            army1.addWarrior(good1);
            good1.placeWarrior(stage, 1, 1);
            army2.addWarrior(evil1);
            evil1.placeWarrior(stage, 0, 0);
            original = new Battle(stage, Arrays.asList(army1, army2));
            writer = new JsonWriter(path);
            writer.open();
            writer.write(original);
            writer.close();

            reader = new JsonReader(path);
            data = reader.read();

            assertTrue(data.getActiveFaction().isAlive());
            assertEquals("Player 1", data.getActiveFaction().getFactionName());
            data.incrementTurn();
            assertTrue(data.getActiveFaction().isAlive());
            assertEquals("Player 2", army2.getFactionName());
            assertEquals(10, data.getStageHeight());
            assertEquals(10, data.getStageWidth());
        } catch (FileNotFoundException e) {
            fail("File should be writable");
        } catch (IOException e) {
            fail("File should be readable");
        } catch (CheckedGameException e) {
            fail("File should be valid");
        }
    }

    @Test
    void testRegularArmy() {
        try {
            String path = "./data/testRegularArmys.json";
            stage = new Stage(6,7);
            try {
                stage.getPosition(0,2).setModifiers(1,2,3,0,4,5);
                stage.getPosition(3,5).setModifiers(3,5,2,-5,4,1);
                stage.getPosition(1,3).setModifiers(3,3,5,3,-4,-3);
            } catch (Exception e) {
                fail("should not have thrown exception");
            }
            army1.addWarrior(good1);
            army1.addWarrior(good2);
            army1.addWarrior(good3);
            army2.addWarrior(evil1);
            army2.addWarrior(evil2);
            army2.addWarrior(evil3);
            try {
                good1.placeWarrior(stage, 6,5);
                good2.placeWarrior(stage, 4,4);
                good3.placeWarrior(stage, 0, 0);
                evil1.placeWarrior(stage, 6, 2);
                evil2.placeWarrior(stage, 0, 3);
                evil3.placeWarrior(stage, 3,5);
            } catch (CheckedGameException e) {
                fail("Should not have thrown exception.");
            }
            good2.reduceHP(100);
            evil1.reduceHP(100);
            good3.setCanMove(false);
            good1.setCanAttack(false);
            evil2.setCanMove(false);
            evil2.setCanAttack(false);
            original = new Battle(stage, Arrays.asList(army1, army2));
            original.incrementTurn();
            original.setTurn(5);

            writer = new JsonWriter(path);
            writer.open();
            writer.write(original);
            writer.close();

            reader = new JsonReader(path);
            data = reader.read();

            assertEquals(6, data.getStageHeight());
            assertEquals(7, data.getStageWidth());
            for (Position pos : data) {
                try {
                    checkPosition(pos, stage.getPosition(pos.getPosX(), pos.getPosY()));
                } catch (Exception e) {
                    fail("Should not have thrown exception");
                }
            }

            assertEquals("Player 2", data.getActiveFaction().getFactionName());
            data.setActiveArmyIndex(0);
            assertEquals("Player 1", data.getActiveFaction().getFactionName());
            assertEquals(5, data.getTurn());
            List<Army> competitors1 = data.getCompetitors();
            List<Army> competitors2 = data.getCompetitors();
            assertEquals(competitors1.size(), competitors2.size());
            for (int i = 0; i < competitors1.size(); i++) {
                checkArmy(competitors1.get(i), competitors2.get(i));
            }
        } catch (FileNotFoundException e) {
            fail("File should be writable");
        } catch (IOException e) {
            fail("File should be readable");
        } catch (CheckedGameException e) {
            fail("File should be valid");
        }
    }

    @Test
    void testIncorrectlySavedBattle() {
        String path = "./data/testInvalid.json";
        reader = new JsonReader(path);
        try {
            data = reader.read();
            fail("Should have thrown exception.");
        } catch (SaveException e) {
            try {
                Warrior w1 = new Warrior("Good 1", 1,1,1,1,
                        1,1,1,ImagePath.NONE);
                fail("Should have thrown exception");
            } catch (DuplicateNameException e1) {
                // pass
            }
        } catch(Exception e) {
            fail("Wrong exception.");
        }
    }

    private void checkPosition(Position pos1, Position pos2) {
        assertEquals(pos1.getStrengthModifier(), pos2.getStrengthModifier());
        assertEquals(pos1.getSpeedModifier(), pos2.getSpeedModifier());
        assertEquals(pos1.getDefenseModifier(), pos2.getDefenseModifier());
        assertEquals(pos1.getLuckModifier(), pos2.getLuckModifier());
        assertEquals(pos1.getMovementModifier(), pos2.getMovementModifier());
        assertEquals(pos1.getRangeModifier(), pos2.getRangeModifier());
        if (pos1.getUnit() == null) {
            assertNull(pos2.getUnit());
        } else {
            assertEquals(pos1.getUnit().getName(), pos2.getUnit().getName());
        }
    }

    private void checkWarrior(Warrior warrior1, Warrior warrior2) {
        assertEquals(warrior1.getHP(), warrior2.getHP());
        assertEquals(warrior1.getStrength(), warrior2.getStrength());
        assertEquals(warrior1.getSpeed(), warrior2.getSpeed());
        assertEquals(warrior1.getDefense(), warrior2.getDefense());
        assertEquals(warrior1.getLuck(), warrior2.getLuck());
        assertEquals(warrior1.getMovement(),warrior2.getMovement());
        assertEquals(warrior1.getRange(),warrior2.getRange());
        assertEquals(warrior1.getIsAlive(),warrior2.getIsAlive());
        assertEquals(warrior1.getCanMove(),warrior2.getCanMove());
        assertEquals(warrior1.getCanAttack(),warrior2.getCanAttack());
        if (warrior1.getPosition() != null) {
            assertEquals(warrior1.getPosition().getPosX(),warrior2.getPosition().getPosX());
            assertEquals(warrior1.getPosition().getPosY(),warrior2.getPosition().getPosY());
        } else {
            assertEquals(warrior1.getPosition(), warrior2.getPosition());
        }
        assertEquals(warrior1.getFaction(),warrior2.getFaction());
        assertEquals(warrior1.getName(),warrior2.getName());
        assertEquals(warrior1.getImageSource(),warrior2.getImageSource());
    }

    private void checkArmy(Army competitor1, Army competitor2) {
        assertEquals(competitor1.getFactionName(), competitor2.getFactionName());
        List<Warrior> warriors1 = competitor1.getWarriors();
        List<Warrior> warriors2 = competitor2.getWarriors();
        assertEquals(warriors1.size(), warriors2.size());
        for (int i = 0; i < warriors2.size(); i++) {
            checkWarrior(warriors1.get(i), warriors2.get(i));
        }
    }
}
