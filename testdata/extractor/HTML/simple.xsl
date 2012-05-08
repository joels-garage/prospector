<?xml version="1.0"?>
<xsl:stylesheet xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:output method="xml" indent="yes"/>
<!--xsl:strip-space elements="*:*" /-->

<xsl:template match="/">
<xsl:element name="result" namespace="">
<xsl:text>
</xsl:text>

<xsl:for-each select="html:wrapfoo/html:foofoo">
<xsl:call-template name="zz"/>
</xsl:for-each>

</xsl:element>
</xsl:template>

<xsl:template name="zz">
<xsl:element name="zz-element" namespace="">
<xsl:value-of select="child::node()[position()=3]" />
</xsl:element>
<xsl:text>
</xsl:text>
</xsl:template>

</xsl:stylesheet>
