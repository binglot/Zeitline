package org.Zeitline;

import static javax.swing.SwingUtilities.*;

public final class Start {
    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        invokeLater(new Runnable() {
            public void run() {
                Zeitline zeitline = new Zeitline();
                zeitline.createAndShowGUI();
            }
        });
    }
}
