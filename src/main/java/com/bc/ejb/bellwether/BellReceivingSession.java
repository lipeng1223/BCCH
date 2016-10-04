package com.bc.ejb.bellwether;


import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;

import org.apache.log4j.Logger;

import com.bc.dao.BaseDao;
import com.bc.dao.DaoResults;
import com.bc.ejb.LifoSessionLocal;
import com.bc.ejb.LifoSession;
import com.bc.ejb.ReceivingSession;
import com.bc.orm.*;
import com.bc.struts.QueryInput;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

@Stateless
public class BellReceivingSession implements BellReceivingSessionLocal {

    public static final String LocalJNDIString = "inventory/"+BellReceivingSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = BellReceivingSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(ReceivingSession.class);
        
    public Integer getCount() {
        BaseDao<Received> dao = new BaseDao<Received>(Received.class);
        return dao.getCount();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BellReceived findById(Long id) throws NoResultException {
        BaseDao<BellReceived> dao = new BaseDao<BellReceived>(BellReceived.class);
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BellReceived findById(Long id, String... joins) throws NoResultException {
        BaseDao<BellReceived> dao = new BaseDao<BellReceived>(BellReceived.class);
        return dao.findById(id, joins);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(BellReceived received) {
        BaseDao<BellReceived> dao = new BaseDao<BellReceived>(BellReceived.class);
        dao.create(received, null);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(BellReceived received) {
        BaseDao<BellReceived> dao = new BaseDao<BellReceived>(BellReceived.class);
        dao.update(received, null);
        dao.executeJdbcUpdate("update bell_received_item as ri, bell_received as r set ri.date = r.po_date where ri.received_id = r.id and r.id = "+received.getId());
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        BaseDao<BellReceived> dao = new BaseDao<BellReceived>(BellReceived.class);
        BellReceived rec = dao.findById(id);
        // have to go and fix the inventory items for any of the received items on this 
        BaseDao<BellReceivedItem> riDao = new BaseDao<BellReceivedItem>(BellReceivedItem.class);
        Criteria crit = riDao.getSession().createCriteria(BellReceivedItem.class);
        crit.add(Restrictions.eq("bellReceived", rec));
        crit.setFetchMode("bellInventory", FetchMode.JOIN);
        List<BellReceivedItem> items = crit.list();
        getLifoSession().deleteBellReceivedItems(items);
        dao.delete(rec, null);
    }
    
    public DaoResults findAll(QueryInput queryInput, String... joins) {
        BaseDao<BellReceived> dao = new BaseDao<BellReceived>(BellReceived.class);
        return dao.findAll(queryInput, joins);
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput, HashMap<String, String> aliases, String... joins) {
        BaseDao<BellReceived> dao = new BaseDao<BellReceived>(BellReceived.class);
        return dao.findAll(queryInput, aliases, joins);
    }
     
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAllItems(QueryInput queryInput, String... joins) {
        BaseDao<BellReceivedItem> dao = new BaseDao<BellReceivedItem>(BellReceivedItem.class);
        return dao.findAll(queryInput, joins);
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAllItems(QueryInput queryInput, HashMap<String, String> aliases, String... joins) {
        BaseDao<BellReceivedItem> dao = new BaseDao<BellReceivedItem>(BellReceivedItem.class);
        return dao.findAll(queryInput, aliases, joins);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BellReceivedItem findItemById(Long id, String... joins) throws NoResultException {
        BaseDao<BellReceivedItem> dao = new BaseDao<BellReceivedItem>(BellReceivedItem.class);
        return dao.findById(id, joins);
    }

    public void create(BellReceivedItem receivedItem) {
        BaseDao<BellReceivedItem> dao = new BaseDao<BellReceivedItem>(BellReceivedItem.class);
        dao.create(receivedItem, receivedItem.getBellReceived().getId());
    }
    
    public void update(BellReceivedItem receivedItem) {
        BaseDao<BellReceivedItem> dao = new BaseDao<BellReceivedItem>(BellReceivedItem.class);
        dao.update(receivedItem, receivedItem.getBellReceived().getId());
    }
    
    public void deleteItem(Long id) {
        BaseDao<BellReceivedItem> dao = new BaseDao<BellReceivedItem>(BellReceivedItem.class);
        BellReceivedItem ri = dao.findById(id);
        LifoSessionLocal lifoSession = getLifoSession();
        lifoSession.deleteBellReceivedItem(ri);
        dao.delete(ri, ri.getBellReceived().getId());
    }

    public Boolean addReceivedItems(List<BellReceivedItem> items) {
        try {
            BaseDao<BellReceivedItem> dao = new BaseDao<BellReceivedItem>(BellReceivedItem.class);

            for (BellReceivedItem ri : items){
                dao.create(ri, ri.getBellReceived().getId());
            }
        } catch (Exception e){
            logger.error("Could not add receiving items to receiving", e);
            return false;
        }
        return true;
    }
    
    public void recalculateReceived(Long id) {
        BaseDao<Received> rDao = new BaseDao<Received>(Received.class);
        rDao.getSession().createSQLQuery("select updateBellReceived("+id+")").list();
    }
    

    private LifoSessionLocal getLifoSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (LifoSessionLocal)ctx.lookup(LifoSession.LocalJNDIStringNoLoader);
            }
            return (LifoSessionLocal)ctx.lookup(LifoSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup LifoSession", ne);
        }
        throw new RuntimeException("Could not lookup LifoSession");
    }
    
}
