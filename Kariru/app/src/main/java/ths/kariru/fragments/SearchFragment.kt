package ths.kariru.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ths.kariru.MapsActivity
import ths.kariru.R
import ths.kariru.databinding.FragmentSearchBinding
import ths.kariru.viewmodels.SearchFragmentViewModel
import timber.log.Timber

class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        viewModel = ViewModelProvider(this).get(SearchFragmentViewModel::class.java)
        binding.viewModel = viewModel

//        binding.searchMapButton.setOnClickListener {
//            val intent = Intent(context, MapsActivity::class.java)
//            startActivity(intent)
//        }

        return binding.root
    }

}
