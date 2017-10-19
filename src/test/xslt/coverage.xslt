<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />

  <xsl:variable name="missed">
    <xsl:value-of select="/report/counter[@type='INSTRUCTION']/@missed" />
  </xsl:variable>

  <xsl:variable name="covered">
    <xsl:value-of select="/report/counter[@type='INSTRUCTION']/@covered" />
  </xsl:variable>

  <xsl:variable name="branches_missed">
    <xsl:value-of select="/report/counter[@type='BRANCH']/@missed" />
  </xsl:variable>

  <xsl:variable name="branches_covered">
    <xsl:value-of select="/report/counter[@type='BRANCH']/@covered" />
  </xsl:variable>

  <xsl:template match="/report[@name='jaoed']/counter[@type='INSTRUCTION']">
--------------------------------------
TEST COVERAGE RESULTS
--------------------------------------
total_test_coverage: <xsl:value-of select="round(100 * ($covered div ($covered + $missed)))" />%
lines_covered:       <xsl:value-of select="$covered" /> of <xsl:value-of select="$covered + $missed" />
branches_covered:    <xsl:value-of select="$branches_covered" /> of <xsl:value-of select="$branches_covered + $branches_missed" />
--------------------------------------
  </xsl:template>
</xsl:stylesheet>
