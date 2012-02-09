package org.Zeitline.Event;

import java.io.Serializable;
import org.Zeitline.Timestamp.ITimestamp;

public abstract class TimeEvent
        implements Serializable, ITimeEvent<javax.swing.ImageIcon> {

    protected static long idCounter = 0; // next free unique id among the event class
    protected Long uniqueId;
    protected ITimestamp startTime;
    protected ComplexEvent parent; // null for complex events which are root
    protected boolean matchesQuery;


    public static void setIdCounter(long id) {
        idCounter = id;
    }

    public static long getIdCounter() {
        return idCounter;
    }

    public Long getId() {
        return uniqueId;
    }

    public void setStartTime(ITimestamp t) {
        // TODO: test the resort parent code
        startTime = t;
        if (parent != null)
            parent.resort(this, t);
    }

    public ITimestamp getStartTime() {
        return startTime;
    }

    public ITimestamp getEndTime() {
        // Assuming start = end times, override for classes
        // that have an end_time field
        return getStartTime();
    }

    public void setParent(ComplexEvent parent) {
        this.parent = parent;
    }

    public ComplexEvent getParent() {
        return parent;
    }

    public boolean startsBefore(ITimestamp t) {
        return startTime.before(t);
    }

    public boolean startsAfter(ITimestamp t) {
        return startTime.after(t);
    }

    public void setQueryFlag(boolean newValue) {
        matchesQuery = newValue;
    }

    public boolean matchesQuery() {
        return matchesQuery;
    }
}
