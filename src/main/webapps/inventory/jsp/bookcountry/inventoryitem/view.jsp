<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Book Country Inventory</title>

<jwr:script src="/bundles/inventoryBundle.js"/> 

<s:set name="stateSession" value="%{'inventory-item-view-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">

function orderOpenButtonClick(){
    window.open("inventoryitem!listOrdersWin.bc?id=${inventoryItem.id}&showNewWindowButton=false");
}
function receivingOpenButtonClick(){
    window.open("inventoryitem!listReceivingsWin.bc?id=${inventoryItem.id}&showNewWindowButton=false");
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
                           title: 'Orders',
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
                        editInventoryItem(<s:property value="inventoryItem.id"/>);
                    }
                }, '-', {
                    text: 'Delete',
                    iconCls: 'delete_icon',
                    handler: function(){
                        deleteInventoryItem(<s:property value="inventoryItem.id"/>, '<s:property value="inventoryItem.isbn"/>', '<s:property value="inventoryItem.cond"/>');
                    }
                }, '-',</s:if> {
                    text: 'History',
                    iconCls: 'calendar_icon',
                    handler: function(){
                        showHistory("inventory_item", <s:property value="inventoryItem.id"/>, "Inventory Item Audit History", null, "Inventory Item Audit History");
                    }
                },'-',{
                    text: 'Print',
                    iconCls: 'print_icon',
                    handler: function(){
                        showPrintInv(<s:property value="inventoryItem.id"/>);
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
                    plugins: [Ext.ux.PanelCollapsedTitle],
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
                           iconCls: 'down_arrow_icon',
                           listeners: {scope: this, 'click': function(){
                                   updateAmazonDetail('<s:property value="inventoryItem.isbn"/>');
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
                                   updateAmazonDetail('<s:property value="inventoryItem.isbn"/>');
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
            <tr><td><div style="height:4px;"></div></td></tr>
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
                <td align="right" nowrap valign="top">Cover:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.cover"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Bell Book:</td>
                <td align="left" style="padding-left:5px;" class="<s:if test="inventoryItem.bellbook">greentext</s:if><s:else>redtext</s:else>" nowrap><s:if test="inventoryItem.bellbook">Yes</s:if><s:else>No</s:else></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Restricted:</td>
                <td align="left" style="padding-left:5px;" class="<s:if test="inventoryItem.restricted">greentext</s:if><s:else>redtext</s:else>" nowrap><s:if test="inventoryItem.restricted">Yes</s:if><s:else>No</s:else></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">HE:</td>
                <td align="left" style="padding-left:5px;" class="<s:if test="inventoryItem.he">greentext</s:if><s:else>redtext</s:else>" nowrap><s:if test="inventoryItem.he">Yes</s:if><s:else>No</s:else></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">List Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventoryItem.formatMoney(inventoryItem.listPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Selling Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventoryItem.formatMoney(inventoryItem.sellingPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Selling Percent list:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventoryItem.formatPercent(inventoryItem.sellPricePercentList / 100F)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <s:if test="isBcInventoryAdmin">
                <tr>
                    <td align="right" nowrap valign="top">Cost:</td>
                    <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventoryItem.formatMoney(inventoryItem.cost)"/></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right" nowrap valign="top">Cost Percent list:</td>
                    <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventoryItem.formatPercent(inventoryItem.costPercentList / 100F)"/></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
            </s:if>
            <tr>
                <td align="right" nowrap valign="top">On Hand:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.onhand" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Available:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.available" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Committed:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.committed" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last Received PO:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.lastpo"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last Received Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="inventoryItem.lastpoDate" format="MM/dd/yyy"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last Received Qty:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.receivedQuantity" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Pre-receiving Qty:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="prereceivingQuantity" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            
            </table>
            </td>
            <td style="padding-left:40px;" valign="top">
            <table>
            
            <tr>
                <td align="right" nowrap valign="top">Number Of Pages:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.numberOfPages"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last Nights Sales Rank:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.salesRank" default="N/A"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last Amazon Update:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:date name="inventoryItem.lastAmazonUpdate" format="MM/dd/yyy hh:mm a"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Title:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.title"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Company Rec:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.companyRec"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Imprint Rec:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.imprintRec"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Publisher:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.companyRec"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Author:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.author"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Publication Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.publishDate"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Length:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.length" default="N/A"/> inches</td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Width:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.width" default="N/A"/> inches</td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Height:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.height" default="N/A"/> inches</td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Weight:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.weight" default="N/A"/> lbs (<s:property value="inventoryItem.weight * 16.0"/> ounces)</td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Bc Category:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.bccategory"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Category 1:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.category1"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Category 2:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.category2"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Category 3:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.category3"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Category 4:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.category1"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            
            </table>
            </td>
            </tr>
    </table>
 
</div>

<inv:table tableName="receivings" sortable="true" exportable="true"
dataAction="inventoryitem!listReceivings.bc?id=${inventoryItem.id}" table="${recListTable}" addToContainer="reclistpanel" stateful="true"
rowDblClick="receivingRowDoubleClick"/>

<inv:table tableName="orders" sortable="true" exportable="true"
dataAction="inventoryitem!listOrders.bc?id=${inventoryItem.id}" table="${orderListTable}" addToContainer="orderlistpanel" stateful="true"
rowDblClick="orderRowDoubleClick"/>


<form action="inventoryitem!delete.bc" name="deleteinventoryform" id="deleteinventoryform"></form>

</div>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
