package com.example.pokedex.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pokedex.data.PokemonDetail
import com.example.pokedex.ui.viewmodel.PokemonViewModel

private val red = Color(0xFFDC3545)
private val types = listOf("all" to "Todos", "normal" to "Normal", "fire" to "Fuego", "water" to "Agua", "grass" to "Planta", "electric" to "Eléctrico", "ice" to "Hielo", "fighting" to "Lucha", "poison" to "Veneno", "ground" to "Tierra", "flying" to "Volador", "psychic" to "Psíquico", "bug" to "Bicho", "rock" to "Roca", "ghost" to "Fantasma", "dragon" to "Dragón", "dark" to "Siniestro", "steel" to "Acero", "fairy" to "Hada")
private fun translated(name: String) = types.firstOrNull { it.first == name }?.second ?: name.replaceFirstChar { it.uppercase() }

@Composable
fun PokemonScreen(viewModel: PokemonViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("all") }
    var selectedId by remember { mutableStateOf<Int?>(null) }
    BackHandler(selectedId != null) { selectedId = null; viewModel.closeDetail() }
    if (selectedId != null) {
        PokemonDetails(selectedId!!, state.selected, state.detailLoading, state.detailError,
            onBack = { selectedId = null; viewModel.closeDetail() },
            onRetry = { viewModel.openDetail(selectedId!!) })
        return
    }
    Column(Modifier.fillMaxSize().background(Color(0xFFF4F6FA))) {
        Text("PokéDex", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = red,
            modifier = Modifier.padding(start = 20.dp, top = 24.dp))
        Text("Explora el mundo Pokémon", color = Color.DarkGray,
            modifier = Modifier.padding(start = 20.dp, bottom = 12.dp))
        OutlinedTextField(value = query, onValueChange = { query = it },
            label = { Text("Buscar por nombre o número") }, singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            trailingIcon = { if (query.isNotEmpty()) TextButton(onClick = { query = "" }) { Text("Borrar") } })
        Spacer(Modifier.height(10.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(types) { option ->
                FilterChip(selected = type == option.first, onClick = {
                    type = option.first; viewModel.filterType(type)
                }, label = { Text(option.second) })
            }
        }
        if (state.filterLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = red) }
            state.error != null -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(state.error!!, textAlign = TextAlign.Center)
                Button(onClick = { viewModel.load(); viewModel.filterType(type) }) { Text("Reintentar") }
            }
            else -> {
                val filtered = state.pokemon.filter { pokemon ->
                    (pokemon.name.contains(query.trim(), ignoreCase = true) || pokemon.id.toString().contains(query.trim().removePrefix("#"))) &&
                    (type == "all" || (state.typeIds?.contains(pokemon.id) == true))
                }
                Text("${filtered.size} Pokémon", color = Color.Gray, modifier = Modifier.padding(start = 20.dp, top = 6.dp, bottom = 4.dp))
                if (filtered.isEmpty() && !state.filterLoading) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No se encontraron Pokémon") }
                else LazyVerticalGrid(columns = GridCells.Adaptive(145.dp), contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    gridItems(filtered, key = { it.id }) { pokemon ->
                        Card(modifier = Modifier.fillMaxWidth().clickable {
                            selectedId = pokemon.id; viewModel.openDetail(pokemon.id)
                        }, shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) {
                            Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                AsyncImage(model = pokemon.imageUrl, contentDescription = pokemon.name,
                                    modifier = Modifier.fillMaxWidth().height(130.dp))
                                Text("#%03d".format(pokemon.id), color = Color.Gray, fontSize = 12.sp)
                                Text(pokemon.name.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonDetails(id: Int, detail: PokemonDetail?, loading: Boolean, error: String?, onBack: () -> Unit, onRetry: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color(0xFFF4F6FA)).padding(20.dp)) {
        TextButton(onClick = onBack) { Text("← Volver a la PokéDex", color = red) }
        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = red) }
            error != null -> Column { Text(error); Button(onClick = onRetry) { Text("Reintentar") } }
            detail != null -> {
                Text(detail.name.replaceFirstChar { it.uppercase() }, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("#%03d".format(detail.id), color = Color.Gray)
                AsyncImage(model = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png",
                    contentDescription = detail.name, modifier = Modifier.fillMaxWidth().height(220.dp))
                Text("Tipos: " + detail.types.sortedBy { it.slot }.joinToString(", ") { translated(it.type.name) }, fontSize = 18.sp)
                Spacer(Modifier.height(10.dp))
                Text("Altura: %.1f m".format(detail.height / 10.0))
                Text("Peso: %.1f kg".format(detail.weight / 10.0))
                Spacer(Modifier.height(10.dp))
                Text("Habilidades", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(detail.abilities.joinToString(", ") { it.ability.name.replace('-', ' ').replaceFirstChar { c -> c.uppercase() } })
                Spacer(Modifier.height(10.dp))
                Text("Estadísticas", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                detail.stats.forEach { stat ->
                    Text("${stat.stat.name.replace('-', ' ')}: ${stat.base_stat}")
                    LinearProgressIndicator(progress = { (stat.base_stat / 255f).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp))
                }
            }
        }
    }
}
