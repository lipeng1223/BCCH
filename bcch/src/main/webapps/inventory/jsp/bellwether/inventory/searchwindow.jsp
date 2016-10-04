<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

function submit() {
    //Ext.form.invSearchWinForm.submit();
    document.getElementById("inventorysearchwinform").submit();
}
function cancel(){
    Ext.invSearchWindow.close();
}

Ext.onReady(function(){
    Ext.form.invSearchWinForm = new Ext.form.BasicForm("inventorysearchwinform");
    Ext.invSearchWindow.getBottomToolbar().addFill();
    Ext.invSearchWindow.getBottomToolbar().add({
        id:'submitButton', 
        type: 'submit',
        cls:'x-btn-text-icon', 
        icon:"/images/zoom.png", 
        text:'Search', 
        handler:submit, 
        disabled:false
    });
    Ext.invSearchWindow.getBottomToolbar().addSeparator();
    Ext.invSearchWindow.getBottomToolbar().add({
        id:'cancelButton', 
        cls:'x-btn-text-icon', 
        icon:'/images/cancel.png', 
        text:'Cancel', 
        handler:cancel, 
        disabled:false
    });

    setFocus("invSearchWin-searchval0");

    new Ext.KeyNav(Ext.form.invSearchWinForm.getEl(), {
        'enter': function(e) {
            submit();
        },
        'scope': Ext.form.invSearchWinForm
    });
});
</script>

<div id="searchpanel">
    <form class="formular" action="inventory!search.bc" id="inventorysearchwinform" method="post">
        <s:set name="invSearchPrefix" value="%{'invSearchWin-'}"/>
        <s:set name="searchNames" value="searchNames"/>
        <%@ include file="search.jspf" %>
        <input type="submit" style="width: 0px; height: 0px; position: absolute; left: -50px; top: -50px;"/>
    </form>

</div>
