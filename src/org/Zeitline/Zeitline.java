package org.Zeitline;

import org.Zeitline.Event.AbstractTimeEvent;
import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.Event.Mask.AtomicEventMask;
import org.Zeitline.Event.Mask.ComplexEventMask;
import org.Zeitline.Event.Mask.L2TEventMask;
import org.Zeitline.GUI.Action.*;
import org.Zeitline.GUI.EventTree.EventTree;
import org.Zeitline.GUI.Graphics.IIconRepository;
import org.Zeitline.GUI.Graphics.IconNames;
import org.Zeitline.Plugin.Input.InputFilter;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.common.model.ActionButtonModel;
import org.pushingpixels.flamingo.api.common.popup.JPopupPanel;
import org.pushingpixels.flamingo.api.common.popup.PopupPanelCallback;
import org.pushingpixels.flamingo.api.ribbon.*;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.IconRibbonBandResizePolicy;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

public class Zeitline implements TreeSelectionListener {
    public static final String APPLICATION_NAME = "Zeitline";
    public static final String APPLICATION_VERSION = "v0.3";

    protected EventTree tree;
    private ComplexEventMask cem;
    protected AtomicEventMask aem;
    private L2TEventMask lem;
    private JSplitPane mainPane;
    private TimelineView timelines;

    protected int displayMode;

    private JRibbonFrame frame;

    protected JMenuItem menuMoveLeft, menuMoveRight;

    private final JFileChooser fileChooser;
    private final List<InputFilter> inputFilters;
    private final IIconRepository<ImageIcon> iconRepository;

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
    private Action saveAction;
    protected Action loadAction;

    private Transferable cutBuffer = null;

    public Zeitline(List<FileFilter> openFileFilters, List<InputFilter> inputFilters, IIconRepository<ImageIcon> iconRepository) {
        this.inputFilters = inputFilters;
        this.iconRepository = iconRepository;

        fileChooser = CreateOpenFileDialog(openFileFilters);
    }

    private static JFileChooser CreateOpenFileDialog(List<FileFilter> filters) {
        String currentDir = System.getProperty("user.dir");
        JFileChooser chooser = new JFileChooser(currentDir);

        for(final FileFilter filter: filters){
            chooser.addChoosableFileFilter(filter);
        }

        return chooser;
    }

    public void createAndShowGUI() {
        frame = new JRibbonFrame(APPLICATION_NAME);
        List<RibbonTask> tasks = createRibbon();

        for (RibbonTask task: tasks){
            frame.getRibbon().addTask(task);
        }

        Component contents = createComponents();
        getFrame().getContentPane().add(contents, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getFrame().pack();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH); //getFrame().setSize(800, 600);
        getFrame().setVisible(true);
    }

    private ResizableIcon getIcon(IconNames icon) {
        return ImageWrapperResizableIcon.getIcon(iconRepository.getIconUrl(icon), new Dimension(48, 48));
    }

