package org.Zeitline.GUI;

import org.Zeitline.Zeitline;

import javax.swing.*;

public class FeelAndLook {
    private Zeitline app;
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
                final String fullName = "org.pushingpixels.substance.api.skin.Substance" + name + "LookAndFeel";

                // Only update UI on Dispatch Thread
                if (SwingUtilities.isEventDispatchThread()) {
                    setFeelAndLook(fullName);
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            setFeelAndLook(fullName);
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
            if (app != null)
                app.repaintAll();
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

    public void setApp(Zeitline app) {
        this.app = app;
    }
}
