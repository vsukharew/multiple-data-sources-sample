package vsukharew.multiple.data.sources.domain.type

import vsukharew.multiple.data.sources.domain.type.Either.Left
import vsukharew.multiple.data.sources.domain.type.Either.Right

inline fun <L, R> sideEffect(block: EitherScope<L>.() -> R): Either<L, R> {
    val binding = EitherScope<L>()
    return try {
        with(binding) { Right(block()) }
    } catch (e: EitherBindingException) {
        binding.left
    }
}

/**
 * Область действия функций для раскрытия [Either] в сторону [Right] или [Left]
 * @param L - тип, хранящийся внутри [Left]
 */
class EitherScope<L> {
    lateinit var left: Left<L>

    /**
     * Раскрывает [Either] в сторону [Right] и возвращает данные внутри него
     * @throws EitherBindingException если `this` имеет тип [Left], а также сохраняет значение `this` внутри класса
     */
    fun <R> Either<L, R>.right(): R {
        return when (this) {
            is Right -> data
            is Left -> left()
        }
    }

    /**
     * Прекращает вычисление значения внутри [sideEffect] и провоцирует [sideEffect] на возврат [Left]
     */
    fun L.left(): Nothing {
        Left(this).left()
    }

    /**
     * Сохраняет `this` внутри класса и выбрасывает [EitherBindingException], останавливая выполнение [sideEffect]
     */
    private fun Left<L>.left(): Nothing {
        left = this
        throw EitherBindingException()
    }
}

class EitherBindingException : Exception()
