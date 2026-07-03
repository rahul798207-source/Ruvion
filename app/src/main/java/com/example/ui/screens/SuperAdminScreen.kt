package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DevelopedByFooter
import com.example.ui.components.StatCard
import com.example.ui.theme.SalesGreen
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ProfitBlue
import com.example.ui.theme.PendingOrange
import com.example.ui.viewmodel.RuvionViewModel

@Composable
fun SuperAdminScreen(viewModel: RuvionViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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
                    text = "Super Admin Platform Panel",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
                Text(
                    text = "SaaS Global Management, Billing & Telemetry",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Icon(Icons.Default.Security, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        }

        // Global Platform Stats
        Text(
            text = "Platform Global Performance",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Total SaaS Revenue",
                value = "₹12,48,900.00",
                subtitle = "Monthly recurring",
                icon = Icons.Default.CurrencyRupee,
                color = SalesGreen,
                modifier = Modifier.weight(1.3f)
            )
            StatCard(
                title = "Active Subscriptions",
                value = "1,248 Store",
                subtitle = "Businesses approved",
                icon = Icons.Default.Storefront,
                color = ProfitBlue,
                modifier = Modifier.weight(1f)
            )
        }

        // Action tools section (Database Monitor, Coupons, Backups)
        Text(
            text = "SaaS System Management",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Database and Cloud sync diagnostics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Dns, null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text("Database Telemetry Status", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            Text("SQLite Local & Firestore cloud synchronization", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(SalesGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("HEALTHY", style = MaterialTheme.typography.labelSmall.copy(color = SalesGreen, fontWeight = FontWeight.Bold))
                    }
                }

                HorizontalDivider()

                // Coupon Management trigger
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.ConfirmationNumber, null, tint = PendingOrange)
                        Column {
                            Text("Coupon & Promo Codes", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            Text("Active: RUVION30, STARTERFREE", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }
                    Button(
                        onClick = {
                            Toast.makeText(context, "New Coupon Draft Created", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Manage", style = MaterialTheme.typography.labelMedium)
                    }
                }

                HorizontalDivider()

                // Support tickets management
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.SupportAgent, null, tint = ProfitBlue)
                        Column {
                            Text("Active Support Tickets", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            Text("0 unresolved support queue entries", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }
                    Text(
                        text = "Clean",
                        style = MaterialTheme.typography.labelMedium.copy(color = SalesGreen, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Platform control action keys
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    Toast.makeText(context, "Full Cloud Snapshot Saved! Metadata: developed_by=rahul_edition", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CloudUpload, null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cloud Backup")
            }

            Button(
                onClick = {
                    Toast.makeText(context, "System configuration logs cleared", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.SettingsBackupRestore, null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clear System Logs")
            }
        }

        // Admin panel footer branding
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
        ) {
            Text(
                text = "Ruvion Platform Console\nAdmin Panel Footer | Developed by Rahul Edition",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        DevelopedByFooter()
    }
}
