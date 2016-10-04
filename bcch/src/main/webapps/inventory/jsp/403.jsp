<!-- 403 -->
<%@ include file="html-start.jspf" %>
<head>
<%@ include file="html-head.jspf" %>

<%@ page isErrorPage="true" %>

<title>Not Authorized</title>

</head>
<body class="page">

<div id="container">

    <table width="100%" cellspacing="0" cellpadding="0" id="header" height="30px">
        <tr>
            <td valign="top" width="300px" nowrap>
                <%@ include file="div-header.jspf" %>
            </td>
            <td valign="top" nowrap>
                <div class="menulinks">
                </div>
            </td>
        </tr>
    </table>    
    
<!-- start error messages -->
    <div id="wrapper">
        
        <center>
        <div id="content" style="margin-top:50px;">
            <div class="head">
                <div class="pageinfo">
                <h1>403 - Unauthorized</h1>
                </div>
            </div>
            
            <div class="item" style="margin-top:20px;">
                You are not authorized to complete the requested action.
            </div>

        </div>
        </center>

        <s:if test="isAdmin()">
            <div style="overflow:auto;height:350px;border:1px solid #ddd;margin-left:15px;margin-right:15px;">
            <s:property value="%{exception.message}"/><br/><br/>
            <pre>
            <s:property value="%{exceptionStack}"/>
            </pre>
            </div>
        </s:if>
                    
        <div style="clear:both"></div>
    </div> <%-- end wrapper --%>
<!-- end error messages -->

</div><%-- end container --%>


<%@ include file="div-footer-noviewport.jspf" %>

</body>
</html>