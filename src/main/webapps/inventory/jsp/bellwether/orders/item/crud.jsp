<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

var reloadOnClose = false;
function submit(gotonext){

    var check;
    
    <s:if test="orderItem == null">
    check = trimString(document.getElementById("orderItem.isbn").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'ISBN must be provided.');
        return;
    }
    </s:if>
    
    check = trimString(document.getElementById("orderItem.quantity").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Quantity must be provided.');
        return;
    }
    if (!checkInt(check, true)) {
        Ext.Msg.alert('Error', 'Quantity must be a number.');
        return;
    }
    
    check = trimString(document.getElementById("orderItem.price").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Price must be provided.');
        return;
    }
    if (!checkFloat(check)) {
        Ext.Msg.alert('Error', 'Price must be a number.');
        return;
    }
    
    Ext.form.crudForm.submit({
        waitMsg:'<s:if test="orderItem != null">Updating</s:if><s:else>Creating</s:else> Order Item...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            if (gotonext){
                <s:if test="orderItem != null">
                doEditNextItem = Ext.grid.bellorderitemsGrid.getSelectionModel().getSelected().get("id");
                </s:if>
            }
            Ext.grid.bellorderitemsGridDs.reload();
            reloadOnClose = true;
            if (gotonext){
                <s:if test="orderItem != null">
                Ext.crudWindow.close();
                return;
                </s:if>
                blankInventoryInfo();
                
                Ext.get("messagediv").fadeOut({duration:.5});
                
                <s:if test="orderItem == null">
                document.getElementById("orderItem.isbn").value = '';
                </s:if>
                document.getElementById("orderItem.quantity").value = '';
                document.getElementById("orderItem.price").value = '';
                document.getElementById("orderItem.discount").value = '';
                document.getElementById("orderItem.title").value = '';

                <s:if test="orderItem == null">
                    setFocus('orderItem.isbn');
                </s:if>
            } else {
                Ext.crudWindow.close();
            }
        }
    });
}
function cancel(){
    Ext.crudWindow.close();
}

var currentIsbn = '';
function getIsbnInfo(){
    blankInventoryInfo();
    var isbn = trimString(Ext.get("orderItem.isbn").getValue());
    currentIsbn = isbn;
    var msgdiv = document.getElementById("messagediv");
    msgdiv.innerHTML = "Getting Info From Inventory...";
    Ext.get("messagediv").fadeIn({duration:.5});
    document.getElementById("orderItem.title").value = "";
    document.getElementById("orderItem.price").value = "";
    Ext.Ajax.request({
        url: 'inventory!getInfo.bc',
        timeout: 60000,
        params: { isbn:isbn},
        success: function(response, options){
            if (Ext.crudWindow == undefined || !Ext.crudWindow.isVisible()){
                return;
            }
            var json = Ext.decode(response.responseText);
            //Ext.get("messagediv").fadeOut({duration:.5});
            if (json.id != null){
                // found
                msgdiv.innerHTML = isbn+" <span class='greentext'>Found</span> in inventory.";
                if (json.title){
                    document.getElementById("orderItem.title").value = json.title;
                }
                if (json.sellPrice){
                    document.getElementById("orderItem.price").value = json.sellPrice;
                }
                if (json.isbn){
                    document.getElementById("inventory.isbn").innerHTML = json.isbn;
                }
                if (json.isbn13){
                    document.getElementById("inventory.isbn13").innerHTML = json.isbn13;
                }
                if (json.bin){
                    document.getElementById("inventory.bin").innerHTML = json.bin;
                }
                if (json.listPrice){
                    document.getElementById("inventory.listPrice").innerHTML = moneyRenderer(json.listPrice);
                }
                if (json.sellPrice){
                    document.getElementById("inventory.sellPrice").innerHTML = moneyRenderer(json.sellPrice);
                }
                if (json.committed != null){
                    document.getElementById("inventory.committed").innerHTML = json.committed;
                }
                if (json.available != null){
                    document.getElementById("inventory.available").innerHTML = json.available;
                }
                if (json.onhand != null){
                    document.getElementById("inventory.onhand").innerHTML = json.onhand;
                }
            } else {
                // not found
                msgdiv.innerHTML = isbn+" <span class='redtext'>NOT</span> Found in inventory.";
            }
            //Ext.get("messagediv").fadeIn({duration:.5});
        },
        failure: function(response, options){
            // failed to talk
            //Ext.get("messagediv").fadeOut({duration:.5});
            msgdiv.innerHTML = "<span class='redtext'>ERROR</span> Could not talk to inventory, try again.";
            Ext.get("messagediv").fadeIn({duration:.5});
        }
     });        
}

