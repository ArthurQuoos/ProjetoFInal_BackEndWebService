package br.pucpr.estoque.security

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Configuração de segurança:
 *  - GET (consultas) e o login são liberados para todos
 *  - POST, PUT, PATCH e DELETE (ações que alteram dados) exigem token JWT
 */
@Configuration
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@OpenAPIDefinition(security = [SecurityRequirement(name = "bearerAuth")])
class SecurityConfig(
    private val jwtTokenFilter: JwtTokenFilter
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain = http
        .cors { }
        .csrf { it.disable() }                                   // API REST sem sessão não precisa de CSRF
        .headers { it.frameOptions { f -> f.disable() } }        // necessário para o console do H2
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .exceptionHandling { it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) }
        .authorizeHttpRequests { requests ->
            requests
                .requestMatchers(HttpMethod.POST, "/users/login").permitAll()
                .requestMatchers(HttpMethod.GET).permitAll()
                .requestMatchers("/h2-console/**", "/error").permitAll()
                .anyRequest().authenticated()
        }
        .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
        .build()

    // Usado para criptografar as senhas dos usuários
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    // CORS: permite que um front-end em outro endereço chame a API
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            addAllowedOriginPattern("*")
            addAllowedHeader("*")
            addAllowedMethod("*")
        }
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
    }
}
