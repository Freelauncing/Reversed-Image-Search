package com.reversedimagesearched.allsavedresults

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reversedimagesearched.data.database.DatabaseModel
import com.reversedimagesearched.data.database.ReverseDbHelper
import com.reversedimagesearched.data.model.CommonResponse
import com.reversedimagesearched.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class AllResultsViewModel:ViewModel() {


    private val _reverseImageLists = MutableLiveData<ArrayList<DatabaseModel>>()
    val reverseImageLists: LiveData<ArrayList<DatabaseModel>> = _reverseImageLists

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    private val _updateList = MutableLiveData<Event<String>>()
    val updateList: LiveData<Event<String>> = _updateList


    fun showSnackbarMessage(message: String) { //
        _snackbarText.value = Event(message)
    }

    fun getAllImagesListFromDb(){
        try {
            val list = ReverseDbHelper.getAllImages()
            _reverseImageLists.value = ArrayList()
            _reverseImageLists.value!!.addAll(list)
            _updateList.value = Event("Update")
        }catch (e:Exception){

        }
    }

    fun deleteItem(id:Int){
        try {
            GlobalScope.launch(Dispatchers.IO) {
                ReverseDbHelper.deleteReverseImageData(id)

                launch(Dispatchers.Main) {
                    getAllImagesListFromDb()
                }
            }
        }catch (e:Exception){

        }
    }

}