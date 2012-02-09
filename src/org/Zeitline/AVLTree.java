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

import org.Zeitline.Event.TimeEvent;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Vector;

public class AVLTree implements Serializable{

    private AVLNode root;
    private int node_count;
    
    private AVLNode find_current = null;

    public AVLTree() {
        this.root = null;
        this.node_count = 0;
    } // org.Zeitline.AVLTree
    
    public int getNodeCount() {
        return node_count;
    } // getNodeCount
    
    public boolean add(TimeEvent event) {
	//	System.out.println("Inserting event " + event);
	if (root == null) {
	    root = new AVLNode(event);
	    node_count++;
	    return true;
	}
	else {
	    if (insert(root, new AVLNode(event))) {
		node_count++;
		return true;
	    }
	}
        return false;
    } // add
    
    public boolean remove(TimeEvent event) {

        if (delete(root, event)) {
	    node_count--;
	    return true;
	}

	return false;
       
    } // remove

    public boolean resort(TimeEvent event, Timestamp new_start) {

	//	System.out.println("Entering resort: " + new_start);

	remove(event);
	event.setStartTime(new_start);
	add(event);
	return true;

    } // resort

    public int getIndex(TimeEvent e) {
	int index = 0;
	AVLNode current = root;

	while (current != null) {

	    int comp = EventComparator.compare(e, current.getEvent());

	    if (comp == 0)
		return index + current.getLeftChildCount();

	    if (comp < 0)
		current = current.getLeftChild();
	    else {
		index = index + current.getLeftChildCount() + 1;
		current = current.getRightChild();
	    }
	}

	return -1;
    } // getIndex

    public TimeEvent getElement(int index) {
	AVLNode current = root;
	int offset = 0;

	int current_index;

	while (current != null) {

	    current_index = current.getLeftChildCount() + offset;

	    if (current_index == index)
		return current.getEvent();

	    if (current_index > index)
		current = current.getLeftChild();
	    else {
		offset = offset + current.getLeftChildCount() + 1;
		current = current.getRightChild();
	    }

	}

	return null;

    } // getElement

    private boolean insert(AVLNode insert_at, AVLNode to_insert) {

        if ((insert_at == null) || (to_insert == null)) return false;

        AVLNode current = insert_at;

        while (current != null) {

            int comp = EventComparator.compare(to_insert.getEvent(),
                                               current.getEvent());

            if (comp < 0) {
                AVLNode left = current.getLeftChild();
                if (left != null)
                    current = left;
                else {
                    current.setLeftChild(to_insert);
                    break;
                }
            }
            else if (comp > 0) {
                AVLNode right = current.getRightChild();
                if (right != null)
                    current = right;
                else {
                    current.setRightChild(to_insert);
                    break;
                }
            }
            else return false;

        }

        adjustBalance(current);

        return true;

    } // insert

    private boolean delete(AVLNode root_node, TimeEvent to_delete) {

	//	System.out.println("Delete from: " + root_node.getEvent());
	//	System.out.println("Deleting: " + to_delete);

	if((root_node == null) || (to_delete == null)) return false;
	
	AVLNode left_child, right_child, parent, current;

	current = root_node;

	while (current != null) {
	    int comp = EventComparator.compare(to_delete, current.getEvent());

	    //	    System.out.println("Comparing " + to_delete + " with " +
	    //	    	       current.getEvent() + "\n\tResult: " + comp);

	    if (comp < 0)
		current = current.getLeftChild();
	    else if (comp > 0)
		current = current.getRightChild();
	    else
		break;
	}
	if (current == null) {
	    //	    System.out.println("node not found");
	    return false;
	}

	//	System.out.println("Node found");
	
	left_child = current.getLeftChild();
	right_child = current.getRightChild();
	parent = current.getParent();


	if ((left_child != null) && (right_child != null)) {

	    //	    System.out.println("Two Children\nLeft Child: " 
	    //		       + left_child.getEvent() + "\nRight Child: "
	    //		       + right_child.getEvent());

	    AVLNode succ;

	    for (succ = right_child; succ.getLeftChild() != null;
		 succ = succ.getLeftChild());

	    //	    System.out.println("Successor Node: " + succ.getEvent());

	    current.setEvent(succ.getEvent());

	    return delete(succ, succ.getEvent());

	}

	else if ((left_child != null) && (right_child == null)) {

	    //	    System.out.println("Left child only: " + left_child.getEvent());

	    if (parent != null) {
		if (isLeftChild(current))
		    parent.setLeftChild(left_child);
		else
		    parent.setRightChild(left_child);
		
		parent.updateStats();
		adjustBalance(parent);
	    }
	    else {
		root = left_child;
		left_child.setParent(null);
	    }

	    return true;
	}

	else {

	    //	    if (right_child == null)
	    //		System.out.println("No children");
	    //	    else
	    //		System.out.println("Right child only: " + right_child.getEvent());

	    if (parent != null) {
		if (isLeftChild(current))
		    parent.setLeftChild(right_child);
		else
		    parent.setRightChild(right_child);

		parent.updateStats();
		adjustBalance(parent);
	    }
	    else {
		root = right_child;
		if (right_child != null)
		    right_child.setParent(null);
	    }

	    return true;
	}

    } // delete

