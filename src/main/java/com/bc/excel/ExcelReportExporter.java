package com.bc.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

//import jxl.CellView;
//import jxl.Workbook;
//import jxl.write.Number;
//import jxl.write.*;
//import jxl.write.biff.RowsExceededException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.BuiltinFormats;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.xssf.usermodel.XSSFDataFormat;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class ExcelReportExporter {
    
    private static final Logger log = Logger.getLogger(ExcelReportExporter.class);
    private static jxl.write.NumberFormat numberFormat = new jxl.write.NumberFormat("##0.00");
    private static jxl.write.NumberFormat intFormat = new jxl.write.NumberFormat("##0");

    private ExcelDataReader dataReader = null;
    private ExcelReport report = null;
    
    private ExcelExtraDataWriter extraDataWriter = null;
    
    private SXSSFWorkbook workbook;
    
//    private static WritableCellFormat nowrapCellFormat;
//    private static WritableCellFormat integerCellFormat;
//    private static WritableCellFormat floatCellFormat;
//    private static WritableCellFormat dateCellFormat;
//    private static SimpleDateFormat dateParseFormat;
    
    private static CellStyle nowrapCellFormat;
    private static CellStyle integerCellFormat;
    private static CellStyle floatCellFormat;
    private static CellStyle dateCellFormat;
    private static CellStyle textCellFormat;
    private static SimpleDateFormat dateParseFormat;

    /**
     * HashMap containing the distinct data strings in the workbook and the cell ranges they
     * apply to.  This is used for "compression" of the workbook.
     */
    private HashMap<String,List<String>> sharedStrings = new HashMap<String,List<String>>();

    /**
     * HashMap containing the distinct cell formats in the workbook and the cell ranges they
     * apply to.  This is used for "compression" of the workbook.
     */
//    private HashMap<WritableCellFormat,List<String>> sharedFormats
//                                                = new HashMap<WritableCellFormat,List<String>>();
    private HashMap<CellStyle,List<String>> sharedFormats
                                                = new HashMap<CellStyle,List<String>>();

    /**
     * HashMap containing the max length of any data item in a data column.  This is used
     * to autofit the columns in Excel.
     */
    private HashMap<String,Integer> maxDataLengths = new HashMap<String,Integer>();

    /**
     * Keep track of sheet number by sheet name because JExcel doesn't provide a method
     * on WritableSheet to get its index number.  DUH?
     */
    private HashMap<String,Integer> sheetNums = new HashMap<String,Integer>();

    /**
     * Delimiter constants
     */
    private static final String RANGE_SEP = ExcelConstants.RANGE_SEP;
    private static final String RANGE_LIST_SEP = ExcelConstants.RANGE_LIST_SEP;
    private static final String SHEET_COL_SEP = ExcelConstants.SHEET_COL_SEP;

    /**
     * Used to track last column number written to a sheet by sheet name
     */
    private HashMap<String,Integer> sheetCols = new HashMap<String,Integer>();

    /**
     * Used to track last row number written to a sheet by sheet name
     */
    private HashMap<String,Integer> sheetRows = new HashMap<String,Integer>();

    public ExcelReportExporter(){}

    public ExcelReportExporter (ExcelReport report) {
        init(report);
    }
    
//    public void init(ExcelReport report){
//        this.report = report;
//        this.dataReader = new ExcelDataReader();
//        
//        try {
//            nowrapCellFormat = new WritableCellFormat();
//            nowrapCellFormat.setWrap(true);
//            
//            integerCellFormat = new WritableCellFormat(NumberFormats.INTEGER);
//            floatCellFormat = new WritableCellFormat(NumberFormats.FLOAT);
//            dateParseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
//            DateFormat customDateFormat = new DateFormat ("MM/dd/yyyy");
//            dateCellFormat = new WritableCellFormat (customDateFormat);
//        } catch (Exception e){
//            log.error("Could not setup the cell formats", e);
//        }
//    }
    
    public void init(ExcelReport report){
        this.report = report;
        this.dataReader = new ExcelDataReader();
        this.workbook = report.getWorkbook();
        try {
            nowrapCellFormat = workbook.createCellStyle();
            nowrapCellFormat.setWrapText(true);
            
            integerCellFormat = workbook.createCellStyle();
            integerCellFormat.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0"));
            floatCellFormat = workbook.createCellStyle();
            floatCellFormat.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0.00"));
            dateParseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            dateCellFormat = workbook.createCellStyle();
            textCellFormat = workbook.createCellStyle();
            DataFormat fmt = workbook.createDataFormat();
            textCellFormat.setDataFormat(fmt.getFormat("@"));
            
            DataFormat dateFormat = workbook.createDataFormat();
            dateCellFormat.setDataFormat(dateFormat.getFormat("mm/dd/yyyy"));
        } catch (Exception e){
            log.error("Could not setup the cell formats", e);
        }
    }

