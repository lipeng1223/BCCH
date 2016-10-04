<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Bellwether Inventory</title>

<jwr:script src="/bundles/bellorderBundle.js"/> 

<s:set name="stateSession" value="%{'bellorder-search-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">
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
                        title: 'Order Search',
                        iconCls: 'view_icon',
                        height:180,
                        minHeight: 80,
                        autoScroll: true,
                        type: 'xpanel',
                        layout: 'fit',
                        bodyCfg : {style: {'background':'#fbfbfb'}},
                        contentEl: 'searchpanel'
                    }                   
                ]
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

    new Ext.Button({
        id:'ordersearchbutton', 
        applyTo:'orderSearchSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'Search', 
        disabled:false,
        handler: function(){
            document.getElementById("ordersearchform").submit();
        } 
    });
    
    setFocus("orderSearch-searchval0");
    
});

</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div style="display:none">

<div id="searchpanel">
    <form class="formular" action="order!search.bc" id="ordersearchform" method="post">
        <s:set name="orderSearchPrefix" value="%{'orderSearch-'}"/>
        <s:set name="searchNames" value="searchNames"/>
        <%@ include file="search.jspf" %>
        <input type="submit" style="width: 0px; height: 0px; position: absolute; left: -50px; top: -50px;"/>
    </form>

</div>

<inv:table tableName="bellorders" sortable="true" exportable="true" tableTitle="Matching Orders"
dataActionParams="${search.queryString}"
dataAction="order!searchData.bc" table="${listTable}" addToContainer="listpanel" stateful="true" rowDblClick="rowDoubleClick"/>

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


<%@ include file="../../div-footer.jspf" %>

</body>
</html>
