package com.example.app.profile.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PaymentMethodStorage {
    private const val PREFS_NAME = "payment_methods_prefs"
    private const val KEY_PAYMENT_METHODS = "payment_methods"
    
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun savePaymentMethods(context: Context, methods: List<PaymentMethod>) {
        val prefs = getSharedPreferences(context)
        val json = Gson().toJson(methods)
        prefs.edit().putString(KEY_PAYMENT_METHODS, json).apply()
    }
    
    fun getPaymentMethods(context: Context): List<PaymentMethod> {
        val prefs = getSharedPreferences(context)
        val json = prefs.getString(KEY_PAYMENT_METHODS, null) ?: return emptyList()
        val type = object : TypeToken<List<PaymentMethod>>() {}.type
        return try {
            Gson().fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addPaymentMethod(context: Context, method: PaymentMethod) {
        val methods = getPaymentMethods(context).toMutableList()
        // If this is set as default, unset others
        if (method.isDefault) {
            methods.forEachIndexed { index, m ->
                methods[index] = m.copy(isDefault = false)
            }
        }
        val newId = if (method.id.isEmpty()) {
            System.currentTimeMillis().toString()
        } else {
            method.id
        }
        methods.add(method.copy(id = newId, isDefault = method.isDefault))
        savePaymentMethods(context, methods)
    }
    
    fun updatePaymentMethod(context: Context, method: PaymentMethod) {
        val methods = getPaymentMethods(context).toMutableList()
        val index = methods.indexOfFirst { it.id == method.id }
        if (index != -1) {
            // If this is set as default, unset others
            if (method.isDefault) {
                methods.forEachIndexed { i, m ->
                    if (i != index) {
                        methods[i] = m.copy(isDefault = false)
                    }
                }
            }
            methods[index] = method
            savePaymentMethods(context, methods)
        }
    }
    
    fun deletePaymentMethod(context: Context, methodId: String) {
        val methods = getPaymentMethods(context).toMutableList()
        methods.removeAll { it.id == methodId }
        savePaymentMethods(context, methods)
    }
    
    fun getDefaultPaymentMethod(context: Context): PaymentMethod? {
        return getPaymentMethods(context).find { it.isDefault }
    }
}

