package ths.kariru.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
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
import ths.kariru.utils.MapsActivity
import ths.kariru.R
import ths.kariru.adapters.SearchRecyclerViewAdapter
import ths.kariru.databinding.FragmentAddBinding
import ths.kariru.models.Property
import ths.kariru.utils.TopSpacingItemDecoration
import ths.kariru.viewmodels.AddFragmentViewModel

class AddFragment : Fragment() {

    private lateinit var viewModel: AddFragmentViewModel
    private lateinit var navController: NavController
    private lateinit var binding: FragmentAddBinding

    private var propertyList = ArrayList<Property>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false)

        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(AddFragmentViewModel::class.java)
        binding.viewModel = viewModel

        binding.searchMapButton.setOnClickListener {
            val intent = Intent(context, MapsActivity::class.java)
            startActivityForResult(intent, REQUEST_PROPERTY_COORDINATES)
        }

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
            binding.addRecyclerview.adapter!!.notifyDataSetChanged()
        })
    }

    private fun initRecyclerView() {
        binding.addRecyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            val topSpacingDecoration = TopSpacingItemDecoration(10)
            addItemDecoration(topSpacingDecoration)
            adapter = SearchRecyclerViewAdapter(propertyList) {property ->
                val action = AddFragmentDirections.addToEdit(
                    propertyItem = property
                )
                navController.navigate(action)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PROPERTY_COORDINATES && resultCode == RESULT_OK && data != null) {
            val latitude = data.getDoubleExtra("latitude", 0.0)
            val longitude = data.getDoubleExtra("longitude", 0.0)
            var streetName = data.getStringExtra("streetName")
            var streetNumber = data.getStringExtra("streetNumber")

            if (streetName == null) streetName = ""
            if (streetNumber == null) streetNumber = ""

            val action = AddFragmentDirections.addToAdd2(
                latitude = latitude.toString(),
                longitude = longitude.toString(),
                streetName = streetName,
                streetNumber = streetNumber
            )
            navController.navigate(action)
        }
    }

    companion object {
        private const val REQUEST_PROPERTY_COORDINATES = 4
    }
}
