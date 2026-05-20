package mx.edu.itson.potros.profecoconsumidor.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import mx.edu.itson.potros.profecoconsumidor.data.network.api.CatalogoApi
import mx.edu.itson.potros.profecoconsumidor.data.network.api.ComerciosApi
import mx.edu.itson.potros.profecoconsumidor.data.network.api.MultasApi
import mx.edu.itson.potros.profecoconsumidor.data.network.api.OfertasApi
import mx.edu.itson.potros.profecoconsumidor.data.network.api.ReseniasApi
import mx.edu.itson.potros.profecoconsumidor.data.network.api.UsuariosApi
import mx.edu.itson.potros.profecoconsumidor.data.network.api.AuthApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class ApiClient(baseUrlProvider: () -> String, private val tokenProvider: () -> String?) {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
        isLenient = true
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            tokenProvider()?.let { token ->
                if (token.isNotBlank()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
            }
            chain.proceed(requestBuilder.build())
        }
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrlProvider().ensureSlash())
        .client(okHttp)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val catalogo: CatalogoApi = retrofit.create(CatalogoApi::class.java)
    val comercios: ComerciosApi = retrofit.create(ComerciosApi::class.java)
    val ofertas: OfertasApi = retrofit.create(OfertasApi::class.java)
    val resenias: ReseniasApi = retrofit.create(ReseniasApi::class.java)
    val multas: MultasApi = retrofit.create(MultasApi::class.java)
    val usuarios: UsuariosApi = retrofit.create(UsuariosApi::class.java)
    val auth: AuthApi = retrofit.create(AuthApi::class.java)
}

private fun String.ensureSlash(): String = if (endsWith("/")) this else "$this/"
