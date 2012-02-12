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

import org.Zeitline.Event.AbstractTimeEvent;
import org.Zeitline.Event.ComplexEvent;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.tree.TreePath;

public class EventTreeModelFilter extends EventTreeModel {

    protected EventTreeModel delegate;
    protected Hashtable mappings = new Hashtable();
    protected Query query;

    public EventTreeModelFilter(EventTreeModel delegate) {
    	this(delegate, null);
    } // org.Zeitline.EventTreeModelFilter(org.Zeitline.EventTreeModel)

    public EventTreeModelFilter(EventTreeModel delegate, Query q) {
	super(null);
	rootEvent = (ComplexEvent)delegate.getRoot();
    	this.delegate = delegate;
	query = q;
    } // org.Zeitline.EventTreeModelFilter(org.Zeitline.EventTreeModel,org.Zeitline.Query)

    public void setQuery(Query q) {
	query = q;
    } // setQuery

    public void initQuery() {

	mappings.clear();
	ComplexEvent root = (ComplexEvent) delegate.getRoot();
	root.computeQuery(query);
	buildMapping(root);

    } // initQuery

    protected void buildMapping(ComplexEvent node) {

	ArrayList mapping = new ArrayList();

    	for (int z = 0; z < delegate.getChildCount(node); z++) {

	    AbstractTimeEvent t = (AbstractTimeEvent) delegate.getChild(node, z);
	    if (t.matchesQuery()) {
		mapping.add(new Integer(z));
		if (t instanceof ComplexEvent)
		    buildMapping((ComplexEvent)t);
	    }
	}

	mappings.put(node, mapping);

    } // buildMapping

    public int getChildCount(Object parent) {
       ComplexEvent e = (ComplexEvent)parent;
       ArrayList mapping = (ArrayList)(mappings.get(e));
       if (mapping == null)
	   return 0;
       else
	   return mapping.size();
    } // getChildCount

    public Object getChild(Object parent, int index) {
        ComplexEvent e = (ComplexEvent)parent;
	ArrayList mapping = (ArrayList)(mappings.get(e));
	int newIndex = ((Integer)mapping.get(index)).intValue();
	return delegate.getChild(parent, newIndex);
    } // getChild

    public int getIndexOfChild(Object parent, Object child) {

        ComplexEvent e = (ComplexEvent)parent;
	ArrayList mapping = (ArrayList)(mappings.get(e));

	int oldIndex = delegate.getIndexOfChild(parent, child);

	return mapping.indexOf(new Integer(oldIndex));

    } // getIndexOfChild

    public Object getRoot() {
        return delegate.getRoot();
    } // getRoot

    public boolean isLeaf(Object node) {
        return delegate.isLeaf(node);
    } // isLeaf

    public void valueForPathChanged(TreePath path, Object newValue) {
        delegate.valueForPathChanged(path, newValue);
    } // valueForPathChanged

    public boolean insertNode(ComplexEvent parent, AbstractTimeEvent toInsert) {

	int index, newIndex;

	ArrayList mapping = (ArrayList)(mappings.get(parent));
	boolean ret = delegate.insertNode(parent, toInsert);
	index = delegate.getIndexOfChild(parent, toInsert);

	if (toInsert instanceof ComplexEvent) {

	    ComplexEvent ce = (ComplexEvent) toInsert;
	    if (ce.computeQuery(query)) {
		buildMapping(ce);
		newIndex = insertMapping(mapping, index);
		fireTreeNodesInserted(parent, new int[]{newIndex}, new Object[]{toInsert});
	    }
	    
	} else {
	    if (query.matches(toInsert)) {
		newIndex = insertMapping(mapping, index);
		fireTreeNodesInserted(parent, new int[]{newIndex}, new Object[]{toInsert});
	    }
	    else {

		int count;
		for (count = 0; count < mapping.size(); count++)
		    if (count < ((Integer)mapping.get(count)).intValue())
			break;
		for (int i = count; i < mapping.size(); i++)
		    mapping.set(i,new Integer(((Integer)mapping.get(i)).intValue()+1));
		    
	    }
	}

	if (ret) {
	    initQuery(); // this can probably be optimized
	    fireTreeStructureChanged(rootEvent);       
	}

	return ret;

    } // insertNode

    protected int insertMapping(ArrayList mapping, int index) {
	
	int newIndex = 0;
	while (newIndex < mapping.size()) {
	    if (index <= ((Integer)mapping.get(newIndex)).intValue())
		break;
	    newIndex++;
	}

	mapping.add(newIndex, new Integer(index));
	
	for (int i = newIndex+1; i < mapping.size(); i++)
	    mapping.set(i,new Integer(((Integer)mapping.get(i)).intValue()+1));

	return newIndex;

    } // insertMapping

    public boolean removeNode(ComplexEvent parent, AbstractTimeEvent toRemove) {

	int index, mappedIndex;

	ArrayList mapping = (ArrayList)(mappings.get(parent));
	index = delegate.getIndexOfChild(parent, toRemove);
	boolean ret = delegate.removeNode(parent, toRemove);
	mappedIndex = mapping.indexOf(new Integer(index));

	if (mappedIndex > -1) {
	    mapping.remove(mappedIndex);
	    for (int i=mappedIndex; i<mapping.size();i++)
		mapping.set(i,new Integer(((Integer)mapping.get(i)).intValue()-1));

	    // TODO: check if toRemove is org.Zeitline.Event.ComplexEvent and delete its mappings
	    // and those the contained CEs (recursively).
	    // Also check if parent is "empty" now and remove parent 
	    // This may cascade up.
	    fireTreeNodesRemoved(parent, new int[]{mappedIndex}, new Object[]{toRemove});
	}

	if (ret) {
	    initQuery();
	    fireTreeStructureChanged(rootEvent);
	}

	return ret;

    } // removeNode

    public boolean removeNodes(ComplexEvent parent, Object[] removals) {

	
	// TODO: not sure if we really need to keep track of whether
	//       parent was deleted or not. We use treeStructureChanged
        //       anyway now.

	int[] indices, mappedIndices;
	boolean parentDeleted = false;
	ArrayList mapping = (ArrayList)(mappings.get(parent));
	indices = new int[removals.length];
	mappedIndices = new int[removals.length];

	for (int i = 0; i < removals.length; i++) {
	    indices[i] = delegate.getIndexOfChild(parent, removals[i]);
	    int mappedIndex = mapping.indexOf(new Integer(indices[i]));
	    mappedIndices[i] = mappedIndex;
	}

	for (int i = 0; i < removals.length; i++) {
	    indices[i] = delegate.getIndexOfChild(parent, removals[i]);
	    if (delegate.removeNode(parent, (AbstractTimeEvent)removals[i]))
		parentDeleted = true;
	}

	if (parentDeleted)
	    initQuery();
	else {
	    int count = 0;
	    
	    for (int i = mappedIndices[0]; i < mapping.size(); i++) {
		if ((count < removals.length) && (i == mappedIndices[count]))
		    count = count + 1;
		mapping.set(i, new Integer(((Integer)mapping.get(i)).intValue()-count));
	    }

	    for (int i = 0; i < removals.length; i++) {
		mapping.remove(mappedIndices[i]-i);
	    }
	}

	fireTreeStructureChanged(rootEvent);
	return parentDeleted;

    } // removeNodes

    public EventTreeModel getDelegate() {
	return delegate;
    } // getDelegate

} // org.Zeitline.EventTreeModelFilter
