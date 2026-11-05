package br.pucpr.estoque.produtos

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProdutoRepository : JpaRepository<Produto, Long> {
    fun findByCodigo(codigo: String): Produto?

    fun existsByCategoriaId(idCategoria: Long): Boolean

    @Query(
        """
            select p from Produto p
            where (:categoria is null or p.categoria.nome = :categoria)
            and (:nome is null or lower(p.nome) like lower(concat('%', :nome, '%')))
            and (:quantidadeMaxima is null or p.quantidade <= :quantidadeMaxima)
        """
    )
    fun filtrar(
        categoria: String?,
        nome: String?,
        quantidadeMaxima: Int?,
        sort: Sort
    ): List<Produto>
}