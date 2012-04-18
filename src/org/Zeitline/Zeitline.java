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
import org.pushingpixels.flamingo.api.common.JCommandMenuButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.common.popup.JCommandPopupMenu;
import org.pushingpixels.flamingo.api.common.popup.JPopupPanel;
import org.pushingpixels.flamingo.api.common.popup.PopupPanelCallback;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
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

    private ComplexEventMask cem;
    private JSplitPane mainPane;
    private TimelineView timelines;

    private JRibbonFrame frame;

    protected JMenuItem menuMoveLeft, menuMoveRight;

    private final JFileChooser fileChooser;
    private final List<InputFilter> inputFilters;
    private final IIconRepository<ImageIcon> iconRepository;

    private Action createFrom;
    private Action createTimelineFrom;
    private Action importAction;
    private Action moveLeft;
    private Action moveRight;
    private Action exitAction;
    private Action removeEvents;
    private Action toggleOrphan;
    private Action clearAction;
    private Action clearAllAction;
    private Action filterQueryAction;
    private Action cutAction;
    private Action pasteAction;
    private Action findAction;
    private Action emptyTimeline;
    private Action deleteTimeline;
    private Action aboutAction;
    private Action saveAction;
    private Action loadAction;

    private Transferable cutBuffer = null;

    public Zeitline(List<FileFilter> openFileFilters, List<InputFilter> inputFilters, IIconRepository<ImageIcon> iconRepository) {
        this.inputFilters = inputFilters;
        this.iconRepository = iconRepository;

        fileChooser = createOpenFileDialog(openFileFilters);
        setActionListeners();
    }

    private void setActionListeners() {
        /* 'File' menu actions */
        saveAction = new SaveAction(this, KeyEvent.VK_S);
        loadAction = new LoadAction(this, KeyEvent.VK_L);
        exitAction = new ExitAction(KeyEvent.VK_X);

        /* 'Edit' menu actions */
        cutAction = new CutAction(this, KeyEvent.VK_T);
        pasteAction = new PasteAction(this, KeyEvent.VK_P);
        clearAction = new ClearAction(this, KeyEvent.VK_C);
        clearAllAction = new ClearAllAction(this, KeyEvent.VK_A);
        findAction = new FindAction(this, KeyEvent.VK_D);

        /* 'Event' menu actions */
        createFrom = new CreateFromAction(this, KeyEvent.VK_C);
        removeEvents = new RemoveEventsAction(this, KeyEvent.VK_R);
        importAction = new ImportAction(this, KeyEvent.VK_I, inputFilters);

        /* 'Timeline' menu actions */
        emptyTimeline = new EmptyTimelineAction(this, KeyEvent.VK_E);
        createTimelineFrom = new CreateTimelineFromAction(this, KeyEvent.VK_C);
        deleteTimeline = new DeleteTimelineAction(this, KeyEvent.VK_D);
        moveLeft = new MoveLeftAction(this, KeyEvent.VK_L);
        moveRight = new MoveRightAction(this, KeyEvent.VK_R);
        filterQueryAction = new FilterQueryAction(this, KeyEvent.VK_F);
        toggleOrphan = new ToggleOrphanAction(this, KeyEvent.VK_O);

        /* 'Help' menu actions */
        aboutAction = new AboutAction(this, KeyEvent.VK_A);
    }

    private static JFileChooser createOpenFileDialog(List<FileFilter> filters) {
        String currentDir = System.getProperty("user.dir");
        JFileChooser chooser = new JFileChooser(currentDir);

        for (final FileFilter filter : filters) {
            chooser.addChoosableFileFilter(filter);
        }

        return chooser;
    }

    public void createAndShowGUI() {
        frame = new JRibbonFrame(APPLICATION_NAME);
        List<RibbonTask> tasks = createRibbon();

        for (RibbonTask task : tasks) {
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

        /* 'File' band */
        JRibbonBand fileBand = new JRibbonBand("File", null);
        List<JCommandButton> fileBandButtons = asList(
                createButton("Save", saveAction, IconNames.FileSave),
                createButton("Open", loadAction, IconNames.FileOpen),
                createButton("Exit", exitAction, IconNames.Unknown)
        );
        addButtonsToBand(fileBand, fileBandButtons, RibbonElementPriority.LOW);

        /* 'Edit' band */
        JRibbonBand editBand = new JRibbonBand("Edit", null);
        List<JCommandButton> editBandButtons = asList(
                createButton("Cut", cutAction, IconNames.EditCut),
                createButton("Paste", pasteAction, IconNames.EditPaste),
                createButton("Clear", clearAction, IconNames.Unknown),
                createButton("Clear All", clearAllAction, IconNames.Unknown)
        );
        addButtonsToBand(editBand, editBandButtons, RibbonElementPriority.MEDIUM);

        /* 'Event' band */
        JRibbonBand eventBand = new JRibbonBand("Event", null);
        List<JCommandButton> eventBandButtons1 = asList(
                createButton("Bundle", createFrom, IconNames.CreateEvent),
                createButton("Remove", removeEvents, IconNames.DeleteEvent),
                createButton("Import", importAction, IconNames.Import)
        );
        List<JCommandButton> eventBandButtons2 = asList(
                createButton("Find", findAction, IconNames.Find),
                createButton("Filter", filterQueryAction, IconNames.Filter)
        );
        addButtonsToBand(eventBand, eventBandButtons1, RibbonElementPriority.TOP);
        eventBand.startGroup(); // Adds a separator
        addButtonsToBand(eventBand, eventBandButtons2, RibbonElementPriority.TOP);

        /* 'Timeline' band */
        JRibbonBand timelineBand = new JRibbonBand("Timeline", null);
        List<JCommandButton> timelineBandButtons1 = asList(
                createButton("Empty Timeline", emptyTimeline, IconNames.NewTimeline),
                createButton("Delete Timeline", deleteTimeline, IconNames.DeleteTimeline),
                createButton("Create Timeline", createTimelineFrom, IconNames.CreateTimeline)
        );
        List<JCommandButton> timelineBandButtons2 = asList(
                createButton("Move Left", moveLeft, IconNames.MoveLeft),
                createButton("Move Right", moveRight, IconNames.MoveRight)
        );
        List<JCommandButton> timelineBandButtons3 = asList(
                createButton("Show Removed", toggleOrphan, IconNames.Unknown) // "Toggle Orphan"
        );
        addButtonsToBand(timelineBand, timelineBandButtons1, RibbonElementPriority.MEDIUM);
        timelineBand.startGroup(); // Adds a separator
        addButtonsToBand(timelineBand, timelineBandButtons2, RibbonElementPriority.TOP);
        timelineBand.startGroup(); // Adds a separator
        addButtonsToBand(timelineBand, timelineBandButtons3, RibbonElementPriority.MEDIUM);

        //
        // Ribbon View
        //

        JRibbonBand displayBand = new JRibbonBand("Display", null);

        JCommandButton formatButton = createFormatButton();

        displayBand.addCommandButton(formatButton, RibbonElementPriority.TOP);

        //
        // Ribbon Help
        //

        /* 'Help' menu */
//
//        JRibbonBand helpBand = new JRibbonBand("Help", null);
//        JCommandButton aboutButton = new JCommandButton("About", getIcon(IconNames.Unknown));
//        helpBand.addCommandButton(aboutButton, RibbonElementPriority.TOP);
//        aboutButton.addActionListener(aboutAction);

        fileBand.setResizePolicies(getRibbonResizePolicies(fileBand));
        editBand.setResizePolicies(getRibbonResizePolicies(editBand));
        eventBand.setResizePolicies(getRibbonResizePolicies(eventBand));
        timelineBand.setResizePolicies(getRibbonResizePolicies(timelineBand));
        displayBand.setResizePolicies(getRibbonResizePolicies(displayBand));

        RibbonTask projectTask = new RibbonTask("Project", fileBand, editBand, eventBand, timelineBand);
        RibbonTask viewTask = new RibbonTask("View", displayBand);

        tasks.add(projectTask);
        tasks.add(viewTask);


        return tasks;
    }

    private JCommandButton createFormatButton() {
        JCommandButton formatButton = new JCommandButton("Format", getIcon(IconNames.Unknown));

        formatButton.setCommandButtonKind(JCommandButton.CommandButtonKind.POPUP_ONLY);
        formatButton.setPopupCallback(new PopupPanelCallback() {
            @Override
            public JPopupPanel getPopupPanel(JCommandButton commandButton) {
                JCommandPopupMenu menu = new JCommandPopupMenu();
                List<JCommandMenuButton> buttons = asList(
                        getChangeDisplayDateButton("yyyy-mm-dd hh:mm:ss", EventTree.DISPLAY_ALL),
                        getChangeDisplayDateButton("hh:mm:ss", EventTree.DISPLAY_HMS)
                );

                for (JCommandMenuButton button : buttons) {
                    menu.addMenuButton(button);
                }

                return menu;
            }
        });

        return formatButton;
    }

    private List<RibbonBandResizePolicy> getRibbonResizePolicies(JRibbonBand band) {
        return Arrays.<RibbonBandResizePolicy>asList(
                new CoreRibbonResizePolicies.None(band.getControlPanel()),
                new CoreRibbonResizePolicies.Mirror(band.getControlPanel()),
                new CoreRibbonResizePolicies.Mid2Low(band.getControlPanel()),
                new CoreRibbonResizePolicies.High2Low(band.getControlPanel())
        );
    }

    private void addButtonsToBand(JRibbonBand band, List<JCommandButton> buttons, RibbonElementPriority priority) {
        for (JCommandButton button : buttons) {
            band.addCommandButton(button, priority);
        }
    }

    private JCommandButton createButton(String name, Action action, IconNames icon) {
        JCommandButton button = new JCommandButton(name, getIcon(icon));
        button.addActionListener(action);

        return button;
    }

    private JCommandMenuButton getChangeDisplayDateButton(String format, final int mode) {
        JCommandMenuButton button = new JCommandMenuButton(format, null);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDateFormatDisplay(mode);
            }
        });

        return button;
    }

    private void setDateFormatDisplay(int mode) {
        EventTree.setDisplayMode(mode);
        this.getTimelines().redraw();
    }

    private Component createComponents() {
        // Create panel that contains the Event masks
        JPanel maskOverlay = new JPanel();
        OverlayLayout layoutManager = new OverlayLayout(maskOverlay);
        maskOverlay.setLayout(layoutManager);

        cem = new ComplexEventMask();
        AtomicEventMask aem = new AtomicEventMask();
        L2TEventMask lem = new L2TEventMask();
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
                cem, aem, lem);

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

    public Action getPasteAction() {
        return pasteAction;
    }

}
