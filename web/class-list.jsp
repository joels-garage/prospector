<%@ include file="/taglibs.jsp" %>

<stripes:layout-render
  name="/layout/list.jsp"
  formActionBeanClass="ClassListActionBean"
  itemActionBeanClass="ClassActionBean"
  pageTitle="Class List"
  itemLayout="/class-list-item.jsp">
  
  <%-- TODO: i guess the row-rendering component would go here --%>
  
</stripes:layout-render>