package org.Zeitline.GUI;

import javax.swing.*;

public class FeelAndLook {

    private String current;

    private final String[] skins = new String[] {
            // Dark
            "Twilight",
            "Raven",
            "GraphiteAqua",
            "EmeraldDusk",
            // Bright
            "Mariner",
            "NebulaBrickWall",
            "Sahara",
    };

    public void setUI(String name) {
        try {
            // Only set skins that exist in the list.
            for(String skin: skins){
                if (skin.equals(name)){
                    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.Substance" + name + "LookAndFeel");
                    return;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public String[] getSkins() {
        return skins;
    }
}
