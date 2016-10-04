package com.bc.ejb;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bc.dao.BaseDao;
import com.bc.orm.*;
import com.bc.util.Money;
import com.bc.util.PropertyAuditLogger;
import java.util.*;
import org.apache.log4j.Priority;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.jboss.annotation.ejb.TransactionTimeout;

@Stateless
public class LifoSession implements LifoSessionLocal {

    public static final String LocalJNDIString = "inventory/"+LifoSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = LifoSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(LifoSession.class);
        
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(2400)
    public List<Long> postOrder(Long id, String username, Date postDate) throws Exception{
        List<Long> iiIds = new ArrayList<Long>();
        BaseDao<ReceivedItem> riDao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        BaseDao<CustomerOrderItem> coiDao = new BaseDao<CustomerOrderItem>(CustomerOrderItem.class);
        BaseDao<InventoryItem> iiDao = new BaseDao<InventoryItem>(InventoryItem.class);
        BaseDao<CustomerOrder> coDao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        
        logger.info("Getting order with items and inventory items");
        CustomerOrder order = coDao.findById(id, "customerOrderItems");
        logger.info("Have the order, total items: "+order.getCustomerOrderItems().size());
        Session session = riDao.getSession();
        int postcount = 0;
        long crittime = 0;
        for (CustomerOrderItem coi : order.getCustomerOrderItems()){
            
            if (coi.getPrice() == null){
                throw new Exception("Price is not set for item with ISBN: "+coi.getIsbn());
            }
            
            postcount++;
            if (postcount % 500 == 0){
                logger.info("finished with "+postcount+" crit time: "+(crittime / 1000.0));
                crittime = 0;
            }
            
            InventoryItem ii = coi.getInventoryItem(); 
            
            long start = System.currentTimeMillis();
            // get the received items that have available
            Criteria crit = session.createCriteria(ReceivedItem.class);
            crit.add(Restrictions.eq("isbn", coi.getIsbn()));
            crit.add(Restrictions.eq("cond", coi.getCond()));
            crit.add(Restrictions.gt("available", 0));
            crit.addOrder(Order.desc("date"));
            crit.setFetchSize(5);
            List<ReceivedItem> items = crit.list();
            crittime += System.currentTimeMillis()-start;
            //logger.info("single query time: "+(crittime / 1000.0));

            
            if (coi.getCredit()){
                if (items.size() > 0){
                    if (!coi.getCredit() || !coi.getCreditDamage()){
                        ReceivedItem ri = (ReceivedItem)items.get(0);
                        ri.setAvailable(ri.getAvailable()+coi.getQuantity());
                        //riDao.update(ri, ri.getReceived().getId());
                    }
                }
            } else {
                BigDecimal extended = new BigDecimal(0);
                int quantity = coi.getQuantity();
                int soFar = 0;
                int count = 0;
                int wanted = coi.getFilled();
                if (wanted > 0){
                    // try and only give what is wanted
                    quantity = wanted;
                }
                while (soFar < quantity && count < items.size()){
                    ReceivedItem ri = (ReceivedItem)items.get(count);
                    if (ri.getCost() == null){
                        throw new Exception("Cost is not set for received item with ISBN: "+ri.getIsbn()+" recived po: "+ri.getPoNumber());
                    }
                    coi.setVendorpo(ri.getReceived().getPoNumber());
                    if (ri.getBin() != null){
                        coi.setBin(ri.getBin());
                    }
                    if (quantity - soFar - ri.getAvailable() >= 0){
                        extended = extended.add(
                            new BigDecimal(ri.getCost()).multiply(
                                new BigDecimal(ri.getAvailable())));
                        soFar += ri.getAvailable();
                        ri.setAvailable(0);
                    } else {
                        int needed = quantity-soFar;
                        extended = extended.add(
                            new BigDecimal(ri.getCost()).multiply(
                                new BigDecimal(needed)));
                        soFar += ri.getAvailable();
                        ri.setAvailable(ri.getAvailable()-needed);
                    }
                    if (ri.getAvailable() < 0) {
                        ri.setAvailable(0);
                    }
                    //riDao.update(ri, ri.getReceived().getId());
                    count++;
                }
                // This takes care of the case where there is available but non in received
                if (ii != null && ii.getReceivedPrice() != null && soFar < quantity && ii.getOnhand()-soFar > 0){
                    if (coi.getVendorpo() == null) {
                        coi.setVendorpo(ii.getLastpo());
                    }
                    coi.setBin(ii.getBin());
                    if (quantity-soFar <= ii.getOnhand()-soFar){
                        extended = extended.add(
                            new BigDecimal(ii.getReceivedPrice()).multiply(
                                new BigDecimal(quantity).subtract(new BigDecimal(soFar))));
                        //extended += inv.getReceivedPrice()*(quantity-soFar);
                        soFar += quantity-soFar;
                    } else {
                        extended = extended.add(
                            new BigDecimal(ii.getReceivedPrice()).multiply(
                                new BigDecimal(ii.getOnhand()).subtract(new BigDecimal(soFar))));
                        //extended += inv.getReceivedPrice()*(inv.getOnhand()-soFar);
                        soFar += ii.getOnhand()-soFar;
                    }
                }
    
                // NOTE: we are trusting that the user enters the correct filled here!
                //if (soFar < quantity){
                    // we need more than we have available
                //    coi.setFilled(soFar);
                //} else {
                //    coi.setFilled(quantity);
                //}
                coi.setExtended(extended.floatValue());
                if (coi.getFilled() == 0){
                    coi.setCost(0F);
                } else {
                    BigDecimal bd = extended.divide(new Money(coi.getFilled()), BigDecimal.ROUND_HALF_UP);
                    bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                    coi.setCost(bd.floatValue());
                }
                if (ii != null && (coi.getCost() == null || coi.getCost() == 0)){
                    // check on cost of coi, in case no receiving was actually seen, we still set cost
                    coi.setCost(ii.getCost());
                }
                if (ii != null && coi.getVendorpo() == null && ii.getLastpo() != null){
                    coi.setVendorpo(ii.getLastpo());
                }
                
                //coiDao.update(coi, coi.getCustomerOrder().getId());
            }
    
            // update inventory item for the new quantity
            if (ii != null){
                // Credit types:
                //   1. Damage (Qty does not go back into inv, Price will be negative)
                //   2. Shortage (Qty added into inv, Price will be negative)
                //   3. Received But Not Billed (Qty reduced from inv, Price will be positive)
                boolean updateInv = false;
                if (order.getCreditMemo() != null && order.getCreditMemo() == true){
                    if (coi.getCredit() && coi.getCreditShortage()){
                        ii.setOnhand(ii.getOnhand()+coi.getQuantity());
                        updateInv = true;
                    } else if (!coi.getCreditDamage()){
                        ii.setOnhand(ii.getOnhand()-coi.getFilled());
                        updateInv = true;
                    }
                } else if (order.getDebitMemo() != null && order.getDebitMemo() == true){
                    if (order.getDebitMemoType().equals("recNoInv")){
                        ii.setOnhand(ii.getOnhand()-coi.getFilled());
                        updateInv = true;
                        logger.info("Updating inv when posting debit memo");
                    }
                } else{
                    ii.setOnhand(ii.getOnhand()-coi.getFilled());
                    updateInv = true;
                    logger.info("Updating inv when posting no credit or debit memo");
                }
                if (ii.getOnhand() < 0){
                    ii.setOnhand(0);
                    updateInv = true;
                }
                if (updateInv) {
                    iiDao.update(ii, null, false);
                    logger.info("Posting an order - updating inventory item");
                }
                iiIds.add(ii.getId());
            }
        }
        
        order.setPosted(true);
        order.setPostedByDate(Calendar.getInstance().getTime());
        order.setPostedBy(username);
        order.setPostDate(postDate);
        if (order.getShipDate() == null) {
            order.setShipDate(postDate);
        }
        
        logger.info("coDao update on co "+order.getId());
        coDao.update(order, null);
        
        return iiIds;
    }
    
