package org.Zeitline;

import org.Zeitline.Event.AbstractTimeEvent;
import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.Event.Mask.AtomicEventMask;
import org.Zeitline.Event.Mask.ComplexEventMask;
import org.Zeitline.GUI.Action.*;
import org.Zeitline.GUI.FormGenerator;
import org.Zeitline.GUI.IFormGenerator;
import org.Zeitline.InputFilter.InputFilter;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Zeitline implements TreeSelectionListener {
    public static final String APPLICATION_NAME = "Zeitline";
    public static final String APPLICATION_VERSION = "v0.3";
    private static final String JAR_FILTERS_DIR = "reg_filters";
    private static final String DYNAMIC_FILTERS_DIR = "filters";
    private static final String PACKAGE_DIR = "org/Zeitline/";
    private static final String PROJECT_FILE_EXTENSION = ".ztl";
    private static final String PROJECT_NAME = "Zeitline Project";

    protected EventTree tree;
    public ComplexEventMask cem;
    protected AtomicEventMask aem;
    public JSplitPane mainPane;
    public TimelineView timelines;
    protected JToolBar toolBar;

    protected int displayMode;

    public static JFrame frame;

    protected JMenuItem menuMoveLeft, menuMoveRight;

    public final JFileChooser fileChooser;
    public List<InputFilter> filters;

    protected Action createFrom;
    protected Action createTimelineFrom;
    public Action importAction;
    protected Action moveLeft;
    protected Action moveRight;
    protected Action exitAction;
    protected Action removeEvents;
    protected Action toggleOrphan;
    protected Action clearAction;
    protected Action clearAllAction;
    protected Action filterQueryAction;
    protected Action cutAction;
    public Action pasteAction;
    protected Action findAction;
    protected Action emptyTimeline;
    protected Action deleteTimeline;
    protected Action aboutAction;

    protected Action testAction;
    protected Action testAction2;
    public Action saveAction;
    protected Action loadAction;

    public Transferable cutBuffer = null;

    public Zeitline() {
        File currentWorkingDirectory = new File(System.getProperty("user.dir"));
        fileChooser = new JFileChooser(currentWorkingDirectory);
        fileChooser.addChoosableFileFilter(new FileInputFilter(PROJECT_FILE_EXTENSION, PROJECT_NAME));

        filters = loadPlugins();

        /* 'File' menu actions */

        loadAction = new LoadAction(this, "Load",
                createNavigationIcon("fileopen"),
                KeyEvent.VK_L);

        saveAction = new SaveAction(this, "Save",
                createNavigationIcon("filesave"),
                KeyEvent.VK_S);
        saveAction.setEnabled(false);

        exitAction = new ExitAction(this, "Exit", KeyEvent.VK_X);

        /* 'Edit' menu actions */

        cutAction = new CutAction(this, "Cut",
                createNavigationIcon("editcut"),
                KeyEvent.VK_T);
        cutAction.setEnabled(false);

        pasteAction = new PasteAction(this, "Paste",
                createNavigationIcon("editpaste"),
                KeyEvent.VK_P);
        pasteAction.setEnabled(false);

        clearAction = new ClearAction(this, "Clear Selection", KeyEvent.VK_C);
        clearAction.setEnabled(false);

        clearAllAction = new ClearAllAction(this, "Clear All Selections", KeyEvent.VK_A);

        findAction = new FindAction(this, "Find ...", createNavigationIcon("find"), KeyEvent.VK_D);
        findAction.setEnabled(false);

        /* 'Event' menu actions */

        createFrom = new CreateFromAction(this, "Create from ...",
                createNavigationIcon("create_event"),
                KeyEvent.VK_C);
        createFrom.setEnabled(false);

        removeEvents = new RemoveEventsAction(this, "Remove",
                createNavigationIcon("delete_event"),
                KeyEvent.VK_R);
        removeEvents.setEnabled(false);

        importAction = new ImportAction(this, "Import ...",
                createNavigationIcon("import"),
                KeyEvent.VK_I);

        /* 'Timeline' menu actions */

        emptyTimeline = new EmptyTimelineAction(this, "Create empty ...",
                createNavigationIcon("new_timeline"),
                KeyEvent.VK_E);
        createTimelineFrom = new CreateTimelineFromAction(this, "Create from ...",
                createNavigationIcon("create_timeline"),
                KeyEvent.VK_C);
        createTimelineFrom.setEnabled(false);

        deleteTimeline = new DeleteTimelineAction(this, "Delete",
                createNavigationIcon("delete_timeline"),
                KeyEvent.VK_D);
        deleteTimeline.setEnabled(false);

        moveLeft = new MoveLeftAction(this, "Move Left",
                createNavigationIcon("moveleft"),
                KeyEvent.VK_L);
        moveRight = new MoveRightAction(this, "Move Right",
                createNavigationIcon("moveright"),
                KeyEvent.VK_R);
        filterQueryAction = new FilterQueryAction(this, "Filter ...",
                createNavigationIcon("filter"),
                KeyEvent.VK_F);
        filterQueryAction.setEnabled(false);

        toggleOrphan = new ToggleOrphanAction(this, "Show Orphans", null, KeyEvent.VK_O);

        /* 'Help' menu actions */

        aboutAction = new AboutAction("About", KeyEvent.VK_A);

        /* actions for testing new code */

        testAction = new TestAction(this, "TEST", KeyEvent.VK_T);
        testAction2 = new TestAction2(this, "TEST2", KeyEvent.VK_2);


        displayMode = EventTree.DISPLAY_ALL;

    }

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

        rbMenuItem = new JRadioButtonMenuItem(new SetDisplayModeAction(this, "yyyy-mm-dd hh:mm:ss.d", KeyEvent.VK_Y, EventTree.DISPLAY_ALL));
        rbMenuItem.setSelected(true);
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem(new SetDisplayModeAction(this, "hh:mm:ss", KeyEvent.VK_H, EventTree.DISPLAY_HMS));
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

    }

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
    }

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

        timelines = new TimelineView(this, moveLeft, moveRight,
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

    void createAndShowGUI() {

        frame = new JFrame(APPLICATION_NAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setJMenuBar(createMenuBar());

        Component contents = createComponents();
        frame.getContentPane().add(contents, BorderLayout.CENTER);

        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);

    }

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


    private List<InputFilter> loadPlugins() {
        List<InputFilter> result = new ArrayList<InputFilter>();
        IFormGenerator formGenerator = new FormGenerator();
        PluginLoader loader = new PluginLoader(formGenerator);

        String location = Zeitline.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        List<InputFilter> plugins;

        // If the application is run from a JAR file, try to find embedded plugins
        if (Utils.containsCaseInsensitive(location, ".jar")) {
            plugins = loader.getPluginsFromJar(location, JAR_FILTERS_DIR);
            result.addAll(plugins);

            // Get the jar's directory
            location = new File(location).getParent();
        }
        
        // Look for the plugins in the 'filters' directory
        if (new File(location).isDirectory()) {
            String pluginsDir = location + PACKAGE_DIR + DYNAMIC_FILTERS_DIR;
            if ((plugins = loader.getPluginsFromDir(pluginsDir)) == null)
                return result;

            result.addAll(plugins);
        }

        return result;
    }

}
