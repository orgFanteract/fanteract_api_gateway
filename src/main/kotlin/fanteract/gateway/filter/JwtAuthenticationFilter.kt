package fanteract.gateway.filter

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class JwtAuthenticationFilter(
    @Value("\${jwt.secret}") private val jwtSecret: String,
): AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config>(Config::class.java) {
    class Config

    private val secretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray(StandardCharsets.UTF_8))

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val authHeader = exchange.request.headers.getFirst("Authorization")
            println("token : $authHeader")

            // 토큰이 없는 경우
            if (authHeader == null){
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                return@GatewayFilter exchange.response.setComplete()
            }

            // secretKey로 토큰 검증 및 페이로드 가져오기
            val token = authHeader.removePrefix("Bearer ").trim()

            val subject = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload.subject
            println("subject : $subject")

            // payload를 X-User-Id 헤더에 담아서 request 전달
            // 다른 서비스에 요청을 전달할 때 userId 정보를 담아서 보낸다
            val mutatedRequest =
                exchange.request
                    .mutate()
                    .header("X-User-Id", subject)
                    .build()

            val mutatedExchange =
                exchange
                    .mutate()
                    .request(mutatedRequest)
                    .build()

            return@GatewayFilter chain.filter(mutatedExchange)
        }

    }
}