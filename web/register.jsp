<%@ include file="/taglibs.jsp" %>

<stripes:layout-render name="/layout/default.jsp"  pageTitle="Register">
  <stripes:layout-component name="contents" >

        <stripes:errors globalErrorsOnly="true"/>

        <stripes:form beanclass="com.joelsgarage.show.RegisterActionBean" focus="first">
            <p>Please provide the following information:</p>

            <table class="leftRightForm">
                <tr>
                    <th><stripes:label for="user.name"/>:</th>
                    <td>
                        <stripes:text name="user.name"/>
                        <stripes:errors field="user.name"/>
                    </td>
                </tr>
                <tr>
                    <th><stripes:label for="user.realName"/>:</th>
                    <td>
                        <stripes:text name="user.realName"/>
                        <stripes:errors field="user.realName"/>
                    </td>
                </tr>
                <tr>
                    <th><stripes:label for="user.emailAddress"/>:</th>
                    <td>
                        <stripes:text name="user.emailAddress"/>
                        <stripes:errors field="user.emailAddress"/>
                    </td>
                </tr>
            </table>

            <div class="buttons">
                <stripes:submit name="gotoStep2" value="Next"/>
            </div>
        </stripes:form>
                
    </stripes:layout-component>
</stripes:layout-render>
