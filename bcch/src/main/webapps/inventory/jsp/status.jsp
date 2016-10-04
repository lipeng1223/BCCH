<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>
<%
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
{
  success: <s:property value="success"/>, 
  id: <s:property value="id" default="-1"/>, 
  error: '<s:property value="@org.apache.commons.lang.StringEscapeUtils@escapeJavaScript(message)"/>',
  result: '<s:property value="result" default=""/>'
 }
