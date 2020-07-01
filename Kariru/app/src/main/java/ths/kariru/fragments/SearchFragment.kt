package ths.kariru.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ths.kariru.R
import ths.kariru.adapters.SearchRecyclerViewAdapter
import ths.kariru.databinding.FragmentSearchBinding
import ths.kariru.models.Property
import ths.kariru.utils.TopSpacingItemDecoration
import ths.kariru.viewmodels.SearchFragmentViewModel
import timber.log.Timber

class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchFragmentViewModel
    private lateinit var binding: FragmentSearchBinding
    private lateinit var navController: NavController

    private var propertyList = ArrayList<Property>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        viewModel = ViewModelProvider(this).get(SearchFragmentViewModel::class.java)
        binding.viewModel = viewModel

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        viewModel.fetchProperties()

        viewModel.properties.observe(viewLifecycleOwner, Observer {
            propertyList.removeAll(propertyList)
            propertyList.addAll(it)
            binding.searchRecyclerview.adapter!!.notifyDataSetChanged()
        })
    }

    private fun initRecyclerView() {
        binding.searchRecyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            val topSpacingDecoration = TopSpacingItemDecoration(10)
            addItemDecoration(topSpacingDecoration)
            adapter = SearchRecyclerViewAdapter(propertyList) {property ->
                val action = SearchFragmentDirections.searchToSearch2(
                    propertyItem = property
                )
                navController.navigate(action)
            }
        }
    }
}
