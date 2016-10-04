<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">
function submit(){

    Ext.form.uploadForm.submit({
        waitMsg:'Uploading Customers...',
        failure: function(form, action){
            Ext.MessageBox.alert('Status', action.result.error);
        },
        success: function(form, action){
            Ext.grid.customersGridDs.reload();
            Ext.uploadWindow.close();
        }
    });
}
function cancel(){
    Ext.uploadWindow.close();
}
Ext.onReady(function(){
    Ext.form.uploadForm = new Ext.form.BasicForm("uploadform");
    
    Ext.uploadWindow.getBottomToolbar().addFill();
    Ext.uploadWindow.getBottomToolbar().add({
        id:'submitButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'Upload', 
        handler:submit, 
        disabled:false
    });
    Ext.uploadWindow.getBottomToolbar().addSeparator();
    Ext.uploadWindow.getBottomToolbar().add({
        id:'cancelButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/cancel.png", 
        text:'Cancel', 
        handler:cancel, 
        disabled:false
    });
    
});
</script>

<div style="padding-left:15px;padding-top:15px;">
    <form action="customer!uploadMas90.bc" name="uploadform" id="uploadform"  method="POST" enctype="multipart/form-data">
        <input type="hidden" name="id" value="<s:property value="id"/>"/>
        MAS 90 Excel: <s:file name="upload" cssClass="text-input" style="width:400px" onkeydown="return ieCheckFileUpload();" onbeforeeditfocus="return false;"/>
        <br/><br/>
        <!--
        Example Order Item Import Excel: <a href="/templates/OrderItemImport.xls">Order Item Upload Template</a>
        -->
    </form>
</div>
