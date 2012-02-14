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

import org.Zeitline.Event.ComplexEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class NewComplexEventDlg
        extends JDialog
        implements ActionListener {

    private static NewComplexEventDlg dialog;
    private static ComplexEvent event = null;
    private JTextField name;
    private JTextArea description;

    /**
     * Set up and show the dialog.  The first Component argument
     * determines which frame the dialog depends on; it should be
     * a component in the dialog's controlling frame. The second
     * Component argument should be null if you want the dialog
     * to come up with its left corner in the center of the screen;
     * otherwise, it should be the component on top of which the
     * dialog should appear.
     */
    public static ComplexEvent showDialog(Component frameComp,
                                          Component locationComp,
                                          String header) {

        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new NewComplexEventDlg(frame, locationComp, header);

        dialog.setVisible(true);
        return event;
    } // showDialog

    private NewComplexEventDlg(Frame frame,
                               Component locationComp,
                               String header) {

        super(frame, header, true);

        // create and initialize the buttons.
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        final JButton okButton = new JButton("Ok");
        okButton.setActionCommand("Ok");
        okButton.addActionListener(this);
        getRootPane().setDefaultButton(okButton);

        name = new JTextField(20);
        description = new JTextArea(6, 20);

        // create a container so that we can add a title around
        // the scroll pane.  Can't add a title directly to the
        // scroll pane because its background would be white.
        // lay out the label and scroll pane from top to bottom.

        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        JLabel label = new JLabel("Name: ", JLabel.TRAILING);
        label.setLabelFor(name);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        fieldPane.add(label, c);

        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        fieldPane.add(name, c);

        label = new JLabel("Description: ", JLabel.TRAILING);
        label.setLabelFor(description);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0;
        fieldPane.add(label, c);

        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        fieldPane.add(new JScrollPane(description,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
                c);

        // lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(okButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createHorizontalGlue());

        // put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(fieldPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        pack();
        setLocationRelativeTo(locationComp);
    } // org.Zeitline.NewComplexEventDlg

    //Handle clicks on the Set and Cancel buttons.
    public void actionPerformed(ActionEvent e) {
        String s = this.name.getText();

        if ("Ok".equals(e.getActionCommand())) {
            if (s.compareTo("") == 0) {
                JOptionPane.showMessageDialog(dialog,
                        "You must specify a name.");
                return;
            }

            event = new ComplexEvent(s,
                    this.description.getText());
        } else
            event = null;

        NewComplexEventDlg.dialog.setVisible(false);
    } // actionPerformed
} // class org.Zeitline.NewComplexEventDlg
