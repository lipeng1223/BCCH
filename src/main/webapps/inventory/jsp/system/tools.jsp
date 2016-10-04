<%@ include file="../html-start.jspf" %>

<head>
<%@ include file="../html-head.jspf" %>

<script language="JavaScript" type="text/javascript">
Ext.onReady(function(){
    
    <s:set name="activeMenu" value="%{'system'}"/>
    <%@ include file="../div-header-menu.jspf" %>

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
            height:40,
            margins: '0 0 0 0',
            bodyCfg : {style: {'border-bottom':'1px solid #999'} },
            items: [tb]
        },{
            region: 'center',
            border   : false,
            bodyBorder: false,
            type: 'xpanel',
            layout: 'fit',
            id: 'contentpanel',
            autoScroll: true,
            contentEl: 'toolsdiv'
        },{
            region: 'south',
            margins: '0 0 0 0',
            collapsible: false,
            height: 24,
            border: true,
            //bodyCfg : {style: {'background':'#F0F4F5 url(/images/tbbg3.png) repeat-x scroll left top'} },
            bodyCfg : {style: {'background-color':'#ddd'} },
            id: 'footerpanel',
            layout: 'fit',
            contentEl: 'invfooter'
        }]
    });    
    
    new Ext.Button({
        id:'inventoryisbnfixbutton', 
        applyTo:'inventoryfixbutton', 
        cls:"x-btn-text-icon", 
        icon:"/images/cog.png", 
        text:'Inventory Item ISBN Fix', 
        disabled:false,
        handler: function(btn){
            btn.disable();
            Ext.Ajax.request({
                url: 'system!fixInventoryIsbns.bc',
                params: {},
                success: function(response, options){
                    Ext.MessageBox.alert('Success', 'Fixed the Bookcountry Inventory ISBNs');
                    btn.enable();
                },
                failure: function(response, options){
                    Ext.MessageBox.alert('Error', 'There was a system error and the inventory isbns were not fixed.');
                    btn.enable();
                }
            });
        } 
    });
    
    new Ext.Button({
        id:'inventorycountfixbutton', 
        applyTo:'inventorycountbutton', 
        cls:"x-btn-text-icon", 
        icon:"/images/cog.png", 
        text:'Inventory Item Count Fix', 
        disabled:false,
        handler: function(btn){
            btn.disable();
            Ext.Ajax.request({
                url: 'system!fixInventoryCounts.bc',
                params: {},
                success: function(response, options){
                    Ext.MessageBox.alert('Success', 'Fixed the Bookcountry Inventory Counts');
                    btn.enable();
                },
                failure: function(response, options){
                    Ext.MessageBox.alert('Error', 'There was a system error and the inventory counts were not fixed.');
                    btn.enable();
                }
            });
        } 
    });
    
    fixInventoryCounts
    
});
</script>

</head>

<body class="page">

<%@ include file="../div-header.jspf"%>

<div style="display:none;">

<div id="toolsdiv">
    
    <table style="width:300px;height:200px;">
    <tr>
    <td valign="top" align="left">
        <div class="homepanelsearch">
            <div id="inventoryfixbutton"/>
        </div>
    </td>
    </tr>
    <tr><td syle="height:15px;">&nbsp;</td></tr>
    <tr>
    <td valign="top" align="left">
        <div class="homepanelsearch">
            <div id="inventorycountbutton"/>
        </div>
    </td>
    </tr>
    </table>
    
</div>

</div>

<%@ include file="../div-footer.jspf" %>

</body>
</html>

