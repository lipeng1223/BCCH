<%@ tag language="java" %>
<%@ attribute name="tableName" required="true" rtexprvalue="true" %>
<%@ attribute name="tableTitle" required="false" rtexprvalue="true" %>
<%@ attribute name="tableIcon" required="false" rtexprvalue="true" %>
<%@ attribute name="width" required="false" rtexprvalue="true" %>
<%@ attribute name="height" required="false" rtexprvalue="true" %>
<%@ attribute name="collapsible" required="false" rtexprvalue="true" %>
<%@ attribute name="collapsed" required="false" rtexprvalue="true" %>
<%@ attribute name="sortable" required="true" rtexprvalue="true" %>
<%@ attribute name="proxyListener" required="false" rtexprvalue="true" %>
<%@ attribute name="dataAction" required="false" rtexprvalue="true" %>
<%@ attribute name="dataActionParams" required="false" rtexprvalue="true" %>
<%@ attribute name="dataActionProxyParam" required="false" rtexprvalue="true" %>
<%@ attribute name="dataActionProxyParamValue" required="false" rtexprvalue="true" %>
<%@ attribute name="table" required="true" rtexprvalue="true" type="com.bc.table.Table"%>
<%@ attribute name="resizeable" required="false" rtexprvalue="true" %>
<%@ attribute name="resizeableDirs" required="false" rtexprvalue="true" %>
<%@ attribute name="addToContainer" required="false" rtexprvalue="true" %>
<%@ attribute name="resizeOnWindow" required="false" rtexprvalue="true" %>
<%@ attribute name="includeGridClass" required="false" rtexprvalue="true" %>
<%@ attribute name="timeout" required="false" rtexprvalue="true" %>
<%@ attribute name="exportable" required="false" rtexprvalue="true" %>
<%@ attribute name="exportAction" required="false" rtexprvalue="true" %>
<%@ attribute name="exportJS" required="false" rtexprvalue="true" %>
<%@ attribute name="emptyMessage" required="false" rtexprvalue="true"%>
<%@ attribute name="filterTextDiv" required="false" rtexprvalue="true"%>
<%@ attribute name="stateful" required="false" rtexprvalue="true"%>
<%@ attribute name="statestartid" required="false" rtexprvalue="true"%>
<%@ attribute name="showAllButton" required="false" rtexprvalue="true"%>
<%@ attribute name="defaultFilter" required="false" rtexprvalue="true"%>
<%@ attribute name="defaultFilters" required="false" rtexprvalue="true"%>
<%@ attribute name="defaultFilterId" required="false" rtexprvalue="true"%>
<%@ attribute name="defaultFilterCol" required="false" rtexprvalue="true"%>
<%@ attribute name="customRowColors" required="false" rtexprvalue="true"%>
<%@ attribute name="customRowColorsFunction" required="false" rtexprvalue="true"%>
<%@ attribute name="rowDblClick" required="false" rtexprvalue="true"%>

<%@ taglib uri="/struts-tags" prefix="s" %>

<script language="JavaScript" type="text/javascript">

