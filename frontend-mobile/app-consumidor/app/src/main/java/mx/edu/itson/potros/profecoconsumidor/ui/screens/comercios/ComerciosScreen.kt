package mx.edu.itson.potros.profecoconsumidor.ui.screens.comercios

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ComercioDto
import mx.edu.itson.potros.profecoconsumidor.ui.UiState
import mx.edu.itson.potros.profecoconsumidor.ui.components.Chip
import mx.edu.itson.potros.profecoconsumidor.ui.components.EmptyState
import mx.edu.itson.potros.profecoconsumidor.ui.components.ErrorBox
import mx.edu.itson.potros.profecoconsumidor.ui.components.Loader
import mx.edu.itson.potros.profecoconsumidor.ui.components.RefreshableLista

@Composable
fun ComerciosScreen(
    onAbrirComercio: (Long) -> Unit,
    vm: ComerciosViewModel = viewModel()
) {
    val state by vm.comercios.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    var refrescando by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.cargar() }
    LaunchedEffect(state) { if (state !is UiState.Loading) refrescando = false }

    RefreshableLista(
        refrescando = refrescando,
        onRefresh = {
            refrescando = true
            if (query.isBlank()) vm.cargar() else vm.buscar(query)
        }
    ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Comercios", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Encuentra tiendas y consulta sus reseñas.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    vm.buscar(it)
                },
                singleLine = true,
                placeholder = { Text("Buscar por nombre…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }

        when (val s = state) {
            is UiState.Loading -> if (!refrescando) item { Loader() }
            is UiState.Error -> item { ErrorBox(s.message) }
            is UiState.Success -> {
                if (s.data.isEmpty()) {
                    item { EmptyState("Sin comercios", "Aún no hay registros que coincidan") }
                } else {
                    items(s.data) { c ->
                        ComercioItem(c, onClick = { onAbrirComercio(c.id) })
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun ComercioItem(comercio: ComercioDto, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (comercio.tipoComercio.isNotBlank()) {
                Chip(comercio.tipoComercio.replace("_", " "))
            }
            Text(comercio.nombreComercial, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            val ubic = listOfNotNull(
                comercio.ciudad.takeIf { it.isNotBlank() },
                comercio.estado.takeIf { it.isNotBlank() }
            ).joinToString(", ")
            if (ubic.isNotBlank()) {
                Text(ubic, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (comercio.direccion.isNotBlank()) {
                Text(
                    comercio.direccion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}
