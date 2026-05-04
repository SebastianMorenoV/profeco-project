package mx.edu.itson.potros.profecoconsumidor.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StarRating(
    value: Double,
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    onChange: ((Int) -> Unit)? = null
) {
    val total = 5
    val filledColor = Color(0xFFF5B301)
    val emptyColor = Color(0xFFD6D8E0)
    val redondeado = value.toInt().coerceIn(0, total)
    Row(modifier = modifier) {
        for (i in 1..total) {
            val activa = i <= redondeado || (onChange == null && i.toDouble() <= value)
            val mod = if (onChange != null) Modifier.clickable { onChange(i) } else Modifier
            Icon(
                imageVector = if (activa) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = if (activa) filledColor else emptyColor,
                modifier = mod.size(size)
            )
        }
    }
}
