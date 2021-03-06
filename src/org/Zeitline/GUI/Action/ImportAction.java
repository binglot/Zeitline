package org.Zeitline.GUI.Action;

import org.Zeitline.Event.AtomicEvent;
import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.*;
import org.Zeitline.GUI.EventTree.EventTree;
import org.Zeitline.Plugin.Input.InputFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public class ImportAction
        extends AbstractAction
        implements StoppableRunnable {

    private static final String NAME = "Import ...";
    private static final String DESCRIPTION = "Import new events";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK);

    private boolean running;
    private JProgressBar progress_bar;
    private ProgressDlg pd;
    private InputFilter input_filter;
    private Source s;
    private Zeitline zeitline;
    private final List<InputFilter> inputFilters;

    public ImportAction(Zeitline zeitline, int mnemonic, List<InputFilter> inputFilters) {
        super(NAME);
        this.zeitline = zeitline;
        this.inputFilters = inputFilters;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);

        this.running = false;
        this.progress_bar = null;
    }

    public void actionPerformed(ActionEvent e) {
        if (ImportDlg.showDialog(zeitline.getFrame(), inputFilters) == ImportDlg.CANCEL_OPTION)
            return;

        input_filter = ImportDlg.getFilter();
        if (input_filter == null)
            return;

        zeitline.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        s = input_filter.init(ImportDlg.getFileName(), zeitline.getFrame());
        if (s == null) {
            return;
        }

        pd = new ProgressDlg(zeitline.getFrame(),
                "Importing Events",
                this);
        pd.setVisible(true);
        zeitline.getFrame().setCursor(Cursor.getDefaultCursor());
    }

    public void stop() {
        // override the deprecated stop() method to provide
        // an alternative method in halting thread execution
        running = false;
    }

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
            zeitline.getTimelines().addTree(tree, zeitline);
            // project has changed, enable the ability to save
            zeitline.getSaveAction().setEnabled(true);
        }

        // close the progress dialog
        pd.setVisible(false);
    }
}
