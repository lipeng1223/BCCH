<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Book Country Inventory</title>

<jwr:script src="/bundles/receivingBundle.js"/> 

<s:set name="stateSession" value="%{'receiving-list-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">

var curSelectedIdx = null;
var curSelectedRec = null;
var curStart = 0;

Ext.onReady(function(){

    <s:set name="activeMenu" value="%{'receiving'}"/>
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
                title: 'Receiving Detail',
                id: 'detailpanel',
                layout   : 'fit',
                collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                border   : false,
                bodyBorder: false,
                width:300,
                minWidth:150,
                split:true,
                autoScroll:true,
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
    <inv:table tableName="receivings" sortable="true" exportable="true" tableTitle="Receivings"
    dataAction="receiving!listData.bc" table="${listTable}" addToContainer="listpanel" stateful="true"
    rowDblClick="rowDoubleClick" defaultFilters="${defaultFilters}"/>
</s:if>
<s:elseif test="isQuickSearch">
    <inv:table tableName="receivings" sortable="true" exportable="true" tableTitle="Receivings - Quick Search - ${quickSearch}"
    dataAction="receiving!listData.bc?quickSearch=${quickSearch}" table="${listTable}" addToContainer="listpanel" stateful="true"
    rowDblClick="rowDoubleClick"/>
</s:elseif>
<s:else>
    <inv:table tableName="receivings" sortable="true" exportable="true" tableTitle="Receivings"
    dataAction="receiving!listData.bc" table="${listTable}" addToContainer="listpanel" stateful="true"
    rowDblClick="rowDoubleClick"/>
</s:else>
<form action="receiving!delete.bc" name="deleteform" id="deleteform"></form>
<form action="receiving!unpost.bc" name="unpostform" id="unpostform"></form>


</div>

<script language="JavaScript" type="text/javascript">
    Ext.onReady(function(){
        Ext.grid.receivingsGrid.getSelectionModel().on('selectionchange', function(){
            setTimeout("updateReceivingDetail()", 300);
        });
        Ext.grid.receivingsGridDs.on('load', function(store, records, options){
            updateReceivingDetail();
            if (options.params.start != curStart){
                curStart = options.params.start;
            } else if (curSelectedIdx != undefined){
                Ext.grid.receivingsGrid.getSelectionModel().selectRow(curSelectedIdx);
                Ext.grid.receivingsGrid.getView().focusRow(curSelectedIdx);
            }
        });
        Ext.grid.receivingsGrid.getSelectionModel().on("rowselect", function(model, idx, rec){
            curSelectedIdx = idx;
            curSelectedRec = rec;
        });
        Ext.form.unpostForm = new Ext.form.BasicForm("unpostform");
    });
</script>


<%@ include file="../../div-footer.jspf" %>

</body>
</html>
