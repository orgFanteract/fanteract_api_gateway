package fanteract.gateway.exception

import org.springframework.http.HttpStatus

enum class MessageType(
    private val code: String,
    private val message: String,
    private val status: HttpStatus
) {
    INVALID_TOKEN("INVALID_TOKEN", "조건에 맞는 토큰이 존재하지 않습니다", HttpStatus.BAD_REQUEST),
    ;

    fun getCode(): String = this.code
    fun getMessage(): String = this.message
    fun getStatus(): HttpStatus = this.status
}
