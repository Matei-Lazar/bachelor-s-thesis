package ths.kariru.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import timber.log.Timber
import java.lang.NullPointerException
import java.util.*

object StorageUtil {
    private val storageInstance: FirebaseStorage by lazy { Firebase.storage}

    private val currentUserRef: StorageReference
        get() = storageInstance.reference
            .child(FirebaseAuth.getInstance().currentUser?.uid ?: throw NullPointerException("UID is null."))

    fun uploadProfilePicture(image: ByteArray, onSuccess: (imagePath: String) -> Unit) {
        val ref = currentUserRef.child("profilePictures/${UUID.nameUUIDFromBytes(image)}")
        ref.putBytes(image)
            .addOnSuccessListener {
                onSuccess(ref.path)
                Timber.i("Profile: uploadProfilePicture, ${ref.path}")
            }
    }

    fun pathToReference(path: String) = storageInstance.getReference(path)
}