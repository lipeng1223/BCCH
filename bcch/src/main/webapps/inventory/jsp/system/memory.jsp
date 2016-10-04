<%@ include file="../html-start.jspf" %>

<head>
<%@ include file="../html-head.jspf" %>

<script language="JavaScript" type="text/javascript">
Ext.onReady(function(){
    
    <s:set name="activeMenu" value="%{'system'}"/>
    <%@ include file="../div-header-menu.jspf" %>

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
            bodyCfg : {style: {'border-bottom':'1px solid #999'} },
            items: [tb]
        },{
            region:'center',
            collapsible: false,
            type:'xpanel',
            title: 'JBoss Memory',
            border   : false,
            bodyBorder: false,
            layout: 'fit',
            autoScroll: true,
            id: 'contentpanel',
            autoLoad:'/jmx-console/HtmlAdaptor?action=invokeOp&arg0=True&methodIndex=0&name=jboss.system%3Atype%3DServerInfo'
        },{
            region: 'south',
            margins: '0 0 0 0',
            collapsible: false,
            height: 24,
            border: true,
            //bodyCfg : {style: {'background':'#F0F4F5 url(/images/tbbg3.png) repeat-x scroll left top'} },
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

<%@ include file="../div-header.jspf"%>

<%@ include file="../div-footer.jspf" %>

</body>
</html>

