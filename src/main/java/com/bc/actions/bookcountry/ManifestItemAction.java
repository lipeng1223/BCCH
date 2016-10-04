package com.bc.actions.bookcountry;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import org.hibernate.criterion.Restrictions;

import com.bc.actions.BaseAction;
import com.bc.amazon.AmazonData;
import com.bc.amazon.AmazonItemLookupSoap;
import com.bc.dao.DaoResults;
import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.ejb.ManifestSessionLocal;
import com.bc.orm.InventoryItem;
import com.bc.orm.Manifest;
import com.bc.orm.ManifestItem;
import com.bc.struts.QueryInput;
import com.bc.struts.QueryResults;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.table.Filter;
import com.bc.table.Table;
import com.bc.table.Toolbar;
import com.bc.table.ToolbarButton;
import com.bc.util.ActionRole;
import com.bc.util.IsbnUtil;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="crud", location="/WEB-INF/jsp/bookcountry/manifest/item/crud.jsp"),
    @Result(name="viewmanifest", location="/WEB-INF/jsp/bookcountry/manifest/view.jsp"),
    @Result(name="detail", location="/WEB-INF/jsp/bookcountry/manifest/item/detail.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class ManifestItemAction extends BaseAction {
    
    private final Logger logger = Logger.getLogger(ManifestItemAction.class);

    private Manifest manifest;
    private ManifestItem manifestItem;
    private Table listTable;
    
    private File upload;//The actual file
    private String uploadContentType; //The content type of the file
    private String uploadFileName; //The uploaded file name    
    
    private static final int ISBN = 0;
    private static final int ISBN13 = 1;
    private static final int QUANTITY = 2; 
    private static final int COND = 3; 
    private static final int BIN = 4; 
    private static final int TITLE = 5;
    
    private static final int BATCH_SIZE = 50;
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String detail(){
        ManifestSessionLocal mSession = getManifestSession();
        manifestItem = mSession.findManifestItemById(id);
        return "detail";
    }
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String list(){
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String create(){
        return "crud";
    }
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String createSubmit(){
        try {
            ManifestSessionLocal mSession = getManifestSession();
            manifest = mSession.findById(manifest.getId());
            // see if this manifestitem is already on the manifest
            QueryInput qi = new QueryInput();
            qi.addAndCriterion(Restrictions.eq("manifest", manifest));
            qi.addAndCriterion(Restrictions.eq("isbn", manifestItem.getIsbn()));
            DaoResults dr = mSession.findAllManifestItems(qi);
            if (dr.getDataSize() > 0){
                ManifestItem dbItem = ((List<ManifestItem>)dr.getData()).get(0);
                dbItem.setQuantity(dbItem.getQuantity()+manifestItem.getQuantity());
                
                if (dbItem.getBin() == null || dbItem.getBin().length() == 0){
                    InventoryItemSessionLocal iisession = getInventoryItemSession();
                    InventoryItem ii = iisession.findByIsbnCond(manifestItem.getIsbn(), manifestItem.getCond());
                    if (ii != null){
                        dbItem.setBin(ii.getBin());
                    }
                }
                
                mSession.update(dbItem);
            } else {
                if (manifestItem.getBin() == null || manifestItem.getBin().length() == 0){
                    InventoryItemSessionLocal iisession = getInventoryItemSession();
                    InventoryItem ii = iisession.findByIsbnCond(manifestItem.getIsbn(), manifestItem.getCond());
                    if (ii != null){
                        manifestItem.setBin(ii.getBin());
                    }
                }
                if (manifestItem.getId() == null){
                    AmazonData amazonData = AmazonItemLookupSoap.getInstance().lookupAmazonData(manifestItem.getIsbn());
                    if (amazonData.getDataLoaded()){
                        manifestItem.setTitle(amazonData.getTitle());
                    }
                }
                if (IsbnUtil.isValid10(manifestItem.getIsbn())){
                    manifestItem.setIsbn13(IsbnUtil.getIsbn13(manifestItem.getIsbn()));
                } else if (IsbnUtil.isValid13(manifestItem.getIsbn())){
                    manifestItem.setIsbn13(manifestItem.getIsbn());
                }
                manifestItem.setManifest(manifest);
                manifestItem.setDate(Calendar.getInstance().getTime());
                mSession.create(manifestItem);
            }
            
            mSession.update(manifest);
            mSession.updateCounts(manifest.getId());
            
            id = manifestItem.getId();
            setSuccess(true);
            setMessage("Created New Manifest Item");
        } catch (Exception e){
            logger.error("Exception", e);
            setSuccess(false);
            setMessage("Could not create the Manifest Item, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String edit(){
        ManifestSessionLocal mSession = getManifestSession();
        manifestItem = mSession.findManifestItemById(id);
        return "crud";
    }
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String editSubmit(){
        try {
            ManifestSessionLocal mSession = getManifestSession();
            ManifestItem dbmi = mSession.findManifestItemById(manifestItem.getId(), "manifest");
            if (dbmi != null){
                dbmi.setTitle(manifestItem.getTitle());
                //dbmi.setCond(manifestItem.getCond());
                dbmi.setIsbn(manifestItem.getIsbn());
                dbmi.setIsbn13(manifestItem.getIsbn13());
                
                manifest = dbmi.getManifest();
                
                dbmi.setQuantity(manifestItem.getQuantity());
                dbmi.setBin(manifestItem.getBin());
                mSession.update(dbmi);
                mSession.updateCounts(manifest.getId());
                setSuccess(true);
                setMessage("Updated the manifest item.");
            } else {
                logger.error("Could not find manifest item to update for id: "+id);
                setSuccess(false);
                setMessage("Could not update the manifest item, there was a system error.");
            }
        } catch (Exception e){
            logger.error("Could not update manifest item", e);
            setSuccess(false);
            setMessage("Could not update the manifest item, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String delete(){
        try {
            ManifestSessionLocal mSession = getManifestSession();
            
            ManifestItem dbmi = mSession.findManifestItemById(id, "manifest");
            manifest = dbmi.getManifest();
            
            mSession.deleteManifestItem(id);
            mSession.updateCounts(manifest.getId());
            
            setSuccess(true);
            setMessage("Deleted manifest item");
        } catch (Exception e){
            logger.error("Could not delete manifest item id: "+id);
            setSuccess(false);
            setMessage("Could not delete manifest item, there was a system error.");
        }
        return "status";
    }
    
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String viewManifest(){
        setupListTable();
        ManifestSessionLocal mSession = getManifestSession();
        manifest = mSession.findById(id);
        return "viewmanifest";
    }
    
    
    @ActionRole({"BcManifestAdmin", "BcManifestViewer"})
    public String listData(){
        setupListTable(); 
        try {
            ManifestSessionLocal mSession = getManifestSession();
            manifest = mSession.findById(id);
            queryInput.addAndCriterion(Restrictions.eq("manifest", manifest));
            queryResults = new QueryResults(mSession.findAllManifestItems(queryInput));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not list data for manifests", e);
        }
        return "queryresults";
    }
    
    private void setupListTable(){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        //cd.add(new ColumnData("date"));
        cd.add(new ColumnData("cond"));
        cd.add(new ColumnData("isbn"));
        cd.add(new ColumnData("bin"));
        cd.add(new ColumnData("isbn13"));
        cd.add(new ColumnData("quantity").setType("int"));
        cd.add(new ColumnData("title"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50));
        //cm.add(new ColumnModel("date", "Date", 150).setRenderer("dateRenderer"));
        cm.add(new ColumnModel("isbn", "ISBN", 100));
        cm.add(new ColumnModel("isbn13", "ISBN13", 100));
        cm.add(new ColumnModel("quantity", "Quantity", 100));
        cm.add(new ColumnModel("cond", "Condition", 100).setRenderer("conditionRenderer"));
        cm.add(new ColumnModel("bin", "Bin", 100));
        cm.add(new ColumnModel("title", "Title", 700));
        listTable.setColumnModels(cm);
        
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("string", "title"));
        filters.add(new Filter("string", "isbn"));
        filters.add(new Filter("string", "cond"));
        filters.add(new Filter("string", "bin"));
        filters.add(new Filter("string", "isbn13"));
        filters.add(new Filter("integer", "quantity"));
        //filters.add(new Filter("date", "date"));
        listTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        
        if (getIsBcManifestAdmin()) {
            buttons.add(new ToolbarButton("Create", "createItemButtonClick", "create_icon", "Create A New Manifest Item"));
            buttons.add(new ToolbarButton("Edit", "editItemButtonClick", "edit_icon", "Edit The Selected Manifest Item").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Delete", "deleteItemButtonClick", "delete_icon", "Delete The Selected Manifest Item").setSingleRowAction(true));
            buttons.add(new ToolbarButton("History", "itemHistoryButtonClick", "calendar_icon", "View The Selected Manifest Items History Of Changes").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Upload", "uploadButtonClick", "upload_icon", "Upload Manifest Items"));
        }
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("id");
        listTable.setDefaultSortDir(Table.SORT_DIR_DESC);
        
        // this is for the exports
        setExcelExportFileName("BookcountryManifestItemExport");
        setExcelExportSheetName("ManifestItems");
        
    }
    
//    public String upload(){
//        
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
//        try {
//            Date now = Calendar.getInstance().getTime();
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
//                setMessage("The uploaded file contained no manifest items to upload.");
//                return "status";
//            }
//            
//            // Check for header row and hidden columns
//            try {
//                
//                // Check for hidden columns
//                for(int i = ISBN; i <= TITLE; i++) {
//                    if(s.getCell(i, 0).isHidden()) {
//                        setSuccess(false);
//                        setMessage("There are hidden columns in the first 6 columns.  Not supported.");
//                        return "status";
//                    }
//                }
//
//                if (!s.getCell(ISBN,0).getContents().startsWith("ISBN") || 
//                    !s.getCell(ISBN13,0).getContents().startsWith("ISBN13") || 
//                    !s.getCell(QUANTITY,0).getContents().startsWith("Quantity") ||
//                    !s.getCell(COND,0).getContents().startsWith("Condition") ||
//                    !s.getCell(BIN,0).getContents().startsWith("Bin") ||
//                    !s.getCell(TITLE,0).getContents().startsWith("Title"))
//                {
//                    setSuccess(false);
//                    setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, ISBN13, Quantity, Conditon, Bin, Title.");
//                    return "status";
//                }
//
//                
//            } catch (ArrayIndexOutOfBoundsException aie) {
//                setSuccess(false);
//                setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, ISBN13, Quantity, Condition, Bin, Title.");
//                return "status";                                
//            }
//            
//            // Process each row.  Skip header row
//            HashMap<String, ManifestItem> items = new HashMap<String, ManifestItem>();
//            for(int row = 1; row < numRows; row++) {
//                
//                // Check for hidden row (and skip it)
//                if(s.getRowView(row).isHidden()) {
//                    logger.debug("Skipping hidden row: " + row);
//                    continue;
//                }
//                
//                //
//                // Process each cell
//                //
//                ManifestItem mi = new ManifestItem();
//                mi.setDate(now);
//                String isbn = s.getCell(ISBN, row).getContents();
//                if (isbn != null && isbn.length() > 0) {
//                    mi.setIsbn(isbn);
//                }
//                String isbn13 = s.getCell(ISBN13, row).getContents();
//                if (isbn13 != null && isbn13.length() > 0) {
//                    mi.setIsbn13(isbn13);
//                }
//                String quantity = s.getCell(QUANTITY, row).getContents();
//                if (quantity != null && quantity.length() > 0) {
//                    try {
//                        mi.setQuantity(Integer.parseInt(quantity));
//                    } catch (NumberFormatException nfe){
//                        // could not set quantity
//                    }
//                }
//                String cond = s.getCell(COND, row).getContents();
//                if (cond != null && cond.length() > 0) {
//                    mi.setCond(cond);
//                } else {
//                    mi.setCond("hurt");
//                }
//                String bin = s.getCell(BIN, row).getContents();
//                if (bin != null && bin.length() > 0) {
//                    mi.setBin(bin);
//                }
//                String title = s.getCell(TITLE, row).getContents();
//                if (title != null && title.length() > 0) {
//                    if (title.length() > 255) title = title.substring(0, 256);
//                    mi.setTitle(title);
//                }
//                mi.fixIsbn();
//                items.put(mi.getIsbn(), mi);
//            }
//            // do amazon lookups on the manifest items
//            List<String> isbns = new ArrayList<String>();
//            int i = 0;
//            for (String key : items.keySet()){
//                if (i % 20 == 0 && i > 0){
//                    //logger.error("ISBNS size: "+isbns.size());
//                    HashMap<String, String> titles = AmazonItemLookupSoap.getInstance().lookupTitles(isbns.toArray(new String[]{}));
//                    for (String tkey : titles.keySet()){
//                        if (items.containsKey(tkey)){
//                            items.get(tkey).setTitle(titles.get(tkey));
//                        }
//                    }
//                    isbns.clear();
//                }
//                // only looking it up if we don't have it
//                if (items.get(key).getTitle() == null || items.get(key).getTitle().length() == 0){
//                    i++;
//                    isbns.add(items.get(key).getIsbn());
//                }
//            }
//            // process remaining items into amazon
//            if (isbns.size() > 0){
//                //logger.error("Remaining ISBNS size: "+isbns.size());
//                HashMap<String, String> titles = AmazonItemLookupSoap.getInstance().lookupTitles(isbns.toArray(new String[]{}));
//                for (String tkey : titles.keySet()){
//                    if (items.containsKey(tkey)){
//                        items.get(tkey).setTitle(titles.get(tkey));
//                    }
//                }
//            }
//
//            // process all of the manifest items
//            ManifestSessionLocal mSession = getManifestSession();
//            if (mSession.processItemUpload(id, new ArrayList<ManifestItem>(items.values()))){
//                setSuccess(true);
//                setMessage("Uploaded the manifest items.");
//            } else {
//                setSuccess(false);
//                setMessage("There was a system error and we could not process the upload file");
//            }
//        } catch (Exception e){
//            logger.error("Could not upload manifest items", e);
//            setSuccess(false);
//            setMessage("Could not upload the manifest items, there was a system error.");
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
        
        try {
            Date now = Calendar.getInstance().getTime();
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
                setMessage("The uploaded file contained no manifest items to upload.");
                return "status";
            }
            
            // Check for header row and hidden columns
            try {
                Row r = s.getRow(0);
                // Check for hidden columns
                for(int i = ISBN; i <= TITLE; i++) {
                    if(s.isColumnHidden(i)) {
                        setSuccess(false);
                        setMessage("There are hidden columns in the first 6 columns.  Not supported.");
                        return "status";
                    }
                }

                if (!getCellValue(r.getCell(ISBN)).startsWith("ISBN") || 
                    !getCellValue(r.getCell(ISBN13)).startsWith("ISBN13") || 
                    !getCellValue(r.getCell(QUANTITY)).startsWith("Quantity") ||
                    !getCellValue(r.getCell(COND)).startsWith("Condition") ||
                    !getCellValue(r.getCell(BIN)).startsWith("Bin") ||
                    !getCellValue(r.getCell(TITLE)).startsWith("Title"))
                {
                    setSuccess(false);
                    setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, ISBN13, Quantity, Conditon, Bin, Title.");
                    return "status";
                }

                
            } catch (ArrayIndexOutOfBoundsException aie) {
                setSuccess(false);
                setMessage("The header row is missing from the uploaded file or the file isn't in the correct format.  Expecting ISBN, ISBN13, Quantity, Condition, Bin, Title.");
                return "status";                                
            }
            
            // Process each row.  Skip header row
            HashMap<String, ManifestItem> items = new HashMap<String, ManifestItem>();
            for(int row = 1; row < numRows; row++) {
                Row r = s.getRow(row);
                if (r == null)
                    continue;
                // Check for hidden row (and skip it)
                if(r.getZeroHeight()) {
                    logger.debug("Skipping hidden row: " + row);
                    continue;
                }
                
                //
                // Process each cell
                //
                ManifestItem mi = new ManifestItem();
                mi.setDate(now);
                String isbn = getCellValue(r.getCell(ISBN));
                if (isbn != null && isbn.length() > 0) {
                    mi.setIsbn(isbn);
                }
                String isbn13 = getCellValue(r.getCell(ISBN13));
                if (isbn13 != null && isbn13.length() > 0) {
                    mi.setIsbn13(isbn13);
                }
                String quantity = getCellValue(r.getCell(QUANTITY));
                if (quantity != null && quantity.length() > 0) {
                    try {
                        mi.setQuantity(Integer.parseInt(quantity));
                    } catch (NumberFormatException nfe){
                        // could not set quantity
                    }
                }
                String cond = getCellValue(r.getCell(COND));
                if (cond != null && cond.length() > 0) {
                    mi.setCond(cond);
                } else {
                    mi.setCond("hurt");
                }
                String bin = getCellValue(r.getCell(BIN));
                if (bin != null && bin.length() > 0) {
                    mi.setBin(bin);
                }
                String title = getCellValue(r.getCell(TITLE));
                if (title != null && title.length() > 0) {
                    if (title.length() > 255) title = title.substring(0, 256);
                    mi.setTitle(title);
                }
                mi.fixIsbn();
                items.put(mi.getIsbn(), mi);
            }
            // do amazon lookups on the manifest items
            List<String> isbns = new ArrayList<String>();
            int i = 0;
            for (String key : items.keySet()){
                if (i % 20 == 0 && i > 0){
                    //logger.error("ISBNS size: "+isbns.size());
                    HashMap<String, String> titles = AmazonItemLookupSoap.getInstance().lookupTitles(isbns.toArray(new String[]{}));
                    for (String tkey : titles.keySet()){
                        if (items.containsKey(tkey)){
                            items.get(tkey).setTitle(titles.get(tkey));
                        }
                    }
                    isbns.clear();
                }
                // only looking it up if we don't have it
                if (items.get(key).getTitle() == null || items.get(key).getTitle().length() == 0){
                    i++;
                    isbns.add(items.get(key).getIsbn());
                }
            }
            // process remaining items into amazon
            if (isbns.size() > 0){
                //logger.error("Remaining ISBNS size: "+isbns.size());
                HashMap<String, String> titles = AmazonItemLookupSoap.getInstance().lookupTitles(isbns.toArray(new String[]{}));
                for (String tkey : titles.keySet()){
                    if (items.containsKey(tkey)){
                        items.get(tkey).setTitle(titles.get(tkey));
                    }
                }
            }

            // process all of the manifest items
            ManifestSessionLocal mSession = getManifestSession();
            if (mSession.processItemUpload(id, new ArrayList<ManifestItem>(items.values()))){
                setSuccess(true);
                setMessage("Uploaded the manifest items.");
            } else {
                setSuccess(false);
                setMessage("There was a system error and we could not process the upload file");
            }
        } catch (Exception e){
            logger.error("Could not upload manifest items", e);
            setSuccess(false);
            setMessage("Could not upload the manifest items, there was a system error.");
        }
        return "status";
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

    public ManifestItem getManifestItem() {
        return manifestItem;
    }

    public void setManifestItem(ManifestItem manifestItem) {
        this.manifestItem = manifestItem;
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
