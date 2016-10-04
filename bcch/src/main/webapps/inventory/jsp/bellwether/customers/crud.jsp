<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

function submit(){

    var check = trimString(document.getElementById("customer.code").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Customer Code must be provided.');
        return;
    }
    //if (!specialCharacterSearch(check, "Customer Code")) return;
    
    check = trimString(document.getElementById("customer.companyName").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Customer Name must be provided.');
        return;
    }
    //if (!specialCharacterSearch(check, "Customer Name")) return;
    
    check = trimString(document.getElementById("customer.contactName").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'Contact Name must be provided.');
        return;
    }
    //if (!specialCharacterSearch(check, "Contact Name")) return;
    
    check = trimString(document.getElementById("customer.discount").value);
    if (check.length > 0){
        if (!checkInt(check, false)){
            Ext.MessageBox.alert('Error', "Discount must be a number");
            return;            
        }
    }
    
    Ext.form.crudForm.submit({
        waitMsg:'<s:if test="customer != null">Updating</s:if><s:else>Creating</s:else> Customer...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            Ext.grid.customersGridDs.reload();
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
        text:'<s:if test="customer != null">Update</s:if><s:else>Create</s:else>', 
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

    setFocus('customer.code');
    
    new Ext.KeyNav(Ext.form.crudForm.getEl(), {
        'enter': function(e) {
            submit();
        },
        'scope': Ext.form.crudForm
    });
    
});
</script>

<s:if test="customer != null">
<form action="customer!editSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="customer.id"/>
</s:if><s:else>
<form action="customer!createSubmit.bc" name="crudform" id="crudform" class="formular">
</s:else>

    <fieldset>
        <legend>Customer Information</legend>
        <table>
            <tr>
            <td valign="top">
            
                <table>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> Customer Code:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.code" maxlength="50" id="customer.code" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> Company Name:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.companyName" maxlength="100" id="customer.companyName" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span><span class="inputrequired">*</span> Contact Name:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.contactName" maxlength="100" id="customer.contactName" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Address 1:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.address1" maxlength="255" id="customer.address1" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Address 2:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.address2" maxlength="255" id="customer.address2" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Address 3:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.address3" maxlength="255" id="customer.address3" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>City:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.city" maxlength="50" id="customer.city" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>State:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.state" maxlength="50" id="customer.state" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Zip:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.zip" maxlength="25" id="customer.zip" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Country:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.country" maxlength="50" id="customer.country" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Sales Rep:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:if test="isBcSalesRepAdmin">
                        <s:textfield key="customer.salesRep" maxlength="50" id="customer.salesRep" cssClass="text-input" />
                        </s:if><s:else>
                        <s:textfield key="customer.salesRep" maxlength="50" id="customer.salesRep" cssClass="text-input" readonly="true"/>
                        </s:else>
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
                        <s:textfield key="customer.email1" maxlength="128" id="customer.email1" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Email 2:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.email2" maxlength="255" id="customer.email2" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Work Phone:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.workPhone" maxlength="25" id="customer.workPhone" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Home Phone:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.homePhone" maxlength="25" id="customer.homePhone" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Cell Phone:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.cellPhone" maxlength="25" id="customer.cellPhone" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Fax:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.fax" maxlength="25" id="customer.fax" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Terms:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.terms" maxlength="50" id="customer.terms" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Discount:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textfield key="customer.discount" maxlength="4" id="customer.discount" cssClass="text-input" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Mailing List:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <select name="customer.maillist">
                            <option value="false" <s:if test="!customer.maillist">selected="selected"</s:if>>No</option>
                            <option value="true" <s:if test="customer.maillist">selected="selected"</s:if>>Yes</option>
                        </select> 
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Book Club:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <select name="customer.bookclub"> 
                            <option value="false" <s:if test="!customer.bookclub">selected="selected"</s:if>>No</option>
                            <option value="true" <s:if test="customer.bookclub">selected="selected"</s:if>>Yes</option>
                        </select> 
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Book Fair:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <select name="customer.bookfair">
                            <option value="false" <s:if test="!customer.bookfair">selected="selected"</s:if>>No</option>
                            <option value="true" <s:if test="customer.bookfair">selected="selected"</s:if>>Yes</option>
                        </select> 
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Tax:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <select name="customer.tax">
                            <option value="false" <s:if test="!customer.tax">selected="selected"</s:if>>No</option>
                            <option value="true" <s:if test="customer.tax">selected="selected"</s:if>>Yes</option>
                        </select> 
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
                        <span>Comment 1:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textarea key="customer.comment1" id="customer.comment1" cols="50" rows="5" cssClass="text-area" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Comment 2:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textarea key="customer.comment2" id="customer.comment2" cols="50" rows="5" cssClass="text-area" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                        <span>Picklist Comment:</span>
                        </td>
                        <td align="left" style="padding-left:10px;">
                        <s:textarea key="customer.picklistComment" id="customer.picklistComment" cols="50" rows="5" cssClass="text-area" />
                        </td>
                    </tr>
                </table>
            </td>
            </tr>
        </table>

    </fieldset>
    
</form>

