<%@ include file="/taglibs.jsp" %>
<stripes:layout-definition>
  <stripes:url var="url" beanclass="${actionBean.class.name}"/>
  <%-- param: pageTitle, which is really a kind of subhead. --%>

  <%--stripes:useActionBean beanclass="com.joelsgarage.show.action.HeaderActionBean" var="actionBean" /--%>
  <c:url value="/images/prospectorLogo.png" var="logoUrl"/>
  <div id="header">
    <div id="logoImg"><img src="${logoUrl}" alt="Prospector Logo"/></div>
    <div id="applicationName">Joel's Garage Prospector</div>
    <div id="tagLine">What Are Your Prospects?</div>
  </div>
  
  <stripes:errors>
     <stripes:errors-header>
       Errors
     </stripes:errors-header>
    - <stripes:individual-error/>
  </stripes:errors>

  <div id="user">
    <c:choose>
      <c:when test="${not empty actionBean.context.user}">
        Welcome: ${actionBean.context.user.realName}
        -
        <stripes:link beanclass="com.joelsgarage.show.LogoutActionBean">
          Logout
          <stripes:param name="targetUrl" value="${url}"/>
        </stripes:link>
      </c:when>
      <c:otherwise>
        <stripes:link beanclass="com.joelsgarage.show.RegisterActionBean" event="start">
          Register
          <stripes:param name="targetUrl" value="${url}"/>
          <%--stripes:param name="_sourcePage" value="${pageContext.request.servletPath}"/--%>
          <%--stripes:param name="_sourcePage" value="${pageContext.request.requestURL}"/--%>
        </stripes:link>
        -
        <stripes:link beanclass="com.joelsgarage.show.LoginActionBean" event="start">
          Login
          <stripes:param name="targetUrl" value="${url}"/>
          <%--stripes:param name="_sourcePage" value="${pageContext.request.servletPath}"/--%>
          <%--stripes:param name="_sourcePage" value="${pageContext.request.requestURL}"/--%>
        </stripes:link>
      </c:otherwise>
    </c:choose>
  </div>
  <div id="headerLinks">
    <stripes:link beanclass="com.joelsgarage.show.action.HomePageActionBean">Home</stripes:link> -
    <stripes:link beanclass="com.joelsgarage.show.action.BrowseActionBean">Browse</stripes:link> -
    <stripes:link beanclass="com.joelsgarage.show.action.RankedAlternativeActionBean">Alternatives</stripes:link> -
    <stripes:link beanclass="com.joelsgarage.show.action.UtilityRankedAlternativeActionBean">Utility-Alternatives</stripes:link>
  </div>
</stripes:layout-definition>