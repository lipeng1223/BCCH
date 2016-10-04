<?xml version="1.0" encoding="UTF-8"?><%--
--%><%@ taglib prefix="s" uri="/struts-tags" %><%--
--%><%@ page isELIgnored="false" %><%--
--%><%
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<%  
    com.bc.util.RafReader rr = (com.bc.util.RafReader)request.getAttribute("rafReader");
    if (rr.getFilePos() < rr.getLogLength()) { %> <%-- this is so we only add the <pre> if there is new content --%>
<pre><%
    while (!rr.finished()){
%><%= rr.content() %><%
    }
    rr.cleanup();
%></pre><div style="display:none;" id="currentFilePos"><%= rr.getFilePos() %></div><div style="display:none;" id="completedRead"><%= rr.getCompletedRead() %></div>
<% } else { %>
<div style="display:none;" id="currentFilePos"><%= rr.getFilePos() %></div><div style="display:none;" id="completedRead">true</div>
<% } %>