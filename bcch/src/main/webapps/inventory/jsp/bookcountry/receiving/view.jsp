<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Book Country Inventory</title>

<jwr:script src="/bundles/receivingBundle.js"/> 

<s:set name="stateSession" value="%{'receiving-view-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">

var doEditNextItem = null;
var curSelectedIdx = null;
var curSelectedRec = null;
var curStart = 0;

function createItemButtonClick(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'createitemwindow',
        title: 'Create Receiving Item',
        width:900,
        height:600,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'receivingitem!create.bc?id=<s:property value="id"/>'
    });
    Ext.crudWindow.show(Ext.grid.receivingitemsGrid);    
}

function fastRecItemButtonClick(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'createitemwindow',
        title: 'Fast Receiving Item',
        width:480,
        height:380,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        layout: 'border',
        items: [
            {
                xtype:'panel',
                region: 'north',
                height: 125,
                autoLoad: 'receivingitem!fastrec.bc?id=<s:property value="id"/>'
            }, {
                xtype: 'panel',
                region: 'center',
                layout: 'fit',
                title: 'History',
                autoScroll: true,
                padding: 5,
                id: 'fastrechistorypanel',
                html: '<div style="width:100%"><table id="fastrechistorytable" style="width:100%"></table></div>'
            }
        ],
        listeners: {
            'close': function(){
                    Ext.grid.receivingitemsGridDs.reload();
            }
        }
    });
    Ext.crudWindow.show(Ext.grid.receivingitemsGrid);    
}

function fastRecHistoryItemButtonClick(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.historyWindow = new Ext.Window({
        id: 'fastrechistorywindow',
        title: 'Fast Receiving History',
        width:550,
        height:400,
        modal:true,
        stateful:false,
        autoScroll:true,
        layout: 'border',
        bbar:["->", {
            id:'cancelButton', 
            cls:"x-btn-text-icon", 
            icon:"/images/cancel.png", 
            text:'Close', 
            handler:function(){
                Ext.historyWindow.close();
            }
        }],
        items : [
            {
                region: 'center',
                border   : false,
                bodyBorder: false,
                split:true,
                type: 'xpanel',
                layout: 'fit',
                id: 'fastrechistorypanel'
            }
        ],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'receivingitem!fastrecHistory.bc?id=<s:property value="id"/>'
    });
    Ext.historyWindow.show(Ext.grid.receivingitemsGrid);    
}

