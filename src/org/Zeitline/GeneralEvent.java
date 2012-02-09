package org.Zeitline; /********************************************************************

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

import org.Zeitline.Event.AtomicEvent;

import org.Zeitline.Timestamp.Timestamp;
import java.io.Serializable;

/**
 * Class for the basic, discrete events that are imported from
 * various sources. In addition to the fields and methods from the
 * abstract {@link org.Zeitline.Event.TimeEvent org.Zeitline.Event.TimeEvent} class, there are also fields
 * and methods for managing the source from where the event is
 * imported and a user identifier (though not currently used).
 */
public class GeneralEvent
    extends AtomicEvent
    implements Serializable {
    
    /**
     * Name of the event. The text in the name will appear in the JTree
     * listing of the {@link EventTree org.Zeitline.EventTree}.
     */
    protected String name;

    /**
     *  Description of the event. This field contains all the
     *  important information that doesn't fit in the name field.
     */
    protected String description;

    /**
     * Returns an org.Zeitline.Event.AtomicEvent with initial name, description, start
     * time, and source.
     *
     * @param name the name of the event
     * @param description the description for the event
     * @param start the start time of the event
     * @param source the source object from where the event was imported
     */
    public GeneralEvent(String name,
        String description,
        Timestamp start,
        Source source) {
        
        this.description = description;
        this.startTime = start;
        this.reported_time = start;
        this.adjusted_time = start;
        this.source = source;
        this.uniqueId = new Long(idCounter);
        idCounter++;
        this.name = name;
    } // org.Zeitline.Event.AtomicEvent(String,String,Timestamp,org.Zeitline.Source)

    /**
     * Returns an org.Zeitline.Event.AtomicEvent with initial name, description and start
     * time. The source is set to <tt> null </tt>.
     *
     * @param name the name of the event
     * @param description the description for the event
     * @param start the start time of the event
     */
    public GeneralEvent(String name,
		       String description,
		       Timestamp start) {
        
        this(name, description, start, null);
    } // org.Zeitline.Event.AtomicEvent(String,String,Timestamp)
    
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
    
    public void setSource(Source s) {
	this.source = s;
    } // setSource
    
    public Source getSource() {
	return source;
    } // getSource

    /**
     * Returns a string representation of the event. This is currently the
     * {@link #name name} field.
     *
     * @return a string representation of the event
     */
    public String toString() {
	return this.name;
    } // toString

} // class org.Zeitline.Event.AtomicEvent
