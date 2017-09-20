package nl.rubensten.texifyidea.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiFile
import nl.rubensten.texifyidea.inspections.latex.LatexTooLargeSectionInspection.Companion.findNextSection
import nl.rubensten.texifyidea.inspections.latex.LatexTooLargeSectionInspection.InspectionFix.Companion.findLabel
import nl.rubensten.texifyidea.psi.LatexCommands
import nl.rubensten.texifyidea.util.*

/**
 * @author Ruben Schellekens
 */
open class LatexMoveSectionToFileIntention : TexifyIntentionBase("Move section contents to seperate file") {

    companion object {

        val AFFECTED_COMMANDS = setOf("\\section", "\\chapter")
    }

    override fun startInWriteAction() = true

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (editor == null || file == null || !file.isLatexFile()) {
            return false
        }

        val element = file.findElementAt(editor.caretModel.offset) ?: return false
        val selected = element as? LatexCommands ?: element.parentOfType(LatexCommands::class) ?: return false
        return selected.name in AFFECTED_COMMANDS
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (editor == null || file == null) {
            return
        }

        // Find related elements.
        val element = file.findElementAt(editor.caretModel.offset) ?: return
        val sectionCommand = element as? LatexCommands ?: element.parentOfType(LatexCommands::class) ?: return
        val document = file.document() ?: return
        val label = findLabel(sectionCommand)
        val nextCmd = findNextSection(sectionCommand)

        // Find text.
        val start = label?.endOffset() ?: sectionCommand.endOffset()
        val end = (nextCmd?.textOffset ?: document.textLength ?: return)
        val text = document.getText(TextRange(start, end)).trimEnd().removeIndents()

        // Create new file.
        val fileNameBraces = sectionCommand.requiredParameter(0) ?: return
        val fileName = fileNameBraces.replace("}", "").replace("{", "")
        val root = file.findRootFile().containingDirectory.virtualFile.canonicalPath

        // Execute write actions.
        val fileSystem = LocalFileSystem.getInstance()
        val filePath = "$root/$fileName.tex";
        val createdFile = TexifyUtil.createFile(filePath, text)
        document.deleteString(start, end);
        val createdFileName = createdFile?.name?.substring(0, createdFile.name.length - 4)
        val indent = sectionCommand.findIndentation()
        document.insertString(start, "\n$indent\\input{$createdFileName}\n\n")
    }
}