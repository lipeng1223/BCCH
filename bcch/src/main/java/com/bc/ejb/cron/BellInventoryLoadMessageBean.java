package com.bc.ejb.cron;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.ResourceAdapter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bc.dao.BaseDao;
import com.bc.orm.BellInventory;
import com.bc.orm.BellSku;
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

   @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 0 4 * * ?")
 */
@MessageDriven(activationConfig = {
   @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 10 4 * * ?")
//    @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 10 14 * * ?")
})
@ResourceAdapter("quartz-ra.rar")
public class BellInventoryLoadMessageBean implements Job {

    private static Logger logger = Logger.getLogger(BellInventoryLoadMessageBean.class);
    
    private static final String GET_REPORT_STATUS = "https://secure.amazon.com/exec/panama/seller-admin/manual-reports/get-report-status";
    private static final String GENERATE_REPORT_NOW = "https://secure.amazon.com/exec/panama/seller-admin/manual-reports/generate-report-now";
    private static final String DOWNLOAD_REPORT = "https://secure.amazon.com/exec/panama/seller-admin/download/report";
    private static final String ADD_MODIFY_DELETE = "https://secure.amazon.com/exec/panama/seller-admin/catalog-upload/add-modify-delete";
    private static final String MODIFYONLY = "https://secure.amazon.com/exec/panama/seller-admin/catalog-upload/modify-only";
    private static final String PURGE_REPLACE = "https://secure.amazon.com/exec/panama/seller-admin/catalog-upload/purge-replace";
    private static final String GET_BATCHES = "https://secure.amazon.com/exec/panama/seller-admin/catalog-upload/get-batches";
    private static final String ERRORLOG = "https://secure.amazon.com/exec/panama/seller-admin/download/errorlog";
    private static final String QUICKFIX = "https://secure.amazon.com/exec/panama/seller-admin/download/quickfix";
    private static final String GET_PENDING_UPLOADS_COUNT = "https://secure.amazon.com/exec/panama/seller-admin/manual-reports/get-pending-uploads-count";
    private static final String BATCH_REFUND = "https://secure.amazon.com/exec/panama/seller-admin/catalog-upload/batch-refund";

    private static final String REPORT_OPEN_LISTINGS = "OpenListings";
    private static final String REPORT_ORDER = "Order";
    private static final String REPORT_BATCH_REFUND = "BatchRefund";
    private static final String REPORT_OPEN_LISTINGS_LITE = "OpenListingsLite";
    private static final String REPORT_OPEN_LISTINGS_LITER = "OpenListingsLiter";
    private static final String REPORT_SOLDLISTINGS = "SoldListings";

    private static final String REPORTGEN_OPEN_LISTINGS = "OpenListings";
    private static final String REPORTGEN_ORDER = "Order";
    private static final String REPORTGEN_SOLDLISTINGS = "SoldListings";
    private static final String REPORTGEN_OPEN_LISTINGS_LITE = "OpenListingsLite";
    private static final String REPORTGEN_OPEN_LISTINGS_LITER = "OpenListingsLiter";
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        
        if (true){
            // NOT RUNNING THIS ANYMORE - AIM IS DEPRECATED
            return;
        }
        
        
        if (System.getProperty("bc.nocron") != null && System.getProperty("bc.nocron").equals("true")) {
            logger.error("************************ NOT running cron, bc.nocron is true");
            return;
        }
        
        logger.info("Running Bell Inventory load...");
        
        // setup the threadcontext for audit
        ThreadContext.setContext(-1L, "cron", "bellInventoryLoad");
        
