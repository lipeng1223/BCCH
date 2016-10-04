package com.bc.excel;

import java.util.List;

import org.apache.log4j.Logger;

import jxl.write.WritableCellFormat;
import org.apache.poi.ss.usermodel.CellStyle;

public class ExcelColumn
{
    private static final Logger log = Logger.getLogger(ExcelColumn.class);

    //private WritableCellFormat headerRowFormat = null;
    private CellStyle headerRowFormat = null;
    //private List<WritableCellFormat> dataRowFormats = null;
    private List<CellStyle> dataRowFormats = null;
    private boolean isEditable = true;
    private boolean isHyperlink = false;
    private boolean isHidden = false;
    // isInternal means the column is added by an Excel exporter, not part of the report
    private boolean isInternal = false;
    private String propertyName = null;
    private String propertyDisplayName = null;
    private Integer width = null;
    private boolean wordWrap = false;

    // converterClass, convertedClass, and convertedProperty are used together.
    // First the AttributeType decorator decorates the value.  Then the converterClass converts the
    // to convertedClass object.  Then the convertedProperty is returned from the converted object.
    private Class attrClass = null;
    private Class converterClass = null;
    private Class convertedClass = null;
    private String convertedProperty = null;
    private Integer colNumber;

    private boolean ignoreValInExport;

//    public ExcelColumn(WritableCellFormat headerRowFormat,
//                       List<WritableCellFormat> dataRowFormats,
//                       boolean isEditable,
//                       boolean isHyperlink,
//                       boolean isHidden,
//                       boolean isInternal,
//                       Class attrClass,
//                       String propertyName,
//                       String displayName,
//                       boolean ignoreValInExport,
//                       Integer width,
//                       boolean wordWrap)
//    {
//        this.headerRowFormat = headerRowFormat;
//        this.dataRowFormats = dataRowFormats;
//        this.isEditable = isEditable;
//        this.isHyperlink = isHyperlink;
//        this.isHidden = isHidden;
//        this.isInternal = isInternal;
//        this.attrClass = attrClass;
//        this.propertyName = propertyName;
//        this.propertyDisplayName = displayName;
//        this.ignoreValInExport = ignoreValInExport;
//        this.width = width;
//        this.wordWrap = wordWrap;
//    }
    
    public ExcelColumn(CellStyle headerRowFormat,
                       List<CellStyle> dataRowFormats,
                       boolean isEditable,
                       boolean isHyperlink,
                       boolean isHidden,
                       boolean isInternal,
                       Class attrClass,
                       String propertyName,
                       String displayName,
                       boolean ignoreValInExport,
                       Integer width,
                       boolean wordWrap,
                       Integer colNumber)
    {
        this.headerRowFormat = headerRowFormat;
        this.dataRowFormats = dataRowFormats;
        this.isEditable = isEditable;
        this.isHyperlink = isHyperlink;
        this.isHidden = isHidden;
        this.isInternal = isInternal;
        this.attrClass = attrClass;
        this.propertyName = propertyName;
        this.propertyDisplayName = displayName;
        this.ignoreValInExport = ignoreValInExport;
        this.width = width;
        this.wordWrap = wordWrap;
        this.colNumber = colNumber;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyDisplayName() {
        return propertyDisplayName;
    }

    public void setPropertyDisplayName(String propertyDisplayName) {
        this.propertyDisplayName = propertyDisplayName;
    }

    public Class getAttrClass() {
        return attrClass;
    }

    public void setAttrClass(Class clazz) {
        this.attrClass = clazz;
    }

    public Class getConverterClass() {
        return converterClass;
    }

    public void setConverterClass(Class converterClass) {
        this.converterClass = converterClass;
    }

    public Class getConvertedClass() {
        return convertedClass;
    }

    public void setConvertedClass(Class convertedClass) {
        this.convertedClass = convertedClass;
    }

    public String getConvertedProperty() {
        return convertedProperty;
    }

    public void setConvertedProperty(String convertedProperty) {
        this.convertedProperty = convertedProperty;
    }

    public boolean isInternal() {
        return isInternal;
    }

    public void setInternal(boolean isInternal) {
        this.isInternal = isInternal;
    }

//    public WritableCellFormat getHeaderRowFormat() {
//        return headerRowFormat;
//    }
    
    public CellStyle getHeaderRowFormat() {
        return headerRowFormat;
    }
    
//    public void setHeaderRowFormat(WritableCellFormat headerRowFormat) {
//        this.headerRowFormat = headerRowFormat;
//    }
    public void setHeaderRowFormat(CellStyle headerRowFormat) {
        this.headerRowFormat = headerRowFormat;
    }
//    public List<WritableCellFormat> getDataRowFormats() {
//        return dataRowFormats;
//    }
    public List<CellStyle> getDataRowFormats() {
        return dataRowFormats;
    }
//    public void setDataRowFormats(List<WritableCellFormat> dataRowFormats) {
//        this.dataRowFormats = dataRowFormats;
//    }
    public void setDataRowFormats(List<CellStyle> dataRowFormats) {
        this.dataRowFormats = dataRowFormats;
    }
    public boolean isEditable() {
        return isEditable;
    }
    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }
    public boolean isHidden() {
        return isHidden;
    }
    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }
    public boolean isHyperlink() {
        return isHyperlink;
    }
    public void setHyperlink(boolean isHyperlink) {
        this.isHyperlink = isHyperlink;
    }

	public boolean isIgnoreValInExport() {
    	return ignoreValInExport;
    }

	public void setIgnoreValInExport(boolean ignoreValInExport) {
    	this.ignoreValInExport = ignoreValInExport;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public boolean isWordWrap() {
        return wordWrap;
    }

    public void setWordWrap(boolean wordWrap) {
        this.wordWrap = wordWrap;
    }
    
    public void setColNumber(Integer colNumber){
        this.colNumber = colNumber;
    }
    
    public Integer getColNumber(){
        return colNumber;
    }

}                                    // ExcelColumn