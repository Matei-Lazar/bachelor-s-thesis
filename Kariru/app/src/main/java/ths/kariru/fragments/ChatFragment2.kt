package ths.kariru.fragments

import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ListenerRegistration
import ths.kariru.MainActivity

import ths.kariru.R

class ChatFragment2 : Fragment() {

    private lateinit var messagesListenerRegistration: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.FragmentSearch2Theme)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        return localInflater.inflate(R.layout.fragment_search2, container, false)

        //return inflater.inflate(R.layout.fragment_chat2, container, false)
    }

}
