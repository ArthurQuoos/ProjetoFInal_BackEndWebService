package br.pucpr.estoque.categorias

import br.pucpr.estoque.categorias.requests.CategoriaRequest
import br.pucpr.estoque.categorias.responses.CategoriaResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/categorias")
@Validated
class CategoriaController(
    private val service: CategoriaService
) {
    @GetMapping
    fun list(): ResponseEntity<List<CategoriaResponse>> =
        service.findAll()
            .map { CategoriaResponse(it) }
            .let { ResponseEntity.ok(it) }

    @PostMapping
    fun insert(
        @Valid @RequestBody request: CategoriaRequest
    ) = service.insert(request.toCategoria())
        .let { CategoriaResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping("/{id}")
    fun getById(
        @Min(1) @PathVariable id: Long
    ) = service.findByIdOrNull(id)
        ?.let { CategoriaResponse(it) }
        ?.let { ResponseEntity.ok(it) }
        ?: ResponseEntity.notFound().build()

    @PutMapping("/{id}")
    fun update(
        @Min(1) @PathVariable id: Long,
        @Valid @RequestBody request: CategoriaRequest
    ) = service.update(id, request.nome!!, request.descricao!!)
        ?.let { CategoriaResponse(it) }
        ?.let { ResponseEntity.ok(it) }
        ?: ResponseEntity.noContent().build()

    @DeleteMapping("/{id}")
    fun deleteById(
        @Min(1) @PathVariable id: Long
    ): ResponseEntity<Void> = service.deleteById(id)
        .let { ResponseEntity.ok().build() }
}