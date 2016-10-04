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

    <s:if test="orderItem == null">
    Ext.Ajax.request({
        url: 'orderitem!existsOnOrder.bc',
        params: { id:<s:property value="id"/>, "orderItem.isbn": document.getElementById("orderItem.isbn").value, "orderItem.cond": document.getElementById("orderItem.cond").value},
        failure: function(response, options){
            Ext.MessageBox.alert('Error', 'Could not lookup the Back Stock information.');
        },
        success: function(response, options){
            var json = Ext.decode(response.responseText);
            var appendQuantity = false;
            if (json.exists == "true"){
                // it already exists, append or overwrite?
                Ext.MessageBox.show({
                    buttons: {ok:'Append Quantity', cancel:'Overwrite Quantity'},
                    closable: false,
                    modal: true,
                    title: 'ISBN exists on this order',
                    msg: "This ISBN exists already on the order, should this quantity be added to the existing quantity: "+json.existingQuantity+" or overwrite the existing quantity?",
                    icon: Ext.MessageBox.QUESTION,
                    fn: function(buttonId, text, opt){
                        if (buttonId == "ok"){
                            appendQuantity = true;
                        }
                        doTheSubmit(appendQuantity, gotonext);
                    }
                });
            } else {
                doTheSubmit(appendQuantity, gotonext);
            }
        }
    });
    </s:if>
    <s:else>
    doTheSubmit(false, gotonext);
    </s:else>
    
    
}
function cancel(){
    Ext.crudWindow.close();
}

