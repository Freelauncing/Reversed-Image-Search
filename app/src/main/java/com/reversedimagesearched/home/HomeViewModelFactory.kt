package com.reversedimagesearched.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddProductViewModelFactory ()
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (HomeViewModel() as T)
}