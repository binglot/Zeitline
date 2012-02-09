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

import org.Zeitline.Event.AtomicEvent;
import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.Event.TimeEvent;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Vector;

public class EventTreeModel implements TreeModel {

    protected Vector treeModelListeners = new Vector();
    protected ComplexEvent rootEvent;

    public EventTreeModel(ComplexEvent root) {
        rootEvent = root;
    } // org.Zeitline.EventTreeModel

    public void refresh() {
	fireTreeStructureChanged(rootEvent);
    } // refresh


    protected void fireTreeStructureChanged(ComplexEvent oldRoot) {

        int len = treeModelListeners.size();
	TreeModelEvent e = new TreeModelEvent(this, new Object[] {oldRoot});
        for (int i = 0; i < len; i++) {
            ((TreeModelListener)treeModelListeners.elementAt(i)).
		treeStructureChanged(e);
        }
    } // fireTreeStructureChanged

    protected void fireTreeNodesChanged(TimeEvent node, int[] indices, Object[] children) {

        int len = treeModelListeners.size();
 	TreePath path = getTreePath(node);
	TreeModelEvent e = new TreeModelEvent(this, path, indices, children);

        for (int i = 0; i < len; i++) {
            ((TreeModelListener)treeModelListeners.elementAt(i)).
		treeNodesChanged(e);
        }
    } // fireTreeNodesChanged


    protected void fireTreeNodesInserted(ComplexEvent node, int[] indices, Object[] children) {
	
        int len = treeModelListeners.size();
 	TreePath path = getTreePath(node);
	TreeModelEvent e = new TreeModelEvent(this, path, indices, children);
	
	for (int i = 0; i < len; i++) {
            ((TreeModelListener)treeModelListeners.elementAt(i)).
		treeNodesInserted(e);
        }
    } // fireTreeNodesInserted

    protected void fireTreeNodesRemoved(ComplexEvent node, int[] indices, Object[] children) {

        int len = treeModelListeners.size();
 	TreePath path = getTreePath(node);
	TreeModelEvent e = new TreeModelEvent(this, path, indices, children);

	for (int i = 0; i < len; i++) {
            ((TreeModelListener)treeModelListeners.elementAt(i)).
		treeNodesRemoved(e);
        }
    } // fireTreeNodesRemoved


    /* The following methods implement the TreeModel interface */

    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.addElement(l);
    }

    /*
     * Returns the child of parent at index index in the parent's child array.
     */
    public Object getChild(Object parent, int index) {
        ComplexEvent e = (ComplexEvent)parent;
	return e.getEventByIndex(index);
    } // getChild

    /* Returns the number of children of parent */
    public int getChildCount(Object parent) {
        ComplexEvent e = (ComplexEvent)parent;
	return e.countChildren();
    } // getChildCount

    /* Returns the index of child in parent */
    public int getIndexOfChild(Object parent, Object child) {

        ComplexEvent e = (ComplexEvent)parent;
	TimeEvent c = (TimeEvent) child;

	return e.getChildIndex(c);

    } // getIndexOfChild

    /* Returns the root of the tree */
    public Object getRoot() {
        return rootEvent;
    } // getRoot

    /* Returns true if node is a leaf */
    public boolean isLeaf(Object node) {

        TimeEvent e = (TimeEvent)node;
	if (e instanceof AtomicEvent) return true;

	ComplexEvent c = (ComplexEvent) e;
	return c.countChildren() == 0;

    } // isLeaf

    /*
     * Removes a listener previously added with addTreeModelListener().
     */
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.removeElement(l);
    } // removeTreeModelListener

    /*
      Messaged when the user has altered the value for the item 
      identified by path to newValue
    */
    public void valueForPathChanged(TreePath path, Object newValue) {

	if (!(newValue instanceof ComplexEvent))
	    return;

	ComplexEvent c = (ComplexEvent) newValue;
	ComplexEvent parent = c.getParent();
	if (parent == null) 
	    fireTreeNodesChanged(c, null, null);
	else {
	    int index = parent.getChildIndex(c);
	    fireTreeNodesChanged(parent, new int[] {index}, new Object[] {c});
	}
    } // valueForPathChanged


    /* This method returns true when the insertion of the node
       caused our parent to be changed (resort children) and
       false if not. Success of the insert is always assumed.
    */
    public boolean insertNode(ComplexEvent parent, TimeEvent toInsert) {

	if (parent.addTimeEvent(toInsert)) {
	    fireTreeStructureChanged(rootEvent);
	    return true;
	}
	else {
	    int index = parent.getChildIndex(toInsert);	    
	    fireTreeNodesInserted(parent, new int[]{index}, new Object[]{toInsert});
	    return false;
	}
    } // insertNode

    /* This method returns true when the deletion of the node caused
       our parent to be changed (resort children) or to be deleted and
       false if not. Success of the delete is always assumed.
    */
    public boolean removeNode(ComplexEvent parent, TimeEvent toRemove) {

	int index = parent.getChildIndex(toRemove);

	if (parent.removeTimeEvent(toRemove)) {
	    fireTreeStructureChanged(rootEvent);
	    return true;
	}
	else {
	    fireTreeNodesRemoved(parent, new int[]{index}, new Object[]{toRemove});
	    return false;
	}
    } // removeNode

    /* This method returns true when the deletion of the nodes caused
       our parent to be changed (resort children) or to be deleted and
       false if not. Success of the deletes is always assumed.
    */
    public boolean removeNodes(ComplexEvent parent, Object[] removals) {

	int[] indices = new int[removals.length];

	boolean parentDeleted = false;

	for (int i = 0; i < removals.length; i++)
	    if (parent.removeTimeEvent((TimeEvent)removals[i]))
		parentDeleted = true;
	
	fireTreeStructureChanged(rootEvent);	
	return parentDeleted;
    } // removeNodes

    // TODO: write insertNodes() method for insertion of multiple items at once

    public TreePath getTreePath(TimeEvent node) {
	
	Vector elements = new Vector();
	while (node != null) {
	    elements.insertElementAt(node,0);
	    node = node.getParent();
	}
	
	return new TreePath(elements.toArray());

    } // getTreePath
} // org.Zeitline.EventTreeModel
