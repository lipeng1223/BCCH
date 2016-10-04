<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<style type="text/css">
.x-form-date-trigger {
    margin-left:130px;
}
</style>

<script language="JavaScript" type="text/javascript">

function submit() {
    document.getElementById("ordersearchwinform").submit();
}
function cancel(){
    Ext.orderSearchWindow.close();
}

Ext.onReady(function(){
    Ext.form.orderSearchWinForm = new Ext.form.BasicForm("ordersearchwinform");
    Ext.orderSearchWindow.getBottomToolbar().addFill();
    Ext.orderSearchWindow.getBottomToolbar().add({
        id:'submitButton', 
        type: 'submit',
        cls:'x-btn-text-icon', 
        icon:"/images/zoom.png", 
        text:'Search', 
        handler:submit, 
        disabled:false
    });
    Ext.orderSearchWindow.getBottomToolbar().addSeparator();
    Ext.orderSearchWindow.getBottomToolbar().add({
        id:'cancelButton', 
        cls:'x-btn-text-icon', 
        icon:'/images/cancel.png', 
        text:'Cancel', 
        handler:cancel, 
        disabled:false
    });

    setFocus("orderSearchWindow-searchval0");
    
    new Ext.KeyNav(Ext.form.orderSearchWinForm.getEl(), {
        'enter': function(e) {
            submit();
        },
        'scope': Ext.form.orderSearchWinForm
    });
});
</script>

<div id="searchpanel">
    <form class="formular" action="order!search.bc" id="ordersearchwinform" method="post">
        <s:set name="orderSearchPrefix" value="%{'orderSearchWindow-'}"/>
        <s:set name="searchNames" value="searchNames"/>
        <%@ include file="search.jspf" %>
    </form>

</div>
