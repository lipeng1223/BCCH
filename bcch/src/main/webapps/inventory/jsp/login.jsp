<!-- LOGIN -->
<%@ include file="html-start.jspf" %>
<head>
<%@ include file="html-head.jspf" %>

<title>Inventory</title>

<script language="JavaScript" type="text/javascript">


Ext.onReady(function(){

    var tb = new Ext.Toolbar({
        //style: 'background:#F0F4F5 url(/images/tbbg2.png) repeat-x scroll left top'
    });
    tb.render('toolbarmenu');
    tb.addElement("thelogospan");
    tb.addSpacer();
    tb.addText(" ");
    tb.addElement("thetitle");
    tb.addSpacer();
    tb.addText(" ");
    tb.addText("Inventory");

    new Ext.Viewport({
        id: 'mainviewport',
        layout: 'border',
        layoutConfig: {
            minWidth: 800,
            minHeight: 500
        },
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
            region: 'center',
            margins: '0 0 0 0',
            collapsible: false,
            border: true,
            id: 'contentpanel',
            layout: 'fit',
            autoScroll:true,
            contentEl: 'container'
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
    
    new Ext.Button({
        id:'submitButton', 
        applyTo:'submitdiv', 
        cls:"x-btn-text-icon", 
        icon:"/images/accept.png", 
        text:'Log me in', 
        disabled:false,
        handler: function(){
            document.getElementById("loginform").submit();
        } 
    });

    var iever = getInternetExplorerVersion();

    if ( iever > -1 ) {
        if ( iever == 6.0 ) 
            document.getElementById("ie6warningdiv").style.display = "";
    }
    setFocus('username');
});
</script>

</head>
<body class="page">

<span id="headerfilllerspan"></span><%-- This is necessary for IE and EXT for some reason --%>
<span style="display:none">
<span id="thetitle"><span style="font-weight:bold !important;font-size:12px !important;">Book Country Clearing House</span></span>
<span id="thelogospan" style="padding-left:10px;padding-right:5px;"><img src="/images/book.png"/></span>
<div id="toolbarmenu"></div>
</span>

<div style="display:none"><!-- faster load no display div -->
<div id="container">

    <div id="wrapper">
        
        <div style="margin-top:50px;">
        
            <center>
            <div class="head" style="width:800px;border-bottom:1px solid #47484a;margin-bottom:15px;">
                <table width="100%" style="height:30px;">
                    <tr><td align="left" style="padding-left:50px;font-size:20px;">
                    Book Country Clearing House
                    <%-- <img src="/images/justtext-noline.png"/> --%>
                    </td><td align="right">
                    
                    </td></tr>
                </table>
            </div>
            </center>
            
            <center>
            <div class="formholder" style="width:800px;">
                <form action="j_security_check" method="post" id="loginform" class="formular formularborder" name="loginform">
                <table>
                    <tr>
                        <td>
                            <table>
                                <tr>
                                    <td align="center" style="padding-top:8px;padding-bottom:15px;font-size:20px;">Inventory Management System</td>
                                </tr>
                                <tr>
                                    <td style="padding-bottom:15px;"><img src="/images/bookicon.png" style="width:200px;"/></td>
                                </tr>
                            </table>
                        </td>
                        <td valign="middle" style="padding-left:25px;">
                            <fieldset>
                                <legend>User Login</legend>
                                    <%
                                        if (request.getParameter("error") != null && request.getParameter("error").equals("true")){
                                            
                                    %>
                                        <div class="loginerror" style="height:35px;color:#CC4040"><img src="/images/exclamation.png" border="0"/> &nbsp;Could not recognize your Username or Password.</div>
                                    <%
                                        }
                                    %>
                                <label>
                                    <span>Username:</span>
                                    <input type="text" name="j_username" class="text-input" style="width:300px;" id="username" maxlength="255"/>
                                </label>
                                <label>
                                    <span>Password:</span>
                                    <input type="password" name="j_password" class="text-input" style="width:300px;" id="password"  redisplay="false" maxlength="255"/>
                                </label>
                            </fieldset>
                            <div style="float:right;padding-top:12px;">
                            <div>
                                <div id="submitdiv"></div>                        
                            </div>
                            </div>
                            <%-- 
                            <a class="submit" href="javascript:document.loginform.submit();">Log me in</a> 
                            --%>
                            <input type="submit" style="width: 0px; height: 0px; position: absolute; left: -50px; top: -50px;"/>
                            <hr/>
                        </td>
                    </tr>
                </table>
                </form>
            </div>
            </center>
            

            <div id="ie6warningdiv" style="margin-top:10px;font-size:14px;display:none;">
            <center>
                <div>
                    <img src="/images/error.gif" align="top"/> 
                    You are using Internet Explorer 6.
                    <img src="/images/error.gif" align="top"/> 
                    <br/>
                    <br/>This site is best viewed with Internet Explorer 7, 8 or 9
                    <br/>
                    <br/>You may notice some performance and display inconsistencies.
                </div>
            </center>
            </div>

        </div>
        
    </div> <%-- end wrapper --%>

        

</div><%-- end container --%>
</div><%-- end display none div --%>
<%@ include file="div-footer.jspf" %>

</body>
</html>

