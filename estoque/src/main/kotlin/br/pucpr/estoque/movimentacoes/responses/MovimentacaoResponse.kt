package br.pucpr.estoque.movimentacoes.responses

import br.pucpr.estoque.movimentacoes.Movimentacao
import br.pucpr.estoque.movimentacoes.TipoMovimentacao
import java.time.LocalDateTime

data class MovimentacaoResponse(
    var id: Long,
    var tipo: TipoMovimentacao,
    var quantidade: Int,
    var dataHora: LocalDateTime,
    var idProduto: Long
) {
    constructor(movimentacao: Movimentacao) :
            this(
                id = movimentacao.id!!,
                tipo = movimentacao.tipo,
                quantidade = movimentacao.quantidade,
                dataHora = movimentacao.dataHora,
                idProduto = movimentacao.produto.id!!
            )
}