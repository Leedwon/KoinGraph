package domain.model

sealed class Dependency {
    abstract val name: String
    abstract val dependencies: List<String>

    data class Singleton(
        override val name: String,
        override val dependencies: List<String>
    ) : Dependency()

    data class ViewModel(
        override val name: String,
        override val dependencies: List<String>
    ) : Dependency()

    data class Factory(
        override val name: String,
        override val dependencies: List<String>
    ) : Dependency()

    data class Scope(
        override val name: String,
        override val dependencies: List<String>,
        val typeName: String
    ) : Dependency()
}
