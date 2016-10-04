package com.bc.ejb;

import javax.ejb.Local;

@Local
public interface UtilitySessionLocal {

    public abstract void fixInventoryCounts();
    public abstract void backOutOnhand();
    
}
