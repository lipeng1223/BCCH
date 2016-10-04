package com.bc.ejb.cron;

import java.io.BufferedReader;
import java.io.File;
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

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.ResourceAdapter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.amazonaws.mws.*;
import com.amazonaws.mws.model.*;

import com.bc.dao.BaseDao;
import com.bc.orm.BellInventory;
import com.bc.orm.BellSku;
import com.bc.util.ThreadContext;
import java.io.*;
import java.util.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

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
   @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 0 4 * * ?")
//    @ActivationConfigProperty(propertyName="cronTrigger", propertyValue="0 0 16 * * ?")
})
@ResourceAdapter("quartz-ra.rar")
public class BellInventoryLoadMwsMessageBean implements Job {

    private static Logger logger = Logger.getLogger(BellInventoryLoadMessageBean.class);
    
    private static final String MWS_ENDPOINT = "https://mws.amazonservices.com";
    private static final String GEN_REPORT = "RequestReport";
    private static final String REPORTGEN_OPEN_LISTINGS = "_GET_MERCHANT_LISTINGS_DATA_BACK_COMPAT_";
    private static final String REPORTGEN_DONE = "_DONE_";
    
    private static final String MERCHANT_ID = "A2PTFCA406K9UV";
    private static final String MARKETPLACE_ID = "ATVPDKIKX0DER";
    private static final String AWS_KEY = "AKIAIXXZGB4UAEDFERYA";
    private static final String SECRET = "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";
    
    private static final String APP_NAME = "Bellwether Books";
    private static final String APP_VERSION = "2";
    
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        
        if (System.getProperty("bc.nocron") != null && System.getProperty("bc.nocron").equals("true")) {
            logger.error("************************ NOT running cron, bc.nocron is true");
            return;
        }
        
        logger.info("Running Bell Inventory load from MWS...");
        
        // setup the threadcontext for audit
        ThreadContext.setContext(-1L, "cron", "bellInventoryLoad");
        
