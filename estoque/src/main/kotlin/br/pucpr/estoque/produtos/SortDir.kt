package br.pucpr.estoque.produtos

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidSortDirException : IllegalArgumentException(
    "Direção de ordenação inválida! Use ASC ou DESC"
)

enum class SortDir {
    ASC, DESC;

    companion object {
        fun findOrThrow(sortDir: String): SortDir =
            entries.find { it.name == sortDir.uppercase() }
                ?: throw InvalidSortDirException()
    }
}