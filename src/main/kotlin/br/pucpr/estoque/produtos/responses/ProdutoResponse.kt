package br.pucpr.estoque.produtos.responses

import br.pucpr.estoque.categorias.responses.CategoriaResumoResponse
import br.pucpr.estoque.fornecedores.responses.FornecedorResumoResponse
import br.pucpr.estoque.produtos.Produto
import java.math.BigDecimal

data class ProdutoResponse(
    var id: Long,
    var nome: String,
    var codigo: String,
    var preco: BigDecimal,
    var quantidade: Int,
    var categoria: CategoriaResumoResponse,
    var fornecedores: List<FornecedorResumoResponse>
) {
    constructor(produto: Produto) :
            this(
                id = produto.id!!,
                nome = produto.nome,
                codigo = produto.codigo,
                preco = produto.preco,
                quantidade = produto.quantidade,
                categoria = CategoriaResumoResponse(produto.categoria),
                fornecedores = produto.fornecedores.map { FornecedorResumoResponse(it) }
            )
}