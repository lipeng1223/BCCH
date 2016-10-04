<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Bellwether Inventory</title>

<jwr:script src="/bundles/bellreceivingBundle.js"/> 

<s:set name="stateSession" value="%{'bellreceiving-list-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">
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
    <inv:table tableName="bellreceivings" sortable="true" exportable="true" tableTitle="Receivings"
    dataAction="receiving!listData.bc" table="${listTable}" addToContainer="listpanel" stateful="true"
    rowDblClick="rowDoubleClick" defaultFilters="${defaultFilters}"/>
</s:if>
<s:elseif test="isQuickSearch">
    <inv:table tableName="bellreceivings" sortable="true" exportable="true" tableTitle="Receivings - Quick Search - ${quickSearch}"
    dataAction="receiving!listData.bc?quickSearch=${quickSearch}" table="${listTable}" addToContainer="listpanel" stateful="true"
    rowDblClick="rowDoubleClick"/>
</s:elseif>
<s:else>
    <inv:table tableName="bellreceivings" sortable="true" exportable="true" tableTitle="Receivings"
    dataAction="receiving!listData.bc" table="${listTable}" addToContainer="listpanel" stateful="true"
    rowDblClick="rowDoubleClick"/>
</s:else>
<form action="receiving!delete.bc" name="deleteform" id="deleteform"></form>


</div>

<script language="JavaScript" type="text/javascript">
    Ext.onReady(function(){
        Ext.grid.bellreceivingsGrid.getSelectionModel().on('selectionchange', function(){
            setTimeout("updateReceivingDetail()", 300);
        });
        Ext.grid.bellreceivingsGridDs.on('load', function(store, records, options){
            updateReceivingDetail();
        });
    });
</script>


<%@ include file="../../div-footer.jspf" %>

</body>
</html>