        load(3, 1, true);
        
    }
    
    private void retry(Integer retries, Integer currentTry, Boolean generate){
        logger.info("Retry... "+retries);
        currentTry++;
        if (currentTry <= retries){
            load(retries, currentTry, generate);
        }
    }

    private void load(Integer retries, Integer currentTry, Boolean generate) {
        logger.info("load - retries: "+retries+" currentTry: "+currentTry+" generate: "+generate);
        
        /*
        if (true){
            // TESTING
            downloadAndImport("5843402618");
            return;
        }
        */
        
        String reportRequestId = null;
        String genStatus = null;
        if (generate){
            reportRequestId = generateReport();
            if (reportRequestId == null){
                retry(retries++, currentTry++, generate);
                return;
            }
            logger.debug("Generating Report...");
            genStatus = checkReport(reportRequestId);
            int sleepCycles = 0;
            while (!REPORTGEN_DONE.equals(genStatus)){
                try {
                    // sleep
                    sleepCycles++;
                    logger.info("Sleeping for 60 seconds");
                    Thread.sleep(60000);
                } catch (Exception e){};
                genStatus = checkReport(reportRequestId);
                if (sleepCycles >= 180){
                    logger.error("waited for 180 minutes, not waiting any more");
                }
            }
        } else {
            // TODO
            // just get the latest report?
        }
        if (!REPORTGEN_DONE.equals(genStatus)) return;
        downloadAndImport(reportRequestId);
        logger.debug("Finished LoadOrders");
    }

    private void downloadAndImport(String reportRequestId){
        String[] vals = new String[52];
        String[] tokens = new String[52];
        File tmp = null;
        try {
            
            MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
            config.setServiceURL(MWS_ENDPOINT);
            MarketplaceWebService service = new MarketplaceWebServiceClient(AWS_KEY, SECRET, APP_NAME, APP_VERSION, config);
            
            
            GetReportListRequest reportListRequest = new GetReportListRequest();
            reportListRequest.setMerchant(MERCHANT_ID);
            List<String> ids = new ArrayList<String>();
            ids.add(reportRequestId);
            IdList idList = new IdList(ids);
            reportListRequest.setReportRequestIdList(idList);
            GetReportListResponse reportListResponse = service.getReportList(reportListRequest);
            String reportId = null;
            if (reportListResponse.isSetGetReportListResult()) {
                GetReportListResult  getReportListResult = reportListResponse.getGetReportListResult();
                java.util.List<ReportInfo> reportInfoListList = getReportListResult.getReportInfoList();
                for (ReportInfo reportInfoList : reportInfoListList) {
                    if (reportInfoList.isSetReportRequestId()) {
                        if (reportInfoList.getReportRequestId().equals(reportRequestId)){
                            reportId = reportInfoList.getReportId();
                            logger.info("report id for report request is: "+reportId);
                            break;
                        }
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            // construct the file name
            sb.append(System.getProperty("java.io.tmpdir"));
            sb.append(File.separator);
            sb.append(new Long(System.currentTimeMillis()).toString());
            sb.append(".csv");
            tmp = new File(sb.toString());
            logger.error("CSV file: "+sb.toString());
            
            config = new MarketplaceWebServiceConfig();
            config.setServiceURL(MWS_ENDPOINT);
            config.setConnectionTimeout(300000);
            service = new MarketplaceWebServiceClient(AWS_KEY, SECRET, APP_NAME, APP_VERSION, config);
            GetReportRequest request = new GetReportRequest();
            request.setMerchant(MERCHANT_ID);
            request.setReportId(reportId);
            
            logger.info("Getting report "+reportId);
            OutputStream report = new FileOutputStream(tmp);
            request.setReportOutputStream( report );
            GetReportResponse response = service.getReport(request);
            logger.info("Have the report");
            
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
    

    private String checkReport(String reportRequestId){
        try {
            MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
            config.setServiceURL(MWS_ENDPOINT);
            MarketplaceWebService service = new MarketplaceWebServiceClient(AWS_KEY, SECRET, APP_NAME, APP_VERSION, config);
            
            GetReportRequestListRequest request = new GetReportRequestListRequest();
            request.setMerchant(MERCHANT_ID);
            request.setMaxCount(1);
            List<String> ids = new ArrayList<String>();
            ids.add(reportRequestId);
            IdList idList = new IdList(ids);
            request.setReportRequestIdList(idList);
            
            GetReportRequestListResponse response = service.getReportRequestList(request);
            String requestId = null, status = null;
            if (response.isSetGetReportRequestListResult()){
                GetReportRequestListResult requestReportResult = response.getGetReportRequestListResult();
                if (requestReportResult.isSetReportRequestInfoList()) {
                    for (ReportRequestInfo rri : requestReportResult.getReportRequestInfoList()){
                        if (rri.isSetReportProcessingStatus()){
                            logger.info("report "+reportRequestId+" status: "+rri.getReportProcessingStatus());
                            return rri.getReportProcessingStatus();
                        }
                    }
                }
            }
        } catch (Exception e){
            logger.error(e);
        }
        return null;
    }

    private String generateReport(){
        try {
            
            MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
            config.setServiceURL(MWS_ENDPOINT);
            MarketplaceWebService service = new MarketplaceWebServiceClient(AWS_KEY, SECRET, APP_NAME, APP_VERSION, config);
            
    		DatatypeFactory df = null;
            try {
                df = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                logger.error("exception", e);
                throw new RuntimeException(e);
            }
            Calendar now = Calendar.getInstance();
            now.add(Calendar.DAY_OF_MONTH, -15);
    		XMLGregorianCalendar startDate = df.newXMLGregorianCalendar(new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)));

            
            RequestReportRequest request = new RequestReportRequest()
		        .withMerchant(MERCHANT_ID)
		        .withReportType(REPORTGEN_OPEN_LISTINGS)
                .withStartDate(startDate);
            
            RequestReportResponse response = service.requestReport(request);
            String requestId = null, status = null;
            if (response.isSetRequestReportResult()) {
                RequestReportResult  requestReportResult = response.getRequestReportResult();
                if (requestReportResult.isSetReportRequestInfo()) {
                    ReportRequestInfo  reportRequestInfo = requestReportResult.getReportRequestInfo();
                    if (reportRequestInfo.isSetReportRequestId()) {
                        requestId = reportRequestInfo.getReportRequestId();
                    }
                    if (reportRequestInfo.isSetReportProcessingStatus()) {
                        status = reportRequestInfo.getReportProcessingStatus();
                    }
                }
            }
            logger.info("Request id: "+requestId);
            logger.info("Status: "+status);
            return requestId;
        } catch (Exception e){
            logger.error(e);
        }
        return null;
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
