<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

function percentageListCalc() {
    var p = document.getElementById("receivedItem.percentageList").value;
    var l = document.getElementById("receivedItem.listPrice").value;
    try {
        if (l != undefined && p != undefined){
            var percent = parseFloat(p);
            var list = parseFloat(l);
            if (percent != undefined && list != undefined)
                document.getElementById("receivedItem.cost").value = (Math.round((percent / 100.0)*list*100.0))/100.0;
        } else {
            Ext.Msg.alert('Error', 'Make sure you have a Percentage List defined and a List Price defined.');
        }
    } catch (err) {
        Ext.Msg.alert('Error', 'Make sure you have a Percentage List defined and a List Price defined.');
    }
}
function costFromLbsCalc(){
  var cplb = document.getElementById("receivedItem.costPerLb").value;
  var aw = document.getElementById("amazonWeightSpan");
  try {
      if (aw != undefined){
          var w = parseFloat(aw.innerHTML);
          var c = parseFloat(cplb);
          if (w != undefined && c != undefined){
              document.getElementById("receivedItem.cost").value = (Math.round(c*w*100.0))/100.0;
          }
      } else {
          Ext.Msg.alert('Error', 'Make sure you click on the Get ISBN Info button to load the current amazon weight.');
      }
  } catch (err){
      Ext.Msg.alert('Error', 'Make sure you click on the Get ISBN Info button to load the current amazon weight and have a Cost Per Lb defined.');
  }
}

