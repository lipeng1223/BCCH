package com.bc.ejb.cron;

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

import com.amazon.xml.AWSECommerceService.Condition;
import com.amazon.xml.AWSECommerceService.Item;
import com.amazon.xml.AWSECommerceService.ItemAttributes;
import com.amazon.xml.AWSECommerceService.ItemLookupResponse;
import com.amazon.xml.AWSECommerceService.Items;
import com.bc.amazon.AmazonItemLookupSoap;
import com.bc.dao.BaseDao;
import com.bc.orm.InventoryItem;
import com.bc.util.Emailer;
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
   @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 00 4 ? * WED")
//    @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 00 16 ? * WED")
})
@ResourceAdapter("quartz-ra.rar")
public class ListPriceDifferencesMessageBean implements Job {

    private static Logger logger = Logger.getLogger(ListPriceDifferencesMessageBean.class);
    
    private static final int CALLS_PER_HOUR = 28800;
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        
        if (System.getProperty("bc.nocron") != null && System.getProperty("bc.nocron").equals("true")) {
            logger.error("************************ NOT running cron, bc.nocron is true");
            return;
        }
        
        try {
            // setup the threadcontext for audit
            ThreadContext.setContext(-1L, "cron", "listPriceDifferences");
            
            Map<String, InvDiffData> bookIsbns = new HashMap<String, InvDiffData>();
    
            BaseDao<InventoryItem> iidao = new BaseDao<InventoryItem>(InventoryItem.class);
            
            UserTransaction tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            //List<Object[]> results = iidao.getSession().createSQLQuery("select distinct isbn, list_price, id from inventory_item where skid = false and onhand > 0 and ROUND((UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(received_date)) / 86400, 1) < 1.0 and (char_length(isbn) = 10 or char_length(isbn) = 13)").
            List<Object[]> results = iidao.getSession().createSQLQuery("select count(distinct isbn) from inventory_item where skid = false and onhand > 0 and (char_length(isbn) = 10 or char_length(isbn) = 13)").
                addScalar("isbn", Hibernate.STRING).addScalar("list_price", Hibernate.FLOAT).addScalar("id", Hibernate.LONG).list();
            for (Object[] obarray : results){
                bookIsbns.put((String)obarray[0], new InvDiffData((String)obarray[0], (Float)obarray[1], (Long)obarray[2]));
            }
            iidao.flushAndClear();
            tx.commit();
    
            logger.info("Bookcountry ISBN's: "+bookIsbns.size());
            Calendar now = Calendar.getInstance();
            logger.info("Starting lookup in amazon: "+now.getTime().toString());
            
            List<String> bookLookup = new ArrayList<String>(bookIsbns.keySet());
            long start = System.currentTimeMillis(), end, diff = 1000;
            
            List<InvDiffData> differences = new ArrayList<InvDiffData>();
            AmazonItemLookupSoap ails = AmazonItemLookupSoap.getInstance();
            int millisInHour = 60*60*1000;
            int amzCalls = 0;
            while (bookLookup.size() > 0){
                if (amzCalls == CALLS_PER_HOUR){
                    amzCalls = 0;
                    end = System.currentTimeMillis();
                    logger.info("Finished "+CALLS_PER_HOUR+" amazon calls, it took: "+(((end-start)/1000F)/60F)+" minutes");
                    diff = end - start;
                    if (diff < millisInHour){ 
                        // sleep till the next hour
                        try {
                            long sleep = (millisInHour - diff)+600000; // giving it an extra 15 minutes
                            logger.info("Amz max reached, have to sleep "+((sleep/1000F)/60F)+" minutes");
                            Thread.sleep(sleep);
                            //System.out.println("Consumer slept: "+diff);
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
                }
                
                try {
                    ItemLookupResponse resp = ails.lookupData(ids, "Medium", Condition.All, "All");
                    if (resp != null && resp.getItems() != null){
                        for (Items items : resp.getItems()){
                            if (items.getItem() != null){
                                for (Item item : items.getItem()){
                                    ItemAttributes ia = item.getItemAttributes();
                                    if (ia != null){
                                        String isbn = ia.getISBN();
                                        if (ia.getListPrice() != null && ia.getListPrice().getAmount() != null){
                                            Float amzList = ia.getListPrice().getAmount().floatValue() / 100.0F;
                                            if (bookIsbns.containsKey(isbn)){
                                                Float curList = bookIsbns.get(isbn).curList;
                                                if (amzList != null && curList != null && amzList.floatValue() != curList.floatValue()){
                                                    //System.out.println("Difference: "+amzList+" "+curList);
                                                    differences.add(new InvDiffData(isbn, amzList, curList, ia.getTitle(), bookIsbns.get(isbn).id));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable t){
                    logger.error("Could not get info from amazon", t);
                } finally {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e){}
                }
                end = System.currentTimeMillis();
                diff = end - start;
            }
            logger.info("Finished difference lookups...");
    
            logger.info("Differences size: "+differences.size());
            if (differences.size() > 0){
                StringBuilder m = new StringBuilder();
                m.append("Differences betweeen inventory and amazon list price: <br><br>");
                m.append("<table cellpadding='3px'>");
                m.append("<tr><td><b>ISBN</b></td><td><b>Current</b></td><td><b>Amazon</b></td><td></td></tr>");
                for (InvDiffData idd : differences){
                    m.append("<tr><td>");
                    m.append(idd.isbn);
                    m.append("</td><td>$");
                    m.append(idd.curList);
                    m.append("</td><td>$");
                    m.append(idd.amzList);
                    m.append("</td><td>");
                    m.append("<a href=\"http://localhost:8080/secure/bookcountry/inventory!view.bc?id=");
                    //m.append("<a href=\"http://inventory.bookcountryclearinghouse.com/secure/bookcountry/inventory!view.bc?id=");
                    m.append(idd.id);
                    m.append("\">view</a>");
                    m.append("</td></tr>");
                }
                m.append("</table>");
                Emailer emailer = new Emailer();
                emailer.sendNotification("megela@gmail.com", "List Price Differences", m.toString(), true);
                emailer.sendNotification("teri@bookcountryclearinghouse.com", "List Price Differences", m.toString(), true);
                emailer.sendNotification("kelley@bookcountryclearinghouse.com", "List Price Differences", m.toString(), true);
                emailer.sendNotification("bob@bookcountryclearinghouse.com", "List Price Differences", m.toString(), true);
            }
            
        } catch (Exception e){
            logger.error("Could not get list price differences", e);
        }
        logger.info("Finished: "+Calendar.getInstance().getTime().toString());
    }
    
    
    private class InvDiffData {
        
        String isbn;
        String title;
        Float amzList, curList;
        Long id;
        
        public InvDiffData(String isbn, Float curList, Long id){
            this.isbn = isbn;
            this.curList = curList;
            this.id = id;
        }
       
        public InvDiffData(String isbn, Float amzList, Float curList, String title, Long id){
           this.isbn = isbn;
           this.amzList = amzList;
           this.curList = curList;
           this.title = title;
           this.id = id;
        }
    }
    
}
