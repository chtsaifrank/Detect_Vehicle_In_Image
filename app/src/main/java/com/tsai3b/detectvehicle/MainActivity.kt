package com.tsai3b.detectvehicle

import android.content.Context
import android.content.Intent
import android.content.ClipboardManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible


class MainActivity : AppCompatActivity() {

    private lateinit var sharedImageView: ImageView
    private lateinit var sharedTextView: TextView
    private lateinit var shareButton : Button
    private lateinit var photoButton : Button
    private var needPlateRect = false
    private val getContent =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            // Handle the returned Uri
            uri?.let { mediaUri ->
                sharedImageUri = mediaUri
                displayImage(mediaUri)
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedImageView = findViewById(R.id.sharedImageView)
        sharedTextView = findViewById(R.id.sharedTextView)
        sharedTextView.movementMethod = ScrollingMovementMethod()
        shareButton = findViewById(R.id.shareButton)
        photoButton = findViewById(R.id.photoButton)

        handleIntent(intent)

        photoButton.setOnClickListener {
            getContent.launch(arrayOf("image/*"))
        }
        shareButton.setOnClickListener {
            sharedImageUri?.let {
                val targetAppPackageName = "com.ml.tensorflow.examples.lpr"

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, sharedImageUri)
                    // important to add permission
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    if (needPlateRect)
                        putExtra(Intent.EXTRA_TEXT, "$packageName()") //"()" need Coordinate of plate box
                    else
                        putExtra(Intent.EXTRA_TEXT, packageName)
                    setPackage(targetAppPackageName) // target app
                }
                startActivity(shareIntent) //specify targetApp
            }
        }
        sharedImageView.setOnLongClickListener {
            //toggle
            needPlateRect = !needPlateRect
            if (sharedTextView.isVisible)
                sharedTextView.visibility = View.GONE
            else
                sharedTextView.visibility = View.VISIBLE
            true
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    var sharedImageUri : Uri? = null
    private fun handleIntent(intent: Intent) :Boolean {
        var hasText = false
        when (intent.action) {
            Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let { text ->
                        if(text.isNotEmpty() && text!="[]") {
                            sharedTextView.text = "Json Text:$text"
                            hasText = true
                        }
                        sharedImageUri = null // comment if don't need load image 
                    }
                }
            }
            Intent.ACTION_MAIN -> {hasText = true}
        }
        return hasText
    }

    private fun displayImage(uri: Uri) {
        sharedImageView.setImageURI(uri)
    }
}

