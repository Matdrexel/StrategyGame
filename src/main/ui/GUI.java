package ui;

import model.*;
import model.exceptions.*;
import persistance.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.util.*;

// Represents the graphical interface used to play this game
public class GUI extends JFrame {

    private static final String JSON_STORE = "./data/game.json";

    private static final GUI FRAME = new GUI();

    private Army player1;
    private Army player2;
    private Stage stage;
    private Battle battle;

    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private JButton loadGameButton;

    private Collection<Warrior> recruits;

    private Map<Warrior, ImageIcon> warriorImageMap;

    // EFFECTS: returns this GUI for other classes to access
    public static GUI getInstance() {
        return FRAME;
    }

    // MODIFIES: this
    // EFFECTS: creates the title of the JFrame
    private GUI() {
        super("Game");
    }

    // MODIFIES: this
    // EFFECTS: Initializes JFrame and loads the new game phase screen
    public void startGame() {
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        printLogOnClose();
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setSize(750,600);
        setMinimumSize(new Dimension(100,100));
        newGamePhase();
    }

    // MODIFIES: this
    // EFFECTS: sets up the loading and initializing army phase
    private void newGamePhase() {
        makeLoadGameButton();
        add(new InitializeGamePanel(loadGameButton), BorderLayout.CENTER);
        setVisible(true);
    }

    // EFFECTS: creates a button that loads a previous save of the game if pressed and
    //          begins the battle phase
    private void makeLoadGameButton() {
        JButton loadGame = UiFormatter.makeDefaultButton("Load Saved Game");
        loadGame.addActionListener(e -> loadGame(loadGame));
        loadGameButton = loadGame;
    }

    // EFFECTS: tries to load the game. If successful, begins the battle phase,
    //          else notifies the user that the game cannot be loaded
    public void loadGame(JButton button) {
        if (tryLoadGame()) {
            warriorImageMap = new HashMap<>();
            for (Warrior warrior : battle.getCompetitors().get(0)) {
                warriorImageMap.put(warrior, UiFormatter.makeResizableImageForBattle(warrior.getImageSource()));
            }
            for (Warrior warrior : battle.getCompetitors().get(1)) {
                warriorImageMap.put(warrior, UiFormatter.makeResizableImageForBattle(warrior.getImageSource()));
            }
            loadGameButton = button;
            nextPhase();
            battlePhase();
        } else {
            button.setText("Error, cannot load game");
        }
    }

    // MODIFIES: this
    // EFFECTS: loads previous game from file, and returns true if successful.
    //          If this cannot be done, the game reverts back to its previous state
    //          before the load and returns false
    private boolean tryLoadGame() {
        Battle prevBattle = battle;
        try {
            battle = jsonReader.read();
            return true;
        } catch (Exception e) {
            battle = prevBattle;
            return false;
        }
    }

    // EFFECTS: ends the initialize game phase and starts the view instructions phase
    public void startNewGame() {
        loadGameButton.setText("Load Saved Game");
        nextPhase();
        viewInstructions();
    }

    // MODIFIES: this
    // EFFECTS: begins the view instructions phase
    private void viewInstructions() {
        add(new InstructionsPanel(), BorderLayout.CENTER);
        refreshPanel();
    }

    // MODIFIES: this
    // EFFECTS: begins the make army name phase
    public void finishedInstructions() {
        nextPhase();
        makeArmyName();
    }

    // MODIFIES: this
    // EFFECTS: creates a panel for the user to input their army name
    private void makeArmyName() {
        nextPhase();
        add(new PlayerArmyNamePanel(), BorderLayout.CENTER);
        refreshPanel();
    }

    // MODIFIES: this
    // EFFECTS: initializes the user's army with name then begins the choose recruits phase
    public void setPlayer1(Army player) {
        player1 = player;
        nextPhase();
        chooseRecruitsPhase();
    }

    // MODIFIES: this
    // EFFECTS: sets up the choosing recruits phase and the enemies warriors
    private void chooseRecruitsPhase() {
        add(new ChooseWarriorsPanel(player1), BorderLayout.CENTER);
        refreshPanel();
    }

    // MODIFIES: this
    // EFFECTS: stores the remaining not chosen recruits and the map associated warriors
    //          with their image icons and begins making the opponents army
    public void finishChooseRecruits(Collection<Warrior> recruits, Map<Warrior, ImageIcon> warriorImageMap) {
        this.recruits = recruits;
        this.warriorImageMap = warriorImageMap;
        nextPhase();
        makeOpponent();
    }

