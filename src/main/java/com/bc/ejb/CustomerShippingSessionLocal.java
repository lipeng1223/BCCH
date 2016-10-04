package com.bc.ejb;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.orm.CustomerShipping;

@Local
public interface CustomerShippingSessionLocal {

    public abstract CustomerShipping findById(Long id) throws NoResultException;
    public abstract CustomerShipping findById(Long id, String... joins) throws NoResultException;
    
    public abstract void create(CustomerShipping customerShipping);
    public abstract void update(CustomerShipping customerShipping);
    public abstract void delete(Long id);
}
