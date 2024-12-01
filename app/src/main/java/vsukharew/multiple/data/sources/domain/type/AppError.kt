package vsukharew.multiple.data.sources.domain.type

import java.io.IOException

sealed class AppError<out T> {
    data class HttpError<T>(val httpCode: Int, val body: T) : AppError<T>()
    data class NetworkError(val e: IOException) : AppError<Nothing>()
    data class OtherError(val e: Throwable) : AppError<Nothing>()
}
