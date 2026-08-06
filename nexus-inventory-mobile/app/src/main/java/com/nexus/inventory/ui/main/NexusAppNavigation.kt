package com.nexus.inventory.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nexus.inventory.data.remote.RetrofitClient
import com.nexus.inventory.data.repository.InventoryRepositoryImpl
import com.nexus.inventory.domain.model.Product
import com.nexus.inventory.domain.usecase.CreateOrderUseCase
import com.nexus.inventory.domain.usecase.GetDashboardUseCase
import com.nexus.inventory.domain.usecase.GetProductsUseCase
import com.nexus.inventory.domain.usecase.UpdateStockUseCase
import com.nexus.inventory.ui.components.UpdateStockDialog
import com.nexus.inventory.ui.dashboard.DashboardScreen
import com.nexus.inventory.ui.dashboard.DashboardViewModel
import com.nexus.inventory.ui.products.ProductListScreen
import com.nexus.inventory.ui.products.ProductViewModel
import com.nexus.inventory.ui.theme.NeonGreen
import com.nexus.inventory.ui.theme.SurfaceDark
import com.nexus.inventory.ui.theme.TextSecondary

sealed class NavScreen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : NavScreen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Products : NavScreen("products", "Productos", Icons.Default.Inventory)
}

@Composable
fun NexusAppNavigation() {
    val navController = rememberNavController()

    val repository = remember { InventoryRepositoryImpl(RetrofitClient.apiService) }

    val dashboardViewModel = remember {
        DashboardViewModel(GetDashboardUseCase(repository))
    }

    val productViewModel = remember {
        ProductViewModel(
            GetProductsUseCase(repository),
            UpdateStockUseCase(repository),
            CreateOrderUseCase(repository)
        )
    }

    var selectedProductForStockUpdate by remember { mutableStateOf<Product?>(null) }

    val items = listOf(NavScreen.Dashboard, NavScreen.Products)

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = SurfaceDark) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonGreen,
                            selectedTextColor = NeonGreen,
                            indicatorColor = NeonGreen.copy(alpha = 0.15f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavScreen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavScreen.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToProducts = {
                        navController.navigate(NavScreen.Products.route)
                    },
                    onUpdateStockClick = { product ->
                        selectedProductForStockUpdate = product
                    }
                )
            }

            composable(NavScreen.Products.route) {
                ProductListScreen(
                    viewModel = productViewModel,
                    onUpdateStockClick = { product ->
                        selectedProductForStockUpdate = product
                    }
                )
            }
        }

        selectedProductForStockUpdate?.let { product ->
            UpdateStockDialog(
                product = product,
                onDismiss = { selectedProductForStockUpdate = null },
                onConfirmUpdate = { newStock ->
                    productViewModel.updateProductStock(product.id, newStock) { success, _ ->
                        selectedProductForStockUpdate = null
                        if (success) {
                            dashboardViewModel.loadDashboard()
                        }
                    }
                }
            )
        }
    }
}
