package mx.edu.itson.potros.profecoconsumidor.ui.screens.wishlist

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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

@Composable
fun MiWishlistScreen(
    onAbrirProducto: (Long) -> Unit,
    vm: MiWishlistViewModel = viewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Mi wishlist", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Productos que quieres conseguir. Toca para ver precios.",
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
                            "Tu wishlist está vacía",
                            "Toca ❤ en el detalle de un producto para guardarlo."
                        )
                    }
                } else {
                    items(s.data) { p ->
                        WishlistItem(
                            producto = p,
                            onClick = { onAbrirProducto(p.id) },
                            onQuitar = { vm.quitar(p.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WishlistItem(
    producto: ProductoDto,
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
                if (producto.categoria.isNotBlank()) Chip(producto.categoria)
                Text(
                    producto.nombre,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium
                )
                val sub = listOfNotNull(
                    producto.marca.takeIf { it.isNotBlank() },
                    producto.unidadMedida.takeIf { it.isNotBlank() }
                ).joinToString(" · ")
                if (sub.isNotBlank()) {
                    Text(
                        sub,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onQuitar) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Quitar de wishlist",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
