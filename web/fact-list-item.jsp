<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
  <%-- param: listItem == the DisplayFact (or subclass thereof) --%>
  <b>
    <c:choose>
      <c:when test="${listItem.instance.class.name == 'com.joelsgarage.prospector.client.model.IndividualFact'}">
        Individual: 
        <stripes:link beanclass="com.joelsgarage.show.action.IndividualPropertyActionBean">
          ${listItem.property.instance.name}:
          <stripes:param name="key.namespace" value="${listItem.property.instance.key.namespace}"/>
          <stripes:param name="key.type" value="${listItem.property.instance.key.type}"/>
          <stripes:param name="key.key" value="${listItem.property.instance.key.key}"/>
        </stripes:link>
        <stripes:link beanclass="com.joelsgarage.show.action.IndividualFactActionBean" event="fetch">
          ${listItem.object.instance.name}&nbsp;:&nbsp;
          <stripes:param name="key.namespace" value="${listItem.object.key.namespace}"/>
          <stripes:param name="key.type" value="${listItem.object.key.type}"/>
          <stripes:param name="key.key" value="${listItem.object.key.key}"/>
        </stripes:link>
      </c:when>
             
      <c:when test="${listItem.instance.class.name == 'com.joelsgarage.prospector.client.model.MeasurementFact'}">
        <stripes:link beanclass="com.joelsgarage.show.action.MeasurementPropertyActionBean">
          ${listItem.property.instance.name}:
          <stripes:param name="key.namespace" value="${listItem.property.instance.key.namespace}"/>
          <stripes:param name="key.type" value="${listItem.property.instance.key.type}"/>
          <stripes:param name="key.key" value="${listItem.property.instance.key.key}"/>
        </stripes:link>
        <stripes:link beanclass="com.joelsgarage.show.action.MeasurementFactActionBean" event="fetch">
          ${listItem.instance.value}
          <stripes:param name="key.namespace" value="${listItem.instance.key.namespace}"/>
          <stripes:param name="key.type" value="${listItem.instance.key.type}"/>
          <stripes:param name="key.key" value="${listItem.instance.key.key}"/>
        </stripes:link>
        (
        <c:choose>
          <c:when test="${not empty listItem.canonicalNameUnitSynonym}">
            <stripes:link beanclass="com.joelsgarage.show.action.UnitSynonymActionBean">
              ${listItem.canonicalNameUnitSynonym.instance.name}
              <stripes:param name="key.namespace" value="${listItem.canonicalNameUnitSynonym.instance.key.namespace}"/>
              <stripes:param name="key.type" value="${listItem.canonicalNameUnitSynonym.instance.key.type}"/>
              <stripes:param name="key.key" value="${listItem.canonicalNameUnitSynonym.instance.key.key}"/>
            </stripes:link>
          </c:when>
          <c:otherwise>
            Unknown unit
          </c:otherwise>
        </c:choose>
        )  
      </c:when>
             
      <c:when test="${listItem.instance.class.name == 'com.joelsgarage.prospector.client.model.QuantityFact'}">
        <stripes:link beanclass="com.joelsgarage.show.action.QuantityPropertyActionBean">
          ${listItem.property.instance.name}:
          <stripes:param name="key.namespace" value="${listItem.property.instance.key.namespace}"/>
          <stripes:param name="key.type" value="${listItem.property.instance.key.type}"/>
          <stripes:param name="key.key" value="${listItem.property.instance.key.key}"/>
        </stripes:link>
        <stripes:link beanclass="com.joelsgarage.show.action.QuantityFactActionBean" event="fetch">
          ${listItem.instance.value}
          <stripes:param name="key.namespace" value="${listItem.instance.key.namespace}"/>
          <stripes:param name="key.type" value="${listItem.instance.key.type}"/>
          <stripes:param name="key.key" value="${listItem.instance.key.key}"/>
        </stripes:link>
      </c:when>
             
      <c:when test="${listItem.instance.class.name == 'com.joelsgarage.prospector.client.model.StringFact'}">
        <stripes:link beanclass="com.joelsgarage.show.action.StringPropertyActionBean">
          ${listItem.property.instance.name}:
          <stripes:param name="key.namespace" value="${listItem.property.instance.key.namespace}"/>
          <stripes:param name="key.type" value="${listItem.property.instance.key.type}"/>
          <stripes:param name="key.key" value="${listItem.property.instance.key.key}"/>
        </stripes:link>
        <stripes:link beanclass="com.joelsgarage.show.action.StringFactActionBean" event="fetch">
          ${listItem.instance.value}
          <stripes:param name="key.namespace" value="${listItem.instance.key.namespace}"/>
          <stripes:param name="key.type" value="${listItem.instance.key.type}"/>
          <stripes:param name="key.key" value="${listItem.instance.key.key}"/>
        </stripes:link>
      </c:when>
      
      <c:otherwise>
        Don't know how to render fact type ${listItem.instance.class.name}.
      </c:otherwise>
    </c:choose>
  </b>
</stripes:layout-definition>