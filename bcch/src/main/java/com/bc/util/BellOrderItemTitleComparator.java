package com.bc.util;

import java.util.Comparator;

import com.bc.orm.BellOrderItem;

public class BellOrderItemTitleComparator implements Comparator<BellOrderItem> {

    @Override
    public int compare(BellOrderItem o1, BellOrderItem o2) {
        if (o1.getTitle() != null && o2.getTitle() != null){
            return o1.getTitle().compareTo(o2.getTitle());
        } else if (o1.getTitle() == null && o2.getTitle() != null){
            return -1;
        } else if (o2.getTitle() == null && o1.getTitle() != null){
            return 1;
        }
        return 0;
    }
    
}
