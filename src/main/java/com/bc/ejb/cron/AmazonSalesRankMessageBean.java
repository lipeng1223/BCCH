package com.bc.ejb.cron;

import com.amazon.xml.AWSECommerceService.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.jboss.annotation.ejb.ResourceAdapter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bc.amazon.AmazonItemLookupSoap;
import com.bc.dao.BaseDao;
import com.bc.orm.BellInventory;
import com.bc.orm.InventoryItem;
import com.bc.util.ThreadContext;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import javax.transaction.Status;

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
    
    "0 0 12 * * ?" - which is every day at 12 px 
    
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
   @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 0 0 * * ?")
//    @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 0 12 * * ?")
})
@ResourceAdapter("quartz-ra.rar")
public class AmazonSalesRankMessageBean implements Job {

    private static Logger logger = Logger.getLogger(AmazonSalesRankMessageBean.class);
    
    private static final int CALLS_PER_HOUR = 28800;
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        
        if (System.getProperty("bc.nocron") != null && System.getProperty("bc.nocron").equals("true")) {
            logger.error("************************ NOT running cron, bc.nocron is true");
            return;
        }
        
        try {
            // setup the threadcontext for audit
            ThreadContext.setContext(-1L, "cron", "nightlySalesRank");
            
            // get all of the inventory for bell and book to get sales rank
            Map<String, Integer> bellIsbns = new HashMap<String, Integer>();
            Map<String, Float> bookIsbns = new HashMap<String, Float>();
    
            BaseDao<BellInventory> bdao = new BaseDao<BellInventory>(BellInventory.class);
            BaseDao<InventoryItem> iidao = new BaseDao<InventoryItem>(InventoryItem.class);
            
            UserTransaction tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            List<String> results = bdao.getSession().createSQLQuery("select distinct isbn from bell_inventory where (noamazon is null or noamazon = false) and onhand > 0 and skid = false").
                addScalar("isbn", Hibernate.STRING).list();
            for (String s : results){
                bellIsbns.put(s, 0);
            }
            
            results = iidao.getSession().createSQLQuery("select distinct isbn from inventory_item where skid = false and onhand > 0").
                addScalar("isbn", Hibernate.STRING).list();
            for (String s : results){
                bookIsbns.put(s, 0F);
            }
            
            bdao.flushAndClear();
            iidao.flushAndClear();
            tx.commit();
    
            logger.info("Bellwether ISBN's: "+bellIsbns.size());
            logger.info("Bookcountry ISBN's: "+bookIsbns.size());
            logger.info("Total to work on: "+(bellIsbns.size()+bookIsbns.size()));
            Calendar now = Calendar.getInstance();
            logger.info("Starting lookup in amazon: "+now.getTime().toString());
            
            List<String> bellLookup = new ArrayList<String>(bellIsbns.keySet());
            List<String> bookLookup = new ArrayList<String>(bookIsbns.keySet());
            long start = System.currentTimeMillis(), end, diff;
            
            // bell loop
            AmazonItemLookupSoap ails = AmazonItemLookupSoap.getInstance();
            int count = 0;
            int millisInHour = 60*60*1000;
            int amzCalls = 0;
            while (bellLookup.size() > 0){
                if (amzCalls == CALLS_PER_HOUR){
                    amzCalls = 0;
                    end = System.currentTimeMillis();
                    logger.info("Finished "+CALLS_PER_HOUR+" amazon calls, it took: "+(((end-start)/1000F)/60F)+" minutes");
                    diff = end - start;
                    if (diff < millisInHour){ 
                        // sleep till the next hour
                        try {
                            long sleep = (millisInHour - diff)+600000; // giving it an extra 10 minutes
                            logger.info("Finished "+count+" have to sleep "+((sleep/1000F)/60F)+" minutes");
                            Thread.sleep(sleep);
                            //System.out.println("Consumer slept: "+diff);
                        } catch (Exception e){} // do nothing
                    }
                    start = System.currentTimeMillis();
                }
                amzCalls++;
                
                String[] ids = null;
                if (bellLookup.size() > 20){
                    ids = new String[20];
                } else {
                    ids = new String[bellLookup.size()];
                }
                for (int i = 0; i < ids.length; i++){
                    ids[i] = bellLookup.remove(0);
                    count++;
                }
                
                try {
                    List<String> updates = new ArrayList<String>();
                    
                    ItemLookupResponse resp = ails.lookupData(ids, "Medium", Condition.All, "All");
                    
                    if (resp != null && resp.getItems() != null){
                        for (Items items : resp.getItems()){
                            if (items.getItem() != null){
                                for (Item item : items.getItem()){
                                    ItemAttributes ia = item.getItemAttributes();
                                    String isbn = ia.getISBN();
                                    String sr = item.getSalesRank();
                                    String p = ia.getPublisher();
                                    if (p != null) p = p.replace("'", "\\'");
                                    if (sr != null){
                                        StringBuilder sb = new StringBuilder("update bell_inventory set salesrank = ");
                                        sb.append(sr);
                                        sb.append(", publisher = '");
                                        sb.append(p);
                                        //sb.append("', companyRec = '");
                                        //sb.append(p);
                                        sb.append("', lastAmzCheck = now() where isbn = '");
                                        sb.append(isbn);
                                        sb.append("'");
                                        updates.add(sb.toString());
                                    }
                                }
                            }
                        }
                    }
                    
                    tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
                    tx.setTransactionTimeout(600);
                    tx.begin();
                    for (String update : updates){
                        bdao.getSession().createSQLQuery(update).executeUpdate();
                    }
                    updates.clear();
                    bdao.flushAndClear();
                    tx.commit();
                } catch (Exception e){
                    tx.rollback();
                    logger.error("Failed at chunk of lookups, count: "+count, e);
                } finally {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e){}
                }
                
                if (count % 1000 == 0) {
                    logger.info("finished with "+count+" bellwether lookups....");
                }
                
            }
            
