package domain.common

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations.openMocks
import org.mockito.kotlin.whenever
import should

class DependencyResolverImplTest {

    @Mock
    private lateinit var fileContentProviderMock: FileContentProvider

    @BeforeEach
    fun before() {
        openMocks(this)
    }

    private fun createDependencyResolver() = DependencyResolverImpl(fileContentProviderMock)

    @Test
    fun `should return empty dependency list when there is no file content`() {
        //given
        whenever(fileContentProviderMock.getFileContentForDependencyOrNull("Test")).thenReturn(null)

        //when
        val dependencies = createDependencyResolver().getDependencyNames("Test")

        //then
        dependencies.should.beEqualTo(emptyList())
        fileContentProviderMock.should.receive { this.getFileContentForDependencyOrNull("Test") }
    }

    @Test
    fun `should parse single dependency`() {
        //given
        val fileContent = """
            import org.junit.jupiter.api.BeforeAll
            import org.junit.jupiter.api.Test
            import org.mockito.Mock
            import org.mockito.MockitoAnnotations.openMocks
            
            class Test (private val testDependency: TestDependency) {
            
            }
        """.trimIndent()

        whenever(fileContentProviderMock.getFileContentForDependencyOrNull("Test")).thenReturn(fileContent)

        //when
        val dependencies = createDependencyResolver().getDependencyNames("Test")

        //then
        dependencies.should.beEqualTo(listOf("TestDependency"))
        fileContentProviderMock.should.receive { this.getFileContentForDependencyOrNull("Test") }
    }

    @Test
    fun `should parse many dependencies`() {
        //given
        val fileContent = """
            import org.junit.jupiter.api.BeforeAll
            import org.junit.jupiter.api.Test
            import org.mockito.Mock
            import org.mockito.MockitoAnnotations.openMocks
            
            class Test (
                private val testDependency0: TestDependency0,
                private val testDependency1: TestDependency1,
                private val testDependency2: TestDependency2,
                private val testDependency3: TestDependency3,
                private val testDependency4: TestDependency4,
            ) {
            
            }
        """.trimIndent()

        whenever(fileContentProviderMock.getFileContentForDependencyOrNull("Test")).thenReturn(fileContent)

        //when
        val dependencies = createDependencyResolver().getDependencyNames("Test")

        //then
        dependencies.should.beEqualTo(
            listOf(
                "TestDependency0",
                "TestDependency1",
                "TestDependency2",
                "TestDependency3",
                "TestDependency4",
            )
        )
        fileContentProviderMock.should.receive { this.getFileContentForDependencyOrNull("Test") }
    }

    @Test
    fun `should parse many dependencies with some class content`() {
        //given
        val fileContent = """
            import org.junit.jupiter.api.BeforeAll
            import org.junit.jupiter.api.Test
            import org.mockito.Mock
            import org.mockito.MockitoAnnotations.openMocks
            
            class Test (
                private val testDependency0: TestDependency0,
                private val testDependency1: TestDependency1,
                private val testDependency2: TestDependency2,
                private val testDependency3: TestDependency3,
                private val testDependency4: TestDependency4,
            ) {
                private val testVal: TestVal = TestVal()
                
                init {
                    for(i in 1..10){
                        println(i + "xd")
                    }
                }
            }
        """.trimIndent()

        whenever(fileContentProviderMock.getFileContentForDependencyOrNull("Test")).thenReturn(fileContent)

        //when
        val dependencies = createDependencyResolver().getDependencyNames("Test")

        //then
        dependencies.should.beEqualTo(
            listOf(
                "TestDependency0",
                "TestDependency1",
                "TestDependency2",
                "TestDependency3",
                "TestDependency4",
            )
        )
        fileContentProviderMock.should.receive { this.getFileContentForDependencyOrNull("Test") }
    }

}
