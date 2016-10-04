package com.bc.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bc.orm.BreakReceived;
import com.bc.orm.BreakReceivedItem;
import com.bc.orm.InventoryItem;
import com.bc.orm.Received;
import com.bc.orm.User;
import com.bc.orm.Vendor;
import com.bc.util.IsbnUtil;
import com.bc.util.MD5Hash;

public class BreakClientDao extends BaseDao {

    public BreakClientDao(){
        super(InventoryItem.class);
    }
    
    public Session getSession(){
        return BreakClientSessionFactory.getSession();
    }
    
    public Serializable save( Object obj ) throws HibernateException
    {
        Session hibSession = getSession();
        Serializable retval = null;
        try {
            Transaction transaction = hibSession.beginTransaction();
            retval = hibSession.save( obj );
            transaction.commit();
        } catch (Throwable t){
            System.out.println("Could not save!");
            t.printStackTrace();
            retval = null;
        }
        hibSession.flush();
        hibSession.close();
        return retval;
    }

    public void delete( Object obj ) throws HibernateException
    {
        Session hibSession = getSession();
        try {
            Transaction transaction = hibSession.beginTransaction();
            hibSession.delete( obj );
            transaction.commit();
        } catch (Throwable t){
            t.printStackTrace();
        }
        hibSession.flush();
        hibSession.close();
    }
    
    public void update( Object obj ) throws HibernateException
    {
        Session hibSession = getSession();
        try {
            Transaction transaction = hibSession.beginTransaction();
            hibSession.update( obj );
            transaction.commit();
        } catch (Throwable t){
            t.printStackTrace();
        }
        hibSession.flush();
        hibSession.close();
    }
    
    
    public int getCommitted(Long id) throws HibernateException
    {
        int count = 0;

        try {
            Session hibSession = getSession();
            StringBuilder hql = new StringBuilder("select sum(coi.quantity) ");
            hql.append("from CustomerOrderItem as coi left outer join ");
            hql.append("coi.customerOrder as co left outer join ");
            hql.append("coi.inventoryItem as inv where inv.id = ");
            hql.append(id.toString());
            hql.append(" and coi.credit = false and co.posted = 0");
            Object ob = hibSession.createQuery(hql.toString()).uniqueResult();
            if (ob != null){
                Long c = (Long)ob;
                count = c.intValue();
            }
            hibSession.close();
            return count;
        } catch (Exception e){
            return 0;
        }
    }
    
    public InventoryItem findByIsbnCond(String isbn, String condition) throws HibernateException
    {
        Session hibSession = getSession();
        InventoryItem inv = findByIsbnCondition(isbn, condition, hibSession);
        hibSession.close();
        return inv;
    }

    public List<InventoryItem> findBunchIsbn( String isbn ) throws HibernateException
    {
        if( isbn == null ) {
            return null;
        }
        Session hibSession = getSession();
        List<InventoryItem> list;
        if (isbn.length() == 13){
            isbn = IsbnUtil.getIsbn10(isbn);
        }
        list = hibSession.createCriteria(InventoryItem.class).
            add(Restrictions.like("isbn", isbn, MatchMode.START)).setMaxResults(20).list();
        hibSession.close();
        return list;
    }


    public List<InventoryItem> findBeginingWithIsbn( String isbn ) throws HibernateException
    {
        if( isbn == null ) {
            return null;
        }
        Session hibSession = getSession();
        List<InventoryItem> items = hibSession.createCriteria(InventoryItem.class).
            add(Restrictions.like("isbn", isbn, MatchMode.START)).
            addOrder(Order.desc("receivedDate")).list();
        hibSession.close();
        return items;
    }
    
    
    public InventoryItem findByIsbnCondition(String isbn, String condition) throws HibernateException
    {
        return findByIsbnCondition(isbn, condition, getSession());
    }
    public InventoryItem findByIsbnCondition(String isbn, String condition, Session hibSession) throws HibernateException
    {
         if( isbn == null || condition == null) {
            return null;
        }
        InventoryItem inv = null;
        // try isbn10 first, then isbn13
        Object invOb = null;
        if (isbn.length() == 13){
            invOb = hibSession.createCriteria(InventoryItem.class).
            add(Restrictions.eq("isbn", IsbnUtil.getIsbn10(isbn))).add(Restrictions.eq("cond", condition)).uniqueResult();
        } 
        if (invOb == null && isbn.length() == 10){
            invOb = hibSession.createCriteria(InventoryItem.class).
                add(Restrictions.eq("isbn", IsbnUtil.getIsbn13(isbn))).add(Restrictions.eq("cond", condition)).uniqueResult();
        }
        // unmodified isbn
        if (invOb == null){
            invOb = hibSession.createCriteria(InventoryItem.class).
                add(Restrictions.eq("isbn", isbn)).add(Restrictions.eq("cond", condition)).uniqueResult();
        }
        if (invOb != null){
            inv = (InventoryItem)invOb;
        }
        return inv;
    }
    
    public BreakReceivedItem findItemByIsbn(String isbn){
        if( isbn == null ) {
            return null;
        }
        Session hibSession = getSession();
        BreakReceivedItem bri = null;
        Object briOb = hibSession.createCriteria(BreakReceivedItem.class).
            add(Restrictions.eq("isbn", isbn)).uniqueResult();
        if (briOb != null){
            bri = (BreakReceivedItem)briOb;
        }
        hibSession.close();
        return bri;
    }
    
    public List<Vendor> findAllVendors(){
        Session hibSession = getSession();
        return hibSession.createCriteria(Vendor.class).addOrder(Order.asc("code")).list();
    }

    public Received findOldestReceivedWithOpen(String isbn, String condition){
        Session hibSession = getSession();
        Criteria crit = hibSession.createCriteria(Received.class);
        crit.createAlias("receivedItems", "receivedItems");
        crit.createAlias("vendor", "vendor");
        crit.setFetchMode("vendor", FetchMode.JOIN);
        crit.addOrder(Order.asc("poDate"));
        crit.add(Restrictions.eq("receivedItems.isbn", isbn));
        crit.add(Restrictions.eq("receivedItems.cond", condition));
        crit.add(Restrictions.gt("receivedItems.available", new Integer(0)));
        crit.setMaxResults(1);
        List<Received> all = crit.list();
        if (all != null && all.size() > 0) return all.get(0);
        return null;
    }
    
    public User findByUsernamePassword(String username, String password) throws HibernateException {
        try {
        Session hibSession = getSession();
        Criteria crit = hibSession.createCriteria(User.class)
            .add(Restrictions.eq("username", username))
            .add(Restrictions.eq("password", MD5Hash.encode(password)));
        User user = (User)crit.uniqueResult();
        hibSession.close();
        return user;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List findAllBreakReceived() throws HibernateException {
        Session hibSession = getSession();
        Criteria crit = hibSession.createCriteria(BreakReceived.class);
        crit.setFetchMode("breakReceivedItems", FetchMode.JOIN);
        crit.setFetchMode("vendor", FetchMode.JOIN);
        crit.setFetchMode("vendor.vendorSkidTypes", FetchMode.JOIN);
        try {
            List<BreakReceived> returnList = crit.list();
            for (BreakReceived br : returnList){
                for (BreakReceivedItem bri : br.getBreakReceivedItems()){
                    Hibernate.initialize(bri.getBriCounts());
                }
            }
            return returnList;
        } finally {
            hibSession.close();
        }
    }

}
