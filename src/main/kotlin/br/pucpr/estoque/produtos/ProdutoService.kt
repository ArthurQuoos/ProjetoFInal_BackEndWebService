package br.pucpr.estoque.produtos

import br.pucpr.estoque.categorias.CategoriaRepository
import br.pucpr.estoque.fornecedores.FornecedorRepository
import br.pucpr.estoque.movimentacoes.MovimentacaoRepository
import br.pucpr.estoque.produtos.requests.PatchProdutoRequest
import br.pucpr.estoque.produtos.requests.ProdutoRequest
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ProdutoService(
    val repository: ProdutoRepository,
    val categoriaRepository: CategoriaRepository,
    val fornecedorRepository: FornecedorRepository,
    val movimentacaoRepository: MovimentacaoRepository
) {
    fun insert(request: ProdutoRequest): Produto {
        if (repository.findByCodigo(request.codigo!!) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Produto com Código ${request.codigo} já existe")
        }
        val categoria = categoriaRepository.findByIdOrNull(request.idCategoria!!)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria ${request.idCategoria} não encontrada")

        return repository
            .save(request.toProduto(categoria))
            .also { log.info("Produto {} criado", it.id) }
    }

    fun filtrar(
        categoria: String?,
        nome: String?,
        quantidadeMaxima: Int?,
        ordenarPor: String,
        dir: SortDir
    ): List<Produto> {
        if (ordenarPor !in CAMPOS_ORDENAVEIS) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Campo de ordenação inválido. Use: ${CAMPOS_ORDENAVEIS.joinToString()}"
            )
        }
        val sort = if (dir == SortDir.ASC) Sort.by(ordenarPor) else Sort.by(ordenarPor).descending()
        return repository.filtrar(categoria, nome, quantidadeMaxima, sort)
    }

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    fun update(id: Long, request: PatchProdutoRequest): Produto? {
        val produto = repository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Produto $id não encontrado")

        var alterado = false
        if (request.nome != null && request.nome != produto.nome) {
            produto.nome = request.nome!!
            alterado = true
        }
        if (request.preco != null && request.preco!!.compareTo(produto.preco) != 0) {
            produto.preco = request.preco!!
            alterado = true
        }
        if (!alterado) return null

        return repository.save(produto)
            .also { log.info("Produto {} atualizado", it.id) }
    }

    fun deleteById(id: Long) {
        if (!repository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Produto $id não encontrado")
        }
        if (movimentacaoRepository.existsByProdutoId(id)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Produto $id possui movimentações e não pode ser removido")
        }
        repository.deleteById(id)
        log.warn("Produto {} removido", id)
    }

    fun addFornecedor(idProduto: Long, idFornecedor: Long): Boolean {
        val produto = repository.findByIdOrNull(idProduto)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Produto $idProduto não encontrado")
        if (produto.fornecedores.any { it.id == idFornecedor }) return false

        val fornecedor = fornecedorRepository.findByIdOrNull(idFornecedor)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor $idFornecedor não encontrado")

        produto.fornecedores.add(fornecedor)
        repository.save(produto)
        log.info("Fornecedor {} associado ao produto {}", idFornecedor, idProduto)
        return true
    }

    fun removeFornecedor(idProduto: Long, idFornecedor: Long): Boolean {
        val produto = repository.findByIdOrNull(idProduto)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Produto $idProduto não encontrado")

        val removido = produto.fornecedores.removeIf { it.id == idFornecedor }
        if (!removido) return false

        repository.save(produto)
        log.warn("Fornecedor {} desassociado do produto {}", idFornecedor, idProduto)
        return true
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProdutoService::class.java)
        private val CAMPOS_ORDENAVEIS = listOf("nome", "preco", "quantidade")
    }
}