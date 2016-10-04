package com.bc.excel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import jxl.format.Alignment;
//import jxl.format.Border;
//import jxl.format.BorderLineStyle;
//import jxl.format.Colour;
//import jxl.format.VerticalAlignment;
//import jxl.write.WritableCellFormat;
//import jxl.write.WritableFont;
//import jxl.write.WriteException;
//import jxl.format.UnderlineStyle;

import org.apache.log4j.Logger;

import com.bc.dao.DaoResults;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import org.apache.poi.hssf.util.HSSFColor;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;


public class ExcelReport
{
    private static final Logger log = Logger.getLogger(ExcelReport.class);

    private List<ExcelColumn> excelColumns = null;
    private DaoResults daoResults = null;
    private HashMap<String, List> resultsMap = null;
    //private List<WritableCellFormat> dataFormats;
    private List<CellStyle> dataFormats;
    private CellStyle headerFormat;
    //private WritableCellFormat headerFormat;
    
    private SXSSFWorkbook workbook;
    
    private Integer startRow;
    /**
     *  Default ctor.
     */

//    public ExcelReport (DaoResults daoResults, List<ColumnData> columnDatas, List<ColumnModel> columnModels, List<String> columnsToExport) {
//        this.daoResults = daoResults;
//        this.headerFormat = getHeaderRowFormat();
//        this.dataFormats = getDataRowFormats();
//        buildExcelColumns(columnDatas, columnModels, columnsToExport);
//    }
    
    public ExcelReport (DaoResults daoResults, List<ColumnData> columnDatas, List<ColumnModel> columnModels, List<String> columnsToExport, List<String> columnNames) {
        this.workbook = new SXSSFWorkbook(1000);
        this.daoResults = daoResults;
        this.headerFormat = getHeaderRowFormat();
        this.dataFormats = getDataRowFormats();
        buildExcelColumns(columnDatas, columnModels, columnsToExport, columnNames);
    }
    
//    public ExcelReport (HashMap<String, List> resultsMap, List<ColumnData> columnDatas, List<ColumnModel> columnModels) {
//        this.resultsMap = resultsMap;
//        this.headerFormat = getHeaderRowFormat();
//        this.dataFormats = getDataRowFormats();
//        buildExcelColumns(columnDatas, columnModels, null);
//    }
    
    public ExcelReport (HashMap<String, List> resultsMap, List<ColumnData> columnDatas, List<ColumnModel> columnModels) {
        this.workbook = new SXSSFWorkbook(1000);
        this.resultsMap = resultsMap;
        this.headerFormat = getHeaderRowFormat();
        this.dataFormats = getDataRowFormats();
        buildExcelColumns(columnDatas, columnModels, null, null);
    }

    public List getData(String sheetKey) {
        if (daoResults != null) return daoResults.getData();
        //log.info("returning data for sheetKey: "+sheetKey);
        return resultsMap.get(sheetKey);
    }

