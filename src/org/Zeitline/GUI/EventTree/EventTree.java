package org.Zeitline.GUI.EventTree; /********************************************************************

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

import org.Zeitline.*;
import org.Zeitline.Event.AbstractTimeEvent;
import org.Zeitline.Event.ComplexEvent;

import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.Zeitline.Timestamp.ITimestamp;

import java.text.NumberFormat;
import java.util.*;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Class for managing events in a JTree component. EventTrees use
 * the org.Zeitline.GUI.EventTree.EventTreeModel or the org.Zeitline.GUI.EventTree.EventTreeModelFilter classes for its
 * TreeModel. Drag and drop support and cut and paste support for
 * events is achieved via the org.Zeitline.TimeEventTransferHandler class. The
 * selection model is set to <tt> DISCONTIGUOUS_TREE_SELECTION </tt>.
 * The JTree cells are rendered by the org.Zeitline.GUI.EventTree.EventTreeCellRenderer class.
 *
 * @see EventTreeCellRenderer
 * @see EventTreeModel
 * @see EventTreeModelFilter
 * @see org.Zeitline.TimeEventTransferHandler
 */

public class EventTree extends JTree implements TreeSelectionListener {

    /**
     * The original data model for the tree. This variable is used
     * when filter models (see {@link EventTreeModelFilter
     * org.Zeitline.GUI.EventTree.EventTreeModelFilter}) are active and we need to access the
     * true model or revert back to the original one.
     */
    protected EventTreeModel origModel;

    /**
     * The transferable variable used for drag and drop and cut and paste.
     */
    protected TransferableEvent transferable;

    /**
     * The {@link org.Zeitline.TreeDisplay org.Zeitline.TreeDisplay} associated with this tree.
     */
    protected TreeDisplay display = null;

    /**
     * A stack containing all the {@link org.Zeitline.Query org.Zeitline.Query} objects that
     * are active inputFilters for the tree.
     */
    protected Stack queryStack;

    /**
     * Insets used for auto scrolling. The boundaries specified in the
     * inset are those when the tree starts to scroll when the mouse
     * enters the borders and scrolling is possible. Current values
     * are 40 for top/bottom and 8 for left/right.
     */
    protected Insets autoscrollInsets = new Insets(40, 8, 40, 8);

    /**
     * Variable to temporarily remember a mouse event. The overridden
     * {@link #processMouseEvent processMouseEvent()} method in this class
     * needs to delay forwarding certain events to certain event listeners.
     *
     * @see #processMouseEvent
     */
    protected MouseEvent delayedEvent = null;

    /**
     * The list of MouseEventListeners that still need to process a
     * delayed event.
     *
     * @see #processMouseEvent
     */
    protected Vector delayedListeners = new Vector();

    /**
     * Constant for displaying events. <tt>DISPLAY_ALL</tt> is
     * currently equivalent to <tt> DISPLAY_TIME </tt>.
     */
    public static final int DISPLAY_ALL = 0;

    /**
     * Constant for displaying events. For <tt>DISPLAY_TIME</tt>,
     * the full time information (as defined by <tt> Timestamp.toString()</tt>)
     * plus the event name are displayed.
     */
    public static final int DISPLAY_TIME = 1;

    /**
     * Constant for displaying events. For <tt>DISPLAY_HMS</tt>, the
     * time in <tt>hh:mm:ss</tt> format plus the event name are displayed.
     */
    public static final int DISPLAY_HMS = 2;

    /**
     * The current display mode for the events in the tree.
     */
    protected static int display_mode = DISPLAY_ALL;

