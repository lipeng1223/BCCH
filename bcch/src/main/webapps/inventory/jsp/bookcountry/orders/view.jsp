<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title><s:property value="order.customer.code"/><s:if test="order.customer.code != null && order.customer.code.length() > 0 && order.customer.companyName != null && order.customer.companyName.length() > 0"> - </s:if><s:property value="order.customer.companyName"/></title>

<jwr:script src="/bundles/orderBundle.js"/> 

<s:set name="stateSession" value="%{'order-view-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">

var doEditNextItem = null;
var curSelectedIdx = null;
var curSelectedRec = null;
var curStart = 0;

function importItemButtonClick(){
    Ext.Updater.defaults.loadScripts = true;
    Ext.uploadWindow = new Ext.Window({
        id: 'uploadwindow',
        title: 'Upload Order Items',
        width:600,
        height:300,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'orderitem!uploadPage.bc?id=<s:property value="id"/>'
    });
    Ext.uploadWindow.show(Ext.grid.orderitemsGrid);
}

function createItemButtonClick(id){
    Ext.Updater.defaults.loadScripts = true;
    Ext.crudWindow = new Ext.Window({
        id: 'createitemwindow',
        title: 'Create Order Item',
        width:600,
        height:400,
        modal:true,
        stateful:false,
        autoScroll:true,
        bbar:[],
        bodyStyle:'background-color:#fbfbfb',
        autoLoad: 'orderitem!create.bc?id=<s:property value="id"/>'
    });
    Ext.crudWindow.show(Ext.grid.orderitemsGrid);
}


function fixZeroButtonClick(){
    Ext.form.fixZeroPriceForm.submit({
        timeout:300,
        params:{'id':<s:property value="order.id"/>},
        waitMsg:'Fixing Zero Prices...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            refreshDetailPanel();
            Ext.grid.orderitemsGridDs.reload();
        }
    });
}

function refreshDetailPanel(){
    var cmp = Ext.getCmp("orderDetailPanel");
    if (cmp != undefined && cmp.getUpdater() != undefined) cmp.getUpdater().refresh();
}

function shipMaxButtonClick(){
    Ext.form.shipMaxForm.submit({
        timeout: 300,
        params:{'id':<s:property value="order.id"/>},
        waitMsg:'Setting Shipped To Current Max...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            refreshDetailPanel();
            Ext.grid.orderitemsGridDs.reload();
        }
    });
}

function postOrder(){
    Ext.postWindow = new Ext.Window({
        id: 'postwindow',
        title: 'Post Order',
        width:340,
        height:150,
        modal:true,
        stateful:false,
        autoScroll:true,
        layout:'fit',
        bbar:['->',{
            text: 'Post This Order',
            iconCls: 'accept_icon',
            handler: function(btn,e){
                if (!Ext.getCmp('postDateField').isValid()){
                    Ext.Msg.alert('Error', 'Post Date must be a valid date mm/dd/yyyy.');
                    return;
                }
                btn.setDisabled(true);
                Ext.form.postForm.submit({
                    timeout:2400,
                    params:{'id':<s:property value="order.id"/>, 'postDateString':Ext.getCmp("postDateField").getValue().format('m/d/Y')},
                    waitMsg:'Posting Order...',
                    failure: function(form, action){
                        Ext.MessageBox.alert('Status', action.result.error);
                    },
                    success: function(form, action){
                        Ext.postWindow.close();
                        if (action.result.result == 'Email'){
                            Ext.MessageBox.alert('Invoice', 'This invoice is enrolled in paperless invoicing.');
                            interPageMove("orderinvoiceemail!email.bc");
                        } else{
                            interPageMove("order!view.bc?id=<s:property value="order.id"/>");
                        }
                    }
                });
            }
        }, '-', {
            text: 'Cancel',
            iconCls: 'delete_icon',
            handler: function(){
                Ext.postWindow.close();
            }
        }],
        bodyStyle:'background-color:#fbfbfb',
        items: [ {
             xtype: 'form',
             layout: 'form',
             labelAlign: 'right',
             bodyBorder:false,
             bodyStyle:'background-color:#fbfbfb;padding:15px;',
             items: [{
                 xtype: 'datefield',
                 id: 'postDateField',
                 name: 'postDateString',
                 fieldLabel: 'Post Date',
                 value: new Date().format('m/d/Y')
             }]
        }]
    });
    Ext.postWindow.show();
}

