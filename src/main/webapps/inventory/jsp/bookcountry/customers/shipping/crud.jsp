<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

function submit(){

    var check = trimString(document.getElementById("customerShipping.address1").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Address 1 must be provided.');
        return;
    }
    
    check = trimString(document.getElementById("customerShipping.city").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'City must be provided.');
        return;
    }

    check = trimString(document.getElementById("customerShipping.state").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'State must be provided.');
        return;
    }
    
    check = trimString(document.getElementById("customerShipping.zip").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Zip must be provided.');
        return;
    }
    
    Ext.form.crudForm.submit({
        waitMsg:'<s:if test="customer != null">Updating</s:if><s:else>Creating</s:else> customerShipping...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            // reload the detail display
            updateCustomerDetail(true);
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
        text:'<s:if test="customerShipping != null">Update</s:if><s:else>Create</s:else>', 
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

    setFocus('customerShipping.address1');
    
    new Ext.KeyNav(Ext.form.crudForm.getEl(), {
        'enter': function(e) {
            submit();
        },
        'scope': Ext.form.crudForm
    });
});
</script>

<s:if test="customerShipping != null">
<form action="customershipping!editSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="customerShipping.id"/>
</s:if><s:else>
<form action="customershipping!createSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden name="customerId" value="%{id}"/>
</s:else>

    <fieldset>
        <legend>Shipping Address Information</legend>
        <table>
            <tr>
            <td valign="top">
            
                <table>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> Address 1:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customerShipping.address1" maxlength="255" id="customerShipping.address1" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Address 2:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customerShipping.address2" maxlength="255" id="customerShipping.address2" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Address 3:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customerShipping.address3" maxlength="255" id="customerShipping.address3" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> City:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customerShipping.city" maxlength="50" id="customerShipping.city" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> State:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customerShipping.state" maxlength="50" id="customerShipping.state" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> Zip:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customerShipping.zip" maxlength="25" id="customerShipping.zip" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Country:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customerShipping.country" maxlength="50" id="customerShipping.country" cssClass="text-input" />
                        </td>
                    </tr>
                    
            </table>
            
            
            </td>
            <td style="padding-left:15px;" valign="top">
            
                <table>
                    <tr>
                        <td align="right">
                        <span>Email:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customerShipping.email" maxlength="128" id="customerShipping.email" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Phone:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customerShipping.phone" maxlength="25" id="customerShipping.phone" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Work Phone:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customerShipping.workPhone" maxlength="15" id="customerShipping.workPhone" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Work Ext:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customerShipping.workExt" maxlength="15" id="customerShipping.workExt" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Home Phone:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customerShipping.homePhone" maxlength="15" id="customerShipping.homePhone" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Fax:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customerShipping.fax" maxlength="25" id="customerShipping.fax" cssClass="text-input" />
                        </td>
                    </tr>
                    
                </table>
            
            </td>
            </tr>
            <tr>
            <td colspan="2">
                <table>
                    <tr>
                        <td align="right">
                        <span>Comment:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textarea key="customerShipping.comment" id="customerShipping.comment" cols="50" rows="5" cssClass="text-area" />
                        </td>
                    </tr>
                </table>
            </td>
            </tr>
        </table>

    </fieldset>
    
</form>

