<%@ include file="/taglibs.jsp" %>

<stripes:layout-render
  name="/layout/list.jsp"
  formActionBeanClass="IndividualListActionBean"
  itemActionBeanClass="IndividualActionBean"
  pageTitle="Individual List"
  itemLayout="/individual-list-item.jsp">
  
  <%-- TODO: i guess the row-rendering component would go here --%>
  
  <%--
    so, in the test database, i have individuals whose "name" field is really uninteresting;
    it's a particular fact that's the interesting part.
    so, maybe it's a per-class thing?  for the class of plants, it's the "common name" fact
    that should be displayed as the "name"?  that would probably work, except for
    class-multivalued-ness, which creates conflict.
    maybe i should have imported a more meaningful name field?
    maybe the conflict isn't real; most individuals will have a small number of class memberships,
    so the "fact promoted to name" conflict won't be an issue.  ?
    
    so the data model is: display-name(class, property).  eh?
    --%>
  
</stripes:layout-render>