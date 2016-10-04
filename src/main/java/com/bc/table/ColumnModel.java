package com.bc.table;

import java.util.HashMap;
import java.util.Map;

public class ColumnModel {

    private String header; // required
    private Integer width; // required
    private String dataIndex; // required
    
    private Integer excelWidth;
    private Boolean excelWordWrap = false;
    private String customType; // not required
    private Map<String,String> customTypeConfig;
    private String cssId; // not required
    private String css; // not required
    private String editor; // not required (only valid for editable tables)
    private String renderer; // not required
    private String summaryRenderer; // not required
    private Boolean hidden; // not required - hides the column
    private Boolean sortable = true; // not required
    private Boolean expanderColumn = false; // not required, if it is true then this column is used in the RowExpander plugin
    private String tooltip; // not required, will show as a quick tip when the mouse hovers over the column header
    private Boolean ignoreValInExport; // not required, will prevent RuntimeExceptions on Excel Export if object doesn't have getter for the column
    
    public ColumnModel(String dataIndex, String header){
        this(dataIndex, header, (Integer)null, (String)null, (String)null, (String)null);
    }

    public ColumnModel(String dataIndex, String header, Integer width){
        this(dataIndex, header, width, (String)null, (String)null, (String)null);
    }
    
    public ColumnModel(String dataIndex, String header, Integer width, String editor){
        this(dataIndex, header, width, editor, (String)null, (String)null, (String)null, false, true);
    }
    
    public ColumnModel(String dataIndex, String header, Integer width, String editor, String renderer){
        this(dataIndex, header, width, editor, renderer, (String)null, (String)null, false, true);
    }
    
    public ColumnModel(String dataIndex, String header, Integer width, Boolean hidden){
        this(dataIndex, header, width, hidden, true);
    }
    
    public ColumnModel(String dataIndex, String header, Integer width, String customType, Map<String,String> customTypeConfig){
        this(dataIndex, header, width, customType, customTypeConfig, (String)null, (String)null, (String)null, (String)null, false, false, false);
    }
    
    public ColumnModel(String dataIndex, String header, Integer width, Boolean hidden, Boolean sortable){
        this(dataIndex, header, width, (String)null, (String)null, (String)null, (String)null, hidden, sortable);
    }
    
    public ColumnModel(String dataIndex, String header, Integer width, String renderer, String cssId, String css){
        this(dataIndex, header, width, null, renderer, cssId, css, false, true);
    }
    
    public ColumnModel(String dataIndex, String header, Integer width, String editor, String renderer, String cssId, String css, Boolean hidden, Boolean sortable){
        this(dataIndex,header,width,null,null,editor,renderer,cssId,css,hidden,sortable, false);
    }
    
    public ColumnModel(String dataIndex, String header, Integer width, String customType, Map<String,String>customTypeConfig, String editor, String renderer, String cssId, String css, Boolean hidden, Boolean sortable, Boolean ignoreValInExport){
        this.dataIndex = dataIndex;
        this.header = header;
        this.width = width;
        this.excelWidth = width / 5;
        this.customType = customType;
        this.editor = editor;
        this.renderer = renderer;
        this.cssId = cssId;
        this.css = css;
        this.hidden = hidden;
        this.sortable = sortable;
        this.ignoreValInExport = ignoreValInExport;
        setCustomTypeConfig(customTypeConfig);
    }
    
    public Boolean getHasCssId(){
        return cssId != null;
    }
    public Boolean getHasCss(){
        return css != null;
    }
    public Boolean getHasEditor(){
        return editor != null;
    }
    public Boolean getHasRenderer(){
        return renderer != null;
    }
    public Boolean getHasDataIndex(){
        return dataIndex != null;
    }
    public Boolean getHasSummaryRenderer(){
        return summaryRenderer != null;
    }
    public Boolean getHasTooltip(){
        return tooltip != null;
    }

    public String getDataIndex() {
        return dataIndex;
    }

    public ColumnModel setDataIndex(String dataIndex) {
        this.dataIndex = dataIndex;
        return this;
    }

    public String getHeader() {
        return header;
    }

    public ColumnModel setHeader(String header) {
        this.header = header;
        return this;        
    }

    public Integer getWidth() {
        return width;
    }

    public ColumnModel setWidth(Integer width) {
        this.width = width;
        this.excelWidth = width / 5;
        return this;
    }

    public String getCssId() {
        return cssId;
    }

    public ColumnModel setCssId(String cssId) {
        this.cssId = cssId;
        return this;
    }

    public String getCss() {
        return css;
    }

    public ColumnModel setCss(String css) {
        this.css = css;
        return this;
    }
    
    public String getEditor() {
        return editor;
    }

    public ColumnModel setEditor(String editor) {
        this.editor = editor;
        return this;
    }

    public String getRenderer() {
        return renderer;
    }

    public ColumnModel setRenderer(String renderer) {
        this.renderer = renderer;
        return this;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public ColumnModel setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public Boolean getSortable() {
        return sortable;
    }

    public ColumnModel setSortable(Boolean sortable) {
        this.sortable = sortable;
        return this;
    }

    public String getSummaryRenderer() {
        return summaryRenderer;
    }

    public ColumnModel setSummaryRenderer(String summaryRenderer) {
        this.summaryRenderer = summaryRenderer;
        return this;
    }
    
    public String getCustomType() {
        return customType;
    }

    public ColumnModel setCustomType(String customType) {
        this.customType = customType;
        return this;
    }

    public Map<String, String> getCustomTypeConfig() {
        return customTypeConfig;
    }

    public ColumnModel setCustomTypeConfig(Map<String, String> customTypeConfig) {
        this.customTypeConfig = customTypeConfig == null ? null :
            new HashMap<String,String>(customTypeConfig);
        return this;
    }

    public Boolean getExpanderColumn() {
        return expanderColumn;
    }

    public ColumnModel setExpanderColumn(Boolean expanderColumn) {
        this.expanderColumn = expanderColumn;
        return this;
    }

    public String getTooltip() {
        return tooltip;
    }

    public ColumnModel setTooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

	public Boolean getIgnoreValInExport() {
    	return ignoreValInExport;
    }

	public void setIgnoreValInExport(Boolean ignoreValInExport) {
    	this.ignoreValInExport = ignoreValInExport;
    }

    public Integer getExcelWidth() {
        return excelWidth;
    }

    public ColumnModel setExcelWidth(Integer excelWidth) {
        this.excelWidth = excelWidth;
        return this;
    }

    public Boolean getExcelWordWrap() {
        return excelWordWrap;
    }

    public ColumnModel setExcelWordWrap(Boolean excelWordWrap) {
        this.excelWordWrap = excelWordWrap;
        return this;
    }

    
}
