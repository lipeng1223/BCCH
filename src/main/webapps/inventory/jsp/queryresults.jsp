<?xml version="1.0" encoding="UTF-8"?><%--
--%><%@ taglib prefix="s" uri="/struts-tags" %><%--
--%><%@ page isELIgnored="false" %><%--
--%><%
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
    response.setContentType("application/xml");
%><%--
--%><dataset><%-- 
    --%><total><s:property value="queryResults.totalRecords"/></total><%--
    --%><filtertext><s:property value="queryResults.filterText"/></filtertext><%--
    --%><s:if test="queryResults.extraHdrInfoMap != null && queryResults.extraHdrInfoMap.size() != 0"><%--
    --%><extrahdrinfo><%--
        --%><s:iterator value="queryResults.extraHdrInfoKeys" status="status" id="key"><%--
            --%><<s:property value="#key"/>><%--
                --%><s:property value="%{queryResults.getExtraHdrInfo(#key)}"/><%--
            --%></<s:property value="#key"/>><%--
        --%></s:iterator><%--    
    --%></extrahdrinfo><%--
    --%></s:if><%--
    --%><s:if test="queryResults.tableConfig != null && queryResults.tableConfig.summary"><%--
    --%><summary><%--
        --%><s:iterator value="queryResults.tableConfig.columnDatas" status="status" id="col"><%--
            --%><<s:property value="#col.xmlEntityName"/>><%--
                --%><s:property value="%{queryResults.getSummary(#col.name)}"/><%--
            --%></<s:property value="#col.xmlEntityName"/>><%--
        --%></s:iterator><%--
    --%></summary><%--
    --%></s:if><%--
    --%><s:iterator value="queryResults.data" id="data" status="resStatus"><%--
    --%><row><%--
        --%><s:if test="queryResults.tableConfig != null"><%-- 
        --%><s:iterator value="queryResults.tableConfig.columnDatas" status="status" id="col"><%--
            --%><<s:property value="#col.xmlEntityName"/>><%--
                --%><s:property value="%{queryResults.get(#data, #col.name)}" escape="false"/><%--
            --%></<s:property value="#col.xmlEntityName"/>><%--
        --%></s:iterator><%--
        --%></s:if><s:else><%--
        --%><s:iterator value="table.columnDatas" status="status" id="col"><%--
            --%><<s:property value="#col.xmlEntityName"/>><%--
                --%><s:property value="%{queryResults.get(#data, #col.name)}"/><%--
            --%></<s:property value="#col.xmlEntityName"/>><%--
        --%></s:iterator><%--
        --%></s:else><%--
    --%></row><%--
    --%></s:iterator><%--
    --%><s:if test="queryResults.includeOtherXml != null"><%--
    --%><s:include value="%{queryResults.includeOtherXml}" /><%--
    --%></s:if><%--
--%></dataset>