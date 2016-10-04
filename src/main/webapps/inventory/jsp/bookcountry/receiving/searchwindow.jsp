<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

function submit() {
    document.getElementById("recsearchwinform").submit();
}
function cancel(){
    Ext.recSearchWindow.close();
}

Ext.onReady(function(){
    Ext.form.recSearchWinForm = new Ext.form.BasicForm("recsearchwinform");
    Ext.recSearchWindow.getBottomToolbar().addFill();
    Ext.recSearchWindow.getBottomToolbar().add({
        id:'submitButton', 
        type: 'submit',
        cls:'x-btn-text-icon', 
        icon:"/images/zoom.png", 
        text:'Search', 
        handler:submit, 
        disabled:false
    });
    Ext.recSearchWindow.getBottomToolbar().addSeparator();
    Ext.recSearchWindow.getBottomToolbar().add({
        id:'cancelButton', 
        cls:'x-btn-text-icon', 
        icon:'/images/cancel.png', 
        text:'Cancel', 
        handler:cancel, 
        disabled:false
    });

    setFocus("recSearchWindow-searchval0");
    
    new Ext.KeyNav(Ext.form.recSearchWinForm.getEl(), {
        'enter': function(e) {
            submit();
        },
        'scope': Ext.form.recSearchWinForm
    });
});
</script>

<div id="searchpanel">
    <form class="formular" action="receiving!search.bc" id="recsearchwinform" method="post">
        <s:set name="recSearchPrefix" value="%{'recSearchWindow-'}"/>
        <s:set name="searchNames" value="searchNames"/>
        <%@ include file="search.jspf" %>
    </form>

</div>
