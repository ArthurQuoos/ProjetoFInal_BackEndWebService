package br.pucpr.estoque.movimentacoes

import br.pucpr.estoque.movimentacoes.requests.MovimentacaoRequest
import br.pucpr.estoque.movimentacoes.responses.MovimentacaoResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/produtos/{idProduto}/movimentacoes")
@Validated
class MovimentacaoController(
    private val service: MovimentacaoService
) {
    @PostMapping
    fun registrar(
        @Min(1) @PathVariable idProduto: Long,
        @Valid @RequestBody request: MovimentacaoRequest
    ) = service.registrar(idProduto, request)
        .let { MovimentacaoResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping
    fun list(
        @Min(1) @PathVariable idProduto: Long
    ): ResponseEntity<List<MovimentacaoResponse>> =
        service.findByProduto(idProduto)
            .map { MovimentacaoResponse(it) }
            .let { ResponseEntity.ok(it) }
}