package nl.rubensten.texifyidea.action.evince

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import nl.rubensten.texifyidea.TexifyIcons
import nl.rubensten.texifyidea.action.EditorAction
import nl.rubensten.texifyidea.run.evince.EvinceConversation
import nl.rubensten.texifyidea.run.evince.isEvinceAvailable

/**
 * Starts a forward search action in Evince.
 *
 * Note: this is only available on Linux.
 *
 * @author Thomas Schouten
 */
open class ForwardSearchAction : EditorAction(
        "_ForwardSearch",
        TexifyIcons.RIGHT
) {

    override fun actionPerformed(file: VirtualFile, project: Project, editor: TextEditor) {
        if (!isEvinceAvailable()) {
            return
        }

        val document = editor.editor.document
        val line = document.getLineNumber(editor.editor.caretModel.offset) + 1

        EvinceConversation.forwardSearch(sourceFilePath = file.path, line = line)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = isEvinceAvailable()
    }
}