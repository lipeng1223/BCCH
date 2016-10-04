package com.bc.table;

public class ToolbarButton {
    
    private Boolean separator = false; // text or separator - if separator then all else is ignored
    private String text; // required
    private String handler; // function for the click event; defined by table.tag if ToolbarMenu
    private String scope = "this"; // scope for the click event handler
    private String icon; // This is just an icon button - no text
    private String iconCls; // Used when text is present
    private String tooltip;
    private Boolean disabled = false;
    private Boolean linkButton = false; // uses an Ext.LinkButton instead of a normal button
    private String linkHref; // not used if ToolbarMenu
    private Boolean singleRowAction;
    private Boolean rowAction;
    private Boolean right = false; // once you go right there is no going back, only use it on the first button to align right
    private Filter filter;
    private String filterText;
    private String menuFilterText;
    
    public ToolbarButton(){}
    
    public ToolbarButton(String text, String iconCls, String tooltip){
        this.text = text;
        this.iconCls = iconCls;
        this.tooltip = tooltip;
    }
    
    public ToolbarButton(String text, String handler, String iconCls, String tooltip){
        this.text = text;
        this.handler = handler;
        this.iconCls = iconCls;
        this.tooltip = tooltip;
    }

    public ToolbarButton(String text, String handler, String iconCls, String tooltip, Boolean right){
        this.text = text;
        this.handler = handler;
        this.iconCls = iconCls;
        this.tooltip = tooltip;
        this.right = right;
    }
    
    public ToolbarButton(String text, String iconCls, String tooltip, Boolean right, Boolean linkButton, String linkHref){
        this.text = text;
        this.linkButton = linkButton;
        this.linkHref = linkHref;
        this.iconCls = iconCls;
        this.tooltip = tooltip;
        this.right = right;
    }
    
    public Boolean getHasIcon(){
        return icon != null;
    }
    public String getIcon() {
        return icon;
    }
    public ToolbarButton setIcon(String icon) {
        this.icon = icon;
        return this;
    }
    public Boolean getHasIconCls(){
        return iconCls != null;
    }
    public String getIconCls() {
        return iconCls;
    }
    public ToolbarButton setIconCls(String iconCls) {
        this.iconCls = iconCls;
        return this;
    }
    public String getText() {
        return text;
    }
    public ToolbarButton setText(String text) {
        this.text = text;
        return this;
    }
    public Boolean getHasTooltip(){
        return tooltip != null;
    }
    public String getTooltip() {
        return tooltip;
    }
    public ToolbarButton setTooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }
    public String getHandler() {
        return handler;
    }
    public ToolbarButton setHandler(String handler) {
        this.handler = handler;
        return this;
    }
    public String getScope() {
        return scope;
    }
    public ToolbarButton setScope(String scope) {
        this.scope = scope;
        return this;
    }
    public Boolean getHasHandler(){
        return handler != null;
    }
    public Boolean getDisabled() {
        return disabled;
    }
    public ToolbarButton setDisabled(Boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public Boolean getSingleRowAction() {
        return singleRowAction;
    }

    public ToolbarButton setSingleRowAction(Boolean singleRowAction) {
        this.singleRowAction = singleRowAction;
        setRowAction(singleRowAction);
        return this;
    }

    public Boolean getRowAction() {
        return rowAction;
    }

    public ToolbarButton setRowAction(Boolean rowAction) {
        this.rowAction = rowAction;
        setDisabled(true);
        return this;
    }

    public Boolean getRight() {
        return right;
    }

    public ToolbarButton setRight(Boolean right) {
        this.right = right;
        return this;
    }

    public Boolean getLinkButton() {
        return linkButton;
    }

    public ToolbarButton setLinkButton(Boolean linkButton) {
        this.linkButton = linkButton;
        return this;
    }

    public String getLinkHref() {
        return linkHref;
    }

    public ToolbarButton setLinkHref(String linkHref) {
        this.linkHref = linkHref;
        return this;
    }
    
    public boolean isMenu() {
        return this instanceof ToolbarMenu;
    }

    public Filter getFilter() {
        return filter;
    }

    public ToolbarButton setFilter(Filter filter) {
        this.filter = filter;
        return this;
    }
    
    public Boolean isFilter(){
        return filter != null;
    }

    public String getMenuFilterText() {
        return menuFilterText;
    }

    public ToolbarButton setMenuFilterText(String menuFilterText) {
        this.menuFilterText = menuFilterText;
        return this;
    }

    public String getFilterText() {
        return filterText;
    }

    public ToolbarButton setFilterText(String filterText) {
        this.filterText = filterText;
        return this;
    }

    public Boolean getSeparator() {
        return separator;
    }

    public ToolbarButton setSeparator(Boolean separator) {
        this.separator = separator;
        return this;
    }
    
}
