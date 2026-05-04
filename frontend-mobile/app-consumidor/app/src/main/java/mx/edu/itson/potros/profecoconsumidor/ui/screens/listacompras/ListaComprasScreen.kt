package mx.edu.itson.potros.profecoconsumidor.ui.screens.listacompras

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.itson.potros.profecoconsumidor.data.ItemCompra
import mx.edu.itson.potros.profecoconsumidor.ui.components.EmptyState

@Composable
fun ListaComprasScreen(vm: ListaComprasViewModel = viewModel()) {
    val items by vm.items.collectAsStateWithLifecycle(initialValue = emptyList())
    var nuevo by remember { mutableStateOf("") }

    val agregar = {
        if (nuevo.isNotBlank()) {
            vm.agregar(nuevo)
            nuevo = ""
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Lista de supermercado", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Anota lo que vas a comprar y ve marcando lo que ya tienes.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = nuevo,
                    onValueChange = { nuevo = it },
                    placeholder = { Text("Ej. Leche, huevos, jabón…") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { agregar() }),
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = agregar, enabled = nuevo.isNotBlank()) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar")
                }
            }
        }

        if (items.isEmpty()) {
            item {
                EmptyState(
                    "Tu lista está vacía",
                    "Agrega productos arriba o desde el detalle de cada producto."
                )
            }
        } else {
            items(items, key = { it.id }) { item ->
                ItemRow(item, onToggle = { vm.toggle(item.id) }, onEliminar = { vm.eliminar(item.id) })
            }
            item {
                OutlinedButton(
                    onClick = vm::limpiar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) { Text("Vaciar lista") }
            }
        }
    }
}

@Composable
private fun ItemRow(item: ItemCompra, onToggle: () -> Unit, onEliminar: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = item.marcado, onCheckedChange = { onToggle() })
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    item.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (item.marcado) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (item.marcado) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
