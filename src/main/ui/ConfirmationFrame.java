package ui;

import javax.swing.*;
import java.awt.*;

// Represents a Frame that pops up for the user to confirm an action
public abstract class ConfirmationFrame extends JFrame {

    private final JLabel title;

    // EFFECTS: Creates a frame with a yes and no button
    public ConfirmationFrame(String message) {
        super();
        setUndecorated(true);
        setLayout(new GridBagLayout());
        title = new JLabel(message);
        add(title, UiFormatter.makeGBC(0,0,2,1,2,1));
        add(makeYesButton(), UiFormatter.makeGBC(0,1,1,1,1,1));
        add(makeNoButton(), UiFormatter.makeGBC(1,1,1,1,1,1));
        pack();
    }

    // MODIFIES: this
    // EFFECTS: creates the yes button
    private JButton makeYesButton() {
        JButton yes = UiFormatter.makeDefaultButton("Yes");
        yes.addActionListener(e -> {
            yesAction();
            setVisible(false);
        });
        return yes;
    }

    // MODIFIES: this
    // EFFECTS: creates the no button
    private JButton makeNoButton() {
        JButton no = UiFormatter.makeDefaultButton("No");
        no.addActionListener(e -> {
            noAction();
            setVisible(false);
        });
        return no;
    }

    // MODIFIES: this
    // EFFECTS: makes this frame visible on top of the button that activates this
    public void openFrame(Component comp) {
        setLocationRelativeTo(comp);
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: makes this frame visible on top of the button that activates this and changes the
    //          title to msg
    protected void setTitleMessage(String msg) {
        title.setText(msg);
        pack();
    }

    // EFFECTS: performs the action the user wants to do
    protected abstract void yesAction();

    // EFFECTS: cancels the users action
    protected abstract void noAction();
}
