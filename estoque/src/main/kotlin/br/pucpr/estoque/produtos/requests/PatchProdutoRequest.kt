package br.pucpr.estoque.produtos.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class PatchProdutoRequest(
    @field:NotBlank
    var nome: String? = null,

    @field:Positive
    var preco: BigDecimal? = null
)