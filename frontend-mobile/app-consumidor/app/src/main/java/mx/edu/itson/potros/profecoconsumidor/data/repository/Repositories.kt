package mx.edu.itson.potros.profecoconsumidor.data.repository

import mx.edu.itson.potros.profecoconsumidor.data.network.ApiClient
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ComercioDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.CrearReporteRequest
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.CrearReseniaRequest
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ItemListaComprasDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.OfertaDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.PrecioDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ProductoDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.PromedioDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.RegistrarFcmTokenRequest
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ReporteDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ReseniaDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.SyncIdsRequest
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.SyncListaComprasRequest

class CatalogoRepository(private val api: ApiClient) {
    suspend fun buscarProductos(query: String? = null, categoria: String? = null): List<ProductoDto> =
        api.catalogo.buscarProductos(query?.ifBlank { null }, categoria?.ifBlank { null }).productos

    suspend fun obtenerProducto(id: Long): ProductoDto? =
        api.catalogo.obtenerProducto(id).producto

    suspend fun obtenerPrecios(productoId: Long): List<PrecioDto> =
        api.catalogo.obtenerPreciosProducto(productoId).precios
}

class ComerciosRepository(private val api: ApiClient) {
    suspend fun listar(ciudad: String? = null, tipoComercio: String? = null): List<ComercioDto> =
        api.comercios.listar(ciudad?.ifBlank { null }, tipoComercio?.ifBlank { null }).comercios

    suspend fun obtener(id: Long): ComercioDto? =
        api.comercios.obtener(id).comercio

    suspend fun buscar(query: String): List<ComercioDto> =
        api.comercios.buscar(query).comercios
}

class OfertasRepository(private val api: ApiClient) {
    suspend fun listar(soloActivas: Boolean = true): List<OfertaDto> =
        api.ofertas.listar(soloActivas).ofertas

    suspend fun listarPorComercio(comercioId: Long): List<OfertaDto> =
        api.ofertas.listarPorComercio(comercioId).ofertas
}

class ReseniasRepository(private val api: ApiClient) {
    suspend fun listarPorComercio(comercioId: Long): List<ReseniaDto> =
        api.resenias.listarPorComercio(comercioId).resenias

    suspend fun obtenerPromedio(comercioId: Long): PromedioDto =
        api.resenias.obtenerPromedio(comercioId)

    suspend fun crear(usuarioId: Long, comercioId: Long, calificacion: Int, comentario: String): ReseniaDto? =
        api.resenias.crear(
            CrearReseniaRequest(
                usuario_id = usuarioId,
                comercio_id = comercioId,
                calificacion = calificacion,
                comentario = comentario
            )
        ).resenia
}

class MultasRepository(private val api: ApiClient) {
    suspend fun crearReporte(usuarioId: Long, comercioId: Long, motivo: String, descripcion: String): ReporteDto? =
        api.multas.crearReporte(
            CrearReporteRequest(
                usuario_id = usuarioId,
                comercio_id = comercioId,
                motivo = motivo,
                descripcion = descripcion
            )
        ).reporte

    suspend fun listarReportesPorUsuario(usuarioId: Long): List<ReporteDto> =
        api.multas.listarReportesPorUsuario(usuarioId).reportes
}

class UsuariosRepository(private val api: ApiClient) {

    suspend fun registrarFcmToken(usuarioId: Long, token: String, plataforma: String = "ANDROID") {
        api.usuarios.registrarFcmToken(
            usuarioId,
            RegistrarFcmTokenRequest(usuario_id = usuarioId, token = token, plataforma = plataforma)
        )
    }

    suspend fun syncComerciosFavoritos(usuarioId: Long, ids: Set<Long>): Set<Long> =
        api.usuarios.syncComerciosFavoritos(
            usuarioId,
            SyncIdsRequest(usuario_id = usuarioId, ids = ids.toList())
        ).ids.toSet()

    suspend fun obtenerComerciosFavoritos(usuarioId: Long): Set<Long> =
        api.usuarios.obtenerComerciosFavoritos(usuarioId).ids.toSet()

    suspend fun syncWishlist(usuarioId: Long, ids: Set<Long>): Set<Long> =
        api.usuarios.syncWishlist(
            usuarioId,
            SyncIdsRequest(usuario_id = usuarioId, ids = ids.toList())
        ).ids.toSet()

    suspend fun obtenerWishlist(usuarioId: Long): Set<Long> =
        api.usuarios.obtenerWishlist(usuarioId).ids.toSet()

    suspend fun syncListaCompras(usuarioId: Long, items: List<ItemListaComprasDto>): List<ItemListaComprasDto> =
        api.usuarios.syncListaCompras(
            usuarioId,
            SyncListaComprasRequest(usuario_id = usuarioId, items = items)
        ).items

    suspend fun obtenerListaCompras(usuarioId: Long): List<ItemListaComprasDto> =
        api.usuarios.obtenerListaCompras(usuarioId).items
}
