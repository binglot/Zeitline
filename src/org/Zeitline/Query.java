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

import org.Zeitline.Event.AbstractTimeEvent;

import org.Zeitline.Timestamp.Timestamp;

public class Query {

    protected String actualString = null;
    protected String stringQuery = null;
    protected Timestamp intervalStart = null;
    protected Timestamp intervalEnd = null;
    protected Source sourceQuery = null;


    public Query(String sq) {
	this(null, null, sq);
    } // org.Zeitline.Query(String)

    public Query(Timestamp start, Timestamp end, String key) {

	intervalStart = start;
	intervalEnd = end;

	if (key != null && (key.matches("\\A\\s*\\Z")))
	    key = null;

	actualString = key;

	if (key == null)
	    stringQuery = null;
	else
	    // The (?s) sets the DOTALL mode (otherwise \n isn't matched)
	    stringQuery = "(?s).*" + key.toLowerCase() + ".*";

    } // org.Zeitline.Query(Timestamp,Timestamp,String)

    public Timestamp getIntervalStart() {
	return intervalStart;
    } // getIntervalStart

    public Timestamp getIntervalEnd() {
	return intervalEnd;
    } // getIntervalEnd

    public String getStringText() {
	return actualString;
    } // getStringText

    public boolean matches(AbstractTimeEvent t) {

	if ((intervalStart != null) &&
	    intervalStart.after(t.getStartTime()))
	    return false;

	if ((intervalEnd != null) &&
	    intervalEnd.before(t.getStartTime()))
	    return false;
	
	if (stringQuery != null) {

	    if (t.getName().toLowerCase().matches(stringQuery))
		return true;
	
	    if (t.getDescription().toLowerCase().matches(stringQuery))
		return true;

	    return false;
	}

	return true;
    } // matches

    public String toString() {

	String ret = new String();

	if (actualString != null)
	    ret = ret + "Keyword: \"" + actualString + "\" ";

	if (intervalStart != null)
	    ret = ret + "From: " + intervalStart + " ";

	if (intervalEnd != null)
	    ret = ret + "Until: " + intervalEnd;

	return ret;

    } // toString

} // org.Zeitline.Query