import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

val <T : Any?> T.should: Assertion<T>
    get() = Assertion(this)

class Assertion<T>(private val value: T) {
    fun beEqualTo(other: T) {
        assertEquals(other, value)
    }

    fun receive(lambda: T.() -> Unit) {
        verify(value).lambda()
    }

    fun neverReceive(lambda: T.() -> Unit) {
        verify(value, never()).lambda()
    }
}
