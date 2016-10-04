package com.bc.ejb;

import java.util.List;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.dao.DaoResults;
import com.bc.orm.Customer;
import com.bc.struts.QueryInput;

@Local
public interface CustomerSessionLocal {

    public abstract Integer getCount();
    public abstract Customer findById(Long id) throws NoResultException;
    public abstract Customer findById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAll(QueryInput queryInput, String... joins);
    public abstract List<Customer> findByName(String name);

    public abstract void initCache();
    
    public abstract void create(Customer customer);
    public abstract void update(Customer customer);
    public abstract void delete(Long id);
}
