<?xml version="1.0"?>
<!--
	So, the point of this XSL is to figure out how to do these transforms
	by hand.  Ultimately I'd like to create some sort of template for this,
	so that each new site is much simpler.  And eventually, automatic.
	
	Anyway the goal, for now, is to take a page representing an individual
	and a set of facts about that individual, and transform it into the following
	record types:
	
	<Individual>
	<StringProperty>
	<StringFact>
	
	Use "String" because I don't want to deal with parsing measurements.  Some later
	"gardener" process will run over the DB and duplicate these string things as the
	correct type.
	
	Note, even things like "latin name" are facts (and in that case, a properly typed
	StringFact.
	
	The input pages in this case don't have it, but nearby ones do: taxonomic data like
	genus, species, family, etc.  Are those classes or individuals?
	
	see http://sites.google.com/a/joelsgarage.com/wik/Home/design/ontology
	
	they're classes.
	
	So I might want to produce some records like
	
	<Class>
	<ClassAxiom>
	
	I should have an authoritative linnean taxonomy for reference, if that matters.
	
	Anyway for now, it's just individuals, properties, and facts.
	
-->
<xsl:stylesheet xmlns:html="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exsl="http://exslt.org/common"
	extension-element-prefixes="exsl" version="1.0">

	<xsl:output method="xml" indent="yes" />

	<xsl:include href="model.xsl" />

	<!-- passed in by setParameter() -->
	<!-- TODO: replace this default with blank -->
	<xsl:param name="crawl_date" select="'1968-06-06T15:35'" />

	<!-- yay globals! -->
	<xsl:variable name="source_namespace">plants.usda.gov</xsl:variable>
	<xsl:variable name="ref_namespace">p1</xsl:variable>
	<!-- TODO: remove this hack -->
	<xsl:variable name="className">Plantae</xsl:variable>
	<xsl:variable name="creatorKeyKey">usda-plants-characteristics-importer</xsl:variable>



	<xsl:template match="/">
		<container>
			<xsl:call-template name="class">
				<xsl:with-param name="namespace" select="$source_namespace" />
				<xsl:with-param name="classKeyKey" select="$className" />
				<xsl:with-param name="creatorKeyKey" select="$creatorKeyKey" />
				<xsl:with-param name="crawl_date" select="$crawl_date" />
				<xsl:with-param name="description" select="''" />
			</xsl:call-template>
			<xsl:apply-templates
				select="html:html/html:body/html:table/html:tbody/html:tr[5]/html:td/html:table/html:tbody/html:tr/html:td[4]/html:table/html:tbody/html:tr[3]/html:td[1]/child::*" />
		</container>
	</xsl:template>

	<xsl:template
		match="html:html/html:body/html:table/html:tbody/html:tr[5]/html:td/html:table/html:tbody/html:tr/html:td[4]/html:table/html:tbody/html:tr[3]/html:td[1]/child::*">
		<xsl:choose>
			<xsl:when test="position()=1">
				<!-- do nothing, this is navgational links -->
			</xsl:when>
			<xsl:when test="name()='p'">
				<xsl:call-template name="cultivar_name" />
			</xsl:when>
			<xsl:when test="name()='table'">
				<xsl:call-template name="data_table" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- 
		Emit an Individual record corresponding to this page.
		Recognize the latin and common names, and make each one into a StringFact,
		with accompanying StringProperty records.
		
		NEW: emit Class and ClassMember records.
	-->
	<xsl:template name="cultivar_name">
		<xsl:variable name="individual_external_key">
			<xsl:call-template name="codekey" />
		</xsl:variable>

		<xsl:variable name="latin_name">
			<xsl:for-each select="html:span/html:br[1]/preceding-sibling::node()">
				<xsl:value-of select="normalize-space()" />
				<xsl:if test="not(position() = last())">
					<xsl:text> </xsl:text>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>

		<xsl:variable name="common_name">
			<xsl:variable name="after1" select="html:span/html:br[1]/following-sibling::node()" />
			<xsl:variable name="before2" select="html:span/html:br[2]/preceding-sibling::node()" />
			<!-- intersect after1 with before2 -->
			<xsl:for-each select="$after1[count(.|$before2)=count($before2)]">
				<xsl:value-of select="normalize-space()" />
				<xsl:if test="not(position() = last())">
					<xsl:text> </xsl:text>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>

		<xsl:variable name="common_name_property_key">
			<xsl:call-template name="codekeytree">
				<xsl:with-param name="namespace" select="$ref_namespace" />
				<xsl:with-param name="type" select="'string_property'" />
				<xsl:with-param name="key" select="'Common Name'" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="common_name_property_nodes"
			select="exsl:node-set($common_name_property_key)" />

		<xsl:variable name="latin_name_property_key">
			<xsl:call-template name="codekeytree">
				<xsl:with-param name="namespace" select="$ref_namespace" />
				<xsl:with-param name="type" select="'string_property'" />
				<xsl:with-param name="key" select="'Latin Name'" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="latin_name_property_nodes"
			select="exsl:node-set($latin_name_property_key)" />

		<xsl:call-template name="individual">
			<xsl:with-param name="namespace" select="$source_namespace" />
			<xsl:with-param name="individual_id_param" select="$individual_external_key" />
			<xsl:with-param name="creatorKeyKey" select="$creatorKeyKey" />
			<xsl:with-param name="crawl_date" select="$crawl_date" />
		</xsl:call-template>

		<xsl:call-template name="class_member">
			<xsl:with-param name="namespace" select="$source_namespace" />
			<xsl:with-param name="creatorKeyKey" select="$creatorKeyKey" />
			<xsl:with-param name="crawl_date" select="$crawl_date" />
			<xsl:with-param name="individual_namespace" select="$source_namespace" />
			<xsl:with-param name="individualKeyKey" select="$individual_external_key" />
			<xsl:with-param name="class_namespace" select="$source_namespace" />
			<xsl:with-param name="classKeyKey" select="$className" />
		</xsl:call-template>

		<xsl:call-template name="string_property">
			<xsl:with-param name="property_nodes" select="$common_name_property_nodes" />
			<xsl:with-param name="domain_class" select="$className" />
			<xsl:with-param name="source_namespace" select="$source_namespace" />
			<xsl:with-param name="creatorKeyKey" select="$creatorKeyKey" />
			<xsl:with-param name="crawl_date" select="$crawl_date" />
		</xsl:call-template>

		<xsl:call-template name="string_property">
			<xsl:with-param name="property_nodes" select="$latin_name_property_nodes" />
			<xsl:with-param name="domain_class" select="$className" />
			<xsl:with-param name="source_namespace" select="$source_namespace" />
			<xsl:with-param name="creatorKeyKey" select="$creatorKeyKey" />
			<xsl:with-param name="crawl_date" select="$crawl_date" />
		</xsl:call-template>

		<xsl:call-template name="string_fact">
			<xsl:with-param name="individual_id_param" select="$individual_external_key" />
			<xsl:with-param name="property_nodes" select="$common_name_property_nodes" />
			<xsl:with-param name="fact_value" select="$common_name" />
			<xsl:with-param name="source_namespace" select="$source_namespace" />
			<xsl:with-param name="creatorKeyKey" select="$creatorKeyKey" />
			<xsl:with-param name="crawl_date" select="$crawl_date" />
		</xsl:call-template>

		<xsl:call-template name="string_fact">
			<xsl:with-param name="individual_id_param" select="$individual_external_key" />
			<xsl:with-param name="property_nodes" select="$latin_name_property_nodes" />
			<xsl:with-param name="fact_value" select="$latin_name" />
			<xsl:with-param name="source_namespace" select="$source_namespace" />
			<xsl:with-param name="creatorKeyKey" select="$creatorKeyKey" />
			<xsl:with-param name="crawl_date" select="$crawl_date" />
		</xsl:call-template>

	</xsl:template>



	<xsl:template name="data_table">
		<!-- first get the subject id -->
		<xsl:variable name="individual_id">
			<!-- this is the <table>; select the preceding paragraph; node these are numbered backwards;
				the first one is the last one in document order -->
			<xsl:for-each select="preceding-sibling::html:p[1]">
				<!-- just the *immediately* preceding node -->
				<xsl:call-template name="codekey" />
			</xsl:for-each>
		</xsl:variable>
		<xsl:for-each select="html:tbody/html:tr">
			<xsl:call-template name="row">
				<xsl:with-param name="individual_id_param" select="$individual_id" />
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>


	<xsl:template name="name">
		<latin-name>
			<xsl:for-each select="html:span/html:br[1]/preceding-sibling::node()">
				<xsl:value-of select="normalize-space()" />
				<xsl:if test="not(position() = last())">
					<xsl:text> </xsl:text>
				</xsl:if>
			</xsl:for-each>
		</latin-name>
		<common-name>
			<xsl:variable name="after1" select="html:span/html:br[1]/following-sibling::node()" />
			<xsl:variable name="before2" select="html:span/html:br[2]/preceding-sibling::node()" />
			<!-- intersect after1 with before2 -->
			<xsl:for-each select="$after1[count(.|$before2)=count($before2)]">
				<xsl:value-of select="normalize-space()" />
				<xsl:if test="not(position() = last())">
					<xsl:text> </xsl:text>
				</xsl:if>
			</xsl:for-each>
		</common-name>
		<code>
			<xsl:call-template name="code" />
		</code>
	</xsl:template>

	<!-- serialized externalKey: type/namespace/key
		see ExternalKey.java -->
	<xsl:template name="stringcode">
		individual/plants.usda.gov/
		<xsl:call-template name="codekey" />
	</xsl:template>

	<!-- 
		For each row in the data table, make a string fact and string property
	-->

	<xsl:template name="row">
		<!-- now this is just the string key not the RTF -->
		<xsl:param name="individual_id_param" />

		<xsl:choose>
			<xsl:when test="html:td[@class='divider_bg']">
				<!-- if this divider child exists in this row, then this row is a (no-op) divider -->
			</xsl:when>
			<xsl:when test="html:td[@class='hdrblkbold']">
				<!-- this is a header row for an attribute group.
					TODO: prepend the attribute with the group name?  -->
			</xsl:when>
			<xsl:otherwise>

				<xsl:variable name="property_name">
					<xsl:for-each select="html:td[1]">
						<xsl:value-of select="normalize-space()" />
					</xsl:for-each>
				</xsl:variable>

				<xsl:variable name="property_key">
					<xsl:call-template name="codekeytree">
						<xsl:with-param name="namespace" select="$source_namespace" />
						<xsl:with-param name="type" select="'string_property'" />
						<xsl:with-param name="key" select="$property_name" />
					</xsl:call-template>
				</xsl:variable>

				<xsl:variable name="property_nodes" select="exsl:node-set($property_key)" />

				<xsl:variable name="fact_value">
					<xsl:for-each select="html:td[2]">
						<xsl:value-of select="normalize-space()" />
					</xsl:for-each>
				</xsl:variable>

				<!-- for every row, make a StringFact -->
				<xsl:call-template name="string_fact">
					<xsl:with-param name="individual_id_param" select="$individual_id_param" />
					<xsl:with-param name="property_nodes" select="$property_nodes" />
					<xsl:with-param name="fact_value" select="$fact_value" />
					<xsl:with-param name="source_namespace" select="$source_namespace" />
					<xsl:with-param name="creatorKeyKey" select="$creatorKeyKey" />
					<xsl:with-param name="crawl_date" select="$crawl_date" />
				</xsl:call-template>

				<!-- for every row, also make a StringProperty.
					This hugely duplicates the StringProperty messages, but
					the context of duplication is above us; we don't have an alternative. -->
				<xsl:call-template name="string_property">
					<!-- TODO: move this name out of usda namespace - it's not their name, it's my name -->
					<xsl:with-param name="property_nodes" select="$property_nodes" />
					<xsl:with-param name="domain_class" select="$className" />
					<xsl:with-param name="source_namespace" select="$source_namespace" />
					<xsl:with-param name="creatorKeyKey" select="$creatorKeyKey" />
					<xsl:with-param name="crawl_date" select="$crawl_date" />
				</xsl:call-template>

			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- xml version of the externalKey -->
	<xsl:template name="code">
		<namespace>plants.usda.gov</namespace>
		<type>individual</type>
		<key>
			<xsl:call-template name="codekey" />
		</key>
	</xsl:template>

	<!-- The individual key.key field -->
	<xsl:template name="codekey">
		<xsl:for-each select="html:span/html:br[2]/following-sibling::node()">
			<xsl:if test="string-length() > 0">
			    <!-- remove newline and tab -->
				<xsl:value-of select="translate(., ' &#xA;&#x9;', '')" />
				<xsl:if test="not(position() = last())">
					<xsl:text>_</xsl:text>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>