package com.reversedimagesearched.reverseimageresult

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reversedimagesearched.data.database.DatabaseModel
import com.reversedimagesearched.data.database.ReverseDbHelper
import com.reversedimagesearched.data.model.CommonResponse
import com.reversedimagesearched.data.remote.ReverseImageRetreiver
import com.reversedimagesearched.home.HomeFragment.Companion.Uploaded_Image_Url
import com.reversedimagesearched.util.Event
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Exception
import java.net.URL

class ReverseImageResultViewModel:ViewModel() {

    private val reverseImageRetreiver: ReverseImageRetreiver = ReverseImageRetreiver()

    val selectedMode = MutableLiveData<String>()

    private val _reverseImageLists = MutableLiveData<ArrayList<CommonResponse>>()
    val reverseImageLists: LiveData<ArrayList<CommonResponse>> = _reverseImageLists

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    private val _updateList = MutableLiveData<Event<String>>()
    val updateList: LiveData<Event<String>> = _updateList

    fun showSnackbarMessage(message: String) { //
        _snackbarText.value = Event(message)
    }

    init {
        selectedMode.value = ""
    }

    fun clearResults(){
        _reverseImageLists.value = ArrayList()
        _updateList.value = Event("Update")
    }
    fun onclickSearch(){
        try {
            if (!Uploaded_Image_Url.isNullOrEmpty()) {
                viewModelScope.launch {
                    _reverseImageLists.value = ArrayList()
                    Log.v("HELLO", "Check")
                    _updateList.value = Event("Update")
                    _showLoading.value = true
                    if (selectedMode.value.toString().equals("Google")) {
                        var res =
                            reverseImageRetreiver.googleInverseImage(Uploaded_Image_Url)
                        Log.v("HELLO", res.toString())
                        _reverseImageLists.value!!.addAll(res)
                    } else if (selectedMode.value.toString().equals("Bing")) {
                        var res =
                            reverseImageRetreiver.bingInverseImage(Uploaded_Image_Url)
                        Log.v("HELLO", res.toString())
                        _reverseImageLists.value!!.addAll(res)
                    } else if (selectedMode.value.toString().equals("Tineye")) {
                        var res =
                            reverseImageRetreiver.tineyeInverseImage(Uploaded_Image_Url)
                        Log.v("HELLO", res.toString())
                        _reverseImageLists.value!!.addAll(res)
                    } else {
                        showSnackbarMessage("No Server Selected")
                    }
                    _updateList.value = Event("Update")
                    _showLoading.value = false
                    Log.v("HELLO", _reverseImageLists.value!!.size.toString())
                }
            } else {
                showSnackbarMessage("No Image Uploaded")
            }
        }catch (e:Exception){

        }
    }

    fun takeUrl(currentItem:CommonResponse) {
        try {
            _showLoading.value = true
            GlobalScope.launch(Dispatchers.IO) {
                val res = ReverseDbHelper.insertReverseImage(
                    DatabaseModel(
                        0,
                        convertToByteArray(Uploaded_Image_Url),// Uploaded_Image_Url
                        Uploaded_Image_Url,//Uploaded_Image_Url,
                        convertToByteArray(currentItem.image_link),
                        currentItem.image_link,
                        currentItem.name
                    )
                )
                launch(Dispatchers.Main) {
                    showSnackbarMessage("Image Downloaded Successfully")
                }
            }

            _showLoading.value = false
        }catch (e:Exception){

        }
    }

    suspend fun convertToByteArray(url:String): ByteArray {
        val baos = ByteArrayOutputStream()
        val bitmap = getImageBitmap(url)
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val photo: ByteArray = baos.toByteArray()
        return photo
    }

    suspend fun getImageBitmap(url:String): Bitmap? {
        try {
            var image: Bitmap? = null
            withContext(Dispatchers.IO) {
                val url = URL(url)
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            }
            Log.v("CHECK",url)
            Log.v("CHECK",image.toString())
            return image
        } catch (e: IOException) {
            System.out.println(e)
        }
        return null
    }
}