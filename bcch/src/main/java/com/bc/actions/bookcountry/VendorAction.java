package com.bc.actions.bookcountry;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.bc.actions.BaseAction;
import com.bc.ejb.VendorSessionLocal;
import com.bc.orm.Vendor;
import com.bc.orm.VendorSkidType;
import com.bc.struts.QueryResults;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.table.Filter;
import com.bc.table.Table;
import com.bc.table.Toolbar;
import com.bc.table.ToolbarButton;
import com.bc.util.ActionRole;
import com.bc.util.cache.VendorCache;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="list", location="/WEB-INF/jsp/bookcountry/vendors/list.jsp"),
    @Result(name="detail", location="/WEB-INF/jsp/bookcountry/vendors/detail.jsp"),
    @Result(name="crud", location="/WEB-INF/jsp/bookcountry/vendors/crud.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class VendorAction extends BaseAction {

    private Vendor vendor;
    private VendorSkidType vendorSkidType;
    private Table listTable;
    private String quickSearch;
    private String quickSearchLocation;
    private Boolean isQuickSearch = false;
    
    @ActionRole({"BcVendorAdmin", "BcVendorViewer"})
    public String list(){
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcVendorAdmin", "BcVendorViewer"})
    public String quickSearch(){
        isQuickSearch = true;
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcVendorAdmin"})
    public String create(){
        return "crud";
    }
    
    @ActionRole({"BcVendorAdmin"})
    public String createSubmit(){
        try {
            VendorSessionLocal vendorSession = getVendorSession();
            vendorSession.create(vendor);
            id = vendor.getId();
            VendorCache.put(vendor);
            setSuccess(true);
            setMessage("Created Vendor");
        } catch (Exception e){
            logger.error("Could not create vendor", e);
            setSuccess(false);
            setMessage("Could not create the Vendor, there was a system error.");
        }
        return "status";
    }

    @ActionRole({"BcVendorAdmin"})
    public String edit(){
        try {
            VendorSessionLocal vendorSession = getVendorSession();
            vendor = vendorSession.findById(id);
        } catch (Exception e){
            logger.error("Could not find vendor to edit for id: "+id, e);
            return "error";
        }
        return "crud";
    }
    
    @ActionRole({"BcVendorAdmin"})
    public String editSubmit(){
        try {
            VendorSessionLocal vendorSession = getVendorSession();
            Vendor dbv = vendorSession.findById(vendor.getId());
            dbv.setAccountNumber(vendor.getAccountNumber());
            dbv.setAddress1(vendor.getAddress1());
            dbv.setAddress2(vendor.getAddress2());
            dbv.setAddress3(vendor.getAddress3());
            dbv.setCellPhone(vendor.getCellPhone());
            dbv.setCity(vendor.getCity());
            dbv.setCode(vendor.getCode());
            dbv.setEmail1(vendor.getEmail1());
            dbv.setEmail2(vendor.getEmail2());
            dbv.setFax(vendor.getFax());
            dbv.setHomePhone(vendor.getHomePhone());
            dbv.setShippingCompany(vendor.getShippingCompany());
            dbv.setState(vendor.getState());
            dbv.setTerms(vendor.getTerms());
            dbv.setVendorName(vendor.getVendorName());
            dbv.setWorkPhone(vendor.getWorkPhone());
            dbv.setZip(vendor.getZip());
            vendorSession.update(dbv);
            VendorCache.remove(vendor.getId());
            VendorCache.put(vendorSession.findById(vendor.getId()));
            setSuccess(true);
            setMessage("Updated Vendor");
        } catch (Exception e){
            logger.error("Could not update vendor", e);
            setSuccess(false);
            setMessage("Could not update the Vendor, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcVendorAdmin"})
    public String delete(){
        try {
            VendorSessionLocal vendorSession = getVendorSession();
            vendorSession.delete(id);
            VendorCache.remove(id);
            setSuccess(true);
            setMessage("Deleted the vendor.");
        } catch (Exception e){
            logger.error("Could not delete vendor", e);
            setSuccess(false);
            setMessage("Could not delete the vendor, there was a system error");
        }
        return "status";
    }
    
    @ActionRole({"BcVendorAdmin", "BcVendorViewer"})
    public String detail(){
        try {
            VendorSessionLocal vendorSession = getVendorSession();
            vendor = vendorSession.findById(id, "vendorSkidTypes");
        } catch (Exception e){
            logger.error("Could not find vendor detail for id: "+id, e);
            return "error";
        }
        return "detail";
    }
    
    @ActionRole({"BcVendorAdmin"})
    public String createSkidType(){
        try {
            VendorSessionLocal vendorSession = getVendorSession();
            vendor = vendorSession.findById(id);
            vendorSkidType.setVendor(vendor);
            vendorSession.create(vendorSkidType);
            setSuccess(true);
            setMessage("Created Vendor Skid Type");
        } catch (Exception e){
            logger.error("Could not create vendor skid type", e);
            setSuccess(false);
            setMessage("Could not create the Vendor Skid Type, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcVendorAdmin"})
    public String updateSkidType(){
        try {
            VendorSessionLocal vendorSession = getVendorSession();
            VendorSkidType dbst = vendorSession.findVendorSkidTypeById(id);
            dbst.setSkidtype(vendorSkidType.getSkidtype());
            vendorSession.update(dbst);
            setSuccess(true);
            setMessage("Updated Vendor Skid Type");
        } catch (Exception e){
            logger.error("Could not create vendor skid type", e);
            setSuccess(false);
            setMessage("Could not update the Vendor Skid Type, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcVendorAdmin"})
    public String deleteSkidType(){
        try {
            VendorSessionLocal vendorSession = getVendorSession();
            vendorSession.deleteVendorSkidType(id);
            setSuccess(true);
            setMessage("Deleted Vendor Skid Type");
        } catch (Exception e){
            logger.error("Could not create vendor skid type", e);
            setSuccess(false);
            setMessage("Could not delete the Vendor Skid Type, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcVendorAdmin", "BcVendorViewer"})
    public String listData(){
        setupListTable(); 
        try {
            VendorSessionLocal vendorSession = getVendorSession();
            if (quickSearch != null){
                Disjunction dis = Restrictions.disjunction();
                dis.add(Restrictions.ilike("vendorName", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("code", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("email1", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("email2", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("accountNumber", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("shippingCompany", quickSearch, MatchMode.ANYWHERE));
                queryInput.addAndCriterion(dis);
            }
            queryResults = new QueryResults(vendorSession.findAll(queryInput, "roles"));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not find all vendors", e);
        }
        return "queryresults";
    }

    private void setupListTable(){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("vendorName"));
        cd.add(new ColumnData("code"));
        cd.add(new ColumnData("accountNumber"));
        cd.add(new ColumnData("terms"));
        cd.add(new ColumnData("email1"));
        cd.add(new ColumnData("email2"));
        cd.add(new ColumnData("shippingCompany"));
        cd.add(new ColumnData("address1"));
        cd.add(new ColumnData("address2"));
        cd.add(new ColumnData("address3"));
        cd.add(new ColumnData("city"));
        cd.add(new ColumnData("state"));
        cd.add(new ColumnData("zip"));
        cd.add(new ColumnData("workPhone"));
        cd.add(new ColumnData("homePhone"));
        cd.add(new ColumnData("cellPhone"));
        cd.add(new ColumnData("fax"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50));
        cm.add(new ColumnModel("vendorName", "Name", 100));
        cm.add(new ColumnModel("code", "Code", 100));
        cm.add(new ColumnModel("accountNumber", "Account Number", 100));
        cm.add(new ColumnModel("terms", "Terms", 100));
        cm.add(new ColumnModel("email1", "email1", 100));
        cm.add(new ColumnModel("email2", "email2", 100));
        cm.add(new ColumnModel("shippingCompany", "Shipping Company", 100));
        cm.add(new ColumnModel("address1", "Address 1", 100));
        cm.add(new ColumnModel("address2", "Address 2", 100));
        cm.add(new ColumnModel("address3", "Address 3", 100));
        cm.add(new ColumnModel("state", "State", 100));
        cm.add(new ColumnModel("city", "City", 100));
        cm.add(new ColumnModel("zip", "Zip", 100));
        cm.add(new ColumnModel("workPhone", "Work Phone", 100));
        cm.add(new ColumnModel("homePhone", "Home Phone", 100));
        cm.add(new ColumnModel("cellPhone", "Cell Phone", 100));
        cm.add(new ColumnModel("fax", "Fax", 100));
        listTable.setColumnModels(cm);
        
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("long", "id"));
        filters.add(new Filter("string", "vendorName"));
        filters.add(new Filter("string", "code"));
        filters.add(new Filter("string", "accountNumber"));
        filters.add(new Filter("string", "terms"));
        filters.add(new Filter("string", "email1"));
        filters.add(new Filter("string", "email2"));
        filters.add(new Filter("string", "shippingCompany"));
        filters.add(new Filter("string", "address1"));
        filters.add(new Filter("string", "address2"));
        filters.add(new Filter("string", "address3"));
        filters.add(new Filter("string", "state"));
        filters.add(new Filter("string", "city"));
        filters.add(new Filter("string", "zip"));
        filters.add(new Filter("string", "workPhone"));
        filters.add(new Filter("string", "homePhone"));
        filters.add(new Filter("string", "cellPhone"));
        filters.add(new Filter("string", "fax"));
        listTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        if (getIsBcVendorAdmin()){
            buttons.add(new ToolbarButton("Create", "createButtonClick", "create_icon", "Create A New Vendor"));
            buttons.add(new ToolbarButton("Edit", "editButtonClick", "edit_icon", "Edit The Selected Vendor").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Delete", "deleteButtonClick", "delete_icon", "Delete The Selected Vendor").setSingleRowAction(true));
        }
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("vendorName");
        listTable.setDefaultSortDir(Table.SORT_DIR_ASC);
        
        // this is for the exports
        setExcelExportFileName("VendorExport");
        setExcelExportSheetName("Vendors");
        
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public Table getListTable() {
        return listTable;
    }

    public void setListTable(Table listTable) {
        this.listTable = listTable;
    }

    public VendorSkidType getVendorSkidType() {
        return vendorSkidType;
    }

    public void setVendorSkidType(VendorSkidType vendorSkidType) {
        this.vendorSkidType = vendorSkidType;
    }

    public String getQuickSearch() {
        return quickSearch;
    }

    public void setQuickSearch(String quickSearch) {
        this.quickSearch = quickSearch;
    }

    public Boolean getIsQuickSearch() {
        return isQuickSearch;
    }

    public void setIsQuickSearch(Boolean isQuickSearch) {
        this.isQuickSearch = isQuickSearch;
    }

    public String getQuickSearchLocation() {
        return quickSearchLocation;
    }

    public void setQuickSearchLocation(String quickSearchLocation) {
        this.quickSearchLocation = quickSearchLocation;
    }
    
}
