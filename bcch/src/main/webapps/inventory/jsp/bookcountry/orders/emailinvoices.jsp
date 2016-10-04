<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Book Country Inventory</title>

<jwr:script src="/bundles/orderBundle.js"/> 

<s:set name="stateSession" value="%{'order-list-state'}"/>
<%@ include file="../../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">

var customers = new Ext.util.MixedCollection();
<s:iterator value="customers" var="cust" status="status">
customers.add(<s:property value="#cust.id"/>, "{display:[<s:iterator value="#cust.customerShippings" var="cs" status="status"><s:if test="#status.index > 0">,</s:if>'<s:property value="escapeJavaScript(#cs.gridDisplayNoQuote)"/>'</s:iterator>], ids:[<s:iterator value="#cust.customerShippings" var="cs" status="status"><s:if test="#status.index > 0">,</s:if>'<s:property value="#cs.id"/>'</s:iterator>], sales:'<s:property value="#cust.salesRep"/>'}");
</s:iterator>

var customerId = null;
var selectedOrders = "";
function customerChange(box){
    customerId = box.value;
    document.getElementById("customerInfoDiv").innerHTML = "";
    document.getElementById("selectedOrdersDiv").innerHTML = "";
    document.getElementById("toAddresses").value = "";
    selectedOrders = "";
    if (customerId > -1) {
        Ext.getCmp('selectOrdersButton').enable();
        Ext.Ajax.request({
            url: 'orderinvoiceemail!getCustomerInfo.bc?customerId='+customerId,
            timeout: 60000,
            success: function(result, request){
                //console.log("success! "+result.responseText);
                document.getElementById("customerInfoDiv").innerHTML = result.responseText;
                document.getElementById("toAddresses").value = result.responseText;
            },
            failure: function(result, request){
            }
        });
        
    } else {
        Ext.getCmp('selectOrdersButton').disable();
    }
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
               type: 'xpanel',
               layout: 'fit',
               id: 'listpanel',
               contentEl: 'emailformdiv'
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

    Ext.form.emailForm = new Ext.form.BasicForm("emailform");
    Ext.form.getOrdersForm = new Ext.form.BasicForm("getorders");
    
    new Ext.Button({
        applyTo:'sendEmailButtonDiv', 
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'Send Invoice Email', 
        disabled:false,
        handler: function(){
            Ext.form.emailForm.submit({
                timeout: 600,
                params:{'orderIds':selectedOrders},
                waitMsg:'Generating Invoices and Sending Email...',
                failure: function(form, action){
                    Ext.MessageBox.alert('Status', action.result.error);
                },
                success: function(form, action){
                    Ext.MessageBox.alert('Success', "Successfully sent the invoice email.");
                }
            });
        } 
    });
    new Ext.Button({
        applyTo:'selectOrdersButtonDiv', 
        cls:"x-btn-text-icon", 
        icon:"/images/table.png", 
        text:'Select Customer Orders', 
        id: 'selectOrdersButton',
        disabled:true,
        handler: function(){
            Ext.Updater.defaults.loadScripts = true;
            Ext.orderSelectWindow = new Ext.Window({
                id: 'orderSelectWindow',
                title: 'Select Orders',
                width:640,
                height:540,
                modal:true,
                stateful:false,
                autoScroll:true,
                bbar:[],
                bodyStyle:'background-color:#fbfbfb',
                autoLoad: 'orderinvoiceemail!getCustomerOrders.bc?customerId='+customerId
            });
            Ext.orderSelectWindow.show();
        } 
    });
    
    
    var quickSearchText = document.getElementById("quickSearchText");
    if (quickSearchText != undefined){
        quickSearchText.focus();
    }
    
});
</script>

</head>
<body class="page">

<div style="display:none">

<form action="orderinvoiceemail!getCustomerOrders.bc" name="getorders" id="getorders"></form>

