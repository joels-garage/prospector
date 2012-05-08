<?xml version="1.0"?>
<!-- 
	This contains "Model" elements, i.e. templates that produce XML compatible with Skiploader's "load" method.
	This file should be xsl:include'd from the source-specific templates.
-->
<xsl:stylesheet xmlns:html="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exsl="http://exslt.org/common"
	extension-element-prefixes="exsl" version="1.0">

	<xsl:template name="creatorKey">
		<namespace>internal-agent</namespace>
		<type>user</type>
		<key>usda-plants-characteristics-importer</key>
	</xsl:template>

</xsl:stylesheet>