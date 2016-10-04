<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title><s:property value="order.customer.code"/><s:if test="order.customer.code != null && order.customer.code.length() > 0 && order.customer.companyName != null && order.customer.companyName.length() > 0"> - </s:if><s:property value="order.customer.companyName"/></title>

<jwr:script src="/bundles/bellorderBundle.js"/> 

<s:set name="stateSession" value="%{'bellorder-view-state'}"/>
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
                <s:if test="!order.posted && isBcOrderAdmin"> 
                , tbar: [{
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
    
});
</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div style="display:none">

<form action="order!delete.bc" name="deleteform" id="deleteform"></form>

</div>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
