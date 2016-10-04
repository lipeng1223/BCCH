package com.bc.ejb;

import com.bc.actions.AmazonLookup;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import com.bc.dao.BaseDao;
import com.bc.orm.BellInventory;
import com.bc.orm.InventoryItem;
import com.bc.util.IsbnUtil;
import com.bc.util.ThreadContext;
import javax.ejb.Stateless;

@Stateless
public class InventoryIsbnCheckSession implements InventoryIsbnCheckSessionLocal {

    private static Logger log = Logger.getLogger(InventoryIsbnCheckSession.class);
    
    public static final String LocalJNDIString = "inventory/"+InventoryIsbnCheckSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = InventoryIsbnCheckSession.class.getSimpleName()+"/local";
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Long> fixBookcountryIsbns() {
        
        String lastUpdate = null;
        List<Long> titleChecks = new ArrayList<Long>();
        try {
            log.info("Starting updates of the inventory_item isbns");
            // setup the threadcontext for audit
            ThreadContext.setContext(-1L, "tools", "inventoryIsbnCheckSession");
            
            BaseDao<BellInventory> bdao = new BaseDao<BellInventory>(BellInventory.class);
            BaseDao<InventoryItem> iidao = new BaseDao<InventoryItem>(InventoryItem.class);
            
            List<Object[]> invresults = iidao.getSession().createSQLQuery("select distinct isbn, id from inventory_item where skid = false and (char_length(isbn) = 10 or char_length(isbn) = 13)").
                addScalar("isbn", Hibernate.STRING).addScalar("id", Hibernate.LONG).list();
            List<String> updates = new ArrayList<String>();
            int updateLoops = 0;
            for (Object[] obarray : invresults){
                String isbn = (String)obarray[0];
                Long id = (Long)obarray[1];
                if (IsbnUtil.isValid10(isbn) || IsbnUtil.isValid13(isbn)){
                    
                    if (IsbnUtil.getIsbn10(isbn).length() != 10) continue;
                    if (IsbnUtil.getIsbn13(isbn).length() != 13) continue;
                    
                    StringBuilder sb = new StringBuilder("update inventory_item set isbn10 = '");
                    sb.append(IsbnUtil.getIsbn10(isbn));
                    sb.append("', isbn = '");
                    sb.append(IsbnUtil.getIsbn10(isbn));
                    sb.append("', isbn13 = '");
                    sb.append(IsbnUtil.getIsbn13(isbn));
                    sb.append("' where id = ");
                    sb.append(id);
                    updates.add(sb.toString());

                    if (updates.size() == 1000){
                        updateLoops++;
                        log.info("Updating inventory items "+(updateLoops * 1000));
                        UserTransaction tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
                        tx.begin();
                        for (String update : updates){
                            lastUpdate = update;
                            iidao.getSession().createSQLQuery(update).executeUpdate();
                        }
                        iidao.flushAndClear();
                        tx.commit();
                        updates.clear();
                    }
                }
            }
            if (updates.size() > 0){
                log.info("Finishing updating the inventory items");
                UserTransaction tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
                tx.begin();
                for (String update : updates){
                    lastUpdate = update;
                    iidao.getSession().createSQLQuery(update).executeUpdate();
                }
                iidao.flushAndClear();
                tx.commit();
                updates.clear();
            }
            log.info("Finished updates of the inventory_item isbns");
            
            
            invresults = iidao.getSession().createSQLQuery("select distinct isbn, id from inventory_item where skid = false and (char_length(isbn) = 10 or char_length(isbn) = 13) and (char_length(title) = 0 or title is null)").addScalar("isbn", Hibernate.STRING).addScalar("id", Hibernate.LONG).list();
            log.info("");
            for (Object[] obarray : invresults){
                String isbn = (String)obarray[0];
                Long id = (Long)obarray[1];
                if (IsbnUtil.isValid10(isbn) || IsbnUtil.isValid13(isbn)){
                    
                    if (IsbnUtil.getIsbn10(isbn).length() != 10) continue;
                    if (IsbnUtil.getIsbn13(isbn).length() != 13) continue;
                    
                    titleChecks.add(id);
                }
            }
            
        } catch (Exception e){
            log.error("lastUpdate: "+lastUpdate);
            log.error("Could not update inventory item isbns", e);
        }
        
        return titleChecks;   
            
    }
}
