package com.bc.ejb;


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
import com.bc.orm.CustomerOrder;
import com.bc.orm.CustomerOrderItem;
import com.bc.orm.InventoryItem;
import com.bc.orm.InvoiceNumber;
import com.bc.struts.QueryInput;
import com.bc.util.IsbnUtil;
import org.hibernate.Session;

@Stateless
public class OrderSession implements OrderSessionLocal {

    public static final String LocalJNDIString = "inventory/"+OrderSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = OrderSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(OrderSession.class);
        
    public Integer getCount() {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        return dao.getCount();
    }
    
    public CustomerOrder findById(Long id) throws NoResultException {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        return dao.findById(id);
    }

    public CustomerOrder findById(Long id, String... joins) throws NoResultException {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        return dao.findById(id, joins);
    }
    
    public CustomerOrderItem findNextItem(String sort, String dir, Integer offset, CustomerOrder order, String... joins){
        BaseDao<CustomerOrderItem> dao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);
        QueryInput qi = new QueryInput(offset, 1);
        qi.addAndCriterion(Restrictions.eq("customerOrder", order));
        qi.setSortCol(sort);
        qi.setSortDir(dir);
        DaoResults dr = dao.findAll(qi, joins);
        if (dr != null && dr.getData() != null && dr.getData().size() > 0){
            return (CustomerOrderItem)dr.getData().get(0);
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(CustomerOrder order) {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        // get the next invoice number
        BaseDao<InvoiceNumber> indao = new BaseDao<InvoiceNumber>(InvoiceNumber.class);
        InvoiceNumber in = new InvoiceNumber();
        indao.create(in, null);
        if (order.getCreditMemo()){
            order.setInvoiceNumber(in.getId().toString()+"-CM");
        } else {
            order.setInvoiceNumber(in.getId().toString());
        }
        dao.create(order, null);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(CustomerOrder order) {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        if (!order.getPosted() && order.getDiscount() != null){
            // make sure we update all of the order items to this orders discount
            dao.getSession().createSQLQuery("update customer_order_item set discount = "+order.getDiscount()+" where customer_order_id = "+order.getId());
        }
        dao.update(order, null);
    }

    public List<Long> getAllInventoryItemIds(Long orderId) {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        StringBuilder sb = new StringBuilder("select inventory_item_id from customer_order_item where customer_order_id = ");
        sb.append(orderId);
        List<BigInteger> list = dao.getSession().createSQLQuery(sb.toString()).list();
        List<Long> ids = new ArrayList<Long>();
        for (BigInteger bi : list){
            ids.add(bi.longValue());
        }
        return ids;
    }
    
    public List<Long> getAllOrderIds(String isbn, int cnt, boolean bell, boolean restricted, boolean he) {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        StringBuilder sb = new StringBuilder("select o.customer_order_id, count(o.customer_order_id) from customer_order_item o join inventory_item i on o.inventory_item_id = i.id where (i.isbn in ");
        sb.append(isbn);
        sb.append(")");
        if (!bell){
            sb.append(" and i.bellbook=0");
        }
        if (!restricted){
            sb.append(" and i.restricted=0");
        }
        if (!he){
            sb.append(" and i.he=0");
        }
        sb.append(" group by o.customer_order_id");
        logger.info("Sql " + sb.toString());
        Session session = dao.getSession();
        List<Object[]> list = session.createSQLQuery(sb.toString()).list();
        List<Long> ids = new ArrayList<Long>();
        for (Object[] os : list){
            BigInteger bi = (BigInteger) os[0];
            Integer c = ((BigInteger) os[1]).intValue();
            if (cnt == c){
                ids.add(bi.longValue());
                logger.info("Got OID : " + bi.longValue());
            }
        }
        logger.info("Got OIDs");
        return ids;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        dao.delete(dao.findById(id), null);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<CustomerOrderItem> findLastN(InventoryItem ii, Integer limit) {
        BaseDao<CustomerOrderItem> dao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);
        Criteria crit = dao.getSession().createCriteria(CustomerOrderItem.class);
        crit.add(Restrictions.eq("inventoryItem", ii));
        crit.addOrder(Order.desc("createTime"));
        crit.setFetchSize(limit);
        crit.setMaxResults(limit);
        return crit.list();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput, String... joins) {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        return dao.findAll(queryInput, joins);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput,HashMap<String, String> aliases, String... joins) {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        return dao.findAll(queryInput, aliases, joins);
    }
     
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAllItems(QueryInput queryInput, String... joins) {
        BaseDao<CustomerOrderItem> dao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);
        return dao.findAll(queryInput, joins);
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAllItems(QueryInput queryInput, HashMap<String, String> aliases, String... joins) {
        BaseDao<CustomerOrderItem> dao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);
        return dao.findAll(queryInput, aliases, joins);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public CustomerOrderItem findItemById(Long id) throws NoResultException {
        BaseDao<CustomerOrderItem> dao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public CustomerOrderItem findItemById(Long id, String... joins) throws NoResultException {
        BaseDao<CustomerOrderItem> dao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);
        return dao.findById(id, joins);
    }
    
    public CustomerOrderItem findItemByIsbnCond(CustomerOrder order, String isbn, String cond) {
        BaseDao<CustomerOrderItem> dao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);
        Criteria crit = dao.getSession().createCriteria(CustomerOrderItem.class);
        crit.add(Restrictions.eq("isbn", isbn));
        crit.add(Restrictions.eq("cond", cond));
        crit.add(Restrictions.eq("customerOrder", order));
        List<CustomerOrderItem> list = crit.list();
        if (list.size() > 1){
            logger.error("There is more than one CustomerOrderItem for isbn: "+isbn+" cond: "+cond);
        }
        if (list.size() > 0) return list.get(0);
        
        // see if isbn 10 or 13 exists for this isbn
        if (isbn.length() == 10) {
            crit = dao.getSession().createCriteria(CustomerOrderItem.class);
            crit.add(Restrictions.eq("isbn", IsbnUtil.getIsbn13(isbn)));
            crit.add(Restrictions.eq("cond", cond));
            crit.add(Restrictions.eq("customerOrder", order));
            list = crit.list();
            if (list.size() > 1){
                logger.error("There is more than one CustomerOrderItem for isbn: "+IsbnUtil.getIsbn13(isbn)+" cond: "+cond);
            }
            if (list.size() > 0) return list.get(0);
        } else if (isbn.length() == 13 && isbn.startsWith("978")) {
            crit = dao.getSession().createCriteria(CustomerOrderItem.class);
            crit.add(Restrictions.eq("isbn", IsbnUtil.getIsbn10(isbn)));
            crit.add(Restrictions.eq("cond", cond));
            crit.add(Restrictions.eq("customerOrder", order));
            list = crit.list();
            if (list.size() > 1){
                logger.error("There is more than one CustomerOrderItem for isbn: "+IsbnUtil.getIsbn10(isbn)+" cond: "+cond);
            }
            if (list.size() > 0) return list.get(0);
        }
        
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(CustomerOrderItem orderItem) {
        BaseDao<CustomerOrderItem> dao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);
        dao.create(orderItem, orderItem.getCustomerOrder().getId());
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(CustomerOrderItem orderItem) {
        BaseDao<CustomerOrderItem> dao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);
        dao.update(orderItem, orderItem.getCustomerOrder().getId());
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteItem(Long id) {
        BaseDao<CustomerOrderItem> dao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);
        CustomerOrderItem coi = dao.findById(id);
        dao.delete(coi, coi.getCustomerOrder().getId());
    }

    public void recalculateOrderTotals(Long id) {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        dao.getSession().createSQLQuery("select updateCustomerOrder("+id+")").list();
    }
    
    public void recalculateAllOrderTotals(Long id) {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        dao.getSession().createSQLQuery("select updateAllCustomerOrder("+id+")").list();
    }
    
    public void recalculateOrderItemTotals(Long id) {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        dao.getSession().createSQLQuery("select updateCustomerOrderItem("+id+")").list();
    }

    public Boolean addOrderItems(List<CustomerOrderItem> items) {
        try {
            BaseDao<CustomerOrderItem> coidao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);

            for (CustomerOrderItem coi : items){
                coidao.create(coi, coi.getCustomerOrder().getId());
            }
        } catch (Exception e){
            logger.error("Could not add order items to order", e);
            return false;
        }
        return true;
    }

