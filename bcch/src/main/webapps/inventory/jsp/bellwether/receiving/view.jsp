<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Bellwether Inventory</title>

<jwr:script src="/bundles/bellreceivingBundle.js"/> 

<s:set name="stateSession" value="%{'receiving-view-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">

var doEditNextItem = null;

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
    Ext.crudWindow.show(Ext.grid.bellreceivingitemsGrid);    
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
    Ext.uploadWindow.show(Ext.grid.bellreceivingitemsGrid);
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
                <s:if test="!receiving.posted && isBcReceivingAdmin">
                ,tbar: [
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
                }, "->", {
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
                                        timeout: 300,
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
                }]</s:if>
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
                <td align="left" style="padding-left:8px;"><s:property value="receiving.publisherCode"/></td>
                
                <td align="right" class="tdName" style="padding-left:50px;">Comment:</td>
                <td align="left" style="padding-left:8px;"><s:property value="receiving.comment"/></td>
                
            </tr>
            </table>
        </td>
        
        
        <td align="left" style="padding-left:50px;" rowspan="10" valign="top">
            <a href="receivingreport!report.bc?id=<s:property value="receiving.id"/>&amp;filename=Receiving-<s:property value="receiving.poNumber"/>.pdf"><img src="/images/page_white_acrobat.png" border="0" align="bottom">&nbsp;&nbsp;Receiving Report PDF</a>
        </td>
        <s:if test="isBcReceivingAdmin"> 
        <td align="left" style="padding-left:50px;" rowspan="10" valign="top">
            <a href="javascript:void(0);" onclick="javascript:exportCost(<s:property value="receiving.id"/>, '<s:property value="receiving.poNumber"/>'); return false;"><img src="/images/page_white_office.png" border="0" align="bottom">&nbsp;&nbsp;Export Cost Update</a>
            <br/> 
            <a href="javascript:void(0);" onclick="javascript:importCost(<s:property value="receiving.id"/>);return false;"><img src="/images/page_white_office.png" border="0" align="bottom">&nbsp;&nbsp;Import Cost Update</a> 
        </td>
        </s:if>
        
    </tr>
    
</table>

 
</div>

<inv:table tableName="bellreceivingitems" sortable="true" exportable="true" tableTitle="Receiving Items"
dataAction="receiving!listItemData.bc?id=${id}" table="${listTable}" addToContainer="listpanel" stateful="true"
statestartid="${id}"
/>

<form action="receivingitem!delete.bc" name="deleteitemform" id="deleteitemform"></form>
<form action="receiving!post.bc" name="postform" id="postform"></form>
<form action="receiving!delete.bc" name="deleteform" id="deleteform"></form>

</div>

<script language="JavaScript" type="text/javascript">
Ext.onReady(function(){
    Ext.grid.bellreceivingitemsGridDs.on("load", function(store, records, opts){
        if (doEditNextItem != null){
            var selId = doEditNextItem;
            doEditNextItem = null;
            var foundRec;
            Ext.grid.bellreceivingitemsGridDs.each(function(rec){
               if (rec.get("id") == selId){
                   foundRec = rec;
                   return false;
               } 
            });
            if (foundRec != undefined){
                var idx = Ext.grid.bellreceivingitemsGridDs.indexOf(foundRec);
                Ext.grid.bellreceivingitemsGrid.getSelectionModel().selectRow(idx);
                editNextItem();
            }
        }
    });
});
</script>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
