<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<style type="text/css">
.x-form-date-trigger {
    margin-left:130px;
}
</style>

<script language="JavaScript" type="text/javascript">

function submit(){
/*
    var check = trimString(document.getElementById("inventoryItem.isbn").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'ISBN must be provided.');
        return;
    }
    check = trimString(document.getElementById("inventoryItem.listPrice").value);
    if (check != null && check.length > 0){
        if (!checkPositiveFloat(check)){
            Ext.Msg.alert('Error', 'List Price must be a positive numeric value.');
            return;
        }
    }
    check = trimString(document.getElementById("inventoryItem.sellingPrice").value);
    if (check != null && check.length > 0){
        if (!checkPositiveFloat(check)){
            Ext.Msg.alert('Error', 'Selling Price must be a positive numeric value.');
            return;
        }
    }
    check = trimString(document.getElementById("inventoryItem.onhand").value);
    if (check != null && check.length > 0){
        if (!checkInt(check, false)){
            Ext.Msg.alert('Error', 'On Hand must be a positive numeric value.');
            return;
        }
    }
    */
    Ext.form.crudForm.submit({
        waitMsg:'<s:if test="order != null">Updating</s:if><s:else>Creating</s:else> Order...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            Ext.crudWindow.close();
            <s:if test="order != null">
            refreshDetailPanel();
            </s:if><s:else>
            interPageMove("order!view.bc?id="+action.result.id);
            </s:else>
        }
    });
}
function cancel(){
    Ext.crudWindow.close();
}

