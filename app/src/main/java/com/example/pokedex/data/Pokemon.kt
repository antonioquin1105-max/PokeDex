package com.example.pokedex.data

import retrofit2.http.Url

data class PokemonResponse (
    val results: List<PokemonResponse>
)

data class pokemon(
    val name: String,
    val url: String
){
    val id: Int
        get() = url.trimEnd('/')
            .substringAfterLast('/')
            .toIntOrNull() ?:0
    val imageUrl: String
        get() =
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/\$id.png"
}

annotation class Pokemon
