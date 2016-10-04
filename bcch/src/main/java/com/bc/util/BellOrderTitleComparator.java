package com.bc.util;

import com.bc.orm.BellOrderItem;
import java.util.Comparator;

public class BellOrderTitleComparator implements Comparator<BellOrderItem> {

    @Override
    public int compare(BellOrderItem coi1, BellOrderItem coi2) {
        if (coi1.getTitle() != null)
            return coi1.getTitle().compareTo(coi2.getTitle());
        return -1;
    }

}
