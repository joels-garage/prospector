<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
    <%-- param: listItem == the entity to display --%>
    <%-- <stripes:layout-render name="/item/model_entity.jsp" listItem="${listItem}" /> --%>
    
    <stripes:link beanclass="${actionBean.class.name}">
      ${listItem.manyToOne['property'].instance.name}
      <stripes:param name="key.namespace" value="${listItem.manyToOne['property'].instance.key.namespace}"/>
      <stripes:param name="key.type" value="${listItem.manyToOne['property'].instance.key.type}"/>
      <stripes:param name="key.key" value="${listItem.manyToOne['property'].instance.key.key}"/>
    </stripes:link>
    
    of
    
    <stripes:link beanclass="${actionBean.class.name}">
      ${listItem.manyToOne['subject'].instance.name}
      <stripes:param name="key.namespace" value="${listItem.manyToOne['subject'].instance.key.namespace}"/>
      <stripes:param name="key.type" value="${listItem.manyToOne['subject'].instance.key.type}"/>
      <stripes:param name="key.key" value="${listItem.manyToOne['subject'].instance.key.key}"/>
    </stripes:link>
    
    is
        
    <stripes:link beanclass="${actionBean.class.name}">
      ${listItem.instance.value}
      <stripes:param name="key.namespace" value="${listItem.instance.key.namespace}"/>
      <stripes:param name="key.type" value="${listItem.instance.key.type}"/>
      <stripes:param name="key.key" value="${listItem.instance.key.key}"/>
    </stripes:link>
    
    <%-- scan (gah) to find the canonical synonym to display --%>
    <%-- todo: add a default here if there's no canonical synonym --%>
         
    <c:forEach var="paginatedList" items="${listItem.manyToOne['expressed_unit'].oneToMany['unit_synonym/measurement_unit'].paginatedLists}">
      <c:forEach var="listEntity" items="${paginatedList.value.list}">
        <c:if test="${listEntity.instance.canonicalName == true}">
          <stripes:link beanclass="${actionBean.class.name}">
            ${listEntity.instance.value}
            <stripes:param name="key.namespace" value="${listEntity.instance.key.namespace}"/>
            <stripes:param name="key.type" value="${listEntity.instance.key.type}"/>
            <stripes:param name="key.key" value="${listEntity.instance.key.key}"/>
          </stripes:link>
        </c:if>
      </c:forEach>
    </c:forEach>
      
</stripes:layout-definition>