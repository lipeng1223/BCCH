package com.bc.util;

import java.io.Serializable;
import java.util.Comparator;

public class ObjectToStringComparator implements Comparator<Object>, Serializable {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        return o1.toString().compareTo(o2.toString());
    }

}
