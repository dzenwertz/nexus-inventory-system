package com.nexus.inventory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.inventory.domain.model.Product
import com.nexus.inventory.ui.theme.ElectricBlue
import com.nexus.inventory.ui.theme.NeonGreen
import com.nexus.inventory.ui.theme.SurfaceDark
import com.nexus.inventory.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderBottomSheet(
    products: List<Product>,
    onDismissRequest: () -> Unit,
    onSubmitOrder: (customerName: String, selectedItems: Map<Long, Int>) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    val itemQuantities = remember { mutableStateMapOf<Long, Int>() }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Registrar Nueva Orden",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = customerName,
                onValueChange = {
                    customerName = it
                    errorMessage = null
                },
                label = { Text("Nombre del Cliente") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Seleccionar Productos:",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .heightIn(max = 280.dp)
            ) {
                items(products) { product ->
                    val qty = itemQuantities[product.id] ?: 0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = product.name, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "$${product.price} | Stock: ${product.stock}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (qty > 0) {
                                        if (qty == 1) itemQuantities.remove(product.id)
                                        else itemQuantities[product.id] = qty - 1
                                    }
                                },
                                enabled = qty > 0
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Restar")
                            }

                            Text(
                                text = qty.toString(),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            IconButton(
                                onClick = {
                                    if (qty < product.stock) {
                                        itemQuantities[product.id] = qty + 1
                                    }
                                },
                                enabled = qty < product.stock
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Sumar")
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (customerName.isBlank()) {
                        errorMessage = "Por favor ingrese el nombre del cliente"
                        return@Button
                    }
                    if (itemQuantities.isEmpty()) {
                        errorMessage = "Seleccione al menos 1 producto con cantidad mayor a 0"
                        return@Button
                    }
                    onSubmitOrder(customerName, itemQuantities)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
            ) {
                Text(
                    text = "Confirmar Orden",
                    color = SurfaceDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
