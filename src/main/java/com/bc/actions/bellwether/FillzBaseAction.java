package com.bc.actions.bellwether;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

//import jxl.NumberCell;
//import jxl.Sheet;
//import jxl.Workbook;
//import jxl.biff.EmptyCell;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.log4j.Logger;

import com.bc.actions.BaseAction;
import com.bc.orm.BellOrder;
import java.io.FileInputStream;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

public class FillzBaseAction extends BaseAction {
    
    private static final Logger logger = Logger.getLogger(FillzBaseAction.class);
    
    private File upload;//The actual file
    private String uploadContentType; //The content type of the file
    private String uploadFileName; //The uploaded file name    
    
//    protected List<BellOrder> readUpload(Integer sheet, Boolean origFillz){
//
//        List<BellOrder> all = new ArrayList<BellOrder>();
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
//        Sheet s = workbook.getSheet(sheet);
//        
//        int numRows = s.getRows();
//        if(numRows <= 1) {
//            logger.error("Now rows in the worksheet");
//            setSuccess(false);
//            setMessage("The uploaded file contained no items.");
//            return null;
//        }
//        
//        try {
//            DecimalFormat df = new DecimalFormat("00000000");
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
//            
//            logger.info("Processing fillz order file, numRows: "+numRows);
//            for(int row = 1; row < numRows; row++) {
//                
//                BellOrder bo = new BellOrder();
//                bo.setPaymentsStatus(s.getCell(0, row).getContents());
//                bo.setOrderId(df.format(new Double(s.getCell(1, row).getContents())));
//                //logger.info("orderId: "+bo.getOrderId()+" contents: "+s.getCell(1, row).getContents());
//                bo.setOrderItemId(s.getCell(2, row).getContents());
//                if (! (s.getCell(3, row) instanceof EmptyCell))
//                    bo.setPaymentsDate(sdf.parse(s.getCell(3, row).getContents()));
//                bo.setPaymentsTransactionId(s.getCell(4, row).getContents());
//                bo.setProductId(s.getCell(5, row).getContents());
//                bo.setItemName(s.getCell(6, row).getContents());
//                bo.setListingId(s.getCell(7, row).getContents());
//                bo.setSku(s.getCell(8, row).getContents());
//                if (! (s.getCell(9, row) instanceof EmptyCell))
//                    bo.setPrice(new Float(((NumberCell)s.getCell(9, row)).getValue()));
//                if (! (s.getCell(10, row) instanceof EmptyCell))
//                    bo.setShippingFee(new Float(((NumberCell)s.getCell(10, row)).getValue()));
//                if (! (s.getCell(11, row) instanceof EmptyCell))
//                    bo.setQuantityPurchased(new Double(((NumberCell)s.getCell(11, row)).getValue()).intValue());
//                if (! (s.getCell(12, row) instanceof EmptyCell))
//                    bo.setTotalPrice(new Float(((NumberCell)s.getCell(12, row)).getValue()));
//                if (! (s.getCell(13, row) instanceof EmptyCell))
//                    bo.setPurchaseDate(sdf.parse(s.getCell(13, row).getContents()));
//                    //bo.setPurchaseDate(((DateCell)s.getCell(13, row)).getDate());
//                bo.setBatchId(s.getCell(14, row).getContents());
//                bo.setBuyerEmail(s.getCell(15, row).getContents());
//                bo.setBuyerName(s.getCell(16, row).getContents());
//                bo.setRecipientName(s.getCell(17, row).getContents());
//                bo.setShipAddress1(s.getCell(18, row).getContents());
//                bo.setShipAddress2(s.getCell(19, row).getContents());
//                bo.setShipCity(s.getCell(20, row).getContents());
//                bo.setShipState(s.getCell(21, row).getContents());
//                bo.setShipZip(s.getCell(22, row).getContents());
//                bo.setShipCountry(s.getCell(23, row).getContents());
//                bo.setSpecialComments(s.getCell(24, row).getContents());
//                bo.setUpc(s.getCell(25, row).getContents());
//                bo.setShipMethod(s.getCell(26, row).getContents());
//                // extra fillz crap
//                bo.setFillzStatus(s.getCell(27, row).getContents());
//                bo.setLocation(s.getCell(28, row).getContents());
//                bo.setTracking(s.getCell(29, row).getContents());
//                bo.setBuyerNote(s.getCell(30, row).getContents());
//                bo.setSellerNote(s.getCell(31, row).getContents());
//                if (origFillz){
//                    bo.setPaymentMethod(s.getCell(32, row).getContents());
//                    if (! (s.getCell(33, row) instanceof EmptyCell))
//                        bo.setItemCondition(new Double(((NumberCell)s.getCell(33, row)).getValue()).intValue());
//                    bo.setItemSource(s.getCell(34, row).getContents());
//                    if (! (s.getCell(35, row) instanceof EmptyCell))
//                        bo.setFillzCost(new Float(((NumberCell)s.getCell(35, row)).getValue()));
//                    bo.setIsBook(s.getCell(36, row).getContents());
//                    bo.setSellerId(s.getCell(37, row).getContents());
//                
//                    if (bo.getShipMethod() == null){
//                        bo.setShipMethod("");
//                    }
//                    if (bo.getShipCountry() == null){
//                        bo.setShipCountry("");
//                    }
//                    if (bo.getShipZip() == null){
//                        bo.setShipZip("");
//                    }
//                    if (bo.getShipState() == null){
//                        bo.setShipState("");
//                    }
//                    
//                    // fix zip if needed
//                    if (bo.getShipCountry().toUpperCase().equals("US") || bo.getShipCountry().toUpperCase().equals("USA") || bo.getShipCountry().toUpperCase().equals("U.S.A.")){
//                        if (bo.getShipZip().length() == 4){
//                            bo.setShipZip("0"+bo.getShipZip());
//                        }
//                    }
//                }
//                
//                all.add(bo);
//            }
//            
//        } catch (Throwable t){
//            setSuccess(false);
//            setMessage("There were errors processing the file, make sure it is a Fillz Excel file.");
//            return null;
//        }
//        
//        return all;
//    }
    
