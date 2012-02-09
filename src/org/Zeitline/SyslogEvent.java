package org.Zeitline; /********************************************************************

This file is part of org.Zeitline.Zeitline: a forensic timeline editor

Written by Florian Buchholz.

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

import java.sql.Timestamp;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class SyslogEvent
    extends AtomicEvent
    implements Serializable {
    
    protected static JPanel panel = null;
    protected static JLabel lbl_host;
    protected static JLabel lbl_process;
    protected static JLabel lbl_pid;
    protected static JLabel lbl_message;
    
    private String host;
    private String process;
    private Integer pid;
    private String message;

    public SyslogEvent(Timestamp start_time,
     		       String host,
		       String process,
		       Integer pid,
		       String message) {

	if (panel == null) {
		initPanel();
	}
	
	this.start_time = start_time;
	this.host = host;
	this.process = process;
	this.pid = pid;
	this.message = message;
        this.unique_id = new Long(id_counter);
	id_counter++;

	adjusted_time = start_time;
	reported_time = start_time;
	
    }
    
    /**
     * Returns the name of the event.
     *
     * @return the name of the event, {@link #name name}
     */
    public String getName() {
	return process + ": " + message;
    } // getName
    
    /**
     * Returns the description of the event.
     *
     * @return the description of the event, {@link #description description}
     */
    public String getDescription() {

	return "Host: " + host +
	    "\nProcess: " + process +
	    "\nPID: " + ((pid == null) ? "-":pid.toString()) + 
	    "\nMessage: " + message;
    } // getDescription
    
    /**
     * Returns a string representation of the event. This is currently the
     * {@link #name name} field.
     *
     * @return a string representation of the event
     */
    public String toString() {
	return getName();
    } // toString


//    public ImageIcon getIcon() {
//	return icon;
//    }
    
    public JPanel getPanel() {
	if (panel == null)
		initPanel();
	return panel;
    }
	
    public void setPanelValues() {
	    if (panel == null)
		    initPanel();
	    lbl_host.setText(host);
	    lbl_process.setText(process);
	    if (pid == null)
		    lbl_pid.setText("-");
	    else
		    lbl_pid.setText(pid.toString());
	    lbl_message.setText(message);
    }


    private void initPanel() {
	
	    Vector items = new Vector();
	    lbl_host = new JLabel("", JLabel.LEADING);
	    items.add(FormGenerator.getFormItem("Host:", lbl_host));
	    lbl_process = new JLabel("", JLabel.LEADING);
	    items.add(FormGenerator.getFormItem("Process:", lbl_process));		
	    lbl_pid = new JLabel("", JLabel.LEADING);
	    items.add(FormGenerator.getFormItem("PID:", lbl_pid));
	    lbl_message = new JLabel("", JLabel.LEADING);
	    items.add(FormGenerator.getFormItem("Message:", lbl_message));

	    panel = FormGenerator.createForm(items);
	    
    }
    
} // class org.Zeitline.SyslogEvent