    public List<Long> unpostOrder(Long id) throws Exception{
        List<Long> iiIds = new ArrayList<Long>();
        BaseDao<ReceivedItem> riDao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        BaseDao<CustomerOrder> coDao = new BaseDao<CustomerOrder>(CustomerOrder.class);
        BaseDao<InventoryItem> iiDao = new BaseDao<InventoryItem>(InventoryItem.class);
        
        logger.info("Getting order with items and inventory items");
        CustomerOrder order = coDao.findById(id, "customerOrderItems");
        logger.info("Have the order, total items: "+order.getCustomerOrderItems().size());
        Session session = riDao.getSession();
        int postcount = 0;
        long crittime = 0;
        for (CustomerOrderItem coi : order.getCustomerOrderItems()){
            
            if (coi.getPrice() == null){
                throw new Exception("Price is not set for item with ISBN: "+coi.getIsbn());
            }
            
            postcount++;
            if (postcount % 500 == 0){
                logger.info("finished with "+postcount+" crit time: "+(crittime / 1000.0));
                crittime = 0;
            }
            
            InventoryItem ii = coi.getInventoryItem(); 
            
            long start = System.currentTimeMillis();
            // get the received items that have available
            Criteria crit = session.createCriteria(ReceivedItem.class);
            crit.add(Restrictions.eq("isbn", coi.getIsbn()));
            crit.add(Restrictions.eq("cond", coi.getCond()));
            crit.add(Restrictions.gt("available", 0));
            crit.addOrder(Order.desc("date"));
            crit.setFetchSize(5);
            List<ReceivedItem> items = crit.list();
            crittime += System.currentTimeMillis()-start;
            //logger.info("single query time: "+(crittime / 1000.0));

            
            if (coi.getCredit()){
                if (items.size() > 0){
                    if (!coi.getCredit() || !coi.getCreditDamage()){
                        ReceivedItem ri = (ReceivedItem)items.get(0);
                        ri.setAvailable(ri.getAvailable()-coi.getQuantity()); // for credit, posting: +, unposting: -
                        //ri.setAvailable(ri.getAvailable()+coi.getQuantity()); 
                        //riDao.update(ri, ri.getReceived().getId());
                    }
                }
            }
    
            // update inventory item for the new quantity
            if (ii != null){
                // Credit types:
                //   1. Damage (Qty does not go back into inv, Price will be negative)
                //   2. Shortage (Qty added into inv, Price will be negative)
                //   3. Received But Not Billed (Qty reduced from inv, Price will be positive)
                boolean updateInv = false;
                if (order.getCreditMemo() != null && order.getCreditMemo() == true){
                    if (coi.getCredit() && coi.getCreditShortage()){
                        ii.setOnhand(ii.getOnhand()-coi.getQuantity());
                        //ii.setOnhand(ii.getOnhand()+coi.getQuantity());
                        updateInv = true;
                    } else if (!coi.getCreditDamage()){
                        ii.setOnhand(ii.getOnhand()+coi.getFilled());
    //                    ii.setOnhand(ii.getOnhand()-coi.getFilled());
                        updateInv = true;
                    }
                } else if (order.getCreditMemo() != null && order.getDebitMemo() == true){
                    if (order.getDebitMemoType().equals("recNoInv")){
                        ii.setOnhand(ii.getOnhand()+coi.getFilled());
                        updateInv = true;
                        logger.info("Updating inv when posting debit memo");
                    }
                } else {
                    ii.setOnhand(ii.getOnhand()+coi.getFilled());
                    updateInv = true;
                    logger.info("Updating inv when posting no credit or debit memo");
                }
                if (ii.getOnhand() < 0){
                    ii.setOnhand(0);
                    updateInv = true;
                }
                if (updateInv) {
                    iiDao.update(ii, null, false);
                    logger.info("Unposting an order - Inventory item updated");
                }
                iiIds.add(ii.getId());
            }
        }
        
        order.setPosted(false);
        order.setPostedByDate(null);
        order.setPostedBy(null);
        order.setPostDate(null);
        if (order.getShipDate() == null) {
            order.setShipDate(null);
        }
        
        logger.info("coDao update on co "+order.getId());
        coDao.update(order, null);
        
        return iiIds;
    }
    
    public List<Long> postBellOrder(Long id, String username, Date postDate) throws Exception {
        List<Long> iiIds = new ArrayList<Long>();
        BaseDao<BellReceivedItem> briDao = new BaseDao<BellReceivedItem>(BellReceivedItem.class);
        BaseDao<BellOrderItem> boiDao = new BaseDao<BellOrderItem>(BellOrderItem.class);
        BaseDao<BellInventory> biDao = new BaseDao<BellInventory>(BellInventory.class);
        BaseDao<BellOrder> boDao = new BaseDao<BellOrder>(BellOrder.class);
        
        BellOrder order = boDao.findById(id, "bellOrderItems");
        
        for (BellOrderItem boi : order.getBellOrderItems()){
            BellInventory bi = boi.getBellInventory(); 
            
            // get the received items that have available
            Criteria crit = briDao.getSession().createCriteria(BellReceivedItem.class);
            crit.add(Restrictions.eq("isbn", boi.getIsbn()));
            crit.add(Restrictions.gt("available", 0));
            crit.addOrder(Order.desc("date"));
            List<BellReceivedItem> items = crit.list();
            
            if (boi.getCredit() != null && boi.getCredit()){
                if (items.size() > 0){
                    if (!boi.getCredit()){
                        BellReceivedItem bri = (BellReceivedItem)items.get(0);
                        bri.setAvailable(bri.getAvailable()+boi.getQuantity());
                        briDao.update(bri, bri.getBellReceived().getId());
                    }
                }
            } else {
                BigDecimal extended = new BigDecimal(0);
                int quantity = boi.getQuantity();
                int soFar = 0;
                int count = 0;
                int wanted = boi.getFilled();
                if (wanted > 0){
                    // try and only give what is wanted
                    quantity = wanted;
                }
                while (soFar < quantity && count < items.size()){
                    BellReceivedItem bri = (BellReceivedItem)items.get(count);
                    if (bri.getCost() == null){
                        throw new Exception("Cost is not set for received item with ISBN: "+bri.getIsbn()+" recived po: "+bri.getPoNumber());
                    }
                    boi.setVendorpo(bri.getBellReceived().getPoNumber());
                    if (bri.getBin() != null){
                        boi.setBin(bri.getBin());
                    }
                    if (quantity - soFar - bri.getAvailable() >= 0){
                        extended = extended.add(
                            new BigDecimal(bri.getCost()).multiply(
                                new BigDecimal(bri.getAvailable())));
                        soFar += bri.getAvailable();
                        bri.setAvailable(0);
                    } else {
                        int needed = quantity-soFar;
                        extended = extended.add(
                            new BigDecimal(bri.getCost()).multiply(
                                new BigDecimal(needed)));
                        soFar += bri.getAvailable();
                        bri.setAvailable(bri.getAvailable()-needed);
                    }
                    briDao.update(bri, bri.getBellReceived().getId());
                    count++;
                }
                // This takes care of the case where there is available but non in received
                if (bi != null && soFar < quantity && bi.getOnhand()-soFar > 0){
                    boi.setBin(bi.getBin());
                    if (quantity-soFar <= bi.getOnhand()-soFar){
                        extended = extended.add(
                            new BigDecimal(bi.getReceivedPrice()).multiply(
                                new BigDecimal(quantity).subtract(new BigDecimal(soFar))));
                        //extended += inv.getReceivedPrice()*(quantity-soFar);
                        soFar += quantity-soFar;
                    } else {
                        extended = extended.add(
                            new BigDecimal(bi.getReceivedPrice()).multiply(
                                new BigDecimal(bi.getOnhand()).subtract(new BigDecimal(soFar))));
                        //extended += inv.getReceivedPrice()*(inv.getOnhand()-soFar);
                        soFar += bi.getOnhand()-soFar;
                    }
                }
    
                // NOTE: we are trusting that the user enters the correct filled here!
                //if (soFar < quantity){
                    // we need more than we have available
                //    coi.setFilled(soFar);
                //} else {
                //    coi.setFilled(quantity);
                //}
                boi.setExtended(extended.floatValue());
                if (boi.getFilled() == 0){
                    boi.setCost(0F);
                } else {
                    BigDecimal bd = extended.divide(new Money(boi.getFilled()), BigDecimal.ROUND_HALF_UP);
                    bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                    boi.setCost(bd.floatValue());
                }
                if (bi != null && (boi.getCost() == null || boi.getCost() == 0)){
                    // check on cost of coi, in case no receiving was actually seen, we still set cost
                    boi.setCost(bi.getReceivedPrice());
                }
                
                boiDao.update(boi, boi.getBellOrder().getId());
            }
    
            // update inventory item for the new quantity
            if (bi != null){
                // Credit types:
                //   1. Damage (Qty does not go back into inv, Price will be negative)
                //   2. Shortage (Qty added into inv, Price will be negative)
                //   3. Received But Not Billed (Qty reduced from inv, Price will be positive)
                boolean updateInv = false;
                if (boi.getCredit() != null && boi.getCredit()){
                    bi.setOnhand(bi.getOnhand()-boi.getFilled());
                    updateInv = true;
                } else {// if (!boi.getCreditDamage()){
                    bi.setOnhand(bi.getOnhand()-boi.getFilled());
                    updateInv = true;            
                }
                
                if (bi.getOnhand() < 0){
                    bi.setOnhand(0);
                    updateInv = true;
                }
                if (updateInv) {
                    //biDao.update(bi, null, false);
                }
                iiIds.add(bi.getId());
            }
        }
        
        order.setPosted(true);
        order.setPostDate(postDate);
        if (order.getShipDate() == null) {
            order.setShipDate(postDate);
        }
        
        
        boDao.update(order, null);
        
        return iiIds;        
    }
   
