package com.example.pokedex.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PokemonApi {

    @GET("pokemon")
    suspend fun getPokemon(
        @Query("limit") limit: Int = 151
    ): PokemonResponse

    companion object {
        fun create(): PokemonApi {
            return Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()
                .create(PokemonApi::class.java)
        }
    }
}