package org.Zeitline.Event;

public interface ITimeEvent<TIcon> {
    String getName();
    String getDescription();
    String toString();
    TIcon getIcon();
    //String getQueryString();
}
