<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Book Country Inventory</title>

<jwr:script src="/bundles/inventoryBundle.js"/> 

<s:set name="stateSession" value="%{'inventory-item-view-recs-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">

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
               collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
               title: 'Item Receivings',
               type: 'xpanel',
               layout: 'fit',
               iconCls: 'lorry_icon',
               id: 'reclistpanel'
            },
            {
                region: 'north',
                title: 'Inventory Item Detail',
                layout   : 'border',
                border   : false,
                bodyBorder: false,
                height:100,
                split:true,
                autoScroll: false,
                items: [{
                    region: 'center',
                    layout: 'fit',
                    autoScroll: true,
                    contentEl: 'itemDetail'
                }]
             }
        ]
    });


    var vp = new Ext.Viewport({
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

});
</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div style="display:none">

<div id="itemDetail" style="margin:8px;margin-left:25px;">

        <table>
            <tr>
            <td valign="top">
            <table>
        
            <tr>
                <td align="right" nowrap valign="top">Bin:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.bin"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.isbn"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Condition:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.cond"/></td>
            </tr>
            
            </table>
            </td>
            <td style="padding-left:40px;" valign="top">
            <table>
                
            <tr>
                <td align="right" nowrap valign="top">ISBN 10:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.isbn10"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN 13:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.isbn13"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Title:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.title"/></td>
            </tr>
            
            </table>
            </td>
            </tr>
    </table>
 
</div>

<inv:table tableName="receivings" sortable="true" exportable="true"
dataAction="inventoryitem!listReceivings.bc?id=${inventoryItem.id}&showNewWindowButton=false" table="${recListTable}" addToContainer="reclistpanel" stateful="true"
rowDblClick="receivingRowDoubleClick"/>

</div>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