    private void adjustBalance(AVLNode current) {

	int left_depth, right_depth;

	while (current != null) {
	    current.updateStats();
	    left_depth = current.getLeftDepth();
	    right_depth = current.getRightDepth();
	    if(Math.abs(left_depth - right_depth) > 1) {
		if (left_depth > right_depth) {
		    if (current.getLeftChild().getLeftDepth() >
			current.getLeftChild().getRightDepth())
			rotateLL(current);
		    else
			rotateLR(current);
		}
		else {
		    if (current.getRightChild().getLeftDepth() >
			current.getRightChild().getRightDepth())
			rotateRL(current);
		    else
			rotateRR(current);
		}
	    }
	    current = current.getParent();
	}
    } // adjustBalance

    private void rotateLL(AVLNode pivot) { 

	AVLNode left = pivot.getLeftChild();
	AVLNode pivot_parent = pivot.getParent();

	boolean isLeft = isLeftChild(pivot);

	pivot.setLeftChild(left.getRightChild());
	left.setRightChild(pivot);

	if (pivot_parent == null) {
	    this.root = left;
	    left.setParent(null);
	}
	else {
	    if (isLeft)
		pivot_parent.setLeftChild(left);
	    else
		pivot_parent.setRightChild(left);
	}
    } // rotateLL


    private void rotateLR(AVLNode pivot) {

	AVLNode left = pivot.getLeftChild();
	AVLNode grandchild = left.getRightChild();
	AVLNode pivot_parent = pivot.getParent();

	boolean isLeft = isLeftChild(pivot);

	left.setRightChild(grandchild.getLeftChild());
	grandchild.setLeftChild(left);
	pivot.setLeftChild(grandchild.getRightChild());
	grandchild.setRightChild(pivot);

	if (pivot_parent == null) {
	    this.root = grandchild;
	    grandchild.setParent(null);
	}
	else {
	    if (isLeft)
		pivot_parent.setLeftChild(grandchild);
	    else
		pivot_parent.setRightChild(grandchild);
	}


    } // rotateLR

    private void rotateRR(AVLNode pivot) { 

	AVLNode right = pivot.getRightChild();
	AVLNode pivot_parent = pivot.getParent();

	boolean isLeft = isLeftChild(pivot);

	pivot.setRightChild(right.getLeftChild());
	right.setLeftChild(pivot);

	if (pivot_parent == null) {
	    this.root = right;
	    right.setParent(null);
	}
	else {
	    if (isLeft)
		pivot_parent.setLeftChild(right);
	    else
		pivot_parent.setRightChild(right);
	}
    } // rotateRR

    private void rotateRL(AVLNode pivot) {

	AVLNode right = pivot.getRightChild();
	AVLNode grandchild = right.getLeftChild();
	AVLNode pivot_parent = pivot.getParent();

	boolean isLeft = isLeftChild(pivot);

	right.setLeftChild(grandchild.getRightChild());
	grandchild.setRightChild(right);
	pivot.setRightChild(grandchild.getLeftChild());
	grandchild.setLeftChild(pivot);

	if (pivot_parent == null) {
	    this.root = grandchild;
	    grandchild.setParent(null);
	}
	else {
	    if (isLeft)
		pivot_parent.setLeftChild(grandchild);
	    else
		pivot_parent.setRightChild(grandchild);
	}


    } // rotateRL

    private boolean isLeftChild(AVLNode n) {
	if (n == null) return false;

	AVLNode parent = n.getParent();

	if (parent == null) return false;

	return n.equals(parent.getLeftChild());

    } // isLeftChild


    public Vector getInterval(Timestamp start, Timestamp end) {

	Vector res = new Vector();
	if (root != null)
	    walkNodes(root, start, end, res);	

	return res;

    } // getInterval

    private void walkNodes(AVLNode current, Timestamp start, Timestamp end, Vector result) {

	AVLNode left, right;

	if ((start != null) && (current.getEvent().startsBefore(start))) {
	    right = current.getRightChild();
	    if (right != null)
		walkNodes(right, start, end, result);
	}
	else if ((end != null) && (current.getEvent().startsAfter(end))) {
	    left = current.getLeftChild();
	    if (left != null)
		walkNodes(left, start, end, result);
	}
	else {
	    left = current.getLeftChild();
	    if (left != null)
		walkNodes(left, start, end, result);

	    result.add(current.getEvent());

	    right = current.getRightChild();
	    if (right != null)
		walkNodes(right, start, end, result);
	}

    } // walkNodes

