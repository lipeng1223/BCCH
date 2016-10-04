package com.bc.actions.bookcountry;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

//import jxl.NumberCell;
//import jxl.Sheet;
//import jxl.Workbook;
//import jxl.biff.EmptyCell;

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
import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.ejb.OrderSessionLocal;
import com.bc.excel.ExcelReport;
import com.bc.excel.ExcelReportExporter;
import com.bc.orm.CustomerOrderItem;
import com.bc.orm.InventoryItem;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.util.ActionRole;
import java.io.FileInputStream;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="status", location="/WEB-INF/jsp/status.jsp")
})
public class SalesAction extends BaseAction {
    
    private static final Logger logger = Logger.getLogger(SalesAction.class);
    
    private File upload;//The actual file
    private String uploadContentType; //The content type of the file
    private String uploadFileName; //The uploaded file name    
 
    @ActionRole({"BcOrderAdmin", "BcOrderViewer"})
	public String offer(){
        try {
            List<SalesOffer> offers = readUpload();
            
            if (offers == null){
                return "status";
            }
            
            InventoryItemSessionLocal invSession = getInventoryItemSession();
            OrderSessionLocal orderSession = getOrderSession();
            logger.info("offers size: "+offers.size());
            for(SalesOffer so : offers) {
                InventoryItem ii = invSession.findByIsbnCond(so.getIsbn(), "hurt");
                if (ii == null)
                    ii = invSession.findByIsbnCond(so.getIsbn(), "unjacketed");
                if (ii == null)
                    ii = invSession.findByIsbnCond(so.getIsbn(), "overstock");
                if (ii == null)
                    continue;

                so.setInventoryItem(ii);
                so.setOrderItems(orderSession.findLastN(ii, 5));
            }
            
            // setup the column data and column models and the results

            LinkedHashMap<String, List> sheets = new LinkedHashMap<String, List>();
            sheets.put("Offers", offers);
            
            List<ColumnData> columnDatas = new ArrayList<ColumnData>();
            columnDatas.add(new ColumnData("isbn"));
            columnDatas.add(new ColumnData("title"));
            columnDatas.add(new ColumnData("quantity").setType("int"));
            columnDatas.add(new ColumnData("offer").setType("float"));
            columnDatas.add(new ColumnData("total").setType("float"));
            columnDatas.add(new ColumnData("inventoryItem.cond"));
            columnDatas.add(new ColumnData("inventoryItem.salesRank"));
            columnDatas.add(new ColumnData("inventoryItem.listPrice"));
            columnDatas.add(new ColumnData("inventoryItem.sellingPrice"));
            columnDatas.add(new ColumnData("co1.price"));
            columnDatas.add(new ColumnData("co2.price"));
            columnDatas.add(new ColumnData("co3.price"));
            columnDatas.add(new ColumnData("co4.price"));
            columnDatas.add(new ColumnData("co5.price"));

            List<ColumnModel> columnModels = new ArrayList<ColumnModel>();
            columnModels.add(new ColumnModel("isbn", "ISBN", 100));
            columnModels.add(new ColumnModel("title", "Title", 100));
            columnModels.add(new ColumnModel("quantity", "Quantity", 100));
            columnModels.add(new ColumnModel("offer", "Offer", 100));
            columnModels.add(new ColumnModel("total", "Total Offer", 100));
            columnModels.add(new ColumnModel("inventoryItem.cond", "Condition", 100));
            columnModels.add(new ColumnModel("inventoryItem.salesRank", "Sales Rank", 100));
            columnModels.add(new ColumnModel("inventoryItem.listPrice", "List Price", 100));
            columnModels.add(new ColumnModel("inventoryItem.sellingPrice", "Selling Price", 100));
            columnModels.add(new ColumnModel("co1.price", "Last Order 1", 100));
            columnModels.add(new ColumnModel("co2.price", "Last Order 2", 100));
            columnModels.add(new ColumnModel("co3.price", "Last Order 3", 100));
            columnModels.add(new ColumnModel("co4.price", "Last Order 4", 100));
            columnModels.add(new ColumnModel("co5.price", "Last Order 5", 100));

            ExcelReport report = new ExcelReport(sheets, columnDatas, columnModels);
            ExcelReportExporter ere = new ExcelReportExporter(report);
            setExcelExportFileName("SalesOffer");
            setExcelReportExporter(ere);
            
        } catch (Throwable t){
            logger.error("Failed doing sales offer upload", t);
            return "error";
        }
		return "excelreport";
	}
    
    public class SalesOffer {
        private String isbn;
        private Integer quantity;
        private Float offer;
        private Float total;
        private String title;
        private InventoryItem inventoryItem;
        private List<CustomerOrderItem> orderItems;
        
