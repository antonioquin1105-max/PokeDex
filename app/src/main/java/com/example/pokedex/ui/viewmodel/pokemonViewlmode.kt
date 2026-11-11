package com.example.pokedex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.PokemonApi
import com.example.pokedex.data.PokemonResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PokemonUiState(
    val pokemon: List<PokemonResponse> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
)

class PokemonViewModel : ViewModel() {

    private val api = PokemonApi.create()

    private val _uiState =
        MutableStateFlow(PokemonUiState())

    val uiState: StateFlow<PokemonUiState> =
        _uiState.asStateFlow()

    init {
        cargarPokemon()
    }

    private fun cargarPokemon() {
        viewModelScope.launch {
            _uiState.value = PokemonUiState(loading = true)

            try {
                val response = api.getPokemon()

                _uiState.value = PokemonUiState(
                    pokemon = response.results,
                    loading = false
                )
            } catch (e: Exception) {
                _uiState.value = PokemonUiState(
                    loading = false,
                    error = "No se pudo cargar el catálogo. Revisa tu conexión."
                )
            }
        }
    }
}