function blankInventoryInfo(){
    document.getElementById("inventory.isbn").innerHTML = "";
    document.getElementById("inventory.isbn13").innerHTML = "";
    document.getElementById("inventory.bin").innerHTML = "";
    document.getElementById("inventory.listPrice").innerHTML = "";
    document.getElementById("inventory.sellPrice").innerHTML = "";
    document.getElementById("inventory.committed").innerHTML = "";
    document.getElementById("inventory.available").innerHTML = "";
    document.getElementById("inventory.onhand").innerHTML = "";
}


Ext.onReady(function(){

    Ext.crudWindow.addListener("beforeclose", function(p){
        if (reloadOnClose) {
            Ext.getCmp("orderDetailPanel").getUpdater().refresh();
        }
    });
    
    Ext.form.crudForm = new Ext.form.BasicForm("crudform");
    Ext.crudWindow.getBottomToolbar().addFill();
    Ext.crudWindow.getBottomToolbar().add({
        id:'submitButton', 
        type: 'submit',
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'<s:if test="orderItem != null">Update</s:if><s:else>Create</s:else>', 
        handler: function(){
            submit(false);
        }, 
        disabled:false
    });
    <s:if test="orderItem == null">
    Ext.crudWindow.getBottomToolbar().addSeparator();
    Ext.crudWindow.getBottomToolbar().add({
        id:'nextButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/arrow_right.png", 
        text:'Create & Go To Next', 
        handler: function(){
            submit(true);
        }, 
        disabled:false
    });
    </s:if><s:else>
    Ext.crudWindow.getBottomToolbar().add({
        id:'nextButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/arrow_right.png", 
        text:'Update & Go To Next', 
        handler: function(){
            submit(true);
        }, 
        disabled:false
    });
    </s:else>
    Ext.crudWindow.getBottomToolbar().addSeparator();
    Ext.crudWindow.getBottomToolbar().add({
        id:'cancelButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/cancel.png", 
        text:'Cancel', 
        handler:cancel, 
        disabled:false
    });

    <s:if test="orderItem == null">
    new Ext.Button({
        applyTo:'getisbninfo', 
        cls:"x-btn-text-icon", 
        icon:"/images/arrow_down.png", 
        text:'Get ISBN Info', 
        disabled:false,
        handler: function(){
            getIsbnInfo();
        } 
    });
    </s:if>
    /*
    new Ext.Button({
        applyTo:'checkquantity', 
        cls:"x-btn-text-icon", 
        icon:"/images/arrow_down.png", 
        text:'Check Quantity', 
        disabled:false,
        handler: function(){
          // TODO
        } 
    });
    new Ext.Button({
        applyTo:'titleinfo', 
        cls:"x-btn-text-icon", 
        icon:"/images/arrow_down.png", 
        text:'Get Info', 
        disabled:false,
        handler: function(){
          // TODO
        } 
    });
    */

    Ext.get("orderItem.quantity").addKeyListener(13, function(){
        submit(<s:if test="orderItem == null">true</s:if><s:else>false</s:else>);
    });
    Ext.get("orderItem.price").addKeyListener(13, function(){
        submit(<s:if test="orderItem == null">true</s:if><s:else>false</s:else>);
    });
    Ext.get("orderItem.discount").addKeyListener(13, function(){
        submit(<s:if test="orderItem == null">true</s:if><s:else>false</s:else>);
    });
    Ext.get("orderItem.title").addKeyListener(13, function(){
        submit(<s:if test="orderItem == null">true</s:if><s:else>false</s:else>);
    });

    
    <s:if test="orderItem == null">
    Ext.get("orderItem.isbn").addKeyListener(13, function(){
        getIsbnInfo();
    });
    Ext.get("orderItem.isbn").addKeyListener(9, function(){
        getIsbnInfo();
    });
    Ext.get("orderItem.isbn").addListener('keyup', function(evt, t, o){
        if (evt.getKey() != 13){
            if (currentIsbn != Ext.get("orderItem.isbn").getValue()){
                Ext.get("messagediv").fadeOut({duration:.5});
                document.getElementById("orderItem.title").value = "";
                document.getElementById("orderItem.price").value = "";
                blankInventoryInfo();
            }
        }
    });
    </s:if>
    
    setFocus('orderItem.isbn');
    
});
</script>