var cslist = new Array();
<%--
<s:iterator value="customers" var="cust">
cslist['<s:property value="#cust.id"/>'] = new Array();
cslist['<s:property value="#cust.id"/>'][0] = new Array(<s:iterator value="#cust.customerShippings" var="cs" status="status"><s:if test="#status.index > 0">,</s:if>'<s:property value="escapeJavaScript(#cs.gridDisplay)"/>'</s:iterator>);
cslist['<s:property value="#cust.id"/>'][1] = new Array(<s:iterator value="#cust.customerShippings" var="cs" status="status"><s:if test="#status.index > 0">,</s:if>'<s:property value="#cs.id"/>'</s:iterator>);
cslist['<s:property value="#cust.id"/>'][2] = new Array('<s:property value="#cust.salesRep"/>');
</s:iterator>
--%>
var customers = new Ext.util.MixedCollection();
<s:iterator value="customers" var="cust" status="status">
    customers.add(<s:property value="#cust.id"/>, "{display:[<s:iterator value="#cust.customerShippings" var="cs" status="status"><s:if test="#status.index > 0">,</s:if>'<s:property value="escapeJavaScript(#cs.gridDisplayNoQuote)"/>'</s:iterator>], ids:[<s:iterator value="#cust.customerShippings" var="cs" status="status"><s:if test="#status.index > 0">,</s:if>'<s:property value="#cs.id"/>'</s:iterator>], defaults:[<s:iterator value="#cust.customerShippings" var="cs" status="status"><s:if test="#status.index > 0">,</s:if>'<s:property value="#cs.defaultShip"/>'</s:iterator>], deleted:[<s:iterator value="#cust.customerShippings" var="cs" status="status"><s:if test="#status.index > 0">,</s:if>'<s:property value="#cs.deleted"/>'</s:iterator>], sales:'<s:property value="#cust.salesRep"/>'}");
</s:iterator>


Ext.onReady(function(){
    Ext.form.crudForm = new Ext.form.BasicForm("crudform");
    Ext.crudWindow.getBottomToolbar().addFill();
    Ext.crudWindow.getBottomToolbar().add({
        id:'submitButton', 
        type: 'submit',
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:<s:if test="order != null">'Update'</s:if><s:else>'Create'</s:else>, 
        handler:submit, 
        disabled:false
    });
    Ext.crudWindow.getBottomToolbar().addSeparator();
    Ext.crudWindow.getBottomToolbar().add({
        id:'cancelButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/cancel.png", 
        text:'Cancel', 
        handler:cancel, 
        disabled:false
    });

    var now = new Date();
    var nowFormatted = now.format('m/d/Y');
    var df = new Ext.form.DateField({
        hideLabel: true,
        name: 'orderDateString',
        width:150,
        renderTo:'orderdatediv',
        allowBlank: true,
        fieldClass: Ext.isIE ? 'x-form-field inlinedisplay' : '',
        <s:if test="order != null">value:'<s:date name="order.orderDate" format="MM/dd/yyyy" />',</s:if><s:else>value: nowFormatted,</s:else>
        style: 'margin-top:4px;margin-bottom:6px;'
    });
    
    <s:if test="order != null">
        customerChange(document.getElementById("customerId"), false);
        var val = '<s:property value="order.customerShipping.id"/>';
        if (val != null && val.length > 0) {
            var box = document.getElementById("customerShippingId");
            for (var i =0; i < box.options.length; i++){
                if (box.options[i].value == val){
                    box.selectedIndex = i;
                    break;
                }
            }
        }
    </s:if>
    
    new Ext.KeyNav(Ext.form.crudForm.getEl(), {
        'enter': function(e) {
            submit();
        },
        'scope': Ext.form.crudForm
    });
    
    
});

function customerChange(box, updateSalesman){

    emptyList( box.form.customerShippingId );
    var json = customers.get(box.options[box.selectedIndex].value);
    if (json != undefined){
            json = Ext.decode(json);
            if (updateSalesman != undefined && updateSalesman) document.getElementById("order.salesman").value = json.sales;
            fillList( box.form.customerShippingId, json.display, json.ids, json.defaults, json.deleted );
    }
}

// This function goes through the options for the given
// drop down box and removes them in preparation for
// a new set of values

function emptyList( box ) {
    // Set each option to null thus removing it
    while ( box.options.length ) box.options[0] = null;
}

// This function assigns new drop down options to the given
// drop down box from the list of lists specified

function fillList( box, disp, ids, defaults, deleted ) {

    var defaulted = false;
    for ( i = 0; i < disp.length; i++ ) {
        if (deleted[i] == "true"){
            continue;
        }
        // Create a new drop down option with the
        // display text and value from arr

        option = new Option( disp[i], ids[i] );
        
        if (defaults[i] == "true"){
            option.defaultSelected = true;
            option.selected = true;
            defaulted = true;
        }

        // Add to the end of the existing options

        box.options[box.length] = option;
    }

    // Preselect option 0 if needed
    if (!defaulted){
        box.selectedIndex=0;
    }
}

    
</script>

<s:if test="order != null">
<form action="order!editSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="order.id"/>
</s:if><s:else>
<form action="order!createSubmit.bc" name="crudform" id="crudform" class="formular">
</s:else>
        
            <fieldset>
                <legend><s:if test="order != null">Edit</s:if><s:else>New</s:else> Order Information</legend>
    
            <table>
                <tr><td><div style="height:5px;"></div></td></tr>
                <s:if test="order != null">
                    <tr>
                        <td align="right">
                        <span>Invoice Number:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:property value="order.invoiceNumber"/>
                        </td>
                </tr>
                <tr><td><div style="height:5px;"></div></td></tr>
                </s:if>
                <tr>
                    <td align="right">
                    <span>Credit Memo:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:if test="order == null">
                    <s:checkbox key="order.creditMemo" id="order.creditMemo" cssClass="checkbox"/>
                    </s:if><s:else>
                    <s:if test="order.creditMemo">Yes</s:if><s:else>No</s:else>
                    </s:else>
                    </td>
                </tr>
                <tr><td><div style="height:5px;"></div></td></tr>
                <tr>
                    <td align="right">
                    <span>Credit Memo Type:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                        <s:select name="order.creditMemoType" list="#{'damage':'Damage (Qty does not go back into inv, Price will be negative)', 'shortage':'Shortage (Qty added into inv, Price will be negative)', 'recNoBill':'Received But Not Billed (Qty reduced from inv, Price will be positive)'}"
                                  value="order.creditMemoType" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;"/>
                    </td>
                </tr>
                <tr><td><div style="height:5px;"></div></td></tr>
                <tr>
                    <td align="right">
                    <span>Customer:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                        <s:select name="customerId"
                               id="customerId"
                               list="customers"
                               listKey="id"
                               listValue="%{companyName + ' - ' + code}"
                               headerKey="-1" headerValue="Select A Customer"
                               value="order.customer.id"
                               onchange="customerChange(this, true);"
                                style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;"
                        />
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Customer Shipping:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <select name="customerShippingId" id="customerShippingId" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;"></select>
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Customer PO Number:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:textfield key="order.poNumber" maxlength="50" id="order.poNumber" cssClass="text-input" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;" />
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Sales Rep:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:if test="isBcSalesRepAdmin">
                    <s:textfield key="order.salesman" maxlength="50" id="order.salesman" cssClass="text-input" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;" />
                    </s:if><s:else>
                    <s:textfield key="order.salesman" maxlength="50" id="order.salesman" cssClass="text-input" readonly="true" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;" />
                    </s:else>
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Picklist Comment:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:textfield key="order.comment2" maxlength="255" id="order.comment2" cssClass="text-input" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;" />
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Comment:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:textfield key="order.comment" maxlength="255" id="order.comment" cssClass="text-input" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;" />
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Ship Via:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:textfield key="order.shipVia" maxlength="100" id="order.shipVia" cssClass="text-input" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;" />
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Shipping Charges:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:textfield key="order.shippingCharges" maxlength="12" id="order.shippingCharges" cssClass="text-input" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;" />
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Pallete Charge:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:textfield key="order.palleteCharge" maxlength="12" id="order.palleteCharge" cssClass="text-input" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;" />
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Deposit Ammount:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:textfield key="order.depositAmmount" maxlength="12" id="order.depositAmmount" cssClass="text-input" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;" />
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Order Date:</span>
                    </td>
                    <td style="padding-left:10px;">
                        <div id="orderdatediv"></div>
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Customer Visit:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:checkbox key="order.customerVisit" id="order.customerVisit" cssClass="checkbox" />
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Picker 1:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:textfield key="order.picker1" maxlength="2" id="order.picker1" cssClass="text-input" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;" />
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Picker 2:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:textfield key="order.picker2" maxlength="2" id="order.picker2" cssClass="text-input" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;" />
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Quality Control:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:textfield key="order.qualityControl" maxlength="2" id="order.qualityControl" cssClass="text-input" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;" />
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Status:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                     <s:select name="order.status" list="#{'':'', 'Picking':'Picking', 'QC':'QC', 'Invoicing':'Invoicing', 'Processing':'Processing', 'Returned to Accounting':'Returned to Accounting', 'Holding for Payment':'Holding for Payment', 'Return to Stock':'Return to Stock', 'Routed':'Routed', 'Hold':'Hold', 'Printed':'Printed'}" id="order.status" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;" />
                    </td>
                </tr>
                <tr>
                    <td align="right">
                    <span>Debit Memo:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:if test="order == null">
                    <s:checkbox key="order.debitMemo" id="order.debitMemo" cssClass="checkbox"/>
                    </s:if><s:else>
                    <s:if test="order.debitMemo">Yes</s:if><s:else>No</s:else>
                    </s:else>
                    </td>
                </tr>
                <tr><td><div style="height:5px;"></div></td></tr>
                <tr>
                    <td align="right">
                    <span>Debit Memo Type:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                        <s:select name="order.debitMemoType" list="#{'recNoInv':'Received books not invoiced (The price would be a positive and it would decrease inventory)', 'billToLow':'Billed price was to low (Price would be a positve but would NOT reduce inventory)'}"
                                  value="order.debitMemoType" style="width: 100%; padding: 0; padding-top: 3px; padding-bottom: 3px; margin-bottom: 3px;"/>
                    </td>
                </tr>
        </table>
        
        
        </fieldset>

        
</form>

