package com.bc.ejb;

import com.bc.orm.Audit;
import com.bc.orm.Auditable;
import com.bc.orm.BaseEntity;
import com.bc.orm.Customer;
import com.bc.orm.CustomerOrder;
import com.bc.orm.CustomerOrderItem;
import com.bc.orm.CustomerShipping;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import com.bc.orm.InventoryItem;
import com.bc.orm.Received;
import com.bc.orm.ReceivedItem;
import com.bc.orm.User;
import com.bc.orm.Vendor;
import com.bc.struts.QueryInput;
import com.bc.util.AuditMessage;
import com.bc.util.IsbnUtil;
import com.bc.util.MD5Hash;
import com.bc.util.ThreadContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import org.hibernate.LazyInitializationException;

public class ApiSession {
    private static Logger logger = Logger.getLogger(ApiSession.class);
    
    private static HashSet<String> ignoredMethods;
    
    static {
        ignoredMethods = new HashSet<String>();
        ignoredMethods.add("getLastUpdate");
        ignoredMethods.add("getLastUpdateBy");
    }
    
       
    public InventoryItem findByIsbnCond(String isbn, String condition) throws HibernateException
    {
        InventoryItemSessionLocal iSession = this.getInventoryItemSession();
        return iSession.findByIsbnCond(isbn, condition);
    }

    public List<InventoryItem> findBunchIsbn( String isbn ) throws HibernateException
    {
        if( isbn == null ) {
            return null;
        }
        InventoryItemSessionLocal iSession = this.getInventoryItemSession();
        return iSession.findByIsbn(isbn);
    }
            
    public Vendor findVendorById(long id){
        VendorSessionLocal vSession = this.getVendorSession();
        return vSession.findById(id);
    }

