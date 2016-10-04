package com.bc.table;

public class ColumnData {

    private String name; // required
    private String xmlEntityName; // not required - NOTE: you need to use this if your name as dots in it, like patients.firstname
    
    private String mapping; // not required
    private String type; // not required
    private String dateFormat; // not required
    
    public ColumnData(String name){
        this(name, (String)null, (String)null, (String)null, (String)null);
    }
    
    public ColumnData(String name, String xmlEntityName){
        this(name, xmlEntityName, (String) null, (String)null, (String)null);
    }
    
    public ColumnData(String name, String xmlEntityName, String type){
        this(name, xmlEntityName, (String) null, type, (String) null); 
    }
    
    public ColumnData(String name, String xmlEntityName, String mapping, String type, String dateFormat){
        this.name = name;
        this.xmlEntityName = xmlEntityName;
        this.mapping = mapping;
        this.type = type;
        this.dateFormat = dateFormat;
    }
    
    public Boolean getHasMapping(){
        return mapping != null;
    }
    public Boolean getHasType(){
        return type != null;
    }
    public Boolean getHasDateFormat(){
        return dateFormat != null;
    }
    
    public String getName() {
        return name;
    }
    public ColumnData setName(String name) {
        this.name = name;
        return this;
    }
    public String getMapping() {
        return mapping;
    }
    public ColumnData setMapping(String mapping) {
        this.mapping = mapping;
        return this;
    }
    public String getType() {
        return type;
    }
    public Boolean getIsStringType(){
        return type == null || type.equals("string");
    }
    public ColumnData setType(String type) {
        this.type = type;
        return this;
    }
    public String getDateFormat() {
        return dateFormat;
    }
    public ColumnData setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public String getXmlEntityName() {
        if (xmlEntityName == null){
            return name;
        }
        return xmlEntityName;
    }

    public ColumnData setXmlEntityName(String xmlEntityName) {
        this.xmlEntityName = xmlEntityName;
        return this;
    }
    
}
