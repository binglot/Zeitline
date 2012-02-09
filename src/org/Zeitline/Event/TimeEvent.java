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

import org.Zeitline.Event.ComplexEvent;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.swing.ImageIcon;

/**

The abstract class for the time events. Here, common fields and methods
for all the event subclasses are defined.

*/
public abstract class TimeEvent
    implements Serializable {
    
    /**
     * A class variable set to the next free unique id among the
     * event classes.
     */
    protected static long id_counter = 0;
    
    /**
     * Time when event occurred. Some events will only have a start time,
     * in which case the time of the event is discrete. Other events may
     * have an end time field, in which case the duration of the event
     * is specified by the interval between start and end times.
     */
    protected Timestamp start_time;


    /**
     * Parent object of the event. Atomic events always have a parent,
     * while complex events may have a <tt> null </tt> parent when
     * they are the root event.
     */
    protected ComplexEvent parent;

    /**
     * The unique identifier of the event
     */
    protected Long unique_id;

    /**
     * A boolean flag that indicates whether the event matches
     * a query run during a filter operation
     */
    protected boolean matches_query;
        
    /**
     * Initializes the unique identifier counter.
     *
     * @param init_id the new value for the counter
     */
    public static void initIDCounter(long init_id) {
	id_counter = init_id;
    } // initIDCounter
      
    /**
     * Returns the unique identifer counter, which is a class variable.
     *
     * @return the value of {@link #id_counter id_counter}, which
     * points to the next available unique identifier
     */
    public static long getIDCounter() {
	return id_counter;
    } // getIDCounter

    /**
     * Returns the unique identifier of the event.
     *
     * @return the unique id of the event, {@link #unique_id unique_id}
     */
    public Long getID() {
	return unique_id;
    } // getID

    /**
     * Returns the start time of the event.
     *
     * @return the start time of the event, {@link #start_time start_time}
     */
    public Timestamp getStartTime() {
	return start_time;
    } // getStartTime
    
    /**
     * Sets the start time of the event.
     *
     * @param t the new start time of the event
     */
    public void setStartTime(Timestamp t) {
	// TODO: test the resort parent code
	start_time = t;
	if (parent != null)
		parent.resort(this, t);
    } // setStartTime

    /**
     * Returns the end time of the event. For classes that do not have
     * an <tt> end_time </tt> field, the start time is returned. The
     * classes that have an <tt> end_time </tt> field must override
     * this method.
     *
     * @return the end time of the event
     */
    public Timestamp getEndTime() {
	// Assuming start = end times, override for classes
	// that have an end_time field
	return getStartTime();
    } // getEndTime
    
    /**
     * Returns the name of the event. Needs to be implemented by
     * a non-abstract sub-class.
     *
     * @return the name of the event.
     */
    public abstract String getName();
    
    /**
     * Returns the description of the event. Needs to be implemented
     * by a non-abstract sub-class.
     *
     * @return the description of the event.
     */
    public abstract String getDescription();
    
    /**
     * Returns the parent event of the event.
     *
     * @return the parent event of the event, {@link #parent parent}
     */
    public ComplexEvent getParent() {
	return parent;
    } // getParent
    
    /**
     * Sets the parent event for this event.
     *
     * @param parent the new parent object for this event
     */
    public void setParent(ComplexEvent parent) {
	this.parent = parent;
    } // setParent
    
    /**
     * Determines if the event starts before a given Timestamp. This
     * is equivalent to calling <tt> getStartTime().before(t) </tt>.
     *
     * @param t the Timestamp to compare against
     * @return <tt> true </tt> if the event's start time is before
     * <tt>t</tt> <br /> <tt> false </tt> if the event's start time is
     * after or at <tt>t</tt>
     */
    public boolean startsBefore(Timestamp t) {
	return start_time.before(t);
    } // startsBefore
    
    /**
     * Determines if the event starts after a given Timestamp. This
     * is equivalent to calling <tt> getStartTime().after(t) </tt>.
     *
     * @param t the Timestamp to compare against
     * @return <tt> true </tt> if the event's start time is after
     * <tt>t</tt> <br /> <tt> false </tt> if the event's start time is
     * before or at <tt>t</tt>
     */
    public boolean startsAfter(Timestamp t) {
	return start_time.after(t);
    } // startsAfter
    
    /**
     * Sets the {@link #matches_query matches_query} flag. This is
     * currently used only for the org.Zeitline.Event.ComplexEvent's {@link
     * ComplexEvent#computeQuery computeQuery()} method.
     *
     * @param newValue the new boolean value for the flag
     * @see ComplexEvent
     * @see org.Zeitline.EventTreeModelFilter
     * @see org.Zeitline.Query
     */
    public void setQueryFlag(boolean newValue) {
	matches_query = newValue;
    } // setQueryFlag
    
    /**
     * Returns the flag whether the event matched the latest filter
     * query.  The flag is set via the org.Zeitline.Event.ComplexEvent's {@link
     * ComplexEvent#computeQuery computeQuery()} method and used by
     * the {@link org.Zeitline.EventTreeModelFilter org.Zeitline.EventTreeModelFilter} class for
     * the filter mappings.
     *
     * @return the flag {@link #matches_query matches_query}, which
     * indicates whether the event matched the last query.
     *
     * @see ComplexEvent
     * @see org.Zeitline.EventTreeModelFilter
     * @see org.Zeitline.Query
     */
    public boolean matchesQuery() {
	return matches_query;
    } // matchesQuery
    
    /**
     * Returns a string representation of the event. Needs to be implemented
     * by the non-abstract sub-class.
     *
     * @return a string representation of the event.
     */
    public abstract String toString();

    public abstract ImageIcon getIcon();

    public abstract String getQueryString();
    
} // abstract class org.Zeitline.Event.TimeEvent
