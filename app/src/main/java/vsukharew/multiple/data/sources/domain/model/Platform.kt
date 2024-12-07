package vsukharew.multiple.data.sources.domain.model

enum class Platform(val value: String) {
    ANDROID("Android"),
    IOS("iOS"),
    WINDOWS("Windows"),
    LINUX("Linux"),
    MAC_OS_X("Mac OS X"),
    UNKNOWN("unknown");

    companion object {
        fun fromString(value: String) =
            entries.find { it.value.equals(value, ignoreCase = true) } ?: UNKNOWN
    }
}