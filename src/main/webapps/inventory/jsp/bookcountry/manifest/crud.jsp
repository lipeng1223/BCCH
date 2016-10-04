<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

function submit(){

    var check = trimString(document.getElementById("manifest.name").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Manifest Name must be provided.');
        return;
    }
    //if (!specialCharacterSearch(check, "Manifest Name")) return;
    
    Ext.form.crudForm.submit({
        waitMsg:'<s:if test="manifest != null">Updating</s:if><s:else>Creating</s:else> Manifest...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            Ext.grid.manifestsGridDs.reload();
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
        text:'<s:if test="manifest != null">Update</s:if><s:else>Create</s:else>', 
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

    setFocus('manifest.name');
    
    new Ext.KeyNav(Ext.form.crudForm.getEl(), {
        'enter': function(e) {
            submit();
        },
        'scope': Ext.form.crudForm
    });
});
</script>

<s:if test="manifest != null">
<form action="manifest!editSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="manifest.id"/>
</s:if><s:else>
<form action="manifest!createSubmit.bc" name="crudform" id="crudform" class="formular">
</s:else>

    <fieldset>
        <legend>Manifest Information</legend>
        <table>
            <tr>
                <td align="right">
                <span><span class="inputrequired">*</span> Manifest Name:</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <s:textfield key="manifest.name" maxlength="50" id="manifest.name" cssClass="text-input" />
                </td>
            </tr>
            <tr>
                <td align="right">
                <span>Manifest Comment:</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <s:textfield key="manifest.comment" maxlength="255" id="manifest.comment" cssClass="text-input" />
                </td>
            </tr>            
    </table>

    </fieldset>
    
</form>

