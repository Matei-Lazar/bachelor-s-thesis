package ths.kariru.utils

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.kotlinandroidextensions.Item
import ths.kariru.adapters.groupie.TextMessageItem
import ths.kariru.models.ChatChannel
import ths.kariru.models.MessageType
import ths.kariru.models.TextMessage
import ths.kariru.models.User
import timber.log.Timber
import java.lang.NullPointerException

object FirestoreUtil {

    private val firestoreInstance: FirebaseFirestore by lazy { Firebase.firestore }

    private val currentUserRef: DocumentReference
    get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
        ?: throw NullPointerException("UID is null.")}")

    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")

    fun initUserIfFirstTime(onComplete: () -> Unit) {
        currentUserRef.get().addOnSuccessListener {
            if (!it.exists()) {
                val newUser = User(FirebaseAuth.getInstance().currentUser?.displayName ?: "", null)
                currentUserRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            } else {
                onComplete()
            }
        }
    }

    fun updateCurrentUser(name: String = "", profilePicture: String? = null) {
        val userMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) {
            userMap["name"] = name
            Timber.i("Profile: updateCurrentUser: $name")
        }
        if (profilePicture != null) {
            userMap["profilePicture"] = profilePicture
            Timber.i("Profile: updateCurrentUser: $profilePicture")
        }
        currentUserRef.update(userMap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserRef.get()
            .addOnSuccessListener {
                it.toObject(User::class.java)?.let { it1 -> onComplete(it1) }
            }
    }

    fun getOrCreateChatChannel(otherUserId: String, onComplete: (channelId: String) -> Unit) {
        currentUserRef.collection("engagedChatChannels")
            .document(otherUserId).get().addOnSuccessListener {
                if (it.exists()) {
                    onComplete(it["channelId"] as String)
                    return@addOnSuccessListener
                }

                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                val newChannel = chatChannelsCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                currentUserRef
                    .collection("engagedChatChannels")
                    .document(otherUserId)
                    .set(mapOf("channelId" to newChannel.id))

                firestoreInstance.collection("users").document(otherUserId)
                    .collection("engagedChatChannels")
                    .document(currentUserId)
                    .set(mapOf("channelId" to newChannel.id))

                onComplete(newChannel.id)
            }
    }

    fun addChatMessagesListener(channelId: String, context: Context,
                                onListen:(List<Item>) -> Unit): ListenerRegistration {
        return chatChannelsCollectionRef.document(channelId).collection("messages")
            .orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Timber.i("Chat: ChatMessagesListener error: ${firebaseFirestoreException.message}")
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it["type"] == MessageType.TEXT)
                        it.toObject(TextMessage::class.java)?.let { it1 ->
                            TextMessageItem(
                                it1, context)
                        }?.let { it2 -> items.add(it2) }
                    // else
                        //TODO add image
                }
                onListen(items)
            }
    }
}