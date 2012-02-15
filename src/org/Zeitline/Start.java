package org.Zeitline;

import org.Zeitline.GUI.FormGenerator;

import static javax.swing.SwingUtilities.*;

public final class Start {
    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        invokeLater(new Runnable() {
            public void run() {
                FormGenerator formGenerator = new FormGenerator();
                PluginLoader pluginLoader = new PluginLoader("filters", formGenerator);

                Zeitline zeitline = new Zeitline(pluginLoader.getPlugins());

                zeitline.createAndShowGUI();
            }
        });
    }
}
