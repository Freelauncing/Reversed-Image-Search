package com.reversedimagesearched.home

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.reversedimagesearched.data.remote.ReverseImageNetworkInterface
import com.reversedimagesearched.data.remote.ReverseImageRetreiver
import com.reversedimagesearched.home.HomeFragment.Companion.Uploaded_Image_Url
import com.reversedimagesearched.util.Event
import kotlinx.coroutines.launch
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


class HomeViewModel:ViewModel() {

    private val reverseImageRetreiver: ReverseImageRetreiver = ReverseImageRetreiver()


    // Two-way databinding, exposing MutableLiveData
    val productImageUri = MutableLiveData<Uri>()

    private val _openChoiceDialogue = MutableLiveData<Event<Unit>>()
    val openChoiceDialogue: LiveData<Event<Unit>> = _openChoiceDialogue
    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    init {
        productImageUri.value = "".toUri()
    }

    fun onclickAddImage(){
        _openChoiceDialogue.value = Event(Unit)
    }

    fun setProductImageUri(uri: Uri){
        productImageUri.value = uri
    }

    fun getProductImageUri(): Uri? {
        return productImageUri.value
    }

    fun showSnackbarMessage(message: String) { //
        _snackbarText.value = Event(message)
    }

    fun uploadImage(){
        if(productImageUri.value==null){
            showSnackbarMessage("Image Not Selected")
        }else{
            viewModelScope.launch {
                _showLoading.value  = true
                val cp = productImageUri.value!!.path
                val file = File(cp)
                if(file.exists()) {
                    Log.v("HELLO", file.toString())
                }
                val reqFile: RequestBody = RequestBody.create(MediaType.parse("image/jpeg"), file)
                val body = MultipartBody.Part.createFormData("image", file.getName(), reqFile)

                Log.v("HELLO",reqFile.contentType().toString())

                val call: Call<ResponseBody> = reverseImageRetreiver.uploadInverseImage(body)

                Log.v("HELLO",call.toString())
                call.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        _showLoading.value  = false
                        Log.v("HELLO",response.toString())
                        if (response.isSuccessful()) {

                            val html = response.body()!!.string()
                            val document: Document = Jsoup.parse(html)
                            Log.d("UploadImage", "Yeepee!!! = " + document.toString())
                            Log.d("UploadImage", "Yeepee!!! = " + document.body().text())

                            Uploaded_Image_Url = document.body().text()
                            if(!Uploaded_Image_Url.isNullOrEmpty()){
                                showSnackbarMessage("Image Uploaded Successfully !")
                            }

                        } else Log.d("UploadImage", "Response failure = " + response.message())
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        _showLoading.value  = false
                        if (t is SocketTimeoutException) {
                            // "Connection Timeout";
                            Log.e("UploadImage", "Connection Timeout")
                        } else if (t is IOException) {
                            // "Timeout";
                            Log.e("UploadImage", "Timeout")
                        } else {
                            //Call was cancelled by user
                            if (call.isCanceled) {
                                Log.e("UploadImage", "Call was cancelled forcefully")
                            } else {
                                //Generic error handling
                                Log.e("UploadImage", "Network Error :: " + t.localizedMessage)
                            }
                        }
                    }
                })
            }
        }
    }
}