function typeChange(){
    var type = document.getElementById("receivedItem.skid");
    var val = type.options[type.selectedIndex].value;
    var lbs = document.getElementById("skidLbsCheck").checked;
    if (val == 'true'){
        // skid
        document.getElementById("receivedItem.isbn").value = "BREAK_SKID";
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
        document.getElementById("receivedItem.isbn").value = "";
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

var inSubmit = false;
function submit(gotonext, createnew){
    if (inSubmit) return;
    inSubmit = true;
    var check;
    <s:if test="receivedItem == null">
    check = trimString(document.getElementById("receivedItem.isbn").value);
    var theisbn = check;
    if (check.length == 0){
        Ext.Msg.alert('Error', 'ISBN must be provided.');
        setFocus('receivedItem.isbn');
        return;
    }
    </s:if>
    
    check = trimString(document.getElementById("receivedItem.quantity").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Quantity must be provided.');
        return;
    }
    if (!checkInt(check, true)) {
        Ext.Msg.alert('Error', 'Quantity must be a number.');
        return;
    }

    var msg = '<s:if test="receivedItem != null">Updating</s:if><s:else>Creating</s:else> Received Item...';
    if (createnew != undefined && createnew){
        msg = false;
    }
    var params = {};
    
    Ext.form.crudForm.submit({
        waitMsg: msg,
        params: params,
        timeout: 60000,
        failure: function(form, action){
            if (action.result.error == "noamazondata"){
                var isbnfield = document.getElementById("receivedItem.isbn");
                isbnfield.disabled=false; 
                setFocus('receivedItem.isbn');
                Ext.MessageBox.alert('Error', "This ISBN was not found in inventory and could not be found by amazon.");
            } else if (action.result.error == "systemerror"){
                var isbnfield = document.getElementById("receivedItem.isbn");
                isbnfield.disabled=false; 
                setFocus('receivedItem.isbn');
                Ext.MessageBox.alert('Error', "The system could not create the receiving item, there was an internal error.");
            } else {
                Ext.MessageBox.alert('Status', action.result.error);
            }
            inSubmit = false;
        },
        success: function(form, action){
            
            if (gotonext){
                <s:if test="receivedItem != null">
                doEditNextItem = Ext.grid.receivingitemsGrid.getSelectionModel().getSelected().get("id");
                </s:if>
            }
            Ext.grid.receivingitemsGridDs.reload();
            if (gotonext){
                <s:if test="receivedItem != null">
                Ext.crudWindow.close();
                return;
                </s:if>
                blankInventoryInfo();
                
                Ext.get("messagediv").fadeOut({duration:.5});
                
                document.getElementById("receivedItem.isbn").value = '';
                blankReceivedInfo();

                setFocus('receivedItem.isbn');
            } else {
                if (createnew != undefined && createnew){
                    var isbnfield = document.getElementById("receivedItem.isbn");
                    isbnfield.disabled=false; 
                    isbnfield.value = '';
                    setFocus('receivedItem.isbn');
                } else {
                    Ext.crudWindow.close();
                }
            }
            inSubmit = false;
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
    var isbn = trimString(Ext.get("receivedItem.isbn").getValue());
    currentIsbn = isbn;
    var cond = Ext.get("receivedItem.cond").getValue();
    var msgdiv = document.getElementById("messagediv");
    msgdiv.innerHTML = "Getting Info From Inventory...";
    Ext.get("messagediv").fadeIn({duration:.5});
    
    Ext.Ajax.request({
        url: 'inventoryitem!getInfo.bc',
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
                    document.getElementById("receivedItem.title").value = json.title;
                }
                if (json.sellingPrice){
                    document.getElementById("receivedItem.sellPrice").value = json.sellingPrice;
                    document.getElementById("inventoryItem.sellingPrice").innerHTML = moneyRenderer(json.sellingPrice);
                }
                if (json.listPrice){
                    document.getElementById("receivedItem.listPrice").value = json.listPrice;
                    document.getElementById("inventoryItem.listPrice").innerHTML = moneyRenderer(json.listPrice);
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
                    document.getElementById("receivedItem.bin").value = json.bin;
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
                if (json.cover != null){
                    document.getElementById("inventoryItem.cover").innerHTML = json.cover;
                    var ddbox = document.getElementById("receivedItem.coverType");
                    for (var i=0; i < ddbox.options.length; i++) {
                        if (ddbox.options[i].value == json.cover) {
                            ddbox.options[i].selected = true;
                        }
                    }
                }
                if (json.receivedPrice != null){
                    document.getElementById("receivedItem.cost").value = json.receivedPrice;
                    document.getElementById("inventoryItem.cost").innerHTML = moneyRenderer(json.receivedPrice);
                }
                document.getElementById("receivedItem.bellbook").checked = false;
                document.getElementById("receivedItem.restricted").checked = false;
                document.getElementById("receivedItem.higherEducation").checked = false;
                if (json.bellbook != null){
                    document.getElementById("receivedItem.bellbook").checked = json.bellbook;
                }
                if (json.restricted != null){
                    document.getElementById("receivedItem.restricted").checked = json.restricted;
                }
                if (json.he != null){
                    document.getElementById("receivedItem.higherEducation").checked = json.he;
                }
            } else {
                // not found
                
                msgdiv.innerHTML = cond+" "+isbn+" <span class='redtext'>NOT</span> Found in inventory.";
            }
            //Ext.get("messagediv").fadeIn({duration:.5});
        },
        failure: function(response, options){
            //Ext.get("messagediv").fadeOut({duration:.5});
            msgdiv.innerHTML = "<span class='redtext'>ERROR</span> Could not talk to inventory, try again.";
            Ext.get("messagediv").fadeIn({duration:.5});
        }
     });

    loadAmazonDetail(isbn);
}

function loadAmazonDetail(isbn, fastRecCreate, fastRecWait){
    Ext.Ajax.request({
        url: 'inventoryitem!amazonDetail.bc',
        params: { isbn:isbn },
        success: function(response, options){
            if (Ext.crudWindow == undefined || !Ext.crudWindow.isVisible()){
                return;
            }
            document.getElementById("amazonItemDiv").innerHTML = response.responseText;

            if (fastRecCreate != undefined && fastRecCreate){
                fastRecWait.hide();
                var titleSpan = document.getElementById("amazonTitleSpan");
                if (titleSpan != undefined){
                    document.getElementById("receivedItem.title").value = titleSpan.innerHTML;
                }
                submit(false, true);
            }
        },
        failure: function(response, options){
            if (fastRecCreate != undefined && fastRecCreate){
                fastRecWait.hide();
                Ext.Msg.alert('Error', 'Could not find the information on amazon or in inventory.  Enter the Title and click Create & Go To Next');
            }
            
        }
     });        
}

function blankInventoryInfo(){
    document.getElementById("amazonItemDiv").innerHTML = "";
    document.getElementById("inventoryItem.isbn").innerHTML = "";
    document.getElementById("inventoryItem.isbn10").innerHTML = "";
    document.getElementById("inventoryItem.isbn13").innerHTML = "";
    document.getElementById("inventoryItem.bin").innerHTML = "";
    document.getElementById("inventoryItem.listPrice").innerHTML = "";
    document.getElementById("inventoryItem.committed").innerHTML = "";
    document.getElementById("inventoryItem.available").innerHTML = "";
    document.getElementById("inventoryItem.onhand").innerHTML = "";
    document.getElementById("inventoryItem.sellingPrice").innerHTML = "";
    document.getElementById("inventoryItem.cost").innerHTML = "";
    document.getElementById("inventoryItem.cover").innerHTML = "";
}

function blankReceivedInfo(){
    document.getElementById("receivedItem.coverType").options[0].selected = true;
    
    blankText("receivedItem.title");
    blankText("receivedItem.listPrice");
    blankText("receivedItem.sellPrice");
    
    // defaulting quantity to 1
    document.getElementById("receivedItem.quantity").value="1";
    
    blankText("receivedItem.orderedQuantity");
    blankText("receivedItem.cost");
    blankText("receivedItem.bin");
    blankText("receivedItem.perSkidCost");
    blankText("receivedItem.skidPieceCount");
    blankText("receivedItem.skidPieceCost");
    blankText("receivedItem.skidPiecePrice");
    blankText("receivedItem.skidLbs");
    blankText("receivedItem.skidLbsCost");
    blankText("receivedItem.skidPrice");
    blankText("receivedItem.percentageList");
    blankText("receivedItem.costPerLb");

    document.getElementById("receivedItem.bellbook").checked = false;
    document.getElementById("receivedItem.breakroom").checked = false;
    document.getElementById("receivedItem.higherEducation").checked = false;
    document.getElementById("receivedItem.restricted").checked = false;
}

function blankText(id){
    var elem = document.getElementById(id);
    if (elem != undefined) {
        elem.value = "";
    }
}


Ext.onReady(function(){
    Ext.form.crudForm = new Ext.form.BasicForm("crudform");
    Ext.crudWindow.getBottomToolbar().addFill();
    Ext.crudWindow.getBottomToolbar().add({
        id:'submitButton', 
        type: 'submit',
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'<s:if test="receivedItem != null">Update</s:if><s:else>Create</s:else>', 
        handler: function(){
            submit(false);
        }, 
        disabled:false
    });
    <s:if test="receivedItem == null">
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

    <s:if test="receivedItem == null">
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
    new Ext.Button({
        applyTo:'pctListBtn', 
        cls:"x-btn-text-icon", 
        icon:"/images/cog.png", 
        text:'', 
        disabled:false,
        handler: function(){
            percentageListCalc();
        } 
    });


    <s:if test="receivedItem == null">
    Ext.get("receivedItem.isbn").addKeyListener(13, function(){
        getIsbnInfo();
    });
    Ext.get("receivedItem.isbn").addListener('keyup', function(evt, t, o){
        
        if (evt.getKey() != 13){
            if (currentIsbn != Ext.get("receivedItem.isbn").getValue()){
                Ext.get("messagediv").fadeOut({duration:.5});
                blankReceivedInfo();
                blankInventoryInfo();
            }
        }
    });
    
    setFocus('receivedItem.isbn');

    </s:if><s:else>
    loadAmazonDetail("<s:property value="receivedItem.isbn"/>");
    </s:else>
});
</script>

<s:if test="receivedItem != null">
<form action="receivingitem!editSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="receivedItem.id"/>
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
                    <tr>
                        <td align="right" nowrap="nowrap">
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:if test="receivedItem != null">
                            <s:if test="receivedItem.skid == true">
                            Skid
                            </s:if><s:else>
                            Pieces
                            </s:else>
                        </s:if><s:else>
                             <s:select name="receivedItem.skid" list="#{'false':'Pieces', 'true':'Skid'}" id="receivedItem.skid" onchange="typeChange();"/>
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
                    <tr><td><div style="height:5px;"></div></td></tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span><span class="inputrequired">*</span> Condition:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:if test="receivedItem != null">
                            <s:property value="receivedItem.cond"/>
                        </s:if><s:else>
                             <s:select name="receivedItem.cond" list="#{'hurt':'hurt', 'unjacketed':'unjacketed', 'overstock':'overstock'}" id="receivedItem.cond"/>
                        </s:else>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span><span class="inputrequired">*</span> ISBN:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:if test="receivedItem != null">
                            <s:property value="receivedItem.isbn"/>
                        </s:if><s:else>
                            <s:textfield key="receivedItem.isbn" maxlength="50" id="receivedItem.isbn" cssClass="text-input" />
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
                            <s:if test="receivedItem == null">
                                <s:textfield key="receivedItem.quantity" maxlength="10" id="receivedItem.quantity" cssClass="text-input" value="1"/>
                            </s:if><s:else>
                                <s:textfield key="receivedItem.quantity" maxlength="10" id="receivedItem.quantity" cssClass="text-input"/>
                            </s:else>
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
                        <s:textfield key="receivedItem.orderedQuantity" maxlength="10" id="receivedItem.orderedQuantity" cssClass="text-input" />
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <div id="checkquantity"></div>
                        </td>
                    </tr>
                    <tr><td><div style="height:5px;"></div></td></tr>
                    <tr id="skidTypeRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Type:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                            <s:if test="received.vendor != null && received.vendor.vendorSkidTypes != null">
                                    <s:select name="skidTypeId" list="received.vendor.vendorSkidTypes" id="receivedItem.skidType" emptyOption="true" listKey="skidtype" listValue="id"/>
                            </s:if><s:else>
                                    <s:select name="skidTypeId" list="#{}" id="receivedItem.skidType" emptyOption="true" listKey="skidtype" listValue="id"/>
                            </s:else>
                        </td>
                    </tr>
                    <tr id="perSkidCostRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Per Skid Cost:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="receivedItem.perSkidCost" maxlength="10" id="receivedItem.perSkidCost" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr id="skidCountRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Piece Count:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="receivedItem.skidPieceCount" maxlength="10" id="receivedItem.skidPieceCount" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr id="skidCostRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Piece Cost:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="receivedItem.skidPieceCost" maxlength="10" id="receivedItem.skidPieceCost" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr id="skidPriceRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Piece Price:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="receivedItem.skidPiecePrice" maxlength="10" id="receivedItem.skidPiecePrice" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr id="skidLbsRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Lbs:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="receivedItem.lbs" maxlength="10" id="receivedItem.skidLbs" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr id="skidLbsCostRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Lbs Cost:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="receivedItem.lbsCost" maxlength="10" id="receivedItem.skidLbsCost" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr id="skidLbsPriceRow" style="display:none;">
                        <td align="right" nowrap="nowrap">
                        <span>Skid Lbs Price:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="receivedItem.lbsPrice" maxlength="10" id="receivedItem.skidLbsPrice" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Percentage List:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="receivedItem.percentageList" maxlength="10" id="receivedItem.percentageList" cssClass="text-input" />
                        </td>
                        <td align="left" style="padding-left:5px;">
                        <div id="pctListBtn"></div>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Cost Per Lb:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="receivedItem.costPerLb" maxlength="10" id="receivedItem.costPerLb" cssClass="text-input" />
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
                        <s:textfield key="receivedItem.cost" maxlength="10" id="receivedItem.cost" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Title:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="receivedItem.title" maxlength="255" id="receivedItem.title" cssClass="text-input" />
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
                        <s:textfield key="receivedItem.bin" maxlength="20" id="receivedItem.bin" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>List Price:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="receivedItem.listPrice" maxlength="10" id="receivedItem.listPrice" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Selling Price:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="receivedItem.sellPrice" maxlength="10" id="receivedItem.sellPrice" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Cover:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                            <s:select name="receivedItem.coverType" list="#{'PAP':'PAP', 'HC':'HC', 'AUDIO':'AUDIO', 'NON':'NON', 'SPIRAL':'SPIRAL', 'BOARD':'BOARD', 'LEATHER':'LEATHER'}" id="receivedItem.coverType"/>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Bell Book:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:checkbox name="receivedItem.bellbook" id="receivedItem.bellbook" cssClass="checkbox"  value="receivedItem.inventoryItem.bellbook"/>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Break Room:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:checkbox key="receivedItem.breakroom" id="receivedItem.breakroom" cssClass="checkbox" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Higher Eduction:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:checkbox name="receivedItem.higherEducation" id="receivedItem.higherEducation" cssClass="checkbox" value="receivedItem.inventoryItem.he"/>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" nowrap="nowrap">
                        <span>Restricted:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:checkbox name="receivedItem.restricted" id="receivedItem.restricted" cssClass="checkbox" value="receivedItem.inventoryItem.restricted"/>
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
                    <td align="left" style="padding-left:10px;"><div id="inventoryItem.isbn"><s:property value="inventoryItem.isbn"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">ISBN10:</td>
                    <td align="left" style="padding-left:10px;"><div id="inventoryItem.isbn10"><s:property value="inventoryItem.isbn10"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">ISBN13:</td>
                    <td align="left" style="padding-left:10px;"><div id="inventoryItem.isbn13"><s:property value="inventoryItem.isbn13"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Bin:</td>
                    <td align="left" style="padding-left:10px;"><div id="inventoryItem.bin"><s:property value="inventoryItem.bin"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Cover:</td>
                    <td align="left" style="padding-left:10px;"><div id="inventoryItem.cover"><s:property value="inventoryItem.cover"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">List Price:</td>
                    <td align="left" style="padding-left:10px;"><div id="inventoryItem.listPrice"><s:property value="formatMoney(inventoryItem.listPrice)"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Selling Price:</td>
                    <td align="left" style="padding-left:10px;"><div id="inventoryItem.sellingPrice"><s:property value="formatMoney(inventoryItem.sellingPrice)"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Committed:</td>
                    <td align="left" style="padding-left:10px;"><div id="inventoryItem.committed"><s:property value="inventoryItem.committed"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Available:</td>
                    <td align="left" style="padding-left:10px;"><div id="inventoryItem.available"><s:property value="inventoryItem.available"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">On Hand:</td>
                    <td align="left" style="padding-left:10px;"><div id="inventoryItem.onhand"><s:property value="inventoryItem.onhand"/></div></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right">Last Cost:</td>
                    <td align="left" style="padding-left:10px;"><div id="inventoryItem.cost"><s:property value="formatMoney(inventoryItem.cost)"/></div></td>
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

