package com.bc.actions.bookcountry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.bc.actions.BaseAction;
import com.bc.ejb.ManifestSessionLocal;
import com.bc.orm.Manifest;
import com.bc.struts.QueryResults;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.table.Filter;
import com.bc.table.Table;
import com.bc.table.Toolbar;
import com.bc.table.ToolbarButton;
import com.bc.util.ActionRole;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="list", location="/WEB-INF/jsp/bookcountry/manifest/list.jsp"),
    @Result(name="searchlist", location="/WEB-INF/jsp/bookcountry/manifest/search.jsp"),
    @Result(name="searchwin", location="/WEB-INF/jsp/bookcountry/manifest/searchwindow.jsp"),    
    @Result(name="viewredirect", location="/secure/bookcountry/manifest!view.bc", type="redirect"),
    @Result(name="detail", location="/WEB-INF/jsp/bookcountry/manifest/detail.jsp"),
    @Result(name="crud", location="/WEB-INF/jsp/bookcountry/manifest/crud.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class ManifestAction extends BaseAction {
    
    private final Logger logger = Logger.getLogger(ManifestAction.class);

    private Manifest manifest;
    private Table listTable;
    private String quickSearch;
    private String quickSearchLocation;
    private Boolean isQuickSearch = false;
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String list(){
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String quickSearch(){
        isQuickSearch = true;
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcManifestAdmin"})
    public String create(){
        return "crud";
    }
    
    @ActionRole({"BcManifestAdmin"})
    public String createSubmit(){
        try {
            ManifestSessionLocal mSession = getManifestSession();
            manifest.setDate(Calendar.getInstance().getTime());
            mSession.create(manifest);
            id = manifest.getId();
            setSuccess(true);
            setMessage("Created New Manifest");
        } catch (Exception e){
            logger.error("Exception", e);
            setSuccess(false);
            setMessage("Could not create the Manifest, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcManifestAdmin"})
    public String edit(){
        ManifestSessionLocal mSession = getManifestSession();
        manifest = mSession.findById(id);
        return "crud";
    }
    
    @ActionRole({"BcManifestAdmin"})
    public String editSubmit(){
        try {
            ManifestSessionLocal mSession = getManifestSession();
            Manifest dbm = mSession.findById(manifest.getId());
            if (dbm != null){
                dbm.setName(manifest.getName());
                dbm.setComment(manifest.getComment());
                mSession.update(dbm);
                setSuccess(true);
                setMessage("Updated the manifest.");
            } else {
                logger.error("Could not find manifest to update");
                setSuccess(false);
                setMessage("Could not update the manifest, there was a system error.");
            }
        } catch (Exception e){
            logger.error("Could not update manifest id: "+id, e);
            setSuccess(false);
            setMessage("Could not update the manifest, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcManifestAdmin"})
    public String delete(){
        try {
            ManifestSessionLocal mSession = getManifestSession();
            mSession.delete(id);
            setSuccess(true);
            setMessage("Deleted manifest");
        } catch (Exception e){
            logger.error("Could not delete manifest id: "+id);
            setSuccess(false);
            setMessage("Could not delete manifest, there was a system error.");
        }
        return "status";
    }

    private void setupSearchNames() {
        for(Filter f : listTable.getFilters()) {
            ColumnModel cm = listTable.getColumnModel(f.getName());
            searchNames.put(f.getName(), cm.getHeader());
        }        
        // additional manifest items search
        for(Filter f : listTable.getAdditionalSearch()) {
            searchNames.put(f.getName(), f.getDisplay());
        }        
    }
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String searchWin(){
        setupListTable();
        setupSearchNames();
        return "searchwin";
    }
        
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String search(){
        setupListTable();
        setupSearchNames();
        return "searchlist";
    }
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String searchData(){
        setupListTable(); 
        setupSearchNames();
        try {
            ManifestSessionLocal mSession = getManifestSession();
            if (search != null){
                for (Criterion crit : search.getRestrictions(listTable)){
                    //logger.info("adding crit "+crit.toString());
                    queryInput.addAndCriterion(crit);
                }
            }
            HashMap<String, String> aliases = new HashMap<String, String>();
            aliases.put("manifestItems", "manifestItems");
            queryResults = new QueryResults(mSession.findAll(queryInput, aliases));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list search data", e);
        }
        return "queryresults";
    }
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String listData(){
        setupListTable(); 
        try {
            ManifestSessionLocal mSession = getManifestSession();
            if (quickSearch != null){
                Disjunction dis = Restrictions.disjunction();
                dis.add(Restrictions.ilike("name", quickSearch, MatchMode.ANYWHERE));
                dis.add(Restrictions.ilike("comment", quickSearch, MatchMode.ANYWHERE));
                queryInput.addAndCriterion(dis);
            }
            queryResults = new QueryResults(mSession.findAll(queryInput));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list data for manifests", e);
        }
        return "queryresults";
    }
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String detail(){
        ManifestSessionLocal mSession = getManifestSession();
        manifest = mSession.findById(id);
        return "detail";
    }

    private void setupListTable(){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("name"));
        cd.add(new ColumnData("date").setType("date"));
        cd.add(new ColumnData("totalItems").setType("int"));
        cd.add(new ColumnData("totalQuantity").setType("int"));
        cd.add(new ColumnData("comment"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50));
        cm.add(new ColumnModel("name", "Name", 300));
        cm.add(new ColumnModel("totalItems", "Total Items", 100));
        cm.add(new ColumnModel("totalQuantity", "Total Quantity", 100));
        cm.add(new ColumnModel("comment", "Comment", 325));
        cm.add(new ColumnModel("date", "Date", 150).setRenderer("dateRenderer"));
        listTable.setColumnModels(cm);
        
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("long", "id"));
        filters.add(new Filter("integer", "totalItems"));
        filters.add(new Filter("integer", "totalQuantity"));
        filters.add(new Filter("string", "name"));
        filters.add(new Filter("string", "comment"));
        filters.add(new Filter("date", "date"));
        listTable.setFilters(filters);
        
        List<Filter> moreSearch = new ArrayList<Filter>();
        moreSearch.add(new Filter("string", "manifestItems.isbn", "Manifest Item: ISBN"));
        moreSearch.add(new Filter("string", "manifestItems.isbn", "Manifest Item: Title"));
        moreSearch.add(new Filter("int", "manifestItems.quantity", "Manifest Item: Quantity"));
        listTable.setAdditionalSearch(moreSearch);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        if (getIsBcManifestAdmin()) {
            buttons.add(new ToolbarButton("Create", "createButtonClick", "create_icon", "Create A New Manifest"));
            buttons.add(new ToolbarButton("Edit", "editButtonClick", "edit_icon", "Edit The Selected Manifest").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Delete", "deleteButtonClick", "delete_icon", "Delete The Selected Manifest").setSingleRowAction(true));
            buttons.add(new ToolbarButton("History", "historyButtonClick", "calendar_icon", "View The Selected Manifests History Of Changes").setSingleRowAction(true));
        }
        buttons.add(new ToolbarButton("View", "viewButtonClick", "view_icon", "View The Selected Manifest Detail Page").setSingleRowAction(true));
        buttons.add(new ToolbarButton("View In New Window", "viewNewWinButtonClick", "view_icon", "View The Selected Manifest Detail Page In A New Window").setSingleRowAction(true));
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("id");
        listTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryManifestExport");
        setExcelExportSheetName("Manifest");
        
    }

    public Table getListTable() {
        return listTable;
    }

    public void setListTable(Table listTable) {
        this.listTable = listTable;
    }

    public Manifest getManifest() {
        return manifest;
    }

    public void setManifest(Manifest manifest) {
        this.manifest = manifest;
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
