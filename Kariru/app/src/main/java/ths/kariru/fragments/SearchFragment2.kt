package ths.kariru.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_search2.view.*

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
            val neighborhood = getString(R.string.search2_neighborhood, property.address.neighborhood)
            val street = getString(R.string.search2_street, property.address.street, property.address.streetNumber)
            val blockName = getString(R.string.search2_block_name, property.address.blockName)
            val apartmentNr = getString(R.string.search2_apartment_nr, property.address.apartmentNumber.toString())
            val floor = getString(R.string.search2_floor, property.floor)
            val room = getString(R.string.search2_room, property.room)
            val bath = getString(R.string.search2_bath, property.bath)
            val balcony = getString(R.string.search2_balcony, property.balcony)
            val surface = getString(R.string.search2_surface, property.surface.toString())


            val adapter = AddViewPagerAdapter(property.imageList)
            search2ViewPager.adapter = adapter
            search2Price.text = property.price.toString()

            search2Neighborhood.text = neighborhood
            search2Street.text = street
            search2BlockName.text = blockName
            search2ApartmentNr.text = apartmentNr
            search2Description.text = property.description
            search2Floor.text = floor
            search2Room.text = room
            search2Bath.text = bath
            search2Balcony.text = balcony
            search2Surface.text = surface
        }
    }

}
