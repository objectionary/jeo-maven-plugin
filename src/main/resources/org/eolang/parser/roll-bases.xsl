<?xml version="1.0" encoding="UTF-8"?>
<!--
The MIT License (MIT)

Copyright (c) 2016-2024 Objectionary.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="roll-bases" version="2.0">
  <!--
  This XSL rolls ONE reversed object dispatch into single base.
  To get all the dispatches rolled up you need to apply the transformation
  multiple times until it makes no effect.

  - <o base=".org">
      <o base="Q"/>  => <o base="org"/>
    </o>
  - <o base=".instructions">
      <o base="$"/>  => <o base="instructions"/>
    </o>
  - <o base=".eolang">
      <o base="org"/>  => <o base="org.eolang"/>
    </o>
  - <o base=".seq">
      <o base="org.eolang"/>  => <o base="org.eolang.seq"/>
    </o>
  -->
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="o[starts-with(@base, '.') and o[position()=1 and not(starts-with(@base, '.')) and count(o)=0]]">
    <xsl:element name="o">
      <xsl:for-each select="@*[name()!='base']">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:attribute name="base">
        <xsl:choose>
          <xsl:when test="o[position()=1 and (@base='Q' or @base='$')]">
            <xsl:value-of select="substring-after(@base, '.')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="concat(o[position()=1]/@base, @base)"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:for-each select="o[position()&gt;1]">
        <xsl:copy-of select="."/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
