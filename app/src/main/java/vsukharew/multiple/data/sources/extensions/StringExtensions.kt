package vsukharew.multiple.data.sources.extensions

fun String.capitalize(): String {
    return replaceFirstChar { it.titlecaseChar() }
}