package br.pucpr.estoque.users.responses

data class LoginResponse(
    val token: String,
    val user: UserResponse
)