//    public boolean writeOutputStream(OutputStream os, String sheetName) {
//        if (os == null) {
//            throw new IllegalArgumentException("OutputStream os argument cannot be null");
//        }
//        WritableWorkbook wb = null;
//        try {
//            wb = createWorkbook(os);
//            // Start with first sheet (0-based indexing)
//            writeDataSheets(wb, 0, sheetName, null);
//            wb.setProtected(false);
//            wb.write();
//        } catch (IOException e) {
//            log.error("Could not export excel report", e);
//        } finally {
//            try {
//                wb.close();
//            } catch (Exception ignored) {}
//        }
//        return true;
//    }
    
    public boolean writeOutputStream(OutputStream os, String sheetName) {
        if (os == null) {
            throw new IllegalArgumentException("OutputStream os argument cannot be null");
        }
        try {
            // Start with first sheet (0-based indexing)
            writeDataSheets(workbook, 0, sheetName, null);
            //workbook.setProtected(false);
            workbook.write(os);
        } catch (Exception e) {
            log.error("Could not export excel report", e);
        } finally {
            try {
                workbook.close();
            } catch (Exception ignored) {}
        }
        return true;
    }

//    public boolean writeOutputStream(OutputStream os, List<String> sheetNames) {
//        if (os == null) {
//            throw new IllegalArgumentException("OutputStream os argument cannot be null");
//        }
//        WritableWorkbook wb = null;
//        try {
//            wb = createWorkbook(os);
//            // Start with first sheet (0-based indexing)
//            for (int i = 0; i < sheetNames.size(); i++){
//                writeDataSheets(wb, i, sheetNames.get(i), sheetNames.get(i));
//            }
//            wb.setProtected(false);
//            wb.write();
//        } catch (IOException e) {
//            log.error("Could not export excel report", e);
//        } finally {
//            try {
//                wb.close();
//            } catch (Exception ignored) {}
//        }
//        return true;
//    }
    
    public boolean writeOutputStream(OutputStream os, List<String> sheetNames) {
        if (os == null) {
            throw new IllegalArgumentException("OutputStream os argument cannot be null");
        }
        try {
            // Start with first sheet (0-based indexing)
            for (int i = 0; i < sheetNames.size(); i++){
                writeDataSheets(workbook, i, sheetNames.get(i), sheetNames.get(i));
            }
            workbook.write(os);
        } catch (IOException e) {
            log.error("Could not export excel report", e);
        } finally {
            try {
                workbook.close();
            } catch (Exception ignored) {}
        }
        return true;
    }


    /**
     * Create a WritableWorkbook object to be written to the passed OutputStream
     * @param os The OutputStream to write the workbook to
     * @return WritableWorkbook
     */
//    public WritableWorkbook createWorkbook(OutputStream os) {
//        if (os == null) {
//            throw new IllegalArgumentException("OutputStream os argument cannot be null");
//        }
//
//        Workbook wbTemplateFile = null;
//        WritableWorkbook wbExportFile = null;
//
//        try {
//
//            wbExportFile = Workbook.createWorkbook(os);
//
//        } catch (Exception e) {
//            log.error("createWorkbook", e);
//            throw new RuntimeException("createWorkbook", e);
//        } finally {
//        }
//        return wbExportFile;
//    }

    /**
     * Write the data sheets to the passed workbook
     * @param wb The WritableWorkbook to write the data sheets to
     * @param startAtSheet The sheet number to start at.  Pass -1 to start with the last
     *                  sheet number in the workbook.
     */
