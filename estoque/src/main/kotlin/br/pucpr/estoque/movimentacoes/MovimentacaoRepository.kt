package br.pucpr.estoque.movimentacoes

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MovimentacaoRepository : JpaRepository<Movimentacao, Long> {
    fun findByProdutoIdOrderByDataHoraDesc(idProduto: Long): List<Movimentacao>
    fun existsByProdutoId(idProduto: Long): Boolean
}