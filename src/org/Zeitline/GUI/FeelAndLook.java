package org.Zeitline.GUI;

import org.Zeitline.Zeitline;

import javax.swing.*;

public class FeelAndLook implements IFeelAndLook {
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

    @Override
    public void setUI(final String name, final Zeitline app) {
        for (String skin : skins) {
            // Only set skins that exist in the list.
            if (skin.equals(name)) {
                final String fullName = "org.pushingpixels.substance.api.skin.Substance" + name + "LookAndFeel";

                // Only update UI on Dispatch Thread
                if (SwingUtilities.isEventDispatchThread()) {
                    setFeelAndLook(fullName, app);
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            setFeelAndLook(fullName, app);
                        }
                    });
                }

                return;
            }
        }
    }



    private void setFeelAndLook(String name, Zeitline app) {
        try {
            UIManager.setLookAndFeel(name);
            if (app != null) {
                SwingUtilities.updateComponentTreeUI(app.getFrame());
            }
        } catch (ClassNotFoundException | IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getSkins() {
        return skins;
    }

    public void setWindowsUI() {
        setFeelAndLook("com.sun.java.swing.plaf.windows.WindowsLookAndFeel", null);
    }
}
