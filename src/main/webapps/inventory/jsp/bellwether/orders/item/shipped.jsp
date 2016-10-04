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
                
                document.getElementById("bellInventory.isbn").innerHTML = res['bellInventory']['isbn'];
                if (res['bellInventory']['isbn10'] != undefined) document.getElementById("bellInventory.isbn10").innerHTML = res['bellInventory']['isbn10'];
                else document.getElementById("bellInventory.isbn10").innerHTML = "";
                if (res['bellInventory']['isbn13'] != undefined) document.getElementById("bellInventory.isbn13").innerHTML = res['bellInventory']['isbn13'];
                else document.getElementById("bellInventory.isbn13").innerHTML = "";
                if (res['bellInventory']['bin'] != undefined) document.getElementById("bellInventory.bin").innerHTML = res['bellInventory']['bin'];
                else document.getElementById("bellInventory.bin").innerHTML = "";
                if (res['bellInventory']['listPrice'] != undefined) document.getElementById("bellInventory.listPrice").innerHTML = Ext.util.Format.usMoney(res['bellInventory']['listPrice']);
                else document.getElementById("bellInventory.listPrice").innerHTML = "";
                if (res['bellInventory']['sellingPrice'] != undefined) document.getElementById("bellInventory.sellingPrice").innerHTML = Ext.util.Format.usMoney(res['bellInventory']['sellingPrice']);
                else document.getElementById("bellInventory.sellingPrice").innerHTML = "";
                if (res['bellInventory']['committed'] != undefined) document.getElementById("bellInventory.committed").innerHTML = res['bellInventory']['committed'];
                else document.getElementById("bellInventory.committed").innerHTML = "";
                if (res['bellInventory']['available'] != undefined) document.getElementById("bellInventory.available").innerHTML = res['bellInventory']['available'];
                else document.getElementById("bellInventory.available").innerHTML = "";
                if (res['bellInventory']['onhand'] != undefined) document.getElementById("bellInventory.onhand").innerHTML = res['bellInventory']['onhand'];
                else document.getElementById("bellInventory.onhand").innerHTML = "";

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
            Ext.grid.bellorderitemsGridDs.reload();
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
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="bellInventory.isbn"><s:property value="bellInventory.isbn"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">ISBN10:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="bellInventory.isbn10"><s:property value="bellInventory.isbn10"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">ISBN13:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="bellInventory.isbn13"><s:property value="bellInventory.isbn13"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Bin:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="bellInventory.bin"><s:property value="bellInventory.bin"/></div></td>
                </tr>
            </table>
            </td>
            <td valign="top" style="padding-left:40px;">
            <table>
                <tr>
                    <td align="right">List Price:</td>
                    <td align="left" style="padding-left:10px;" class="greentext"><div id="bellInventory.listPrice"><s:property value="formatMoney(bellInventory.listPrice)"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Selling Price:</td>
                    <td align="left" style="padding-left:10px;" class="greentext"><div id="bellInventory.sellingPrice"><s:property value="formatMoney(bellInventory.sellingPrice)"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Committed:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="bellInventory.committed"><s:property value="bellInventory.committed"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Available:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="bellInventory.available" style="font-weight:bold;"><s:property value="bellInventory.available"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">On Hand:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="bellInventory.onhand"><s:property value="bellInventory.onhand"/></div></td>
                </tr>
            </table>
            </td>
            </tr>
        </table>
        
    </fieldset>
        
</form>

