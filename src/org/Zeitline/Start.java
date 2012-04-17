package org.Zeitline;

import org.Zeitline.GUI.Graphics.IconRepository;
import org.Zeitline.OpenFileFilters.FiltersProvider;
import org.Zeitline.Plugin.Input.InputFilter;
import org.Zeitline.Plugin.Input.InputPluginLoader;
import org.Zeitline.Plugin.PluginLoader;

import javax.swing.*;

import static javax.swing.SwingUtilities.invokeLater;

public final class Start {
    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        invokeLater(new Runnable() {
            public void run() {
                final String inputFiltersDir = "InputFilters";
                try {
                    //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                    String name = "OfficeBlack2007";
                    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.Substance" + name + "LookAndFeel");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InstantiationException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }


                FiltersProvider openFileFilters = new FiltersProvider();
                PluginLoader<InputFilter> pluginLoader = new InputPluginLoader(inputFiltersDir);
                IconRepository iconRepository = new IconRepository();
                Zeitline zeitline = new Zeitline(openFileFilters.getFilters(),  pluginLoader.getPlugins(), iconRepository);

                zeitline.createAndShowGUI();
            }
        });
    }
}