//    public void writeDataSheets(WritableWorkbook wb, int startAtSheet, String sheetName, String sheetKey) {
//        if (wb == null) {
//            throw new IllegalArgumentException("WritableWorkbook wb argument cannot be null");
//        }
//
//        int sheetIndex = (startAtSheet < 0) ? wb.getNumberOfSheets() : startAtSheet;
//        log.debug("Writing data sheets");
//
//        writeDataSheet(wb, sheetName, sheetIndex, sheetKey);
//    }
    
    public void writeDataSheets(SXSSFWorkbook wb, int startAtSheet, String sheetName, String sheetKey) {
        if (wb == null) {
            throw new IllegalArgumentException("WritableWorkbook wb argument cannot be null");
        }

        int sheetIndex = (startAtSheet < 0) ? wb.getNumberOfSheets() : startAtSheet;
        log.debug("Writing data sheets");

        writeDataSheet(wb, sheetName, sheetIndex, sheetKey);
    }


    /**
     * Write the data for the passed config key to the passed workbook starting at the passed
     * sheet index.  If there are more than 65,536 rows (the max allowed by Excel), multiple sheets
     * will be created in the workbook
     * @param wb The WritableWorkbook to write sheets in
     * @param configKey The config key to write data for
     * @param sheetIndex The sheet index to start at
     * @return int The last sheet index written to
     */
//    public int writeDataSheet(WritableWorkbook wb, String configKey, int sheetIndex, String sheetKey) {
//        if (wb == null) {
//            throw new IllegalArgumentException("WritableWorkbook wb argument cannot be null");
//        }
//        if (configKey == null) {
//            throw new IllegalArgumentException("String configKey argument cannot be null");
//        }
//        if (log.isDebugEnabled()) {
//            log.debug("configKey = " + configKey + ", sheetIndex = " + sheetIndex);
//        }
//
//        WritableSheet sheet = wb.createSheet(configKey, sheetIndex);
//        // Store sheet index by config key (sheet name) since JExcel (WritableSheet) doesn't support
//        // retrieval of sheet index
//        sheetNums.put(configKey, sheetIndex);
//        int sheetNameIndex = 1;
//
//        List<ExcelColumn> ecList = report.getColumns();
//        List dataList = report.getData(sheetKey);
//
//        int row = 0;
//        if (extraDataWriter != null) row = extraDataWriter.writeExtraPreData(row, sheet);
//        
//        writeColumnHeaders(sheet, ecList, 0, row);
//        row++;
//        
//        if (dataList != null) {
//            dataReader.setDataList(dataList);
//            // Iterate through data reader items and write rows to sheet
//            while (dataReader.hasNext()) {
//                // If we will exceed max Excel rows, create another sheet
//                if (row+1 == ExcelConstants.EXCEL_MAX_DATA_ROW) {
//                    sheetIndex++;
//                    sheetNameIndex++;
//                    //log.error("Excel max rows exceeded: "+ExcelConstants.EXCEL_MAX_DATA_ROW+", creating new sheet: "+(sheetIndex+1));
//                    String sheetName = new StringBuffer(configKey).append(" (").append(sheetNameIndex)
//                        .append(")").toString();
//                    sheet = wb.createSheet(sheetName, sheetIndex);
//                    sheetNums.put(sheetName, sheetIndex);
//                    row = 0;
//                    writeColumnHeaders(sheet, ecList, 0, 0);
//                    row++;
//                    
//                    log.info("Excel max rows exceeded, moving to sheet "+sheetIndex);
//                    //log.error("Excel max rows exceeded: "+ExcelConstants.EXCEL_MAX_DATA_ROW+", not going any further");
//                    //break;
//                }
//                //row = writeDataRow(sheet, ecList, dataReader, 0, -1);
//                writeDataRow(sheet, ecList, dataReader, 0, row++);
//                dataReader.next();
//            }
//        }
//        
//        if (extraDataWriter != null) extraDataWriter.writeExtraPostData(row, sheet);
//        
//        sizeColumns(sheet, ecList, 0, 0);
//        return sheetIndex;
//    }
    
    public int writeDataSheet(SXSSFWorkbook wb, String configKey, int sheetIndex, String sheetKey) {
        if (wb == null) {
            throw new IllegalArgumentException("WritableWorkbook wb argument cannot be null");
        }
        if (configKey == null) {
            throw new IllegalArgumentException("String configKey argument cannot be null");
        }
        if (log.isDebugEnabled()) {
            log.debug("configKey = " + configKey + ", sheetIndex = " + sheetIndex);
        }

        Sheet sheet = workbook.createSheet(configKey);
        // Store sheet index by config key (sheet name) since JExcel (WritableSheet) doesn't support
        // retrieval of sheet index
        sheetNums.put(configKey, sheetIndex);
        int sheetNameIndex = 1;

        List<ExcelColumn> ecList = report.getColumns();
        List dataList = report.getData(sheetKey);

        int row = 0;
        if (report.getStartRow() != null)
            row = report.getStartRow() - 1;
        if (extraDataWriter != null) row = extraDataWriter.writeExtraPreData(row, sheet);
        
        writeColumnHeaders(sheet, ecList, 0, row);
        row++;
        
        if (dataList != null) {
            dataReader.setDataList(dataList);
            // Iterate through data reader items and write rows to sheet
            while (dataReader.hasNext()) {
                // If we will exceed max Excel rows, create another sheet
//                if (row+1 == ExcelConstants.EXCEL_MAX_DATA_ROW) {
//                    sheetIndex++;
//                    sheetNameIndex++;
//                    //log.error("Excel max rows exceeded: "+ExcelConstants.EXCEL_MAX_DATA_ROW+", creating new sheet: "+(sheetIndex+1));
//                    String sheetName = new StringBuffer(configKey).append(" (").append(sheetNameIndex)
//                        .append(")").toString();
//                    sheet = wb.createSheet(sheetName);
//                    sheetNums.put(sheetName, sheetIndex);
//                    row = 0;
//                    writeColumnHeaders(sheet, ecList, 0, 0);
//                    row++;
//                    
//                    log.info("Excel max rows exceeded, moving to sheet "+sheetIndex);
//                    //log.error("Excel max rows exceeded: "+ExcelConstants.EXCEL_MAX_DATA_ROW+", not going any further");
//                    //break;
//                }
                //row = writeDataRow(sheet, ecList, dataReader, 0, -1);
//                if (row % 10 == 0){
//                    log.info(row + " th row passed.");
//                }
                writeDataRow(sheet, ecList, dataReader, 0, row++);
                dataReader.next();
            }
        }
        
        if (extraDataWriter != null) extraDataWriter.writeExtraPostData(row, sheet);
        
        sizeColumns(sheet, ecList, 0, 0);
        return sheetIndex;
    }

    /**
     * Write the column headers to the passed data sheet
     * @param sheet The sheet to write the column headers to
     * @param ecList The list of {@link ExcelColumn ExcelColumns} for the column headers
     * @param startCol The column to start at (zero-based)
     * @param startRow The row to start at (zero-based)
     */
