package mx.edu.itson.potros.profecoconsumidor.ui.screens.perfil

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.itson.potros.profecoconsumidor.data.UserPrefsState

@Composable
fun PerfilScreen(
    onVerFavoritos: () -> Unit,
    onVerWishlist: () -> Unit,
    onVerListaCompras: () -> Unit,
    onCerrarSesion: () -> Unit,
    vm: PerfilViewModel = viewModel()
) {
    val prefs by vm.prefs.collectAsStateWithLifecycle(initialValue = UserPrefsState())

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Mi cuenta", style = MaterialTheme.typography.headlineSmall)
        }

        item {
            Tarjeta {
                Text("Usuario", style = MaterialTheme.typography.titleMedium)
                if (prefs.usuarioNombre.isNotBlank()) {
                    Text(prefs.usuarioNombre, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { vm.cerrarSesion(onCerrarSesion) }
                ) { Text("Cerrar sesión") }
            }
        }

        item {
            Tarjeta {
                Text("Mis listas", style = MaterialTheme.typography.titleMedium)
                ContadorRow(
                    titulo = "Comercios favoritos",
                    valor = prefs.comerciosFavoritos.size,
                    onClick = onVerFavoritos
                )
                ContadorRow(
                    titulo = "Productos en wishlist",
                    valor = prefs.wishlist.size,
                    onClick = onVerWishlist
                )
                ContadorRow(
                    titulo = "Items en lista de compras",
                    valor = prefs.listaCompras.size,
                    onClick = onVerListaCompras
                )
                Text(
                    "Estos datos viven solo en este dispositivo. Cuando exista el endpoint en ms-usuarios se sincronizarán.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        item {
            Tarjeta {
                Text("Búsquedas recientes", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Tienes ${prefs.busquedasRecientes.size} búsquedas guardadas.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                if (prefs.busquedasRecientes.isNotEmpty()) {
                    TextButton(onClick = vm::limpiarBusquedas) { Text("Borrar historial") }
                }
            }
        }
    }
}

@Composable
private fun ContadorRow(titulo: String, valor: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(titulo)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(valor.toString(), fontWeight = FontWeight.Bold)
            Text(
                "  →",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun Tarjeta(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), content = { content() })
    }
}
