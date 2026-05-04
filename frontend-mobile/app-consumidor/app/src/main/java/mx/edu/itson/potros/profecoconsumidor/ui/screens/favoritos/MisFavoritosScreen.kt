package mx.edu.itson.potros.profecoconsumidor.ui.screens.favoritos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

@Composable
fun MisFavoritosScreen(
    onAbrirComercio: (Long) -> Unit,
    vm: MisFavoritosViewModel = viewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Mis comercios favoritos", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Tus tiendas guardadas. Toca para abrir, o el botón ★ para quitar.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        when (val s = state) {
            is UiState.Loading -> item { Loader() }
            is UiState.Error -> item { ErrorBox(s.message) }
            is UiState.Success -> {
                if (s.data.isEmpty()) {
                    item {
                        EmptyState(
                            "Sin favoritos todavía",
                            "Marca un comercio con ★ desde su pantalla de detalle."
                        )
                    }
                } else {
                    items(s.data) { c ->
                        FavoritoItem(
                            comercio = c,
                            onClick = { onAbrirComercio(c.id) },
                            onQuitar = { vm.quitar(c.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritoItem(
    comercio: ComercioDto,
    onClick: () -> Unit,
    onQuitar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (comercio.tipoComercio.isNotBlank()) Chip(comercio.tipoComercio.replace("_", " "))
                Text(
                    comercio.nombreComercial,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium
                )
                val ubic = listOfNotNull(
                    comercio.ciudad.takeIf { it.isNotBlank() },
                    comercio.estado.takeIf { it.isNotBlank() }
                ).joinToString(", ")
                if (ubic.isNotBlank()) {
                    Text(
                        ubic,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onQuitar) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "Quitar de favoritos",
                    tint = Color(0xFFF5B301)
                )
            }
        }
    }
}
