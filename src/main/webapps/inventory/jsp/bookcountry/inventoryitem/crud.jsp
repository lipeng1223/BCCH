<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

function submit(){

    var check = trimString(document.getElementById("inventoryItem.isbn").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'ISBN must be provided.');
        return;
    }
    check = trimString(document.getElementById("inventoryItem.listPrice").value);
    if (check != null && check.length > 0){
        if (!checkPositiveFloatAllowZero(check)){
            Ext.Msg.alert('Error', 'List Price must be a positive numeric value.');
            return;
        }
    }
    check = trimString(document.getElementById("inventoryItem.sellingPrice").value);
    if (check != null && check.length > 0){
        if (!checkPositiveFloatAllowZero(check)){
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
    
    Ext.form.crudForm.submit({
        waitMsg:'Updating Inventory Item...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            if (Ext.grid.inventoryGridDs){
                Ext.grid.inventoryGridDs.reload();
                Ext.crudWindow.close();
            } else {
                document.location = 'inventoryitem!view.bc?id=<s:property value="inventoryItem.id"/>';
            }
        }
    });
}
function cancel(){
    Ext.crudWindow.close();
}

function getInfo(){
    blankInfo();
    var isbn = Ext.get("inventoryItem.isbn").getValue();
    var msgdiv = document.getElementById("messagediv");
    msgdiv.innerHTML = "Getting Info From Amazon...";
    Ext.get("messagediv").fadeIn({duration:.5});
    Ext.Ajax.request({
        url: 'inventoryitem!getAmazonInfo.bc',
        timeout: 60000,
        params: { isbn:isbn},
        success: function(response, options){
            var json = Ext.decode(response.responseText);
            //Ext.get("messagediv").fadeOut({duration:.5});
            if (json.dataLoaded != null && json.dataLoaded && json.notFound != null && !json.notFound){
                // found
                msgdiv.innerHTML = isbn+" <span class='greentext'>Found</span> by Amazon.";
                if (json.isbn){
                    document.getElementById("amazon.isbn").innerHTML = json.isbn;
                }
                if (json.title){
                    document.getElementById("amazon.title").innerHTML = json.title;
                }
                if (json.publisher){
                    document.getElementById("amazon.publisher").innerHTML = json.publisher;
                }
                if (json.author){
                    document.getElementById("amazon.author").innerHTML = json.author;
                }
                if (json.listPrice){
                    document.getElementById("amazon.listPrice").innerHTML = json.listPrice;
                }
                if (json.numberOfPages){
                    document.getElementById("amazon.numberOfPages").innerHTML = json.numberOfPages;
                }
                if (json.publicationDate){
                    document.getElementById("amazon.publicationDate").innerHTML = json.publicationDate;
                }
                if (json.binding){
                    document.getElementById("amazon.binding").innerHTML = json.binding;
                }
                if (json.length){
                    document.getElementById("amazon.length").innerHTML = json.length;
                }
                if (json.width){
                    document.getElementById("amazon.width").innerHTML = json.width;
                }
                if (json.height){
                    document.getElementById("amazon.height").innerHTML = json.height;
                }
                if (json.weight){
                    document.getElementById("amazon.weight").innerHTML = json.weight;
                }
                if (json.smallImageUrl){
                    document.getElementById("amazon.image").innerHTML = "<img src='"+json.smallImageUrl+"'/>";
                }
                if (json.salesRank){
                    document.getElementById("amazon.salesRank").innerHTML = json.salesRank;
                }
                Ext.getCmp("updateFromAmazonButton").enable();
            } else {
                // not found
                msgdiv.innerHTML = isbn+" <span class='redtext'>NOT</span> Found by Amazon.";
            }
            
            //Ext.get("messagediv").fadeIn({duration:.5});
        },
        failure: function(response, options){
            // failed to talk
            //Ext.get("messagediv").fadeOut({duration:.5});
            msgdiv.innerHTML = "<span class='redtext'>ERROR</span> Could not talk to Amazon, try again.";
            Ext.get("messagediv").fadeIn({duration:.5});
        }
     });        
}

function blankInfo(){
    Ext.getCmp("updateFromAmazonButton").disable();
    document.getElementById("amazon.isbn").innerHTML = "";
    document.getElementById("amazon.title").innerHTML = "";
    document.getElementById("amazon.publisher").innerHTML = "";
    document.getElementById("amazon.author").innerHTML = "";
    document.getElementById("amazon.listPrice").innerHTML = "";
    document.getElementById("amazon.numberOfPages").innerHTML = "";
    document.getElementById("amazon.publicationDate").innerHTML = "";
    document.getElementById("amazon.binding").innerHTML = "";
    document.getElementById("amazon.length").innerHTML = "";
    document.getElementById("amazon.width").innerHTML = "";
    document.getElementById("amazon.height").innerHTML = "";
    document.getElementById("amazon.weight").innerHTML = "";
    document.getElementById("amazon.image").innerHTML = "";
    document.getElementById("amazon.salesRank").innerHTML = "";
}

