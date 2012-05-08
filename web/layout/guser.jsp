<%@ include file="/taglibs.jsp" %>
<stripes:layout-definition>

<stripes:useActionBean beanclass="com.joelsgarage.show.GUserActionBean" var="bean" />
  
 <div align=right id=guser style="font-size:84%;padding:0 0 4px" width=100%>
  <nobr>
   <b>Your UserName</b>
    <c:forEach var="link" items="${bean.links}">
     | <a href="${link.url}">${link.text}</a>
    </c:forEach>
  </nobr>
 </div>
 
  </stripes:layout-definition>
 