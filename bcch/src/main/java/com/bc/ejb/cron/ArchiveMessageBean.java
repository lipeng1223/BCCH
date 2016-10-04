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
    
    "0 0 12 * * ?" - which is every day at 12 px 
    
    "0 0/1 * * * ?" activate every minute
    
    http://www.quartz-scheduler.org/docs/tutorial/TutorialLesson06.html
    
 */
@MessageDriven(activationConfig = {
   @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 0 22 1 * ?")
//    @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 0 10 1 * ?")
})
@ResourceAdapter("quartz-ra.rar")
public class ArchiveMessageBean implements Job {

    private static Logger logger = Logger.getLogger(ArchiveMessageBean.class);
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        
        if (System.getProperty("bc.nocron") != null && System.getProperty("bc.nocron").equals("true")) {
            logger.error("************************ NOT running cron, bc.nocron is true");
            return;
        }
        
        // setup the threadcontext for audit
        ThreadContext.setContext(-1L, "cron", "monthlyArchive");
        Calendar now = Calendar.getInstance();
 
        // look at anything in receiving or orders that are more than 2 years old and put them into archive tables
        
        
        
        logger.info("Finished: "+now.getTime().toString());
    }
}
