package com.fibu.ui.utility

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.fibu.theme.FiBuTheme
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    data: DonutChartData,
    chartSize: Dp,
    onClick: (Int) -> Unit
) {
    val radius = with(LocalDensity.current){ chartSize.toPx()} / 2
    val strokeWidth = radius * 0.15f
    var circleCenter by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(chartSize)
                .pointerInput(data) {
                    detectTapGestures(
                        onTap = { offset ->
                            if (data.items.isNotEmpty()) {
                                val width = size.width
                                val height = size.height
                                val tapCoordinates =
                                    Coordinates(offset.x - width / 2, offset.y - height / 2)
                                val tapDistance = sqrt(tapCoordinates.x.pow(2) + tapCoordinates.y.pow(2))
                                if(tapDistance > radius - strokeWidth*2 && tapDistance < radius){
                                    val tapAngle = (-atan2(
                                        x = (circleCenter.y - offset.y).toDouble(),
                                        y = (circleCenter.x - offset.x).toDouble()
                                    ) * (180f / PI).toFloat() - 90f).mod(360f)
                                    onClick(data.findClickedItemIndex(tapAngle.toFloat()))
                                }
                            }
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            circleCenter = Offset(width/2,height/2)
            var currentAngle = 0f
            if(data.items.isEmpty()){
                drawArc(
                    color = Color.Gray,
                    startAngle = currentAngle,
                    sweepAngle = 360f,
                    useCenter = false,
                    size = Size(
                        width = (radius - strokeWidth) * 2,
                        height = (radius - strokeWidth) * 2
                    ),
                    style = Stroke(
                        width = strokeWidth
                    ),
                    topLeft = Offset(
                        strokeWidth,
                        strokeWidth
                    )
                )
            } else {
                data.items.forEachIndexed { index, donutChartItem ->
                    val sweepAngle = data.calculateSweepAngle(index)
                    drawArc(
                        color = donutChartItem.color,
                        startAngle = currentAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        size = Size(
                            width = (radius - strokeWidth) * 2,
                            height = (radius - strokeWidth) * 2
                        ),
                        style = Stroke(
                            width = strokeWidth
                        ),
                        topLeft = Offset(
                            strokeWidth,
                            strokeWidth
                        )
                    )
                    currentAngle += sweepAngle
                }
            }
        }
        Text(
            text = "Total: ${String.format("%.2f", data.totalValue)}$",
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = FiBuTheme.contentColors.primary
        )
    }
}
data class DonutChartItem(
    val value: BigDecimal,
    val color: Color
)
data class DonutChartData(
    var items: List<DonutChartItem> = listOf()
) {
    internal var totalValue: BigDecimal = items.sumOf { it.value }
}
private fun DonutChartData.calculateSweepAngle(
    index: Int,
): Float {
    val value = items[index].value
    return value.divide(this.totalValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal(360)).toFloat()
}
// Function is bad will need to find an alternative way to find clicked item
private fun DonutChartData.findClickedItemIndex(tapAngle: Float): Int{
    val angles: MutableList<Pair<Float, Float>> = mutableListOf()
    var currentAngle = 0f
    var clickedItemIndex = 0
    items.forEachIndexed{ index, _ ->
        val sweepAngle = this.calculateSweepAngle(index)
        angles.add(Pair(currentAngle, currentAngle + sweepAngle))
        currentAngle += sweepAngle
    }
    //Stupidest break; I have ever seen in programming should probably use a for loop here :P
    run breaking@{
        angles.forEachIndexed { index, anglePair ->
            if (tapAngle in anglePair.first..anglePair.second) {
                clickedItemIndex = index
                return@breaking
            }
        }
    }
    return clickedItemIndex
}
private data class Coordinates(
    val x: Float,
    val y: Float
)