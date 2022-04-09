package com.reversedimagesearched.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reversedimagesearched.data.remote.ReverseImageRetreiver
import com.reversedimagesearched.util.Event
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException


class HomeViewModel:ViewModel() {

    val reverseImageRetreiver: ReverseImageRetreiver = ReverseImageRetreiver()

    // Two-way databinding, exposing MutableLiveData
    val productImageUri = MutableLiveData<Uri>()

    private val _openChoiceDialogue = MutableLiveData<Event<Unit>>()
    val openChoiceDialogue: LiveData<Event<Unit>> = _openChoiceDialogue
    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    init {

    }

    fun onclickAddImage(){
        _openChoiceDialogue.value = Event(Unit)
    }

    fun setProductImageUri(uri: Uri){
        productImageUri.value = uri
    }

    private fun showSnackbarMessage(message: String) { //
        _snackbarText.value = Event(message)
    }

    fun uploadImage(){
        if(productImageUri.value==null){
            showSnackbarMessage("Image Not Selected")
        }else{
            viewModelScope.launch {
                val cp = productImageUri.value!!.path
                val file = File(cp)
                Log.v("HELLO",file.toString())

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
                        Log.v("HELLO",response.toString())
                        if (response.isSuccessful()) {

                            val html = response.body()!!.string()
                            val document: Document = Jsoup.parse(html)
                            Log.d("UploadImage", "Yeepee!!! = " + document.toString())
                            Log.d("UploadImage", "Yeepee!!! = " + response.message())
                            Log.d("UploadImage", "Yeepee!!! = " + response.body()?.contentLength())
                            Log.d("UploadImage", "Yeepee!!! = " + response.body()?.contentType())
                            Log.d("UploadImage", "Yeepee!!! = " + response.headers().byteCount())

                        } else Log.d("UploadImage", "Response failure = " + response.message())
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
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