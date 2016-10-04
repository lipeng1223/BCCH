<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>
<%
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
{
  success: false,
  error: 'The following fields are not valid: <s:iterator value="fieldErrors" var="fe" status="status"><s:if test="#status.index > 0">, </s:if><s:property value="#fe.key"/></s:iterator>'
 }
