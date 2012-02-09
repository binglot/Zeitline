package org.Zeitline.Timestamp;

import java.io.Serializable;
import java.util.Date;

public interface ITimestamp extends Serializable, Cloneable, Comparable<Date> {
    void setTime(long time);
    long getTime();

    void setNanos(int n);
    int getNanos();

    boolean before(ITimestamp ts);
    boolean after(ITimestamp ts);

    @Override
    int compareTo(java.util.Date o);
    int compareTo(ITimestamp ts);

    @Override
    boolean equals(Object ts);
    boolean equals(ITimestamp ts);
    @Override
    int hashCode();

    String toString();
}
