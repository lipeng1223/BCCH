package com.bc.util;

import java.util.Comparator;

import com.bc.orm.CustomerOrderItem;

public class CustomerOrderItemTitleComparator implements Comparator<CustomerOrderItem> {

    @Override
    public int compare(CustomerOrderItem o1, CustomerOrderItem o2) {
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
