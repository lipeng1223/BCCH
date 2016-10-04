<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Book Country Inventory</title>

<jwr:script src="/bundles/backstockBundle.js"/> 

<s:set name="stateSession" value="%{'backstock-list-state'}"/>
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
                title: 'Back Stock Locations',
                layout   : 'fit',
                collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                border   : false,
                bodyBorder: false,
                autoScroll: true,
                id: 'detailpanel',
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
    Ext.form.deleteLocationForm = new Ext.form.BasicForm("deletelocationform");
    
});
</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div style="display:none">

<inv:table tableName="backStock" sortable="true" exportable="true" tableTitle="Back Stock"
    dataAction="backstock!listData.bc" table="${listTable}" addToContainer="listpanel" stateful="true"
  />

</div>

<form action="backstock!delete.bc" name="deleteform" id="deleteform"></form>
<form action="backstock!deleteLocation.bc" name="deletelocationform" id="deletelocationform"></form>

<script language="JavaScript" type="text/javascript">
Ext.onReady(function(){
    Ext.grid.backStockGridDs.on("load", function(store, records, options){
        if (options.params.start != curStart){
            curStart = options.params.start;
        } else if (curSelectedIdx != undefined){
            Ext.grid.backStockGrid.getSelectionModel().selectRow(curSelectedIdx);
            Ext.grid.backStockGrid.getView().focusRow(curSelectedIdx);
        }
        updateBackStockDetail();
    });
    Ext.grid.backStockGrid.getSelectionModel().on("rowselect", function(model, idx, rec){
        curSelectedIdx = idx;
        curSelectedRec = rec;
    });
    
    Ext.grid.backStockGrid.getSelectionModel().on('selectionchange', function(){
        setTimeout("updateBackStockDetail()", 300);
    });
});
</script>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
