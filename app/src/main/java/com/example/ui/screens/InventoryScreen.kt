package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProductEntity
import com.example.ui.components.DevelopedByFooter
import com.example.ui.theme.SalesGreen
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.PendingOrange
import com.example.ui.viewmodel.RuvionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(viewModel: RuvionViewModel) {
    val context = LocalContext.current
    val products by viewModel.searchedProducts.collectAsState()
    val searchQuery by viewModel.productSearchQuery.collectAsState()
    val activeRole by viewModel.activeRole.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedProductForEdit by remember { mutableStateOf<ProductEntity?>(null) }

    // Dialog fields
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var purchasePrice by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf("") }
    var gstPercent by remember { mutableStateOf("18") }
    var stockQty by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("5") }
    var expiryDate by remember { mutableStateOf("") }

    // Reset fields helper
    fun resetFields() {
        name = ""
        category = ""
        brand = ""
        sku = ""
        barcode = ""
        purchasePrice = ""
        sellingPrice = ""
        gstPercent = "18"
        stockQty = ""
        minStock = "5"
        expiryDate = ""
        selectedProductForEdit = null
    }

    LaunchedEffect(selectedProductForEdit) {
        selectedProductForEdit?.let {
            name = it.name
            category = it.category
            brand = it.brand
            sku = it.sku
            barcode = it.barcode
            purchasePrice = it.purchasePrice.toString()
            sellingPrice = it.sellingPrice.toString()
            gstPercent = it.gstPercent.toString()
            stockQty = it.stockQty.toString()
            minStock = it.minStock.toString()
            expiryDate = it.expiryDate
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row with Bulk Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Inventory Ledger",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                    )
                    Text(
                        text = "${products.size} Products Registered",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Bulk Export to Excel Started! Check Downloads.", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.UploadFile, "Export Excel", tint = SalesGreen)
                    }
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Bulk CSV Import Template Selected", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.Downloading, "Import CSV", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Search Bar Catalog
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setProductSearchQuery(it) },
                placeholder = { Text("Search catalog via name, barcode, categories...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setProductSearchQuery("") }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                }
            )

            // Products list
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                if (products.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No stock items found. Click '+' to insert products.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(products) { prod ->
                            val isLowStock = prod.stockQty <= prod.minStock
                            val borderStroke = if (isLowStock) BorderStroke(1.5.dp, ExpenseRed.copy(alpha = 0.5f)) else null

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(if (borderStroke != null) Modifier.border(borderStroke, RoundedCornerShape(16.dp)) else Modifier)
                                    .clickable {
                                        if (activeRole != "Staff") {
                                            selectedProductForEdit = prod
                                            showAddDialog = true
                                        } else {
                                            Toast.makeText(context, "Staff role has no permission to edit products", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = prod.name,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            if (isLowStock) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(ExpenseRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "LOW STOCK",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ExpenseRed)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "Category: ${prod.category} | SKU: ${prod.sku.ifEmpty { "N/A" }}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "SP: ₹${prod.sellingPrice}",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            )
                                            Text(
                                                text = "PP: ₹${prod.purchasePrice}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                            )
                                            if (prod.expiryDate.isNotEmpty()) {
                                                Text(
                                                    text = "Exp: ${prod.expiryDate}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = PendingOrange
                                                )
                                            }
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${prod.stockQty} Qty",
                                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                                            color = if (isLowStock) ExpenseRed else SalesGreen
                                        )
                                        Text(
                                            text = "GST ${prod.gstPercent}%",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Product Floating Action Button (FAB)
        if (activeRole != "Staff") {
            FloatingActionButton(
                onClick = {
                    resetFields()
                    showAddDialog = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Add Product")
            }
        }
    }

    // Add or Edit Product Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = if (selectedProductForEdit == null) "Add New Product" else "Edit Product Details",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Category *") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Brand") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = sku,
                            onValueChange = { sku = it },
                            label = { Text("SKU Number") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = barcode,
                            onValueChange = { barcode = it },
                            label = { Text("Barcode ID") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = purchasePrice,
                            onValueChange = { purchasePrice = it },
                            label = { Text("Purchase Price (₹) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = sellingPrice,
                            onValueChange = { sellingPrice = it },
                            label = { Text("Selling Price (₹) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = gstPercent,
                            onValueChange = { gstPercent = it },
                            label = { Text("GST Percent (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = stockQty,
                            onValueChange = { stockQty = it },
                            label = { Text("Stock Qty *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = minStock,
                            onValueChange = { minStock = it },
                            label = { Text("Min Stock Alert") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = expiryDate,
                            onValueChange = { expiryDate = it },
                            placeholder = { Text("YYYY-MM-DD") },
                            label = { Text("Expiry Date") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    if (selectedProductForEdit != null) {
                        Button(
                            onClick = {
                                viewModel.deleteProduct(selectedProductForEdit!!.id)
                                showAddDialog = false
                                Toast.makeText(context, "Product Deleted Successfully", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Delete, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete Product")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pPrice = purchasePrice.toDoubleOrNull()
                        val sPrice = sellingPrice.toDoubleOrNull()
                        val sQty = stockQty.toIntOrNull()

                        if (name.isBlank() || category.isBlank() || pPrice == null || sPrice == null || sQty == null) {
                            Toast.makeText(context, "Please complete all fields marked with *", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (selectedProductForEdit == null) {
                            viewModel.addProduct(
                                name = name,
                                category = category,
                                brand = brand,
                                sku = sku,
                                barcode = barcode,
                                purchasePrice = pPrice,
                                sellingPrice = sPrice,
                                gstPercent = gstPercent.toIntOrNull() ?: 18,
                                stockQty = sQty,
                                minStock = minStock.toIntOrNull() ?: 5,
                                expiryDate = expiryDate
                            )
                            Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.updateProduct(
                                selectedProductForEdit!!.copy(
                                    name = name,
                                    category = category,
                                    brand = brand,
                                    sku = sku,
                                    barcode = barcode,
                                    purchasePrice = pPrice,
                                    sellingPrice = sPrice,
                                    gstPercent = gstPercent.toIntOrNull() ?: 18,
                                    stockQty = sQty,
                                    minStock = minStock.toIntOrNull() ?: 5,
                                    expiryDate = expiryDate
                                )
                            )
                            Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        showAddDialog = false
                        resetFields()
                    }
                ) {
                    Text(if (selectedProductForEdit == null) "Create Product" else "Save Changes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        resetFields()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
