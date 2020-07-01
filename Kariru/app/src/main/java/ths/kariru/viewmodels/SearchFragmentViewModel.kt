package ths.kariru.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ths.kariru.models.Property
import timber.log.Timber

class SearchFragmentViewModel : ViewModel() {

    private val propertyCollectionRef = Firebase.firestore.collection("properties")

    private var _properties = MutableLiveData<MutableList<Property>>()
    val properties: LiveData<MutableList<Property>> = _properties

    internal fun fetchProperties() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val propertyCollection = propertyCollectionRef

            propertyCollection.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                val innerProperties = querySnapshot?.toObjects(Property::class.java)
                _properties.postValue(innerProperties)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Timber.i("Search: $e")
            }
        }
    }

    internal fun uploadChatsToFirestore(currentUserId: String, otherUserId: String) {

    }
}