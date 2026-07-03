package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class RuvionRepository(private val db: AppDatabase) {

    val allBusinesses: Flow<List<BusinessEntity>> = db.businessDao().getAllBusinesses()
    val allProducts: Flow<List<ProductEntity>> = db.productDao().getAllProducts()
    val allCustomers: Flow<List<CustomerEntity>> = db.customerDao().getAllCustomers()
    val allTransactions: Flow<List<TransactionEntity>> = db.transactionDao().getAllTransactions()
    val allExpenses: Flow<List<ExpenseEntity>> = db.expenseDao().getAllExpenses()
    val allEmployees: Flow<List<EmployeeEntity>> = db.employeeDao().getAllEmployees()

    // Business mutators
    suspend fun insertBusiness(business: BusinessEntity) = db.businessDao().insertBusiness(business)
    suspend fun updateBusiness(business: BusinessEntity) = db.businessDao().updateBusiness(business)
    suspend fun deleteBusiness(business: BusinessEntity) = db.businessDao().deleteBusiness(business)

    // Product mutators
    suspend fun insertProduct(product: ProductEntity) = db.productDao().insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = db.productDao().updateProduct(product)
    suspend fun deleteProductById(id: Int) = db.productDao().deleteProductById(id)
    suspend fun getProductByBarcode(barcode: String): ProductEntity? = db.productDao().getProductByBarcode(barcode)
    fun searchProducts(query: String): Flow<List<ProductEntity>> = db.productDao().searchProducts(query)

    // Customer mutators
    suspend fun insertCustomer(customer: CustomerEntity) = db.customerDao().insertCustomer(customer)
    suspend fun updateCustomer(customer: CustomerEntity) = db.customerDao().updateCustomer(customer)
    suspend fun deleteCustomerById(id: Int) = db.customerDao().deleteCustomerById(id)
    fun getCustomersByType(type: String): Flow<List<CustomerEntity>> = db.customerDao().getCustomersByType(type)

    // Transaction mutators
    suspend fun insertTransaction(transaction: TransactionEntity) = db.transactionDao().insertTransaction(transaction)
    suspend fun updateTransaction(transaction: TransactionEntity) = db.transactionDao().updateTransaction(transaction)
    suspend fun deleteTransactionById(id: Int) = db.transactionDao().deleteTransactionById(id)
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>> = db.transactionDao().getTransactionsByType(type)

    // Expense mutators
    suspend fun insertExpense(expense: ExpenseEntity) = db.expenseDao().insertExpense(expense)
    suspend fun updateExpense(expense: ExpenseEntity) = db.expenseDao().updateExpense(expense)
    suspend fun deleteExpenseById(id: Int) = db.expenseDao().deleteExpenseById(id)

    // Employee mutators
    suspend fun insertEmployee(employee: EmployeeEntity) = db.employeeDao().insertEmployee(employee)
    suspend fun updateEmployee(employee: EmployeeEntity) = db.employeeDao().updateEmployee(employee)
    suspend fun deleteEmployeeById(id: Int) = db.employeeDao().deleteEmployeeById(id)

    // Prepopulate database with gorgeous sample data
    suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        val currentBusinesses = allBusinesses.first()
        if (currentBusinesses.isNotEmpty()) return@withContext // Database already populated

        // 1. Prepopulate default active business (Ruvion Smart Store)
        val defaultBusiness = BusinessEntity(
            name = "Ruvion Tech Retail",
            tagline = "Smart Business. Simplified.",
            ownerName = "Rahul Edition",
            phone = "+91 99999 88888",
            email = "support@ruvion.com",
            address = "Sector 62, Noida, Delhi NCR, India",
            gstNumber = "09AAAAA1111A1Z1",
            subscriptionPlan = "Enterprise"
        )
        insertBusiness(defaultBusiness)

        // 2. Prepopulate premium products
        val sampleProducts = listOf(
            ProductEntity(name = "Apple iPhone 15 Pro Max", category = "Electronics", brand = "Apple", sku = "APP-IP15PM-256", barcode = "195949033331", purchasePrice = 110000.0, sellingPrice = 139900.0, gstPercent = 18, stockQty = 12, minStock = 3),
            ProductEntity(name = "Samsung Galaxy S24 Ultra", category = "Electronics", brand = "Samsung", sku = "SAM-S24U-512", barcode = "880609530000", purchasePrice = 105000.0, sellingPrice = 129999.0, gstPercent = 18, stockQty = 8, minStock = 2),
            ProductEntity(name = "Sony WH-1000XM5 Headphones", category = "Accessories", brand = "Sony", sku = "SON-XM5-W", barcode = "027242922222", purchasePrice = 19000.0, sellingPrice = 29990.0, gstPercent = 18, stockQty = 15, minStock = 4),
            ProductEntity(name = "Nike Air Max Running Shoes", category = "Garments", brand = "Nike", sku = "NKE-AM-10", barcode = "196151011111", purchasePrice = 6500.0, sellingPrice = 11995.0, gstPercent = 12, stockQty = 25, minStock = 5),
            ProductEntity(name = "Levi's 511 Slim Fit Jeans", category = "Garments", brand = "Levi's", sku = "LEV-511-32", barcode = "193153022222", purchasePrice = 1500.0, sellingPrice = 3499.0, gstPercent = 12, stockQty = 40, minStock = 8),
            ProductEntity(name = "Crocin Pain Relief Tablet", category = "Medical Store", brand = "GSK", sku = "MED-CR-15", barcode = "890123456789", purchasePrice = 18.0, sellingPrice = 32.5, gstPercent = 5, stockQty = 120, minStock = 20, expiryDate = "2027-12-31"),
            ProductEntity(name = "Dolo 650 MG Tablet", category = "Medical Store", brand = "Micro Labs", sku = "MED-DL-650", barcode = "890291200112", purchasePrice = 15.0, sellingPrice = 30.0, gstPercent = 5, stockQty = 200, minStock = 30, expiryDate = "2027-08-30"),
            ProductEntity(name = "Amul Gold Milk 1L", category = "Supermarket", brand = "Amul", sku = "GRO-AM-MILK", barcode = "890126201004", purchasePrice = 58.0, sellingPrice = 66.0, gstPercent = 0, stockQty = 4, minStock = 10), // Low stock alert!
            ProductEntity(name = "Fortune Soyabean Oil 1L", category = "Supermarket", brand = "Fortune", sku = "GRO-FR-OIL", barcode = "890600728203", purchasePrice = 120.0, sellingPrice = 145.0, gstPercent = 5, stockQty = 3, minStock = 8) // Low stock alert!
        )
        for (prod in sampleProducts) {
            insertProduct(prod)
        }

        // 3. Prepopulate customers and suppliers
        val sampleCustomers = listOf(
            CustomerEntity(name = "Anjali Sharma", phone = "+91 98111 22222", email = "anjali@gmail.com", address = "Connaught Place, New Delhi", type = "CUSTOMER", outstandingBalance = 4500.0, creditLimit = 15000.0, loyaltyPoints = 350, notes = "Premium customer, prefers SMS reminders"),
            CustomerEntity(name = "Vikram Singh", phone = "+91 98222 33333", email = "vikram@gmail.com", address = "Gurugram, Haryana", type = "CUSTOMER", outstandingBalance = 0.0, creditLimit = 20000.0, loyaltyPoints = 120, notes = "Pays promptly via UPI"),
            CustomerEntity(name = "Rajesh Gupta", phone = "+91 93111 44444", email = "gupta.store@yahoo.com", address = "Chandni Chowk, Delhi", type = "SUPPLIER", outstandingBalance = -24500.0, creditLimit = 50000.0, notes = "Primary wholesale supplier for garments"),
            CustomerEntity(name = "Medicos Distributor", phone = "+91 93555 66666", email = "orders@medicos.com", address = "Daryaganj, Delhi", type = "SUPPLIER", outstandingBalance = -12000.0, creditLimit = 30000.0, notes = "Pharma distributor, 30 days credit cycle")
        )
        for (cust in sampleCustomers) {
            insertCustomer(cust)
        }

        // 4. Prepopulate sample transactions (Sales and Purchases)
        val sampleTransactions = listOf(
            TransactionEntity(
                invoiceNumber = "INV-2026-001",
                customerName = "Anjali Sharma",
                customerPhone = "+91 98111 22222",
                totalAmount = 33489.0,
                discount = 1500.0,
                tax = 5098.3,
                paymentMethod = "UPI",
                timestamp = System.currentTimeMillis() - 86400000 * 2, // 2 days ago
                type = "SALE",
                itemsJson = """[{"name":"Sony WH-1000XM5 Headphones","qty":1,"price":29990.0,"gst":18}]"""
            ),
            TransactionEntity(
                invoiceNumber = "INV-2026-002",
                customerName = "Vikram Singh",
                customerPhone = "+91 98222 33333",
                totalAmount = 6998.0,
                discount = 500.0,
                tax = 749.7,
                paymentMethod = "CASH",
                timestamp = System.currentTimeMillis() - 86400000 * 1, // 1 day ago
                type = "SALE",
                itemsJson = """[{"name":"Levi's 511 Slim Fit Jeans","qty":2,"price":3499.0,"gst":12}]"""
            ),
            TransactionEntity(
                invoiceNumber = "PUR-2026-001",
                customerName = "Rajesh Gupta",
                totalAmount = 45000.0,
                discount = 2000.0,
                tax = 5400.0,
                paymentMethod = "CREDIT",
                timestamp = System.currentTimeMillis() - 86400000 * 5, // 5 days ago
                type = "PURCHASE",
                itemsJson = """[{"name":"Stock Garments Lot","qty":1,"price":45000.0,"gst":12}]"""
            )
        )
        for (trans in sampleTransactions) {
            insertTransaction(trans)
        }

        // 5. Prepopulate default expenses
        val sampleExpenses = listOf(
            ExpenseEntity(category = "Rent", amount = 12000.0, description = "July Shop Rent Payment", timestamp = System.currentTimeMillis() - 86400000 * 4, isRecurring = true),
            ExpenseEntity(category = "Electricity", amount = 4250.0, description = "Electricity bill for June", timestamp = System.currentTimeMillis() - 86400000 * 3, isRecurring = true),
            ExpenseEntity(category = "Fuel", amount = 1200.0, description = "Delivery bike fuel refill", timestamp = System.currentTimeMillis() - 86400000 * 1, isRecurring = false),
            ExpenseEntity(category = "Salary", amount = 15000.0, description = "Salary paid to Staff Rohan", timestamp = System.currentTimeMillis() - 86400000 * 6, isRecurring = true)
        )
        for (exp in sampleExpenses) {
            insertExpense(exp)
        }

        // 6. Prepopulate employees
        val sampleEmployees = listOf(
            EmployeeEntity(name = "Rohan Kumar", phone = "+91 95555 12345", salary = 15000.0, role = "Staff", attendanceCount = 24),
            EmployeeEntity(name = "Priya Mehta", phone = "+91 95555 54321", salary = 28000.0, role = "Manager", attendanceCount = 26)
        )
        for (emp in sampleEmployees) {
            insertEmployee(emp)
        }
    }
}