//    public void writeColumnHeaders(WritableSheet sheet,
//                                    List<ExcelColumn> ecList,
//                                    int startCol,
//                                    int startRow)
//    {
//        if (sheet == null) {
//            throw new IllegalArgumentException("WritableSheet sheet argument cannot be null");
//        }
//        if (ecList == null) {
//            throw new IllegalArgumentException("List ecList argument cannot be null");
//        }
//        if (log.isDebugEnabled()) {
//            log.debug("sheet = " + sheet.getName() +
//                      ", startCol = " + startCol +
//                      ", startRow = " + startRow);
//        }
//        boolean freezeHeader = startRow == 0;
//
//        try {
//            int colCount = startCol;
//            for (ExcelColumn ec : ecList) {
//
//                if (ec.isHidden()) {
//                    CellView cellView = new CellView();
//                    cellView.setHidden(true);
//                    sheet.setColumnView(colCount,cellView);
//                }
//                // Create label with contents of the attribute name
//                Label headerLabel = null;
//                if (ec.isInternal()) {
//                    headerLabel = new Label(colCount, startRow, ec.getPropertyName());
//                }  else {
//                    StringBuilder displayName = new StringBuilder();
//                    displayName.append(ec.getPropertyDisplayName());
//                    headerLabel = new Label(colCount, startRow, displayName.toString());
//                    sheetCols.put(sheet.getName(), colCount);
//                    sheetRows.put(sheet.getName(), 0);
//                }
//                headerLabel.setCellFormat(ec.getHeaderRowFormat());
//                sheet.addCell(headerLabel);
//
//                colCount++;
//            }
//            // freeze the header row
//            if (freezeHeader) sheet.getSettings().setVerticalFreeze(1);
//        } catch (RowsExceededException e) {
//            log.error("writeColumnHeaders", e);
//            throw new RuntimeException("writeColumnHeaders", e);
//        } catch (WriteException e) {
//            log.error("writeColumnHeaders", e);
//            throw new RuntimeException("writeColumnHeaders", e);
//        }
//    }
    
    public void writeColumnHeaders(Sheet sheet,
                                    List<ExcelColumn> ecList,
                                    int startCol,
                                    int startRow)
    {
        if (sheet == null) {
            throw new IllegalArgumentException("WritableSheet sheet argument cannot be null");
        }
        if (ecList == null) {
            throw new IllegalArgumentException("List ecList argument cannot be null");
        }
        if (log.isDebugEnabled()) {
            log.debug("sheet = " + sheet.getSheetName() +
                      ", startCol = " + startCol +
                      ", startRow = " + startRow);
        }
        boolean freezeHeader = startRow == 0;
        
        Row row = sheet.createRow(startRow);

        try {
            int colCount = startCol;
            for (ExcelColumn ec : ecList) {
                if (ec.getColNumber() == -1){
                    continue;
                }
                sheet.setColumnHidden(colCount, ec.isHidden());
                // Create label with contents of the attribute name
                Cell cell = row.createCell(ec.getColNumber());
                if (ec.isInternal()) {
                    cell.setCellValue(ec.getPropertyDisplayName());
                }  else {
                    StringBuilder displayName = new StringBuilder();
                    displayName.append(ec.getPropertyDisplayName());
                    cell.setCellValue(displayName.toString());
                    sheetCols.put(sheet.getSheetName(), colCount);
                    sheetRows.put(sheet.getSheetName(), 0);
                }
                cell.setCellStyle(ec.getHeaderRowFormat());

                colCount++;
            }
            // freeze the header row
            if (freezeHeader)
                sheet.createFreezePane(0, 1, 0, 1);
        } catch (Exception e) {
            log.error("writeColumnHeaders", e);
            throw new RuntimeException("writeColumnHeaders", e);
        }
    }


