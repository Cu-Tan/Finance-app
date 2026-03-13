package com.fibu.logic
import java.util.Calendar
import kotlin.math.min

open class Date(
  var year: Int,
  var month: Int,
  var day: Int
) {

  companion object {
    fun now(): Date{
      val calendar = Calendar.getInstance()
      val year = calendar.get(Calendar.YEAR)
      val month = calendar.get(Calendar.MONTH) + 1
      val day = calendar.get(Calendar.DAY_OF_MONTH)
      return Date(year, month, day)
    }
  }

  val ymd_text: String
    get() = "${year} ${monthName} ${day}"
  val ym_text: String
    get() = "${year} ${monthName}"
  val monthName: String
    get() = monthNames[month - 1]
  val daysInMonth
    get() = if(month == 2 && checkLeapYear()) daysInMonths[month - 1] + 1
    else daysInMonths[month - 1]

  override fun toString(): String {
    return "${year.toString().padStart(4,'0')}-${month.toString().padStart(2,'0')}-${day.toString().padStart(2,'0')}"
  }
  fun equals(other: Date): Boolean {
    return (this.year == other.year && this.month == other.month && this.day == other.day)
  }
}
class DateTime(
  year: Int,
  month: Int,
  day: Int,
  var hour: Int,
  var minute: Int,
  var second: Int
) : Date (year, month, day) {

  companion object {
    fun zero(): DateTime {
      return DateTime(
        year = 0,
        month = 0,
        day  = 0,
        hour = 0,
        minute = 0,
        second = 0
      )
    }
    fun now(): DateTime {
      val calendar = Calendar.getInstance()
      val year = calendar.get(Calendar.YEAR)
      val month = calendar.get(Calendar.MONTH) + 1
      val day = calendar.get(Calendar.DAY_OF_MONTH)
      val hour = calendar.get(Calendar.HOUR_OF_DAY)
      val minute = calendar.get(Calendar.MINUTE)
      val second = calendar.get(Calendar.SECOND)
      return DateTime(year, month, day, hour, minute, second)
    }
  }

  fun toDateString() : String {
    return "${year.toString().padStart(4,'0')}-${month.toString().padStart(2,'0')}-${day.toString().padStart(2,'0')}"
  }
  fun toDate(): Date {
    return Date(
      year = year,
      month = month,
      day = day
    )
  }
  fun copy(): DateTime{
    return DateTime(year, month, day, hour, minute, second)
  }
  override fun toString(): String {
    return  "${year.toString().padStart(4,'0')}-" +
        "${month.toString().padStart(2,'0')}-" +
        "${day.toString().padStart(2,'0')} " +
        "${hour.toString().padStart(2,'0')}:" +
        "${minute.toString().padStart(2,'0')}:" +
        "${second.toString().padStart(2,'0')}"
  }
  val ymdhmText: String
    get() = "${year.toString().padStart(4,'0')}-" +
        "${month.toString().padStart(2,'0')}-" +
        "${day.toString().padStart(2,'0')} " +
        "${hour.toString().padStart(2,'0')}:" +
        "${minute.toString().padStart(2,'0')}"
  val timeText: String
    get() = "${hour.toString().padStart(2,'0')}:${minute.toString().padStart(2,'0')}"
  fun equals(other: DateTime): Boolean {
    return (
        this.year == other.year
            &&
            this.month == other.month
            &&
            this.day == other.day
            &&
            this.hour == other.hour
            &&
            this.minute == other.minute
            &&
            this.second == other.second
        )
  }
}
class DateRange(
  var startDate: Date,
  var endDate: Date
){

  companion object {
    fun now(): DateRange {
      val date = Date.now()
      return DateRange(date, date)
    }
  }

  val title: String
    get() = "${startDate.year} " +
        "${monthNames[startDate.month - 1]} " +
        "${startDate.day} " +
        "to " +
        "${endDate.year} " +
        "${monthNames[endDate.month - 1]} " +
        "${endDate.day}"
}
val daysInMonths = listOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
val monthNames = listOf("January", "February", "March", "April", "May", "June ", "July", "August ", "September", "October", "November", "December")
fun Date.checkLeapYear(): Boolean{
  return (this.year % 4 == 0 || this.year % 400 == 0 && this.year % 100 != 0)
}
fun Date.findDayOfTheWeek(): Int {
  var dayOfYear = 0
  for (i in 0 until (this.month-1) step 1){
    dayOfYear += if (i == 1 && checkLeapYear()){
      daysInMonths[i] + 1
    } else {
      daysInMonths[i]
    }
  }
  val leapCount = (this.year - 1) / 4
  val dayRemainder = (this.year - 1 ) % 4
  val nonLeapCount = (this.year - 1) / 100
  val weirdLeapCount = (this.year - 1) / 400
  val dayOfTheWeekIndex = (dayOfYear + (this.day - 1) + (leapCount * 5) + dayRemainder + weirdLeapCount - nonLeapCount) % 7
  return dayOfTheWeekIndex
}
fun Date.backMonth(){
  this.month -= 1
  if (this.month <= 0){
    this.year -= 1
    this.month = 12
  }
}
fun Date.forwardMonth(){
  this.month += 1
  if (this.month > 12){
    this.year += 1
    this.month = 1
  }
}

// Check for input errors because .split can crash
fun Date.Companion.convertStringToDateTime(dateTimeString: String): DateTime {
  val dateTimeParts = dateTimeString.split(" ")
  val datePart = dateTimeParts[0]
  val timePart = dateTimeParts[1]

  val dateParts = datePart.split("-")
  val year = dateParts[0].toInt()
  val month = dateParts[1].toInt()
  val day = dateParts[2].toInt()

  val timeParts = timePart.split(":")
  val hour = timeParts[0].toInt()
  val minute = timeParts[1].toInt()
  val second = timeParts[2].toInt()

  return DateTime(year, month, day, hour, minute, second)
}
