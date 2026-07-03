package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import com.example.data.local.CustomerEntity
import com.example.ui.components.DevelopedByFooter
import com.example.ui.theme.SalesGreen
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.PendingOrange
import com.example.ui.viewmodel.RuvionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UdhaarKhataScreen(viewModel: RuvionViewModel) {
    val context = LocalContext.current
    val customers by viewModel.allCustomers.collectAsState()
    val activeRole by viewModel.activeRole.collectAsState()

    var activeTab by remember { mutableStateOf("CUSTOMER") } // CUSTOMER or SUPPLIER
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCustomerForPayment by remember { mutableStateOf<CustomerEntity?>(null) }
    var paymentAmountInput by remember { mutableStateOf("") }

    // Dialog fields for customer creation
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var gstNumber by remember { mutableStateOf("") }
    var outstandingBalance by remember { mutableStateOf("") }
    var creditLimit by remember { mutableStateOf("10000") }
    var loyaltyPoints by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }

    val filteredList = customers.filter { it.type == activeTab }

    // Aggregate summary
    val totalOutstanding = customers.filter { it.type == "CUSTOMER" }.sumOf { it.outstandingBalance }
    val totalPayable = customers.filter { it.type == "SUPPLIER" }.sumOf { kotlin.math.abs(it.outstandingBalance) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Digital Udhaar Khata",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                    )
                    Text(
                        text = "Smart Bookkeeping Ledger",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            // Summary Stats Card (HIDDEN from Staff)
            if (activeRole != "Staff") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Receivables (Udhaar)", style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = "₹${"%,.2f".format(totalOutstanding)}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = PendingOrange
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Payables (Suppliers)", style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = "₹${"%,.2f".format(totalPayable)}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = ExpenseRed
                            )
                        }
                    }
                }
            }

            // Tabs toggle: Customers vs Suppliers
            TabRow(
                selectedTabIndex = if (activeTab == "CUSTOMER") 0 else 1,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = activeTab == "CUSTOMER",
                    onClick = { activeTab = "CUSTOMER" },
                    text = { Text("Customers (Receivable)") }
                )
                Tab(
                    selected = activeTab == "SUPPLIER",
                    onClick = { activeTab = "SUPPLIER" },
                    text = { Text("Suppliers (Payable)") }
                )
            }

            // List of customers/suppliers
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                if (filteredList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No entries recorded under ${activeTab.lowercase()}s.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredList) { client ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = client.name,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "Phone: ${client.phone}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "₹${"%,.2f".format(kotlin.math.abs(client.outstandingBalance))}",
                                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                                color = if (client.outstandingBalance > 0) PendingOrange else SalesGreen
                                            )
                                            Text(
                                                text = if (client.outstandingBalance > 0) "Pending Udhaar" else "Clear Balance",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Action tools (Reminders, Payment Clear)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            // WhatsApp Reminder simulation
                                            IconButton(
                                                onClick = {
                                                    Toast.makeText(context, "WhatsApp Reminder drafted for ${client.name}!", Toast.LENGTH_SHORT).show()
                                                }
                                            ) {
                                                Icon(Icons.Default.Share, "WhatsApp Alert", tint = SalesGreen)
                                            }
                                            // SMS Reminder simulation
                                            IconButton(
                                                onClick = {
                                                    Toast.makeText(context, "SMS reminder queue cleared!", Toast.LENGTH_SHORT).show()
                                                }
                                            ) {
                                                Icon(Icons.Default.Sms, "SMS Alert", tint = MaterialTheme.colorScheme.primary)
                                            }
                                        }

                                        if (client.outstandingBalance > 0 && activeRole != "Staff") {
                                            Button(
                                                onClick = { selectedCustomerForPayment = client },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = SalesGreen)
                                            ) {
                                                Icon(Icons.Default.Payment, null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Receive Money")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Customer/Supplier Float button
        if (activeRole != "Staff") {
            FloatingActionButton(
                onClick = {
                    name = ""
                    phone = ""
                    email = ""
                    address = ""
                    gstNumber = ""
                    outstandingBalance = ""
                    notes = ""
                    showAddDialog = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.PersonAdd, "Add Customer/Supplier")
            }
        }
    }

    // Payment Clear Dialog
    if (selectedCustomerForPayment != null) {
        AlertDialog(
            onDismissRequest = { selectedCustomerForPayment = null },
            title = { Text("Receive Payment from ${selectedCustomerForPayment!!.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Total outstanding debt balance is ₹${selectedCustomerForPayment!!.outstandingBalance}. Enter amount received:")
                    OutlinedTextField(
                        value = paymentAmountInput,
                        onValueChange = { paymentAmountInput = it },
                        label = { Text("Amount Received (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = paymentAmountInput.toDoubleOrNull()
                        if (amt == null || amt <= 0.0) {
                            Toast.makeText(context, "Please enter a valid cash amount", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.receiveUdhaarPayment(selectedCustomerForPayment!!, amt)
                        selectedCustomerForPayment = null
                        paymentAmountInput = ""
                        Toast.makeText(context, "Payment recorded & balance adjusted!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SalesGreen)
                ) {
                    Text("Save Payment")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCustomerForPayment = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Customer Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New $activeTab Profile") },
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
                        label = { Text("Full Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = gstNumber,
                        onValueChange = { gstNumber = it },
                        label = { Text("GST Number (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = outstandingBalance,
                        onValueChange = { outstandingBalance = it },
                        label = { Text("Initial Ledger Balance (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes / Description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank()) {
                            Toast.makeText(context, "Name and Phone are required fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.addCustomer(
                            name = name,
                            phone = phone,
                            email = email,
                            address = address,
                            gstNumber = gstNumber,
                            type = activeTab,
                            outstandingBalance = outstandingBalance.toDoubleOrNull() ?: 0.0,
                            creditLimit = creditLimit.toDoubleOrNull() ?: 10000.0,
                            loyaltyPoints = loyaltyPoints.toIntOrNull() ?: 0,
                            notes = notes
                        )
                        showAddDialog = false
                        Toast.makeText(context, "Profile Saved Successfully", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Save Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
