package nl.hannahsten.texifyidea.run.latex.logtab.ui

import com.intellij.icons.AllIcons
import javax.swing.Icon

enum class LatexKeywordFilter(val triggers: List<String>, val icon: Icon) {
    OVERFULL_HBOX(listOf("overfull \\hbox", "underfull \\hbox", "overfull \\vbox", "underfull \\vbox"), icon = AllIcons.General.ArrowSplitCenterH);

    override fun toString(): String {
        return triggers.dropLast(1).joinToString() + " and " + triggers.last()
    }
}