    public User findByUsernamePassword(String username, String password) throws HibernateException {
        UserSessionLocal uSession = this.getUserSession();
        User user = uSession.findByName(username);
        if (user == null){
            logger.info("No such user: " + username);
            return null;
        }
        try {
            if (user.getPassword().equals(MD5Hash.encode(password)))
                return user;
        } catch (NoSuchAlgorithmException ex) {
            java.util.logging.Logger.getLogger(ApiSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public Customer findCustomerById(Long id){
        try{
            CustomerSessionLocal cSession = this.getCustomerSession();
            return cSession.findById(id);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public Received findReceivingById(Long id){
        try{
            ReceivingSessionLocal rSession = this.getReceivingSession();
            return rSession.findById(id);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public CustomerShipping findCustomerShippingById(Long id){
        try{
            CustomerShippingSessionLocal csSession = this.getCustomerShippingSession();
            return csSession.findById(id);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public CustomerOrderItem findOrderItemByIsbnCond(CustomerOrder order, String isbn, String cond){
        OrderSessionLocal oSession = this.getOrderSession();
        return oSession.findItemByIsbnCond(order, isbn, cond);
    }
    
    public List getCustomers(){
        try{
            CustomerSessionLocal cSession = this.getCustomerSession();
            return cSession.findAll(new QueryInput(), "customerShippings").getData();
            
        } catch (Exception e){
            return null;
        }
    }

    public Long createOrder(CustomerOrder order){
        OrderSessionLocal oSession = this.getOrderSession();
        oSession.create(order);
        return order.getId();
    }
    
    public Boolean createOrderItem(CustomerOrderItem item){
        OrderSessionLocal oSession = this.getOrderSession();
        oSession.create(item);
        return true;
    }
    
    public void recalculateCommitted(Long id) {
        this.getInventoryItemSession().recalculateCommitted(id);
    }
    
    public void recalculateOrderItemTotals(Long id){
        this.getOrderSession().recalculateOrderItemTotals(id);
    }
    
    public void createReceiving(Received r){
        this.getReceivingSession().create(r);
    }
    
    public void createReceivingItem(ReceivedItem ri){
        this.getReceivingSession().create(ri);
    }
    
    public Boolean addReceivedItems(List<ReceivedItem> items) {
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            for (ReceivedItem ri : items){
                rSession.create(ri);
            }
        } catch (Exception e){
            logger.error("Could not add receiving items to order", e);
            return false;
        }
        return true;
    }
    
    public List<Received> getReceivings(int page){
        ReceivingSessionLocal rSession = this.getReceivingSession();
        QueryInput qi = new QueryInput();
        qi.addAndCriterion(Restrictions.eq("posted", false));
        qi.setSortCol("id");
        qi.setSortDir("desc");
        qi.setLimit(25);
        qi.setStart(page * 25);
        return rSession.findAll(qi, "vendor").getData();
    }
    
    public void updateReceiving(long id, String pono, Date podate, long vid, String publisher, String comment){
        ReceivingSessionLocal rSession = this.getReceivingSession();
        VendorSessionLocal vSession = this.getVendorSession();
        Received r = rSession.findById(id);
        
        r.setPoNumber(pono);
        r.setPoDate(podate);
        
        Vendor v = vSession.findById(vid);
        r.setVendor(v);
        r.setPublisher(publisher);
        r.setComment(comment);
        rSession.update(r);
    }
    
    public List<ReceivedItem> getReceivingItems(long rid, int page){
        List<ReceivedItem> receivedItems = null;
        ReceivingSessionLocal rSession = this.getReceivingSession();
        
        Received r = rSession.findById(rid);
        QueryInput qi = new QueryInput();
        qi.addAndCriterion(Restrictions.eq("received", r));
        qi.setSortCol("id");
        qi.setSortDir("desc");
        qi.setLimit(25);
        qi.setStart(page * 25);
        
        receivedItems = (List<ReceivedItem>) rSession.findAllItems(qi, "received").getData();
        return receivedItems;
    }
    
    public void updateReceivedItem(long id, int qty, int oqty, float percent, float costplb, float cost, String title, String bin, float lstprice, float selprice, String cover, Boolean bb, Boolean br, Boolean he, Boolean re){
        
        ReceivingSessionLocal rSession = this.getReceivingSession();
        ReceivedItem r = rSession.findItemById(id, "received");
        
        r.setPreQuantity(r.getQuantity());
        r.setQuantity(qty);
        r.setAvailable(qty);
        r.setOrderedQuantity(oqty);
        r.setPercentageList(percent);
        r.setCostPerLb(costplb);
        r.setCost(cost);
        boolean titleEquals = true;
        if (title != null) titleEquals = title.equals(r.getTitle());
        r.setTitle(title);
        r.setBin(bin);
        r.setListPrice(lstprice);
        r.setSellPrice(selprice);
        r.setCoverType(cover);
        r.setBellbook(bb);
        r.setBreakroom(br);
        r.setHigherEducation(he);

        if (!r.getReceived().getHolding()) {
            InventoryItemSessionLocal iSession = this.getInventoryItemSession();
            InventoryItem ii = iSession.findByIsbnCond(r.getIsbn(), r.getCond());
            if (ii != null){
                ii.setBellbook(bb);
                ii.setHe(he);
                ii.setRestricted(re);
                ii.setCover(cover);
                ii.setBin(bin);
                if (!titleEquals) ii.setTitle(title);
                if (ii.getListPrice() == null && r.getListPrice() != null) ii.setListPrice(r.getListPrice());
                iSession.update(ii);
            }
        }

        rSession.update(r);

        if (!r.getReceived().getHolding()) {
            LifoSessionLocal lifoSession = getLifoSession();
            lifoSession.updateReceivedItem(r);
        }

        rSession.recalculateReceived(r.getReceived().getId());
        
        //session.close();
    }
    
//    public void updateInventory(long id, String isbn, String cond, String title, String author, String publisher, float lprice, float sprice, int onhand, String bin, String cover, Boolean bb, Boolean skid, Boolean rest, Boolean he, String category, int pages, float length, float width, float height, float weight){
    public void updateInventory(long id, String bin){
        InventoryItemSessionLocal iSession = this.getInventoryItemSession();
        InventoryItem i = iSession.findById(id);
        if (i == null)
            return;
//        if (i.getIsbn().length() == 10){
//            i.setIsbn10(isbn);
//            i.setIsbn13(IsbnUtil.getIsbn13(i.getIsbn()));
//        } else if (i.getIsbn().length() == 13 && i.getIsbn().startsWith("978")) {
//            i.setIsbn10(IsbnUtil.getIsbn10(i.getIsbn()));
//            i.setIsbn13(i.getIsbn());
//            i.setIsbn(i.getIsbn10());
//        }
//        i.setCond(cond);
//        i.setAuthor(author);
//        i.setPublisher(publisher);
//        i.setListPrice(lprice);
//        i.setSellingPrice(sprice);
        
//        if (i.getOnhand() != onhand){
//            i.setOnhand(onhand);
//            i.setAvailable(i.getOnhand() - i.getCommitted());
//            if (i.getAvailable() < 0) i.setAvailable(0);
//        }
        i.setBin(bin);
//        i.setCover(cover);
//        i.setHe(he);
//        i.setBellbook(bb);
//        i.setRestricted(rest);
//        i.setCategory1(category);
//        i.setNumberOfPages(pages);
//        i.setLength(length);
//        i.setWeight(weight);
//        i.setHeight(height);
        
        iSession.update(i);
        iSession.updatePendingBins(id, bin);
        
    }
    
    
    public void createReceivedItems(List<ReceivedItem> items, List<Long> ids){
        if (items == null || items.size() == 0) return;
        logger.info("Starting createReceivedItems with "+items.size()+" items..");
        
        Received rec = items.get(0).getReceived();
        if (rec.getHolding()) return;
        
        InventoryItemSessionLocal iSession = this.getInventoryItemSession();
        QueryInput qi = new QueryInput();
        qi.addAndCriterion(Restrictions.in("id", ids));
        List<InventoryItem> invItems = (List<InventoryItem>) iSession.findAll(qi);;
        Map<Long, InventoryItem> invMap = new HashMap<Long, InventoryItem>();
        for (InventoryItem ii : invItems){
            invMap.put(ii.getId(), ii);
        }
        
        for (int i = 0; i < items.size(); i++){
            ReceivedItem ri = items.get(i);
            InventoryItem ii = invMap.get(ri.getInventoryItem().getId());
            updateInventoryItemForReceivedItem(ii, rec.getPoDate(), ri.getQuantity(), ri.getCost(), rec.getPoNumber(), ri, true);
        }
        //session.close();
        logger.info("Finished createReceivedItems with "+items.size()+" items.");
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
            this.getLifoSession().updatePendingOrderBins(ii.getId(), ii.getBin(), ii.getTitle());
        
    }
    
    public InventoryItemSessionLocal getInventoryItemSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (InventoryItemSessionLocal)ctx.lookup(InventoryItemSession.LocalJNDIStringNoLoader);
            }
            return (InventoryItemSessionLocal)ctx.lookup(InventoryItemSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup InventoryItemSession", ne);
        }
        throw new RuntimeException("Could not lookup InventoryItemSession");
    }
    
    public LifoSessionLocal getLifoSession() {
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
    
    public UserSessionLocal getUserSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (UserSessionLocal)ctx.lookup(UserSession.LocalJNDIStringNoLoader);
            }
            return (UserSessionLocal)ctx.lookup(UserSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup UserSession", ne);
        }
        throw new RuntimeException("Could not lookup UserSession");
    }

    public CustomerSessionLocal getCustomerSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (CustomerSessionLocal)ctx.lookup(CustomerSession.LocalJNDIStringNoLoader);
            }
            return (CustomerSessionLocal)ctx.lookup(CustomerSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup CustomerSession", ne);
        }
        throw new RuntimeException("Could not lookup CustomerSession");
    }
    
    public OrderSessionLocal getOrderSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (OrderSessionLocal)ctx.lookup(OrderSession.LocalJNDIStringNoLoader);
            }
            return (OrderSessionLocal)ctx.lookup(OrderSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup OrderSession", ne);
        }
        throw new RuntimeException("Could not lookup OrderSession");
    }

    public ReceivingSessionLocal getReceivingSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (ReceivingSessionLocal)ctx.lookup(ReceivingSession.LocalJNDIStringNoLoader);
            }
            return (ReceivingSessionLocal)ctx.lookup(ReceivingSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup ReceivingSession", ne);
        }
        throw new RuntimeException("Could not lookup ReceivingSession");
    }
    
    public VendorSessionLocal getVendorSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (VendorSessionLocal)ctx.lookup(VendorSession.LocalJNDIStringNoLoader);
            }
            return (VendorSessionLocal)ctx.lookup(VendorSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup BellVendorSession", ne);
        }
        throw new RuntimeException("Could not lookup VendorSession");
    }
    
    public CustomerShippingSessionLocal getCustomerShippingSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (CustomerShippingSessionLocal)ctx.lookup(CustomerShippingSession.LocalJNDIStringNoLoader);
            }
            return (CustomerShippingSessionLocal)ctx.lookup(CustomerShippingSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup CustomerShippingSession", ne);
        }
        throw new RuntimeException("Could not lookup CustomerShippingSession");
    } 
    
    public AuditSessionLocal getAuditSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (AuditSessionLocal)ctx.lookup(AuditSession.LocalJNDIStringNoLoader);
            }
            return (AuditSessionLocal)ctx.lookup(AuditSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup AuditSession", ne);
        }
        throw new RuntimeException("Could not lookup AuditSession");
    }
    
    
    
}
