<%@ taglib prefix="s" uri="/struts-tags" %><%@ page isELIgnored="false" %><% response.setHeader("Cache-Control","no-cache"); response.setHeader("Pragma","no-cache"); response.setDateHeader ("Expires", 0); %>
<s:if test="inventoryItem != null"><s:property value="inventoryItem.entityJson" escape="false"/></s:if><s:else>{"found": "false"}</s:else>