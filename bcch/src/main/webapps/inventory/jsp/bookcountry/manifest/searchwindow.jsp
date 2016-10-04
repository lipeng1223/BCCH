<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

function submit() {
    document.getElementById("mansearchwinform").submit();
}
function cancel(){
    Ext.manSearchWindow.close();
}

Ext.onReady(function(){
    Ext.form.manSearchWinForm = new Ext.form.BasicForm("mansearchwinform");
    Ext.manSearchWindow.getBottomToolbar().addFill();
    Ext.manSearchWindow.getBottomToolbar().add({
        id:'submitButton', 
        type: 'submit',
        cls:'x-btn-text-icon', 
        icon:'/images/accept.png', 
        text:'Search', 
        handler:submit, 
        disabled:false
    });
    Ext.manSearchWindow.getBottomToolbar().addSeparator();
    Ext.manSearchWindow.getBottomToolbar().add({
        id:'cancelButton', 
        cls:'x-btn-text-icon', 
        icon:'/images/cancel.png', 
        text:'Cancel', 
        handler:cancel, 
        disabled:false
    });

    setFocus("manifestSearchWindow-searchval0");
    
    new Ext.KeyNav(Ext.form.manSearchWinForm.getEl(), {
        'enter': function(e) {
            submit();
        },
        'scope': Ext.form.manSearchWinForm
    });

});
</script>

<div id="searchpanel">
    <form class="formular" action="manifest!search.bc" id="mansearchwinform" method="post">
        <s:set name="manifestSearchPrefix" value="%{'manifestSearchWindow-'}"/>
        <s:set name="searchNames" value="searchNames"/>
        <%@ include file="search.jspf" %>
    </form>

</div>
