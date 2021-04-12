package domain.common

import domain.model.Dependency
import domain.model.KoinModule
import org.junit.jupiter.api.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations.openMocks
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import should

class KoinModuleParserTest {

    @Mock
    private lateinit var dependencyResolverMock: DependencyResolver

    @BeforeEach
    fun before() {
        openMocks(this)
    }

    @AfterEach
    fun after() {

    }

    private fun createKoinModuleParser(): KoinModuleParser = KoinModuleParser(dependencyResolverMock)

    @Test
    fun `should parse module with singleton which has no dependencies`() {
        //given
        val moduleContent = """
            import org.koin.androidx.viewmodel.dsl.viewModel
            import org.koin.dsl.module
            val testModule = module {
                single { 
                    TestDependency()
                }
            }
        """.trimIndent()

        //when
        val parsedModule = createKoinModuleParser().parse(moduleContent)

        //then
        parsedModule.should.beEqualTo(
            KoinModule(
                name = "testModule",
                dependencies = listOf(
                    Dependency.Singleton(
                        name = "TestDependency",
                        dependencies = emptyList()
                    )
                )
            )
        )
        dependencyResolverMock.should.neverReceive { getDependencyNames(any()) }
    }

    @Test
    fun `should parse module with singleton which has dependencies`() {
        //given
        val moduleContent = """
            import org.koin.androidx.viewmodel.dsl.viewModel
            import org.koin.dsl.module
            val testModule = module {
                single { 
                    TestDependency(get())
                }
            }
        """.trimIndent()

        whenever(dependencyResolverMock.getDependencyNames("TestDependency")).thenReturn(listOf("deps"))

        //when
        val parsedModule = createKoinModuleParser().parse(moduleContent)

        //then
        parsedModule.should.beEqualTo(
            KoinModule(
                name = "testModule",
                dependencies = listOf(
                    Dependency.Singleton(
                        name = "TestDependency",
                        dependencies = listOf("deps")
                    )
                )
            )
        )

        dependencyResolverMock.should.receive { getDependencyNames("TestDependency") }
    }

    @Test
    fun `should parse typed singleton which has dependencies`() {
        //given
        val moduleContent = """
            import org.koin.androidx.viewmodel.dsl.viewModel
            import org.koin.dsl.module
            val testModule = module {
                single<TestDependency> { 
                    TestDependency(get())
                }
            }
        """.trimIndent()

        whenever(dependencyResolverMock.getDependencyNames("TestDependency")).thenReturn(listOf("deps"))

        //when
        val parsedModule = createKoinModuleParser().parse(moduleContent)

        //then
        parsedModule.should.beEqualTo(
            KoinModule(
                name = "testModule",
                dependencies = listOf(
                    Dependency.Singleton(
                        name = "TestDependency",
                        dependencies = listOf("deps")
                    )
                )
            )
        )

        dependencyResolverMock.should.receive { getDependencyNames("TestDependency") }
    }

    @Test
    fun `should parse module with viewModel which has dependencies`() {
        //given
        val moduleContent = """
            import org.koin.androidx.viewmodel.dsl.viewModel
            import org.koin.dsl.module
            val testModule = module {
                viewModel { 
                    TestViewModel(get())
                }
            }
        """.trimIndent()

        whenever(dependencyResolverMock.getDependencyNames("TestViewModel")).thenReturn(listOf("deps"))

        //when
        val parsedModule = createKoinModuleParser().parse(moduleContent)

        //then
        parsedModule.should.beEqualTo(
            KoinModule(
                name = "testModule",
                dependencies = listOf(
                    Dependency.ViewModel(
                        name = "TestViewModel",
                        dependencies = listOf("deps")
                    )
                )
            )
        )

        dependencyResolverMock.should.receive { getDependencyNames("TestViewModel") }
    }

    @Test
    fun `should parse module with viewModel which has dependencies and parameters`() {
        //given
        val moduleContent = """
            import org.koin.androidx.viewmodel.dsl.viewModel
            import org.koin.dsl.module
            val testModule = module {
                viewModel { parameters ->
                    TestViewModel(
                        param1 = get()
                        param2 = parameters[0]
                    )
                }
            }
        """.trimIndent()

        whenever(dependencyResolverMock.getDependencyNames("TestViewModel")).thenReturn(listOf("deps"))

        //when
        val parsedModule = createKoinModuleParser().parse(moduleContent)

        //then
        parsedModule.should.beEqualTo(
            KoinModule(
                name = "testModule",
                dependencies = listOf(
                    Dependency.ViewModel(
                        name = "TestViewModel",
                        dependencies = listOf("deps")
                    )
                )
            )
        )

        dependencyResolverMock.should.receive { getDependencyNames("TestViewModel") }
    }

    @Test
    fun `should parse module with simple factory which has dependencies`() {
        //given
        val moduleContent = """
            import org.koin.androidx.viewmodel.dsl.viewModel
            import org.koin.dsl.module
            val testModule = module {
                factory { 
                    TestDependency(get())
                }
            }
        """.trimIndent()

        whenever(dependencyResolverMock.getDependencyNames("TestDependency")).thenReturn(listOf("deps"))

        //when
        val parsedModule = createKoinModuleParser().parse(moduleContent)

        //then
        parsedModule.should.beEqualTo(
            KoinModule(
                name = "testModule",
                dependencies = listOf(
                    Dependency.Factory(
                        name = "TestDependency",
                        dependencies = listOf("deps")
                    )
                )
            )
        )

        dependencyResolverMock.should.receive { getDependencyNames("TestDependency") }
    }

    @Test
    fun `should parse module with viewModel and factory`() {
        //given
        val moduleContent = """
            import org.koin.androidx.viewmodel.dsl.viewModel
            import org.koin.dsl.module
            val testModule = module {
                factory { 
                    TestDependency(get())
                }
                viewModel {
                    TestViewModel(get())
                }
            }
        """.trimIndent()


        whenever(dependencyResolverMock.getDependencyNames("TestDependency")).thenReturn(listOf("deps"))
        whenever(dependencyResolverMock.getDependencyNames("TestViewModel")).thenReturn(listOf("viewModelDeps"))

        //when
        val parsedModule = createKoinModuleParser().parse(moduleContent)

        //then
        parsedModule.should.beEqualTo(
            KoinModule(
                name = "testModule",
                dependencies = listOf(
                    Dependency.Factory(
                        name = "TestDependency",
                        dependencies = listOf("deps")
                    ),
                    Dependency.ViewModel(
                        name = "TestViewModel",
                        dependencies = listOf("viewModelDeps")
                    )
                )
            )
        )

        dependencyResolverMock.should.receive {
            getDependencyNames("TestDependency")
            getDependencyNames("TestViewModel")
        }
    }
}