function importItemButtonClick(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.uploadWindow = new Ext.Window({
        id: 'uploadwindow',
        title: 'Upload Receiving Items',
        width:600,
        height:200,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'receivingitem!uploadPage.bc?id=<s:property value="id"/>'
    });
    Ext.uploadWindow.show(Ext.grid.receivingitemsGrid);
}

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
               collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
               type: 'xpanel',
               layout: 'border',
               items : [
                        {
                           region: 'center',
                           border   : false,
                           bodyBorder: false,
                           split:true,
                           type: 'xpanel',
                           layout: 'fit',
                           id: 'listpanel'
                        }, {
                            region: 'east',
                            width: 300,
                            title: "Receiving Item Detail",
                            collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                            bodyStyle: 'background:#eaeaea;',
                            border   : false,
                            bodyBorder: false,
                            split:true,
                            type: 'xpanel',
                            layout: 'fit',
                            id: 'detailpanel',
                            collapsed: true,
                            autoScroll: true,
                            html: noitemselectionhtml
                         }
                    ]
            },
            {
                region: 'north',
                title: 'Receiving Detail',
                layout   : 'fit',
                collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                border   : false,
                bodyBorder: false,
                height:250,
                minHeight:150,
                split:true,
                autoScroll: true,
                contentEl: 'receivingDetail' 
                ,tbar: [
                <s:if test="!receiving.posted && isBcReceivingAdmin">
                {
                    text: 'Edit Receiving',
                    iconCls: 'edit_icon', 
                    handler: function(){
                        Ext.Updater.defaults.loadScripts = true;
                        Ext.crudWindow = new Ext.Window({
                            id: 'editwindow',
                            title: 'Edit Receiving',
                            width:600,
                            height:600,
                            modal:true,
                            stateful:false,
                            autoScroll:true,
                            bbar:[],
                            bodyStyle:'background-color:#fbfbfb',
                            autoLoad: 'receiving!edit.bc?id=<s:property value="receiving.id"/>'
                        });
                        Ext.crudWindow.show();
                    }
                }, "-", {
                    text: 'Delete Receiving',
                    iconCls: 'delete_icon', 
                    handler: function(){
                        Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the Receiving id: <s:property value="receiving.id"/> PO: <s:property value="receiving.poNumber"/> ?', function(btn){
                            if (btn == "yes"){
                                Ext.form.deleteForm.submit({
                                    timeout: 300,
                                    params:{'id':<s:property value="receiving.id"/>},
                                    waitMsg:'Deleting Receiving...',
                                    failure: function(form, action){
                                        Ext.MessageBox.alert('Status', action.result.error);
                                    },
                                    success: function(form, action){
                                        interPageMove("receiving!list.bc");
                                    }
                                });
                            }
                        });
                    }
                }, "-", {
                    text: 'History',
                    iconCls: 'calendar_icon',
                    handler: function(){
                        showHistory("received", <s:property value="receiving.id"/>, "Receiving Audit History", "received_item", "Receiving Audit History", "Received Items Audit History");
                    }
                }<s:if test="!receiving.holding">, "->", {
                    text: 'Post This Receiving',
                    iconCls: 'accept_icon', 
                    handler: function(){
                        var win = new Ext.Window({
                            id: 'postwindow',
                            title: 'Post Receiving',
                            width:280,
                            height:120,
                            modal:true,
                            stateful:false,
                            autoScroll:true,
                            bbar:['->',{
                                text: 'Post',
                                cls:"x-btn-text-icon", 
                                icon:"/images/accept.png", 
                                handler: function(){
                                    var date = Ext.getCmp("postDateField").getValue();
                                    var dateString = undefined;
                                    if (date != undefined && date.format){
                                        dateString = date.format("m/d/Y");
                                    }
                                    if (dateString == undefined){
                                        Ext.MessageBox.alert('Error', "A Post Date must be given.");
                                        return;
                                    }
                                    win.close();
                                    Ext.form.postForm.submit({
                                        timeout: 1500,
                                        params:{'id':<s:property value="receiving.id"/>, 'dateString':dateString},
                                        waitMsg:'Posting Receiving...',
                                        failure: function(form, action){
                                            Ext.MessageBox.alert('Status', action.result.error);
                                        },
                                        success: function(form, action){
                                            interPageMove("receiving!view.bc?id=<s:property value="receiving.id"/>");
                                        }
                                    });
                                }
                            }, '-', {
                                text: 'Cancel',
                                cls:"x-btn-text-icon", 
                                icon:"/images/cancel.png", 
                                handler: function(){
                                    win.close();
                                }
                            }],
                            bodyStyle:'background-color:#fbfbfb;padding:15px;',
                            layout:'form',
                            items: [
                                {
                                    xtype:'datefield',
                                    fieldLabel: 'Post Date',
                                    id: 'postDateField',
                                    value: (new Date()).format('m/d/Y')                                    
                                }
                            ]
                        });
                        win.show();
                    }
                }</s:if></s:if>
                <s:elseif test="isBcReceivingAdmin">
                {
                    text: 'History',
                    iconCls: 'calendar_icon',
                    handler: function(){
                        showHistory("received", <s:property value="receiving.id"/>, "Receiving Audit History", "received_item", "Receiving Audit History", "Received Items Audit History");
                    }
                }
                </s:elseif>
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

    Ext.form.deleteForm = new Ext.form.BasicForm("deleteform");
    Ext.form.postForm = new Ext.form.BasicForm("postform");
    Ext.form.deleteItemForm = new Ext.form.BasicForm("deleteitemform");
    
});
</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div style="display:none">

<div id="receivingDetail" style="margin:8px;margin-left:25px;">

