package domain.common

import java.io.File

class KoinModuleFinder(private val projectFilePath: String) {

    private val moduleRegex = Regex("val .*=.*module.*\\{")

    private val projectFile = File(projectFilePath)
    private val projectFileWalk = projectFile.walkTopDown()

    fun findModuleFiles(): Sequence<File> =
        projectFileWalk.filter {
            it.isFile && it.extension == "kt" && it.readLines().joinToString(separator = "").contains(moduleRegex)
        }
}
