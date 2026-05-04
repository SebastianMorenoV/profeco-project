package mx.edu.itson.potros.profecoconsumidor.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.itson.potros.profecoconsumidor.data.network.ApiClient
import mx.edu.itson.potros.profecoconsumidor.data.repository.CatalogoRepository
import mx.edu.itson.potros.profecoconsumidor.data.repository.ComerciosRepository
import mx.edu.itson.potros.profecoconsumidor.data.repository.MultasRepository
import mx.edu.itson.potros.profecoconsumidor.data.repository.OfertasRepository
import mx.edu.itson.potros.profecoconsumidor.data.repository.ReseniasRepository

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
    }
}
