package ths.kariru.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue.arrayUnion
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ths.kariru.models.Property
import timber.log.Timber
import java.lang.Exception

class AddFragmentViewModel : ViewModel() {

    private val propertyCollectionRef = Firebase.firestore.collection("properties")
    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private var _properties = MutableLiveData<MutableList<Property>>()
    val properties: LiveData<MutableList<Property>> = _properties

    internal fun fetchProperties() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val propertyCollection = propertyCollectionRef

            propertyCollection.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                val innerProperties = querySnapshot?.toObjects(Property::class.java)
                val myProperties: MutableList<Property> = mutableListOf()
                innerProperties?.forEach {
                    if (it.userId == currentUserId)
                        myProperties.add(it)
                }
                _properties.postValue(myProperties)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Timber.i("Search: $e")
            }
        }
    }

    fun updatePropertyToFirestore(
        property: Property,
        images: MutableList<Uri>,
        imagesHaveBeenUpdated: Boolean
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val document = propertyCollectionRef.document(property.propertyId)
            if (imagesHaveBeenUpdated)
                deleteImagesFromStorage(property.imageList)

            document.set(property).await()
            withContext(Dispatchers.Main) {
                Timber.i("Firebase: property saved in firestore")
            }
            if (imagesHaveBeenUpdated)
                uploadImagesToStorage2(property, images)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Timber.i("Firebase: failed to save property: $e")
            }
        }
    }

    private fun deleteImagesFromStorage(imageUrl: MutableList<String>) {
        imageUrl.forEach {
            val imageRef = Firebase.storage.getReferenceFromUrl(it)
            imageRef.delete()
                .addOnSuccessListener {
                    Timber.i("Firebaes: deleted image from storage")
                }
                .addOnFailureListener {e ->
                    Timber.i("Firebase: delete image from storage: ${e.message}")
                }
        }

    }

    fun savePropertyToFirestore(
        property: Property,
        user: FirebaseUser,
        images: MutableList<Uri>
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val document = propertyCollectionRef.document()
            property.propertyId = document.id
            property.userId = user.uid
            document.set(property).await()
            withContext(Dispatchers.Main) {
                Timber.i("Firebase: property saved in firestore")
            }
            uploadImagesToStorage2(property, images)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Timber.i("Firebase: failed to save property: $e")
            }
        }
    }

    private fun uploadImagesToStorage2(property: Property, images: MutableList<Uri>) = CoroutineScope(Dispatchers.IO).launch {

        val storageRef = Firebase.storage.reference.child("images/${property.userId}/${property.propertyId}")
        val imageList: MutableList<String> = arrayListOf()

        if (images.size > 0) {
            try {
                images.let {
                    for (image in images) {
                        val imageRef = storageRef.child("${image.lastPathSegment}")
                        val uploadTask = imageRef.putFile(image)
                        uploadTask.addOnSuccessListener {
                            val downloadUrl = imageRef.downloadUrl
                            downloadUrl.addOnSuccessListener {
                                    uri ->
                                imageList.add(uri.toString())
                                updateImagesDatabase2(property, uri.toString())
                            }
                        }
                        uploadTask.addOnFailureListener {
                            Timber.i("Firebase: ${it.message}")
                        }
                    }

                    withContext(Dispatchers.Main) {
                        Timber.i("Firebase: uploaded images to storage")
                    }

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Timber.i("Firebase: failed to upload images to storage: ${e.message}")
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Timber.i("Firebase: no images to upload")
            }
        }
    }

    private fun updateImagesDatabase2(property: Property, image: String) = CoroutineScope(Dispatchers.IO).launch {
        try {

            val imageListRef = propertyCollectionRef
                .document(property.propertyId)
            val updateMap = mapOf("imageList" to image)

            imageListRef.update("imageList", arrayUnion(image)).await()

            withContext(Dispatchers.Main) {
                Timber.i("Firebase: uploaded images to property's collection")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Timber.i("Firebase: failed to upload downloadUrl to property's images collection in firestore: ${e.message}")
            }
        }
    }
}