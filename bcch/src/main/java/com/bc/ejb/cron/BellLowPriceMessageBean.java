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

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.jboss.annotation.ejb.ResourceAdapter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.amazon.xml.AWSECommerceService.Condition;
import com.amazon.xml.AWSECommerceService.ItemLookupResponse;
import com.bc.amazon.AmazonItemLookupSoap;
import com.bc.amazon.response.OfferFull;
import com.bc.amazon.response.OfferFull.OfferItem;
import com.bc.amazon.response.OfferFull.OfferSummary;
import com.bc.dao.BaseDao;
import com.bc.orm.BellInventory;
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
   @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 0 21 * * ?")
//    @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 0 9 * * ?")
})
@ResourceAdapter("quartz-ra.rar")
public class BellLowPriceMessageBean implements Job {

    private static Logger logger = Logger.getLogger(BellLowPriceMessageBean.class);
    
    private static final int CALLS_PER_HOUR = 28800;
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        
        if (System.getProperty("bc.nocron") != null && System.getProperty("bc.nocron").equals("true")) {
            logger.error("************************ NOT running cron, bc.nocron is true");
            return;
        }
        
        // setup the threadcontext for audit
        ThreadContext.setContext(-1L, "cron", "bellLowPrice");
        
        Map<String, Integer> bellIsbns = new HashMap<String, Integer>();

        BaseDao<BellInventory> bdao = new BaseDao<BellInventory>(BellInventory.class);
        
        List<String> results = bdao.getSession().createSQLQuery("select distinct isbn from bell_inventory where (noamazon is null or noamazon = false) and onhand > 0 and skid = false").
            addScalar("isbn", Hibernate.STRING).list();
        for (String s : results){
            bellIsbns.put(s, 0);
        }
        
        logger.info("Bellwether ISBN's: "+bellIsbns.size());
        Calendar now = Calendar.getInstance();
        logger.info("Starting lookup in amazon: "+now.getTime().toString());
        
        List<String> bellLookup = new ArrayList<String>(bellIsbns.keySet());
        long start = System.currentTimeMillis(), end, diff = 1000;
        
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
            
            List<String> updates = new ArrayList<String>();
            
            try {
                ItemLookupResponse resp = ails.lookupData(ids, "Offers", Condition.All, "All");
                if (resp != null && resp.getItems() != null){
                    OfferFull of = new OfferFull(resp);
                    List<OfferItem> items = of.getItems();
                    for (int j = 0; j < items.size(); j++){
                        OfferItem item = items.get(j);
                        if (item != null){
                            OfferSummary summary = item.getOfferSummary();
                            if (summary != null) {
                                // now update for this summary
                                StringBuilder sb = new StringBuilder();
                                sb.append("update bell_inventory set lowNew = ");
                                sb.append(summary.getLowestNewPriceAmmount().toString());
                                sb.append(", lowNewFormat = '");
                                sb.append(summary.getLowestNewPriceFormatted());
                                sb.append("', ");
                                sb.append("lowUsed = ");
                                sb.append(summary.getLowestUsedPriceAmmount().toString());
                                sb.append(", lowUsedFormat = '");
                                sb.append(summary.getLowestUsedPriceFormatted());
                                sb.append("', lowCollectible = ");
                                sb.append(summary.getLowestCollectiblePriceAmmount().toString());
                                sb.append(", lowCollectibleFormat = '");
                                sb.append(summary.getLowestCollectiblePriceFormatted());
                                sb.append("', lowRefurb = ");
                                sb.append(summary.getLowestRefurbishedPriceAmmount().toString());
                                sb.append(", lowRefurbFormat = '");
                                sb.append(summary.getLowestRefurbishedPriceFormatted());
                                sb.append("', lastAmzCheck = CURRENT_TIMESTAMP(), totalNew = ");
                                sb.append(summary.getTotalNew());
                                sb.append(", totalUsed = ");
                                sb.append(summary.getTotalUsed());
                                sb.append(", totalCollectible = ");
                                sb.append(summary.getTotalCollectible());
                                sb.append(", totalRefurb = ");
                                sb.append(summary.getTotalRefurbished());
                                sb.append(" where isbn = '");
                                sb.append(item.getAsin());
                                sb.append("'");
                                updates.add(sb.toString());

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
            for (String update : updates){
                bdao.getSession().createSQLQuery(update).executeUpdate();
            }
            updates.clear();
            
            // divide the low prices by 100
            String s = "update bell_inventory set lowNew = lowNew / 100, lowUsed = lowUsed / 100, lowCollectible = lowCollectible / 100, lowRefurb = lowRefurb / 100";
            bdao.getSession().createSQLQuery(s).executeUpdate();
            s = "update bell_sku set lowest = false";
            bdao.getSession().createSQLQuery(s).executeUpdate();
            s = "update bell_sku as bs, bell_inventory as bi set bs.lowest = true where bs.inventory_id = bi.id and bs.sell_price <= bi.lowUsed";
            bdao.getSession().createSQLQuery(s).executeUpdate();
            
            end = System.currentTimeMillis();
            diff = end - start;
            
            if (count % 1000 == 0){
                logger.info("finished "+count+" bellwether low price isbns....");
            }
        }
        
        logger.info("Finished bellwether load low prices...");
        
        now = Calendar.getInstance();
        logger.info("Finished: "+now.getTime().toString());
    }
    
}
