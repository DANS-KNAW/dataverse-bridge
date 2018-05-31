<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns:ddi="ddi:codebook:2_5"
        xmlns:dcterms="http://purl.org/dc/terms/"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns="https://easy.dans.knaw.nl/schemas/bag/metadata/files/files.xsd"
        exclude-result-prefixes="ddi"
        xsi:schemaLocation="ddi:codebook:2_5 http://www.ddialliance.org/Specification/DDI-Codebook/2.5/XMLSchema/codebook.xsd"
        version="2.0">
    <xsl:output encoding="UTF-8" indent="yes" method="xml"/>

    <xsl:template match="ddi:codeBook">
        <files>
            <xsl:call-template name="files"/>
        </files>
    </xsl:template>

    <!-- Translates the DDI to 'files.xml' for Sword deposits in the EASY archive for long term preservation
        Note that the translation works with the DDI from the Dataverse Export and not with any DDI.
        file schema: https://easy.dans.knaw.nl/schemas/bag/metadata/files/files.xsd
        and
        https://easy.dans.knaw.nl/doc/sword2.html
    -->

    <xsl:template name="files">
        <xsl:for-each select="ddi:otherMat/ddi:labl">
            <xsl:element name="file">
                <xsl:attribute name="filepath" select="concat('data/',.)"/>
                <dcterms:format xsi:type="dcterms:IMT"><xsl:value-of select="../ddi:notes[@level='file' and @subject='Content/MIME Type']"/></dcterms:format>
                <dcterms:title><xsl:value-of select="."/></dcterms:title>

                <!-- description is not used by EASY
                <xsl:for-each select="../ddi:txt">
                    <dcterms:description><xsl:value-of select="."/></dcterms:description>
                </xsl:for-each>
                  -->

                <!-- the same for all files in this dataset, so not informative
                <dcterms:created><xsl:value-of select="/ddi:codeBook/ddi:stdyDscr/ddi:stdyInfo/ddi:depDate"/></dcterms:created>
                -->

                <!-- No dcterms:accessRights we don't heve those in the ddi, dataset rights are used implicitly ? -->
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>