package org.Zeitline;

import org.Zeitline.Event.AtomicEvent;

import java.io.Serializable;
import java.io.File;

import org.Zeitline.Timestamp.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class Source
        implements Serializable {

    public static final long GRANULARITY_MILLI = 1;
    public static final long GRANULARITY_SEC = 1000;
    public static final long GRANULARITY_MIN = 60000;

    private final List<AtomicEvent> eventList = new ArrayList<AtomicEvent>();
    private Timestamp created;
    private String name; // filter type name

    // none of the following variables are currently being used
    // these are for future reference and/or development only
    private String inputSource; // full path file name
    private String host; // should there be a seperate Host object?
    private long granularity;
    private int userId;

    public Source(String name, String inputSource, long granularity) {
        this.created = new Timestamp(new Date().getTime());
        this.name = name;
        this.inputSource = inputSource;
        this.granularity = granularity;
    }

    public void addEvent(AtomicEvent event) {
        eventList.add(event);
    }

    public boolean removeEvent(AtomicEvent event) {
        return eventList.remove(event);
    }

    public List<AtomicEvent> getEvents() {
        return eventList;
    }

    public Timestamp getTimestamp() {
        return created;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        String filename = (new File(inputSource)).getName();
        return name + ": " + filename;
    }
}