<s:if test="orderItem != null">
<form action="orderitem!editSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="orderItem.id"/>
</s:if><s:else>
<form action="orderitem!createSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="id"/>
</s:else>

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
                    <s:if test="(order != null && order.creditMemo) || orderItem.customerOrder.creditMemo">
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> Credit Type:</span>
                        <s:property value="order.creditMemoType"/>
                        <s:property value="order.creditMemoType.equals('damage')"/>
                        </td>
                        <td align="left" style="padding-left:10px;padding-bottom:10px;" colspan="2">
                            <table>
                            <tr><td><input type="radio" name="orderItem.creditType" id="damagedRadio" value="damage" <s:if test="orderItem.creditDamage">checked</s:if><s:elseif test="orderItem.customerOrder.creditMemoType.equals('damage')">checked</s:elseif><s:elseif test="order.creditMemoType.equals('damage')">checked</s:elseif>/></td><td style="padding-left:10px;"><label for="damagedRadio">Damage <br/>(Qty does not go back into inv, Price will be negative)</label></td></tr>
                            <tr><td><input type="radio" name="orderItem.creditType" id="shortageRadio" value="shortage" <s:if test="orderItem.creditShortage">checked</s:if><s:elseif test="orderItem.customerOrder.creditMemoType.equals('shortage')">checked</s:elseif><s:elseif test="order.creditMemoType.equals('shortage')">checked</s:elseif>/></td><td style="padding-left:10px;"><label for="shortageRadio">Shortage <br/>(Qty added into inv, Price will be negative)</label></td></tr>
                            <tr><td><input type="radio" name="orderItem.creditType" id="recNoBillRadio" value="recNoBill" <s:if test="orderItem.creditRecNoBill">checked</s:if><s:elseif test="orderItem.customerOrder.creditMemoType.equals('recNoBill')">checked</s:elseif><s:elseif test="order.creditMemoType.equals('recNoBill')">checked</s:elseif>/></td><td style="padding-left:10px;"><label for="recNoBillRadio">Received But Not Billed <br/>(Qty reduced from inv, Price will be positive)</label></td></tr>
                            </table>
                        </td>
                    </tr>
                    </s:if>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> ISBN:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:if test="orderItem != null">
                            <s:property value="orderItem.isbn"/>
                        </s:if><s:else>
                            <s:textfield key="orderItem.isbn" maxlength="50" id="orderItem.isbn" cssClass="text-input" tabindex="1"/>
                        </s:else>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <div id="getisbninfo"></div>
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> Quantity:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="orderItem.quantity" maxlength="10" id="orderItem.quantity" cssClass="text-input"  tabindex="3"/>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <div id="checkquantity"></div>
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> Price:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="orderItem.price" maxlength="10" id="orderItem.price" cssClass="text-input"  tabindex="4"/>
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Discount:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="orderItem.discount" maxlength="10" id="orderItem.discount" cssClass="text-input"  tabindex="5"/>
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Title:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="orderItem.title" maxlength="255" id="orderItem.title" cssClass="text-input"  tabindex="6"/>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <div id="titleinfo"></div>
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
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="inventory.isbn"><s:property value="inventory.isbn"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">ISBN13:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="inventory.isbn13"><s:property value="inventory.isbn13"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Bin:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="inventory.bin"><s:property value="inventory.bin"/></div></td>
                </tr>
            </table>
            </td>
            <td valign="top" style="padding-left:40px;">
            <table>
                <tr>
                    <td align="right">List Price:</td>
                    <td align="left" style="padding-left:10px;" class="greentext"><div id="inventory.listPrice"><s:property value="formatMoney(inventory.listPrice)"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Selling Price:</td>
                    <td align="left" style="padding-left:10px;" class="greentext"><div id="inventory.sellPrice"><s:property value="formatMoney(inventory.sellPrice)"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Committed:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="inventory.committed"><s:property value="inventory.committed"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Available:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="inventory.available"><s:property value="inventory.available"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">On Hand:</td>
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="inventory.onhand"><s:property value="inventory.onhand"/></div></td>
                </tr>
            </table>
            </td>
            </tr>
        </table>
        
    </fieldset>
        
</form>

