package com.bc.util;

import java.util.Comparator;

import com.bc.orm.CustomerOrderItem;

public class OrderTitleComparator implements Comparator<CustomerOrderItem> {

    @Override
    public int compare(CustomerOrderItem coi1, CustomerOrderItem coi2) {
        if (coi1.getTitle() != null)
            return coi1.getTitle().compareTo(coi2.getTitle());
        return -1;
    }

}
