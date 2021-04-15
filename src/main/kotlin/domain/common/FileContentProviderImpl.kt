package domain.common

import java.io.File

class FileContentProviderImpl(projectFilePath: String) : FileContentProvider {
    private val projectFile = File(projectFilePath)
    private val fileTreeWalk = projectFile.walkTopDown()

    override fun getFileContentForDependencyOrNull(dependencyName: String): String? {
        val fileFromNames = fileTreeWalk.firstOrNull { file ->
            file.extension == "kt" && file.nameWithoutExtension.trim() == dependencyName.trim()
        }

        val file: File = fileFromNames ?: findFileFromContentsOrNull(dependencyName) ?: return null
        return file.readLines().joinToString(separator = "")
    }

    private fun findFileFromContentsOrNull(dependencyName: String): File? {
        val dependencyClassRegex = Regex("class\\s*$dependencyName(.*)")
        return fileTreeWalk.firstOrNull { file ->
            file.isFile && file.extension == "kt" &&
                    file.readLines().joinToString(separator = "").contains(dependencyClassRegex)
        }
    }

}
