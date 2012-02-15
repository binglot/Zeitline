package org.Zeitline.Plugin;

public interface IClassInstantiator<T> {
    T newInstance(Class classDef) throws InstantiationException, IllegalAccessException, ClassCastException ;
}
