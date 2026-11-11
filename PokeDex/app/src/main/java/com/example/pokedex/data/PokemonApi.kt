package com.example.pokedex.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApi {
    @GET("pokemon") suspend fun getPokemon(@Query("limit") limit: Int = 151): PokemonResponse
    @GET("pokemon/{id}") suspend fun getDetail(@Path("id") id: Int): PokemonDetail
    @GET("type/{type}") suspend fun getByType(@Path("type") type: String): PokemonTypeResponse
    companion object {
        fun create(): PokemonApi = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(PokemonApi::class.java)
    }
}
