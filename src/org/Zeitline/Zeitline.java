package org.Zeitline;

import org.Zeitline.Event.AbstractTimeEvent;
import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.Event.Mask.AtomicEventMask;
import org.Zeitline.Event.Mask.ComplexEventMask;
import org.Zeitline.Event.Mask.L2TEventMask;
import org.Zeitline.GUI.Action.*;
import org.Zeitline.GUI.EventTree.EventTree;
import org.Zeitline.GUI.FeelAndLook;
import org.Zeitline.GUI.Graphics.IIconRepository;
import org.Zeitline.GUI.Graphics.IconNames;
import org.Zeitline.Plugin.Input.InputFilter;
import org.pushingpixels.flamingo.api.common.AbstractCommandButton;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class Zeitline implements TreeSelectionListener {
    public static final String APPLICATION_NAME = "Zeitline";
    public static final String APPLICATION_VERSION = "v0.3";

    private ComplexEventMask cem;
    private JSplitPane mainPane;
    private TimelineView timelines;
    private JPanel maskOverlay;

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
    private Action filterQueryAction;
    private Action cutAction;
    private Action pasteAction;
    private Action findAction;
    private Action emptyTimeline;
    private Action deleteTimeline;
    private Action aboutAction;
    private Action saveAction;
    private Action loadAction;
    private Action exportAction;

    private Transferable cutBuffer = null;

    public Zeitline(List<FileFilter> openFileFilters, List<InputFilter> inputFilters, IIconRepository<ImageIcon> iconRepository) {
        this.inputFilters = inputFilters;
        this.iconRepository = iconRepository;

        fileChooser = createOpenFileDialog(openFileFilters);
        setActionListeners();
    }

    public void createAndShowGUI() {
        frame = new JRibbonFrame(APPLICATION_NAME);
        List<RibbonTask> tasks = createRibbon();

        for (RibbonTask task : tasks) {
            frame.getRibbon().addTask(task);
        }

        // Add the 'About' button
        frame.getRibbon().configureHelp(getSmallIcon(IconNames.Info), aboutAction);

        Component contents = createComponents();
        frame.getContentPane().add(contents, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH); //getFrame().setSize(800, 600);
        frame.setVisible(true);
    }

    private void setActionListeners() {
        /* 'File' menu actions */
        saveAction = new SaveAction(this, KeyEvent.VK_S);
        loadAction = new LoadAction(this, KeyEvent.VK_L);
        exportAction = new ExportAction(this);
        exitAction = new ExitAction(KeyEvent.VK_X);

        /* 'Edit' menu actions */
        cutAction = new CutAction(this, KeyEvent.VK_T);
        pasteAction = new PasteAction(this, KeyEvent.VK_P);
        clearAction = new ClearAction(this, KeyEvent.VK_C);
        //clearAllAction = new ClearAllAction(this, KeyEvent.VK_A);
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

    private ResizableIcon getSmallIcon(IconNames icon) {
        return getIcon(icon, 16, 16);
    }

    private ResizableIcon getIcon(IconNames icon) {
        return getIcon(icon, 48, 48);
    }

    private ResizableIcon getIcon(IconNames icon, int width, int height) {
        return ImageWrapperResizableIcon.getIcon(iconRepository.getIconUrl(icon), new Dimension(width, height));
    }

    private List<RibbonTask> createRibbon() {
        List<RibbonTask> tasks = new ArrayList<>();

        //
        // Ribbon Project
        //

        /* 'File' band */
        JRibbonBand fileBand = new JRibbonBand("File", null);
        List<AbstractCommandButton> fileBandButtons1 = asList(
                createButton("Save", saveAction, IconNames.FileSave),
                createButton("Open", loadAction, IconNames.FileOpen),
                createButton("Export", exportAction, IconNames.FileExport)
        );
        List<AbstractCommandButton> fileBandButtons2 = asList(
                createButton("Exit", exitAction, IconNames.Exit)
        );
        addButtonsToBand(fileBand, fileBandButtons1, RibbonElementPriority.MEDIUM);
        fileBand.startGroup();
        addButtonsToBand(fileBand, fileBandButtons2, RibbonElementPriority.LOW);

        /* 'Edit' band */
        JRibbonBand editBand = new JRibbonBand("Edit", null);
        List<AbstractCommandButton> editBandButtons = asList(
                createButton("Cut", cutAction, IconNames.EditCut),
                createButton("Paste", pasteAction, IconNames.EditPaste)
        );
        addButtonsToBand(editBand, editBandButtons, RibbonElementPriority.MEDIUM);

        /* 'Event' band */
        JRibbonBand eventBand = new JRibbonBand("Event", null);
        List<AbstractCommandButton> eventBandButtons1 = asList(
                createButton("Group", createFrom, IconNames.Group),
                createButton("Remove", removeEvents, IconNames.DeleteEvent),
                createButton("Import", importAction, IconNames.Import)
        );
        List<AbstractCommandButton> eventBandButtons2 = asList(
                createButton("Find", findAction, IconNames.Find),
                createButton("Filter", filterQueryAction, IconNames.Filter)
        );
        addButtonsToBand(eventBand, eventBandButtons1, RibbonElementPriority.TOP);
        eventBand.startGroup();
        addButtonsToBand(eventBand, eventBandButtons2, RibbonElementPriority.TOP);

        /* 'Timeline' band */
        JRibbonBand timelineBand = new JRibbonBand("Timeline", null);
        List<AbstractCommandButton> timelineBandButtons1 = asList(
                createButton("Add", emptyTimeline, IconNames.NewTimeline),
                createButton("Delete", deleteTimeline, IconNames.DeleteTimeline),
                createButton("From Selected", createTimelineFrom, IconNames.CreateTimeline)
        );
        List<AbstractCommandButton> timelineBandButtons2 = asList(
                createButton("Move Left", moveLeft, IconNames.MoveLeft),
                createButton("Move Right", moveRight, IconNames.MoveRight)
        );
        List<AbstractCommandButton> timelineBandButtons3 = asList(
                createButton("Show Removed", toggleOrphan, IconNames.Orphan) // "Toggle Orphan"
        );
        addButtonsToBand(timelineBand, timelineBandButtons1, RibbonElementPriority.MEDIUM);
        timelineBand.startGroup();
        addButtonsToBand(timelineBand, timelineBandButtons2, RibbonElementPriority.TOP);
        timelineBand.startGroup();
        addButtonsToBand(timelineBand, timelineBandButtons3, RibbonElementPriority.LOW);

        //
        // Ribbon View
        //

        JRibbonBand displayBand = new JRibbonBand("Display", null);
        List<AbstractCommandButton> displayBandButtons = asList(
                createFormatPopupButton(),
                createOrderPopupButton(),
                createStylePopupButton()
        );
        addButtonsToBand(displayBand, displayBandButtons, RibbonElementPriority.TOP);

        //
        // Set Policies and add Ribbon Tasks
        //

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

    private AbstractCommandButton createFormatPopupButton() {
        List<JCommandMenuButton> buttons = asList(
                getChangeDisplayDateButton("yyyy-mm-dd hh:mm:ss", EventTree.DISPLAY_ALL, getIcon(IconNames.DateFull, 16, 16)),
                getChangeDisplayDateButton("hh:mm:ss", EventTree.DISPLAY_HMS, getIcon(IconNames.DateShort, 16, 16))
        );

        return createPopupButton("Format", IconNames.DateFormat, buttons);
    }

    private AbstractCommandButton createOrderPopupButton() {
        List<JCommandMenuButton> buttons = asList(
                getChangeDisplayDateButton("Ascending", EventTree.DISPLAY_ALL, getSmallIcon(IconNames.SortAsc)),
                getChangeDisplayDateButton("Descending", EventTree.DISPLAY_HMS, getSmallIcon(IconNames.SortDesc))
        );

        return createPopupButton("Arrange", IconNames.Sort, buttons);
    }

    private AbstractCommandButton createStylePopupButton() {
        org.Zeitline.GUI.FeelAndLook ui = new FeelAndLook();
        List<JCommandMenuButton> buttons = new ArrayList<>();
        
        for(String skin: ui.getSkins()){
            buttons.add(getChangeStyleButton(skin, ui, getSmallIcon(IconNames.Appearance)));
        }

        return createPopupButton("Style", IconNames.GraphicDesign, buttons);
    }

    private JCommandButton createPopupButton(String name, IconNames icon, final List<JCommandMenuButton> buttons) {
        JCommandButton formatButton = new JCommandButton(name, getIcon(icon));

        formatButton.setCommandButtonKind(JCommandButton.CommandButtonKind.POPUP_ONLY);
        formatButton.setPopupCallback(new PopupPanelCallback() {
            @Override
            public JPopupPanel getPopupPanel(JCommandButton commandButton) {
                JCommandPopupMenu menu = new JCommandPopupMenu();

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

    private void addButtonsToBand(JRibbonBand band, List<AbstractCommandButton> buttons, RibbonElementPriority priority) {
        for (AbstractCommandButton button : buttons) {
            band.addCommandButton(button, priority);
        }
    }

    private AbstractCommandButton createButton(String name, Action action, IconNames icon) {
        final AbstractCommandButton button = new JCommandButton(name, getIcon(icon));
        button.addActionListener(action);
        button.setEnabled(action.isEnabled());

        // JCommandButton doesn't integrate javax.swing.Action and therefore the hack below
        // i.e. if the action's 'enabled' field has been changed, so has the button's field
        action.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("enabled")) {
                    button.setEnabled((Boolean) evt.getNewValue());
                }
            }
        });

        return button;
    }

    private JCommandMenuButton getChangeDisplayDateButton(String format, final int mode, ResizableIcon icon) {
        JCommandMenuButton button = new JCommandMenuButton(format, icon);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDateFormatDisplay(mode);
            }
        });

        return button;
    }

    private JCommandMenuButton getChangeStyleButton(final String name, final FeelAndLook ui, ResizableIcon icon) {
        final Zeitline app = this;
        JCommandMenuButton button = new JCommandMenuButton(name, icon);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ui.setApp(app);
                ui.setUI(name);
            }
        });

        return button;
    }

    public void repaintAll(){
        mainPane.updateUI();
        timelines.updateUI();
        cem.updateUI();
        frame.getRibbon().updateUI();
        maskOverlay.updateUI();

        if (maskOverlay.getRootPane() != null)
            maskOverlay.getRootPane().updateUI();

        int taskNo = frame.getRibbon().getTaskCount();
        for (int i = 0; i < taskNo; i++) {
            RibbonTask task = frame.getRibbon().getTask(i);

            int bandNo = task.getBandCount();
            for (int j = 0; j < bandNo; j++) {
                task.getBand(j).updateUI();
            }
        }
    }

    private void setDateFormatDisplay(int mode) {
        EventTree.setDisplayMode(mode);
        this.getTimelines().redraw();
    }

    private Component createComponents() {
        // Create panel that contains the Event masks
        maskOverlay = new JPanel();
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

        //  Date after = new Date();
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
            exportAction.setEnabled(false);
            createFrom.setEnabled(false);
            createTimelineFrom.setEnabled(false);
            removeEvents.setEnabled(false);
        } else {
            cutAction.setEnabled(true);
            exportAction.setEnabled(true);
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