function copyAmazonData(){
    document.getElementById("inventoryItem.title").value = document.getElementById("amazon.title").innerHTML;
    document.getElementById("inventoryItem.companyRec").value = document.getElementById("amazon.publisher").innerHTML;
    document.getElementById("inventoryItem.length").value = document.getElementById("amazon.length").innerHTML;
    document.getElementById("inventoryItem.width").value = document.getElementById("amazon.width").innerHTML;
    document.getElementById("inventoryItem.height").value = document.getElementById("amazon.height").innerHTML;
    document.getElementById("inventoryItem.weight").value = document.getElementById("amazon.weight").innerHTML;
    document.getElementById("inventoryItem.numberOfPages").value = document.getElementById("amazon.numberOfPages").innerHTML;
    var lp = document.getElementById("amazon.listPrice").innerHTML;
    if (lp && lp.length > 0){
        document.getElementById("inventoryItem.listPrice").value = lp.substring(1);
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
        text:'Update', 
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

    new Ext.Button({
        applyTo:'getinfo', 
        cls:"x-btn-text-icon", 
        icon:"/images/amazon_icon.png", 
        text:'Get Amazon Info', 
        disabled:false,
        handler: function(){
            getInfo();
        } 
    });
    new Ext.Button({
        id: 'updateFromAmazonButton',
        applyTo:'updateFromAmazonDiv', 
        cls:"x-btn-text-icon", 
        icon:"/images/arrow_left.png", 
        text:'Copy Amazon Data To Item', 
        disabled: true,
        handler: function(){
             copyAmazonData();
        } 
    });


    
    
    Ext.get("inventoryItem.isbn").addKeyListener(13, function(){
        getInfo();
    });
    Ext.get("inventoryItem.isbn").addListener('keydown', function(evt, t, o){
        if (evt.getKey() != 13){
            Ext.get("messagediv").fadeOut({duration:.5});
            blankInfo();
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
    
    setFocus('inventoryItem.isbn');

    getInfo();
        
});
</script>

<form action="inventoryitem!editSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="inventoryItem.id"/>
        
        <table>
            <tr>
            <td valign="top">
        
        
                <fieldset>
                    <legend>Inventory Item Information</legend>
        
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
                        <span><span class="inputrequired">*</span> Condition:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                         <s:select name="inventoryItem.cond" list="#{'hurt':'hurt', 'unjacketed':'unjacketed', 'overstock':'overstock'}" id="inventoryItem.cond"/>
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> ISBN:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventoryItem.isbn" maxlength="100" id="inventoryItem.isbn" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td align="left" style="padding-left:10px;">
                        <div id="getinfo"></div>
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Title:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventoryItem.title" maxlength="100" id="inventoryItem.title" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Author:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventoryItem.author" maxlength="100" id="inventoryItem.author" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Publisher:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventoryItem.companyRec" maxlength="100" id="inventoryItem.companyRec" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>List Price:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <input type="text" name="inventoryItem.listPrice" value="<s:property value="inventoryItem.listPrice"/>" maxlength="100" id="inventoryItem.listPrice" class="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Selling Price:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <input type="text" name="inventoryItem.sellingPrice" value="<s:property value="inventoryItem.sellingPrice"/>" maxlength="100" id="inventoryItem.sellingPrice" class="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>On Hand:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventoryItem.onhand" maxlength="100" id="inventoryItem.onhand" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Bin:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventoryItem.bin" maxlength="100" id="inventoryItem.bin" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> Cover:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                         <s:select name="inventoryItem.cover" list="#{'PAP':'PAP', 'HC':'HC', 'AUDIO':'AUDIO', 'NON':'NON', 'SPIRAL':'SPIRAL', 'BOARD':'BOARD', 'LEATHER':'LEATHER'}" id="inventoryItem.cover"/>
                        </td>
                    </tr>
                    <tr><td><div style="height:5px;"></div></td></tr>
                    <tr>
                        <td align="right">
                        <span>Bell Book:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:checkbox key="inventoryItem.bellbook" id="inventoryItem.bellbook" cssClass="checkbox" />
                        </td>
                    </tr>
                    <tr><td><div style="height:5px;"></div></td></tr>
                    <tr>
                        <td align="right">
                        <span>Skid:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:checkbox key="inventoryItem.skid" id="inventoryItem.skid" cssClass="checkbox" />
                        </td>
                    </tr>
                    <tr><td><div style="height:5px;"></div></td></tr>
                    <tr>
                        <td align="right">
                        <span>Restricted:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:checkbox key="inventoryItem.restricted" id="inventoryItem.restricted" cssClass="checkbox" />
                        </td>
                    </tr>
                    <tr><td><div style="height:5px;"></div></td></tr>
                    <tr>
                        <td align="right">
                        <span>Higher Education:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:checkbox key="inventoryItem.he" id="inventoryItem.he" cssClass="checkbox" />
                        </td>
                    </tr>
                    <tr><td><div style="height:5px;"></div></td></tr>
                    <tr>
                        <td align="right">
                        <span>Biblio:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventoryItem.biblio" maxlength="100" id="inventoryItem.biblio" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Bc Category:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventoryItem.bccategory" maxlength="100" id="inventoryItem.bccategory" cssClass="text-input" />
                        </td>
                    </tr>

                    <tr>
                        <td align="right">
                        <span>Number Of Pages:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventoryItem.numberOfPages" maxlength="100" id="inventoryItem.numberOfPages" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Length (inches):</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventoryItem.length" maxlength="10" id="inventoryItem.length" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Width (inches):</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventoryItem.width" maxlength="10" id="inventoryItem.width" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Height (inches):</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventoryItem.height" maxlength="10" id="inventoryItem.height" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Weight (lbs):</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventoryItem.weight" maxlength="100" id="inventoryItem.weight" cssClass="text-input" />
                        </td>
                    </tr>
                    
            </table>
            
            
            </fieldset>
    
        </td>
        <td valign="top" style="padding-left:15px;">
        
            <fieldset style="margin-top:10px;">
                <legend><img src="/images/amazon_icon.png"/> Amazon Information</legend>
                
                    <table>
                        <tr>
                            <td align="right">ISBN:</td>
                            <td align="left" style="padding-left:10px;"><div id="amazon.isbn"></div></td>
                        </tr>
                        <tr><td><div style="height:4px;"></div></td></tr>
                        <tr>
                            <td align="right">Sales Rank:</td>
                            <td align="left" style="padding-left:10px;"><div id="amazon.salesRank"></div></td>
                        </tr>
                        <tr><td><div style="height:4px;"></div></td></tr>
                        <tr>
                            <td align="right">Title:</td>
                            <td align="left" style="padding-left:10px;"><div id="amazon.title"></div></td>
                        </tr>
                        <tr><td><div style="height:4px;"></div></td></tr>
                        <tr>
                            <td align="right">Publisher:</td>
                            <td align="left" style="padding-left:10px;"><div id="amazon.publisher"></div></td>
                        </tr>
                        <tr><td><div style="height:4px;"></div></td></tr>
                        <tr>
                            <td align="right">Author:</td>
                            <td align="left" style="padding-left:10px;"><div id="amazon.author"></div></td>
                        </tr>
                        <tr>
                            <td align="right">List Price:</td>
                            <td align="left" style="padding-left:10px;"><div id="amazon.listPrice"></div></td>
                        </tr>
                        <tr>
                            <td align="right">Number Of Pages:</td>
                            <td align="left" style="padding-left:10px;"><div id="amazon.numberOfPages"></div></td>
                        </tr>
                        <tr>
                            <td align="right">Publication Date:</td>
                            <td align="left" style="padding-left:10px;"><div id="amazon.publicationDate"></div></td>
                        </tr>
                        <tr>
                            <td align="right">Binding:</td>
                            <td align="left" style="padding-left:10px;"><div id="amazon.binding"></div></td>
                        </tr>
                        <tr>
                            <td align="right">Length (inches):</td>
                            <td align="left" style="padding-left:10px;"><div id="amazon.length"></div></td>
                        </tr>
                        <tr>
                            <td align="right">Width (inches):</td>
                            <td align="left" style="padding-left:10px;"><div id="amazon.width"></div></td>
                        </tr>
                        <tr>
                            <td align="right">Height (inches):</td>
                            <td align="left" style="padding-left:10px;"><div id="amazon.height"></div></td>
                        </tr>
                        <tr>
                            <td align="right">Weight (lbs):</td>
                            <td align="left" style="padding-left:10px;"><div id="amazon.weight"></div></td>
                        </tr>
                        <tr>
                            <td align="right">Image:</td>
                            <td align="left" style="padding-left:10px;"><div id="amazon.image"></div></td>
                        </tr>
                    </table>
            </fieldset>
        
            <div style="margin-top:10px;margin-left:20px;"><div id="updateFromAmazonDiv"></div></div>
        </td>
        </tr>
        </table>

        
</form>

