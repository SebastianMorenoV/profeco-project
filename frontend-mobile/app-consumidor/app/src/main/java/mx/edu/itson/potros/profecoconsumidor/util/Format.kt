package mx.edu.itson.potros.profecoconsumidor.util

import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

private val mxn = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

fun formatMXN(value: Double): String =
    mxn.format(if (value.isFinite()) value else 0.0)

private val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "MX"))

fun formatDate(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    return try {
        LocalDateTime.parse(raw).format(outputFormatter)
    } catch (e: DateTimeParseException) {
        try {
            LocalDate.parse(raw).format(outputFormatter)
        } catch (e2: DateTimeParseException) {
            raw
        }
    }
}