<table>
    <tr>
        <td valign="top">
            <table>
            <tr>
                <td align="right" class="tdName">PO Number:</td>
                <td align="left" style="padding-left:8px;"><s:property value="receiving.poNumber"/></td>
                
                <td align="right" class="tdName" style="padding-left:50px;">PO Date:</td>
                <td align="left" style="padding-left:8px;"><s:date name="receiving.poDate" format="MM/dd/yyyy" /></td>
                
            </tr>
            <tr><td style="height:4px;"></td></tr>
            <tr>
                <td align="right" class="tdName" style="padding-left:50px;">Vendor:</td>
                <td align="left" style="padding-left:8px;"><s:property value="receiving.vendor.vendorName"/></td>
                
                <td align="right" class="tdName" style="padding-left:50px;">Vendor Code:</td>
                <td align="left" style="padding-left:8px;"><s:property value="receiving.vendor.code"/></td>
                
            </tr>
            <tr><td style="height:4px;"></td></tr>
            <tr>
                <td align="right" class="tdName">Posted:</td>
                <td align="left" style="padding-left:8px;"><s:if test="receiving.posted"><span class='greentext'>Yes</span></s:if><s:else><span class='redtext'>No</span></s:else></td>
                
                <td align="right" class="tdName" style="padding-left:50px;">Post Date:</td>
                <td align="left" style="padding-left:8px;"><s:if test="!receiving.posted">not posted yet</s:if><s:date name="receiving.postDate" format="MM/dd/yyyy" /></td>
            </tr>
            <tr><td style="height:4px;"></td></tr>
            <tr>
                <td align="right" class="tdName" style="padding-left:50px;">Publisher:</td>
                <td align="left" style="padding-left:8px;"><s:property value="receiving.publisher"/></td>
                
                <td align="right" class="tdName" style="padding-left:50px;">Comment:</td>
                <td align="left" style="padding-left:8px;"><s:property value="receiving.comment"/></td>
                
            </tr>
            <tr><td style="height:4px;"></td></tr>
            <tr>
                <td align="right" class="tdName">Holding:</td>
                <td align="left" style="padding-left:8px;"><s:if test="receiving.holding"><span class='greentext'>Yes</span></s:if><s:else><span class='redtext'>No</span></s:else></td>
            </tr>
            </table>
        </td>
        
        
        <td align="left" style="padding-left:50px;" rowspan="10" valign="top">
            <a href="receivingreport!report.bc?id=<s:property value="receiving.id"/>&amp;filename=Receiving-<s:property value="receiving.poNumber"/>.pdf"><img src="/images/page_white_acrobat.png" border="0" align="bottom">&nbsp;&nbsp;Receiving Report PDF</a>
        </td>
        <s:if test="isBcReceivingAdmin && !receiving.holding"> 
        <td align="left" style="padding-left:50px;" rowspan="10" valign="top">
            <a href="javascript:void(0);" onclick="javascript:exportCost(<s:property value="receiving.id"/>, '<s:property value="receiving.poNumber"/>'); return false;"><img src="/images/page_white_office.png" border="0" align="bottom">&nbsp;&nbsp;Export Cost Update</a>
            <br/> 
            <a href="javascript:void(0);" onclick="javascript:importCost(<s:property value="receiving.id"/>);return false;"><img src="/images/page_white_office.png" border="0" align="bottom">&nbsp;&nbsp;Import Cost Update</a> 
        </td>
        </s:if>
        
    </tr>
    
</table>

 
</div>

<inv:table tableName="receivingitems" sortable="true" exportable="true" tableTitle="Receiving Items"
dataAction="receiving!listItemData.bc?id=${id}" table="${listTable}" addToContainer="listpanel" stateful="true"
statestartid="${id}"
/>

<form action="receivingitem!delete.bc" name="deleteitemform" id="deleteitemform"></form>
<form action="receiving!post.bc" name="postform" id="postform"></form>
<form action="receiving!delete.bc" name="deleteform" id="deleteform"></form>

</div>

<script language="JavaScript" type="text/javascript">
Ext.onReady(function(){
    Ext.grid.receivingitemsGrid.getSelectionModel().on('selectionchange', function(){
        setTimeout("updateRecItemDetail()", 300);
    });
    Ext.grid.receivingitemsGridDs.on("load", function(store, records, options){
        updateRecItemDetail();
        if (doEditNextItem != null){
            var selId = doEditNextItem;
            doEditNextItem = null;
            var foundRec;
            Ext.grid.receivingitemsGridDs.each(function(rec){
               if (rec.get("id") == selId){
                   foundRec = rec;
                   return false;
               } 
            });
            if (foundRec != undefined){
                var idx = Ext.grid.receivingitemsGridDs.indexOf(foundRec);
                Ext.grid.receivingitemsGrid.getSelectionModel().selectRow(idx);
                editNextItem();
            }
        } else if (options.params.start != curStart){
            curStart = options.params.start;
        } else if (curSelectedIdx != undefined){
            if (Ext.crudWindow != undefined && Ext.crudWindow.isVisible()){
                // see if we are fast receiving
                var fastRec = document.getElementById("fastReceiving").checked;
                if (fastRec != undefined && fastRec) return;
            }
            try {
                Ext.grid.receivingitemsGrid.getSelectionModel().selectRow(curSelectedIdx);
                Ext.grid.receivingitemsGrid.getView().focusRow(curSelectedIdx);
            } catch (err){}
        }
    });
    Ext.grid.receivingitemsGrid.getSelectionModel().on("rowselect", function(model, idx, rec){
        curSelectedIdx = idx;
        curSelectedRec = rec;
    });
});
</script>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
