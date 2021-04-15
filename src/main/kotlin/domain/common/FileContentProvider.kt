package domain.common

interface FileContentProvider {
    fun getFileContentForDependencyOrNull(dependencyName: String): String?
}
