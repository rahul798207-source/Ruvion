package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AISmartScreen(viewModel: RuvionViewModel) {
    val context = LocalContext.current
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val aiForecastText by viewModel.aiForecastText.collectAsState()
    val chatHistory by viewModel.chatHistory.collectAsState()
    val activeRole by viewModel.activeRole.collectAsState()

    var activeTab by remember { mutableStateOf("FORECAST") } // FORECAST or CHAT
    var chatMessageInput by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val chatListState = rememberLazyListState()

    // Scroll to latest message on change
    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            chatListState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Ruvion AI Smart Hub",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
                Text(
                    text = "Predictive intelligence & virtual assistance",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(32.dp)
            )
        }

        // Mode Navigation Row: Forecasting vs Chat Companion
        TabRow(
            selectedTabIndex = if (activeTab == "FORECAST") 0 else 1,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = activeTab == "FORECAST",
                onClick = { activeTab = "FORECAST" },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.TrendingUp, null, modifier = Modifier.size(16.dp))
                        Text("Predictive Forecasts")
                    }
                }
            )
            Tab(
                selected = activeTab == "CHAT",
                onClick = { activeTab = "CHAT" },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.SmartToy, null, modifier = Modifier.size(16.dp))
                        Text("Ruvion Chat AI")
                    }
                }
            )
        }

        if (activeRole == "Staff") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Lock, "Lock", tint = ExpenseRed)
                    Text(
                        text = "Access Denied: AI predictive analyses and business forecasts are disabled for Staff accounts.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = ExpenseRed
                    )
                }
            }
        } else {
            when (activeTab) {
                "FORECAST" -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Action card to generate forecast
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "3-Month AI Forecasting Engine",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Ruvion AI compiles real-time bookkeeping records, inventories, and operational overheads directly from SQLite and constructs dynamic projections with Gemini.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )

                                Button(
                                    onClick = { viewModel.generateAiForecast() },
                                    enabled = !isAiLoading,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (isAiLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                                    } else {
                                        Icon(Icons.Default.AutoAwesome, null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Execute AI Analysis")
                                    }
                                }
                            }
                        }

                        // Forecast results Card
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.QueryStats, null, tint = MaterialTheme.colorScheme.primary)
                                    Text(
                                        text = "AI Projections Result",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(12.dp))

                                if (isAiLoading) {
                                    // Skeletal loader
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(modifier = Modifier.fillMaxWidth().height(16.dp).background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(4.dp)))
                                        Box(modifier = Modifier.fillMaxWidth(0.9f).height(16.dp).background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(4.dp)))
                                        Box(modifier = Modifier.fillMaxWidth(0.85f).height(16.dp).background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(4.dp)))
                                        Box(modifier = Modifier.fillMaxWidth(0.6f).height(16.dp).background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(4.dp)))
                                    }
                                } else {
                                    Text(
                                        text = aiForecastText,
                                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp, fontFamily = FontFamily.Monospace),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        DevelopedByFooter()
                    }
                }

                "CHAT" -> {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Conversational Chat bubbles
                        LazyColumn(
                            state = chatListState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(chatHistory) { messagePair ->
                                val isUser = messagePair.second
                                val alignment = if (isUser) Alignment.End else Alignment.Start
                                val containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                val textColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                val cornerShape = if (isUser) {
                                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp)
                                } else {
                                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp)
                                }

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = alignment
                                ) {
                                    Card(
                                        shape = cornerShape,
                                        colors = CardDefaults.cardColors(containerColor = containerColor),
                                        modifier = Modifier.widthIn(max = 280.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            if (!isUser) {
                                                Text(
                                                    text = "RUVION AI",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                                                    color = MaterialTheme.colorScheme.tertiary,
                                                    modifier = Modifier.padding(bottom = 4.dp)
                                                )
                                            }
                                            Text(
                                                text = messagePair.first,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = textColor
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Message entry row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = chatMessageInput,
                                onValueChange = { chatMessageInput = it },
                                placeholder = { Text("Ask Ruvion AI about accounting, stock level projections...") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            Toast.makeText(context, "Voice OCR Scanner Mock triggered!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.Mic, "Voice commands", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            )

                            Button(
                                onClick = {
                                    if (chatMessageInput.isNotBlank()) {
                                        viewModel.sendChatMessage(chatMessageInput)
                                        chatMessageInput = ""
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(12.dp)
                            ) {
                                Icon(Icons.Default.Send, "Send")
                            }
                        }
                    }
                }
            }
        }
    }
}
