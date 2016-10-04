package com.bc.ejb.bellwether;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.dao.DaoResults;
import com.bc.orm.BellVendor;
import com.bc.orm.VendorSkidType;
import com.bc.struts.QueryInput;

@Local
public interface BellVendorSessionLocal {

    public abstract Integer getCount();
    public abstract BellVendor findById(Long id) throws NoResultException;
    public abstract BellVendor findById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAll(QueryInput queryInput, String... joins);

    public abstract void initCache();
    
    public abstract void create(BellVendor vendor);
    public abstract void update(BellVendor vendor);
    public abstract void delete(Long id);
        
}