    private List<RibbonTask> createRibbon() {
        List<RibbonTask> tasks = new ArrayList<RibbonTask>();

        //
        // Ribbon Project
        //

        /* 'File' menu actions */
        saveAction = new SaveAction(this, KeyEvent.VK_S);
        loadAction = new LoadAction(this, KeyEvent.VK_L);
        exitAction = new ExitAction(KeyEvent.VK_X);

        JRibbonBand fileBand = new JRibbonBand("File", null);
        JCommandButton saveButton = new JCommandButton("Save", getIcon(IconNames.FileSave));
        JCommandButton openButton = new JCommandButton("Open", getIcon(IconNames.FileOpen));
        JCommandButton exitButton = new JCommandButton("Exit", getIcon(IconNames.Unknown));
        fileBand.addCommandButton(saveButton, RibbonElementPriority.TOP);
        fileBand.addCommandButton(openButton, RibbonElementPriority.TOP);
        fileBand.addCommandButton(exitButton, RibbonElementPriority.TOP);
        saveButton.addActionListener(saveAction);
        openButton.addActionListener(loadAction);
        exitButton.addActionListener(exitAction);

        /* 'Edit' menu actions */
        cutAction = new CutAction(this, KeyEvent.VK_T);
        pasteAction = new PasteAction(this, KeyEvent.VK_P);
        clearAction = new ClearAction(this, KeyEvent.VK_C);
        clearAllAction = new ClearAllAction(this, KeyEvent.VK_A);
        findAction = new FindAction(this, KeyEvent.VK_D);

        JRibbonBand editBand = new JRibbonBand("Edit", getIcon(IconNames.Unknown));
        JCommandButton cutButton = new JCommandButton("Cut", getIcon(IconNames.EditCut));
        JCommandButton pasteButton = new JCommandButton("Paste", getIcon(IconNames.EditPaste));
        JCommandButton clearButton = new JCommandButton("Clear", getIcon(IconNames.Unknown));
        JCommandButton clearAllButton = new JCommandButton("Clear All", getIcon(IconNames.Unknown));
        JCommandButton findButton = new JCommandButton("Find", getIcon(IconNames.Find));
        editBand.addCommandButton(cutButton, RibbonElementPriority.TOP);
        editBand.addCommandButton(pasteButton, RibbonElementPriority.TOP);
        editBand.addCommandButton(clearButton, RibbonElementPriority.TOP);
        editBand.addCommandButton(clearAllButton, RibbonElementPriority.TOP);
        editBand.addCommandButton(findButton, RibbonElementPriority.TOP);
        cutButton.addActionListener(cutAction);
        pasteButton.addActionListener(pasteAction);
        clearButton.addActionListener(clearAction);
        clearAllButton.addActionListener(clearAllAction);
        findButton.addActionListener(findAction);

        /* 'Event' menu actions */
        createFrom = new CreateFromAction(this, KeyEvent.VK_C);
        removeEvents = new RemoveEventsAction(this, KeyEvent.VK_R);
        importAction = new ImportAction(this, KeyEvent.VK_I, inputFilters);

        JRibbonBand eventBand = new JRibbonBand("Event", getIcon(IconNames.Unknown));
        JCommandButton createFromButton = new JCommandButton("Create From", getIcon(IconNames.CreateEvent));
        JCommandButton removeEventsButton = new JCommandButton("Remove", getIcon(IconNames.DeleteEvent));
        JCommandButton importButton = new JCommandButton("Import", getIcon(IconNames.Import));
        eventBand.addCommandButton(createFromButton, RibbonElementPriority.TOP);
        eventBand.addCommandButton(removeEventsButton, RibbonElementPriority.TOP);
        eventBand.addCommandButton(importButton, RibbonElementPriority.TOP);
        createFromButton.addActionListener(createFrom);
        removeEventsButton.addActionListener(removeEvents);
        importButton.addActionListener(importAction);

        /* 'Timeline' menu actions */
        emptyTimeline = new EmptyTimelineAction(this, KeyEvent.VK_E);
        createTimelineFrom = new CreateTimelineFromAction(this, KeyEvent.VK_C);
        deleteTimeline = new DeleteTimelineAction(this, KeyEvent.VK_D);
        moveLeft = new MoveLeftAction(this, KeyEvent.VK_L);
        moveRight = new MoveRightAction(this, KeyEvent.VK_R);
        filterQueryAction = new FilterQueryAction(this, KeyEvent.VK_F);
        toggleOrphan = new ToggleOrphanAction(this, KeyEvent.VK_O);

        JRibbonBand timelineBand = new JRibbonBand("Timeline", null);
        JCommandButton emptyTimelineButton = new JCommandButton("Empty Timeline", getIcon(IconNames.NewTimeline));
        JCommandButton createTimelineButton = new JCommandButton("Create Timeline", getIcon(IconNames.CreateTimeline));
        JCommandButton deleteTimelineButton = new JCommandButton("Delete Timeline", getIcon(IconNames.DeleteTimeline));
        JCommandButton MoveLeftButton = new JCommandButton("Move Left", getIcon(IconNames.MoveLeft));
        JCommandButton MoveRightButton = new JCommandButton("Move Right", getIcon(IconNames.MoveRight));
        JCommandButton FilterQueryButton = new JCommandButton("Filter Query", getIcon(IconNames.Filter));
        JCommandButton toggleOrphanButton = new JCommandButton("Toggle Orphan", getIcon(IconNames.Unknown));
        timelineBand.addCommandButton(emptyTimelineButton, RibbonElementPriority.TOP);
        timelineBand.addCommandButton(createTimelineButton, RibbonElementPriority.TOP);
        timelineBand.addCommandButton(deleteTimelineButton, RibbonElementPriority.TOP);
        timelineBand.addCommandButton(MoveLeftButton, RibbonElementPriority.TOP);
        timelineBand.addCommandButton(MoveRightButton, RibbonElementPriority.TOP);
        timelineBand.addCommandButton(FilterQueryButton, RibbonElementPriority.TOP);
        timelineBand.addCommandButton(toggleOrphanButton, RibbonElementPriority.TOP);
        emptyTimelineButton.addActionListener(emptyTimeline);
        createTimelineButton.addActionListener(createTimelineFrom);
        deleteTimelineButton.addActionListener(deleteTimeline);
        MoveLeftButton.addActionListener(moveLeft);
        MoveRightButton.addActionListener(moveRight);
        FilterQueryButton.addActionListener(filterQueryAction);
        toggleOrphanButton.addActionListener(toggleOrphan);

        //
        // Ribbon Help
        //

        /* 'Help' menu actions */
        aboutAction = new AboutAction(this, KeyEvent.VK_A);

        JRibbonBand helpBand = new JRibbonBand("Help", null);
        JCommandButton aboutButton = new JCommandButton("About", getIcon(IconNames.Unknown));
        helpBand.addCommandButton(aboutButton, RibbonElementPriority.TOP);
        aboutButton.addActionListener(aboutAction);

        /* Actions for testing new code */
//        testAction = new TestAction(this, "TEST", KeyEvent.VK_T);
//        testAction2 = new TestAction2(this, "TEST2", KeyEvent.VK_2);

        fileBand.setResizePolicies(Arrays.<RibbonBandResizePolicy>asList(new CoreRibbonResizePolicies.None(fileBand.getControlPanel()),
                new IconRibbonBandResizePolicy(fileBand.getControlPanel())));
        editBand.setResizePolicies(Arrays.<RibbonBandResizePolicy>asList(new CoreRibbonResizePolicies.None(editBand.getControlPanel()),
                new IconRibbonBandResizePolicy(editBand.getControlPanel())));
        eventBand.setResizePolicies(Arrays.<RibbonBandResizePolicy>asList(new CoreRibbonResizePolicies.None(eventBand.getControlPanel()),
                new IconRibbonBandResizePolicy(eventBand.getControlPanel())));
        timelineBand.setResizePolicies(Arrays.<RibbonBandResizePolicy>asList(new CoreRibbonResizePolicies.None(timelineBand.getControlPanel()),
                new IconRibbonBandResizePolicy(timelineBand.getControlPanel())));
        helpBand.setResizePolicies(Arrays.<RibbonBandResizePolicy>asList(new CoreRibbonResizePolicies.None(helpBand.getControlPanel()),
                new IconRibbonBandResizePolicy(helpBand.getControlPanel())));

        RibbonTask projectTask = new RibbonTask("Project", fileBand, editBand, eventBand, timelineBand);
        RibbonTask helpTask = new RibbonTask("Help", helpBand);

        tasks.add(projectTask);
        tasks.add(helpTask);


        return tasks;
    }

