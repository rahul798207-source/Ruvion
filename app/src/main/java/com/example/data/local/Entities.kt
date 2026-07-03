package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "businesses")
data class BusinessEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val tagline: String = "Smart Business. Simplified.",
    val ownerName: String = "Rahul",
    val phone: String = "+91 98765 43210",
    val email: String = "rahul798207@gmail.com",
    val address: String = "Delhi, India",
    val gstNumber: String = "07AAAAA0000A1Z5",
    val subscriptionPlan: String = "Professional", // Free, Starter, Professional, Business, Enterprise
    val logoUrl: String = ""
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val brand: String = "",
    val sku: String = "",
    val barcode: String = "",
    val purchasePrice: Double,
    val sellingPrice: Double,
    val gstPercent: Int = 18,
    val stockQty: Int,
    val minStock: Int = 5,
    val expiryDate: String = "" // YYYY-MM-DD
)

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val email: String = "",
    val address: String = "",
    val gstNumber: String = "",
    val type: String = "CUSTOMER", // CUSTOMER, SUPPLIER
    val outstandingBalance: Double = 0.0, // positive means customer owes us money, negative means we owe supplier
    val creditLimit: Double = 10000.0,
    val loyaltyPoints: Int = 0,
    val notes: String = ""
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val invoiceNumber: String,
    val customerName: String,
    val customerPhone: String = "",
    val totalAmount: Double,
    val discount: Double = 0.0,
    val tax: Double = 0.0,
    val paymentMethod: String = "CASH", // CASH, UPI, CARD, SPLIT, CREDIT
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "SALE", // SALE, PURCHASE, RETURN
    val itemsJson: String // Serialized array of items in JSON format
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // Rent, Salary, Fuel, Electricity, Maintenance, Transport, Miscellaneous
    val amount: Double,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRecurring: Boolean = false
)

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val salary: Double = 15000.0,
    val role: String = "Staff", // Staff, Manager
    val attendanceCount: Int = 0,
    val permissionsJson: String = "" // Permissions configuration in JSON
)
