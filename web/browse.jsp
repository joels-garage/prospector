<%@ include file="/taglibs.jsp" %>

<stripes:layout-render name="/layout/default.jsp"  pageTitle="Joel's Garage">
  <stripes:layout-component name="contents" >
    <c:choose>
      <c:when test="${not actionBean.typeAllowed}">
        <p>
          Hello!  Feel free to browse any of the allowed types:
        </p>
        <p>
          <ul>
            <c:forEach var="item" items="${actionBean.dowser.allowedTypes}">
              <li>
                <stripes:link beanclass="${actionBean.class.name}">
                  ${item.key}
                  <stripes:param name="key.type" value="${item.key}"/>
                </stripes:link>
              </li>
            </c:forEach>
          </ul>
        </p>
      </c:when>
      <c:otherwise>
        <p>
          You specified type: ${actionBean.key.type}.
        </p>
        <%-- display instance details, for ENTITY or JOIN type --%>
        <c:if test="${not empty actionBean.instance}">   
          <p>
            Single item details:
          </p>
          <stripes:layout-render name="/detail/${actionBean.instance.typeName}.jsp" singleInstance="${actionBean.instance}" />
        </c:if>
  
        <%-- display the list, for LIST type --%>
        <c:if test="${not empty actionBean.multiList.paginatedLists}">
          <stripes:layout-render name="/layout/multi-list.jsp" multiList="${actionBean.multiList}"/>
        </c:if>
      </c:otherwise>
    </c:choose>
  </stripes:layout-component>
</stripes:layout-render>

