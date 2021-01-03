package nl.hannahsten.texifyidea.index.file

import com.intellij.util.indexing.FileContent
import nl.hannahsten.texifyidea.util.containsAny
import nl.hannahsten.texifyidea.util.startsWithAny

/**
 * Extract docs and do some basic formatting on documentation strings found in dtx files.
 *
 * @author Thomas
 */
object LatexDocsRegexer {

    /**
     * Regexes and replacements which clean up the documentation.
     */
    private val formattingReplacers = listOf(
        // Commands to remove entirely,, making sure to capture in the argument nested braces
        Pair("""\\(cite|footnote)\{(\{[^}]*}|[^}])+?}\s*""".toRegex(), { "" }),
        // \cs command from the doctools package
        Pair("""(?<pre>[^|]|^)\\c[sn]\{(?<command>[^}]+?)}""".toRegex(), { result -> result.groups["pre"]?.value + "\\" + result.groups["command"]?.value }),
        // todo use html formatting for textbf/cmd/env
        // Other commands, except when in short verbatim
        Pair<Regex, (MatchResult) -> String>("""(?<pre>[^|]|^)\\(?:textbf|emph|textsf|cmd|pkg|env)\{(?<argument>(\{[^}]*}|[^}])+?)}""".toRegex(), { result -> result.groups["pre"]?.value + result.groups["argument"]?.value }),
        // Short verbatim, provided by ltxdoc
        Pair("""\|""".toRegex(), { "" }),
    )

    /**
     * Commands that indicate that the documentation of a macro has stopped.
     */
    private val stopsDocs = setOf("\\begin{", "\\DescribeMacro")

    /**
     * Skip lines that start with one of these strings.
     */
    private val skipLines = arrayOf("\\changes")

    /**
     * Regex for the documentation lines itself.
     * Some things to note:
     * - Docs do not necessarily start on their own line
     * - We do use empty lines to guess where the docs end
     */
    private val docsAfterMacroRegex = """(?:%?\h*(?<line>.+))""".toRegex()

    /**
     * Should format to valid HTML as used in the docs popup.
     * Only done when indexing, but it should still be fast because it can be done up to 28714 times for full TeX Live.
     */
    fun format(docs: String): String {
        var formatted = docs
        formattingReplacers.forEach { formatted = it.first.replace(formatted, it.second) }
        return formatted
    }

    /**
     * Extract documentation keys and values from [inputData] based on [regex] and put them in [map].
     * Assumes that the regex contains groups with the name "key" and "value".
     */
    fun getDocsByRegex(inputData: FileContent, map: MutableMap<String, String>, regex: Regex) {
        val macrosBeingOverloaded = mutableSetOf<String>()

        regex.findAll(inputData.contentAsText).forEach loop@{ macroWithDocsResult ->
            val key = macroWithDocsResult.groups["key"]?.value ?: return@loop
            // The string that hopefully contains some documentation about the macro
            val containsDocs = macroWithDocsResult.groups["value"]?.value ?: return@loop

            // If we are overloading macros, just save this one to fill with documentation later.
            if (containsDocs.trim(' ', '%').startsWithAny("\\begin{macro}", "\\DescribeMacro")) {
                macrosBeingOverloaded.add(key)
            }
            else {
                var docs = ""
                // Strip the line prefixes and guess until where the documentation goes.
                run breaker@{
                    docsAfterMacroRegex.findAll(containsDocs).forEach { lineResult ->
                        val line = lineResult.groups["line"]?.value ?: return@forEach
                        if (line.trim().startsWithAny(*skipLines)) {
                            return@forEach
                        }
                        else if (!line.containsAny(stopsDocs) && line.trim(' ', '%').isNotBlank()) {
                            docs += " $line"
                        }
                        else {
                            return@breaker
                        }
                    }
                }
                map[key] = format(docs.trim())
                if (macrosBeingOverloaded.isNotEmpty()) {
                    macrosBeingOverloaded.forEach { map[it] = format(docs.trim()) }
                    macrosBeingOverloaded.clear()
                }
            }
        }
    }
}