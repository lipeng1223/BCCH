<%@ include file="../html-start.jspf" %>
<head>
<%@ include file="../html-head.jspf" %>

<title>Book Country Inventory</title>

<jwr:script src="/bundles/userBundle.js"/> 

<s:set name="stateSession" value="%{'user-list-state'}"/>
<%@ include file="../statemanagement.jspf" %>

<script language="JavaScript" type="text/javascript">
Ext.onReady(function(){

    <s:set name="activeMenu" value="%{'users'}"/>
    <%@ include file="../div-header-menu.jspf" %>

    var contentpanel = new Ext.Panel({
        id       : 'dualPanel',
        layout   : 'border',
        border   : false,
        bodyBorder: false,
        items : [
            {
               region: 'center',
               border   : false,
               bodyBorder: false,
               split:true,
               type: 'xpanel',
               layout: 'fit',
               id: 'listpanel'
            }, {
                region: 'north',
                id: 'northpanel',
                layout: 'fit',
                height:100,
                margins: '0 0 0 0',
                title: 'User Audit',
                contentEl: 'useraudit'
            }
        ]
    });


    new Ext.Viewport({
        id: 'mainviewport',
        layout: 'border',
        layoutConfig: {
            minWidth: 800,
            minHeight: 500
        },
        //bufferResize:true,
        items: [{
            region: 'north',
            id: 'northpanel',
            border: false,
            collapsible: false,
            layout: 'fit',
            height:40,
            margins: '0 0 0 0',
            cls: 'invtoolbar',
            bodyCfg : {style: {'border-bottom':'1px solid #999'} },
            items: [tb]
        },{
            region: 'center',
            margins: '0 0 0 0',
            collapsible: false,
            border: true,
            layout: 'fit',
            id: 'contentpanel',
            items: [contentpanel]
        },{
            region: 'south',
            margins: '0 0 0 0',
            collapsible: false,
            height: 24,
            border: true,
            bodyCfg : {style: {'background-color':'#ddd'} },
            id: 'footerpanel',
            layout: 'fit',
            contentEl: 'invfooter'
        }]
    });

});
</script>

</head>
<body class="page">

<%@ include file="../div-header.jspf" %>

<div id="useraudit" >
    User: 
    <select>
        <s:iterator value="queryResults.data" status="st" var="res">
            <option value="<s:property value="#res.id"/>"><s:property value="#res.username"/> - <s:property value="#res.firstName"/> <s:property value="#res.lastName"/></option>
        </s:iterator>
    </select>
</div>

<div style="display:none">

<inv:table tableName="users" sortable="true" exportable="true" tableTitle="Users"
dataAction="user!listData.bc" table="${listTable}" addToContainer="listpanel" stateful="true" tableIcon="user_icon"/>

</div>

<%@ include file="../div-footer.jspf" %>

</body>
</html>
