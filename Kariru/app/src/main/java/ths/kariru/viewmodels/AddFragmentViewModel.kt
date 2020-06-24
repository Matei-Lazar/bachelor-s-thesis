package ths.kariru.viewmodels

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ths.kariru.MainActivity
import ths.kariru.fragments.AddFragment2
import ths.kariru.models.Image
import ths.kariru.models.Property
import timber.log.Timber
import java.lang.Exception

class AddFragmentViewModel : ViewModel() {

    private val propertyCollectionRef = Firebase.firestore.collection("properties")

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
            uploadImagesToStorage(property, images)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Timber.i("Firebase: failed to save property: $e")
            }
        }
    }

    private fun uploadImagesToStorage(property: Property, images: MutableList<Uri>) = CoroutineScope(Dispatchers.IO).launch {

        val storageRef = Firebase.storage.reference.child("images/${property.userId}/${property.propertyId}")
        val imageList: ArrayList<Image> = ArrayList()

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
                                    Timber.i("Firebase: $uri")
                                updateImagesDatabase(property, Image(image.lastPathSegment.toString(), uri.toString()))
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

    private fun updateImagesDatabase(property: Property, image: Image) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val imagesCollectionRef = propertyCollectionRef
                .document(property.propertyId)
                .collection("images")
                .document()
                image.id = imagesCollectionRef.id
                imagesCollectionRef.set(image).await()
            withContext(Dispatchers.Main) {
                Timber.i("Firebase: uploaded images to property's collection")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Timber.i("Firebase: failed to upload downloadUrl to property's images collection in firestore: ${e.message}")
            }
        }
    }

//    fun savePropertyToFirestore(
//        property: Property,
//        user: FirebaseUser,
//        images: MutableList<Uri>
//    ) {
//        try {
//            val document = propertyCollectionRef.document()
//            property.propertyId = document.id
//            property.userId = user.uid
//            document.set(property)
//
//                Timber.i("Firebase: property saved in firestore")
//
//            uploadImagesToStorage(property, images)
//        } catch (e: Exception) {
//                Timber.i("Firebase: failed to save property: $e")
//        }
//    }
//
//    private fun uploadImagesToStorage(property: Property, images: MutableList<Uri>) {
//
//        val storageRef = Firebase.storage.reference.child("images/${property.userId}/${property.propertyId}")
//        val imageList: ArrayList<Image> = ArrayList()
//
//        if (images.size > 0) {
//            try {
//                images.let {
//                    for (image in images) {
//                        val imageRef = storageRef.child("${image.lastPathSegment}")
//                        val uploadTask = imageRef.putFile(image)
//                        uploadTask.addOnSuccessListener {
//                            val downloadUrl = imageRef.downloadUrl
//                            downloadUrl.addOnSuccessListener {
//                                    uri ->
//                                Timber.i("Firebase: $uri")
//                                updateImagesDatabase(property, Image(image.lastPathSegment.toString(), uri.toString()))
//                            }
//                        }
//                        uploadTask.addOnFailureListener {
//                            Timber.i("Firebase: ${it.message}")
//                        }
//                    }
//
//                        Timber.i("Firebase: uploaded images to storage")
//
//                }
//            } catch (e: Exception) {
//                    Timber.i("Firebase: failed to upload images to storage: ${e.message}")
//            }
//        } else {
//            Timber.i("Firebase: no images to upload")
//        }
//    }
//
//    private fun updateImagesDatabase(property: Property, image: Image) {
//        try {
//            val imagesCollectionRef = propertyCollectionRef
//                .document(property.propertyId)
//                .collection("images")
//                .document()
//                image.id = imagesCollectionRef.id
//                imagesCollectionRef.set(image)
//
//            Timber.i("Firebase: uploaded image to property's collection")
//        } catch (e: Exception) {
//            Timber.i("Firebase: failed to upload downloadUrl to property's images collection in firestore: ${e.message}")
//        }
//    }

}