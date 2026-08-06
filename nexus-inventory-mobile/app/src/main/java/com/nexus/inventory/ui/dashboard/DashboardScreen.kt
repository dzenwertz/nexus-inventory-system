package com.nexus.inventory.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.inventory.domain.model.Product
import com.nexus.inventory.ui.components.ErrorStateView
import com.nexus.inventory.ui.components.MetricCard
import com.nexus.inventory.ui.components.ProductItemCard
import com.nexus.inventory.ui.components.ShimmerLoadingEffect
import com.nexus.inventory.ui.theme.ElectricBlue
import com.nexus.inventory.ui.theme.NeonGreen
import com.nexus.inventory.ui.theme.TextSecondary
import com.nexus.inventory.ui.theme.WarningYellow

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToProducts: () -> Unit,
    onUpdateStockClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    text = "NexusInventory",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonGreen
                )
                Text(
                    text = "Panel de Control & Cadena de Suministro",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is DashboardUiState.Loading -> {
                    ShimmerLoadingEffect()
                }

                is DashboardUiState.Error -> {
                    ErrorStateView(
                        message = state.message,
                        onRetryClick = { viewModel.loadDashboard() }
                    )
                }

                is DashboardUiState.Success -> {
                    val metrics = state.metrics
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                MetricCard(
                                    title = "Total Stock",
                                    value = "${metrics.totalStockUnits} u.",
                                    icon = Icons.Default.Inventory2,
                                    accentColor = NeonGreen,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    title = "Órdenes Activas",
                                    value = "${metrics.activeOrders}",
                                    icon = Icons.Default.ShoppingCart,
                                    accentColor = ElectricBlue,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            MetricCard(
                                title = "Alertas de Stock Bajo",
                                value = "${metrics.lowStockCount} Productos",
                                icon = Icons.Default.Warning,
                                accentColor = WarningYellow,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }


                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Productos en Alerta Crítica",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(onClick = onNavigateToProducts) {
                                    Text(text = "Ver Todos", color = ElectricBlue)
                                }
                            }
                        }

                        if (metrics.lowStockAlerts.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "¡Excelente! No hay productos en stock bajo.",
                                        color = TextSecondary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        } else {
                            items(metrics.lowStockAlerts) { product ->
                                ProductItemCard(
                                    product = product,
                                    onUpdateStockClick = onUpdateStockClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
