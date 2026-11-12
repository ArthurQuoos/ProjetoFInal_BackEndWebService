package br.pucpr.estoque.users

import br.pucpr.estoque.security.Jwt
import br.pucpr.estoque.users.responses.LoginResponse
import br.pucpr.estoque.users.responses.UserResponse
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    val repository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val jwt: Jwt
) {
    fun login(email: String, password: String): LoginResponse {
        val user = repository.findByEmail(email)
        // A senha salva está criptografada (BCrypt), por isso usamos o matches()
        if (user == null || !passwordEncoder.matches(password, user.password)) {
            log.warn("Tentativa de login inválida para {}", email)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos")
        }
        log.info("Usuário {} fez login", user.id)
        return LoginResponse(
            token = jwt.createToken(user),
            user = UserResponse(user)
        )
    }

    fun findAll() = repository.findAll(Sort.by("name"))

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    companion object {
        private val log = LoggerFactory.getLogger(UserService::class.java)
    }
}
