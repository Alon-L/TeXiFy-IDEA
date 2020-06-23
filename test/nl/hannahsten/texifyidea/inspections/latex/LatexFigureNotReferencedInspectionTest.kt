package nl.hannahsten.texifyidea.inspections.latex

import nl.hannahsten.texifyidea.file.LatexFileType
import nl.hannahsten.texifyidea.inspections.TexifyInspectionTestBase

class LatexFigureNotReferencedInspectionTest : TexifyInspectionTestBase(LatexFigureNotReferencedInspection()) {

    fun testFigureNotReferencedWarning() {
        myFixture.configureByText(LatexFileType, """
            \usepackage{listings}
            \begin{document}
                \begin{figure}
                    <weak_warning descr="Figure is not referenced">\label{fig:some-figure}</weak_warning>
                \end{figure}
            \end{document}
        """.trimIndent())
        myFixture.checkHighlighting(false, false, true, false)
    }

    fun testFigureReferencedNoWarning() {
        myFixture.configureByText(LatexFileType, """
            \usepackage{listings}
            \begin{document}
                \begin{figure}
                    \label{fig:some-figure}
                \end{figure}
                
                \ref{fig:some-figure}
            \end{document}
        """.trimIndent())
        myFixture.checkHighlighting(false, false, true, false)
    }

    fun testFigureReferencedMultipleReferencesNoWarning() {
        myFixture.configureByText(LatexFileType, """
            \documentclass{article}
            \usepackage{cleveref}
            \begin{document}
                \begin{figure}
                    Figure text.
                    \caption{Caption}
                    \label{fig:some-figure}
                \end{figure}
            
                \begin{figure}
                    Figure text.
                    \caption{Caption2}
                    \label{fig:some-figure2}
                \end{figure}
            
                I ref~\cref{fig:some-figure,fig:some-figure2}
            \end{document}
        """.trimIndent())
        myFixture.checkHighlighting(false, false, true, false)
    }
}