    public JMenuBar createMenuBar() {

        JMenuBar menuBar;
        JMenu menu;
        List<JMenu> menus = new ArrayList<JMenu>();
        List<Action> actions;

        menuBar = new JMenuBar();

        actions = asList(saveAction, loadAction, exitAction);
        menus.add(CreateMenu("File", actions, KeyEvent.VK_F));

        actions = asList(cutAction, pasteAction, clearAction, clearAllAction, findAction);
        menus.add(CreateMenu("Edit", actions, KeyEvent.VK_E));

        actions = asList(createFrom, removeEvents, importAction);
        menus.add(CreateMenu("Event", actions, KeyEvent.VK_N));

        actions = asList(emptyTimeline, createTimelineFrom, deleteTimeline, moveLeft, moveRight, filterQueryAction);
        menu = CreateMenu("Timeline", actions, KeyEvent.VK_T);
        JMenuItem menuItem = new JCheckBoxMenuItem(toggleOrphan);
        menu.add(menuItem);
        menus.add(menu);

        actions = asList();
        menu = CreateMenu("View", actions, KeyEvent.VK_V);
        JMenu submenu = CreateDateFormatSubMenu();
        menu.add(submenu);
        menus.add(menu);

        actions = asList(aboutAction);
        menus.add(CreateMenu("Help", actions, KeyEvent.VK_H));

        for(final JMenu menuToAdd: menus){
            menuBar.add(menuToAdd);
        }

        return menuBar;
    }

