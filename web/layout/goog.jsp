<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
 <%-- param: pageTitle --%>

<html>
 <head>
  <meta http-equiv=content-type content="text/html; charset=UTF-8">
  <title>${pageTitle}</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/style/goog.css"/>
 </head>

 <body bgcolor=#ffffff topmargin=3 marginheight=3>

  <stripes:layout-component name="gbar">
   <jsp:include page="/layout/gbar.jsp"/>
  </stripes:layout-component>
 
  <div class=gbh style=left:0></div>
  <div class=gbh style=right:0></div>
 
  <stripes:layout-component name="guser">
   <jsp:include page="/layout/guser.jsp"/>
  </stripes:layout-component>
 
  <stripes:layout-component name="search-form">
   <jsp:include page="/layout/search-form.jsp"/>
  </stripes:layout-component>
 
  <stripes:layout-component name="mid-bar">
   <jsp:include page="/layout/mid-bar.jsp"/>
  </stripes:layout-component>
 
  <stripes:layout-component name="ads">
   <jsp:include page="/layout/ads.jsp"/>
  </stripes:layout-component>

  <stripes:layout-component name="results">
   <jsp:include page="/layout/results.jsp"/>
  </stripes:layout-component>

  <stripes:layout-component name="bottom-search">
   <jsp:include page="/layout/bottom-search.jsp"/>
  </stripes:layout-component>

  <stripes:layout-component name="footer">
   <jsp:include page="/layout/footer.jsp"/>
  </stripes:layout-component>
 
 </body>
</html>

</stripes:layout-definition>