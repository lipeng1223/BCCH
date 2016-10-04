<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>
<%
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
{
  success: <s:property value="success"/>, 
  hasImportErrors: <s:property value="hasImportErrors" default="false"/>, 
  errors: '<s:property value="importErrors" escape="true"/>'
 }
