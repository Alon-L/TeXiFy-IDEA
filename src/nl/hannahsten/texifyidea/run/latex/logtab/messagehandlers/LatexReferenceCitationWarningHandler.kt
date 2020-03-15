package nl.hannahsten.texifyidea.run.latex.logtab.messagehandlers

import nl.hannahsten.texifyidea.run.latex.logtab.LatexLogMessage
import nl.hannahsten.texifyidea.run.latex.logtab.LatexLogMessageType
import nl.hannahsten.texifyidea.run.latex.logtab.LatexMessageHandler

object LatexReferenceCitationWarningHandler : LatexMessageHandler(
        LatexLogMessageType.WARNING,
        """^$LATEX_WARNING_REGEX (?<ref>Reference|Citation) $REFERENCE_REGEX on page \d+ undefined $LINE_REGEX$""".toRegex()
) {
    override fun findMessage(text: String, newText: String, currentFile: String?): LatexLogMessage? {
        regex.first().find(text)?.apply {
            val ref = groups["ref"]?.value
            val label = groups["label"]?.value
            val message = "$ref $label undefined"
            val line = groups["line"]?.value?.toInt()
            return LatexLogMessage(message, currentFile, line, messageType)
        }
        return null
    }
}