function setShipDate(){
    Ext.shipDate = new Ext.Window({
        id: 'shipdatewindow',
        title: 'Set Ship Date',
        width:340,
        height:150,
        modal:true,
        stateful:false,
        autoScroll:true,
        layout:'fit',
        bbar:['->',{
            text: 'Set Ship Date',
            iconCls: 'accept_icon',
            handler: function(){
                if (!Ext.getCmp('shipDateField').isValid()){
                    Ext.Msg.alert('Error', 'Ship Date must be a valid date mm/dd/yyyy.');
                    return;
                }
                Ext.form.shipDateForm.submit({
                    params:{'id':<s:property value="order.id"/>, 'shipDateString':Ext.getCmp("shipDateField").getValue().format('m/d/Y')},
                    waitMsg:'Setting Ship Date...',
                    failure: function(form, action){
                        Ext.MessageBox.alert('Status', action.result.error);
                    },
                    success: function(form, action){
                        Ext.shipDate.close();
                        interPageMove("order!view.bc?id=<s:property value="order.id"/>");
                    }
                });
            }
        }, '-', {
            text: 'Cancel',
            iconCls: 'delete_icon',
            handler: function(){
                Ext.shipDate.close();
            }
        }],
        bodyStyle:'background-color:#fbfbfb',
        items: [ {
             xtype: 'form',
             layout: 'form',
             labelAlign: 'right',
             bodyBorder:false,
             bodyStyle:'background-color:#fbfbfb;padding:15px;',
             items: [{
                 xtype: 'datefield',
                 name: 'shipDateString',
                 id: 'shipDateField',
                 fieldLabel: 'Ship Date',
                 value: <s:if test="order.shipDate != null">'<s:date name="order.shipDate" format="MM/dd/yyyy"/>'</s:if><s:else>new Date().format('m/d/Y')</s:else>
             }]
        }]
    });
    Ext.shipDate.show();
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
                             title: "Order Item Detail",
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
                id: 'orderDetailPanel',
                title: 'Order Detail',
                layout   : 'fit',
                collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                border   : false,
                bodyBorder: false,
                height:250,
                minHeight:150,
                split:true,
                autoScroll: true,
                autoLoad: 'order!viewDetail.bc?id=<s:property value="order.id"/>'
                //contentEl: 'orderDetail'
                , tbar: [
                <s:if test="!order.posted && isBcOrderAdmin"> 
                {
                    text: 'Edit Order',
                    iconCls: 'edit_icon', 
                    handler: function(){
                        Ext.Updater.defaults.loadScripts = true;
                        Ext.crudWindow = new Ext.Window({
                            id: 'editwindow',
                            title: 'Edit Order',
                            width:600,
                            height:600,
                            modal:true,
                            stateful:false,
                            autoScroll:true,
                            bbar:[],
                            bodyStyle:'background-color:#fbfbfb',
                            autoLoad: 'order!edit.bc?id=<s:property value="order.id"/>'
                        });
                        Ext.crudWindow.show();
                    }
                }, "-", {
                    text: 'Delete Order',
                    iconCls: 'delete_icon', 
                    handler: function(){
                        Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete the Order: <s:property value="order.invoiceNumber"/>?', function(btn){
                            if (btn == "yes"){
                                Ext.form.deleteForm.submit({
                                    timeout: 600,
                                    params:{'id':<s:property value="order.id"/>},
                                    waitMsg:'Deleting Order...',
                                    failure: function(form, action){
                                        Ext.MessageBox.alert('Status', action.result.error);
                                    },
                                    success: function(form, action){
                                        interPageMove("order!list.bc");
                                    }
                                });
                            }
                        });
                    }
                }, "-", {
                    text: 'History',
                    iconCls: 'calendar_icon',
                    handler: function(){
                        showHistory("customer_order", <s:property value="order.id"/>, "Order Audit History", "customer_order_item", "Order Audit History", "Order Items Audit History");
                    }
                }, "->",{
                    text: 'Set Ship Date',
                    iconCls: 'lorry_icon', 
                    handler: setShipDate
                }, '-', {
                    text: 'Post This Order',
                    iconCls: 'accept_icon', 
                    handler: postOrder
                }</s:if>
                <s:elseif test="isBcOrderAdmin"> 
                {
                    text: 'History',
                    iconCls: 'calendar_icon',
                    handler: function(){
                        showHistory("customer_order", <s:property value="order.id"/>, "Order Audit History", "customer_order_item", "Order Audit History", "Order Items Audit History");
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
    Ext.form.shipDateForm = new Ext.form.BasicForm("shipdateform");
    Ext.form.shipMaxForm = new Ext.form.BasicForm("shipmaxform");
    Ext.form.fixZeroPriceForm = new Ext.form.BasicForm("fixzeropriceform");
    Ext.form.shippedForm = new Ext.form.BasicForm("shippedform");
    
});
</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div style="display:none">

