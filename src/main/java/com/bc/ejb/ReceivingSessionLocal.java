package com.bc.ejb;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.dao.DaoResults;
import com.bc.orm.Received;
import com.bc.orm.ReceivedItem;
import com.bc.struts.QueryInput;

@Local
public interface ReceivingSessionLocal {

    public abstract Integer getCount();
    public abstract Received findById(Long id) throws NoResultException;
    public abstract Received findById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAll(QueryInput queryInput, String... joins);
    public abstract DaoResults findAll(QueryInput queryInput, HashMap<String, String> aliases, String... joins);
    public abstract DaoResults findAllItems(QueryInput queryInput, String... joins);
    public abstract DaoResults findAllItems(QueryInput queryInput, HashMap<String, String> aliases, String... joins);
    public abstract DaoResults findAllFastRecItems(QueryInput queryInput);

    public abstract ReceivedItem findItemById(Long id, String... joins) throws NoResultException;
    public abstract ReceivedItem findItemBy(Received received, String isbn, String cond);
    
    public abstract void create(Received received);
    public abstract void update(Received received);
    public abstract void delete(Long id);
    
    public abstract void updateInventoryItemsForPost(Long receivedId);
    public abstract void create(ReceivedItem receivedItem);
    public abstract void update(ReceivedItem receivedItem);
    public abstract List<ReceivedItem> updateWithLifo(List<ReceivedItem> receivedItems, Long receivedId);
    public abstract void updateWithLifo(ReceivedItem receivedItem, Long receivedId);
    public abstract void deleteItem(Long id);

    public abstract void createFromFastRec(Long receivedId, ReceivedItem receivedItem);
    
    public abstract Boolean addReceivedItems(List<ReceivedItem> items);     
    
    public abstract void recalculateReceived(Long id);
    
    public List<Long> getAllReceivedIds(String isbn, int cnt, boolean bell, boolean restricted, boolean he);
   
}
