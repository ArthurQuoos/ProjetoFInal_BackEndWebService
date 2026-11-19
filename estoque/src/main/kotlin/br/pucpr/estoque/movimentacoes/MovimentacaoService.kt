package br.pucpr.estoque.movimentacoes

import br.pucpr.estoque.movimentacoes.requests.MovimentacaoRequest
import br.pucpr.estoque.produtos.ProdutoRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class MovimentacaoService(
    val repository: MovimentacaoRepository,
    val produtoRepository: ProdutoRepository
) {
    @Transactional
    fun registrar(idProduto: Long, request: MovimentacaoRequest): Movimentacao {
        val produto = produtoRepository.findByIdOrNull(idProduto)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Produto $idProduto não encontrado")

        when (request.tipo!!) {
            TipoMovimentacao.ENTRADA -> produto.quantidade += request.quantidade!!
            TipoMovimentacao.SAIDA -> {
                if (request.quantidade!! > produto.quantidade) {
                    log.warn("Saída recusada no produto {}: estoque insuficiente", idProduto)
                    throw EstoqueInsuficienteException(produto.quantidade, request.quantidade!!)
                }
                produto.quantidade -= request.quantidade!!
            }
        }
        produtoRepository.save(produto)

        return repository
            .save(request.toMovimentacao(produto))
            .also { log.info("Movimentação {} ({}) registrada no produto {}", it.id, it.tipo, idProduto) }
    }

    fun findByProduto(idProduto: Long): List<Movimentacao> {
        if (!produtoRepository.existsById(idProduto)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Produto $idProduto não encontrado")
        }
        return repository.findByProdutoIdOrderByDataHoraDesc(idProduto)
    }

    companion object {
        private val log = LoggerFactory.getLogger(MovimentacaoService::class.java)
    }
}