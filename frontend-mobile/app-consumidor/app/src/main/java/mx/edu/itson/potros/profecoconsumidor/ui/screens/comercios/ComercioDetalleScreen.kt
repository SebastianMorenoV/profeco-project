package mx.edu.itson.potros.profecoconsumidor.ui.screens.comercios

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.OfertaDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.PromedioDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ReseniaDto
import mx.edu.itson.potros.profecoconsumidor.ui.UiState
import mx.edu.itson.potros.profecoconsumidor.ui.components.Chip
import mx.edu.itson.potros.profecoconsumidor.ui.components.EmptyState
import mx.edu.itson.potros.profecoconsumidor.ui.components.ErrorBox
import mx.edu.itson.potros.profecoconsumidor.ui.components.Loader
import mx.edu.itson.potros.profecoconsumidor.ui.components.OfertaCard
import mx.edu.itson.potros.profecoconsumidor.ui.components.StarRating
import mx.edu.itson.potros.profecoconsumidor.ui.components.SuccessBox
import mx.edu.itson.potros.profecoconsumidor.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComercioDetalleScreen(
    comercioId: Long,
    onReportar: (Long) -> Unit,
    vm: ComercioDetalleViewModel = viewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val esFavorito by vm.esFavorito.collectAsStateWithLifecycle()

    LaunchedEffect(comercioId) { vm.cargar(comercioId) }

    when (val s = state) {
        is UiState.Loading -> Loader()
        is UiState.Error -> ErrorBox(s.message, modifier = Modifier.padding(16.dp))
        is UiState.Success -> {
            val data = s.data
            if (data.comercio == null) {
                EmptyState("Comercio no encontrado")
            } else {
                Contenido(
                    comercio = data.comercio,
                    promedio = data.promedio,
                    resenias = data.resenias,
                    ofertas = data.ofertas,
                    okMsg = data.okMensaje,
                    onPublicar = vm::publicarResenia,
                    onReportar = { onReportar(data.comercio.id) },
                    esFavorito = esFavorito,
                    onToggleFavorito = { vm.toggleFavorito(data.comercio.id) }
                )
            }
        }
    }
}

@Composable
private fun Contenido(
    comercio: ComercioDto,
    promedio: PromedioDto?,
    resenias: List<ReseniaDto>,
    ofertas: List<OfertaDto>,
    okMsg: String?,
    onPublicar: (Int, String) -> Unit,
    onReportar: () -> Unit,
    esFavorito: Boolean,
    onToggleFavorito: () -> Unit
) {
    var calificacion by remember { mutableIntStateOf(5) }
    var comentario by remember { mutableStateOf("") }

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
                    if (comercio.tipoComercio.isNotBlank()) Chip(comercio.tipoComercio.replace("_", " "))
                    Text(comercio.nombreComercial, style = MaterialTheme.typography.headlineSmall)
                    if (comercio.razonSocial.isNotBlank() || comercio.rfc.isNotBlank()) {
                        Text(
                            "${comercio.razonSocial} · RFC ${comercio.rfc}".trim('·', ' '),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (comercio.direccion.isNotBlank()) Text(comercio.direccion)
                    val ubic = listOfNotNull(
                        comercio.ciudad.takeIf { it.isNotBlank() },
                        comercio.estado.takeIf { it.isNotBlank() }
                    ).joinToString(", ")
                    if (ubic.isNotBlank()) Text(
                        ubic + (if (comercio.codigoPostal.isNotBlank()) " · CP ${comercio.codigoPostal}" else ""),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val contacto = listOfNotNull(
                        comercio.telefono.takeIf { it.isNotBlank() },
                        comercio.email.takeIf { it.isNotBlank() }
                    ).joinToString(" · ")
                    if (contacto.isNotBlank()) Text(
                        contacto,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (promedio != null && promedio.totalResenias > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            StarRating(value = promedio.promedio, size = 22.dp)
                            Text(
                                "${"%.1f".format(promedio.promedio)} de 5",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "· ${promedio.totalResenias} reseñas",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            "Aún no hay reseñas para este comercio.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(onClick = onToggleFavorito, modifier = Modifier.weight(1f)) {
                            Text(if (esFavorito) "★ Favorito" else "☆ Marcar favorito")
                        }
                        OutlinedButton(onClick = onReportar, modifier = Modifier.weight(1f)) {
                            Text("Reportar")
                        }
                    }
                }
            }
        }

        item { Text("Ofertas vigentes", style = MaterialTheme.typography.titleLarge) }
        if (ofertas.isEmpty()) {
            item { EmptyState("Sin ofertas activas") }
        } else {
            items(ofertas) { o -> OfertaCard(o) }
        }

        item { Text("Reseñas", style = MaterialTheme.typography.titleLarge) }
        if (resenias.isEmpty()) {
            item { EmptyState("Sé el primero en opinar") }
        } else {
            items(resenias) { r -> ReseniaItem(r) }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Deja tu reseña", style = MaterialTheme.typography.titleMedium)
                    Text("Tu calificación", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    StarRating(
                        value = calificacion.toDouble(),
                        size = 28.dp,
                        onChange = { calificacion = it }
                    )
                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { if (it.length <= 500) comentario = it },
                        placeholder = { Text("¿Cómo fue tu experiencia?") },
                        minLines = 2,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (okMsg != null) SuccessBox(okMsg)
                    Button(
                        onClick = {
                            if (comentario.isNotBlank()) {
                                onPublicar(calificacion, comentario)
                                comentario = ""
                            }
                        },
                        enabled = comentario.isNotBlank()
                    ) {
                        Text("Publicar reseña")
                    }
                }
            }
        }
    }
}

@Composable
private fun ReseniaItem(resenia: ReseniaDto) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StarRating(value = resenia.calificacion.toDouble())
                Text(
                    "Usuario #${resenia.usuarioId} · ${formatDate(resenia.fechaCreacion)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (resenia.comentario.isNotBlank()) Text(resenia.comentario)
        }
    }
}
