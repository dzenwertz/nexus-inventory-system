package com.nexus.inventory.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nexus.inventory.domain.model.Product
import com.nexus.inventory.ui.theme.ElectricBlue
import com.nexus.inventory.ui.theme.SurfaceDark

@Composable
fun UpdateStockDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirmUpdate: (newStock: Int) -> Unit
) {
    var stockInput by remember { mutableStateOf(product.stock.toString()) }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Actualizar Stock",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                Text(text = "Producto: ${product.name}")
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = stockInput,
                    onValueChange = {
                        stockInput = it
                        errorText = null
                    },
                    label = { Text("Nuevo Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = errorText != null
                )
                if (errorText != null) {
                    Text(
                        text = errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newStock = stockInput.toIntOrNull()
                    if (newStock == null || newStock < 0) {
                        errorText = "Ingrese un número válido mayor o igual a 0"
                    } else {
                        onConfirmUpdate(newStock)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
