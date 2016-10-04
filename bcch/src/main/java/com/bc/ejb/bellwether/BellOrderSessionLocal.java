package com.bc.ejb.bellwether;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.dao.DaoResults;
import com.bc.orm.BellOrder;
import com.bc.orm.BellOrderItem;
import com.bc.orm.InventoryItem;
import com.bc.struts.QueryInput;

@Local
public interface BellOrderSessionLocal {

    public abstract Integer getCount();
    public abstract BellOrder findById(Long id) throws NoResultException;
    public abstract BellOrder findById(Long id, String... joins) throws NoResultException;
    public abstract BellOrder findByOrderIdAndSku(String orderId, String sku);
    public abstract DaoResults findAll(QueryInput queryInput, String... joins);
    public abstract DaoResults findAll(QueryInput queryInput, HashMap<String, String> aliases,  String... joins);
    public abstract DaoResults findAllItems(QueryInput queryInput, String... joins);
    public abstract DaoResults findAllItems(QueryInput queryInput, HashMap<String, String> aliases, String... joins);
    public abstract BellOrderItem findItemByIsbn(BellOrder order, String isbn);
    public abstract BellOrderItem findNextItem(String sort, String dir, Integer offset, BellOrder order, String... joins);
    public abstract void shipMax(Long id);
    
    public abstract void create(BellOrder order);
    public abstract void update(BellOrder order);
    public abstract void create(List<BellOrder> orders);
    public abstract void update(List<BellOrder> orders);
    public abstract void delete(Long id);
    public abstract void updateLocations(BellOrder order);
    
    public abstract BellOrderItem findItemById(Long id, String... joins) throws NoResultException;
    public abstract void create(BellOrderItem orderItem);
    public abstract void update(BellOrderItem orderItem);
    public abstract void deleteItem(Long id);

    public abstract List<Long> getAllInventoryItemIds(Long orderId);
    
    public abstract void recalculateAllOrderTotals(Long id);
    public abstract void recalculateOrderTotals(Long id);
    public abstract void recalculateOrderItemTotals(Long id);
    
    public abstract Boolean addOrderItems(List<BellOrderItem> items);
    
    public abstract List<BellOrderItem> findLastN(InventoryItem ii, Integer limit);
    
}
