package com.study.prepshala.Utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.study.prepshala.R
import com.study.prepshala.fullscreenImageDisplay.FullscreenDisplay
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

fun displayImage(context: Context, imageUrl: String?, personName: String? = "") {
    val inflater  = LayoutInflater.from(context)
    val view = inflater.inflate(R.layout.display_image, null)
    val displayImage = view.findViewById<ImageView>(R.id.displayImage)
    val addDialog = AlertDialog.Builder(context)
    addDialog.setView(view)
    Glide.with(context).load(imageUrl).placeholder(R.drawable.avatar).into(displayImage)
    addDialog.create()
    addDialog.show()
    displayImage.setOnClickListener {
        val intent = Intent(context, FullscreenDisplay::class.java)
        intent.putExtra("imageUrl", imageUrl)
        intent.putExtra("personName", personName)
        context.startActivity(intent)
    }
}
