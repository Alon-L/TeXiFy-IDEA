package nl.hannahsten.texifyidea.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import nl.hannahsten.texifyidea.reference.LatexLabelParameterReference
import nl.hannahsten.texifyidea.util.Magic
import nl.hannahsten.texifyidea.util.firstParentOfType

/**
 * If the normal text is the parameter of a \ref-like command, get the references to the label declaration.
 */
@Suppress("RemoveExplicitTypeArguments") // Somehow they are needed
fun getReferences(element: LatexNormalText): Array<PsiReference> {
    val command = element.firstParentOfType(LatexCommands::class)
    return if (Magic.Command.reference.contains(command?.name)) {
        val reference = LatexLabelParameterReference(element)
        if (reference.multiResolve(false).isNotEmpty()) {
            arrayOf<PsiReference>(reference)
        } else {
            emptyArray<PsiReference>()
        }
    } else {
        emptyArray<PsiReference>()
    }
}

/**
 * If [getReferences] returns one reference return that one, null otherwise.
 */
fun getReference(element: LatexNormalText): PsiReference? {
    val references = getReferences(element)
    return if (references.size != 1) {
        null
    } else {
        references[0]
    }
}

fun getNameIdentifier(element: LatexNormalText): PsiElement {
    return element
}

fun setName(element: LatexNormalText, name: String): PsiElement {
    // Get a new psi element for the complete label command (\label included),
    // because if we replace the complete command instead of just the normal text
    // then the indices will be updated, which is necessary for the reference resolve to work
    val labelCommand = element.firstParentOfType(LatexCommands::class)
    val labelText = "${labelCommand?.name}{$name}"
    val newElement = LatexPsiHelper(element.project).createFromText(labelText).firstChild
    val oldNode = labelCommand?.node
    val newNode = newElement.node
    if (oldNode == null) {
        labelCommand?.parent?.node?.addChild(newNode)
    }
    else {
        labelCommand.parent.node.replaceChild(oldNode, newNode)
    }
    return element
}

fun getName(element: LatexNormalText): String {
    return element.text ?: ""
}