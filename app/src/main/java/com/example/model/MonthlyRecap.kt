package com.example.model

data class CategoryBreakdown(
    val category: TransactionCategory,
    val totalAmount: Long,
    val percentage: Float,
    val count: Int
)

data class MonthlyRecap(
    val month: Int,
    val year: Int,
    val startingBalance: Long,
    val totalIncome: Long,
    val totalExpense: Long,
    val netBalance: Long,
    val endingBalance: Long,
    val totalCitizens: Int,
    val paidCitizensCount: Int,
    val unpaidCitizensCount: Int,
    val complianceRate: Float, // 0.0 to 100.0%
    val incomeCategories: List<CategoryBreakdown>,
    val expenseCategories: List<CategoryBreakdown>,
    val transactions: List<TransactionEntity>,
    val isApprovedByKetua: Boolean = false,
    val approvedAtMillis: Long? = null,
    val approvalNotes: String = ""
) {
    val monthName: String
        get() = when (month) {
            1 -> "Januari"
            2 -> "Februari"
            3 -> "Maret"
            4 -> "April"
            5 -> "Mei"
            6 -> "Juni"
            7 -> "Juli"
            8 -> "Agustus"
            9 -> "September"
            10 -> "Oktober"
            11 -> "November"
            12 -> "Desember"
            else -> "Bulan $month"
        }
}

data class PettyCashRecap(
    val month: Int,
    val year: Int,
    val startingBalance: Long,
    val totalTopUp: Long,
    val totalDisbursement: Long,
    val netFluctuation: Long,
    val endingBalance: Long,
    val totalVouchers: Int,
    val expenseCategoryBreakdowns: List<CategoryBreakdown>,
    val transactions: List<TransactionEntity>,
    val methodType: String = "Kas Kecil Operasional RT",
    val custodianName: String = "Prihatini Endah Yulia M. (Bendahara Kasir)",
    val approverName: String = "Nohan Pancono (Ketua RT 004)"
) {
    val monthName: String
        get() = when (month) {
            1 -> "Januari"
            2 -> "Februari"
            3 -> "Maret"
            4 -> "April"
            5 -> "Mei"
            6 -> "Juni"
            7 -> "Juli"
            8 -> "Agustus"
            9 -> "September"
            10 -> "Oktober"
            11 -> "November"
            12 -> "Desember"
            else -> "Bulan $month"
        }
}