    public void printSubtree(AVLNode r, String indent) {

	if (r == null) return;

	System.out.println(indent + "Count: " + (r.getChildCount() + 1));
	System.out.println(indent + "Node: " + r.getEvent());
	System.out.println(indent + "Left child:");
	printSubtree(r.getLeftChild(), indent + " ");
	System.out.println(indent + "Right child:");
	printSubtree(r.getRightChild(), indent + " ");

    } // printSubtree

    public Timestamp getMinStartTime() {

	if (root == null) return null;

	AVLNode current;

	for (current = root; current.getLeftChild() != null; 
	     current = current.getLeftChild());

	return current.getEvent().getStartTime();

    } // getMinStartTime

    public Timestamp getMaxStartTime() {

	if (root == null) return null;

	AVLNode current;

	for (current = root; current.getRightChild() != null; 
	     current = current.getRightChild());

	return current.getEvent().getStartTime();

    } // getMaxStartTime

    public Timestamp getMaxEndTime() {

	if (root == null) return null;

	AVLNode current;

	for (current = root; current.getRightChild() != null; 
	     current = current.getRightChild());

	return current.getEvent().getEndTime();

    } // getMaxEndTime

} // class org.Zeitline.AVLTree

class AVLNode implements Serializable {
    private TimeEvent event;
    private AVLNode parent, left_child, right_child;
    private int child_count, depth;
    
    public AVLNode(TimeEvent event, AVLNode parent) {
        this.event = event;
        this.parent = parent;
        this.left_child = null;
        this.right_child = null;
        this.child_count = 0;
        this.depth = 0;
    } // org.Zeitline.AVLNode(org.Zeitline.Event.TimeEvent,org.Zeitline.AVLNode)
    
    public AVLNode(TimeEvent event) {
        this(event, null);
    } // org.Zeitline.AVLNode(org.Zeitline.Event.TimeEvent)
    
    public TimeEvent getEvent() {
        return event;
    } // getEvent
    
    public void setEvent(TimeEvent e) {
	event = e;
    } // setEvent

    public AVLNode getParent() {
        return parent;
    } // getParent
    
    public void setParent(AVLNode parent) {
        this.parent = parent;
    } // setParent
    
    public AVLNode getLeftChild() {
        return left_child;
    } // getLeftChild
    
    public void setLeftChild(AVLNode child) {
        this.left_child = child;
	if (child != null)
	    child.setParent(this);
	updateStats();
    } // setLeftChild
    
    public AVLNode getRightChild() {
        return right_child;
    } // getRightChild
    
    public void setRightChild(AVLNode child) {
        this.right_child = child;
	if (child != null)
	    child.setParent(this);
	updateStats();
    } // setRightChild
    
    public int getChildCount() {
        return child_count;
    } // getChildCount
    
    public void setChildCount(int child_count) {
        this.child_count = child_count;
    } // setChildCounrt

    public int getLeftChildCount() {
	if (this.left_child != null)
	    return left_child.getChildCount() + 1;
	else
	    return 0;
    } // getLeftChildCount
    
    public int getRightChildCount() {
	if (this.right_child != null)
	    return right_child.getChildCount() + 1;
	else
	    return 0;
    } // getRightChildCount
    
    public int getDepth() {
        return depth;
    } // getDepth
    
    public void setDepth(int depth) {
        this.depth = depth;
    } // setDepth
    
    public int getLeftDepth() {
	if (this.left_child != null)
	    return left_child.getDepth() + 1;
	else
	    return 0;
    } // getLeftDepth
		
    public int getRightDepth() {
	if (this.right_child != null)
	    return right_child.getDepth() + 1;
	else
	    return 0;
    } // getRightDepth
		
    public void updateStats() {
	child_count = getLeftChildCount() + getRightChildCount();
	depth = Math.max(getLeftDepth(), getRightDepth());
    } // updateStats

    public AVLNode succ() {

	AVLNode ret;

	if (right_child != null) {
	    ret = right_child;
	    while (ret.getLeftChild() != null)
		ret = ret.getLeftChild();
	    
	    return ret;
	}

	ret = parent;
	while (ret != null) {
	    if (EventComparator.compare(event, ret.getEvent()) < 0)
		return ret;
	    ret = ret.getParent();
	}
	
	return ret;

    } // succ

    public AVLNode pred() {

	AVLNode ret;

	if (left_child != null) {
	    ret = left_child;
	    while (ret.getRightChild() != null)
		ret = ret.getRightChild();
	    
	    return ret;
	}

	ret = parent;

	while (ret != null) {
	    if (EventComparator.compare(event, ret.getEvent()) > 0)
		return ret;
	    ret = ret.getParent();
	}
	
	return ret;

    } // pred

    public static void delete(AVLNode delete_from, TimeEvent to_delete) {
	return;
    } // delete

} // class org.Zeitline.AVLNode
