package br.pucpr.estoque.fornecedores.responses

import br.pucpr.estoque.fornecedores.Fornecedor

// Versão resumida do fornecedor, usada dentro do ProdutoResponse.
// Para ver CNPJ e contato, use GET /fornecedores/{id}.
data class FornecedorResumoResponse(
    val id: Long,
    val nome: String
) {
    constructor(fornecedor: Fornecedor) : this(
        id = fornecedor.id!!,
        nome = fornecedor.nome
    )
}
