package com.bc.ejb;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.dao.DaoResults;
import com.bc.orm.CustomerOrder;
import com.bc.orm.CustomerOrderItem;
import com.bc.orm.InventoryItem;
import com.bc.struts.QueryInput;

@Local
public interface OrderSessionLocal {

    public abstract Integer getCount();
    public abstract CustomerOrder findById(Long id) throws NoResultException;
    public abstract CustomerOrder findById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAll(QueryInput queryInput, String... joins);
    public abstract DaoResults findAll(QueryInput queryInput, HashMap<String, String> aliases,  String... joins);
    public abstract DaoResults findAllItems(QueryInput queryInput, String... joins);
    public abstract DaoResults findAllItems(QueryInput queryInput, HashMap<String, String> aliases, String... joins);
    public abstract CustomerOrderItem findItemByIsbnCond(CustomerOrder order, String isbn, String cond);
    public abstract CustomerOrderItem findNextItem(String sort, String dir, Integer offset, CustomerOrder order, String... joins);
    
    public abstract void create(CustomerOrder order);
    public abstract void update(CustomerOrder order);
    public abstract void delete(Long id);
    
    public abstract void shipMax(Long id);
    public abstract void fixZeroPrice(Long id);

    public abstract CustomerOrderItem findItemById(Long id, String... joins) throws NoResultException;
    public abstract void create(CustomerOrderItem orderItem);
    public abstract void update(CustomerOrderItem orderItem);
    public abstract void deleteItem(Long id);

    public abstract void recalculateAllOrderTotals(Long id);
    public abstract void recalculateOrderTotals(Long id);
    public abstract void recalculateOrderItemTotals(Long id);
    
    public abstract List<Long> getAllInventoryItemIds(Long orderId);
    public List<Long> getAllOrderIds(String isbn, int cnt, boolean bell, boolean restricted, boolean he);
    
    public abstract Boolean addOrderItems(List<CustomerOrderItem> items);
    
    public abstract List<CustomerOrderItem> findLastN(InventoryItem ii, Integer limit);
    public abstract void close();
    
}
