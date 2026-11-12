package br.pucpr.estoque

import br.pucpr.estoque.categorias.Categoria
import br.pucpr.estoque.categorias.CategoriaRepository
import br.pucpr.estoque.fornecedores.Fornecedor
import br.pucpr.estoque.fornecedores.FornecedorRepository
import br.pucpr.estoque.produtos.Produto
import br.pucpr.estoque.produtos.ProdutoRepository
import br.pucpr.estoque.users.User
import br.pucpr.estoque.users.UserRepository
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class BootStrapper(
    val userRepository: UserRepository,
    val categoriaRepository: CategoriaRepository,
    val fornecedorRepository: FornecedorRepository,
    val produtoRepository: ProdutoRepository,
    val passwordEncoder: PasswordEncoder
) : ApplicationListener<ContextRefreshedEvent> {

    @Transactional
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if (userRepository.findByEmail("admin@estoque.com") == null) {
            userRepository.save(
                User(
                    name = "Administrator",
                    password = passwordEncoder.encode("admin")!!,  // senha salva criptografada
                    email = "admin@estoque.com"
                )
            )
        }

        if (categoriaRepository.count() == 0L) {
            val bebidas = categoriaRepository.save(
                Categoria(nome = "Bebidas", descricao = "Sucos, refrigerantes e águas")
            )
            val limpeza = categoriaRepository.save(
                Categoria(nome = "Limpeza", descricao = "Produtos de limpeza em geral")
            )

            val fornecedor = fornecedorRepository.save(
                Fornecedor(
                    nome = "Distribuidora Central",
                    cnpj = "12345678000190",
                    contato = "contato@distribuidora.com"
                )
            )

            produtoRepository.save(
                Produto(
                    nome = "Suco de laranja 1L", codigo = "BEB-001",
                    preco = BigDecimal("8.50"), quantidade = 20,
                    categoria = bebidas, fornecedores = mutableSetOf(fornecedor)
                )
            )
            produtoRepository.save(
                Produto(
                    nome = "Água mineral 500ml", codigo = "BEB-002",
                    preco = BigDecimal("2.00"), quantidade = 5,
                    categoria = bebidas
                )
            )
            produtoRepository.save(
                Produto(
                    nome = "Detergente 500ml", codigo = "LIM-001",
                    preco = BigDecimal("3.20"), quantidade = 50,
                    categoria = limpeza, fornecedores = mutableSetOf(fornecedor)
                )
            )
        }
    }
}