package br.pucpr.estoque.users

import br.pucpr.estoque.users.requests.LoginRequest
import br.pucpr.estoque.users.responses.UserResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@Validated
class UserController(
    private val service: UserService
) {
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ) = service.login(request.email!!, request.password!!)
        .let { UserResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping
    fun list(): ResponseEntity<List<UserResponse>> =
        service.findAll()
            .map { UserResponse(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(
        @Min(1) @PathVariable id: Long
    ) = service.findByIdOrNull(id)
        ?.let { UserResponse(it) }
        ?.let { ResponseEntity.ok(it) }
        ?: ResponseEntity.notFound().build()
}