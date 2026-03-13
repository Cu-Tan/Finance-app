package com.fibu.ui.utility.color

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.drawable.BitmapDrawable
import kotlin.math.min

internal class HsvBitmapDrawable(
    resources: Resources,
    bitmap: Bitmap
) : BitmapDrawable(resources, bitmap) {
    private val huePaint = Paint()
    private val saturationPaint = Paint()

    override fun draw(canvas: Canvas) {
        val width = bounds.width()
        val height = bounds.height()
        val centerX = width * 0.5f
        val centerY = height * 0.5f
        val radius = min(width, height) * 0.5f

        val sweepShader = SweepGradient(
            centerX,
            centerY,
            intArrayOf(
                Color.RED,
                Color.YELLOW,
                Color.GREEN,
                Color.CYAN,
                Color.BLUE,
                Color.MAGENTA,
                Color.RED
            ),
            null
        )
        huePaint.shader = sweepShader
        val saturationShader = RadialGradient(
            centerX,
            centerY,
            radius,
            Color.WHITE,
            0x00FFFFFF,
            Shader.TileMode.CLAMP
        )
        saturationPaint.shader = saturationShader

        canvas.drawCircle(centerX, centerY, radius, huePaint)
        canvas.drawCircle(centerX, centerY, radius, saturationPaint)
    }
}