    public void shipMax(Long id) {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        BaseDao<CustomerOrderItem> coidao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);
        CustomerOrder order = dao.findById(id, "customerOrderItems", "customerOrderItems.inventoryItem");
        HashMap<String, Integer> sofar = new HashMap<String, Integer>();
        for (CustomerOrderItem coi : order.getCustomerOrderItems()){
            InventoryItem ii = coi.getInventoryItem();
            if (ii == null){
                // no inventory item
                coi.setFilled(coi.getQuantity());
            } else {
                int committed = 0;
                if (ii.getCommitted() != null){
                    committed = ii.getCommitted() - coi.getQuantity();
                }
                if (sofar.containsKey(ii.getIsbn())){
                    Integer sf = sofar.get(ii.getIsbn());
                    committed -= sf;
                    sofar.put(ii.getIsbn(), committed);
                } else {
                    sofar.put(ii.getIsbn(), coi.getQuantity());
                }
                if (committed > ii.getOnhand()){
                    coi.setFilled(0);
                } else {
                    if (committed < 0){
                        committed = 0;
                    }
                    int max = ii.getOnhand() - committed;
                    if (max > coi.getQuantity()){
                        max = coi.getQuantity();
                    }
                    int quantity = max;
                    if (max < 0){
                        quantity = coi.getQuantity()+max;
                        if (quantity < 0){
                            quantity = 0;
                        }
                    }
                    coi.setFilled(quantity);
                }
            }
            coidao.update(coi, order.getId());
            //logger.info("" + coi.getId());
        }
        logger.info("finished ship max");
    }
    
    public void fixZeroPrice(Long id) {
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        BaseDao<CustomerOrderItem> coidao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);
        CustomerOrder order = dao.findById(id, "customerOrderItems", "customerOrderItems.inventoryItem");
        for (CustomerOrderItem coi : order.getCustomerOrderItems()){
            if (coi.getPrice() == null || coi.getPrice() == 0){
                InventoryItem ii = coi.getInventoryItem();
                if (ii != null && ii.getSellingPrice() != null && ii.getSellingPrice() > 0){
                    coi.setPrice(ii.getSellingPrice());
                    coidao.update(coi, order.getId());
                }
            }
        }
    }
    
    public void close(){
        BaseDao<CustomerOrder> dao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        dao.getSession().close();
    }
    
}