            logger.info("Finished bellwether...");
            
            // book loop
            start = System.currentTimeMillis();
            //List<InvData> differences = new ArrayList<InvData>();
            count = 0;
            start = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfnoday = new SimpleDateFormat("yyyy-MM");
            while (bookLookup.size() > 0){
                
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
                    
                    if (amzCalls == CALLS_PER_HOUR){
                        amzCalls = 0;
                        end = System.currentTimeMillis();
                        logger.info("Finished "+CALLS_PER_HOUR+" amazon calls, it took: "+(((end-start)/1000F)/60F)+" minutes");
                        diff = end - start;
                        if (diff < millisInHour){ 
                            // sleep till the next hour
                            try {
                                long sleep = (millisInHour - diff)+600000; // giving it an extra 10 minutes
                                logger.info("Finished "+count+" have to sleep "+((sleep/1000F)/60F)+" minutes");
                                Thread.sleep(sleep);
                                //System.out.println("Consumer slept: "+diff);
                            } catch (Exception e){} // do nothing
                        }
                        start = System.currentTimeMillis();
                    }
                    amzCalls++;
                    
                    ItemLookupResponse resp = ails.lookupData(ids, "Large", Condition.All, "All");
                    if (resp != null && resp.getItems() != null){
                        for (Items items : resp.getItems()){
                            if (items.getItem() != null){
                                for (Item item : items.getItem()){
                                    ItemAttributes ia = item.getItemAttributes();
                                    OfferSummary os = item.getOfferSummary();
                                    if (ia != null){
                                        String isbn = ia.getISBN();
                                        String sr = item.getSalesRank();
                                        /* if we are doing differences here is where it is
                                        if (ia.getListPrice() != null && ia.getListPrice().getAmount() != null){
                                            Float amzList = ia.getListPrice().getAmount().floatValue() / 100.0F;
                                            Float curList = bookIsbns.get(isbn);
                                            if (amzList != null && curList != null && amzList.floatValue() != curList.floatValue()){
                                                //System.out.println("Difference: "+amzList+" "+curList);
                                                //differences.add(new InvData(isbn, amzList, curList, ia.getTitle()));
                                            }
                                        }
                                        */
                                        String p = ia.getPublisher();
                                        String pds = ia.getPublicationDate();
                                        Date pd = null;
                                        try {
                                            pd = sdf.parse(pds);
                                        } catch (Exception e){
                                            try {
                                                pd = sdfnoday.parse(pds);
                                            } catch (Exception ex){}
                                        }
                                        if (p != null) p = p.replace("'", "\\'");
                                        if (sr != null){
                                            StringBuilder sb = new StringBuilder("update inventory_item set salesRank = ");
                                            sb.append(sr);
                                            sb.append(", companyRec = '");
                                            sb.append(p);
                                            sb.append("', lastAmazonUpdate = now()");
                                            if (pd != null){
                                                sb.append(", publish_date = '");
                                                sb.append(sdf.format(pd));
                                                sb.append(" 00:00:00'");
                                            }
                                            if (os != null){
                                                sb.append(", nightlyAmazonTotalNew = ");
                                                sb.append(os.getTotalNew());
                                                sb.append(", nightlyAmazonTotalUsed = ");
                                                sb.append(os.getTotalUsed());
                                                sb.append(", nightlyAmazonTotalCollectible = ");
                                                sb.append(os.getTotalCollectible());
                                                if (os.getLowestNewPrice() != null){
                                                    sb.append(", nightlyAmazonLowestNewPrice = ");
                                                    sb.append(os.getLowestNewPrice().getAmount().floatValue() / 100F);
                                                }
                                                if (os.getLowestUsedPrice() != null){
                                                    sb.append(", nightlyAmazonLowestUsedPrice = ");
                                                    sb.append(os.getLowestUsedPrice().getAmount().floatValue() / 100F);
                                                }
                                                if (os.getLowestCollectiblePrice() != null){
                                                    sb.append(", nightlyAmazonLowestCollectiblePrice = ");
                                                    sb.append(os.getLowestCollectiblePrice().getAmount().floatValue() / 100F);
                                                }
                                            }
                                            if (item.getLargeImage() != null){
                                                sb.append(", mediumImage = '");
                                                sb.append(item.getLargeImage());
                                                sb.append("'");
                                            } 
                                            if (item.getMediumImage() != null){
                                                sb.append(", smallImage = '");
                                                sb.append(item.getMediumImage());
                                                sb.append("'");
                                            }
                                            if (ia.getNumberOfPages() != null){
                                                sb.append(", numberOfPages = ");
                                                sb.append(ia.getNumberOfPages().intValue());
                                            }
                                            sb.append(" where isbn = '");
                                            sb.append(isbn);
                                            sb.append("'");
                                            updates.add(sb.toString());
                                        }
                                    }
                                    
                                    // categories
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
//                                            sb.append("', bccategory = '");
//                                            sb.append(catList.size() > 0 ? catList.get(0).replace("'", "\\'") : "");
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
                    tx.rollback();
                } finally {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e){}
                }
                
                end = System.currentTimeMillis();
                diff = end - start;
                
                if (count % 1000 == 0) {
                    logger.info("finished with "+count+" bookcountry lookups....");
                }
            }
            logger.info("Finished bookcountry...");
            /* 
            if (differences.size() > 0){
                StringBuilder m = new StringBuilder();
                m.append("Differences betweeen inventory and amazon list price: \n\n");
                for (InvData id : differences){
                    m.append(id.isbn);
                    m.append("  current: $");
                    m.append(id.curList);
                    m.append("  amazon: $");
                    m.append(id.amzList);
                    m.append("  title: ");
                    m.append(id.title);
                    m.append("\n");
                }
                 EmailNotification en = new EmailNotification();
                 en.sendNotification("megela@gmail.com", "List Price Differences", m.toString());
                 en.sendNotification("denise@bookcountryclearinghouse.com", "List Price Differences", m.toString());
                 
                 System.out.println("");
                 System.out.println("Differences size: "+differences.size());
            }
             */
        } catch (Exception e){
            logger.error("Could not get Amazon Sales Rank", e);
        }
        logger.info("Finished: "+Calendar.getInstance().getTime().toString());        
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
