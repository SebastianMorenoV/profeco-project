package mx.edu.itson.potros.profecoconsumidor.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Wrapper sobre PullToRefreshBox para mantener consistente el gesto de refrescar
 * en las pantallas con listas. El caller es responsable de poner [refrescando] en
 * true al iniciar la recarga y volverlo a false cuando termine.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshableLista(
    refrescando: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val state = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = refrescando,
        onRefresh = onRefresh,
        state = state,
        modifier = modifier.fillMaxSize()
    ) {
        content()
    }
}
