package org.Zeitline.GUI.Graphics;

import org.Zeitline.Utils;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.net.URL;

public class IconRepository implements  IIconRepository<ImageIcon>{
    private static final String ICONS_DIR = "icons";
    private static final String ICONS_EXTENSION = ".png";


    public ImageIcon getIcon(String imageName) {
        String imgLocation = Utils.pathJoin(ICONS_DIR, imageName + ICONS_EXTENSION);
        URL imageURL = Zeitline.class.getResource(imgLocation);

        if (imageURL != null)
            return new ImageIcon(imageURL);

        System.err.println("Resource not found: " + imgLocation);
        return null;
    }
}
