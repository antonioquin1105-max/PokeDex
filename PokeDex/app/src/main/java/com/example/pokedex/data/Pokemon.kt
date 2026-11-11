package com.example.pokedex.data

data class PokemonResponse(val results: List<PokemonItem>)

data class PokemonItem(val name: String, val url: String) {
    val id: Int get() = url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0
    val imageUrl: String get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}

data class PokemonDetail(
    val id: Int, val name: String, val height: Int, val weight: Int,
    val types: List<PokemonTypeSlot>, val abilities: List<PokemonAbilitySlot>,
    val stats: List<PokemonStatSlot>
)
data class PokemonTypeSlot(val slot: Int, val type: NamedResource)
data class PokemonAbilitySlot(val ability: NamedResource)
data class PokemonStatSlot(val base_stat: Int, val stat: NamedResource)
data class NamedResource(val name: String, val url: String)
data class PokemonTypeResponse(val pokemon: List<PokemonTypeMember>)
data class PokemonTypeMember(val pokemon: PokemonItem)
