package org.Zeitline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

public class ProgressDlg
        extends JDialog
        implements ActionListener {

    private Thread thread;
    private JLabel status;
    private JProgressBar progressBar;
    private JButton cancelButton;
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

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        pane.add(progressBar);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        pane.add(cancelButton);

        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == cancelButton) {
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
        return progressBar;
    }

    public void setStatus(String newStatus) {
        status.setText(newStatus);
    }

    public void setVisible(boolean visible) {
        // It can throw an exception if a skin is used.
        // Need to look at diff. implementations, e.g.:
        // http://docs.oracle.com/javase/tutorial/uiswing/components/progress.html
        // http://stackoverflow.com/questions/8916721/java-swing-update-label/8917565#8917565

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
