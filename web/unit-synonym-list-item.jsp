<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
    <%-- param: listItem == the DisplayFact --%>
           <b>   
            <c:choose>
             <c:when test="${listItem.instance.class.name == 'com.joelsgarage.prospector.client.model.UnitSynonym'}">                   
              <stripes:link beanclass="com.joelsgarage.show.action.UnitSynonymActionBean">
               ${listItem.instance.name}:
               <stripes:param name="key.namespace" value="${listItem.instance.key.namespace}"/>
               <stripes:param name="key.type" value="${listItem.instance.key.type}"/>
               <stripes:param name="key.key" value="${listItem.instance.key.key}"/>
              </stripes:link>
             </c:when>
            </c:choose>
           </b>
</stripes:layout-definition>