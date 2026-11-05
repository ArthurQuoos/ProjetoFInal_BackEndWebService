package br.pucpr.estoque.movimentacoes.requests

import br.pucpr.estoque.movimentacoes.Movimentacao
import br.pucpr.estoque.movimentacoes.TipoMovimentacao
import br.pucpr.estoque.produtos.Produto
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class MovimentacaoRequest(
    @NotNull
    var tipo: TipoMovimentacao?,

    @NotNull
    @Min(1)
    var quantidade: Int?
) {
    fun toMovimentacao(produto: Produto) = Movimentacao(
        id = null,
        tipo = tipo!!,
        quantidade = quantidade!!,
        produto = produto
    )
}