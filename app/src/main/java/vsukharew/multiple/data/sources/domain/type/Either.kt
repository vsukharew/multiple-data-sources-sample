package vsukharew.multiple.data.sources.domain.type

sealed class Either<out L, out R> {
    data class Left<T>(val data: T) : Either<T, Nothing>()
    data class Right<T>(val data: T) : Either<Nothing, T>()
}
