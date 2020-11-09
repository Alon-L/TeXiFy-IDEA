package nl.hannahsten.texifyidea.startup

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import nl.hannahsten.texifyidea.run.linuxpdfviewer.PdfViewer
import nl.hannahsten.texifyidea.run.linuxpdfviewer.evince.EvinceInverseSearchListener
import nl.hannahsten.texifyidea.util.selectedRunConfig

/**
 * @author Sten Wessel
 */
class StartEvinceInverseSearchListener : StartupActivity, DumbAware {

    override fun runActivity(project: Project) {
        if (project.selectedRunConfig()?.pdfViewer == PdfViewer.EVINCE) {
            EvinceInverseSearchListener.start(project)
        }
    }
}
