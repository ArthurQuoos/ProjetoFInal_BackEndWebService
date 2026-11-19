package br.pucpr.estoque.fornecedores

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FornecedorRepository : JpaRepository<Fornecedor, Long> {
    fun findByCnpj(cnpj: String): Fornecedor?
}