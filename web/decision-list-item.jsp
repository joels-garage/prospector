<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
    <%-- param: listItem == the Display item --%>
           <b>
            <c:choose>
             <c:when test="${listItem.instance.class.name == 'com.joelsgarage.prospector.client.model.Decision'}">
               Decision:   
              <stripes:link beanclass="com.joelsgarage.show.action.DecisionActionBean">
               ${listItem.instance.name}
               <%--
               <stripes:param name="key.namespace" value="${listItem.property.instance.key.namespace}"/>
               <stripes:param name="key.type" value="${listItem.property.instance.key.type}"/>
               <stripes:param name="key.key" value="${listItem.property.instance.key.key}"/>
               --%>
              </stripes:link>
               <stripes:link beanclass="com.joelsgarage.show.action.ClassActionBean" event="fetch">
               Class goes here.
                <%--
                ${listItem.clas.instance.name}&nbsp;:&nbsp;
                <stripes:param name="key.namespace" value="${listItem.clas.key.namespace}"/>
                <stripes:param name="key.type" value="${listItem.clas.key.type}"/>
                <stripes:param name="key.key" value="${listItem.clas.key.key}"/>
                --%>
               </stripes:link>
             </c:when>
            </c:choose>
           </b>
  
</stripes:layout-definition>