<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

function submit(){

    var check = trimString(document.getElementById("inventory.isbn").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'ISBN must be provided.');
        return;
    }
    check = trimString(document.getElementById("inventory.listPrice").value);
    if (check != null && check.length > 0){
        if (!checkPositiveFloatAllowZero(check)){
            Ext.Msg.alert('Error', 'List Price must be a positive numeric value.');
            return;
        }
    }
    check = trimString(document.getElementById("inventory.sellingPrice").value);
    if (check != null && check.length > 0){
        if (!checkPositiveFloatAllowZero(check)){
            Ext.Msg.alert('Error', 'Selling Price must be a positive numeric value.');
            return;
        }
    }
    check = trimString(document.getElementById("inventory.onhand").value);
    if (check != null && check.length > 0){
        if (!checkInt(check, false)){
            Ext.Msg.alert('Error', 'On Hand must be a positive numeric value.');
            return;
        }
    }
    
    Ext.form.crudForm.submit({
        waitMsg:'Updating Bellwether Inventory Item...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            if (Ext.grid.inventoryGridDs){
                Ext.grid.inventoryGridDs.reload();
                Ext.crudWindow.close();
            } else {
                document.location = 'inventory!view.bc?id=<s:property value="inventory.id"/>';
            }
        }
    });
}
function cancel(){
    Ext.crudWindow.close();
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

    setFocus('inventory.isbn');

    getInfo();
        
});
</script>

<form action="inventory!editSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="inventory.id"/>
        
        <table>
            <tr>
            <td valign="top">
        
        
                <fieldset>
                    <legend>Bellwether Inventory Item Information</legend>
        
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
                        <span><span class="inputrequired">*</span> ISBN:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventory.isbn" maxlength="100" id="inventory.isbn" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Title:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventory.title" maxlength="100" id="inventory.title" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Author:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventory.author" maxlength="100" id="inventory.author" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Publisher:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventory.publisher" maxlength="100" id="inventory.publisher" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>List Price:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <input type="text" name="inventory.listPrice" value="<s:property value="inventory.listPrice"/>" maxlength="100" id="inventory.listPrice" class="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Selling Price:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <input type="text" name="inventory.sellPrice" value="<s:property value="inventory.sellPrice"/>" maxlength="100" id="inventory.sellingPrice" class="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>On Hand:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventory.onhand" maxlength="100" id="inventory.onhand" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Bin:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventory.bin" maxlength="100" id="inventory.bin" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> Cover:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                         <s:select name="inventory.cover" list="#{'PAP':'PAP', 'HC':'HC', 'AUDIO':'AUDIO', 'NON':'NON', 'SPIRAL':'SPIRAL', 'BOARD':'BOARD', 'LEATHER':'LEATHER'}" id="inventory.cover"/>
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Category:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventory.category" maxlength="100" id="inventory.category" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Bell Comment:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="inventory.bellcomment" maxlength="100" id="inventory.bellcomment" cssClass="text-input" />
                        </td>
                    </tr>
                    
            </table>
            
            
            </fieldset>
    
        </td>

        </tr>
        </table>

        
</form>

