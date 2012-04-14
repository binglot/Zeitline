package org.Zeitline.Event;

import org.Zeitline.GUI.IFormGenerator;
import org.Zeitline.GUI.IFormItem;
import org.Zeitline.Timestamp.ITimestamp;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class L2tEvent
        extends AtomicEvent
        implements Serializable {

    private static JPanel panel = null;

    private final ITimestamp time;
    private final String timezone;
    private final String macb;
    private final String source;
    private final String sourceType;
    private final String type;
    private final String user;
    private final String host;
    private final String shortDesc;
    private final String desc;
    private final int version;
    private final String filename;
    private final long inode;
    private final String notes;
    private final String format;
    private final String extra;
    private final IFormGenerator<IFormItem> formGenerator;

    public L2tEvent(ITimestamp time, String timezone, String macb, String source, String sourceType, String type, String user,
                    String host, String shortDesc, String desc, int version, String filename, long inode, String notes, 
                    String format, String extra, IFormGenerator<IFormItem> formGenerator){

        this.time = time;
        this.timezone = timezone;
        this.macb = macb;
        this.source = source;
        this.sourceType = sourceType;
        this.type = type;
        this.user = user;
        this.host = host;
        this.shortDesc = shortDesc;
        this.desc = desc;
        this.version = version;
        this.filename = filename;
        this.inode = inode;
        this.notes = notes;
        this.format = format;
        this.extra = extra;
        this.formGenerator = formGenerator;

        // Inherited bad behaviour from the AtomicEvent class.
        // Will need need to fix it!
        adjustedTime = startTime;
        reportedTime = startTime;
        // =
    }

    // As presented on the main panel, along with its date.
    @Override
    public String getName() {
        return macb + " "  + sourceType;
    }

    // It's used to sort events (FilterComparator class).
    @Override
    public String toString(){
        return getName();
    }

    //
    // Description is not used directly to be display but rather when performing string searches.
    //
    @Override
    public String getDescription() {

        // First the most important entries
        return "Source Type: " + sourceType +
                "\nSource: " + source +
                "\nMACB: " + macb +
                "\nShort: " + shortDesc +

        // Then the rest (ex. 'format' field)
                "\nType: " + type +
                "\nUser: " + user +
                "\nHost: " + host +
                "\nDescription: " + desc +
                "\nVersion: " + version +
                "\nFilename: " + filename +
                "\nInode: " + inode +
                "\nNotes: " + notes +
                "\nExtra: " + extra;
    }

    public JPanel getPanel() {
        if (panel == null)
            initPanel();
        return panel;
    }

    //
    // Labels that are shown when an entry is selected.
    //
    private void initPanel() {
        String[] labelNames = new String[] {
                "Source Type:",
                "Source:",
                "MACB:",
                "Short:",
                "Type:",
                "User:",
                "Host:",
                "Description:",
                "Version:",
                "Filename:",
                "Inode:",
                "Notes:",
                "Extra:"
        };

        List<IFormItem> items = new ArrayList<IFormItem>();
        for (String name: labelNames){
            items.add(getFormItem(name));
        }

        panel = formGenerator.createForm(items);
    }
    
    private IFormItem getFormItem(String name) {
        JLabel label = new JLabel("", JLabel.LEADING);
        return formGenerator.getFormItem(name, label);
    }

    public ITimestamp getTime() {
        return time;
    }

    public String getTimezone() {
        return timezone;
    }
}
