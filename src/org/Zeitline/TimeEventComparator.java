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

import java.util.Comparator;

public class TimeEventComparator
        implements Comparator {

    public int compare(Object o1, Object o2) throws ClassCastException {
        return EventComparator.compare(o1, o2);
    } // compare

    public boolean equals(Object obj) {
        return compare(this, obj) == 0;
    } // equals
} // class org.Zeitline.TimeEventComparator
