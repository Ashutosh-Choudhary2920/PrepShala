package com.study.prepshala.Utils

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.time.LocalDateTime
import java.time.LocalTime


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun logD(message: String, tag: String = "TAG!!!!") {
    Log.d(tag, message)
}

fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun closeKeyboard(context: Context, currentFocus: View?) {
    if (currentFocus != null) {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDate(): String {
    val currentDateTime = LocalDateTime.now()
    return currentDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentTime(): String {
    val currentTime = LocalTime.now()
    return currentTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
}

private const val DATE_FORMAT_1 = "dd MMMM yyyy"
fun getTimestamp(dateString: String?): Long {
    try {
        val date =
            SimpleDateFormat(DATE_FORMAT_1, Locale.ENGLISH)
                .parse(dateString)
        return date.time
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return 0
}

fun getDate(time: Long): String {
    val date = Date(time)
    val format: DateFormat =
        SimpleDateFormat(DATE_FORMAT_1, Locale.getDefault())
    format.timeZone = TimeZone.getDefault()
    return format.format(date)
}
