<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

var startOffset = <s:property value="start"/>;
var reloadOnClose = false;

function submit(gotonext){

    var check = trimString(document.getElementById("orderItem.filled").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Shipped must be provided.');
        return;
    }
    if (!checkInt(check, true)) {
        Ext.Msg.alert('Error', 'Shipped must be a number.');
        return;
    }

    startOffset += 1;
    Ext.form.crudForm.submit({
        waitMsg:'Updating Order Item...',
        params: {start: startOffset+currentOrderItemSelection, loadNext:gotonext},
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
            closeWindow();
        },
        success: function(form, action){
            reloadOnClose = true;
            if (gotonext){
                var res = action.result;
                
                document.getElementById("id").value = res['id'];
                document.getElementById("orderItem.filled").value = res['filled'];
                document.getElementById("orderItem.title").innerHTML = res['title'];
                document.getElementById("orderItem.cond").innerHTML = res['cond'];
                document.getElementById("orderItem.isbn").innerHTML = res['isbn'];
                document.getElementById("orderItem.quantity").innerHTML = res['quantity'];
                
                document.getElementById("inventoryItem.isbn").innerHTML = res['inventoryItem']['isbn'];
                if (res['inventoryItem']['isbn10'] != undefined) document.getElementById("inventoryItem.isbn10").innerHTML = res['inventoryItem']['isbn10'];
                else document.getElementById("inventoryItem.isbn10").innerHTML = "";
                if (res['inventoryItem']['isbn13'] != undefined) document.getElementById("inventoryItem.isbn13").innerHTML = res['inventoryItem']['isbn13'];
                else document.getElementById("inventoryItem.isbn13").innerHTML = "";
                if (res['inventoryItem']['bin'] != undefined) document.getElementById("inventoryItem.bin").innerHTML = res['inventoryItem']['bin'];
                else document.getElementById("inventoryItem.bin").innerHTML = "";
                if (res['inventoryItem']['listPrice'] != undefined) document.getElementById("inventoryItem.listPrice").innerHTML = Ext.util.Format.usMoney(res['inventoryItem']['listPrice']);
                else document.getElementById("inventoryItem.listPrice").innerHTML = "";
                if (res['inventoryItem']['sellingPrice'] != undefined) document.getElementById("inventoryItem.sellingPrice").innerHTML = Ext.util.Format.usMoney(res['inventoryItem']['sellingPrice']);
                else document.getElementById("inventoryItem.sellingPrice").innerHTML = "";
                if (res['inventoryItem']['committed'] != undefined) document.getElementById("inventoryItem.committed").innerHTML = res['inventoryItem']['committed'];
                else document.getElementById("inventoryItem.committed").innerHTML = "";
                if (res['inventoryItem']['available'] != undefined) document.getElementById("inventoryItem.available").innerHTML = res['inventoryItem']['available'];
                else document.getElementById("inventoryItem.available").innerHTML = "";
                if (res['inventoryItem']['onhand'] != undefined) document.getElementById("inventoryItem.onhand").innerHTML = res['inventoryItem']['onhand'];
                else document.getElementById("inventoryItem.onhand").innerHTML = "";

                document.getElementById("notEqualDiv").style.display = "none";
                if (res['filled'] < res['quantity']){
                    document.getElementById("notEqualDiv").style.display = "";
                }
                
                setFocus('orderItem.filled');
                document.getElementById("orderItem.filled").select();
            } else {
                Ext.crudWindow.close();
            }
        }
    });
}
function closeWindow(){
    Ext.crudWindow.close();
}

