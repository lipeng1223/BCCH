<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

function submit(stayhere){

    var check = trimString(document.getElementById("backStockItem.isbn").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'ISBN must be provided.');
        return;
    }
    
    // gather up any quantity changes at locations
    var quantities = "";
    
    var qelem = document.getElementById("locQuantity-0");
    var ielem = document.getElementById("locIds-0");
    var count = 0;
    while (qelem != null && qelem != undefined) {
        if (count > 0) quantities += ",";
        quantities += ielem.value+":"+qelem.value;
        count++;
        qelem = document.getElementById("locQuantity-"+count);
        ielem = document.getElementById("locIds-"+count);
    }

    Ext.form.crudForm.submit({
        waitMsg:'Submitting Back Stock...',
        params: { quantities: quantities },
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            if (stayhere != undefined && stayhere) {
                document.getElementById("backStockItem.title").value = '';
                document.getElementById("backStockItem.isbn").value = '';
                document.getElementById("backStockItem.isbn13").value = '';
                document.getElementById("backStockItem.id").value = '';
                document.getElementById("locationsdiv").innerHTML = '';
                setFocus('backStockItem.isbn');
                return;
            }
            Ext.crudWindow.close();
        }
    });
}
function cancel(){
    updateBackStockDetail(true);
    Ext.grid.backStockGridDs.reload();
    Ext.crudWindow.close();
}
function loadLocations(id, isbn){
    document.getElementById("locationsdiv").innerHTML = '';
    Ext.Ajax.request({
        url: 'backstock!locations.bc',
        params: { id:id, isbn:isbn },
        success: function(response, options){
            if (Ext.crudWindow == undefined || !Ext.crudWindow.isVisible()){
                return;
            }
            document.getElementById("locationsdiv").innerHTML = response.responseText;
        },
        failure: function(response, options){
            
        }
     });        
}

function loadTitle(isbn){
    Ext.Ajax.request({
        url: 'backstock!lookupTitle.bc',
        params: { isbn:isbn},
        failure: function(response, options){
            Ext.MessageBox.alert('Error', 'Could not lookup the Back Stock information.');
        },
        success: function(response, options){
            if (Ext.crudWindow == undefined || !Ext.crudWindow.isVisible()){
                return;
            }
            var json = Ext.decode(response.responseText);
            document.getElementById("backStockItem.title").value = json.title;
            document.getElementById("backStockItem.isbn").value = json.isbn;
            document.getElementById("backStockItem.isbn13").value = json.isbn13;
            document.getElementById("backStockItem.id").value = json.id;
        }
    });
}

Ext.onReady(function(){
    Ext.form.crudForm = new Ext.form.BasicForm("crudform");
    Ext.crudWindow.getBottomToolbar().addFill();
    Ext.crudWindow.getBottomToolbar().add({
        id:'submitAndContinueButton', 
        type: 'submit',
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'Submit And Continue', 
        handler: function(){submit(true);}, 
        disabled:false
    });
    Ext.crudWindow.getBottomToolbar().addSeparator();
    Ext.crudWindow.getBottomToolbar().add({
        id:'submitAndCloseButton', 
        type: 'submit',
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'Submit And Close', 
        handler:function(){submit(false);}, 
        disabled:false
    });
    Ext.crudWindow.getBottomToolbar().addSeparator();
    Ext.crudWindow.getBottomToolbar().add({
        id:'cancelButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/cancel.png", 
        text:'Close', 
        handler:cancel, 
        disabled:false
    });

    setFocus('backStockItem.isbn');
    
    Ext.get("backStockItem.isbn").addKeyListener(13, function(){
        var isbnfield = document.getElementById("backStockItem.isbn");
        loadLocations(null, isbnfield.value);
        loadTitle(isbnfield.value);
    });
    
    document.getElementById("backStockItem.isbn").setAttribute( "autocomplete", "off" );
    
    loadLocations(<s:if test="backStockItem != null"><s:property value="backStockItem.id"/></s:if><s:else>null</s:else>, null);
    
});
</script>

<form action="backstock!crudSubmit.bc" name="crudform" id="crudform" class="formular">
    <input type="hidden" name="backStockItem.id" value="<s:property value="backStockItem.id"/>" id="backStockItem.id">
    <table>
    <tr>
    <td valign="top">
    <fieldset>
        <legend>Back Stock Item</legend>
        <table>
            <tr>
            <td valign="top">
            
                <table>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> ISBN:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="backStockItem.isbn" maxlength="255" id="backStockItem.isbn" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>ISBN13:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="backStockItem.isbn13" maxlength="255" id="backStockItem.isbn13" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Title:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="backStockItem.title" maxlength="255" id="backStockItem.title" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Onhand:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="backStockItem.onhand" maxlength="255" id="backStockItem.onhand" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Committed:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="backStockItem.committed" maxlength="255" id="backStockItem.committed" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Comment:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="backStockItem.comment" maxlength="255" id="backStockItem.comment" cssClass="text-input" />
                        </td>
                    </tr>
                    
                    <tr><td style="height:10px"></td><td></td></tr>
                    <tr><td></td><td>New Location</td></tr>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> Location:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="backStockLocation.location" maxlength="255" id="backStockLocation.location" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Row:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="backStockLocation.row" maxlength="255" id="backStockLocation.row" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Quantity:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="backStockLocation.quantity" maxlength="9" id="backStockLocation.quantity" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Tub:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="backStockLocation.tub" maxlength="255" id="backStockLocation.tub" cssClass="text-input" />
                        </td>
                    </tr>
                    
                    
                </table>
                        
            </td>
            </tr>
        </table>

    </fieldset>
                        
    </td>
                        
                        
    <td valign="top" style="padding-left:10px;">
    <fieldset style="width:325px;">
        <legend>Locations</legend>
        <div id="locationsdiv" style="width:100%;"></div>
    </fieldset>
    </td>
    </tr>
    </table>
    

</form>
