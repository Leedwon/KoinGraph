package domain.common

interface DependencyResolver {
    fun getDependencyNames(dependencyName: String) : List<String>
}
