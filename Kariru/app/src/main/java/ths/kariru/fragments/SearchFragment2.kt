package ths.kariru.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import ths.kariru.R
import ths.kariru.adapters.AddViewPagerAdapter
import ths.kariru.databinding.FragmentSearch2Binding
import ths.kariru.databinding.FragmentSearchBinding
import ths.kariru.models.Property
import ths.kariru.viewmodels.SearchFragmentViewModel
import timber.log.Timber

class SearchFragment2 : Fragment() {

    private lateinit var viewModel: SearchFragmentViewModel
    private lateinit var binding: FragmentSearch2Binding
    private lateinit var propertyItem: Property

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search2, container, false)
        viewModel = ViewModelProvider(this).get(SearchFragmentViewModel::class.java)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val safeArgs = SearchFragment2Args.fromBundle(it)
            propertyItem = safeArgs.propertyItem!!
            Timber.i("Search2: ${propertyItem.propertyId}")
            bindData(propertyItem)
        }
    }

    private fun bindData(property: Property) {
        binding.apply {
            val neighborhood = getString(R.string.cluj_napoca, property.address.neighborhood)
            val street = getString(R.string.street, property.address.street, property.address.streetNumber)
            val room = getString(R.string.room, property.room)
            val rooms = getString(R.string.rooms, property.room)
            val bath = getString(R.string.bath, property.bath)
            val baths = getString(R.string.baths, property.bath)


            val adapter = AddViewPagerAdapter(property.imageList)
            search2ViewPager.adapter = adapter
            search2Price.text = property.price.toString()

            search2Neighborhood.text = neighborhood
            search2Street.text = street

            if (property.room == "1") {
                search2Room.text = room
            } else {
                search2Room.text = rooms
            }
            if (property.bath == "1") {
                search2Bath.text = bath
            } else {
                search2Bath.text = baths
            }
            search2Surface.text = property.surface.toString()
        }
    }

}
