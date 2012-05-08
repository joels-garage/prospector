<%@ include file="/taglibs.jsp" %>

<stripes:layout-render
  name="/layout/list.jsp"
  formActionBeanClass="ClassMemberListActionBean"
  itemActionBeanClass="ClassMemberActionBean"
  pageTitle="Class Member List"
  itemLayout="/class-member-list-item.jsp">
  
  <%-- TODO: i guess the row-rendering component would go here --%>
  
</stripes:layout-render>