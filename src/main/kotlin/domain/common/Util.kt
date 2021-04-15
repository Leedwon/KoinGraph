package domain.common

//todo test

fun String.remove(vararg delimiters: Char): String {
    var result = this
    for (delimiter in delimiters) {
        result = result.replace(delimiter, '\u0000')
    }
    return result
}

fun String.remove(vararg delimiters: String): String {
    var result = this
    for (delimiter in delimiters) {
        result = result.replace(delimiter, "")
    }
    return result
}
