<%@ include file="/taglibs.jsp" %>
<stripes:layout-definition>

<stripes:useActionBean beanclass="com.joelsgarage.show.GBarActionBean" var="bean" />
  
 <div id=gbar>
  <nobr>
   <span class=gb1><b>You are here</b></span>
    <c:forEach var="link" items="${bean.links}">
     <span class=gb1><a href="${link.url}">${link.text}</a></span> 
    </c:forEach>
  </nobr>
 </div>
 
  </stripes:layout-definition>
 