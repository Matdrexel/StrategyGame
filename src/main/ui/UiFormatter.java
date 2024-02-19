package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

import static java.lang.Math.max;

// Class to create common components used in the GUI
public abstract class UiFormatter {

    public static final Color PLAYER1_UNIT = Color.GREEN;
    public static final Color PLAYER2_UNIT = Color.RED;
    public static final Color BLANK = null;
    public static final Color FAR_GOOD = new Color(211, 248, 211);
    public static final Color CLOSE_GOOD = new Color(179, 220, 92);
    public static final Color FAR_EVIL = new Color(255, 179, 178);
    public static final Color CLOSE_EVIL = new Color(255, 104, 101);
    public static final Color BABY_BLUE = new Color(157, 217, 243);
    public static final Color DARK_GREEN = new Color(0,204,0);
    public static final ImageIcon splashImage = makeResizableImage(ImagePath.SWORDS_CROSSING, 350, 350);
    public static final ImageIcon bloodImage = makeResizableImage(ImagePath.BLOOD, 50, 50);

    // EFFECTS: creates an image with dimensions width x height based on the image located
    //          at imagePath
    public static ImageIcon makeResizableImage(ImagePath imagePath, int width, int height) {
        ImageIcon originalImage = new ImageIcon(imagePath.getImagePath());
        Image image = originalImage.getImage();
        return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    // EFFECTS: creates an image with dimensions 40 x 40
    public static ImageIcon makeResizableImageForBattle(ImagePath imagePath) {
        return makeResizableImage(imagePath, 40, 40);
    }

    // EFFECTS: creates a default button with no text or image
    public static JButton makeDefaultButton() {
        return makeDefaultButton(null, (ImageIcon) null);
    }

    // EFFECTS: creates a default button with the specified text and no image
    public static JButton makeDefaultButton(String txt) {
        return makeDefaultButton(txt, (ImageIcon) null);
    }

    // EFFECTS: creates a default button with the specified text, and the associated resizeable image for battl
    //          with the image path
    public static JButton makeDefaultButton(String txt, ImagePath imgPath) {
        return makeDefaultButton(txt, makeResizableImageForBattle(imgPath));
    }

    // EFFECTS: constructs a not focusable button with the given text and image icon
    public static JButton makeDefaultButton(String txt, ImageIcon img) {
        JButton button = new JButton(txt, img);
        button.setFocusable(false);
        return button;
    }

    // EFFECTS: creates a GridBagConstraints with x as its x-position, y as its y-position,
    //          wx as its width weight, wy as its height weight, and 1 as its fill
    //          if width/height is -100, then uses the default grid-width/grid-height, else
    public static GridBagConstraints makeGBC(int x, int y, int width, int height, int wx, int wy) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        if (width != -100) {
            gbc.gridwidth = width;
        }
        if (height != -100) {
            gbc.gridheight = height;
        }
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = wx;
        gbc.weighty = wy;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        return gbc;
    }

    // EFFECTS: creates a formatted JLabel with the given text and the splash screen image
    public static JLabel makeSplashLabel(String message, Color background, Color textColour) {
        JLabel splashScreen = new JLabel(splashImage);
        splashScreen.setBackground(background);
        splashScreen.setForeground(textColour);
        splashScreen.setText(message);
        splashScreen.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        splashScreen.setVerticalTextPosition(JLabel.TOP);
        splashScreen.setHorizontalTextPosition(JLabel.CENTER);
        splashScreen.setOpaque(true);
        return splashScreen;
    }

    // EFFECTS: creates a JLabel with red font that can be used to notify the user if their
    //          input values are not valid
    public static JLabel makeErrorLabel() {
        JLabel error = new JLabel();
        error.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        error.setForeground(Color.RED);
        return error;
    }

    // Inspiration for BasicProgressBarUI found from
    // https://forums.oracle.com/ords/apexds/post/text-color-in-jprogressbar-2326
    //
    // EFFECTS: creates a JProgressBar with stat as its value and max as its maximum with
    //          stat displayed on it
    public static JProgressBar makeStatBar(int stat, int max) {
        JProgressBar statBar = new JProgressBar();
        statBar.setMaximum(max(max,stat));
        statBar.setValue(stat);
        statBar.setString(Integer.toString(stat));
        statBar.setStringPainted(true);
        statBar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        statBar.setForeground(DARK_GREEN);
        statBar.setBackground(Color.WHITE);
        BasicProgressBarUI ui = new BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() {
                return DARK_GREEN;
            }

            @Override
            protected Color getSelectionForeground() {
                return Color.WHITE;
            }
        };
        statBar.setUI(ui);
        return statBar;
    }
}
