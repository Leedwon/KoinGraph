package domain.common

class DependencyResolverImpl(
    private val fileContentProvider: FileContentProvider
) : DependencyResolver {

    override fun getDependencyNames(className: String): List<String> {
        val fileContent = fileContentProvider.getFileContentForDependencyOrNull(className) ?: return emptyList()

        //captures format of class Test(val someParams: SomeParam)
        val classConstructorRegex = Regex("class\\s*$className\\s*\\(.*\\)")

        val rawFileContent = fileContent.remove("\n")

        val constructor = classConstructorRegex.find(rawFileContent)!!.value.split("(")[1]
        val constructorEndIndex = constructor.indexOf(")")

        val constructorParamsRaw: String = constructor.substring(0, constructorEndIndex)
        val constructorParams: List<String> = constructorParamsRaw.split(",").filter { it.isNotEmpty() }

        return constructorParams.mapNotNull { dependency ->
            dependency.split(":").getOrNull(1)?.trim()
        }
    }
}
