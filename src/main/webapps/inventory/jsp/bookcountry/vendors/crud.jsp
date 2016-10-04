<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

function submit(){

    var check = trimString(document.getElementById("vendor.code").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Vendor Code must be provided.');
        return;
    }
    //if (!specialCharacterSearch(check, "Vendor Code")) return;
    
    check = trimString(document.getElementById("vendor.vendorName").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Vendor Name must be provided.');
        return;
    }
    //if (!specialCharacterSearch(check, "Vendor Name")) return;
    
    check = trimString(document.getElementById("vendor.accountNumber").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Account Number must be provided.');
        return;
    }
    //if (!specialCharacterSearch(check, "Account Number")) return;
    
    
    Ext.form.crudForm.submit({
        waitMsg:'<s:if test="vendor != null">Updating</s:if><s:else>Creating</s:else> Vendor...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            Ext.grid.vendorsGridDs.reload();
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
        text:'<s:if test="vendor != null">Update</s:if><s:else>Create</s:else>', 
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

    setFocus('vendor.code');
    
    new Ext.KeyNav(Ext.form.crudForm.getEl(), {
        'enter': function(e) {
            submit();
        },
        'scope': Ext.form.crudForm
    });
    
});
</script>

<s:if test="vendor != null">
<form action="vendor!editSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="vendor.id"/>
</s:if><s:else>
<form action="vendor!createSubmit.bc" name="crudform" id="crudform" class="formular">
</s:else>

    <fieldset>
        <legend>Vendor Information</legend>
        
        <table>
            <tr>
            <td valign="top">
        
                <table>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> Vendor Code:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.code" maxlength="50" id="vendor.code" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> Vendor Name:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.vendorName" maxlength="100" id="vendor.vendorName" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> Account  Number:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.accountNumber" maxlength="100" id="vendor.accountNumber" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Shipping Company:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.shippingCompany" maxlength="100" id="vendor.shippingCompany" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Address 1:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.address1" maxlength="255" id="vendor.address1" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Address 2:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.address2" maxlength="255" id="vendor.address2" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Address 3:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.address3" maxlength="255" id="vendor.address3" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>City:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.city" maxlength="50" id="vendor.city" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>State:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.state" maxlength="50" id="vendor.state" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Zip:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.zip" maxlength="25" id="vendor.zip" cssClass="text-input" />
                        </td>
                    </tr>
            </table>
        </td>
        <td style="padding-left:15px;" valign="top">
                <table>
                    <tr>
                        <td align="right">
                        <span>Email 1:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.email1" maxlength="128" id="vendor.email1" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Email 2:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.email2" maxlength="255" id="vendor.email2" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Work Phone:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.workPhone" maxlength="25" id="vendor.workPhone" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Home Phone:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.homePhone" maxlength="25" id="vendor.homePhone" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Cell Phone:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.cellPhone" maxlength="25" id="vendor.cellPhone" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Fax:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.fax" maxlength="25" id="vendor.fax" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Terms:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="vendor.terms" maxlength="50" id="vendor.terms" cssClass="text-input" />
                        </td>
                    </tr>
                    
            </table>
        </td>
        </tr>
        </table>

    </fieldset>
        
</form>

