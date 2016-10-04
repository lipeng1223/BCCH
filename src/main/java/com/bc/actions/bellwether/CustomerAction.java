package com.bc.actions.bellwether;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.bc.actions.BaseAction;
import com.bc.ejb.bellwether.BellCustomerSessionLocal;
import com.bc.orm.BellCustomer;
import com.bc.struts.QueryResults;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.table.Filter;
import com.bc.table.Table;
import com.bc.table.Toolbar;
import com.bc.table.ToolbarButton;
import com.bc.util.ActionRole;
import com.bc.util.cache.BellCustomerCache;
import org.apache.log4j.Logger;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bellwether")
@Results({
    @Result(name="list", location="/WEB-INF/jsp/bellwether/customers/list.jsp"),
    @Result(name="crud", location="/WEB-INF/jsp/bellwether/customers/crud.jsp"),
    @Result(name="detail", location="/WEB-INF/jsp/bellwether/customers/detail.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class CustomerAction extends BaseAction {

    private Logger log = Logger.getLogger(CustomerAction.class);
    
    private BellCustomer customer;
    private Table listTable;
    private String quickSearch;
    private String quickSearchLocation;
    private Boolean isQuickSearch = false;
    
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
            BellCustomerSessionLocal customerSession = getBellCustomerSession();
            customerSession.create(customer);
            id = customer.getId();
            BellCustomerCache.put(customer);
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
            BellCustomerSessionLocal customerSession = getBellCustomerSession();
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
            BellCustomerSessionLocal customerSession = getBellCustomerSession();
            BellCustomer dbc = customerSession.findById(customer.getId());
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
                dbc.setFax(customer.getFax());
                dbc.setHomePhone(customer.getHomePhone());
                dbc.setMaillist(customer.getMaillist());
                dbc.setPicklistComment(customer.getPicklistComment());
                if (ServletActionContext.getRequest().getUserPrincipal().getName().equals("manager")) {
                    dbc.setSalesRep(customer.getSalesRep());
                }
                dbc.setState(customer.getState());
                dbc.setTax(customer.getTax());
                dbc.setTerms(customer.getTerms());
                dbc.setWorkPhone(customer.getWorkPhone());
                dbc.setZip(customer.getZip());
                customerSession.update(dbc);
            }
            BellCustomerCache.remove(customer.getId());
            BellCustomerCache.put(customerSession.findById(customer.getId()));
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
            BellCustomerSessionLocal customerSession = getBellCustomerSession();
            customerSession.delete(id);
            BellCustomerCache.remove(id);
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
            BellCustomerSessionLocal customerSession = getBellCustomerSession();
            customer = customerSession.findById(id);
        } catch (Exception e){
            logger.error("Could not get customer detail", e);
        }
        return "detail";
    }
    
    @ActionRole({"BcCustomerAdmin", "BcCustomerViewer"})
    public String listData(){
        setupListTable(); 
        try {
            BellCustomerSessionLocal customerSession = getBellCustomerSession();
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
        cm.add(new ColumnModel("email1", "Email 1", 120));
        cm.add(new ColumnModel("email2", "Email 2", 120));
        cm.add(new ColumnModel("backorder", "Backorder", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("hold", "Hold", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("balance", "Balance", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("creditLimit", "Credit Limit", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("avedays", "Ave Days", 100));
        cm.add(new ColumnModel("salesYtd", "Sales YTD", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("salesPyr", "Sales PYR", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("terms", "Terms", 100));
        cm.add(new ColumnModel("discount", "Discount", 100));
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
        filters.add(new Filter("string", "companyName"));
        filters.add(new Filter("string", "code"));
        filters.add(new Filter("string", "contactName"));
        filters.add(new Filter("string", "salesRep"));
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
    
    public BellCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(BellCustomer customer) {
        this.customer = customer;
    }

    public Table getListTable() {
        return listTable;
    }

    public void setListTable(Table listTable) {
        this.listTable = listTable;
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
