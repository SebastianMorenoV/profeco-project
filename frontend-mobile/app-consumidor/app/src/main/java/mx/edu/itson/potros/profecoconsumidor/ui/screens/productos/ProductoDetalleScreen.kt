package mx.edu.itson.potros.profecoconsumidor.ui.screens.productos

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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ComercioDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.PrecioDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ProductoDto
import mx.edu.itson.potros.profecoconsumidor.ui.UiState
import mx.edu.itson.potros.profecoconsumidor.ui.components.Chip
import mx.edu.itson.potros.profecoconsumidor.ui.components.EmptyState
import mx.edu.itson.potros.profecoconsumidor.ui.components.ErrorBox
import mx.edu.itson.potros.profecoconsumidor.ui.components.Loader
import mx.edu.itson.potros.profecoconsumidor.ui.components.SuccessBox
import mx.edu.itson.potros.profecoconsumidor.ui.theme.ProfecoBest
import mx.edu.itson.potros.profecoconsumidor.util.formatDate
import mx.edu.itson.potros.profecoconsumidor.util.formatMXN

@Composable
fun ProductoDetalleScreen(
    productoId: Long,
    onAbrirComercio: (Long) -> Unit,
    vm: ProductoDetalleViewModel = viewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val esWishlist by vm.esWishlist.collectAsStateWithLifecycle()
    var msgLista by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productoId) { vm.cargar(productoId) }

    when (val s = state) {
        is UiState.Loading -> Loader()
        is UiState.Error -> ErrorBox(s.message, modifier = Modifier.padding(16.dp))
        is UiState.Success -> {
            val data = s.data
            if (data.producto == null) {
                EmptyState("Producto no encontrado")
            } else {
                Contenido(
                    producto = data.producto,
                    precios = data.precios,
                    comercios = data.comercios,
                    esWishlist = esWishlist,
                    msgLista = msgLista,
                    onToggleWishlist = vm::toggleWishlist,
                    onAgregarALista = {
                        vm.agregarALista(data.producto.nombre)
                        msgLista = "Agregado a tu lista de compras"
                    },
                    onAbrirComercio = onAbrirComercio
                )
            }
        }
    }
}

@Composable
private fun Contenido(
    producto: ProductoDto,
    precios: List<PrecioDto>,
    comercios: Map<Long, ComercioDto>,
    esWishlist: Boolean,
    msgLista: String?,
    onToggleWishlist: () -> Unit,
    onAgregarALista: () -> Unit,
    onAbrirComercio: (Long) -> Unit
) {
    val ordenados = precios.sortedBy { it.precio }
    val mejor = ordenados.firstOrNull()
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (producto.categoria.isNotBlank()) Chip(producto.categoria)
                    Text(producto.nombre, style = MaterialTheme.typography.headlineSmall)
                    val sub = listOfNotNull(
                        producto.marca.takeIf { it.isNotBlank() },
                        producto.unidadMedida.takeIf { it.isNotBlank() }
                    ).joinToString(" · ")
                    if (sub.isNotBlank()) {
                        Text(sub, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (producto.descripcion.isNotBlank()) {
                        Text(producto.descripcion, modifier = Modifier.padding(top = 4.dp))
                    }
                    if (producto.codigoBarras.isNotBlank()) {
                        Text(
                            "Código de barras: ${producto.codigoBarras}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onToggleWishlist,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (esWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = if (esWishlist) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                if (esWishlist) "  En wishlist" else "  Wishlist",
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        OutlinedButton(
                            onClick = onAgregarALista,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Outlined.AddShoppingCart, contentDescription = null)
                            Text("  A mi lista", modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                    if (msgLista != null) {
                        SuccessBox(msgLista, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Comparación de precios", style = MaterialTheme.typography.titleLarge)
                if (mejor != null) {
                    Text(
                        "Mejor: ${formatMXN(mejor.precio)}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (ordenados.isEmpty()) {
            item { EmptyState("Aún no hay precios reportados para este producto") }
        } else {
            items(ordenados) { p ->
                FilaPrecio(
                    precio = p,
                    comercio = comercios[p.comercioId],
                    esMejor = p.id == mejor?.id,
                    onClick = { comercios[p.comercioId]?.let { onAbrirComercio(it.id) } }
                )
            }
        }
    }
}

@Composable
private fun FilaPrecio(
    precio: PrecioDto,
    comercio: ComercioDto?,
    esMejor: Boolean,
    onClick: () -> Unit
) {
    val bg = if (esMejor) ProfecoBest else MaterialTheme.colorScheme.surface
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    comercio?.nombreComercial ?: "Comercio #${precio.comercioId}",
                    fontWeight = FontWeight.SemiBold
                )
                Text(formatMXN(precio.precio), fontWeight = FontWeight.Bold)
            }
            val sub = listOfNotNull(
                comercio?.ciudad?.takeIf { it.isNotBlank() },
                "Reportado: ${formatDate(precio.fechaReporte)}".takeIf { precio.fechaReporte.isNotBlank() }
            ).joinToString(" · ")
            if (sub.isNotBlank()) {
                Text(sub, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
            if (esMejor) {
                Text(
                    "Mejor precio",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
