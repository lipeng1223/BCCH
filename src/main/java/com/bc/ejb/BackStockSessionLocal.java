package com.bc.ejb;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.dao.DaoResults;
import com.bc.orm.BackStockItem;
import com.bc.orm.BackStockLocation;
import com.bc.struts.QueryInput;

@Local
public interface BackStockSessionLocal {

    public abstract Integer getCount();
    public abstract BackStockItem findById(Long id) throws NoResultException;
    public abstract BackStockItem findById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAll(QueryInput queryInput, String... joins);
    public abstract DaoResults findAll(QueryInput queryInput, HashMap<String, String> aliases, String... joins);
    
    public abstract void create(BackStockItem backStockItem);
    public abstract void update(BackStockItem backStockItem);
    public abstract void delete(Long id);
    public abstract Boolean exists(String isbn);
    public abstract List<BackStockItem> findByIsbn(String isbn);
    
    public abstract BackStockLocation findBackStockLocationById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAllBackStockLocations(QueryInput queryInput, HashMap<String, String> aliases, String... joins);
    
    public abstract void create(BackStockLocation backStockLocation);
    public abstract void update(BackStockLocation backStockLocation);
    public abstract void deleteBackStockLocation(Long id);
    public abstract void updateCounts(Long id);
    
}
