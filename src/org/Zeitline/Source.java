package org.Zeitline; /********************************************************************

This file is part of org.Zeitline.Zeitline: a forensic timeline editor

Written by Florian Buchholz and Courtney Falk.

Copyright (c) 2004,2005 Florian Buchholz, Courtney Falk, Purdue
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

import java.io.Serializable;
import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

public class Source
        implements Serializable{
        
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
