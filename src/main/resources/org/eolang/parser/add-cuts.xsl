<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="add-refs" version="2.0">
  <!--
  Here we go through all objects which 'line' attribute value
  exists as value of 'ref' attribute in some other object.
  If the object is found, it gets 'cut' attribute to identify the
  object to be removed later.
  -->
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="o[@name and @line=//o[@ref]/@ref]">
    <xsl:copy>
      <xsl:attribute name="cut"/>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
