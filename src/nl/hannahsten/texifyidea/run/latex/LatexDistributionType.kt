package nl.hannahsten.texifyidea.run.latex

import com.intellij.openapi.project.Project
import nl.hannahsten.texifyidea.settings.LatexSdk
import nl.hannahsten.texifyidea.settings.LatexSdkUtil

/**
 * Options for the run configuration.
 * See [LatexSdk].
 */
enum class LatexDistributionType(val displayName: String) {
    TEXLIVE("TeX Live"),
    MIKTEX("MiKTeX"),
    WSL_TEXLIVE("TeX Live using WSL"),
    DOCKER_MIKTEX("Dockerized MiKTeX"),
    PROJECT_SDK("Use project SDK");

    fun isMiktex() = this == MIKTEX || this == DOCKER_MIKTEX

    fun isTexlive() = this == TEXLIVE || this == WSL_TEXLIVE

    fun isAvailable(project: Project) = LatexSdkUtil.isAvailable(this, project)

    override fun toString() = displayName

    companion object {
        fun valueOfIgnoreCase(value: String?): LatexDistributionType {
            return values().firstOrNull { it.name.equals(value, true) } ?: TEXLIVE
        }
    }
}