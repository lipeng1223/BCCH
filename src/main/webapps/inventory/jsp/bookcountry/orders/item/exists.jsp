<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>
<%
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
{
  success: <s:property value="success"/>, 
  existingQuantity: '<s:property value="orderItem.quantity"/>', 
  exists: '<s:property value="@org.apache.commons.lang.StringEscapeUtils@escapeJavaScript(message)"/>'
 }
