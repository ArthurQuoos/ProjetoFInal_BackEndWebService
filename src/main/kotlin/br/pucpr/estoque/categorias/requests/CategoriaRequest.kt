package br.pucpr.estoque.categorias.requests

import br.pucpr.estoque.categorias.Categoria
import jakarta.validation.constraints.NotBlank

data class CategoriaRequest(
    @NotBlank
    var nome: String?,

    @NotBlank
    var descricao: String?
) {
    fun toCategoria() = Categoria(
        id = null,
        nome = nome!!,
        descricao = descricao!!
    )
}