    @TransactionTimeout(1200)
    public void createReceivedItems(List<ReceivedItem> items, List<Long> ids){
        if (items == null || items.size() == 0) return;
        logger.info("Starting createReceivedItems with "+items.size()+" items..");
        
        Received rec = items.get(0).getReceived();
        if (rec.getHolding()) return;
        
        BaseDao<InventoryItem> iiDao = new BaseDao<InventoryItem>(InventoryItem.class);
        Criteria crit = iiDao.getSession().createCriteria(InventoryItem.class);
        crit.add(Restrictions.in("id", ids));
        List<InventoryItem> invItems = crit.list();
        Map<Long, InventoryItem> invMap = new HashMap<Long, InventoryItem>();
        for (InventoryItem ii : invItems){
            invMap.put(ii.getId(), ii);
        }
        
        for (int i = 0; i < items.size(); i++){
            ReceivedItem ri = items.get(i);
            InventoryItem ii = invMap.get(ri.getInventoryItem().getId());
            updateInventoryItemForReceivedItem(ii, rec.getPoDate(), ri.getQuantity(), ri.getCost(), rec.getPoNumber(), ri, true);
        }
        logger.info("Finished createReceivedItems with "+items.size()+" items.");
    }
    
    public void createReceivedItem(ReceivedItem item, Long inventoryItemId){
        createReceivedItem(item, inventoryItemId, null);
    }
    
    public void createReceivedItem(ReceivedItem item, Long inventoryItemId, BaseDao<ReceivedItem> riDao){
        createReceivedItem(item, inventoryItemId, riDao, true);
    }
    
    public void createReceivedItem(ReceivedItem item, Long inventoryItemId, BaseDao<ReceivedItem> riDao, boolean updateBins){
        if (riDao == null){
            riDao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        }
        
        ReceivedItem ritem = riDao.findById(item.getId(), "received", "inventoryItem");
        if (ritem == null){
            logger.error("There was no received item for createReceivedItem "+item.getId());
            return;
        }
        if (ritem.getReceived().getHolding()) return;
        
        Criteria crit = riDao.getSession().createCriteria(ReceivedItem.class);
        crit.add(Restrictions.eq("received", item.getReceived()));
        crit.add(Restrictions.eq("breakroom", true));
        crit.setFetchMode("inventoryItem", FetchMode.JOIN);
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<ReceivedItem> items = crit.list();
        for (ReceivedItem ri : items){
            ri.setQuantity(ri.getQuantity()-item.getQuantity());
            riDao.update(ri, item.getReceived().getId());
            InventoryItem iinv = ri.getInventoryItem();
            if (iinv != null){
                iinv.setOnhand(iinv.getOnhand()-item.getQuantity());
                if (iinv.getOnhand() < 0){
                    iinv.setOnhand(0);
                }
                iinv.setAvailable(iinv.getOnhand()-iinv.getCommitted());
            }
        }
        updateInventoryItemForReceivedItem(ritem.getInventoryItem(), item.getReceived().getPoDate(), item.getQuantity(), item.getCost(), item.getReceived().getPoNumber(), item, updateBins);
    }
    
    private void updateInventoryItemForReceivedItem(InventoryItem ii, Date poDate, Integer quantity, Float cost, String poNumber, ReceivedItem item, Boolean updateBins){
        // update the inventory onhand and available
        if (ii == null) return;
        int givenToBackorder = 0;
        if (ii.getBackorder() != null && ii.getBackorder() > 0){
            // we have a backorder on this item
            givenToBackorder = ii.getBackorder() - quantity;
            if (givenToBackorder < 0){
                givenToBackorder = ii.getBackorder();
            }
            ii.setBackorder(ii.getBackorder()-givenToBackorder);
            //item.setBackordered(item.getBackordered()-givenToBackorder);
        }
        if (ii.getOnhand() == null){
            ii.setOnhand(quantity-givenToBackorder);
        } else {
            ii.setOnhand(ii.getOnhand()+quantity-givenToBackorder);
        }
        if (ii.getOnhand() < 0){
            ii.setOnhand(0);
        }
        if (ii.getCommitted() == null) ii.setCommitted(0);
        ii.setAvailable(ii.getOnhand()-ii.getCommitted());
        ii.setReceivedPrice(cost);

        if (item.getBin() != null) ii.setBin(item.getBin());
        if (ii.getBin() == null) ii.setBin("");
        if (item.getSellPrice() != null) ii.setSellingPrice(item.getSellPrice());
        if (item.getCoverType() != null) ii.setCover(item.getCoverType());

        if (item.getSkidPieceCost() != null) ii.setSkidPieceCost(item.getSkidCost());
        if (item.getSkidPiecePrice() != null) ii.setSkidPiecePrice(item.getSkidPiecePrice());
        
        if (updateBins)
            updatePendingOrderBins(ii.getId(), ii.getBin(), ii.getTitle());
        
    }
    