    /**
     * Returns a new org.Zeitline.GUI.EventTree.EventTree with root <tt> root </tt>.
     *
     * @param root the root node of the tree
     */
    public EventTree(ComplexEvent root) {

        super(new EventTreeModel(root));

        queryStack = new Stack();

        origModel = (EventTreeModel) treeModel;

        setLargeModel(true);

        getSelectionModel().setSelectionMode
                (TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        /* old drag and drop enabling
          setTransferHandler(new EventTransferHandler());
          setDragEnabled(true);
      */

        setTransferHandler(new TimeEventTransferHandler(this));

        setFont(new Font("Monospaced", Font.PLAIN, 12));
        setCellRenderer(new EventTreeCellRenderer());

        /* this needs to be commented until drag & drop for
         JTabs is implemented.
      */
        //	setRootVisible(false);

        /* if we uncomment this, then the selection performance
         goes down the drain.
         TODO: try to find solution to enforce our selection
               policy
      */
        //	addTreeSelectionListener(this);

    } // org.Zeitline.GUI.EventTree.EventTree

    /**
     * Overrides <tt> java.awt.Component</tt>'s <tt>
     * processMouseEvent()</tt> method. This is done to recognize a
     * drag and drop drag gesture when left-clicking
     * (<tt>BUTTON1</tt>) on an event and immediately starting the
     * drag and for not losing the selection of multiple events when
     * starting a drag. For this purpose, when we receive a <tt>
     * MOUSE_PRESSED </tt> mouse event for <tt> BUTTON1_MASK </tt>, we
     * only invoke the <tt> mousePressed() </tt> method of those
     * listeners that are of the DragGestureRecognizer class. We
     * further remember the event in the {@link #delayedEvent
     * delayedEvent} field and those listeners that are not of the
     * DragGestureRecognizer class in the {@link #delayedListeners
     * delayedListeners} Vector.  when we receive a <tt>
     * MOUSE_RELEASED </tt> event, we then invoke the <tt>
     * mousePressed() </tt> method for the delayed listeners.  In any
     * case, at the end we invoke <tt> super.processMouseEvent()</tt>.
     * <p> This method is invoked automatically when mouse events occur
     * in the JTree.
     *
     * @param e the mouse event to be processed
     */
    protected void processMouseEvent(MouseEvent e) {

        if ((e.getID() == MouseEvent.MOUSE_PRESSED) &&
                (e.getModifiers() == MouseEvent.BUTTON1_MASK)) {

            TreePath clickedPath = getPathForLocation(e.getX(), e.getY());

            if ((clickedPath != null) && isPathSelected(clickedPath)) {
                MouseListener[] listeners = getMouseListeners();
                delayedEvent = e;
                delayedListeners.clear();
                for (int i = 0; i < listeners.length; i++) {
                    if (listeners[i] instanceof DragGestureRecognizer) {
                        listeners[i].mousePressed(e);
                    } else
                        delayedListeners.add(listeners[i]);
                }
                return;
            }
        } else if ((e.getID() == MouseEvent.MOUSE_RELEASED) &&
                (delayedEvent != null)) {
            for (Enumeration listeners = delayedListeners.elements();
                 listeners.hasMoreElements(); )
                ((MouseListener) listeners.nextElement()).mousePressed(e);
            delayedEvent = null;
        }

        super.processMouseEvent(e);

    } // processMouseEvent

    /**
     * Adds a new view filter on top of the original model or any
     * existing inputFilters. A new {@link EventTreeModelFilter
     * org.Zeitline.GUI.EventTree.EventTreeModelFilter} class is created with the org.Zeitline.Query and set
     * as the new TreeModel for the tree. Because expanded paths
     * are not remembered when a TreeModel is changed, we remember
     * the expanded paths before activating the new model and then
     * expand them again afterwards.
     *
     * @param q org.Zeitline.Query object that determines which events are filtered
     */
    public void addFilter(Query q) {

        EventTreeModelFilter f = new EventTreeModelFilter((EventTreeModel) treeModel, q);
        f.initQuery();

        Enumeration descendants = getExpandedDescendants(new TreePath(getModel().getRoot()));

        setModel(f);

        while (descendants.hasMoreElements()) {
            TreePath tp = (TreePath) descendants.nextElement();
            expandPath(tp);
        }

        queryStack.push(q);

    } // addFilter

    /**
     * Removes the active {@link EventTreeModelFilter
     * org.Zeitline.GUI.EventTree.EventTreeModelFilter} TreeFilter class and replaces it with
     * that model's delegate. If no <tt> org.Zeitline.GUI.EventTree.EventTreeModelFilter </tt> is
     * currently active, the method has no effect.  Because expanded
     * paths are not remembered when a TreeModel is changed, we
     * remember the expanded paths before activating the new model and
     * then expand them again afterwards.
     *
     * @see EventTreeModelFilter
     */
    public void removeCurrentFilter() {

        TreeModel m = getModel();
        if (m instanceof EventTreeModelFilter) {
            EventTreeModelFilter fm = (EventTreeModelFilter) m;
            Enumeration descendants = getExpandedDescendants(new TreePath(getModel().getRoot()));
            setModel(fm.getDelegate());
            queryStack.pop();
            while (descendants.hasMoreElements()) {
                TreePath tp = (TreePath) descendants.nextElement();
                expandPath(tp);
            }
        }

    } // removeCurrentFilter


    /**
     * Returns the active filter org.Zeitline.Query object.
     *
     * @return the active filter org.Zeitline.Query object, or <tt> null </tt> if no
     *         filter is active.
     */
    public Query getActiveQuery() {
        return (Query) queryStack.peek();
    } // getActiveQuery

    /**
     * Clears all inputFilters and reverts to the original TreeModel.
     */
    public void clearFilters() {
        setModel(origModel);
        queryStack.clear();
    } // clearFilter

    /**
     * Determines if the tree contains no events.
     *
     * @return <tt>true</tt> if the tree contains no events;
     *         <tt>false</tt> otherwise
     */
    public boolean isEmpty() {
        return (((ComplexEvent) (getModel().getRoot())).countChildren() == 0);
    } // isEmpty

    /**
     * Moves the selected events from the tree to the specified
     * target.  First, a Hashtable for each parent node is created
     * that contains selected nodes (which are to be moved). The
     * selected nodes of each parent node are kept in sorted order
     * (with a TreeSet data structure). Once all selected events are
     * associated with their parent, for each parent the events are
     * removed from the tree with the {@link EventTreeModel
     * org.Zeitline.GUI.EventTree.EventTreeModel}'s {@link EventTreeModel#removeNodes
     * removeNodes()} method. They are then inserted either directly
     * into the target <tt>org.Zeitline.Event.ComplexEvent</tt> or through the target
     * model's {@link EventTreeModel#insertNode insertNode()} method.
     *
     * @param target       the target event to which the selected events are moved
     * @param target_model the <tt> org.Zeitline.GUI.EventTree.EventTreeModel</tt> of the target event.
     *                     If this is <tt>null</tt>, the events are inserted directly into
     *                     <tt>target</tt>, otherwise they are inserted through the model.
     */
    public void moveSelected(ComplexEvent target, EventTreeModel target_model) {

        ComplexEvent parent;
        AbstractTimeEvent node;

        Hashtable sortedSelections = new Hashtable();
        TreePath[] paths = getSelectionPaths();

        for (int i = 0; i < paths.length; i++) {
            node = (AbstractTimeEvent) paths[i].getLastPathComponent();
            //	    parent = (org.Zeitline.Event.ComplexEvent)paths[i].getParentPath().getLastPathComponent();
            parent = node.getParent();
            TreeSet nodes = (TreeSet) sortedSelections.get(parent);
            if (nodes == null) {
                nodes = new TreeSet(new TimeEventComparator());
                sortedSelections.put(parent, nodes);
            }
            nodes.add(node);
        }

        for (Enumeration keylist = sortedSelections.keys();
             keylist.hasMoreElements(); ) {

            parent = (ComplexEvent) keylist.nextElement();
            TreeSet children = (TreeSet) sortedSelections.get(parent);

            Object[] childrenArray = children.toArray();

            ((EventTreeModel) getModel()).removeNodes(parent, childrenArray);

            for (int i = 0; i < childrenArray.length; i++) {
                node = (AbstractTimeEvent) childrenArray[i];
                if (target_model != null)
                    target_model.insertNode(target, node);
                else
                    target.addTimeEvent(node);
            }
        }
    } // moveSelected

    /**
     * Returns a String representation of the tree. This is the currently
     * the <tt>toString()</tt> value of the root node of the tree.
     *
     * @return the String representation of the root node
     */
    public String toString() {
        return getModel().getRoot().toString();
    } // toString

    /**
     * Scrolls the tree if necessary. If the current cursor location
     * lies outside the tree's rectangle minus the {@link
     * #autoscrollInsets autoscrollInsets}, then the tree is scrolled
     * to the rectangle that has the cursor location as its center and
     * the dimensions of the autoscroll insets.
     * <p/>
     * <p> This method is called by the {@link
     * TimeEventTransferHandler org.Zeitline.TimeEventTransferHandler} class for
     * auto scrolling during drag and drop operations.
     *
     * @param cursorLocation the coordinates of the current cursor location
     */
    public void autoscroll(Point cursorLocation) {
        //	Insets insets = getAutoscrollInsets();
        Insets insets = autoscrollInsets;
        Rectangle outer = getVisibleRect();
        Rectangle inner = new Rectangle(outer.x + insets.left,
                outer.y + insets.top,
                outer.width - (insets.left + insets.right),
                outer.height - (insets.top + insets.bottom));
        if (!inner.contains(cursorLocation)) {
            Rectangle scrollRect = new Rectangle(cursorLocation.x - insets.left,
                    cursorLocation.y - insets.top,
                    insets.left + insets.right,
                    insets.top + insets.bottom);
            scrollRectToVisible(scrollRect);
        }
    } // autoscroll

    /*    
    public Insets getAutoscrollInsets()  {
	return (autoscrollInsets);
    } // getAutoScrollInsets
    */

    /**
     * Sets the <tt>org.Zeitline.TreeDisplay</tt> for the tree.
     *
     * @param td the new <tt>org.Zeitline.TreeDisplay</tt> object
     * @see TreeDisplay
     */
    public void setDisplay(TreeDisplay td) {
        display = td;
    } // setDisplay

    /**
     * Returns the current <tt>org.Zeitline.TreeDisplay</tt> for this tree.
     *
     * @return the current <tt>org.Zeitline.TreeDisplay</tt>
     * @see TreeDisplay
     */
    public TreeDisplay getDisplay() {
        return display;
    } // getDisplay

    /**
     * Returns the start time of the root event
     *
     * @return the start time of the root event
     */
    public ITimestamp getStartTime() {
        return ((ComplexEvent) getModel().getRoot()).getStartTime();
    } // getStartTime

    /**
     * Returns the latest start time of an event contained in the tree.
     *
     * @return the latest start time of an event
     */
    public ITimestamp getMaxStartTime() {
        return ((ComplexEvent) getModel().getRoot()).getMaxStartTime();
    } // getMaxStartTime

    /**
     * Returns the lowest common parent event of the topmost selected
     * event(s) in the hierarchy. If more than one selected event is
     * at the same level in the selection hierarchy, then their lowest
     * common ancestor is returned.
     * <p/>
     * <p> This is used in org.Zeitline.Zeitline's
     * {@link org.Zeitline.GUI.Action.CreateFromAction CreateFromAction} to determine
     * where to insert the new <tt>org.Zeitline.Event.ComplexEvent</tt>.
     *
     * @return the lowest common parent of the topmost selected event(s)
     */
    public ComplexEvent getTopSelectionParent() {

        if (getSelectionCount() == 0)
            return null;

        ComplexEvent current = null;

        TreePath[] selections = getSelectionPaths();

        for (int i = 0; i < selections.length; i++) {

            AbstractTimeEvent node = (AbstractTimeEvent) selections[i].getLastPathComponent();

            if (node == (AbstractTimeEvent) getModel().getRoot())
                return (ComplexEvent) node;

            ComplexEvent parent = node.getParent();

            if (parent == (ComplexEvent) getModel().getRoot())
                return parent;

            if (current == null) {
                current = parent;
                continue;
            }

            if (isDescendantOf((AbstractTimeEvent) parent, current))
                continue;

            if (current.getParent() == parent.getParent())
                current = current.getParent();
            else
                current = parent;

        }

        return current;

    } // getTopSelectionParent

    /**
     * Determines is a <tt>org.Zeitline.Event.AbstractTimeEvent</tt> is a descendant of a
     * <tt>org.Zeitline.Event.ComplexEvent</tt>.
     * <p/>
     * <p> Used by {@link #getTopSelectionParent getTopSelectionParent}.
     *
     * @param node     the potential descendant <tt>org.Zeitline.Event.AbstractTimeEvent</tt>
     * @param ancestor the event to compare against for ancestry
     */
    protected boolean isDescendantOf(AbstractTimeEvent node, ComplexEvent ancestor) {

        for (AbstractTimeEvent temp = node; temp != null; temp = temp.getParent())
            if (temp == ancestor)
                return true;

        return false;
    } // isDescendantOf

    /**
     * Implementation of the <tt>TreeSelectionListener</tt> interface.
     */
    public void valueChanged(TreeSelectionEvent e) {

        //System.out.println("Tree selection changed");

        TreePath[] paths = e.getPaths();

        //	System.out.println("Got paths");

        TreePath lastParentPath = null;
        TreePath parentPath;

        for (int i = 0; i < paths.length; i++) {
            if (e.isAddedPath(paths[i])) {
                if (paths[i].getLastPathComponent() instanceof ComplexEvent)
                    removeDescendantSelectedPaths(paths[i], false);
                parentPath = paths[i].getParentPath();
                if (parentPath.equals(lastParentPath))
                    continue;
                for (TreePath temp = parentPath; temp != null;
                     temp = temp.getParentPath())
                    if (isPathSelected(temp)) {
                        removeSelectionPath(paths[i]);
                        return;
                    }
            }
        }
    } // valueChanged

    public String convertValueToText(Object value,
                                     boolean selected,
                                     boolean expanded,
                                     boolean leaf,
                                     int row,
                                     boolean hasFocus) {
        AbstractTimeEvent te;

        try {
            te = (AbstractTimeEvent) value;
        } catch (ClassCastException ce) {
            return value.toString();
        }

        String name = te.getName();

        if (te.getParent() == null)
            return name;

        if (display_mode == DISPLAY_ALL)
            return te.getStartTime().toString() + " " + name;

        if (display_mode == DISPLAY_TIME)
            return te.getStartTime().toString() + " " + name;

        if (display_mode == DISPLAY_HMS) {

            Calendar c = Calendar.getInstance();
            //
            // There might be a problem in the future with this casting!
            // TODO: Add a type check.
            //
            c.setTime((Date) te.getStartTime());
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumIntegerDigits(2);

            return nf.format(c.get(Calendar.HOUR_OF_DAY)) + ":" +
                    nf.format(c.get(Calendar.MINUTE)) + ":" +
                    nf.format(c.get(Calendar.SECOND)) + " " + name;
        }

        return name;
    } // convertValueToText

    public static void setDisplayMode(int mode) {
        display_mode = mode;
    } // setDisplayMode

    public void saveComplexEvents(ObjectOutputStream out_stream) {
        // make sure that the ObjectOutputStream is instantiated
        if (out_stream == null) return;

        try {
            // write the root of the model to the ObjectOutputStream
            out_stream.writeObject(origModel.getRoot());
        } catch (IOException io_excep) {
        }
    } // saveComplexEvents

    public void centerEvent(AbstractTimeEvent event) {

        TreePath path = origModel.getTreePath(event);
        scrollPathToVisible(path);
        centerPath(path);

    } // centerEvent

    protected void centerPath(TreePath path) {

        Rectangle r = getPathBounds(path);

        int from = r.y;
        int to = r.y + r.height;

        Rectangle bounds = getBounds();
        Insets i = new Insets(0, 0, 0, 0);

        bounds.x = i.left;
        bounds.y = i.top;
        bounds.width -= i.left + i.right;
        bounds.height -= i.top + i.bottom;

        Rectangle visible = getVisibleRect();

        visible.y = from - (visible.height + from - to) / 2;

        if (visible.y < bounds.y)
            visible.y = bounds.y;

        if (visible.y + visible.height > bounds.y + bounds.height)
            visible.y = bounds.y + bounds.height - visible.height;

        scrollRectToVisible(visible);
    } // centerPath
} // org.Zeitline.GUI.EventTree.EventTree
