package ui;

import javax.swing.*;
import java.awt.*;

// Represents a panel used for setting the user's preferred stage size
public class StageSizePanel extends DefaultPanel {

    private final JTextField stageHeight;
    private final JTextField stageWidth;
    private final JLabel stageSizeError;
    private final int minSize;

    // EFFECTS: creates a panel with a splash screen telling the user to input their preferred stage size
    //          and two text fields to input the width and height of the stage
    public StageSizePanel(int minSize) {
        super();
        this.minSize = minSize;
        setLayout(new GridBagLayout());
        add(UiFormatter.makeSplashLabel("Set the size of the battle stage", Color.CYAN, Color.BLACK),
                UiFormatter.makeGBC(0,0,2,-100,1,5));
        stageHeight = makeDimensionTextField();
        stageWidth = makeDimensionTextField();
        JLabel height = new JLabel("Height:     ");
        JLabel width = new JLabel("Width:      ");
        height.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        width.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        stageSizeError = UiFormatter.makeErrorLabel();
        add(height, UiFormatter.makeGBC(0,2,-100,-100,0,1));
        add(width, UiFormatter.makeGBC(0,3,-100,-100,0,1));
        add(stageSizeError, UiFormatter.makeGBC(0,1,2,-100,1,0));
        add(stageHeight, UiFormatter.makeGBC(1,2,-100,-100,1,1));
        add(stageWidth, UiFormatter.makeGBC(1,3,-100,-100,1,1));
    }

    // MODIFIES: this, frame
    // EFFECTS: creates a JTextField to input a dimension for the map. If the text field is entered,
    //          uses the stageWidth and stageHeight to construct a stage with those dimensions if their
    //          multiple is at least 3 times as big as minSize
    //          if the user's input is not valid, updates the error message to notify the user
    private JTextField makeDimensionTextField() {
        JTextField dimension = new JTextField();
        dimension.setToolTipText("Note: dimensions must be large enough to fit 3 times your army size");
        dimension.addActionListener(e -> {
            if (stageHeight.getText().matches("[1-9]\\d*")
                    && stageWidth.getText().matches("[1-9]\\d*")) {
                int height = Integer.parseInt(stageHeight.getText());
                int width = Integer.parseInt(stageWidth.getText());
                if (height * width >= minSize) {
                    GUI.getInstance().setStage(height, width);
                } else {
                    stageSizeError.setText("Your stage is too small. Please increase the stage size.");
                }
            } else {
                stageSizeError.setText("At least one of your dimension values is not valid. Please try again.");
            }
        });
        return dimension;
    }
}
