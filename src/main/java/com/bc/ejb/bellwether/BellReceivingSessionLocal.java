package com.bc.ejb.bellwether;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.dao.DaoResults;
import com.bc.orm.BellReceived;
import com.bc.orm.BellReceivedItem;
import com.bc.struts.QueryInput;

@Local
public interface BellReceivingSessionLocal {

    public abstract Integer getCount();
    public abstract BellReceived findById(Long id) throws NoResultException;
    public abstract BellReceived findById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAll(QueryInput queryInput, String... joins);
    public abstract DaoResults findAll(QueryInput queryInput, HashMap<String, String> aliases, String... joins);
    public abstract DaoResults findAllItems(QueryInput queryInput, String... joins);
    public abstract DaoResults findAllItems(QueryInput queryInput, HashMap<String, String> aliases, String... joins);

    public abstract BellReceivedItem findItemById(Long id, String... joins) throws NoResultException;
    
    public abstract void create(BellReceived received);
    public abstract void update(BellReceived received);
    public abstract void delete(Long id);
    
    public abstract void create(BellReceivedItem receivedItem);
    public abstract void update(BellReceivedItem receivedItem);
    public abstract void deleteItem(Long id);

    public abstract Boolean addReceivedItems(List<BellReceivedItem> items);     
    
    public abstract void recalculateReceived(Long id);
}
