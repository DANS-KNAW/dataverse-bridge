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
        exclude-result-prefixes="ddi"
        xsi:schemaLocation="ddi:codebook:2_5 http://www.ddialliance.org/Specification/DDI-Codebook/2.5/XMLSchema/codebook.xsd"
        version="2.0">
    <xsl:output encoding="UTF-8" indent="yes" method="xml"/>

    <!-- not needed anymore because the values needed are looked up and placed in a mapping in this document
    <xsl:variable name="xsd" select="document('narcis-type.xsd')"/>
    -->

    <!-- Translates the DDI to 'dataset.xml' file in DDM format for Sword deposits in the EASY archive for long term preservation
        Note that the translation works with the DDI from the Dataverse Export and not with any DDI.
        DDM schema: https://easy.dans.knaw.nl/schemas/md/ddm/ddm.xsd
        and
        https://easy.dans.knaw.nl/doc/sword2.html
    -->
    <!-- Also note that the DDI from the export seems to be not valid, need to check this out! -->
    <xsl:template match="ddi:codeBook">
        <ddm:DDM>
            <xsl:call-template name="profile"/>
            <xsl:call-template name="dcmiMetadata"/>
        </ddm:DDM>
    </xsl:template>

    <!-- Translating to required DDM fields -->
    <xsl:template name="profile">
        <ddm:profile>
            <dc:title>
                <xsl:value-of select="ddi:docDscr/ddi:citation/ddi:titlStmt/ddi:titl"/>
                <!-- add subtitle if available -->
                <xsl:if test="ddi:docDscr/ddi:citation/ddi:titlStmt/ddi:subTitl != ''">
                    <xsl:text>, </xsl:text>
                    <xsl:value-of select="ddi:stdyDscr/ddi:stdyInfo/ddi:subTitl"/>
                </xsl:if>
            </dc:title>

            <dcterms:description>
                <xsl:value-of select="ddi:stdyDscr/ddi:stdyInfo/ddi:abstract"/>
            </dcterms:description>

            <xsl:for-each select="ddi:stdyDscr/ddi:citation/ddi:rspStmt/ddi:AuthEnty">
                <!-- TODO  improve split surname, initial, titles -->
                <!-- Note that we have to do a simple mapping and have the depositor fix the user settings if the result is not good enough ? -->
                <xsl:variable name="intial" select="substring-after(., ', ')"/>
                <xsl:variable name="surname" select="substring-before(., ', ')"/>
                <dcx-dai:creatorDetails>
                    <dcx-dai:author>
                        <!-- <dcx-dai:titles></dcx-dai:titles> -->
                        <dcx-dai:initials>
                            <xsl:value-of select="$intial"/>
                        </dcx-dai:initials>
                        <dcx-dai:insertions/>
                        <dcx-dai:surname>
                            <xsl:value-of select="$surname"/>
                        </dcx-dai:surname>
                        <dcx-dai:organization>
                            <dcx-dai:name xml:lang="en">
                                <xsl:value-of select="./@affiliation"/>
                            </dcx-dai:name>
                        </dcx-dai:organization>
                    </dcx-dai:author>
                </dcx-dai:creatorDetails>
            </xsl:for-each>

            <!-- <ddm:created><xsl:value-of select="ddi:stdyDscr/ddi:stdyInfo/ddi:depDate"/></ddm:created> -->
            <ddm:created>
                <xsl:value-of select="ddi:docDscr/ddi:citation/ddi:verStmt/ddi:version/@date"/>
            </ddm:created>


            <!-- <ddm:available><xsl:value-of select="ddi:stdyDscr/ddi:citation/ddi:distStmt/ddi:distDate"/></ddm:available> -->
            <!-- current date instead, according to document -->
            <ddm:available>
                <xsl:value-of select="format-dateTime(current-dateTime(), '[Y0001]-[M01]-[D01]')"/>
            </ddm:available>

            <!-- TODO we can have multipe audiences in DDM, but we only want the first one, see the [1] ! -->
            <xsl:for-each select="ddi:stdyDscr/ddi:stdyInfo/ddi:subject/ddi:keyword[1]">
                <xsl:variable name="audience">
                    <xsl:call-template name="audiencefromkeyword">
                        <xsl:with-param name="val" select="."/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:if test="$audience != ''">
                    <ddm:audience>
                        <xsl:value-of select="$audience"/>
                    </ddm:audience>
                </xsl:if>
            </xsl:for-each>

            <!-- In order to handle the case that there is no match, we need to find them again and detect nothing is found -->
            <xsl:variable name="audiences">
                <xsl:for-each select="ddi:stdyDscr/ddi:stdyInfo/ddi:subject/ddi:keyword[1]">
                    <xsl:call-template name="audiencefromkeyword">
                        <xsl:with-param name="val" select="."/>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="$audiences = ''">
                <ddm:audience>
                    <xsl:value-of select="'E10000'"/>
                </ddm:audience>
            </xsl:if>

            <!-- TODO Access Right? DVN: CC0 Waiver, we need to do somthing that works? -->
            <!-- permission erequest is possible per file in Dataverse,
                but this is not exported to DDI, shoudl change this with PR -->
            <!--
            <xsl:choose>
                <xsl:when test="ddi:stdyDscr/ddi:useStmt='CC0 Waiver'">
                    <ddm:accessRights>OPEN_ACCESS</ddm:accessRights>
                </xsl:when>
                <xsl:otherwise>
                    <ddm:accessRights>REQUEST_PERMISSION</ddm:accessRights>
                </xsl:otherwise>
            </xsl:choose>
             -->
            <ddm:accessRights>REQUEST_PERMISSION</ddm:accessRights>
            <!-- NO_ACCESS as default seems a bit 'dark archive' -->
        </ddm:profile>
    </xsl:template>

    <!-- Translating to recommended (but optional) DDM fields -->
    <xsl:template name="dcmiMetadata">
        <ddm:dcmiMetadata>
            <!-- TODO maybe add dcterms:rightsHolder ? -->
            <!-- TODO maybe add dc:publisher ? -->
            <!-- TODO maybe add dc:date ? -->
            <!-- TODO maybe add dc:language ? -->

            <!-- Only one allowed, so just take the first if there is more than one -->
            <xsl:for-each select="ddi:docDscr/ddi:useStmt[1]">
                <dc:license><xsl:value-of select="."/></dc:license>
            </xsl:for-each>

            <!-- list all keywords, even if allreay mapped to audience, because we have human readable Datavese specific text -->
            <xsl:for-each select="ddi:stdyDscr/ddi:stdyInfo/ddi:subject/ddi:keyword">
                <dc:subject>
                    <xsl:value-of select="."/>
                </dc:subject>
            </xsl:for-each>

            <!-- Always a Dataset
            We cannot be more specific, but maybe Collection might be semantically better? -->
            <dc:type xsi:type="dcterms:DCMIType">Dataset</dc:type>

            <!-- compile list of distinct file mime types -->
            <xsl:for-each select="ddi:otherMat/ddi:notes[@level='file' and @subject='Content/MIME Type']">
                <xsl:if test="not(.=preceding::*)">
                    <!-- as long as it is a true mime type we can add the dcterms:IMT -->
                    <dc:format xsi:type="dcterms:IMT"><xsl:value-of select="."/></dc:format>
                </xsl:if>
            </xsl:for-each>

            <!-- TODO would like to have the handle (pid) here, would be very strange not to have it.
                It points to the dataset in Dataverse which has all versions and not just the one deposited in EASY,
                also it might be just a tumbstone. -->
            <dc:identifier><xsl:value-of select="ddi:stdyDscr/ddi:citation/ddi:titlStmt/ddi:IDNo"/></dc:identifier>

            <!-- maybe also add handle as relation with link and dcterms:isVersionOf, then it becomes a clickable link in EASY ?  -->

            <!-- Note: Where do we put the version information; like V1 in the citation? -->

        </ddm:dcmiMetadata>
    </xsl:template>

    <!-- Mapping from the Dataverse keywords to the Narcis Discipline types (https://easy.dans.knaw.nl/schemas/vocab/2015/narcis-type.xsd) -->
    <xsl:template name="audiencefromkeyword">
        <xsl:param name="val"/>
        <!-- make our own map, it's small -->
        <xsl:choose>
            <xsl:when test="$val = 'Agricultural sciences'">
                <xsl:value-of select="'D18000'"/>
            </xsl:when>
            <xsl:when test="$val = 'Law'">
                <xsl:value-of select="'D40000'"/>
            </xsl:when>
            <xsl:when test="$val = 'Social Sciences'">
                <xsl:value-of select="'D60000'"/>
            </xsl:when>
            <xsl:when test="$val = 'Arts and Humanities'">
                <xsl:value-of select="'D30000'"/>
            </xsl:when>
            <xsl:when test="$val = 'Astronomy and Astrophysics'">
                <xsl:value-of select="'D17000'"/>
            </xsl:when>
            <xsl:when test="$val = 'Business and Management'">
                <xsl:value-of select="'D70000'"/>
            </xsl:when>
            <xsl:when test="$val = 'Chemistry'">
                <xsl:value-of select="'D13000'"/>
            </xsl:when>
            <xsl:when test="$val = 'Computer and Information Science'">
                <xsl:value-of select="'D16000'"/>
            </xsl:when>
            <xsl:when test="$val = 'Earth and Environmental Sciences'">
                <xsl:value-of select="'D15000'"/>
            </xsl:when>
            <xsl:when test="$val = 'Engineering'">
                <xsl:value-of select="'D14400'"/>
            </xsl:when>
            <xsl:when test="$val = 'Mathematical Sciences'">
                <xsl:value-of select="'D11000'"/>
            </xsl:when>
            <xsl:when test="$val = 'Medicine, Health and Life Sciences'">
                <xsl:value-of select="'D20000'"/>
            </xsl:when>
            <xsl:when test="$val = 'Physics'">
                <xsl:value-of select="'D12000'"/>
            </xsl:when>
            <xsl:when test="$val = 'Other'">
                <xsl:value-of select="'E10000'"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- Don't do the default mapping to E10000, otherwise we cannot detect that nothing was found -->
                <xsl:value-of select="''"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>