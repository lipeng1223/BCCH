package com.bc.ejb.bellwether;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.dao.DaoResults;
import com.bc.orm.BellInventory;
import com.bc.struts.QueryInput;

@Local
public interface BellInventorySessionLocal {

    public abstract Integer getCount();
    public abstract BellInventory findById(Long id) throws NoResultException;
    public abstract BellInventory findById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAll(QueryInput queryInput, String... joins);
    public abstract DaoResults findAllSkus(QueryInput queryInput, String... joins);
    //public abstract BellInventory findByIsbnCond(String isbn, String bellcondition, String... joins) throws NoResultException;
    public abstract BellInventory findByIsbn(String isbn, String... joins) throws NoResultException;
    
    public abstract void create(BellInventory bellInventory);
    public abstract void update(BellInventory bellInventory);
    public abstract void delete(Long id);
    
    public abstract void recalculateCommitted(Long id);
    
}
