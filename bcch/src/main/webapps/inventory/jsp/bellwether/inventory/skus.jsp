<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Bellwether Inventory</title>

<jwr:script src="/bundles/bellInventoryBundle.js"/> 

<s:set name="stateSession" value="%{'bell-inventory-list-skus-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">
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
                            }, '-',
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
    
});
</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div style="display:none">

<s:if test="defaultFilters != null">
<inv:table tableName="inventoryskus" sortable="true" exportable="true" tableTitle="Inventory Items By SKU"
dataAction="inventory!listSkuData.bc" table="${listTable}" addToContainer="listpanel" stateful="true"
rowDblClick="rowDoubleClick" defaultFilters="${defaultFilters}"/>
</s:if><s:else>
<inv:table tableName="inventoryskus" sortable="true" exportable="true" tableTitle="Inventory Items By SKU"
dataAction="inventory!listSkuData.bc" table="${listTable}" addToContainer="listpanel" stateful="true"
rowDblClick="rowDoubleClick"/>
</s:else>

</div>

<script language="JavaScript" type="text/javascript">
    Ext.onReady(function(){
        Ext.grid.inventoryskusGrid.getSelectionModel().on('selectionchange', function(){
            setTimeout("updateInventorySkuDetail()", 300);
        });
        Ext.grid.inventoryskusGridDs.on('load', function(store, records, options){
            updateInventorySkuDetail();
        });
    });
</script>

<form action="inventory!deletesku.bc" name="deleteinventoryform" id="deleteinventoryform"></form>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
