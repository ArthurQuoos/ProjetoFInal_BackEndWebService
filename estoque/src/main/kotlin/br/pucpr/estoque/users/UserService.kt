package br.pucpr.estoque.users

import br.pucpr.estoque.security.JWT
import br.pucpr.estoque.users.responses.LoginResponse
import br.pucpr.estoque.users.responses.UserResponse
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    val repository: UserRepository,
    val jwt: JWT
) {
    @Transactional(readOnly = true)
    fun login(email: String, password: String): LoginResponse {
        val user = repository.findByEmail(email)
        if (user == null || user.password != password) {
            log.warn("Tentativa de login inválida para {}", email)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos")
        }
        log.info("Usuário {} autenticado", user.id)
        return LoginResponse(jwt.createToken(user), UserResponse(user))
    }

    fun findAll() = repository.findAll(Sort.by("name"))

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    companion object {
        private val log = LoggerFactory.getLogger(UserService::class.java)
    }
}