<s:if test="isBcOrderAdmin && order.posted == false">
    <inv:table tableName="orderitems" sortable="true" exportable="true" tableTitle="Order Items"
    dataAction="order!listItemData.bc?id=${id}" table="${listTable}" addToContainer="listpanel" stateful="true"
    statestartid="${id}"
    customRowColors="true" customRowColorsFunction="filledRowColors"
    rowDblClick="itemRowDoubleClick"/>
</s:if><s:else>
    <inv:table tableName="orderitems" sortable="true" exportable="true" tableTitle="Order Items"
    dataAction="order!listItemData.bc?id=${id}" table="${listTable}" addToContainer="listpanel" stateful="true"
    statestartid="${id}"
    customRowColors="true" customRowColorsFunction="filledRowColors"/>
</s:else>

<script language="JavaScript" type="text/javascript">
Ext.onReady(function(){
    Ext.grid.orderitemsGrid.getSelectionModel().addListener("rowselect", function(model, rowindex, rec){
        currentOrderItemSelection = rowindex;
    });
});
</script>

<form action="order!fixZeroPrice.bc" name="fixzeropriceform" id="fixzeropriceform"></form>
<form action="order!shipDate.bc" name="shipdateform" id="shipdateform"></form>
<form action="order!shipMax.bc" name="shipmaxform" id="shipmaxform"></form>
<form action="order!post.bc" name="postform" id="postform"></form>
<form action="order!delete.bc" name="deleteform" id="deleteform"></form>
<form action="orderitem!delete.bc" name="deleteitemform" id="deleteitemform"></form>
<form action="orderitem!shipped.bc" name="shippedform" id="shippedform"></form>

</div>

<script language="JavaScript" type="text/javascript">
Ext.onReady(function(){
    Ext.grid.orderitemsGrid.getSelectionModel().on('selectionchange', function(){
        setTimeout("updateOrderItemDetail()", 300);
    });
    Ext.grid.orderitemsGridDs.on("load", function(store, records, options){
        updateOrderItemDetail();
        if (doEditNextItem != null){
            var selId = doEditNextItem;
            doEditNextItem = null;
            var foundRec;
            Ext.grid.orderitemsGridDs.each(function(rec){
               if (rec.get("id") == selId){
                   foundRec = rec;
                   return false;
               } 
            });
            if (foundRec != undefined){
                var idx = Ext.grid.orderitemsGridDs.indexOf(foundRec);
                Ext.grid.orderitemsGrid.getSelectionModel().selectRow(idx);
                editNextItem();
            }
        } else if (options.params.start != curStart){
            curStart = options.params.start;
        } else if (curSelectedIdx != undefined){
            Ext.grid.orderitemsGrid.getSelectionModel().selectRow(curSelectedIdx);
            Ext.grid.orderitemsGrid.getView().focusRow(curSelectedIdx);
        }
    });
    Ext.grid.orderitemsGrid.getSelectionModel().on("rowselect", function(model, idx, rec){
        curSelectedIdx = idx;
        curSelectedRec = rec;
    });
});
</script>


<%@ include file="../../div-footer.jspf" %>

</body>
</html>
