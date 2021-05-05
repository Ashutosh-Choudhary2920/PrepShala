package com.study.prepshala.pdfReader

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.study.prepshala.R
import com.study.prepshala.Utils.logD
import com.study.prepshala.Utils.toast
import com.study.prepshala.notes.NotesActivity
import kotlinx.android.synthetic.main.activity_pdf_view.*

class PdfViewActivity : AppCompatActivity() {

    companion object {
        const val path: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)
        init()
    }

    private fun init() {
        val pathQuery = intent.getStringExtra(path)?.toUri()
        showPdfFromUri(pathQuery)
    }

    private fun showPdfFromUri(uri: Uri?) {

        try {
            logD("$uri")
            pdfView.fromUri(uri)
                .defaultPage(0)
                .spacing(10)
                .load()
        }
        catch (e: Exception) {
            logD("Inside catch")
            toast("Oops! Seems wrong file location")
            val intent: Intent = Intent(this, NotesActivity::class.java)
            startActivity(intent)
        }



    }
}