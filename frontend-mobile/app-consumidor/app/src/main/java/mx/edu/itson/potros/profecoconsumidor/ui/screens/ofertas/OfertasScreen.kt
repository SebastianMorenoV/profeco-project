package mx.edu.itson.potros.profecoconsumidor.ui.screens.ofertas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.itson.potros.profecoconsumidor.ui.UiState
import mx.edu.itson.potros.profecoconsumidor.ui.components.EmptyState
import mx.edu.itson.potros.profecoconsumidor.ui.components.ErrorBox
import mx.edu.itson.potros.profecoconsumidor.ui.components.Loader
import mx.edu.itson.potros.profecoconsumidor.ui.components.OfertaCard
import mx.edu.itson.potros.profecoconsumidor.ui.components.RefreshableLista

@Composable
fun OfertasScreen(vm: OfertasViewModel = viewModel()) {
    val state by vm.ofertas.collectAsStateWithLifecycle()
    var soloActivas by remember { mutableStateOf(true) }
    var refrescando by remember { mutableStateOf(false) }

    LaunchedEffect(soloActivas) { vm.cargar(soloActivas) }
    LaunchedEffect(state) { if (state !is UiState.Loading) refrescando = false }

    RefreshableLista(
        refrescando = refrescando,
        onRefresh = {
            refrescando = true
            vm.cargar(soloActivas)
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Ofertas", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Promociones publicadas por comercios.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Switch(checked = soloActivas, onCheckedChange = { soloActivas = it })
                    Text(if (soloActivas) "Solo vigentes" else "Todas (vigentes y vencidas)")
                }
            }
            when (val s = state) {
                is UiState.Loading -> if (!refrescando) item { Loader() }
                is UiState.Error -> item { ErrorBox(s.message) }
                is UiState.Success -> {
                    if (s.data.isEmpty()) {
                        item { EmptyState("Sin ofertas para mostrar") }
                    } else {
                        items(s.data) { o -> OfertaCard(o) }
                    }
                }
            }
        }
    }
}
