<%@ include file="/taglibs.jsp" %>

<stripes:layout-render name="/layout/default.jsp"  pageTitle="Register">
  <stripes:layout-component name="contents" >


        <stripes:form beanclass="com.joelsgarage.show.RegisterActionBean" focus="user.password">
            <stripes:errors/>

            <p>Welcome ${actionBean.user.realName}, please pick a password:</p>

            <table class="leftRightForm">
                <tr>
                    <th><stripes:label for="user.password"/>:</th>
                    <td><stripes:password name="user.password"/></td>
                </tr>
                <tr>
                    <th><stripes:label for="confirmPassword"/>:</th>
                    <td><stripes:password name="confirmPassword"/></td>
                </tr>
            </table>

            <div class="buttons">
                <stripes:submit name="register" value="Create Account"/>
            </div>
        </stripes:form>
                 
    </stripes:layout-component>
</stripes:layout-render>