package br.pucpr.estoque.security

import br.pucpr.estoque.users.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

/**
 * Classe responsável por CRIAR e LER os tokens JWT.
 */
@Component
class Jwt {
    private val key = Keys.hmacShaKeyFor(SECRET.toByteArray())

    // Gera o token que será devolvido no login
    fun createToken(user: User): String {
        val agora = Instant.now()
        return Jwts.builder()
            .issuer(ISSUER)                     // quem emitiu o token
            .subject(user.id.toString())        // id do usuário
            .claim("name", user.name)
            .claim("email", user.email)
            .issuedAt(Date.from(agora))
            .expiration(Date.from(agora.plus(EXPIRE_HOURS, ChronoUnit.HOURS)))
            .signWith(key)                      // assinatura com o segredo
            .compact()
    }

    // Lê o header "Authorization: Bearer <token>" e devolve o usuário logado.
    // Se o token não existir ou for inválido, devolve null (usuário não logado).
    fun extract(req: HttpServletRequest): Authentication? {
        val header = req.getHeader(AUTHORIZATION)
        if (header == null || !header.startsWith("Bearer ")) return null

        return try {
            val token = header.removePrefix("Bearer ").trim()
            val claims = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .payload

            val email = claims["email"].toString()
            UsernamePasswordAuthenticationToken.authenticated(email, null, emptyList())
        } catch (e: Exception) {
            log.warn("Token inválido: {}", e.message)
            null
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(Jwt::class.java)

        // Segredo usado para assinar o token (precisa ter pelo menos 32 caracteres)
        private const val SECRET = "estoque-pucpr-segredo-super-secreto-123"
        private const val ISSUER = "EstoqueServer"
        private const val EXPIRE_HOURS = 2L
    }
}
