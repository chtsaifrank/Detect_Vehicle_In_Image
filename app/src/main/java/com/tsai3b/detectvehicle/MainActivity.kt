package com.tsai3b.detectvehicle

import android.content.Context
import android.content.Intent
import android.content.ClipboardManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
                    type = "image/*" //MINE_TYPE // "image/jpeg" // "image/png" // 或  等，根據你的儲存格式
                    putExtra(Intent.EXTRA_STREAM, sharedImageUri)
                    // 非常重要：授予讀取 URI 的權限給接收 Intent 的 App
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    if (needPlateRect)
                        putExtra(Intent.EXTRA_TEXT, "$packageName()") //"()" needCoordinate of plate box
                    else
                        putExtra(Intent.EXTRA_TEXT, packageName)
                    setPackage(targetAppPackageName) // target app
                }
                startActivity(shareIntent) //specify targetApp
                //startActivity(Intent.createChooser(shareIntent, "Share to")) //choice app
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
                        sharedImageUri = null
                    }
                } else if (intent.type?.startsWith("image/") == true) {
                    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    sharedImageUri =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM, Uri::class.java)
                    else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                    }
                    if (!sharedText.isNullOrEmpty() && sharedImageUri != null) {
                        //sharedTextView.text = "分享的文字:\n$sharedText"
                        //displayImage(sharedImageUri)
                        hasText = true
                        sharedTextView.text = sharedText
                        displayImage(sharedImageUri!!)
                    } else if (!sharedText.isNullOrEmpty()) {
                        //sharedTextView.text = "分享的文字:\n$sharedText"
                        hasText = true
                        sharedTextView.text = sharedText
                    } else if (sharedImageUri != null) {
                        //displayImage(sharedImageUri)
                        displayImage(sharedImageUri!!)
                    }
                }
                sharedImageUri?.let {
                    displayImage(it)
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