    private void buildExcelColumns(List<ColumnData> columnDatas, List<ColumnModel> columnModels, List<String> columnsToExport, List<String> columnNames) {
        excelColumns = new ArrayList<ExcelColumn>();
        // column models may not fit the column datas, order may be off or there may be additional info in the column data that is never
        // shown in the model, so we have to make a map out of it
        Map<String, ColumnModel> cmMap = new HashMap<String, ColumnModel>(columnModels.size());
        int i = -1;
        for (ColumnModel cm : columnModels){
            i++;
            if (columnsToExport == null || (columnsToExport != null && columnsToExport.contains(cm.getDataIndex())))
                if (columnNames != null){
                    if (columnNames.get(i).equals("")){
                        continue;
                    }
                }
                cmMap.put(cm.getDataIndex(), cm);
        }
        int colNo = 0;
        int idx = 0;
        for (ColumnData cd : columnDatas) {
            if (cmMap.containsKey(cd.getXmlEntityName())){
                ColumnModel cm = cmMap.get(cd.getXmlEntityName());
                if (columnNames != null){
                    i = columnsToExport.indexOf(cm.getDataIndex());
                    String colName = columnNames.get(i);
                    colNo = 0;
                    if (colName.length() == 2){
                        colNo = 26;
                        colName = colName.substring(1);
                        colNo += ((int)colName.charAt(0) - (int)'A');
                    } else if (colName.length() == 1){
                        colNo += ((int)colName.charAt(0) - (int)'A');
                    } else{
                        colNo = -1;
                        //continue;
                    }
                } else{
                    colNo = idx;
                }
                String propName = cd.getName();
                String displayName = cm.getHeader();
                Boolean hidden = cm.getHidden();
                Integer width = cm.getExcelWidth();
                Boolean wordWrap = cm.getExcelWordWrap();
                String type = cd.getType();                
                Class colClass = String.class;
                if ("int".equalsIgnoreCase(type)) {
                    colClass = Integer.class;
                } else if ("float".equalsIgnoreCase(type)) {
                    colClass = Number.class;
                } else if ("date".equalsIgnoreCase(type)) {
                    colClass = Date.class;
                }
                boolean ignoreValInExport = cm.getIgnoreValInExport();
                if (log.isInfoEnabled()){
                    log.info("excel column info: name:" + propName + ", displayName:" + displayName + ", hidden:" +
                              hidden + ", excelWidth:" + width + ", type:" + type + ", colClass:" + colClass.getName() + 
                              ", ignoreValInExport: " + ignoreValInExport);
                }
//                log.info(cm.getDataIndex() + " : Col " + colName);
                excelColumns.add(new ExcelColumn(headerFormat,
                                                 dataFormats,
                                                 false,
                                                 false,
                                                 hidden == null ? false : hidden,
                                                 false,
                                                 colClass,
                                                 propName,
                                                 displayName,
                                                 ignoreValInExport,
                                                 width,
                                                 wordWrap,
                                                 colNo));
                idx++;
            }
        }
    }
    
//    public void setHeaderRowFormatPlain(){
//        headerFormat = null;
//        try {
//            WritableFont font = new WritableFont(WritableFont.ARIAL);
//            font.setBoldStyle(WritableFont.BOLD);
//            font.setUnderlineStyle(UnderlineStyle.SINGLE);
//            headerFormat = new WritableCellFormat(font);
//            headerFormat.setAlignment(Alignment.GENERAL);
//            headerFormat.setVerticalAlignment(VerticalAlignment.BOTTOM);
//            headerFormat.setWrap(false);
//        } catch (WriteException e) {
//            log.error("getHeaderRowFormat", e);
//            throw new RuntimeException("getHeaderRowFormat", e);
//        }
//    }
    
    public void setHeaderRowFormatPlain(){
        headerFormat = workbook.createCellStyle();
        try {
            Font font = workbook.createFont();
            font.setBold(true);
            font.setUnderline((byte)1);
            headerFormat.setFont(font);
            headerFormat.setWrapText(false);
        } catch (Exception e) {
            log.error("getHeaderRowFormat", e);
            throw new RuntimeException("getHeaderRowFormat", e);
        }
    }

//    private WritableCellFormat getHeaderRowFormat() {
//        WritableCellFormat headerFormat = null;
//        try {
//            WritableFont font = new WritableFont(WritableFont.ARIAL);
//            font.setBoldStyle(WritableFont.BOLD);
//            font.setColour(Colour.WHITE);
//            headerFormat = new WritableCellFormat(font);
//            headerFormat.setBackground(Colour.GREY_40_PERCENT);
//            headerFormat.setAlignment(Alignment.CENTRE);
//            headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
//            headerFormat.setBorder(Border.LEFT, BorderLineStyle.THIN, Colour.WHITE);
//            headerFormat.setWrap(false);
//        } catch (WriteException e) {
//            log.error("getHeaderRowFormat", e);
//            throw new RuntimeException("getHeaderRowFormat", e);
//        }
//        return headerFormat;
//    }
    
