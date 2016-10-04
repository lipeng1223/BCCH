package com.bc.ejb.cron;

import java.io.File;
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
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
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
    
 */
@MessageDriven(activationConfig = {
   @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 0 3 * * ?")
//    @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 0 15 * * ?")
})
@ResourceAdapter("quartz-ra.rar")
public class CleanExportsMessageBean implements Job {

    private static Logger logger = Logger.getLogger(CleanExportsMessageBean.class);

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Calendar now = Calendar.getInstance();
        logger.info("Running cleanup of the exports directory "+now.getTime().toString());
        
        Configuration config = ConfigurationManager.getConfiguration("inventory");
        String dir = config.getProperty("exportfilestore", "exportstore", "general");
        File exportDir = new File(dir);
        int count = 0;
        if (exportDir.exists() && exportDir.isDirectory()){
            for (File f : exportDir.listFiles()){
                try {
                    f.delete();
                    count++;
                } catch (Exception e){
                    logger.error("Could not delete old export: "+f.getName());
                }
            }
        }
        logger.info("Deleted "+count+" old export files");
        now = Calendar.getInstance();
        logger.info("Finished: "+now.getTime().toString());
    }
}
