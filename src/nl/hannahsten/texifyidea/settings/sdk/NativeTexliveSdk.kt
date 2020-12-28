package nl.hannahsten.texifyidea.settings.sdk

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import nl.hannahsten.texifyidea.util.runCommand

/**
 * TeX Live, as installed natively by the OS's package manager.
 * Differences with [TexliveSdk] are for example that not all executables (like tlmgr) are available,
 * and the texmf-dist folder is not assumed to be in a fixed location relative to the executables, but can be anywhere.
 */
class NativeTexliveSdk : TexliveSdk("Native TeX Live SDK") {

    companion object {
        // Path to texmf-dist
        val texmfDistPath: String by lazy {
            "kpsewhich article.sty".runCommand()?.substringBefore("texmf-dist") + "texmf-dist"
        }
    }

    override fun suggestHomePath(): String {
        // This method should work fast and allow running from the EDT thread.
        // It will be the starting point when someone opens the file explorer dialog to select an SDK of this type
        return "/usr/bin"
    }

    override fun suggestHomePaths(): MutableCollection<String> {
        // Note that suggested paths appear under "Detected SDK's" when adding an SDK
        val results = mutableSetOf<String>()
        val path = "which pdflatex".runCommand()
        if (!path.isNullOrEmpty()) {
            results.add(path.substringBefore("/pdflatex"))
        }
        results.add(suggestHomePath())
        return results
    }

    override fun isValidSdkHome(path: String?): Boolean {
        if (path == null) return false

        // We expect the location of the executables, wherever that is.
        // This is different from a TexliveSdk installation, where we have the parent directory of the TeX Live installation and find everything there.

        // If this is a valid LaTeX installation, pdflatex should be present
        return "$path/pdflatex --version".runCommand()?.contains("pdfTeX") == true
    }

    override fun getVersionString(sdkHome: String?): String {
        // Assume pdflatex --version contains output of the form
        // pdfTeX 3.14159265-2.6-1.40.21 (TeX Live 2020/mydistro)
        return """TeX Live (\d\d\d\d\/.+)""".toRegex().find(LatexSdkUtil.pdflatexVersionText)?.value ?: "Unknown version"
    }

    override fun getDefaultDocumentationUrl(sdk: Sdk): String? {
        return "$texmfDistPath/doc"
    }

    /**
     * Get the executable name of a certain LaTeX executable (e.g. pdflatex/lualatex)
     * and prefix it with the full path to the executable using the homePath of the specified LaTeX SDK.
     *
     * @param executable Name of a program, e.g. pdflatex
     */
    override fun getExecutableName(executable: String, project: Project): String {
        // Even though pdflatex is in path, it may be not the pdflatex we want, so we prefix the path to be sure.
        // Get base path of LaTeX distribution
        val basePath = "/usr/bin"
        return "$basePath/$executable"
    }
}