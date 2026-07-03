package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.RetrofitClient
import com.example.data.local.*
import com.example.data.repository.RuvionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartItem(
    val product: ProductEntity,
    val quantity: Int
)

class RuvionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = RuvionRepository(db)

    // User Role management (dynamic switching for demonstration of enterprise capabilities)
    private val _activeRole = MutableStateFlow("Business Owner") // Super Admin, Business Owner, Staff
    val activeRole: StateFlow<String> = _activeRole.asStateFlow()

    // Active Business
    val allBusinesses = repository.allBusinesses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activeBusiness = allBusinesses.map { list ->
        list.firstOrNull() ?: BusinessEntity(name = "Ruvion Tech Retail", tagline = "Smart Business. Simplified.")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BusinessEntity(name = "Ruvion Tech Retail", tagline = "Smart Business. Simplified."))

    // Lists from Room
    val allProducts = repository.allProducts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allCustomers = repository.allCustomers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allTransactions = repository.allTransactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allExpenses = repository.allExpenses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allEmployees = repository.allEmployees.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Lists
    val suppliersList = allCustomers.map { list -> list.filter { it.type == "SUPPLIER" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val customersList = allCustomers.map { list -> list.filter { it.type == "CUSTOMER" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Product search queries and filtering
    private val _productSearchQuery = MutableStateFlow("")
    val productSearchQuery = _productSearchQuery.asStateFlow()

    val searchedProducts = combine(allProducts, _productSearchQuery) { products, query ->
        if (query.isEmpty()) {
            products
        } else {
            products.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                it.barcode == query ||
                it.sku.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // POS Billing Cart State
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _selectedCartCustomer = MutableStateFlow<CustomerEntity?>(null)
    val selectedCartCustomer = _selectedCartCustomer.asStateFlow()

    private val _cartDiscount = MutableStateFlow(0.0)
    val cartDiscount = _cartDiscount.asStateFlow()

    private val _paymentMethod = MutableStateFlow("CASH") // CASH, UPI, CARD, SPLIT, CREDIT
    val paymentMethod = _paymentMethod.asStateFlow()

    // AI States
    private val _aiForecastText = MutableStateFlow("Click 'Generate AI Forecast' to analyze your shop's performance.")
    val aiForecastText: StateFlow<String> = _aiForecastText.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // AI Chat Bot
    private val _chatHistory = MutableStateFlow<List<Pair<String, Boolean>>>(listOf(
        Pair("Hello! I am Ruvion AI, your smart business companion. Ask me anything about your products, sales forecasts, or accounting entries!", false)
    )) // Pair of (Message, isUser)
    val chatHistory: StateFlow<List<Pair<String, Boolean>>> = _chatHistory.asStateFlow()

    init {
        // Automatically pre-populate database on startup
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    // Role Switching
    fun setActiveRole(role: String) {
        _activeRole.value = role
    }

    // Product CRUD
    fun addProduct(name: String, category: String, brand: String, sku: String, barcode: String, purchasePrice: Double, sellingPrice: Double, gstPercent: Int, stockQty: Int, minStock: Int, expiryDate: String) {
        viewModelScope.launch {
            repository.insertProduct(
                ProductEntity(
                    name = name,
                    category = category,
                    brand = brand,
                    sku = sku,
                    barcode = barcode,
                    purchasePrice = purchasePrice,
                    sellingPrice = sellingPrice,
                    gstPercent = gstPercent,
                    stockQty = stockQty,
                    minStock = minStock,
                    expiryDate = expiryDate
                )
            )
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            repository.deleteProductById(id)
        }
    }

    fun setProductSearchQuery(query: String) {
        _productSearchQuery.value = query
    }

    // Customer CRUD
    fun addCustomer(name: String, phone: String, email: String, address: String, gstNumber: String, type: String, outstandingBalance: Double, creditLimit: Double, loyaltyPoints: Int, notes: String) {
        viewModelScope.launch {
            repository.insertCustomer(
                CustomerEntity(
                    name = name,
                    phone = phone,
                    email = email,
                    address = address,
                    gstNumber = gstNumber,
                    type = type,
                    outstandingBalance = outstandingBalance,
                    creditLimit = creditLimit,
                    loyaltyPoints = loyaltyPoints,
                    notes = notes
                )
            )
        }
    }

    fun updateCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
        }
    }

    fun deleteCustomer(id: Int) {
        viewModelScope.launch {
            repository.deleteCustomerById(id)
        }
    }

    // Expense CRUD
    fun addExpense(category: String, amount: Double, description: String, isRecurring: Boolean) {
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(
                    category = category,
                    amount = amount,
                    description = description,
                    timestamp = System.currentTimeMillis(),
                    isRecurring = isRecurring
                )
            )
        }
    }

    fun deleteExpense(id: Int) {
        viewModelScope.launch {
            repository.deleteExpenseById(id)
        }
    }

    // Employee CRUD
    fun addEmployee(name: String, phone: String, salary: Double, role: String, attendanceCount: Int) {
        viewModelScope.launch {
            repository.insertEmployee(
                EmployeeEntity(
                    name = name,
                    phone = phone,
                    salary = salary,
                    role = role,
                    attendanceCount = attendanceCount
                )
            )
        }
    }

    fun deleteEmployee(id: Int) {
        viewModelScope.launch {
            repository.deleteEmployeeById(id)
        }
    }

    fun updateEmployee(employee: EmployeeEntity) {
        viewModelScope.launch {
            repository.updateEmployee(employee)
        }
    }

    // Business Settings Update
    fun updateBusinessSettings(name: String, tagline: String, ownerName: String, phone: String, email: String, address: String, gstNumber: String, subscriptionPlan: String) {
        viewModelScope.launch {
            val current = activeBusiness.value
            repository.updateBusiness(
                current.copy(
                    name = name,
                    tagline = tagline,
                    ownerName = ownerName,
                    phone = phone,
                    email = email,
                    address = address,
                    gstNumber = gstNumber,
                    subscriptionPlan = subscriptionPlan
                )
            )
        }
    }

    // POS Billing Actions
    fun addProductToCart(product: ProductEntity) {
        val currentCart = _cart.value.toMutableList()
        val index = currentCart.indexOfFirst { it.product.id == product.id }
        if (index != -1) {
            currentCart[index] = currentCart[index].copy(quantity = currentCart[index].quantity + 1)
        } else {
            currentCart.add(CartItem(product, 1))
        }
        _cart.value = currentCart
    }

    fun removeProductFromCart(product: ProductEntity) {
        val currentCart = _cart.value.toMutableList()
        val index = currentCart.indexOfFirst { it.product.id == product.id }
        if (index != -1) {
            if (currentCart[index].quantity > 1) {
                currentCart[index] = currentCart[index].copy(quantity = currentCart[index].quantity - 1)
            } else {
                currentCart.removeAt(index)
            }
        }
        _cart.value = currentCart
    }

    fun clearCart() {
        _cart.value = emptyList()
        _selectedCartCustomer.value = null
        _cartDiscount.value = 0.0
        _paymentMethod.value = "CASH"
    }

    fun selectCartCustomer(customer: CustomerEntity?) {
        _selectedCartCustomer.value = customer
    }

    fun setCartDiscount(discount: Double) {
        _cartDiscount.value = discount
    }

    fun setPaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    fun checkoutCart() {
        val currentCart = _cart.value
        if (currentCart.isEmpty()) return

        viewModelScope.launch {
            val totalBeforeDiscount = currentCart.sumOf { it.product.sellingPrice * it.quantity }
            val discount = _cartDiscount.value
            val totalAmount = (totalBeforeDiscount - discount).coerceAtLeast(0.0)
            val calculatedTax = currentCart.sumOf { (it.product.sellingPrice * it.quantity * it.product.gstPercent) / 100.0 }

            val itemsString = currentCart.map {
                """{"name":"${it.product.name}","qty":${it.quantity},"price":${it.product.sellingPrice},"gst":${it.product.gstPercent}}"""
            }.joinToString(prefix = "[", postfix = "]")

            val invoiceNo = "INV-2026-${(1000..9999).random()}"
            val customerName = _selectedCartCustomer.value?.name ?: "Walk-in Customer"
            val customerPhone = _selectedCartCustomer.value?.phone ?: ""

            // Save POS bill transaction
            val transaction = TransactionEntity(
                invoiceNumber = invoiceNo,
                customerName = customerName,
                customerPhone = customerPhone,
                totalAmount = totalAmount,
                discount = discount,
                tax = calculatedTax,
                paymentMethod = _paymentMethod.value,
                timestamp = System.currentTimeMillis(),
                type = "SALE",
                itemsJson = itemsString
            )
            repository.insertTransaction(transaction)

            // Deduct inventory stock
            for (item in currentCart) {
                val updatedProduct = item.product.copy(
                    stockQty = (item.product.stockQty - item.quantity).coerceAtLeast(0)
                )
                repository.updateProduct(updatedProduct)
            }

            // If credit sale, add to customer outstanding balance (Digital Udhaar)
            if (_paymentMethod.value == "CREDIT" && _selectedCartCustomer.value != null) {
                val customer = _selectedCartCustomer.value!!
                val updatedCustomer = customer.copy(
                    outstandingBalance = customer.outstandingBalance + totalAmount,
                    loyaltyPoints = customer.loyaltyPoints + (totalAmount / 100).toInt()
                )
                repository.updateCustomer(updatedCustomer)
            } else if (_selectedCartCustomer.value != null) {
                val customer = _selectedCartCustomer.value!!
                val updatedCustomer = customer.copy(
                    loyaltyPoints = customer.loyaltyPoints + (totalAmount / 100).toInt()
                )
                repository.updateCustomer(updatedCustomer)
            }

            // Clear Cart after successful POS Checkout
            clearCart()
        }
    }

    // Receive Digital Udhaar Payment
    fun receiveUdhaarPayment(customer: CustomerEntity, amount: Double) {
        viewModelScope.launch {
            val updatedCustomer = customer.copy(
                outstandingBalance = (customer.outstandingBalance - amount).coerceAtLeast(0.0)
            )
            repository.updateCustomer(updatedCustomer)

            // Record transaction for payment received
            val transaction = TransactionEntity(
                invoiceNumber = "PAY-2026-${(1000..9999).random()}",
                customerName = customer.name,
                customerPhone = customer.phone,
                totalAmount = amount,
                paymentMethod = "CASH",
                timestamp = System.currentTimeMillis(),
                type = "SALE", // Or separate type
                itemsJson = """[{"name":"Udhaar Payment Received","qty":1,"price":$amount,"gst":0}]"""
            )
            repository.insertTransaction(transaction)
        }
    }

    // AI Forecasting & Predictions (GEMINI INTEGRATION)
    fun generateAiForecast() {
        _isAiLoading.value = true
        _aiForecastText.value = "Ruvion AI is analyzing your store's transactions, products inventory, and expenses data..."

        viewModelScope.launch {
            // Compile stats for Gemini
            val products = allProducts.value
            val transactions = allTransactions.value
            val expenses = allExpenses.value

            val totalSales = transactions.filter { it.type == "SALE" }.sumOf { it.totalAmount }
            val totalProfit = transactions.filter { it.type == "SALE" }.sumOf { it.totalAmount - (it.discount) } * 0.25 // Assumed margin
            val totalExpenses = expenses.sumOf { it.amount }
            val lowStockCount = products.filter { it.stockQty <= it.minStock }.size

            val prompt = """
                You are Ruvion AI, an enterprise-grade ERP/POS business intelligence agent.
                Analyze the following business metrics for Rahul Edition's store and provide a structured, premium 3-month forecast:
                - Active Business Name: ${activeBusiness.value.name}
                - Total Products in Database: ${products.size}
                - Total Historical Sales Volume: ₹$totalSales
                - Simulated Gross Profit (Estimated Margin): ₹$totalProfit
                - Total Operational Expenses recorded: ₹$totalExpenses
                - Number of Low Stock Alerts: $lowStockCount

                Please output your answer in 4 parts:
                1. 📈 AI SALES FORECAST: Estimate sales for the next 3 months based on current sales volume.
                2. 💰 AI PROFIT PREDICTION: Predict net profit margins with expense-reduction suggestions.
                3. 📉 OPERATIONAL EXPENSE ANALYSIS: Highlight potential leaks or areas to save.
                4. 🔔 STRATEGIC INVENTORY RECOMMENDATIONS: Recommend restocks based on the low stock alerts.
                
                Keep the response highly professional, actionable, structured, and start with:
                "DEVELOPED BY RAHUL EDITION - BUSINESS INTELLIGENCE SYSTEM"
            """.trimIndent()

            val response = RetrofitClient.generateContent(prompt)
            _aiForecastText.value = response
            _isAiLoading.value = false
        }
    }

    // AI Chat Bot Conversation
    fun sendChatMessage(message: String) {
        if (message.isBlank()) return

        val currentHistory = _chatHistory.value.toMutableList()
        currentHistory.add(Pair(message, true))
        _chatHistory.value = currentHistory

        viewModelScope.launch {
            val systemPrompt = """
                You are Ruvion AI, an expert enterprise-grade AI Business assistant, developed by Rahul Edition.
                Answer queries about retail, inventory management, bookkeeping, POS, GST filing, and ERP.
                Be concise, structured, and extremely helpful. Always represent yourself as Ruvion AI, Developed by Rahul Edition.
            """.trimIndent()

            val aiResponse = RetrofitClient.generateContent(message, systemPrompt)
            val updatedHistory = _chatHistory.value.toMutableList()
            updatedHistory.add(Pair(aiResponse, false))
            _chatHistory.value = updatedHistory
        }
    }
}
