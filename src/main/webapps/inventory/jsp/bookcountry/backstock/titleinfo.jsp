<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>
<%
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
{
  success: <s:property value="success"/>, 
  isbn: '<s:if test="backStockItem != null"><s:property value="backStockItem.isbn"/></s:if><s:else><s:property value="isbn"/></s:else>',
  isbn13: '<s:if test="backStockItem != null"><s:property value="backStockItem.isbn13"/></s:if><s:else><s:property value="isbn13"/></s:else>',
  id: '<s:property value="backStockItem.id"/>',
  <s:if test="backStockItem != null">
  title: '<s:property value="@org.apache.commons.lang.StringEscapeUtils@escapeJavaScript(backStockItem.title)"/>'
  </s:if><s:else>
  title: '<s:property value="@org.apache.commons.lang.StringEscapeUtils@escapeJavaScript(message)"/>'
  </s:else>
 }
