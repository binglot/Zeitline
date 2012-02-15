package org.Zeitline;

import org.Zeitline.Plugin.Input.InputFilter;
import org.Zeitline.Plugin.Input.InputPluginLoader;
import org.Zeitline.Plugin.PluginLoader;

import static javax.swing.SwingUtilities.*;

public final class Start {
    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        invokeLater(new Runnable() {
            public void run() {
                PluginLoader<InputFilter> pluginLoader = new InputPluginLoader("filters");
                Zeitline zeitline = new Zeitline(pluginLoader.getPlugins());

                zeitline.createAndShowGUI();
            }
        });
    }
}
