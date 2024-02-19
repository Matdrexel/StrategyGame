package ui;

import java.util.HashMap;

public enum ImagePath {
    SWORDS_CROSSING("./data/SwordsCrossing.png"),
    BLOOD("./data/Blood.png"),
    SWORD("./data/Sword.png"),
    SHIELD("./data/Shield.png"),
    BOW("./data/Bow.png"),
    AXE("./data/Axe.png"),
    DAGGER("./data/Dagger.png"),
    NONE("");

    private static final HashMap<String, ImagePath> IMAGE_MAP_FINDER = new HashMap<>();
    private final String imagePath;

    static {
        for (ImagePath image : values()) {
            IMAGE_MAP_FINDER.put(image.imagePath, image);
        }
    }

    ImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public static ImagePath getImageValue(String image) {
        return IMAGE_MAP_FINDER.get(image);
    }
}
