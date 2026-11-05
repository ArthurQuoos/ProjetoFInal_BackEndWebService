package br.pucpr.estoque.produtos

import br.pucpr.estoque.produtos.requests.PatchProdutoRequest
import br.pucpr.estoque.produtos.requests.ProdutoRequest
import br.pucpr.estoque.produtos.responses.ProdutoResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/produtos")
@Validated
class ProdutoController(
    private val service: ProdutoService
) {
    @GetMapping
    fun list(
        @RequestParam categoria: String?,
        @RequestParam nome: String?,
        @RequestParam quantidadeMaxima: Int?,
        @RequestParam ordenarPor: String?,
        @RequestParam sortDir: String?
    ): ResponseEntity<List<ProdutoResponse>> =
        service.filtrar(
            categoria = categoria,
            nome = nome,
            quantidadeMaxima = quantidadeMaxima,
            ordenarPor = ordenarPor ?: "nome",
            dir = SortDir.findOrThrow(sortDir ?: "ASC")
        )
            .map { ProdutoResponse(it) }
            .let { ResponseEntity.ok(it) }

    @PostMapping
    fun insert(
        @Valid @RequestBody request: ProdutoRequest
    ) = service.insert(request)
        .let { ProdutoResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping("/{id}")
    fun getById(
        @Min(1) @PathVariable id: Long
    ) = service.findByIdOrNull(id)
        ?.let { ProdutoResponse(it) }
        ?.let { ResponseEntity.ok(it) }
        ?: ResponseEntity.notFound().build()

    @PatchMapping("/{id}")
    fun update(
        @Min(1) @PathVariable id: Long,
        @Valid @RequestBody request: PatchProdutoRequest
    ) = service.update(id, request)
        ?.let { ProdutoResponse(it) }
        ?.let { ResponseEntity.ok(it) }
        ?: ResponseEntity.noContent().build()

    @DeleteMapping("/{id}")
    fun deleteById(
        @Min(1) @PathVariable id: Long
    ): ResponseEntity<Void> = service.deleteById(id)
        .let { ResponseEntity.ok().build() }

    @PutMapping("/{id}/fornecedores/{idFornecedor}")
    fun addFornecedor(
        @Min(1) @PathVariable id: Long,
        @Min(1) @PathVariable idFornecedor: Long
    ): ResponseEntity<Void> =
        if (service.addFornecedor(id, idFornecedor)) ResponseEntity.ok().build()
        else ResponseEntity.noContent().build()

    @DeleteMapping("/{id}/fornecedores/{idFornecedor}")
    fun removeFornecedor(
        @Min(1) @PathVariable id: Long,
        @Min(1) @PathVariable idFornecedor: Long
    ): ResponseEntity<Void> =
        if (service.removeFornecedor(id, idFornecedor)) ResponseEntity.ok().build()
        else ResponseEntity.noContent().build()
}