package mx.edu.itson.potros.profecoconsumidor.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductoDto(
    val id: Long = 0,
    val nombre: String = "",
    val descripcion: String = "",
    val marca: String = "",
    val categoria: String = "",
    val codigoBarras: String = "",
    val unidadMedida: String = "",
    val activo: Boolean = true,
    val fechaCreacion: String = ""
)

@Serializable
data class PrecioDto(
    val id: Long = 0,
    val productoId: Long = 0,
    val comercioId: Long = 0,
    val precio: Double = 0.0,
    val fechaReporte: String = ""
)

@Serializable
data class ComercioDto(
    val id: Long = 0,
    val nombreComercial: String = "",
    val razonSocial: String = "",
    val rfc: String = "",
    val direccion: String = "",
    val ciudad: String = "",
    val estado: String = "",
    val codigoPostal: String = "",
    val telefono: String = "",
    val email: String = "",
    val tipoComercio: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val idPropietario: Long = 0,
    val activo: Boolean = true,
    val fechaRegistro: String = ""
)

@Serializable
data class OfertaDto(
    val id: Long = 0,
    val comercioId: Long = 0,
    val titulo: String = "",
    val descripcion: String = "",
    val precioOriginal: Double = 0.0,
    val precioOferta: Double = 0.0,
    val porcentajeDescuento: Double = 0.0,
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val activa: Boolean = true,
    val fechaCreacion: String = ""
)

@Serializable
data class ReseniaDto(
    val id: Long = 0,
    val usuarioId: Long = 0,
    val comercioId: Long = 0,
    val calificacion: Int = 0,
    val comentario: String = "",
    val fechaCreacion: String = ""
)

@Serializable
data class PromedioDto(
    val comercioId: Long = 0,
    val promedio: Double = 0.0,
    val totalResenias: Int = 0
)

@Serializable
data class ReporteDto(
    val id: Long = 0,
    val usuarioId: Long = 0,
    val comercioId: Long = 0,
    val motivo: String = "",
    val descripcion: String = "",
    val estatus: String = "",
    val fechaCreacion: String = "",
    val multaId: Long = 0
)

@Serializable
data class UsuarioDto(
    val id: Long = 0,
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val tipoUsuario: String = "",
    val activo: Boolean = true,
    val fechaRegistro: String = ""
)

// =============== Wrappers de respuesta (espejo de los proto Response) ===============

@Serializable data class ProductoResponse(val producto: ProductoDto? = null)
@Serializable data class ListaProductosResponse(val productos: List<ProductoDto> = emptyList())
@Serializable data class ListaPreciosResponse(val precios: List<PrecioDto> = emptyList())

@Serializable data class ComercioResponse(val comercio: ComercioDto? = null)
@Serializable data class ListaComerciosResponse(val comercios: List<ComercioDto> = emptyList())

@Serializable data class OfertaResponse(val oferta: OfertaDto? = null)
@Serializable data class ListaOfertasResponse(val ofertas: List<OfertaDto> = emptyList())

@Serializable data class ReseniaResponse(val resenia: ReseniaDto? = null)
@Serializable data class ListaReseniasResponse(val resenias: List<ReseniaDto> = emptyList())

@Serializable data class ReporteResponse(val reporte: ReporteDto? = null)
@Serializable data class ListaReportesResponse(val reportes: List<ReporteDto> = emptyList())

@Serializable data class UsuarioResponse(val usuario: UsuarioDto? = null)

// =============== Bodies de petición (snake_case como el backend espera) ===============

@Serializable
data class CrearReseniaRequest(
    val usuario_id: Long,
    val comercio_id: Long,
    val calificacion: Int,
    val comentario: String
)

@Serializable
data class CrearReporteRequest(
    val usuario_id: Long,
    val comercio_id: Long,
    val motivo: String,
    val descripcion: String
)

@Serializable
data class RegistrarUsuarioRequest(
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val tipo_usuario: String = "CONSUMIDOR",
    val password: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val exito: Boolean = false,
    val mensaje: String = "",
    val usuario: UsuarioDto? = null
)

@Serializable
data class CambiarPasswordRequest(
    val id: Long,
    val password_actual: String,
    val password_nuevo: String
)

// =============== Sync del consumidor con el backend ===============

@Serializable
data class RegistrarFcmTokenRequest(
    val usuario_id: Long,
    val token: String,
    val plataforma: String = "ANDROID"
)

@Serializable
data class MensajeResponseDto(
    val mensaje: String = "",
    val exito: Boolean = false
)

@Serializable
data class SyncIdsRequest(
    val usuario_id: Long,
    val ids: List<Long>
)

@Serializable
data class ListaIdsResponseDto(
    val usuarioId: Long = 0,
    val ids: List<Long> = emptyList()
)

@Serializable
data class ItemListaComprasDto(
    val idLocal: Int = 0,
    val nombre: String = "",
    val marcado: Boolean = false
)

@Serializable
data class SyncListaComprasRequest(
    val usuario_id: Long,
    val items: List<ItemListaComprasDto>
)

@Serializable
data class ListaItemsComprasResponseDto(
    val usuarioId: Long = 0,
    val items: List<ItemListaComprasDto> = emptyList()
)