    protected List<BellOrder> readUpload(Integer sheet, Boolean origFillz){

        List<BellOrder> all = new ArrayList<BellOrder>();
        
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
        
        XSSFSheet s = workbook.getSheetAt(sheet);
        
        int numRows = s.getPhysicalNumberOfRows() + 1;
        if(numRows <= 1) {
            logger.error("No rows in the worksheet");
            setSuccess(false);
            setMessage("The uploaded file contained no items.");
            return null;
        }
        
        try {
            DecimalFormat df = new DecimalFormat("00000000");
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
            
            logger.info("Processing fillz order file, numRows: "+numRows);
            for(int row = 1; row < numRows; row++) {
                //logger.info("Row : " + row);
                Row r = s.getRow(row);
                if (r == null)
                    continue;
                BellOrder bo = new BellOrder();
//                logger.info("Cell : A");
                bo.setPaymentsStatus(getCellValue(r.getCell(0)));
//                logger.info("Cell : B");
                bo.setOrderId(df.format(new Double(getCellValue(r.getCell(1)))));
                //logger.info("orderId: "+bo.getOrderId()+" contents: "+s.getCell(1, row).getContents());
//                logger.info("Cell : C");
                bo.setOrderItemId(getCellValue(r.getCell(2)));
//                logger.info("Cell : D");
                if (r.getCell(3) != null)
                    bo.setPaymentsDate(sdf.parse(getCellValue(r.getCell(3))));
//                logger.info("Cell : E");
                bo.setPaymentsTransactionId(getCellValue(r.getCell(4)));
//                logger.info("Cell : F");
                bo.setProductId(getCellValue(r.getCell(5)));
//                logger.info("Cell : G");
                bo.setItemName(getCellValue(r.getCell(6)));
//                logger.info("Cell : H");
                bo.setListingId(getCellValue(r.getCell(7)));
//                logger.info("Cell : I");
                bo.setSku(getCellValue(r.getCell(8)));
//                logger.info("Cell : J");
                if (r.getCell(9) != null)
                    bo.setPrice(Float.parseFloat(getCellValue(r.getCell(9))));
//                logger.info("Cell : K");
                if (r.getCell(10) != null)
                    bo.setShippingFee(Float.parseFloat(getCellValue(r.getCell(10))));
//                logger.info("Cell : L");
                if (r.getCell(11) != null)
                    bo.setQuantityPurchased(Integer.parseInt(getCellValue(r.getCell(11))));
//                logger.info("Cell : M");
                if (r.getCell(12) != null)
                    bo.setTotalPrice(Float.parseFloat(getCellValue(r.getCell(12))));
//                logger.info("Cell : N");
                if (r.getCell(13) != null)
                    bo.setPurchaseDate(sdf.parse(getCellValue(r.getCell(13))));
//                logger.info("Cell : O");
                bo.setBatchId(getCellValue(r.getCell(14)));
//                logger.info("Cell : P");
                bo.setBuyerEmail(getCellValue(r.getCell(15)));
//                logger.info("Cell : Q");
                bo.setBuyerName(getCellValue(r.getCell(16)));
//                logger.info("Cell : R");
                bo.setRecipientName(getCellValue(r.getCell(17)));
//                logger.info("Cell : S");
                bo.setShipAddress1(getCellValue(r.getCell(18)));
//                logger.info("Cell : T");
                bo.setShipAddress2(getCellValue(r.getCell(19)));
//                logger.info("Cell : U");
                bo.setShipCity(getCellValue(r.getCell(20)));
//                logger.info("Cell : V");
                bo.setShipState(getCellValue(r.getCell(21)));
//                logger.info("Cell : W");
                bo.setShipZip(getCellValue(r.getCell(22)));
//                logger.info("Cell : X");
                bo.setShipCountry(getCellValue(r.getCell(23)));
//                logger.info("Cell : Y");
                bo.setSpecialComments(getCellValue(r.getCell(24)));
//                logger.info("Cell : Z");
                bo.setUpc(getCellValue(r.getCell(25)));
//                logger.info("Cell : AA");
                bo.setShipMethod(getCellValue(r.getCell(26)));
//                logger.info("Cell : AB");
                // extra fillz crap
                bo.setFillzStatus(getCellValue(r.getCell(27)));
//                logger.info("Cell : AC");
                bo.setLocation(getCellValue(r.getCell(28)));
//                logger.info("Cell : AD");
                bo.setTracking(getCellValue(r.getCell(29)));
//                logger.info("Cell : AE");
                bo.setBuyerNote(getCellValue(r.getCell(30)));
//                logger.info("Cell : AF");
                bo.setSellerNote(getCellValue(r.getCell(31)));
//                logger.info("Cell : AG");
                if (origFillz){
                    bo.setPaymentMethod(getCellValue(r.getCell(32)));
//                    logger.info("Cell : AH");
                    if (r.getCell(33) != null)
                        bo.setItemCondition(Integer.parseInt(getCellValue(r.getCell(33))));
//                    logger.info("Cell : AI");
                    bo.setItemSource(getCellValue(r.getCell(34)));
//                    logger.info("Cell : AJ");
                    if (r.getCell(35) != null)
                        bo.setFillzCost(Float.parseFloat(getCellValue(r.getCell(35))));
//                    logger.info("Cell : AK");
                    bo.setIsBook(getCellValue(r.getCell(36)));
//                    logger.info("Cell : AL");
                    bo.setSellerId(getCellValue(r.getCell(37)));
                
                    if (bo.getShipMethod() == null){
                        bo.setShipMethod("");
                    }
                    if (bo.getShipCountry() == null){
                        bo.setShipCountry("");
                    }
                    if (bo.getShipZip() == null){
                        bo.setShipZip("");
                    }
                    if (bo.getShipState() == null){
                        bo.setShipState("");
                    }
                    
                    // fix zip if needed
                    if (bo.getShipCountry().toUpperCase().equals("US") || bo.getShipCountry().toUpperCase().equals("USA") || bo.getShipCountry().toUpperCase().equals("U.S.A.")){
                        if (bo.getShipZip().length() == 4){
                            bo.setShipZip("0"+bo.getShipZip());
                        }
                    }
                }
                
                all.add(bo);
            }
            
        } catch (Exception e){
            setSuccess(false);
            logger.error(e.getMessage());
            setMessage("There were errors processing the file, make sure it is a Fillz Excel file.");
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
