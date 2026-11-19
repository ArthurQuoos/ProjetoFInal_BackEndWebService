package br.pucpr.estoque

import br.pucpr.estoque.roles.Role
import br.pucpr.estoque.roles.RoleRepository
import br.pucpr.estoque.users.User
import br.pucpr.estoque.users.UserRepository
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class BootStrapper(
    val roleRepository: RoleRepository,
    val userRepository: UserRepository
) : ApplicationListener<ContextRefreshedEvent> {

    @Transactional
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val adminRole = roleRepository.findByName("ADMIN")
            ?: roleRepository.save(Role(name = "ADMIN", description = "Administrador do sistema"))
        val userRole = roleRepository.findByName("USER")
            ?: roleRepository.save(Role(name = "USER", description = "Operador de estoque"))

        if (userRepository.findByEmail("admin@estoque.com") == null) {
            val admin = User(name = "Administrator", password = "admin", email = "admin@estoque.com")
            admin.roles.add(adminRole)
            userRepository.save(admin)
        }
        if (userRepository.findByEmail("operador@estoque.com") == null) {
            val operador = User(name = "Operador", password = "1234", email = "operador@estoque.com")
            operador.roles.add(userRole)
            userRepository.save(operador)
        }
    }
}