function doTheSubmit(appendQuantity, gotonext){
    // submit
    Ext.form.crudForm.submit({
        params: {appendQuantity: appendQuantity},
        waitMsg:'<s:if test="orderItem != null">Updating</s:if><s:else>Creating</s:else> Order Item...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            if (gotonext){
                <s:if test="orderItem != null">
                doEditNextItem = Ext.grid.orderitemsGrid.getSelectionModel().getSelected().get("id");
                </s:if>
            }
            Ext.grid.orderitemsGridDs.reload();
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

var currentIsbn = '';
function getIsbnInfo(){
    blankInventoryInfo();
    var isbn = trimString(Ext.get("orderItem.isbn").getValue());
    currentIsbn = isbn;
    var cond = Ext.get("orderItem.cond").getValue();
    var msgdiv = document.getElementById("messagediv");
    msgdiv.innerHTML = "Getting Info From Inventory...";
    Ext.get("messagediv").fadeIn({duration:.5});
    document.getElementById("orderItem.title").value = "";
    document.getElementById("orderItem.price").value = "";
    Ext.Ajax.request({
        url: 'inventoryitem!getInfo.bc',
        timeout: 60000,
        params: { isbn:isbn, condition:cond},
        success: function(response, options){
            if (Ext.crudWindow == undefined || !Ext.crudWindow.isVisible()){
                return;
            }
            var json = Ext.decode(response.responseText);
            //Ext.get("messagediv").fadeOut({duration:.5});
            if (json.id != null){
                // found
                msgdiv.innerHTML = cond+" "+isbn+" <span class='greentext'>Found</span> in inventory.";
                if (json.title){
                    document.getElementById("orderItem.title").value = json.title;
                }
                if (json.sellingPrice){
                    document.getElementById("orderItem.price").value = json.sellingPrice;
                }
                if (json.isbn){
                    document.getElementById("inventoryItem.isbn").innerHTML = json.isbn;
                }
                if (json.isbn10){
                    document.getElementById("inventoryItem.isbn10").innerHTML = json.isbn10;
                }
                if (json.isbn13){
                    document.getElementById("inventoryItem.isbn13").innerHTML = json.isbn13;
                }
                if (json.bin){
                    document.getElementById("inventoryItem.bin").innerHTML = json.bin;
                }
                if (json.listPrice){
                    document.getElementById("inventoryItem.listPrice").innerHTML = moneyRenderer(json.listPrice);
                }
                if (json.sellingPrice){
                    document.getElementById("inventoryItem.sellingPrice").innerHTML = moneyRenderer(json.sellingPrice);
                }
                if (json.committed != null){
                    document.getElementById("inventoryItem.committed").innerHTML = json.committed;
                }
                if (json.available != null){
                    document.getElementById("inventoryItem.available").innerHTML = json.available;
                }
                if (json.onhand != null){
                    document.getElementById("inventoryItem.onhand").innerHTML = json.onhand;
                }
            } else {
                // not found
                msgdiv.innerHTML = cond+" "+isbn+" <span class='redtext'>NOT</span> Found in inventory.";
            }
            //Ext.get("messagediv").fadeIn({duration:.5});
        },
        failure: function(response, options){
            console.log(response);
            // failed to talk
            //Ext.get("messagediv").fadeOut({duration:.5});
            msgdiv.innerHTML = "<span class='redtext'>ERROR</span> Could not talk to inventory, try again.";
            Ext.get("messagediv").fadeIn({duration:.5});
        }
     });        
}

function getTitleInfo(){
    blankInventoryInfo();
    var title = trimString(Ext.get("orderItem.title").getValue());
    currentTitle = title;
    var cond = Ext.get("orderItem.cond").getValue();
    var msgdiv = document.getElementById("messagediv");
    msgdiv.innerHTML = "Getting Info From Inventory...";
    Ext.get("messagediv").fadeIn({duration:.5});
    document.getElementById("orderItem.isbn").value = "";
    document.getElementById("orderItem.price").value = "";
    Ext.Ajax.request({
        url: 'inventoryitem!getInfoFromTitle.bc',
        timeout: 60000,
        params: { title:title, condition:cond},
        success: function(response, options){
            if (Ext.crudWindow == undefined || !Ext.crudWindow.isVisible()){
                return;
            }
            var json = Ext.decode(response.responseText);
            //Ext.get("messagediv").fadeOut({duration:.5});
            if (json.id != null){
                // found
                msgdiv.innerHTML = cond+" "+title+" <span class='greentext'>Found</span> in inventory.";
                if (json.title){
                    document.getElementById("orderItem.isbn").value = json.isbn;
                }
                if (json.sellingPrice){
                    document.getElementById("orderItem.price").value = json.sellingPrice;
                }
                if (json.isbn){
                    document.getElementById("inventoryItem.isbn").innerHTML = json.isbn;
                }
                if (json.isbn10){
                    document.getElementById("inventoryItem.isbn10").innerHTML = json.isbn10;
                }
                if (json.isbn13){
                    document.getElementById("inventoryItem.isbn13").innerHTML = json.isbn13;
                }
                if (json.bin){
                    document.getElementById("inventoryItem.bin").innerHTML = json.bin;
                }
                if (json.listPrice){
                    document.getElementById("inventoryItem.listPrice").innerHTML = moneyRenderer(json.listPrice);
                }
                if (json.sellingPrice){
                    document.getElementById("inventoryItem.sellingPrice").innerHTML = moneyRenderer(json.sellingPrice);
                }
                if (json.committed != null){
                    document.getElementById("inventoryItem.committed").innerHTML = json.committed;
                }
                if (json.available != null){
                    document.getElementById("inventoryItem.available").innerHTML = json.available;
                }
                if (json.onhand != null){
                    document.getElementById("inventoryItem.onhand").innerHTML = json.onhand;
                }
            } else {
                // not found
                msgdiv.innerHTML = cond+" "+title+" <span class='redtext'>NOT</span> Found in inventory.";
            }
            //Ext.get("messagediv").fadeIn({duration:.5});
        },
        failure: function(response, options){
            console.log(response);
            // failed to talk
            //Ext.get("messagediv").fadeOut({duration:.5});
            msgdiv.innerHTML = "<span class='redtext'>ERROR</span> Could not talk to inventory, try again.";
            Ext.get("messagediv").fadeIn({duration:.5});
        }
     });        
}

function blankInventoryInfo(){
    document.getElementById("inventoryItem.isbn").innerHTML = "";
    document.getElementById("inventoryItem.isbn10").innerHTML = "";
    document.getElementById("inventoryItem.isbn13").innerHTML = "";
    document.getElementById("inventoryItem.bin").innerHTML = "";
    document.getElementById("inventoryItem.listPrice").innerHTML = "";
    document.getElementById("inventoryItem.sellingPrice").innerHTML = "";
    document.getElementById("inventoryItem.committed").innerHTML = "";
    document.getElementById("inventoryItem.available").innerHTML = "";
    document.getElementById("inventoryItem.onhand").innerHTML = "";
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
    new Ext.Button({
        applyTo:'titleinfo', 
        cls:"x-btn-text-icon", 
        icon:"/images/arrow_down.png", 
        text:'Get Title Info', 
        disabled:false,
        handler: function(){
            getTitleInfo();
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
    
        <s:if test="order.customer != null">
            document.getElementById("orderItem.discount").value = "<s:property value="order.customer.discount"/>";
        </s:if>
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
                        <span><span class="inputrequired">*</span> Condition:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:if test="orderItem != null">
                            <s:property value="orderItem.cond"/>
                        </s:if><s:else>
                             <s:select name="orderItem.cond" list="#{'hurt':'hurt', 'unjacketed':'unjacketed', 'overstock':'overstock'}" id="orderItem.cond"/>
                        </s:else>
                        </td>
                    </tr>
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
                    <td align="left" style="padding-left:10px;" class="bluetext"><div id="inventoryItem.available"><s:property value="inventoryItem.available"/></div></td>
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

