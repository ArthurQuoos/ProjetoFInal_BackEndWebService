package br.pucpr.estoque

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan


@SpringBootApplication
@ConfigurationPropertiesScan
class EstoqueApplication

fun main(args: Array<String>) {
	runApplication<EstoqueApplication>(*args)
}
