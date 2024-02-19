package ui;

import javax.swing.*;

// The main class that runs the game
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error with setting look and feel of UI");
        } finally {
            GUI.getInstance().startGame();
        }
    }
}
