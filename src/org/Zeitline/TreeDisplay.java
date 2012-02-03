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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

public class TreeDisplay extends JPanel implements ActionListener {

    protected static final int ACTIVE_BORDER_WIDTH = 2;
    protected static Border activeBorder;
    protected EventTree tree;
    protected JPanel query_pane;
    protected JScrollPane scrollPane;
    protected Stack query_displays;

    public TreeDisplay(EventTree et) {

        // call JPanel constructor
	super();

        // initialize local variables
        if(activeBorder == null)
            activeBorder = BorderFactory.createLineBorder(Color.blue,
							  ACTIVE_BORDER_WIDTH);
        
	tree = et;
	tree.setDisplay(this);
	query_displays = new Stack();

	query_pane = new JPanel();
	query_pane.setLayout(new BoxLayout(query_pane, BoxLayout.PAGE_AXIS));
	
	scrollPane = new JScrollPane(query_pane);
	scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // initialize layout of dialog and add components
	this.setLayout(new BorderLayout());

	this.add(scrollPane, BorderLayout.PAGE_START);

	JScrollPane s = new JScrollPane(tree);
	s.getHorizontalScrollBar().setFocusable(false);
	s.getVerticalScrollBar().setFocusable(false);

	this.add(s);

    } // org.Zeitline.TreeDisplay

    public EventTree getTree() {
	return tree;
    } // getTree

    public void addQuery(Query q) {
	if (! query_displays.empty()) {
	    QueryDisplay qd = (QueryDisplay) query_displays.peek();
            qd.setEnabled(false);
	}
	else
	    scrollPane.setVisible(true);

	QueryDisplay newQuery = new QueryDisplay(q, this);
	tree.addFilter(q);
	
	Dimension query_size = scrollPane.getSize(null);
	double max_height = this.getSize(null).getHeight();
	if (query_size.getHeight() * 8 > max_height) {
		Dimension new_size = new Dimension();
		new_size.setSize(query_size.getWidth(), max_height / 8);
		scrollPane.setSize(new_size);
	}
	
	query_displays.push(newQuery);
	query_pane.add(newQuery);
	this.validate();

    } // addQuery

    public void removeQuery() {
	if(query_displays.empty()) return;

	QueryDisplay qd = (QueryDisplay) query_displays.pop();
	query_pane.remove(qd);
	this.validate();
	
	if (! query_displays.empty()) {
	    QueryDisplay current = (QueryDisplay) query_displays.peek();
            current.setEnabled(true);
	}
	else {
	    scrollPane.setVisible(false);
	    this.validate();
	}
        
	tree.removeCurrentFilter();

    } // removeQuery

    public void showBorder() {
	this.setBorder(activeBorder);
    } // showBorder

    public void hideBorder() {
	this.setBorder(null);
    } // hideBorder

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("change")) {
	    Query newQuery = NewQueryDlg.showDialog(this,
		this,
                tree.getStartTime(),
                tree.getMaxStartTime(),
                tree.getActiveQuery());

	    if(newQuery == null) return;

	    removeQuery();
	    addQuery(newQuery);
	}
        else if (e.getActionCommand().equals("remove"))
	    removeQuery();

    } // actionPerformed

} // class org.Zeitline.TreeDisplay
