package com.example.pokedex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PokemonUiState(
    val pokemon: List<PokemonItem> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
    val selected: PokemonDetail? = null,
    val detailLoading: Boolean = false,
    val detailError: String? = null,
    val typeIds: Set<Int>? = null,
    val filterLoading: Boolean = false
)

class PokemonViewModel : ViewModel() {
    private val api = PokemonApi.create()
    private val _uiState = MutableStateFlow(PokemonUiState())
    val uiState = _uiState.asStateFlow()
    private val typeCache = mutableMapOf<String, Set<Int>>()
    private var filterRequest = 0

    init { load() }
    fun load() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        try {
            val results = api.getPokemon().results
            _uiState.value = _uiState.value.copy(pokemon = results, loading = false)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(loading = false, error = "No se pudo cargar el catálogo. Revisa tu conexión.")
        }
    }
    fun filterType(type: String) {
        val request = ++filterRequest
        if (type == "all") {
            _uiState.value = _uiState.value.copy(typeIds = null, filterLoading = false)
            return
        }
        typeCache[type]?.let {
            _uiState.value = _uiState.value.copy(typeIds = it, filterLoading = false)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(filterLoading = true)
            try {
                val ids = api.getByType(type).pokemon.map { it.pokemon.id }.toSet()
                typeCache[type] = ids
                if (request == filterRequest) _uiState.value = _uiState.value.copy(typeIds = ids, filterLoading = false)
            } catch (e: Exception) {
                if (request == filterRequest) _uiState.value = _uiState.value.copy(filterLoading = false, typeIds = emptySet(), error = "No se pudo cargar el filtro. Intenta nuevamente.")
            }
        }
    }
    fun openDetail(id: Int) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(selected = null, detailLoading = true, detailError = null)
        try {
            _uiState.value = _uiState.value.copy(selected = api.getDetail(id), detailLoading = false)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(detailLoading = false, detailError = "No se pudieron cargar los detalles.")
        }
    }
    fun closeDetail() {
        _uiState.value = _uiState.value.copy(selected = null, detailLoading = false, detailError = null)
    }
}
