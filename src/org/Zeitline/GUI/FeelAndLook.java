package org.Zeitline.GUI;

import javax.swing.*;

public class FeelAndLook {

    private String current;

    private final String[] skins = new String[]{
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

    public void setUI(final String name) {
        for (String skin : skins) {
            // Only set skins that exist in the list.
            if (skin.equals(name)) {
                // Only update UI on Dispatch Thread
                if (SwingUtilities.isEventDispatchThread()) {
                    setFeelAndLook(name);
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            setFeelAndLook("org.pushingpixels.substance.api.skin.Substance" + name + "LookAndFeel");
                        }
                    });
                }

                return;
            }
        }
    }

    private void setFeelAndLook(String name) {
        try {
            UIManager.setLookAndFeel(name);
        } catch (ClassNotFoundException | IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public String[] getSkins() {
        return skins;
    }

    public void setWindowsUI() {
        setFeelAndLook("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    }
}
