package com.bc.amazon;

import java.net.HttpURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

public class AmazonInventory {

    private static final Logger logger =
        Logger.getLogger("com.sc.util.amazon.AmazonInventory");
    
    public static final String GET_REPORT_STATUS = "https://secure.amazon.com/exec/panama/seller-admin/manual-reports/get-report-status";
    public static final String GENERATE_REPORT_NOW = "https://secure.amazon.com/exec/panama/seller-admin/manual-reports/generate-report-now";
    public static final String DOWNLOAD_REPORT = "https://secure.amazon.com/exec/panama/seller-admin/download/report";
    public static final String ADD_MODIFY_DELETE = "https://secure.amazon.com/exec/panama/seller-admin/catalog-upload/add-modify-delete";
    public static final String MODIFYONLY = "https://secure.amazon.com/exec/panama/seller-admin/catalog-upload/modify-only";
    public static final String PURGE_REPLACE = "https://secure.amazon.com/exec/panama/seller-admin/catalog-upload/purge-replace";
    public static final String GET_BATCHES = "https://secure.amazon.com/exec/panama/seller-admin/catalog-upload/get-batches";
    public static final String ERRORLOG = "https://secure.amazon.com/exec/panama/seller-admin/download/errorlog";
    public static final String QUICKFIX = "https://secure.amazon.com/exec/panama/seller-admin/download/quickfix";
    public static final String GET_PENDING_UPLOADS_COUNT = "https://secure.amazon.com/exec/panama/seller-admin/manual-reports/get-pending-uploads-count";
    public static final String BATCH_REFUND = "https://secure.amazon.com/exec/panama/seller-admin/catalog-upload/batch-refund";

    public static final String REPORT_OPEN_LISTINGS = "OpenListings";
    public static final String REPORT_ORDER = "Order";
    public static final String REPORT_BATCH_REFUND = "BatchRefund";
    public static final String REPORT_OPEN_LISTINGS_LITE = "OpenListingsLite";
    public static final String REPORT_OPEN_LISTINGS_LITER = "OpenListingsLiter";
    public static final String REPORT_SOLDLISTINGS = "SoldListings";

    public static final String REPORTGEN_OPEN_LISTINGS = "OpenListings";
    public static final String REPORTGEN_ORDER = "Order";
    public static final String REPORTGEN_SOLDLISTINGS = "SoldListings";
    public static final String REPORTGEN_OPEN_LISTINGS_LITE = "OpenListingsLite";
    public static final String REPORTGEN_OPEN_LISTINGS_LITER = "OpenListingsLiter";

    public static void addHeaders(HttpMethodBase getOrPut, String username, String password) {
        StringBuilder sb = new StringBuilder(username);
        sb.append(":");
        sb.append(password);
        String userpass = new String(Base64.encodeBase64(sb.toString().getBytes()));
        getOrPut.addRequestHeader("Authorization", "Basic "+userpass);
        getOrPut.addRequestHeader("Content Type", "text/xml");
        getOrPut.addRequestHeader("Cookie", "x-main=YvjPkwfntqDKun0QEmVRPcTTZDMe?Tn?; ubid-main=002-8989859-9917520; ubid-tacbus=019-5423258-4241018;x-tacbus=vtm4d53DvX@Sc9LxTnAnxsFL3DorwxJa; ubid-tcmacb=087-8055947-0795529; ubid-ty2kacbus=161-5477122-2773524; session-id=087-178254-5924832; session-id-time=950660664");
    }

    public static boolean checkLogin(String username, String password){
        try {
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod(GET_REPORT_STATUS);
            addHeaders(get, username, password);
            get.addRequestHeader("NumberOfReports", "1");
            get.addRequestHeader("ReportName", REPORT_OPEN_LISTINGS);
            // execute the GET
            int responseCode = client.executeMethod( get );
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }
            return true;
        } catch (Throwable t){} // do nothing, could not login
        return false;
    }

    public static void main(String[] args){
        try {
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod(GET_REPORT_STATUS);
            StringBuilder sb = new StringBuilder("sales@bellwetherbooks.net");
            sb.append(":");
            sb.append("bell2008");
            String userpass = new String(Base64.encodeBase64(sb.toString().getBytes()));
            get.addRequestHeader("Authorization", "Basic "+userpass);
            get.addRequestHeader("Content Type", "text/xml");
            get.addRequestHeader("Cookie", "x-main=YvjPkwfntqDKun0QEmVRPcTTZDMe?Tn?; ubid-main=002-8989859-9917520; ubid-tacbus=019-5423258-4241018;x-tacbus=vtm4d53DvX@Sc9LxTnAnxsFL3DorwxJa; ubid-tcmacb=087-8055947-0795529; ubid-ty2kacbus=161-5477122-2773524; session-id=087-178254-5924832; session-id-time=950660664");
            get.addRequestHeader("NumberOfReports", "1");
            get.addRequestHeader("ReportName", REPORT_OPEN_LISTINGS);
            // execute the GET
            client.executeMethod( get );
            // print the status and response
            String reports = get.getResponseBodyAsString();
            System.out.println("reports: "+reports);
        } catch (Throwable t){
            t.printStackTrace();
        } // do nothing, could not login
    }
    
}