<div id="emailformdiv" style="margin:8px;margin-left:25px;border: 1px solid #ddd;">

    <form action="orderinvoiceemail!sendInvoiceEmail.bc" name="emailform" id="emailform"  method="POST" enctype="multipart/form-data" class="formular">
        <fieldset>
            <legend>Send Invoice Email</legend>
            
            <table>
                <tr>
                <td align="right">Customer:</td>
                <td style="padding-left:5px;">
                    <s:select name="customerId"
                           id="customerId"
                           list="customers"
                           listKey="id"
                           listValue="%{companyName + ' - ' + code}"
                           headerKey="-1" headerValue="Select A Customer"
                           onchange="customerChange(this, true);"
                    />                
                </td>
                </tr>
                <tr><td style="height:5px;"></td></tr>
                <tr>
                <td align="right">Customer Email:</td>
                <td style="padding-left:5px;"><div id="customerInfoDiv"></div></td>
                </tr>
                <tr><td style="height:5px;"></td></tr>
                <tr>
                <td colspan="2">
                    <fieldset>
                        <legend style="margin:0px !important">Selected Orders</legend>
                        <div id="selectOrdersButtonDiv"></div>
                        <div id="selectedOrdersDiv" style="margin-bottom:10px;">
                        </div>
                    </fieldset>
                </td>
                </tr>
                <tr><td style="height:20px;"></td></tr>
                <tr>
                <td align="right">Invoice Type:</td>
                <td style="padding-left:5px;">
                    <table>
                        <tr><td><input type="radio" name="invoiceType" value="regular" checked id="regular"></td>
                            <td style="padding-left:10px;"><label for="regular">Invoice</label></td></tr>
                        <tr><td><input type="radio" name="invoiceType" value="barcodes" id="barcodes"></td>
                            <td style="padding-left:10px;"><label for="barcodes">Invoice With Barcodes</label></td></tr>
                        <tr><td><input type="radio" name="invoiceType" value="notshipped"id="notshipped"></td>
                            <td style="padding-left:10px;"><label for="notshipped">Sales Order With Just Ordered Quantity</label></td></tr>
                    </table>
                </td>
                </tr>
                <tr>
                <td align="right">Attachment:</td>
                <td style="padding-left:5px;">
                    <select name="attachMode">
                        <option value="3">PDF &amp; Excel sheet</option>
                        <option value="2">PDF only</option>
                        <option value="1">Excel sheet only</option>
                    </select>
                </td>
                </tr>
                <tr><td style="height:5px;"></td></tr>
                <tr>
                <td align="right">To Addresses (comma separated):</td>
                <td style="padding-left:5px;"><s:textfield name="toAddresses" is="toAddresses" cssStyle="width:400px;"/></td>
                </tr>
                <tr><td style="height:5px;"></td></tr>
                <tr>
                <td align="right">CC Addresses (comma separated):</td>
                <td style="padding-left:5px;"><s:textfield name="ccAddresses" cssStyle="width:400px;"/></td>
                </tr>
                <tr><td style="height:5px;"></td></tr>
                <tr>
                <td align="right">BCC Addresses (comma separated):</td>
                <td style="padding-left:5px;"><s:textfield name="bccAddresses" cssStyle="width:400px;"/></td>
                </tr>
                <tr><td style="height:5px;"></td></tr>
                <tr>
                <td align="right">From Address:</td>
                <td style="padding-left:5px;"><s:textfield name="fromAddress" cssStyle="width:400px;" value="info@bookcountryclearinghouse.com"/></td>
                </tr>
                <tr><td style="height:15px;"></td></tr>
                <tr>
                <td align="right">Subject:</td>
                <td style="padding-left:5px;"><s:textfield name="subject" cssStyle="width:400px;"/></td>
                </tr>
                <tr><td style="height:5px;"></td></tr>
                <tr>
                <td align="right">Message Body:</td>
                <td style="padding-left:5px;"><s:textarea name="bodyText" id="bodyText" rows="8" cssStyle="width:400px;"/></td>
                </tr>
            </table>
            
            <div style="height:15px;"></div>
            <div id="sendEmailButtonDiv"></div>
        </fieldset>
    </form>
            
</div>
            
</div>
<%@ include file="../div-header.jspf" %>

<%@ include file="../../div-footer.jspf" %>

</body>
</html>
