package br.pucpr.estoque.produtos.requests

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

// No PATCH os campos são opcionais: só altera o que for enviado.
// @Pattern aceita null (campo não enviado), mas recusa texto em branco.
data class PatchProdutoRequest(
    @field:Pattern(regexp = ".*\\S.*", message = "não pode ficar em branco")
    var nome: String? = null,

    @field:Positive
    var preco: BigDecimal? = null
)
