<%@ taglib prefix="s" uri="/struts-tags" %><%@ page isELIgnored="false" %><% response.setHeader("Cache-Control","no-cache"); response.setHeader("Pragma","no-cache"); response.setDateHeader ("Expires", 0); %>
<s:if test="amazonData.dataLoaded">
<s:property value="amazonData.getEntityJson()" escape="false"/>
</s:if>
<s:else>
{ dataLoaded:"false" }
</s:else>