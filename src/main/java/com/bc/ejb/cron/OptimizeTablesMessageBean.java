package com.bc.ejb.cron;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.ResourceAdapter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bc.dao.BaseDao;
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
   @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 30 3 * * ?")
//    @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 30 15 * * ?")
})
@ResourceAdapter("quartz-ra.rar")
public class OptimizeTablesMessageBean implements Job {

    private static Logger logger = Logger.getLogger(OptimizeTablesMessageBean.class);

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        
        if (System.getProperty("bc.nocron") != null && System.getProperty("bc.nocron").equals("true")) {
            logger.error("************************ NOT running cron, bc.nocron is true");
            return;
        }
        
        // setup the threadcontext for audit
        ThreadContext.setContext(-1L, "cron", "optimizeTables");

        List<String> tableNames = new ArrayList<String>();
        tableNames.add("bell_cost");
        tableNames.add("bell_customer");
        tableNames.add("bell_customer_shipping");
        tableNames.add("bell_inventory");
        tableNames.add("bell_order");
        tableNames.add("bell_order_item");
        tableNames.add("bell_received");
        tableNames.add("bell_received_item");
        tableNames.add("bell_sku");
        tableNames.add("bell_vendor");
        tableNames.add("break_received");
        tableNames.add("break_received_item");
        tableNames.add("category");
        tableNames.add("customer");
        tableNames.add("customer_order");
        tableNames.add("customer_order_item");
        tableNames.add("customer_shipping");
        tableNames.add("inventory_item");
        tableNames.add("manifest");
        tableNames.add("manifest_item");
        tableNames.add("publisher");
        tableNames.add("publisher_imprint");
        tableNames.add("received");
        tableNames.add("received_item");
        tableNames.add("user");
        tableNames.add("userrole");
        tableNames.add("vendor");
        tableNames.add("vendor_skid_type");
        
        BaseDao<InventoryItem> iidao = new BaseDao<InventoryItem>(InventoryItem.class);

        Calendar now = Calendar.getInstance();

        try {
            for (String name : tableNames){
                logger.info("optimizing table: "+name);
                
                UserTransaction tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
                tx.setTransactionTimeout(600);
                tx.begin();
                iidao.getSession().createSQLQuery("optimize table "+name).executeUpdate();
                tx.commit();
                
            }
            
            // update inventory item cost in case it did not get set
            String iiUpdate = "update inventory_item as ii set ii.received_price = (select ri.cost from received_item as ri where ri.inventory_item_id = ii.id order by ri.createTimeBc desc limit 1) where (ii.received_price is null or ii.received_price = 0)";
            UserTransaction tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iidao.getSession().createSQLQuery(iiUpdate).executeUpdate();
            tx.commit();

            // update the date on the received_item if it was not set
            iiUpdate = "update received_item as ri set ri.date = ri.createTimeBc where ri.date is null";
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iidao.getSession().createSQLQuery(iiUpdate).executeUpdate();
            tx.commit();
            
            // update the receiveddate on the inventory item if it was not set
            iiUpdate = "update inventory_item as ii set ii.received_date = (select ri.date from received_item as ri where ri.inventory_item_id = ii.id order by ri.date desc limit 1) where ii.received_date is null";
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iidao.getSession().createSQLQuery(iiUpdate).executeUpdate();
            tx.commit();
            
            // update the inventory item sell price if it was not set somehow
            iiUpdate = "update inventory_item as ii set ii.selling_price = (select ri.sell_price from received_item as ri where ri.inventory_item_id = ii.id order by createTimeBc desc limit 1) where ii.selling_price is null and (select ri.sell_price from received_item as ri where ri.inventory_item_id = ii.id order by createTimeBc desc limit 1) > 0";
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iidao.getSession().createSQLQuery(iiUpdate).executeUpdate();
            tx.commit();

            // update customer order items cost in case that is missing
            String coiUpdate = "update customer_order_item as coi, inventory_item as ii set coi.cost = ii.received_price where coi.inventory_item_id = ii.id and coi.cost is null";
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iidao.getSession().createSQLQuery(coiUpdate).executeUpdate();
            tx.commit();
            
            // make sure receive_item createTimeBc is set
            
            String riUpdate = "update received_item as ri, received as r set ri.createTimeBc = r.createTimeBc where ri.received_id = r.id and ri.createTimeBc is null and r.createTimeBc is not null";
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iidao.getSession().createSQLQuery(riUpdate).executeUpdate();
            tx.commit();
            
            // customer order item totalPrice update
            coiUpdate = "update customer_order_item as coi set coi.totalPrice = ( (coi.price*coi.filled) - ( (coi.price*coi.filled) * (coi.discount/100.0) ) ) where coi.totalPrice != ( (coi.price*coi.filled) - ( (coi.price*coi.filled) * (coi.discount/100.0) ) )";
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iidao.getSession().createSQLQuery(coiUpdate).executeUpdate();
            tx.commit();

            coiUpdate = "update customer_order_item as coi, customer_order as co set coi.extended = ( coi.cost * coi.filled ) where coi.extended != ( coi.cost * coi.filled ) and coi.customer_order_id = co.id and co.posted = true";
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            iidao.getSession().createSQLQuery(coiUpdate).executeUpdate();
            tx.commit();

        } catch (Exception e){
            logger.error("Could not optimize tables", e);
        }
        
        // making sure totalExtended gets set on orders
        try {
            StringBuilder sb = new StringBuilder("update customer_order as co set co.totalExtended = ");
            sb.append("(select coalesce(sum( coi.cost ), 0) from customer_order_item as coi where coi.customer_order_id = co.id) ");
            sb.append("where ");
            sb.append("(co.totalExtended is null or co.totalExtended = 0) and  ");
            sb.append("(select coalesce(sum( coi.cost ), 0) from customer_order_item as coi where coi.customer_order_id = co.id) > 0");
            
            logger.info("Updating any orders that don't have totalExtended but have item costs set");
            UserTransaction tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            int updated = iidao.getSession().createSQLQuery(sb.toString()).executeUpdate();
            tx.commit();
            logger.info("Updated: "+updated+" orders for totalExtended missing");
        } catch (Exception e){
            logger.error("Could not make sure that totalExtended is set on orders");
        }
        
        logger.info("Finished: "+now.getTime().toString());
    }
}