Ext.onReady(function(){

    /*
    Ext.ux.menu.RangeMenu.prototype.icons = {
          gt: '/images/greater_then.png', 
          lt: '/images/less_then.png',
          eq: '/images/equals.png'
    };
    */
    Ext.ux.grid.filter.StringFilter.prototype.icon = '/images/find.png';

    var doSelectionOnLoad = false;
    
    <s:if test="#attr.addToContainer == null">
    if (Ext.isIE6){
        new Ext.Panel({
            border: false,
            bodyBorder: false,
            id: '<s:property value="#attr.tableName"/>listGridPanel',
            renderTo: '<s:property value="#attr.tableName"/>-grid',
            layout: 'fit'
            <s:if test="#attr.height != null">,height:<s:property value="#attr.height"/></s:if>
            <s:else>,height:450</s:else>
            <s:if test="#attr.width != null">,width:<s:property value="#attr.width"/></s:if>
            <s:else>,width:800</s:else>
        });
    } else {
        new Ext.Panel({
            border: false,
            bodyBorder: false,
            id: '<s:property value="#attr.tableName"/>listGridPanel',
            renderTo: '<s:property value="#attr.tableName"/>-grid',
            layout: 'fit'
            <s:if test="#attr.height != null">,height:<s:property value="#attr.height"/></s:if>
            <s:if test="#attr.width != null">,width:<s:property value="#attr.width"/></s:if>
        });
    }
    </s:if>
    
    // retrieve data action and optional params -- added optional params to deal with ampersand encoding 
    var dataActionParams = '<s:property value="#attr.dataActionParams" escape="false"/>';
    var dataActionUrl = '<s:property value="#attr.dataAction"/>';
    // if we have a dataActionsParams add params to the url
    if((dataActionParams !== undefined) && (dataActionParams !== null) && (dataActionParams.length > 0)) {
        dataActionParams = dataActionParams.replace(/,/g,"&");//replace all commas with ampersand
        dataActionUrl = dataActionUrl + '?' + dataActionParams;
    }

    // see if we are going to use the expander plugin
    <s:if test="#attr.table.hasExpanderTemplate">
    var <s:property value="#attr.tableName"/>Expander = new Ext.grid.RowExpander({
        tpl : <s:property value="#attr.table.expanderTemplate"/>
    });
    Ext.grid.<s:property value="#attr.tableName"/>GridExpander = <s:property value="#attr.tableName"/>Expander;
    </s:if>
    
    // create the Data Record
    var <s:property value="#attr.tableName"/>Record = new Ext.data.Record.create([
      <s:iterator value="#attr.table.columnDatas" status="status" id="col"><%-- 
        --%>{name: '<s:property value="#col.xmlEntityName"/>'<%-- 
        --%><s:if test="#col.hasMapping">, mapping: '<s:property value="#col.mapping"/>'</s:if><%-- 
        --%><s:if test="#col.hasType">, type: '<s:property value="#col.type"/>'</s:if><%-- 
        --%><s:if test="#col.hasDateFormat">, dateFormat: '<s:property value="#col.dateFormat"/>'</s:if><%-- 
        --%>}<s:if test="! #status.last">,</s:if>
      </s:iterator>]);

    <s:if test="! #attr.dataAction">
    // Set the local data array
    var <s:property value="#attr.tableName"/>Data = <s:property value="getJsonResults(#attr.tableName)" escape="false"/>; 
    </s:if>
    
    // create the Data Store
    var groupBy = null;
    if (Ext.state.Manager.getProvider().get("<s:property value="#attr.tableName"/>GridGroupBy")){
        if ( Ext.state.Manager.getProvider().get("<s:property value="#attr.tableName"/>GridGroupBy") != 'undefined'){
            groupBy = Ext.state.Manager.getProvider().get("<s:property value="#attr.tableName"/>GridGroupBy");
            Ext.grid.<s:property value="#attr.tableName"/>GroupBy = groupBy;
        }
    }
    var <s:property value="#attr.tableName"/>HttpProxy = new Ext.data.HttpProxy({
        url: dataActionUrl
        <s:if test="#attr.timeout != null">, timeout: <s:property value="#attr.timeout"/></s:if>
    });
    <s:if test="#attr.proxyListener != null">
    <s:property value="#attr.tableName"/>HttpProxy.addListener(<s:property value="#attr.proxyListener"/>);
    </s:if>
    var <s:property value="#attr.tableName"/>Ds = new Ext.data.GroupingStore({
    //var <s:property value="#attr.tableName"/>Ds = new Ext.data.Store({
        id:'<s:property value="#attr.tableName"/>Ds',
        baseParams:{groupBy:groupBy<s:iterator value="#attr.table.baseParams" id="bp" status="status">,<s:property value="#pb.key"/>:'<s:property value="#bp.value"/>'</s:iterator><s:if test="#attr.dataActionProxyParam != null">, "<s:property value="#attr.dataActionProxyParam"/>":"<s:property value="#attr.dataActionProxyParamValue"/>"</s:if>},
        <s:if test="#attr.table.defaultGroupCol != null">
            //groupField: '<s:property value="#attr.table.defaultGroupCol"/>',
        </s:if>
        <s:if test="! #attr.table.pageable">
            sortInfo: {field:'<s:property value="#attr.table.defaultSortCol"/>'.replace('.', '_')<s:if test="#attr.table.defaultSortDir != null">, direction: '<s:property value="#attr.table.defaultSortDir"/>'</s:if>},
        </s:if><s:elseif test="#attr.table.defaultSortCol != null">
            sortInfo: {field:'<s:property value="#attr.table.defaultSortCol"/>'<s:if test="#attr.table.defaultSortDir != null">, direction: '<s:property value="#attr.table.defaultSortDir"/>'</s:if>},
        </s:elseif>
        <s:if test="! #attr.table.pageable">
            remoteGroup: false, 
            groupField: groupBy,
            proxy: <s:property value="#attr.tableName"/>HttpProxy,
            remoteSort: false,
        </s:if>
        <s:elseif test="! #attr.dataAction">
          proxy: new Ext.ux.data.PagingMemoryProxy(<s:property value="#attr.tableName"/>Data),
          remoteSort: false,
        </s:elseif><s:else>
          remoteGroup: true, 
          groupField: groupBy,
          proxy: <s:property value="#attr.tableName"/>HttpProxy,
          remoteSort: <s:property value="#attr.sortable"/>,
        </s:else>
        <s:if test="#attr.table.jsonReader || ! #attr.dataAction">
          reader: new Ext.data.JsonReader({ root: "rows",
              totalProperty: "total"
              <s:if test="#attr.table.id != null">,id: '<s:property value="#attr.table.id"/>'</s:if>
          }, <s:property value="#attr.tableName"/>Record)
        </s:if><s:else>
          reader: new Ext.data.XmlReader({ record: "row",
              totalRecords: "total"
              <s:if test="#attr.table.id != null">,id: '<s:property value="#attr.table.id"/>'</s:if>
          }, <s:property value="#attr.tableName"/>Record)
        </s:else>
    });

    
    // grid plugins (for custom types, filters, etc.)
    var <s:property value="#attr.tableName"/>GridPlugins = new Array();
    function addGridPlugin(type) {
        <s:property value="#attr.tableName"/>GridPlugins[<s:property value="#attr.tableName"/>GridPlugins.length] = type;
        return type; 
    }
    
    var <s:property value="#attr.tableName"/>Filters = new Ext.ux.grid.GridFilters({
        updateBuffer: 500, 
        <s:if test="! #attr.dataAction">local: true,</s:if>
        <s:if test="#attr.defaultFilter != null || #attr.defaultFilters != null">
        doDefaultFilter:true,
        <s:if test="#attr.defaultFilters != null">defaultFilters:<s:property value="defaultFilters"/>,</s:if>
        defaultFilter:<s:if test="#attr.defaultFilter == 'true' || #attr.defaultFilter == 'false'"><s:property value="#attr.defaultFilter"/></s:if><s:else>'<s:property value="#attr.defaultFilter"/>'</s:else>,
        defaultFilterCol:'<s:property value="#attr.defaultFilterCol"/>',
        </s:if>
        filters:[
        <s:iterator value="#attr.table.filters" status="status" id="filter">
            <s:if test="#status.index > 0">,</s:if><%--
            --%>{type: '<s:property value="#filter.type"/>', dataIndex: '<s:property value="#filter.name"/>'<%--
            --%><s:if test="#filter.className != null">, className: '<s:property value="#filter.className"/>'</s:if><%--
            --%><s:if test="#filter.optionsJson != null">, options: <s:property escape="false" value="#filter.optionsJson"/></s:if><%--
            --%><s:if test="#filter.filterSettings != null">, <s:property escape="false" value="#filter.filterSettings"/></s:if>}
        </s:iterator>]
    });
    for (var i=0; i < <s:property value="#attr.tableName"/>Filters.filters.length; i++) {
        <s:property value="#attr.tableName"/>Filters.getFilter(i).on('serialize',
            function(args) { if (this.className) args.className = this.className; }
        );
    }

    <%-- define the table level filters --%>
    <s:if test="#attr.table.hasToolbar">
        <% int tfcount = 0; %>
        var <s:property value="#attr.tableName"/>SingleRowActionMenu = null;
        var <s:property value="#attr.tableName"/>MultiRowActionMenu = null;
        <s:iterator value="#attr.table.toolbar.buttons" status="status" id="button">
        
        <s:if test="(#button.singleRowAction || #button.rowAction) && ! #button.menu">
        if (!<s:property value="#attr.tableName"/>SingleRowActionMenu) 
            <s:property value="#attr.tableName"/>SingleRowActionMenu = new Ext.menu.Menu({id:'<s:property value="#attr.tableName"/>-menu'});
        <s:if test="! #button.filter">
        <s:if test="#button.separator"><s:property value="#attr.tableName"/>SingleRowActionMenu.addSeparator();</s:if><s:else>
        <s:property value="#attr.tableName"/>SingleRowActionMenu.add({text: '<s:property value="#button.text"/>'<%--
        --%><s:if test="#button.hasHandler">, handler: <s:property value="#button.handler"/></s:if><%--
        --%><s:if test="#button.hasIcon">, icon: '<s:property value="#button.icon"/>'</s:if><%--
        --%><s:if test="#button.hasIconCls">, iconCls: '<s:property value="#button.iconCls"/>'</s:if><%--
        --%>});</s:else></s:if><%--
        --%>
        </s:if>
        <s:if test="#button.rowAction && !#button.menu && !#button.singleRowAction"><%-- gives us the right click for multi select from the top level buttons --%>
        if (!<s:property value="#attr.tableName"/>MultiRowActionMenu) 
            <s:property value="#attr.tableName"/>MultiRowActionMenu = new Ext.menu.Menu({id:'<s:property value="#attr.tableName"/>-menu'});
        <s:if test="#button.separator"><s:property value="#attr.tableName"/>MultiRowActionMenu.addSeparator();</s:if><s:else>
        <s:property value="#attr.tableName"/>MultiRowActionMenu.add({text: '<s:property value="#button.text"/>'<%--
            --%><s:if test="#button.hasHandler">, handler: <s:property value="#button.handler"/></s:if><%--
            --%><s:if test="#button.hasIcon">, icon: '<s:property value="#button.icon"/>'</s:if><%--
            --%><s:if test="#button.hasIconCls">, iconCls: '<s:property value="#button.iconCls"/>'</s:if>});</s:else>
        </s:if>
        
        
        <s:if test="#button.menu">
        var tableMenu<s:property value="#status.count"/> = new Ext.menu.Menu({<%--
        --%>id:  '<s:property value="#attr.tableName"/>-menu-<s:property value="#status.count"/>',<%-- 
        --%>items: [<%--
        --%><s:iterator value="#button.buttons" status="mstatus" id="mbutton">
            <s:if test="! #mbutton.filter">
            <s:if test="#mbutton.separator">'-'</s:if><s:else>
            { text: '<s:property value="#mbutton.text"/>'<%--
            --%><s:if test="#mbutton.hasHandler">, handler: <s:property value="#mbutton.handler"/></s:if><%--
            --%><s:if test="#mbutton.hasIcon">, icon: '<s:property value="#mbutton.icon"/>'</s:if><%--
            --%><s:if test="#mbutton.hasIconCls">, iconCls: '<s:property value="#mbutton.iconCls"/>'</s:if><%--
            --%><s:if test="#mbutton.disabled">, disabled: '<s:property value="#mbutton.disabled"/>'</s:if>}</s:else><s:if test="!#mstatus.last">,</s:if><%--
            --%></s:if><%--
        --%></s:iterator>
        ]});
        <s:if test="#button.singleRowAction">
        if (!<s:property value="#attr.tableName"/>SingleRowActionMenu) 
            <s:property value="#attr.tableName"/>SingleRowActionMenu = new Ext.menu.Menu({id:'<s:property value="#attr.tableName"/>-menu'});
        <s:iterator value="#button.buttons" status="mstatus" id="mbutton"><s:if test="! #mbutton.filter">
        <s:if test="#mbutton.separator"><s:property value="#attr.tableName"/>SingleRowActionMenu.addSeparator();</s:if><s:else>
        <s:property value="#attr.tableName"/>SingleRowActionMenu.add({text: '<s:property value="#mbutton.text"/>'<%--
        --%><s:if test="#mbutton.hasHandler">, handler: <s:property value="#mbutton.handler"/></s:if><%--
        --%><s:if test="#mbutton.hasIcon">, icon: '<s:property value="#mbutton.icon"/>'</s:if><%--
        --%><s:if test="#mbutton.hasIconCls">, iconCls: '<s:property value="#mbutton.iconCls"/>'</s:if><%--
        --%><s:if test="#mbutton.disabled">, disabled: '<s:property value="#mbutton.disabled"/>'</s:if>});</s:else></s:if><%--
        --%></s:iterator>
        </s:if>
        <s:if test="#button.rowAction">
        if (!<s:property value="#attr.tableName"/>MultiRowActionMenu) 
            <s:property value="#attr.tableName"/>MultiRowActionMenu = new Ext.menu.Menu({id:'<s:property value="#attr.tableName"/>-menu'});            
        <s:iterator value="#button.buttons" status="mstatus" id="mbutton"><s:if test="! #mbutton.filter && ! #button.singleRowAction">
        <s:if test="#button.separator"><s:property value="#attr.tableName"/>MultiRowActionMenu.addSeparator();</s:if><s:else>
        <s:property value="#attr.tableName"/>MultiRowActionMenu.add({text: '<s:property value="#mbutton.text"/>'<%--
        --%><s:if test="#mbutton.hasHandler">, handler: <s:property value="#mbutton.handler"/></s:if><%--
        --%><s:if test="#mbutton.hasIcon">, icon: '<s:property value="#mbutton.icon"/>'</s:if><%--
        --%><s:if test="#mbutton.hasIconCls">, iconCls: '<s:property value="#mbutton.iconCls"/>'</s:if><%--
        --%><s:if test="#mbutton.disabled">, disabled: '<s:property value="#mbutton.disabled"/>'</s:if>});</s:else></s:if><%--
        --%></s:iterator>
        </s:if>

        <s:iterator value="#button.buttons" status="mstatus" id="mbutton">
            <s:if test="#mbutton.filter">
            var tf<s:property value="#status.count"/>tf<s:property value="#mstatus.count"/> = new Ext.ux.grid.GridTableFilters({
                updateBuffer: 1000, <%-- default 500ms is too fast for multi-select filters --%>
                buttonMenu: tableMenu<s:property value="#status.count"/>,
                paramPrefix: 'tablefilter<%= tfcount++ %>',
                <s:if test="#mbutton.menuFilterText != null">menuText: '<s:property value="#mbutton.menuFilterText"/>',</s:if>
                <s:if test="! #attr.dataAction">local: true,</s:if>
                filters:[
                    {type: '<s:property value="#mbutton.filter.type"/>', dataIndex: '<s:property value="#mbutton.filter.name"/>'<%--
                    --%><s:if test="#mbutton.filter.className != null">, className: '<s:property value="#mbutton.filter.className"/>'</s:if><%--
                    --%><s:if test="#mbutton.filter.optionsJson != null">, options: <s:property escape="false" value="#mbutton.filter.optionsJson"/></s:if><%--
                    --%><s:if test="#mbutton.filter.filterSettings != null">, <s:property escape="false" value="#mbutton.filter.filterSettings"/></s:if>}
                ]
            });
            addGridPlugin(tf<s:property value="#status.count"/>tf<s:property value="#mstatus.count"/>);   
            </s:if>
        </s:iterator>
        </s:if><s:elseif test="false">  <%-- end #button.menu check --%>
        
            <s:if test="#button.singleRowAction">
            if (!<s:property value="#attr.tableName"/>SingleRowActionMenu) 
                <s:property value="#attr.tableName"/>SingleRowActionMenu = new Ext.menu.Menu({id:'<s:property value="#attr.tableName"/>-menu'});
                <s:if test="! #button.filter">
                <s:if test="#button.separator"><s:property value="#attr.tableName"/>SingleRowActionMenu.addSeparator();</s:if><s:else>
                <s:property value="#attr.tableName"/>SingleRowActionMenu.add({text: '<s:property value="#button.text"/>'<%--
                --%><s:if test="#button.hasHandler">, handler: <s:property value="#button.handler"/></s:if><%--
                --%><s:if test="#button.hasIcon">, icon: '<s:property value="#button.icon"/>'</s:if><%--
                --%><s:if test="#button.hasIconCls">, iconCls: '<s:property value="#button.iconCls"/>'</s:if><%--
                --%><s:if test="#button.disabled">, disabled: '<s:property value="#button.disabled"/>'</s:if>});</s:else>
                </s:if>
            </s:if>
            
            <s:if test="#button.rowAction">
            if (!<s:property value="#attr.tableName"/>MultiRowActionMenu) 
                <s:property value="#attr.tableName"/>MultiRowActionMenu = new Ext.menu.Menu({id:'<s:property value="#attr.tableName"/>-menu'});
                <s:if test="! #button.filter && ! #button.singleRowAction">
                <s:if test="#button.separator"><s:property value="#attr.tableName"/>MultiRowActionMenu.addSeparator();</s:if><s:else>
                <s:property value="#attr.tableName"/>MultiRowActionMenu.add({text: '<s:property value="#button.text"/>'<%--
                --%><s:if test="#button.hasHandler">, handler: <s:property value="#button.handler"/></s:if><%--
                --%><s:if test="#button.hasIcon">, icon: '<s:property value="#button.icon"/>'</s:if><%--
                --%><s:if test="#button.hasIconCls">, iconCls: '<s:property value="#button.iconCls"/>'</s:if><%--
                --%><s:if test="#button.disabled">, disabled: '<s:property value="#button.disabled"/>'</s:if>});</s:else>
                </s:if>
            </s:if>
            
        </s:elseif> <%-- end else on #button.menu check --%>
        
        </s:iterator>    <%-- end buttons iterator --%>
    </s:if><%-- end table level filters --%>    

    function getNodeFromReader(name) {
    <s:if test="#attr.table.jsonReader || ! #attr.dataAction">
        return <s:property value="#attr.tableName"/>Ds.reader.jsonData[name];
    </s:if><s:else>
        return Ext.DomQuery.selectNode("dataset/"+name, <s:property value="#attr.tableName"/>Ds.reader.xmlData);
    </s:else>
    }

    function getValueFromReader(name) {
    <s:if test="#attr.table.jsonReader || ! #attr.dataAction">
        return <s:property value="#attr.tableName"/>Ds.reader.jsonData[name];
    </s:if><s:else>
        return Ext.DomQuery.selectValue("dataset/"+name, <s:property value="#attr.tableName"/>Ds.reader.xmlData);
    </s:else>
    }
    
    <%-- This is to handle timeouts, just reloads the current page --%>
    <s:if test="#attr.dataAction">
    <s:property value="#attr.tableName"/>Ds.on('loadexception',
       function(a,conn,resp) {
        if (resp.status == '401') {
            window.location.href=window.location.href;
            // Authentication required - You need to Login
        }else if (resp.status == '302') {
            window.location.href=window.location.href;
            // Session Has Expired
        }   
    });
    </s:if><s:else>    
    /*
    <s:property value="#attr.tableName"/>Ds.on("datachanged",
        function() { <s:property value="#attr.tableName"/>Grid.getBottomToolbar().updateInfo(); }); 
    */
    </s:else>
    
    // pluggable renders
    <s:iterator value="#attr.table.renders" status="status" id="render">
        <s:property value="#render.renderFunction"/>
    </s:iterator>

    addGridPlugin(<s:property value="#attr.tableName"/>Filters); 
    <s:if test="#attr.table.hasExpanderTemplate">addGridPlugin(<s:property value="#attr.tableName"/>Expander);</s:if>

    // selection model is dependent on whether multi-select is enabled
    var <s:property value="#attr.tableName"/>Sm = new Ext.grid.<s:if test="#attr.table.multiselect">CheckboxSelectionModel({singleSelect:false});</s:if><s:else>RowSelectionModel({singleSelect:true})</s:else>
    //var <s:property value="#attr.tableName"/>Sm = new Ext.grid.<s:if test="#attr.table.multiselect">RowSelectionModel();</s:if><s:else>RowSelectionModel({singleSelect:true});</s:else>
    //<s:property value="#attr.tableName"/>Sm.on('rowselect', function(sm) {
        // state save
        //Ext.state.Manager.getProvider().set("<s:property value="#attr.tableName"/>GridSel", <s:property value="#attr.tableName"/>Grid.getSelectionModel().getSelected().get("id"));
    //});
    
    // the column model has information about grid columns
    // dataIndex maps the column to the specific data field in
    // the data store
    //       id: 'id', // id assigned so we can apply custom css (e.g. .x-grid-col-topic b { color:#333 })
    var <s:property value="#attr.tableName"/>Cm = new Ext.grid.ColumnModel([
        <s:if test="#attr.table.hasExpanderTemplate"><s:property value="#attr.tableName"/>Expander,</s:if>
        <s:if test="#attr.table.multiselect"><s:property value="#attr.tableName"/>Sm,</s:if>
        <s:iterator value="#attr.table.columnModels" status="status" id="col"><%--
            --%><s:if test="!#col.expanderColumn"><%--
            --%><s:if test="#col.customType != null">addGridPlugin(new <s:property value="#col.customType"/>(</s:if><%--
            --%>{header: '<s:property value="#col.header" escape="false"/>'<%-- 
            --%><s:if test="#col.width != null">, width: <s:property value="#col.width"/></s:if><%--
            --%>, dataIndex: '<s:property value="#col.dataIndex"/>'<%--
            --%>        
            <s:iterator value="#attr.table.filters" status="status" id="filter">
            <s:if test="#filter.name == #col.dataIndex && #filter.type == 'string'">
            , filter: {}
            </s:if>
            </s:iterator>
            <%--
            --%><s:if test="#col.hasCssId">, id: '<s:property value="#col.cssId"/>'</s:if><s:else>, id: '<s:property value="#col.dataIndex"/>'</s:else><%--
            --%><s:if test="#col.hasCss">, css: '<s:property value="#col.css"/>'</s:if><%--
            --%><s:if test="#col.hasEditor">, editor: <s:property value="#col.editor"/></s:if><%--
            --%><s:if test="#col.hasRenderer">, renderer: <s:property value="#col.renderer"/></s:if><%--
            --%><s:if test="#col.hasSummaryRenderer">, summaryRenderer: <s:property value="#col.summaryRenderer"/></s:if><%--
            --%><s:if test="#col.sortable == false">, sortable: <s:property value="#col.sortable"/>, groupable:false</s:if><%--
            --%><s:if test="#col.hasTooltip">, tooltip: '<s:property value="#col.tooltip"/>'</s:if><%--
            --%><s:if test="#col.hidden">, hidden: true</s:if><%--
            --%><s:if test="#col.customType != null"><s:if test="#col.customTypeConfig != null"><%--
              --%><s:iterator id="ct" value="#col.customTypeConfig" status="ctStatus">, <s:property value="#ct.key"/>:<s:property value="#ct.value"/></s:iterator><%--
            --%></s:if>}))</s:if><s:else>}</s:else><s:if test="! #status.last">,</s:if></s:if>
          </s:iterator>]);
    <s:property value="#attr.tableName"/>Cm.defaultSortable = <s:property value="#attr.sortable"/>;
    <s:if test="#attr.stateful">
    <%--
    <s:property value="#attr.tableName"/>Cm.on("columnMoved", function(col, oldIndex, newIndex){
        if (Ext.grid.<s:property value="#attr.tableName"/>Grid){
            Ext.grid.<s:property value="#attr.tableName"/>Grid.saveState();
        }
    });
    --%>
    <s:property value="#attr.tableName"/>Cm.on("hiddenchange", function(col, oldIndex, newIndex){
        if (Ext.grid.<s:property value="#attr.tableName"/>Grid){
            Ext.grid.<s:property value="#attr.tableName"/>Grid.saveState();
        }
    });
    <%--
    <s:property value="#attr.tableName"/>Cm.on("widthchange", function(col, oldIndex, newIndex){
        if (Ext.grid.<s:property value="#attr.tableName"/>Grid){
            Ext.grid.<s:property value="#attr.tableName"/>Grid.saveState();
        }
    });
    --%>
    </s:if>
    <%--
    for (var l in <s:property value="#attr.tableName"/>Cm.lookup)
        console.log("dataIndex="+<s:property value="#attr.tableName"/>Cm.lookup[l].dataIndex);
    --%>
    
    <%-- summary --%>
    <s:if test="#attr.table.summary">
        var <s:property value="#attr.tableName"/>Summary = new Ext.ux.grid.FIGridSummary({
            cols:[<s:iterator value="#attr.table.columnModels" status="status" id="col"><s:if test="#status.index > 0">,</s:if>''</s:iterator>]
        });
        addGridPlugin(<s:property value="#attr.tableName"/>Summary);
    </s:if>

    <s:if test="#attr.table.pageable">
        var <s:property value="#attr.tableName"/>PageSize = new Ext.ux.Andrie.pPageSize({
            nonDynamicVariations: [<s:iterator value="#attr.table.pageVariationValues" status="status" id="variation"><s:if test="#status.index > 0">,</s:if>['<s:property value="#attr.table.pageVariationDisplay[#status.index]"/>', <s:property value="#variation"/>]</s:iterator>]
            <s:if test="#attr.showAllButton">
            , showAll:true
            </s:if>
        });
        <s:if test="#attr.stateful">
        <s:property value="#attr.tableName"/>PageSize.stateful = true;
        <s:property value="#attr.tableName"/>PageSize.statename = "<s:property value="#attr.tableName"/>GridDsLimit";
        </s:if>        
    </s:if>
    
    var <s:property value="#attr.tableName"/>filterRow = new Ext.ux.grid.FilterRow({
        // automatically refilter store when records are added
        //refilterOnStoreUpdate: true
        gridFilter: <s:property value="#attr.tableName"/>Filters
      });
    addGridPlugin(<s:property value="#attr.tableName"/>filterRow);
    
    // create the grid
    <s:if test="#attr.stateful">
    var l = Ext.state.Manager.getProvider().get("<s:property value="#attr.tableName"/>GridDsLimit");
    <s:if test="#attr.statestartid != null">
    var s = Ext.state.Manager.getProvider().get("<s:property value="#attr.tableName"/>GridDsStart<s:property value="#attr.statestartid"/>");
    </s:if><s:else>
    var s = Ext.state.Manager.getProvider().get("<s:property value="#attr.tableName"/>GridDsStart");
    </s:else>
    var psize = <s:property value="#attr.table.pageSize"/>;
    var start = 0;
    if (l) psize = l;
    if (s) start = s;
    Ext.grid.<s:property value="#attr.tableName"/>GridDsStart = start;
    Ext.grid.<s:property value="#attr.tableName"/>GridDsLimit = psize;
    Ext.grid.<s:property value="#attr.tableName"/>GridFooter = 
        <s:if test="#attr.table.pageable">new Ext.PagingToolbar({
            <s:if test="#attr.stateful">stateful:true,
            stateId:'<s:property value="#attr.tableName"/>PagingToolbarState',
            pageSize: psize,          
            </s:if>
            <s:else>stateful:false,
            pageSize: <s:property value="#attr.table.pageSize"/>,
            </s:else>
            store: <s:property value="#attr.tableName"/>Ds,
            displayInfo: true,
            emptyMsg: "No records to display",
            plugins: <s:property value="#attr.tableName"/>Filters, 
            plugins: <s:property value="#attr.tableName"/>PageSize
        })</s:if><s:else>new Ext.ux.grid.GridFooter({store: <s:property value="#attr.tableName"/>Ds}),</s:else>;
        
    </s:if><s:else>
    Ext.grid.<s:property value="#attr.tableName"/>GridDsStart = 0;
    Ext.grid.<s:property value="#attr.tableName"/>GridDsLimit = <s:property value="#attr.table.pageSize"/>;
    </s:else>
    Ext.grid.<s:property value="#attr.tableName"/>GridSelections = new Array(); 
    var <s:property value="#attr.tableName"/>Grid = new Ext.grid.<s:if test="#attr.table.editable">Editor</s:if>GridPanel({
        filterRow: <s:property value="#attr.tableName"/>filterRow,
        id: '<s:property value="#attr.tableName"/>-gridid',
        //<s:if test="#attr.addToContainer == null">el: '<s:property value="#attr.tableName"/>-grid',</s:if>
        <s:if test="#attr.tableTitle != null">title: '<s:property value="#attr.tableTitle"/>',</s:if>
        <s:if test="#attr.tableIcon != null">iconCls: '<s:property value="#attr.tableIcon"/>',</s:if>
        ds: <s:property value="#attr.tableName"/>Ds,
        cm: <s:property value="#attr.tableName"/>Cm,
        sm:  <s:property value="#attr.tableName"/>Sm,
        <s:if test="#attr.collapsible != null && #attr.collapsible == 'true'">collapsible:true,</s:if>
        <s:if test="#attr.collapsed != null && #attr.collapsed == 'true'">collapsed:true,</s:if>
        <s:if test="#attr.table.enableDragDrop">enableDragDrop:true,ddGroup:'<s:property value="#attr.table.ddGroup"/>',</s:if>
        <s:if test="#attr.table.useRegion">region:'<s:property value="#attr.table.region"/>',</s:if>
        <s:if test="#attr.table.useAnchor">anchor:'<s:property value="#attr.table.anchor"/>',</s:if>
        <s:if test="#attr.table.useTitle">title:'<s:property value="#attr.table.title"/>',</s:if>
        <s:if test="#attr.table.useColumnWidth">columnWidth:'<s:property value="#attr.table.columnWidth"/>',</s:if>
        enableColLock:false,
        <%--autoExpandColumn: '<s:property value="#attr.table.columnModels.size()-1"/>',--%><%-- this gets the columns to fit the table by default --%>
        loadMask: true,
        alwaysShowHdMenu: true,
        <s:if test="#attr.table.stripeRows">
            stripeRows: true,
        </s:if>
        plugins: <s:property value="#attr.tableName"/>GridPlugins,
        <s:if test="#attr.table.hasToolbar">tbar: [''],</s:if>
        <s:if test="#attr.stateful">stateful:true,stateId:'<s:property value="#attr.tableName"/>GridState',</s:if><s:else>stateful:false,</s:else>
            bbar: Ext.grid.<s:property value="#attr.tableName"/>GridFooter,
        view: new Ext.grid.GroupingView({
            <s:if test="#attr.customRowColors != null && #attr.customRowColors">
            getRowClass : <s:property value="#attr.customRowColorsFunction"/>
            </s:if>
            //groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
        }),
        listeners: {
            'filterupdate': function() {
                // reset paging offset if filters are changed
                if (this.getStore() && this.getStore().lastOptions && this.getStore().lastOptions.params){
                    var params = this.getStore().lastOptions.params; 
                    if (params.start > 0)
                        params.start = 0;
                }
            }<s:if test="#attr.stateful">, 
            'beforestaterestore':function(cmp,state){
                <s:if test="#attr.statestartid != null">
                var s = Ext.state.Manager.getProvider().get("<s:property value="#attr.tableName"/>GridDsStart<s:property value="#attr.statestartid"/>");
                </s:if><s:else>
                var s = Ext.state.Manager.getProvider().get("<s:property value="#attr.tableName"/>GridDsStart");
                </s:else>
                var l = Ext.state.Manager.getProvider().get("<s:property value="#attr.tableName"/>GridDsLimit");
                if (s && l){
                    Ext.grid.<s:property value="#attr.tableName"/>GridDsStart = s;
                    Ext.grid.<s:property value="#attr.tableName"/>GridDsLimit = l;
                }
                <s:if test="#attr.defaultFilterId != null">
                Ext.grid.<s:property value="#attr.tableName"/>GridSelections[0] = <s:property value="#attr.defaultFilterId"/>;
                </s:if><s:else>
                //var selid = Ext.state.Manager.getProvider().get("<s:property value="#attr.tableName"/>GridSel");
                //if (selid){
                //  Ext.grid.<s:property value="#attr.tableName"/>GridSelections[0] = selid;
                //}
                </s:else>
            }</s:if>
            <s:if test="#attr.rowDblClick != null">
            , 'rowdblclick': <s:property value="#attr.rowDblClick"/> <%-- function(grid, rowIndex, e) --%>
            </s:if>
        }
        <s:if test="#attr.resizeOnWindow">
        ,monitorResize: true,
        doLayout: function() {
            this.setSize(Ext.get(this.getEl().dom.parentNode).getSize(true));
            Ext.grid.GridPanel.prototype.doLayout.call(this);
        }
        </s:if>
    });

    <s:property value="#attr.tableName"/>Grid.getView().emptyText = '<div id="<s:property value="#attr.tableName"/>gridNoRecDiv" style="width:200px;"><s:if test="#attr.emptyMessage != null"><s:property value="#attr.emptyMessage"/></s:if><s:else>No records to display</s:else></div>';

    // this is for outside javascript access
    Ext.grid.<s:property value="#attr.tableName"/>Grid = <s:property value="#attr.tableName"/>Grid;
    Ext.grid.<s:property value="#attr.tableName"/>GridDs = <s:property value="#attr.tableName"/>Ds;
    Ext.grid.<s:property value="#attr.tableName"/>GridFilters = <s:property value="#attr.tableName"/>Filters;
    Ext.grid.<s:property value="#attr.tableName"/>GridPlugins = <s:property value="#attr.tableName"/>GridPlugins;
    Ext.grid.<s:property value="#attr.tableName"/>GridRecord = <s:property value="#attr.tableName"/>Record;
    
    Ext.grid.currentGrid = Ext.grid.<s:property value="#attr.tableName"/>Grid;
    Ext.grid.currentGridDs = Ext.grid.<s:property value="#attr.tableName"/>GridDs;
    Ext.grid.currentToolbar = Ext.grid.<s:property value="#attr.tableName"/>GridFooter;
    
    <s:if test="! #attr.dataAction">Ext.grid.<s:property value="#attr.tableName"/>Data = <s:property value="#attr.tableName"/>Data;</s:if>

    <s:property value="#attr.tableName"/>Ds.on("beforeload", function(store, records, options){
        <%-- wiggle fix --%>
        //Ext.grid.<s:property value="#attr.tableName"/>Grid.getView().scrollToTop();
        if (Ext.grid.<s:property value="#attr.tableName"/>Grid.getView().scroller){ 
            Ext.grid.<s:property value="#attr.tableName"/>Grid.getView().scroller.dom.scrollLeft = 0;
        }
    });
    
    // this gets fired after the load of the data
    <s:property value="#attr.tableName"/>Ds.on("load", function(store, records, options){
        var fixGridByResize = false;
        if (Ext.grid.<s:property value="#attr.tableName"/>FilterToolbar && Ext.grid.<s:property value="#attr.tableName"/>FilterToolbar.isVisible()){
            fixGridByResize = true;
            Ext.grid.<s:property value="#attr.tableName"/>FilterToolbar.destroy();
        }
        <s:if test="#attr.filterTextDiv != null">
        var filtertextdiv = document.getElementById("<s:property value="#attr.filterTextDiv"/>");
        var filtercmp = Ext.get("<s:property value="#attr.filterTextDiv"/>");
        </s:if><s:else>
        var filtertextdiv = document.getElementById("filtertextdiv");
        var filtercmp = Ext.get("filtertextdiv");
        </s:else>
        if (filtertextdiv != null && filtertextdiv.style.display == ""){
            fixGridByResize = true;
            filtercmp.fadeOut({
                callback: function(){
                    filtertextdiv.style.display = "none";
                }
            });
        }        
        var filtertext = getValueFromReader("filtertext"); 
        if (filtertext && filtertext.replace(/^\s+|\s+$/g, '').length > 0){
            fixGridByResize = true;
            if (filtertextdiv != null){
                filtertextdiv.innerHTML = "<img src='/images/asterisk_yellow_small.png' border='0' align='top'/> &nbsp;&nbsp;"+filtertext;
                if (filtercmp != null) filtercmp.fadeIn();
            } else {
                Ext.grid.<s:property value="#attr.tableName"/>FilterToolbar = new Ext.Toolbar({
                    items:["<img src='/images/asterisk_yellow_small.png' border='0' align='top'/> &nbsp;&nbsp;"+filtertext+" &nbsp;&nbsp;<img src='/images/asterisk_yellow_small.png' border='0' align='top'/>"],
                    style:'background:#edf6e7;margin-left:0px;margin-right:0px;margin-top:0px;margin-bottom:3px;position:relative;overflow-x:auto;overflow-y:hidden;padding:5px;border-top:0px;border-left:0px;border-right:0px;border-bottom:1px solid #ccc;'
                });
                if (<s:property value="#attr.tableName"/>Grid.getTopToolbar()){ // see if there is a toolbar
                    Ext.grid.<s:property value="#attr.tableName"/>FilterToolbar.render(<s:property value="#attr.tableName"/>Grid.getTopToolbar().getEl(), 0);
                }
           
                //if (Ext.isIE){
                    // Fix for IE weirdness
                    //Ext.grid.<s:property value="#attr.tableName"/>FilterToolbar.getEl().applyStyles('background:#fff;margin-left:0px;margin-right:0px;margin-top:0px;margin-bottom:3px;position:relative;overflow-x:auto;overflow-y:hidden;padding:5px;border-top:0px;border-left:0px;border-right:0px;border-bottom:1px solid #ccc;width:100%');
                //}
                <s:property value="#attr.tableName"/>Grid.syncSize();
            }
        }
        <%-- This is to get the grid to show up sized correctly --%>
        if (fixGridByResize) {
            <s:if test="#attr.addToContainer != null">
            if (Ext.getCmp('<s:property value="#attr.addToContainer"/>').getEl()){
                Ext.getCmp('<s:property value="#attr.addToContainer"/>').setHeight(Ext.getCmp('<s:property value="#attr.addToContainer"/>').getSize().height+1);
                Ext.getCmp('<s:property value="#attr.addToContainer"/>').setHeight(Ext.getCmp('<s:property value="#attr.addToContainer"/>').getSize().height-1);
            }
            </s:if><s:else>
            if (Ext.getCmp('<s:property value="#attr.tableName"/>listGridPanel').getEl()){
                Ext.getCmp('<s:property value="#attr.tableName"/>listGridPanel').setHeight(Ext.getCmp('<s:property value="#attr.tableName"/>listGridPanel').getSize().height+1);
                Ext.getCmp('<s:property value="#attr.tableName"/>listGridPanel').setHeight(Ext.getCmp('<s:property value="#attr.tableName"/>listGridPanel').getSize().height-1);
            }
            </s:else>
        }
        
        <s:if test="#attr.table.summary">
        <s:property value="#attr.tableName"/>Summary.updateSummary(getNodeFromReader("summary"));
        </s:if>

        if (records.length == 0){
            var d = document.getElementById('<s:property value="#attr.tableName"/>gridNoRecDiv');
            if (d) {
                d.style.width=<s:property value="#attr.tableName"/>Cm.getTotalWidth()+"px";
            }  
        }

        <%-- state  start / limit --%>
        <s:if test="#attr.stateful">
        //console.log("stateful, in on load");
        //console.log("options.params.groupBy: "+options.params.groupBy);
        //console.log("store.baseParams.groupBy: "+store.baseParams.groupBy);
        //console.log("groupBy: "+groupBy);
        <s:if test="#attr.statestartid != null">
        Ext.state.Manager.getProvider().set("<s:property value="#attr.tableName"/>GridDsStart<s:property value="#attr.statestartid"/>", <s:property value="#attr.tableName"/>Ds.lastOptions.params.start);
        </s:if>
        <%--  If we want to enable page state on all other grids
        <s:else>
        Ext.state.Manager.getProvider().set("<s:property value="#attr.tableName"/>GridDsStart", <s:property value="#attr.tableName"/>Ds.lastOptions.params.start);
        </s:else>
        --%>
        //Ext.state.Manager.getProvider().set("<s:property value="#attr.tableName"/>GridDsLimit", <s:property value="#attr.tableName"/>Ds.lastOptions.params.limit);
        if (store.baseParams.groupBy){
            Ext.state.Manager.getProvider().set("<s:property value="#attr.tableName"/>GridGroupBy", store.baseParams.groupBy);
            //console.log("set GridGroupBy to: "+store.baseParams.groupBy);
        } else if (options.params.groupBy){ // then this is the case where we ungrouped, so we need to clear the groupBy state
            //console.log("groupBy get: "+Ext.state.Manager.getProvider().get("<s:property value="#attr.tableName"/>GridGroupBy"));
            store.baseParams.groupBy = null;
            Ext.state.Manager.getProvider().clear("<s:property value="#attr.tableName"/>GridGroupBy");
            //console.log("cleared GridGroupBy");
        }
        /*
        var recs = new Array();
        if (Ext.grid.<s:property value="#attr.tableName"/>GridSelections && Ext.grid.<s:property value="#attr.tableName"/>GridSelections[0]){
            var ourid = Ext.grid.<s:property value="#attr.tableName"/>GridSelections[0].toString();
            var sel = store.findBy(function(rec,id){
                if (rec.get("id") == ourid){
                    return true;
                }
            });
            if (sel > -1 && doSelectionOnLoad) {
                <s:property value="#attr.tableName"/>Grid.getSelectionModel().selectRow(sel);
                setTimeout(function(){<s:property value="#attr.tableName"/>Grid.getView().focusRow(sel);},250);
                doSelectionOnLoad = false;
            } else if (sel < 0 && !doSelectionOnLoad){
                Ext.state.Manager.getProvider().clear("<s:property value="#attr.tableName"/>GridSel");
            }
        }
        */
        </s:if><s:else>
        if (options.params.groupBy){ // then this is the case where we ungrouped, so we need to clear the groupBy state
            //console.log("groupBy get: "+Ext.state.Manager.getProvider().get("<s:property value="#attr.tableName"/>GridGroupBy"));
            store.baseParams.groupBy = null;
            //console.log("cleared GridGroupBy");
        }        
        </s:else>
        
        e = document.getElementById('btnStopSearch');
        if (e != null) 
            e.parentNode.removeChild(e);
        
    });

    <s:property value="#attr.tableName"/>Grid.on('render', function(component){
        <s:if test="#attr.table.hasToolbar">
        var <s:property value="#attr.tableName"/>Toolbar = <s:property value="#attr.tableName"/>Grid.getTopToolbar();
        var rightButtons = false;
        Ext.grid.<s:property value="#attr.tableName"/>Toolbar = <s:property value="#attr.tableName"/>Toolbar;
        <s:iterator value="#attr.table.toolbar.text" status="status" id="text">
            <s:if test="#status.index > 0">
                <s:property value="#attr.tableName"/>Toolbar.addSeparator();
            </s:if>
            <s:property value="#attr.tableName"/>Toolbar.addText('<s:property value="#text" escape="false"/>');
        </s:iterator>
        <s:if test="#attr.table.toolbar.text != null &&  #attr.table.toolbar.text.size() > 0 && #attr.table.toolbar.buttons != null && #attr.table.toolbar.buttons.size() > 0">
            <s:property value="#attr.tableName"/>Toolbar.addSeparator();
        </s:if>
        <s:iterator value="#attr.table.toolbar.buttons" status="status" id="button">
            <s:if test="#status.index > 0 && !#button.right">
                <s:property value="#attr.tableName"/>Toolbar.addSeparator();
            </s:if>
            <s:if test="#button.right">
                rightButtons = true;
                <s:property value="#attr.tableName"/>Toolbar.addFill();
            </s:if>
            <s:if test="#button.linkButton">
                if (rightButtons){
                    var lb<s:property value="#status.count"/> = new Ext.PaddedLinkButton({
                        text: '<s:property value="#button.text"/>',
                        href:'<s:property value="#button.linkHref"/>'
                        <s:if test="#button.hasIconCls">,iconCls: '<s:property value="#button.iconCls"/>'</s:if>
                        <s:if test="#button.hasTooltip">,tooltip: '<s:property value="#button.tooltip"/>'</s:if>
                    });
                    <s:property value="#attr.tableName"/>Toolbar.add(lb<s:property value="#status.count"/>);
                } else {
                    var lb<s:property value="#status.count"/> = new Ext.LinkButton({
                        text: '<s:property value="#button.text"/>',
                        href:'<s:property value="#button.linkHref"/>'
                        <s:if test="#button.hasIconCls">,iconCls: '<s:property value="#button.iconCls"/>'</s:if>
                        <s:if test="#button.hasTooltip">,tooltip: '<s:property value="#button.tooltip"/>'</s:if>
                    });
                    <s:property value="#attr.tableName"/>Toolbar.add(lb<s:property value="#status.count"/>);
                }
            </s:if>
            <s:else>
                var lb<s:property value="#status.count"/> = new Ext.Button({
                    id:  '<s:property value="#attr.tableName"/>-lb-<s:property value="#status.count"/>',
                    text: '<s:property value="#button.text"/>'
                    <s:if test="#button.hasHandler">,listeners: {'click': <s:property value="#button.handler"/>, scope: <s:property value="#button.scope"/>}</s:if>
                    <s:if test="#button.hasIcon">,icon: '<s:property value="#button.icon"/>'</s:if>
                    <s:if test="#button.hasIconCls">,iconCls: '<s:property value="#button.iconCls"/>'</s:if>
                    <s:if test="#button.hasTooltip">,tooltip: '<s:property value="#button.tooltip"/>'</s:if>
                    <s:if test="#button.disabled">,disabled: '<s:property value="#button.disabled"/>'</s:if>
                });
                <s:property value="#attr.tableName"/>Toolbar.add(lb<s:property value="#status.count"/>);
            </s:else><%--
            --%><s:if test="#button.rowAction">
                <s:property value="#attr.tableName"/>Sm.on('selectionchange', function(sm) {
                    var disable = <s:if test="#button.singleRowAction">sm.getSelections().length != 1 ||</s:if> !sm.hasSelection();
                    var button = Ext.getCmp('<s:property value="#attr.tableName"/>-lb-<s:property value="#status.count"/>'); 
                    if (button && button.disabled != disable) 
                        button.setDisabled(disable);
                });
            </s:if><%--
            --%><s:if test="#button.menu">
                lb<s:property value="#status.count"/>.setHandler(function() { tableMenu<s:property value="#status.count"/>.show(lb<s:property value="#status.count"/>.getEl()) });
            </s:if><%-- 
        --%></s:iterator><%-- end button iterator --%>
        if (<s:property value="#attr.tableName"/>SingleRowActionMenu){
            <s:property value="#attr.tableName"/>Grid.on('rowcontextmenu', function(grid, rowIndex, e) {
                e.stopEvent(); // Stops the browser context menu from showing.
                if (grid.getSelectionModel().getCount() == 1 && grid.getSelectionModel().isSelected(rowIndex))
                    this.showAt(e.getXY());
            },<s:property value="#attr.tableName"/>SingleRowActionMenu);
        }
        if (<s:property value="#attr.tableName"/>MultiRowActionMenu){
            <s:property value="#attr.tableName"/>Grid.on('rowcontextmenu', function(grid, rowIndex, e) {
                e.stopEvent(); // Stops the browser context menu from showing.
                if (grid.getSelectionModel().getCount() > 1 && grid.getSelectionModel().isSelected(rowIndex))
                    this.showAt(e.getXY());
            },<s:property value="#attr.tableName"/>MultiRowActionMenu);
        }
        
        var clearFilters = {
                text: 'Clear Filters',
                href: "javascript:clearGridFilters('<s:property value="#attr.tableName"/>');",
                iconCls: 'reset_icon',
                tooltip: 'Clear Table Filters'
        };
        if (!rightButtons) {
            <s:property value="#attr.tableName"/>Toolbar.addFill();
            rightButtons = true;
        }
        <s:property value="#attr.tableName"/>Toolbar.add(new Ext.PaddedLinkButton(clearFilters));
        
        <%-- add the excel export button link --%>
        <s:if test="#attr.exportable">
            var exportAction = '<s:property value="#attr.exportAction"/>' || dataActionUrl
            var exportConfig = {
                    text: 'Excel',
                    <s:if test="#attr.exportJS != null">
                    href: "javascript:<s:property value="#attr.exportJS"/>('<s:property value="#attr.tableName"/>-gridid', '"+exportAction+"');",
                    </s:if><s:else>
                    href: "javascript:exportGridToExcel('<s:property value="#attr.tableName"/>-gridid', '"+exportAction+"');",
                    </s:else>
                    iconCls: 'excel_icon',
                    tooltip: 'Export To Excel'
            };
            if (!rightButtons) {
                <s:property value="#attr.tableName"/>Toolbar.addFill();
                rightButtons = true;
            }
            <s:if test="#attr.stateful">
            <s:property value="#attr.tableName"/>Toolbar.add(new Ext.PaddedLinkButton(exportConfig));
            <s:property value="#attr.tableName"/>Toolbar.addSeparator();
            </s:if><s:else>
            <s:property value="#attr.tableName"/>Toolbar.add(new Ext.PaddedLinkButton(exportConfig));
            </s:else>
            
        </s:if><%-- end excel export button --%>
        <s:if test="#attr.stateful">
        var clearConfig = {
                text: 'Reset',
                href: "javascript:resetGridState('<s:property value="#attr.tableName"/>');",
                iconCls: 'reset_icon',
                tooltip: 'Reset View Of The Grid Columns To Default'
        };
        if (!rightButtons) {
            <s:property value="#attr.tableName"/>Toolbar.addFill();
            rightButtons = true;
        }
        <s:property value="#attr.tableName"/>Toolbar.add(new Ext.PaddedLinkButton(clearConfig));
        </s:if>
            
        <s:if test="#attr.table.marketable">
            var marketBtn = {
                text: 'Market',
                href: "javascript:Ext.form.generateForm.submit({failure: function(form, action){Ext.MessageBox.alert('Status', action.result.error);}});",
                iconCls: 'market_icon',
                tooltip: 'Export marketing from this result'
            };
            <s:property value="#attr.tableName"/>Toolbar.add(new Ext.PaddedLinkButton(marketBtn));
        </s:if>
        
        <s:property value="#attr.tableName"/>Grid.syncSize();
        </s:if><%-- and if hasToolbar --%>
        
        <%-- This is to get the grid to show up sized correctly 
        <s:if test="#attr.addToContainer != null">
        if (Ext.getCmp('<s:property value="#attr.addToContainer"/>').getEl()){
            Ext.getCmp('<s:property value="#attr.addToContainer"/>').setHeight(Ext.getCmp('<s:property value="#attr.addToContainer"/>').getSize().height+1);
            Ext.getCmp('<s:property value="#attr.addToContainer"/>').setHeight(Ext.getCmp('<s:property value="#attr.addToContainer"/>').getSize().height-1);
        }
        </s:if><s:else>
        if (Ext.getCmp('<s:property value="#attr.tableName"/>listGridPanel').getEl()){
            Ext.getCmp('<s:property value="#attr.tableName"/>listGridPanel').setHeight(Ext.getCmp('<s:property value="#attr.tableName"/>listGridPanel').getSize().height+1);
            Ext.getCmp('<s:property value="#attr.tableName"/>listGridPanel').setHeight(Ext.getCmp('<s:property value="#attr.tableName"/>listGridPanel').getSize().height-1);
        }
        </s:else>
        --%>


    });
    
    <%-- events --%>
    <s:iterator value="#attr.table.gridEvents" status="status" id="event">
        <s:property value="#attr.tableName"/>Grid.on("<s:property value="#event.eventName"/>", <s:property value="#event.function"/>);
    </s:iterator>
    <s:iterator value="#attr.table.gridStoreEvents" status="status" id="event">
        <s:property value="#attr.tableName"/>Ds.on("<s:property value="#event.eventName"/>", <s:property value="#event.function"/>);
    </s:iterator>
    <s:iterator value="#attr.table.gridSelectionEvents" status="status" id="event">
        <s:property value="#attr.tableName"/>Grid.getSelectionModel().on("<s:property value="#event.eventName"/>", <s:property value="#event.function"/>);
    </s:iterator>

    <%-- add this to an existing ext component --%>
    <s:if test="#attr.addToContainer != null">
    var container = Ext.getCmp('<s:property value="#attr.addToContainer"/>');
    </s:if><s:else>
    var container = Ext.getCmp('<s:property value="#attr.tableName"/>listGridPanel');
    </s:else>
    if (container) {
        if (container.items && container.getComponent('<s:property value="#attr.tableName"/>-gridid')){
            <%-- if this is called without the if check then the render is not called on the grid so no toolbar --%>
            container.remove(<s:property value="#attr.tableName"/>Grid);
        }
        container.add(<s:property value="#attr.tableName"/>Grid);
        container.doLayout();
    }
    <%--
    else
        console.log("addToContainer '<s:property value="#attr.addToContainer"/>' not found");
    --%>

    // trigger the data store load
    <s:if test="#attr.table.pageable">
        if (Ext.grid.<s:property value="#attr.tableName"/>GroupBy){
            <s:property value="#attr.tableName"/>Ds.load({params:{start:Ext.grid.<s:property value="#attr.tableName"/>GridDsStart, limit:Ext.grid.<s:property value="#attr.tableName"/>GridDsLimit,groupBy:Ext.grid.<s:property value="#attr.tableName"/>GroupBy}});
        } else {
            <s:property value="#attr.tableName"/>Ds.load({params:{start:Ext.grid.<s:property value="#attr.tableName"/>GridDsStart, limit:Ext.grid.<s:property value="#attr.tableName"/>GridDsLimit}});
        }
    </s:if><s:else>
        <s:property value="#attr.tableName"/>Ds.load({params:{start:0}});
    </s:else>
             
    if (dataActionUrl.indexOf('search') > 0 || dataActionUrl.indexOf('Search') > 0){
        stopSearch = document.createElement("button");
        stopSearch.textContent = "Stop Search";
        stopSearch.id = "btnStopSearch";
        stopSearch.addEventListener("click", function(){
            Ext.Ajax.abort(Ext.grid.currentGridDs.proxy.activeRequest); 
            e = document.getElementById('btnStopSearch');
            if (e != null){
                Ext.grid.currentToolbar.loading.enable();
                e.parentNode.removeChild(e);
            }
            Ext.grid.currentGrid.loadMask.hide();
        });
        
        possibleMasks = Ext.select('.ext-el-mask-msg');
        if (possibleMasks != null){
           if (possibleMasks.elements.length > 0){
               mask = possibleMasks.elements[0];
               mask.appendChild(stopSearch);
           }
        }
    }

});

</script>

<s:if test="#attr.addToContainer == null">
<%-- actual ext grid div --%>
<div id="<s:property value="#attr.tableName"/>-grid" <s:if test="#attr.includeGridClass == null || #attr.includeGridClass == 'true'">class="gridtable"</s:if> >
</div>
</s:if>
