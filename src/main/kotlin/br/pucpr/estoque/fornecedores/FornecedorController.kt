package br.pucpr.estoque.fornecedores

import br.pucpr.estoque.fornecedores.requests.FornecedorRequest
import br.pucpr.estoque.fornecedores.responses.FornecedorResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/fornecedores")
@Validated
class FornecedorController(
    private val service: FornecedorService
) {
    @GetMapping
    fun list(): ResponseEntity<List<FornecedorResponse>> =
        service.findAll()
            .map { FornecedorResponse(it) }
            .let { ResponseEntity.ok(it) }

    @PostMapping
    fun insert(
        @Valid @RequestBody request: FornecedorRequest
    ) = service.insert(request.toFornecedor())
        .let { FornecedorResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping("/{id}")
    fun getById(
        @Min(1) @PathVariable id: Long
    ) = service.findByIdOrNull(id)
        ?.let { FornecedorResponse(it) }
        ?.let { ResponseEntity.ok(it) }
        ?: ResponseEntity.notFound().build()
}