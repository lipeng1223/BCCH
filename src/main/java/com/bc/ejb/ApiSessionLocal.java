package com.bc.ejb;

import com.bc.orm.Customer;
import com.bc.orm.CustomerOrder;
import com.bc.orm.CustomerOrderItem;
import com.bc.orm.CustomerShipping;
import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.bc.orm.InventoryItem;

import com.bc.orm.Received;
import com.bc.orm.ReceivedItem;
import com.bc.orm.User;
import com.bc.orm.Vendor;

import java.util.Date;


public interface ApiSessionLocal {
    
    public abstract Serializable save( Object obj ) throws HibernateException;

    public abstract void delete( Object obj ) throws HibernateException;
    
    public abstract void update( Object obj ) throws HibernateException;
    
    public abstract void update(Object entity, Long parentOid);
    
    public abstract Object findById(Class entityClass, Long id, String... joins);
    
    
    public abstract int getCommitted(Long id) throws HibernateException;
    
    public abstract InventoryItem findByIsbnCond(String isbn, String condition) throws HibernateException;

    public abstract List<InventoryItem> findBunchIsbn( String isbn ) throws HibernateException;
    
    public abstract List<InventoryItem> findBeginingWithIsbn( String isbn ) throws HibernateException;
    
    public abstract List<InventoryItem> findBunchWithIsbns( String[] isbns ) throws HibernateException;
    
    
    public abstract InventoryItem findByIsbnCondition(String isbn, String condition) throws HibernateException;
    
    public abstract InventoryItem findByIsbnCondition(String isbn, String condition, Session hibSession) throws HibernateException;
        
    public abstract List<Vendor> findAllVendors();
    
    public abstract Vendor findVendorById(long id);

    public abstract Received findOldestReceivedWithOpen(String isbn, String condition);
    
    public abstract User findByUsernamePassword(String username, String password) throws HibernateException;
    
    public abstract Customer findCustomerById(Long id);
    
    public abstract Received findReceivingById(Long id);
    
    public abstract CustomerShipping findCustomerShippingById(Long id);
    
    public abstract CustomerOrderItem findOrderItemByIsbnCond(CustomerOrder order, String isbn, String cond);
    
    public abstract List getCustomers();

    public abstract Long createOrder(CustomerOrder order);
    
    public abstract Boolean createOrderItem(CustomerOrderItem item);
    
    public abstract void recalculateCommitted(Long id);
    
    public abstract void recalculateOrderItemTotals(Long id);
    
    public abstract void createReceiving(Received r);
    
    public abstract void createReceivingItem(ReceivedItem ri);
    
    public abstract Boolean addReceivedItems(List<ReceivedItem> items);
    
    public abstract List<ReceivedItem> updateWithLifo(List<ReceivedItem> receivedItems, Long receivedId);
    
    public abstract List<Received> getReceivings(int page);
    
    public abstract void updateReceiving(long id, String pono, Date podate, long vid, String publisher, String comment);
    
    public abstract List<ReceivedItem> getReceivingItems(long rid, int page);
    
    public abstract void updateReceivedItem(long id, int qty, int oqty, float percent, float costplb, float cost, String title, String bin, float lstprice, float selprice, String cover, Boolean bb, Boolean br, Boolean he, Boolean re);
    
//    public abstract void updateInventory(long id, String isbn, String cond, String title, String author, String publisher, float lprice, float sprice, int onhand, String bin, String cover, Boolean bb, Boolean skid, Boolean rest, Boolean he, String category, int pages, float length, float width, float height, float weight);
    
    public abstract void updateInventory(long id, String bin);
    public abstract void recalculateReceived(Long id);
    
    public abstract void createReceivedItems(List<ReceivedItem> items, List<Long> ids);
    
}
