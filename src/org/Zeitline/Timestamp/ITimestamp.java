package org.Zeitline.Timestamp;

import java.io.Serializable;
import java.util.Date;

public interface ITimestamp extends Serializable, Cloneable, Comparable<Date> {
    void setTime(long time);
    long getTime();

    void setNanos(int n);
    int getNanos();

    boolean before(Timestamp ts);
    boolean after(Timestamp ts);

    int compareTo(Timestamp ts);
    int compareTo(java.util.Date o);

    boolean equals(Timestamp ts);
    boolean equals(Object ts);
    @Override
    int hashCode();

    String toString();
}
