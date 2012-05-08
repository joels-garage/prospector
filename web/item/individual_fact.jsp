<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
    <%-- param: listItem == the entity to display --%>
    
    <stripes:link beanclass="${actionBean.class.name}">
      fact:
      <stripes:param name="key.namespace" value="${listItem.instance.key.namespace}"/>
      <stripes:param name="key.type" value="${listItem.instance.key.type}"/>
      <stripes:param name="key.key" value="${listItem.instance.key.key}"/>
    </stripes:link>
 
    <stripes:link beanclass="${actionBean.class.name}">
      ${listItem.manyToOne['property'].instance.name}
      <stripes:param name="key.namespace" value="${listItem.manyToOne['property'].instance.key.namespace}"/>
      <stripes:param name="key.type" value="${listItem.manyToOne['property'].instance.key.type}"/>
      <stripes:param name="key.key" value="${listItem.manyToOne['property'].instance.key.key}"/>
    </stripes:link>
    
    of
    
    <stripes:link beanclass="${actionBean.class.name}">
      ${listItem.manyToOne['subject'].instance.name}
      <stripes:param name="key.namespace" value="${listItem.manyToOne['subject'].instance.key.namespace}"/>
      <stripes:param name="key.type" value="${listItem.manyToOne['subject'].instance.key.type}"/>
      <stripes:param name="key.key" value="${listItem.manyToOne['subject'].instance.key.key}"/>
    </stripes:link>
    
    is
    
    <stripes:link beanclass="${actionBean.class.name}">
      ${listItem.manyToOne['object'].instance.name}
      <stripes:param name="key.namespace" value="${listItem.manyToOne['object'].instance.key.namespace}"/>
      <stripes:param name="key.type" value="${listItem.manyToOne['object'].instance.key.type}"/>
      <stripes:param name="key.key" value="${listItem.manyToOne['object'].instance.key.key}"/>
    </stripes:link>
        
</stripes:layout-definition>