package mx.edu.itson.potros.profecoconsumidor.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "profeco_prefs")

class UserPrefs(private val context: Context) {

    val state: Flow<UserPrefsState> = context.dataStore.data.map { p ->
        UserPrefsState(
            usuarioId = p[KEY_USUARIO_ID] ?: 1L,
            baseUrl = p[KEY_BASE_URL] ?: DEFAULT_BASE_URL,
            comerciosFavoritos = p[KEY_FAV_COMERCIOS]
                ?.split(",")
                ?.mapNotNull { it.trim().toLongOrNull() }
                ?.toSet()
                .orEmpty(),
            wishlist = p[KEY_WISHLIST]
                ?.split(",")
                ?.mapNotNull { it.trim().toLongOrNull() }
                ?.toSet()
                .orEmpty(),
            recibioPushes = p[KEY_RECIBIO_PUSHES] ?: false
        )
    }

    suspend fun setUsuarioId(id: Long) =
        context.dataStore.edit { it[KEY_USUARIO_ID] = id }

    suspend fun setBaseUrl(url: String) =
        context.dataStore.edit { it[KEY_BASE_URL] = url.ifBlank { DEFAULT_BASE_URL } }

    suspend fun toggleComercioFavorito(comercioId: Long) =
        context.dataStore.edit { p ->
            val actuales = p[KEY_FAV_COMERCIOS]
                ?.split(",")
                ?.mapNotNull { it.trim().toLongOrNull() }
                ?.toMutableSet()
                ?: mutableSetOf()
            if (!actuales.add(comercioId)) actuales.remove(comercioId)
            p[KEY_FAV_COMERCIOS] = actuales.joinToString(",")
        }

    suspend fun toggleProductoWishlist(productoId: Long) =
        context.dataStore.edit { p ->
            val actuales = p[KEY_WISHLIST]
                ?.split(",")
                ?.mapNotNull { it.trim().toLongOrNull() }
                ?.toMutableSet()
                ?: mutableSetOf()
            if (!actuales.add(productoId)) actuales.remove(productoId)
            p[KEY_WISHLIST] = actuales.joinToString(",")
        }

    suspend fun marcarPushRecibido() =
        context.dataStore.edit { it[KEY_RECIBIO_PUSHES] = true }

    companion object {
        const val DEFAULT_BASE_URL = "http://10.0.2.2:8084"
        private val KEY_USUARIO_ID = longPreferencesKey("usuario_id")
        private val KEY_BASE_URL = stringPreferencesKey("base_url")
        private val KEY_FAV_COMERCIOS = stringPreferencesKey("fav_comercios")
        private val KEY_WISHLIST = stringPreferencesKey("wishlist")
        private val KEY_RECIBIO_PUSHES = booleanPreferencesKey("recibio_pushes")
    }
}

data class UserPrefsState(
    val usuarioId: Long = 1L,
    val baseUrl: String = UserPrefs.DEFAULT_BASE_URL,
    val comerciosFavoritos: Set<Long> = emptySet(),
    val wishlist: Set<Long> = emptySet(),
    val recibioPushes: Boolean = false
)
