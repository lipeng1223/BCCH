<%@ include file="../html-start.jspf" %>
<head>
<%@ include file="../html-head.jspf" %>

<style>
.tomcatstatus h1 {
margin-left:10px;
font-size:16px;
font-weight:bold;
margin-top:15px;
}
.tomcatstatus h2 {
margin-left:10px;
font-size:14px;
font-weight:bold;
margin-top:15px;
}
.tomcatstatus p {
margin-left:10px;
margin-top:15px;
font-size:12px;
}
.tomcatstatus table {
margin-left:10px;
margin-top:15px;
font-size:12px;
}
.tomcatstatus a,.tomcatstatus a:active,.tomcatstatus a:hover,.tomcatstatus a:visited {
color:#05295d !important;
}
</style>

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
            title: 'Tomcat Status',
            border   : false,
            bodyBorder: false,
            layout: 'fit',
            autoScroll: true,
            id: 'contentpanel',
            cls: 'tomcatstatus',
            autoLoad:'/secure/status?full=true'
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

