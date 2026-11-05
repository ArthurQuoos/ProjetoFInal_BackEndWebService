package br.pucpr.estoque.categorias.responses

import br.pucpr.estoque.categorias.Categoria

data class CategoriaResponse(
    var id: Long,
    var nome: String,
    var descricao: String
) {
    constructor(categoria: Categoria) :
            this(
                id = categoria.id!!,
                nome = categoria.nome,
                descricao = categoria.descricao
            )
}