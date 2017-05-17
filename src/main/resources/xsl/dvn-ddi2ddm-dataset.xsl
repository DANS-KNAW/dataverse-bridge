<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns:ddi="ddi:codebook:2_5"
        xmlns:emd="http://easy.dans.knaw.nl/easy/easymetadata/"
        xmlns:dc="http://purl.org/dc/elements/1.1/"
        xmlns:dcterms="http://purl.org/dc/terms/"
        xmlns:ddm="http://easy.dans.knaw.nl/schemas/md/ddm/"
        xmlns:dcx-dai="http://easy.dans.knaw.nl/schemas/dcx/dai/"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:id-type="http://easy.dans.knaw.nl/schemas/vocab/identifier-type/"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        exclude-result-prefixes="xs"
        xsi:schemaLocation="ddi:codebook:2_5 http://www.ddialliance.org/Specification/DDI-Codebook/2.5/XMLSchema/codebook.xsd"
        version="2.0">
    <xsl:output encoding="UTF-8" indent="yes" method="xml"/>
    <xsl:variable name="xsd" select="document('narcis-type.xsd')"/>
    <xsl:template match="/">
        <ddm:DDM>
            <xsl:call-template name="item0"/>
        </ddm:DDM>
    </xsl:template>

    <xsl:template name="item0">
        <ddm:profile>
            <dc:title><xsl:value-of  select="format-dateTime(current-dateTime(), 'D: [Y0001]-[M01]-[D01] - T: [h1]:[m01]')"/> # <xsl:value-of select="/ddi:codeBook/ddi:stdyDscr/ddi:citation/ddi:titlStmt/ddi:titl"/>
            </dc:title>
            <!--                <xsl:value-of select="//*[local-name()='titl']"/> -->
            <!-- TODO create dcterms:description for all description -->
            <dcterms:description><xsl:value-of select="/ddi:codeBook/ddi:stdyDscr/ddi:stdyInfo/ddi:abstract"/></dcterms:description>
            <xsl:for-each select="/ddi:codeBook/ddi:stdyDscr/ddi:citation/ddi:rspStmt/ddi:AuthEnty">
                <xsl:variable name="intial" select="substring-after(., ', ')"/>
                <xsl:variable name="surname" select="substring-before(., ', ')"/>
                <dcx-dai:creatorDetails>
                    <dcx-dai:author>
                        <dcx-dai:titles></dcx-dai:titles>
                        <dcx-dai:initials><xsl:value-of select="$intial"/></dcx-dai:initials>
                        <dcx-dai:insertions></dcx-dai:insertions>
                        <!-- TODO:  Split surname, initial, titles-->

                        <dcx-dai:surname><xsl:value-of select="$surname"/></dcx-dai:surname>
                        <dcx-dai:organization>
                            <dcx-dai:name xml:lang="en"><xsl:value-of select="./@affiliation"/></dcx-dai:name>
                        </dcx-dai:organization>
                    </dcx-dai:author>
                </dcx-dai:creatorDetails>
            </xsl:for-each>
            <ddm:created><xsl:value-of select="/ddi:codeBook/ddi:stdyDscr/ddi:stdyInfo/ddi:depDate"/></ddm:created>
            <ddm:available><xsl:value-of select="/ddi:codeBook/ddi:stdyDscr/ddi:citation/ddi:distStmt/ddi:distDate"/></ddm:available>
            <xsl:variable name="matchedaudience">
                <xsl:for-each select="/ddi:codeBook/ddi:stdyDscr/ddi:stdyInfo/ddi:subject/ddi:keyword">
                    <xsl:variable name="keyword" select="."/>
                    <xsl:variable name="narciscode" select="$xsd//xs:enumeration[./xs:annotation/xs:documentation/text()=$keyword]/@value"/>
                    <xsl:choose>
                        <xsl:when  test="string-length($narciscode) != 0">
                            <ddm:audience><xsl:value-of select="$narciscode"/></ddm:audience>
                        </xsl:when>
                    </xsl:choose>
                </xsl:for-each>
            </xsl:variable>
            <xsl:choose>
                <xsl:when test="string-length($matchedaudience) != 0">
                    <xsl:call-template name="ddmaudience"/>
                </xsl:when>
                <xsl:otherwise>
                    <ddm:audience>D10000</ddm:audience>
                </xsl:otherwise>
            </xsl:choose>
            <!--<xsl:call-template name="ddmaudience"/>-->
            <!-- TODO Access Right? DVN: CC0 Waiver -->
            <ddm:accessRights>NO_ACCESS</ddm:accessRights>
        </ddm:profile>
    </xsl:template>
    <xsl:template name="ddmaudience">
        <xsl:for-each select="/ddi:codeBook/ddi:stdyDscr/ddi:stdyInfo/ddi:subject/ddi:keyword">
            <xsl:variable name="keywordval" select="."/>
            <xsl:variable name="audienceval" select="$xsd//xs:enumeration[./xs:annotation/xs:documentation/text()=$keywordval]/@value"/>
            <xsl:choose>
                <xsl:when test="$audienceval">
                    <ddm:audience><xsl:value-of select="$audienceval"/></ddm:audience>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>

    </xsl:template>
</xsl:stylesheet>
