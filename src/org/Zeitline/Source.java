package org.Zeitline;

import org.Zeitline.Event.AtomicEvent;

import java.io.Serializable;
import java.io.File;

import org.Zeitline.Timestamp.Timestamp;

import java.util.Date;
import java.util.Vector;

public class Source
        implements Serializable {

    public static final long GRANULARITY_MILLI = 1,
            GRANULARITY_SEC = 1000,
            GRANULARITY_MIN = 60000;

    private Vector event_list;
    private Timestamp created;
    private String name; // filter type name

    // none of the following variables are currently being used
    // these are for future reference and/or development only
    private String input_source; // full path file name
    private String host; // should there be a seperate Host object?
    private long granularity;
    private int userid;

    public Source(String name, String input_source, long granularity) {
        this.created = new Timestamp(new Date().getTime());
        this.name = name;
        this.input_source = input_source;
        this.granularity = granularity;
        this.event_list = new Vector();
    } // org.Zeitline.Source

    public void addEvent(AtomicEvent event) {
        event_list.add(event);
    }

    public boolean removeEvent(AtomicEvent event) {
        return event_list.remove(event);
    }

    public Vector getEvents() {
        return event_list;
    }

    public Timestamp getTimestamp() {
        return created;
    } // getTimestamp

    public String getName() {
        return name;
    } // getName

    public String toString() {
        String filename = (new File(input_source)).getName();
        return name + ": " + filename;
    } // toString
} // class org.Zeitline.Source