//    public void sizeColumns(WritableSheet sheet,
//                            List<ExcelColumn> ecList,
//                            int startCol,
//                            int startRow)
//    {
//        if (sheet == null) {
//            throw new IllegalArgumentException("WritableSheet sheet argument cannot be null");
//        }
//        if (ecList == null) {
//            throw new IllegalArgumentException("List ecList argument cannot be null");
//        }
//
//        int colCount = startCol;
//        for (ExcelColumn ec : ecList) {
//
//            if (ec.getWidth() != null){
//                sheet.setColumnView(colCount, ec.getWidth());
//            } else if (!ec.isHidden()) {
//                if (maxDataLengths.containsKey(sheet.getName()+colCount)){
//                    sheet.setColumnView(colCount, maxDataLengths.get(sheet.getName()+colCount));
//                }
//            }
//            colCount++;
//        }
//    }
    
    public void sizeColumns(Sheet sheet,
                            List<ExcelColumn> ecList,
                            int startCol,
                            int startRow)
    {
        if (sheet == null) {
            throw new IllegalArgumentException("WritableSheet sheet argument cannot be null");
        }
        if (ecList == null) {
            throw new IllegalArgumentException("List ecList argument cannot be null");
        }

        int colCount = startCol;
        for (ExcelColumn ec : ecList) {

            if (ec.getWidth() != null){
                sheet.setColumnWidth(colCount, ec.getWidth() * 256);
            } else if (!ec.isHidden()) {
                if (maxDataLengths.containsKey(sheet.getSheetName()+colCount)){
                    sheet.setColumnWidth(colCount, maxDataLengths.get(sheet.getSheetName()+colCount));
                }
            }
            colCount++;
        }
    }

    
    /**
     * Write a data row to the passed sheet
     * @param sheet The sheet to write the data row to
     * @param ecList The list of {@link ExcelColumn ExcelColumns} for the data row
     * @param reader The data reader to retrieve property values from
     * @param startCol The column number to start at (zero-based)
     * @param atRow The row number to write to (zero-based).  Pass -1 to write to next data row
     *                          available.  Call repeatedly with -1 to write successive rows.
     * @return int The row number that was written to
     */
