package domain.common

interface DependencyResolver {
    fun getDependencyNames(name: String) : List<String>
}