    public void updatePendingOrderBins(Long iiId, String bin, String title) {
        BaseDao<InventoryItem> iiDao = new BaseDao<InventoryItem>(InventoryItem.class);
        StringBuilder sql = new StringBuilder();
        sql.append("update customer_order_item as coi, customer_order as co set ");
        sql.append("coi.bin = '");
        sql.append(bin);
        sql.append("'");
        if (title != null && title.length() > 0){
            sql.append(", coi.title = ?");
            //sql.append(title.replaceAll("'", "''"));
            //sql.append("'");
        }
        sql.append(" where coi.inventory_item_id = ");
        sql.append(iiId);
        sql.append(" and (co.posted = false or co.posted is null) and co.id = coi.customer_order_id");
        if (title != null && title.length() > 0)
            iiDao.getSession().createSQLQuery(sql.toString()).setParameter(0, title).executeUpdate();
        else
            iiDao.getSession().createSQLQuery(sql.toString()).executeUpdate();
    }
    
    @TransactionTimeout(1200)
    public void deleteReceivedItems(List<ReceivedItem> items){
        if (items == null || items.size() == 0) return;
        Received rec = items.get(0).getReceived();
        if (rec.getHolding()) return;
        boolean hasBreakroom = false;
        BaseDao<ReceivedItem> riDao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        Criteria crit = riDao.getSession().createCriteria(ReceivedItem.class);
        crit.add(Restrictions.eq("received", rec));
        crit.add(Restrictions.eq("breakroom", true));
        crit.setProjection(Projections.rowCount());        
        hasBreakroom = (Integer)crit.uniqueResult() > 0;
        
        if (hasBreakroom){
            for (ReceivedItem item : items){
                for (ReceivedItem ri : items){
                    ri.setQuantity(ri.getQuantity()+item.getQuantity());
                    riDao.update(ri, ri.getReceived().getId());
                    InventoryItem ii = ri.getInventoryItem();
                    if (ii != null){
                        ii.setOnhand(ii.getOnhand()-item.getQuantity());
                        if (ii.getOnhand() < 0){
                            ii.setOnhand(0);
                        }
                        ii.setAvailable(ii.getOnhand()-ii.getCommitted());
                    }
                }
            }
        }
        
        for (ReceivedItem ri : items){
            if (ri.getBreakroom() == null || !ri.getBreakroom()){
                updateInvForDeletedRecItem(ri.getInventoryItem(), ri, riDao);
            }
        }        
    }
    
