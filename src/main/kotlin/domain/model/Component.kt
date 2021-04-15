package domain.model

sealed class Component {
    abstract val name: String
    abstract val dependencies: List<String>

    data class Singleton(
        override val name: String,
        override val dependencies: List<String>
    ) : Component()

    data class ViewModel(
        override val name: String,
        override val dependencies: List<String>
    ) : Component()

    data class Factory(
        override val name: String,
        override val dependencies: List<String>
    ) : Component()

    data class Scope(
        override val name: String,
        override val dependencies: List<String>,
        val typeName: String
    ) : Component()
}
