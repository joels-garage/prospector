<%@ include file="/taglibs.jsp" %>

<stripes:layout-render
  name="/layout/list.jsp"
  formActionBeanClass="UserListActionBean"
  itemActionBeanClass="UserActionBean"
  pageTitle="User List"
  itemLayout="/user-list-item.jsp">
  
  <%-- TODO: i guess the row-rendering component would go here --%>
  
</stripes:layout-render>