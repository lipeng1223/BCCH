package com.bc.ejb.bellwether;

import java.util.List;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.dao.DaoResults;
import com.bc.orm.BellCustomer;
import com.bc.orm.BellCustomerShipping;
import com.bc.struts.QueryInput;

@Local
public interface BellCustomerSessionLocal {

    public abstract Integer getCount();
    public abstract BellCustomer findById(Long id) throws NoResultException;
    public abstract BellCustomerShipping findShippingById(Long id) throws NoResultException;
    public abstract BellCustomer findById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAll(QueryInput queryInput, String... joins);
    public abstract List<BellCustomer> findByName(String name);

    public abstract void initCache();
    
    public abstract void create(BellCustomer customer);
    public abstract void update(BellCustomer customer);
    public abstract void delete(Long id);
}