    private CellStyle getHeaderRowFormat() {
        CellStyle headerFormat = workbook.createCellStyle();
        try {
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(HSSFColor.WHITE.index);
            headerFormat.setFont(font);
            headerFormat.setFillBackgroundColor(HSSFColor.GREY_40_PERCENT.index);
            headerFormat.setFillPattern(CellStyle.FINE_DOTS);
            
            headerFormat.setAlignment(CellStyle.ALIGN_CENTER);
            headerFormat.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            headerFormat.setBorderLeft(CellStyle.BORDER_THIN);
            headerFormat.setLeftBorderColor(HSSFColor.WHITE.index);
            headerFormat.setWrapText(false);
        } catch (Exception e) {
            log.error("getHeaderRowFormat", e);
            throw new RuntimeException("getHeaderRowFormat", e);
        }
        return headerFormat;
    }

//    private List<WritableCellFormat> getDataRowFormats() {
//        List<WritableCellFormat> dataRowFormats = null;
//        try {
//            WritableCellFormat oddFormat = null, evenFormat = null;
//            evenFormat = new WritableCellFormat();
//            evenFormat.setBackground(Colour.GREY_25_PERCENT);
//            evenFormat.setBorder(Border.LEFT, BorderLineStyle.THIN, Colour.GRAY_50);
//            evenFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN, Colour.GRAY_50);
//            evenFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
//            //evenFormat.setWrap(true);
//            oddFormat = new WritableCellFormat();
//            oddFormat.setBackground(Colour.WHITE);
//            oddFormat.setBorder(Border.LEFT, BorderLineStyle.THIN, Colour.GRAY_50);
//            oddFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN, Colour.GRAY_50);
//            oddFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
//            //oddFormat.setWrap(true);
//            dataRowFormats = Arrays.asList(new WritableCellFormat[] { evenFormat, oddFormat });
//        } catch (WriteException e) {
//            log.error("getDataRowFormats", e);
//            throw new RuntimeException("getDataRowFormats", e);
//        }
//        return dataRowFormats;
//    }
    
    private List<CellStyle> getDataRowFormats() {
        List<CellStyle> dataRowFormats = null;
        try {
            CellStyle oddFormat = null, evenFormat = null;
            evenFormat = workbook.createCellStyle();
            evenFormat.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
            evenFormat.setBorderLeft(CellStyle.BORDER_THIN);
            evenFormat.setLeftBorderColor(HSSFColor.GREY_50_PERCENT.index);
            evenFormat.setBorderRight(CellStyle.BORDER_THIN);
            evenFormat.setRightBorderColor(HSSFColor.GREY_50_PERCENT.index);
            evenFormat.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            //evenFormat.setWrap(true);
            oddFormat = workbook.createCellStyle();
            oddFormat.setFillBackgroundColor(HSSFColor.WHITE.index);
            oddFormat.setBorderLeft(CellStyle.BORDER_THIN);
            oddFormat.setLeftBorderColor(HSSFColor.GREY_50_PERCENT.index);
            oddFormat.setBorderRight(CellStyle.BORDER_THIN);
            oddFormat.setRightBorderColor(HSSFColor.GREY_50_PERCENT.index);
            oddFormat.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            //oddFormat.setWrap(true);
            dataRowFormats = Arrays.asList(new CellStyle[] { evenFormat, oddFormat });
        } catch (Exception e) {
            log.error("getDataRowFormats", e);
            throw new RuntimeException("getDataRowFormats", e);
        }
        return dataRowFormats;
    }

    List<ExcelColumn> getColumns() {
        return excelColumns;
    }

    public HashMap<String, List> getResultsMap() {
        return resultsMap;
    }

    public void setResultsMap(HashMap<String, List> resultsMap) {
        this.resultsMap = resultsMap;
    }
    
    public SXSSFWorkbook getWorkbook(){
        return workbook;
    }
    
    public void setStartRow(Integer startRow){
        this.startRow = startRow;
    }
    
    public Integer getStartRow(){
        return startRow;
    }

}                                    // ExcelReport