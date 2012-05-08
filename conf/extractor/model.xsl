<?xml version="1.0"?>
<!-- 
	This contains "Model" elements, i.e. templates that produce XML compatible with Skiploader's "load" method, and
	are independent of any specific content source.
	
	This file should be xsl:include'd from the source-specific stylesheets.
-->
<xsl:stylesheet xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:exsl="http://exslt.org/common" extension-element-prefixes="exsl" version="1.0">

	<!-- 
		Emit the ExternalKey for the specified field values.
	-->
	<xsl:template name="codekeytree">
		<xsl:param name="namespace" />
		<xsl:param name="type" />
		<xsl:param name="key" />
		<namespace>
			<xsl:value-of select="$namespace" />
		</namespace>
		<type>
			<xsl:value-of select="$type" />
		</type>
		<key>
			<xsl:value-of select="$key" />
		</key>
	</xsl:template>

	<!-- 
		Emit the ExternalKey for the Creator field for all records produced by this importer.
	-->
	<xsl:template name="creatorKey">
		<!-- the key to represent the specific importer stylesheet -->
		<xsl:param name="key" />
		<creatorKey>
			<xsl:call-template name="codekeytree">
				<xsl:with-param name="namespace" select="'internal-agent'" />
				<xsl:with-param name="type" select="'user'" />
				<xsl:with-param name="key" select="$key" />
			</xsl:call-template>
		</creatorKey>
	</xsl:template>

	<!-- 
		Emit an Individual record with name equal to the key.key value.
	-->
	<xsl:template name="individual">
		<!-- the key.namespace for this record -->
		<xsl:param name="namespace" />
		<!-- the key.key for this record -->
		<xsl:param name="individual_id_param" />
		<!-- the creator key.key for this record -->
		<xsl:param name="creatorKeyKey" />
		<!-- the ISO8601 date -->
		<xsl:param name="crawl_date" />
		<Individual>
			<key>
				<xsl:call-template name="codekeytree">
					<xsl:with-param name="namespace" select="$namespace" />
					<xsl:with-param name="type" select="'individual'" />
					<xsl:with-param name="key" select="$individual_id_param" />
				</xsl:call-template>
			</key>

			<xsl:call-template name="creatorKey">
				<xsl:with-param name="key" select="$creatorKeyKey" />
			</xsl:call-template>

			<lastModified>
				<xsl:value-of select="$crawl_date" />
			</lastModified>
			<name>
				<xsl:value-of select="$individual_id_param" />
			</name>
		</Individual>
	</xsl:template>

	<!-- 
		Emit a StringFact record.
	-->
	<xsl:template name="string_fact">
		<!--  -->
		<xsl:param name="individual_id_param" />
		<!-- the node-set key of the property for this fact -->
		<xsl:param name="property_nodes" />
		<!-- the string value of this fact -->
		<xsl:param name="fact_value" />
		<!-- the namespace of this fact and also its subject -->
		<xsl:param name="source_namespace" />
		<!-- the creator key.key for this record -->
		<xsl:param name="creatorKeyKey" />
		<!-- the ISO8601 date -->
		<xsl:param name="crawl_date" />

		<xsl:variable name="fact_key">
			<xsl:choose>
				<!-- same namespace: don't need to include namespace -->
				<xsl:when test="$source_namespace = $property_nodes/namespace">
					<xsl:text>subject=</xsl:text>
					<xsl:value-of select="$individual_id_param" />
					<xsl:text>&amp;property=</xsl:text>
					<xsl:value-of select="$property_nodes/key" />
					<xsl:text>&amp;value=</xsl:text>
					<xsl:value-of select="$fact_value" />
				</xsl:when>
				<!-- different namespace: do need to include namespace -->
				<xsl:otherwise>
					<xsl:text>subject=</xsl:text>
					<xsl:value-of select="$individual_id_param" />
					<xsl:text>&amp;property=</xsl:text>
					<xsl:value-of select="$property_nodes/namespace" />
					<xsl:text>/</xsl:text>
					<xsl:value-of select="$property_nodes/key" />
					<xsl:text>&amp;value=</xsl:text>
					<xsl:value-of select="$fact_value" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<StringFact>
			<key>
				<xsl:call-template name="codekeytree">
					<xsl:with-param name="namespace" select="$source_namespace" />
					<xsl:with-param name="type" select="'string_fact'" />
					<xsl:with-param name="key" select="$fact_key" />
				</xsl:call-template>
			</key>

			<xsl:call-template name="creatorKey">
				<xsl:with-param name="key" select="$creatorKeyKey" />
			</xsl:call-template>

			<lastModified>
				<xsl:value-of select="$crawl_date" />
			</lastModified>
			<!-- name here is the same as the key part of the externalkey, but it doesn't have to be -->
			<name>
				<xsl:value-of select="$fact_key" />
			</name>
			<subjectKey>
				<xsl:call-template name="codekeytree">
					<xsl:with-param name="namespace" select="$source_namespace" />
					<xsl:with-param name="type" select="'individual'" />
					<xsl:with-param name="key" select="$individual_id_param" />
				</xsl:call-template>
				<!--xsl:copy-of select="$individual_id_param"/-->
			</subjectKey>
			<propertyKey>
				<xsl:copy-of select="$property_nodes" />
				<!--xsl:call-template name="codekeytree">
					<xsl:with-param name="namespace" select="$property_namespace"/>
					<xsl:with-param name="type" select="'string_property'"/>
					<xsl:with-param name="key" select="$property_name"/>
					</xsl:call-template-->
			</propertyKey>
			<value>
				<xsl:value-of select="$fact_value" />
			</value>
		</StringFact>
	</xsl:template>

	<!-- 
		Emit a StringProperty record, with name equal to the key.key field.
	-->
	<xsl:template name="string_property">
		<!-- the node-set key of this record -->
		<xsl:param name="property_nodes" />
		<!-- the namespace of the domain class -->
		<xsl:param name="source_namespace" />
		<!-- the key.key of the domain class -->
		<xsl:param name="domain_class" />
		<!-- the creator key.key for this record -->
		<xsl:param name="creatorKeyKey" />
		<!-- the ISO8601 date -->
		<xsl:param name="crawl_date" />

		<StringProperty>
			<key>
				<xsl:copy-of select="$property_nodes" />
			</key>

			<xsl:call-template name="creatorKey">
				<xsl:with-param name="key" select="$creatorKeyKey" />
			</xsl:call-template>

			<lastModified>
				<xsl:value-of select="$crawl_date" />
			</lastModified>
			<name>
				<xsl:value-of select="$property_nodes/key" />
			</name>
			<domainClassKey>
				<xsl:call-template name="codekeytree">
					<xsl:with-param name="namespace" select="$source_namespace" />
					<xsl:with-param name="type" select="'class'" />
					<!-- Geez I have no classes at all.  Until I do ... -->
					<xsl:with-param name="key" select="$domain_class" />
				</xsl:call-template>
			</domainClassKey>
		</StringProperty>
	</xsl:template>

	<!-- 
		Emit a Class record with name equal to the key.key value.
	-->
	<xsl:template name="class">
		<!-- the key.namespace for this record -->
		<xsl:param name="namespace" />
		<!-- the key.key for this record -->
		<xsl:param name="classKeyKey" />
		<!-- the creator key.key for this record -->
		<xsl:param name="creatorKeyKey" />
		<!-- the ISO8601 date -->
		<xsl:param name="crawl_date" />
		<!-- string description -->
		<xsl:param name="description" />
		<Class>
			<key>
				<xsl:call-template name="codekeytree">
					<xsl:with-param name="namespace" select="$namespace" />
					<xsl:with-param name="type" select="'class'" />
					<xsl:with-param name="key" select="$classKeyKey" />
				</xsl:call-template>
			</key>

			<xsl:call-template name="creatorKey">
				<xsl:with-param name="key" select="$creatorKeyKey" />
			</xsl:call-template>

			<lastModified>
				<xsl:value-of select="$crawl_date" />
			</lastModified>
			<name>
				<xsl:value-of select="$classKeyKey" />
			</name>
			<description>
				<xsl:value-of select="$description" />
			</description>
		</Class>
	</xsl:template>

	<!-- 
		Emit a ClassMember record with synthesized name and key.key value.
	-->
	<xsl:template name="class_member">
		<!-- the key.namespace for this record -->
		<xsl:param name="namespace" />
		<!-- the creator key.key for this record -->
		<xsl:param name="creatorKeyKey" />
		<!-- the ISO8601 date -->
		<xsl:param name="crawl_date" />
		<xsl:param name="individual_namespace" />
		<xsl:param name="individualKeyKey" />
		<xsl:param name="class_namespace" />
		<xsl:param name="classKeyKey" />

		<!-- See NameUtil.makeClassMemberName() -->
		<!-- TODO: implement namespace shortcutting -->
		<xsl:variable name="classMemberKeyKey">
			<xsl:text>individual=</xsl:text>
			<xsl:value-of select="$individual_namespace" />
			<xsl:text>/</xsl:text>
			<xsl:value-of select="$individualKeyKey" />
			<xsl:text>&amp;class=</xsl:text>
			<xsl:value-of select="$class_namespace" />
			<xsl:text>/</xsl:text>
			<xsl:value-of select="$classKeyKey" />
		</xsl:variable>

		<ClassMember>
			<key>
				<xsl:call-template name="codekeytree">
					<xsl:with-param name="namespace" select="$namespace" />
					<xsl:with-param name="type" select="'class_member'" />
					<xsl:with-param name="key" select="$classMemberKeyKey" />
				</xsl:call-template>
			</key>

			<xsl:call-template name="creatorKey">
				<xsl:with-param name="key" select="$creatorKeyKey" />
			</xsl:call-template>

			<lastModified>
				<xsl:value-of select="$crawl_date" />
			</lastModified>
			<name>
				<xsl:value-of select="$classMemberKeyKey" />
			</name>
			<individualKey>
				<xsl:call-template name="codekeytree">
					<xsl:with-param name="namespace" select="$individual_namespace" />
					<xsl:with-param name="type" select="'individual'" />
					<xsl:with-param name="key" select="$individualKeyKey" />
				</xsl:call-template>
			</individualKey>
			<classKey>
				<xsl:call-template name="codekeytree">
					<xsl:with-param name="namespace" select="$class_namespace" />
					<xsl:with-param name="type" select="'class'" />
					<xsl:with-param name="key" select="$classKeyKey" />
				</xsl:call-template>
			</classKey>
		</ClassMember>
	</xsl:template>

</xsl:stylesheet>