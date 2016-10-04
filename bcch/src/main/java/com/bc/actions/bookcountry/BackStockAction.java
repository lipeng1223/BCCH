package com.bc.actions.bookcountry;

import java.io.File;
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

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.actions.BaseAction;
import com.bc.amazon.AmazonData;
import com.bc.amazon.AmazonItemLookupSoap;
import com.bc.dao.DaoResults;
import com.bc.ejb.BackStockSessionLocal;
import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.orm.BackStockItem;
import com.bc.orm.BackStockLocation;
import com.bc.orm.InventoryItem;
import com.bc.struts.QueryInput;
import com.bc.struts.QueryResults;
import com.bc.table.*;
import com.bc.util.ActionRole;
import com.bc.util.IsbnUtil;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.hibernate.criterion.Restrictions;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="upload", location="/WEB-INF/jsp/bookcountry/backstock/upload.jsp"),
    @Result(name="crud", location="/WEB-INF/jsp/bookcountry/backstock/crud.jsp"),
    @Result(name="location", location="/WEB-INF/jsp/bookcountry/backstock/location.jsp"),
    @Result(name="locations", location="/WEB-INF/jsp/bookcountry/backstock/locations.jsp"),
    @Result(name="detail", location="/WEB-INF/jsp/bookcountry/backstock/detail.jsp"),
    @Result(name="list", location="/WEB-INF/jsp/bookcountry/backstock/list.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="titleinfo", location="/WEB-INF/jsp/bookcountry/backstock/titleinfo.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class BackStockAction extends BaseAction {
    
    private final Logger logger = Logger.getLogger(BackStockAction.class);
    
    private Table listTable;
    
    private BackStockItem backStockItem;
    private BackStockLocation backStockLocation;
    private Long backStockId;
    private String selectionIds;
    private String isbn;
    private String isbn13;
    private Boolean exportWithLocationsToExcel = false;
    private List<BackStockLocation> backStockLocations;
    private String quantities;
    
    private File upload;//The actual file
    private String uploadContentType; //The content type of the file
    private String uploadFileName; //The uploaded file name    
    
    private Boolean hasImage;
    
    private static final int LOCATION = 0;
    private static final int ROW = 1;
    private static final int QUANTITY = 2;
    private static final int TUB = 3;
    private static final int ISBN10 = 4;
    private static final int ISBN13 = 5;
    private static final int TITLE = 6;
    
    
    @ActionRole({"BcBackStockAdmin"})
    public String lookupTitle(){
        try {
            if (isbn != null && IsbnUtil.isValid(isbn)){
                isbn13 = IsbnUtil.getIsbn13(isbn);
                BackStockSessionLocal bsSession = getBackStockSession();
                List<BackStockItem> items = bsSession.findByIsbn(IsbnUtil.getIsbn10(isbn));
                if (items != null){
                    for (BackStockItem bsi : items){
                        backStockItem = bsi;
                    }
                }
                if (backStockItem == null){
                    HashMap<String, String> titles = AmazonItemLookupSoap.getInstance().lookupTitles(new String[]{isbn});
                    if (titles != null && titles.size() > 0){
                        setMessage(titles.values().iterator().next());
                    }
                }
            }
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not lookup title information", e);
            setSuccess(false);
        }
        return "titleinfo";
    }
    
    @ActionRole({"BcBackStockAdmin"})
    public String create(){
        return "crud";
    }
    
    @ActionRole({"BcBackStockAdmin"})
    public String locations(){
        backStockLocations = new ArrayList<BackStockLocation>();
        BackStockSessionLocal bsSession = getBackStockSession();
        if (id != null){
            backStockItem = bsSession.findById(id, "backStockLocations");
            backStockLocations.addAll(backStockItem.getBackStockLocations());
        } else if (isbn != null){
            if (IsbnUtil.isValid(isbn)){
                List<BackStockItem> items = bsSession.findByIsbn(IsbnUtil.getIsbn10(isbn));
                if (items != null){
                    for (BackStockItem bsi : items){
                        backStockLocations.addAll(bsi.getBackStockLocations());
                    }
                }
            }
        }
        return "locations";
    }

   @ActionRole({"BcBackStockAdmin"})
    public String crudSubmit(){
        try {
            BackStockSessionLocal bsSession = getBackStockSession();
            if (backStockItem.getId() != null && backStockItem.getId() > 0){
                // update
                BackStockItem dbBackStockItem = bsSession.findById(backStockItem.getId());
                if (dbBackStockItem != null){
                    dbBackStockItem.setIsbn(IsbnUtil.getIsbn10(backStockItem.getIsbn()));
                    dbBackStockItem.setIsbn13(IsbnUtil.getIsbn13(backStockItem.getIsbn()));
                    dbBackStockItem.setTitle(backStockItem.getTitle());
                    dbBackStockItem.setCommitted(backStockItem.getCommitted());
                    dbBackStockItem.setAvailable(backStockItem.getAvailable());
                    dbBackStockItem.setComment(backStockItem.getComment());
                    bsSession.update(dbBackStockItem);
                    
                    // create the backstocklocation if it exists
                    if (backStockLocation != null && backStockLocation.getLocation() != null && backStockLocation.getLocation().length() > 0 && backStockLocation.getQuantity() > 0) {
                        backStockLocation.setBackStockItem(dbBackStockItem);
                        bsSession.create(backStockLocation);
                        bsSession.updateCounts(dbBackStockItem.getId());
                    }
                }
            } else {
                // create
                String bsisbn = backStockItem.getIsbn();
                if (IsbnUtil.isValid(isbn)){
                    backStockItem.setIsbn(IsbnUtil.getIsbn10(bsisbn));
                    backStockItem.setIsbn13(IsbnUtil.getIsbn13(bsisbn));
                }
                bsSession.create(backStockItem);
                
                // create the backstocklocation if it exists
                if (backStockLocation != null && backStockLocation.getLocation() != null && backStockLocation.getLocation().length() > 0 && backStockLocation.getQuantity() > 0) {
                    backStockLocation.setBackStockItem(backStockItem);
                    bsSession.create(backStockLocation);
                    bsSession.updateCounts(backStockItem.getId());
                }
            }
            
            // go through quantities and update any locations quantities
            if (quantities != null){
                StringTokenizer st = new StringTokenizer(quantities, ",");
                while (st.hasMoreTokens()){
                    String idAndQuantity = st.nextToken();
                    StringTokenizer stid = new StringTokenizer(idAndQuantity, ":");
                    try {
                        Long lid = new Long(stid.nextToken());
                        Integer quantity = new Integer(stid.nextToken());
                        BackStockLocation bsl = bsSession.findBackStockLocationById(lid);
                        if (bsl != null){
                            bsl.setQuantity(quantity);
                            bsSession.update(bsl);
                        }
                    } catch (Exception e){
                        logger.error("Could not set location quantity for id and quantity: "+idAndQuantity);
                    }
                }
                bsSession.updateCounts(backStockItem.getId());
            }
            
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not create back stock item", e);
            setSuccess(false);
            setMessage("Could not create or update the Back Stock Item, there was a system error");
        }
        return "status";
    }

    @ActionRole({"BcBackStockAdmin"})
    public String createLocation(){
        return "location";
    }
    
    @ActionRole({"BcBackStockAdmin"})
    public String createLocationSubmit(){
        try {
            BackStockSessionLocal bsSession = getBackStockSession();
            backStockItem = bsSession.findById(backStockId);
            backStockLocation.setBackStockItem(backStockItem);
            bsSession.create(backStockLocation);
            bsSession.updateCounts(backStockId);
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not update back stock location", e);
            setSuccess(false);
            setMessage("Could not update the Back Stock Location, there was a system error");
        }
        return "status";
    }
   
    @ActionRole({"BcBackStockAdmin"})
    public String edit(){
        BackStockSessionLocal bsSession = getBackStockSession();
        backStockItem = bsSession.findById(id);
        return "crud";
    }

    @ActionRole({"BcBackStockAdmin"})
    public String editLocationSubmit(){
        try {
            BackStockSessionLocal bsSession = getBackStockSession();
            BackStockLocation dbBackStockLocation = bsSession.findBackStockLocationById(backStockLocation.getId(), "backStockItem");
            dbBackStockLocation.setLocation(backStockLocation.getLocation());
            dbBackStockLocation.setQuantity(backStockLocation.getQuantity());
            dbBackStockLocation.setRow(backStockLocation.getRow());
            dbBackStockLocation.setTub(backStockLocation.getTub());
            bsSession.update(dbBackStockLocation);
            bsSession.updateCounts(dbBackStockLocation.getBackStockItem().getId());
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not update back stock location", e);
            setSuccess(false);
            setMessage("Could not update the Back Stock Location, there was a system error");
        }
        return "status";
    }
    
    @ActionRole({"BcBackStockAdmin"})
    public String editLocation(){
        BackStockSessionLocal bsSession = getBackStockSession();
        backStockLocation = bsSession.findBackStockLocationById(id);
        return "location";
    }
    
    @ActionRole({"BcBackStockAdmin"})
    public String delete(){
        try {
            BackStockSessionLocal bsSession = getBackStockSession();
            bsSession.delete(id);
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not delete Back Stock", e);
            setSuccess(false);
            setMessage("Could not delete Back Stock");
        }
        return "status";
    }

    @ActionRole({"BcBackStockAdmin"})
    public String deleteLocation(){
        try {
            BackStockSessionLocal bsSession = getBackStockSession();
            backStockLocation = bsSession.findBackStockLocationById(id, "backStockItem");
            bsSession.deleteBackStockLocation(id);
            bsSession.updateCounts(backStockLocation.getBackStockItem().getId());
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not delete Back Stock Location", e);
            setSuccess(false);
            setMessage("Could not delete Back Stock Location");
        }
        return "status";
    }

    @ActionRole({"BcBackStockAdmin", "BcBackStockViewer"})
    public String detail(){
        BackStockSessionLocal bsSession = getBackStockSession();
        backStockItem = bsSession.findById(id, "backStockLocations");
        List<InventoryItem> items = this.getInventoryItemSession().findByIsbn(backStockItem.getIsbn());
        isbn = backStockItem.getIsbn();
        this.hasImage = false;
        if (backStockItem.getSmallImage() != null && backStockItem.getSmallImage().startsWith("http"))
        {
            this.hasImage = true;
            logger.info("image from backstock");
        } else {
            for (InventoryItem i : items){
                if (i.getSmallImage() == null)
                    continue;
                if (i.getSmallImage().startsWith("http")){
                    backStockItem.setSmallImage(i.getSmallImage());
                    backStockItem.setMediumImage(i.getMediumImage());
                    bsSession.update(backStockItem);
                    this.hasImage = true;
                    logger.info("image from inventory");
                    break;
                }
            }
            if (!this.hasImage){
                AmazonData amazonData = AmazonItemLookupSoap.getInstance().lookupAmazonData(isbn);
                amazonData.setCategories(AmazonItemLookupSoap.getInstance().lookupCategories(isbn));
                if (amazonData.getSalesRank() != null){
                    backStockItem.setSmallImage(amazonData.getSmallImageUrl());
                    backStockItem.setMediumImage(amazonData.getLargeImageUrl());
                    bsSession.update(backStockItem);
                    this.hasImage = true;
                    logger.info("image from amazon");
                }
            }
        }
        return "detail";
    }

    @ActionRole({"BcBackStockAdmin", "BcBackStockViewer"})
    public String list(){
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcBackStockAdmin", "BcBackStockViewer"})
    public String listData(){
        if (exportWithLocationsToExcel)
            setupExportListTable(); 
        else
            setupListTable(); 
        try {
            BackStockSessionLocal bsSession = getBackStockSession();
            if (exportWithLocationsToExcel){
                queryInput.setStart(0);
                queryInput.setLimit(null);
                HashMap<String, String> aliases = new HashMap<String, String>();
                DaoResults dr = bsSession.findAll(queryInput);
                List<Long> oids = new ArrayList<Long>();
                for (BackStockItem bsi : (List<BackStockItem>)dr.getData()){
                    oids.add(bsi.getId());
                }
                logger.info("oids size: "+oids.size());
                List<HashMap<String, Object>> fparams = queryInput.getFilterParams();
                aliases.put("backStockItem", "backStockItem");
                queryInput = new QueryInput();
                queryInput.addAndCriterion(Restrictions.in("backStockItem.id", oids));
                queryInput.setSortCol("backStockItem.title");
                queryInput.setSortDir(QueryInput.SORT_ASC);
                queryResults = new QueryResults(bsSession.findAllBackStockLocations(queryInput, aliases, "backStockItem"));
                queryResults.setTableConfig(listTable, fparams);
            } else {
                queryResults = new QueryResults(bsSession.findAll(queryInput));
                queryResults.setTableConfig(listTable, queryInput.getFilterParams());
            }
        } catch (Exception e){
            logger.error("Could not list data for back stock", e);
        }
        return "queryresults";
    }

    private void setupListTable(){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("isbn13"));
        cd.add(new ColumnData("totalQuantity").setType("int"));
        cd.add(new ColumnData("totalLocations").setType("int"));
        cd.add(new ColumnData("title"));
        cd.add(new ColumnData("onhand").setType("int"));
        cd.add(new ColumnData("committed").setType("int"));
        cd.add(new ColumnData("available").setType("int"));
        cd.add(new ColumnData("comment"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50));
        cm.add(new ColumnModel("isbn", "ISBN", 100));
        cm.add(new ColumnModel("isbn13", "ISBN13", 100));
        cm.add(new ColumnModel("totalQuantity", "Total Quantity", 100));
        cm.add(new ColumnModel("totalLocations", "Total Locations", 100));
        cm.add(new ColumnModel("title", "Title", 700));
        cm.add(new ColumnModel("onhand", "On Hand", 100));
        cm.add(new ColumnModel("committed", "Committed", 100));
        cm.add(new ColumnModel("available", "Available", 100));
        cm.add(new ColumnModel("comment", "Comment", 300));
        listTable.setColumnModels(cm);
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("long", "id"));
        filters.add(new Filter("integer", "totalQuantity"));
        filters.add(new Filter("integer", "totalLocations"));
        filters.add(new Filter("string", "isbn"));
        filters.add(new Filter("string", "isbn13"));
        filters.add(new Filter("string", "title"));
        listTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        if (getIsBcManifestAdmin()) {
            buttons.add(new ToolbarButton("Create", "createButtonClick", "create_icon", "Create A New Back Stock"));
            buttons.add(new ToolbarButton("Edit", "editButtonClick", "edit_icon", "Edit The Selected Back Stock").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Delete", "deleteButtonClick", "delete_icon", "Delete The Selected Back Stock").setSingleRowAction(true));
            buttons.add(new ToolbarButton("History", "historyButtonClick", "history_icon", "Audit History For The Selected Back Stock"));
            buttons.add(new ToolbarButton("ISBN History", "isbnHistoryButtonClick", "history_icon", "Audit History For Input ISBN"));
        }
        buttons.add(new ToolbarButton("Excel With Locations", "exportWithLocationsButtonClick", "excel_icon", "Export these Back Stock Items with Locations"));
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("title");
        listTable.setDefaultSortDir(Table.SORT_DIR_ASC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryBackStockExport");
        setExcelExportSheetName("Back Stock");
        
    }    
    
    private void setupExportListTable(){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("location"));
        cd.add(new ColumnData("row"));
        cd.add(new ColumnData("quantity"));
        cd.add(new ColumnData("tub"));
        cd.add(new ColumnData("backStockItem.isbn", "backStockItem_isbn"));
        cd.add(new ColumnData("backStockItem.isbn13", "backStockItem_isbn13"));
        cd.add(new ColumnData("backStockItem.totalQuantity", "backStockItem_totalQuantity").setType("int"));
        cd.add(new ColumnData("backStockItem.totalLocations", "backStockItem_totalLocations").setType("int"));
        cd.add(new ColumnData("backStockItem.title", "backStockItem_title"));
        cd.add(new ColumnData("backStockItem.onhand", "backStockItem_onhand").setType("int"));
        cd.add(new ColumnData("backStockItem.committed", "backStockItem_committed").setType("int"));
        cd.add(new ColumnData("backStockItem.available", "backStockItem_available").setType("int"));
        cd.add(new ColumnData("backStockItem.comment", "backStockItem_comment"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("location", "Location", 50));
        cm.add(new ColumnModel("row", "Row", 50));
        cm.add(new ColumnModel("quantity", "Quantity", 50));
        cm.add(new ColumnModel("tub", "Tub", 50));
        cm.add(new ColumnModel("backStockItem_isbn", "ISBN", 100));
        cm.add(new ColumnModel("backStockItem_isbn13", "ISBN13", 100));
        cm.add(new ColumnModel("backStockItem_totalQuantity", "Total Quantity", 100));
        cm.add(new ColumnModel("backStockItem_totalLocations", "Total Locations", 100));
        cm.add(new ColumnModel("backStockItem_title", "Title", 700));
        cm.add(new ColumnModel("backStockItem_onhand", "On Hand", 100));
        cm.add(new ColumnModel("backStockItem_committed", "Committed", 100));
        cm.add(new ColumnModel("backStockItem_available", "Available", 100));
        cm.add(new ColumnModel("backStockItem_comment", "Comment", 300));
        listTable.setColumnModels(cm);
        
        listTable.setDefaultSortCol("title");
        listTable.setDefaultSortDir(Table.SORT_DIR_ASC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryBackStockLocationsExport");
        setExcelExportSheetName("Back Stock Locations");
        
    }

    @ActionRole({"BcBackStockAdmin"})
    public String uploadPage(){
        return "upload";
    }
    
    @ActionRole({"BcBackStockAdmin"})
//    public String upload(){
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
//        BackStockSessionLocal bsSession = getBackStockSession();
//        
//        logger.info("Importing back stock items from: "+uploadFileName+" by user: "+getUserName());
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
//                setMessage("The uploaded file contained no items to upload.");
//                return "status";
//            }
//            
//            // Check for hidden columns
//            for(int i = LOCATION; i <= TITLE; i++) {
//                if(s.getCell(i, 0).isHidden()) {
//                    setSuccess(false);
//                    setMessage("There are hidden columns in the first 8 columns.  Not supported.");
//                    return "status";
//                }
//            }
//                
//            
//            // Process each row.  Skip header row
//            InventoryItemSessionLocal iiSession = getInventoryItemSession();
//            HashMap<String, BackStockItem> items = new HashMap<String, BackStockItem>();
//            logger.info("starting to import the back stock items, rows: "+numRows);
//            for(int row = 1; row < numRows; row++) {
//                
//                // Check for hidden row (and skip it)
//                if(s.getRowView(row).isHidden()) {
//                    logger.debug("Skipping hidden row: " + row);
//                    continue;
//                }
//                
//                BackStockItem item = new BackStockItem();
//                
//                String isbn10 = s.getCell(ISBN10, row).getContents();
//                String isbn13 = s.getCell(ISBN13, row).getContents();
//                String title = s.getCell(TITLE, row).getContents();
//                String location = s.getCell(LOCATION, row).getContents();
//                String tub = s.getCell(TUB, row).getContents();
//                String rowString = s.getCell(ROW, row).getContents();
//                String quantityString = s.getCell(QUANTITY, row).getContents();
//                if (quantityString == null || quantityString.length() == 0){
//                    quantityString = "0";
//                }
//                
//                while (isbn10.length() < 10) isbn10 = "0"+isbn10;
//                if (isbn13 == null || isbn13.length() == 0) isbn13 = IsbnUtil.getIsbn13(isbn10);
//                
//                item.setIsbn(isbn10);
//                item.setIsbn13(isbn13);
//                item.setTitle(title);
//                item.setBackStockLocations(new HashSet<BackStockLocation>());
//                item.setTotalQuantity(0);
//                
//                // put it into the map
//                if (items.containsKey(isbn10)){
//                    item = items.get(isbn10);
//                } else {
//                    items.put(isbn10, item);
//                }
//                
//                BackStockLocation bsl = new BackStockLocation();
//                bsl.setBackStockItem(item);
//                try {
//                    bsl.setQuantity(Integer.parseInt(quantityString));
//                } catch (Exception e){
//                    bsl.setQuantity(0);
//                }
//                bsl.setLocation(location);
//                bsl.setTub(tub);
//                bsl.setRow(rowString);
//                item.setTotalQuantity(item.getTotalQuantity()+bsl.getQuantity());
//                item.getBackStockLocations().add(bsl);
//                item.setTotalLocations(item.getBackStockLocations().size());
//            }
//            logger.info("finished the excel processing");
//
//            if (items.size() > 0){
//                int count = 0;
//                for (BackStockItem bsi : items.values()){
//                    count++;
//                    bsSession.create(bsi);
//                    if (count % 100 == 0){
//                        logger.info("finished with "+count+" items");
//                    }
//                }
//            }
//            logger.info("finished back stock import");
//            setSuccess(true);
//        } catch (Exception e){
//            logger.error("Could not upload back stock items", e);
//            setSuccess(false);
//            setMessage("Could not upload the back stock items, there was a system error.");
//        }
//        return "status";        
//    }
    public String upload(){
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
        
        BackStockSessionLocal bsSession = getBackStockSession();
        
        logger.info("Importing back stock items from: "+uploadFileName+" by user: "+getUserName());
        
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
                setMessage("The uploaded file contained no items to upload.");
                return "status";
            }
            
            // Check for hidden columns
            for(int i = LOCATION; i <= TITLE; i++) {
                if(s.isColumnHidden(i)) {
                    setSuccess(false);
                    setMessage("There are hidden columns in the first 8 columns.  Not supported.");
                    return "status";
                }
            }
                
            
            // Process each row.  Skip header row
            InventoryItemSessionLocal iiSession = getInventoryItemSession();
            HashMap<String, BackStockItem> items = new HashMap<String, BackStockItem>();
            logger.info("starting to import the back stock items, rows: "+numRows);
            for(int row = 1; row < numRows; row++) {
                
                Row r = s.getRow(row);
                if (r == null)
                    continue;
                // Check for hidden row (and skip it)
                if(r.getZeroHeight()) {
                    logger.debug("Skipping hidden row: " + row);
                    continue;
                }
                
                BackStockItem item = new BackStockItem();
                
                String isbn10 = getCellValue(r.getCell(ISBN10));
                String isbn13 = getCellValue(r.getCell(ISBN13));
                String title = getCellValue(r.getCell(TITLE));
                String location = getCellValue(r.getCell(LOCATION));
                String tub = getCellValue(r.getCell(TUB));
                String rowString = getCellValue(r.getCell(ROW));
                String quantityString = getCellValue(r.getCell(QUANTITY));
                if (quantityString == null || quantityString.length() == 0){
                    quantityString = "0";
                }
                
                while (isbn10.length() < 10) isbn10 = "0"+isbn10;
                if (isbn13 == null || isbn13.length() == 0) isbn13 = IsbnUtil.getIsbn13(isbn10);
                
                item.setIsbn(isbn10);
                item.setIsbn13(isbn13);
                item.setTitle(title);
                item.setBackStockLocations(new HashSet<BackStockLocation>());
                item.setTotalQuantity(0);
                
                // put it into the map
                if (items.containsKey(isbn10)){
                    item = items.get(isbn10);
                } else {
                    items.put(isbn10, item);
                }
                
                BackStockLocation bsl = new BackStockLocation();
                bsl.setBackStockItem(item);
                try {
                    bsl.setQuantity(Integer.parseInt(quantityString));
                } catch (Exception e){
                    bsl.setQuantity(0);
                }
                bsl.setLocation(location);
                bsl.setTub(tub);
                bsl.setRow(rowString);
                item.setTotalQuantity(item.getTotalQuantity()+bsl.getQuantity());
                item.getBackStockLocations().add(bsl);
                item.setTotalLocations(item.getBackStockLocations().size());
            }
            logger.info("finished the excel processing");

            if (items.size() > 0){
                int count = 0;
                for (BackStockItem bsi : items.values()){
                    count++;
                    bsSession.create(bsi);
                    if (count % 100 == 0){
                        logger.info("finished with "+count+" items");
                    }
                }
            }
            logger.info("finished back stock import");
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not upload back stock items", e);
            setSuccess(false);
            setMessage("Could not upload the back stock items, there was a system error.");
        }
        return "status";        
    }

    public BackStockItem getBackStockItem() {
        return backStockItem;
    }

    public void setBackStockItem(BackStockItem backStockItem) {
        this.backStockItem = backStockItem;
    }

    public BackStockLocation getBackStockLocation() {
        return backStockLocation;
    }

    public void setBackStockLocation(BackStockLocation backStockLocation) {
        this.backStockLocation = backStockLocation;
    }

    public Table getListTable() {
        return listTable;
    }

    public void setListTable(Table listTable) {
        this.listTable = listTable;
    }

    public String getSelectionIds() {
        return selectionIds;
    }

    public void setSelectionIds(String selectionIds) {
        this.selectionIds = selectionIds;
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

    public Long getBackStockId() {
        return backStockId;
    }

    public void setBackStockId(Long backStockId) {
        this.backStockId = backStockId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Boolean getExportWithLocationsToExcel() {
        return exportWithLocationsToExcel;
    }

    public void setExportWithLocationsToExcel(Boolean exportWithLocationsToExcel) {
        this.exportWithLocationsToExcel = exportWithLocationsToExcel;
    }

    public List<BackStockLocation> getBackStockLocations() {
        return backStockLocations;
    }

    public void setBackStockLocations(List<BackStockLocation> backStockLocations) {
        this.backStockLocations = backStockLocations;
    }

    public String getQuantities() {
        return quantities;
    }

    public void setQuantities(String quantities) {
        this.quantities = quantities;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }
    
    public Boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(Boolean hasImage) {
        this.hasImage = hasImage;
    }
    
    private String getCellValue(Cell cell){
        if (cell!=null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && HSSFDateUtil.isCellDateFormatted(cell))
            {
                return sdf.format(cell.getDateCellValue());
            }
            
            switch (cell.getCellType()) {
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
