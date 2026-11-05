package br.pucpr.estoque.produtos

import br.pucpr.estoque.categorias.Categoria
import br.pucpr.estoque.fornecedores.Fornecedor
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import java.math.BigDecimal

@Entity
class Produto(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var nome: String,

    @Column(nullable = false, unique = true)
    var codigo: String,

    @Column(nullable = false)
    var preco: BigDecimal,

    @Column(nullable = false)
    var quantidade: Int = 0,

    @ManyToOne(optional = false)
    @JoinColumn(name = "idCategoria")
    var categoria: Categoria,

    @ManyToMany
    @JoinTable(
        name = "ProdutoFornecedor",
        joinColumns = [JoinColumn(name = "idProduto")],
        inverseJoinColumns = [JoinColumn(name = "idFornecedor")],
    )
    var fornecedores: MutableSet<Fornecedor> = mutableSetOf()
)