<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Book Country Inventory</title>

<jwr:script src="/bundles/manifestBundle.js"/> 

<s:set name="stateSession" value="%{'manifest-search-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">

var curSelectedIdx = null;
var curSelectedRec = null;
var curStart = 0;

Ext.onReady(function(){

    <s:set name="activeMenu" value="%{'manifests'}"/>
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
                layout:'border',
                items: [
                    {    
                        region: 'center',
                        border   : false,
                        bodyBorder: false,
                        split:true,
                        type: 'xpanel',
                        layout: 'fit',
                        id: 'listpanel'
                    }, {    
                        region: 'north',
                        collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                        split:true,
                        border   : false,
                        bodyBorder: false,
                        title: 'Manifest Search',
                        iconCls: 'view_icon',
                        height:160,
                        minHeight: 80,
                        autoScroll: true,
                        type: 'xpanel',
                        layout: 'fit',
                        bodyCfg : {style: {'background':'#fbfbfb'}},
                        contentEl: 'searchpanel'
                    }                   
                ]
            }<%--,
            {
                region: 'east',
                title: 'Manifest Detail',
                layout   : 'fit',
                collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                border   : false,
                bodyBorder: false,
                id: 'detailpanel',
                width:300,
                minWidth:150,
                split:true,
                html: noselectionhtml
             } --%>
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

    new Ext.Button({
        id:'manisearchbutton', 
        applyTo:'manifestSearchSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'Search', 
        disabled:false,
        handler: function(){
            document.getElementById("manisearchform").submit();
        } 
    });

    setFocus("manifestSearch-searchval0");
    
});
</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div style="display:none">

<div id="searchpanel">
    <form class="formular" action="manifest!search.bc" id="manisearchform" method="post">
        <s:set name="manifestSearchPrefix" value="%{'manifestSearch-'}"/>
        <s:set name="searchNames" value="searchNames"/>
        <%@ include file="search.jspf" %>
        <input type="submit" style="width: 0px; height: 0px; position: absolute; left: -50px; top: -50px;"/>
    </form>

</div>

<inv:table tableName="manifests" sortable="true" exportable="true" tableTitle="Matching Manifests"
dataActionParams="${search.queryString}"
dataAction="manifest!searchData.bc" table="${listTable}" addToContainer="listpanel" stateful="true" rowDblClick="rowDoubleClick"/>

</div>

<%--
<script language="JavaScript" type="text/javascript">
    Ext.onReady(function(){
        Ext.grid.manifestsGrid.getSelectionModel().on('selectionchange', function(){
            setTimeout("updateManifestDetail()", 300);
        });
        Ext.grid.manifestsGridDs.on('load', function(store, records, options){
            updateManifestDetail();
        });
    });
</script>
--%>

<form action="manifest!delete.bc" name="deleteform" id="deleteform"></form>

<script language="JavaScript" type="text/javascript">
Ext.onReady(function(){
    Ext.grid.manifestsGridDs.on("load", function(store, records, options){
        if (options.params.start != curStart){
            curStart = options.params.start;
        } else if (curSelectedIdx != undefined){
            Ext.grid.manifestsGrid.getSelectionModel().selectRow(curSelectedIdx);
            Ext.grid.manifestsGrid.getView().focusRow(curSelectedIdx);
        }
    });
    Ext.grid.manifestsGrid.getSelectionModel().on("rowselect", function(model, idx, rec){
        curSelectedIdx = idx;
        curSelectedRec = rec;
    });
});
</script>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
