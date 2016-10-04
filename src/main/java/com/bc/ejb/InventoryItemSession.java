package com.bc.ejb;


import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.bc.dao.BaseDao;
import com.bc.dao.DaoResults;
import com.bc.orm.InventoryItem;
import com.bc.struts.QueryInput;
import com.bc.util.IsbnUtil;
import java.util.ArrayList;

@Stateless
public class InventoryItemSession implements InventoryItemSessionLocal {

    public static final String LocalJNDIString = "inventory/"+InventoryItemSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = InventoryItemSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(InventoryItemSession.class);
        
    public Integer getCount() {
        BaseDao<InventoryItem> dao = new BaseDao<InventoryItem>(InventoryItem.class);
        return dao.getCount();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public InventoryItem findById(Long id) throws NoResultException {
        BaseDao<InventoryItem> dao = new BaseDao<InventoryItem>(InventoryItem.class);
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public InventoryItem findById(Long id, String... joins) throws NoResultException {
        BaseDao<InventoryItem> dao = new BaseDao<InventoryItem>(InventoryItem.class);
        return dao.findById(id, joins);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(InventoryItem inventoryItem) {
        BaseDao<InventoryItem> dao = new BaseDao<InventoryItem>(InventoryItem.class);
        dao.create(inventoryItem, null);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(InventoryItem inventoryItem) {
        BaseDao<InventoryItem> dao = new BaseDao<InventoryItem>(InventoryItem.class);
        dao.update(inventoryItem, null);
        updatePendingBins(inventoryItem.getId(), inventoryItem.getBin());
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        BaseDao<InventoryItem> dao = new BaseDao<InventoryItem>(InventoryItem.class);
        dao.delete(dao.findById(id), null);
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput, String... joins) {
        BaseDao<InventoryItem> iiDao = new BaseDao<InventoryItem>(InventoryItem.class);
        return iiDao.findAll(queryInput, joins);
    }


    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public InventoryItem findByIsbnCond(String isbn, String cond, String... joins) throws NoResultException {
        if (isbn == null || cond == null) {
            logger.error("Cannot find by isbn, cond that are null - isbn: "+isbn+" cond: "+cond);
            return null;
        }
        BaseDao<InventoryItem> iiDao = new BaseDao<InventoryItem>(InventoryItem.class);
        Criteria crit = iiDao.getSession().createCriteria(InventoryItem.class);
        crit.add(Restrictions.eq("isbn", isbn));
        crit.add(Restrictions.eq("cond", cond));
        List<InventoryItem> list = crit.list();
        if (list.size() > 1){
            logger.error("There is more than one InventoryItem for isbn: "+isbn+" cond: "+cond);
        }
        if (list.size() > 0) return list.get(0);
        
        // see if isbn 10 or 13 exists for this isbn
        if (isbn.length() == 10) {
            crit = iiDao.getSession().createCriteria(InventoryItem.class);
            crit.add(Restrictions.eq("isbn", IsbnUtil.getIsbn13(isbn)));
            crit.add(Restrictions.eq("cond", cond));
            list = crit.list();
            if (list.size() > 1){
                logger.error("There is more than one InventoryItem for isbn: "+IsbnUtil.getIsbn13(isbn)+" cond: "+cond);
            }
            if (list.size() > 0) return list.get(0);
        } else if (isbn.length() == 13 && isbn.startsWith("978")) {
            crit = iiDao.getSession().createCriteria(InventoryItem.class);
            crit.add(Restrictions.eq("isbn", IsbnUtil.getIsbn10(isbn)));
            crit.add(Restrictions.eq("cond", cond));
            list = crit.list();
            if (list.size() > 1){
                logger.error("There is more than one InventoryItem for isbn: "+IsbnUtil.getIsbn10(isbn)+" cond: "+cond);
            }
            if (list.size() > 0) return list.get(0);
        }
        
        return null;
    }
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public InventoryItem findByTitleCond(String title, String cond, String... joins) throws NoResultException {
        if (title == null || cond == null) {
            logger.error("Cannot find by isbn, cond that are null - isbn: "+title+" cond: "+cond);
            return null;
        }
        BaseDao<InventoryItem> iiDao = new BaseDao<InventoryItem>(InventoryItem.class);
        Criteria crit = iiDao.getSession().createCriteria(InventoryItem.class);
        crit.add(Restrictions.like("title", title + "%"));
        crit.add(Restrictions.eq("cond", cond));
        List<InventoryItem> list = crit.list();
        if (list.size() > 1){
            logger.info("There is more than one InventoryItem for isbn: "+title+" cond: "+cond);
        }
        if (list.size() > 0) return list.get(0);
                
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<InventoryItem> findByIsbn(String isbn, String... joins) {
        if (isbn == null) {
            logger.error("Cannot find by isbn that is null - isbn: "+isbn);
            return null;
        }
        BaseDao<InventoryItem> iiDao = new BaseDao<InventoryItem>(InventoryItem.class);
        Criteria crit = iiDao.getSession().createCriteria(InventoryItem.class);
        crit.add(Restrictions.eq("isbn", isbn));
        List<InventoryItem> list = crit.list();
        if (list.size() > 0) return list;
        
        // see if isbn 10 or 13 exists for this isbn
        if (isbn.length() == 10) {
            crit = iiDao.getSession().createCriteria(InventoryItem.class);
            crit.add(Restrictions.eq("isbn", IsbnUtil.getIsbn13(isbn)));
            list = crit.list();
            if (list.size() > 0) return list;
        } else if (isbn.length() == 13 && isbn.startsWith("978")) {
            crit = iiDao.getSession().createCriteria(InventoryItem.class);
            crit.add(Restrictions.eq("isbn", IsbnUtil.getIsbn10(isbn)));
            list = crit.list();
            if (list.size() > 0) return list;
        }
        return new ArrayList<InventoryItem>();
    }

    public void recalculateCommitted(Long id) {
        BaseDao<InventoryItem> iiDao = new BaseDao<InventoryItem>(InventoryItem.class);
        iiDao.getSession().createSQLQuery("select recalculateInvCommitted("+id+")").list();
    }

    public void updatePendingBins(Long iiId, String bin) {
        BaseDao<InventoryItem> iiDao = new BaseDao<InventoryItem>(InventoryItem.class);
        StringBuilder sql = new StringBuilder();
        sql.append("update received_item as ri, received as r set ");
        sql.append("ri.bin = '");
        sql.append(bin);
        sql.append("' where ri.inventory_item_id = ");
        sql.append(iiId);
        sql.append(" and (r.posted = false or r.posted is null) and r.id = ri.received_id");
        iiDao.getSession().createSQLQuery(sql.toString()).executeUpdate();
        
        sql = new StringBuilder();
        sql.append("update customer_order_item as coi, customer_order as co set ");
        sql.append("coi.bin = '");
        sql.append(bin);
        sql.append("' where coi.inventory_item_id = ");
        sql.append(iiId);
        sql.append(" and (co.posted = false or co.posted is null) and co.id = coi.customer_order_id");
        iiDao.getSession().createSQLQuery(sql.toString()).executeUpdate();
    }
    
    public Boolean canBeDeleted(Long id){
        BaseDao<InventoryItem> iiDao = new BaseDao<InventoryItem>(InventoryItem.class);
        StringBuilder sql = new StringBuilder();
        sql.append("select id from customer_order_item where inventory_item_id = ");
        sql.append(id);
        sql.append(" limit 1");
        List test = iiDao.getSession().createSQLQuery(sql.toString()).list();
        if (test != null && test.size() > 0) return false;
        
        InventoryItem ii = iiDao.findById(id);
        sql = new StringBuilder();
        sql.append("select id from received_item where inventory_item_id = ");
        sql.append(id);
        sql.append(" limit 1");
        test = iiDao.getSession().createSQLQuery(sql.toString()).list();
        if (test != null && test.size() > 0) return false;
        
        return true;
    }
    
}
