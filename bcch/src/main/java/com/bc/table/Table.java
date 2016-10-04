package com.bc.table;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Table {

    private List<ColumnData> columnDatas;
    private LinkedHashMap<String, ColumnModel> columnModels; // keyed by property name/path
    private List<Filter> filters;
    private List<Render> renders;
    private List<Event> gridEvents;
    private List<Event> gridSelectionEvents;
    private List<Event> gridStoreEvents;
    private Toolbar toolbar;
    private Boolean summary = false;
    private Boolean pageable = true;
    private Boolean editable = false;
    private Boolean multiselect = false;
    private Boolean stripeRows = true;
    private Boolean jsonReader = false;
    private Boolean exportable = false;
    private Boolean excelLandscape = false;
    private String id;
    private String defaultGroupCol;
    private String defaultSortCol;
    private String defaultSortDir; // ASC or DESC
    private String expanderTemplate;
    private Integer pageSize = 25;
    private String[] pageVariationDisplay = new String[]{"25", "50", "75", "100", "150", "200", "250", "300", "400", "500"};
    private Integer[] pageVariationValues = new Integer[]{25, 50, 75, 100, 150, 200, 250, 300, 400, 500};
    private HashMap<String, String> baseParams = new HashMap<String, String>();
    private List<Filter> additionalSearch;
    
    private Boolean marketable = false;
    

    private Boolean useRegion = false;
    private String region;
    private Boolean useAnchor = false;
    private String anchor;
    private Boolean useColumnWidth = false;
    private String columnWidth;
    
    private Boolean useTitle = false;
    private String title;
    
    // drag and drop
    private String ddGroup;
    private Boolean enableDragDrop = false;

    public static final String SORT_DIR_ASC = "ASC";
    public static final String SORT_DIR_DESC = "DESC";
    
    public String getColumnType(String colName){
        if (columnDatas != null && colName != null){
            for (ColumnData cd : columnDatas){
                if (cd.getName().equals(colName))
                    return cd.getType();
            }
        }
        if (additionalSearch != null && colName != null){
            for (Filter f : additionalSearch){
                if (f.getName().equals(colName))
                    return f.getType();
            }
        }
        return null;
    }

    public Boolean getHasToolbar(){
        return toolbar != null;
    }
    
    public Boolean getHasExpanderTemplate(){
        return expanderTemplate != null;
    }

    public List<Render> getRenders() {
        return renders;
    }
    public Table setRenders(List<Render> renders) {
        this.renders = renders;
        return this;
    }
    public List<Filter> getFilters() {
        return filters;
    }
    public Table setFilters(List<Filter> filters) {
        this.filters = filters;
        return this;
    }
    public Boolean hasFilter(String col){
        if (filters == null) return false;
        for (Filter f : filters){
            if (f.getName().equals(col)) return true;
        }
        return false;
    }
    public List<ColumnData> getColumnDatas() {
        return columnDatas;
    }
    public Table setColumnDatas(List<ColumnData> columnDatas) {
        this.columnDatas = columnDatas;
        return this;
    }
    public Collection<ColumnModel> getColumnModels() {
        return columnModels.values();
    }
    public ColumnModel getColumnModel(String dataIndex) {
        return columnModels.get(dataIndex);
    }
    public Table setColumnModels(List<ColumnModel> columnModels) {
        this.columnModels = new LinkedHashMap<String, ColumnModel>();
        for (ColumnModel cm : columnModels) {
            this.columnModels.put(cm.getDataIndex(), cm);
        }
        return this;
    }
    public String getId() {
        return id;
    }
    public Table setId(String id) {
        this.id = id;
        return this;
    }
    public Toolbar getToolbar() {
        return toolbar;
    }
    public Table setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        return this;
    }

    public Boolean getEditable() {
        return editable;
    }

    public Table setEditable(Boolean editable) {
        this.editable = editable;
        return this;
    }
    
    public Boolean getPageable() {
        return pageable;
    }

    public Table setPageable(Boolean pageable) {
        this.pageable = pageable;
        return this;
    }

    public Boolean getMultiselect() {
        return multiselect;
    }

    public Table setMultiselect(Boolean multiselect) {
        this.multiselect = multiselect;
        return this;
    }

    public Boolean getStripeRows() {
        return stripeRows;
    }

    public Table setStripeRows(Boolean stripeRows) {
        this.stripeRows = stripeRows;
        return this;
    }

    public Boolean getJsonReader() {
        return jsonReader;
    }

    public Table setJsonReader(Boolean jsonReader) {
        this.jsonReader = jsonReader;
        return this;
    }

    public List<Event> getGridEvents() {
        return gridEvents;
    }

    public Table setGridEvents(List<Event> gridEvents) {
        this.gridEvents = gridEvents;
        return this;
    }

    public List<Event> getGridSelectionEvents() {
        return gridSelectionEvents;
    }

    public Table setGridSelectionEvents(List<Event> gridSelectionEvents) {
        this.gridSelectionEvents = gridSelectionEvents;
        return this;
    }

    public List<Event> getGridStoreEvents() {
        return gridStoreEvents;
    }

    public Table setGridStoreEvents(List<Event> gridStoreEvents) {
        this.gridStoreEvents = gridStoreEvents;
        return this;
    }

    public String getDefaultGroupCol() {
        return defaultGroupCol;
    }

    public Table setDefaultGroupCol(String defaultGroupCol) {
        this.defaultGroupCol = defaultGroupCol;
        return this;
    }

    public String getDefaultSortCol() {
        return defaultSortCol;
    }

    public Table setDefaultSortCol(String defaultSortCol) {
        this.defaultSortCol = defaultSortCol;
        return this;
    }

    public String getDefaultSortDir() {
        return defaultSortDir;
    }

    public Table setDefaultSortDir(String defaultSortDir) {
        this.defaultSortDir = defaultSortDir;
        return this;
    }

    public Boolean getSummary() {
        return summary;
    }

    public Table setSummary(Boolean summary) {
        this.summary = summary;
        return this;
    }

    public String getExpanderTemplate() {
        return expanderTemplate;
    }

    public void setExpanderTemplate(String expanderTemplate) {
        this.expanderTemplate = expanderTemplate;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Table setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public String[] getPageVariationDisplay() {
        return pageVariationDisplay;
    }

    public Table setPageVariationDisplay(String... pageVariationDisplay) {
        this.pageVariationDisplay = pageVariationDisplay;
        return this;
    }

    public Integer[] getPageVariationValues() {
        return pageVariationValues;
    }

    public Table setPageVariationValues(Integer... pageVariationValues) {
        this.pageVariationValues = pageVariationValues;
        return this;
    }

    public Boolean getExportable() {
        return exportable;
    }

    public Table setExportable(Boolean exportable) {
        this.exportable = exportable;
        return this;
    }
    
    public Boolean getMarketable(){
        return this.marketable;
    }
    
    public void setMarketable(Boolean marketable){
        this.marketable = marketable;
    }

    public Boolean getEnableDragDrop() {
        return enableDragDrop;
    }

    public void setEnableDragDrop(Boolean enableDragDrop) {
        this.enableDragDrop = enableDragDrop;
    }

    public String getDdGroup() {
        return ddGroup;
    }

    public void setDdGroup(String ddGroup) {
        this.ddGroup = ddGroup;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Boolean getUseRegion() {
        return useRegion;
    }

    public void setUseRegion(Boolean useRegion) {
        this.useRegion = useRegion;
    }

    public Boolean getUseTitle() {
        return useTitle;
    }

    public void setUseTitle(Boolean useTitle) {
        this.useTitle = useTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getUseAnchor() {
        return useAnchor;
    }

    public void setUseAnchor(Boolean useAnchor) {
        this.useAnchor = useAnchor;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public Boolean getUseColumnWidth() {
        return useColumnWidth;
    }

    public void setUseColumnWidth(Boolean useColumnWidth) {
        this.useColumnWidth = useColumnWidth;
    }

    public String getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(String columnWidth) {
        this.columnWidth = columnWidth;
    }

    public HashMap<String, String> getBaseParams() {
        return baseParams;
    }

    public void setBaseParams(HashMap<String, String> baseParams) {
        this.baseParams = baseParams;
    }

    public Boolean getExcelLandscape() {
        return excelLandscape;
    }

    public void setExcelLandscape(Boolean excelLandscape) {
        this.excelLandscape = excelLandscape;
    }

    public List<Filter> getAdditionalSearch() {
        return additionalSearch;
    }

    public void setAdditionalSearch(List<Filter> additionalSearch) {
        this.additionalSearch = additionalSearch;
    }

}
