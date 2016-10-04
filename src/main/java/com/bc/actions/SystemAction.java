package com.bc.actions;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;

import com.bc.ejb.cron.CategoryLoadMessageBean;
import com.bc.ejb.InventoryIsbnCheckSessionLocal;
import com.bc.ejb.InventoryIsbnCheckSession;
import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.ejb.UtilitySessionLocal;
import com.bc.orm.InventoryItem;
import com.bc.util.ActionRole;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@ParentPackage("bcpackage")
@Namespace("/secure")
@Results({
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="tomcat", location="/WEB-INF/jsp/system/tomcat.jsp"),
    @Result(name="tools", location="/WEB-INF/jsp/system/tools.jsp"),
    @Result(name="success", location="/WEB-INF/jsp/system/success.jsp"),
    @Result(name="memory", location="/WEB-INF/jsp/system/memory.jsp")
})
public class SystemAction extends BaseAction {
	
    private static final Logger logger = Logger.getLogger(SystemAction.class);

    @ActionRole({"SystemAdmin"})
    public String tomcatStatus(){
        return "tomcat";
    }
    
    @ActionRole({"SystemAdmin"})
    public String jbossMemory(){
        return "memory";
    }

    @ActionRole({"SystemAdmin"})
    public String tools(){
        return "tools";
    }
    
    @ActionRole({"SystemAdmin"})
    public String fixInventoryIsbns(){
         List<Long> titleChecks = getInventoryIsbnCheckSessionLocal().fixBookcountryIsbns();
         logger.info("Attempting to get title information for: "+titleChecks.size()+" items");
         InventoryItemSessionLocal iiSession = getInventoryItemSession();
         try {
            int count = 0;
            for (Long iiId : titleChecks){
                InventoryItem ii = iiSession.findById(iiId);
                AmazonLookup.getInstance().lookupData(ii, true);
                if (ii.getAmazonDataLoaded()){
                    iiSession.update(ii);
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e){}
            }
         } catch (Exception e){
             logger.error("Exception", e);
         }
         logger.info("Finished title information lookups from amazon");
         return "success";
    }
    
    
    @ActionRole({"SystemAdmin"})
    public String backOutOnhand(){
        UtilitySessionLocal uSession = getUtilitySession();
        uSession.backOutOnhand();
        return "success";
    }
    
    
    @ActionRole({"SystemAdmin"})
    public String fixInventoryCounts(){
        UtilitySessionLocal uSession = getUtilitySession();
        uSession.fixInventoryCounts();
        return "success";
    }

    @ActionRole({"SystemAdmin"})
    public String runCategoryCron(){
        try {
            SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();
            sched.start();
            JobDetail jobDetail = new JobDetail("categoryCronJbo",
                                                sched.DEFAULT_GROUP,
                                                CategoryLoadMessageBean.class);
            SimpleTrigger trigger = new SimpleTrigger("categoryCronJobTrigger",
                                                      sched.DEFAULT_MANUAL_TRIGGERS,
                                                      new Date(),
                                                      null,
                                                      0,
                                                      0L);
            sched.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            logger.error("Could not run category cron", e);
        }
        setSuccess(true);
        setMessage("Started off a category cron job");
        return "status";
    }
	
    public InventoryIsbnCheckSessionLocal getInventoryIsbnCheckSessionLocal() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (InventoryIsbnCheckSessionLocal)ctx.lookup(InventoryIsbnCheckSession.LocalJNDIStringNoLoader);
            }
            return (InventoryIsbnCheckSessionLocal)ctx.lookup(InventoryIsbnCheckSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup InventoryIsbnCheckSession", ne);
        }
        throw new RuntimeException("Could not lookup InventoryIsbnCheckSession");
    }

}
