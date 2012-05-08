<%@ include file="/taglibs.jsp" %>

<stripes:layout-render
  name="/layout/list.jsp"
  formActionBeanClass="DecisionListActionBean"
  itemActionBeanClass="DecisionActionBean"
  pageTitle="Decision List"
  itemLayout="/decision-list-item.jsp">
  
  <%-- TODO: i guess the row-rendering component would go here --%>
  
</stripes:layout-render>