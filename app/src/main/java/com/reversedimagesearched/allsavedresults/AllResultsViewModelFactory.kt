package com.reversedimagesearched.allsavedresults

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.reversedimagesearched.home.HomeViewModel

class AllResultsViewModelFactory: ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (AllResultsViewModel() as T)
}