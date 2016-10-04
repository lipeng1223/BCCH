<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Book Country Inventory</title>

<jwr:script src="/bundles/inventoryBundle.js"/> 

<s:set name="stateSession" value="%{'inventory-audit-list-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">

var curSelectedIdx = null;
var curSelectedRec = null;
var curStart = 0;

function updateInventoryAuditDetail(force){
    var selections = Ext.grid.inventoryauditGrid.getSelectionModel().getSelections();
    if (selections && selections.length == 1){
        var selected = selections[0];
        if (selected && selected.get("id") != null){
            
            if (force || currentItemDetailId != selected.get("id")) {
                currentlyBlank = false;
                currentItemDetailId = selected.get("id");
                // load the view data 
                var panel = Ext.getCmp('detailpanel');
                if (panel){
                    panel.getUpdater().update({
                       url:'inventoryitem!detail.bc',
                    params: {id:selected.get("tableId")},
                    //text: 'Loading Data for: '+selected.get('name'),
                    nocache: true,
                    timeout: 30,
                    scripts: true
                 });
                    //panel.getEl().child("div.x-panel-body").applyStyles("background:#f5f5f5");
                }
                
            }
            
        } else {
            blankTheAuditDetail();
        }
    } else {
        blankTheAuditDetail();
        
    }
}
function blankTheAuditDetail(){
    if (currentlyBlank) return;
    currentlyBlank = true;
    currentItemDetailId = -1;
    var panel = Ext.getCmp('detailpanel');
    if(panel) {
        panel.body.update(noselectionhtml);
    }
}

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
                title: 'Inventory Item Detail',
                collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                border   : false,
                bodyBorder: false,
                width:300,
                minWidth:150,
                split:true,
                type: 'xpanel',
                autoScroll:true,
                layout: 'fit',
                bodyCfg : {style: {'background':'#fff'}},
                id: 'detailpanel',
                html: noselectionhtml
             },
             {
                region: 'north',
                title: 'Date Range',
                collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                border   : false,
                bodyBorder: false,
                height:120,
                minHeight:100,
                split:true,
                id: 'daterange',
                autoScroll: true,
                type: 'xpanel',
                layout: 'fit',
                bodyCfg : {style: {'background':'#fbfbfb'}},
                contentEl: 'daterangepanel'
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
        id:'invauditbutton', 
        applyTo:'inventoryAuditSubmit', 
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'List', 
        disabled:false,
        handler: function(){
            document.getElementById("inventoryauditform").submit();
        } 
    });

});
</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div style="display:none">

<div id="daterangepanel">
    <form class="formular" action="inventoryaudit!list.bc" id="inventoryauditform" method="post">
        <table>
            <tr>
                <td>Start Date:</td><td style="padding-left:10px;"><s:textfield key="startDate"/></td>
                <td style="padding-left:10px;"><a href="#" onclick="javascript:searchDateSelect('startDate');return false;" title="Select Date"><img src="/images/calendar.png" align="top"/></a></td>
            </tr>
            <tr>
                <td>End Date:</td><td style="padding-left:10px;"><s:textfield key="endDate"/></td>
                <td style="padding-left:10px;"><a href="#" onclick="javascript:searchDateSelect('endDate');return false;" title="Select Date"><img src="/images/calendar.png" align="top"/></a></td>
            </tr>
            <tr>
                <td align="right" colspan="3"><div id="inventoryAuditSubmit"></div></td>
            </tr>
        </table>
        <input type="submit" style="width: 0px; height: 0px; position: absolute; left: -50px; top: -50px;"/>
    </form>

</div>
    
    
<inv:table tableName="inventoryaudit" sortable="true" exportable="true" tableTitle="Inventory Item Bin Updates"
dataAction="inventoryaudit!listData.bc" table="${listTable}" addToContainer="listpanel" stateful="true"
dataActionParams="startDate=${startDate}&endDate=${endDate}"/>

</div>

<script language="JavaScript" type="text/javascript">
    Ext.onReady(function(){
        Ext.grid.inventoryauditGrid.getSelectionModel().on('selectionchange', function(){
            setTimeout("updateInventoryAuditDetail()", 300);
        });
        Ext.grid.inventoryauditGridDs.on('load', function(store, records, options){
            updateInventoryAuditDetail();
            if (options.params.start != curStart){
                curStart = options.params.start;
            } else if (curSelectedIdx != undefined){
                Ext.grid.inventoryauditGrid.getSelectionModel().selectRow(curSelectedIdx);
                Ext.grid.inventoryauditGrid.getView().focusRow(curSelectedIdx);
            }
        });
        Ext.grid.inventoryauditGrid.getSelectionModel().on("rowselect", function(model, idx, rec){
            curSelectedIdx = idx;
            curSelectedRec = rec;
        });
    });
</script>

<form action="inventoryitem!delete.bc" name="deleteinventoryform" id="deleteinventoryform"></form>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
