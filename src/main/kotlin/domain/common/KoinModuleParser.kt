package domain.common

import domain.model.Dependency
import domain.model.KoinModule

class KoinModuleParser(private val dependencyResolver: DependencyResolver) {
    private val moduleStartRegex = Regex("val.*=\\s*module\\s*\\{")
    private val dependencyRegex = Regex("(single|viewModel|factory)")
    private val parametersRegex = Regex("parameters.*->")
    private val dependenciesGroupsRegex =
        Regex("(single|viewModel|factory|scoped)((?!(single|viewModel|factory|scoped)).)*\\{((?!(single|viewModel|factory|scoped)).)*}")

    fun parse(content: String): KoinModule {
        val match = moduleStartRegex.find(content)!!

        val moduleName = match.value.substring(0, match.value.indexOf("=")).split(" ")[1]

        val moduleContent = content.substring(match.range.last)

        val dependencyMatch = dependencyRegex.find(moduleContent)!!
        val dependencyContent = moduleContent.substring(dependencyMatch.range.first)
        val dependencies = parseDependencies(dependencyContent)

        return KoinModule(
            name = moduleName,
            dependencies = dependencies
        )
    }

    private fun parseDependencies(dependenciesString: String): List<Dependency> {
        val dependenciesStrings = dependenciesGroupsRegex.findAll(dependenciesString.replace("\n", "")).map { it.value }

        return dependenciesStrings.map { dependency ->
            val (type, content) = dependency.split("{")
            val actualContent = content.replace(parametersRegex, "")

            val actualType = type.trim().split("<")[0] //remove type if one was specified i.e single<TestDependency>

            val (name, dependencyString) = actualContent.substring(0, actualContent.indexOf("}")).trim().split("(")
            val dependencies: List<String> = if (dependencyString.trim() == ")") {
                emptyList()
            } else {
                dependencyResolver.getDependencyNames(name)
            }

            when (actualType) {
                "single" -> Dependency.Singleton(name, dependencies)
                "viewModel" -> Dependency.ViewModel(name, dependencies)
                "factory" -> Dependency.Factory(name, dependencies)
                else -> error("unknown dependency type $type")
                //todo scopes
            }
        }.toList()
    }
}
