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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

public class TimeEventTransferHandler extends TransferHandler 
    implements DragSourceListener, DragGestureListener, DropTargetListener {

    protected EventTree sourceTree;
    protected EventTree tree;
    protected boolean isSource = false;
    protected Hashtable selectedNodes;
    protected DragSource dragSource;
    protected DropTarget dropTarget;
    protected JLabel dragLabel;
    protected static JLayeredPane dragPane;
    protected boolean drawDragImage = true;
    protected Rectangle targetCueLine = new Rectangle();
    protected TreePath lastPath = null;
    protected Timer hoverTimer;
    protected DataFlavor timeEventFlavor;
    protected String timeEventType = 
	"application/x-java-jvm-local-objectref; class=org.Zeitline.TimeEvent";
    //	"application/x-java-serialized-object; class=org.Zeitline.TimeEvent";

    public TimeEventTransferHandler(EventTree t) {

        try {
            timeEventFlavor = new DataFlavor(timeEventType);
        } catch (ClassNotFoundException e) {
            System.err.println(
             "org.Zeitline.TimeEventTransferHandler: unable to create data flavor");
        }

	tree = t;
	selectedNodes = new Hashtable();
	dragSource = new DragSource();
	dragSource.createDefaultDragGestureRecognizer(tree, 
						      DnDConstants.ACTION_MOVE,
						      this);
	dropTarget = new DropTarget(tree, DnDConstants.ACTION_MOVE, this);

	hoverTimer = new Timer(1000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (lastPath.getPathCount() == 1)
			return;    
		    if (tree.isExpanded(lastPath))
			tree.collapsePath(lastPath);
		    else
			tree.expandPath(lastPath);
		    hoverTimer.stop();
		}
	    });

    } // org.Zeitline.TimeEventTransferHandler

    /* Override transfer handler stuff first */

    protected Transferable createTransferable(JComponent c, boolean removeNodes) {
        if (c instanceof EventTree) {
            sourceTree = (EventTree)c;

	    TimeEvent node;

	    Vector v = new Vector();
	    TreePath[] selections = sourceTree.getSelectionPaths();
	    build:
	    for (int i=0; i < selections.length; i++) {
		for (TreePath ancestor = selections[i].getParentPath();
		     ancestor != null;
		     ancestor = ancestor.getParentPath()) {
		    if (sourceTree.isPathSelected(ancestor))
			continue build;
		}
		TimeEvent te = (TimeEvent)selections[i].getLastPathComponent();
		v.add(te);
		if (removeNodes) {
		    EventTreeModel etm = (EventTreeModel)sourceTree.getModel();
		    etm.removeNode(te.getParent(), te);
		}
		else {
		    TreeSet nodes = (TreeSet)selectedNodes.get(te.getParent());
		    if (nodes == null) {
			nodes = new TreeSet(new TimeEventComparator());
			selectedNodes.put(te.getParent(), nodes);
		    }
		    nodes.add(te);
		}
	    }
	    return new TransferableEvent(v);
	}
	return null;

    } // createTransferable(JComponent,boolean)

    protected Transferable createTransferable(JComponent c) {
	return createTransferable(c, false);
    } // createTransferable(JComponent)

    public int getSourceActions(JComponent c) {
	return MOVE;
    } // getSourceActions


    /* DragGesture interface */

    public void dragGestureRecognized(DragGestureEvent dge) {

	if (tree.isPathSelected(new TreePath(tree.getModel().getRoot())))
	    return;

	TransferableEvent transferable = (TransferableEvent)createTransferable(tree);

	BufferedImage image = null;
	Point ptDragOrigin = dge.getDragOrigin();
	if (drawDragImage) {
	    //	    Point ptDragOrigin = dge.getDragOrigin();
	    TreePath path = tree.getPathForLocation(ptDragOrigin.x, ptDragOrigin.y);
	    Rectangle pathBounds = tree.getPathBounds(path);

	    JLabel lbl = (JLabel)tree.getCellRenderer().
		getTreeCellRendererComponent(tree,
					     path.getLastPathComponent(),
					     false,
					     tree.isExpanded(path),
					     tree.getModel().isLeaf(path.getLastPathComponent()),
					     0,
					     false);
	    lbl.setBounds(pathBounds);
	    image = new BufferedImage(lbl.getWidth(),
				      lbl.getHeight(),
				      BufferedImage.TYPE_INT_ARGB_PRE);
	    Graphics2D graphics = image.createGraphics();
	    graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
	    lbl.setOpaque(false);
	    lbl.paint(graphics);
	    graphics.dispose();
	    dragLabel = new JLabel(new ImageIcon(image));
	    dragLabel.setOpaque(false);
	    dragLabel.setBounds(pathBounds);
	    Container container = tree.getTopLevelAncestor();
	    if (container == null)
		drawDragImage = false;
	    else {
		if (container instanceof JFrame) {
		    dragPane = ((JFrame)container).getLayeredPane();
		    dragPane.add(dragLabel, JLayeredPane.DRAG_LAYER);
		    dragLabel.setLocation(
		         SwingUtilities.convertPoint(dge.getComponent(),
						     pathBounds.getLocation(),
						     dragPane));
		}
		else {
		    drawDragImage = false;
		}
	    }

	}

	isSource = true;

	dragSource.startDrag(dge, 
			     DragSource.DefaultMoveNoDrop,
			     //			     image,
			     //			     new Point(0,0),
			     transferable,
			     this);

    } // dragGestureRecognized


    /* DragSource interface */

    public void dragEnter(DragSourceDragEvent dsde) {

	//	System.out.println("Drag enter");

    }

    public void dragExit(DragSourceEvent dse) {

	//	System.out.println("Drag exit");

	Point pt = dse.getLocation();
	if (drawDragImage)
	    paintDragImage(pt);
        dse.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
    } // dragExit

    public void dragOver(DragSourceDragEvent dsde) {
	//	System.out.println("Drag over");

	Point pt = dsde.getLocation();
	if (drawDragImage)
	    paintDragImage(pt);
	dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
    } // dragOver

    public void dropActionChanged(DragSourceDragEvent dsde) {}

    public void dragDropEnd(DragSourceDropEvent dsde) {

	isSource = false;
	if (drawDragImage) {
	    dragPane.remove(dragLabel);
	    dragPane.repaint(dragLabel.getBounds());
	}

	if (dsde.getDropSuccess()) {
	    EventTreeModel model = (EventTreeModel)tree.getModel();
	    Enumeration expanded = tree.getExpandedDescendants(new TreePath(model.getRoot()));
	    
	    for (Enumeration keylist = selectedNodes.keys(); 
		 keylist.hasMoreElements();) {
		ComplexEvent parent = (ComplexEvent)keylist.nextElement();
		TreeSet children = (TreeSet) selectedNodes.get(parent);
		model.removeNodes(parent, children.toArray());
	    }
	    	    
	    while (expanded.hasMoreElements()) {
		TreePath tp = (TreePath) expanded.nextElement();
		tree.expandPath(tp);
	    }
	    
	    tree.clearSelection();

	}

	selectedNodes.clear();

    } // dragDropEnd

    /* DropTarget interface */

    public void dragEnter(DropTargetDragEvent dtde) {
        dragOver(dtde);
    } // dragEnter

    public void dragOver(DropTargetDragEvent dtde) {

	Graphics2D g2 = (Graphics2D) tree.getGraphics();

	Point loc = dtde.getLocation();

        TreePath destinationPath = tree.getPathForLocation(loc.x, loc.y);
	if (destinationPath != null) {
	    Rectangle raPath = tree.getPathBounds(destinationPath);
	    tree.paintImmediately(targetCueLine.getBounds());    
	    targetCueLine.setRect(0,  raPath.y+(int)raPath.getHeight(), tree.getWidth(), 2);

	    g2.setColor(new Color(0,0,0));
	    g2.fill(targetCueLine);         // Draw the cue line
	}

	tree.autoscroll(loc);

	if (!(destinationPath == lastPath)) {
	    lastPath = destinationPath;
	    hoverTimer.restart();
	}


	if ((dtde.getDropAction() == DnDConstants.ACTION_MOVE) &&
	    (destinationPath != null) &&
	    (destinationPath.getLastPathComponent() instanceof ComplexEvent))
	    dtde.acceptDrag(DnDConstants.ACTION_MOVE);	
	else {
	    hoverTimer.stop();
	    dtde.rejectDrag();
	}

    } // dragOver

    public void dragExit(DropTargetEvent dte) {
	tree.paintImmediately(targetCueLine.getBounds());    
    } // dragExit

    public void dropActionChanged(DropTargetDragEvent dtde) {}

    public void drop(DropTargetDropEvent dtde) {

	tree.paintImmediately(targetCueLine.getBounds());

	Point loc = dtde.getLocation();
        TreePath targetPath = tree.getPathForLocation(loc.x, loc.y);
	if (targetPath == null)
		return;
	Object currentNode = targetPath.getLastPathComponent();
	if (!(currentNode instanceof ComplexEvent))
		return;
	ComplexEvent target_node = (ComplexEvent)currentNode;

	Transferable tr = dtde.getTransferable();

	if (! doTransfer(tr, target_node, true)) {
	    hoverTimer.stop();
	    dtde.rejectDrop();
	    return;
	}

	lastPath = null;
	hoverTimer.stop();
	dtde.dropComplete(true);

    } // drop

    protected boolean doTransfer(Transferable tr, ComplexEvent target_node,
			      boolean removeNodes) {

	if (!hasTimeEventFlavor(tr.getTransferDataFlavors()))
	    return false;
   
	// prevent drop to item in the selection
	EventTreeModel target_model = (EventTreeModel)tree.getModel();
	if (removeNodes && isSource &&
	    tree.isPathSelected(target_model.getTreePath(target_node)))
	    return false;	    

	Vector data;

	try {
	    data = (Vector)tr.getTransferData(timeEventFlavor);
	}
	catch (Exception ex) {
	    System.err.println("Exception: " + ex);
	    return false;
	}
	    
	for (Enumeration node_list = data.elements(); node_list.hasMoreElements();) {
	    
	    TimeEvent node = (TimeEvent)node_list.nextElement();

            if ((removeNodes && 
		 (EventComparator.compare(target_node, node.getParent()) == 0))) {
		selectedNodes.remove(target_node);
		continue;
	    }

	    target_model.insertNode(target_node, node);
	}

	tree.expandPath(target_model.getTreePath(target_node));

	return true;

    } // doTransfer

    protected void paintDragImage(Point pt) {

        Point anchor = dragPane.getTopLevelAncestor().getLocationOnScreen();
        pt.translate(- (int)anchor.getX(), - (int)anchor.getY());

	dragLabel.setLocation(pt);

    } // paintDragImage

    protected boolean hasTimeEventFlavor(DataFlavor[] flavors) {
        if (timeEventFlavor == null)
            return false;

        for (int i = 0; i < flavors.length; i++)
            if (flavors[i].equals(timeEventFlavor))
                return true;

        return false;

    } // hasTimeEventFlavor

    public Transferable performCut() {

	if (tree.getSelectionCount() == 0)
	    return null;

	return createTransferable(tree, true);

    } // performCut

    public void performPaste(Transferable data, ComplexEvent node) {
	doTransfer(data, node, false);
    } // performPaste

} // class org.Zeitline.TimeEventTransferHandler