    private void updateInvForDeletedRecItem(InventoryItem ii, ReceivedItem ritem, BaseDao<ReceivedItem> riDao){
        if (ii == null) return;
        // update the inventory
        boolean orderPostInBetween = false;
        Integer availableDiff = null;
        if (ritem.getAvailable() != null && !ritem.getQuantity().equals(ritem.getAvailable())){
            orderPostInBetween = true;
            availableDiff = ritem.getQuantity() - ritem.getAvailable();
        }
        if (ii.getOnhand() != null){
            ii.setOnhand(ii.getOnhand()-ritem.getQuantity());
            if (ii.getOnhand() < 0){
                ii.setOnhand(0);
            }
        } else {
            ii.setOnhand(0);
        }
        if (ii.getOnhand() < 0){
            ii.setOnhand(0);
        }
        ii.setAvailable(ii.getOnhand()-ii.getCommitted());

        Criteria prevcrit = riDao.getSession().createCriteria(ReceivedItem.class, "received");
        prevcrit.createAlias("received", "rec");
        prevcrit.add(Restrictions.eq("inventoryItem", ritem.getInventoryItem()));
        prevcrit.add(Restrictions.eq("rec.holding", Boolean.FALSE));
        prevcrit.addOrder(Order.desc("rec.poDate"));
        prevcrit.setFetchSize(2);
        prevcrit.setMaxResults(2);
        List<ReceivedItem> previtems = prevcrit.list();
        if (previtems != null && previtems.size() > 1) {
            ReceivedItem prevRecItem = previtems.get(1);
            ii.setReceivedPrice(prevRecItem.getCost());
            /* 
             * Request from John J to NOT do this any more
             * 
            //ii.setBin(prevRecItem.getBin());
             */
            ii.setSellingPrice(prevRecItem.getSellPrice());
        } else {
            ii.setReceivedPrice(null);
        }

        if (orderPostInBetween){
            logger.info("Have a post since this item was created, must update previous availables...");
            // we have to reduce other receiving items available
            prevcrit = riDao.getSession().createCriteria(ReceivedItem.class, "received");
            prevcrit.createAlias("received", "rec");
            prevcrit.add(Restrictions.eq("inventoryItem", ritem.getInventoryItem()));
            prevcrit.add(Restrictions.eq("rec.holding", Boolean.FALSE));
            prevcrit.addOrder(Order.desc("date"));
            previtems = prevcrit.list();
            if (previtems != null){
                previtems.remove(0); // we do not want to look at the current item
                for (ReceivedItem ri : previtems){
                    if (ri.getAvailable() != null && ri.getAvailable() > 0){
                        if (ri.getAvailable() - availableDiff >= 0){
                            ri.setAvailable(ri.getAvailable() - availableDiff);
                            availableDiff = 0;
                        } else {
                            availableDiff = availableDiff - ri.getAvailable();
                            ri.setAvailable(0);
                        }
                    }
                    if (availableDiff <= 0) {
                        break;
                    }
                }
            }
        }
    }
    
    
    public void deleteReceivedItem(ReceivedItem item){
        BaseDao<ReceivedItem> riDao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        
        ReceivedItem ritem = riDao.findById(item.getId(), "received", "inventoryItem");
        if (ritem == null){
            logger.error("There was no received item for createReceivedItem "+item.getId());
            return;
        }
        if (ritem.getReceived().getHolding()) return;
        Criteria crit = riDao.getSession().createCriteria(ReceivedItem.class);
        crit.add(Restrictions.eq("received", ritem.getReceived()));
        crit.add(Restrictions.eq("breakroom", true));
        crit.setFetchMode("inventoryItem", FetchMode.JOIN);
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<ReceivedItem> items = crit.list();
        for (ReceivedItem ri : items){
            ri.setQuantity(ri.getQuantity()+item.getQuantity());
            riDao.update(ri, ri.getReceived().getId());
            InventoryItem ii = ri.getInventoryItem();
            if (ii != null){
                ii.setOnhand(ii.getOnhand()-item.getQuantity());
                if (ii.getOnhand() < 0){
                    ii.setOnhand(0);
                }
                ii.setAvailable(ii.getOnhand()-ii.getCommitted());
            }
        }

        if (item.getBreakroom() == null || !item.getBreakroom()){
            updateInvForDeletedRecItem(ritem.getInventoryItem(), ritem, riDao);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @TransactionTimeout(1200)
    public void updateReceivedItems(List<ReceivedItem> items){
        for (ReceivedItem ri : items){
            updateReceivedItem(ri);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateReceivedItem(ReceivedItem item){
        BaseDao<ReceivedItem> riDao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        
        ReceivedItem ritem = riDao.findById(item.getId(), "received", "inventoryItem");
        if (ritem == null){
            logger.error("There was no received item for updateReceivedItem "+item.getId());
            return;
        }
        if (ritem.getReceived().getHolding()) return;
        if (item.getBreakroom() != null && !item.getBreakroom()){
            Criteria crit = riDao.getSession().createCriteria(ReceivedItem.class);
            crit.add(Restrictions.eq("received", ritem.getReceived()));
            crit.add(Restrictions.eq("breakroom", true));
            List<ReceivedItem> items = crit.list();
            for (ReceivedItem ri : items){
                ri.setQuantity(ri.getQuantity()+item.getPreQuantity());
                ri.setQuantity(ri.getQuantity()-item.getQuantity());
                riDao.update(ri, ri.getReceived().getId());
                InventoryItem ii = ri.getInventoryItem();
                if (ii != null){
                    ii.setOnhand(ii.getOnhand()+item.getPreQuantity());
                    ii.setOnhand(ii.getOnhand()-item.getQuantity());
                    if (ii.getOnhand() < 0){
                        ii.setOnhand(0);
                    }
                    ii.setAvailable(ii.getOnhand()-ii.getCommitted());
                }
            }
        }

        InventoryItem ii = ritem.getInventoryItem();
        if (ii != null){
        // update the inventory onhand and available
            
            InventoryItem clonedItem = null;
            try {
                clonedItem = (InventoryItem)ii.clone();
            } catch (Exception e){}
            
            if (ii.getOnhand() != null){
                ii.setOnhand(ii.getOnhand()-item.getPreQuantity());
                ii.setOnhand(ii.getOnhand()+item.getQuantity());
            } else {
                ii.setOnhand(item.getQuantity());
            }
            if (ii.getOnhand() == null || ii.getOnhand() < 0){
                ii.setOnhand(0);
            }
            if (ii.getCommitted() == null) ii.setCommitted(0);
            ii.setAvailable(ii.getOnhand()-ii.getCommitted());
            
            Date dcheck = Calendar.getInstance().getTime();
            if (ritem.getReceived().getPoDate() != null) dcheck = ritem.getReceived().getPoDate();
            else if (ritem.getDate() != null) dcheck = ritem.getDate();
            
            if (ii.getReceivedDate() == null || ii.getReceivedDate().before(dcheck) || ii.getReceivedDate().equals(dcheck)){
                ii.setReceivedPrice(item.getCost());
                // making sure we update bin, sell price, and cover
                if (item.getBin() != null && item.isUpdated("bin")) ii.setBin(item.getBin());
                if (item.getSellPrice() != null && item.isUpdated("sellPrice")) ii.setSellingPrice(item.getSellPrice());
                if (item.getCoverType() != null && item.isUpdated("coverType")) ii.setCover(item.getCoverType());

                if (item.getSkidPieceCost() != null && item.isUpdated("skidPieceCost")) ii.setSkidPieceCost(item.getSkidCost());
                if (item.getSkidPiecePrice() != null && item.isUpdated("skidPiecePrice")) ii.setSkidPiecePrice(item.getSkidPiecePrice());

                updatePendingOrderBins(ii.getId(), ii.getBin(), ii.getTitle());
            }
            
            if (clonedItem != null) PropertyAuditLogger.logDiff(clonedItem, ii, null);
            
        } else {
            logger.error("No inventory item found for received item id: "+ritem.getId()+" isbn: "+ritem.getIsbn()+" cond: "+ritem.getCond());
        }
    }

    public void createBellReceivedItems(List<BellReceivedItem> items, List<Long> ids){
        if (items == null || items.size() == 0) return;
        logger.info("Starting createReceivedItems with "+items.size()+" items..");
        
        BaseDao<BellInventory> iiDao = new BaseDao<BellInventory>(BellInventory.class);
        Criteria crit = iiDao.getSession().createCriteria(BellInventory.class);
        crit.add(Restrictions.in("id", ids));
        List<BellInventory> invItems = crit.list();
        Map<Long, BellInventory> invMap = new HashMap<Long, BellInventory>();
        for (BellInventory ii : invItems){
            invMap.put(ii.getId(), ii);
        }
        
        BellReceived rec = items.get(0).getBellReceived();
        for (int i = 0; i < items.size(); i++){
            BellReceivedItem ri = items.get(i);
            BellInventory ii = invMap.get(ri.getBellInventory().getId());
            updateInventoryItemForBellReceivedItem(ii, rec.getPoDate(), ri.getQuantity(), ri.getCost(), rec.getPoNumber());
        }
        logger.info("Finished createReceivedItems with "+items.size()+" items.");
    }
    
    public void createBellReceivedItem(BellReceivedItem item, Long inventoryItemId){
        createBellReceivedItem(item, inventoryItemId, null);
    }
    
    public void createBellReceivedItem(BellReceivedItem item, Long inventoryItemId, BaseDao<BellReceivedItem> riDao){
        if (riDao == null){
            riDao = new BaseDao<BellReceivedItem>(BellReceivedItem.class);
        }
        
        BellReceivedItem ritem = riDao.findById(item.getId(), "bellReceived", "bellInventory");
        if (ritem == null){
            logger.error("There was no received item for createReceivedItem "+item.getId());
            return;
        }
        
        Criteria crit = riDao.getSession().createCriteria(BellReceivedItem.class);
        crit.add(Restrictions.eq("bellReceived", item.getBellReceived()));
        crit.add(Restrictions.eq("breakroom", true));
        crit.setFetchMode("inventoryItem", FetchMode.JOIN);
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<BellReceivedItem> items = crit.list();
        for (BellReceivedItem ri : items){
            ri.setQuantity(ri.getQuantity()-item.getQuantity());
            riDao.update(ri, item.getBellReceived().getId());
            BellInventory iinv = ri.getBellInventory();
            if (iinv != null){
                iinv.setOnhand(iinv.getOnhand()-item.getQuantity());
                if (iinv.getOnhand() < 0){
                    iinv.setOnhand(0);
                }
                iinv.setAvailable(iinv.getOnhand()-iinv.getCommitted());
            }
        }
        updateInventoryItemForBellReceivedItem(item.getBellInventory(), item.getBellReceived().getPoDate(), item.getQuantity(), item.getCost(), item.getBellReceived().getPoNumber());
    }
    
    private void updateInventoryItemForBellReceivedItem(BellInventory ii, Date poDate, Integer quantity, Float cost, String poNumber){
        // update the inventory onhand and available
        if (ii == null) return;
        if (ii.getOnhand() == null){
            ii.setOnhand(quantity);
        } else {
            ii.setOnhand(ii.getOnhand()+quantity);
        }
        if (ii.getOnhand() == null || ii.getOnhand() < 0){
            ii.setOnhand(0);
        }
        if (ii.getCommitted() == null) ii.setCommitted(0);
        ii.setAvailable(ii.getOnhand()-ii.getCommitted());
        /* TODO should we add these
        ii.setLastpo(poNumber);
        ii.setReceivedQuantity(quantity);
        ii.setReceivedDate(poDate);
        ii.setLastpoDate(poDate);
        */
        ii.setReceivedPrice(cost);
    }
    
    public void deleteBellReceivedItems(List<BellReceivedItem> items){
        if (items == null || items.size() == 0) return;
        BellReceived rec = items.get(0).getBellReceived();
        boolean hasBreakroom = false;
        BaseDao<BellReceivedItem> riDao = new BaseDao<BellReceivedItem>(BellReceivedItem.class);
        Criteria crit = riDao.getSession().createCriteria(BellReceivedItem.class);
        crit.add(Restrictions.eq("bellReceived", rec));
        crit.add(Restrictions.eq("breakroom", true));
        crit.setProjection(Projections.rowCount());        
        hasBreakroom = (Integer)crit.uniqueResult() > 0;
        
        if (hasBreakroom){
            for (BellReceivedItem item : items){
                for (BellReceivedItem ri : items){
                    ri.setQuantity(ri.getQuantity()+item.getQuantity());
                    riDao.update(ri, ri.getBellReceived().getId());
                    BellInventory ii = ri.getBellInventory();
                    if (ii != null){
                        ii.setOnhand(ii.getOnhand()-item.getQuantity());
                        if (ii.getOnhand() < 0){
                            ii.setOnhand(0);
                        }
                        ii.setAvailable(ii.getOnhand()-ii.getCommitted());
                    }
                }
            }
        }
        
        for (BellReceivedItem ri : items){
            if (ri.getBreakroom() == null || !ri.getBreakroom()){
                BellInventory ii = ri.getBellInventory();
                if (ii != null){
                    // update the inventory
                    if (ii.getOnhand() != null){
                        ii.setOnhand(ii.getOnhand()-ri.getQuantity());
                        if (ii.getOnhand() < 0){
                            ii.setOnhand(0);
                        }
                    } else {
                        ii.setOnhand(0);
                    }
                    if (ii.getOnhand() < 0){
                        ii.setOnhand(0);
                    }
                    ii.setAvailable(ii.getOnhand()-ii.getCommitted());
                }
            }
        }        
    }
    
    
    public void deleteBellReceivedItem(BellReceivedItem item){
        BaseDao<BellReceivedItem> riDao = new BaseDao<BellReceivedItem>(BellReceivedItem.class);
        
        BellReceivedItem ritem = riDao.findById(item.getId(), "bellReceived", "bellInventory");
        if (ritem == null){
            logger.error("There was no received item for createReceivedItem "+item.getId());
            return;
        }
        Criteria crit = riDao.getSession().createCriteria(BellReceivedItem.class);
        crit.add(Restrictions.eq("bellReceived", ritem.getBellReceived()));
        crit.add(Restrictions.eq("breakroom", true));
        crit.setFetchMode("bellInventory", FetchMode.JOIN);
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<BellReceivedItem> items = crit.list();
        for (BellReceivedItem ri : items){
            ri.setQuantity(ri.getQuantity()+item.getQuantity());
            riDao.update(ri, ri.getBellReceived().getId());
            BellInventory ii = ri.getBellInventory();
            if (ii != null){
                ii.setOnhand(ii.getOnhand()-item.getQuantity());
                if (ii.getOnhand() < 0){
                    ii.setOnhand(0);
                }
                ii.setAvailable(ii.getOnhand()-ii.getCommitted());
            }
        }

        
        if (item.getBreakroom() == null || !item.getBreakroom()){
            BellInventory ii = ritem.getBellInventory();
            if (ii != null){
                // update the inventory
                if (ii.getOnhand() != null){
                    ii.setOnhand(ii.getOnhand()-item.getQuantity());
                    if (ii.getOnhand() < 0){
                        ii.setOnhand(0);
                    }
                } else {
                    ii.setOnhand(0);
                }
                if (ii.getOnhand() < 0){
                    ii.setOnhand(0);
                }
                ii.setAvailable(ii.getOnhand()-ii.getCommitted());
            }
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateBellReceivedItems(List<BellReceivedItem> items){
        for (BellReceivedItem ri : items){
            updateBellReceivedItem(ri);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateBellReceivedItem(BellReceivedItem item){
        BaseDao<BellReceivedItem> riDao = new BaseDao<BellReceivedItem>(BellReceivedItem.class);
        
        BellReceivedItem ritem = riDao.findById(item.getId(), "bellReceived", "bellInventory");
        if (ritem == null){
            logger.error("There was no received item for updateReceivedItem "+item.getId());
            return;
        }
        if (item.getBreakroom() != null && !item.getBreakroom()){
            Criteria crit = riDao.getSession().createCriteria(BellReceivedItem.class);
            crit.add(Restrictions.eq("bellReceived", ritem.getBellReceived()));
            crit.add(Restrictions.eq("breakroom", true));
            List<BellReceivedItem> items = crit.list();
            for (BellReceivedItem ri : items){
                ri.setQuantity(ri.getQuantity()+item.getPreQuantity());
                ri.setQuantity(ri.getQuantity()-item.getQuantity());
                riDao.update(ri, ri.getBellReceived().getId());
                BellInventory ii = ri.getBellInventory();
                if (ii != null){
                    ii.setOnhand(ii.getOnhand()+item.getPreQuantity());
                    ii.setOnhand(ii.getOnhand()-item.getQuantity());
                    if (ii.getOnhand() < 0){
                        ii.setOnhand(0);
                    }
                    ii.setAvailable(ii.getOnhand()-ii.getCommitted());
                }
            }
        }

        BellInventory ii = ritem.getBellInventory();
        if (ii != null){
        // update the inventory onhand and available
            if (ii.getOnhand() != null){
                ii.setOnhand(ii.getOnhand()-item.getPreQuantity());
                ii.setOnhand(ii.getOnhand()+item.getQuantity());
            } else {
                ii.setOnhand(item.getQuantity());
            }
            if (ii.getOnhand() == null || ii.getOnhand() < 0){
                ii.setOnhand(0);
            }
            if (ii.getCommitted() == null) ii.setCommitted(0);
            ii.setAvailable(ii.getOnhand()-ii.getCommitted());
            // TODO if we add these
            //ii.setReceivedDate(ritem.getBellReceived().getPoDate());
            //ii.setReceivedQuantity(item.getQuantity());
            ii.setReceivedPrice(item.getCost());
        }
    }
    
}



/*
 * 
 * public class LifoUpdate {

    private static final Logger logger =
        Logger.getLogger("com.bc.util.LifoUpdate");

    public static final int DELETE_ORDERITEM = 0;
    public static final int UPDATE_ORDERITEM = 1;
    public static final int CREATE_ORDERITEM = 2;
    public static final int DELETE_RECEIVEDITEM = 3;
    public static final int UPDATE_RECEIVEDITEM = 4;
    public static final int CREATE_RECEIVEDITEM = 5;
    public static final int POST_ORDERITEM = 6;
    public static final int DELETE_BELLRECEIVEDITEM = 7;
    public static final int UPDATE_BELLRECEIVEDITEM = 8;
    public static final int CREATE_BELLRECEIVEDITEM = 9;
    public static final int DELETE_BELLORDERITEM = 10;
    public static final int UPDATE_BELLORDERITEM = 11;
    public static final int CREATE_BELLORDERITEM = 12;
    public static final int POST_BELLORDERITEM = 13;

    private static InventoryManagerSessionBeanLocal inventoryManager;
    private static ReceivingManagerSessionBeanLocal receivingManager;
    private static OrderManagerSessionBeanLocal orderManager;
    private static BellwetherManagerSessionBeanLocal bellManager;

    static {
        inventoryManager = SessionHelper.getInventoryManager();
        receivingManager = SessionHelper.getReceivingManager();
        bellManager = SessionHelper.getBellwetherManager();
    }

// any updateInventoryItem just has to do a recalc committed

    private static void deleteOrderItem(CustomerOrderItem coi, String name){
        inventoryManager.updateInventoryItem(inventoryManager.findByISBNCondition(coi.getIsbn(), coi.getCondition(), false), name);
    }

    private static void deleteBellOrderItem(BellOrderItem item, String name){
        bellManager.updateInventory(bellManager.findInventoryByIsbn(item.getIsbn()));
    }

    private static void updateOrderItem(CustomerOrderItem coi, String name){
        inventoryManager.updateInventoryItem(inventoryManager.findByISBNCondition(coi.getIsbn(), coi.getCondition(), false), name);
    }

    private static void updateBellOrderItem(BellOrderItem item, String name){
        bellManager.updateInventory(bellManager.findInventoryByIsbn(item.getIsbn()));
    }

    private static void createOrderItem(CustomerOrderItem coi, String name){
        inventoryManager.updateInventoryItem(inventoryManager.findByISBNCondition(coi.getIsbn(), coi.getCondition(), false), name);
    }

    private static void createBellOrderItem(BellOrderItem item, String name){
        bellManager.updateInventory(bellManager.findInventoryByIsbn(item.getIsbn()));
    }

// NOT USED
     * Update all pending orders with this isbn
     * Basically go through all of the orders, give back the quantity to the available
     * on the received and then go through and take back what you need for the order item
     *
     * @param isbn
    private static void updateAllPendingOrders(String isbn){
        // Get the data we need to make this happen
        ArrayList orderItems = orderManager.getAllPendingOrderItems(isbn);
        // NOTE - this will only give back the last 200 received's of this item
        ArrayList recItems = receivingManager.getAllReceivedItems(isbn, 200);

        // Step 1: give back
        CustomerOrderItem coi = null;
        int receivedItemChanges = 0;
        int count = 0;
        int quantity, giveBack;
        for (int i = 0; i < orderItems.size(); i++){
            coi = (CustomerOrderItem)orderItems.get(i);
            giveBack = coi.getFilled();
            coi.setFilled(0);

            // NOTE: This is really the deleteOrderItem part
            count = 0;
            quantity = giveBack;
            while (quantity > 0 && count < recItems.size()){
                ReceivedItem ri = (ReceivedItem)recItems.get(count);
                if (ri.getAvailable()+quantity <= ri.getQuantity()) {
                    // just add it onto the available
                    ri.setAvailable(ri.getAvailable()+quantity);
                    quantity = 0;
                } else {
                    quantity -= ri.getQuantity()-ri.getAvailable();
                    ri.setAvailable(ri.getQuantity());
                }
                count++;
            }
        }


        // Step 2: take what you need
        int soFar = 0;
        BigDecimal cost = new BigDecimal(0);
        for (int i = 0; i < orderItems.size(); i++){
            coi = (CustomerOrderItem)orderItems.get(i);
            quantity = coi.getQuantity();
            count = 0;
            cost = new BigDecimal(0);
            // NOTE: this is really the createOrderItem part
            while (soFar < quantity && count < recItems.size()){
                ReceivedItem ri = (ReceivedItem)recItems.get(count);
                if (quantity - soFar - ri.getAvailable() >= 0){
                    cost = cost.add(
                        new BigDecimal(ri.getCost()).multiply(
                            new BigDecimal(ri.getAvailable())));
                    soFar += ri.getAvailable();
                    ri.setAvailable(0);
                } else {
                    int needed = quantity-soFar;
                    cost = cost.add(
                        new BigDecimal(ri.getCost()).multiply(
                            new BigDecimal(needed)));
                    soFar += ri.getAvailable();
                    ri.setAvailable(ri.getAvailable()-needed);
                }
                receivingManager.updateReceivedItem(ri);
                count++;
            }
            if (soFar < quantity){
                // we need more than we have available
                coi.setFilled(soFar);
            } else {
                coi.setFilled(quantity);
            }
            coi.setCost(cost.floatValue());
            orderManager.updateOrderItem(coi);
        }
    }


    private static void postOrderItem(CustomerOrderItem coi, String name){
        Date now = Calendar.getInstance().getTime();
        BigDecimal extended = new BigDecimal(0);
        extended.setScale(2, BigDecimal.ROUND_HALF_UP);
        ArrayList recItems = new ArrayList(receivingManager.
            findReceivedItemsByIsbn(new PagingSortingBean(), coi.getIsbn(), coi.getCondition(), true));
        InventoryItem inv = inventoryManager.findByISBNCondition(coi.getIsbn(), coi.getCondition(), false);
        if (coi.getCredit()){
            if (recItems.size() > 0){
                ReceivedItem ri = (ReceivedItem)recItems.get(0);
                ri.setAvailable(ri.getAvailable()+coi.getQuantity());
                receivingManager.updateReceivedItem(ri);
            }
        } else {
            int quantity = coi.getQuantity();
            int soFar = 0;
            int count = 0;
            int wanted = coi.getFilled();
            if (wanted > 0){
                // try and only give what is wanted
                quantity = wanted;
                //coi.setIgnoreBackorder(true);
            }
            while (soFar < quantity && count < recItems.size()){
                ReceivedItem ri = (ReceivedItem)recItems.get(count);
                ri.setLastUpdate(now);
                ri.setLastUpdateBy(name);
                coi.setVendorPo(ri.getReceived().getPoNumber());
                if (ri.getBin() != null){
                    coi.setBin(ri.getBin());
                }
                if (quantity - soFar - ri.getAvailable() >= 0){
                    extended = extended.add(
                        new BigDecimal(ri.getCost()).multiply(
                            new BigDecimal(ri.getAvailable())));
                    soFar += ri.getAvailable();
                    ri.setAvailable(0);
                } else {
                    int needed = quantity-soFar;
                    extended = extended.add(
                        new BigDecimal(ri.getCost()).multiply(
                            new BigDecimal(needed)));
                    soFar += ri.getAvailable();
                    ri.setAvailable(ri.getAvailable()-needed);
                }
                receivingManager.updateReceivedItem(ri);
                count++;
            }
            // This takes care of the case where there is available but non in received
            if (inv != null && soFar < quantity && inv.getOnhand()-soFar > 0){
                coi.setBin(inv.getBin());
                if (quantity-soFar <= inv.getOnhand()-soFar){
                    extended = extended.add(
                        new BigDecimal(inv.getReceivedPrice()).multiply(
                            new BigDecimal(quantity).subtract(new BigDecimal(soFar))));
                    //extended += inv.getReceivedPrice()*(quantity-soFar);
                    soFar += quantity-soFar;
                } else {
                    extended = extended.add(
                        new BigDecimal(inv.getReceivedPrice()).multiply(
                            new BigDecimal(inv.getOnhand()).subtract(new BigDecimal(soFar))));
                    //extended += inv.getReceivedPrice()*(inv.getOnhand()-soFar);
                    soFar += inv.getOnhand()-soFar;
                }
            }

            // NOTE: we are trusting that the user enters the correct filled here!
            //if (soFar < quantity){
                // we need more than we have available
            //    coi.setFilled(soFar);
            //} else {
            //    coi.setFilled(quantity);
            //}
            coi.setExtended(extended.floatValue());
            if (coi.getFilled() == 0){
                coi.setCost(0F);
            } else {
                BigDecimal bd = extended.divide(new Money(coi.getFilled()), BigDecimal.ROUND_HALF_UP);
                bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                coi.setCost(bd.floatValue());
            }
        }

        // update inventory item for the new quantity
        if (inv != null){
            // check on cost of coi, in case no receiving was actually seen, we still set cost
            if (coi.getCost() == null || coi.getCost() == 0){
                coi.setCost(inv.getReceivedPrice());
            }
            // Credit types:
            //   1. Damage (Qty does not go back into inv, Price will be negative)
            //   2. Shortage (Qty added into inv, Price will be negative)
            //   3. Received But Not Billed (Qty reduced from inv, Price will be positive)
            if (coi.getCredit() && coi.getCreditShortage()){
                inv.setOnhand(inv.getOnhand()+coi.getQuantity());
            } else if (!coi.getCreditDamage()){
                inv.setOnhand(inv.getOnhand()-coi.getFilled());
            }
            if (inv.getOnhand() < 0){
                inv.setOnhand(0);
            }
            if (!inventoryManager.updateInventoryItem(inv, name)){
                logger.error("Could not update inventory item with isbn: "+inv.getIsbn());
            }
        }
    }

    private static void postBellOrderItem(BellOrderItem boi, String name){
        Date now = Calendar.getInstance().getTime();
        BigDecimal extended = new BigDecimal(0);
        extended.setScale(2, BigDecimal.ROUND_HALF_UP);
        ArrayList recItems = new ArrayList(bellManager.findBellReceivedItemsByIsbn(boi.getIsbn()));
        BellInventory inv = bellManager.findInventoryByIsbn(boi.getIsbn());
        if (boi.getCredit()){
            if (recItems.size() > 0){
                BellReceivedItem ri = (BellReceivedItem)recItems.get(0);
                ri.setAvailable(ri.getAvailable()+boi.getQuantity());
                bellManager.updateReceivedItem(ri);
            }
        } else {
            int quantity = boi.getQuantity();
            int soFar = 0;
            int count = 0;
            int wanted = boi.getFilled();
            if (wanted > 0){
                // try and only give what is wanted
                quantity = wanted;
                //coi.setIgnoreBackorder(true);
            }
            while (soFar < quantity && count < recItems.size()){
                BellReceivedItem ri = (BellReceivedItem)recItems.get(count);
                ri.setLastUpdate(now);
                ri.setLastUpdateBy(name);
                boi.setVendorPo(ri.getReceived().getPoNumber());
                if (ri.getBin() != null){
                    boi.setBin(ri.getBin());
                }
                if (quantity - soFar - ri.getAvailable() >= 0){
                    extended = extended.add(
                        new BigDecimal(ri.getCost()).multiply(
                            new BigDecimal(ri.getAvailable())));
                    //extended += ri.getCost()*ri.getAvailable();
                    soFar += ri.getAvailable();
                    ri.setAvailable(0);
                } else {
                    int needed = quantity-soFar;
                    extended = extended.add(
                        new BigDecimal(ri.getCost()).multiply(
                            new BigDecimal(needed)));
                    //extended += ri.getCost()*needed;
                    soFar += ri.getAvailable();
                    ri.setAvailable(ri.getAvailable()-needed);
                }
                bellManager.updateReceivedItem(ri);
                count++;
            }
            // This takes care of the case where there is available but non in received
            if (inv != null && soFar < quantity && inv.getOnhand()-soFar > 0){
                boi.setBin(inv.getBin());
                if (quantity-soFar <= inv.getOnhand()-soFar){
                    extended = extended.add(
                        new BigDecimal(inv.getReceivedPrice()).multiply(
                            new BigDecimal(quantity).subtract(new BigDecimal(soFar))));
                    //extended += inv.getReceivedPrice()*(quantity-soFar);
                    soFar += quantity-soFar;
                } else {
                    extended = extended.add(
                        new BigDecimal(inv.getReceivedPrice()).multiply(
                            new BigDecimal(inv.getOnhand()).subtract(new BigDecimal(soFar))));
                    //extended += inv.getReceivedPrice()*(inv.getOnhand()-soFar);
                    soFar += inv.getOnhand()-soFar;
                }
            }

            // NOTE: we are trusting that the user enters the correct filled here!
            //if (soFar < quantity){
                // we need more than we have available
            //    coi.setFilled(soFar);
            //} else {
            //    coi.setFilled(quantity);
            //}
            boi.setExtended(extended.floatValue());
            if (boi.getFilled() == 0){
                boi.setCost(0F);
            } else {
                //boi.setCost(new Float(extended/boi.getFilled()));
                BigDecimal bd = extended.divide(new Money(boi.getFilled()), BigDecimal.ROUND_HALF_UP);
                bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                boi.setCost(bd.floatValue());
            }
        }

        // update inventory item for the new quantity
        if (inv != null){
            // check on cost of coi, in case no receiving was actually seen, we still set cost
            if (boi.getCost() == null || boi.getCost() == 0){
                boi.setCost(inv.getLastRecPrice());
            }

            if (boi.getCredit()){
                inv.setOnhand(inv.getOnhand()+boi.getQuantity());
            } else { // no idea why we commented this out
                inv.setOnhand(inv.getOnhand()-boi.getFilled());
            }
            if (!bellManager.updateInventory(inv)){
                logger.error("Could not update inventory item with isbn: "+inv.getIsbn());
            }
        }
    }
    // bellwether


    private static void createBellReceivedItem(BellReceivedItem item, String name){
        // This reduces break room marked items
        List<BellReceivedItem> allItems = item.getReceived().getReceivedItems();
        for (BellReceivedItem ri : allItems){
            if (ri.getBreakRoom() != null && ri.getBreakRoom()){
                ri.setQuantity(ri.getQuantity()-item.getQuantity());
                bellManager.updateReceivedItem(ri);
                BellInventory inv = bellManager.findInventoryByIsbn(ri.getIsbn());
                inv.setOnhand(inv.getOnhand()-item.getQuantity());
                bellManager.updateInventory(inv);
            }
        }

        // update the inventory onhand and available
        BellInventory inventory = bellManager.findInventoryByIsbn(item.getIsbn());
        if (inventory != null){
            inventory.setLastPo(item.getReceived().getPoNumber());
            inventory.setLastRecQuantity(item.getQuantity());
            inventory.setLastRecPrice(item.getCost());
            inventory.setOnhand(inventory.getOnhand()+item.getQuantity());
            if (!bellManager.updateInventory(inventory)){
                logger.error("Could not update inventory item with isbn: "+inventory.getIsbn());
            }
        }
    }

    private static void deleteBellReceivedItem(BellReceivedItem item, String name){
        // This reduces break room marked items
        BellReceived rec = bellManager.findReceivedById(item.getReceived().getId(), true);
        List<BellReceivedItem> allItems = rec.getReceivedItems();
        for (BellReceivedItem ri : allItems){
            if (ri.getBreakRoom()){
                ri.setQuantity(ri.getQuantity()+item.getQuantity());
                bellManager.updateReceivedItem(ri);
                BellInventory inv = bellManager.findInventoryByIsbn(ri.getIsbn());
                inv.setOnhand(inv.getOnhand()+item.getQuantity());
                bellManager.updateInventory(inv);
            }
        }

        BellInventory invItem = bellManager.findInventoryByIsbn(item.getIsbn());
        // update the inventory
        if (invItem.getOnhand() != null){
            invItem.setOnhand(invItem.getOnhand()-item.getQuantity());
        } else {
            invItem.setOnhand(0);
        }
        if (!bellManager.updateInventory(invItem)){
            logger.error("Could not update inventory item with isbn: "+invItem.getIsbn());
        }
    }

    private static void updateBellReceivedItem(BellReceivedItem item, String name){
        // This reduces break room marked items
        BellReceived rec = bellManager.findReceivedById(item.getReceived().getId(), true);
        List<BellReceivedItem> allItems = rec.getReceivedItems();
        for (BellReceivedItem ri : allItems){
            if (ri.getBreakRoom() != null && ri.getBreakRoom()){
                ri.setQuantity(ri.getQuantity()+item.getPreQuantity());
                ri.setQuantity(ri.getQuantity()-item.getQuantity());
                bellManager.updateReceivedItem(ri);
                BellInventory inv = bellManager.findInventoryByIsbn(ri.getIsbn());
                inv.setOnhand(inv.getOnhand()+item.getPreQuantity());
                inv.setOnhand(inv.getOnhand()-item.getQuantity());
                bellManager.updateInventory(inv);
            }
        }

        BellInventory inventory = bellManager.findInventoryByIsbn(item.getIsbn());
        // update the inventory onhand and available
        if (inventory.getOnhand() != null){
            inventory.setOnhand(inventory.getOnhand()-item.getPreQuantity());
            inventory.setOnhand(inventory.getOnhand()+item.getQuantity());
        } else {
            inventory.setOnhand(item.getQuantity());
        }
        inventory.setLastRecDate(new Timestamp(item.getReceived().getDate().getTime()));
        inventory.setLastRecQuantity(item.getQuantity());
        inventory.setLastRecPrice(item.getCost());
        if (!bellManager.updateInventory(inventory)){
            logger.error("Could not update inventory item with isbn: "+inventory.getIsbn());
        }
    }

}
 * 
 * 
 */
