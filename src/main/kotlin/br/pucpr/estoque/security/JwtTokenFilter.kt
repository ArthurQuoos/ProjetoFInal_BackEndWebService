package br.pucpr.estoque.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean

/**
 * Filtro que roda em TODA requisição.
 * Se tiver um token válido, marca o usuário como logado no SecurityContextHolder.
 */
@Component
class JwtTokenFilter(
    private val jwt: Jwt
) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val auth = jwt.extract(request as HttpServletRequest)
        if (auth != null) {
            SecurityContextHolder.getContext().authentication = auth
        }
        chain.doFilter(request, response)
    }
}
