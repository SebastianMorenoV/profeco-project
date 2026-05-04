package mx.edu.itson.potros.profecoconsumidor.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import mx.edu.itson.potros.profecoconsumidor.data.network.ApiClient
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ItemListaComprasDto
import mx.edu.itson.potros.profecoconsumidor.data.repository.CatalogoRepository
import mx.edu.itson.potros.profecoconsumidor.data.repository.ComerciosRepository
import mx.edu.itson.potros.profecoconsumidor.data.repository.MultasRepository
import mx.edu.itson.potros.profecoconsumidor.data.repository.OfertasRepository
import mx.edu.itson.potros.profecoconsumidor.data.repository.ReseniasRepository
import mx.edu.itson.potros.profecoconsumidor.data.repository.UsuariosRepository

object ServiceLocator {

    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var appContext: Context
    lateinit var prefs: UserPrefs
        private set

    private val baseUrlState = MutableStateFlow(UserPrefs.DEFAULT_BASE_URL)
    val baseUrl: StateFlow<String> get() = baseUrlState.asStateFlow()

    private var apiClient: ApiClient = ApiClient { baseUrlState.value }

    val catalogo: CatalogoRepository get() = CatalogoRepository(apiClient)
    val comercios: ComerciosRepository get() = ComerciosRepository(apiClient)
    val ofertas: OfertasRepository get() = OfertasRepository(apiClient)
    val resenias: ReseniasRepository get() = ReseniasRepository(apiClient)
    val multas: MultasRepository get() = MultasRepository(apiClient)
    val usuarios: UsuariosRepository get() = UsuariosRepository(apiClient)

    fun init(context: Context) {
        appContext = context.applicationContext
        prefs = UserPrefs(appContext)
        ioScope.launch {
            prefs.state.collect { state ->
                if (state.baseUrl != baseUrlState.value) {
                    baseUrlState.value = state.baseUrl
                    apiClient = ApiClient { state.baseUrl }
                }
            }
        }
        prefs.setRemoteSync { event ->
            ioScope.launch { sincronizarRemoto(event) }
        }
    }

    /**
     * Empuja los cambios locales al backend.
     * Why: el sync es best-effort — si falla la red, el cambio local ya está guardado y se reintenta
     * la próxima vez que el usuario toque algo.
     */
    private suspend fun sincronizarRemoto(event: SyncEvent) {
        val usuarioId = prefs.state.first().usuarioId
        if (usuarioId <= 0L) return
        runCatching {
            when (event) {
                is SyncEvent.Favoritos    -> usuarios.syncComerciosFavoritos(usuarioId, event.ids)
                is SyncEvent.Wishlist     -> usuarios.syncWishlist(usuarioId, event.ids)
                is SyncEvent.ListaCompras -> usuarios.syncListaCompras(
                    usuarioId,
                    event.items.map { ItemListaComprasDto(it.id, it.nombre, it.marcado) }
                )
            }
        }.onFailure {
            android.util.Log.w("ServiceLocator", "Sync remoto falló: ${it.message}")
        }
    }

    /** Llamado por ProfecoFcmService cuando llega/cambia el token FCM. */
    fun registrarFcmToken(token: String) {
        ioScope.launch {
            val usuarioId = prefs.state.first().usuarioId
            if (usuarioId <= 0L) return@launch
            runCatching { usuarios.registrarFcmToken(usuarioId, token) }
                .onFailure { android.util.Log.w("ServiceLocator", "registrarFcmToken falló: ${it.message}") }
        }
    }
}
