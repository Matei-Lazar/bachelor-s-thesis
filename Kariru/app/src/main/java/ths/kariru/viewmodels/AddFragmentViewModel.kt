package ths.kariru.viewmodels

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ths.kariru.fragments.AddFragment
import ths.kariru.models.Property
import timber.log.Timber
import java.lang.Exception

class AddFragmentViewModel :ViewModel() {

    private val propertyCollectionRef = Firebase.firestore.collection("properties")



    private fun savePropertyToFirestore(property: Property) = CoroutineScope(Dispatchers.IO).launch {
        try {
            propertyCollectionRef.add(property).await()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Timber.i("$e")
            }
        }
    }

    fun saveProperty(property: Property) {
        propertyCollectionRef
            .document()
            .set(property)
            .addOnSuccessListener {
                Timber.i("property saved in firestore")
            }
            .addOnFailureListener {
                Timber.i("saved to fail property: $it")
            }
    }


}