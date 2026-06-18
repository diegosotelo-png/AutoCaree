package com.auto_care_test.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.auto_care_test.ui.mantenimiento.DetalleScreen
import com.auto_care_test.ui.mantenimiento.FormularioScreen
import com.auto_care_test.ui.mantenimiento.ListaScreen
import com.auto_care_test.ui.resumen.ResumenScreen
import com.auto_care_test.ui.vehiculo.VehiculosScreen
import com.auto_care_test.viewmodel.MantenimientoViewModel
import com.auto_care_test.viewmodel.VehiculoViewModel

sealed class Screen(val route: String) {
    object Lista : Screen("lista")
    object Detalle : Screen("detalle/{idMantenimiento}") {
        fun createRoute(id: Int) = "detalle/$id"
    }
    object Formulario : Screen("formulario?idMantenimiento={idMantenimiento}") {
        fun createRoute(id: Int? = null) = if (id != null) "formulario?idMantenimiento=$id" else "formulario"
    }
    object Vehiculos : Screen("vehiculos")
    object Resumen : Screen("resumen")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    mantenimientoViewModel: MantenimientoViewModel,
    vehiculoViewModel: VehiculoViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Lista.route
    ) {
        composable(Screen.Lista.route) {
            ListaScreen(
                viewModel = mantenimientoViewModel,
                onNavigateToDetalle = { id -> navController.navigate(Screen.Detalle.createRoute(id)) },
                onNavigateToFormulario = { id -> navController.navigate(Screen.Formulario.createRoute(id)) },
                onNavigateToVehiculos = { navController.navigate(Screen.Vehiculos.route) },
                onNavigateToResumen = { navController.navigate(Screen.Resumen.route) }
            )
        }
        composable(
            route = Screen.Detalle.route,
            arguments = listOf(navArgument("idMantenimiento") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("idMantenimiento") ?: 0
            DetalleScreen(
                idMantenimiento = id,
                viewModel = mantenimientoViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Screen.Formulario.createRoute(id)) }
            )
        }
        composable(
            route = Screen.Formulario.route,
            arguments = listOf(navArgument("idMantenimiento") { 
                type = NavType.IntType
                defaultValue = -1 
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("idMantenimiento") ?: -1
            FormularioScreen(
                idMantenimiento = if (id == -1) null else id,
                mantenimientoViewModel = mantenimientoViewModel,
                vehiculoViewModel = vehiculoViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Vehiculos.route) {
            VehiculosScreen(
                viewModel = vehiculoViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Resumen.route) {
            ResumenScreen(
                viewModel = mantenimientoViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
