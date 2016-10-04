<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

/**
 * Toggle all the user role checkboxes.
 */
function toggle() { 

    var toggleCb = document.getElementById('toggleAll');

    if(toggleCb) {
        
        var cbs = document.getElementsByName('userRoles');

        for(var i = 0; i < cbs.length; i++) {
            cbs[i].checked = toggleCb.checked;            
        }
    }
}

function submit(){
    var uname = trimString(document.getElementById("username").value);
    if (uname.length == 0 || trimString(uname).length == 0){
        Ext.Msg.alert('Error', 'Username must be provided.');
        return;
    }
    if (!specialCharacterSearch(uname, "Username")) return;
    var p = trimString(document.getElementById("password").value);
    var c = trimString(document.getElementById("confirmpassword").value);
    if (p.length < 6 || trimString(p).length < 6){
        Ext.Msg.alert('Error', 'Password must be at least 6 characters.');
        return;
    }
    if (p != c){
        Ext.Msg.alert('Error', 'Passwords do not match.');
        return;
    }
    if (!specialCharacterSearch(p, "Password")) return;

    var pin = trimString(document.getElementById("pin").value);
    if (pin.length == 0){
        Ext.Msg.alert('Error', 'Pin must be provided.');
        return;
    }
    if (!specialCharacterSearch(pin, "Pin")) return;
    if (!checkPositiveInt(pin)){
        Ext.Msg.alert('Error', 'Pin must be a number.');
        return;
    }
    
    var selected = false;
    var buttonGroup = document.forms["crudform"].userRoles;
    for (var i=0; i<buttonGroup.length; i++) {
        if (buttonGroup[i].checked) {
            selected = true;
        }
    }
    if (!selected){
        Ext.Msg.alert('Error', 'You must select at least one Role.');
        return;
    }

    if (!formSpecialCharacterCheck(document.getElementById("crudform"))){
        return;
    }
    
    Ext.form.crudForm.submit({
        waitMsg:'<s:if test="user != null">Updating</s:if><s:else>Creating</s:else> User...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            Ext.grid.usersGridDs.reload();
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
        text:'<s:if test="user != null">Update</s:if><s:else>Create</s:else>', 
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

    setFocus('username');
    
    new Ext.KeyNav(Ext.form.crudForm.getEl(), {
        'enter': function(e) {
            submit();
        },
        'scope': Ext.form.crudForm
    });
    
});
</script>

<s:if test="user != null">
<form action="user!editSubmit.bc" name="crudform" id="crudform" class="formular">
<s:hidden key="user.id"/>
</s:if><s:else>
<form action="user!createSubmit.bc" name="crudform" id="crudform" class="formular">
</s:else>

    <fieldset>
        <legend>User Information</legend>
        <table>
            <tr>
                <td align="right">
                <span><span class="inputrequired">*</span> Username:</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <s:textfield key="user.username" maxlength="64" id="username" cssClass="text-input" />
                </td>
            </tr>
            <tr>
                <td align="right">
                <span><span class="inputrequired">*</span> Password (at least 6 characters):</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <input type="password" maxlength="64" name="user.password" id="password" value="<s:property value="user.password"/>" class="text-input"/>
                </td>
            </tr>
            <tr>
                <td align="right">
                <span><span class="inputrequired">*</span> Confirm Password:</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <input type="password" maxlength="64" id="confirmpassword" name="user.confirmPassword" value="<s:property value="user.password"/>" class="text-input"/>
                </td>
            </tr>
            <tr>
                <td align="right">
                <span><span class="inputrequired">*</span> PIN:</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <s:textfield key="user.pin" maxlength="4" id="pin" cssClass="text-input" />
                </td>
            </tr>
            <tr>
                <td align="right">
                <span>Email:</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <s:textfield key="user.email" maxlength="255" id="email" cssClass="text-input" />
                </td>
            </tr>
            <tr>
                <td align="right">
                <span>First Name:</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <s:textfield key="user.firstName" maxlength="128" id="firstName" cssClass="text-input" />
                </td>
            </tr>
            <tr>
                <td align="right">
                <span>Last Name:</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <s:textfield key="user.lastName" maxlength="128" id="lastName" cssClass="text-input" />
                </td>
            </tr>
            <tr>
                <td align="right">
                <span>Employee ID:</span>
                </td>
                <td align="left" style="padding-left:10px;">
                <s:textfield key="user.employeeId" maxlength="64" id="employeeId" cssClass="text-input" />
                </td>
            </tr>
            <tr>
                <td align="right" valign="top">
                <span><span class="inputrequired">*</span> Roles:</span><br/>
                </td>
                <td align="left" style="padding-left:10px;">
                <input id="toggleAll" type="checkbox" class="checkbox" name="toggleAll" value="" onclick="toggle();" title="Toggle All"/><br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcInvAdmin" id="BcInvAdmin" <s:if test="user.hasRole('BcInvAdmin')">checked</s:if>/><label for="BcInvAdmin"> Book Country Inventory Admin</label><br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcInvViewer" id="BcInvViewer" <s:if test="user.hasRole('BcInvViewer')">checked</s:if>/><label for="BcInvViewer"> Book Country Inventory Viewer</label><br/>
                <br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcRecAdmin" id="BcRecAdmin" <s:if test="user.hasRole('BcRecAdmin')">checked</s:if>/><label for="BcRecAdmin"> Book Country Receiving Admin</label><br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcRecViewer" id="BcRecViewer" <s:if test="user.hasRole('BcRecViewer')">checked</s:if>/><label for="BcRecViewer"> Book Country Receiving Viewer</label><br/>
                <br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcOrderAdmin" id="BcOrderAdmin" <s:if test="user.hasRole('BcOrderAdmin')">checked</s:if>/><label for="BcOrderAdmin"> Book Country Order Admin</label><br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcOrderViewer" id="BcOrderViewer" <s:if test="user.hasRole('BcOrderViewer')">checked</s:if>/><label for="BcOrderViewer"> Book Country Order Viewer</label><br/>
                <br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcCustomerAdmin" id="BcCustomerAdmin" <s:if test="user.hasRole('BcCustomerAdmin')">checked</s:if>/><label for="BcCustomerAdmin"> Book Country Customer Admin</label><br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcCustomerViewer" id="BcCustomerViewer" <s:if test="user.hasRole('BcCustomerViewer')">checked</s:if>/><label for="BcCustomerViewer"> Book Country Customer Viewer</label><br/>
                <br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcVendorAdmin" id="BcVendorAdmin" <s:if test="user.hasRole('BcVendorAdmin')">checked</s:if>/><label for="BcVendorAdmin"> Book Country Vendor Admin</label><br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcVendorViewer" id="BcVendorViewer" <s:if test="user.hasRole('BcVendorViewer')">checked</s:if>/><label for="BcVendorViewer"> Book Country Vendor Viewer</label><br/>
                <br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcUserAdmin" id="BcUserAdmin" <s:if test="user.hasRole('BcUserAdmin')">checked</s:if>/><label for="BcUserAdmin"> Book Country User Admin</label><br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcUserViewer" id="BcUserViewer" <s:if test="user.hasRole('BcUserViewer')">checked</s:if>/><label for="BcUserViewer"> Book Country User Viewer</label><br/>
                <br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcManifestAdmin" id="BcManifestAdmin" <s:if test="user.hasRole('BcManifestAdmin')">checked</s:if>/><label for="BcManifestAdmin"> Book Country Manifest Admin</label><br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcManifestViewer" id="BcManifestViewer" <s:if test="user.hasRole('BcManifestViewer')">checked</s:if>/><label for="BcManifestViewer"> Book Country Manifest Viewer</label><br/>
                <br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcBackStockAdmin" id="BcManifestAdmin" <s:if test="user.hasRole('BcBackStockAdmin')">checked</s:if>/><label for="BcBackStockAdmin"> Book Country Back Stock Admin</label><br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcBackStockViewer" id="BcManifestViewer" <s:if test="user.hasRole('BcBackStockViewer')">checked</s:if>/><label for="BcBackStockViewer"> Book Country Back Stock Viewer</label><br/>
                <br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BcSalesRepAdmin" id="BcSalesRepAdmin" <s:if test="user.hasRole('BcSalesRepAdmin')">checked</s:if>/><label for="BcSalesRepAdmin"> Book Country Sales Rep Admin</label><br/>
                <br/>
                <br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BellInvAdmin" id="BellInvAdmin" <s:if test="user.hasRole('BellInvAdmin')">checked</s:if>/><label for="BellInvAdmin"> Bellwether Inventory Admin</label><br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BellInvViewer" id="BellInvViewer" <s:if test="user.hasRole('BellInvViewer')">checked</s:if>/><label for="BellInvViewer"> Bellwether Inventory Viewer</label><br/>
                <br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BellRecAdmin" id="BellRecAdmin" <s:if test="user.hasRole('BellRecAdmin')">checked</s:if>/><label for="BellRecAdmin"> Bellwether Receiving Admin</label><br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BellRecViewer" id="BellRecViewer" <s:if test="user.hasRole('BellRecViewer')">checked</s:if>/><label for="BellRecViewer"> Bellwether Receiving Viewer</label><br/>
                <br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BellOrderAdmin" id="BellOrderAdmin" <s:if test="user.hasRole('BellOrderAdmin')">checked</s:if>/><label for="BellOrderAdmin"> Bellwether Order Admin</label><br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="BellOrderViewer" id="BellOrderViewer" <s:if test="user.hasRole('BellOrderViewer')">checked</s:if>/><label for="BellOrderViewer"> Bellwether Order Viewer</label><br/>
                <br/>
                <input type="checkbox" class="checkbox" name="userRoles" value="SystemAdmin" id="SystemAdmin" <s:if test="user.hasRole('SystemAdmin')">checked</s:if>/><label for="SystemAdmin"> System Admin</label><br/>
                
                </td>
            </tr>
    </table>

    </fieldset>
    
</form>

