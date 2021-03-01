package nl.hannahsten.texifyidea.run

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.openapi.project.Project
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.ui.EmptyIcon
import javax.swing.Icon

class LatexExecutionNode(project: Project, val parent: LatexExecutionNode? = null) : PresentableNodeDescriptor<LatexExecutionNode>(project, parent) {

    companion object {

        private val ICON_RUNNING = AnimatedIcon.Default()
        private val ICON_SUCCESS = AllIcons.RunConfigurations.TestPassed
        private val ICON_ERROR = AllIcons.RunConfigurations.TestError
        private val ICON_WARNING = AllIcons.General.Warning
        private val ICON_INFO = AllIcons.General.Information
        private val ICON_SKIPPED = AllIcons.RunConfigurations.TestIgnored
        private val ICON_NONE = EmptyIcon.ICON_16
    }

    val children = mutableListOf<LatexExecutionNode>()

    var state = State.UNKNOWN
    var title: String? = null
    var description: String? = null

    override fun getElement() = this

    override fun update(presentation: PresentationData) {
        icon = state.icon
        presentation.setIcon(icon)
        if (!title.isNullOrEmpty()) {
            presentation.addText("$title: ", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
        }

        if (!description.isNullOrEmpty()) {
            presentation.addText(description, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        }
    }

    enum class State(val icon: Icon) {

        UNKNOWN(ICON_NONE),
        RUNNING(ICON_RUNNING),
        SUCCEEDED(ICON_SUCCESS),
        SKIPPED(ICON_SKIPPED),
        FAILED(ICON_ERROR);
    }
}
