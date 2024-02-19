package ui;

import javax.swing.*;
import java.awt.*;

// Represents a panel that displays the results of the battle
public class GameResultsPanel extends DefaultPanel {

    // EFFECTS: sets the text of this panel according to whether the user won the game or not
    public GameResultsPanel(boolean win) {
        super();
        setLayout(new GridLayout(1,1));
        setBackground(UiFormatter.BABY_BLUE);
        JLabel end = new JLabel("", SwingConstants.CENTER);
        end.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        if (win) {
            end.setText("<html><center>Congratulations, "
                    + "you won!<br/>Thank you for playing my game!</center></html>");
        } else {
            end.setText("<html><center>Sorry, you lost!<br/>Thank you for playing my game!</center></html>");
        }
        end.setHorizontalTextPosition(JLabel.CENTER);
        end.setVerticalTextPosition(JLabel.CENTER);
        end.setForeground(Color.BLACK);
        add(end);
    }
}
