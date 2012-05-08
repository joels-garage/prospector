<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
    <%-- param: listItem == the DisplayFact --%>
    item:

           <b>
            Fact class name: ${listItem.instance.class.name}<br>
            <c:choose>
             <c:when test="${listItem.instance.class.name == 'com.joelsgarage.prospector.client.model.IndividualFact'}">
               Individual:
                   
              <stripes:link beanclass="com.joelsgarage.show.action.MeasurementUnitActionBean">
              <%-- ${listItem.property.instance.name}: --%>
               Property Name Goes Here:
               <%--
               <stripes:param name="key.namespace" value="${listItem.property.instance.key.namespace}"/>
               <stripes:param name="key.type" value="${listItem.property.instance.key.type}"/>
               <stripes:param name="key.key" value="${listItem.property.instance.key.key}"/>
               --%>
              </stripes:link>
               <stripes:link beanclass="com.joelsgarage.show.action.IndividualFactActionBean" event="fetch">
               Value goes here.
                <%--
                ${listItem.object.instance.name}&nbsp;:&nbsp;
                <stripes:param name="key.namespace" value="${listItem.object.key.namespace}"/>
                <stripes:param name="key.type" value="${listItem.object.key.type}"/>
                <stripes:param name="key.key" value="${listItem.object.key.key}"/>
                --%>
               </stripes:link>
             </c:when>
             
             <c:when test="${listItem.instance.class.name == 'com.joelsgarage.prospector.client.model.MeasurementFact'}">
              Measurement:
                 
              <stripes:link beanclass="com.joelsgarage.show.action.MeasurementPropertyActionBean">
               <%-- ${listItem.property.instance.name}: --%>
               Property Name Goes Here:
               <%--
               <stripes:param name="key.namespace" value="${listItem.property.instance.key.namespace}"/>
               <stripes:param name="key.type" value="${listItem.property.instance.key.type}"/>
               <stripes:param name="key.key" value="${listItem.property.instance.key.key}"/>
               --%>
              </stripes:link>
               <stripes:link beanclass="com.joelsgarage.show.action.MeasurementFactActionBean" event="fetch">
               Value goes here.
                <%--
                ${listItem.instance.value}
                <stripes:param name="key.namespace" value="${listItem.instance.key.namespace}"/>
                <stripes:param name="key.type" value="${listItem.instance.key.type}"/>
                <stripes:param name="key.key" value="${listItem.instance.key.key}"/>
                --%>
               </stripes:link>
          
            <%--
            <c:if test="${not empty listItem.canonicalNameUnitSynonym}">
                          --%>
              (
                <stripes:link beanclass="com.joelsgarage.show.action.UnitSynonymActionBean">
                 Unit name goes here   
                 <%--
                  ${listItem.canonicalNameUnitSynonym}
                  <stripes:param name="key.namespace" value="${listItem.property.instance.key.namespace}"/>
                  <stripes:param name="key.type" value="${listItem.property.instance.key.type}"/>
                  <stripes:param name="key.key" value="${listItem.property.instance.key.key}"/>
                 --%>
                </stripes:link>
              )
                 <%--
            </c:if>
            --%>
             </c:when>
             
             <c:when test="${listItem.instance.class.name == 'com.joelsgarage.prospector.client.model.QuantityFact'}">
              Quantity:
              <stripes:link beanclass="com.joelsgarage.show.action.QuantityPropertyActionBean">
              <%-- ${listItem.property.instance.name}: --%>
              Property Name Goes Here:
               <stripes:param name="key.namespace" value="${listItem.property.instance.key.namespace}"/>
               <stripes:param name="key.type" value="${listItem.property.instance.key.type}"/>
               <stripes:param name="key.key" value="${listItem.property.instance.key.key}"/>
              </stripes:link>
              <stripes:link beanclass="com.joelsgarage.show.action.QuantityFactActionBean" event="fetch">
              Value goes here.
              <%--
                ${listItem.instance.value}
                <stripes:param name="key.namespace" value="${listItem.instance.key.namespace}"/>
                <stripes:param name="key.type" value="${listItem.instance.key.type}"/>
                <stripes:param name="key.key" value="${listItem.instance.key.key}"/>
                --%>
              </stripes:link>

    
    
             </c:when>
             
             <c:when test="${listItem.instance.class.name == 'com.joelsgarage.prospector.client.model.StringFact'}">
              String:
              <stripes:link beanclass="com.joelsgarage.show.action.StringPropertyActionBean">
               <%-- ${listItem.property.instance.name}: --%>
                Property Name Goes Here:
                <%--
               <stripes:param name="key.namespace" value="${listItem.property.instance.key.namespace}"/>
               <stripes:param name="key.type" value="${listItem.property.instance.key.type}"/>
               <stripes:param name="key.key" value="${listItem.property.instance.key.key}"/>
               --%>
              </stripes:link>
               <stripes:link beanclass="com.joelsgarage.show.action.StringFactActionBean" event="fetch">
                Value goes here.
                 <%--
                 ${listItem.instance.value}&nbsp;:&nbsp;
                 <stripes:param name="key.namespace" value="${listItem.instance.key.namespace}"/>
                 <stripes:param name="key.type" value="${listItem.instance.key.type}"/>
                 <stripes:param name="key.key" value="${listItem.instance.key.key}"/>
                 --%>
               </stripes:link>
             </c:when>
            </c:choose>
           </b>
  
</stripes:layout-definition>