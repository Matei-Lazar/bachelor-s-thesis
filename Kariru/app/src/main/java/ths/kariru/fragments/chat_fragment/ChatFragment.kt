package ths.kariru.fragments.chat_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ths.kariru.R
import ths.kariru.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private lateinit var viewModel: ChatFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentChatBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)

        viewModel = ViewModelProvider(this).get(ChatFragmentViewModel::class.java)
        binding.viewModel = viewModel

        return binding.root
    }

}
