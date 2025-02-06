package dev.niltptr

data class CurrencyResponse (
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)