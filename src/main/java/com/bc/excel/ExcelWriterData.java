package com.bc.excel;

import java.util.List;

public class ExcelWriterData {

    private List<Object> data;
    private List<String> headers;
    private List<String> propertyNames;
    private String sheetName;
    private List<List<String>> extraData;
    
    public ExcelWriterData(){}

    public ExcelWriterData(String sheetName, List<Object> data, List<String> headers, List<String> propertyNames){
        this.sheetName = sheetName;
        this.data = data;
        this.headers = headers;
        this.propertyNames = propertyNames;
    }

    public List<Object> getData() {
        return data;
    }
    public void setData(List<Object> data) {
        this.data = data;
    }
    public List<String> getHeaders() {
        return headers;
    }
    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }
    public List<String> getPropertyNames() {
        return propertyNames;
    }
    public void setPropertyNames(List<String> propertyNames) {
        this.propertyNames = propertyNames;
    }
    public String getSheetName() {
        return sheetName;
    }
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    public List<List<String>> getExtraData() {
        return extraData;
    }
    public void setExtraData(List<List<String>> extraData) {
        this.extraData = extraData;
    }
    
}