    // MODIFIES: this
    // EFFECTS: creates a screen for the user to input their opponents army in a text field
    private void makeOpponent() {
        add(new EnemyArmyNamePanel(), BorderLayout.CENTER);
        refreshPanel();
    }

    // MODIFIES: this
    // EFFECTS: initializes Player2's army with Name, then sets up Player2 and begins the stage making phase
    public void setPlayer2(Army enemyArmy) {
        player2 = enemyArmy;
        player2.generateArmy(player1, recruits);
        for (Warrior enemy : player2) {
            warriorImageMap.put(enemy, UiFormatter.makeResizableImageForBattle(enemy.getImageSource()));
        }
        nextPhase();
        makeStage();
    }

    // MODIFIES: this
    // EFFECTS: creates a screen for the user to choose the dimension of the stage
    private void makeStage() {
        add(new StageSizePanel(player1.getWarriors().size() * 3), BorderLayout.CENTER);
        refreshPanel();
    }

    // MODIFIES: this
    // EFFECTS: constructs a stage with the given height and width and moves the GUI to the
    //          placing phase
    public void setStage(int height, int width) {
        stage = new Stage(height, width);
        nextPhase();
        placingPhase();
    }

    // MODIFIES: this
    // EFFECTS: creates a visualization for the stage and prompts the user to place their warriors
    private void placingPhase() {
        add(new PlacingPanel(stage, player1), BorderLayout.CENTER);
        refreshPanel();
    }

    // MODIFIES: this
    // EFFECTS: places enemy warriors on the map
    public void placeEnemyArmy() {
        nextPhase();
        try {
            player2.placeArmy(stage);
        } catch (NoPositionAvailableException e) {
            throw new RuntimeException("Problem with placing all enemy warriors");
        }
        player1.beginTurn();
        battle = new Battle(stage, Arrays.asList(player1, player2));
        battlePhase();
    }

    // MODIFIES: this
    // EFFECTS: constructs a JFrame for the battle phase
    private void battlePhase() {
        add(new BattlePanel(battle, loadGameButton), BorderLayout.CENTER);
        refreshPanel();
    }

    // EFFECTS: saves the current game state, or informs the user that the game cannot
    //          be saved if the saving file cannot be opened
    public void saveGame() throws FileNotFoundException {
        jsonWriter.open();
        jsonWriter.write(battle);
        jsonWriter.close();
    }

    // MODIFIES: this
    // EFFECTS: creates a final screen prompting the user to save before they quit the application
    //          or cancels quitting the application
    public void saveAndQuit() {
        nextPhase();
        add(new QuittingPanel(), BorderLayout.CENTER);
        refreshPanel();
    }

    // MODIFIES: this
    // EFFECTS: closes the application
    public void quit() {
        nextPhase();
        dispose();
    }

    // MODIFIES: this
    // EFFECTS: moves the user back to the battle phase
    public void cancelQuit() {
        nextPhase();
        battlePhase();
    }

    // MODIFIES: this
    // EFFECTS: ends the battle and moves to the finish phase
    public void endBattle() {
        nextPhase();
        finishPhase();
    }

    // MODIFIES: this
    // EFFECTS: creates a panel used to notify the user whether they won or lost
    private void finishPhase() {
        add(new GameResultsPanel(battle.getCompetitors().get(0).isAlive()), BorderLayout.CENTER);
        refreshPanel();
    }

    // EFFECTS: returns the image icon associated with w
    public ImageIcon getWarriorIcon(Warrior w) {
        return warriorImageMap.get(w);
    }

    // MODIFIES: this
    // EFFECTS: adds the panel to the specified location then refreshes the frame
    public void refreshPanel() {
        validate();
        repaint();
    }

    // MODIFIES: this
    // EFFECTS: removes all components from the JFrame and sets the visibility to false
    private void nextPhase() {
        getContentPane().removeAll();
    }

    // MODIFIES: this
    // EFFECTS: prints the log of the game events to the console when the application closes
    private void printLogOnClose() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent w) {
                printLog();
            }

            @Override
            public void windowClosed(WindowEvent w) {
                printLog();
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: prints the EventLog to the console
    private void printLog() {
        EventLog el = EventLog.getInstance();
        for (GameEvent e : el) {
            System.out.println(e.toString());
        }
    }
}