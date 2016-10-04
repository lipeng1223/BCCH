<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<style type="text/css">
.x-form-date-trigger {
    margin-left:130px;
}
</style>

<script language="JavaScript" type="text/javascript">

function submit(){
    
    var checkBoxes = document.getElementsByName("orderSelect");
    var selectedText = "";
    selectedOrders = "";
    var selectedCount = 0;
    for (var i = 0; i < checkBoxes.length; i++){
        if (checkBoxes[i].checked){
            selectedCount++;
            if (selectedCount > 1) selectedOrders += ",";
            selectedOrders += checkBoxes[i].value;
            selectedText += "<div>";
            selectedText += document.getElementById("invoiceNumber-"+checkBoxes[i].value).innerHTML;
            selectedText += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
            selectedText += document.getElementById("orderDate-"+checkBoxes[i].value).innerHTML;
            selectedText += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
            selectedText += document.getElementById("posted-"+checkBoxes[i].value).innerHTML;
            selectedText += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
            selectedText += document.getElementById("po-"+checkBoxes[i].value).innerHTML;
            selectedText += "</div>";
        }
    }
    document.getElementById("selectedOrdersDiv").innerHTML = selectedText;
    
    Ext.orderSelectWindow.close();
}
function cancel(){
    Ext.orderSelectWindow.close();
}

Ext.onReady(function(){
    Ext.orderSelectWindow.getBottomToolbar().addFill();
    Ext.orderSelectWindow.getBottomToolbar().add({
        id:'submitButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text: 'Select',
        handler:submit, 
        disabled:false
    });
    Ext.orderSelectWindow.getBottomToolbar().addSeparator();
    Ext.orderSelectWindow.getBottomToolbar().add({
        id:'cancelButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/cancel.png", 
        text:'Cancel', 
        handler:cancel, 
        disabled:false
    });
    
});

    
</script>

<form name="selectform" id="selectform" class="formular">
        
    <fieldset>
        <legend>Select Orders</legend>

        <table>
            <s:iterator value="queryResults.data" id="data" status="resStatus">
            <tr>
                <td align="right">
                    <input type="checkbox" name="orderSelect" id="orderSelect-<s:property value="#data.id" escape="false"/>" value="<s:property value="#data.id" escape="false"/>">
                </td>
                <td>
                    <label for="orderSelect-<s:property value="#data.id" escape="false"/>">
                    <table>
                        <tr>
                            <td align="left" style="padding-left:10px;">
                                <span id="invoiceNumber-<s:property value="#data.id" escape="false"/>"><s:property value="#data.invoiceNumber" escape="false"/></span>
                            </td>
                            <td align="left" style="padding-left:10px;">
                                <span id="orderDate-<s:property value="#data.id" escape="false"/>"><s:date name="#data.orderDate" format="MM/dd/yyyy"/></span>
                            </td>
                            <td align="left" style="padding-left:10px;">
                                <span id="posted-<s:property value="#data.id" escape="false"/>">Posted: <s:property value="#data.posted" escape="false"/></span>
                            </td>
                            <td align="left" style="padding-left:10px;">
                                <span id="po-<s:property value="#data.id" escape="false"/>">PO: <s:property value="#data.poNumber" escape="false"/></span>
                            </td>
                        </tr>
                    </table>
                    </label>
                </td>
            </tr>
            <tr><td><div style="height:5px;"></div></td></tr>
            </s:iterator>
        </table>


    </fieldset>

        
</form>

