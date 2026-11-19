package br.pucpr.estoque.security

import br.pucpr.estoque.users.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JWT(
    private val props: SecurityProperties
) {
    private val key = Keys.hmacShaKeyFor(props.secret.toByteArray())

    fun createToken(user: User): String {
        val agora = System.currentTimeMillis()
        val admin = user.roles.any { it.name == "ADMIN" }
        val horas = if (admin) props.adminExpireHours else props.expireHours

        return Jwts.builder()
            .issuer(props.issuer)
            .subject(user.email)
            .claim("id", user.id)
            .claim("roles", user.roles.map { it.name })
            .issuedAt(Date(agora))
            .expiration(Date(agora + horas * 60 * 60 * 1000))
            .signWith(key)
            .compact()
    }

    fun extractAuthentication(token: String): Authentication? =
        try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(props.issuer)
                .build()
                .parseSignedClaims(token)
                .payload

            val roles = (claims["roles"] as? List<*>)?.map { it.toString() } ?: emptyList()
            UsernamePasswordAuthenticationToken(
                claims.subject,
                null,
                roles.map { SimpleGrantedAuthority("ROLE_$it") }
            )
        } catch (e: Exception) {
            log.warn("Token inválido: {}", e.message)
            null
        }

    companion object {
        private val log = LoggerFactory.getLogger(JWT::class.java)
    }
}