package com.bc.ejb;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.dao.DaoResults;
import com.bc.orm.Vendor;
import com.bc.orm.VendorSkidType;
import com.bc.struts.QueryInput;

@Local
public interface VendorSessionLocal {

    public abstract Integer getCount();
    public abstract Vendor findById(Long id) throws NoResultException;
    public abstract Vendor findById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAll(QueryInput queryInput, String... joins);

    public abstract void initCache();
    
    public abstract void create(Vendor vendor);
    public abstract void update(Vendor vendor);
    public abstract void delete(Long id);
    
    public abstract void create(VendorSkidType vendorSkidType);
    public abstract void update(VendorSkidType vendorSkidType);
    public abstract void deleteVendorSkidType(Long id);
    public abstract VendorSkidType findVendorSkidTypeById(Long id);
    
}
