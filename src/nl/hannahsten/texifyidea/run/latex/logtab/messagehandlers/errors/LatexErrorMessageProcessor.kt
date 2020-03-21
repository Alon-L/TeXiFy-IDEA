package nl.hannahsten.texifyidea.run.latex.logtab.messagehandlers.errors

import nl.hannahsten.texifyidea.run.latex.logtab.LogMagicRegex

abstract class LatexErrorMessageProcessor(vararg val regex: Regex) {
    abstract fun process(message: String): String?
}

/**
 * LaTeX Error: text -> text
 */
object LatexRemoveErrorTextProcessor : LatexErrorMessageProcessor("""LaTeX Error:""".toRegex()) {
    override fun process(message: String): String? {
        regex.forEach {
            if (it.containsMatchIn(message)) return it.replace(message, "").trim()
        }
        return null
    }
}

/**
 * Package amsmath error: text -> amsmath: text
 */
object LatexPackageErrorProcessor : LatexErrorMessageProcessor("""^Package ${LogMagicRegex.PACKAGE_REGEX} Error:""".toRegex()) {
    override fun process(message: String): String? {
        regex.forEach {
            val `package` = it.find(message)?.groups?.get("package")?.value ?: return@forEach
            return "${`package`}: ${it.replace(message, "").trim()}"
        }
        return null
    }
}