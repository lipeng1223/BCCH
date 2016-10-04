package com.bc.actions.bookcountry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//import jxl.Sheet;
//import jxl.Workbook;
//import jxl.format.UnderlineStyle;
//import jxl.write.DateFormat;
//import jxl.write.DateTime;
//import jxl.write.Label;
//import jxl.write.NumberFormats;
//import jxl.write.WritableCellFormat;
//import jxl.write.WritableFont;
//import jxl.write.WritableSheet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormat;

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
import com.bc.dao.DaoResults;
import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.ejb.LifoSessionLocal;
import com.bc.ejb.ReceivingSessionLocal;
import com.bc.ejb.VendorSessionLocal;
import com.bc.excel.ExcelConstants;
import com.bc.excel.ExcelExtraDataWriter;
import com.bc.orm.CustomerOrder;
import com.bc.orm.InventoryItem;
import com.bc.orm.Received;
import com.bc.orm.ReceivedItem;
import com.bc.orm.Vendor;
import com.bc.struts.QueryInput;
import com.bc.struts.QueryResults;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.table.Filter;
import com.bc.table.Table;
import com.bc.table.Toolbar;
import com.bc.table.ToolbarButton;
import com.bc.util.ActionRole;
import com.bc.util.cache.VendorCache;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="list", location="/WEB-INF/jsp/bookcountry/receiving/list.jsp"),
    @Result(name="searchlist", location="/WEB-INF/jsp/bookcountry/receiving/search.jsp"),
    @Result(name="searchwin", location="/WEB-INF/jsp/bookcountry/receiving/searchwindow.jsp"),    
    @Result(name="view", location="/WEB-INF/jsp/bookcountry/receiving/view.jsp"),
    @Result(name="detail", location="/WEB-INF/jsp/bookcountry/receiving/detail.jsp"),
    @Result(name="crud", location="/WEB-INF/jsp/bookcountry/receiving/crud.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="importcost", location="/WEB-INF/jsp/bookcountry/receiving/importcost.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class ReceivingAction extends BaseAction {
    
    private final Logger logger = Logger.getLogger(ReceivingAction.class);

    private Received receiving;
    private Table listTable;
    private String dateString;
    private Long vendorId;
    private String filename;
    private Boolean exportCost = false;
    private String quickSearch;
    private String quickSearchLocation;
    private Boolean isQuickSearch = false;
    
    private List<Vendor> vendors;
    
    private File upload;//The actual file
    private String uploadContentType; //The content type of the file
    private String uploadFileName; //The uploaded file name    
    
    private FormulaEvaluator evaluator;
    
    @ActionRole({"BcRecAdmin", "BcRecViewer"})
    public String list(){
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcRecAdmin", "BcRecViewer"})
    public String quickSearch(){
        isQuickSearch = true;
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcRecAdmin"})
    public String create(){
        vendors = VendorCache.getVendors();
        return "crud";
    }
    
    @ActionRole({"BcRecAdmin"})
    public String createSubmit(){
        if (dateString != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                receiving.setPoDate(sdf.parse(dateString));
            } catch (Exception e){
                logger.error("invalid date: "+dateString);
                receiving.setPoDate(Calendar.getInstance().getTime());
            }
        }
        try {
            VendorSessionLocal vSession = getVendorSession();
            ReceivingSessionLocal rSession = getReceivingSession();
            if (vendorId > -1){
                Vendor v = vSession.findById(vendorId);
                if (v != null) {
                    receiving.setVendor(v);
                    receiving.setVendorCode(v.getCode());
                }
            }
            receiving.setPosted(false);
            rSession.create(receiving);
            id = receiving.getId();
            setSuccess(true);
        } catch (Exception e){
            setSuccess(false);
            setMessage("Could not create the receiving, there was a system error.");
            logger.error("Could not create receiving", e);
        }
        return "status";
    }
    
    @ActionRole({"BcRecAdmin"})
    public String edit(){
        vendors = VendorCache.getVendors();
        ReceivingSessionLocal rSession = getReceivingSession();
        receiving = rSession.findById(id, "vendor");
        return "crud";
    }
    
    @ActionRole({"BcRecAdmin"})
    public String editSubmit(){
        if (dateString != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                receiving.setPoDate(sdf.parse(dateString));
            } catch (Exception e){
                logger.error("invalid date: "+dateString);
                receiving.setPoDate(Calendar.getInstance().getTime());
            }
        }
        try {
            VendorSessionLocal vSession = getVendorSession();
            ReceivingSessionLocal rSession = getReceivingSession();
            if (vendorId > -1){
                Vendor v = vSession.findById(vendorId);
                if (v != null) {
                    receiving.setVendor(v);
                    receiving.setVendorCode(v.getCode());
                }
            }
            Received dbr = rSession.findById(receiving.getId(), "vendor");
            dbr.setPoDate(receiving.getPoDate());
            dbr.setVendor(receiving.getVendor());
            dbr.setVendorCode(receiving.getVendorCode());
            dbr.setPoNumber(receiving.getPoNumber());
            dbr.setPublisher(receiving.getPublisher());
            dbr.setComment(receiving.getComment());
            
            rSession.update(dbr);
            id = receiving.getId();
            setSuccess(true);
        } catch (Exception e){
            setSuccess(false);
            setMessage("Could not update the receiving, there was a system error.");
            logger.error("Could not update receiving", e);
        }
        return "status";
    }
    
    @ActionRole({"BcRecAdmin"})
    public String delete(){
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            receiving = rSession.findById(id);
            if (receiving.getPosted()){
                setSuccess(false);
                setMessage("This Receiving has been Posted, refresh the page.");
                return "status";
            }
            
            rSession.delete(id);
            setSuccess(true);
        } catch (Exception e){
            setSuccess(false);
            setMessage("Could not delete the receiving, there was a system error.");
            logger.error("Could not delete receiving", e);
        }
        return "status";
    }

    @ActionRole({"BcRecAdmin"})
    public String post(){
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            receiving = rSession.findById(id);
            
            if (receiving.getPosted()){
                setSuccess(false);
                setMessage("This Receiving has been Posted, refresh the page.");
                return "status";
            }
            
            if (receiving.getHolding()){
                setSuccess(false);
                setMessage("This Receiving is a Holding receiving, it cannot be posted.");
                return "status";
            }
            
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                Date postDate = sdf.parse(dateString);
                receiving.setPostDate(postDate);
            } catch (Exception e){
                setSuccess(false);
                setMessage("You must provide a valid Post Date.");
                return "status";
            }
            
            receiving.setPosted(true);
            rSession.update(receiving);
            rSession.updateInventoryItemsForPost(id);
            
            rSession.recalculateReceived(receiving.getId());
            
            setSuccess(true);
        } catch (Exception e){
            setSuccess(false);
            setMessage("Could not post the receiving, there was a system error.");
            logger.error("Could not post receiving", e);
        }
        return "status";
    }

    private void setupSearchNames() {
        for(Filter f : listTable.getFilters()) {
            ColumnModel cm = listTable.getColumnModel(f.getName());
            searchNames.put(f.getName(), cm.getHeader());
        }        
        // additional receiving items search
        for(Filter f : listTable.getAdditionalSearch()) {
            searchNames.put(f.getName(), f.getDisplay());
        }        
    }
    
    @ActionRole({"BcRecAdmin", "BcRecViewer"})
    public String searchWin(){
        setupListTable();
        setupSearchNames();
        return "searchwin";
    }

    @ActionRole({"BcRecAdmin", "BcRecViewer"})
    public String search(){
        setupListTable();
        setupSearchNames();
        return "searchlist";
    }
    
    @ActionRole({"BcRecAdmin", "BcRecViewer"})
    public String searchData(){
        //logger.info("start");
        setupListTable(); 
        setupSearchNames();
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            if (search != null){
                for (Criterion crit : search.getRestrictions(listTable)){
                    //logger.info("adding crit "+crit.toString());
                    queryInput.addAndCriterion(crit);
                }
            }
            HashMap<String, String> aliases = new HashMap<String, String>();
            aliases.put("receivedItems", "receivedItems");
            queryResults = new QueryResults(rSession.findAll(queryInput, aliases));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list search data", e);
        }
        //logger.info("end");
        return "queryresults";
    }
    
    @ActionRole({"BcOrderAdmin"})
    public String unpost(){
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            receiving = rSession.findById(id);
            if (receiving == null){
                setSuccess(false);
                setMessage("Could not unpost the order, the order no longer exists in the system.");
            }
            logger.info("unposting order id: "+id);
            receiving.setPosted(false);
            rSession.update(receiving);
            rSession.recalculateReceived(receiving.getId());
            logger.info("unposted order id: "+id);
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not get unpost the order", e);
            setSuccess(false);
            setMessage("Could not unpost the order, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcRecAdmin", "BcRecViewer"})
    public String listData(){
        if (exportWithItemsToExcel){
            try {
                ReceivingSessionLocal rSession = getReceivingSession();
                setupListTable();
                setupSearchNames();
                queryInput = new QueryInput();
                queryInput.setStart(0);
                int limit = 250;
                queryInput.setLimit(limit);
                if (search != null){
                    for (Criterion crit : search.getRestrictions(listTable)){
                        //logger.info("adding crit "+crit.toString());
                        queryInput.addAndCriterion(crit);
                    }
                }
                if (quickSearch != null){
                    Disjunction dis = Restrictions.disjunction();
                    dis.add(Restrictions.ilike("poNumber", quickSearch, MatchMode.ANYWHERE));
                    dis.add(Restrictions.ilike("vendorCode", quickSearch, MatchMode.ANYWHERE));
                    queryInput.addAndCriterion(dis);
                }
                HashMap<String, String> aliases = new HashMap<String, String>();
                aliases.put("receivedItems", "receivedItems");
                List<Long> oids = new ArrayList<Long>();
                long total = 0;
                int loop = 0;
                boolean keepLooping = true;
                while (keepLooping){
                    queryInput.setStart(loop * limit);
                    loop++;
                    DaoResults recResults = rSession.findAll(queryInput, aliases);
                    //boolean breakout = false;
                    for (Received rec : (List<Received>)recResults.getData()){
                        oids.add(rec.getId());
                        //total += rec.getTotalItems();
                        //if (total >= ExcelConstants.EXCEL_MAX_ROW) {
                        //    breakout = true;
                        //    break;
                        //}
                    }
                    //if (breakout) break;
                    //logger.info("loop start: "+queryInput.getStart()+" data size: "+orderResults.getDataSize()+" total items: "+total);
                    keepLooping = recResults.getDataSize() == limit;
                }
                /*
                if (total >= ExcelConstants.EXCEL_MAX_ROW) {
                    // break out and return
                    logger.error("Could not export received with items, went over excel row limit");
                    queryResults = new QueryResults(new DaoResults());
                    queryResults.setTableConfig(listTable, queryInput.getFilterParams());
                    exportLimitExceeded = true;
                    return "queryresults";
                }
                */
                
                setupWithItemListTable();
                queryResults = new QueryResults(rSession.findAll(queryInput, aliases));
                //long start = System.currentTimeMillis();
                queryInput = new QueryInput();
                queryInput.setStart(0);
                limit = 500;
                queryInput.setLimit(limit);
                queryInput.setExportToExcel(true);
                if (oids.size() > 0)
                    queryInput.addAndCriterion(Restrictions.in("received.id", oids));
                // add any additional order item specific criteria
                List<Criterion> crits = search.getRestrictions(listTable, "receivedItems.");
                for (Criterion crit : crits){
                    queryInput.addAndCriterion(crit);
                }
                aliases = new HashMap<String, String>();
                aliases.put("received", "received");
                
                DaoResults rolledUp = new DaoResults(new ArrayList());
                loop = 0;
                keepLooping = true;
                while (keepLooping){
                    queryInput.setStart(loop * limit);
                    loop++;
                    DaoResults recResults = rSession.findAllItems(queryInput, aliases, "received");
                    rolledUp.getData().addAll(recResults.getData());
                    keepLooping = recResults.getDataSize() == limit;
                }
                queryResults = new QueryResults(rolledUp);
                
                queryResults.setTableConfig(listTable, queryInput.getFilterParams());
                //long end = System.currentTimeMillis();
                //logger.info("timing: "+((end-start) / 1000.0));
            } catch (Exception e){
                logger.error("Could not listItemData", e);
            }
        } else {
            setupListTable(); 
            try {
                ReceivingSessionLocal rSession = getReceivingSession();
                if (quickSearch != null){
                    Disjunction dis = Restrictions.disjunction();
                    dis.add(Restrictions.ilike("poNumber", quickSearch, MatchMode.ANYWHERE));
                    dis.add(Restrictions.ilike("vendorCode", quickSearch, MatchMode.ANYWHERE));
                    queryInput.addAndCriterion(dis);
                }
                queryResults = new QueryResults(rSession.findAll(queryInput));
                queryResults.setTableConfig(listTable, queryInput.getFilterParams());
            } catch (Exception e){
                logger.error("Could not listData", e);
            }
        }
        return "queryresults";
    }
    
    @ActionRole({"BcRecAdmin", "BcRecViewer"})
    public String listItemData(){
        try {
            ReceivingSessionLocal rSession = getReceivingSession();
            receiving = rSession.findById(id);
            if (exportCost) {
                setupItemCostListTable(receiving.getPosted());
                setExtraDataWriter(new CostExportExtraDataWriter(receiving));
            } else { 
                setupItemListTable(receiving.getPosted());
            }
            //long start = System.currentTimeMillis();
            queryInput.addAndCriterion(Restrictions.eq("received", receiving));
            queryResults = new QueryResults(rSession.findAllItems(queryInput, "inventoryItem"));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
            //long end = System.currentTimeMillis();
            //logger.info("timing: "+((end-start) / 1000.0));
        } catch (Exception e){
            logger.error("Could not listItemData", e);
        }
        return "queryresults";
    }

    @ActionRole({"BcRecAdmin", "BcRecViewer"})
    public String view(){
        ReceivingSessionLocal rSession = getReceivingSession();
        receiving = rSession.findById(id, "vendor");
        setupItemListTable(receiving.getPosted());
        return "view";
    }
    
    @ActionRole({"BcRecAdmin", "BcRecViewer"})
    public String detail(){
        ReceivingSessionLocal rSession = getReceivingSession();
        receiving = rSession.findById(id, "vendor");
        return "detail";
    }

    private void setupListTable(){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("createTime").setType("date"));
        cd.add(new ColumnData("posted").setType("boolean"));
        cd.add(new ColumnData("transno"));
        cd.add(new ColumnData("poDate").setType("date"));
        cd.add(new ColumnData("poNumber"));
        cd.add(new ColumnData("publisherCode"));
        cd.add(new ColumnData("vendorCode"));
        cd.add(new ColumnData("poTotal").setType("float"));
        cd.add(new ColumnData("clerk"));
        cd.add(new ColumnData("duedate").setType("date"));
        cd.add(new ColumnData("postDate").setType("date"));
        //cd.add(new ColumnData("skid").setType("boolean"));
        //cd.add(new ColumnData("skidIsbn"));
        //cd.add(new ColumnData("skidBreak"));
        cd.add(new ColumnData("publisher"));
        cd.add(new ColumnData("skidCondition"));
        cd.add(new ColumnData("totalItems").setType("int"));
        cd.add(new ColumnData("totalQuantity").setType("int"));
        cd.add(new ColumnData("totalOrderedQuantity").setType("int"));
        cd.add(new ColumnData("totalExtendedCost").setType("float"));
        cd.add(new ColumnData("holding").setType("boolean"));
        //cd.add(new ColumnData("totalSellPrice"));
        cd.add(new ColumnData("comment"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50));
        cm.add(new ColumnModel("createTime", "Created", 150).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("poDate", "PO Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("poNumber", "PO", 100));
        cm.add(new ColumnModel("posted", "Posted", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("postDate", "Post Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("totalItems", "Total Items", 100));
        cm.add(new ColumnModel("totalQuantity", "Total Quantity", 100));
        cm.add(new ColumnModel("totalOrderedQuantity", "Total Ordered Quantity", 100));
        cm.add(new ColumnModel("totalExtendedCost", "Total Extended Cost", 100).setRenderer("moneyRenderer"));
        //cm.add(new ColumnModel("totalSellPrice", "Total Sell Price", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("publisher", "Publisher", 100));
        cm.add(new ColumnModel("publisherCode", "Publisher Code", 100));
        cm.add(new ColumnModel("vendorCode", "Vendor Code", 100));
        cm.add(new ColumnModel("transno", "Trans No", 100));
        cm.add(new ColumnModel("poTotal", "PO Total", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("clerk", "Clerk", 100));
        cm.add(new ColumnModel("duedate", "Due Date", 100).setRenderer("dateRenderer"));
        //cm.add(new ColumnModel("skid", "Skid", 100));
        //cm.add(new ColumnModel("skidIsbn", "Skid Isbn", 100));
        //cm.add(new ColumnModel("skidBreak", "Skid Break", 100));
        cm.add(new ColumnModel("skidCondition", "Skid Condition", 100));
        cm.add(new ColumnModel("holding", "Holding", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("comment", "Comment", 300));
        listTable.setColumnModels(cm);
        
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("long", "id"));
        filters.add(new Filter("date", "createTime"));
        filters.add(new Filter("string", "poNumber"));
        filters.add(new Filter("integer", "totalItems"));
        filters.add(new Filter("integer", "totalQuantity"));
        filters.add(new Filter("integer", "totalOrderedQuantity"));
        filters.add(new Filter("float", "totalExtendedCost"));
        //filters.add(new Filter("float", "totalSellPrice"));
        filters.add(new Filter("string", "publisher"));
        filters.add(new Filter("string", "publisherCode"));
        filters.add(new Filter("string", "vendorCode"));
        filters.add(new Filter("boolean", "posted"));
        filters.add(new Filter("boolean", "holding"));
        filters.add(new Filter("date", "postDate"));
        filters.add(new Filter("string", "transno"));
        filters.add(new Filter("date", "poDate"));
        filters.add(new Filter("float", "poTotal"));
        filters.add(new Filter("string", "clerk"));
        filters.add(new Filter("date", "duedate"));
        //filters.add(new Filter("boolean", "skid"));
        //filters.add(new Filter("string", "skidIsbn"));
        //filters.add(new Filter("boolean", "skidBreak"));
        filters.add(new Filter("string", "skidCondition"));
        filters.add(new Filter("string", "comment"));
        listTable.setFilters(filters);
        
        List<Filter> moreSearch = new ArrayList<Filter>();
        moreSearch.add(new Filter("string", "receivedItems.isbn", "Received Item: ISBN"));
        moreSearch.add(new Filter("string", "receivedItems.title", "Received Item: Title"));
        moreSearch.add(new Filter("string", "receivedItems.bin", "Received Item: Bin"));
        moreSearch.add(new Filter("int", "receivedItems.quantity", "Received Item: Quantity"));
        moreSearch.add(new Filter("int", "receivedItems.orderedQuantity", "Received Item: Ordered Quantity"));
        listTable.setAdditionalSearch(moreSearch);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        if (getIsBcReceivingAdmin()){
            buttons.add(new ToolbarButton("Create", "createRecButtonClick", "create_icon", "Create A New Receiving"));
            buttons.add(new ToolbarButton("Edit", "editButtonClick", "edit_icon", "Edit The Selected Receiving").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Delete", "deleteButtonClick", "delete_icon", "Delete The Selected Receiving").setSingleRowAction(true));
            buttons.add(new ToolbarButton("History", "historyButtonClick", "calendar_icon", "View The Selected Receiving History Of Changes").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Unpost", "unpostButtonClick", "table_delete_icon", "Unpost this receiving").setSingleRowAction(true));
        }
        buttons.add(new ToolbarButton("View", "viewButtonClick", "view_icon", "View The Selected Receivings Detail Page").setSingleRowAction(true));
        buttons.add(new ToolbarButton("View In New Window", "viewNewWinButtonClick", "view_icon", "View The Selected Receivings Detail Page In A New Window").setSingleRowAction(true));
        buttons.add(new ToolbarButton("Items", "exportWithItemsButtonClick", "excel_icon", "Export these Receivings with their items"));
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("id");
        listTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryReceivingExport");
        setExcelExportSheetName("Receivings");
        
    }
    
    
    private void setupItemListTable(Boolean posted){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        listTable.setPageSize(250);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("inventoryItem.id", "inventoryItem_id"));
        cd.add(new ColumnData("createTime").setType("date"));
        cd.add(new ColumnData("title"));
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("isbn13"));
        cd.add(new ColumnData("cond"));
        cd.add(new ColumnData("bin"));
        cd.add(new ColumnData("quantity").setType("int"));
        cd.add(new ColumnData("orderedQuantity").setType("int"));
        cd.add(new ColumnData("available").setType("int"));
        cd.add(new ColumnData("inventoryItem.available", "inventoryItem_available").setType("int"));
        cd.add(new ColumnData("listPrice").setType("float"));
        cd.add(new ColumnData("sellPrice").setType("float"));
        cd.add(new ColumnData("cost").setType("float"));
        cd.add(new ColumnData("extendedCost").setType("float"));
        cd.add(new ColumnData("discount").setType("float"));
        cd.add(new ColumnData("type"));
        cd.add(new ColumnData("bookType"));
        cd.add(new ColumnData("coverType"));
        cd.add(new ColumnData("breakroom"));
        cd.add(new ColumnData("lbs").setType("int"));
        cd.add(new ColumnData("lbsPrice").setType("float"));
        cd.add(new ColumnData("lbsCost").setType("float"));
        cd.add(new ColumnData("pieces").setType("int"));
        cd.add(new ColumnData("skid"));
        cd.add(new ColumnData("skidPrice").setType("float"));
        cd.add(new ColumnData("skidCost").setType("float"));
        cd.add(new ColumnData("skidPieceCount").setType("int"));
        cd.add(new ColumnData("skidPiecePrice").setType("float"));
        cd.add(new ColumnData("skidPieceCost").setType("float"));
        cd.add(new ColumnData("percentageList").setType("float"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50));
        cm.add(new ColumnModel("createTime", "Created", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("title", "Title", 200));
        cm.add(new ColumnModel("isbn", "ISBN", 80));
        cm.add(new ColumnModel("isbn13", "ISBN13", 100));
        cm.add(new ColumnModel("cond", "Condition", 50).setRenderer("conditionRenderer"));
        cm.add(new ColumnModel("bin", "Bin", 90));
        cm.add(new ColumnModel("quantity", "Quantity", 50));
        cm.add(new ColumnModel("orderedQuantity", "Ordered Quantity", 50));
        cm.add(new ColumnModel("available", "Available", 50));
        cm.add(new ColumnModel("inventoryItem_available", "Available Inventory", 90));
        cm.add(new ColumnModel("listPrice", "List Price", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("sellPrice", "Sell Price", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("cost", "Cost", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("extendedCost", "Extended Cost", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("discount", "Discount", 90).setRenderer("percentRenderer"));
        cm.add(new ColumnModel("type", "Type", 70));
        cm.add(new ColumnModel("breakRoom", "Break Room", 70).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("bookType", "Book Type", 70));
        cm.add(new ColumnModel("coverType", "Cover Type", 70));
        cm.add(new ColumnModel("lbs", "Lbs", 70));
        cm.add(new ColumnModel("lbsPrice", "Lbs Price", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("lbsCost", "Lbs Cost", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("pieces", "Pieces", 70));
        cm.add(new ColumnModel("skid", "Skid", 70).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("skidPieceCount", "Skid Piece Count", 70));
        cm.add(new ColumnModel("skidPiecePrice", "Skid Piece Price", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("skidPieceCost", "Skid Piece Cost", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("percentageList", "Percent List", 70));
        listTable.setColumnModels(cm);
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("date", "createTime"));
        filters.add(new Filter("string", "title"));
        filters.add(new Filter("string", "isbn"));
        filters.add(new Filter("string", "isbn13"));
        filters.add(new Filter("string", "cond"));
        filters.add(new Filter("string", "bin"));
        filters.add(new Filter("integer", "quantity"));
        filters.add(new Filter("integer", "orderedQuantity"));
        filters.add(new Filter("integer", "available"));
        filters.add(new Filter("float", "listPirce"));
        filters.add(new Filter("float", "sellPrice"));
        filters.add(new Filter("float", "cost"));
        filters.add(new Filter("float", "extendedCost"));
        filters.add(new Filter("float", "discount"));
        filters.add(new Filter("string", "type"));
        filters.add(new Filter("boolean", "breakRoom"));
        filters.add(new Filter("string", "bookType"));
        filters.add(new Filter("string", "coverType"));
        filters.add(new Filter("integer", "lbs"));
        filters.add(new Filter("float", "lbsPrice"));
        filters.add(new Filter("float", "lbsCost"));
        filters.add(new Filter("integer", "pieces"));
        filters.add(new Filter("boolean", "skid"));
        filters.add(new Filter("integer", "skidPieceCount"));
        filters.add(new Filter("float", "skidPiecePrice"));
        filters.add(new Filter("float", "skidPieceCost"));
        filters.add(new Filter("float", "percentageList"));
        listTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        if (posted == null || !posted){
            listTable.setMultiselect(true);
            if (getIsBcReceivingAdmin()){
                buttons.add(new ToolbarButton("Create", "createItemButtonClick", "create_icon", "Create A New Receiving Item"));
                buttons.add(new ToolbarButton("Edit", "editItemButtonClick", "edit_icon", "Edit The Selected Receiving Item").setSingleRowAction(true));
                buttons.add(new ToolbarButton("Delete", "deleteItemButtonClick", "delete_icon", "Delete The Selected Receiving Item").setRowAction(true));
                buttons.add(new ToolbarButton("History", "itemHistoryButtonClick", "calendar_icon", "View The Selected Receiving Items History Of Changes").setSingleRowAction(true));
                buttons.add(new ToolbarButton("Fast", "fastRecItemButtonClick", "create_icon", "Fast Receiving Item"));
            }
        }
        buttons.add(new ToolbarButton("Inventory", "viewInvItemButtonClick", "view_icon", "View Inventory Item").setSingleRowAction(true));
        buttons.add(new ToolbarButton("Inventory - New Window", "viewInvItemNewWindowButtonClick", "view_icon", "View Inventory Item New Window").setSingleRowAction(true));
        if (posted == null || !posted){
            if (getIsBcReceivingAdmin()){
                buttons.add(new ToolbarButton().setRight(true));
                buttons.add(new ToolbarButton("Import", "importItemButtonClick", "down_arrow_icon", "Import Receiving Items From Excel"));
                buttons.add(new ToolbarButton().setSeparator(true));
            }
        }
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("id");
        listTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryReceivingExport");
        setExcelExportSheetName("Receiving");
        
    }
    
    private void setupWithItemListTable(){
        listTable = new Table();
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        
        cd.add(new ColumnData("received.id").setType("int"));
        cd.add(new ColumnData("received.createTime").setType("date"));
        cd.add(new ColumnData("received.posted"));
        cd.add(new ColumnData("received.transno"));
        cd.add(new ColumnData("received.poDate").setType("date"));
        cd.add(new ColumnData("received.poNumber"));
        cd.add(new ColumnData("received.publisherCode"));
        cd.add(new ColumnData("received.vendorCode"));
        cd.add(new ColumnData("received.poTotal").setType("float"));
        cd.add(new ColumnData("received.clerk"));
        cd.add(new ColumnData("received.duedate").setType("date"));
        cd.add(new ColumnData("received.postDate").setType("date"));
        //cd.add(new ColumnData("received.skid"));
        //cd.add(new ColumnData("received.skidIsbn"));
        //cd.add(new ColumnData("received.skidBreak"));
        //cd.add(new ColumnData("received.skidCondition"));
        cd.add(new ColumnData("received.publisher"));
        cd.add(new ColumnData("received.totalItems").setType("int"));
        cd.add(new ColumnData("received.totalQuantity").setType("int"));
        cd.add(new ColumnData("received.totalOrderedQuantity").setType("int"));
        cd.add(new ColumnData("received.totalCost").setType("float"));
        cd.add(new ColumnData("received.totalSellPrice").setType("float"));
        cd.add(new ColumnData("received.comment"));
        
        cd.add(new ColumnData("title"));
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("isbn13"));
        cd.add(new ColumnData("cond"));
        cd.add(new ColumnData("bin"));
        cd.add(new ColumnData("quantity").setType("int"));
        cd.add(new ColumnData("orderedQuantity").setType("int"));
        cd.add(new ColumnData("available").setType("int"));
        cd.add(new ColumnData("listPrice").setType("float"));
        cd.add(new ColumnData("sellPrice").setType("float"));
        cd.add(new ColumnData("cost").setType("float"));
        cd.add(new ColumnData("discount").setType("float"));
        cd.add(new ColumnData("type"));
        cd.add(new ColumnData("bookType"));
        cd.add(new ColumnData("coverType"));
        cd.add(new ColumnData("breakroom"));
        cd.add(new ColumnData("lbs").setType("int"));
        cd.add(new ColumnData("lbsPrice").setType("float"));
        cd.add(new ColumnData("lbsCost").setType("float"));
        cd.add(new ColumnData("pieces").setType("int"));
        cd.add(new ColumnData("skid"));
        cd.add(new ColumnData("skidPrice").setType("float"));
        cd.add(new ColumnData("skidCost").setType("float"));
        cd.add(new ColumnData("skidPieceCount").setType("int"));
        cd.add(new ColumnData("skidPiecePrice").setType("float"));
        cd.add(new ColumnData("skidPieceCost").setType("float"));
        cd.add(new ColumnData("percentageList").setType("float"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        
        cm.add(new ColumnModel("received.id", "ID", 50));
        cm.add(new ColumnModel("received.createTime", "Created", 150).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("received.poDate", "PO Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("received.poNumber", "PO", 100));
        cm.add(new ColumnModel("received.totalItems", "Total Items", 100));
        cm.add(new ColumnModel("received.totalQuantity", "Total Quantity", 100));
        cm.add(new ColumnModel("received.totalOrderedQuantity", "Total Ordered Quantity", 100));
        cm.add(new ColumnModel("received.totalCost", "Total Cost", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("received.totalSellPrice", "Total Sell Price", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("received.publisher", "Publisher", 100));
        cm.add(new ColumnModel("received.publisherCode", "Publisher Code", 100));
        cm.add(new ColumnModel("received.vendorCode", "Vendor Code", 100));
        cm.add(new ColumnModel("received.posted", "Posted", 100).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("received.postDate", "Post Date", 100).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("received.transno", "Trans No", 100));
        cm.add(new ColumnModel("received.poTotal", "PO Total", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("received.clerk", "Clerk", 100));
        cm.add(new ColumnModel("received.duedate", "Due Date", 100).setRenderer("dateRenderer"));
        //cm.add(new ColumnModel("received.skid", "Skid", 100));
        //cm.add(new ColumnModel("received.skidIsbn", "Skid Isbn", 100));
        //cm.add(new ColumnModel("received.skidBreak", "Skid Break", 100));
        //cm.add(new ColumnModel("received.skidCondition", "Skid Condition", 100));
        cm.add(new ColumnModel("received.comment", "Comment", 300));
        
        cm.add(new ColumnModel("isbn", "ISBN", 80));
        cm.add(new ColumnModel("isbn13", "ISBN13", 100));
        cm.add(new ColumnModel("cond", "Condition", 50));
        cm.add(new ColumnModel("bin", "Bin", 90));
        cm.add(new ColumnModel("quantity", "Quantity", 50));
        cm.add(new ColumnModel("orderedQuantity", "Ordered Quantity", 50));
        cm.add(new ColumnModel("available", "Available", 50));
        cm.add(new ColumnModel("listPrice", "List Price", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("sellPrice", "Sell Price", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("title", "Title", 200));
        cm.add(new ColumnModel("cost", "Cost", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("discount", "Discount", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("type", "Type", 70));
        cm.add(new ColumnModel("breakRoom", "Break Room", 70).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("bookType", "Book Type", 70));
        cm.add(new ColumnModel("coverType", "Cover Type", 70));
        cm.add(new ColumnModel("lbs", "Lbs", 70));
        cm.add(new ColumnModel("lbsPrice", "Lbs Price", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("lbsCost", "Lbs Cost", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("pieces", "Pieces", 70));
        cm.add(new ColumnModel("skid", "Skid", 70).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("skidPieceCount", "Skid Piece Count", 70));
        cm.add(new ColumnModel("skidPiecePrice", "Skid Piece Price", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("skidPieceCost", "Skid Piece Cost", 70).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("percentageList", "Percent List", 70));
        listTable.setColumnModels(cm);
        
        listTable.setDefaultSortCol("received.createTime");
        listTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryReceivingWithItemsExport");
        setExcelExportSheetName("Receivings");
        
    }
    
    private void setupItemCostListTable(Boolean posted){
        listTable = new Table();
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("isbn13"));
        cd.add(new ColumnData("cond"));
        cd.add(new ColumnData("title"));
        cd.add(new ColumnData("listPrice").setType("float"));
        cd.add(new ColumnData("quantity").setType("int"));
        cd.add(new ColumnData("orderedQuantity").setType("int"));
        cd.add(new ColumnData("blank1"));
        cd.add(new ColumnData("cost").setType("float"));
        cd.add(new ColumnData("blank2").setType("float"));
        cd.add(new ColumnData("sellPrice").setType("float"));
        cd.add(new ColumnData("blank3").setType("float"));
        cd.add(new ColumnData("bin"));
        cd.add(new ColumnData("blank4"));
        cd.add(new ColumnData("coverType"));
        cd.add(new ColumnData("blank5"));
        cd.add(new ColumnData("skidPieceCount").setType("int"));
        cd.add(new ColumnData("blank6"));
        cd.add(new ColumnData("skidPiecePrice").setType("float"));
        cd.add(new ColumnData("blank7").setType("float"));
        cd.add(new ColumnData("skidPieceCost").setType("float"));
        cd.add(new ColumnData("blank8").setType("float"));
        cd.add(new ColumnData("lbs").setType("float"));
        cd.add(new ColumnData("blank9").setType("float"));
        cd.add(new ColumnData("lbsPrice").setType("float"));
        cd.add(new ColumnData("blank10").setType("float"));
        cd.add(new ColumnData("lbsCost").setType("float"));
        cd.add(new ColumnData("blank11").setType("float"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50));
        cm.add(new ColumnModel("isbn", "ISBN", 80));
        cm.add(new ColumnModel("isbn13", "ISBN13", 100));
        cm.add(new ColumnModel("cond", "Condition", 50));
        cm.add(new ColumnModel("title", "Title", 200));
        cm.add(new ColumnModel("listPrice", "List Price", 90).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("quantity", "Received Qty", 100));
        cm.add(new ColumnModel("orderedQuantity", "Ordered Qty", 100));
        cm.add(new ColumnModel("blank1", "Updated Ordered Qty", 100));
        cm.add(new ColumnModel("cost", "Cost", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("blank2", "Updated Cost", 100));
        cm.add(new ColumnModel("sellPrice", "Sell Price", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("blank3", "Updated Sell Price", 100));
        cm.add(new ColumnModel("bin", "Bin", 100));
        cm.add(new ColumnModel("blank4", "Updated Bin", 100));
        cm.add(new ColumnModel("coverType", "Cover", 100));
        cm.add(new ColumnModel("blank5", "Updated Cover", 100));
        cm.add(new ColumnModel("skidPieceCount", "Skid Piece Count", 100));
        cm.add(new ColumnModel("blank6", "Updated Skid Piece Count", 100));
        cm.add(new ColumnModel("skidPiecePrice", "Skid Piece Price", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("blank7", "Updated Skid Piece Price", 100));
        cm.add(new ColumnModel("skidPieceCost", "Skid Piece Cost", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("blank8", "Updated Skid Piece Cost", 100));
        cm.add(new ColumnModel("lbs", "Skid Lbs", 100));
        cm.add(new ColumnModel("blank9", "Updated Skid Lbs", 100));
        cm.add(new ColumnModel("lbsPrice", "Skid Lbs Price", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("blank10", "Updated Skid Lbs Price", 100));
        cm.add(new ColumnModel("lbsCost", "Skid Lbs Cost", 100).setRenderer("moneyRenderer"));
        cm.add(new ColumnModel("blank11", "Updated Skid Lbs Cost", 100));
        
        listTable.setColumnModels(cm);
        
        listTable.setDefaultSortCol("id");
        listTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName(filename);
        setExcelExportSheetName("Cost Update");
        
    }
    
    
    public class CostExportExtraDataWriter implements ExcelExtraDataWriter {
        
        private Received received;
        Workbook workbook;
        
        public CostExportExtraDataWriter(Received received){
            this.received = received;
        }
        @Override
//        public int writeExtraPreData(int row, WritableSheet sheet) {
//            try {
//                WritableFont boldFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
//                boldFont.setUnderlineStyle(UnderlineStyle.SINGLE);
//                WritableCellFormat bold = new WritableCellFormat(boldFont);
//                WritableCellFormat integerCellFormat = new WritableCellFormat(NumberFormats.INTEGER);
//                WritableCellFormat floatCellFormat = new WritableCellFormat(NumberFormats.FLOAT);
//                DateFormat customDateFormat = new DateFormat ("MM/dd/yyyy");
//                WritableCellFormat dateCellFormat = new WritableCellFormat (customDateFormat);
//                
//                sheet.addCell(new Label( 0, row, "Receiving ID:", bold ));
//                sheet.addCell(new jxl.write.Number( 1, row, received.getId(), integerCellFormat));
//                row++;
//                sheet.addCell(new Label( 0, row, "PO:", bold ));
//                sheet.addCell(new Label( 1, row, received.getPoNumber()));
//                row++;
//                sheet.addCell(new Label( 0, row, "Vendor:", bold ));
//                sheet.addCell(new Label( 1, row, received.getVendorCode()));
//                row++;
//                sheet.addCell(new Label( 0, row, "Entered:", bold ));
//                sheet.addCell(new DateTime( 1, row, received.getCreateTime(), dateCellFormat));
//                row++;
//                sheet.addCell(new Label( 0, row, "PO Date:", bold ));
//                sheet.addCell(new DateTime( 1, row, received.getPoDate(), dateCellFormat));
//                row++;
//                sheet.addCell(new Label( 0, row, "Total Qty:", bold ));
//                if (received.getTotalQuantity() != null)
//                    sheet.addCell(new jxl.write.Number( 1, row, received.getTotalQuantity(), integerCellFormat));
//                else 
//                    sheet.addCell(new jxl.write.Number( 1, row, received.getTotalQuantity(), integerCellFormat));
//                row++;
//                sheet.addCell(new Label( 0, row, "Total Ordered Qty:", bold ));
//                if (received.getTotalOrderedQuantity() != null)
//                    sheet.addCell(new jxl.write.Number( 1, row, received.getTotalOrderedQuantity(), integerCellFormat));
//                else 
//                    sheet.addCell(new jxl.write.Number( 1, row, 0, integerCellFormat));
//                row++;
//                sheet.addCell(new Label( 0, row, "Total Cost:", bold ));
//                if (received.getTotalCost() != null)
//                    sheet.addCell(new jxl.write.Number( 1, row, received.getTotalCost().doubleValue(), floatCellFormat));
//                else
//                    sheet.addCell(new jxl.write.Number( 1, row, 0, floatCellFormat));
//                row++;
//                sheet.addCell(new Label( 0, row, "Total Sell Price:", bold ));
//                if (received.getTotalSellPrice() != null)
//                    sheet.addCell(new jxl.write.Number( 1, row, received.getTotalSellPrice().doubleValue(), floatCellFormat));
//                else 
//                    sheet.addCell(new jxl.write.Number( 1, row, 0, floatCellFormat));
//                row+=2;
//                return row;
//            } catch (Exception e){
//                logger.error("Could not write extra data", e);
//            }
//            return 0;
//        }
        
        public int writeExtraPreData(int row, Sheet sheet) {
            try {
                workbook = sheet.getWorkbook();
                Font boldFont = workbook.createFont();
                boldFont.setBold(true);
                boldFont.setUnderline(Font.U_SINGLE);
                CellStyle bold = workbook.createCellStyle();
                bold.setFont(boldFont);
                
                CellStyle integerCellFormat = workbook.createCellStyle();
                integerCellFormat.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0"));
                
                CellStyle floatCellFormat = workbook.createCellStyle();
                floatCellFormat.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0.00"));
                
                SimpleDateFormat dateParseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                CellStyle dateCellFormat = workbook.createCellStyle();
                DataFormat dateFormat = workbook.createDataFormat();
                dateCellFormat.setDataFormat(dateFormat.getFormat("mm/dd/yyyy"));
                
                Row r = sheet.createRow(row);
                Cell cell = r.createCell(0);
                cell.setCellValue("Receiving ID:");
                cell.setCellStyle(bold);
                
                cell = r.createCell(1);
                cell.setCellValue(received.getId());
                cell.setCellStyle(integerCellFormat);
                
                row++;
                r = sheet.createRow(row);
                
                cell = r.createCell(0);
                cell.setCellValue("PO:");
                cell.setCellStyle(bold);
                
                cell = r.createCell(1);
                cell.setCellValue(received.getPoNumber());
                
                row++;
                r = sheet.createRow(row);
                
                cell = r.createCell(0);
                cell.setCellValue("Vendor:");
                cell.setCellStyle(bold);
                
                cell = r.createCell(1);
                cell.setCellValue(received.getVendorCode());
                
                row++;
                r = sheet.createRow(row);
                
                cell = r.createCell(0);
                cell.setCellValue("Entered:");
                cell.setCellStyle(bold);
                
                cell = r.createCell(1);
                cell.setCellValue(received.getCreateTime());
                cell.setCellStyle(dateCellFormat);
                
                row++;
                r = sheet.createRow(row);
                
                cell = r.createCell(0);
                cell.setCellValue("PO Date:");
                cell.setCellStyle(bold);
                
                cell = r.createCell(1);
                cell.setCellValue(received.getPoDate());
                cell.setCellStyle(dateCellFormat);
                
                row++;
                r = sheet.createRow(row);
                
                cell = r.createCell(0);
                cell.setCellValue("Total Qty:");
                cell.setCellStyle(bold);
                
                if (received.getTotalQuantity() != null){
                    cell = r.createCell(1);
                    cell.setCellValue(received.getTotalQuantity());
                    cell.setCellStyle(integerCellFormat);
                }
                else {
                    cell = r.createCell(1);
                    cell.setCellValue(0);
//                    cell.setCellValue(received.getTotalQuantity());
                    cell.setCellStyle(integerCellFormat);
                }
                
                row++;
                r = sheet.createRow(row);
                
                cell = r.createCell(0);
                cell.setCellValue("Total Ordered Qty:");
                cell.setCellStyle(bold);
                
                if (received.getTotalOrderedQuantity() != null){
                    cell = r.createCell(1);
                    cell.setCellValue(received.getTotalOrderedQuantity());
                    cell.setCellStyle(integerCellFormat);
                }
                else {
                    cell = r.createCell(1);
                    cell.setCellValue(0);
                    cell.setCellStyle(integerCellFormat);
                }
                
                row++;
                r = sheet.createRow(row);
                
                cell = r.createCell(0);
                cell.setCellValue("Total Cost:");
                cell.setCellStyle(bold);
                
                if (received.getTotalCost() != null){
                    cell = r.createCell(1);
                    cell.setCellValue(received.getTotalCost().doubleValue());
                    cell.setCellStyle(floatCellFormat);
                }
                else{
                    cell = r.createCell(1);
                    cell.setCellValue(0);
                    cell.setCellStyle(floatCellFormat);
                }
                
                row++;
                r = sheet.createRow(row);
                
                cell = r.createCell(0);
                cell.setCellValue("Total Sell Price:");
                cell.setCellStyle(bold);
                
                if (received.getTotalSellPrice() != null){
                    cell = r.createCell(0);
                    cell.setCellValue(received.getTotalSellPrice().doubleValue());
                    cell.setCellStyle(floatCellFormat);
                }
                else {
                    cell = r.createCell(1);
                    cell.setCellValue(0);
                    cell.setCellStyle(floatCellFormat);
                }
                
                row+=2;
                return row;
            } catch (Exception e){
                logger.error("Could not write extra data", e);
            }
            return 0;
        }

        @Override
        public void writeExtraPostData(int row, Sheet sheet) {
        }
        
    }
    
    @ActionRole({"BcRecAdmin"})
    public String importCostPage(){
        return "importcost";
    }
    
    @ActionRole({"BcRecAdmin"})
    public String importCost(){
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
        
        ReceivingSessionLocal rSession = getReceivingSession();
        InventoryItemSessionLocal iSession = getInventoryItemSession();
        Received received = rSession.findById(id);
        if (received == null){
            setSuccess(false);
            setMessage("Could not find the receiving to add the items to, refresh the page.");
            return "status";
        }
        
        try {
            XSSFWorkbook workbook = null;
            FileInputStream fis = new FileInputStream(upload);
            try {
                workbook = new XSSFWorkbook(fis);
                evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            } catch(Exception e) {
                logger.error("Unsupported file type.", e);
                setSuccess(false);
                setMessage("Unsupported file type.  Please ensure you are uploading an Excel file.");
                return "status";
            }
            
            XSSFSheet s = workbook.getSheetAt(0);
            
            int numRows = s.getPhysicalNumberOfRows() + 1;
            logger.info("Total Rows : " + numRows);
            if(numRows <= 1) {
                setSuccess(false);
                setMessage("The uploaded file contained no items to update cost.");
                return "status";
            }
            
            /*
             * Expect row 0 column 0 = Receiving ID:
             *        row 10 column 0 = ID
             *        data is at row 11
             *        0    id 
             *        4    title
             *        8    orderedQuantity
             *        10   cost
             *        12   sellPrice
             *        14   bin
             *        16   coverType
             *        18   skidPieceCount
             *        20   skidPiecePrice
             *        22   skidPieceCost
             *        24   lbs
             *        26   lbsPrice
             *        28   lbsCost
             */
            try {
                Row r = s.getRow(0);
                Row r1 = s.getRow(10);

                if (!getCellValue(r.getCell(0)).startsWith("Receiving ID") ||
                    !getCellValue(r1.getCell(0)).startsWith("ID"))
                {
                    setSuccess(false);
                    setMessage("This file does not seem to be the correct format for a cost import.  Expecting Receiving ID in row 1 and ID in row 11.");
                    return "status";                                
                }
            } catch (ArrayIndexOutOfBoundsException aie) {
                setSuccess(false);
                setMessage("This file does not seem to be the correct format for a cost import.  Expecting Receiving ID in row 1 and ID in row 11.");
                return "status";                                
            }
            List<ReceivedItem> items = new ArrayList<ReceivedItem>();
            for(int row = 11; row < numRows; row++) {
                Row r = s.getRow(row);
                if (r == null)
                    continue;
                // Check for hidden row (and skip it)
                if(r.getZeroHeight()) {
                    logger.debug("Skipping hidden row: " + row);
                    continue;
                }
                
                Long rid = Long.parseLong(getCellValue(r.getCell(0)));
                
                ReceivedItem item = rSession.findItemById(rid, "inventoryItem");
                if (item == null){
                    logger.error("ReceivedItem was not found for ID: "+rid);
                    continue;
                }
                String updatestr = "Updating ISBN : " + item.getIsbn();
                item.getUpdated().clear();
                item.setPreQuantity(item.getQuantity());
                
                boolean updated = false;
                String title = getStringFromData(s, row, 4);
                if (title != null){
                    item.setTitle(title);
                    InventoryItem ii = item.getInventoryItem();
                    ii.setTitle(title);
                    iSession.update(ii);
                    updatestr += " title : " + title;
                    updated = true;
                }
                Integer oq = getIntegerFromData(s, row, 8);
                if (oq != null) {
                    item.setOrderedQuantity(oq);
                    updatestr += " ordered qty : " + oq;
                    updated = true;
                }
                Float c = getFloatFromData(s, row, 10);
                if (c != null) {
                    updatestr += " cost : " + c;
                    item.setCost(c);
                    updated = true;
                }
                Float sp = getFloatFromData(s,  row, 12);
                if (sp != null) {
                    updatestr += " sell price : " + sp;
                    item.setSellPrice(sp);
                    updated = true;
                }
                String b = getStringFromData(s,  row, 14);
                if (b != null) {
                    item.setBin(b);
                    updatestr += " bin : " + b;
                    updated = true;
                }
                String ct = getStringFromData(s,  row, 16);
                if (ct != null) {
                    item.setCoverType(ct);
                    updatestr += " cover type : " + ct;
                    updated = true;
                }
                Integer spc = getIntegerFromData(s,  row, 18);
                if (spc != null) {
                    item.setSkidPieceCount(spc);
                    updatestr += " skid piece cnt : " + spc;
                    updated = true;
                }
                Float spp = getFloatFromData(s,  row, 20);
                if (spp != null) {
                    item.setSkidPiecePrice(spp);
                    updatestr += " skid piece price : " + spp;
                    updated = true;
                }
                Float spcc = getFloatFromData(s,  row, 22);
                if (spcc != null) {
                    item.setSkidPieceCost(spcc);
                    updatestr += " skid piece cost : " + spcc;
                    updated = true;
                }
                Float l = getFloatFromData(s,  row, 24);
                if (l != null) {
                    item.setLbs(l);
                    updatestr += " lbs : " + l;
                    updated = true;
                }
                Float lp = getFloatFromData(s,  row, 26);
                if (lp != null) {
                    item.setLbsPrice(lp);
                    updatestr += " lbs price : " + lp;
                    updated = true;
                }
                Float lc = getFloatFromData(s,  row, 28);
                if (lc != null) {
                    item.setLbsCost(lc);
                    updatestr += " lbs cost : " + lc;
                    updated = true;
                }
                
                if (updated){
                    rSession.update(item);
                    items.add(item);
                    logger.info(updatestr);
                } else{
                    logger.info("not " + updatestr);
                }
            }
            logger.info("Updating");
            if (!received.getHolding()){
                LifoSessionLocal lifoSession = getLifoSession();
                lifoSession.updateReceivedItems(items);
            }
            logger.info("Recalculating");
            rSession.recalculateReceived(received.getId());
            logger.info("done");

        } catch (Exception e){
            logger.error("Could not upload receiving cost updates", e);
            setSuccess(false);
            setMessage("Could not upload the receiving cost updates, there was a system error.");
            return "status";
        }
        setSuccess(true);
        return "status";        
    }

//    private String getStringFromData(Sheet s, int row, int col){
//        String ups = s.getCell(col, row).getContents();
//        if (ups == null || ups.length() == 0) return null;
//        return ups;
//    }
//    private Integer getIntegerFromData(Sheet s, int row, int col){
//        String ups = s.getCell(col, row).getContents();
//        if (ups == null || ups.length() == 0) return null;
//        try {
//            return new Integer(ups);
//        } catch (Exception ex) {
//        }
//        return null;
//    }
//    private Float getFloatFromData(Sheet s, int row, int col){
//        String ups = s.getCell(col, row).getContents();
//        if (ups == null || ups.length() == 0) return null;
//        try {
//            return new Float(ups);
//        } catch (Exception ex) {
//        }
//        return null;
//    }
//    
    private String getStringFromData(XSSFSheet s, int row, int col){
        Row r = s.getRow(row);
        String ups = getCellValue(r.getCell(col));
        if (ups == null || ups.length() == 0) return null;
        return ups;
    }
    private Integer getIntegerFromData(XSSFSheet s, int row, int col){
        Row r = s.getRow(row);
        String ups = getCellValue(r.getCell(col));
        if (ups == null || ups.length() == 0) return null;
        try {
            return new Integer(ups);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }
    private Float getFloatFromData(XSSFSheet s, int row, int col){
        Row r = s.getRow(row);
        String ups = getCellValue(r.getCell(col));
        if (ups == null || ups.length() == 0) return null;
        try {
            return new Float(ups);
        } catch (Exception ex) {
        }
        return null;
    }
    
    
    public Table getListTable() {
        return listTable;
    }

    public void setListTable(Table listTable) {
        this.listTable = listTable;
    }

    public Received getReceiving() {
        return receiving;
    }

    public void setReceiving(Received receiving) {
        this.receiving = receiving;
    }

    public List<Vendor> getVendors() {
        return vendors;
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }


    public Date getCurrentDate(){
        return Calendar.getInstance().getTime();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Boolean getExportCost() {
        return exportCost;
    }

    public void setExportCost(Boolean exportCost) {
        this.exportCost = exportCost;
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
    
    private String getCellValue(Cell cell){
        if (cell!=null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && HSSFDateUtil.isCellDateFormatted(cell))
            {
                return sdf.format(cell.getDateCellValue());
            }
            
            switch (evaluator.evaluate(cell).getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                case Cell.CELL_TYPE_NUMERIC:
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                case Cell.CELL_TYPE_STRING:
                    return cell.getStringCellValue();
                case Cell.CELL_TYPE_BLANK:
                    return "";
                case Cell.CELL_TYPE_ERROR:
                    return "";

                // CELL_TYPE_FORMULA will never occur
                case Cell.CELL_TYPE_FORMULA: 
                    return "";
            }
        }
        return "";
    }
}
