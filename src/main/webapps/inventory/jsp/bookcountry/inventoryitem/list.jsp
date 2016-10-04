<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Book Country Inventory</title>

<jwr:script src="/bundles/inventoryBundle.js"/> 

<s:set name="stateSession" value="%{'inventory-item-list-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">

var curSelectedIdx = null;
var curSelectedRec = null;
var curStart = 0;

Ext.onReady(function(){

    <s:set name="activeMenu" value="%{'inventory'}"/>
    <%@ include file="../div-header-menu.jspf" %>

    var contentpanel = new Ext.Panel({
        id       : 'dualPanel',
        layout   : 'border',
        border   : false,
        bodyBorder: false,
        items : [
            {
               region: 'center',
               border   : false,
               bodyBorder: false,
               split:true,
               type: 'xpanel',
               layout: 'fit',
               id: 'listpanel'
            },
            {
                region: 'east',
                title: 'Inventory Detail',
                layout   : 'border',
                collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                border   : false,
                bodyBorder: false,
                width:300,
                minWidth:150,
                split:true,
                items: [
                    {    
                        region: 'center',
                        border   : false,
                        bodyBorder: false,
                        minHeight: 150,
                        autoScroll: true,
                        split:true,
                        type: 'xpanel',
                        layout: 'fit',
                        bodyCfg : {style: {'background':'#fff'}},
                        id: 'detailpanel',
                        html: noselectionhtml
                    }, {    
                        region: 'south',
                        collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                        split:true,
                        border   : false,
                        bodyBorder: false,
                        title: 'Amazon Detail',
                        iconCls: 'amazon_icon',
                        height:350,
                        minHeight: 150,
                        autoScroll: true,
                        type: 'xpanel',
                        layout: 'fit',
                        bodyCfg : {style: {'background':'#fff'}},
                        id: 'amazonpanel',
                        html: clickloadhtml,
                        tbar: [
                           {
                               text: 'Load',
                               id: 'loadAmazonDataButton',
                               tooltip: 'Load Amazon Data',
                               hidden: false,
                               disabled: true,
                               iconCls: 'down_arrow_icon',
                               listeners: {scope: this, 'click': function(){
                                       updateAmazonDetail();
                                   }
                               }
                           },'-',
                           {
                               text: 'Refresh',
                               id: 'refreshAmazonDataButton',
                               tooltip: 'Refresh Amazon Data',
                               hidden: false,
                               disabled: true,
                               iconCls: 'refresh_icon',
                               listeners: {scope: this, 'click': function(){
                                       updateAmazonDetail();
                                   }
                               }
                           }
                        ]
                    }
                ]                
             }
        ]
    });


    new Ext.Viewport({
        id: 'mainviewport',
        layout: 'border',
        layoutConfig: {
            minWidth: 800,
            minHeight: 500
        },
        //bufferResize:true,
        items: [{
            region: 'north',
            id: 'northpanel',
            border: false,
            collapsible: false,
            layout: 'fit',
            height:60,
            margins: '0 0 0 0',
            cls: 'invtoolbar',
            bodyCfg : {style: {'border-bottom':'1px solid #999'} },
            items: [headermenu]
        },{
            region: 'center',
            margins: '0 0 0 0',
            collapsible: false,
            border: true,
            layout: 'fit',
            id: 'contentpanel',
            items: [contentpanel]
        },{
            region: 'south',
            margins: '0 0 0 0',
            collapsible: false,
            height: 24,
            border: true,
            bodyCfg : {style: {'background-color':'#ddd'} },
            id: 'footerpanel',
            layout: 'fit',
            contentEl: 'invfooter'
        }]
    });

    Ext.form.deleteInventoryForm = new Ext.form.BasicForm("deleteinventoryform");
    
    var quickSearchText = document.getElementById("quickSearchText");
    if (quickSearchText != undefined){
        quickSearchText.focus();
    }
    
});
</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div style="display:none">


<s:if test="defaultFilters != null">
<inv:table tableName="inventory" sortable="true" exportable="true" tableTitle="Inventory Items"
dataAction="inventoryitem!listData.bc" table="${listTable}" addToContainer="listpanel" stateful="true"
rowDblClick="rowDoubleClick" defaultFilters="${defaultFilters}"/>
</s:if>
<s:elseif test="isQuickSearch">
<inv:table tableName="inventory" sortable="true" exportable="true" tableTitle="Inventory Items - Quick Search - ${quickSearch}"
dataAction="inventoryitem!quickSearchListData.bc?quickSearch=${quickSearch}" table="${listTable}" addToContainer="listpanel" stateful="true"
rowDblClick="rowDoubleClick"/>
</s:elseif>
<s:else>
<inv:table tableName="inventory" sortable="true" exportable="true" tableTitle="Inventory Items"
dataAction="inventoryitem!listData.bc" table="${listTable}" addToContainer="listpanel" stateful="true"
rowDblClick="rowDoubleClick"/>
</s:else>

</div>

<script language="JavaScript" type="text/javascript">
    Ext.onReady(function(){
        Ext.grid.inventoryGrid.getSelectionModel().on('selectionchange', function(){
            setTimeout("updateInventoryDetail()", 300);
        });
        Ext.grid.inventoryGridDs.on('load', function(store, records, options){
            updateInventoryDetail();
            if (options.params.start != curStart){
                curStart = options.params.start;
            } else if (curSelectedIdx != undefined){
                Ext.grid.inventoryGrid.getSelectionModel().selectRow(curSelectedIdx);
                Ext.grid.inventoryGrid.getView().focusRow(curSelectedIdx);
            }
        });
        Ext.grid.inventoryGrid.getSelectionModel().on("rowselect", function(model, idx, rec){
            curSelectedIdx = idx;
            curSelectedRec = rec;
        });
    });
</script>

<form action="inventoryitem!delete.bc" name="deleteinventoryform" id="deleteinventoryform"></form>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
