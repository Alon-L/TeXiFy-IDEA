package nl.hannahsten.texifyidea.psi

import com.intellij.testFramework.ParsingTestCase
import nl.hannahsten.texifyidea.LatexParserDefinition

class LatexParserToPsiTest : ParsingTestCase("", "tex", LatexParserDefinition()) {

    override fun getTestDataPath(): String = "test/resources/psi/parser"

    override fun skipSpaces(): Boolean = false

    override fun includeRanges(): Boolean = true

    fun testParsingInlineVerbatim() {
        doTest(true)
    }
}