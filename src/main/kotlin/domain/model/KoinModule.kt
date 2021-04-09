package domain.model

data class KoinModule(
    val name: String,
    val dependencies: List<Dependency>
)
