package com.example.data

import androidx.room.TypeConverter
import com.example.model.CitizenType
import com.example.model.PaymentMethod
import com.example.model.TransactionCategory
import com.example.model.TransactionType

class Converters {
    @TypeConverter
    fun fromCitizenType(value: CitizenType?): String? = value?.name

    @TypeConverter
    fun toCitizenType(value: String?): CitizenType? = value?.let {
        try { CitizenType.valueOf(it) } catch (e: Exception) { CitizenType.WARGA_TETAP }
    }

    @TypeConverter
    fun fromTransactionType(value: TransactionType?): String? = value?.name

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? = value?.let {
        try { TransactionType.valueOf(it) } catch (e: Exception) { TransactionType.PEMASUKAN }
    }

    @TypeConverter
    fun fromTransactionCategory(value: TransactionCategory?): String? = value?.name

    @TypeConverter
    fun toTransactionCategory(value: String?): TransactionCategory? = value?.let {
        try { TransactionCategory.valueOf(it) } catch (e: Exception) { TransactionCategory.PEMASUKAN_LAINNYA }
    }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod?): String? = value?.name

    @TypeConverter
    fun toPaymentMethod(value: String?): PaymentMethod? = value?.let {
        try {
            PaymentMethod.valueOf(it)
        } catch (e: Exception) {
            when (it) {
                "TRANSFER_BANK" -> PaymentMethod.TRANSFER_BCA
                "QRIS_KAS_RT" -> PaymentMethod.QRIS_BCA
                else -> PaymentMethod.TUNAI
            }
        }
    }
}
