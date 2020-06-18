package ths.kariru.fragments.profile_fragment

import androidx.lifecycle.ViewModel
import timber.log.Timber

class ProfileFragmentViewModel : ViewModel() {
    init {
        Timber.i("ProfileFragmentViewModel created")
    }

    var text = "Profile boiii"

    override fun onCleared() {
        super.onCleared()
        Timber.i("ProfileFragmentViewModel destroyed")
    }
}