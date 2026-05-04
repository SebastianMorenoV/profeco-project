package mx.edu.itson.potros.profecoconsumidor.ui.screens.reportar

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.itson.potros.profecoconsumidor.ui.components.EmptyState
import mx.edu.itson.potros.profecoconsumidor.ui.components.ErrorBox
import mx.edu.itson.potros.profecoconsumidor.ui.components.Loader
import mx.edu.itson.potros.profecoconsumidor.ui.components.SuccessBox
import mx.edu.itson.potros.profecoconsumidor.util.MOTIVOS_REPORTE

@Composable
fun ReportarScreen(
    comercioIdInicial: Long = 0L,
    vm: ReportarViewModel = viewModel()
) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    var comercioIdTxt by remember { mutableStateOf(if (comercioIdInicial > 0) comercioIdInicial.toString() else "") }
    var motivoSeleccionado by remember { mutableStateOf(MOTIVOS_REPORTE.first()) }
    var descripcion by remember { mutableStateOf("") }
    var menuMotivo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.cargarMisReportes() }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Reportar inconsistencia", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Notifica a PROFECO sobre comercios que incumplen precios u ofertas.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = comercioIdTxt,
                        onValueChange = { comercioIdTxt = it.filter { c -> c.isDigit() } },
                        label = { Text("ID del comercio") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box {
                        OutlinedTextField(
                            value = motivoSeleccionado.second,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Motivo") },
                            trailingIcon = {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { menuMotivo = true }
                        )
                        DropdownMenu(
                            expanded = menuMotivo,
                            onDismissRequest = { menuMotivo = false }
                        ) {
                            MOTIVOS_REPORTE.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.second) },
                                    onClick = {
                                        motivoSeleccionado = item
                                        menuMotivo = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { if (it.length <= 500) descripcion = it },
                        label = { Text("Descripción") },
                        placeholder = { Text("Cuenta brevemente lo que pasó…") },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (ui.okMensaje != null) SuccessBox(ui.okMensaje!!)
                    if (ui.errorMensaje != null) ErrorBox(ui.errorMensaje!!)
                    Button(
                        onClick = {
                            val cid = comercioIdTxt.toLongOrNull() ?: 0L
                            if (cid > 0 && descripcion.isNotBlank()) {
                                vm.enviar(cid, motivoSeleccionado.first, descripcion)
                                descripcion = ""
                            }
                        },
                        enabled = !ui.enviando &&
                                comercioIdTxt.toLongOrNull()?.let { it > 0 } == true &&
                                descripcion.isNotBlank()
                    ) {
                        Text(if (ui.enviando) "Enviando…" else "Enviar reporte")
                    }
                }
            }
        }

        item {
            Text("Mis reportes", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp))
        }

        when {
            ui.cargandoReportes -> item { Loader() }
            ui.reportes.isEmpty() -> item { EmptyState("Aún no has hecho reportes") }
            else -> {
                ui.reportes.forEach { r ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Reporte #${r.id}", style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        r.estatus,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                                Text(
                                    "Comercio #${r.comercioId} · ${r.motivo.replace("_", " ")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (r.descripcion.isNotBlank()) Text(r.descripcion, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