    private JMenu CreateDateFormatSubMenu() {
        JMenu submenu;
        JRadioButtonMenuItem rbMenuItem;
        submenu = new JMenu("Time Display");
        submenu.setMnemonic(KeyEvent.VK_D);

        ButtonGroup group = new ButtonGroup();

        rbMenuItem = new JRadioButtonMenuItem(new SetDisplayModeAction(this, "yyyy-mm-dd hh:mm:ss", KeyEvent.VK_Y, EventTree.DISPLAY_ALL));
        rbMenuItem.setSelected(true);
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem(new SetDisplayModeAction(this, "hh:mm:ss", KeyEvent.VK_H, EventTree.DISPLAY_HMS));
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);
        return submenu;
    }

    private Component createComponents() {
//        long ts;
//        Date afterInsert = new Date();

        // Create panel that contains the Event masks
        JPanel maskOverlay = new JPanel();
        OverlayLayout layoutManager = new OverlayLayout(maskOverlay);
        maskOverlay.setLayout(layoutManager);

        cem = new ComplexEventMask();
        aem = new AtomicEventMask();
        lem = new L2TEventMask();
        maskOverlay.add(cem);
        maskOverlay.add(aem);
        maskOverlay.add(lem);
        maskOverlay.setMinimumSize(cem.getPreferredSize());
        cem.setVisible(false);
        aem.setVisible(false);
        lem.setVisible(false);

        timelines = new TimelineView(this, moveLeft, moveRight,
                filterQueryAction, deleteTimeline,
                getSaveAction(), pasteAction,
                cutAction, clearAction, findAction,
                getCem(), aem, lem);

        mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                getTimelines(), new JScrollPane(maskOverlay));


        getMainPane().setOneTouchExpandable(true);
        getMainPane().setResizeWeight(1.0);

        JPanel mainCanvas = new JPanel(new BorderLayout());

        mainCanvas.add(getMainPane(), BorderLayout.CENTER);

        Date after = new Date();
        //	System.out.println("Drawing GUI: " + (after.getTime() - afterInsert.getTime()));

        return mainCanvas;

    }

    public void valueChanged(TreeSelectionEvent e) {
        EventTree tree = (EventTree) e.getSource();

        int count = tree.getSelectionCount();

        if (count != 1)
            pasteAction.setEnabled(false);
        else {
            AbstractTimeEvent te = (AbstractTimeEvent) tree.getLastSelectedPathComponent();
            pasteAction.setEnabled((te instanceof ComplexEvent) &&
                    (getCutBuffer() != null));
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

    }

    private void AddMenuItem(JMenu menu, Action action) {
        JMenuItem menuItem;
        menuItem = createMenuItem(action);

        menu.add(menuItem);
    }

    private JMenu CreateMenu(String name, List<Action> actions, int mnemonic) {
        JMenu menu = new JMenu(name);

        menu.setMnemonic(mnemonic);
        for(final Action action: actions) {
            AddMenuItem(menu, action);
        }

        return menu;
    }

    public JButton createButton(Action a) {
        JButton b = new JButton(a);
        b.setText(null);
        b.setMnemonic(0);
        String name = (String) a.getValue(a.NAME);

        InputMap imap = b.getInputMap(b.WHEN_IN_FOCUSED_WINDOW);
        KeyStroke ks = (KeyStroke) a.getValue(a.ACCELERATOR_KEY);
        imap.put(ks, name);

        return b;
    }

    public JMenuItem createMenuItem(Action a) {

        JMenuItem m = new JMenuItem(a);
        m.setIcon(null);

        return m;

    }


    public ComplexEventMask getCem() {
        return cem;
    }

    public JSplitPane getMainPane() {
        return mainPane;
    }

    public TimelineView getTimelines() {
        return timelines;
    }

    public Action getSaveAction() {
        return saveAction;
    }

    public Transferable getCutBuffer() {
        return cutBuffer;
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    public void setCutBuffer(Transferable cutBuffer) {
        this.cutBuffer = cutBuffer;
    }

    public JFrame getFrame() {
        return frame;
    }

}
