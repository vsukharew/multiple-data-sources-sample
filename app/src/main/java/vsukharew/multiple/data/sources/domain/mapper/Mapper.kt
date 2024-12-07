package vsukharew.multiple.data.sources.domain.mapper

fun interface Mapper<T, R> {
    fun map(source: T): R
}