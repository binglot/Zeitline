package org.Zeitline.Event;

import org.Zeitline.Event.AtomicEvent;

import org.Zeitline.Source;
import org.Zeitline.Timestamp.Timestamp;

import java.io.Serializable;

/**
 * Class for the basic, discrete events that are imported from
 * various sources. Extra are: the source from where the event is
 * imported and a user identifier (though not currently used).
 */
public class GeneralEvent
        extends AtomicEvent
        implements Serializable {

    protected String name;
    protected String description;


    public GeneralEvent(String name, String description, Timestamp start, Source source) {
        this.description = description;
        this.startTime = start;
        this.reportedTime = start;
        this.adjustedTime = start;
        this.source = source;
        this.uniqueId = idCounter;
        idCounter++;
        this.name = name;
    }

    public GeneralEvent(String name, String description, Timestamp start) {
        this(name, description, start, null);
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setSource(Source s) {
        source = s;
    }

    public Source getSource() {
        return source;
    }

    public String toString() {
        return name;
    }

}
