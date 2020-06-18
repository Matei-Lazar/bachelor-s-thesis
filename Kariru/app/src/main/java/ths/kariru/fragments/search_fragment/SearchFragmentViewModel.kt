package ths.kariru.fragments.search_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class SearchFragmentViewModel : ViewModel() {
    init {
        Timber.i("SearchFragmentViewModel created")
    }

    var text = "Search boiii"

    override fun onCleared() {
        super.onCleared()
        Timber.i("SearchFragmentViewModel destroyed")
    }
}