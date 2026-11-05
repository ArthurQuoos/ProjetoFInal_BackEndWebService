package br.pucpr.estoque.categorias

import br.pucpr.estoque.produtos.ProdutoRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CategoriaService(
    val repository: CategoriaRepository,
    val produtoRepository: ProdutoRepository
) {
    fun insert(categoria: Categoria): Categoria {
        if (repository.findByNome(categoria.nome) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Categoria ${categoria.nome} já existe")
        }
        return repository
            .save(categoria)
            .also { log.info("Categoria {} criada", it.id) }
    }

    fun findAll() = repository.findAll(Sort.by("nome"))

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    fun update(id: Long, nome: String, descricao: String): Categoria? {
        val categoria = repository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria $id não encontrada")

        val existente = repository.findByNome(nome)
        if (existente != null && existente.id != id) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Categoria $nome já existe")
        }
        if (categoria.nome == nome && categoria.descricao == descricao) return null

        categoria.nome = nome
        categoria.descricao = descricao
        return repository.save(categoria)
            .also { log.info("Categoria {} atualizada", it.id) }
    }

    fun deleteById(id: Long) {
        if (!repository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria $id não encontrada")
        }
        if (produtoRepository.existsByCategoriaId(id)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Categoria $id possui produtos e não pode ser removida")
        }
        repository.deleteById(id)
        log.warn("Categoria {} removida", id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(CategoriaService::class.java)
    }
}