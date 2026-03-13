package com.fibu.logic

import androidx.compose.ui.graphics.Color
import kotlin.math.floor
import kotlin.math.roundToInt

//region Color <-> Hex
fun Color.toHex(): String {
  return "#${intToHex((this.red*255).roundToInt())}${intToHex((this.green*255).roundToInt())}${intToHex((this.blue*255).roundToInt())}"
}
fun Color.Companion.hexToColor(hexString: String): Color {
  val hex = if (hexString.startsWith("#")) hexString.substring(1) else hexString
  if (hex.length != 6) {
    throw IllegalArgumentException("Invalid hexadecimal color string length")
  }
  val red = hexCharToInt(hex[0]) * 16 + hexCharToInt(hex[1])
  val green = hexCharToInt(hex[2]) * 16 + hexCharToInt(hex[3])
  val blue = hexCharToInt(hex[4]) * 16 + hexCharToInt(hex[5])
  return Color(red, green, blue)
}

private fun hexCharToInt(hex: Char): Int {
  return when (hex) {
    in '0'..'9' -> hex - '0'
    in 'A'..'F' -> hex - 'A' + 10
    in 'a'..'f' -> hex - 'a' + 10
    else -> throw IllegalArgumentException("Invalid hexadecimal character: $hex")
  }
}
private fun intToHex(value: Int): String {
  val hexDigits = "0123456789ABCDEF"
  val highNibble = (value shr 4) and 0xF
  val lowNibble = value and 0xF
  return "${hexDigits[highNibble]}${hexDigits[lowNibble]}"
}
//endregion
//region Color <-> HSV
fun Color.toHSV(): Triple<Float, Float, Float> {
  val r = this.red
  val g = this.green
  val b = this.blue
  val max = maxOf(r, g, b)
  val min = minOf(r, g, b)
  val delta = max - min
  var h = when{
    delta == 0f -> 0f
    max == r -> (g - b) / delta + if(g < b) 6 else 0
    max == g -> (b - r) / delta + 2
    max == b -> (r - g) / delta + 4
    else -> 0f
  }
  h *= 60
//    h += if(h < 0f) 360f else 0f
  val s = if(max == 0f) 0f else delta / max
  return Triple(h, s, max)
}
fun Color.Companion.HSVToColor(h: Float, s: Float, v: Float): Color{

  val i = floor(h/60)
  val f = h/60 - i
  val p = v * (1 - s)
  val q = v * (1 - s * f)
  val t = v * (1 - s * (1 - f))

  val (rPrime, gPrime, bPrime) = when {
    h < 60 -> Triple(v, t, p)
    h < 120 -> Triple(q, v, p)
    h < 180 -> Triple(p, v, t)
    h < 240 -> Triple(p, q, v)
    h < 300 -> Triple(t, p, v)
    else -> Triple(v, p, q)
  }

  val r = (rPrime * 255).toInt()
  val g = (gPrime * 255).toInt()
  val b = (bPrime * 255).toInt()

  return Color(r, g, b)
}
//endregion

