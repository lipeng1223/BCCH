package com.bc.ejb;

import java.util.List;
import javax.ejb.Local;

@Local
public interface InventoryIsbnCheckSessionLocal {
    
    public abstract List<Long> fixBookcountryIsbns();
}
