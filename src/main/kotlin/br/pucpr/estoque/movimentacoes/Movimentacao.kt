package br.pucpr.estoque.movimentacoes

import br.pucpr.estoque.produtos.Produto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class Movimentacao(
    @Id @GeneratedValue
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var tipo: TipoMovimentacao,

    @Column(nullable = false)
    var quantidade: Int,

    @Column(nullable = false)
    var dataHora: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(optional = false)
    @JoinColumn(name = "idProduto")
    var produto: Produto,
)