<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

function submit(){

    var check = trimString(document.getElementById("backStockLocation.location").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Location must be provided.');
        return;
    }
    
    Ext.form.crudForm.submit({
        waitMsg:'<s:if test="backStockLocation != null">Updating</s:if><s:else>Creating</s:else> Back Stock Location...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            // reload the detail display
            updateBackStockDetail(true);
            Ext.grid.backStockGridDs.reload();
            Ext.crudWindow.close();
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
        text:'<s:if test="backStockLocation != null">Update</s:if><s:else>Create</s:else>', 
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

    setFocus('backStockLocation.location');
    
    new Ext.KeyNav(Ext.form.crudForm.getEl(), {
        'enter': function(e) {
            submit();
        },
        'scope': Ext.form.crudForm
    });
});
</script>

<s:if test="backStockLocation != null">
<form action="backstock!editLocationSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="backStockLocation.id"/>
</s:if><s:else>
<form action="backstock!createLocationSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden name="backStockId" value="%{id}"/>
</s:else>

    <fieldset>
        <legend>Back Stock Location</legend>
        <table>
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

    </fieldset>
    
</form>

