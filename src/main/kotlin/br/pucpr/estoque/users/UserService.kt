package br.pucpr.estoque.users

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    val repository: UserRepository
) {
    fun login(email: String, password: String): User {
        val user = repository.findByEmail(email)
        if (user == null || user.password != password) {
            log.warn("Tentativa de login inválida para {}", email)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos")
        }
        log.info("Usuário {} fez login", user.id)
        return user
    }

    fun findAll() = repository.findAll(Sort.by("name"))

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    companion object {
        private val log = LoggerFactory.getLogger(UserService::class.java)
    }
}