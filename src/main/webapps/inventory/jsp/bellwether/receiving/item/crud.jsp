<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

function costFromLbsCalc(){
  var cplb = document.getElementById("bellReceivedItem.costPerLb").value;
  var aw = document.getElementById("amazonWeightSpan");
  try {
      if (aw != undefined){
          var w = parseFloat(aw.innerHTML);
          var c = parseFloat(cplb);
          if (w != undefined && c != undefined){
              document.getElementById("bellReceivedItem.cost").value = (Math.round(c*w*100.0))/100.0;
          }
      } else {
          Ext.Msg.alert('Error', 'Make sure you click on the Get ISBN Info button to load the current amazon weight.');
      }
  } catch (err){
      Ext.Msg.alert('Error', 'Make sure you click on the Get ISBN Info button to load the current amazon weight and have a Cost Per Lb defined.');
  }
}


function typeChange(){
    var type = document.getElementById("bellReceivedItem.skid");
    var val = type.options[type.selectedIndex].value;
    var lbs = document.getElementById("skidLbsCheck").checked;
    if (val == 'true'){
        // skid
        document.getElementById("bellReceivedItem.isbn").value = "";
        document.getElementById("skidTypeRow").style.display = "";
        document.getElementById("perSkidCostRow").style.display = "";
        document.getElementById("skidLbsCheckRow").style.display = "";
        
        document.getElementById("skidCountRow").style.display = "none";
        document.getElementById("skidCostRow").style.display = "none";
        document.getElementById("skidPriceRow").style.display = "none";
        document.getElementById("skidLbsRow").style.display = "none";
        document.getElementById("skidLbsCostRow").style.display = "none";
        document.getElementById("skidLbsPriceRow").style.display = "none";        
        if (lbs){
            document.getElementById("skidLbsRow").style.display = "";
            document.getElementById("skidLbsCostRow").style.display = "";
            document.getElementById("skidLbsPriceRow").style.display = "";
        } else {
            document.getElementById("skidCountRow").style.display = "";
            document.getElementById("skidCostRow").style.display = "";
            document.getElementById("skidPriceRow").style.display = "";
        }
    } else {
        // pieces
        document.getElementById("bellReceivedItem.isbn").value = "";
        document.getElementById("skidLbsCheckRow").style.display = "none";
        document.getElementById("skidTypeRow").style.display = "none";
        document.getElementById("perSkidCostRow").style.display = "none";
        document.getElementById("skidCountRow").style.display = "none";
        document.getElementById("skidCostRow").style.display = "none";
        document.getElementById("skidPriceRow").style.display = "none";
        document.getElementById("skidLbsRow").style.display = "none";
        document.getElementById("skidLbsCostRow").style.display = "none";
        document.getElementById("skidLbsPriceRow").style.display = "none";        
    } 
}

