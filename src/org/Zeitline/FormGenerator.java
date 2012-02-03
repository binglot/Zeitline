package org.Zeitline; /********************************************************************

This file is part of org.Zeitline.Zeitline: a forensic timeline editor

Written by Florian Buchholz.

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

import java.util.Enumeration;
import java.util.Vector;

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

public class FormGenerator {

    private FormGenerator() {
	// we do nothing here to avoid instantiation of the class
    }
	
    public static JPanel createForm(Vector items) {

	JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

	int ypos = 0;
	c.fill = GridBagConstraints.HORIZONTAL;
	c.insets = new Insets(5,5,5,5);
	
	
	for (Enumeration itemList = items.elements(); itemList.hasMoreElements();) {
		c.gridx = 0;
		c.gridy = ypos;
		c.anchor = GridBagConstraints.PAGE_START;
		FormItem formItem = (FormItem) (itemList.nextElement());
		JLabel label = new JLabel(formItem.getLabelText(), JLabel.TRAILING);
		contentPanel.add(label, c);
		
		// TODO: consider c.weight
		c.gridx = 1;
		c.anchor = GridBagConstraints.CENTER;
		Component comp = formItem.getComponent();
		label.setLabelFor(comp);
		// TODO consider flag for scrollbar
		contentPanel.add(comp, c);
		ypos++;
	}
	
	return contentPanel;
    }
    
    public static FormItem getFormItem(String l, Component c) {
	return new FormItem(l, c);
    }
    
    private static class FormItem {
	
	protected String labelText;
	protected Component comp;
		
	FormItem(String l, Component c) {
		labelText = l;
		comp = c;
	}
	
	public String getLabelText() {
		return labelText;
	}
	
	public Component getComponent() {
		return comp;
	}
		
    }
	
} // class org.Zeitline.FormGenerator
