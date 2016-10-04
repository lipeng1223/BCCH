package com.bc.ejb;


import com.bc.actions.AmazonLookup;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;

import org.apache.log4j.Logger;

import com.bc.dao.BaseDao;
import com.bc.dao.DaoResults;
import com.bc.orm.FastReceivedItem;
import com.bc.orm.InventoryItem;
import com.bc.orm.Received;
import com.bc.orm.ReceivedItem;
import com.bc.struts.QueryInput;
import com.bc.util.IsbnUtil;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Set;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

@Stateless
public class ReceivingSession implements ReceivingSessionLocal {

    public static final String LocalJNDIString = "inventory/"+ReceivingSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = ReceivingSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(ReceivingSession.class);
    
    public Integer getCount() {
        BaseDao<Received> dao = new BaseDao<Received>(Received.class);
        return dao.getCount();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Received findById(Long id) throws NoResultException {
        BaseDao<Received> dao = new BaseDao<Received>(Received.class);
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Received findById(Long id, String... joins) throws NoResultException {
        BaseDao<Received> dao = new BaseDao<Received>(Received.class);
        return dao.findById(id, joins);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(Received received) {
        BaseDao<Received> dao = new BaseDao<Received>(Received.class);
        dao.create(received, null);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createFromFastRec(Long receivedId, ReceivedItem receivedItem) {
        Received received = findById(receivedId);
        if (received == null){
            throw new RuntimeException("noexist");
        }
        if (received.getPosted()){
            throw new RuntimeException("posted");
        }
        
        QueryInput qi = new QueryInput();
        qi.addAndCriterion(Restrictions.eq("isbn", receivedItem.getIsbn()));
        qi.addAndCriterion(Restrictions.eq("cond", receivedItem.getCond()));
        qi.addAndCriterion(Restrictions.eq("received", received));
        DaoResults results = findAllItems(qi);
        if (results.getData() != null && results.getData().size() > 0){
            ReceivedItem toUpdate = (ReceivedItem)results.getData().get(0);
            toUpdate.setPreQuantity(toUpdate.getQuantity());
            toUpdate.setQuantity(toUpdate.getQuantity()+receivedItem.getQuantity());
            toUpdate.setAvailable(toUpdate.getAvailable()+receivedItem.getQuantity());
            updateWithLifo(toUpdate, received.getId());
        } else {
            // not already on the receiving
            receivedItem.setReceived(received);
            receivedItem.setDate(received.getPoDate());
            receivedItem.setAvailable(receivedItem.getQuantity());
            receivedItem.setType("Pieces");

            InventoryItemSessionLocal iiSession = getInventoryItemSession();
            InventoryItem ii = iiSession.findByIsbnCond(receivedItem.getIsbn(), receivedItem.getCond());
            if (ii == null){
                // create a new inventory item
                ii = new InventoryItem();
                ii.setIsbn(receivedItem.getIsbn());
                if (IsbnUtil.isValid10(ii.getIsbn())){
                    ii.setIsbn10(ii.getIsbn());
                }
                ii.setIsbn13(receivedItem.getIsbn13());
                ii.setCond(receivedItem.getCond());
                ii.setBin(receivedItem.getBin());
                if (receivedItem.getSellPrice() != null) ii.setSellingPrice(receivedItem.getSellPrice().floatValue());
                ii.setSkid(receivedItem.getSkid());
                ii.setBellbook(receivedItem.getBellbook());
                ii.setHe(receivedItem.getHigherEducation());
                ii.setRestricted(receivedItem.getRestricted());
                ii.setSellPricePercentList(receivedItem.getPercentageList());
                ii.setListPrice(receivedItem.getListPrice());
                ii.setCover(receivedItem.getCoverType());
                ii.setOnhand(0);
                ii.setTitle(receivedItem.getTitle());
                AmazonLookup.getInstance().lookupData(ii, true);
                if (!ii.getAmazonDataLoaded()){
                    throw new RuntimeException("noamazondata");
                }
                iiSession.create(ii);
            } else if (!received.getHolding()) {
                ii.setBellbook(receivedItem.getBellbook());
                ii.setHe(receivedItem.getHigherEducation());
                ii.setRestricted(receivedItem.getRestricted());
                ii.setCover(receivedItem.getCoverType());
                ii.setBin(receivedItem.getBin());
                iiSession.update(ii);
            }
            receivedItem.setTitle(ii.getTitle());
            receivedItem.setInventoryItem(ii);
            create(receivedItem);
            recalculateReceived(received.getId());
            
            if (!received.getHolding()) {
            	LifoSessionLocal lifoSession = getLifoSession();
            	lifoSession.createReceivedItem(receivedItem, ii.getId(), null, false);
            }
        }
        
        BaseDao<FastReceivedItem> dao = new BaseDao<FastReceivedItem>(FastReceivedItem.class);
        FastReceivedItem fri = new FastReceivedItem();
        fri.setIsbn(receivedItem.getIsbn());
        fri.setCond(receivedItem.getCond());
        fri.setReceived(received);
        dao.create(fri, null);
    }
    
    private InventoryItemSessionLocal getInventoryItemSession() {
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
    
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(Received received) {
        BaseDao<Received> dao = new BaseDao<Received>(Received.class);
        dao.update(received, null);
        dao.executeJdbcUpdate("update received_item as ri, received as r set ri.date = r.po_date where ri.received_id = r.id and r.id = "+received.getId());
        if (received.getPosted()){
            // update last po on all of the inventory items for this received
            dao.executeJdbcUpdate("update inventory_item as ii, received_item as ri, received as r set ii.lastpo_date = r.po_date, ii.received_date = r.po_date, ii.received_quantity = ri.quantity, ii.lastpo = r.po_number where ii.id = ri.inventory_item_id and ri.received_id = r.id and r.id = "+received.getId());
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        BaseDao<Received> dao = new BaseDao<Received>(Received.class);
        Received rec = dao.findById(id);
        // have to go and fix the inventory items for any of the received items on this 
        BaseDao<ReceivedItem> riDao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        Criteria crit = riDao.getSession().createCriteria(ReceivedItem.class);
        crit.add(Restrictions.eq("received", rec));
        crit.setFetchMode("inventoryItem", FetchMode.JOIN);
        List<ReceivedItem> items = crit.list();
        if (!rec.getHolding())
        	getLifoSession().deleteReceivedItems(items);
        dao.delete(rec, null);
    }
    
    public DaoResults findAll(QueryInput queryInput, String... joins) {
        BaseDao<Received> dao = new BaseDao<Received>(Received.class);
        return dao.findAll(queryInput, joins);
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput, HashMap<String, String> aliases, String... joins) {
        BaseDao<Received> dao = new BaseDao<Received>(Received.class);
        return dao.findAll(queryInput, aliases, joins);
    }
     
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAllItems(QueryInput queryInput, String... joins) {
        BaseDao<ReceivedItem> dao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        return dao.findAll(queryInput, joins);
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAllFastRecItems(QueryInput queryInput) {
        BaseDao<FastReceivedItem> dao = new BaseDao<FastReceivedItem>(FastReceivedItem.class);
        return dao.findAll(queryInput);
    }

    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAllItems(QueryInput queryInput, HashMap<String, String> aliases, String... joins) {
        BaseDao<ReceivedItem> dao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        return dao.findAll(queryInput, aliases, joins);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ReceivedItem findItemById(Long id, String... joins) throws NoResultException {
        BaseDao<ReceivedItem> dao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        return dao.findById(id, joins);
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ReceivedItem findItemBy(Received received, String isbn, String cond) {
        BaseDao<ReceivedItem> dao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        Criteria crit = dao.getSession().createCriteria(ReceivedItem.class);
        crit.add(Restrictions.eq("received", received));
        crit.add(Restrictions.eq("isbn", isbn));
        crit.add(Restrictions.eq("cond", cond));
        List<ReceivedItem> items = crit.list();
        if (items.size() > 0){
            return items.get(0);
        }
        return null;
    }
    
    public List<Long> getAllReceivedIds(String isbn, int cnt, boolean bell, boolean restricted, boolean he) {
        BaseDao<Received> dao = new BaseDao<Received>(Received.class);
        StringBuilder sb = new StringBuilder("select ri.received_id, count(ri.received_id) from received_item o join inventory_item i on ri.inventory_item_id = i.id where (i.isbn in ");
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
        sb.append(" group by ri.received_id");
        logger.info("Sql: " + sb.toString());
        List<Object[]> list = dao.getSession().createSQLQuery(sb.toString()).list();
        List<Long> ids = new ArrayList<Long>();
        for (Object[] rs : list){
            BigInteger bi = (BigInteger) rs[0];
            Integer c = ((BigInteger) rs[1]).intValue();
            if (cnt == c)
                ids.add(bi.longValue());
        }
        logger.info("Got OIDs");
        return ids;
    }
    

    public void create(ReceivedItem receivedItem) {
        BaseDao<ReceivedItem> dao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        dao.create(receivedItem, receivedItem.getReceived().getId());
    }
    
    private ReceivedItem findExisting(Set<ReceivedItem> items, String isbn, String cond) {
        for (ReceivedItem ri : items){
            if (ri.getIsbn().equals(isbn) && ri.getCond().equals(cond)) return ri;
        }
        return null;
    }
    
    public void updateInventoryItemsForPost(Long receivedId){
        Received received = findById(receivedId, "receivedItems", "receivedItems.inventoryItem");
        for (ReceivedItem ri : received.getReceivedItems()){
        	ri.getInventoryItem().setLastpoDate(received.getPoDate());
        	ri.getInventoryItem().setLastpo(received.getPoNumber());
        	ri.getInventoryItem().setReceivedDate(received.getPoDate());
        	ri.getInventoryItem().setReceivedQuantity(ri.getQuantity());
            if (ri.getInventoryItem().getLastpo().length() > 20){
            	ri.getInventoryItem().setLastpo(ri.getInventoryItem().getLastpo().substring(0, 20));
            }
        }    	
    }
    
    public List<ReceivedItem> updateWithLifo(List<ReceivedItem> receivedItems, Long receivedId) {
        List<ReceivedItem> newItems = new ArrayList<ReceivedItem>();
        Received received = findById(receivedId, "receivedItems");
        for (ReceivedItem ri : receivedItems){
            ReceivedItem existingItem = findExisting(received.getReceivedItems(), ri.getIsbn(), ri.getCond());
            if (existingItem != null){
                existingItem.setPreQuantity(existingItem.getQuantity());
                existingItem.setQuantity(existingItem.getQuantity() + ri.getQuantity());
                existingItem.setAvailable(existingItem.getAvailable()+ ri.getQuantity());
                if (ri.getBin() != null) existingItem.setBin(ri.getBin());
                if (ri.getCost() != null) existingItem.setCost(ri.getCost());
                if (ri.getSellPrice() != null) existingItem.setSellPrice(ri.getSellPrice());
                 
                updateWithLifo(existingItem, receivedId);
            } else {
                newItems.add(ri);
            }
        }
        return newItems;
    }    
    
    public void update(ReceivedItem receivedItem) {
        BaseDao<ReceivedItem> dao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        dao.update(receivedItem, receivedItem.getReceived().getId());
    }
    
    public void updateWithLifo(ReceivedItem receivedItem, Long receivedId){
        BaseDao<ReceivedItem> dao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        dao.update(receivedItem, receivedItem.getReceived().getId());
        if (!receivedItem.getReceived().getHolding())
            getLifoSession().updateReceivedItem(receivedItem);
    }
    
    public void deleteItem(Long id) {
        BaseDao<ReceivedItem> dao = new BaseDao<ReceivedItem>(ReceivedItem.class);
        ReceivedItem ri = dao.findById(id);
        if (!ri.getReceived().getHolding())
        	getLifoSession().deleteReceivedItem(ri);
        dao.delete(ri, ri.getReceived().getId());
    }

    public Boolean addReceivedItems(List<ReceivedItem> items) {
        try {
            BaseDao<ReceivedItem> dao = new BaseDao<ReceivedItem>(ReceivedItem.class);

            for (ReceivedItem ri : items){
                dao.create(ri, ri.getReceived().getId());
            }
        } catch (Exception e){
            logger.error("Could not add receiving items to order", e);
            return false;
        }
        return true;
    }

    public void recalculateReceived(Long id) {
        BaseDao<Received> rDao = new BaseDao<Received>(Received.class);
        rDao.getSession().createSQLQuery("select updateReceived("+id+")").list();
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
