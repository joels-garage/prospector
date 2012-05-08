<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
   <%-- param: pageTitle --%>
    <html>
        <head>
            <title>${pageTitle}</title>
            <link rel="stylesheet"
                  type="text/css"
                  href="${pageContext.request.contextPath}/style/goog.css"/>
            <c:url value="/style/show.css" var="showStyleUrl"/>
            <link rel="stylesheet"
                  type="text/css"
                  href="${showStyleUrl}"/>
            <stripes:layout-component name="html-head"/>
        </head>
        <body>
            <stripes:layout-component name="header">
                <stripes:layout-render name="/layout/_header.jsp" pageTitle="${pageTitle}"/>
            </stripes:layout-component>
            
            <div id="pageTitle"><p>${pageTitle}</p></div>
            
            <div id="pageContent">
              <p>
                <stripes:layout-component name="contents"/>
              </p>
            </div>
            
            <div id="adBlock">
              <stripes:layout-component name="ads">
               <stripes:layout-render name="/layout/ads.jsp"/>
              </stripes:layout-component>
            </div>

              <stripes:layout-component name="footer">
                  <stripes:layout-render name="/layout/_footer.jsp"/>
              </stripes:layout-component>

        </body>
    </html>
</stripes:layout-definition>

