<%@ include file="/taglibs.jsp" %>

<stripes:layout-render name="/layout/default.jsp"  pageTitle="Ranked Alternatives">
  <stripes:layout-component name="contents" >
    <p>
      List of ranked alternatives
    </p>
    <c:if test="${empty actionBean.alternatives}">
      <p>
        There are no alternatives in the list.
      </p>
    </c:if>
    
    <c:if test="${not empty actionBean.alternatives}">
      <p>
        There are ${fn:length(actionBean.alternatives)} decisions.
      </p>
      <c:forEach var="alternativeCollection" items="${actionBean.alternatives}">
        <hr>
        <p>
          There are ${fn:length(alternativeCollection.alternatives)} 
          <stripes:link beanclass="${actionBean.class.name}">
            alternatives for decision
            <b>
              ${alternativeCollection.decision.name}.
            </b>
            <stripes:param name="key.namespace" value="${alternativeCollection.decision.key.namespace}"/>
            <stripes:param name="key.type" value="${alternativeCollection.decision.key.type}"/>
            <stripes:param name="key.key" value="${alternativeCollection.decision.key.key}"/>
          </stripes:link>
       
        </p>
        <c:forEach var="annotatedAlternative" items="${alternativeCollection.alternatives}">
          <p>
            <stripes:link beanclass="com.joelsgarage.show.action.BrowseActionBean">
              <stripes:param name="key.namespace" value="${annotatedAlternative.individual.key.namespace}"/>
              <stripes:param name="key.type" value="${annotatedAlternative.individual.key.type}"/>
              <stripes:param name="key.key" value="${annotatedAlternative.individual.key.key}"/>
              ${dereference.value.instance.name}
              ${annotatedAlternative.individual.name}
            </stripes:link>
            (
              score: <fmt:formatNumber value="${annotatedAlternative.score}" pattern="#.###"/>
              ${annotatedAlternative.reason}
            )
          </p>
        </c:forEach>
      </c:forEach>
    </c:if>

  </stripes:layout-component>
</stripes:layout-render>