//    public int writeDataRow(WritableSheet sheet,
//                             List<ExcelColumn> ecList,
//                             ExcelDataReader reader,
//                             int startCol,
//                             int atRow)
//    {
//        if (sheet == null) {
//            throw new IllegalArgumentException("WritableSheet sheet argument cannot be null");
//        }
//        if (ecList == null) {
//            throw new IllegalArgumentException("List ecList argument cannot be null");
//        }
//        if (reader == null) {
//            throw new IllegalArgumentException("ExcelDataReader reader argument cannot be null");
//        }
//
//        int currRow = -1;
//        try {
//            if (atRow == 0) {
//                throw new IllegalArgumentException(
//                        "Argument 'atRow' cannot be 0, which is reserved for header row.");
//            } else if (atRow < 0) {
//                if (sheetRows.containsKey(sheet.getName())) {
//                    currRow = sheetRows.get(sheet.getName()) + 1;
//                } else {
//                    sheetRows.put(sheet.getName(), 1);
//                    currRow = 1;
//                }
//            } else {
//                currRow = atRow;
//            }
//            int currCol = startCol;
//            String key = null;
//            COLUMN_LOOP: for (ExcelColumn ec : ecList) {
//                String currColName = getExcelColumnName(currCol);
//
//                String propertyName = null, propertyValue = null;
//                try {
//                    propertyName = ec.getPropertyName();
//                    propertyValue = reader.getPropertyValue(ec, propertyName);
//                } catch (Exception e) {
//                    log.error("writeDataRow: currRow="+currRow+",currCol="+currCol, e);
//                    throw new RuntimeException("writeDataRow: currRow="+currRow+",currCol="+currCol, e);
//                }
//
//                List<WritableCellFormat> rowFormats = ec.getDataRowFormats();
//
//                // use modulus of formatIndex to alternate among the passed row formats/colors
//                WritableCellFormat currRowFormat
//                        = rowFormats.get((reader.getCurrentIndex()) % rowFormats.size());
//                
//                if (!ec.isHidden()) {
//                    // Store max data length in each column.  This will later be written to the
//                    // hidden Autofit sheet and Excel VBA code will use this to size each column.
//                    key = sheet.getName()+currCol;
//                    int currLen = (propertyValue == null) ? 0 : propertyValue.length();
//                    if (currLen < 15) currLen = 15;
//                    if (maxDataLengths.keySet().contains(key)) {
//                        int savedLen = maxDataLengths.get(key);
//                        if (currLen > savedLen) {
//                            maxDataLengths.put(key, currLen);
//                        }
//                    } else {
//                        maxDataLengths.put(key, currLen);
//                    }
//                }
//
//                boolean addAsCell = true;
//                
//                if (propertyValue != null && propertyValue.startsWith("http://")){
//                    try {
//                        WritableHyperlink link = new WritableHyperlink(currCol, currRow, new URL(propertyValue));
//                        if (propertyValue.indexOf("ASIN/") > 0){
//                            link.setDescription(propertyValue.substring(propertyValue.indexOf("ASIN/")+5));
//                        }
//                        sheet.addHyperlink(link);
//                        addAsCell = false;
//                    } catch (Exception e){}
//
//                }
//                
//                if (addAsCell){
//                    // We're not using compression so just write the format and data value
//                    WritableCell cell = getTypedDataCell(currCol, currRow, ec.getAttrClass(), propertyValue);
//                    // TODO do we want this to be formatted?
//                    //cell.setCellFormat(currRowFormat);
//                    if (ec.isWordWrap()){
//                        cell.setCellFormat(nowrapCellFormat);
//                    }
//                    sheet.addCell(cell);
//                }
//                currCol++;
//            }
//            // Save the row number we wrote so we can look it up next time this method is called
//            sheetRows.put(sheet.getName(), currRow);
//        } catch (RowsExceededException e) {
//            log.error("writeDataRow: currRow=" + currRow, e);
//            throw new RuntimeException("writeDataRow: currRow=" + currRow, e);
//        } catch (WriteException e) {
//            log.error("writeDataRow: currRow=" + currRow, e);
//            throw new RuntimeException("writeDataRow: currRow=" + currRow, e);
//        }
//        return currRow;
//    }
    
    public int writeDataRow(Sheet sheet,
                             List<ExcelColumn> ecList,
                             ExcelDataReader reader,
                             int startCol,
                             int atRow)
    {
        if (sheet == null) {
            throw new IllegalArgumentException("WritableSheet sheet argument cannot be null");
        }
        if (ecList == null) {
            throw new IllegalArgumentException("List ecList argument cannot be null");
        }
        if (reader == null) {
            throw new IllegalArgumentException("ExcelDataReader reader argument cannot be null");
        }

        int currRow = -1;
        try {
            if (atRow == 0) {
                throw new IllegalArgumentException(
                        "Argument 'atRow' cannot be 0, which is reserved for header row.");
            } else if (atRow < 0) {
                if (sheetRows.containsKey(sheet.getSheetName())) {
                    currRow = sheetRows.get(sheet.getSheetName()) + 1;
                } else {
                    sheetRows.put(sheet.getSheetName(), 1);
                    currRow = 1;
                }
            } else {
                currRow = atRow;
            }
            
            Row row = sheet.createRow(currRow);
            
            int currCol = startCol;
            String key = null;
            COLUMN_LOOP: for (ExcelColumn ec : ecList) {
                String currColName = getExcelColumnName(currCol);
                if (ec.getColNumber() == -1){
                    continue;
                }
                currCol = ec.getColNumber();
                String propertyName = null, propertyValue = null;
                try {
                    propertyName = ec.getPropertyName();
                    propertyValue = reader.getPropertyValue(ec, propertyName);
                } catch (Exception e) {
                    log.error("writeDataRow: currRow="+currRow+",currCol="+currCol, e);
                    throw new RuntimeException("writeDataRow: currRow="+currRow+",currCol="+currCol, e);
                }

//                List<CellStyle> rowFormats = ec.getDataRowFormats();

                // use modulus of formatIndex to alternate among the passed row formats/colors
//                CellStyle currRowFormat
//                        = rowFormats.get((reader.getCurrentIndex()) % rowFormats.size());
                
                if (!ec.isHidden()) {
                    // Store max data length in each column.  This will later be written to the
                    // hidden Autofit sheet and Excel VBA code will use this to size each column.
                    key = sheet.getSheetName()+currCol;
                    int currLen = (propertyValue == null) ? 0 : propertyValue.length();
                    if (currLen < 15) currLen = 15;
                    if (maxDataLengths.keySet().contains(key)) {
                        int savedLen = maxDataLengths.get(key);
                        if (currLen > savedLen) {
                            maxDataLengths.put(key, currLen);
                        }
                    } else {
                        maxDataLengths.put(key, currLen);
                    }
                }

                boolean addAsCell = true;
                
                if (propertyValue != null && propertyValue.startsWith("http://")){
                    try {
                        Cell cell = row.createCell(currCol);
                        CellStyle hlink_style = workbook.createCellStyle();
                        Font hlink_font = workbook.createFont();
                        hlink_font.setUnderline(Font.U_SINGLE);
                        hlink_font.setColor(IndexedColors.BLUE.index);
                        hlink_style.setFont(hlink_font);
                        cell.setCellStyle(hlink_style);
                        String value = "";
                        if (propertyValue.indexOf("ASIN/") > 0){
                            value = propertyValue.substring(propertyValue.indexOf("ASIN/") + 5);
                        } else {
                            value = propertyValue.substring(7);
                        }
                        cell.setCellType(Cell.CELL_TYPE_FORMULA);
                        cell.setCellFormula("hyperlink(\"" + propertyValue + "\", \"" + value + "\")");
                        //cell.setHyperlink(link);
                        addAsCell = false;
                    } catch (Exception e){}

                }
                
                if (addAsCell){
                    // We're not using compression so just write the format and data value
                    Cell cell = getTypedDataCell(currCol, row, ec.getAttrClass(), propertyValue);
                    // TODO do we want this to be formatted?
                    //cell.setCellFormat(currRowFormat);
                    if (ec.isWordWrap()){
                        cell.setCellStyle(nowrapCellFormat);
                    }
                }
                currCol++;
            }
            // Save the row number we wrote so we can look it up next time this method is called
        } catch (Exception e) {
            log.error("writeDataRow: currRow=" + currRow, e);
            throw new RuntimeException("writeDataRow: currRow=" + currRow, e);
        }
        return currRow;
    }

    /**
     * Create the appropriate WritableCell for the attribute class.
     * @param column The column number to create the WritableCell at (zero-based)
     * @param row The row number to create the WritableCell at (zero-based)
     * @param attrClass The attribute class.  May be null.
     * @param propertyValue The property value.  May be null.
     */