        public String getIsbn() {
            return isbn;
        }
        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }
        public Integer getQuantity() {
            return quantity;
        }
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        public Float getOffer() {
            return offer;
        }
        public void setOffer(Float offer) {
            this.offer = offer;
        }
        public Float getTotal() {
            return total;
        }
        public void setTotal(Float total) {
            this.total = total;
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public List<CustomerOrderItem> getOrderItems() {
            return orderItems;
        }
        public void setOrderItems(List<CustomerOrderItem> orderItems) {
            this.orderItems = orderItems;
        }
        
        public CustomerOrderItem getCo1() { 
            if (orderItems != null && orderItems.size() > 0)
                return orderItems.get(0);
            return null;
        }
        public CustomerOrderItem getCo2() { 
            if (orderItems != null && orderItems.size() > 1)
                return orderItems.get(1);
            return null;
        }
        public CustomerOrderItem getCo3() { 
            if (orderItems != null && orderItems.size() > 2)
                return orderItems.get(2);
            return null;
        }
        public CustomerOrderItem getCo4() { 
            if (orderItems != null && orderItems.size() > 3)
                return orderItems.get(3);
            return null;
        }
        public CustomerOrderItem getCo5() { 
            if (orderItems != null && orderItems.size() > 4)
                return orderItems.get(4);
            return null;
        }
        public InventoryItem getInventoryItem() {
            return inventoryItem;
        }
        public void setInventoryItem(InventoryItem inventoryItem) {
            this.inventoryItem = inventoryItem;
        }
    }
    
//    private List<SalesOffer> readUpload(){
//        
//        List<SalesOffer> all = new ArrayList<SalesOffer>();
//        
//        Workbook workbook = null;
//        try {
//            workbook = Workbook.getWorkbook(upload);
//        } catch(Exception e) {
//            logger.error("Unsupported file type.", e);
//            setSuccess(false);
//            setMessage("Unsupported file type.  Please ensure you are uploading an Excel file.");
//            return null;
//        }
//        
//        Sheet s = workbook.getSheet(0);
//        
//        int numRows = s.getRows();
//        if(numRows <= 1) {
//            setSuccess(false);
//            setMessage("The uploaded file contained no items.");
//            return null;
//        }
//        
//        try {
//            
//            for(int row = 1; row < numRows; row++) {
//                //logger.info(row+" of "+numRows);
//                SalesOffer so = new SalesOffer();
//                so.setIsbn(s.getCell(0, row).getContents());
//                if (so.getIsbn() != null && so.getIsbn().length() > 0){
//                    if (! (s.getCell(1, row) instanceof EmptyCell)) {
//                        so.setTitle(s.getCell(1, row).getContents());
//                    }
//                    
//                    String quantity = s.getCell(2, row).getContents();
//                    if (quantity != null && quantity.length() > 0) {
//                        try {
//                            so.setQuantity(Integer.parseInt(quantity));
//                        } catch (NumberFormatException nfe){
//                            // could not set quantity
//                        }
//                    }
//                    
//                    String offer = s.getCell(3, row).getContents();
//                    if (offer != null && offer.length() > 0) {
//                        try {
//                            so.setOffer(Float.parseFloat(offer));
//                        } catch (NumberFormatException nfe){
//                            // could not set quantity
//                        }
//                    }
//                    
//                    if (! (s.getCell(4, row) instanceof EmptyCell)){
//                        String total = s.getCell(4, row).getContents();
//                        if (total != null && total.length() > 0) {
//                            try {
//                                so.setTotal(Float.parseFloat(total));
//                            } catch (NumberFormatException nfe){
//                                // could not set quantity
//                            }
//                        }
//                    }
//                    all.add(so);
//                }
//            }
//        } catch (Throwable t){
//            logger.error("error processing file", t);
//            setSuccess(false);
//            setMessage("There were errors processing the file, make sure it is a Sales Offer Excel file, ISBN, Title, Quantity, Offer, Total.");
//            return null;
//        }
//        
//        return all;
//    } 
    private List<SalesOffer> readUpload(){
        
        List<SalesOffer> all = new ArrayList<SalesOffer>();
        
        XSSFWorkbook workbook = null;
            try {
                FileInputStream fis = new FileInputStream(upload);
                workbook = new XSSFWorkbook(fis);
            } catch(Exception e) {
                logger.error("Unsupported file type.", e);
                setSuccess(false);
                setMessage("Unsupported file type.  Please ensure you are uploading an Excel file.");
                return null;
            }
            
            XSSFSheet s = workbook.getSheetAt(0);
            
            int numRows = s.getPhysicalNumberOfRows() + 1;
        if(numRows <= 1) {
            setSuccess(false);
            setMessage("The uploaded file contained no items.");
            return null;
        }
        
        try {
            
            for(int row = 1; row < numRows; row++) {
                //logger.info(row+" of "+numRows);
                Row r = s.getRow(row);
                if (r == null)
                    continue;
                SalesOffer so = new SalesOffer();
                so.setIsbn(r.getCell(0).getStringCellValue());
                if (so.getIsbn() != null && so.getIsbn().length() > 0){
                    if (r.getCell(1) != null) {
                        so.setTitle(r.getCell(1).getStringCellValue());
                    }
                    
                    String quantity = r.getCell(2).getStringCellValue();
                    if (quantity != null && quantity.length() > 0) {
                        try {
                            so.setQuantity(Integer.parseInt(quantity));
                        } catch (NumberFormatException nfe){
                            // could not set quantity
                        }
                    }
                    
                    String offer = r.getCell(3).getStringCellValue();
                    if (offer != null && offer.length() > 0) {
                        try {
                            so.setOffer(Float.parseFloat(offer));
                        } catch (NumberFormatException nfe){
                            // could not set quantity
                        }
                    }
                    
                    if (r.getCell(4) != null){
                        String total = r.getCell(4).getStringCellValue();
                        if (total != null && total.length() > 0) {
                            try {
                                so.setTotal(Float.parseFloat(total));
                            } catch (NumberFormatException nfe){
                                // could not set quantity
                            }
                        }
                    }
                    all.add(so);
                }
            }
        } catch (Throwable t){
            logger.error("error processing file", t);
            setSuccess(false);
            setMessage("There were errors processing the file, make sure it is a Sales Offer Excel file, ISBN, Title, Quantity, Offer, Total.");
            return null;
        }
        
        return all;
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

	
}
