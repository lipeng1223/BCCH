package com.bc.ejb;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.dao.DaoResults;
import com.bc.orm.InventoryItem;
import com.bc.struts.QueryInput;
import java.util.List;

@Local
public interface InventoryItemSessionLocal {

    public abstract Integer getCount();
    public abstract InventoryItem findById(Long id) throws NoResultException;
    public abstract InventoryItem findById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAll(QueryInput queryInput, String... joins);
    public abstract InventoryItem findByIsbnCond(String isbn, String cond, String... joins) throws NoResultException;
    public abstract InventoryItem findByTitleCond(String title, String cond, String... joins) throws NoResultException;
    public List<InventoryItem> findByIsbn(String isbn, String... joins);

    public abstract void create(InventoryItem inventoryItem);
    public abstract void update(InventoryItem inventoryItem);
    public abstract void delete(Long id);
    
    public abstract void updatePendingBins(Long iiId, String bin);
    public abstract void recalculateCommitted(Long id);
    
    public abstract Boolean canBeDeleted(Long id);
    
}
