package org.Zeitline; /********************************************************************

 This file is part of org.Zeitline.Zeitline: a forensic timeline editor

 Written by Florian Buchholz and Courtney Falk.

 Copyright (c) 2004,2005 Florian Buchholz, Courtney Falk, Purdue
 University. All rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal with the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimers.
 Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimers in the
 documentation and/or other materials provided with the distribution.
 Neither the names of Florian Buchholz, Courtney Falk, CERIAS, Purdue
 University, nor the names of its contributors may be used to endorse
 or promote products derived from this Software without specific prior
 written permission.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NON-INFRINGEMENT.  IN NO EVENT SHALL THE CONTRIBUTORS OR COPYRIGHT
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE
 SOFTWARE.

 **********************************************************************/

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

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
    } // org.Zeitline.ProgressDlg

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
    } // actionPerformed

    public JProgressBar getProgressBar() {
        return progress_bar;
    } // getProgressBar

    public void setStatus(String newStatus) {
        status.setText(newStatus);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            // must start thread first
            thread.start();
            // setting visible to true causes control to be given to the dialog
            super.setVisible(true);
        } else super.setVisible(false);
    } // setVisible
} // class org.Zeitline.ProgressDlg
