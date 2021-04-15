package domain.common

import domain.model.Component
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
                components = listOf(
                    Component.Singleton(
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
                components = listOf(
                    Component.Singleton(
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
                components = listOf(
                    Component.Singleton(
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
                components = listOf(
                    Component.ViewModel(
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
                        param1 = get(),
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
                components = listOf(
                    Component.ViewModel(
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
                components = listOf(
                    Component.Factory(
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
                components = listOf(
                    Component.Factory(
                        name = "TestDependency",
                        dependencies = listOf("deps")
                    ),
                    Component.ViewModel(
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

    @Test
    fun `should parse module with many dependencies`() {
        //given
        val moduleContent = """
            import org.koin.androidx.viewmodel.dsl.viewModel
            import org.koin.dsl.module
            val                    testModule = module {
                factory { 
                    TestDependency(
                        testDep1 = get(),
                        testDep2 = get(),
                        testDep3 = get()
                    )
                }
            }
        """.trimIndent()


        whenever(dependencyResolverMock.getDependencyNames("TestDependency")).thenReturn(listOf("TestDep1", "TestDep2", "TestDep3"))

        //when
        val parsedModule = createKoinModuleParser().parse(moduleContent)

        //then
        parsedModule.should.beEqualTo(
            KoinModule(
                name = "testModule",
                components = listOf(
                    Component.Factory(
                        name = "TestDependency",
                        dependencies = listOf("TestDep1", "TestDep2", "TestDep3")
                    ),
                )
            )
        )

        dependencyResolverMock.should.receive { getDependencyNames("TestDependency") }
    }

    @Test
    @Disabled("todo figure out how to treat those complicated modules")
    fun `should parse complicated module`() {
        //given
        val moduleContent = """
            import android.content.Context
            import android.content.ContextWrapper
            import androidx.room.Room
            import org.koin.dsl.module

            val persistenceModule = module {
                single {
                    val context: Context = get()

                    Room.databaseBuilder(
                        context,
                        TestDatabase::class.java,
                        "test_db"
                    )
                        .build()
                }

                single { get<TestDatabase>().timerDao() }

                single {
                    val context: Context = get()
                    val name = context.packageName + "_preferences"
                    context.getSharedPreferences(name, ContextWrapper.MODE_PRIVATE)
                }

                single {
                    TestDataStore(preferences = get())
                }
            }
        """.trimIndent()


        whenever(dependencyResolverMock.getDependencyNames("TestDataStore")).thenReturn(listOf("SharedPreferences"))

        //when
        val parsedModule = createKoinModuleParser().parse(moduleContent)

        //then
        parsedModule.should.beEqualTo(
            KoinModule(
                name = "persistenceModule",
                components = listOf(
                    Component.Singleton(
                        name = "TestDependency",
                        dependencies = listOf("TestDep1", "TestDep2", "TestDep3")
                    ),
                )
            )
        )

        dependencyResolverMock.should.receive { getDependencyNames("TestDependency") }
    }
}
