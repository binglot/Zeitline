package org.Zeitline.Event;

import org.Zeitline.AVLTree;
import org.Zeitline.GUI.Graphics.IconNames;
import org.Zeitline.GUI.Graphics.IconRepository;
import org.Zeitline.Query;
import org.Zeitline.Source;
import org.Zeitline.Timestamp.ITimestamp;
import org.Zeitline.Timestamp.Timestamp;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

public class ComplexEvent
        extends AbstractTimeEvent
        implements Serializable {

    protected static ImageIcon icon = new IconRepository().getIcon(IconNames.ComplexSmall);
    protected static boolean defaultDeleteEmpty = true;
    protected ITimestamp endTime;
    protected AVLTree childrenByTime;
    protected Vector sources;
    protected boolean deleteEmptyEvent;
    protected String name; // Name of the event will appear in the JTree listing
    protected String description;


    public ComplexEvent() {
        this("", "", false);
    }

    public ComplexEvent(String name) {
        this(name, "", false);
    }

    public ComplexEvent(String name, String description) {
        this(name, description, false);
    }

    public ComplexEvent(String name, String description, boolean pers) {
        this.sources = new Vector();
        //this.sources_updated = true;
        this.childrenByTime = new AVLTree();
        this.description = description;
        this.startTime = new Timestamp(-1);
        this.endTime = startTime;
        this.uniqueId = idCounter;
        idCounter++;
        this.name = name;
        this.deleteEmptyEvent = defaultDeleteEmpty;
        // this.name = name + " (id: " + this.uniqueId + ")";
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public ITimestamp getEndTime() {
        return endTime;
    }

    public ITimestamp getMaxStartTime() {
        ITimestamp ret = childrenByTime.getMaxStartTime();

        if (ret == null)
            ret = startTime;

        return ret;
    }

    public int countChildren() {
        return childrenByTime.getNodeCount();
    } // countChildren

    public void resort(AbstractTimeEvent event, ITimestamp new_start) {
        childrenByTime.resort(event, new_start);
    } // resort

    public void setDeleteEmptyEvent(boolean newVal) {
        deleteEmptyEvent = newVal;
    }

    public boolean getDeleteEmptyEvent() {
        return deleteEmptyEvent;
    }

    public static void setDefaultDelete(boolean newVal) {
        defaultDeleteEmpty = newVal;
    }

    /* This function returns "true" if the adding of the 
       event resulted in a status change of our parent
       as well. Otherwise, "false" is returned.
       It is assumed that the insertion into the org.Zeitline.AVLTree
       will always succeed!
    */
    public boolean addTimeEvent(AbstractTimeEvent event) {
        boolean ret = false;

        /*
      if (childrenByTime.getNodeCount() == 0) {
          System.out.println("Inserting into empty event (" + this +"): " + event);
          System.out.println(this + "'s start time: " + startTime);
          System.out.println(this + "'s end time: " + endTime);
          startTime = null;
          endTime = null;
      }
      */
//	if((this.startTime == null) ||
        if ((childrenByTime.getNodeCount() == 0) ||
                (event.getStartTime().before(this.startTime))) {
            if (parent != null) {
                parent.resort(this, event.getStartTime());
                ret = true;
            } else this.startTime = event.getStartTime();
        }

        if ((this.endTime == null) ||
                (event.getEndTime().after(this.endTime))) {
            this.endTime = event.getEndTime();
        }

        if (childrenByTime.add(event))
            event.setParent(this);

        //sources_updated = false;
        return ret;
    } // addTimeEvent

    /* This function returns "true" if the removal of the 
       event resulted in a status change of our parent
       as well. Otherwise, "false" is returned.
       It is assumed that the deletion of the org.Zeitline.AVLTree
       will always succeed!
    */
    public boolean removeTimeEvent(AbstractTimeEvent event) {
        //sources_updated = false;
        boolean ret = false;

        if (childrenByTime.remove(event)) {

            // for now always delete empty CEs (except for roots)
            // in future we might want a flag here also
            if ((childrenByTime.getNodeCount() == 0)) {
                if ((parent != null) && deleteEmptyEvent)
                    parent.removeTimeEvent(this);
                else {
                    //   startTime = new Timestamp(-1);
                    endTime = startTime;
                }
                return true;
            }

            if (startTime.equals(event.getStartTime())) {
                if (parent != null) {
                    //			System.out.println("Resorting parent");
                    parent.resort(this, childrenByTime.getMinStartTime());
                    ret = true;
                    //			parent.removeTimeEvent(this);
                    //			parent.addTimeEvent(this);
                } else
                    startTime = childrenByTime.getMinStartTime();
            }

            if (endTime.equals(event.getEndTime()))
                endTime = childrenByTime.getMaxEndTime();

        }

        return ret;
    } // removeTimeEvent

    public AbstractTimeEvent getEventByIndex(int i) {
        return (AbstractTimeEvent) childrenByTime.getElement(i);
    } // getEventByIndex

    public int getChildIndex(AbstractTimeEvent e) {
        return childrenByTime.getIndex(e);
    } // getChildIndex

    public Vector getChildren(ITimestamp start, ITimestamp end) {
        return childrenByTime.getInterval(start, end);
    } // getChildren

    public boolean computeQuery(Query q) {
        setQueryFlag(q.matches(this));

        for (Enumeration e = getChildren(null, null).elements(); e.hasMoreElements(); ) {
            AbstractTimeEvent ev = (AbstractTimeEvent) e.nextElement();
            if (ev instanceof ComplexEvent) {
                ComplexEvent ce = (ComplexEvent) ev;
                if (ce.computeQuery(q))
                    setQueryFlag(true);
            } else {
                ev.setQueryFlag(q.matches(ev));
                if (ev.matchesQuery())
                    setQueryFlag(true);
            }
        }

        return matchesQuery;
    } // computeQuery

    public void print() {
        System.out.println("Name: "
                + this.name
                + "\nStart: "
                + this.startTime
                + "\nEnd: "
                + this.endTime
                + "\nNumber of children: "
                //			+ childrenByTime.size());
                + childrenByTime.getNodeCount());
    } // print

    public Vector getSources() {
        //if(sources_updated) return sources;
        // TODO: this is horribly inefficient. Try to replace this code with
        // an armortized bookkeeping into a Vector when inserting and removing
        AbstractTimeEvent time_event = null;
        Source temp_source = null;
        sources.clear();
        for (int i = 0; i < childrenByTime.getNodeCount(); i++) {
            time_event = childrenByTime.getElement(i);
            if (time_event instanceof AtomicEvent) {
                temp_source = ((AtomicEvent) time_event).getSource();
                if (!sources.contains(temp_source)) sources.addElement(temp_source);
            } else if (time_event instanceof ComplexEvent) {
                Enumeration temp_sources = ((ComplexEvent) time_event).getSources().elements();
                while (temp_sources.hasMoreElements()) {
                    temp_source = (Source) temp_sources.nextElement();
                    if (!sources.contains(temp_source)) sources.addElement(temp_source);
                }
            }
        }

        //sources_updated = true;
        return sources;
    } // getSources

    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * Returns a string representation of the event. This is currently the
     * {@link #name name} field.
     *
     * @return a string representation of the event
     */
    public String toString() {
        return this.name;
    } // toString

    public String getQueryString() {
        return getDescription();
    }

} // class org.Zeitline.Event.ComplexEvent
