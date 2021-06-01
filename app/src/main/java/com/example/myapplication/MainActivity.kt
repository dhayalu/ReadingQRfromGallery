package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val pickImage = 100
    private var detector: BarcodeDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val fragmentManger = supportFragmentManager
        val fragmentTransaction = fragmentManger.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, HomeFragment()).commit()
*/

        detector = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.QR_CODE)
            .build()
        if (!detector?.isOperational()!!) {
            println("Could not set up the detector!")
            return
        }

        get_qr_code.setOnClickListener {

            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            val imageUri = data?.data
            val bitmap = decodeBitmapUri(this, imageUri!!)

            if (detector!!.isOperational() && bitmap != null) {
                val frame = Frame.Builder().setBitmap(bitmap).build()
                val barcodes = detector!!.detect(frame)

                for (i in 0 until barcodes.size()){
                    val code = barcodes.valueAt(i)
                    println("the value of qr code is "+code.displayValue + "\n")
                }
            }
        }
    }

    private fun decodeBitmapUri(ctx: Context, uri: Uri): Bitmap? {
        val targetW = 600
        val targetH = 600
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight
        val scaleFactor = Math.min(photoW / targetW, photoH / targetH)
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        return BitmapFactory.decodeStream(
            ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions
        )
    }

}