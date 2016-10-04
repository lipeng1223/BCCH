<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Bellwether Inventory</title>

<jwr:script src="/bundles/bellorderBundle.js"/> 

<s:set name="stateSession" value="%{'bellorder-list-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">

function refreshDetailPanel(){
    var cmp = Ext.getCmp("orderDetailPanel");
    if (cmp != undefined && cmp.getUpdater() != undefined) cmp.getUpdater().refresh();
}


Ext.onReady(function(){

    <s:set name="activeMenu" value="%{'orders'}"/>
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
                title: 'Order Detail',
                layout   : 'fit',
                id: 'detailpanel',
                autoScroll: true,
                collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                border   : false,
                bodyBorder: false,
                width:300,
                minWidth:150,
                split:true,
                html: noselectionhtml
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

    Ext.form.deleteForm = new Ext.form.BasicForm("deleteform");
    
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
    <inv:table tableName="bellorders" sortable="true" exportable="true" tableTitle="Orders"
    dataAction="order!listData.bc" table="${listTable}" addToContainer="listpanel" stateful="true"
    rowDblClick="rowDoubleClick" defaultFilters="${defaultFilters}"/>
</s:if>
<s:elseif test="isQuickSearch">
    <inv:table tableName="bellorders" sortable="true" exportable="true" tableTitle="Orders - Quick Search - ${quickSearch}"
    dataAction="order!listData.bc?quickSearch=${quickSearch}" table="${listTable}" addToContainer="listpanel" stateful="true"
    rowDblClick="rowDoubleClick"/>
</s:elseif>
<s:else>
    <inv:table tableName="bellorders" sortable="true" exportable="true" tableTitle="Orders"
    dataAction="order!listData.bc" table="${listTable}" addToContainer="listpanel" stateful="true"
    rowDblClick="rowDoubleClick"/>
</s:else>

</div>

<script language="JavaScript" type="text/javascript">
    Ext.onReady(function(){
        Ext.grid.bellordersGrid.getSelectionModel().on('selectionchange', function(){
            setTimeout("updateOrderDetail()", 300);
        });
        Ext.grid.bellordersGridDs.on('load', function(store, records, options){
            updateOrderDetail();
        });
    });
</script>

<form action="order!delete.bc" name="deleteform" id="deleteform"></form>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
