package mx.edu.itson.potros.profecoconsumidor.ui.nav

object Routes {
    const val HOME = "home"
    const val PRODUCTOS = "productos?query={query}&categoria={categoria}"
    const val PRODUCTOS_BASE = "productos"
    const val PRODUCTO_DETALLE = "producto/{id}"
    const val PRODUCTO_DETALLE_BASE = "producto"
    const val COMERCIOS = "comercios"
    const val COMERCIO_DETALLE = "comercio/{id}"
    const val COMERCIO_DETALLE_BASE = "comercio"
    const val OFERTAS = "ofertas"
    const val REPORTAR = "reportar?comercioId={comercioId}"
    const val REPORTAR_BASE = "reportar"
    const val PERFIL = "perfil"
    const val MIS_FAVORITOS = "mis-favoritos"
    const val MI_WISHLIST = "mi-wishlist"
    const val LISTA_COMPRAS = "lista-compras"

    fun productos(query: String? = null, categoria: String? = null): String {
        val q = query?.takeIf { it.isNotBlank() } ?: ""
        val c = categoria?.takeIf { it.isNotBlank() } ?: ""
        return "$PRODUCTOS_BASE?query=$q&categoria=$c"
    }

    fun productoDetalle(id: Long) = "$PRODUCTO_DETALLE_BASE/$id"
    fun comercioDetalle(id: Long) = "$COMERCIO_DETALLE_BASE/$id"
    fun reportar(comercioId: Long? = null): String =
        "$REPORTAR_BASE?comercioId=${comercioId ?: 0L}"
}
