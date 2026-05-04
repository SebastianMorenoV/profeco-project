package mx.edu.itson.potros.profecoconsumidor.data.network.api

import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ComercioResponse
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.CrearReporteRequest
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.CrearReseniaRequest
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ListaComerciosResponse
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ListaOfertasResponse
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ListaPreciosResponse
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ListaProductosResponse
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ListaReportesResponse
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ListaReseniasResponse
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.OfertaResponse
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ProductoResponse
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.PromedioDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.RegistrarUsuarioRequest
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ReporteResponse
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ReseniaResponse
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.UsuarioResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CatalogoApi {
    @GET("/api/catalogo/productos")
    suspend fun buscarProductos(
        @Query("query") query: String? = null,
        @Query("categoria") categoria: String? = null
    ): ListaProductosResponse

    @GET("/api/catalogo/productos/{id}")
    suspend fun obtenerProducto(@Path("id") id: Long): ProductoResponse

    @GET("/api/catalogo/productos/{id}/precios")
    suspend fun obtenerPreciosProducto(@Path("id") id: Long): ListaPreciosResponse
}

interface ComerciosApi {
    @GET("/api/comercios")
    suspend fun listar(
        @Query("ciudad") ciudad: String? = null,
        @Query("tipo_comercio") tipoComercio: String? = null
    ): ListaComerciosResponse

    @GET("/api/comercios/{id}")
    suspend fun obtener(@Path("id") id: Long): ComercioResponse

    @GET("/api/comercios/buscar")
    suspend fun buscar(@Query("query") query: String): ListaComerciosResponse
}

interface OfertasApi {
    @GET("/api/ofertas")
    suspend fun listar(@Query("solo_activas") soloActivas: Boolean = true): ListaOfertasResponse

    @GET("/api/ofertas/{id}")
    suspend fun obtener(@Path("id") id: Long): OfertaResponse

    @GET("/api/ofertas/comercio/{id}")
    suspend fun listarPorComercio(@Path("id") comercioId: Long): ListaOfertasResponse
}

interface ReseniasApi {
    @GET("/api/resenias/comercio/{id}")
    suspend fun listarPorComercio(@Path("id") comercioId: Long): ListaReseniasResponse

    @GET("/api/resenias/comercio/{id}/promedio")
    suspend fun obtenerPromedio(@Path("id") comercioId: Long): PromedioDto

    @POST("/api/resenias")
    suspend fun crear(@Body body: CrearReseniaRequest): ReseniaResponse
}

interface MultasApi {
    @POST("/api/reportes")
    suspend fun crearReporte(@Body body: CrearReporteRequest): ReporteResponse

    @GET("/api/reportes/usuario/{id}")
    suspend fun listarReportesPorUsuario(@Path("id") usuarioId: Long): ListaReportesResponse
}

interface UsuariosApi {
    @POST("/api/usuarios")
    suspend fun registrar(@Body body: RegistrarUsuarioRequest): UsuarioResponse

    @GET("/api/usuarios/{id}")
    suspend fun obtener(@Path("id") id: Long): UsuarioResponse

    @GET("/api/usuarios/email/{email}")
    suspend fun buscarPorEmail(@Path("email") email: String): UsuarioResponse
}
