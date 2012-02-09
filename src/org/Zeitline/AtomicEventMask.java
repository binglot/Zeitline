package org.Zeitline; /********************************************************************

This file is part of org.Zeitline.Zeitline: a forensic timeline editor

Written by Florian Buchholz and Courtney Falk.

Copyright (c) 2004-2006 Florian Buchholz, Courtney Falk, Purdue
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

import org.Zeitline.Event.AtomicEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AtomicEventMask extends JPanel {
    private final static int FIELD_WIDTH = 15;
    
    private JLabel lbl_start_val;
    private JLabel lbl_adjusted_val;
    private JLabel lbl_reported_val;
    private JLabel lbl_name_cap;
    private JTextField txtfld_name_val;
    private JLabel lbl_descr_cap;
    private JTextArea txtar_descr_val;
    private JLabel lbl_sourcename_cap, lbl_sourcename_val;
    private JLabel lbl_sourcecreated_cap, lbl_sourcecreated_val;
    private JPanel display;
    private JPanel descriptionPanel;
    
    protected AtomicEvent current;
    
    public AtomicEventMask() {
	super();

	display = new JPanel();
	descriptionPanel = new JPanel(new GridBagLayout());
	JPanel topPanel = new JPanel(new GridBagLayout());
	JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
	current = null;
	
	c.fill = GridBagConstraints.HORIZONTAL;
	c.insets = new Insets(5,5,5,5);

	c.gridx = 0;
        c.gridy = 0;
	topPanel.add(new JLabel("Time: ", JLabel.TRAILING), c);

	lbl_start_val = new JLabel();
//	lbl_start_cap.setLabelFor(lbl_start_val);
        c.gridx = 1;
        c.gridy = 0;
	topPanel.add(lbl_start_val, c);

	c.gridx = 0;
        c.gridy = 1;
	topPanel.add(new JLabel("Adjusted time: ", JLabel.TRAILING), c);

	lbl_adjusted_val = new JLabel();
        c.gridx = 1;
        c.gridy = 1;
	topPanel.add(lbl_adjusted_val, c);

	c.gridx = 0;
        c.gridy = 2;
	topPanel.add(new JLabel("Reported time: ", JLabel.TRAILING), c);

	lbl_reported_val = new JLabel();
        c.gridx = 1;
        c.gridy = 2;
	topPanel.add(lbl_reported_val, c);

	c.gridx = 0;
        c.gridy = 3;
	c.anchor = GridBagConstraints.PAGE_START;
	topPanel.add(new JLabel("Name: ", JLabel.TRAILING), c);

	txtfld_name_val = new JTextField(FIELD_WIDTH);
//	lbl_name_cap.setLabelFor(txtfld_name_val);
        c.gridx = 1;
        c.gridy = 3;
	c.weightx = 1;
	c.anchor = GridBagConstraints.CENTER;
	topPanel.add(txtfld_name_val, c);

	lbl_descr_cap = new JLabel("Description: ", JLabel.TRAILING);
        c.gridx = 0;
        c.gridy = 0;
	c.anchor = GridBagConstraints.PAGE_START;
	c.weightx = 0;
	descriptionPanel.add(lbl_descr_cap, c);

	txtar_descr_val = new JTextArea(6, FIELD_WIDTH);
	lbl_descr_cap.setLabelFor(txtar_descr_val);
        c.gridx = 1;
        c.gridy = 0;
	c.anchor = GridBagConstraints.CENTER;
	c.weightx = 1;
	descriptionPanel.add(new JScrollPane(txtar_descr_val,
				  JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),c);

        lbl_sourcename_cap = new JLabel("org.Zeitline.Source: ", JLabel.TRAILING);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0;
        bottomPanel.add(lbl_sourcename_cap, c);
        
        lbl_sourcename_val = new JLabel();
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        bottomPanel.add(lbl_sourcename_val, c);
 
        lbl_sourcecreated_cap = new JLabel("Created: ", JLabel.TRAILING);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0;
        bottomPanel.add(lbl_sourcecreated_cap, c);
        
        lbl_sourcecreated_val = new JLabel();
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        bottomPanel.add(lbl_sourcecreated_val, c);
        
	topPanel.setAlignmentY(Component.TOP_ALIGNMENT);
	bottomPanel.setAlignmentY(Component.TOP_ALIGNMENT);
	this.setLayout(new BorderLayout());
	this.add(topPanel, BorderLayout.PAGE_START);
	this.add(display, BorderLayout.WEST);
	this.add(bottomPanel, BorderLayout.PAGE_END);
    } // org.Zeitline.AtomicEventMask

    public void set(AtomicEvent e) {
        Source s = e.getSource();
	if ((current == null) || !(current.getClass().equals(e.getClass())))
		this.display.removeAll();
	this.lbl_start_val.setText(e.getStartTime().toString());
	this.lbl_adjusted_val.setText(e.getAdjustedTime().toString());
	this.lbl_reported_val.setText(e.getReportedTime().toString());
	this.txtfld_name_val.setText(e.getName());
	this.txtfld_name_val.setCaretPosition(0);
	this.txtar_descr_val.setText(e.getDescription());

        if(s != null) {
            this.lbl_sourcename_val.setText(s.toString());
            this.lbl_sourcecreated_val.setText(s.getTimestamp().toString());
        }
	if ((current == null) || !(current.getClass().equals(e.getClass()))) {
		JPanel p = e.getPanel();
		if (p != null) {
			display.add(p);
		}
		else
			display.add(descriptionPanel);
	}
	e.setPanelValues();
	current = e;
    } // set
} // class org.Zeitline.AtomicEventMask
