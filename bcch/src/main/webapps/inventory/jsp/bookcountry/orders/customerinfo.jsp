<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>
<s:if test="customer.invoiceEmail != null && customer.invoiceEmail.length() > 0"><s:property value="customer.invoiceEmail"/></s:if><s:else><s:if test="customer.email1 != null && customer.email1.length() > 0"><s:property value="customer.email1"/></s:if><s:if test="customer.email2 != null && customer.email2.length() > 0">, <s:property value="customer.email2"/></s:if></s:else>

