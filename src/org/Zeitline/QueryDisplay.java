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

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class QueryDisplay extends JPanel {

    protected JButton change, remove;
    protected JTextField text;

    public QueryDisplay(Query q, ActionListener al) {

        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        text = new JTextField(q.toString(), 1);
        text.setBackground(this.getBackground());
        text.setEditable(false);
        text.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        this.add(text);
        this.add(Box.createHorizontalGlue());

        change = new JButton(Zeitline.createNavigationIcon("edit"));
        change.setBorderPainted(false);
        change.setMargin(new Insets(0, 0, 0, 0));
        change.setActionCommand("change");
        change.addActionListener(al);
        change.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (change.isEnabled())
                    change.setBorderPainted(true);
            }

            public void mouseExited(MouseEvent e) {
                change.setBorderPainted(false);
            }
        });

        this.add(change);

        remove = new JButton(Zeitline.createNavigationIcon("cancel"));
        remove.setBorderPainted(false);
        remove.setMargin(new Insets(0, 0, 0, 0));
        remove.setActionCommand("remove");
        remove.addActionListener(al);
        remove.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (remove.isEnabled())
                    remove.setBorderPainted(true);
            }

            public void mouseExited(MouseEvent e) {
                remove.setBorderPainted(false);
            }
        });
        this.add(remove);
    } // org.Zeitline.QueryDisplay

    public void setEnabled(boolean enable) {
        text.setEnabled(enable);
        change.setEnabled(enable);
        remove.setEnabled(enable);
    } // setEnabled

    public String toString() {
        return text.getText();
    } // toString

} // class org.Zeitline.QueryDisplay
