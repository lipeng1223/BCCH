package com.bc.ejb;


import com.bc.dao.BaseDao;
import com.bc.dao.DaoResults;
import com.bc.orm.BackStockItem;
import com.bc.orm.BackStockLocation;
import com.bc.orm.InventoryItem;
import com.bc.struts.QueryInput;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.NoResultException;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

@Stateless
public class BackStockSession implements BackStockSessionLocal {

    public static final String LocalJNDIString = "inventory/"+BackStockSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = BackStockSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(BackStockSession.class);
        
    public Integer getCount() {
        BaseDao<BackStockItem> dao = new BaseDao<BackStockItem>(BackStockItem.class);
        return dao.getCount();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BackStockItem findById(Long id) throws NoResultException {
        BaseDao<BackStockItem> dao = new BaseDao<BackStockItem>(BackStockItem.class);
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Boolean exists(String isbn) {
        BaseDao<BackStockItem> dao = new BaseDao<BackStockItem>(BackStockItem.class);
        Criteria crit = dao.getSession().createCriteria(BackStockItem.class);
        crit.add(Restrictions.eq("isbn", isbn));
        List res = crit.list();
        return res != null && res.size() > 0;
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<BackStockItem> findByIsbn(String isbn) { 
        BaseDao<BackStockItem> dao = new BaseDao<BackStockItem>(BackStockItem.class);
        Criteria crit = dao.getSession().createCriteria(BackStockItem.class);
        crit.add(Restrictions.eq("isbn", isbn));
        crit.setFetchMode("backStockLocations", FetchMode.JOIN);
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return (List<BackStockItem>)crit.list();
    }


    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BackStockItem findById(Long id, String... joins) throws NoResultException {
        BaseDao<BackStockItem> dao = new BaseDao<BackStockItem>(BackStockItem.class);
        return dao.findById(id, joins);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(BackStockItem backStockItem) {
        BaseDao<BackStockItem> dao = new BaseDao<BackStockItem>(BackStockItem.class);
        dao.create(backStockItem, null);
        
        // update the inventory item back stock
        InventoryItemSessionLocal iiSession = getInventoryItemSession();
        List<InventoryItem> invItems = iiSession.findByIsbn(backStockItem.getIsbn());
        for (InventoryItem ii : invItems){
            ii.setBackStock(true);
            if (ii.getBin() != null && !ii.getBin().endsWith("-B")){
                ii.setBin(ii.getBin()+"-B");
            }
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(BackStockItem backStockItem) {
        BaseDao<BackStockItem> dao = new BaseDao<BackStockItem>(BackStockItem.class);
        dao.update(backStockItem, null);
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        BaseDao<BackStockItem> dao = new BaseDao<BackStockItem>(BackStockItem.class);
        BackStockItem item = dao.findById(id);
        String isbn = item.getIsbn();
        
        InventoryItemSessionLocal iiSession = getInventoryItemSession();
        List<InventoryItem> invItems = iiSession.findByIsbn(isbn);
        for (InventoryItem ii : invItems){
            ii.setBackStock(false);
            if (ii.getBin() != null && ii.getBin().endsWith("-B")){
                ii.setBin(ii.getBin().substring(0, ii.getBin().length()-2));
            }
        }
        
        dao.delete(item, null);
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
    
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput, String... joins) {
        BaseDao<BackStockItem> dao = new BaseDao<BackStockItem>(BackStockItem.class);
        return dao.findAll(queryInput, joins);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput, HashMap<String, String> aliases, String... joins) {
        BaseDao<BackStockItem> dao = new BaseDao<BackStockItem>(BackStockItem.class);
        return dao.findAll(queryInput, aliases, joins);
    }
        
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BackStockLocation findBackStockLocationById(Long id, String... joins) throws NoResultException {
        BaseDao<BackStockLocation> dao = new BaseDao<BackStockLocation>(BackStockLocation.class);
        return dao.findById(id, joins);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAllBackStockLocations(QueryInput queryInput, HashMap<String, String> aliases, String... joins) {
        BaseDao<BackStockLocation> dao = new BaseDao<BackStockLocation>(BackStockLocation.class);
        return dao.findAll(queryInput, aliases, joins);
    }
        
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(BackStockLocation backStockLocation) {
        BaseDao<BackStockLocation> dao = new BaseDao<BackStockLocation>(BackStockLocation.class);
        dao.create(backStockLocation, backStockLocation.getBackStockItem().getId());
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(BackStockLocation backStockLocation) {
        BaseDao<BackStockLocation> dao = new BaseDao<BackStockLocation>(BackStockLocation.class);
        dao.update(backStockLocation, backStockLocation.getBackStockItem().getId());
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteBackStockLocation(Long id) {
        BaseDao<BackStockLocation> dao = new BaseDao<BackStockLocation>(BackStockLocation.class);
        BackStockLocation bsl = dao.findById(id);
        dao.delete(bsl, bsl.getBackStockItem().getId());
    }
    
    public void updateCounts(Long id){
        BaseDao<BackStockItem> dao = new BaseDao<BackStockItem>(BackStockItem.class);
        StringBuilder sb = new StringBuilder("update backstock_item as bsi set bsi.totalQuantity = (select sum(bsl.quantity) from backstock_location as bsl where bsl.backStockItem_id = bsi.id) where bsi.id = ");
        sb.append(id);
        dao.getSession().createSQLQuery(sb.toString()).executeUpdate();
        sb = new StringBuilder("update backstock_item as bsi set bsi.totalLocations = (select count(bsl.id) from backstock_location as bsl where bsl.backStockItem_id = bsi.id) where bsi.id = ");
        sb.append(id);
        dao.getSession().createSQLQuery(sb.toString()).executeUpdate();
    }
    
}
