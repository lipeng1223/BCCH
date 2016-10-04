package com.bc.ejb;


import com.bc.dao.BaseDao;
import com.bc.orm.CustomerOrderItem;
import com.bc.orm.ReceivedItem;
import com.bc.orm.InventoryItem;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@Stateless
public class UtilitySession implements UtilitySessionLocal {

    public static final String LocalJNDIString = "inventory/"+UtilitySession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = UtilitySession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(UtilitySession.class);
    
    // ascending order sorts
    static final Comparator<ReceivedItem> REC_ITEM_DATE = 
                                        new Comparator<ReceivedItem>() {
            public int compare(ReceivedItem ri1, ReceivedItem ri2) {
                if (ri1.getDate() == null && ri2.getDate() != null) return -1;
                if (ri2.getDate() == null && ri1.getDate() != null) return +1;
                if (ri1.getDate() == null && ri2.getDate() == null) return 0;
                return ri1.getDate().compareTo(ri2.getDate());
            }
    };

    static final Comparator<CustomerOrderItem> ORDER_ITEM_DATE = 
                                        new Comparator<CustomerOrderItem>() {
            public int compare(CustomerOrderItem coi1, CustomerOrderItem coi2) {
                if (coi1.getCustomerOrder() == null && coi2.getCustomerOrder() != null) return -1;
                if (coi2.getCustomerOrder() == null && coi1.getCustomerOrder() != null) return +1;
                if (coi2.getCustomerOrder() == null && coi1.getCustomerOrder() == null) return 0;
                
                if (coi1.getCustomerOrder().getPostDate() == null && coi2.getCustomerOrder().getPostDate() != null) return -1;
                if (coi2.getCustomerOrder().getPostDate() == null && coi1.getCustomerOrder().getPostDate() != null) return +1;
                if (coi1.getCustomerOrder().getPostDate() == null && coi2.getCustomerOrder().getPostDate() == null) return 0;
                return coi1.getCustomerOrder().getPostDate().compareTo(coi2.getCustomerOrder().getPostDate());
            }
    };
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void backOutOnhand() {
        logger.info("Start backOutOnhand");
        try {
            
            BaseDao<InventoryItem> iiDao = new BaseDao<InventoryItem>(InventoryItem.class);
           
            UserTransaction tx = null;
            // /home/jboss/
            Workbook workbook = Workbook.getWorkbook(new File("/home/jboss/Master_060513_BR_2.xls"));
            Sheet s = workbook.getSheet(0);
            int numRows = s.getRows();
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            for(int row = 1; row < numRows; row++) {
                if (row % 100 == 0){
                    tx.commit();
                    tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
                    tx.setTransactionTimeout(600);
                    tx.begin();
                }                
                String id = s.getCell(0, row).getContents();
                String isbn = s.getCell(1, row).getContents();
                String bin = s.getCell(12, row).getContents();
                String available = s.getCell(15, row).getContents();
                String onhand = s.getCell(16, row).getContents();
                String committed = s.getCell(17, row).getContents();
                /*
                List<Integer> iionhand = iiDao.getSession().createSQLQuery("select onhand from inventory_item where id = "+id).addScalar("onhand", Hibernate.INTEGER).list();
                if (iionhand.get(0) > Integer.parseInt(onhand) ){
                    logger.info("mismatch "+id+", "+isbn+", "+bin+", "+onhand+", "+iionhand.get(0));
                }
                */
                
                List<Integer> avail = iiDao.getSession().createSQLQuery("select sum(available) as a from received_item where inventory_item_id = "+id).addScalar("a", Hibernate.INTEGER).list();
                logger.info(row+" "+id+" "+available+" "+onhand+" "+committed+" avail: "+avail.get(0));
                //if (avail.size() == 1 && avail.get(0) == Integer.parseInt(onhand)){
                    logger.info("UPDATED "+row+" "+id+" "+available+" "+onhand+" "+committed);
                    iiDao.getSession().createSQLQuery("update inventory_item set onhand = "+onhand+", available = "+available+",  commited = "+committed+" where id = "+id).executeUpdate();
                    iiDao.getSession().createSQLQuery("update inventory_item as ii, received_item as ri set ii.onhand = ii.onhand + ri.available where ri.createTimeBc > '2013-06-05 20:00:00' and ii.id = "+id+" and ri.inventory_item_id = "+id).executeUpdate();
                //}
            }
            tx.commit();
            
            
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iiDao.getSession().createSQLQuery("update inventory_item as ii set ii.available = ii.onhand - ii.commited").executeUpdate();
            tx.commit();
            
            
            /*
            List<Object[]> audits = iiDao.getSession().createSQLQuery("select tableId, auditTime, currentValue1 from audit where tableName = 'inventory_item' and columnName1 = 'onhand' and auditTime < '2013-06-06 20:00:00' order by auditTime desc").list();
            
            UserTransaction tx = null;
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            HashSet<Long> alreadySeen = new HashSet<Long>();
            for (Object[] audit : audits){
                logger.info(audit[0]+" auditTime: "+audit[1]+" onhand: "+audit[2]);
                Long id = Long.parseLong(audit[0].toString());
                if (!alreadySeen.contains(id)){
                    iiDao.getSession().createSQLQuery("update inventory_item set onhand = "+audit[2]+" where id = "+id);
                    alreadySeen.add(id);
                }
            }
            tx.commit();
            
            
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iiDao.getSession().createSQLQuery("update inventory_item as ii set ii.available = ii.onhand - ii.commited").executeUpdate();
            tx.commit();
            */
            
            
            
            //List<Long> ids = iiDao.getSession().createSQLQuery("select id from inventory_item order by id asc").addScalar("id", Hibernate.LONG).list();
            //List<Long> ids = iiDao.getSession().createSQLQuery("select id from inventory_item where isbn = '0061734780' order by id asc").addScalar("id", Hibernate.LONG).list();
            /*
            logger.info("Have the inventory item ids, starting work");
            
            UserTransaction tx = null;
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            int count = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date deadDate = sdf.parse("06/06/2013");
            for (Long iiId : ids){
                count++;
                if (count % 100 == 0){
                    // TESTING
                    break;
                    
                    //tx.commit();
                    //logger.info("finished working on "+count);
                    //tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
                    //tx.setTransactionTimeout(600);
                    //tx.begin();
                }
                InventoryItem ii = iiDao.findById(iiId);
                logger.info(ii.getIsbn());
                List<Object[]> audits = iiDao.getSession().createSQLQuery("select auditTime, tableName, auditAction, auditMessage, columnName1, previousValue1, currentValue1, tableId, parentTableId from audit where tableName = 'inventory_item' and columnName1 = 'onhand' and auditMessage like '%"+ii.getIsbn()+"%' order by auditTime desc limit 20").
                    addScalar("auditTime", Hibernate.TIMESTAMP).list();
                for (Object[] audit : audits){
                    logger.info(ii.getIsbn()+" auditTime: "+audit[0]+" onhand: "+audit[6]);
                    
                }
            }
            tx.commit();
            * 
            */
            
        } catch (Throwable t){
           logger.error("Could not backOutOnhand", t);
        }
        logger.info("Finished backOutOnhand");
     }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void fixInventoryCounts() {
        logger.info("Start fixInventoryCounts");
        try {
            BaseDao<InventoryItem> iiDao = new BaseDao<InventoryItem>(InventoryItem.class);
            BaseDao<ReceivedItem> riDao = new BaseDao<ReceivedItem>(ReceivedItem.class);
            BaseDao<CustomerOrderItem> coiDao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);
            UserTransaction tx = null;

            // go through all inventory
            // get all of the receivings for that item
            // do the lifo on the received items for the orders
            
            List<Long> ids = iiDao.getSession().createSQLQuery("select id from inventory_item order by id asc").addScalar("id", Hibernate.LONG).list();
            logger.info("Have the inventory item ids, starting work");
            
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iiDao.getSession().createSQLQuery("update received as r set r.po_date = r.createTimeBc where r.po_date is null").executeUpdate();
            iiDao.getSession().createSQLQuery("update customer_order set post_date = posted_by_date where posted = true and post_date is null").executeUpdate();
            iiDao.getSession().createSQLQuery("update received_item as ri, received as r set ri.date = r.po_date where ri.received_id = r.id").executeUpdate();
            tx.commit();

            int count = 0;
            for (Long iiId : ids){
                count++;
                if (count % 500 == 0){
                    logger.info("finished working on "+count);
                }
                tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
                tx.setTransactionTimeout(600);
                tx.begin();
                iiDao.getSession().createSQLQuery("update received_item as ri set ri.available = ri.quantity where ri.inventory_item_id = "+iiId).executeUpdate();
                tx.commit();
                
                // get the orders and the receivings
                InventoryItem ii = iiDao.findById(iiId);
                
                //List<ReceivedItem> recItems = new ArrayList<ReceivedItem>(ii.getReceivedItems());
                //List<CustomerOrderItem> orderItems = new ArrayList<CustomerOrderItem>(ii.getCustomerOrderItems());
                //Collections.sort(recItems, REC_ITEM_DATE);
                //Collections.sort(orderItems, ORDER_ITEM_DATE);
                
                // run through and recalc the available
                boolean done = false;
                int coiCount = 0; 
                int size = 500;
                while (!done) {
                    Criteria coiCrit = coiDao.getSession().createCriteria(CustomerOrderItem.class);
                    coiCrit.setFetchSize(size);
                    coiCrit.setMaxResults(size);
                    coiCrit.setFirstResult(coiCount*size);
                    coiCrit.add(Restrictions.eq("inventoryItem", ii));
                    coiCrit.setFetchMode("customerOrder", FetchMode.JOIN);
                    coiCrit.createAlias("customerOrder", "co");
                    coiCrit.addOrder(Order.desc("co.postDate"));
                    List<CustomerOrderItem> orderItems = coiCrit.list();
                    done = orderItems.isEmpty();
                    if (done) break;
                    
                    if (coiCount > 0){
                        logger.info("    working on order items "+(coiCount*size)+" for inventory item id "+iiId);
                    }
                    
                    coiCount++;
                    
                    tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
                    tx.setTransactionTimeout(600);
                    tx.begin();
                    
                    for (CustomerOrderItem coi : orderItems){
                        if (coi.getCustomerOrder() == null || !coi.getCustomerOrder().getPosted()) continue; // only doing work on posted

                        // get the received items before this post
                        Criteria riCrit = coiDao.getSession().createCriteria(ReceivedItem.class);
                        riCrit.add(Restrictions.eq("inventoryItem", ii));
                        riCrit.add(Restrictions.le("date", coi.getCustomerOrder().getPostDate()));
                        riCrit.addOrder(Order.desc("date"));
                        List<ReceivedItem> recItems = riCrit.list();
                        
                        if (coi.getCredit()){
                            if (!coi.getCreditDamage()){
                                if (recItems.size() > 0) {
                                    int qty = coi.getQuantity();
                                    for (int i = recItems.size()-1; i > -1; i--){
                                        ReceivedItem ri = recItems.get(i);
                                        ri.setAvailable(ri.getAvailable()+qty);
                                        if (ri.getAvailable() > ri.getQuantity()){
                                            qty = ri.getAvailable()-ri.getQuantity();
                                            ri.setAvailable(ri.getQuantity());
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }

                        } else {
                            // regular order
                            int quantity = coi.getQuantity();
                            int soFar = 0;
                            int wanted = coi.getFilled();
                            if (wanted > 0){
                                // try and only give what is wanted
                                quantity = wanted;
                            }
                            for (ReceivedItem ri : recItems){
                                if (soFar >= quantity) break;
                                if (quantity - soFar - ri.getAvailable() >= 0){
                                    soFar += ri.getAvailable();
                                    ri.setAvailable(0);
                                } else {
                                    int needed = quantity-soFar;
                                    soFar += needed;
                                    ri.setAvailable(ri.getAvailable()-needed);
                                }
                                if (ri.getAvailable() < 0) ri.setAvailable(0);
                            }

                        } 
                    }
                    orderItems.clear();
                    riDao.flushAndClear();
                    tx.commit();
                    
                }
                
            }
            
            logger.info("Finished inventory, running count sql updates...");
            
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iiDao.getSession().createSQLQuery("update inventory_item as ii set ii.onhand = (select sum(ri.available) from received_item as ri where ri.inventory_item_id = ii.id) where ii.id in (select rid.inventory_item_id from received_item as rid where rid.available > 0)").executeUpdate();
            tx.commit();
            
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iiDao.getSession().createSQLQuery("update inventory_item as ii set ii.onhand = 0 where (select sum(ri.available) from received_item as ri where ri.inventory_item_id = ii.id) = 0").executeUpdate();
            tx.commit();
            
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iiDao.getSession().createSQLQuery("update inventory_item as ii set ii.onhand = 0 where (select sum(ri.available) from received_item as ri where ri.inventory_item_id = ii.id) is null").executeUpdate();
            tx.commit();
            
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iiDao.getSession().createSQLQuery("update inventory_item as ii set ii.onhand = 0 where ii.onhand is null").executeUpdate();
            tx.commit();
            
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iiDao.getSession().createSQLQuery("update inventory_item as ii set ii.commited = (select sum(coi.quantity) from customer_order_item as coi, customer_order as co where coi.inventory_item_id = ii.id and coi.customer_order_id = co.id and co.posted = false and coi.credit = false)").executeUpdate();
            tx.commit();
            
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iiDao.getSession().createSQLQuery("update inventory_item as ii set ii.commited = 0 where ii.commited is null").executeUpdate();
            tx.commit();
            
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iiDao.getSession().createSQLQuery("update inventory_item as ii set ii.available = ii.onhand - ii.commited").executeUpdate();
            tx.commit();
            
            
        } catch (Throwable t){
            logger.error("Could not fixInventoryCounts", t);
        }
        logger.info("Finished fixInventoryCounts");
    }
    
    
}
