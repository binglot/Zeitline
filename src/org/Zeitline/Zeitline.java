package org.Zeitline;
/********************************************************************

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

import org.Zeitline.Event.AbstractTimeEvent;
import org.Zeitline.Event.AtomicEvent;
import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.Event.Mask.AtomicEventMask;
import org.Zeitline.Event.Mask.ComplexEventMask;
import org.Zeitline.InputFilter.InputFilter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.OverlayLayout;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class Zeitline implements TreeSelectionListener {
    private static final String JAR_FILTERS = "reg_filters";
    private static final String DYNAMIC_FILTERS = "filters";
    private static final String PACKAGE_NAME = "org/Zeitline/";
    static Zeitline app;
    public static final String PROJECT_FILE_EXTENSION = ".ztl";
    public static final String PROJECT_NAME = "Zeitline Project";

    protected EventTree tree;
    protected ComplexEventMask cem;
    protected AtomicEventMask aem;
    protected JSplitPane mainPane;
    protected TimelineView timelines;
    protected JToolBar toolBar;

    protected int displayMode;

    static JFrame frame;

    protected JMenuItem menuMoveLeft, menuMoveRight;

    protected final JFileChooser fileChooser;
    //    protected Hashtable filters;
    protected Vector filters;

    protected Action createFrom;
    protected Action createTimelineFrom;
    protected Action importAction;
    protected Action moveLeft;
    protected Action moveRight;
    protected Action exitAction;
    protected Action removeEvents;
    protected Action toggleOrphan;
    protected Action clearAction;
    protected Action clearAllAction;
    protected Action filterQueryAction;
    protected Action cutAction;
    protected Action pasteAction;
    protected Action findAction;
    protected Action emptyTimeline;
    protected Action deleteTimeline;
    protected Action aboutAction;

    protected Action testAction;
    protected Action testAction2;
    protected Action saveAction;
    protected Action loadAction;

    protected Transferable cutBuffer = null;

    public Zeitline() {
        File currentWorkingDirectory = new File(System.getProperty("user.dir"));
        fileChooser = new JFileChooser(currentWorkingDirectory);
        fileChooser.addChoosableFileFilter(new FileInputFilter(PROJECT_FILE_EXTENSION, PROJECT_NAME));

        filters = loadPlugins();

        /* 'File' menu actions */

        loadAction = new LoadAction("Load",
                createNavigationIcon("fileopen"),
                KeyEvent.VK_L);

        saveAction = new SaveAction("Save",
                createNavigationIcon("filesave"),
                KeyEvent.VK_S);
        saveAction.setEnabled(false);

        exitAction = new ExitAction("Exit", KeyEvent.VK_X);

        /* 'Edit' menu actions */

        cutAction = new CutAction("Cut",
                createNavigationIcon("editcut"),
                KeyEvent.VK_T);
        cutAction.setEnabled(false);

        pasteAction = new PasteAction("Paste",
                createNavigationIcon("editpaste"),
                KeyEvent.VK_P);
        pasteAction.setEnabled(false);

        clearAction = new ClearAction("Clear Selection", KeyEvent.VK_C);
        clearAction.setEnabled(false);

        clearAllAction = new ClearAllAction("Clear All Selections", KeyEvent.VK_A);

        findAction = new FindAction("Find ...", createNavigationIcon("find"), KeyEvent.VK_D);
        findAction.setEnabled(false);

        /* 'Event' menu actions */

        createFrom = new CreateFromAction("Create from ...",
                createNavigationIcon("create_event"),
                KeyEvent.VK_C);
        createFrom.setEnabled(false);

        removeEvents = new RemoveEventsAction("Remove",
                createNavigationIcon("delete_event"),
                KeyEvent.VK_R);
        removeEvents.setEnabled(false);

        importAction = new ImportAction("Import ...",
                createNavigationIcon("import"),
                KeyEvent.VK_I);

        /* 'Timeline' menu actions */

        emptyTimeline = new EmptyTimelineAction("Create empty ...",
                createNavigationIcon("new_timeline"),
                KeyEvent.VK_E);
        createTimelineFrom = new CreateTimelineFromAction("Create from ...",
                createNavigationIcon("create_timeline"),
                KeyEvent.VK_C);
        createTimelineFrom.setEnabled(false);

        deleteTimeline = new DeleteTimelineAction("Delete",
                createNavigationIcon("delete_timeline"),
                KeyEvent.VK_D);
        deleteTimeline.setEnabled(false);

        moveLeft = new MoveLeftAction("Move Left",
                createNavigationIcon("moveleft"),
                KeyEvent.VK_L);
        moveRight = new MoveRightAction("Move Right",
                createNavigationIcon("moveright"),
                KeyEvent.VK_R);
        filterQueryAction = new FilterQueryAction("Filter ...",
                createNavigationIcon("filter"),
                KeyEvent.VK_F);
        filterQueryAction.setEnabled(false);

        toggleOrphan = new ToggleOrphanAction("Show Orphans", null, KeyEvent.VK_O);

        /* 'Help' menu actions */

        aboutAction = new AboutAction("About", KeyEvent.VK_A);

        /* actions for testing new code */

        testAction = new TestAction("TEST", KeyEvent.VK_T);
        testAction2 = new TestAction2("TEST2", KeyEvent.VK_2);


        displayMode = EventTree.DISPLAY_ALL;

    } // org.Zeitline.Zeitline

    public JMenuBar createMenuBar() {

        JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbMenuItem;
        JCheckBoxMenuItem cbMenuItem;

        menuBar = new JMenuBar();

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);

        menuItem = createMenuItem(saveAction);
        menu.add(menuItem);

        menuItem = createMenuItem(loadAction);
        menu.add(menuItem);

        menuItem = createMenuItem(exitAction);
        menu.add(menuItem);

        menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(menu);

        menuItem = createMenuItem(cutAction);
        menu.add(menuItem);

        menuItem = createMenuItem(pasteAction);
        menu.add(menuItem);

        menuItem = createMenuItem(clearAction);
        menu.add(menuItem);

        menuItem = createMenuItem(clearAllAction);
        menu.add(menuItem);

        menuItem = createMenuItem(findAction);
        menu.add(menuItem);

        menu = new JMenu("Event");
        menu.setMnemonic(KeyEvent.VK_N);
        menuBar.add(menu);

        menuItem = createMenuItem(createFrom);
        menu.add(menuItem);

        menuItem = createMenuItem(removeEvents);
        menu.add(menuItem);

        menuItem = createMenuItem(importAction);
        menu.add(menuItem);

        menu = new JMenu("Timeline");
        menu.setMnemonic(KeyEvent.VK_T);
        menuBar.add(menu);

        menuItem = createMenuItem(emptyTimeline);
        menu.add(menuItem);

        menuItem = createMenuItem(createTimelineFrom);
        menu.add(menuItem);

        menuItem = createMenuItem(deleteTimeline);
        menu.add(menuItem);

        menuItem = createMenuItem(moveLeft);
        menu.add(menuItem);

        menuItem = createMenuItem(moveRight);
        menu.add(menuItem);

        menuItem = createMenuItem(filterQueryAction);
        menu.add(menuItem);

        menuItem = new JCheckBoxMenuItem(toggleOrphan);
        menu.add(menuItem);

        menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);
        menuBar.add(menu);

        submenu = new JMenu("Time Display");
        submenu.setMnemonic(KeyEvent.VK_D);

        ButtonGroup group = new ButtonGroup();

        rbMenuItem = new JRadioButtonMenuItem(new SetDisplayModeAction("yyyy-mm-dd hh:mm:ss.d", KeyEvent.VK_Y, EventTree.DISPLAY_ALL));
        rbMenuItem.setSelected(true);
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem(new SetDisplayModeAction("hh:mm:ss", KeyEvent.VK_H, EventTree.DISPLAY_HMS));
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);

        menu.add(submenu);
        menuBar.add(menu);

        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menu);

        menuItem = createMenuItem(aboutAction);
        menu.add(menuItem);

        return menuBar;

    } // createMenuBar

    public JToolBar createToolBar() {

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        toolBar.add(createButton(loadAction));
        toolBar.add(createButton(saveAction));
        toolBar.addSeparator(new Dimension(16, 32));
        toolBar.add(createButton(cutAction));
        toolBar.add(createButton(pasteAction));
        toolBar.add(createButton(findAction));
        toolBar.addSeparator(new Dimension(16, 32));
        toolBar.add(createButton(importAction));
        toolBar.add(createButton(createFrom));
        toolBar.add(createButton(removeEvents));
        toolBar.addSeparator(new Dimension(16, 32));
        toolBar.add(createButton(moveLeft));
        toolBar.add(createButton(moveRight));
        toolBar.add(createButton(filterQueryAction));
        toolBar.add(createButton(emptyTimeline));
        toolBar.add(createButton(createTimelineFrom));
        toolBar.add(createButton(deleteTimeline));


//	toolBar.add(testAction);
        //	toolBar.add(testAction2);


        return toolBar;

    } // createToolBar

    public Component createComponents() {

        long ts;

        Date afterInsert = new Date();

        toolBar = createToolBar();

        // Create panel that contains the Event masks
        JPanel maskOverlay = new JPanel();
        maskOverlay.setLayout(new OverlayLayout(maskOverlay));
        cem = new ComplexEventMask();
        aem = new AtomicEventMask();
        maskOverlay.add(cem);
        maskOverlay.add(aem);
        maskOverlay.setMinimumSize(cem.getPreferredSize());
        cem.setVisible(false);
        aem.setVisible(false);

        timelines = new TimelineView(app, moveLeft, moveRight,
                filterQueryAction, deleteTimeline,
                saveAction, pasteAction,
                cutAction, clearAction, findAction,
                cem, aem);

        mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                timelines, new JScrollPane(maskOverlay));


        mainPane.setOneTouchExpandable(true);
        mainPane.setResizeWeight(1.0);

        JPanel mainCanvas = new JPanel(new BorderLayout());

        mainCanvas.add(toolBar, BorderLayout.PAGE_START);
        mainCanvas.add(mainPane, BorderLayout.CENTER);

        Date after = new Date();
        //	System.out.println("Drawing GUI: " + (after.getTime() - afterInsert.getTime()));

        return mainCanvas;

    } // createComponents

    static void createAndShowGUI() {

        //Create and set up the window.
        frame = new JFrame("org.Zeitline.Zeitline");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        app = new Zeitline();
        frame.setJMenuBar(app.createMenuBar());

        Component contents = app.createComponents();
        frame.getContentPane().add(contents, BorderLayout.CENTER);

        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);

    } // createAndShowGUI

    public void valueChanged(TreeSelectionEvent e) {
        EventTree tree = (EventTree) e.getSource();

        int count = tree.getSelectionCount();

        if (count != 1)
            pasteAction.setEnabled(false);
        else {
            AbstractTimeEvent te = (AbstractTimeEvent) tree.getLastSelectedPathComponent();
            pasteAction.setEnabled((te instanceof ComplexEvent) &&
                    (cutBuffer != null));
        }

        // TODO: once drag and drop for tabs works, we can remove
        //       the second condition
        if ((count == 0) || (tree.isPathSelected(new TreePath(new Object[]{tree.getModel().getRoot()})))) {
            cutAction.setEnabled(false);
            createFrom.setEnabled(false);
            createTimelineFrom.setEnabled(false);
            removeEvents.setEnabled(false);
        } else {
            cutAction.setEnabled(true);
            createFrom.setEnabled(true);
            createTimelineFrom.setEnabled(true);
            removeEvents.setEnabled(true);
        }

    } // valueChanged

    /***************************************************************/
    /*
     * GUI Action definitions
     */

    /**
     * ***********************************************************
     */

    /* 'File' menu actions */

    public class LoadAction extends AbstractAction {

        private ComplexEvent complex_event;

        public LoadAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
            putValue(SHORT_DESCRIPTION, "Load project");
        } // LoadAction

        public void setComplexEvent(ComplexEvent complex_event) {
            this.complex_event = complex_event;
        } // setComplexEvent

        public void actionPerformed(ActionEvent e) {
            if (saveAction.isEnabled()) {
                // prompt to save current project before loading
                int save_confirm = JOptionPane.showConfirmDialog(null,
                        "Would you like to save the current project before loading a different one?");
                switch (save_confirm) {
                    case JOptionPane.CANCEL_OPTION:
                        // user doesn't want to continue, end loading process
                        return;
                    case JOptionPane.NO_OPTION:
                        // do not save first, therefore do nothing
                        break;
                    case JOptionPane.YES_OPTION:
                        // save the project first before loading another one
                        saveAction.actionPerformed(e);
                        break;
                    default:
                        System.err.println("JOptionPane.showConfirmDialog() returned "
                                + "the unknown value of "
                                + save_confirm);
                }
            }

            ObjectInputStream in_stream = null;
            complex_event = null;

            // use JFileChooser to get a file name from which to load the ComplexEvents
            if (fileChooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;
            File chosen = fileChooser.getSelectedFile();
            try {
                in_stream = new ObjectInputStream(new FileInputStream(chosen));
                long temp_long = ((Long) in_stream.readObject()).longValue();
                AbstractTimeEvent.setIdCounter(temp_long);
                timelines.loadFromFile(in_stream, app);
                in_stream.close();
            } catch (IOException io_excep) {
                if (io_excep instanceof StreamCorruptedException) {
                    // TODO: this is not really a check whether the file is in the proper format.
                    // All we do right now is to make sure a proper Java Stream is openend.
                    JOptionPane.showMessageDialog(null,
                            "The file you specified is not in the proper org.Zeitline.Zeitline project format.\n If you want to add events, choose the 'Import' function.",
                            "Invalid format",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "The following error occurred when trying to access file '"
                                    + chosen + "': " + io_excep,
                            "I/O error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (ClassNotFoundException cnf_excep) {
                System.err.println("ERROR: ClassNotFoundException while writing ID counter");
            }

            saveAction.setEnabled(false);
        } // actionPerformed

    } // class LoadAction

    public class SaveAction extends AbstractAction {

        public SaveAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
            putValue(SHORT_DESCRIPTION, "Save project");
        } // SaveAction

        public void actionPerformed(ActionEvent e) {

            ObjectOutputStream out_stream = null;

            // use a JFileChooser to get a file name to save the ComplexEvents as
            fileChooser.setDialogTitle("Save Project");
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            if (fileChooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) return;
            File chosen = fileChooser.getSelectedFile();
            String name = chosen.getName();

            if (!name.contains("."))
                chosen = new File(chosen.getParent() + File.separator + name + ".ztl");

            // open the ObjectOutputStream
            try {
                out_stream = new ObjectOutputStream(new FileOutputStream(chosen));

                // write out the current ID counter
                out_stream.writeObject(new Long(AbstractTimeEvent.getIdCounter()));
            } catch (IOException io_excep) {
                JOptionPane.showMessageDialog(null,
                        "The following error occurred when trying to write file '"
                                + chosen + "': " + io_excep,
                        "I/O error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // dump cut buffer to the orphan timeline
            if (cutBuffer != null) {
                EventTree orphan = timelines.getOrphanTree();
                TimeEventTransferHandler transfer = (TimeEventTransferHandler) orphan.getTransferHandler();
                ComplexEvent orphan_root = (ComplexEvent) orphan.getModel().getRoot();
                transfer.performPaste(cutBuffer, orphan_root);
            }

            // save all the org.Zeitline.TimelineView
            timelines.saveEventTrees(out_stream);

            // close the ObjectOutputStream
            try {
                out_stream.close();
            } catch (IOException io_excep) {
            }

            saveAction.setEnabled(false);

        } // actionPerformed

    } // class SaveAction

    public class ExitAction extends AbstractAction {

        public ExitAction(String text, int mnemonic) {
            super(text);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        } // ExitAction

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        } // actionPerformed

    } // class ExitAction

    /* 'Edit' menu actions */

    public class CutAction extends AbstractAction {

        public CutAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.SHIFT_MASK));
            putValue(SHORT_DESCRIPTION, "Cut");
        } // CutAction

        public void actionPerformed(ActionEvent e) {

            if (cem.isVisible() && cem.isModified() && (cem.checkUpdate() == JOptionPane.CANCEL_OPTION))
                return;

            Transferable t = ((TimeEventTransferHandler) timelines.getCurrentTree().getTransferHandler()).performCut();
            if (t == null)
                return;

            if (cutBuffer != null) {
                EventTree orphan = timelines.getOrphanTree();
                TimeEventTransferHandler transfer = (TimeEventTransferHandler) orphan.getTransferHandler();
                ComplexEvent orphan_root = (ComplexEvent) orphan.getModel().getRoot();
                transfer.performPaste(t, orphan_root);
            }

            cutBuffer = t;
            saveAction.setEnabled(true);

        } // actionPerformed

    } // class CutAction

    public class PasteAction extends AbstractAction {

        public PasteAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, ActionEvent.SHIFT_MASK));
            putValue(SHORT_DESCRIPTION, "Paste");
        } // PasteAction

        public void actionPerformed(ActionEvent e) {

            if (cutBuffer == null)
                return;

            EventTree currentTree = timelines.getCurrentTree();
            if (currentTree.getSelectionCount() != 1)
                return;

            ComplexEvent targetNode;
            try {
                targetNode = (ComplexEvent) currentTree.getSelectionPath().getLastPathComponent();
            } catch (ClassCastException ce) {
                return;
            }

            ((TimeEventTransferHandler) currentTree.getTransferHandler()).performPaste(cutBuffer, targetNode);

            cutBuffer = null;

            saveAction.setEnabled(true);
            pasteAction.setEnabled(false);

        } // actionPerformed

        public boolean pastePossible() {
            return (cutBuffer != null);
        } // pastePossible

    } // class PasteAction

    public class ClearAction extends AbstractAction {

        public ClearAction(String text, int mnemonic) {
            super(text);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));
        } // ClearAction

        public void actionPerformed(ActionEvent e) {
            EventTree current = timelines.getCurrentTree();
            if (current != null)
                current.clearSelection();
        } // actionPerformed

    } // class ClearAction

    public class ClearAllAction extends AbstractAction {

        public ClearAllAction(String text, int mnemonic) {
            super(text);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        } // ClearAllAction

        public void actionPerformed(ActionEvent e) {
            timelines.clearSelections();
        } // actionPerformed

    } // class ClearAllAction

    public class FindAction extends AbstractAction {

        public FindAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        } // FindAction


        public void actionPerformed(ActionEvent e) {

            EventTree currentTree = timelines.getCurrentTree();

            NewQueryDlg dialog = new NewQueryDlg(frame,
                    mainPane.getRightComponent(),
                    "Find Events",
                    false,
                    NewQueryDlg.MODE_SEARCH,
                    currentTree.getStartTime(),
                    currentTree.getMaxStartTime(),
                    null, timelines);

            dialog.setVisible(true);

        } // actionPerformed

    } // class FindAction

    /* 'Event' menu actions */

    public class CreateFromAction extends AbstractAction {

        public CreateFromAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
            putValue(SHORT_DESCRIPTION, "Create new event from selection");
        } // CreateFromAction

        public void actionPerformed(ActionEvent e) {
            EventTree currentTree = timelines.getCurrentTree();

            ComplexEvent event = NewComplexEventDlg.showDialog(frame, currentTree.getDisplay(), "Create new event");

            if (event == null) return;

            ComplexEvent target = currentTree.getTopSelectionParent();

            boolean saveDeleteValue = target.getDeleteEmptyEvent();
            target.setDeleteEmptyEvent(false);

            currentTree.moveSelected(event, null);
            EventTreeModel model = (EventTreeModel) currentTree.getModel();

            model.insertNode(target, event);

            target.setDeleteEmptyEvent(saveDeleteValue);

            TreePath path = model.getTreePath(event);
            currentTree.expandPath(path);
            currentTree.centerEvent(event);

            saveAction.setEnabled(true);

        } // actionPerformed

    } // class CreateFromAction

    public class RemoveEventsAction extends AbstractAction {

        public RemoveEventsAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
            putValue(SHORT_DESCRIPTION, "Delete selected events");
        } // RemoveEventsAction

        public void actionPerformed(ActionEvent e) {
            timelines.removeSelected(timelines.getCurrentTree());
            saveAction.setEnabled(true);
        } // actionPerformed

    } // class RemoveEventsAction

    public class ImportAction
            extends AbstractAction
            implements StoppableRunnable {

        private boolean running;
        private JProgressBar progress_bar;
        private ProgressDlg pd;
        private InputFilter input_filter;
        private Source s;

        public ImportAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
            putValue(SHORT_DESCRIPTION, "Import new events");
            this.running = false;
            this.progress_bar = null;
        } // ImportAction

        public void actionPerformed(ActionEvent e) {
            if (ImportDlg.showDialog(frame, filters) == ImportDlg.CANCEL_OPTION) return;
            input_filter = ImportDlg.getFilter();
            if (input_filter == null) return;
            s = input_filter.init(ImportDlg.getFileName(), frame);
            if (s == null) return;

            pd = new ProgressDlg(frame,
                    "Importing Events",
                    (StoppableRunnable) importAction);
            pd.setVisible(true);
        } // actionPerformed

        public void stop() {
            // override the deprecated stop() method to provide
            // an alternative method in halting thread execution
            running = false;
        } // stop

        public void run() {
            progress_bar = pd.getProgressBar();
            // enable thread execution
            running = true;

            String filter_name = input_filter.getName();
            int percent_done = 0;
            double total_size = 0;

            if (progress_bar != null) {
                progress_bar.setString(filter_name
                        + " ("
                        + percent_done
                        + "%)");
                progress_bar.setMaximum(new Long(input_filter.getTotalCount()).intValue());
                progress_bar.setValue(0);
                total_size = new Long(input_filter.getTotalCount()).doubleValue();
            }

            pd.setStatus("Parsing import file");
            ComplexEvent ev = new ComplexEvent("Import from " + s, "");
            AtomicEvent t = null;
            int value = 0;
            while (running && ((t = input_filter.getNextEvent()) != null)) {
                t.setSource(s);
                ev.addTimeEvent(t);

                // update the progress bar
                if (progress_bar != null) {
                    value = new Long(input_filter.getProcessedCount()).intValue();
                    progress_bar.setValue(value);

                    percent_done = new Double(new Long(input_filter.getProcessedCount()).doubleValue() / total_size * 100.0).intValue();
                    progress_bar.setString(filter_name
                            + " ("
                            + percent_done
                            + "%)");
                }
            }

            // make sure the import wasn't canceled
            if (running) {
                // change progress bar to undetermined time
                pd.setStatus("Adding events to the timeline");
                progress_bar.setIndeterminate(true);
                // add the newly imported tree to the timeline
                EventTree tree = new EventTree(ev);
                timelines.addTree(tree, app);
                // project has changed, enable the ability to save
                saveAction.setEnabled(true);
            }

            // close the progress dialog
            pd.setVisible(false);
        } // run
    } // class ImportAction

    /* 'Timeline' menu actions */

    public class EmptyTimelineAction extends AbstractAction {

        public EmptyTimelineAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
            putValue(SHORT_DESCRIPTION, "Create empty timeline");
        } // EmptyTimelineAction

        public void actionPerformed(ActionEvent e) {
            ComplexEvent event = NewComplexEventDlg.showDialog(frame, timelines, "Create empty timeline");

            if (event == null) return;

            EventTree t = new EventTree(event);
            timelines.addTree(t, app);
            saveAction.setEnabled(true);
        } // actionPerformed

    } // class EmptyTimelineAction

    public class CreateTimelineFromAction extends AbstractAction {

        public CreateTimelineFromAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
            putValue(SHORT_DESCRIPTION, "Create new timeline from selection");
        } // CreateTimelineFromAction

        public void actionPerformed(ActionEvent e) {
            ComplexEvent event = NewComplexEventDlg.showDialog(frame, timelines, "Create new timeline");

            if (event == null) return;

            timelines.getCurrentTree().moveSelected(event, null);
            EventTree t = new EventTree(event);
            timelines.addTree(t, app);
            saveAction.setEnabled(true);

        } // actionPerformed

    } // class CreateTimelineFromAction

    public class DeleteTimelineAction extends AbstractAction {

        public DeleteTimelineAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.CTRL_MASK));
            putValue(SHORT_DESCRIPTION, "Delete timeline");
        } // DeleteTimelineAction

        public void actionPerformed(ActionEvent e) {
            EventTree currentTree = timelines.getCurrentTree();

            if ((((ComplexEvent) currentTree.getModel().getRoot()).countChildren() != 0)
                    || timelines.isOrphan(currentTree))
                return;

            timelines.deleteTree(currentTree);
            saveAction.setEnabled(true);
        } // actionPerformed

    } // class DeleteTimelineAction

    public class MoveLeftAction extends AbstractAction {

        public MoveLeftAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.CTRL_MASK));
            putValue(SHORT_DESCRIPTION, "Move current timeline to left");
        } // MoveLeftAction

        public void actionPerformed(ActionEvent e) {
            timelines.moveLeft();
            saveAction.setEnabled(true);
        } // actionPerformed

    } // class MoveLeftAction

    public class MoveRightAction extends AbstractAction {

        public MoveRightAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ActionEvent.CTRL_MASK));
            putValue(SHORT_DESCRIPTION, "Move current timeline to right");
        } // MoveRightAction

        public void actionPerformed(ActionEvent e) {
            timelines.moveRight();
            saveAction.setEnabled(true);
        } // actionPerformed

    } // class MoveRightAction

    public class FilterQueryAction extends AbstractAction {

        public FilterQueryAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
            putValue(SHORT_DESCRIPTION, "Filter event view");
        } // FilterQueryAction

        public void actionPerformed(ActionEvent e) {
            EventTree currentTree = timelines.getCurrentTree();

            Query q = NewQueryDlg.showDialog(frame, currentTree.getDisplay(),
                    currentTree.getStartTime(),
                    currentTree.getMaxStartTime(),
                    null);

            if (q == null)
                return;

            timelines.getCurrentTree().getDisplay().addQuery(q);
        } // actionPerformed

    } // class FilterQueryAction

    public class ToggleOrphanAction extends AbstractAction {

        public ToggleOrphanAction(String text, ImageIcon icon, int mnemonic) {
            super(text, icon);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
        } // ToggleOrphanAction

        public void actionPerformed(ActionEvent e) {
            timelines.toggleOrphanVisible();
        } // actionPerformed

    } // class ToggleOrphanAction

    /* 'View' menu actions */

    public class SetDisplayModeAction extends AbstractAction {

        protected int mode;

        public SetDisplayModeAction(String text, int mnemonic, int mode) {
            super(text);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            this.mode = mode;
        } // SetDisplayModeAction

        public void actionPerformed(ActionEvent e) {
            EventTree.setDisplayMode(mode);
            timelines.redraw();
        } // actionPerformed

    } // class SetDisplayModeAction

    /* 'Help' menu actions */

    public class AboutAction extends AbstractAction {

        public AboutAction(String text, int mnemonic) {
            super(text);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
        } // ClearAllAction

        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame,
                    "org.Zeitline.Zeitline v0.2 (beta status)\n\n" +
                            "Written by Florian Buchholz and Courtney Falk\n" +
                            "\n" +
                            "Copyright the authors, Purdue University 2004-2006", "org.Zeitline.Zeitline", JOptionPane.PLAIN_MESSAGE);
        } // actionPerformed

    } // class AboutAction

    // action used to test certain functionality at the push of a button
    public class TestAction extends AbstractAction {

        public TestAction(String text, int mnemonic) {
            super(text);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
        }

        public void actionPerformed(ActionEvent e) {

//	    System.out.println(HostDlg.showDialog(frame));

        }
    }

    // action used to test certain functionality at the push of a button
    public class TestAction2 extends AbstractAction {

        public TestAction2(String text, int mnemonic) {
            super(text);
            putValue(MNEMONIC_KEY, new Integer(mnemonic));
        }

        public void actionPerformed(ActionEvent e) {

            /*
           org.Zeitline.EventTree tree = timelines.getCurrentTree();

           org.Zeitline.EventTreeModel currentModel = (org.Zeitline.EventTreeModel)tree.getModel();

           org.Zeitline.Event.ComplexEvent currentRoot = (org.Zeitline.Event.ComplexEvent)currentModel.getRoot();

           org.Zeitline.Event.AbstractTimeEvent res = currentRoot.findPrev(new org.Zeitline.Query("README"));

           if (res != null) {
           tree.setSelectionPath(currentModel.getTreePath(res));
           tree.centerEvent(res);
           }
           else
           System.out.println("No match found");
           */

            timelines.findNextEvent(new Query("readme"), false);
        }
    }

    public static ImageIcon createNavigationIcon(String imageName) {
        String imgLocation = "icons/"
                + imageName
                + ".png";
        java.net.URL imageURL = Zeitline.class.getResource(imgLocation);

        if (imageURL == null) {
            System.err.println("Resource not found: "
                    + imgLocation);
            return null;
        } else {
            return new ImageIcon(imageURL);
        }
    } // createNavigationIcon

    public JButton createButton(Action a) {
        JButton b = new JButton(a);
        b.setText(null);
        b.setMnemonic(0);
        String name = (String) a.getValue(a.NAME);

        InputMap imap = b.getInputMap(b.WHEN_IN_FOCUSED_WINDOW);
        KeyStroke ks = (KeyStroke) a.getValue(a.ACCELERATOR_KEY);
        imap.put(ks, name);

        return b;
    } // createButton

    public JMenuItem createMenuItem(Action a) {

        JMenuItem m = new JMenuItem(a);
        m.setIcon(null);

        return m;

    } // createMenuItem


    private Vector loadPlugins() {
        Vector result = new Vector();
        PluginLoader loader = new PluginLoader();

        String location = Zeitline.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        Enumeration plugins;
        InputFilter temp = null;

        // If the application is run from a JAR file, try to find embedded plugins
        if (Utils.ContainsCaseInsensitive(location, ".jar")) {
            plugins = loader.getPluginsFromJar(location, JAR_FILTERS);
            while (plugins.hasMoreElements()) {
                result.add(plugins.nextElement());
            }
        }

        // Look for the plugins in the 'filters' directory
        // TODO: NEEDS FIXING, the 'location' can be a full file path
        String pluginsDir = location + PACKAGE_NAME + DYNAMIC_FILTERS;
        if ((plugins = loader.getPluginsFromDir(pluginsDir)) == null)
            return result;

        while (plugins.hasMoreElements()) {
            result.add(plugins.nextElement());
        }

        return result;
    } // loadPlugins

} // class org.Zeitline.Zeitline
