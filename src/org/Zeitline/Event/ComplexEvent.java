package org.Zeitline.Event; /********************************************************************

 This file is part of org.Zeitline.Zeitline: a forensic timeline editor

 Written by Florian Buchholz and Courtney Falk.

 Copyright (c) 2004-2006 Florian Buchholz, Courtney Falk, Purdue
 University. All rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal with the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimers.
 Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimers in the
 documentation and/or other materials provided with the distribution.
 Neither the names of Florian Buchholz, Courtney Falk, CERIAS, Purdue
 University, nor the names of its contributors may be used to endorse
 or promote products derived from this Software without specific prior
 written permission.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NON-INFRINGEMENT.  IN NO EVENT SHALL THE CONTRIBUTORS OR COPYRIGHT
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE
 SOFTWARE.

 **********************************************************************/

import org.Zeitline.*;

import java.io.Serializable;

import org.Zeitline.Timestamp.ITimestamp;
import org.Zeitline.Timestamp.Timestamp;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.ImageIcon;

public class ComplexEvent
        extends TimeEvent
        implements Serializable {

    /**
     * End time of the event. This field denotes the endpoint of the
     * time interval formed with the startTime field, describing the
     * duration of the event.
     */
    protected ITimestamp end_time;
    protected AVLTree children_by_time;
    protected Vector sources;
    protected boolean deleteEmptyEvent;
    protected static boolean defaultDeleteEmpty = true;

    /**
     * Name of the event. The text in the name will appear in the JTree
     * listing of the {@link org.Zeitline.EventTree org.Zeitline.EventTree}.
     */
    protected String name;

    /**
     * Description of the event. This field contains all the
     * important information that doesn't fit in the name field.
     */
    protected String description;

    protected static ImageIcon icon = Zeitline.createNavigationIcon("complex_small");

    public ComplexEvent() {
        this("", "", false);
    } // org.Zeitline.Event.ComplexEvent()

    public ComplexEvent(String name) {
        this(name, "", false);
    } // org.Zeitline.Event.ComplexEvent(String)

    public ComplexEvent(String name, String description) {
        this(name, description, false);
    } // org.Zeitline.Event.ComplexEvent(String,String)

    public ComplexEvent(String name, String description, boolean pers) {
        this.sources = new Vector();
        //this.sources_updated = true;
        this.children_by_time = new AVLTree();
        this.description = description;
        this.startTime = new Timestamp(-1);
        this.end_time = startTime;
        this.uniqueId = new Long(idCounter);
        idCounter++;
        this.name = name;
        this.deleteEmptyEvent = this.defaultDeleteEmpty;
        // this.name = name + " (id: " + this.uniqueId + ")";
    } // org.Zeitline.Event.ComplexEvent(String,String,boolean)

    /**
     * Returns the name of the event.
     *
     * @return the name of the event, {@link #name name}
     */
    public String getName() {
        return name;
    } // getName

    /**
     * Returns the description of the event.
     *
     * @return the description of the event, {@link #description description}
     */
    public String getDescription() {
        return description;
    } // getDescription

    public ITimestamp getEndTime() {
        return end_time;
    } // getEndTime

    public ITimestamp getMaxStartTime() {
        ITimestamp ret = children_by_time.getMaxStartTime();
        if (ret == null)
            ret = startTime;
        return ret;
    } // getMaxStartTime

    public void setName(String name) {
        this.name = name;
    } // setName

    public void setDescription(String description) {
        this.description = description;
    } // setDescription

    public int countChildren() {
        return children_by_time.getNodeCount();
    } // countChildren

    public void resort(TimeEvent event, ITimestamp new_start) {
        children_by_time.resort(event, new_start);
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
    public boolean addTimeEvent(TimeEvent event) {
        boolean ret = false;

        /*
      if (children_by_time.getNodeCount() == 0) {
          System.out.println("Inserting into empty event (" + this +"): " + event);
          System.out.println(this + "'s start time: " + startTime);
          System.out.println(this + "'s end time: " + end_time);
          startTime = null;
          end_time = null;
      }
      */
//	if((this.startTime == null) ||
        if ((children_by_time.getNodeCount() == 0) ||
                (event.getStartTime().before(this.startTime))) {
            if (parent != null) {
                parent.resort(this, event.getStartTime());
                ret = true;
            } else this.startTime = event.getStartTime();
        }

        if ((this.end_time == null) ||
                (event.getEndTime().after(this.end_time))) {
            this.end_time = event.getEndTime();
        }

        if (children_by_time.add(event))
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
    public boolean removeTimeEvent(TimeEvent event) {
        //sources_updated = false;
        boolean ret = false;

        if (children_by_time.remove(event)) {

            // for now always delete empty CEs (except for roots)
            // in future we might want a flag here also
            if ((children_by_time.getNodeCount() == 0)) {
                if ((parent != null) && deleteEmptyEvent)
                    parent.removeTimeEvent(this);
                else {
                    //   startTime = new Timestamp(-1);
                    end_time = startTime;
                }
                return true;
            }

            if (startTime.equals(event.getStartTime())) {
                if (parent != null) {
                    //			System.out.println("Resorting parent");
                    parent.resort(this, children_by_time.getMinStartTime());
                    ret = true;
                    //			parent.removeTimeEvent(this);
                    //			parent.addTimeEvent(this);
                } else
                    startTime = children_by_time.getMinStartTime();
            }

            if (end_time.equals(event.getEndTime()))
                end_time = children_by_time.getMaxEndTime();

        }

        return ret;
    } // removeTimeEvent

    public TimeEvent getEventByIndex(int i) {
        return (TimeEvent) children_by_time.getElement(i);
    } // getEventByIndex

    public int getChildIndex(TimeEvent e) {
        return children_by_time.getIndex(e);
    } // getChildIndex

    public Vector getChildren(Timestamp start, Timestamp end) {
        return children_by_time.getInterval(start, end);
    } // getChildren

    public boolean computeQuery(Query q) {
        setQueryFlag(q.matches(this));

        for (Enumeration e = getChildren(null, null).elements(); e.hasMoreElements(); ) {
            TimeEvent ev = (TimeEvent) e.nextElement();
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
                + this.end_time
                + "\nNumber of children: "
                //			+ children_by_time.size());
                + children_by_time.getNodeCount());
    } // print

    public Vector getSources() {
        //if(sources_updated) return sources;
        // TODO: this is horribly inefficient. Try to replace this code with
        // an armortized bookkeeping into a Vector when inserting and removing
        TimeEvent time_event = null;
        Source temp_source = null;
        sources.clear();
        for (int i = 0; i < children_by_time.getNodeCount(); i++) {
            time_event = children_by_time.getElement(i);
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
