package ths.kariru.fragments.add_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ths.kariru.R
import ths.kariru.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    private lateinit var viewModel: AddFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentAddBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false)

        viewModel = ViewModelProvider(this).get(AddFragmentViewModel::class.java)
        binding.viewModel = viewModel

        return binding.root
    }

}
