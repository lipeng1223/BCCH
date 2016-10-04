<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<style type="text/css">
.x-form-date-trigger {
    margin-left:130px;
}
</style>

<script language="JavaScript" type="text/javascript">

function submit(){
    var check = trimString(document.getElementById("receiving.poNumber").value);
    if (check.length == 0){
        Ext.Msg.alert('Error', 'PO Number must be provided.');
        return;
    }
    Ext.form.crudForm.submit({
        waitMsg:'<s:if test="receiving != null">Updating</s:if><s:else>Creating</s:else> Receiving...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            Ext.crudWindow.close();
            if (Ext.grid.receivingsGrid && <s:if test="receiving != null">true</s:if><s:else>false</s:else>){
                Ext.grid.receivingsGridDs.reload();
            } else {
                interPageMove("receiving!view.bc?id="+action.result.id);
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
        text:<s:if test="receiving != null">'Update'</s:if><s:else>'Create'</s:else>, 
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

    var df = new Ext.form.DateField({
        hideLabel: true,
        name: 'dateString',
        width:150,
        allowBlank: true,
        <s:if test="receiving != null">value:'<s:date name="receiving.poDate" format="MM/dd/yyyy" />',</s:if><s:else>value:'<s:date name="currentDate" format="MM/dd/yyyy" />',</s:else>
        style: 'margin-top:4px;margin-bottom:6px;'
    });
    df.render("receivingdatediv");

    setFocus("receiving.poNumber");
    
    new Ext.KeyNav(Ext.form.crudForm.getEl(), {
        'enter': function(e) {
            submit();
        },
        'scope': Ext.form.crudForm
    });
});

</script>

<s:if test="receiving != null">
<form action="receiving!editSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="receiving.id"/>
</s:if><s:else>
<form action="receiving!createSubmit.bc" name="crudform" id="crudform" class="formular">
</s:else>
        
            <fieldset>
                <legend><s:if test="receiving != null">Edit</s:if><s:else>New</s:else> Receiving Information</legend>
    
            <table>
                <tr><td><div style="height:5px;"></div></td></tr>
                <tr>
                    <td align="right">
                    <span>PO Number:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                        <s:textfield key="receiving.poNumber" maxlength="255" id="receiving.poNumber" cssClass="text-input" />
                    </td>
                </tr>
                <tr><td><div style="height:5px;"></div></td></tr>
                <tr>
                    <td align="right">
                    <span>PO Date:</span>
                    </td>
                    <td style="padding-left:10px;">
                        <div id="receivingdatediv"></div>
                    </td>
                </tr>
                <tr><td><div style="height:5px;"></div></td></tr>
                <tr>
                    <td align="right">
                    <span>Vendor:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                        <s:select name="vendorId"
                               list="vendors"
                               listKey="id"
                               listValue="%{code + ' - ' + vendorName}"
                               headerKey="-1" headerValue="Select A Vendor"
                               value="receiving.vendor.id"
                        />
                    </td>
                </tr>
                <tr><td><div style="height:5px;"></div></td></tr>
                <tr>
                    <td align="right">
                    <span>Publisher:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:textfield key="receiving.publisherCode" maxlength="50" id="receiving.publisherCode" cssClass="text-input" />
                    </td>
                </tr>
                <tr><td><div style="height:5px;"></div></td></tr>
                <tr>
                    <td align="right">
                    <span>Comment:</span>
                    </td>
                    <td align="left" style="padding-left:10px;">
                    <s:textfield key="receiving.comment" maxlength="255" id="receiving.comment" cssClass="text-input" />
                    </td>
                </tr>

        </table>
        
        
        </fieldset>

        
</form>

