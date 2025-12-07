package fanteract.account.exception

import fanteract.gateway.exception.MessageType

class ExceptionType(
    val type: MessageType,
    override val message: String = type.getMessage(),
    override val cause: Throwable? = null
) : RuntimeException(message, cause) {
    companion object {
        fun withType(type: MessageType): ExceptionType = ExceptionType(type)
    }
}
