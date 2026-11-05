package br.pucpr.estoque.fornecedores

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.util.Objects

@Entity
class Fornecedor(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var nome: String,

    @Column(nullable = false, unique = true)
    var cnpj: String,

    @Column(nullable = false)
    var contato: String,
) {
    final override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (this::class !== other::class) return false
        val o = other as Fornecedor
        return o.id == id
    }

    final override fun hashCode(): Int = Objects.hash(id)
}