Ext.onReady(function(){
    Ext.form.crudForm = new Ext.form.BasicForm("crudform");

    Ext.crudWindow.addListener("beforeclose", function(p){
        if (reloadOnClose) {
            Ext.getCmp("orderDetailPanel").getUpdater().refresh();
            Ext.grid.orderitemsGridDs.reload();
        }
    });
    
    Ext.crudWindow.getBottomToolbar().addFill();
    Ext.crudWindow.getBottomToolbar().add({
        tabIndex: 3,
        id:'nextButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/arrow_right.png", 
        text:'Update & Go To Next', 
        handler: function(){
            submit(true);
        }, 
        disabled:false
    });
    Ext.crudWindow.getBottomToolbar().addSeparator();
    Ext.crudWindow.getBottomToolbar().add({
        tabIndex: 2,
        id:'submitButton', 
        type: 'submit',
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'Update', 
        handler: function(){
            submit(false);
        }, 
        disabled:false
    });
    Ext.crudWindow.getBottomToolbar().addSeparator();
    Ext.crudWindow.getBottomToolbar().add({
        tabIndex: 4,
        id:'cancelButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/cancel.png", 
        text:'Close', 
        handler:closeWindow, 
        disabled:false
    });

    Ext.get("orderItem.filled").addKeyListener(13, function(){
        submit(true);
    });
    
    setFocus('orderItem.filled');
    document.getElementById("orderItem.filled").select();

    if (<s:property value="orderItem.filled"/> < <s:property value="orderItem.quantity"/>){
        document.getElementById("notEqualDiv").style.display = "";
    }
    
});
</script>

<form action="orderitem!shippedSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="id" id="id"/>
<s:hidden key="sort" id="sort"/>
<s:hidden key="dir" id="dir"/>

    <fieldset>
        <legend>Order Item Information</legend>
        
        <table>
            <tr>
            <td valign="top">
        
                <table>
                    <tr>
                        <td></td>
                        <td align="left" style="padding-left:10px;" colspan="2">
                            <div id="messagediv" style="display:none;"></div>
                        </td>
                    </tr>
                    <tr><td><div style="height:5px;"></div></td></tr>
                    <tr>
                        <td align="right">
                        <span>Condition:</span>
                        </td>
                        <td align="left" style="padding-left:10px;" class="bluetext">
                            <div id="orderItem.cond"><s:property value="orderItem.cond"/></div>
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>ISBN:</span>
                        </td>
                        <td align="left" style="padding-left:10px;" class="bluetext">
                            <div id="orderItem.isbn"><s:property value="orderItem.isbn"/></div>
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Title:</span>
                        </td>
                        <td align="left" style="padding-left:10px;" class="bluetext">
                            <div id="orderItem.title"><s:property value="orderItem.title"/></div>
                        </td>
                    </tr>
                    <tr><td><div style="height:8px;"></div></td></tr>
                    <tr>
                        <td align="right">
                        <span>Quantity:</span>
                        </td>
                        <td align="left" style="padding-left:10px;" class="bluetext">
                            <div id="orderItem.quantity" style="font-weight:bold;"><s:property value="orderItem.quantity"/></div>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <div id="checkquantity"></div>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap>
                        <span><span class="inputrequired">*</span> Shipped:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="orderItem.filled" maxlength="10" id="orderItem.filled" cssClass="text-input"  tabindex="1"/>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <div id="notEqualDiv" style="display:none;font-size:14px;" class="redtext">NOT Equal To Quantity</div>
                        </td>
                    </tr>
                    
            </table>
        </td>
        </tr>
        </table>

    </fieldset>

    <fieldset style="margin-top:10px;">
        <legend>Inventory Item Information</legend>
        
        <table>
            <tr>
            <td valign="top">
            <table>
                <tr>
                    <td align="right">ISBN:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="inventoryItem.isbn"><s:property value="inventoryItem.isbn"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">ISBN10:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="inventoryItem.isbn10"><s:property value="inventoryItem.isbn10"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">ISBN13:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="inventoryItem.isbn13"><s:property value="inventoryItem.isbn13"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Bin:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="inventoryItem.bin"><s:property value="inventoryItem.bin"/></div></td>
                </tr>
            </table>
            </td>
            <td valign="top" style="padding-left:40px;">
            <table>
                <tr>
                    <td align="right">List Price:</td>
                    <td align="left" style="padding-left:10px;" class="greentext"><div id="inventoryItem.listPrice"><s:property value="formatMoney(inventoryItem.listPrice)"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Selling Price:</td>
                    <td align="left" style="padding-left:10px;" class="greentext"><div id="inventoryItem.sellingPrice"><s:property value="formatMoney(inventoryItem.sellingPrice)"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Committed:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="inventoryItem.committed"><s:property value="inventoryItem.committed"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Available:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="inventoryItem.available" style="font-weight:bold;"><s:property value="inventoryItem.available"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">On Hand:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="inventoryItem.onhand"><s:property value="inventoryItem.onhand"/></div></td>
                </tr>
            </table>
            </td>
            </tr>
        </table>
        
    </fieldset>
        
</form>

