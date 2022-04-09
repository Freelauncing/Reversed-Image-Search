package com.reversedimagesearched.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime


class Utility {

    companion object {

        val LOG_TAG: String = "Utility"

        @SuppressLint("NewApi")
        fun readUriImage(context: Context, selectedFileUri: Uri): Uri? {
            try {
                val parcelFileDescriptor: ParcelFileDescriptor? =
                    context.getContentResolver()?.openFileDescriptor(selectedFileUri, "r")
                val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
                val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close()
                }

                return createDirectoryAndSaveFile(context, image)

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null;
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun createFileForImage(context: Context): File? {
            val directoryToStore: File
            directoryToStore = context.getExternalFilesDir("ReversedImagesSearch")!!
            if (!directoryToStore.exists()) {
                if (directoryToStore.mkdir());
            }
            var n = LocalDateTime.now()
            val fname = "Image-$n.jpeg"
            val file = File(directoryToStore, fname)
            Log.i(LOG_TAG, "" + file)
            if (file.exists()) file.delete()
            file.createNewFile()
            try {
                val out = FileOutputStream(file)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return file
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun createDirectoryAndSaveFile(context: Context, imageToSave: Bitmap): Uri? {
            val directoryToStore: File
            directoryToStore = context.getExternalFilesDir("ReversedImagesSearch")!!
            if (!directoryToStore.exists()) {
                if (directoryToStore.mkdir());
            }
            var n = LocalDateTime.now()
            val fname = "Image-$n.jpeg"
            val file = File(directoryToStore, fname)
            Log.i(LOG_TAG, "" + file)
            if (file.exists()) file.delete()
            file.createNewFile()
            try {
                val out = FileOutputStream(file)
                imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return file.toUri()
        }

        fun displayImageInDialog(context: Context,uri: Uri){

            val builder = Dialog(context)
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
            builder.getWindow()!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            builder.setOnDismissListener(DialogInterface.OnDismissListener {
                //nothing;
            })

            val bitmapImage = BitmapFactory.decodeFile(uri.path)
            val nh = (bitmapImage.height * (512.0 / bitmapImage.width)).toInt()
            val scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true)
            val imageView = ImageView(context)
            imageView.setImageBitmap(rotateImageIfRequired(scaled,uri))

            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            params.setMargins(86,86,86,86)
            builder.addContentView(
                imageView, params
            )
            builder.show()
        }

        @Throws(IOException::class)
        fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap? {
            val ei = ExifInterface(selectedImage.path!!)
            val orientation: Int =
                ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
                else -> img
            }
        }

        fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
            img.recycle()
            return rotatedImg
        }


    }
}