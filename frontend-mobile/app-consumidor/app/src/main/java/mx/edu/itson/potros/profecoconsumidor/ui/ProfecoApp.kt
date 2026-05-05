package mx.edu.itson.potros.profecoconsumidor.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.edu.itson.potros.profecoconsumidor.ui.nav.Routes
import mx.edu.itson.potros.profecoconsumidor.ui.screens.comercios.ComercioDetalleScreen
import mx.edu.itson.potros.profecoconsumidor.ui.screens.comercios.ComerciosScreen
import mx.edu.itson.potros.profecoconsumidor.ui.screens.favoritos.MisFavoritosScreen
import mx.edu.itson.potros.profecoconsumidor.ui.screens.home.HomeScreen
import mx.edu.itson.potros.profecoconsumidor.ui.screens.listacompras.ListaComprasScreen
import mx.edu.itson.potros.profecoconsumidor.ui.screens.ofertas.OfertasScreen
import mx.edu.itson.potros.profecoconsumidor.ui.screens.perfil.PerfilScreen
import mx.edu.itson.potros.profecoconsumidor.ui.screens.productos.ProductoDetalleScreen
import mx.edu.itson.potros.profecoconsumidor.ui.screens.productos.ProductosScreen
import mx.edu.itson.potros.profecoconsumidor.ui.screens.reportar.ReportarScreen
import mx.edu.itson.potros.profecoconsumidor.ui.screens.wishlist.MiWishlistScreen

private data class BottomItem(val label: String, val icon: ImageVector, val route: String, val baseRoute: String)

private val bottomItems = listOf(
    BottomItem("Inicio", Icons.Filled.Home, Routes.HOME, Routes.HOME),
    BottomItem("Productos", Icons.Filled.Search, Routes.productos(), Routes.PRODUCTOS_BASE),
    BottomItem("Comercios", Icons.Filled.Storefront, Routes.COMERCIOS, Routes.COMERCIOS),
    BottomItem("Ofertas", Icons.Filled.LocalOffer, Routes.OFERTAS, Routes.OFERTAS),
    BottomItem("Reportar", Icons.Filled.ReportProblem, Routes.reportar(), Routes.REPORTAR_BASE),
    BottomItem("Cuenta", Icons.Filled.Person, Routes.PERFIL, Routes.PERFIL)
)

@Composable
fun ProfecoApp() {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route.orEmpty()

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomItems.forEach { item ->
                    val seleccionado = currentRoute.startsWith(item.baseRoute)
                    NavigationBarItem(
                        selected = seleccionado,
                        onClick = {
                            nav.navigate(item.route) {
                                popUpTo(Routes.HOME) { saveState = false }
                                launchSingleTop = true
                                restoreState = false
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onBuscar = { q -> nav.navigate(Routes.productos(query = q)) },
                    onVerOfertas = { nav.navigate(Routes.OFERTAS) },
                    onVerProductos = { nav.navigate(Routes.productos()) },
                    onVerComercios = { nav.navigate(Routes.COMERCIOS) },
                    onVerFavoritos = { nav.navigate(Routes.MIS_FAVORITOS) },
                    onVerWishlist = { nav.navigate(Routes.MI_WISHLIST) },
                    onVerListaCompras = { nav.navigate(Routes.LISTA_COMPRAS) }
                )
            }

            composable(
                route = Routes.PRODUCTOS,
                arguments = listOf(
                    navArgument("query") { type = NavType.StringType; defaultValue = "" },
                    navArgument("categoria") { type = NavType.StringType; defaultValue = "" }
                )
            ) { entry ->
                val q = entry.arguments?.getString("query").orEmpty()
                val c = entry.arguments?.getString("categoria").orEmpty()
                ProductosScreen(
                    queryInicial = q,
                    categoriaInicial = c,
                    onAbrirProducto = { id -> nav.navigate(Routes.productoDetalle(id)) }
                )
            }

            composable(
                route = Routes.PRODUCTO_DETALLE,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("id") ?: 0L
                ProductoDetalleScreen(
                    productoId = id,
                    onAbrirComercio = { cid -> nav.navigate(Routes.comercioDetalle(cid)) }
                )
            }

            composable(Routes.COMERCIOS) {
                ComerciosScreen(onAbrirComercio = { id -> nav.navigate(Routes.comercioDetalle(id)) })
            }

            composable(
                route = Routes.COMERCIO_DETALLE,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("id") ?: 0L
                ComercioDetalleScreen(
                    comercioId = id,
                    onReportar = { cid -> nav.navigate(Routes.reportar(cid)) }
                )
            }

            composable(Routes.OFERTAS) { OfertasScreen() }

            composable(
                route = Routes.REPORTAR,
                arguments = listOf(navArgument("comercioId") { type = NavType.LongType; defaultValue = 0L })
            ) { entry ->
                val cid = entry.arguments?.getLong("comercioId") ?: 0L
                ReportarScreen(comercioIdInicial = cid)
            }

            composable(Routes.PERFIL) {
                PerfilScreen(
                    onVerFavoritos = { nav.navigate(Routes.MIS_FAVORITOS) },
                    onVerWishlist = { nav.navigate(Routes.MI_WISHLIST) },
                    onVerListaCompras = { nav.navigate(Routes.LISTA_COMPRAS) }
                )
            }

            composable(Routes.MIS_FAVORITOS) {
                MisFavoritosScreen(onAbrirComercio = { id -> nav.navigate(Routes.comercioDetalle(id)) })
            }

            composable(Routes.MI_WISHLIST) {
                MiWishlistScreen(onAbrirProducto = { id -> nav.navigate(Routes.productoDetalle(id)) })
            }

            composable(Routes.LISTA_COMPRAS) {
                ListaComprasScreen()
            }
        }
    }
}
