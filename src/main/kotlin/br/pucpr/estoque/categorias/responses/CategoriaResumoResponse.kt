package br.pucpr.estoque.categorias.responses

import br.pucpr.estoque.categorias.Categoria

// Versão resumida da categoria, usada dentro do ProdutoResponse.
// Traz só o necessário para identificar a categoria, sem repetir a descrição em cada produto.
data class CategoriaResumoResponse(
    val id: Long,
    val nome: String
) {
    constructor(categoria: Categoria) : this(
        id = categoria.id!!,
        nome = categoria.nome
    )
}
