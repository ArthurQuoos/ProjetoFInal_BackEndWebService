package br.pucpr.estoque.fornecedores

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class FornecedorService(
    val repository: FornecedorRepository
) {
    fun insert(fornecedor: Fornecedor): Fornecedor {
        if (repository.findByCnpj(fornecedor.cnpj) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Fornecedor com CNPJ ${fornecedor.cnpj} já existe")
        }
        return repository
            .save(fornecedor)
            .also { log.info("Fornecedor {} criado", it.id) }
    }

    fun findAll() = repository.findAll(Sort.by("nome"))

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    companion object {
        private val log = LoggerFactory.getLogger(FornecedorService::class.java)
    }
}