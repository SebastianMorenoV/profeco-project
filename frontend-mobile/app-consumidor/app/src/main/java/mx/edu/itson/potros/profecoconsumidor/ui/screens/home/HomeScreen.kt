package mx.edu.itson.potros.profecoconsumidor.ui.screens.home

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.itson.potros.profecoconsumidor.ui.UiState
import mx.edu.itson.potros.profecoconsumidor.ui.components.EmptyState
import mx.edu.itson.potros.profecoconsumidor.ui.components.ErrorBox
import mx.edu.itson.potros.profecoconsumidor.ui.components.Loader
import mx.edu.itson.potros.profecoconsumidor.ui.components.OfertaCard
import mx.edu.itson.potros.profecoconsumidor.ui.theme.ProfecoPurple
import mx.edu.itson.potros.profecoconsumidor.ui.theme.ProfecoPurpleDark

@Composable
fun HomeScreen(
    onBuscar: (String) -> Unit,
    onVerOfertas: () -> Unit,
    onVerProductos: () -> Unit,
    onVerComercios: () -> Unit,
    vm: HomeViewModel = viewModel()
) {
    val state by vm.ofertas.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.cargar() }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Hero(query = query, onQueryChange = { query = it }, onSubmit = { onBuscar(query) }) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ofertas activas", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Ver todas →",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onVerOfertas)
                        .padding(8.dp)
                )
            }
        }

        when (val s = state) {
            is UiState.Loading -> item { Loader() }
            is UiState.Error -> item { ErrorBox(s.message) }
            is UiState.Success -> {
                val destacadas = s.data.take(3)
                if (destacadas.isEmpty()) {
                    item { EmptyState("Aún no hay ofertas publicadas") }
                } else {
                    items(destacadas) { o ->
                        OfertaCard(o)
                    }
                }
            }
        }

        item {
            Text(
                "Explorar",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            QuickActionCard(
                titulo = "Catálogo de productos",
                descripcion = "Compara precios reportados por comercio.",
                onClick = onVerProductos
            )
        }
        item {
            QuickActionCard(
                titulo = "Comercios registrados",
                descripcion = "Califica y consulta reseñas de otros consumidores.",
                onClick = onVerComercios
            )
        }
        item {
            QuickActionCard(
                titulo = "Promociones vigentes",
                descripcion = "Las mejores ofertas publicadas en tiempo real.",
                onClick = onVerOfertas
            )
        }
    }
}

@Composable
private fun Hero(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(ProfecoPurpleDark, ProfecoPurple)))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "Compara precios y reseñas antes de comprar",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                "Encuentra el mejor precio entre comercios de tu ciudad, evalúa el servicio y consulta multas públicas.",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Busca un producto…", color = Color.White.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Color.White) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = onSubmit) { Text("Buscar") }
            }
        }
    }
}

@Composable
private fun QuickActionCard(titulo: String, descripcion: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Text(
                descripcion,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                "Abrir →",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}
