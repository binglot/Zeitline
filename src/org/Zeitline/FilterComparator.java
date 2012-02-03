package org.Zeitline;

import java.util.Comparator;

class FilterComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        return o1.toString().compareTo(o2.toString());
    }

    public boolean equals(Object obj) {
        return false;
    }

}
