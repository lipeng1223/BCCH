package com.bc.ejb.bellwether;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bc.dao.BaseDao;
import com.bc.dao.DaoResults;
import com.bc.orm.*;
import com.bc.struts.QueryInput;
import com.bc.util.IsbnUtil;

@Stateless
public class BellOrderSession implements BellOrderSessionLocal {

    public static final String LocalJNDIString = "inventory/"+BellOrderSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = BellOrderSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(BellOrderSession.class);
        
    public Integer getCount() {
        BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
        return dao.getCount();
    }
    
    public BellOrder findById(Long id) throws NoResultException {
        BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
        return dao.findById(id);
    }

    public BellOrder findById(Long id, String... joins) throws NoResultException {
        BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
        return dao.findById(id, joins);
    }
    
    public BellOrder findByOrderIdAndSku(String orderId, String sku) {
        BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
        Criteria crit = dao.getSession().createCriteria(BellOrder.class);
        crit.add(Restrictions.eq("orderId", orderId));
        crit.add(Restrictions.eq("sku", sku));
        return (BellOrder)crit.uniqueResult();
    }
    
    public BellOrderItem findNextItem(String sort, String dir, Integer offset, BellOrder order, String... joins){
        BaseDao<BellOrderItem> dao = new BaseDao<BellOrderItem>(BellOrderItem.class);
        QueryInput qi = new QueryInput(offset, 1);
        qi.addAndCriterion(Restrictions.eq("bellOrder", order));
        qi.setSortCol(sort);
        qi.setSortDir(dir);
        DaoResults dr = dao.findAll(qi, joins);
        if (dr != null && dr.getData() != null && dr.getData().size() > 0){
            return (BellOrderItem)dr.getData().get(0);
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(List<BellOrder> orders) {
        for (BellOrder bo : orders) create(bo);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(BellOrder order) {
        BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
        // get the next invoice number
        BaseDao<BellInvoiceNumber> indao = new BaseDao<BellInvoiceNumber>(BellInvoiceNumber.class);
        BellInvoiceNumber in = new BellInvoiceNumber();
        indao.create(in, null);
        order.setInvoiceNumber(in.getId().toString());
        dao.create(order, null);
        updateLocations(order);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(List<BellOrder> orders) {
        for (BellOrder bo : orders) update(bo);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(BellOrder order) {
        BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
        dao.update(order, null);
        updateLocations(order);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateLocations(BellOrder order) {
        if (order.getLocation() != null && order.getSku() != null) {
            BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
            dao.getSession().createSQLQuery("update bell_sku as bs set bs.location = '"+order.getLocation()+"' where bs.sku = '"+order.getSku()+"'").executeUpdate();
        }
    }

    public List<Long> getAllInventoryItemIds(Long orderId) {
        BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
        StringBuilder sb = new StringBuilder("select inventory_item_id from customer_order_item where customer_order_id = ");
        sb.append(orderId);
        List<BigInteger> list = dao.getSession().createSQLQuery(sb.toString()).list();
        List<Long> ids = new ArrayList<Long>();
        for (BigInteger bi : list){
            ids.add(bi.longValue());
        }
        return ids;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
        dao.delete(dao.findById(id), null);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<BellOrderItem> findLastN(InventoryItem ii, Integer limit) {
        BaseDao<BellOrderItem> dao = new BaseDao<BellOrderItem>(BellOrderItem.class);
        Criteria crit = dao.getSession().createCriteria(BellOrderItem.class);
        crit.add(Restrictions.eq("inventoryItem", ii));
        crit.addOrder(Order.desc("createTime"));
        crit.setFetchSize(limit);
        crit.setMaxResults(limit);
        return crit.list();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput, String... joins) {
        BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
        return dao.findAll(queryInput, joins);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput,HashMap<String, String> aliases, String... joins) {
        BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
        return dao.findAll(queryInput, aliases, joins);
    }
     
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAllItems(QueryInput queryInput, String... joins) {
        BaseDao<BellOrderItem> dao = new BaseDao<BellOrderItem>(BellOrderItem.class);
        return dao.findAll(queryInput, joins);
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAllItems(QueryInput queryInput, HashMap<String, String> aliases, String... joins) {
        BaseDao<BellOrderItem> dao = new BaseDao<BellOrderItem>(BellOrderItem.class);
        return dao.findAll(queryInput, aliases, joins);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BellOrderItem findItemById(Long id) throws NoResultException {
        BaseDao<BellOrderItem> dao = new BaseDao<BellOrderItem>(BellOrderItem.class);
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BellOrderItem findItemById(Long id, String... joins) throws NoResultException {
        BaseDao<BellOrderItem> dao = new BaseDao<BellOrderItem>(BellOrderItem.class);
        return dao.findById(id, joins);
    }
    
    public BellOrderItem findItemByIsbn(BellOrder order, String isbn) {
        BaseDao<BellOrderItem> dao = new BaseDao<BellOrderItem>(BellOrderItem.class);
        Criteria crit = dao.getSession().createCriteria(BellOrderItem.class);
        crit.add(Restrictions.eq("isbn", isbn));
        crit.add(Restrictions.eq("bellOrder", order));
        List<BellOrderItem> list = crit.list();
        if (list.size() > 1){
            logger.error("There is more than one BellOrderItem for isbn: "+isbn);
        }
        if (list.size() > 0) return list.get(0);
        
        // see if isbn 10 or 13 exists for this isbn
        if (isbn.length() == 10) {
            crit = dao.getSession().createCriteria(BellOrderItem.class);
            crit.add(Restrictions.eq("isbn", IsbnUtil.getIsbn13(isbn)));
            crit.add(Restrictions.eq("bellOrder", order));
            list = crit.list();
            if (list.size() > 1){
                logger.error("There is more than one BellOrderItem for isbn: "+IsbnUtil.getIsbn13(isbn));
            }
            if (list.size() > 0) return list.get(0);
        } else if (isbn.length() == 13 && isbn.startsWith("978")) {
            crit = dao.getSession().createCriteria(BellOrderItem.class);
            crit.add(Restrictions.eq("isbn", IsbnUtil.getIsbn10(isbn)));
            crit.add(Restrictions.eq("bellOrder", order));
            list = crit.list();
            if (list.size() > 1){
                logger.error("There is more than one BellOrderItem for isbn: "+IsbnUtil.getIsbn10(isbn));
            }
            if (list.size() > 0) return list.get(0);
        }
        
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(BellOrderItem orderItem) {
        BaseDao<BellOrderItem> dao = new BaseDao<BellOrderItem>(BellOrderItem.class);
        dao.create(orderItem, orderItem.getBellOrder().getId());
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(BellOrderItem orderItem) {
        BaseDao<BellOrderItem> dao = new BaseDao<BellOrderItem>(BellOrderItem.class);
        dao.update(orderItem, orderItem.getBellOrder().getId());
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteItem(Long id) {
        BaseDao<BellOrderItem> dao = new BaseDao<BellOrderItem>(BellOrderItem.class);
        BellOrderItem coi = dao.findById(id);
        dao.delete(coi, coi.getBellOrder().getId());
    }

    public void recalculateOrderTotals(Long id) {
        BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
        dao.getSession().createSQLQuery("select updateBellOrder("+id+")").list();
    }
    
    public void recalculateAllOrderTotals(Long id) {
        BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
        dao.getSession().createSQLQuery("select updateAllBellOrder("+id+")").list();
    }
    
    public void recalculateOrderItemTotals(Long id) {
        BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
        dao.getSession().createSQLQuery("select updateBellOrderItem("+id+")").list();
    }
    
    public Boolean addOrderItems(List<BellOrderItem> items) {
        try {
            BaseDao<BellOrderItem> boidao = new BaseDao<BellOrderItem>(BellOrderItem.class);

            for (BellOrderItem boi : items){
                boidao.create(boi, boi.getBellOrder().getId());
            }
        } catch (Exception e){
            logger.error("Could not add bell order items to order", e);
            return false;
        }
        return true;
    }
    
    public void shipMax(Long id) {
        BaseDao<BellOrder> dao = new BaseDao<BellOrder>(BellOrder.class);
        BaseDao<BellOrderItem> boidao = new BaseDao<BellOrderItem>(BellOrderItem.class);
        BellOrder order = dao.findById(id, "bellOrderItems", "bellOrderItems.bellInventory");
        HashMap<String, Integer> sofar = new HashMap<String, Integer>();
        for (BellOrderItem boi : order.getBellOrderItems()){
            BellInventory bi = boi.getBellInventory();
            if (bi == null){
                // no inventory item
                boi.setFilled(boi.getQuantity());
            } else {
                int committed = 0;
                if (bi.getCommitted() != null){
                    committed = bi.getCommitted() - boi.getQuantity();
                }
                if (sofar.containsKey(bi.getIsbn())){
                    Integer sf = sofar.get(bi.getIsbn());
                    committed -= sf;
                    sofar.put(bi.getIsbn(), committed);
                } else {
                    sofar.put(bi.getIsbn(), boi.getQuantity());
                }
                if (committed > bi.getOnhand()){
                    boi.setFilled(0);
                } else {
                    if (committed < 0){
                        committed = 0;
                    }
                    int max = bi.getOnhand() - committed;
                    if (max > boi.getQuantity()){
                        max = boi.getQuantity();
                    }
                    int quantity = max;
                    if (max < 0){
                        quantity = boi.getQuantity()+max;
                        if (quantity < 0){
                            quantity = 0;
                        }
                    }
                    boi.setFilled(quantity);
                }
            }
            boidao.update(boi, order.getId());
        }
    }
}
