package com.fibu.ui.utility

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fibu.theme.FiBuTheme
import kotlin.math.min

@Composable
fun ProgressBar(
    limit: Float,
    value: Float,
    mediumLimit: Float = limit * ProgressBarDefaults.MEDIUMLIMITRATIO,
    highLimit: Float = limit * ProgressBarDefaults.HIGHLIMITRATIO
) {
    Column {
        Canvas(
            modifier = Modifier
                .height(10.dp)
                .fillMaxWidth()
                .border(width = 2.dp, color = Color.Black, RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(FiBuTheme.contentColors.primary)
        ) {
            val stepWidth = 5F
            val stepCount = 2
            val stepDistance = listOf(
                size.width * (mediumLimit / limit),
                size.width * (highLimit / limit)
            )
            val progressDistance = min(size.width * (value / limit), size.width)
            drawRect(
                setProgressBarColor(value, mediumLimit, highLimit),
                topLeft = Offset(0F,0F),
                size = Size(
                    width = progressDistance,
                    height = size.height
                )
            )
            for (i in 0 until stepCount) {
                val rect = Rect(
                    offset = Offset(
                        x = stepDistance[i],
                        y = 0F
                    ),
                    size = Size(
                        width = stepWidth,
                        height = size.height
                    )
                )
                drawRect(
                    Color.Black,
                    topLeft = rect.topLeft,
                    size = rect.size
                )
            }

        }
    }
}
fun setProgressBarColor (
    value: Float,
    mediumLimit: Float,
    highLimit: Float,
): Color {
    if (value < mediumLimit){
        return Color.Green
    } else if (value in mediumLimit..highLimit) {
        return Color.Yellow
    }
    return Color.Red
}

object ProgressBarDefaults {
    const val MEDIUMLIMITRATIO: Float = 0.35F
    const val HIGHLIMITRATIO: Float = 0.75F
}