package com.reversedimagesearched.reverseimageresult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.reversedimagesearched.home.HomeViewModel

class ReverseImageResultViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (ReverseImageResultViewModel() as T)
}