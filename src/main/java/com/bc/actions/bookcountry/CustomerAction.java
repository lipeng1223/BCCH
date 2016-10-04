package com.bc.actions.bookcountry;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

//import jxl.Sheet;
//import jxl.Workbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.bc.actions.BaseAction;
import com.bc.ejb.CustomerSessionLocal;
import com.bc.ejb.UserSessionLocal;
import com.bc.orm.Customer;
import com.bc.orm.User;
import com.bc.struts.QueryResults;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.table.Filter;
import com.bc.table.Table;
import com.bc.table.Toolbar;
import com.bc.table.ToolbarButton;
import com.bc.util.ActionRole;
import com.bc.util.cache.CustomerCache;
import java.io.FileInputStream;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="list", location="/WEB-INF/jsp/bookcountry/customers/list.jsp"),
    @Result(name="uploadPage", location="/WEB-INF/jsp/bookcountry/customers/upload.jsp"),
    @Result(name="crud", location="/WEB-INF/jsp/bookcountry/customers/crud.jsp"),
    @Result(name="detail", location="/WEB-INF/jsp/bookcountry/customers/detail.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class CustomerAction extends BaseAction {

    private Customer customer;
    private Table listTable;
    private String quickSearch;
    private String quickSearchLocation;
    private Boolean isQuickSearch = false;
    
    private File upload;//The actual file
    private String uploadContentType; //The content type of the file
    private String uploadFileName; //The uploaded file name    
    
    @ActionRole({"BcCustomerAdmin", "BcCustomerViewer"})
    public String list(){
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcCustomerAdmin", "BcCustomerViewer"})
    public String quickSearch(){
        isQuickSearch = true;
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcCustomerAdmin"})
    public String create(){
        return "crud";
    }
    
    @ActionRole({"BcCustomerAdmin"})
    public String createSubmit(){
        try {
            CustomerSessionLocal customerSession = getCustomerSession();
            if (customer.getDiscount() == null) customer.setDiscount(0);
            customerSession.create(customer);
            id = customer.getId();
            CustomerCache.put(customer);
            setSuccess(true);
            setMessage("Created New Customer");
        } catch (Exception e){
            logger.error("Exception", e);
            setSuccess(false);
            setMessage("Could not create the Customer, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcCustomerAdmin"})
    public String edit(){
        try {
            CustomerSessionLocal customerSession = getCustomerSession();
            customer = customerSession.findById(id);
        } catch (Exception e){
            logger.error("Could not find customer by id: "+id, e);
            return "error";
        }
        return "crud";
    }
    
    @ActionRole({"BcCustomerAdmin"})
    public String editSubmit(){
        try {
            CustomerSessionLocal customerSession = getCustomerSession();
            Customer dbc = customerSession.findById(customer.getId());
            UserSessionLocal userSession = getUserSession();
            User currentUser = userSession.findByName(ServletActionContext.getRequest().getUserPrincipal().getName(), "roles");
            if (dbc != null){
                dbc.setAddress1(customer.getAddress1());
                dbc.setAddress2(customer.getAddress2());
                dbc.setAddress3(customer.getAddress3());
                dbc.setBackorder(customer.getBackorder());
                dbc.setBookclub(customer.getBookclub());
                dbc.setBookfair(customer.getBookfair());
                dbc.setCellPhone(customer.getCellPhone());
                dbc.setCity(customer.getCity());
                dbc.setCode(customer.getCode());
                dbc.setComment1(customer.getComment1());
                dbc.setComment2(customer.getComment2());
                dbc.setCompanyName(customer.getCompanyName());
                dbc.setContactName(customer.getContactName());
                dbc.setCountry(customer.getCountry());
                dbc.setDiscount(customer.getDiscount());
                dbc.setEmail1(customer.getEmail1());
                dbc.setEmail2(customer.getEmail2());
                dbc.setInvoiceEmail(customer.getInvoiceEmail());
                dbc.setEmailInvoice(customer.getEmailInvoice());
                dbc.setFax(customer.getFax());
                dbc.setHomePhone(customer.getHomePhone());
                dbc.setMaillist(customer.getMaillist());
                dbc.setPicklistComment(customer.getPicklistComment());
                if (ServletActionContext.getRequest().getUserPrincipal().getName().equals("manager") || 
                    ServletActionContext.getRequest().getUserPrincipal().getName().equals("kelley") || 
                    ServletActionContext.getRequest().getUserPrincipal().getName().equals("teri") ||
                    currentUser.hasRole("BcSalesRepAdmin")) {
                    dbc.setSalesRep(customer.getSalesRep());
                }
                dbc.setState(customer.getState());
                dbc.setTax(customer.getTax());
                dbc.setTerms(customer.getTerms());
                dbc.setWorkPhone(customer.getWorkPhone());
                dbc.setZip(customer.getZip());
                customerSession.update(dbc);
            }
            CustomerCache.remove(customer.getId());
            CustomerCache.put(customerSession.findById(customer.getId(), "customerShippings"));
            setSuccess(true);
            setMessage("Updated Customer");
        } catch (Exception e){
            logger.error("Exception", e);
            setSuccess(false);
            setMessage("Could not update the Customer, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcCustomerAdmin"})
    public String delete(){
        try {
            CustomerSessionLocal customerSession = getCustomerSession();
            customerSession.delete(id);
            CustomerCache.remove(id);
            setSuccess(true);
            setMessage("Deleted the customer.");
        } catch (Exception e){
            logger.error("Could not delete customer", e);
            setSuccess(false);
            setMessage("Could not delete the customer, there was a system error");
        }
        return "status";
    }
    
    @ActionRole({"BcCustomerAdmin", "BcCustomerViewer"})
    public String detail(){
        try {
            CustomerSessionLocal customerSession = getCustomerSession();
            customer = customerSession.findById(id, "customerShippings");
        } catch (Exception e){
            logger.error("Could not get customer detail", e);
        }
        return "detail";
    }
    
    @ActionRole({"BcCustomerAdmin", "BcCustomerViewer"})
    public String listData(){
        setupListTable(); 
        try {
            CustomerSessionLocal customerSession = getCustomerSession();
            if (quickSearch != null){
                Disjunction dis = Restrictions.disjunction();
                dis.add(Restrictions.ilike("code", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("contactName", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("companyName", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("salesRep", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("email1", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("email2", quickSearch, MatchMode.ANYWHERE));
                queryInput.addAndCriterion(dis);
            }
            queryInput.addAndCriterion(Restrictions.eq("deleted", Boolean.FALSE));
            queryResults = new QueryResults(customerSession.findAll(queryInput));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not get list data", e);
        }
        return "queryresults";
    }

    private void setupListTable(){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("companyName"));
        cd.add(new ColumnData("code"));
        cd.add(new ColumnData("contactName"));
        cd.add(new ColumnData("salesRep"));
        cd.add(new ColumnData("email1"));
        cd.add(new ColumnData("email2"));
        cd.add(new ColumnData("invoiceEmail"));
        cd.add(new ColumnData("address1"));
        cd.add(new ColumnData("address2"));
        cd.add(new ColumnData("address3"));
        cd.add(new ColumnData("city"));
        cd.add(new ColumnData("state"));
        cd.add(new ColumnData("zip"));
        cd.add(new ColumnData("country"));
        cd.add(new ColumnData("picklistComment"));
        cd.add(new ColumnData("backorder"));
        cd.add(new ColumnData("balance"));
        cd.add(new ColumnData("creditLimit").setType("float"));
        cd.add(new ColumnData("hold"));
        cd.add(new ColumnData("avedays"));
        cd.add(new ColumnData("lastSalesDate"));
        cd.add(new ColumnData("lastActivity"));
        cd.add(new ColumnData("salesYtd").setType("float"));
        cd.add(new ColumnData("salesPyr").setType("float"));
        cd.add(new ColumnData("homePhone"));
        cd.add(new ColumnData("workPhone"));
        cd.add(new ColumnData("cellPhone"));
        cd.add(new ColumnData("fax"));
        cd.add(new ColumnData("terms"));
        cd.add(new ColumnData("discount").setType("float"));
        cd.add(new ColumnData("tax"));
        cd.add(new ColumnData("emailInvoice"));
        cd.add(new ColumnData("bookclub"));
        cd.add(new ColumnData("bookfair"));
        cd.add(new ColumnData("maillist"));
        cd.add(new ColumnData("comment1"));
        cd.add(new ColumnData("comment2"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50));
        cm.add(new ColumnModel("companyName", "Company Name", 150));
        cm.add(new ColumnModel("code", "Code", 100));
        cm.add(new ColumnModel("contactName", "Contact Name", 150));
        cm.add(new ColumnModel("salesRep", "Sales Rep", 100));
        cm.add(new ColumnModel("lastSalesDate", "Last Sales Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("email1", "Email 1", 120));
        cm.add(new ColumnModel("email2", "Email 2", 120));
        cm.add(new ColumnModel("emailInvoice", "Email Invoice", 120).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("invoiceEmail", "Invoice Email", 120));
        cm.add(new ColumnModel("backorder", "Backorder", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("hold", "Hold", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("balance", "Balance", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("creditLimit", "Credit Limit", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("avedays", "Ave Days", 100));
        cm.add(new ColumnModel("salesYtd", "Sales YTD", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("salesPyr", "Sales PYR", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("terms", "Terms", 100));
        cm.add(new ColumnModel("discount", "Discount", 100).setRenderer("percentNoModRenderer"));
        cm.add(new ColumnModel("tax", "Tax", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("bookclub", "Book Club", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("bookfair", "Book Fair", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("maillist", "Mail List", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("address1", "Address 1", 100));
        cm.add(new ColumnModel("address2", "Address 2", 100));
        cm.add(new ColumnModel("address3", "Address 3", 100));
        cm.add(new ColumnModel("country", "Country", 100));
        cm.add(new ColumnModel("state", "State", 100));
        cm.add(new ColumnModel("city", "City", 100));
        cm.add(new ColumnModel("zip", "Zip", 100));
        cm.add(new ColumnModel("homePhone", "Home Phone", 100));
        cm.add(new ColumnModel("workPhone", "Work Phone", 100));
        cm.add(new ColumnModel("cellPhone", "Cell Phone", 100));
        cm.add(new ColumnModel("fax", "Fax", 100));
        cm.add(new ColumnModel("picklistComment", "Picklist Comment", 300));
        cm.add(new ColumnModel("comment1", "Comment 1", 300));
        cm.add(new ColumnModel("comment2", "Comment 2", 300));
        listTable.setColumnModels(cm);
        
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("long", "id"));
        filters.add(new Filter("string", "companyName"));
        filters.add(new Filter("string", "code"));
        filters.add(new Filter("string", "contactName"));
        filters.add(new Filter("string", "salesRep"));
        filters.add(new Filter("date", "lastSalesDate"));
        filters.add(new Filter("string", "email1"));
        filters.add(new Filter("string", "email2"));
        filters.add(new Filter("boolean", "backorder"));
        filters.add(new Filter("boolean", "hold"));
        filters.add(new Filter("float", "balance"));
        filters.add(new Filter("float", "creditLimit"));
        filters.add(new Filter("integer", "avedays"));
        filters.add(new Filter("float", "salesYtd"));
        filters.add(new Filter("float", "salesPyr"));
        filters.add(new Filter("float", "Discount"));
        filters.add(new Filter("boolean", "tax"));
        filters.add(new Filter("boolean", "bookclub"));
        filters.add(new Filter("boolean", "bookfair"));
        filters.add(new Filter("boolean", "maillist"));
        filters.add(new Filter("string", "address1"));
        filters.add(new Filter("string", "address2"));
        filters.add(new Filter("string", "address3"));
        filters.add(new Filter("string", "country"));
        filters.add(new Filter("string", "state"));
        filters.add(new Filter("string", "city"));
        filters.add(new Filter("string", "zip"));
        filters.add(new Filter("string", "homePhone"));
        filters.add(new Filter("string", "workPhone"));
        filters.add(new Filter("string", "cellPhone"));
        filters.add(new Filter("string", "fax"));
        filters.add(new Filter("string", "picklistComment"));
        filters.add(new Filter("string", "comment1"));
        filters.add(new Filter("string", "comment2"));
        listTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        if (getIsBcCustomerAdmin()) {
            buttons.add(new ToolbarButton("Create", "createButtonClick", "create_icon", "Create A New Customer"));
            buttons.add(new ToolbarButton("Edit", "editButtonClick", "edit_icon", "Edit The Selected Customer").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Delete", "deleteButtonClick", "delete_icon", "Delete The Selected Customer").setSingleRowAction(true));
        }
        buttons.add(new ToolbarButton().setRight(true));
        if(getIsBcCustomerAdmin()) {
            buttons.add(new ToolbarButton("MAS 90 Upload", "mas90ButtonClick", "excel_icon", "Upload MAS 90 Excel"));
        }
        buttons.add(new ToolbarButton("Sales History", "salesHistoryButtonClick", "history_icon", "Sales History For The Selected Customer").setSingleRowAction(true));
        buttons.add(new ToolbarButton("Title History", "titleHistoryButtonClick", "history_icon", "Title History For The Selected Customer").setSingleRowAction(true));
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("companyName");
        listTable.setDefaultSortDir(Table.SORT_DIR_ASC);
        
        // this is for the exports
        setExcelExportFileName("CustomerExport");
        setExcelExportSheetName("Customers");
        
    }
    
    @ActionRole({"BcCustomerAdmin"})
    public String uploadMas90Page(){
        return "uploadPage";
    }
    
    @ActionRole({"BcCustomerAdmin"})
//    public String uploadMas90(){
//        if (upload == null){
//            setSuccess(false);
//            setMessage("You must provide a file to upload.");
//            return "status";
//        }
//        
//        // Check File Extension
//        int place = uploadFileName.lastIndexOf( '.' );
//        if ( place >= 0 ) {           
//            String ext = uploadFileName.substring( place + 1 );
//            if(!ext.toLowerCase().equals("xlsx")) {
//                setSuccess(false);
//                setMessage("Only xlsx files are supported at this time.");
//                return "status";
//            }
//        }
//
//        
//        try {
//            Workbook workbook = null;
//            try {
//                workbook = Workbook.getWorkbook(upload);
//            } catch(Exception e) {
//                logger.error("Unsupported file type.", e);
//                setSuccess(false);
//                setMessage("Unsupported file type.  Please ensure you are uploading an Excel file.");
//                return "status";
//            }
//            
//            Sheet s = workbook.getSheet(0);
//            
//            int numRows = s.getRows();
//            if(numRows <= 1) {
//                setSuccess(false);
//                setMessage("The uploaded file contained no customers to upload.");
//                return "status";
//            }
//            
//            CustomerSessionLocal customerSession = getCustomerSession();
//            
//            // start at row 6
//            for(int row = 6; row < numRows; row++) { 
//                String name = s.getCell(0, row).getContents();
//                
//                if (name == null || name.length() == 0) break;
//                if ("Grand Total:".equals(name)) break;
//                
//                String hold = s.getCell(3, row).getContents();
//                String aveDays = s.getCell(5, row).getContents();
//                String limit = s.getCell(7, row).getContents();
//                String lastActivity = s.getCell(8, row).getContents();
//                String balance = s.getCell(9, row).getContents();
//                String salesYtd = s.getCell(10, row).getContents();
//                String salesPyr = s.getCell(11, row).getContents();
//                
//                SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
//                NumberFormat nf = NumberFormat.getInstance();
//                List<Customer> customers = customerSession.findByName(name);
//                for (Customer cust : customers){
//                    cust.setHold(false);
//                    if ("Y".equals(hold)){
//                        cust.setHold(true);
//                    }
//                    if (aveDays != null && aveDays.length() > 0) cust.setAvedays(new Integer(aveDays));
//                    if (limit != null && limit.length() > 0) cust.setCreditLimit(nf.parse(limit).floatValue());
//                    if (lastActivity != null && lastActivity.length() > 0) cust.setLastActivity(sdf.parse(lastActivity));
//                    if (balance != null && balance.length() > 0) cust.setBalance(nf.parse(balance).floatValue());
//                    if (salesYtd != null && salesYtd.length() > 0) cust.setSalesYtd(nf.parse(salesYtd).floatValue());
//                    if (salesPyr != null && salesPyr.length() > 0) cust.setSalesPyr(nf.parse(salesPyr).floatValue());
//                    customerSession.update(cust);
//                }
//            }            
//            
//            setSuccess(true);
//        } catch (Exception e){
//            logger.error("Could not upload customers", e);
//            setSuccess(false);
//            setMessage("Could not upload the customers, there was a system error.");
//        }
//        return "status";
//    }
    public String uploadMas90(){
        if (upload == null){
            setSuccess(false);
            setMessage("You must provide a file to upload.");
            return "status";
        }
        
        // Check File Extension
        int place = uploadFileName.lastIndexOf( '.' );
        if ( place >= 0 ) {           
            String ext = uploadFileName.substring( place + 1 );
            if(!ext.toLowerCase().equals("xlsx")) {
                setSuccess(false);
                setMessage("Only xlsx files are supported at this time.");
                return "status";
            }
        }

        
        try {
            XSSFWorkbook workbook = null;
            FileInputStream fis = new FileInputStream(upload);
            try {
                workbook = new XSSFWorkbook(fis);
            } catch(Exception e) {
                logger.error("Unsupported file type.", e);
                setSuccess(false);
                setMessage("Unsupported file type.  Please ensure you are uploading an Excel file.");
                return "status";
            }
            
            XSSFSheet s = workbook.getSheetAt(0);
            
            int numRows = s.getPhysicalNumberOfRows() + 1;
            if(numRows <= 1) {
                setSuccess(false);
                setMessage("The uploaded file contained no customers to upload.");
                return "status";
            }
            
            CustomerSessionLocal customerSession = getCustomerSession();
            
            // start at row 6
            for(int row = 6; row < numRows; row++) { 
                Row r = s.getRow(row);
                if (r == null)
                    continue;
                String name = r.getCell(0).getStringCellValue();
                
                if (name == null || name.length() == 0) break;
                if ("Grand Total:".equals(name)) break;
                
                String hold = r.getCell(3).getStringCellValue();
                String aveDays = r.getCell(5).getStringCellValue();
                String limit = r.getCell(7).getStringCellValue();
                String lastActivity = r.getCell(8).getStringCellValue();
                String balance = r.getCell(9).getStringCellValue();
                String salesYtd = r.getCell(10).getStringCellValue();
                String salesPyr = r.getCell(11).getStringCellValue();
                
                SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
                NumberFormat nf = NumberFormat.getInstance();
                List<Customer> customers = customerSession.findByName(name);
                for (Customer cust : customers){
                    cust.setHold(false);
                    if ("Y".equals(hold)){
                        cust.setHold(true);
                    }
                    if (aveDays != null && aveDays.length() > 0) cust.setAvedays(new Integer(aveDays));
                    if (limit != null && limit.length() > 0) cust.setCreditLimit(nf.parse(limit).floatValue());
                    if (lastActivity != null && lastActivity.length() > 0) cust.setLastActivity(sdf.parse(lastActivity));
                    if (balance != null && balance.length() > 0) cust.setBalance(nf.parse(balance).floatValue());
                    if (salesYtd != null && salesYtd.length() > 0) cust.setSalesYtd(nf.parse(salesYtd).floatValue());
                    if (salesPyr != null && salesPyr.length() > 0) cust.setSalesPyr(nf.parse(salesPyr).floatValue());
                    customerSession.update(cust);
                }
            }            
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not upload customers", e);
            setSuccess(false);
            setMessage("Could not upload the customers, there was a system error.");
        }
        return "status";
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Table getListTable() {
        return listTable;
    }

    public void setListTable(Table listTable) {
        this.listTable = listTable;
    }

    public File getUpload() {
        return upload;
    }

    public void setUpload(File upload) {
        this.upload = upload;
    }

    public String getUploadContentType() {
        return uploadContentType;
    }

    public void setUploadContentType(String uploadContentType) {
        this.uploadContentType = uploadContentType;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public String getQuickSearch() {
        return quickSearch;
    }

    public void setQuickSearch(String quickSearch) {
        this.quickSearch = quickSearch;
    }

    public String getQuickSearchLocation() {
        return quickSearchLocation;
    }

    public void setQuickSearchLocation(String quickSearchLocation) {
        this.quickSearchLocation = quickSearchLocation;
    }

    public Boolean getIsQuickSearch() {
        return isQuickSearch;
    }

    public void setIsQuickSearch(Boolean isQuickSearch) {
        this.isQuickSearch = isQuickSearch;
    }
    
}
