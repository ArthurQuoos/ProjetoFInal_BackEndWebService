package br.pucpr.estoque.movimentacoes

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
class EstoqueInsuficienteException(disponivel: Int, solicitado: Int) : RuntimeException(
    "Estoque insuficiente: disponível $disponivel, solicitado $solicitado"
)