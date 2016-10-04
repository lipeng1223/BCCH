<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Bellwether Inventory</title>

<jwr:script src="/bundles/bellInventoryBundle.js"/> 

<s:set name="stateSession" value="%{'bell-inventory-item-view-state'}"/>
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
               region: 'south',
               border   : false,
               bodyBorder: false,
               split:true,
               collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
               
               title: 'Item Orders And Receivings',
               type: 'xpanel',
               layout: 'fit',
               height: 0.5 * Ext.lib.Dom.getViewHeight(), // starts it at a percentage,
               items : [
                   {
                       xtype: 'tabpanel',
                       activeTab: 0,
                       items: [
                        {
                           title: 'Amazon Orders',
                           iconCls: 'basket_icon',
                           type: 'xpanel',
                           layout: 'fit',
                           id: 'amzorderlistpanel'
                        },
                        {
                           title: 'Internal Orders',
                           iconCls: 'basket_icon',
                           type: 'xpanel',
                           layout: 'fit',
                           id: 'orderlistpanel'
                         },
                        {
                            title: 'Receivings',
                            iconCls: 'lorry_icon',
                            type: 'xpanel',
                            layout: 'fit',
                            id: 'reclistpanel'
                          }
                           
                       ]
                   }
                ]
            },
            {
                region: 'center',
                title: 'Inventory Item Detail',
                layout   : 'border',
                border   : false,
                bodyBorder: false,
                split:true,
                autoScroll: false,
                tbar: [<s:if test="isBcInventoryAdmin"> {
                    text: 'Edit',
                    iconCls: 'edit_icon',
                    handler: function(){
                        editInventoryItem(<s:property value="inventory.id"/>);
                    }
                }, '-', {
                    text: 'Delete',
                    iconCls: 'delete_icon',
                    handler: function(){
                        deleteInventoryItem(<s:property value="inventory.id"/>, '<s:property value="inventory.isbn"/>');
                    }
                }, '-',</s:if> {
                    text: 'History',
                    iconCls: 'calendar_icon',
                    handler: function(){
                        showInventoryHistory(<s:property value="inventory.id"/>);
                    }
                }, '-', {
                    text: 'Print',
                    iconCls: 'print_icon',
                    handler: function(){
                        showPrintBell(<s:property value="inventory.id"/>);
                    }
                }],
                items: [{
                    region: 'center',
                    layout: 'fit',
                    autoScroll: true,
                    contentEl: 'itemDetail'
                }, {    
                    region: 'east',
                    collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                    split:true,
                    border   : false,
                    bodyBorder: false,
                    title: 'Amazon Detail',
                    iconCls: 'amazon_icon',
                    width:400,
                    minWidth: 100,
                    autoScroll: true,
                    type: 'xpanel',
                    layout: 'fit',
                    bodyCfg : {style: {'background':'#fff'}},
                    id: 'amazonpanel',
                    html: clickloadhtml,
                    tbar: [
                        {
                            text: 'Load',
                            id: 'loadAmazonDataButton',
                            tooltip: 'Load Amazon Data',
                            hidden: false,
                            disabled: false,
                            iconCls: 'down_arrow_icon',
                            listeners: {scope: this, 'click': function(){
                                    updateAmazonDetail('<s:property value="inventory.isbn"/>');
                                }
                            }
                        }, '-',
                       {
                           text: 'Refresh',
                           id: 'refreshAmazonDataButton',
                           tooltip: 'Refresh Amazon Data',
                           hidden: false,
                           iconCls: 'refresh_icon',
                           listeners: {scope: this, 'click': function(){
                                   updateAmazonDetail('<s:property value="inventory.isbn"/>');
                               }
                           }
                       }
                    ]
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
            height:40,
            margins: '0 0 0 0',
            cls: 'invtoolbar',
            bodyCfg : {style: {'border-bottom':'1px solid #999'} },
            items: [tb]
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

    Ext.form.deleteInventoryForm = new Ext.form.BasicForm("deleteinventoryform");
    

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
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.bin"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.isbn"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN 13:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.isbn13"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Cover:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.cover"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">List Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventory.formatMoney(inventory.listPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Selling Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventory.formatMoney(inventory.sellPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Selling Percent list:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventory.formatPercent(inventory.sellPricePercentList / 100F)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <s:if test="isBcInventoryAdmin">
                <tr>
                    <td align="right" nowrap valign="top">Cost:</td>
                    <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventory.formatMoney(inventory.cost)"/></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right" nowrap valign="top">Cost Percent list:</td>
                    <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventory.formatPercent(inventory.costPercentList / 100F)"/></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
            </s:if>
            <tr>
                <td align="right" nowrap valign="top">On Hand:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.onhand" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Available:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.available" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Committed:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.committed" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Listed:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.listed" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last List Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="inventory.lastListDate" format="MM/dd/yyy hh:mm a"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last Received Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="inventory.lastRecDate" format="MM/dd/yyy hh:mm a"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last Received Qty:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.lastRecQuantity" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Received Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext"><s:property value="inventory.formatMoney(inventory.receivedPrice)" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            
            </table>
            </td>
            <td style="padding-left:40px;" valign="top">
            <table>
            
            <tr>
                <td align="right" nowrap valign="top">Sales Rank:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.salesRank" default="N/A"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last Amazon Update:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:date name="inventory.lastAmzCheck" format="MM/dd/yyy hh:mm a"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Title:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.title"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Publisher:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.publisher"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Author:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.author"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Category:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.category"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Bell Comment:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.bellcomment"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            
            </table>
            </td>
            </tr>
    </table>
 
</div>

<inv:table tableName="bellreceivings" sortable="true" exportable="true"
dataAction="inventory!listReceivings.bc?id=${id}" table="${recListTable}" addToContainer="reclistpanel" stateful="true"
rowDblClick="receivingRowDoubleClick"/>

<inv:table tableName="bellorders" sortable="true" exportable="true"
dataAction="inventory!listOrders.bc?id=${id}" table="${orderListTable}" addToContainer="orderlistpanel" stateful="true"
rowDblClick="orderRowDoubleClick"/>

<inv:table tableName="bellamazonorders" sortable="true" exportable="true"
dataAction="inventory!listAmazonOrders.bc?id=${id}" table="${amzOrderListTable}" addToContainer="amzorderlistpanel" stateful="true"/>

<form action="inventory!delete.bc" name="deleteinventoryform" id="deleteinventoryform"></form>

</div>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
