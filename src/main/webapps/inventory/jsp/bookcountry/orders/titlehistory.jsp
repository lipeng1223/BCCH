<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Book Country Inventory</title>

<jwr:script src="/bundles/orderBundle.js"/> 

<script language="JavaScript" type="text/javascript">
Ext.onReady(function(){

    noselectionhtml = '<div><img src="/images/arrow_left.png" border="0" align="bottom"/>  &nbsp;&nbsp;Click on an order title to view it\'s inventory item detail...</div>';
    
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
               type: 'xpanel',
               layout: 'fit',
               id: 'listpanel'
            },
            {
                region: 'east',
                title: 'Inventory Item Detail',
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
             },
            {
                region: 'north',
                title: 'Customer',
                layout   : 'fit',
                id: 'customerpanel',
                contentEl: 'customerData',
                autoScroll: true,
                collapsible: true, plugins: [Ext.ux.PanelCollapsedTitle], 
                border   : false,
                bodyBorder: false,
                height:200,
                minWidth:100,
                split:true
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
    
});
</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div style="display:none">

<div id="customerData">
    <div style="padding:8px;">
        <table>
            <tr>
                <td align="right" nowrap valign="top">Company Name:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.companyName"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Code:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.code"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Contact Name:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.contactName"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Sales Rep:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.salesRep"/></td>
            </tr>
            <tr><td><div style="height:10px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Picklist Comment:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.picklistComment"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Comment 1:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.comment1"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Comment 2:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.comment2"/></td>
            </tr>
            <tr><td><div style="height:10px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Shipping Addresses (<s:property value="customer.customerShippings.size()" default="0"/>):</td>
            </tr>
            <tr style="padding-left:10px;">
                <td align="left" style="padding-left:5px;" nowrap colspan="2">
                    <s:iterator value="customer.customerShippingsOrdered" var="sa">
                        <table style="margin-top:15px;">
                            <s:if test="#sa.address1 != null && #sa.address1.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Address 1:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.address1"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.address2 != null && #sa.address2.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Address 2:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.address2"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.address3 != null && #sa.address3.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Address 3:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.address3"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.city != null && #sa.city.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">City:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.city"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.state != null && #sa.state.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">State:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.state"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.country != null && #sa.country.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Country:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.country"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.phone != null && #sa.phone.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Phone:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.phone"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.workPhone != null && #sa.workPhone.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Work Phone:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.workPhone"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.homePhone != null && #sa.homePhone.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Home Phone:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.homePhone"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.fax != null && #sa.fax.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Fax:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.fax"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.email != null && #sa.email.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Email:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.email"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.comment != null && #sa.comment.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Comment:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.comment"/></td>
                            </tr>
                            </s:if>
                        </table>
                    </s:iterator>
                </td>
            </tr>
        
        </table>
    </div>
</div>

<inv:table tableName="titles" sortable="true" exportable="true" tableTitle="Title History"
dataAction="history!titleData.bc?id=${id}" table="${listTable}" addToContainer="listpanel" stateful="true"/>

</div>

<script language="JavaScript" type="text/javascript">
    Ext.onReady(function(){
        Ext.grid.titlesGrid.getSelectionModel().on('selectionchange', function(){
            setTimeout("updateTitleInventoryItemDetail()", 300);
        });
        Ext.grid.titlesGridDs.on('load', function(store, records, options){
            updateTitleInventoryItemDetail();
        });
    });
</script>


<%@ include file="../../div-footer.jspf" %>

</body>
</html>
