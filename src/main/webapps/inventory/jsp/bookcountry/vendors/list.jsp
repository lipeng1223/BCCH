<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Book Country Inventory</title>

<jwr:script src="/bundles/vendorBundle.js"/> 

<s:set name="stateSession" value="%{'vendor-list-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">

var curSelectedIdx = null;
var curSelectedRec = null;
var curStart = 0;

Ext.onReady(function(){

    <s:set name="activeMenu" value="%{'vendors'}"/>
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
                border   : false,
                bodyBorder: false,
                title: 'Vendor Detail',
                width: 350,
                minWidth: 150,
                autoScroll: true,
                collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                split:true,
                type: 'xpanel',
                layout: 'fit',
                bodyCfg : {style: {'background':'#eaeaea'}},
                html: noselectionhtml,
                id: 'detailpanel'
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
    Ext.form.deleteSkidTypeForm = new Ext.form.BasicForm("deleteskidtypeform");
    Ext.form.createSkidTypeForm = new Ext.form.BasicForm("createskidtypeform");
    Ext.form.updateSkidTypeForm = new Ext.form.BasicForm("updateskidtypeform");
    
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

<s:if test="isQuickSearch">
    <inv:table tableName="vendors" sortable="true" exportable="true" tableTitle="Vendors - Quick Search - ${quickSearch}"
    dataAction="vendor!listData.bc?quickSearch=${quickSearch}" table="${listTable}" addToContainer="listpanel" 
    tableIcon="group_icon" stateful="true"/>
</s:if><s:else>
    <inv:table tableName="vendors" sortable="true" exportable="true" tableTitle="Vendors"
    dataAction="vendor!listData.bc" table="${listTable}" addToContainer="listpanel" 
    tableIcon="group_icon" stateful="true"/>
</s:else>

<script language="JavaScript" type="text/javascript">
    Ext.onReady(function(){
        Ext.grid.vendorsGrid.getSelectionModel().on('selectionchange', function(){
            setTimeout("updateVendorDetail()", 300);
        });
        Ext.grid.vendorsGridDs.on('load', function(store, records, options){
            updateVendorDetail();
            if (options.params.start != curStart){
                curStart = options.params.start;
            } else if (curSelectedIdx != undefined){
                Ext.grid.vendorsGrid.getSelectionModel().selectRow(curSelectedIdx);
                Ext.grid.vendorsGrid.getView().focusRow(curSelectedIdx);
            }
        });
        Ext.grid.vendorsGrid.getSelectionModel().on("rowselect", function(model, idx, rec){
            curSelectedIdx = idx;
            curSelectedRec = rec;
        });
    });
</script>


<form action="vendor!delete.bc" name="deleteform" id="deleteform"></form>
<form action="vendor!deleteSkidType.bc" name="deleteskidtypeform" id="deleteskidtypeform"></form>
<form action="vendor!createSkidType.bc" name="createskidtypeform" id="createskidtypeform"></form>
<form action="vendor!updateSkidType.bc" name="updateskidtypeform" id="updateskidtypeform"></form>

</div>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
