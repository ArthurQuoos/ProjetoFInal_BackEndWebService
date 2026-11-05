package br.pucpr.estoque.produtos.requests

import br.pucpr.estoque.categorias.Categoria
import br.pucpr.estoque.produtos.Produto
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class ProdutoRequest(
    @NotBlank
    var nome: String?,

    @NotBlank
    var codigo: String?,

    @NotNull
    @Positive
    var preco: BigDecimal?,

    @NotNull
    @Min(0)
    var quantidade: Int?,

    @NotNull
    @Min(1)
    var idCategoria: Long?
) {
    fun toProduto(categoria: Categoria) = Produto(
        id = null,
        nome = nome!!,
        codigo = codigo!!,
        preco = preco!!,
        quantidade = quantidade!!,
        categoria = categoria,
        fornecedores = mutableSetOf()
    )
}