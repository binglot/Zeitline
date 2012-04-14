package org.Zeitline.Event;

import org.Zeitline.GUI.Graphics.IIconRepository;
import org.Zeitline.GUI.Graphics.IconNames;
import org.Zeitline.GUI.Graphics.IconRepository;
import org.Zeitline.Source;
import org.Zeitline.Timestamp.ITimestamp;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.io.Serializable;

/**
 * Class for the basic, discrete events that are imported from
 * various sources. In addition to the fields and methods from the
 * abstract {@link AbstractTimeEvent} class, there are also fields
 * and methods for managing the source from where the event is
 * imported and a user identifier (though not currently used).
 */

public abstract class AtomicEvent
        extends AbstractTimeEvent
        implements Serializable {

    // TODO: The icon should be created by a different class.
    protected static ImageIcon icon = new IconRepository().getIcon(IconNames.AtomicSmall);
    // =
    protected Source source; // Source object from where the event was imported.
    protected ITimestamp reportedTime;
    protected ITimestamp adjustedTime; // WHAT IS IT?!

//    public AtomicEvent(IIconRepository<ImageIcon> iconRepository) {
//        icon = iconRepository.getIcon(IconNames.AtomicSmall);
//    }

    public void setSource(Source s) {
        if (source != null)
            s.removeEvent(this);

        source = s;
        s.addEvent(this);
    }

    public Source getSource() {
        return source;
    }

//    public void setAdjustedTime(ITimestamp newVal) {
//        adjustedTime = newVal;
//    }

    public ITimestamp getAdjustedTime() {
        return adjustedTime;
    }

    public ITimestamp getReportedTime() {
        return reportedTime;
    }

    // WHY IS IT RETURNING NULL?!
    public JPanel getPanel() {
        return null;
    }

    // WHY IS IT EMPTY?!
    public void setPanelValues() {
    }

    public ImageIcon getIcon() {
        return icon;
    }

//    public String getQueryString() {
//        return getDescription();
//    }

}
