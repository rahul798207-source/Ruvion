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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CustomerEntity
import com.example.data.local.ProductEntity
import com.example.ui.components.DevelopedByFooter
import com.example.ui.components.GeneratedByFooter
import com.example.ui.theme.SalesGreen
import com.example.ui.theme.ExpenseRed
import com.example.ui.viewmodel.RuvionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun POSBillingScreen(viewModel: RuvionViewModel) {
    val context = LocalContext.current
    val products by viewModel.searchedProducts.collectAsState()
    val searchQuery by viewModel.productSearchQuery.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val customers by viewModel.customersList.collectAsState(initial = emptyList())
    val selectedCustomer by viewModel.selectedCartCustomer.collectAsState()
    val cartDiscount by viewModel.cartDiscount.collectAsState()
    val paymentMethod by viewModel.paymentMethod.collectAsState()
    val activeBusiness by viewModel.activeBusiness.collectAsState()

    var showCustomerDropdown by remember { mutableStateOf(false) }
    var discountInput by remember { mutableStateOf("") }
    var showCheckoutInvoiceDialog by remember { mutableStateOf(false) }
    var invoiceDetailsForReceipt by remember { mutableStateOf<String?>(null) }

    // Summary totals
    val subtotal = cart.sumOf { it.product.sellingPrice * it.quantity }
    val calculatedTax = cart.sumOf { (it.product.sellingPrice * it.quantity * it.product.gstPercent) / 100.0 }
    val grandTotal = (subtotal - cartDiscount).coerceAtLeast(0.0)

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Side: POS Cart, checkout configurations, client links (60% width)
            Column(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cart Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "POS Billing Terminal",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                    )
                    IconButton(
                        onClick = {
                            viewModel.clearCart()
                            Toast.makeText(context, "POS Terminal Reset", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.DeleteSweep, "Clear Cart", tint = ExpenseRed)
                    }
                }

                // Customer Link Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCustomerDropdown = true },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedCustomer?.name ?: "Walk-in Retail Customer",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = selectedCustomer?.let { "Phone: ${it.phone} | Udhaar Bal: ₹${it.outstandingBalance}" }
                                        ?: "Select customer to link credit/loyalty points",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                    }

                    DropdownMenu(
                        expanded = showCustomerDropdown,
                        onDismissRequest = { showCustomerDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Walk-in Retail Customer") },
                            onClick = {
                                viewModel.selectCartCustomer(null)
                                showCustomerDropdown = false
                            }
                        )
                        customers.forEach { cust ->
                            DropdownMenuItem(
                                text = { Text("${cust.name} (${cust.phone})") },
                                onClick = {
                                    viewModel.selectCartCustomer(cust)
                                    showCustomerDropdown = false
                                }
                            )
                        }
                    }
                }

                // Billing Cart List Items
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    if (cart.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Empty",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                                    modifier = Modifier.size(72.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Your billing cart is empty.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                                Text(
                                    text = "Tap products on the right to scan/add.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(cart) { item ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.product.name,
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "₹${item.product.sellingPrice} | GST: ${item.product.gstPercent}%",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            IconButton(
                                                onClick = { viewModel.removeProductFromCart(item.product) },
                                                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surface)
                                            ) {
                                                Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp))
                                            }
                                            Text(
                                                text = "${item.quantity}",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            IconButton(
                                                onClick = { viewModel.addProductToCart(item.product) },
                                                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surface)
                                            ) {
                                                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                                            }
                                        }

                                        Text(
                                            text = "₹${"%,.2f".format(item.product.sellingPrice * item.quantity)}",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Billing checkout modifiers (discounts, splits, final totals)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Discount / Coupon", style = MaterialTheme.typography.bodyMedium)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = discountInput,
                                    onValueChange = {
                                        discountInput = it
                                        val disc = it.toDoubleOrNull() ?: 0.0
                                        viewModel.setCartDiscount(disc)
                                    },
                                    placeholder = { Text("₹0.0") },
                                    prefix = { Text("₹") },
                                    modifier = Modifier.width(100.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    maxLines = 1
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tax Summary (GST)", style = MaterialTheme.typography.bodyMedium)
                            Text("₹${"%,.2f".format(calculatedTax)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Grand Total",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "₹${"%,.2f".format(grandTotal)}",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Payment Methods Selector
                        Text(
                            text = "Payment Mode:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("CASH", "UPI", "CARD", "CREDIT").forEach { mode ->
                                val selected = paymentMethod == mode
                                // Disable Credit if customer is not selected
                                val enabled = mode != "CREDIT" || selectedCustomer != null

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = if (selected) 2.dp else 1.dp,
                                            color = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .background(
                                            if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable(enabled = enabled) { viewModel.setPaymentMethod(mode) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = mode,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                        )
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (cart.isEmpty()) {
                                    Toast.makeText(context, "Please add products to your cart first", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                showCheckoutInvoiceDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SalesGreen)
                        ) {
                            Icon(Icons.Default.Check, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Checkout POS Transaction")
                        }
                    }
                }
            }

            // Right Side: Product search library catalogue (40% width)
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Catalog search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setProductSearchQuery(it) },
                    placeholder = { Text("Search catalog, categories, brand...") },
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

                // Simulated Barcode Trigger
                Button(
                    onClick = {
                        // Pick a random product from database and add to cart
                        if (products.isNotEmpty()) {
                            val randomProduct = products.random()
                            viewModel.addProductToCart(randomProduct)
                            Toast.makeText(context, "Barcode Beep! Added ${randomProduct.name}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Catalog is empty. Add products in Inventory first.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.QrCodeScanner, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trigger Barcode Scan")
                }

                // Catalogue Grid list
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    if (products.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No matching catalog items.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(products) { prod ->
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.addProductToCart(prod)
                                            Toast.makeText(context, "Added ${prod.name}", Toast.LENGTH_SHORT).show()
                                        },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = prod.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "${prod.category} | Stock: ${prod.stockQty}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                            )
                                        }
                                        Text(
                                            text = "₹${prod.sellingPrice}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // High Fidelity Invoice Checkout Dialog
    if (showCheckoutInvoiceDialog) {
        val invoiceNo = remember { "INV-2026-${(1000..9999).random()}" }
        AlertDialog(
            onDismissRequest = { showCheckoutInvoiceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Receipt, "Invoice", tint = MaterialTheme.colorScheme.primary)
                    Text("Ruvion Smart Invoice PDF")
                }
            },
            text = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Invoice header
                        Text(
                            text = activeBusiness.name.uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "GSTIN: ${activeBusiness.gstNumber} | Owner: ${activeBusiness.ownerName}\nAddress: ${activeBusiness.address}\nPhone: ${activeBusiness.phone}",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Text(
                            text = "Invoice No: $invoiceNo\nCustomer: ${selectedCustomer?.name ?: "Walk-in Retail"}\nPayment Mode: $paymentMethod",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Render Cart Line Items
                        cart.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.product.name} (x${item.quantity})",
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                                )
                                Text(
                                    text = "₹${"%,.2f".format(item.product.sellingPrice * item.quantity)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                            Text("₹${"%,.2f".format(subtotal)}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                        }
                        if (cartDiscount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Discount", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = ExpenseRed)
                                Text("-₹${"%,.2f".format(cartDiscount)}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = ExpenseRed)
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Calculated GST Tax", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                            Text("₹${"%,.2f".format(calculatedTax)}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("GRAND TOTAL", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                            Text("₹${"%,.2f".format(grandTotal)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // QR Code placeholder / Digital Signature
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Icon(Icons.Default.QrCode2, "QR Code", modifier = Modifier.size(56.dp))
                                Text("Scan to Pay", style = MaterialTheme.typography.labelSmall)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Rahul Edition",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                                )
                                Text("Authorized Signatory", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        GeneratedByFooter()
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.checkoutCart()
                        showCheckoutInvoiceDialog = false
                        Toast.makeText(context, "Invoice $invoiceNo Checkout Completed!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SalesGreen)
                ) {
                    Text("Confirm Checkout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckoutInvoiceDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