function submit(gotonext){

    var check;
    
    <s:if test="receivedItem == null">
    check = trimString(document.getElementById("bellReceivedItem.isbn").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'ISBN must be provided.');
        return;
    }
    </s:if>
    
    check = trimString(document.getElementById("bellReceivedItem.quantity").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Quantity must be provided.');
        return;
    }
    if (!checkInt(check, true)) {
        Ext.Msg.alert('Error', 'Quantity must be a number.');
        return;
    }
    
    Ext.form.crudForm.submit({
        waitMsg:'<s:if test="bellReceivedItem != null">Updating</s:if><s:else>Creating</s:else> Received Item...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            if (gotonext){
                <s:if test="bellReceivedItem != null">
                doEditNextItem = Ext.grid.bellreceivingitemsGrid.getSelectionModel().getSelected().get("id");
                </s:if>
            }
            Ext.grid.bellreceivingitemsGridDs.reload();
            if (gotonext){
                <s:if test="bellReceivedItem != null">
                Ext.crudWindow.close();
                return;
                </s:if>
                blankInventoryInfo();
                
                Ext.get("messagediv").fadeOut({duration:.5});
                
                <s:if test="bellReceivedItem == null">
                document.getElementById("bellReceivedItem.isbn").value = '';
                </s:if>
                blankReceivedInfo();

                <s:if test="bellReceivedItem == null">
                    setFocus('bellReceivedItem.isbn');
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
    blankReceivedInfo();
    var isbn = trimString(Ext.get("bellReceivedItem.isbn").getValue());
    currentIsbn = isbn;
    var msgdiv = document.getElementById("messagediv");
    msgdiv.innerHTML = "Getting Info From Inventory...";
    Ext.get("messagediv").fadeIn({duration:.5});
    Ext.Ajax.request({
        url: 'inventory!getInfo.bc',
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
                    document.getElementById("bellReceivedItem.title").value = json.title;
                }
                if (json.sellPrice){
                    document.getElementById("bellReceivedItem.sellPrice").value = json.sellPrice;
                    document.getElementById("bellInventory.sellPrice").innerHTML = moneyRenderer(json.sellPrice);
                }
                if (json.listPrice){
                    document.getElementById("bellReceivedItem.listPrice").value = json.listPrice;
                    document.getElementById("bellInventory.listPrice").innerHTML = moneyRenderer(json.listPrice);
                }
                if (json.isbn){
                    document.getElementById("bellInventory.isbn").innerHTML = json.isbn;
                }
                if (json.isbn13){
                    document.getElementById("bellInventory.isbn13").innerHTML = json.isbn13;
                }
                if (json.bin){
                    document.getElementById("bellInventory.bin").innerHTML = json.bin;
                    document.getElementById("bellReceivedItem.bin").value = json.bin;
                }
                if (json.committed != null){
                    document.getElementById("bellInventory.committed").innerHTML = json.committed;
                }
                if (json.available != null){
                    document.getElementById("bellInventory.available").innerHTML = json.available;
                }
                if (json.onhand != null){
                    document.getElementById("bellInventory.onhand").innerHTML = json.onhand;
                }
                if (json.cover != null){
                    document.getElementById("bellInventory.cover").innerHTML = json.cover;
                    var ddbox = document.getElementById("bellReceivedItem.coverType");
                    for (var i=0; i < ddbox.options.length; i++) {
                        if (ddbox.options[i].value == json.cover) {
                            ddbox.options[i].selected = true;
                        }
                    }
                }
                if (json.receivedPrice != null){
                    document.getElementById("bellReceivedItem.cost").value = json.receivedPrice;
                    document.getElementById("bellInventory.cost").innerHTML = moneyRenderer(json.receivedPrice);
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

    loadAmazonDetail(isbn);
}

function loadAmazonDetail(isbn){
    Ext.Ajax.request({
        url: 'inventory!amazonDetail.bc',
        params: { isbn:isbn },
        success: function(response, options){
            if (Ext.crudWindow == undefined || !Ext.crudWindow.isVisible()){
                return;
            }
            document.getElementById("amazonItemDiv").innerHTML = response.responseText;
        },
        failure: function(response, options){
        }
     });        
}

function blankInventoryInfo(){
    document.getElementById("amazonItemDiv").innerHTML = "";
    document.getElementById("bellInventory.isbn").innerHTML = "";
    document.getElementById("bellInventory.isbn13").innerHTML = "";
    document.getElementById("bellInventory.bin").innerHTML = "";
    document.getElementById("bellInventory.listPrice").innerHTML = "";
    document.getElementById("bellInventory.committed").innerHTML = "";
    document.getElementById("bellInventory.available").innerHTML = "";
    document.getElementById("bellInventory.onhand").innerHTML = "";
    document.getElementById("bellInventory.sellPrice").innerHTML = "";
    document.getElementById("bellInventory.cost").innerHTML = "";
    document.getElementById("bellInventory.cover").innerHTML = "";
}

function blankReceivedInfo(){
    document.getElementById("bellReceivedItem.title").value = "";
    document.getElementById("bellReceivedItem.listPrice").value = "";
    document.getElementById("bellReceivedItem.sellPrice").value = "";
    document.getElementById("bellReceivedItem.coverType").options[0].selected = true;
}


Ext.onReady(function(){
    Ext.form.crudForm = new Ext.form.BasicForm("crudform");
    Ext.crudWindow.getBottomToolbar().addFill();
    Ext.crudWindow.getBottomToolbar().add({
        id:'submitButton', 
        type: 'submit',
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'<s:if test="bellReceivedItem != null">Update</s:if><s:else>Create</s:else>', 
        handler: function(){
            submit(false);
        }, 
        disabled:false
    });
    <s:if test="bellReceivedItem == null">
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

    <s:if test="bellReceivedItem == null">
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
    
    new Ext.Button({
        applyTo:'costPerLbBtn', 
        cls:"x-btn-text-icon", 
        icon:"/images/cog.png", 
        text:'',
        tooltip:'Calculate the Cost from Lbs', 
        disabled:false,
        handler: function(){
          costFromLbsCalc();
        } 
    });

    <s:if test="bellReceivedItem == null">
    Ext.get("bellReceivedItem.isbn").addKeyListener(13, function(){
        getIsbnInfo();
    });
    Ext.get("bellReceivedItem.isbn").addListener('keyup', function(evt, t, o){
        if (evt.getKey() != 13){
            if (currentIsbn != Ext.get("bellReceivedItem.isbn").getValue()){
                Ext.get("messagediv").fadeOut({duration:.5});
                blankReceivedInfo();
                blankInventoryInfo();
            }
        }
    });
    
    /*
    new Ext.KeyNav(Ext.form.crudForm.getEl(), {
        'enter': function(e) {
            submit();
        },
        'scope': Ext.form.crudForm
    });
    */
    
    setFocus('bellReceivedItem.isbn');

    </s:if><s:else>
    loadAmazonDetail("<s:property value="bellReceivedItem.isbn"/>");
    </s:else>
});
</script>

<s:if test="bellReceivedItem != null">
<form action="receivingitem!editSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="bellReceivedItem.id"/>
</s:if><s:else>
<form action="receivingitem!createSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="id"/>
</s:else>

    <table>
    <tr>
    <td valign="top">
    <fieldset>
        <legend>Received Item Information</legend>
        
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
                        <td align="right" nowrap="nowrap">
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:if test="receivedItem != null">
                            <s:if test="bellReceivedItem.skid == true">
                            Skid
                            </s:if><s:else>
                            Pieces
                            </s:else>
                        </s:if><s:else>
                             <s:select name="bellReceivedItem.skid" list="#{'false':'Pieces', 'true':'Skid'}" id="bellReceivedItem.skid" onchange="typeChange();"/>
                        </s:else>
                        </td>
                    </tr>
                    <tr id="skidLbsCheckRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Lbs:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:checkbox name="skidLbs" id="skidLbsCheck" cssClass="checkbox" onchange="typeChange();"/>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span><span class="inputrequired">*</span> ISBN:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:if test="receivedItem != null">
                            <s:property value="bellReceivedItem.isbn"/>
                        </s:if><s:else>
                            <s:textfield key="bellReceivedItem.isbn" maxlength="50" id="bellReceivedItem.isbn" cssClass="text-input" />
                        </s:else>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td align="left" style="padding-left:10px;">
                        <div id="getisbninfo"></div>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span><span class="inputrequired">*</span> Received Quantity:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.quantity" maxlength="10" id="bellReceivedItem.quantity" cssClass="text-input" />
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <div id="checkquantity"></div>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Ordered Quantity:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.orderedQuantity" maxlength="10" id="bellReceivedItem.orderedQuantity" cssClass="text-input" />
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <div id="checkquantity"></div>
                        </td>
                    </tr>
                    <tr><td><div style="height:5px;"></div></td></tr>
                   <s:if test="received.vendor != null && received.vendor.vendorSkidTypes != null">
                    <tr id="skidTypeRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Type:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                           <s:select name="skidTypeId" list="received.vendor.vendorSkidTypes" id="bellReceivedItem.skidType" emptyOption="true" listKey="skidtype" listValue="id"/>
                        </td>
                    </tr>
                   </s:if>
                    <tr id="perSkidCostRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Per Skid Cost:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.perSkidCost" maxlength="10" id="bellReceivedItem.perSkidCost" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr id="skidCountRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Piece Count:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.skidPieceCount" maxlength="10" id="bellReceivedItem.skidPieceCount" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr id="skidCostRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Piece Cost:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.skidPieceCost" maxlength="10" id="bellReceivedItem.skidPieceCost" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr id="skidPriceRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Piece Price:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.skidPiecePrice" maxlength="10" id="bellReceivedItem.skidPiecePrice" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr id="skidLbsRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Lbs:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.lbs" maxlength="10" id="bellReceivedItem.skidLbs" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr id="skidLbsCostRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Lbs Cost:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.lbsCost" maxlength="10" id="bellReceivedItem.skidLbsCost" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr id="skidLbsPriceRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Lbs Price:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.lbsPrice" maxlength="10" id="bellReceivedItem.skidLbsPrice" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Cost Per Lb:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.costPerLb" maxlength="10" id="bellReceivedItem.costPerLb" cssClass="text-input" />
                        </td>
                        <td align="left" style="padding-left:5px;">
                        <div id="costPerLbBtn"></div>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Cost:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.cost" maxlength="10" id="bellReceivedItem.cost" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Title:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.title" maxlength="255" id="bellReceivedItem.title" cssClass="text-input" />
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <div id="titleinfo"></div>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Bin:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.bin" maxlength="20" id="bellReceivedItem.bin" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>List Price:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.listPrice" maxlength="10" id="bellReceivedItem.listPrice" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Selling Price:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="bellReceivedItem.sellPrice" maxlength="10" id="bellReceivedItem.sellPrice" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Cover:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                            <s:select name="bellReceivedItem.coverType" list="#{'':'', 'PAP':'PAP', 'HC':'HC', 'AUDIO':'AUDIO', 'NON':'NON', 'SPIRAL':'SPIRAL', 'BOARD':'BOARD', 'LEATHER':'LEATHER'}" id="bellReceivedItem.coverType"/>
                        </td>
                    </tr>
                    
            </table>
        </td>
        </tr>
        </table>

    </fieldset>
    </td>
    
    <td valign="top" style="padding-left:10px;">
    <fieldset>
        <legend>Inventory Item Information</legend>
        
            <table>
                <tr>
                    <td align="right">ISBN:</td>
                    <td align="left" style="padding-left:10px;"><div id="bellInventory.isbn"><s:property value="bellInventory.isbn"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">ISBN13:</td>
                    <td align="left" style="padding-left:10px;"><div id="bellInventory.isbn13"><s:property value="bellInventory.isbn13"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Bin:</td>
                    <td align="left" style="padding-left:10px;"><div id="bellInventory.bin"><s:property value="bellInventory.bin"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Cover:</td>
                    <td align="left" style="padding-left:10px;"><div id="bellInventory.cover"><s:property value="bellInventory.cover"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">List Price:</td>
                    <td align="left" style="padding-left:10px;"><div id="bellInventory.listPrice"><s:property value="formatMoney(bellInventory.listPrice)"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Selling Price:</td>
                    <td align="left" style="padding-left:10px;"><div id="bellInventory.sellPrice"><s:property value="formatMoney(bellInventory.sellPrice)"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Committed:</td>
                    <td align="left" style="padding-left:10px;"><div id="bellInventory.committed"><s:property value="bellInventory.committed"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Available:</td>
                    <td align="left" style="padding-left:10px;"><div id="bellInventory.available"><s:property value="bellInventory.available"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">On Hand:</td>
                    <td align="left" style="padding-left:10px;"><div id="bellInventory.onhand"><s:property value="bellInventory.onhand"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Last Cost:</td>
                    <td align="left" style="padding-left:10px;"><div id="bellInventory.cost"><s:property value="formatMoney(bellInventory.cost)"/></div></td>
                </tr>
            </table>
        
    </fieldset>
    
    <fieldset style="margin-top:10px;">
        <legend>Amazon Item Information</legend>
        <div id="amazonItemDiv"></div>
    </fieldset>
    
    </td>
    </tr></table>
        
</form>

