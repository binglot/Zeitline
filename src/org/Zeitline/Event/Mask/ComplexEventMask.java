package org.Zeitline.Event.Mask; /********************************************************************

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

import org.Zeitline.Event.ComplexEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.tree.TreeModel;

public class ComplexEventMask extends JPanel implements ActionListener,DocumentListener,ChangeListener {
    private static final int FIELD_WIDTH = 15;
    private boolean contentModified = false;
    private static boolean paneIsActive = false;
    
    protected JLabel l_start;
    protected JLabel l_end;
    protected JLabel l_name;
    protected JLabel l_desc;
    protected JLabel l_children;
    protected JLabel lbl_sources_list;

    protected JLabel start_time;
    protected JLabel end_time;
    protected JTextField name;
    protected JTextArea description;
    protected JCheckBox deleteEmpty;
    protected JList lst_sources_list;
    
    protected TreeModel currentModel;

    private JLabel lbl_sources_cap;
    private JButton update_button;
    
    private ComplexEvent currentEvent;

    public ComplexEventMask() {
	super();

	JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

	c.fill = GridBagConstraints.HORIZONTAL;
	c.insets = new Insets(5, 5, 5, 5);

	l_start = new JLabel("Start time: ", JLabel.TRAILING);
        c.gridx = 0;
        c.gridy = 0;
	contentPanel.add(l_start,c);

	start_time = new JLabel();
	l_start.setLabelFor(start_time);
        c.gridx = 1;
        c.gridy = 0;
	contentPanel.add(start_time,c);

	l_end = new JLabel("End time: ", JLabel.TRAILING);
        c.gridx = 0;
        c.gridy = 1;
	contentPanel.add(l_end,c);

	end_time = new JLabel();
	l_end.setLabelFor(end_time);
        c.gridx = 1;
        c.gridy = 1;
	contentPanel.add(end_time,c);

	l_name = new JLabel("Name: ", JLabel.TRAILING);
        c.gridx = 0;
        c.gridy = 2;
	c.anchor = GridBagConstraints.PAGE_START;
	contentPanel.add(l_name,c);

	name = new JTextField(FIELD_WIDTH);
	name.getDocument().addDocumentListener(this);
	l_name.setLabelFor(name);
        c.gridx = 1;
        c.gridy = 2;
	c.anchor = GridBagConstraints.CENTER;
	c.weightx = 1;
	contentPanel.add(name,c);

	l_desc = new JLabel("Description: ", JLabel.TRAILING);
        c.gridx = 0;
        c.gridy = 3;
	c.anchor = GridBagConstraints.PAGE_START;
	c.weightx = 0;
	contentPanel.add(l_desc,c);

	description = new JTextArea(6,FIELD_WIDTH);
	description.getDocument().addDocumentListener(this);
	l_desc.setLabelFor(description);
        c.gridx = 1;
        c.gridy = 3;
	c.anchor = GridBagConstraints.CENTER;
	c.weightx = 1;
	contentPanel.add(new JScrollPane(description,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
            c);

	deleteEmpty = new JCheckBox("Delete when empty");
	deleteEmpty.addChangeListener(this);
	c.gridx = 1;
	c.gridy = 4;
	c.anchor = GridBagConstraints.PAGE_START;
	c.weightx = 0;
	contentPanel.add(deleteEmpty, c);
	    
        lbl_sources_list = new JLabel("Sources:", JLabel.TRAILING);
        c.gridx = 0;
        c.gridy = 5;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0;
        contentPanel.add(lbl_sources_list, c);
        
        lst_sources_list = new JList();
        lst_sources_list.setVisibleRowCount(5);
        c.gridx = 1;
        c.gridy = 5;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        contentPanel.add(new JScrollPane(lst_sources_list,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
            c);

	contentPanel.setAlignmentY(Component.TOP_ALIGNMENT);

	// create panel for the buttons and set action listeners
	JPanel buttonPanel = new JPanel();

	update_button = new JButton("Update");
	update_button.setActionCommand("update");
	update_button.addActionListener(this);
	buttonPanel.add(update_button);

	JButton b = new JButton("Revert");
	b.setActionCommand("revert");
	b.addActionListener(this);
	buttonPanel.add(b);

	b = new JButton("Clear");
	b.setActionCommand("clear");
	b.addActionListener(this);
	buttonPanel.add(b);

	this.setLayout(new BorderLayout());
	this.add(contentPanel, BorderLayout.PAGE_START);
	this.add(buttonPanel);
    } // org.Zeitline.Event.Mask.ComplexEventMask

    public void set(ComplexEvent e, TreeModel m) {
	this.currentModel = m;
	this.currentEvent = e;
	this.start_time.setText(e.getStartTime().toString());
	this.end_time.setText(e.getEndTime().toString());
	this.name.setText(e.getName());
	this.description.setText(e.getDescription());
        this.lst_sources_list.setListData(e.getSources());
	this.deleteEmpty.setSelected(e.getDeleteEmptyEvent());
	contentModified = false;
	update_button.setEnabled(false);
    } // set

    public boolean isModified() {
	return contentModified;
    }
    
    private boolean update() {
	    // TODO: determine what we want to enforce here, really
	    if(this.name.getText().equals("")) return false;	    
	    currentEvent.setName(this.name.getText());
	    currentEvent.setDescription(this.description.getText());
	    currentEvent.setDeleteEmptyEvent(deleteEmpty.isSelected());
	    currentModel.valueForPathChanged(null, currentEvent);
	    contentModified = false;
	    update_button.setEnabled(false);
	    return true;
    }
    
    public int checkUpdate() {
	if (paneIsActive)
		return JOptionPane.CANCEL_OPTION;
	paneIsActive = true;
	int choice = JOptionPane.showConfirmDialog(this,
		"Do you want to save your changes?",
                "Event modified",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
	if (choice == JOptionPane.YES_OPTION)
		update();
	paneIsActive = false;
	return choice;
    }
    
    public void actionPerformed(ActionEvent e) {
	if(e.getActionCommand().equals("update")) {
		update();
	}
        else if(e.getActionCommand().equals("revert")) {
	    this.set(currentEvent, currentModel);
        }
	else if(e.getActionCommand().equals("clear")) {
	    this.name.setText("");
	    this.description.setText("");
	}
    } // actionPerformed
    
    public void insertUpdate(DocumentEvent e) {
        contentModified = true;
	update_button.setEnabled(true);
    }
    public void removeUpdate(DocumentEvent e) {
        contentModified = true;
	update_button.setEnabled(true);
    }
    public void changedUpdate(DocumentEvent e) {}
    
    public void stateChanged(ChangeEvent e) {
	contentModified = true;
	update_button.setEnabled(true);
    }
    
} // class org.Zeitline.Event.Mask.ComplexEventMask
