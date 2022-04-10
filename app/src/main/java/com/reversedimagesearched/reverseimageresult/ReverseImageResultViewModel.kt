package com.reversedimagesearched.reverseimageresult

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reversedimagesearched.data.model.CommonResponse
import com.reversedimagesearched.data.remote.ReverseImageRetreiver
import com.reversedimagesearched.home.HomeFragment.Companion.Uploaded_Image_Url
import com.reversedimagesearched.util.Event
import kotlinx.coroutines.launch

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

    fun onclickStoreAllImages(){

    }

    fun onclickSearch(){
        if(!Uploaded_Image_Url.isNullOrEmpty()) {
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
        }else{
            showSnackbarMessage("No Image Uploaded")
        }
    }
}