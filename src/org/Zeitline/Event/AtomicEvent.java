package org.Zeitline.Event;
/********************************************************************

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

import org.Zeitline.Source;
import org.Zeitline.Zeitline;

import org.Zeitline.Timestamp.Timestamp;
import java.io.Serializable;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

/**
 * Class for the basic, discrete events that are imported from
 * various sources. In addition to the fields and methods from the
 * abstract {@link TimeEvent org.Zeitline.Event.TimeEvent} class, there are also fields
 * and methods for managing the source from where the event is
 * imported and a user identifier (though not currently used).
 */
public abstract class AtomicEvent
    extends TimeEvent
    implements Serializable {
    
    protected static ImageIcon icon = Zeitline.createNavigationIcon("atomic_small");

    /**
     * The org.Zeitline.Source object from where the event was imported.
     */
    protected Source source;

    protected Timestamp reported_time;
    protected Timestamp adjusted_time;
    
    public void setSource(Source s) {
	if (this.source != null)
		s.removeEvent(this);
	this.source = s;
	s.addEvent(this);
    } // setSource
    
    public Source getSource() {
	return source;
    } // getSource

    public void setAdjustedTime(Timestamp newVal) {
	    adjusted_time = newVal;
    }
    
    public Timestamp getAdjustedTime() {
	    return adjusted_time;
    }
    
    public Timestamp getReportedTime() {
	    return reported_time;
    }
    
    public JPanel getPanel() {
	return null;
    } //getPanel

    public void setPanelValues() {}
    
    public ImageIcon getIcon() {
	return icon;
    }

    public String getQueryString() {
	return getDescription();
    }

} // class org.Zeitline.Event.AtomicEvent
