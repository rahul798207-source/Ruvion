package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.DevelopedByFooter
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ExpenseRed
import com.example.ui.viewmodel.RuvionViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val rViewModel: RuvionViewModel = viewModel()

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen(
                            onNavigateToAuth = {
                                navController.navigate("auth") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("auth") {
                        val activeRole by rViewModel.activeRole.collectAsState()
                        AuthScreens(
                            activeRole = activeRole,
                            onRoleSelected = { rViewModel.setActiveRole(it) },
                            onLoginSuccess = {
                                navController.navigate("main") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("main") {
                        MainLayout(viewModel = rViewModel, onLogout = {
                            navController.navigate("auth") {
                                popUpTo("main") { inclusive = true }
                            }
                        })
                    }
                }
            }
        }
    }
}

// Data holder for side drawer menu options
data class DrawerMenuItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val requiredRoles: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(viewModel: RuvionViewModel, onLogout: () -> Unit) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val activeRole by viewModel.activeRole.collectAsState()
    val activeBusiness by viewModel.activeBusiness.collectAsState()

    var activeScreenRoute by remember { mutableStateOf("dashboard") }

    // Navigation Drawer Menu items (Enterprise compliant)
    val menuItems = listOf(
        DrawerMenuItem("dashboard", "Dashboard Analytics", Icons.Default.Analytics),
        DrawerMenuItem("pos", "POS Billing Terminal", Icons.Default.PointOfSale),
        DrawerMenuItem("inventory", "Inventory Ledger", Icons.Default.Inventory),
        DrawerMenuItem("udhaar", "Digital Udhaar Book", Icons.Default.ImportContacts),
        DrawerMenuItem("expenses", "Expenses Book", Icons.Default.TrendingDown),
        DrawerMenuItem("employees", "Staff & Payroll", Icons.Default.People),
        DrawerMenuItem("reports", "GST & Tax Reports", Icons.Default.Assessment),
        DrawerMenuItem("ai_smart", "Ruvion AI Smart Hub", Icons.Default.AutoAwesome),
        DrawerMenuItem("super_admin", "Platform Admin Console", Icons.Default.Security, listOf("Super Admin")),
        DrawerMenuItem("settings", "Business Settings", Icons.Default.Settings)
    )

    // Filter menu options based on selected user roles
    val visibleMenuItems = menuItems.filter {
        it.requiredRoles.isEmpty() || it.requiredRoles.contains(activeRole)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Profile Banner in Drawer Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.BusinessCenter, "Store", tint = MaterialTheme.colorScheme.onPrimary)
                                }
                                Column {
                                    Text(
                                        text = activeBusiness.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Role: $activeRole",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Render visible navigation options
                        visibleMenuItems.forEach { item ->
                            val isSelected = activeScreenRoute == item.route
                            NavigationDrawerItem(
                                label = { Text(item.title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                selected = isSelected,
                                onClick = {
                                    activeScreenRoute = item.route
                                    coroutineScope.launch { drawerState.close() }
                                    Toast.makeText(context, "Navigated to ${item.title}", Toast.LENGTH_SHORT).show()
                                },
                                icon = { Icon(item.icon, contentDescription = item.title) },
                                modifier = Modifier.padding(vertical = 2.dp),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // App Drawer Footer attribution (Required!)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ruvion Enterprise Console\nApp Drawer Footer",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        DevelopedByFooter()
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "BUSINESS CONSOLE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = activeBusiness.name,
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open Drawer Menu")
                        }
                    },
                    actions = {
                        // Action buttons: Notifications, quick logout
                        IconButton(
                            onClick = {
                                Toast.makeText(context, "Ruvion Smart Notifications: 0 unresolved logs", Toast.LENGTH_LONG).show()
                            }
                        ) {
                            BadgedBox(
                                badge = { Badge { Text("0") } }
                            ) {
                                Icon(Icons.Default.Notifications, "Notifications")
                            }
                        }

                        IconButton(onClick = { onLogout() }) {
                            Icon(Icons.Default.Logout, "Log Out Securely", tint = ExpenseRed)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.navigationBarsPadding(),
                    windowInsets = WindowInsets.navigationBars
                ) {
                    val navigationItems = listOf(
                        Triple("Home", Icons.Default.Dashboard, "dashboard"),
                        Triple("POS Billing", Icons.Default.PointOfSale, "pos"),
                        Triple("Inventory", Icons.Default.Inventory, "inventory"),
                        Triple("Settings", Icons.Default.Settings, "settings")
                    )
                    navigationItems.forEach { (label, icon, route) ->
                        val isSelected = activeScreenRoute == route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { activeScreenRoute = route },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Screen Navigation Routing matching selections
                when (activeScreenRoute) {
                    "dashboard" -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToBilling = { activeScreenRoute = "pos" },
                        onNavigateToInventory = { activeScreenRoute = "inventory" },
                        onNavigateToExpenses = { activeScreenRoute = "expenses" },
                        onNavigateToUdhaar = { activeScreenRoute = "udhaar" }
                    )
                    "pos" -> POSBillingScreen(viewModel = viewModel)
                    "inventory" -> InventoryScreen(viewModel = viewModel)
                    "udhaar" -> UdhaarKhataScreen(viewModel = viewModel)
                    "expenses" -> ExpensesScreen(viewModel = viewModel)
                    "employees" -> EmployeesScreen(viewModel = viewModel)
                    "reports" -> ReportsScreen(viewModel = viewModel)
                    "ai_smart" -> AISmartScreen(viewModel = viewModel)
                    "super_admin" -> SuperAdminScreen(viewModel = viewModel)
                    "settings" -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