        load("sales@bellwetherbooks.net", "Dramatic753", 3, 1, true);
        
    }
    
    private void retry(String username, String password, Integer retries, Integer currentTry, Boolean generate){
        logger.info("Retry... "+retries);
        currentTry++;
        if (currentTry <= retries){
            load(username, password, retries, currentTry, generate);
        }
    }

    private void load(String username, String password, Integer retries, Integer currentTry, Boolean generate) {
        logger.info("load - retries: "+retries+" currentTry: "+currentTry+" generate: "+generate);
        String currentReportId = checkReport(username, password, REPORT_OPEN_LISTINGS);
        if (currentReportId == null){
            retry(username, password, retries++, currentTry++, generate);
            return;
        }
        if (generate){
            if (!generateReport(username, password, REPORTGEN_OPEN_LISTINGS)){
                retry(username, password, retries++, currentTry++, generate);
                return;
            }
            logger.debug("Generating Report...");
            while (currentReportId.equals(checkReport(username, password, REPORT_OPEN_LISTINGS))){
                try {
                    // sleep
                    logger.error("Sleeping for 30 seconds");
                    Thread.sleep(30000);
                } catch (Exception e){};
            }
        }
        downloadAndImport(username, password, checkReport(username, password, REPORT_OPEN_LISTINGS));
        logger.debug("Finished LoadOrders for: "+username);
    }

    private void addHeaders(HttpMethodBase getOrPut, String username, String password) {
        StringBuilder sb = new StringBuilder(username);
        sb.append(":");
        sb.append(password);
        String userpass = new String(Base64.encodeBase64(sb.toString().getBytes()));
        getOrPut.addRequestHeader("Authorization", "Basic "+userpass);
        getOrPut.addRequestHeader("Content Type", "text/xml");
        getOrPut.addRequestHeader("Cookie", "x-main=YvjPkwfntqDKun0QEmVRPcTTZDMe?Tn?; ubid-main=002-8989859-9917520; ubid-tacbus=019-5423258-4241018;x-tacbus=vtm4d53DvX@Sc9LxTnAnxsFL3DorwxJa; ubid-tcmacb=087-8055947-0795529; ubid-ty2kacbus=161-5477122-2773524; session-id=087-178254-5924832; session-id-time=950660664");
    }
    
    private void downloadAndImport(String username, String password, String reportId){
        String[] vals = new String[52];
        String[] tokens = new String[52];
        File tmp = null;
        try {
            StringBuilder sb = new StringBuilder();
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod("https://secure.amazon.com/exec/panama/seller-admin/download/report");
            addHeaders(get, username, password);
            get.addRequestHeader("ReportID", reportId);
            // execute the GET
            client.executeMethod( get );
            // construct the file name
            sb.append(System.getProperty("java.io.tmpdir"));
            sb.append(File.separator);
            sb.append(new Long(System.currentTimeMillis()).toString());
            sb.append(".csv");
            tmp = new File(sb.toString());
            BufferedInputStream bis = new BufferedInputStream(get.getResponseBodyAsStream());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmp));
            byte[] buff = new byte[500000];
            int read = bis.read(buff);
            while (read != -1){
                bos.write(buff, 0, read);
                read = bis.read(buff);
            }
            bis.close();
            bos.flush();
            bos.close();

            logger.info("Importing from: "+tmp.toString());
            // now to import the csv
            BufferedReader in = new BufferedReader(new FileReader(tmp));

            String line = in.readLine(); // first line, should be header
            if (line == null) {
                return;
            }
            line = in.readLine();

            UserTransaction tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            BaseDao<BellInventory> bdao = new BaseDao<BellInventory>(BellInventory.class);
            
            // first things first, set all listed down to 0
            bdao.getSession().createSQLQuery("update bell_inventory set listed = 0").executeUpdate();
            bdao.getSession().createSQLQuery("update bell_sku set listed = 0").executeUpdate();
            tx.commit();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            boolean errorLine = false;
            int count = 0;
            List<BellSku> newSku = new ArrayList<BellSku>();
            List<ListedData> listedData = new ArrayList<ListedData>();
            while (line != null){
                count++;
                StringTokenizer st = new StringTokenizer(line, "\t", true);
                for (int i = 0; i < tokens.length; i++){
                    tokens[i] = "\t";
                }
                int c = 0;
                while (st.hasMoreTokens()){
                    String s = st.nextToken();
                    tokens[c++] = s;
                }
                c = 0;
                if (errorLine) {
                    c = 11;
                }
                for (int i = 0; i < tokens.length; i++){
                    if (c >= tokens.length){
                        break;
                    }
                    if (!tokens[i].equals("\t") && i+1 < tokens.length && tokens[i+1].equals("\t")){
                        if (errorLine){
                            vals[c] = vals[c]+"\n"+tokens[i];
                            c++;
                            errorLine = false;
                        } else {
                            vals[c++] = tokens[i];
                        }
                    } else if (tokens[i].equals("\t") && i+1 < tokens.length && tokens[i+1].equals("\t")){
                        vals[c++] = "";
                    }
                }

                
                String isbn = vals[22];
                String sku = vals[3];
                Float sellPrice = 0F;
                Timestamp ldate = null;
                Integer listed = 0;
                Integer cond = 0;
                try {
                    try {
                        sellPrice = new Float(vals[4]);
                    } catch (NumberFormatException nfe){
                        sellPrice = 0F;
                    }
                    try {
                        listed = new Integer(vals[5]);
                    } catch (NumberFormatException nfe){
                        listed = 0;
                    }
                        
                    try {
                        ldate = (new Timestamp(sdf.parse(vals[6]).getTime()));
                    } catch (Exception e){
                        logger.error("bad date: "+vals[6]);
                    }
                    try {
                        cond = new Integer(vals[12]);
                    } catch (NumberFormatException nfe){
                        cond = 0;
                    }
                } catch (Exception e){
                    logger.info("ERROR line: "+line);

                    /*
                    logger.info("Error Line:");
                    logger.info(line);
                    logger.info("");
                    for (int i = 0; i < vals.length; i++){
                        logger.info("vals["+i+"] = "+vals[i]);
                    }
                    */
                    
                    // This is where the item-note has \n in it
                    errorLine = true;
                    line = in.readLine();
                    continue;
                }
                
                listedData.add(new ListedData(isbn, sku, sellPrice, ldate, listed, cond));
                
                line = in.readLine();
                errorLine = false;
            }
            in.close();

            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            for (ListedData ld : listedData){
                sb = new StringBuilder();
                sb.append("update bell_inventory set sell_price = ");
                sb.append(ld.getSellPrice());
                sb.append(", listed = ");
                sb.append(ld.getListed());
                if (ld.getLastListDate() != null){
                    sb.append(", last_list_date = '");
                    sb.append(msdf.format(ld.getLastListDate()));
                    sb.append("'");
                }
                sb.append(" where isbn = '");
                sb.append(ld.getIsbn());
                sb.append("'");
                //logger.info(sb.toString());
                int num = bdao.getSession().createSQLQuery(sb.toString()).executeUpdate();
                if (num == 0){
                    logger.info("not found: "+ld.getIsbn());
                    continue;
                }
                
                sb = new StringBuilder();
                sb.append("update bell_sku set sell_price = ");
                sb.append(ld.getSellPrice());
                sb.append(", listed = ");
                sb.append(ld.getListed());
                sb.append(", bellcondition = '");
                sb.append(ld.getCondition());
                sb.append("' where isbn = '");
                sb.append(ld.getIsbn());
                sb.append("' and sku = '");
                sb.append(ld.getSku());
                sb.append("'");
                //logger.info(sb.toString());
                num = bdao.getSession().createSQLQuery(sb.toString()).executeUpdate();
                
                if (num == 0){
                    // new bs
                    BellSku bs = new BellSku();
                    bs.setSku(ld.getSku());
                    bs.setIsbn(ld.getIsbn());
                    bs.setListed(ld.getListed());
                    bs.setBellcondition(ld.getCondition());
                    bs.setSellPrice(ld.getSellPrice());
                    newSku.add(bs);
                }
            }
            tx.commit();
            
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            // create new bell skus
            logger.info("New Sku's: "+newSku.size());
            BaseDao<BellSku> bsdao = new BaseDao<BellSku>(BellSku.class);
            for (BellSku bs : newSku){
                logger.info("new sku: "+bs.getSku());
                bsdao.create(bs, null);
            }
            bsdao.flushAndClear();
            tx.commit();

            logger.info("Updating inventory...");
            tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
            tx.setTransactionTimeout(600);
            tx.begin();
            bsdao.getSession().createSQLQuery("update bell_sku as bs set bs.inventory_id = (select bi.id from bell_inventory as bi where bi.isbn = bs.isbn)").executeUpdate();
            bsdao.getSession().createSQLQuery("update bell_inventory as bi set bi.listed = (select sum(bs.listed) from bell_sku as bs where bi.id = bs.inventory_id)").executeUpdate();
            tx.commit();
            
            logger.info("Finished.");

        } catch (Exception e){
            logger.error("Error processing: ", e);

            for (int i = 0; i < vals.length; i++){
                logger.info(i+" "+vals[i]);
            }

        } finally {
            try {
                // delete the tmp file
                tmp.delete();
            } catch (Exception e){}
        }
    }
    

    private String checkReport(String username, String password, String report){
        try {
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod(GET_REPORT_STATUS);
            addHeaders(get, username, password);
            get.addRequestHeader("NumberOfReports", "1");
            get.addRequestHeader("ReportName", report);
            // execute the GET
            client.executeMethod( get );

            // print the status and response
            String reports = get.getResponseBodyAsString();
            logger.info("reports: "+reports);
            if (reports != null){
                int loc = reports.indexOf("reportid=");
                if (loc > 0){
                    loc+=9;
                    String end = reports.substring(loc);
                    StringTokenizer st = new StringTokenizer(end, " ");
                    String rid = st.nextToken();
                    logger.info("Current Report: "+rid);
                    return rid;
                }
            }
        } catch (Exception e){
            logger.error(e);
        }
        return null;
    }

    private boolean generateReport(String username, String password, String report){
        try {
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod(GENERATE_REPORT_NOW);
            addHeaders(get, username, password);
            get.addRequestHeader("ReportName", report);
            get.addRequestHeader("NumberOfDays", "15");
            // execute the GET
            client.executeMethod( get );
            if (get.getResponseBodyAsString().contains("SUCCESS")){
                return true;
            }
            logger.info(get.getResponseBodyAsString());
        } catch (Exception e){
            logger.error(e);
        }
        return false;
    }
    

    private class ListedData {
        
        private String isbn;
        private String sku;
        private Float sellPrice;
        private Timestamp lastListDate;
        private Integer listed;
        private Integer condition;
        
        public ListedData(String isbn,
                          String sku,
                          Float sellPrice,
                          Timestamp lastListDate,
                          Integer listed,
                          Integer condition)
        {
            this.isbn = isbn;
            this.sku = sku;
            this.sellPrice = sellPrice;
            this.lastListDate = lastListDate;
            this.listed = listed;
            this.condition = condition;
        }

        public Integer getCondition() {
            return condition;
        }

        public void setCondition(Integer condition) {
            this.condition = condition;
        }

        public String getIsbn() {
            return isbn.replaceAll("'", "''");
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public Timestamp getLastListDate() {
            return lastListDate;
        }

        public void setLastListDate(Timestamp lastListDate) {
            this.lastListDate = lastListDate;
        }

        public Integer getListed() {
            return listed;
        }

        public void setListed(Integer listed) {
            this.listed = listed;
        }

        public Float getSellPrice() {
            return sellPrice;
        }

        public void setSellPrice(Float sellPrice) {
            this.sellPrice = sellPrice;
        }

        public String getSku() {
            return sku.replaceAll("'", "''");
        }

        public void setSku(String sku) {
            this.sku = sku;
        }
        
    }
    
}
