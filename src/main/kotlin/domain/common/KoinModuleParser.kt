package domain.common

import domain.model.Dependency
import domain.model.KoinModule

class KoinModuleParser(
    private val dependencyResolver: DependencyResolver
) {
    private val moduleStartRegex = Regex("val.*=\\s*module\\s*\\{")
    private val dependencyRegex = Regex("(single|viewModel|factory)")
    private val parametersRegex = Regex("parameters.*->")

    fun parse(content: String): KoinModule {
        val match = moduleStartRegex.find(content)!!

        val moduleName = match.value.substring(0, match.value.indexOf("=")).split(" ")[1]

        val moduleContent = content.substring(match.range.last)

        val dependencyMatch = dependencyRegex.find(moduleContent)!!
        val dependencyContent = moduleContent.substring(dependencyMatch.range.first)
        val dependency = parseDependency(dependencyContent)

        return KoinModule(
            name = moduleName,
            dependencies = listOf(dependency)
        )
    }

    private fun parseDependency(dependency: String): Dependency {
        val (type, content) = dependency.split("{", limit = 2)
        val actualContent = content.replace(parametersRegex, "")

        val (name, dependencyString) = actualContent.substring(0, actualContent.indexOf("}")).trim().split("(")
        val dependencies: List<String> = if (dependencyString.trim() == ")") {
            emptyList()
        } else {
            dependencyResolver.getDependencyNames(name)
        }

        val actualType = type.trim().split("<")[0] //remove type if one was specified i.e single<TestDependency>

        return when (actualType) {
            "single" -> Dependency.Singleton(name, dependencies)
            "viewModel" -> Dependency.ViewModel(name, dependencies)
            "factory" -> Dependency.Factory(name, dependencies)
            else -> error("unknown dependency type $type")
            //todo scopes
        }
    }
}
