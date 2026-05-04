package mx.edu.itson.potros.profecoconsumidor.ui.screens.productos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
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
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ProductoDto
import mx.edu.itson.potros.profecoconsumidor.ui.UiState
import mx.edu.itson.potros.profecoconsumidor.ui.components.Chip
import mx.edu.itson.potros.profecoconsumidor.ui.components.EmptyState
import mx.edu.itson.potros.profecoconsumidor.ui.components.ErrorBox
import mx.edu.itson.potros.profecoconsumidor.ui.components.Loader
import mx.edu.itson.potros.profecoconsumidor.ui.components.RefreshableLista
import mx.edu.itson.potros.profecoconsumidor.util.CATEGORIAS

@Composable
fun ProductosScreen(
    queryInicial: String = "",
    categoriaInicial: String = "",
    onAbrirProducto: (Long) -> Unit,
    vm: ProductosViewModel = viewModel()
) {
    val state by vm.productos.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf(queryInicial) }
    var categoria by remember { mutableStateOf(categoriaInicial) }
    var refrescando by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.buscar(queryInicial, categoriaInicial) }
    LaunchedEffect(state) { if (state !is UiState.Loading) refrescando = false }

    RefreshableLista(
        refrescando = refrescando,
        onRefresh = {
            refrescando = true
            vm.buscar(query, categoria)
        }
    ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Catálogo de productos", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Busca por nombre o filtra por categoría.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    vm.buscar(query, categoria)
                },
                singleLine = true,
                placeholder = { Text("Buscar producto…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = categoria.isBlank(),
                        onClick = {
                            categoria = ""
                            vm.buscar(query, "")
                        },
                        label = { Text("Todas") }
                    )
                }
                items(CATEGORIAS) { c ->
                    FilterChip(
                        selected = categoria == c,
                        onClick = {
                            categoria = if (categoria == c) "" else c
                            vm.buscar(query, categoria)
                        },
                        label = { Text(c) }
                    )
                }
            }
        }

        when (val s = state) {
            is UiState.Loading -> if (!refrescando) item { Loader() }
            is UiState.Error -> item { ErrorBox(s.message) }
            is UiState.Success -> {
                if (s.data.isEmpty()) {
                    item { EmptyState("Sin resultados", "Intenta otra búsqueda o cambia el filtro") }
                } else {
                    items(s.data) { p ->
                        ProductoItem(p, onClick = { onAbrirProducto(p.id) })
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun ProductoItem(producto: ProductoDto, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (producto.categoria.isNotBlank()) Chip(producto.categoria)
            Text(producto.nombre, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            if (producto.marca.isNotBlank() || producto.unidadMedida.isNotBlank()) {
                Text(
                    listOfNotNull(
                        producto.marca.takeIf { it.isNotBlank() },
                        producto.unidadMedida.takeIf { it.isNotBlank() }
                    ).joinToString(" · "),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (producto.descripcion.isNotBlank()) {
                Text(producto.descripcion, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
        }
    }
}
