package domain.common

import domain.model.Component
import domain.model.KoinModule

class KoinModuleParser(private val dependencyResolver: DependencyResolver) {
    //captures format of: val testModule = module {
    private val moduleStartRegex = Regex("val.*=\\s*module\\s*\\{")

    private val moduleParametersRegex = Regex("parameters.*->")
    private val componentsRegex =
        Regex("(single|viewModel|factory|scoped)((?!(single|viewModel|factory|scoped)).)*\\{((?!(single|viewModel|factory|scoped)).)*}")

    fun parse(content: String): KoinModule {
        val moduleMatch = moduleStartRegex.find(content)!!

        val moduleName = moduleMatch.value.split(" ").filter { it.isNotEmpty() }[1]

        val moduleContent = content.substring(moduleMatch.range.last)

        val components = parseComponents(moduleContent)

        return KoinModule(
            name = moduleName,
            components = components
        )
    }

    private fun parseComponents(moduleContent: String): List<Component> {
        val components = componentsRegex.findAll(moduleContent.remove("\n")).map { it.value }

        return components.map { component ->
            val (componentTypeRaw, componentDefinitionRaw) = component.split("{")
            val componentDefinition = componentDefinitionRaw.replace(moduleParametersRegex, "") //remove koin's "parameters -> ..." if exists
            val componentType = componentTypeRaw.trim().split("<")[0] //remove type if one was specified i.e single<TestDependency>

            val (dependencyName, dependencyString) = componentDefinition.trim().split("(")
            val dependencies: List<String> = if (dependencyString.remove("}").trim() == ")") {
                //it means that dependency has empty constructor
                emptyList()
            } else {
                dependencyResolver.getDependencyNames(dependencyName)
            }

            when (componentType) {
                "single" -> Component.Singleton(dependencyName, dependencies)
                "viewModel" -> Component.ViewModel(dependencyName, dependencies)
                "factory" -> Component.Factory(dependencyName, dependencies)
                else -> error("unknown dependency type $componentType")
                //todo scopes
            }
        }.toList()
    }
}
