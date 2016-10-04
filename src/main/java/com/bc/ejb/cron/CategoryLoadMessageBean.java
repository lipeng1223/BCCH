package com.bc.ejb.cron;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.jboss.annotation.ejb.ResourceAdapter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.amazon.xml.AWSECommerceService.BrowseNode;
import com.amazon.xml.AWSECommerceService.BrowseNodes;
import com.amazon.xml.AWSECommerceService.Condition;
import com.amazon.xml.AWSECommerceService.Item;
import com.amazon.xml.AWSECommerceService.ItemAttributes;
import com.amazon.xml.AWSECommerceService.ItemLookupResponse;
import com.amazon.xml.AWSECommerceService.Items;
import com.bc.amazon.AmazonItemLookupSoap;
import com.bc.dao.BaseDao;
import com.bc.orm.BellInventory;
import com.bc.orm.InventoryItem;
import com.bc.util.ThreadContext;

/*
 *  cronTrigger vals:
 *  
    1. Seconds
    2. Minutes
    3. Hours
    4. Day-of-Month
    5. Month
    6. Day-of-Week
    7. Year (optional field)
    
    "0 0 12 ? * WED" - which means "every Wednesday at 12:00 pm
    
    "0 0 12 * * ?" - which is every day at 12 pm 
    
    "0 0/1 * * * ?" activate every minute
    
    http://www.quartz-scheduler.org/docs/tutorial/TutorialLesson06.html
    
    
    current crontab -l
    
# (nightly.cron installed on Tue Jun  3 20:14:11 2008)
# (Cron version V5.0 -- $Id: crontab.c,v 1.12 2004/01/23 18:56:42 vixie Exp $)
00 20 * * * /home/megela/bookcountry/dbscripts/nightlybackup.sh > /home/megela/cron/nightlybackup.out
00 00 * * * /home/megela/bookcountry/dbscripts/bwlowprice.sh > /home/megela/cron/bwlowprice.out
00 01 * * * /home/megela/bookcountry/dbscripts/salesrank.sh > /home/megela/cron/salesrank.out
30 02 * * 2 /home/megela/bookcountry/dbscripts/dailyrec.sh > /home/megela/cron/dailyrec.out
00 03 * * * /home/megela/bookcountry/dbscripts/restartjboss.sh > /home/megela/cron/restartjboss.out
30 03 * * * /home/megela/bookcountry/dbscripts/nightlyoptimize.sh > /home/megela/cron/nightlyoptimize.out
00 04 * * * /home/megela/bookcountry/dbscripts/bwinventory.sh > /home/megela/cron/bwinventory.out

    
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 30 21 * * ?")
//    @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 30 9 * * ?")
})
@ResourceAdapter("quartz-ra.rar")
public class CategoryLoadMessageBean implements Job {

    private static Logger logger = Logger.getLogger(CategoryLoadMessageBean.class);
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        
        logger.error("Not running category load");
        if (true) return;
        
        if (System.getProperty("bc.nocron") != null && System.getProperty("bc.nocron").equals("true")) {
            logger.error("************************ NOT running cron, bc.nocron is true");
            return;
        }
        
        try {
            // setup the threadcontext for audit
            ThreadContext.setContext(-1L, "cron", "categoryLoad");
            
            // get all of the inventory for bell and book to get sales rank
            Map<String, Float> bookIsbns = new HashMap<String, Float>();
    
            BaseDao<BellInventory> bdao = new BaseDao<BellInventory>(BellInventory.class);
            BaseDao<InventoryItem> iidao = new BaseDao<InventoryItem>(InventoryItem.class);
            
            UserTransaction tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            
            List<String> results = iidao.getSession().createSQLQuery("select distinct isbn from inventory_item where skid = false and (char_length(isbn) = 10 or char_length(isbn) = 13)").
                addScalar("isbn", Hibernate.STRING).list();
            for (String s : results){
                bookIsbns.put(s, 0F);
            }
            
            bdao.flushAndClear();
            iidao.flushAndClear();
            tx.commit();
    
            logger.info("Bookcountry ISBN's: "+bookIsbns.size());
            Calendar now = Calendar.getInstance();
            logger.info("Starting lookup categories in amazon: "+now.getTime().toString());
            
            List<String> bookLookup = new ArrayList<String>(bookIsbns.keySet());
            long start = System.currentTimeMillis(), end, diff = 1000;
            
            AmazonItemLookupSoap ails = AmazonItemLookupSoap.getInstance();
            
            int count = 0;
            start = System.currentTimeMillis();
            int millisInHour = 60*60*1000;
            int amzCalls = 0;
            count = 0;
            while (bookLookup.size() > 0){
                if (amzCalls == 2000){
                    amzCalls = 0;
                    end = System.currentTimeMillis();
                    logger.info("Finished 2000 amazon calls, it took: "+(((end-start)/1000F)/60F)+" minutes");
                    diff = end - start;
                    if (diff < millisInHour){ 
                        // sleep till the next hour
                        try {
                            long sleep = (millisInHour - diff)+900000; // giving it an extra 15 minutes
                            logger.info("Finished "+count+" have to sleep "+((sleep/1000F)/60F)+" minutes");
                            Thread.sleep(sleep);
                        } catch (Exception e){} // do nothing
                    }
                    start = System.currentTimeMillis();
                }
                amzCalls++;
                
                String[] ids = null;
                if (bookLookup.size() > 20){
                    ids = new String[20];
                } else {
                    ids = new String[bookLookup.size()];
                }
                for (int i = 0; i < ids.length; i++){
                    ids[i] = bookLookup.remove(0);
                    count++;
                }
                
                try {
                    List<String> updates = new ArrayList<String>();
                    ItemLookupResponse resp = ails.lookupData(ids, "BrowseNodes", Condition.All, "All");
                    if (resp != null && resp.getItems() != null){
                        for (Items items : resp.getItems()){
                            if (items.getItem() != null){
                                for (Item item : items.getItem()){
                                    
                                    if (item.getBrowseNodes() != null){
                                        BrowseNodes nodes = item.getBrowseNodes();
                                        if (nodes.getBrowseNode() != null){
                                            HashMap<Integer, List<String>> nodeMap = new HashMap<Integer, List<String>>();
                                            recurseNodes(nodes.getBrowseNode(), nodeMap, 1);

                                            List<String> catList = new ArrayList<String>();
                                            boolean done = false;
                                            for (Integer key : nodeMap.keySet()){
                                                List<String> cats = nodeMap.get(key);
                                                if (cats.size() > 1 && !cats.contains("Stores")){
                                                    Collections.reverse(cats);
                                                    for (String cat : cats){
                                                        if (cat != null && !cat.equals("Subjects") && !cat.equals("Books") && !catList.contains(cat)){
                                                            catList.add(cat);
                                                            if (catList.size() == 4) {
                                                                done = true;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if (done) break;
                                                }
                                            }
                                            
                                            String isbn = item.getASIN();
                                            StringBuilder sb = new StringBuilder("update inventory_item set category1 = '");
                                            sb.append(catList.size() > 0 ? catList.get(0).replace("'", "\\'") : "");
                                            sb.append("', category2 = '");
                                            sb.append(catList.size() > 1 ? catList.get(1).replace("'", "\\'") : "");
                                            sb.append("', category3 = '");
                                            sb.append(catList.size() > 2 ? catList.get(2).replace("'", "\\'") : "");
                                            sb.append("', category4 = '");
                                            sb.append(catList.size() > 3 ? catList.get(3).replace("'", "\\'") : "");
                                            sb.append("' where isbn = '");
                                            sb.append(isbn);
                                            sb.append("'");
                                            updates.add(sb.toString());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
                    tx.setTransactionTimeout(600);
                    tx.begin();
                    for (String update : updates){
                        iidao.getSession().createSQLQuery(update).executeUpdate();
                    }
                    updates.clear();
                    iidao.flushAndClear();
                    tx.commit();
                } catch (Exception e){
                    logger.error("Failed at chunk of lookups, count: "+count, e);
                } finally {
                    if (tx.getStatus() == Status.STATUS_ACTIVE){
                        try {
                            tx.commit();
                        } catch (Exception ex){}
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e){}
                }
                
                end = System.currentTimeMillis();
                diff = end - start;
                
                if (count % 1000 == 0) {
                    logger.info("finished with "+count+" bookcountry category lookups....");
                }
            }
            
        } catch (Exception e){
            logger.error("Could not get Amazon Categories", e);
        }
        logger.info("Finished Category Load: "+Calendar.getInstance().getTime().toString());        
    }
    
    private void recurseNodes(BrowseNode[] nodes, HashMap<Integer, List<String>> nodeMap, int level){
        if (nodes != null){
            for (BrowseNode node : nodes){
                if (level == 1){
                    nodeMap.put(nodeMap.size()+1, new ArrayList<String>());
                }
                List<String> list = nodeMap.get(nodeMap.size());
                list.add(node.getName());
                recurseNodes(node.getAncestors(), nodeMap, level+1);
            }
        }
    }
    
}
