package com.bc.actions;

import com.bc.ejb.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.PrincipalAware;
import org.apache.struts2.interceptor.PrincipalProxy;
import org.apache.struts2.interceptor.SessionAware;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bc.ejb.bellwether.*;
import com.bc.excel.ExcelExtraDataWriter;
import com.bc.excel.ExcelReportExporter;
import com.bc.struts.QueryInput;
import com.bc.struts.QueryResults;
import com.bc.util.Search;
import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport implements ParameterAware, PrincipalAware, SessionAware {
    
    public static final String UNAUTHORIZED = "unauthorized";

    protected Long id; // all actions will need an id for input / output
    protected String result; // for possible data output
    
    protected static Logger logger = Logger.getLogger(BaseAction.class);
    protected Map<String,String[]> parameters;
    protected QueryInput queryInput = new QueryInput();
    protected QueryResults queryResults;  // Needed for queryresults.jsp
    protected String jsonResults;
    private Integer start = 0; // page offset
    private Integer limit = 25; // page size
    private String groupBy; // grouping 
    private String dir; // sort direction
    private String sort; // sort column
    private String searchString; // global search string, menu search
    protected String message; // used by status.jsp
    protected Boolean success;
    protected PrincipalProxy principalProxy;
    protected Map sessionMap;
    protected Boolean exportToExcel = false;
    protected Boolean exportBulkToExcel = false;
    protected Boolean exportCountToExcel = false;
    protected Boolean exportWithItemsToExcel = false;
    protected Boolean exportLimitExceeded = false;
    protected ExcelReportExporter exporter;
    protected ExcelExtraDataWriter extraDataWriter = null;
    private String excelExportFileName = "Export";
    private String excelExportSheetName = "Export";
    private String[] exportColumns;
    private String[] exportColumnNames;
    private Integer startRow;
    
    private String defaultFilter;
    private String defaultFilters;
    private String defaultFilterCol;

    private String filterCol;
    private String filterString;
    private Long filterId;
    
    protected Search search;
    protected Map<String, String> searchNames = new TreeMap<String, String>(); 
    
    // jasper report data
    private JRAbstractBeanDataSource jasperDatasource;
    private HashMap jasperParamMap;
    private String jasperFilename;
    private String jasperReportName;
    private String jasperExportType;
    
    @SuppressWarnings("unchecked")
    public void setParameters(Map params) {
        this.parameters = params;
        boolean done = false;
        int count = 0;
        try {
            checkParamMap(params, "filter");
            // This will only allow for up to 10 table level filters
            for (int i = 0; i < 10; i++){
                checkParamMap(params, "tablefilter"+i);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public String getConfigProperty(String name, String defaultValue){
        return getConfigProperty(name, defaultValue, "general");
    }
    public String getConfigProperty(String name, String defaultValue, String category){
        Configuration config = ConfigurationManager.getConfiguration("inventory");
        return config.getProperty(name, defaultValue, category);
    }
    
    private void checkParamMap(Map params, String startString) throws Exception {
        boolean done = false;
        int count = 0;
        while (!done){
            if (params.containsKey(startString+"["+count+"][field]")){
                HashMap<String, Object> fvals = new HashMap<String, Object>();
                fvals.put("field", (((String[])params.get(startString+"["+count+"][field]"))[0]).replace('_', '.'));
                if (params.containsKey(startString+"["+count+"][data][type]")){
                    fvals.put("type", ((String[])params.get(startString+"["+count+"][data][type]"))[0]);
                }
                if (params.containsKey(startString+"["+count+"][data][className]")){
                    fvals.put("className", ((String[])params.get(startString+"["+count+"][data][className]"))[0]);
                }
                if (params.containsKey(startString+"["+count+"][data][value]")){
                    if ("list".equals(fvals.get("type"))) {
                        fvals.put("value", params.get(startString+"["+count+"][data][value]"));
                    } else {
                        fvals.put("value", ((String[])params.get(startString+"["+count+"][data][value]"))[0]);
                    }
                }
                if (params.containsKey(startString+"["+count+"][data][comparison]")){
                    fvals.put("comparison", ((String[])params.get(startString+"["+count+"][data][comparison]"))[0]);
                }
                queryInput.addFilterParams(fvals);
            } else {
                done = true;;
            }
            count++;
        }
    }

    public String getErrorMessage(Throwable t) {
        if (t instanceof EJBException) {
            EJBException ejbe = (EJBException) t;
            if (ejbe.getCausedByException() != null) {
                 return ejbe.getCausedByException().getMessage();
            }
        }
        return t.getMessage();
    }

    public static boolean isSpecifiedError(Class <? extends Throwable> specified, Throwable error) {
        return isSpecifiedError(specified, null,  error);
    }
        
    public static boolean isSpecifiedError(Class <? extends Throwable> specified, String text, Throwable error) {
        Throwable t = error;
        boolean done = false;
        while (!done) {
            if (specified.isAssignableFrom(t.getClass()) &&((text == null) || t.getMessage().contains(text))) {
                return true;
            }

            Throwable cause = null;
            if (t instanceof javax.ejb.EJBException) {
                cause = ((javax.ejb.EJBException) t).getCausedByException();
            } else {
                cause = t.getCause();
            }

            if (cause == null) {
                done = true;
            } else {
                t = cause;
            }
        }

        return false;
    }


    public QueryInput getQueryInput() {
        return queryInput;
    }

    public void setQueryInput(QueryInput queryInput) {
        this.queryInput = queryInput;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
        queryInput.setStart(start);
    }

    public Integer getLimit() {
        if (limit == null) return Integer.MAX_VALUE; // if there is no limit then the grid wants all results
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
        queryInput.setLimit(limit);
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        queryInput.setSortDir(dir);
    }

    public String getSort() {
        return sort;
    }


    public void setSort(String sort) {
        sort = sort.replace('_', '.');
        this.sort = sort;
        queryInput.setSortCol(sort);
    }

    public Map<String,String[]> getParameters() {
        return parameters;
    }

    public QueryResults getQueryResults() {
        return this.queryResults;
    }

    /**
     * Override this method to return grid-specific results.
     * @param tableName
     * @return
     */
    public String getJsonResults(String tableName) {
        return getJsonResults();
    }

    public String getJsonResults() {
        return jsonResults;
    }

    public void setJsonResults(String jsonResults) {
        this.jsonResults = jsonResults;
    }


    public void setJson(JSONObject jsonObject)  {
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }

        try {
            jsonObject.put("success", this.success);

            if (hasActionMessages()) {
                jsonObject.put("actionMessages", getActionMessages());
            }
            if (hasActionErrors()) {
                jsonObject.put("actionErrors", getActionErrors());
            }
            if (hasFieldErrors()) {
                JSONArray errors = new JSONArray();
                errors.put(getFieldErrors());
                jsonObject.put("fieldErrors", errors);
            }

            this.jsonResults = jsonObject.toString();
        } catch (Exception e) {
            logger.error("Error setting json results:", e);
        }
    }

    /**
     * Add attributes to JSON object.
     * @param bean Object containing properties
     * @param props Map containing the properties to add
     * @param json JSONObject
     * @return
     * @throws Exception
     */
    public JSONObject beanPut(Object bean, Map<String, String> props, JSONObject json) 
    throws Exception {
        if (json == null) {
            json = new JSONObject();
        }

        for (String label : props.keySet()) {
            json.put(label, PropertyUtils.getProperty(bean, props.get(label)));
        }

        return json;
    }
    
    public JSONObject beanPut(Object bean, Collection<String> propertyNames, JSONObject json) throws Exception {
        if (json == null) {
            json = new JSONObject();
        }

        for (String prop : propertyNames) {
            json.put(prop, PropertyUtils.getProperty(bean, prop));
        }

        return json;
    }

    public Boolean isSuccess() {
        return success;
    }
    
    /* (non-Javadoc)
     * @see org.apache.struts2.interceptor.PrincipalAware#setPrincipalProxy(org.apache.struts2.interceptor.PrincipalProxy)
     */
    public void setPrincipalProxy(PrincipalProxy principalProxy) {
       this.principalProxy = principalProxy;
    }
    
    public void setSession(Map sessionMap) {
        this.sessionMap = sessionMap;
    }    
    
    public Boolean isUserInRole(String role){
    	//logger.info("Is user in role: " + role + " ? " + ServletActionContext.getRequest().isUserInRole(role));
        return ServletActionContext.getRequest().isUserInRole(role);
    }

    /**
     * This should be overriden by subclasses that want a custom result for
     * an unauthorized request
     */
    public String getUnauthorizedResult() {
        return UNAUTHORIZED;
    }

    public Boolean getExportToExcel() {
        return exportToExcel;
    }

    public void setExportToExcel(Boolean exportToExcel) {
        queryInput.setExportToExcel(exportToExcel);
        this.exportToExcel = exportToExcel;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ExcelReportExporter getExcelReportExporter() {
        return exporter;
    }

    public void setExcelReportExporter(ExcelReportExporter exporter){
        this.exporter = exporter;
    }

    public String getExcelExportFileName() {
        return excelExportFileName;
    }

    public void setExcelExportFileName(String excelExportFileName) {
        this.excelExportFileName = excelExportFileName;
    }

    public String getExcelExportSheetName() {
        return excelExportSheetName;
    }

    public void setExcelExportSheetName(String excelExportSheetName) {
        this.excelExportSheetName = excelExportSheetName;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        groupBy = groupBy.replace('_', '.');
        this.groupBy = groupBy;
        queryInput.setGroupBy(groupBy);
    }
    
    
    /**
     * Gets a "$ 0.00" style value from a Number
     */
    public String getMoneyDisplay(Number number){
        DecimalFormat df = new DecimalFormat("$#,##0.00;-$#,##0.00");
        return df.format(number);
    }
    
    
    protected boolean checkEmail(String email){
        if (email == null || email.length() == 0){
            setSuccess(false);
            setMessage("You must provide a valid email address.");
            return false;
        }
        int atindex = email.indexOf("@");
        boolean fail = false;
        if (atindex < 1){
            fail = true;
        } else {
            int dotindex = email.indexOf(".", atindex);
            if (dotindex < atindex+2){
                fail = true;
            }
            if (dotindex == email.length()-1){
                fail = true;
            }
        }
        if (fail){
            setSuccess(false);
            setMessage("You must provide a valid email address.");
            return false;
        }
        return true;
    }

    public String getUserName() {
    	return ServletActionContext.getRequest().getUserPrincipal().getName();    	
    }

    public boolean isLoggedIn() {
    	return ServletActionContext.getRequest().getUserPrincipal() != null;    	
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
        queryInput.setSearchString(searchString);
    }

    public String getFilterCol() {
        return filterCol;
    }

    public void setFilterCol(String filterCol) {
        this.filterCol = filterCol;
    }

    public String getFilterString() {
        if (filterString != null && filterString.length() > 0){
            // replace single quotes with escaped single quote
            return filterString.replace("'", "\\'");
        }
        return filterString;
    }

    public void setFilterString(String filterString) {
        this.filterString = filterString;
    }

    public Long getFilterId() {
        return filterId;
    }

    public void setFilterId(Long filterId) {
        this.filterId = filterId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getResult(){
        return this.result;
    }
    
    public void setResult(String result){
        this.result = result;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }    
    
    public String escapeJavaScript(String str){
        return StringEscapeUtils.escapeJavaScript(str);
    }

    public String escapeHtml(String str){
        return StringEscapeUtils.escapeHtml(str);
    }
    
    
    /*
     * 
     * EJB Getters
     * 
     * 
     */
    
    public InventoryItemSessionLocal getInventoryItemSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (InventoryItemSessionLocal)ctx.lookup(InventoryItemSession.LocalJNDIStringNoLoader);
            }
            return (InventoryItemSessionLocal)ctx.lookup(InventoryItemSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup InventoryItemSession", ne);
        }
        throw new RuntimeException("Could not lookup InventoryItemSession");
    }
    
    public UtilitySessionLocal getUtilitySession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (UtilitySessionLocal)ctx.lookup(UtilitySession.LocalJNDIStringNoLoader);
            }
            return (UtilitySessionLocal)ctx.lookup(UtilitySession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup UtilitySession", ne);
        }
        throw new RuntimeException("Could not lookup UtilitySession");
    }
    
    public LifoSessionLocal getLifoSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (LifoSessionLocal)ctx.lookup(LifoSession.LocalJNDIStringNoLoader);
            }
            return (LifoSessionLocal)ctx.lookup(LifoSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup LifoSession", ne);
        }
        throw new RuntimeException("Could not lookup LifoSession");
    }
    
    public BellInventorySessionLocal getBellInventorySession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (BellInventorySessionLocal)ctx.lookup(BellInventorySession.LocalJNDIStringNoLoader);
            }
            return (BellInventorySessionLocal)ctx.lookup(BellInventorySession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup BellInventorySession", ne);
        }
        throw new RuntimeException("Could not lookup BellInventorySession");
    }

    public UserSessionLocal getUserSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (UserSessionLocal)ctx.lookup(UserSession.LocalJNDIStringNoLoader);
            }
            return (UserSessionLocal)ctx.lookup(UserSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup UserSession", ne);
        }
        throw new RuntimeException("Could not lookup UserSession");
    }

    public CustomerSessionLocal getCustomerSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (CustomerSessionLocal)ctx.lookup(CustomerSession.LocalJNDIStringNoLoader);
            }
            return (CustomerSessionLocal)ctx.lookup(CustomerSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup CustomerSession", ne);
        }
        throw new RuntimeException("Could not lookup CustomerSession");
    }

    public BellCustomerSessionLocal getBellCustomerSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (BellCustomerSessionLocal)ctx.lookup(BellCustomerSession.LocalJNDIStringNoLoader);
            }
            return (BellCustomerSessionLocal)ctx.lookup(BellCustomerSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup CustomerSession", ne);
        }
        throw new RuntimeException("Could not lookup CustomerSession");
    }

    public CustomerShippingSessionLocal getCustomerShippingSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (CustomerShippingSessionLocal)ctx.lookup(CustomerShippingSession.LocalJNDIStringNoLoader);
            }
            return (CustomerShippingSessionLocal)ctx.lookup(CustomerShippingSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup CustomerShippingSession", ne);
        }
        throw new RuntimeException("Could not lookup CustomerShippingSession");
    }

    public OrderSessionLocal getOrderSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (OrderSessionLocal)ctx.lookup(OrderSession.LocalJNDIStringNoLoader);
            }
            return (OrderSessionLocal)ctx.lookup(OrderSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup OrderSession", ne);
        }
        throw new RuntimeException("Could not lookup OrderSession");
    }

    public ReceivingSessionLocal getReceivingSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (ReceivingSessionLocal)ctx.lookup(ReceivingSession.LocalJNDIStringNoLoader);
            }
            return (ReceivingSessionLocal)ctx.lookup(ReceivingSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup ReceivingSession", ne);
        }
        throw new RuntimeException("Could not lookup ReceivingSession");
    }

    public BackStockSessionLocal getBackStockSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (BackStockSessionLocal)ctx.lookup(BackStockSession.LocalJNDIStringNoLoader);
            }
            return (BackStockSessionLocal)ctx.lookup(BackStockSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup BackStockSession", ne);
        }
        throw new RuntimeException("Could not lookup BackStockSession");
    }


    public VendorSessionLocal getVendorSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (VendorSessionLocal)ctx.lookup(VendorSession.LocalJNDIStringNoLoader);
            }
            return (VendorSessionLocal)ctx.lookup(VendorSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup BellVendorSession", ne);
        }
        throw new RuntimeException("Could not lookup VendorSession");
    }

    public BellVendorSessionLocal getBellVendorSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (BellVendorSessionLocal)ctx.lookup(BellVendorSession.LocalJNDIStringNoLoader);
            }
            return (BellVendorSessionLocal)ctx.lookup(BellVendorSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup VendorSession", ne);
        }
        throw new RuntimeException("Could not lookup BellVendorSession");
    }

    public BellOrderSessionLocal getBellOrderSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (BellOrderSessionLocal)ctx.lookup(BellOrderSession.LocalJNDIStringNoLoader);
            }
            return (BellOrderSessionLocal)ctx.lookup(BellOrderSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup BellOrderSession", ne);
        }
        throw new RuntimeException("Could not lookup BellOrderSession");
    }

    public BellReceivingSessionLocal getBellReceivingSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (BellReceivingSessionLocal)ctx.lookup(BellReceivingSession.LocalJNDIStringNoLoader);
            }
            return (BellReceivingSessionLocal)ctx.lookup(BellReceivingSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup BellReceivingSession", ne);
        }
        throw new RuntimeException("Could not lookup BellOrderSession");
    }

    public ManifestSessionLocal getManifestSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (ManifestSessionLocal)ctx.lookup(ManifestSession.LocalJNDIStringNoLoader);
            }
            return (ManifestSessionLocal)ctx.lookup(ManifestSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup ManifestSession", ne);
        }
        throw new RuntimeException("Could not lookup ManifestSession");
    }

    public AuditSessionLocal getAuditSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (AuditSessionLocal)ctx.lookup(AuditSession.LocalJNDIStringNoLoader);
            }
            return (AuditSessionLocal)ctx.lookup(AuditSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup AuditSession", ne);
        }
        throw new RuntimeException("Could not lookup AuditSession");
    }

    
    public JRAbstractBeanDataSource getJasperDatasource() {
        return jasperDatasource;
    }

    public void setJasperDatasource(JRAbstractBeanDataSource jasperDatasource) {
        this.jasperDatasource = jasperDatasource;
    }

    public HashMap getJasperParamMap() {
        return jasperParamMap;
    }

    public void setJasperParamMap(HashMap jasperParamMap) {
        this.jasperParamMap = jasperParamMap;
    }

    public String getJasperFilename() {
        return jasperFilename;
    }

    public void setJasperFilename(String jasperFilename) {
        this.jasperFilename = jasperFilename;
    }

    public String getJasperReportName() {
        return jasperReportName;
    }

    public void setJasperReportName(String jasperReportName) {
        this.jasperReportName = jasperReportName;
    }

    public String getJasperExportType() {
        return jasperExportType;
    }

    public void setJasperExportType(String jasperExportType) {
        this.jasperExportType = jasperExportType;
    }

    public String formatMoney(Float f){
        if (f == null) f = 0F;
        if (f == Float.NEGATIVE_INFINITY) f = 0F;
        else if (f == Float.POSITIVE_INFINITY) f = 0F;
        DecimalFormat df = new DecimalFormat("$#,##0.00;-$#,##0.00");
        return df.format(f);
    }

    public String formatBoolean(Boolean bool){
        if (bool != null && bool)
            return "<span class='greentext'>Yes</span>";
        return "<span class='redtext'>No</span>";
    }
    
    public String formatMoney(Double d){
        if (d == null) d = 0D;
        if (d == Double.NEGATIVE_INFINITY) d = 0D;
        else if (d == Double.POSITIVE_INFINITY) d = 0D;
        DecimalFormat df = new DecimalFormat("$#,##0.00;-$#,##0.00");
        return df.format(d);
    }
    
    public String formatMoney(BigDecimal bd){
        if (bd == null) bd = BigDecimal.valueOf(0D);
        return formatMoney(bd.doubleValue());
    }
    
    public String formatPercent(Float f){
        if (f == null) return "";
        return formatTwoDecimal(f*100.0)+"%";
    }
    public String formatPercent(Double d){
        if (d == null) return "";
        return formatTwoDecimal(d*100.0)+"%";
    }
    
    public String formatPercentNoMod(Float f){
        if (f == null) return "";
        return formatTwoDecimal(f)+"%";
    }
    public String formatPercentNoMod(Double d){
        if (d == null) return "";
        return formatTwoDecimal(d)+"%";
    }
    
    public String formatTwoDecimal(Double d){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return df.format(d);
    }
    public String formatTwoDecimal(Float f){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return df.format(f);
    }
    
    public Boolean getIsBcCapable(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            for (String r : new String[]{"BcInvAdmin", "BcInvViewer","BcRecAdmin", "BcRecViewer",
                    "BcOrderAdmin", "BcOrderViewer","BcCustomerAdmin", "BcCustomerViewer","BcVendorAdmin", "BcVendorViewer"}) 
            {
                if (roles.contains(r)){
                    return true;
                }
            }
        }
        return false;
    }
    public Boolean getIsBellCapable(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            for (String r : new String[]{"BellInvAdmin", "BellInvViewer","BellRecAdmin", "BellRecViewer", "BellOrderAdmin", "BellOrderViewer"}) {
                if (roles.contains(r)){
                    return true;
                }
            }
        }
        return false;
    }
    public Boolean getIsBcInventoryAdmin(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcInvAdmin");
        }
        return false;
    }
    public Boolean getIsSystemAdmin(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("SystemAdmin");
        }
        return false;
    }
    public Boolean getIsBcInventoryViewer(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcInvViewer");
        }
        return false;
    }
    public Boolean getIsBcReceivingAdmin(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcRecAdmin");
        }
        return false;
    }
    public Boolean getIsBcReceivingViewer(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcRecViewer");
        }
        return false;
    }
    public Boolean getIsBcOrderAdmin(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcOrderAdmin");
        }
        return false;
    }
    public Boolean getIsBcSalesRepAdmin(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcSalesRepAdmin");
        }
        return false;
    }
    public Boolean getIsBcOrderViewer(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcOrderViewer");
        }
        return false;
    }
    public Boolean getIsBcCustomerAdmin(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcCustomerAdmin");
        }
        return false;
    }
    public Boolean getIsBcCustomerViewer(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcCustomerViewer");
        }
        return false;
    }
    public Boolean getIsBcBackStockAdmin(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcBackStockAdmin");
        }
        return false;
    }
    public Boolean getIsBcBackStockViewer(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcBackStockViewer");
        }
        return false;
    }
    public Boolean getIsBcVendorAdmin(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcVendorAdmin");
        }
        return false;
    }
    public Boolean getIsBcVendorViewer(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcVendorViewer");
        }
        return false;
    }
    public Boolean getIsBcUserAdmin(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcUserAdmin");
        }
        return false;
    }
    public Boolean getIsBcUserViewer(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcUserViewer");
        }
        return false;
    }
    public Boolean getIsBcManifestAdmin(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcManifestAdmin");
        }
        return false;
    }
    public Boolean getIsBcManifestViewer(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BcManifestViewer");
        }
        return false;
    }
    public Boolean getIsBellInventoryAdmin(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BellInvAdmin");
        }
        return false;
    }
    public Boolean getIsBellInventoryViewer(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BellInvViewer");
        }
        return false;
    }
    public Boolean getIsBellReceivingAdmin(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BellRecAdmin");
        }
        return false;
    }
    public Boolean getIsBellReceivingViewer(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BellRecViewer");
        }
        return false;
    }
    public Boolean getIsBellOrderAdmin(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BellOrderAdmin");
        }
        return false;
    }
    public Boolean getIsBellOrderViewer(){
        if (sessionMap != null && sessionMap.containsKey("roles")){
            HashSet<String> roles = (HashSet<String>)sessionMap.get("roles");
            return roles.contains("BellOrderViewer");
        }
        return false;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public Boolean getExportBulkToExcel() {
        return exportBulkToExcel;
    }

    public void setExportBulkToExcel(Boolean exportBulkToExcel) {
        queryInput.setExportToExcel(exportBulkToExcel);
        this.exportBulkToExcel = exportBulkToExcel;
    }

    public Boolean getExportCountToExcel() {
        return exportCountToExcel;
    }

    public void setExportCountToExcel(Boolean exportCountToExcel) {
        queryInput.setExportToExcel(exportCountToExcel);
        this.exportCountToExcel = exportCountToExcel;
    }

    public Boolean getExportWithItemsToExcel() {
        return exportWithItemsToExcel;
    }

    public void setExportWithItemsToExcel(Boolean exportWithItemsToExcel) {
        this.exportWithItemsToExcel = exportWithItemsToExcel;
    }

    public ExcelExtraDataWriter getExtraDataWriter() {
        return extraDataWriter;
    }

    public void setExtraDataWriter(ExcelExtraDataWriter extraDataWriter) {
        this.extraDataWriter = extraDataWriter;
    }

    public String getDefaultFilter() {
        return defaultFilter;
    }

    public void setDefaultFilter(String defaultFilter) {
        this.defaultFilter = defaultFilter;
    }

    public String getDefaultFilterCol() {
        return defaultFilterCol;
    }

    public void setDefaultFilterCol(String defaultFilterCol) {
        this.defaultFilterCol = defaultFilterCol;
    }

    public String getDefaultFilters() {
        return defaultFilters;
    }

    public void setDefaultFilters(String defaultFilters) {
        this.defaultFilters = defaultFilters;
    }

    public Map<String, String> getSearchNames() {
        return searchNames;
    }

    public void setSearchNames(Map<String, String> searchNames) {
        this.searchNames = searchNames;
    }

    public String[] getExportColumns() {
        return exportColumns;
    }

    public void setExportColumns(String[] exportColumns) {
        this.exportColumns = exportColumns;
    }
    
    public String[] getExportColumnNames() {
        return exportColumnNames;
    }

    public void setExportColumnNames(String[] exportColumnNames) {
        this.exportColumnNames = exportColumnNames;
    }
    
    public Integer getStartRow(){
        return startRow;
    }
    
    public void setStartRow(Integer startRow){
        this.startRow = startRow;
    }
    
    public List<String> getExportColumnsList(){
        if (exportColumns != null && exportColumns.length > 0){
            return Arrays.asList(exportColumns);
        }
        return null;
    }
    
    public List<String> getExportColumnNamesList(){
        if (exportColumnNames != null && exportColumnNames.length > 0){
            return Arrays.asList(exportColumnNames);
        }
        return null;
    }

    public Boolean getExportLimitExceeded() {
        return exportLimitExceeded;
    }

    
}
