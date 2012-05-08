<?xml version="1.0"?>
<xsl:stylesheet xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:output method="xml" indent="yes"/>
<xsl:template match="/">
<result>
<xsl:text>
</xsl:text>
<xsl:for-each select="html:html/html:body/html:table/html:tbody/html:tr[5]/html:td/html:table/html:tbody/html:tr/html:td[4]/html:table/html:tbody/html:tr[4]/html:td[1]/html:table/html:tbody/html:tr[1]/html:td/html:p[2]/html:span">
<xsl:call-template name="common-name"/>
<xsl:text>
</xsl:text>
</xsl:for-each>
<xsl:for-each select="html:html/html:body/html:table/html:tbody/html:tr[5]/html:td/html:table/html:tbody/html:tr/html:td[4]/html:table/html:tbody/html:tr[4]/html:td[1]/html:table/html:tbody/html:tr[1]/html:td/html:p[2]/html:span">
<xsl:call-template name="latin-name"/>
<xsl:text>
</xsl:text>
</xsl:for-each>
<xsl:for-each select="html:html/html:body/html:table/html:tbody/html:tr[5]/html:td/html:table/html:tbody/html:tr/html:td[4]/html:table/html:tbody/html:tr[4]/html:td[1]/html:table/html:tbody/html:tr[2]/html:td/html:table/html:tbody/html:tr/html:td[1]/html:table/html:tbody/html:tr[5]/html:td[3]">
<xsl:call-template name="duration"/>
<xsl:text>
</xsl:text>
</xsl:for-each>
<xsl:for-each select="html:html/html:body/html:table/html:tbody/html:tr[5]/html:td/html:table/html:tbody/html:tr/html:td[4]/html:table/html:tbody/html:tr[4]/html:td[1]/html:table/html:tbody/html:tr[2]/html:td/html:table/html:tbody/html:tr/html:td[1]/html:table/html:tbody/html:tr[6]/html:td[3]">
<xsl:call-template name="habit"/>
<xsl:text>
</xsl:text>
</xsl:for-each>
</result>
</xsl:template>

<xsl:template name="latin-name">
<latin-name>
<xsl:for-each select="html:br[1]/preceding-sibling::node()">
<xsl:value-of select="normalize-space()"/>
<xsl:text> </xsl:text>
</xsl:for-each>
</latin-name>
</xsl:template>

<xsl:template name="common-name">
<common-name>
<xsl:value-of select="normalize-space(html:br[1]/following-sibling::node())"/>
<xsl:text> </xsl:text>
</common-name>
</xsl:template>

<xsl:template name="duration">
<duration>
<xsl:value-of select="normalize-space()"/>
</duration>
</xsl:template>

<xsl:template name="habit">
<habit>
<xsl:value-of select="normalize-space()"/>
</habit>
</xsl:template>

</xsl:stylesheet>