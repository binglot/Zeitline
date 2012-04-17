package org.Zeitline;

import org.Zeitline.GUI.Action.ImportAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProgressDlg
        extends JDialog
        implements ActionListener {

    private Thread thread;
    private JLabel status;
    private JProgressBar progress_bar;
    private JButton cancel_button;
    private StoppableRunnable runner;

    public ProgressDlg(Frame owner, String title, StoppableRunnable run) {
        super(owner, title, true);

        runner = run;
        thread = new Thread(runner);

        JPanel pane = (JPanel) getContentPane();
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        pane.setLayout(new GridLayout(3, 1));

        status = new JLabel("Adding events to the timeline", JLabel.CENTER);
        pane.add(status);

        progress_bar = new JProgressBar();
        progress_bar.setStringPainted(true);
        pane.add(progress_bar);

        cancel_button = new JButton("Cancel");
        cancel_button.addActionListener(this);
        pane.add(cancel_button);

        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == cancel_button) {
            //            thread.stop();
            /*
           if (runner instanceof org.Zeitline.Zeitline.ImportAction) {
           ((org.Zeitline.Zeitline.ImportAction)runner).stop();
           hide();
           }
           */
            runner.stop();
            //	    hide();
            setVisible(false);
        }
    }

    public JProgressBar getProgressBar() {
        return progress_bar;
    }

    public void setStatus(String newStatus) {
        status.setText(newStatus);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            // must start thread first
            thread.start();
            // setting visible to true causes control to be given to the dialog
            super.setVisible(true);
        } else {
            super.setVisible(false);
        }
    }

}
