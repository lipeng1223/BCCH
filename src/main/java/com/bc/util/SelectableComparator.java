package com.bc.util;

import java.io.Serializable;
import java.util.Comparator;

public class SelectableComparator implements Comparator<Selectable>, Serializable {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Selectable s1, Selectable s2) {
        return s1.getDisplayName().compareTo(s2.getDisplayName());
    }

}
