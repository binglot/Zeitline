package org.Zeitline.filters;
/********************************************************************

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
import org.Zeitline.FileInputFilter;
import org.Zeitline.InputFilter.InputFilter;
import org.Zeitline.MACTimeEvent;
import org.Zeitline.Source;

import java.awt.Component;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import javax.swing.filechooser.FileFilter;

public class FLSFilter extends InputFilter {
    protected LinkedList event_queue;
    protected RandomAccessFile file_input;
    private static FileFilter filter;

    protected String filename;
    protected Timestamp mtime;
    protected Timestamp atime;
    protected Timestamp ctime;
    protected int user_id;
    protected int group_id;
    protected int mode;
    protected int permissions;
    protected long size;
    protected int type;

    public FLSFilter() {
	event_queue = new LinkedList();
        if(filter == null) filter = new FileInputFilter(".fls", "*.fls");
    } // org.Zeitline.filters.FLSFilter

    public Source init(String filename, Component parent) {

	try {
	    //	    file_input = new BufferedReader(new FileReader(filename));
	    file_input = new RandomAccessFile(filename, "r");
	}
	catch (IOException ioe) {
	    return null;
	}

	return new Source("FLS filter", filename, Source.GRANULARITY_SEC);
    } // init

    public AtomicEvent getNextEvent() {

	String line;
	String[] fields;
	
	if (event_queue.isEmpty()) {	    
	    while (true) {

		try {
		    line = file_input.readLine();
		}
		catch (IOException ioe) {
		    return null;
		}
		
		if (line == null) return null;

		fields = line.split("\\|");	    
		
		if (fields.length < 16)
		    System.err.println("Line not in proper format: " + line);
		else
		    break;

	    }
		  

	    // get timestamps, we have second granularity but need to
	    // convert to ms

	    Timestamp mtime = new Timestamp(Long.decode(fields[12]).intValue() * (long)1000);
	    Timestamp atime = new Timestamp(Long.decode(fields[11]).intValue() * (long)1000);
	    Timestamp ctime = new Timestamp(Long.decode(fields[13]).intValue() * (long)1000);

	    String name = fields[1];
	    int user_id = Integer.decode(fields[7]).intValue();
	    int group_id = Integer.decode(fields[8]).intValue();
	    int mode = Integer.decode(fields[4]).intValue();
	    long size = Long.decode(fields[10]).intValue();


	    if ((mtime.compareTo(atime) == 0) && (mtime.compareTo(ctime)==0))
		return new MACTimeEvent(name, mtime, atime, ctime, user_id,
					group_id, mode, size,
					MACTimeEvent.TYPE_MAC);



	    if (mtime.compareTo(atime) == 0) {
		event_queue.add(new MACTimeEvent(name, mtime, atime, ctime, 
						 user_id, group_id, mode, size,
						 MACTimeEvent.TYPE_MA));

		return new MACTimeEvent(name, mtime, atime, ctime, user_id,
					group_id, mode, size,
					MACTimeEvent.TYPE_C);
	    }
		    
	    if (mtime.compareTo(ctime) == 0) {
		event_queue.add(new MACTimeEvent(name, mtime, atime, ctime, 
						 user_id, group_id, mode, size,
						 MACTimeEvent.TYPE_MC));

		return new MACTimeEvent(name, mtime, atime, ctime, user_id,
					group_id, mode, size,
					MACTimeEvent.TYPE_A);
	    }
		    
	    if (atime.compareTo(ctime) == 0) {
		event_queue.add(new MACTimeEvent(name, mtime, atime, ctime, 
						 user_id, group_id, mode, size,
						 MACTimeEvent.TYPE_AC));

		return new MACTimeEvent(name, mtime, atime, ctime, user_id,
					group_id, mode, size,
					MACTimeEvent.TYPE_M);
	    }
	    
	    event_queue.add(new MACTimeEvent(name, mtime, atime, ctime, 
					     user_id, group_id, mode, size,
					     MACTimeEvent.TYPE_M));

	    event_queue.add(new MACTimeEvent(name, mtime, atime, ctime, 
					     user_id, group_id, mode, size,
					     MACTimeEvent.TYPE_A));

	    return new MACTimeEvent(name, mtime, atime, ctime, user_id,
				    group_id, mode, size,
				    MACTimeEvent.TYPE_C);


	    /* old code for general events
		
	    if ((mtime == atime) && (mtime == ctime))
		return new org.Zeitline.GeneralEvent("MAC " + name, description,
				       new Timestamp(mtime));

	    if (mtime == atime) {
		event_queue.add(new org.Zeitline.GeneralEvent("MA. " + name, description,
						new Timestamp(mtime)));
		return new org.Zeitline.GeneralEvent("..C " + name, description,
				       new Timestamp(ctime));
	    }
		    
	    if (mtime == ctime) {
		event_queue.add(new org.Zeitline.GeneralEvent("M.C " + name, description,
						new Timestamp(mtime)));
		return new org.Zeitline.GeneralEvent(".A. " + name, description,
				       new Timestamp(atime));
	    }
		    
	    if (atime == ctime) {
		event_queue.add(new org.Zeitline.GeneralEvent(".AC " + name, description,
						new Timestamp(atime)));
		return new org.Zeitline.GeneralEvent("M.. " + name, description,
				       new Timestamp(mtime));
	    }
	    
	    event_queue.add(new org.Zeitline.GeneralEvent("M.. " + name, description,
					    new Timestamp(mtime)));
	    event_queue.add(new org.Zeitline.GeneralEvent(".A. " + name, description,
					    new Timestamp(atime)));
	    return new org.Zeitline.GeneralEvent("..C " + name, description,
				   new Timestamp(ctime));

	    */ 

	}
	else return (MACTimeEvent) event_queue.removeFirst();

    } // getNextEvent

    public FileFilter getFileFilter() {
	return filter;
    } // getFileFilter

    public String getName() {
        return "FLS Filter";
    } // getName
    
    public String getDescription() {
        return "Reads in MAC times as output by Brian Carrier's FLS tool.";
    } // getDescription

    public long getExactCount() {
	return 0;
    } // getExactCount

    public long getTotalCount() {
	try {
	    return file_input.length();
	}
	catch (IOException ie) {
	    return 0;
	}
    } // getTotalCount

    public long getProcessedCount() {
	try {
	    return file_input.getFilePointer();
	}
	catch (IOException ie) {
	    return 0;
	}
    } // getProcessedCount

} // org.Zeitline.filters.FLSFilter
