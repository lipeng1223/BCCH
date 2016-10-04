<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

function submit(){

    var check = trimString(document.getElementById("manifestItem.isbn").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'ISBN must be provided.');
        return;
    }
    //if (!specialCharacterSearch(check, "Manifest Name")) return;
    check = trimString(document.getElementById("manifestItem.quantity").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Quantity must be provided.');
        return;
    }

    // TODO: Make sure quantity is a number
    
    Ext.form.crudForm.submit({
        waitMsg:'<s:if test="manifestItem != null">Updating</s:if><s:else>Creating</s:else> Manifest Item...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            if (Ext.grid.manifestitemsGridDs){
                var cmp = Ext.getCmp("detailpanel");
                if (cmp != undefined && cmp.getUpdater() != undefined) cmp.getUpdater().refresh();
                Ext.grid.manifestitemsGridDs.reload();
            }
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
        text:'<s:if test="manifestItem != null">Update</s:if><s:else>Create</s:else>', 
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

    setFocus('manifestItem.isbn');
    
    new Ext.KeyNav(Ext.form.crudForm.getEl(), {
        'enter': function(e) {
            //console.log(inspect(e, 2));
            if (e.target.id != undefined && e.target.id != "manifestItem.isbn")
                submit();
        },
        'scope': Ext.form.crudForm
    });
});
</script>

<s:if test="manifestItem != null">
<form action="manifestitem!editSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden name="manifestItem.id" value="%{id}"/>
</s:if><s:else>
<form action="manifestitem!createSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden name="manifest.id" value="%{id}"/>
</s:else>

    <fieldset>
        <legend>Manifest Item Information</legend>
        <table>
            <tr>
                <td align="right">
                <span><span class="inputrequired">*</span> ISBN:</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <s:textfield key="manifestItem.isbn" maxlength="50" id="manifestItem.isbn" cssClass="text-input" />
                </td>
            </tr>
            <tr>
                <td align="right">
                <span><span class="inputrequired">*</span> Quantity:</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <s:textfield key="manifestItem.quantity" maxlength="50" id="manifestItem.quantity" cssClass="text-input" />
                </td>
            </tr>
            <tr>
                <td align="right" nowrap="nowrap">
                <span>Condition:</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <s:if test="manifestItem != null">
                    <s:property value="manifestItem.cond"/>
                </s:if><s:else>
                     <s:select name="manifestItem.cond" list="#{'hurt':'hurt', 'unjacketed':'unjacketed', 'overstock':'overstock'}" id="manifestItem.cond"/>
                </s:else>
                </td>
            </tr>
            <tr>
                <td align="right">
                <span>Bin:</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <s:textfield key="manifestItem.bin" maxlength="50" id="manifestItem.bin" cssClass="text-input" />
                </td>
            </tr>
            <tr>
                <td align="right">
                <span>Title:</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <s:textfield key="manifestItem.title" maxlength="50" id="manifestItem.title" cssClass="text-input" />
                </td>
            </tr>
    </table>

    </fieldset>
    
</form>

