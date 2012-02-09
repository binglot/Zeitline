package org.Zeitline.Timestamp;

import java.io.Serializable; /**
 * Created by IntelliJ IDEA.
 * User: Bart
 * Date: 09/02/12
 * Time: 11:06
 * To change this template use File | Settings | File Templates.
 */
public interface ITimestamp extends Serializable, Cloneable, Comparable<Date> {
void setTime(long time);
    long getTime();
    String toString ();
    int getNanos();
    void setNanos(int n);
    boolean equals(Timestamp ts);
    boolean equals(Object ts);
    boolean before(Timestamp ts);
    boolean after(Timestamp ts);
    int compareTo(Timestamp ts);
    int compareTo(java.util.Date o);
    @Override int hashCode();}