//    private static WritableCell getTypedDataCell(int column,
//                                                 int row,
//                                                 Class attrClass,
//                                                 String propertyValue)
//    {
//        if ( propertyValue == null || StringUtils.isEmpty(propertyValue)) {
//            return new Blank( column, row );
//        } else if ( attrClass == null ) {
//            // attrClass could be null for "artificial" non-persistent attributes, treat as string
//            return new Label( column, row, propertyValue );
//        } else if ( java.lang.Integer.class.isAssignableFrom(attrClass)) {
//            return new Number( column, row, Double.parseDouble(propertyValue), integerCellFormat );
//        } else if ( java.lang.Number.class.isAssignableFrom(attrClass)) {
//            return new Number( column, row, Double.parseDouble(propertyValue), floatCellFormat );
//        } else if ( Date.class.isAssignableFrom(attrClass)) {
//            try {
//                return new DateTime( column, row, dateParseFormat.parse(propertyValue), dateCellFormat );
//            } catch (ParseException e) {
//                log.error("getTypedDataCell", e);
//                throw new RuntimeException("getTypedDataCell", e);
//            }
//        }
//        else {
//            // Either this is a type we want formatted as a label/string, or an unknown type,
//            // so we punt and format as a label/string.
//            return new Label( column, row, propertyValue );
//        }
//    }
    
    private static Cell getTypedDataCell(int column,
                                                 Row row,
                                                 Class attrClass,
                                                 String propertyValue)
    {       
        Cell cell = row.createCell(column);
        if ( propertyValue == null || StringUtils.isEmpty(propertyValue)) {
            return cell;
        } else if ( attrClass == null ) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellStyle(textCellFormat);
            return cell;
        } else if ( java.lang.Integer.class.isAssignableFrom(attrClass)) {
            cell.setCellValue(Double.parseDouble(propertyValue));
            cell.setCellStyle(integerCellFormat);
            return cell;
        } else if ( java.lang.Number.class.isAssignableFrom(attrClass)) {
            cell.setCellValue(Double.parseDouble(propertyValue));
            cell.setCellStyle(floatCellFormat);
            return cell;
        } else if ( Date.class.isAssignableFrom(attrClass)) {
            try {
                cell.setCellValue(dateParseFormat.parse(propertyValue));
                cell.setCellStyle(dateCellFormat);
                return cell;
            } catch (ParseException e) {
                log.error("getTypedDataCell", e);
                throw new RuntimeException("getTypedDataCell", e);
            }
        }
        else {
            // Either this is a type we want formatted as a label/string, or an unknown type,
            // so we punt and format as a label/string.
            cell.setCellValue(propertyValue);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellStyle(textCellFormat);
            return cell;
        }
    }
    

    /**
     * Converts from a number to the Excel column letters/name.  This method assumes zero-based
     * column indexing.  For example: 0 = A, 1 = B, 26 = AA, 27 = BB, and so on.
     * @param columnNumber the column number to be converted
     * @return the Excel column letter(s)
     */
    public static String getExcelColumnName(int columnNumber) {
        String ret = null;
        if (columnNumber <= 25) {
            ret = ExcelConstants.EXCEL_COL_NAMES[columnNumber];
        } else {
            int letter1 = (columnNumber / 26) - 1;
            int letter2 = columnNumber % 26;
            ret = ExcelConstants.EXCEL_COL_NAMES[letter1]
                  + ExcelConstants.EXCEL_COL_NAMES[letter2];
        }
        return ret;
    }

    public void configureResponse(HttpServletResponse response, String fileName) {
        response.setContentType(ExcelConstants.CONTENT_TYPE_EXCEL);
        response.setHeader(ExcelConstants.CONTENT_DISPOSITION_HEADER_NAME,
                           ExcelConstants.CONTENT_DISPOSITION_PFX +
                           fileName +
                           ExcelConstants.CONTENT_DISPOSITION_EXCEL_SFX);
    }
    
    public ExcelReportExporter setExtraDataWriter(ExcelExtraDataWriter extraDataWriter){
        this.extraDataWriter = extraDataWriter;
        return this;
    }

    public ExcelReport getReport() {
        return report;
    }

    public void setReport(ExcelReport report) {
        this.report = report;
    }

}                                    // ExcelReportExporter