package br.pucpr.estoque.fornecedores.responses

import br.pucpr.estoque.fornecedores.Fornecedor

data class FornecedorResponse(
    var id: Long,
    var nome: String,
    var cnpj: String,
    var contato: String
) {
    constructor(fornecedor: Fornecedor) :
            this(
                id = fornecedor.id!!,
                nome = fornecedor.nome,
                cnpj = fornecedor.cnpj,
                contato = fornecedor.contato
            )
}