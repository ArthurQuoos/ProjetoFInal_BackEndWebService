package br.pucpr.estoque.fornecedores.requests

import br.pucpr.estoque.fornecedores.Fornecedor
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class FornecedorRequest(
    @NotBlank
    var nome: String?,

    @NotBlank
    @Size(min = 14, max = 14, message = "CNPJ deve ter 14 dígitos")
    var cnpj: String?,

    @NotBlank
    var contato: String?
) {
    fun toFornecedor() = Fornecedor(
        id = null,
        nome = nome!!,
        cnpj = cnpj!!,
        contato = contato!!
    )
}