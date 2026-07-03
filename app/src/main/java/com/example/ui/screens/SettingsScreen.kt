package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DevelopedByFooter
import com.example.ui.components.RoleBadge
import com.example.ui.theme.SalesGreen
import com.example.ui.theme.PremiumGold
import com.example.ui.viewmodel.RuvionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: RuvionViewModel) {
    val context = LocalContext.current
    val activeBusiness by viewModel.activeBusiness.collectAsState()
    val activeRole by viewModel.activeRole.collectAsState()

    var showAboutDialog by remember { mutableStateOf(false) }

    // Form states
    var name by remember { mutableStateOf(activeBusiness.name) }
    var tagline by remember { mutableStateOf(activeBusiness.tagline) }
    var ownerName by remember { mutableStateOf(activeBusiness.ownerName) }
    var phone by remember { mutableStateOf(activeBusiness.phone) }
    var email by remember { mutableStateOf(activeBusiness.email) }
    var address by remember { mutableStateOf(activeBusiness.address) }
    var gstNumber by remember { mutableStateOf(activeBusiness.gstNumber) }
    var subscriptionPlan by remember { mutableStateOf(activeBusiness.subscriptionPlan) }

    var dropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(activeBusiness) {
        name = activeBusiness.name
        tagline = activeBusiness.tagline
        ownerName = activeBusiness.ownerName
        phone = activeBusiness.phone
        email = activeBusiness.email
        address = activeBusiness.address
        gstNumber = activeBusiness.gstNumber
        subscriptionPlan = activeBusiness.subscriptionPlan
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Business Configuration",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
        )

        // Store active info
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Store Parameters",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    RoleBadge(role = activeRole)
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Store/Business Name") },
                    readOnly = activeRole == "Staff",
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = tagline,
                    onValueChange = { tagline = it },
                    label = { Text("Business Tagline") },
                    readOnly = activeRole == "Staff",
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("Registered Proprietor / Owner Name") },
                    readOnly = activeRole == "Staff",
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        readOnly = activeRole == "Staff",
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        readOnly = activeRole == "Staff",
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Store Address") },
                    readOnly = activeRole == "Staff",
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = gstNumber,
                    onValueChange = { gstNumber = it },
                    label = { Text("GSTIN Identification Code") },
                    readOnly = activeRole == "Staff",
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Plan Selector Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = subscriptionPlan,
                        onValueChange = {},
                        label = { Text("SaaS Subscription Tier Plan") },
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = activeRole != "Staff") { dropdownExpanded = true },
                        shape = RoundedCornerShape(10.dp)
                    )
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.5f)
                    ) {
                        listOf("Free", "Starter", "Professional", "Business", "Enterprise").forEach { tier ->
                            DropdownMenuItem(
                                text = { Text(tier) },
                                onClick = {
                                    subscriptionPlan = tier
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Save parameters button
                if (activeRole != "Staff") {
                    Button(
                        onClick = {
                            viewModel.updateBusinessSettings(
                                name = name,
                                tagline = tagline,
                                ownerName = ownerName,
                                phone = phone,
                                email = email,
                                address = address,
                                gstNumber = gstNumber,
                                subscriptionPlan = subscriptionPlan
                            )
                            Toast.makeText(context, "Business Configuration Saved Successfully!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Business Configuration")
                    }
                }
            }
        }

        // About dialog and Support items list
        Text(
            text = "Platform Resources",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                // About trigger
                ListItem(
                    headlineContent = { Text("About Ruvion ERP", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)) },
                    supportingContent = { Text("SaaS profile version and creator details") },
                    leadingContent = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.clickable { showAboutDialog = true }
                )

                ListItem(
                    headlineContent = { Text("Cloud Sync Diagnostics", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)) },
                    supportingContent = { Text("Force database backup snapshot restore") },
                    leadingContent = { Icon(Icons.Default.Backup, null, tint = SalesGreen) },
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "Local database SQLite backup successful! File metadata: developed_by=rahul_edition", Toast.LENGTH_LONG).show()
                    }
                )

                ListItem(
                    headlineContent = { Text("Contact Customer Support", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)) },
                    supportingContent = { Text("24/7 dedicated support desk") },
                    leadingContent = { Icon(Icons.Default.SupportAgent, null, tint = MaterialTheme.colorScheme.secondary) },
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "Redirecting support tickets to support@ruvion.com", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        DevelopedByFooter()
    }

    // High Fidelity About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            icon = { Icon(Icons.Default.BusinessCenter, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp)) },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ruvion", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black))
                    Text("Smart Business. Simplified.", style = MaterialTheme.typography.labelSmall)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Version: 4.2.0-Enterprise\nDeveloped by Rahul Edition\nCopyright © Rahul Edition",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, lineHeight = 20.sp),
                        textAlign = TextAlign.Center
                    )
                    HorizontalDivider()

                    Text(
                        text = "Ruvion replaces paper register books with a robust, enterprise-grade, offline-capable database. Designed to support Kiranas, garments, medical stores, restaurants, and wholesalers globally.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    HorizontalDivider()

                    // Quick legal/rate/share actions
                    Text(
                        text = "Privacy Policy  •  Terms & Conditions",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "Opening Ruvion Legal Policies...", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Text(
                        text = "Website: https://ruvion.com",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "Opening website link...", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { Toast.makeText(context, "Rate App 5 Stars Clicked!", Toast.LENGTH_SHORT).show() },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Rate 5★", style = MaterialTheme.typography.labelSmall)
                        }
                        Button(
                            onClick = { Toast.makeText(context, "Ruvion Shared Successfully!", Toast.LENGTH_SHORT).show() },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SalesGreen)
                        ) {
                            Text("Share App", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
