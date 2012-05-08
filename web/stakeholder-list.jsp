<%@ include file="/taglibs.jsp" %>

<stripes:layout-render
  name="/layout/list.jsp"
  formActionBeanClass="StakeholderListActionBean"
  itemActionBeanClass="StakeholderActionBean"
  pageTitle="Stakeholder List"
  itemLayout="/stakeholder-list-item.jsp">
  
  <%-- TODO: i guess the row-rendering component would go here --%>
  
</stripes:layout-render>