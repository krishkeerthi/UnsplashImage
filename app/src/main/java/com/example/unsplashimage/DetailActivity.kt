package com.example.unsplashimage

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentValues.TAG
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.unsplashimage.databinding.ActivityDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.*

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    var msg: String? = ""
    var lastMsg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val url = intent.getStringExtra("IMAGE_URL")

        CoroutineScope(Dispatchers.IO).launch {
            Log.i(TAG, "Current thread ${Thread.currentThread().name}")
            val bitmap = downloadBitmap(url!!)
            withContext(Dispatchers.Main) {
                Log.i(TAG, "Current thread in the main dispatcher: ${Thread.currentThread().name}")
                binding.unsplashPhotoImageView.setImageBitmap(bitmap)
            }
        }

//        Glide.with(this)
//            .load(url)
//            .error(R.drawable.ic_baseline_close_24)
//            .into(binding.unsplashPhotoImageView)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.detail_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.save_menu -> {
                //saveImage()
                downloadImage()
                true
            }
            else ->{
                super.onOptionsItemSelected(item)
            }
        }
    }

    @SuppressLint("Range")
    private fun downloadImage(){
        val url = intent.getStringExtra("IMAGE_URL")

        val directory = File(Environment.DIRECTORY_PICTURES)

        Log.d(TAG, "downloadImage: directory ${directory}")
        val directory1 = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )//.toString() + File.separator //+ IMAGES_FOLDER_NAME

        Log.d(TAG, "downloadImage: directory1 ${directory1}")
       // val file = File(imagesDir, "IMG${Date().time}.png")

        if(!directory.exists())
            directory.mkdirs()

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or
            DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(url?.substring(url.lastIndexOf("/") + 1))
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    directory.toString(),
                    "IMG${Date().time}.png"
                   // url?.substring(url.lastIndexOf("/")+ 1)
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)

        Thread {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                msg = statusMessage(url!!, directory, status) // not null asserting url
                if (msg != lastMsg) {
                    this.runOnUiThread {
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                    lastMsg = msg ?: ""
                }
                cursor.close()
            }
        }.start()

    }

    private fun statusMessage(url: String, directory: File, status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully in $directory" + File.separator + url.substring(
                url.lastIndexOf("/") + 1
            )
            else -> "There's nothing to download"
        }
        return msg
    }

    private fun saveImage(){

        val url = intent.getStringExtra("IMAGE_URL")

        CoroutineScope(Dispatchers.IO).launch {
            val inputStream = URL(url).openStream() // shorthand for openConnection().getInputStream()
            //val storagePath = Environment.getExternalStorageDirectory()

            val imagesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ).toString() + File.separator //+ IMAGES_FOLDER_NAME
            val file = File(imagesDir, "IMG${Date().time}.png")

            val outputStream = FileOutputStream(file)

            try{
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer, 0, buffer.size).also { bytesRead = it } >= 0) {
                    outputStream.write(buffer, 0, bytesRead)
                    Log.d(TAG, "saveImage: bytes read ${bytesRead}")
                }

                Log.d(TAG, "saveImage: inside coroutine")
               // Toast.makeText(this@DetailActivity, "saved successfully", Toast.LENGTH_SHORT).show()
            }
            finally {
                inputStream.close()
                outputStream.flush()
                outputStream.close()
                Log.d(TAG, "saveImage: finally called")
            }
        }

        Log.d(TAG, "saveImage: outside coroutine")
    }
}