<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="vars-float-down" version="2.0">
  <!--
  The transformation does almost the opposite of "/org/eolang/parser/vars-float-up.xsl".

  At the level of EO it looks like:

  [] > object             [] > object
    opcode > somebody       seq > @
      42               =>     opcode > somebody
    seq > @                     42
      somebody.             opcode > somebody
                              42

  In XMIR it works via "ref" and "line" attributes, it the transformation must
  be applied after "/org/eolang/parser/add-refs.xsl"

  <o>                                         <o>
    <o ref="22" base="abc" line="3"/>           <o line="22" base="xyz" name="hello"/>
  </o>                                   =>   </o>
  ...
  <o line="22" base="xyz" name="hello"/>

  To achieve the right result, the transformation must be applied several times.
  -->
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:key name="o-by-line" match="o" use="@line"/>
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="o[@ref and @ref!='' and not(ancestor::o[@ref])]">
    <xsl:variable name="reference" select="@ref"/>
    <xsl:variable name="found" select="key('o-by-line', $reference)"/>
    <xsl:element name="o">
      <xsl:copy-of select="$found/@*[name() != 'cut']"/>
      <xsl:choose>
        <xsl:when test="$found/o">
          <xsl:apply-templates select="$found/o"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$found"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
